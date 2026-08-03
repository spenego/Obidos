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

package com.spenego.Obidos.server.model;

import static com.spenego.Obidos.shared.dto.CapabilityDTO.CHANGE_ADMIN_CREDENTIALS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CHANGE_USER_CREDENTIALS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_ADMIN;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_GLOBAL_GROUPS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_GLOBAL_TEMPLATES;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_USER;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.DELETE_ADMIN;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.DELETE_USER;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.LOCK_ADMIN;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.LOCK_USER;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.MODIFY_EMAIL_TEMPLATES;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.MODIFY_SETTINGS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.ROOT_ADMIN;

import java.io.Serializable;

import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.Clearable;

public final class Capability extends BaseModel implements Clearable, Model, Serializable {
	private static final long serialVersionUID = 1L;
	private Long capabilities;

	public Capability() {
		capabilities = 0L;
	}

	public Capability(final Long id) {
		super(null, id);
		capabilities = 0L;
		setAll(true);
	}

	public Capability(final Long id, final Long rawCapabilities) {
		super(null, id);
		capabilities = rawCapabilities;
	}

	public String hex() {
		return Long.toHexString(capabilities);
	}

	@Override
	public String getName() {
		return null;
	}

	public void setAll(Boolean val) {
		capabilities = Boolean.TRUE.equals(val) ? Long.MAX_VALUE : 0L;
	}

	@Override
	public void clear() {
		setAll(null);
		super.clear();
	}

	private boolean valueModified(final long flag, final Capability prev) { return ((capabilities & flag) ^ (prev.capabilities & flag)) != 0; }

	private boolean isSet(final long flag) { return (flag & capabilities) != 0; }

	private void set(final boolean v, final long flag) {
		if (capabilities == null) {
			capabilities = 0L;
		}

		if (v) {
			capabilities |= flag;
		} else {
			capabilities &= ~flag;
		}
	}

	public Long getCapabilities() { return capabilities; }
	public void setCapabilities(Long capabilities)	{ this.capabilities = capabilities; }

	public boolean getRootAdmin()				{ return isSet(ROOT_ADMIN); }
	public boolean getCreateUser()				{ return isSet(CapabilityDTO.CREATE_USER); }
	public boolean getCreateAdmin()				{ return isSet(CapabilityDTO.CREATE_ADMIN); }
	public boolean getDeleteUser()				{ return isSet(CapabilityDTO.DELETE_USER); }
	public boolean getDeleteAdmin()				{ return isSet(DELETE_ADMIN); }
	public boolean getLockUser()				{ return isSet(LOCK_USER); }
	public boolean getLockAdmin()				{ return isSet(LOCK_ADMIN); }
	public boolean getChangeUserCredentials()	{ return isSet(CHANGE_USER_CREDENTIALS); }
	public boolean getChangeAdminCredentials()	{ return isSet(CHANGE_ADMIN_CREDENTIALS); }
	public boolean getCreateGlobalTemplate()	{ return isSet(CREATE_GLOBAL_TEMPLATES); }
	public boolean getModifyEmailTemplates()	{ return isSet(MODIFY_EMAIL_TEMPLATES); }
	public boolean getCreateGlobalGroups()		{ return isSet(CREATE_GLOBAL_GROUPS); }
	public boolean getModifySettings()			{ return isSet(MODIFY_SETTINGS); }

	public void setRootAdmin(boolean value)					{ set(value, ROOT_ADMIN); }
	public void setCreateUser(boolean value)				{ set(value, CREATE_USER); }
	public void setCreateAdmin(boolean value)				{ set(value, CREATE_ADMIN); }
	public void setDeleteUser(boolean value)				{ set(value, DELETE_USER); }
	public void setDeleteAdmin(boolean value)				{ set(value, DELETE_ADMIN); }
	public void setLockUser(boolean value)					{ set(value, LOCK_USER); }
	public void setLockAdmin(Boolean value)					{ set(value, LOCK_ADMIN); }
	public void setChangeUserCredentials(boolean value)		{ set(value, CHANGE_USER_CREDENTIALS); }
	public void setChangeAdminCredentials(boolean value)	{ set(value, CHANGE_ADMIN_CREDENTIALS); }
	public void setCreateGlobalTemplate(boolean value)		{ set(value, CREATE_GLOBAL_TEMPLATES); }
	public void setModifyEmailTemplates(final boolean value){ set(value, MODIFY_EMAIL_TEMPLATES); }
	public void setCreateGlobalGroups(boolean value)		{ set(value, CREATE_GLOBAL_GROUPS); }
	public void setModifySettings(final boolean value)		{ set(value, MODIFY_SETTINGS); }

	public boolean createUserModified(final Capability prev)			{ return valueModified(CREATE_USER, prev); }
	public boolean createAdminModified(final Capability prev)			{ return valueModified(CREATE_ADMIN, prev); }
	public boolean deleteUserModified(final Capability prev)			{ return valueModified(DELETE_USER, prev); }
	public boolean deleteAdminModified(final Capability prev)			{ return valueModified(DELETE_ADMIN, prev); }
	public boolean lockUserModified(final Capability prev)				{ return valueModified(LOCK_USER, prev); }
	public boolean lockAdminModified(final Capability prev)				{ return valueModified(LOCK_ADMIN, prev); }
	public boolean changeUserCredentialsModified(final Capability prev) { return valueModified(CHANGE_USER_CREDENTIALS, prev); }
	public boolean changeAdminCredentialsModified(final Capability prev){ return valueModified(CHANGE_ADMIN_CREDENTIALS, prev); }
	public boolean changeCreateGlobalTemplate(final Capability prev)	{ return valueModified(CREATE_GLOBAL_TEMPLATES, prev); }
	public boolean changeRootAdmin(final Capability prev)				{ return valueModified(ROOT_ADMIN, prev); }
	public boolean changeCreateGlobalGroups(final Capability prev)		{ return valueModified(CREATE_GLOBAL_GROUPS, prev); }
	public boolean changeModifySettings(final Capability prev)			{ return valueModified(MODIFY_SETTINGS, prev); }
}
