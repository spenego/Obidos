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

package com.spenego.Obidos.client.application.usersettings;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.gwtplatform.mvp.client.UiHandlers;

interface UserSettingsUiHandlers extends UiHandlers
{
	void help();
	void save();
	void reset();
	
	void showFullnameChange(KeyUpEvent e);
	void showAltEmailChagne(KeyUpEvent e);
	void showPhoneChange(KeyUpEvent e);
	void showMobilePhoneChange(KeyUpEvent e);
	void showOfficeChange(KeyUpEvent e);
	void showFacebookChange(KeyUpEvent e);
	void showTwitterChange(KeyUpEvent e);
	void showAcceptSmsChange();
	void showDefaultPageChange();
	void showAcceptNotifiationChange();
	void showDeleteProfilePicChagne();
	void showHideItemSecondsChange();
	void showUploadProfilePicPage();
	void countryCodeSelectCallback();
	
	void highContractSwitchCallback();
	void largetTextSwitchCallback();
}