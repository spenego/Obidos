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
import java.util.Date;
import java.util.List;

/**
 * The {@code ItemDTO} class represents the client exposed fundamental
 * unit of sharing in the Obidos system.  Items are shared via
 * containers, groups, and directly with other users.
 *
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.shared.dto.ItemExpiration
 * @see     com.spenego.Obidos.shared.dto.ItemsResult
 * @see     com.spenego.Obidos.shared.dto.LimitedItemDTO
 * @since   Obidos1.0
*/

public class ItemDTO extends LimitedItemDTO implements Clearable, Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean			deleted;
	private Integer			securityClassification;
	private Date			createdAt;
	private Date			updatedAt;
	private Boolean			isContainerPrivate;
	private Boolean			clearExpireTime;
	private PermissionDTO	permissions;
	private ArrayList<UserDefinedTypeValueDTO> values; // needs to be concrete type per GWT recommendations

	public ItemDTO() {}

	public ItemDTO(final Long id, final String name, final Collection<UserDefinedTypeValueDTO> values) {
		super(id, name);
		this.values = new ArrayList<>(values);
	}

	public ItemDTO(final Long id, final String name) { super(id, name); }

	@Override
	public void clear() {
		if (values != null) {
			values.forEach(Clearable::clear);
			values = null;
		}
		if (permissions != null) {
			permissions.clear();
			permissions = null;
		}
		clearExpireTime = isContainerPrivate = null;
		updatedAt = createdAt = null;
		securityClassification = null;
		deleted = null;
		super.clear();
	}

	@Override
	public String toString() { return getContainerName() != null ? (getName() + " in container " + getContainerName()) : getName(); }

	private static boolean fieldsExist(final Collection<UserDefinedTypeValueDTO> values) {
		for(final UserDefinedTypeValueDTO dto : values) {
			if (dto.getFieldValues() != null && !dto.getFieldValues().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static final boolean containsType(final List<UserDefinedTypeValueDTO> values, final long type) {
		for(final UserDefinedTypeValueDTO v : values) {
			if (v.getFieldValues() != null) {
				for(final UserDefinedFieldValueDTO f : v.getFieldValues()) {
					final Long t = f.getType();
					if (t != null && type == t.longValue()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean containsValues()					{ return values != null && !values.isEmpty() && fieldsExist(values); }
	public boolean containsType(long type)			{ return containsType(values, type); }
	public boolean containsDocument()				{ return values != null && containsType(UserDefinedFieldDTO.TYPE_DOCUMENT); }
	public boolean containsQRCode()					{ return containsQRCode(values); }
	public Boolean getDeleted()						{ return deleted; }
	public Date getCreatedAt()						{ return createdAt; }
	public Date getUpdatedAt()						{ return updatedAt; }
	public Boolean getIsContainerPrivate()			{ return isContainerPrivate; }
	public Boolean isExpireTimeCleared()			{ return clearExpireTime; }
	public Boolean getClearExpireTime()				{ return isExpireTimeCleared(); }
	public PermissionDTO getPermissions()			{ return permissions; }
	public Integer getSecurityClassification()		{ return securityClassification; }
	public List<UserDefinedTypeValueDTO> getValues(){ return values; }
	public static boolean containsQRCode(final List<UserDefinedTypeValueDTO> values) { return values != null && containsType(values, UserDefinedFieldDTO.TYPE_QRCODE); }

	/**
	 * The Client will set this when the user wishes to clear or remove an expiration time from an item.
	 * @return
	 */
	public void clearExpireTime()											{ this.clearExpireTime = true; }
	public void clearValues()												{ this.values = null; }
	public void setDeleted(final Boolean deleted)							{ this.deleted = deleted; }
	public void setCreatedAt(final Date createdAt)							{ this.createdAt = createdAt; }
	public void setUpdatedAt(final Date updatedAt)							{ this.updatedAt = updatedAt; }
	public void setClearExpireTime(Boolean clearExpireTime)					{ this.clearExpireTime = clearExpireTime; }
	public void setPermissions(final PermissionDTO permissions)				{ this.permissions = permissions; }
	public void setIsContainerPrivate(final Boolean isPrivate)				{ this.isContainerPrivate = isPrivate; }
	public void setSecurityClassification(Integer securityClassification)	{ this.securityClassification = securityClassification; }
	public void setValues(final Collection<UserDefinedTypeValueDTO> values)	{ this.values = values == null ? null : toArrayList(values);}
}