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

package com.spenego.Obidos.server.security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.model.HasPublicKey;
import com.spenego.Obidos.server.model.PublicKeyCryptoProperties;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.exceptions.PassphraseRequiredException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class LibSodiumEncryption implements Encryption {
	protected static final Logger	logger = LoggerFactory.getLogger(LibSodiumEncryption.class);

	private static Encoder encoder = Base64.getEncoder();
	private static Decoder decoder = Base64.getDecoder();

	public LibSodiumEncryption() {
		LibSodium.initialize();
	}

	protected static byte[] decodeBase64(final String encoded) {
		return decoder.decode(encoded);
	}

	protected static String base64(final byte[] data) {
		return encoder.encodeToString(data);
	}

	private interface ExceptionWrapper<T> {
		T lambda() throws SodiumLibraryException, ServerSideException;
	}

	/**
	 * Converts SodiumLibraryExceptions into ServerSideExceptions.
	 *
	 * @param x
	 * @param exceptionMessage
	 * @return
	 */
	protected static <T> T ex_wrapper(final ExceptionWrapper<T> x, final String exceptionMessage) {
		try {
			return x.lambda();
		} catch(final SodiumLibraryException ex) {
			logger.exception(ex);
			throw new ServerSideException(exceptionMessage);
		}
	}

	protected static byte[] cryptoPwhashSaltBytes() {
		return ServerUtils.randomBytes(SodiumLibrary.cryptoPwhashSaltBytes());
	}

	protected static byte[] cryptoSecretBoxNonceBytes() {
		return ServerUtils.randomBytes(SodiumLibrary.cryptoSecretBoxNonceBytes().intValue());
	}

	protected static byte[] cryptoSecretBoxEasy(final byte[] message, final byte[] nonce, final byte[] key) throws SodiumLibraryException {
		return SodiumLibrary.cryptoSecretBoxEasy(message, nonce, key);
	}

	protected static byte[] cryptoSecretBoxOpenEasy(final byte[] cipherText, final byte[] nonce, final byte[] key) throws SodiumLibraryException {
		return SodiumLibrary.cryptoSecretBoxOpenEasy(cipherText, nonce, key);
	}

	protected static byte[] cryptoBoxSeal(final byte[] message, final HasPublicKey keySupplier) throws SodiumLibraryException {
		return SodiumLibrary.cryptoBoxSeal(message, keySupplier.getDecodedPublicKey());
	}

	protected static byte[] cryptoBoxSealOpen(final byte[] cipherText, final byte[] pk, final byte[] sk) throws SodiumLibraryException {
		return SodiumLibrary.cryptoBoxSealOpen(cipherText, pk, sk);
	}

	protected static byte[] cryptoPwhashArgon2i(final byte[] passphrase, final byte[] salt) throws SodiumLibraryException {
		return SodiumLibrary.cryptoPwhashArgon2i(passphrase, salt);
	}

	private static SodiumKeyPair cryptoBoxKeyPair() throws SodiumLibraryException {
		return SodiumLibrary.cryptoBoxKeyPair();
	}

	/**
	 * To ensure that a black-hat will be unable to ascertain how long an item value is, we pad the values to a 32-byte boundary.
	 *
	 * @param src
	 * @return
	 */
	private static byte[] padded(final byte[] src) {
		final int padlength = 32 - (8 + src.length) % 32;
		final int offset = padlength + 8;
		final byte[] v;

		if (src.length < 24) {
			v = ServerUtils.randomBytes(32);
		} else {
			v = new byte[src.length + offset];
			System.arraycopy(ServerUtils.randomBytes(offset), 0, v, 0, offset);
		}
		System.arraycopy(src, 0, v, offset, src.length);
		v[0] = (byte) ((byte) padlength ^ v[1] ^ v[2] ^ v[3] ^ v[4]);

		return v;
	}

	private static byte[] strip_padding(final byte[] src, final boolean mask) {
		final int offset = mask ? ((src[0] ^ src[1] ^ src[2] ^ src[3] ^ src[4]) & 0xff) + 8 : src[0] + 1;
		final int len = src.length - offset;
		final byte[] v = new byte[len];

		System.arraycopy(src, offset, v, 0, len);
		Arrays.fill(src, (byte) 0);

		return v;
	}

	@Override
	public byte[] decryptPrivateKey(final PublicKeyCryptoProperties pkp, final PassphraseHash passphraseHash) {
		return ex_wrapper(() -> cryptoSecretBoxOpenEasy(pkp.getDecodedPrivateKey(), pkp.getDecodedNonce(), passphraseHash.get()), "Unable to decrypt private key.");
	}

	@Override
	public PublicKeyEncryptor createPublicKeyEncryptor(final HasPublicKey keySupplier) {
		return secret -> ex_wrapper(() -> {
							final byte[] p = (secret == null) ? null : padded(secret);
							try {
								return secret == null ? null : cryptoBoxSeal(p, keySupplier);
							} finally {
								if (p != null) {
									Arrays.fill(p, (byte) 0); // zero-out un-encrypted padded secret
								}}}, "Unable to encrypt secret");
	}

	private static byte[] decrypt(final byte[] encodedSecret, final byte[] publicKey, final byte[] privateKey, final int encryptionMode) throws SodiumLibraryException {
		final byte[] plainText = cryptoBoxSealOpen(encodedSecret, publicKey, privateKey);
		return encryptionMode == ObidosConstants.ENCRYPTION_MODE_PADDED2 ? strip_padding(plainText, true) :
				encryptionMode == ObidosConstants.ENCRYPTION_MODE_PADDED ? strip_padding(plainText, false) : plainText;
	}

	@Override
	public PublicKeyDecryptor createPublicKeyDecryptor(final PublicKeyCryptoProperties pkp, final PassphraseHash passphraseHash) {
		if (passphraseHash == null)			{ throw new ServerSideException("Supplied passphraseHash is null!"); }
		final byte[] privateKey	= decryptPrivateKey(pkp, passphraseHash);

		return (cipherText, encryptionMode) -> ex_wrapper(() -> cipherText == null ? null : decrypt(cipherText, pkp.getDecodedPublicKey(), privateKey, encryptionMode), "Unable to decrypt secret.");
	}

	@Override
	public void validatePassphrase(final PublicKeyCryptoProperties pkp, final PassphraseHash passphraseHash) {
		decryptPrivateKey(pkp, passphraseHash);
	}

	protected static byte[] generateSecretKey(final byte[] passphrase, final byte[] salt) {
		return ex_wrapper(() -> cryptoPwhashArgon2i(passphrase, salt), "Unable to generate secret key.");
	}

	private final class MySecretKeyEncryptor implements SecretKeyEncryptor {
		private final byte[] key;
		private final byte[] salt;
		private final byte[] nonce;
		private final byte[] passphrase;

		public MySecretKeyEncryptor(final byte[] passphrase, final byte[] nonce, final byte[] salt) {
			this.passphrase = passphrase;
			this.nonce = nonce;
			this.salt = salt;
			this.key = generateSecretKey(passphrase, salt);
		}

		public MySecretKeyEncryptor() {
			this(ServerUtils.randomBytes(128), cryptoSecretBoxNonceBytes(), cryptoPwhashSaltBytes());
		}

		public MySecretKeyEncryptor(final String passphrase, final String nonce, final String salt) {
			// this(decodeBase64(passphrase), decodeBase64(nonce), decodeBase64(salt));
			this.passphrase = decodeBase64(passphrase);
			this.nonce = decodeBase64(nonce);
			this.salt = decodeBase64(salt);
			try {
				this.key =  SodiumLibrary.cryptoPwhashArgon2i(this.passphrase, this.salt);
			} catch(final SodiumLibraryException ex) {
				logger.exception(ex);
				throw new ServerSideException("Unable to decrypt secret key");
			}
		}

		public void supplySecretKeyComponents(final SecretKeyComponentsProcessor processor) {
			processor.lambda(passphrase, nonce, salt);
		}

		@Override
		public byte[] encrypt(final String text) {
			return ex_wrapper(() -> cryptoSecretBoxEasy(text.getBytes(), nonce, key), "Unable to encrypt");
		}

		@Override
		public String decrypt(final byte[] cipherText) {
			return ex_wrapper(() -> new String(cryptoSecretBoxOpenEasy(cipherText, nonce, key)), "Unable to decrypt");
		}
	}

	@Override
	public SecretKeyEncryptor createSecretKeyEncryptor() {
		return new MySecretKeyEncryptor();
	}

	@Override
	public SecretKeyEncryptor createSecretKeyEncryptor(final String passphrase, final String nonce, final String salt) {
		return new MySecretKeyEncryptor(passphrase, nonce, salt);
	}

	private static String secureBase64(final byte[] data) {
		try {
			return base64(data);
		} finally {
			Arrays.fill(data, (byte) 0);
		}
	}

	private static Void keypair(final String b64PublicKey, final byte[] privateKey, final byte[] passPhrase, final KeyPairProcessor processor) throws SodiumLibraryException, ServerSideException {
		try {
			final byte[] salt				 = cryptoPwhashSaltBytes();
			final byte[] nonce				 = cryptoSecretBoxNonceBytes();
			final byte[] encryptedPrivateKey = cryptoSecretBoxEasy(privateKey, nonce, generateSecretKey(passPhrase, salt));
			processor.run(b64PublicKey, secureBase64(encryptedPrivateKey), secureBase64(nonce), secureBase64(salt));
			return null;
		} finally {
			Arrays.fill(passPhrase, (byte) 0);
		}
	}

	private static Void keypair(final byte[] passPhrase, final KeyPairProcessor processor) throws SodiumLibraryException, ServerSideException {
		final SodiumKeyPair keyPair = cryptoBoxKeyPair();
		return keypair(base64(keyPair.getPublicKey()), keyPair.getPrivateKey(), passPhrase, processor);
	}

	@Override
	public Void createKeypair(final byte[] passPhrase, final KeyPairProcessor processor) {
		return ex_wrapper(() -> keypair(passPhrase, processor), "Unable to create key pair.");
	}

	@Override
	public Void updatePrivateKey(final byte[] privateKey, final byte[] newPassphrase, final KeyPairProcessor processor) {
		return ex_wrapper(() -> keypair(null, privateKey, newPassphrase, processor), "unable to encrypt private key");
	}

	@Override
	public PassphraseHash secureHashCompute(final PublicKeyCryptoProperties pkp, final byte[] passphrase) {
		try {
			return new PassphraseHash(cryptoPwhashArgon2i(passphrase, pkp.getDecodedSalt()));
		} catch (final SodiumLibraryException e) {
			throw new PassphraseRequiredException("Unable to generate passphrase hash: " + e.toString());
		} finally {
			// zero-out the passphrase to clear it from memory
			Arrays.fill(passphrase, (byte) 0);
		}
	}
}
