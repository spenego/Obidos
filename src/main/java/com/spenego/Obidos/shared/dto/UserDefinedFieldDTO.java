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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.spenego.Obidos.shared.HasName;
import com.spenego.Obidos.shared.Selectable;

public final class UserDefinedFieldDTO extends BaseDTO implements HasName, Serializable, Selectable {
	private static final long serialVersionUID = 1L;

	public static final long TYPE_STRING	= 1;
	public static final long TYPE_INT		= 2;
	public static final long TYPE_BOOLEAN	= 3;
	public static final long TYPE_ENCRYPTED	= 4;
	public static final long TYPE_DOCUMENT	= 5;
	public static final long TYPE_QRCODE	= 6;

	public static final long TYPE_NOTES_FIELD_ID = 2000L; // Notes fields are hard-coded to 2000
	// any value > 100000000 is a UserDefinedType

	@NotNull(message="Please specify the name")
	@NotBlank(message="Please specify the name")
	@Pattern(regexp="\\b[a-zA-Z][a-zA-Z0-9\\-._]{1,}\\b", message="Invalid name")
	private String 		name;
	private Long		type;
	private Integer		position;
	private Long		typeId;
	private Boolean		delete;

	public UserDefinedFieldDTO() {}

	public UserDefinedFieldDTO(final Long id) { super(id); }

	public UserDefinedFieldDTO(final Long typeId, final Long type, final String name) {
		this.typeId = typeId;
		this.type = type;
		this.name = name;
	}

	public UserDefinedFieldDTO(final Long typeId, final Long type, final String name, final Integer position) {
		this(typeId, type, name);
		this.position = position;
	}

	@Override
	public void clear() {
		delete = null;
		type = typeId = null;
		position = null;
		name = null;
		super.clear();
	}

	@Override
	public boolean equals(final Object o) {
		if((o == null) || (getClass() != o.getClass())){
	        return false;
	    }
		final UserDefinedFieldDTO udf = (UserDefinedFieldDTO) o;
		return name != null && name.equals(udf.name) && type != null && type.equals(udf.type);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ type.hashCode();
	}

	public boolean	isUserDefinedType()			{ return type > 1000000L; }
	public boolean	isString()					{ return type.equals(TYPE_STRING); }
	public boolean	isInteger()					{ return type.equals(TYPE_INT); }
	public boolean	isBoolean()					{ return type.equals(TYPE_BOOLEAN); }
	public boolean	isEncrypted()				{ return type.equals(TYPE_ENCRYPTED); }
	public boolean	isDocument()				{ return type.equals(TYPE_DOCUMENT); }
	public boolean	isQRCode()					{ return type.equals(TYPE_QRCODE); }
	public Boolean	getDelete()					{ return delete; }
	public String	getName()					{ return name; }
	public Long		getType()					{ return type; }
	public Long		getTypeId()					{ return typeId; }
	public Integer	getPosition()				{ return position; }
	public void		setName(String name)		{ this.name = name; }
	public void		setPosition(Integer p)		{ this.position = p; }
	public void		setTypeId(Long typeId)		{ this.typeId = typeId; }
	public void		setDelete(Boolean delete)	{ this.delete = delete; }
	public void		setType(Long type)			{ this.type = type; }

	@Override
	public void setSelected(Boolean selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean getSelected() {
		// TODO Auto-generated method stub
		return null;
	}
}
