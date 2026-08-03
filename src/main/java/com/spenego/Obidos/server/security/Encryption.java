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

import com.spenego.Obidos.server.model.HasPublicKey;
import com.spenego.Obidos.server.model.PublicKeyCryptoProperties;

public interface Encryption {
	@FunctionalInterface
	public interface KeyPairProcessor {
		void run(String publicKey, String privateKey, String nonce, String salt);
	}

	@FunctionalInterface
	public interface PublicKeyEncryptor {
		byte[] encrypt(byte[] plaintext);
	}

	@FunctionalInterface
	public interface PublicKeyDecryptor {
		byte[] decrypt(byte[] cipherText, int encryptionMode);
	}

	@FunctionalInterface
	public interface SecretKeyComponentsProcessor {
		void lambda(byte[] passphrase, byte[] nonce, byte[] salt);
	}

	public interface SecretKeyEncryptor {
		byte[] encrypt(String plaintext);
		String decrypt(byte[] cipherText);
		void supplySecretKeyComponents(SecretKeyComponentsProcessor processor);
	}

	/**
	 * Secrets are encrypted with the public key of a keypair.  No passphrase is required for this.
	 * The public key is not encrypted.
	 **/
	PublicKeyEncryptor createPublicKeyEncryptor(final HasPublicKey keySupplier);

	/**
	 * The private key of a keypair is used to decrypt secrets. It is stored encrypted so details like
	 * passphrase, nonce, salt are required to decrypt it.
	 **/
	PublicKeyDecryptor createPublicKeyDecryptor(PublicKeyCryptoProperties pkp, PassphraseHash passphrase);
	SecretKeyEncryptor createSecretKeyEncryptor();
	SecretKeyEncryptor createSecretKeyEncryptor(String passphrase, String nonce, String salt);
	void validatePassphrase(PublicKeyCryptoProperties pkp, PassphraseHash passphraseHash);

	/**
	 * We do not save pass phrase or the key. We re-generate the key again from the specified
	 * pass pass phrase with this salt and other hard coded parameters generate a nonce for
	 * encrypting the private key. This nonce must be saved as well with the encrypted private
	 * key generate salt for key generation from pass phrase.
	 *
	 * @param passPhrase
	 * @param processor This does something with the generated keypair info (probably save to user
	 * record in database).
	 */
	Void createKeypair(byte[] passPhrase,  KeyPairProcessor processor);

	/**
	 * Returns a private key re-encrypted with a new passphrase.
	 *
	 * @param privateKey
	 * @param oldPassphrase
	 * @param newPassphrase
	 * @param nonce
	 * @param salt
	 * @return
	 */
	Void updatePrivateKey(byte[] privateKey, byte[] newPassphrase, KeyPairProcessor processor);

	byte[] decryptPrivateKey(PublicKeyCryptoProperties pkp, PassphraseHash passphraseHash);

	/**
	 * Computes the passphrase hash and also zeros out the passphrase.
	 *
	 * @param passphrase
	 * @return
	 */
	PassphraseHash secureHashCompute(PublicKeyCryptoProperties pkp, byte[] passphrase);
}
