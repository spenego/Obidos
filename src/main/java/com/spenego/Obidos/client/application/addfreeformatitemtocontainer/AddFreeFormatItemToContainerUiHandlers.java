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

package com.spenego.Obidos.client.application.addfreeformatitemtocontainer;

import com.gwtplatform.mvp.client.UiHandlers;

interface AddFreeFormatItemToContainerUiHandlers extends UiHandlers
{
    void navigateToListContainers();
    void resetForm();
    void addItemToContainer();
    void publicRadioCallback();
    void privateRadioCallback();
    void help();
    void listItems();
    void back();
    void showQrCodeTextBoxes();
    void processQRCodeImageFile();
    void resetQRCodeInfo();
    void showQRCodeInfoWidgets();
    void showAttachmentFileUploadWidget();
    void show2FACodeDialog();
    void generateTotpOtpAuthUri();
	void languageListBoxCallback();
}