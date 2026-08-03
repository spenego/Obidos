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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic PEM reader, based on the format outlined in RFC 1421
 */
public class PemReader extends BufferedReader {
	private static final String BEGIN = "-----BEGIN ";
	private static final String END = "-----END ";

	public PemReader(Reader reader) {
		super(reader);
	}

	/**
	 * Read the next PEM object as a blob of raw data with header information.
	 *
	 * @return the next object in the stream, null if no objects left.
	 * @throws IOException
	 *             in case of a parse error.
	 */
	public PemObject readPemObject() throws IOException {
		String line = readLine();

		while (line != null && !line.startsWith(BEGIN)) {
			line = readLine();
		}

		if (line != null) {
			line = line.substring(BEGIN.length());
			int index = line.indexOf('-');

			if (index > 0 && line.endsWith("-----") && (line.length() - index) == 5) {
				String type = line.substring(0, index);

				return loadObject(type);
			}
		}

		return null;
	}

	private PemObject loadObject(String type) throws IOException {
		String line;
		String endMarker = END + type;
		StringBuffer buf = new StringBuffer();
		List<PemHeader> headers = new ArrayList<PemHeader>();

		while ((line = readLine()) != null) {
			if (line.indexOf(":") >= 0) {
				int index = line.indexOf(':');
				String hdr = line.substring(0, index);
				String value = line.substring(index + 1).trim();

				headers.add(new PemHeader(hdr, value));

				continue;
			}

			if (line.indexOf(endMarker) != -1) {
				break;
			}

			buf.append(line.trim());
		}

		if (line == null) {
			throw new IOException(endMarker + " not found");
		}

		return new PemObject(type, headers, Base64.decode(buf.toString()));
	}

}
