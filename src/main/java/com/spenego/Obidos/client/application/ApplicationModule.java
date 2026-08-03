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

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.spenego.Obidos.client.application.about.AboutModule;
import com.spenego.Obidos.client.application.actionhistory.ActionHistoryModule;
import com.spenego.Obidos.client.application.addfreeformatitemtocontainer.AddFreeFormatItemToContainerModule;
import com.spenego.Obidos.client.application.additemtocontainer.AddItemToContainerModule;
import com.spenego.Obidos.client.application.adduserstogroup.AddUsersToGroupModule;
import com.spenego.Obidos.client.application.adminconsole.AdminConsoleModule;
import com.spenego.Obidos.client.application.advancedusersearch.AdvancedUserSearchModule;
import com.spenego.Obidos.client.application.auditlogs.AuditLogsModule;
import com.spenego.Obidos.client.application.changeinitialpassword.ChangeInitialPasswordModule;
import com.spenego.Obidos.client.application.changepassphrase.ChangePassphraseModule;
import com.spenego.Obidos.client.application.changepassword.ChangePasswordModule;
import com.spenego.Obidos.client.application.copytemplate.CopyTemplateModule;
import com.spenego.Obidos.client.application.createtemplate.CreateTemplateModule;
import com.spenego.Obidos.client.application.editadmin.EditAdminModule;
import com.spenego.Obidos.client.application.edititem.EditItemModule;
import com.spenego.Obidos.client.application.editldapconfig.EditLdapConfigModule;
import com.spenego.Obidos.client.application.editnotificationtemplates.EditNotificationTemplatesModule;
import com.spenego.Obidos.client.application.edittemplate.EditTemplateModule;
import com.spenego.Obidos.client.application.edituser.EditUserModule;
import com.spenego.Obidos.client.application.error.ErrorModule;
import com.spenego.Obidos.client.application.forgotpassphrase.ForgotPassphraseModule;
import com.spenego.Obidos.client.application.grantpermissionstousersforcontainer.GrantPermissionsToUsersForContainerModule;
import com.spenego.Obidos.client.application.grantpermissionstousersforitem.GrantPermissionsToUsersForItemModule;
import com.spenego.Obidos.client.application.home.HomeModule;
import com.spenego.Obidos.client.application.installcertificate.InstallCertificateModule;
import com.spenego.Obidos.client.application.installlicense.InstallLicenseModule;
import com.spenego.Obidos.client.application.keypair.KeyPairModule;
import com.spenego.Obidos.client.application.ldapconfig.LDAPConfigModule;
import com.spenego.Obidos.client.application.licenseinfo.LicenseInfoModule;
import com.spenego.Obidos.client.application.listallmyitems.ListAllMyItemsModule;
import com.spenego.Obidos.client.application.listcontainers.ListContainersModule;
import com.spenego.Obidos.client.application.listcontainersforaddingitem.ListContainersForAddingItemModule;
import com.spenego.Obidos.client.application.listcontainerssharedwithme.ListContainersSharedWithMeModule;
import com.spenego.Obidos.client.application.listdeletedusers.ListDeletedUsersModule;
import com.spenego.Obidos.client.application.listglobaltemplates.ListGlobalTemplatesModule;
import com.spenego.Obidos.client.application.listgroups.ListGroupsModule;
import com.spenego.Obidos.client.application.listitems.ListItemsModule;
import com.spenego.Obidos.client.application.listitemsadd.ListItemsAddModule;
import com.spenego.Obidos.client.application.listitemsinsharedcontainer.ListItemsInSharedContainerModule;
import com.spenego.Obidos.client.application.listitemssharedwithme.ListItemsSharedWithMeModule;
import com.spenego.Obidos.client.application.listldapsettings.ListLdapSettingsModule;
import com.spenego.Obidos.client.application.listlockedusers.ListLockedUsersModule;
import com.spenego.Obidos.client.application.listnotes.ListNotesModule;
import com.spenego.Obidos.client.application.listnotessharedwithme.ListNotesSharedWithMeModule;
import com.spenego.Obidos.client.application.listsmtpsettings.ListSmtpSettingsModule;
import com.spenego.Obidos.client.application.listtemplates.ListTemplatesModule;
import com.spenego.Obidos.client.application.listusers.ListUsersModule;
import com.spenego.Obidos.client.application.listuserscontainerissharedwith.ListUsersContainerIsSharedWithModule;
import com.spenego.Obidos.client.application.listusersingroup.ListUsersInGroupModule;
import com.spenego.Obidos.client.application.listusersitemissharedwith.ListUsersItemIsSharedWithModule;
import com.spenego.Obidos.client.application.listusersitemissharedwithme.ListUsersItemIsSharedWithMeModule;
import com.spenego.Obidos.client.application.listusersnoteissharedwith.ListUsersNoteIsSharedWithModule;
import com.spenego.Obidos.client.application.login.LoginModule;
import com.spenego.Obidos.client.application.newadmin.NewAdminModule;
import com.spenego.Obidos.client.application.newcontainer.NewContainerModule;
import com.spenego.Obidos.client.application.newgroup.NewGroupModule;
import com.spenego.Obidos.client.application.newuser.NewUserModule;
import com.spenego.Obidos.client.application.notes.NotesModule;
import com.spenego.Obidos.client.application.notificationmessage.NotificationMessageModule;
import com.spenego.Obidos.client.application.passwordchanged.PasswordChangedModule;
import com.spenego.Obidos.client.application.pickitemtype.PickItemTypeModule;
import com.spenego.Obidos.client.application.picknotificationtemplate.PickNotificationTemplateModule;
import com.spenego.Obidos.client.application.registerpassphrase.RegisterPassphraseModule;
import com.spenego.Obidos.client.application.resetpassword.ResetPasswordModule;
import com.spenego.Obidos.client.application.resetpasswordrequest.ResetPasswordRequestModule;
import com.spenego.Obidos.client.application.revokecontainer.RevokeContainerModule;
import com.spenego.Obidos.client.application.revokecontainerfromgroups.RevokeContainerFromGroupsModule;
import com.spenego.Obidos.client.application.revokecontainerfromusers.RevokeContainerFromUsersModule;
import com.spenego.Obidos.client.application.revokeitem.RevokeItemModule;
import com.spenego.Obidos.client.application.revokeitemfromgroups.RevokeItemFromGroupsModule;
import com.spenego.Obidos.client.application.revokeitemfromusers.RevokeItemFromUsersModule;
import com.spenego.Obidos.client.application.selectcontainer.SelectContainerModule;
import com.spenego.Obidos.client.application.selecttemplate.SelectTemplateModule;
import com.spenego.Obidos.client.application.sendresetpassphraserequest.SendResetPassphraseRequestModule;
import com.spenego.Obidos.client.application.sessioninfo.SessionInfoModule;
import com.spenego.Obidos.client.application.sharecontainer.ShareContainerModule;
import com.spenego.Obidos.client.application.sharecontainerwithgroup.ShareContainerWithGroupModule;
import com.spenego.Obidos.client.application.sharecontainerwithusers.ShareContainerWithUsersModule;
import com.spenego.Obidos.client.application.sharewith.ShareWithModule;
import com.spenego.Obidos.client.application.sharewithgroups.ShareWithGroupsModule;
import com.spenego.Obidos.client.application.sharewithusers.ShareWithUsersModule;
import com.spenego.Obidos.client.application.smtpconfig.SmtpConfigModule;
import com.spenego.Obidos.client.application.systemsettings.SystemSettingsModule;
import com.spenego.Obidos.client.application.twofactor.TwoFactorModule;
import com.spenego.Obidos.client.application.uploadprofilepic.UploadProfilePicModule;
import com.spenego.Obidos.client.application.userconsole.UserConsoleModule;
import com.spenego.Obidos.client.application.usersettings.UserSettingsModule;
import com.spenego.Obidos.client.application.viewcertificate.ViewCertificateModule;
import com.spenego.Obidos.client.application.viewitem.ViewItemModule;
import com.spenego.Obidos.client.application.viewldapsettings.ViewLdapSettingsModule;
import com.spenego.Obidos.client.application.viewpersonaltemplate.ViewPersonalTemplateModule;
import com.spenego.Obidos.client.application.viewusersingroup.ViewUsersInGroupModule;
import com.spenego.Obidos.client.application.obidoserrorpage.ObidosErrorPageModule;
import com.spenego.Obidos.client.application.changelog.ChangeLogModule;
import com.spenego.Obidos.client.application.listloggedinusers.ListLoggedInUsersModule;
import com.spenego.Obidos.client.application.smsconfig.SMSConfigModule;
import com.spenego.Obidos.client.application.auditreportintext.AuditReportInTextModule;
import com.spenego.Obidos.client.application.generatepassword.GeneratePasswordModule;

public class ApplicationModule extends AbstractPresenterModule
{
	@Override
	protected void configure()
	{
		install(new GeneratePasswordModule());
		install(new AuditReportInTextModule());
		install(new ListLoggedInUsersModule());
		install(new SMSConfigModule());
		install(new ChangeLogModule());
		install(new ObidosErrorPageModule());
		install(new GrantPermissionsToUsersForContainerModule());
		install(new RevokeContainerModule());
		install(new RevokeItemModule());
		install(new ViewCertificateModule());
		install(new InstallCertificateModule());
		install(new ListUsersItemIsSharedWithMeModule());
		install(new ActionHistoryModule());
		install(new ListItemsAddModule());
		install(new UploadProfilePicModule());
		install(new ViewUsersInGroupModule());
		install(new LicenseInfoModule());
		install(new InstallLicenseModule());
		install(new SystemSettingsModule());
		install(new AuditLogsModule());
		install(new EditAdminModule());
		install(new ListDeletedUsersModule());
		install(new ListLockedUsersModule());
		install(new SelectContainerModule());
		install(new UserConsoleModule());
		install(new NotificationMessageModule());
		install(new UserSettingsModule());
		install(new SendResetPassphraseRequestModule());
		install(new ViewItemModule());
		install(new ListContainersForAddingItemModule());
		install(new ListUsersContainerIsSharedWithModule());
		install(new ListUsersItemIsSharedWithModule());
		install(new GrantPermissionsToUsersForItemModule());
		install(new RevokeItemFromGroupsModule());
		install(new RevokeItemFromUsersModule());
		install(new RevokeContainerFromGroupsModule());
		install(new ListItemsInSharedContainerModule());
		install(new RevokeContainerFromUsersModule());
		install(new TwoFactorModule());
		install(new EditNotificationTemplatesModule());
		install(new PickNotificationTemplateModule());
		install(new CopyTemplateModule());
		install(new ListGlobalTemplatesModule());
		install(new ForgotPassphraseModule());
		install(new AdvancedUserSearchModule());
		install(new ListAllMyItemsModule());
		install(new EditItemModule());
		install(new ShareWithGroupsModule());
		install(new ListItemsSharedWithMeModule());
		install(new ShareWithUsersModule());
		install(new ShareWithModule());
		install(new ListTemplatesModule());
		install(new CreateTemplateModule());
		install(new ListItemsModule());
		install(new AddFreeFormatItemToContainerModule());
		install(new SelectTemplateModule());
		install(new PickItemTypeModule());
		install(new AddItemToContainerModule());
		install(new EditTemplateModule());
		install(new ViewPersonalTemplateModule());
		install(new PasswordChangedModule());
		install(new AdminConsoleModule());
		install(new ListUsersNoteIsSharedWithModule());
		install(new SessionInfoModule());
		install(new ResetPasswordModule());
		install(new ResetPasswordRequestModule());
		install(new ListNotesSharedWithMeModule());
		install(new ListNotesModule());
		install(new NotesModule());
		install(new ListContainersSharedWithMeModule());
		install(new ChangePassphraseModule());
		install(new ListUsersInGroupModule());
		install(new AddUsersToGroupModule());
		install(new ListGroupsModule());
		install(new NewGroupModule());
		install(new ShareContainerWithGroupModule());
		install(new ShareContainerWithUsersModule());
		install(new RegisterPassphraseModule());
		install(new ShareContainerModule());
		install(new ChangeInitialPasswordModule());
		install(new ListContainersModule());
		install(new NewContainerModule());
		install(new ListSmtpSettingsModule());
		install(new EditLdapConfigModule());
		install(new ErrorModule());
		install(new ChangePasswordModule());
		install(new SmtpConfigModule());
		install(new ViewLdapSettingsModule());
		install(new ListLdapSettingsModule());
		install(new NewAdminModule());
		install(new EditUserModule());
		install(new ListUsersModule());
		install(new KeyPairModule());
		install(new NewUserModule());
		install(new LDAPConfigModule());
		install(new LoginModule());
		install(new AboutModule());
		install(new HomeModule());

		bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
				ApplicationPresenter.MyProxy.class);
	}
}
