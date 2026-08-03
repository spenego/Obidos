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

package com.spenego.Obidos.client.application;

import com.gwtplatform.mvp.client.UiHandlers;
import com.spenego.Obidos.shared.dto.MessageDTO;

public interface ApplicationUiHandlers extends UiHandlers
{
	public void logout(MessageDTO messageDTO);
	public void showAboutPage();
	public void showCreateNewUserPage();
	public void showCreateLDAPSettingsPage();
	public void showCreateSMTPSettingsPage();
	public void showListDAPSettingsPage();
	public void showGenKeyPairPage();
	public void showNewContainerPager();
	public void showGeneratePasswordPage();
	public void showChangePasswordPage();
	public void showRegisterPassphrasePage();
	public void showChangePassphrasedPage();
	public void showForgotPassphrasePage();
	public void showUserSettingsPage();
	public void listActiveUsers();
	public void listLoggedInUserUsers();
	public void listLockedUsers();
	public void listDeletedUsers();
	public void listSmtpSettings();
	public void createNewAdmin();
	public void listAdmins();
	public void listLockedAdmins();
	public void listDeletedAdmins();
	public void searchUsers();
	public void showAdvancedUsersSearchPage();
	public void createNewNote();
	public void listMyNotes();
	public void listNotesSharedWithOthers();
	public void listNotesSharedWithMe();
	public void createItem(); // just send to Container list view
	public void listMyItems();
	public void listItemsSharedWithMe();
	public void listMyItemsSharedWithOthers();
	public void showCreatePersonalTemplateView();
	public void showListPersonalTemplatesView();
	public void showCreateGlobalTemplateView();
	public void showListGlobalTemplatesView();
	public void showCreateNewGroupPage();
	void showListGroupPage();
	public void listMyContainers();
	public void listMyContainersSharedWithOthers();
	public void listContainersSharedWithMe();
	void showBuildInfoPage();
	void showAdminConsole();
	void showSessionInfoPage();
	void showPickNotificationTemplatesPage();
	void showTwoFactorPage();
	void pop2FAAuthenticatorModal();
	void notesHelp();
	void itemsHelp();
	void containersHelp();
	void templatesHelp();
	void groupsHelp();
	void showNotifications();
	/* callbacks and menu anchor buttons */
	void adminAnchorButtonCallback();
	void showAuditTableReport();
	void showAuditLogs();
	void showMyCapabilities();
	
	// Nav Admins help
	void showNavAdminsHelp();
	void showNavUsersHelp();
	void showNavSettingsHelp();
	void showNavReportsHelp();

	// System Settings
	void showSystemSettingsPage();
	
	void showSmsSettingsPage();
	
	
	// Certificate
	void showViewCertificatesPage();
	void showInstallObidosCertificatePage();
	void showInstallAdLdapServerPage();
	
	// License
	void installLicense();
	void displayLicense();
}
