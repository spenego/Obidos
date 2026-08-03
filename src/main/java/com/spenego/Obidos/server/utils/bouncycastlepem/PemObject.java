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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A generic PEM object - type, header properties, and byte content.
 */
public class PemObject implements PemObjectGenerator {
	private static List<PemHeader> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>());
	private String type;
	private List<PemHeader> headers;
	private byte[] content;

	/**
	 * Generic constructor for object without headers.
	 *
	 * @param type
	 *            pem object type.
	 * @param content
	 *            the binary content of the object.
	 */
	public PemObject(String type, byte[] content) {
		this.type = type;
		this.headers = EMPTY_LIST;
		this.content = content;
	}

	/**
	 * Generic constructor for object with headers.
	 *
	 * @param type
	 *            pem object type.
	 * @param headers
	 *            a list of PemHeader objects.
	 * @param content
	 *            the binary content of the object.
	 */
	public PemObject(String type, List<PemHeader> headers, byte[] content) {
		this.type = type;
		this.headers = Collections.unmodifiableList(headers);
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public List<?> getHeaders() {
		return headers;
	}

	public byte[] getContent() {
		return content;
	}

	public PemObject generate() throws PemGenerationException {
		return this;
	}
}
