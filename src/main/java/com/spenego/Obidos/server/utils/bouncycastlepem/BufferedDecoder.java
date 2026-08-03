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

package com.spenego.Obidos.server.utils.bouncycastlepem;

/**
 * A buffering class to allow translation from one format to another to be done
 * in discrete chunks.
 */
public class BufferedDecoder {
	protected byte[] buf;
	protected int bufOff;

	protected Translator translator;

	/**
	 * @param translator
	 *            the translator to use.
	 * @param bufSize
	 *            amount of input to buffer for each chunk.
	 */
	public BufferedDecoder(Translator translator, int bufSize) {
		this.translator = translator;

		if ((bufSize % translator.getEncodedBlockSize()) != 0) {
			throw new IllegalArgumentException("buffer size not multiple of input block size");
		}

		buf = new byte[bufSize];
		bufOff = 0;
	}

	public int processByte(byte in, byte[] out, int outOff) {
		int resultLen = 0;

		buf[bufOff++] = in;

		if (bufOff == buf.length) {
			resultLen = translator.decode(buf, 0, buf.length, out, outOff);
			bufOff = 0;
		}

		return resultLen;
	}

	public int processBytes(byte[] in, int inoff, int inlen, byte[] out, int outoff) {
		int inOff = inoff;
		int len = inlen;
		int outOff = outoff;
		
		if (len < 0) {
			throw new IllegalArgumentException("Can't have a negative input length!");
		}

		int resultLen = 0;
		int gapLen = buf.length - bufOff;

		if (len > gapLen) {
			System.arraycopy(in, inOff, buf, bufOff, gapLen);

			resultLen += translator.decode(buf, 0, buf.length, out, outOff);

			bufOff = 0;

			len -= gapLen;
			inOff += gapLen;
			outOff += resultLen;

			int chunkSize = len - (len % buf.length);

			resultLen += translator.decode(in, inOff, chunkSize, out, outOff);

			len -= chunkSize;
			inOff += chunkSize;
		}

		if (len != 0) {
			System.arraycopy(in, inOff, buf, bufOff, len);

			bufOff += len;
		}

		return resultLen;
	}
}
