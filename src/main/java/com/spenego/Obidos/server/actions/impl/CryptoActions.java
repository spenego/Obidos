/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.server.actions.impl;

import static com.spenego.Obidos.server.utils.ServerUtils.decodeFromBase64;
import static com.spenego.Obidos.server.utils.ServerUtils.encodeToBase64;
import static com.spenego.Obidos.server.utils.ServerUtils.getPropertiesFile;
import static com.spenego.Obidos.shared.dto.ContainerDTO.NOTEBOOK_ID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.model.HasOwner;
import com.spenego.Obidos.server.model.HasPublicKey;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.Model;
import com.spenego.Obidos.server.model.PublicKeyCryptoProperties;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.Encryption;
import com.spenego.Obidos.server.security.Encryption.KeyPairProcessor;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.Encryption.PublicKeyEncryptor;
import com.spenego.Obidos.server.security.Encryption.SecretKeyEncryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.CachedSupplier;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;


/**
 * The CryptoActions is an abstract layer for building actions that need to
 * encrypt and decrypt user secrets.  All item values are encrypted with a
 * public key.  This class provides the functionality to do that.
 *
 *
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.server.actions.impl.ObidosActions
 * @since   Obidos1.0
 *
 */
public abstract class CryptoActions<O extends Model> extends ObidosActions<O> {
	private SecretKeyEncryptor secretKeyEncryptor;
	private final String propertiesFilename;
	private final String name;

	@Autowired protected final Encryption encryption = null;

	protected abstract String elementName();

	/**
	 * @param propertiesFilename The name of the properties file that contains the secret key.
	 * @param name
	 */
	protected CryptoActions(final String propertiesFilename, final String name) {
		this.propertiesFilename = propertiesFilename;
		this.name = name;
	}

	protected static final <T extends Model> Map<Long,Long> createUserModelMap(final Collection<T> c) {
		final Map<Long,Long> map = new ConcurrentHashMap<>(c.size());

		c.forEach(m -> map.put(m.getUserId(), m.getId()));

		return map;
	}

	protected static final boolean containerIsNotebook(final Long containerId) {
		return containerId.equals(NOTEBOOK_ID);
	}

	// This can be optimized by getting a count of items (with this item) that reside in a notebook container.
	// However, C3P0 is likely caching the objects for us, so it should be fast enough anyway.
	public final boolean isNote(final Item item) {
		return containerIsNotebook(getContainerAssignment(item.getContainerAssignmentId()).getContainerId());
	}

	protected final Supplier<PublicKeyEncryptor> getPKESupplier(final HasPublicKey keyProvider) {
		return new CachedSupplier<>(() -> encryption.createPublicKeyEncryptor(keyProvider));
	}

	protected final Supplier<PublicKeyDecryptor> getPKDSupplier(final PublicKeyCryptoProperties pkp, final PassphraseHash hash) {
		return new CachedSupplier<>(() -> encryption.createPublicKeyDecryptor(pkp, hash));
	}

	protected final void ensureCallerOwnsAssignment(final HasOwner hasOwner, final User caller, final Long id, final Integer action, final String operation) throws PermissionDeniedException {
		if (!hasOwner.self(caller.getId())) {
			final String username = caller.getUsername();
			audit(action, username, caller.getId(), null, id, null);
			getLogger().warn(() -> "User " + username + " attempted to " + operation + " assignment " + id + ". They do not own it.");
			throw new PermissionDeniedException("You may not attempt to " + operation + "s assigned to other users. This incident has been reported.");
		}
	}

	private final SecretKeyEncryptor createSecretKeyEncryptor(final String passphrase, final String nonce, final String salt) {
		return encryption.createSecretKeyEncryptor(passphrase, nonce, salt);
	}

	private static void setEncodedProperty(final Properties props, final String property, final byte[] value) {
		if (value == null || value.length == 0) {
			throw new ServerSideException(property + " component for secret key is incomplete.");
		}

		props.setProperty(property, encodeToBase64(value));
	}

	private SecretKeyEncryptor createNew(final File propertiesFile) throws IOException {
		final SecretKeyEncryptor ske = createSecretKeyEncryptor();
		final Properties props = new Properties();
		ske.supplySecretKeyComponents((final byte[] passphrase, final byte[] nonce, final byte[] salt) -> {
			setEncodedProperty(props, "salt", salt);
			setEncodedProperty(props, "nonce", nonce);
			setEncodedProperty(props, "passphrase", passphrase);});

		getLogger().info(() -> "Saving " + name);
		try(final FileOutputStream fileOut = new FileOutputStream(propertiesFile)) {
			props.store(fileOut, name);
		}
		getLogger().info(() -> name + " saved");
		return ske;
	}

	private SecretKeyEncryptor createFromFile(final File propertiesFile) throws IOException, ServerSideException {
		final Properties props = new Properties();
		try(final FileInputStream fileInput = new FileInputStream(propertiesFile)) {
			props.load(fileInput);
		}
		return createSecretKeyEncryptor(props.getProperty("passphrase"), props.getProperty("nonce"), props.getProperty("salt"));
	}

	private SecretKeyEncryptor createSKE() {
		final File propertiesFile = getPropertiesFile(propertiesFilename);

		try {
			return propertiesFile.exists() ? createFromFile(propertiesFile) : createNew(propertiesFile);
		} catch (final IOException e) {
			getLogger().exception(e);
		}
		throw new ServerSideException("Unable to get " + name + " key");
	}

	private synchronized SecretKeyEncryptor ske() {
		if (secretKeyEncryptor == null) {
			secretKeyEncryptor = createSKE();
		}
		return secretKeyEncryptor;
	}

	protected final String decrypt(final String cipherText) {
		return ske().decrypt(decodeFromBase64(cipherText));
	}

	protected final String encrypt(final String text) {
		return encodeToBase64(ske().encrypt(text));
	}

	public final void validatePassphraseHash(final User caller, final PassphraseHash passphraseHash) {
		if (caller.isAdmin()) {
			return;					// Admins do not have passphrases
		}

		if (passphraseHash == null) {
			throw new ServerSideException("Please supply your passphrase to perform this operation.");
		}

		if (!passphraseHash.isValidated()) {
			encryption.validatePassphrase(caller, passphraseHash);
			passphraseHash.setValidated(true);
		}
	}

	protected final SecretKeyEncryptor createSecretKeyEncryptor() {
		return encryption.createSecretKeyEncryptor();
	}

	protected final Void createKeypair(final byte[] passPhrase, final KeyPairProcessor processor) {
		return encryption.createKeypair(passPhrase, processor);
	}

	protected final void updatePrivateKey(final byte[] privateKey, final byte[] newPassphrase, final KeyPairProcessor processor) {
		encryption.updatePrivateKey(privateKey, newPassphrase, processor);
	}

	public Void delete(final User caller, final Collection<Long> ids) {
		processStream(() -> "deleting " + elementName() + "s", ids::stream, i -> delete(caller, i));
		return null;
	}

	public Void delete(final User caller, final Collection<Long> ids, final PassphraseHash passphraseHash) {
		validatePassphraseHash(caller, passphraseHash); // ensures user has active public key as an additional level of authentication
		return delete(caller, ids);
	}

	protected final void relinquish(final User caller, final Long ownerId, final Long targetId, final String name, final int action, final Runnable relinquisher) {
		relinquisher.run();
		createNotification(caller.getId(), ownerId, targetId, name, action, null);
	}
}
