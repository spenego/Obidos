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

package com.spenego.Obidos.client.application.viewitem;

import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.gwtplatform.mvp.client.UiHandlers;

interface ViewItemUiHandlers extends UiHandlers
{
	void help();
	void showHideItem();
	void hideItemCheckBoxCallback();
	void takeOwnership();
	void listUsersItemIsSharedWithMe();
	void editItem();
	void emailOwner();
	void callOwner();
	void showOwnerDetailsPage();
	void back();
	void showContainerPage();
	void downloadFile();
	void gwtFileDownloadCompleteCallback(SubmitCompleteEvent e);
	void show2FAButtonCallback();
}