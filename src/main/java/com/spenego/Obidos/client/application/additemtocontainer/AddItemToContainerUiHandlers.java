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

package com.spenego.Obidos.client.application.additemtocontainer;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.gwtplatform.mvp.client.UiHandlers;

interface AddItemToContainerUiHandlers extends UiHandlers
{
    void listContainers();
    void listItems();
    void resetForm();
    void showHideHelp();
    void addItemToContainer();
    void publicRadioCallback();
    void privateRadioCallback();
    void back();
    void showNameChange(KeyUpEvent e);
    void namePasted();
    void showAttachmentFileUploadWidget();
    void showQrCodeTextBoxes();
    void processQRCodeImageFile();
    void resetQRCodeInfo();
    void showQRCodeInfoWidgets();
    void show2FACodeDialog();
    void generateTotpOtpAuthUri();
	void languageListBoxCallback();
	
	void wifiSsidKeyUpHandler();
	void wifiPasswordKeyUpHandler();
	void wifiHiddenToggleSwitchHandler();
	void wifiEncryptionSelectHandler();
}