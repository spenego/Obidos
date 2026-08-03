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
import java.io.InputStream;
import java.util.Arrays;

/**
 * A class that decrypts a stream which was previously encrypted with EncryptedOutputStream.
 *
 * Since the encryption algorithm used is a block encryption cipher, the stream created by
 * EncryptedOutputStream consists of consecutive blocks of encrypted data.
 *
 * @author mmorgan
 *
 */
public final class DecryptedInputStream extends InputStream {
	private final InputStream	in;
	private final Decryptor		decryptor;
	private final byte[]		buffer;			// used when reading bytes from the underlying input stream, encryptedtext
	private final byte[]		byteBuffer;		// some outputstream types actually read 1 byte at a time
	private int					offset;			// index into output_buffer, used when supplying data during a read call
	private byte[]				outputBuffer;	// contains the plaintext

	/**
	 * Creates an input stream that decrypts data read from in.  Decrypted data is returned in calls to read.
	 *
	 * @param in          The underlying input stream. Decrypted data is sent to this input stream.
	 * @param decryptor   The function used to decrypt the data.
	 * @param bufferSize  This object will use a buffer of this size. It will not attempt to decrypt any data until this much data is read or the end of the input stream.
	 */
	public DecryptedInputStream(final InputStream in, final Decryptor decryptor, final int bufferSize) {
		this.in = in;
		this.decryptor = decryptor;
		buffer = new byte[bufferSize];
		byteBuffer = new byte[1];
		offset = 0;
	}

	@Override
	public void close() throws IOException {
		in.close();
		Arrays.fill(buffer, (byte) 0);
		byteBuffer[0] = 0;
		outputBuffer = null;
	}

	@Override
	public int read() throws IOException {
		if (read(byteBuffer) < 1) {
			throw new IOException("Unable to read at least one byte from input stream.");
		}
		return 0xff & byteBuffer[0];
	}

	private int fillOutputBuffer() throws IOException {
		int bytesRead = 0;

		while(bytesRead < buffer.length) {
			int len = in.read(buffer, bytesRead, buffer.length - bytesRead);
			if (len == -1) {
				if (bytesRead == 0) {
					return -1;
				}

				break;
			}
			bytesRead += len;
		}

		outputBuffer = decryptor.decrypt(buffer, bytesRead);

		return bytesRead;
	}

	private int drainOutputBuffer(final byte[] b, final int off, final int len) {
		final int bytesLeft = outputBuffer.length - offset;
		final int bytesToOutput = len > bytesLeft ? bytesLeft : len;

		System.arraycopy(outputBuffer, offset, b, off, bytesToOutput);
		offset += bytesToOutput;

		if (offset == outputBuffer.length) {
			Arrays.fill(outputBuffer, (byte) 0);
			outputBuffer = null;
			offset = 0;
		}

		return bytesToOutput;
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		int bytesRead = 0;
		int bytesToRead = len;

		while(bytesToRead > 0) {
			if (outputBuffer == null && fillOutputBuffer() == -1) {
				return bytesRead > 0 ? bytesRead : -1;
			}

			if (outputBuffer != null) {
				int bytesDrained = drainOutputBuffer(b, off + bytesRead, bytesToRead);
				bytesToRead -= bytesDrained;
				bytesRead += bytesDrained;
			}
		}

		return bytesRead;
	}
}
