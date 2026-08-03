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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Data sent to this output stream is encrypted and sent to out.
 *
 * Data is not completely flushed until the stream is closed.
 *
 * @author mmorgan
 *
 */
public final class EncryptedOutputStream extends OutputStream  {
	private final OutputStream	out;
	private final Encryptor		encryptor;
	private final byte[]		buffer;
	private int					offset;
	private long				totalBytesRead;	// we keep track of this so we know how much padding to strip when decrypting

	@FunctionalInterface
	public interface Encryptor {
		byte[] encrypt(byte[] b, int bytesInBuffer) throws IOException;
	}

	/**
	 * Creates an output stream that sends encrypted data to out.
	 *
	 * @param out         The encrypted data is passed to this output stream.
	 * @param encryptor   A function used to encrypt data.
	 * @param bufferSize  The size of the buffer created by this object. Buffers of this size will be sent to the encryptor.
	 */
	public EncryptedOutputStream(final OutputStream out, final Encryptor encryptor, final int bufferSize) {
		this.out = out;
		this.encryptor = encryptor;
		buffer = new byte[bufferSize];
		offset = 0;
		totalBytesRead = 0;
	}

	private void write() throws IOException {
		out.write(encryptor.encrypt(buffer, offset));
		offset = 0;
	}

	@Override
	public void close() throws IOException {
		try {
			if (offset > 0) {
				write();
			}
		} finally {			// need to close output stream even if the write throws an exception
			out.close();
			Arrays.fill(buffer, (byte) 0);
		}
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	public long getTotalBytesRead() {
		return totalBytesRead;
	}

	@Override
	public void write(int b) throws IOException {
		final byte[] bb = new byte[1];

		bb[0] = (byte) b;

		write(bb, 0, 1);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		int bytesCopied = 0;
		int bytesLeftToCopy = len;

		totalBytesRead += len;

		while(bytesCopied < len) {
			int bufferLeft = buffer.length - offset;
			int bytesToCopy = bytesLeftToCopy > bufferLeft ? bufferLeft : bytesLeftToCopy;

			System.arraycopy(b, off + bytesCopied, buffer, offset, bytesToCopy);
			offset += bytesToCopy;
			bytesCopied += bytesToCopy;
			bytesLeftToCopy -= bytesToCopy;

			if (offset == buffer.length) {
				write();
			}
		}
	}
}
