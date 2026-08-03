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

package com.spenego.Obidos.server.utils;

import static com.spenego.Obidos.shared.ObidosConstants.ENCRYPTION_BLOCK_OVERHEAD;
import static com.spenego.Obidos.shared.ObidosConstants.ENCRYPTION_BLOCK_SIZE;

import java.io.IOException;
import java.util.Arrays;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.shared.ObidosConstants;

/**
 * This class understands how encrypted documents are stored on disk.  Documents are stored in blocks of size (BUFFER_SIZE).
 * The last block stored is padded to fit this size.
 * This class will strip the appropriate amount of padding from a block if it is determined to be the
 * last block (bytesDecrypted > availableBytes)
 *
 * @author mmorgan
 *
 */
public final class DocumentDecryptor implements Decryptor {
	public static final int BUFFER_SIZE = ENCRYPTION_BLOCK_SIZE + ENCRYPTION_BLOCK_OVERHEAD;
	private final PublicKeyDecryptor pkd;
	private final SodiumKeyPair kp;
	private long bytesDecrypted;
	private long availableBytes;

	/**
	 * Implements a Decryptor that is used to decrypt Documents (files stored on disk). The details of how the file was
	 * encrypted and how it was stored on disk and potential padding are known by this implementation.
	 *
	 * @param pkd
	 * @param availableBytes The number of bytes that have been encrypted. This determines how many times decrypt can be invoked.
	 *                       availableBytes is expected to be encrypted. It is decrypted via the supplied PublicKeyDecryptor.
	 * @param publicKey      The public key used to
	 * @param decryptionKey  The key used to decrypt the Document (file).
	 */
	public DocumentDecryptor(final PublicKeyDecryptor pkd, final byte[] availableBytes, final byte[] publicKey, final byte[] decryptionKey) {
		this.pkd = pkd;
		this.bytesDecrypted = 0;
		this.availableBytes = Long.parseLong(new String(decrypt(availableBytes)));
		this.kp = new SodiumKeyPair(publicKey, decrypt(decryptionKey));
	}

	private static void clear(final byte[] b) {
		if (b != null) {
			Arrays.fill(b, (byte) 0);
		}
	}

	private static void clear(final SodiumKeyPair kp) {
		if (kp != null) {
			clear(kp.getPrivateKey());
			clear(kp.getPublicKey());
		}
	}

	public void clear() {
		clear(kp);
	}

	public byte[] decrypt(final byte[] data) {
		return pkd.decrypt(data, ObidosConstants.ENCRYPTION_MODE_PADDED2);
	}

	private byte[] paddingStripped(final byte[] data) {
		int len = data.length - (int) (bytesDecrypted - availableBytes);
		final byte[] bb = new byte[len];

		System.arraycopy(data, 0, bb, 0, len);
		Arrays.fill(data, (byte) 0);

		return bb;
	}

	// Encrypted streams are only written in blocks of ENCRYPTION_BLOCK_SIZE + ENCRYPTION_BLOCK_OVERHEAD bytes
	@Override
	public byte[] decrypt(final byte[] data, final int len) throws IOException {
		if (len != BUFFER_SIZE) {
			throw new IOException("Truncated Buffer"); // encrypted streams are always written in blocks of ENCRYPTION_BLOCK_SIZE + ENCRYPTION_BLOCK_OVERHEAD
		}

		try {
			final byte[] b = ServerUtils.cryptoBoxSealOpen(data, kp);
			bytesDecrypted += b.length;

			return (bytesDecrypted > availableBytes) ? paddingStripped(b) : b;
		} catch (final SodiumLibraryException ex) {
			throw new IOException(ex);
		}
	}
}
