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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.spenego.Obidos.shared.HasName;

public final class UserDefinedTypeValueDTO extends BaseDTO implements Clearable, HasName, Serializable {
	private static final long serialVersionUID = 1L;

	private String name; // this is the name given to the User Defined Type (not stored with the UDT value - output only param)
	private Long userDefinedTypeId;
	private Long userId;
	private ArrayList<UserDefinedFieldValueDTO> fieldValues;

	public boolean isNote() {
		return userDefinedTypeId.equals(UserDefinedTypeDTO.NOTES_ID);
	}

	public UserDefinedTypeValueDTO() {
	}

	public UserDefinedTypeValueDTO(final Long userDefinedTypeId) {
		this.userDefinedTypeId = userDefinedTypeId;
	}

	@Override
	public void clear() {
		if (fieldValues != null) {
			if (!fieldValues.isEmpty()) {
				fieldValues.forEach(v -> { if (v != null) { v.clear();}});
			}
			fieldValues = null;
		}
		userId = userDefinedTypeId = null;
		name = null;
		super.clear();
	}

	public UserDefinedTypeValueDTO(final Long userDefinedTypeId, final Collection<UserDefinedFieldValueDTO> fieldValues) {
		this.userDefinedTypeId = userDefinedTypeId;
		this.fieldValues = new ArrayList<>(fieldValues);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getUserDefinedTypeId() {
		return userDefinedTypeId;
	}
	public void setUserDefinedTypeId(final Long userDefinedTypeId) {
		this.userDefinedTypeId = userDefinedTypeId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<UserDefinedFieldValueDTO> getFieldValues() {
		return fieldValues;
	}
	public void setFieldValues(final Collection<UserDefinedFieldValueDTO> fieldValues) {
		this.fieldValues = new ArrayList<>(fieldValues);
	}
	public void addFieldValue(final UserDefinedFieldValueDTO fieldValueDTO) {
		if (fieldValues == null) {
			fieldValues = new ArrayList<>();
		}
		fieldValues.add(fieldValueDTO);
	}
}
