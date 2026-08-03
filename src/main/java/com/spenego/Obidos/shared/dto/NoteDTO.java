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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;
import java.util.Arrays;

import org.hibernate.validator.constraints.Length;

public final class NoteDTO extends ItemDTO implements Clearable, Serializable
{
	private static final long serialVersionUID = 1L;

	@Length(max=45000, message = "Notes Notes must be less than 45k")
	private byte[] notes;

	public NoteDTO() {}

	public NoteDTO(final Long id, final String name) {
		super(id, name);
	}

	public NoteDTO(final Long id, final String name, final byte[] notes) {
		super(id, name);
		this.notes = notes;
	}

	@Override
	public void clear() {
		if (notes != null) {
			Arrays.fill(notes, (byte) 0);
			notes = null;
		}
		super.clear();
	}

	protected void finalize() {	// NOSONAR -- we know this may not be called for a long time.
		clear();
	}

	public final byte[] getNotes() {
		return notes;
	}

	public final void setNotes(final byte[] notes) {
		this.notes = notes;
	}
}
