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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.spenego.Obidos.shared.Selectable;

public final class UserDefinedTypeDTO extends BaseDTO implements HasId, Serializable, Selectable {
	private static final long serialVersionUID = 1L;

	public static final long NOTES_ID					= 1001L;
	public static final long BANK_ACCOUNT_ID			= 1000L;
	public static final long CONTACT_INFO_ID			= 1002L;
	public static final long CREDENTIALS_ID				= 1003L;
	public static final long CREDIT_CARD_ID				= 1004L;
	public static final long INSURANCE_DETAILS_ID		= 1005L;
	public static final long SOFTWARE_LICENSE_ID		= 1006L;
	public static final long SUBSCRIPTION_ID			= 1007L;
	public static final long SUPPORT_CONTRACT_ID		= 1008L;
	public static final long WIFI_PASSWORD_ID			= 1009L;
	public static final long WEBSITE_ID					= 1010L;
	public static final long QRCODE_2FA_ID				= 1011L;
	public static final long FILE_UPLOAD_ID				= 1012L;
	public static final long MAX_SPENEGO_DEFINED_TYPE	= MIN_DYNAMIC_ID;

	private Boolean   	personal;
	private Boolean		global;
	private Boolean		adHoc;
	private Boolean		selected;
	private Long		userId;

	@NotNull(message="Please specify the name")
	@NotBlank(message="Please specify the name")
	@Pattern(regexp="\\b[a-zA-Z][a-zA-Z0-9\\-._]{1,}\\b", message="Invalid name")
	private String 		name;
	private ArrayList<UserDefinedFieldDTO> fields;

	public UserDefinedTypeDTO() {}

	public UserDefinedTypeDTO(final Long id) {
		super(id);
	}

	public UserDefinedTypeDTO(final String name) {
		this.name = name;
	}

	public UserDefinedTypeDTO(final Long id, final String name) {
		super(id);
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void clear() {
		userId = null;
		personal = global = adHoc = selected = null;
		super.clear();
	}

	public boolean containsQRCode() {
		for(final UserDefinedFieldDTO f : fields) {
			if (f.isQRCode()) {
				return true;
			}
		}
		return false;
	}

	public boolean containsDocument() {
		for(final UserDefinedFieldDTO f : fields) {
			if (f.isDocument()) {
				return true;
			}
		}
		return false;
	}

	public void addField(final UserDefinedFieldDTO field) {
		if (fields == null) {
			fields = new ArrayList<>();
		}
		fields.add(field);
	}
	
	public void removeField(final UserDefinedFieldDTO field) {
		if (fields != null) {
			fields.remove(field);
		}
	}

	public Boolean	getPersonal()						{ return personal; }
	public void		setPersonal(final Boolean personal) { this.personal = personal; }
	public String	getName()							{ return name; }
	public void		setName(final String name)			{ this.name = name; }
	public List<UserDefinedFieldDTO> getFields()		{ return fields; }
	public void		setFields(final Collection<UserDefinedFieldDTO> fields) { this.fields = new ArrayList<>(fields); }
	public Long		getUserId()							{ return userId; }
	public void		setUserId(Long userId)				{ this.userId = userId; }
	public Boolean	getGlobal()							{ return global; }
	public void		setGlobal(Boolean global)			{ this.global = global; }
	public boolean	isOwner(final Long id)				{ return userId.equals(id); }
	public Boolean	getAdHoc()							{ return adHoc; }
	public void		setAdHoc(final Boolean adHoc)		{ this.adHoc = adHoc; }
	@Override
	public Boolean	getSelected()						{ return selected; }
	@Override
	public void		setSelected(Boolean selected)		{ this.selected = selected; }
}
