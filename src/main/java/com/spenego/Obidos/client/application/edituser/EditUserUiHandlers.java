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

package com.spenego.Obidos.client.application.edituser;

import com.gwtplatform.mvp.client.UiHandlers;

interface EditUserUiHandlers extends UiHandlers
{
    void showUsernameChange();
    void showFullnameChange();
    void showPrimaryEmailChange();
    void showPrimaryPhoneChange();
    void showAuthSourceChange();
    void showResetUserPasswordChange();

    void showRequires2FAPasswordResetChange();
    void showIs2FAEnabledChange();
    void showReset2FAChange();
    void showCanCreateGlobalTemplateChange();

    void resetForm(String msg);
    void updateUser();
    void listUsers();
    void help();
    void back();
	void showUploadProfilePicPage();
	void showDeleteProfilePicChagne();
	void showPasswordAgeChange();
	void showCountryCodeSlectionChange();
	void countryCodeSelectCallback();
}
