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

public final class CapabilityDTO implements Clearable, Serializable {
	private static final long serialVersionUID = 1L;

	public static final long ROOT_ADMIN					= (1L <<  0);
	public static final long CREATE_USER				= (1L <<  1);
	public static final long CREATE_ADMIN				= (1L <<  2);	// Not used. Only Root Admins can create admins.
	public static final long DELETE_USER				= (1L <<  3);
	public static final long DELETE_ADMIN				= (1L <<  4);
	public static final long LOCK_USER					= (1L <<  5);
	public static final long LOCK_ADMIN					= (1L <<  6);
	public static final long CHANGE_USER_CREDENTIALS	= (1L <<  7);
	public static final long CHANGE_ADMIN_CREDENTIALS	= (1L <<  8);
	public static final long MODIFY_EMAIL_TEMPLATES		= (1L <<  9);
	public static final long CREATE_GLOBAL_TEMPLATES	= (1L << 10);
	public static final long CREATE_GLOBAL_GROUPS		= (1L << 11);
	public static final long MODIFY_SETTINGS			= (1L << 12); // Any/All settings under Admin -> Settings dropdown menu
	public static final long ALL_CAPABILTIES			= ROOT_ADMIN | CREATE_USER | CREATE_ADMIN | DELETE_USER |
														DELETE_ADMIN | LOCK_USER | LOCK_ADMIN	| CHANGE_USER_CREDENTIALS |
														CHANGE_ADMIN_CREDENTIALS | MODIFY_EMAIL_TEMPLATES	 |  CREATE_GLOBAL_TEMPLATES |
														CREATE_GLOBAL_GROUPS | MODIFY_SETTINGS;

	private Long id;
	private Long userId;
	private Long capabilities;

	public CapabilityDTO() {
		capabilities = 0L;
	}

	public CapabilityDTO(final Long userId) {
		this.userId = userId;
		capabilities = 0L;
	}

	public CapabilityDTO(final Long userId, final Long capabilities) {
		this.userId = userId;
		this.capabilities = capabilities;
	}

	public CapabilityDTO(final Long userId, final Boolean createGlobalTemplate) {
		this.userId = userId;
		capabilities = 0L;
		setCreateGlobalTemplate(createGlobalTemplate);
	}

	public String hex() {
		return Long.toHexString(capabilities);
	}

	@Override
	public void clear() {
		id = this.userId = null;
		capabilities = 0L;
	}

	public Long getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Long capabilities) {
		this.capabilities = capabilities;
	}

	public void disableAdminCapabilities() {
		capabilities &= CREATE_GLOBAL_TEMPLATES | CREATE_GLOBAL_GROUPS;
	}

	public void enableEverything() {
		this.capabilities = ALL_CAPABILTIES;
	}

	/**
	 * To prevent admins from creating admins that have more capabilities than themselves, we ensure this object
	 * has no more capabilities than limitingCapabilities.
	 *
	 * @param limitingCapabilities
	 */
	public void disableNonAuthorizedCapabilities(final CapabilityDTO limitingCapabilities) {
		if (!limitingCapabilities.getCreateUser())				{ setCreateUser(false); }
		if (!limitingCapabilities.getCreateAdmin())				{ setCreateAdmin(false); }
		if (!limitingCapabilities.getDeleteUser())				{ setDeleteUser(false); }
		if (!limitingCapabilities.getDeleteAdmin())				{ setDeleteAdmin(false); }
		if (!limitingCapabilities.getLockUser())				{ setLockUser(false); }
		if (!limitingCapabilities.getLockAdmin())				{ setLockAdmin(false); }
		if (!limitingCapabilities.getChangeUserCredentials())	{ setChangeUserCredentials(false); }
		if (!limitingCapabilities.getChangeAdminCredentials())	{ setChangeAdminCredentials(false); }
		if (!limitingCapabilities.getModifyEmailTemplates())	{ setModifyEmailTemplates(false); }
		if (!limitingCapabilities.getModifySettings())			{ setModifySettings(false); }
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getUserId() { return userId; }
	public void setUserId(final Long userId) { this.userId = userId; }

	private boolean isSet(final long flag) { return (flag & capabilities) != 0; }

	private void set(final boolean v, final long flag) {
		if (v) {
			capabilities |= flag;
		} else {
			capabilities &= ~flag;
		}
	}

	public boolean getRootAdmin()				{ return isSet(ROOT_ADMIN); }
	public boolean getCreateUser()				{ return isSet(CREATE_USER); }
	public boolean getCreateAdmin()				{ return isSet(CREATE_ADMIN); }
	public boolean getDeleteUser()				{ return isSet(DELETE_USER); }
	public boolean getDeleteAdmin()				{ return isSet(DELETE_ADMIN); }
	public boolean getLockUser()				{ return isSet(LOCK_USER); }
	public boolean getLockAdmin()				{ return isSet(LOCK_ADMIN); }
	public boolean getChangeUserCredentials()	{ return isSet(CHANGE_USER_CREDENTIALS); }
	public boolean getChangeAdminCredentials()	{ return isSet(CHANGE_ADMIN_CREDENTIALS); }
	public boolean getCreateGlobalTemplate()	{ return isSet(CREATE_GLOBAL_TEMPLATES); }
	public boolean getModifyEmailTemplates()	{ return isSet(MODIFY_EMAIL_TEMPLATES); }
	public boolean getCreateGlobalGroups()		{ return isSet(CREATE_GLOBAL_GROUPS); }
	public boolean getModifySettings()			{ return isSet(MODIFY_SETTINGS); }

	public void setRootAdmin(final boolean value)				{ set(value, ROOT_ADMIN); }
	public void setCreateUser(final boolean value)				{ set(value, CREATE_USER); }
	public void setCreateAdmin(final boolean value)				{ set(value, CREATE_ADMIN); }
	public void setDeleteUser(final boolean value)				{ set(value, DELETE_USER); }
	public void setDeleteAdmin(final boolean value)				{ set(value, DELETE_ADMIN); }
	public void setLockUser(final boolean value)				{ set(value, LOCK_USER); }
	public void setLockAdmin(final boolean value)				{ set(value, LOCK_ADMIN); }
	public void setChangeUserCredentials(final boolean value)	{ set(value, CHANGE_USER_CREDENTIALS); }
	public void setChangeAdminCredentials(final boolean value)	{ set(value, CHANGE_ADMIN_CREDENTIALS); }
	public void setCreateGlobalTemplate(final boolean value)	{ set(value, CREATE_GLOBAL_TEMPLATES); }
	public void setModifyEmailTemplates(final boolean value)	{ set(value, MODIFY_EMAIL_TEMPLATES); }
	public void setCreateGlobalGroups(final boolean value)		{ set(value, CREATE_GLOBAL_GROUPS); }
	public void setModifySettings(final boolean value)			{ set(value, MODIFY_SETTINGS); }
}
