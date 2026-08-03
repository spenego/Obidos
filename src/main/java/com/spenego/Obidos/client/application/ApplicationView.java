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

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosNotificationBadge;
import com.spenego.Obidos.shared.dto.MessageDTO;

/**
 * Created by mk_gwtp_project.sh @Sun Dec 11 11:19:30 EST 2016
 * @author spgdev@spenego.com Sun Dec 11 11:19:30 EST 2016
 * The one created by GWTP Maven Archetype has a missing curly brace!
 */
//public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView
/**
 * @author spgdev@spenego.com - Jan 3, 2017
 *
 */
public class ApplicationView extends ViewWithUiHandlers<ApplicationUiHandlers> implements ApplicationPresenter.MyView
{
	interface Binder extends UiBinder<Widget, ApplicationView>
	{
	}

	@UiField
	ScrollPanel scrollPanel;

	@UiField
	Element busyField;

	@UiField
	SimplePanel main;

	@UiField
	Navbar navBar;

	@UiField
	NavbarBrand navBarBrand;

	@UiField
	NavbarCollapse navbarCollapse;

	// admin starts--
	@UiField
	ListDropDown adminsListDropDown;
	
	@UiField
	AnchorButton configurationAnchorButton;

	@UiField
	AnchorButton userAccountsAnchorButton;

	@UiField
	AnchorButton settingsAnchorButton;

	@UiField
	NavbarNav advancedSearchNavBarNav;
	
	@UiField
	AnchorButton auditAnchorButton;
	
	@UiField
	AnchorListItem auditReportAli;
	
	
	@UiField
	AnchorButton templates;

	@UiField
	AnchorListItem createNewGlobalTemplateAL;

	@UiField
	TextBox searchBox;

	@UiField
	Button searchButton;

	@UiField
	AnchorButton adminAnchorButton;

	@UiField
	AnchorListItem editNotificationTemplateItem;
	// admin ends--

	// user starts--

	@UiField
	AnchorButton notesAnchorButton;

	@UiField
	AnchorButton itemsAnchorButton;

	@UiField
	AnchorButton container;

	@UiField
	AnchorButton group;

	@UiField
	Label messageLabel;

	@UiField
	AnchorListItem changePassphrasedAnchorListItem;

	@UiField
	AnchorListItem generatePasswordAnchorListItem;

	@UiField
	AnchorListItem changePasswordAnchorListItem;

	@UiField
	AnchorListItem registerPassphrasedAnchorListItem;

	@UiField
	AnchorListItem forgotPassphrasedAnchorListItem;

	@UiField
	AnchorListItem userSettingsAnchorListItem;

	@UiField
	AnchorListItem twoFactorAnchorListItem;

	@UiField
	AnchorListItem twoFactorAnchorModalListItem;

	@UiField
	NavPills navNotificaionPill;

	@UiField
	AnchorListItem notificationALI;
	
	@UiField
	ListDropDown auditDropDown;
	
	@UiField
	AnchorListItem createNewAdmin;
	
	@UiField
	AnchorListItem listadmins;

	@UiField
	AnchorListItem listLockedAdminAnchorList;

	@UiField
	AnchorListItem listDeletedAdmins;

	@UiField
	AnchorListItem createNewUser;
	
	@UiField
	AnchorListItem myCapabilitiesALI;
	
	@UiField
	Divider changePassDividerStart;
	
	@UiField
	Divider changePassDividerEnd;
	
	@UiField
	Divider twoFADividerStart;

	@UiField
	ObidosNotificationBadge notificationBadge;
	
	@UiField
	AnchorListItem auditLogsAli;

	@UiField
	AnchorListItem listLoggedInUsersAnchorList;

	@UiField
	Divider ldivider1;

	@UiField
	Divider ldivider2;
	
	@UiField
	AnchorListItem displayLicense;

	@UiField
	AnchorListItem displayLicenseAdmin;

	// system setting
	@UiField
	AnchorListItem systemSettings;

	@UiField
	AnchorListItem createSMTPAnchorListItem;
	
	@UiField
	AnchorListItem smsSettings;
	
	@UiField
	AnchorListItem createAdldapsettings;

	@UiField
	AnchorListItem listAdldapsettings;

	@UiField
	AnchorListItem installCertificate;

	@UiField
	AnchorListItem installAdCertificate;
	
	@UiField
	AnchorListItem installLicense;

	@UiField
	AnchorListItem listLockedUsersAnchorList;
	
	@UiField
	AnchorListItem listDeletedUsersAnchorList;

	@UiField
	HTMLPanel processingPanel;

	@Inject
	ApplicationView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));

		bindSlot(ApplicationPresenter.SLOT_MAIN, main);
	}

	@UiHandler("createAdldapsettings")
	void onClickADLDAPSettings(ClickEvent e)
	{
		getUiHandlers().showCreateLDAPSettingsPage();
	}

	@UiHandler("createSMTPAnchorListItem")
	void onClickCreateSMTPSettings(ClickEvent e)
	{
		getUiHandlers().showCreateSMTPSettingsPage();
	}

	// General Settings menu for Admin Settings > General Settings in nav bar
	/*
	@UiHandler("settingsAnchorListItem")
	void onClicksettingsAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showAdminGeneralSettingsPage();
	}
	*/

	@UiHandler("listAdldapsettings")
	void onClientListADLDAPSettings(ClickEvent e)
	{
		getUiHandlers().showListDAPSettingsPage();
	}

	@UiHandler("createNewUser")
	void onClickNewUser(ClickEvent e)
	{
		getUiHandlers().showCreateNewUserPage();
	}

	@UiHandler("listusers")
	void onClickListUsers(ClickEvent e)
	{
		getUiHandlers().listActiveUsers();
	}

	@UiHandler("listLockedUsersAnchorList")
	void onClickListLockedUsers(ClickEvent e)
	{
		getUiHandlers().listLockedUsers();
	}

	@UiHandler("listDeletedUsersAnchorList")
	void onclickListDeletedUsersAnchorList(ClickEvent e)
	{
		getUiHandlers().listDeletedUsers();
	}

	@UiHandler("listSmtpSettingsAnchorListItem")
	void onClickListSmtpSettings(ClickEvent e)
	{
		getUiHandlers().listSmtpSettings();
	}

	@UiHandler("createNewAdmin")
	void onSelectNewAdmin(ClickEvent e)
	{
		getUiHandlers().createNewAdmin();
	}

	@UiHandler("listadmins")
	void onSelectListAdmins(ClickEvent e)
	{
		getUiHandlers().listAdmins();
	}

	@UiHandler("listDeletedAdmins")
	void onclickListDeletedAdmins (ClickEvent e)
	{
		getUiHandlers().listDeletedAdmins();
	}
	
	@UiHandler("listLockedAdminAnchorList")
	void onclickListLockedAdminAnchorList (ClickEvent e)
	{
		getUiHandlers().listLockedAdmins();
	}

	@UiHandler("listContainersAnchorList")
	void onSelectListContainers(ClickEvent e)
	{
		getUiHandlers().listMyContainers();
	}

	@UiHandler("createNoteAnchorList")
	void onSelectcreateNoteAnchorList(ClickEvent e)
	{
		getUiHandlers().createNewNote();
	}

	@UiHandler("listNotesAnchorList")
	void onSelectlistNotesAnchorList(ClickEvent e)
	{
		getUiHandlers().listMyNotes();
	}

	@UiHandler("listNotesSharedWithOthersAnchorList")
	void onSelectionListlistNotesSharedWithOthersAnchorList(ClickEvent e)
	{
		getUiHandlers().listNotesSharedWithOthers();
	}

	@UiHandler("listNotesSharedWithMeAnchorList")
	void onSelectionlistNotesSharedWithMeAnchorList(ClickEvent e)
	{
		getUiHandlers().listNotesSharedWithMe();
	}

	@UiHandler("notesHelpAnchorList")
	void onclickNotesHelpAnchorList(ClickEvent e)
	{
		getUiHandlers().notesHelp();
	}

	@UiHandler("itemsHelpAnchorList")
	void onclickItemsHelpAnchorList(ClickEvent e)
	{
		getUiHandlers().itemsHelp();
	}

	@UiHandler("containersHelpAnchorList")
	void onclickContainersHelpAnchorList(ClickEvent e)
	{
		getUiHandlers().containersHelp();
	}

	@UiHandler("templatesHelpAnchorList")
	void onclickTemplatesHelpAnchorList(ClickEvent e)
	{
		getUiHandlers().templatesHelp();
	}

	@UiHandler("groupHelpAnchorList")
	void onclickGroupAnchorList(ClickEvent e)
	{
		getUiHandlers().groupsHelp();
	}

	@UiHandler("createItemAnchorList")
	void onClickCreateItemAnchorList(ClickEvent e)
	{
		getUiHandlers().createItem();
	}

	@UiHandler("listItemsAnchorList")
	void onSelectlistItemsAnchorList(ClickEvent e)
	{
		getUiHandlers().listMyItems();
	}

	@UiHandler("listItemsSharedWithOthersAnchorList")
	void onSelectlistItemsSharedWithOthersAnchorList(ClickEvent e)
	{
		getUiHandlers().listMyItemsSharedWithOthers();
	}

	@UiHandler("listItemsSharedWithMeAnchorList")
	void onSelectlistItemsSharedWithMeAnchorList(ClickEvent e)
	{
		getUiHandlers().listItemsSharedWithMe();
	}

	@UiHandler("listMyContainersSharedWithOthersAnchorList")
	void onSelectionlistMyContainersSharedWithOthersAnchorList(ClickEvent e)
	{
		getUiHandlers().listMyContainersSharedWithOthers();
	}

	@UiHandler("listContainersSharedWithMeAnchorList")
	void onSelectionlistContainersSharedWithMeAnchorList(ClickEvent e)
	{
		getUiHandlers().listContainersSharedWithMe();
	}
	@UiHandler("listLoggedInUsersAnchorList")
	void onSelectionlistLoggedInUsersAnchorList(ClickEvent e)
	{
		getUiHandlers().listLoggedInUserUsers();
	}

	@UiHandler("searchBox")
	void onSearchBoxdKeyUp(KeyDownEvent e)
	{
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().searchUsers();
		}
	}

	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().searchUsers();
	}

	@UiHandler("advancedAnchorListItem")
	void onClickAdvancedSearch(ClickEvent e)
	{
		getUiHandlers().showAdvancedUsersSearchPage();
	}

	// -- user starts
	// @UiHandler("genKeyPair")
	// void onClickGenKeyPair(ClickEvent e)
	// {
	// getUiHandlers().showGenKeyPairPage();
	// }
	@UiHandler("newContainerAnchorList")
	void onClickNewContainerAnchorList(ClickEvent e)
	{
		getUiHandlers().showNewContainerPager();
	}

	@UiHandler("generatePasswordAnchorListItem")
	void onClickgeneratePasswordAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showGeneratePasswordPage();
	}

	@UiHandler("changePasswordAnchorListItem")
	void onClickChangePasswordAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showChangePasswordPage();
	}

	@UiHandler("forgotPassphrasedAnchorListItem")
	void onClickForgotPassphrasedAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showForgotPassphrasePage();
	}

	@UiHandler("userSettingsAnchorListItem")
	void onclickuserSettingsAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showUserSettingsPage();
	}

	@UiHandler("editNotificationTemplateItem")
	void onClickeditNotificationTemplateItem(ClickEvent e)
	{
		getUiHandlers().showPickNotificationTemplatesPage();
	}

	@UiHandler("twoFactorAnchorListItem")
	void onClickTwoFactorAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showTwoFactorPage();
	}
	
	@UiHandler("twoFactorAnchorModalListItem")
	void onClickTwoFactorAnchorModalListItem(ClickEvent e)
	{
		getUiHandlers().pop2FAAuthenticatorModal();
	}

	@UiHandler("registerPassphrasedAnchorListItem")
	void onClickregisterPassphrasedAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showRegisterPassphrasePage();
	}

	@UiHandler("changePassphrasedAnchorListItem")
	void onClickchangePassphrasedAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showChangePassphrasedPage();
	}

	@UiHandler("buildInfo")
	void onClickbuildInfo(ClickEvent e)
	{
		getUiHandlers().showBuildInfoPage();
	}
	
	@UiHandler("displayLicense")
	void onclickDisplayLicense (ClickEvent e)
	{
		getUiHandlers().displayLicense();
	}

	@UiHandler("displayLicenseAdmin")
	void onclickDisplayLicenseAdmin (ClickEvent e)
	{
		getUiHandlers().displayLicense();
	}

	@UiHandler("sessionInfo")
	void onClicksessionInfo(ClickEvent e)
	{
		getUiHandlers().showSessionInfoPage();
	}

	@UiHandler("menuItemLogout")
	void onClickLogout(ClickEvent e)
	{
		MessageDTO messageDTO = null;
		getUiHandlers().logout(messageDTO);
	}

	@UiHandler("navBarBrand")
	void onClickNavbarBrand(ClickEvent e)
	{
		// getUiHandlers().showAboutPage();
		// Fixes #189
		getUiHandlers().showAdminConsole();
	}

	@UiHandler("cnptAL")
	void onClickcnptAL(ClickEvent e)
	{
		getUiHandlers().showCreatePersonalTemplateView();
	}

	@UiHandler("lptAL")
	void onCLicklptAL(ClickEvent e)
	{
		getUiHandlers().showListPersonalTemplatesView();
	}

	@UiHandler("createNewGlobalTemplateAL")
	void onCLickCreateNewGlobalTemplateAL(ClickEvent e)
	{
		getUiHandlers().showCreateGlobalTemplateView();
	}

	@UiHandler("listGlobalTemplatesAL")
	void onClickListGlobalTemplatesAL(ClickEvent e)
	{
		getUiHandlers().showListGlobalTemplatesView();
	}

	@UiHandler("newGroupAnchorListItem")
	void onClicknewGroupAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showCreateNewGroupPage();
	}

	@UiHandler("listGroupAnchorListItem")
	void onClickistGroupAnchorListItem(ClickEvent e)
	{
		getUiHandlers().showListGroupPage();
	}

	@UiHandler("adminAnchorButton")
	void onclickAdminAnchorButton(ClickEvent e)
	{
		getUiHandlers().adminAnchorButtonCallback();
	}

	@UiHandler("notificationALI")
	void onclickNotificationALI(ClickEvent e)
	{
		getUiHandlers().showNotifications();
	}
	
	@UiHandler("notificationBadge")
	void onclickNotificationBadge (ClickEvent e)
	{
		getUiHandlers().showNotifications();
	}
	/*
	 * public AnchorButton getListAdminsAnchorButton() { return
	 * listAdminsAnchorButton; }
	 * 
	 * public AnchorButton getListUsersAnchorButton() { return
	 * listUsersAnchorButton; }
	 */
	
	@UiHandler("auditReportAli")
	void onclickAuditAli (ClickEvent e)
	{
		getUiHandlers().showAuditTableReport();
	}
	
	@UiHandler("auditLogsAli")
	void onclickAuditLogsAli (ClickEvent e)
	{
		getUiHandlers().showAuditLogs();
	}
	
	@UiHandler("myCapabilitiesALI")
	void onclickMyCapabilities (ClickEvent e)
	{
		getUiHandlers().showMyCapabilities();
	}
	
	@UiHandler("navAdminsHelp")
	void onclickNavAdminsHelp (ClickEvent e)
	{
		getUiHandlers().showNavAdminsHelp();
	}
	@UiHandler("navUsersHelp")
	void onclickNavUsersHelp (ClickEvent e)
	{
		getUiHandlers().showNavUsersHelp();
	}

	@UiHandler("navSettignsHelp")
	void onclickNavSettignsHelp (ClickEvent e)
	{
		getUiHandlers().showNavSettingsHelp();
	}

	@UiHandler("navReportsHelp")
	void onclickNavReportsHelp (ClickEvent e)
	{
		getUiHandlers().showNavReportsHelp();
	}
	
	// System Settings
	@UiHandler("systemSettings")
	void onclickSystemSettings (ClickEvent e)
	{
		getUiHandlers().showSystemSettingsPage();
	}
	
	@UiHandler("smsSettings")
	void onClickSmsSettings(ClickEvent e)
	{
		getUiHandlers().showSmsSettingsPage();
	}
	
	// Certificates
	@UiHandler("installCertificate")
	void onclickinstallCertificate (ClickEvent e)
	{
		getUiHandlers().showInstallObidosCertificatePage();
	}
	
	@UiHandler("installAdCertificate")
	void onclickInstallAdCertificate (ClickEvent e)
	{
		getUiHandlers().showInstallAdLdapServerPage();
	}
	
	@UiHandler("viewCertificate")
	void onclickViewCertificate (ClickEvent e)
	{
		getUiHandlers().showViewCertificatesPage();
	}

	// License
	@UiHandler("installLicense")
	void onclickInstallLicense (ClickEvent e)
	{
		getUiHandlers().installLicense();
	}

	public AnchorButton getAdminAnchorButton()
	{
		return adminAnchorButton;
	}

	public AnchorButton getConfigurationAnchorButton()
	{
		return configurationAnchorButton;
	}

	public NavbarCollapse getNavbarCollapse()
	{
		return navbarCollapse;
	}

	public Navbar getNavBar()
	{
		return navBar;
	}

	public NavbarBrand getNavBarBrand()
	{
		return navBarBrand;
	}

	public TextBox getSearchBox()
	{
		return searchBox;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public AnchorButton getContainer()
	{
		return container;
	}

	public AnchorButton getGroup()
	{
		return group;
	}

	// public AnchorButton getKeypair()
	// {
	// return keypair;
	// }

	public Label getMessageLabel()
	{
		return messageLabel;
	}

	@Override
	public void showBusyState(boolean visibile)
	{
		busyField.getStyle().setVisibility(visibile ? Visibility.VISIBLE : Visibility.HIDDEN);
		processingPanel.setVisible(visibile);
	}

	public Element getBusyField()
	{
		return busyField;
	}

	public SimplePanel getMain()
	{
		return main;
	}

	public AnchorListItem getChangePassphrasedAnchorListItem()
	{
		return changePassphrasedAnchorListItem;
	}

	public AnchorButton getNotesAnchorButton()
	{
		return notesAnchorButton;
	}

	public AnchorButton getUserAccountsAnchorButton()
	{
		return userAccountsAnchorButton;
	}

	public AnchorButton getSettingsAnchorButton()
	{
		return settingsAnchorButton;
	}

	public NavbarNav getAdvancedSearchNavBarNav()
	{
		return advancedSearchNavBarNav;
	}

	public AnchorButton getTemplates()
	{
		return templates;
	}

	public AnchorListItem getCreateNewGlobalTemplateAL()
	{
		return createNewGlobalTemplateAL;
	}

	public AnchorButton getItemsAnchorButton()
	{
		return itemsAnchorButton;
	}

	public AnchorListItem getRegisterPassphrasedAnchorListItem()
	{
		return registerPassphrasedAnchorListItem;
	}

	public AnchorListItem getForgotPassphrasedAnchorListItem()
	{
		return forgotPassphrasedAnchorListItem;
	}

	public AnchorListItem getEditNotificationTemplateItem()
	{
		return editNotificationTemplateItem;
	}

	public AnchorListItem getTwoFactorAnchorListItem()
	{
		return twoFactorAnchorListItem;
	}

	public ScrollPanel getScrollPanel()
	{
		return scrollPanel;
	}

	public AnchorListItem getUserSettingsAnchorListItem()
	{
		return userSettingsAnchorListItem;
	}

	public AnchorListItem getNotificationALI()
	{
		return notificationALI;
	}

	public NavPills getNavNotificaionPill()
	{
		return navNotificaionPill;
	}

	public ListDropDown getAdminsListDropDown()
	{
		return adminsListDropDown;
	}

	public AnchorListItem getCreateNewAdmin()
	{
		return createNewAdmin;
	}

	public AnchorListItem getCreateNewUser()
	{
		return createNewUser;
	}

	public AnchorListItem getMyCapabilitiesALI()
	{
		return myCapabilitiesALI;
	}

	public AnchorButton getAuditAnchorButton()
	{
		return auditAnchorButton;
	}

	public AnchorListItem getAuditReportAli()
	{
		return auditReportAli;
	}

	public ListDropDown getAuditDropDown()
	{
		return auditDropDown;
	}

	public Divider getChangePassDividerStart()
	{
		return changePassDividerStart;
	}

	public Divider getChangePassDividerEnd()
	{
		return changePassDividerEnd;
	}

	public Divider getTwoFADividerStart()
	{
		return twoFADividerStart;
	}

	public ObidosNotificationBadge getNotificationBadge()
	{
		return notificationBadge;
	}

	public AnchorListItem getAuditLogsAli()
	{
		return auditLogsAli;
	}

	public AnchorListItem getChangePasswordAnchorListItem()
	{
		return changePasswordAnchorListItem;
	}

	public AnchorListItem getTwoFactorAnchorModalListItem()
	{
		return twoFactorAnchorModalListItem;
	}

	// this menu item used to be called List Admins, It has been changed to 
	// Manage Admins, I don't know why. 
	public AnchorListItem getListadmins()
	{
		return listadmins;
	}

	public AnchorListItem getListLockedAdminAnchorList()
	{
		return listLockedAdminAnchorList;
	}

	public AnchorListItem getListDeletedAdmins()
	{
		return listDeletedAdmins;
	}

	public AnchorListItem getListLoggedInUsersAnchorList()
	{
		return listLoggedInUsersAnchorList;
	}

	public Divider getLdivider1()
	{
		return ldivider1;
	}

	public Divider getLdivider2()
	{
		return ldivider2;
	}

	public AnchorListItem getDisplayLicense()
	{
		return displayLicense;
	}

	public AnchorListItem getDisplayLicenseAdmin()
	{
		return displayLicenseAdmin;
	}

	public AnchorListItem getGeneratePasswordAnchorListItem()
	{
		return generatePasswordAnchorListItem;
	}

	public AnchorListItem getSystemSettings()
	{
		return systemSettings;
	}

	public AnchorListItem getCreateSMTPAnchorListItem()
	{
		return createSMTPAnchorListItem;
	}

	public AnchorListItem getSmsSettings()
	{
		return smsSettings;
	}

	public AnchorListItem getCreateAdldapsettings()
	{
		return createAdldapsettings;
	}

	public AnchorListItem getListAdldapsettings()
	{
		return listAdldapsettings;
	}

	public AnchorListItem getInstallCertificate()
	{
		return installCertificate;
	}

	public AnchorListItem getInstallLicense()
	{
		return installLicense;
	}

	public AnchorListItem getInstallAdCertificate()
	{
		return installAdCertificate;
	}

	public AnchorListItem getListLockedUsersAnchorList()
	{
		return listLockedUsersAnchorList;
	}

	public AnchorListItem getListDeletedUsersAnchorList()
	{
		return listDeletedUsersAnchorList;
	}

	public HTMLPanel getProcessingPanel()
	{
		return processingPanel;
	}

}

