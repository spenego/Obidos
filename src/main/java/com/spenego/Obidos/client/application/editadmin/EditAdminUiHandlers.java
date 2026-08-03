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

package com.spenego.Obidos.client.application.editadmin;

import com.gwtplatform.mvp.client.UiHandlers;

interface EditAdminUiHandlers extends UiHandlers
{
	void save();
	void reset();
	void help();
	
	void showUsernameChange();
	void showFullnameChange();
	void showEmailChange();
	void showPhoneChange();
	void showPasswordChange();
	
	void showPromoteToRotoAdminSwitchChange();
	void showCreateUsersChange();
	void showCreateAdminsChange();
	void showDeleteUsersChange();
	void showDeleteAdminsChange();
	void showLockUsersChange();
	void showLockAdminsChange();
	void showChangeUsersCredentialsChange();
	void showChangeAdminsCredentialsChange();
	void showModifyEmailTemplateChange();
	void showUploadProfilePicPage();
	void showDeleteProfilePicChagne();
	void showModifySettingsChagne();
	void back();
}