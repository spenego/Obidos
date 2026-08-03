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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.NavbarType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.LockInteractionEvent;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import com.spenego.Obidos.client.application.events.LoginEvent;
import com.spenego.Obidos.client.application.events.LogoutEvent;
import com.spenego.Obidos.client.application.events.LogoutMessageEvent;
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.widgets.ObidosNotificationBadge;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.NotificationService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosSessionTimer;
import com.spenego.Obidos.client.util.SendNotificationTestEmailModalData;
import com.spenego.Obidos.client.util.TwoFAModalData;
import com.spenego.Obidos.client.util.UserInfoModalData;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ObidosException;

public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy>
		implements ApplicationUiHandlers, LoginEvent.LoginEventHandler, LogoutEvent.LogoutEventHandler,
		SendMessageEvent.SendMessageEventHandler
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private static Timer notificationCountTimer = null;
	private static UserInfoModalData sModalData = null;
	private static SendNotificationTestEmailModalData sNotificationTestEmailData = null;
	private static TwoFAModalData sTwoFAModalData = null;

	private static final int CREATE_NEW_NOTE = 1;
	private static final int MY_NOTES = 2;
	private static final int MY_NOTES_SHARED_WITH_OTHERS = 3;
	private static final int NOTES_SHARED_WITH_ME = 4;

	private static final int CREATE_NEW_ITEM = 5;
	private static final int MY_ITEMS = 6;
	private static final int MY_ITEMS_SHARED_WITH_OTHERS = 7;
	private static final int ITEMS_SHARED_WITH_ME = 8;

	private static final int CREATE_NEW_CONTAINER = 9;
	private static final int MY_CONTAINERS = 10;
	private static final int MY_CONTAINERS_SHARED_WITH_OTHERS = 11;
	private static final int CONTAINERS_SHARED_WITH_ME = 12;

	private static final int CREATE_NEW_PERSONAL_TEMPLATES = 13;
	private static final int PERSONAL_TEMPLATES = 14;
	private static final int CREATE_NEW_GLOBAL_TEMPLATES = 15;
	private static final int GLOBAL_TEMPLATES = 16;

	private static final int CREATE_NEW_GROUP = 17;
	private static final int MY_GROUPS = 18;

	private static final int ACTIVITY_HISTORY = 19;

	private static final int SHOW_NOTIFICATIONS = 20;

	private static final int EDIT_PROFILE = 21;

	private static final int ENABLE_TWO_FACTOR = 22;

	private static final int CHANGE_PASSWORD = 23;
	private static final int CHANGE_PASSPHRASE = 24;
	private static final int FORGOT_PASSPHRASE = 25;
	private static final int SESSION_INFO = 26;

	// admin
	private static final int CREATE_NEW_ADMIN = 27;
	private static final int LIST_ACTIVE_ADMINS = 28;
	private static final int LIST_LOCKED_ADMINS = 29;
	private static final int LIST_TOMBSTONED_ADMINS = 30;

	private static final int CREATE_NEW_USER = 31;
	private static final int LIST_ACTIVE_USERS = 32;
	private static final int LIST_LOCKED_USERS = 33;
	private static final int LIST_TOMBSTONED_USERS = 34;

	private static final int CREATE_AD_SETTINGS = 35;
	private static final int CREATE_SMTP_SETTINGS = 36;
	private static final int LIST_AD_SETTINGS = 37;
	private static final int LIST_SMTP_SETTINGS = 38;
	private static final int SYSTEM_SETTINGS = 39;

	private static final int PICK_NOTIFICATION_TEMPLATES = 41;
	private static final int AUDIT_REPORT = 42;
	private static final int ADVANCED_SEARCH = 43;
	private static final int LIST_LOGGED_IN_USERS = 44;
	private static final int SMS_SETTINGS = 45;
	private static final int TWILIO_SMS_SETTINGS = 46;
	private static final int GENERATE_PASSWORD = 47;

	private static final String PASSWORD_EXPIRED = "Password has expired";

	interface MyView extends View, HasUiHandlers<ApplicationUiHandlers>
	{
		public ScrollPanel getScrollPanel();

		public Navbar getNavBar();

		public NavbarBrand getNavBarBrand();

		public NavbarCollapse getNavbarCollapse();

		public ListDropDown getAdminsListDropDown();

		public AnchorButton getConfigurationAnchorButton();

		public AnchorButton getUserAccountsAnchorButton();

		public AnchorButton getSettingsAnchorButton();

		public AnchorButton getTemplates();

		public AnchorListItem getCreateNewGlobalTemplateAL();

		public TextBox getSearchBox();

		public Button getSearchButton();

		public NavbarNav getAdvancedSearchNavBarNav();

		public AnchorButton getAdminAnchorButton();

		public AnchorButton getNotesAnchorButton();

		public AnchorButton getItemsAnchorButton();

		public AnchorButton getContainer();

		public AnchorButton getGroup();

		public Label getMessageLabel();

		public AnchorListItem getChangePassphrasedAnchorListItem();

		public AnchorListItem getRegisterPassphrasedAnchorListItem();

		public AnchorListItem getForgotPassphrasedAnchorListItem();

		public AnchorListItem getUserSettingsAnchorListItem();

		public AnchorListItem getEditNotificationTemplateItem();

		public AnchorListItem getTwoFactorAnchorListItem();

		public NavPills getNavNotificaionPill();

		public AnchorListItem getNotificationALI();

		public void showBusyState(boolean visible);

		public AnchorListItem getCreateNewAdmin();
		public AnchorListItem getListadmins(); // Manage Admins
		public AnchorListItem getListLockedAdminAnchorList();
		public AnchorListItem getListDeletedAdmins();

		public AnchorListItem getCreateNewUser();

		public AnchorListItem getMyCapabilitiesALI();

		public ListDropDown getAuditDropDown();

		public AnchorButton getAuditAnchorButton();

		public AnchorListItem getAuditReportAli();

		public Divider getChangePassDividerStart();

		public Divider getChangePassDividerEnd();

		public Divider getTwoFADividerStart();

		public ObidosNotificationBadge getNotificationBadge();

		public AnchorListItem getAuditLogsAli();

		public AnchorListItem getChangePasswordAnchorListItem();

		public Divider getLdivider1();
		public Divider getLdivider2();
		public AnchorListItem getDisplayLicense();
		public AnchorListItem getDisplayLicenseAdmin();
		public AnchorListItem getGeneratePasswordAnchorListItem();
		
		public AnchorListItem getSystemSettings();
		public AnchorListItem getCreateSMTPAnchorListItem();
		public AnchorListItem getSmsSettings();
		public AnchorListItem getCreateAdldapsettings();
		public AnchorListItem getListAdldapsettings();
		public AnchorListItem getInstallCertificate();
		public AnchorListItem getInstallLicense();
		public AnchorListItem getInstallAdCertificate();

		// Bug #97
		public AnchorListItem getListLockedUsersAnchorList();
		public AnchorListItem getListDeletedUsersAnchorList();

		public HTMLPanel getProcessingPanel();
	}

	@ProxyStandard
	interface MyProxy extends Proxy<ApplicationPresenter>
	{
	}

	public static final NestedSlot SLOT_MAIN = new NestedSlot();
	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

	@Inject
	ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
			CurrentUser currentUser, TokenFormatter tokenFormatter)
	{
		super(eventBus, view, proxy, RevealType.Root);

		this.placeManager = placeManager;
		this.currentUser = currentUser;

		eventBus.addHandler(NavigationEvent.getType(), new NavigationHandler()
		{
			@Override
			public void onNavigation(NavigationEvent navigationEvent)
			{
				Scheduler.get().scheduleDeferred(new Command()
				{
					@Override
					public void execute()
					{
						Window.scrollTo(0, 0);
						hideNavbarCollapse();
					}

				});
			}
		});

		getView().setUiHandlers(this);
	}

	private void hideNavbarCollapse()
	{
		NavbarCollapse navbarCollapse = getView().getNavbarCollapse();
		String ariaExpanded = navbarCollapse.getElement().getAttribute("aria-expanded");
		if (Boolean.parseBoolean(ariaExpanded))
		{
			navbarCollapse.toggle();
		}
	}

	// When this method is called from NavBar, messageDTO is null, so be
	// careful.
	// When it is called from ClientUtils.logout() it will have proper
	// messageDTO
	// ClientUtils.logout() fires a LogoutMessageEvent which is received by
	// onReceiveLogoutMessage() which in turn calls logout(messageDTO)
	@Override
	public void logout(MessageDTO messageDTO)
	{

		// actually logout from the server
		// Do not call the GwtAsyncWrapper, it can create a infinite loop
		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				GWT.log("logout error. excaption: " + caught.getMessage());
				// do not care, just get out of here
				onLogoutSuccess(messageDTO);
			}

			@Override
			public void onSuccess(Void result)
			{
				gwtLog("Successfully logged out");
				gwtLog("Reset search box..");
				String placeHolder = getView().getSearchBox().getPlaceholder();
				gwtLog(" search box place holder: " + placeHolder);
				gwtLog(" resetting place holder to: " + ObidosMessages.LANG.searchUsers());
				setSearchBoxPlaceHolderText(ObidosMessages.LANG.searchUsers());
				onLogoutSuccess(messageDTO);
			}
		};
		LoginService.Utility.getInstance().logout(ClientUtils.getAuthCreds(), callback);
	}

	private void resetClientSide()
	{
		gwtLog(" Clearing cookies");
		ClientUtils.clearClipboard();
		ClientUtils.clearCookies();
		gwtLog(" Set CurrentUser singleton to Logged out state");
		currentUser.setLoggedIn(false);
		currentUser.setPassphraseRegistered(Boolean.FALSE);
		ClientUtils.cancelTimers();

	}

	private void onLogoutSuccess(final MessageDTO messageDTO)
	{
		gwtLog("onLogoutSuccess");
		resetClientSide();

		String place = History.getToken();
		gwtLog("Current Place: " + place + " length: " + place.length());

		String nameToken = NameTokens.LOGIN;
		// navigate to Login View
		ClientUtils.showPage(placeManager, nameToken);

		// Note: event has to be fired after the reveal place, otherwise the
		// message won't show up
		if (messageDTO != null)
		{
			// LoginPresenter is listening for this message
			gwtLog("Firing LogoutMessageEvent");
			LogoutMessageEvent.fire(this, messageDTO);
		}
	}

	@Override
	protected void onReveal()
	{
		super.onReveal();
		gwtLog("Apllication Presenter onReveal()");
		getView().showBusyState(false);
		showProcessing(false);
	}
	
	private void showProcessing(boolean show)
	{
		getView().getProcessingPanel().setVisible(show);
	}

	@Override
	protected void onReset()
	{
		super.onReset();
		gwtLog("onReseet()");
		updateNotificationBell(0);
		gwtLog("Apllication Presenter onReset()");

		hideMessageLabel();

		UserDTO user = currentUser.getUserDTO();
		if (user != null)
		{
			gwtLog(" username: " + user.getUsername());
			gwtLog(" ApplicationPresenter onReset username: " + user.getUsername());
		} else
		{
			gwtLog(" ApplicationPresenter onReset authCreds username: " + currentUser.getUserDTO().getUsername());
		}
		adjustNavBar();
		gwtLog("Passphrase registered: " + currentUser.getPassphraseRegistered());
		changePassphraseRegisteredIcon();
		getView().getSearchBox().setFocus(true);
		setNavbarBrandImage();
		gwtLog("Scroll panel height: " + getView().getScrollPanel().getOffsetHeight());
		setNotificationPillStyle();
		updateNotificationCount();
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				startNotificationCountTimer();
			}

		});
	}
	
	private void showDisplayLicense(final boolean show)
	{
		getView().getLdivider1().setVisible(show);
		getView().getLdivider2().setVisible(show);
		getView().getDisplayLicense().setVisible(show);
	}


	private void setNotificationPillStyle()
	{
		AnchorListItem notificatioAnchorListItem = getView().getNotificationALI();
		notificatioAnchorListItem.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		gwtLog(" Style: " + notificatioAnchorListItem.getElement().getStyle().getFontSize());
		notificatioAnchorListItem.getElement().setPropertyString("boder-radius", "3px !important");
		notificatioAnchorListItem.getElement().setPropertyString("font-weight", "normal");
		String radius = notificatioAnchorListItem.getElement().getPropertyString("border-radious");
		gwtLog("Notificaton anchor list item radius: " + radius);
		NavPills notificationPill = getView().getNavNotificaionPill();
		gwtLog(" Style: " + notificationPill.getElement().getStyle().getFontSize());
		gwtLog("Notification Pill font weight: " + notificationPill.getElement().getPropertyString("font-weight"));
		// n.getElement().getStyle().setBackgroundColor("red"); -- also works
	}

	private void updateNotificationCount()
	{
		getNotificationCount();
	}

	private void startNotificationCountTimer()
	{
		cancelNotificationCountTimer();
		if (notificationCountTimer == null)
		{
			notificationCountTimer = new Timer()
			{
				@Override
				public void run()
				{
					getNotificationCount();
					int timeout = ObidosSessionTimer.getSessionTimeoutIntervalSeconds()
							- ObidosSessionTimer.getSessionWillTimeoutInSeconds();
					notificationCountTimer.scheduleRepeating((timeout < 10 ? 10 : timeout > 180 ? 180 : timeout) * 500);
				}
			};
			notificationCountTimer.scheduleRepeating(3 * 1000);
		}
	}

	private void getNotificationCount()
	{
		// only request notification count if the window is visible
		// Issue #520
		if (!ClientUtils.windowisVisible())
		{
			return;
		}

		AsyncCallback<Integer> callback = new AsyncCallback<Integer>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				// don't give a f
				gwtLog("Could ont get notification count: " + caught.getMessage());
			//	Window.alert(caught.getMessage());
				/*
				if (caught instanceof PasswordChangeRequiredException)
				{
					placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.CHANGE_INITIAL_PASSWORD).build());
				}
				else
				{
					placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.ABOUT).build());
				}
				*/
			}

			@Override
			public void onSuccess(Integer count)
			{
				currentUser.setNotificationCount(count);
				updateNotificationBell(count);
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		NotificationService.Utility.getInstance().getNotificationCount(authCreds, true, callback);
	}

	private Timer getNotificationCountTimer()
	{
		return notificationCountTimer;
	}

	private void setNotificationCountTimer(Timer t)
	{
		notificationCountTimer = t;
	}

	private void cancelNotificationCountTimer()
	{
		Timer t = getNotificationCountTimer();
		if (t != null)
		{
			gwtLog("Cancelling notification timer");
			t.cancel();
			setNotificationCountTimer(null);
		}
	}

	private void updateNotificationBell(Integer count)
	{
		int nc = ClientUtils.fromInteger(count);
		AnchorListItem nacli = getView().getNotificationALI();
		nacli.setEnabled(true);
		if (nc > 0)
		{
			nacli.setBadgeText(Integer.toString(nc));
			nacli.setEnabled(true);
		} else
		{
			nacli.setBadgeText("0");
			nacli.setBadgeText(Integer.toString(nc));
			nacli.setIconSpin(false);
		}
		getView().getNotificationBadge().setMessageCount(nc);
		Date d = new Date();
		String tooltip = ObidosMessages.LANG.notificationTooltip(d.toString());
		nacli.setTitle(tooltip);
	}

	private void setNavbarBrandImage()
	{
		NavbarBrand brand = getView().getNavBarBrand();
		String obidos = ObidosMessages.LANG.obidos();
		brand.setHTML("<img src=\"SpenegoLogo.png\" width=\"60px\" height=\"40%\" class=\"img-responsive\">" + obidos);
	}

	private void changePassphraseRegisteredIcon()
	{
		AnchorButton b = getView().getAdminAnchorButton();
		if (currentUser != null && currentUser.getUserDTO().getAdministrator())
		{
			b.setIcon(null);
			return;
		}

		b.setIcon(IconType.CIRCLE);
		if (currentUser != null && currentUser.getPassphraseRegistered() == Boolean.TRUE)
		{
			b.setIconColor("green");
			b.setTitle(ObidosMessages.LANG.passphraseIsInServerCache());
		} else
		{
			b.setIconColor("red");
			b.setTitle(ObidosMessages.LANG.passphraseIsInServerCache());
			b.setTitle(ObidosMessages.LANG.passphraseIsNotInIServerCache());
		}
	}

	private void enableCreateNewGlobalTemplateAnchor(boolean enabled)
	{
		gwtLog(">>>>>>>>>>>>>>>>>>>>>> can manage global template" + enabled);
		getView().getCreateNewGlobalTemplateAL().setEnabled(enabled);
	}

	private void hideMessageLabel()
	{
		getView().getMessageLabel().setVisible(false);
	}

	@Override
	protected void onBind()
	{
		super.onBind();
		getView().getSearchBox().setValue("");
	}

	@Override
	protected void onHide()
	{
		super.onHide();
		cancelNotificationCountTimer();
		gwtLog("onHide() resetting searchbox");
		getView().getSearchBox().setValue("");
		getView().showBusyState(false);
		showProcessing(false);
	}

	/**
	 * return true if the user is an administrator, false otherwise
	 *
	 * Note: There is no security here, it is possible to alter this behavior by
	 * anyone. The main security is in the server side. Even if someone manages
	 * to display the admin gui as a normal user, they won't be able to do
	 * anything because server really checks who the user is before doing
	 * anything.
	 *
	 * @param userDTO
	 * @return true of false
	 *         <p>
	 * @author spgdev@spenego.com - Jan 5, 2017
	 */
	private boolean userIsAdmin(UserDTO userDTO)
	{
		if (userDTO == null)
		{
			return false;
		} else if (userDTO.getAdministrator() == null)
		{
			return false;
		} else if (Boolean.TRUE.equals(userDTO.getAdministrator()))
		{
			return true;
		}

		return false;
	}

	private void adminMenuItems(boolean visible)
	{
		getView().getAdminsListDropDown().setVisible(visible);
		getView().getUserAccountsAnchorButton().setVisible(visible);
		getView().getSearchBox().setVisible(visible);
		getView().getSearchButton().setVisible(visible);
		getView().getAdvancedSearchNavBarNav().setVisible(visible);
		getView().getSettingsAnchorButton().setVisible(visible);
		getView().getForgotPassphrasedAnchorListItem().setVisible(visible);
		getView().getEditNotificationTemplateItem().setVisible(visible);
		getView().getTwoFactorAnchorListItem().setVisible(visible);
		getView().getAuditReportAli().setVisible(visible);
		getView().getMyCapabilitiesALI().setVisible(visible);
		if (visible)
		{
			showAdminMenusByCapabilites();
		}
	}

	private void userMenuItems(boolean visible)
	{
		getView().getNotesAnchorButton().setVisible(visible);
		getView().getItemsAnchorButton().setVisible(visible);
		getView().getContainer().setVisible(visible);
		getView().getGroup().setVisible(visible);
		getView().getChangePassphrasedAnchorListItem().setVisible(visible);
		getView().getTemplates().setVisible(visible);
		getView().getForgotPassphrasedAnchorListItem().setVisible(visible);
		getView().getTwoFactorAnchorListItem().setVisible(visible);
	}
	
	// Bug #69
	// Lockup setting menu if admin does not have capabilities
	// to change settings.
	// Oct-22-2024
	private void enableDisableSystemSettings(CapabilityDTO cdto)
	{
		AnchorListItem ali = getView().getSystemSettings();
		boolean ms = cdto.getModifySettings();
		ali.setEnabled(ms);
		
		ali = getView().getCreateSMTPAnchorListItem();
		ali.setEnabled(ms);
		
		ali = getView().getSmsSettings();
		ali.setEnabled(ms);
		
		ali = getView().getCreateAdldapsettings();
		ali.setEnabled(ms);
		
		ali = getView().getListAdldapsettings();
		ali.setEnabled(ms);
		
		ali = getView().getInstallCertificate();
		ali.setEnabled(ms);
		
		ali = getView().getInstallAdCertificate();
		ali.setEnabled(ms);

		ali = getView().getInstallLicense();
		ali.setEnabled(ms);
	}

	private void showAdminMenusByCapabilites()
	{
		if (currentUser == null)
		{
			return;
		}
		if (!isAdmin())
		{
			return;
		}
		CapabilityDTO cdto = currentUser.getUserDTO().getCapabilities();
		enableDisableSystemSettings(cdto);

		// enable all
		AnchorListItem ali = getView().getCreateNewAdmin();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getCreateAdmin());
		ali.setTitle("");
		if (! cdto.getCreateAdmin())
		{
			ali.setTitle(glang.noCapabilitySet());
		}
		
		
		ali = getView().getListadmins();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getChangeAdminCredentials());
		ali.setTitle("");
		if (! cdto.getChangeAdminCredentials())
		{
			ali.setTitle(glang.noCapabilitySet());
		}

		ali = getView().getListLockedAdminAnchorList();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getLockAdmin());
		ali.setTitle("");
		if (! cdto.getLockAdmin())
		{
			ali.setTitle(glang.noCapabilitySet());
		}

		ali = getView().getListDeletedAdmins();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getDeleteAdmin());
		ali.setTitle("");
		if (! cdto.getDeleteAdmin())
		{
			ali.setTitle(glang.noCapabilitySet());
		}

		ali = getView().getListLockedUsersAnchorList();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getDeleteUser());
		ali.setTitle("");
		if (! cdto.getDeleteUser())
		{
			ali.setTitle(glang.noCapabilitySet());
		}


		ali = getView().getCreateNewUser();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getCreateUser());
		ali.setTitle("");
		if (! cdto.getCreateUser())
		{
			ali.setTitle(glang.noCapabilitySet());
		}
			

		getView().getEditNotificationTemplateItem().setEnabled(cdto.getModifyEmailTemplates());
		
		// Bug# 97
		ali = getView().getListLockedUsersAnchorList();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getLockUser());
		ali.setTitle("");
		if (! cdto.getLockUser())
		{
			ali.setTitle(glang.noCapabilitySet());
		}

		ali = getView().getListDeletedUsersAnchorList();
		ali.setEnabled(true);
		ali.setEnabled(cdto.getDeleteUser());
		ali.setTitle("");
		if (! cdto.getDeleteUser())
		{
			ali.setTitle(glang.noCapabilitySet());
		}
		
		// Bug #103
		ali = getView().getAuditLogsAli();
		ali.setEnabled(true);
		ali.setTitle("");
		if (! ClientUtils.doesLicenseSupportAuditReport(currentUser, true))
		{
			ali.setEnabled(false);
			ali.setTitle(glang.notSupportedByLicense());
		}

		ali = getView().getAuditReportAli();
		ali.setEnabled(true);
		ali.setTitle("");
		if (! ClientUtils.doesLicenseSupportAuditReport(currentUser, true))
		{
			ali.setEnabled(false);
			ali.setTitle(glang.notSupportedByLicense());
		}
		
		ali = getView().getSmsSettings();
		ali.setEnabled(true);
		ali.setTitle("");
		if (! ClientUtils.doesLicenseSupportSMSSettings(currentUser, true))
		{
			gwtLog("MMM XXX XXXX MMMM");
			ali.setEnabled(false);
			ali.setTitle(glang.notSupportedByLicense());
		}
	
	}

	/**
	 * show Admin menu items, hide User menu items
	 *
	 * @author spgdev@spenego.com - Jan 5, 2017
	 */
	private void showAdminMenuItems()
	{
		adminMenuItems(true);
		userMenuItems(false);
		showPassDividers(false);
		showDisplayLicense(false);
	}

	private void showPassDividers(boolean visible)
	{
		getView().getChangePassDividerStart().setVisible(visible);
		getView().getTwoFADividerStart().setVisible(visible);
	}

	/**
	 * show User menu items, hide Admin menu items
	 *
	 * @author spgdev@spenego.com - Jan 5, 2017
	 */
	private void showUserMenuItems()
	{
		adminMenuItems(false);
		userMenuItems(true);
		showPassDividers(true);
		showDisplayLicense(true);
	}

	private void enableForgotPassphraseMenuItem(boolean enabled)
	{
		getView().getForgotPassphrasedAnchorListItem().setEnabled(enabled);
	}

	/**
	 * Based on the logged in user, adjust the look of Navigation bar
	 *
	 * @param userDTO
	 *            UserDTO object sent by LoginService. Note: the object is
	 *            copied from User model by BeanUtils, therefore members can be
	 *            NULL if it is null in User. For example, say if
	 *            "administrator" field is not set in User model, it will be
	 *            null in UserDTO. So if it is accessed as
	 *            !userDTO.getAdminstrator() it will through NULL pointer
	 *            exception. Therefore, be careful about accessing the member
	 *            vars, check first if they are null or not
	 *            <p>
	 * @author spgdev@spenego.com - Jan 3, 2017
	 */
	private void adjustNavBar(UserDTO userDTO)
	{
		gwtLog("AppliationPresente: on adjustNavBar...");
		enableCreateNewGlobalTemplateAnchor(false);

		if (userIsAdmin(userDTO))
		{
			getView().getNavBar().setType(NavbarType.INVERSE);
			String adminString = "Administrator " + userDTO.getUsername();
			getView().getAdminAnchorButton().setText(adminString);
			showAdminMenuItems();
		} else
		{
			getView().getNavBar().setType(NavbarType.DEFAULT);
			String username = userDTO.getUsername();
			// Community Bug #11
			getView().getAdminAnchorButton().setText(username);
			gwtLog("Make user menu items visble");
			showUserMenuItems();
			checkCapabilites(userDTO);
		}
	}

	// called from onReset()
	private void adjustNavBar()
	{
		if (currentUser != null && currentUser.isKeypairExists())
		{
			enableForgotPassphraseMenuItem(true);
		} else
		{
			gwtLog("currentUser is null, disable Forgot Passphrase Menu item");
			enableForgotPassphraseMenuItem(false);
		}
		// It is client side hack to display admin menu.
		// Note: there is no security here, anyone can expose admin menus.
		// However, even though admin menus can be
		// exposed and methods can be called, server side will enforce security.
		// Meaning a regular user will not be
		// able to execute admin's tasks.
		if (currentUser != null && currentUser.getUserDTO().getAdministrator())
		{
			getView().getAuditAnchorButton().setText(glang.audit());
			getView().getAuditLogsAli().setText(glang.logs());
			gwtLog("Inverse navbar..for admin");
			getView().getNavBar().setType(NavbarType.INVERSE);
			String adminString = "Administrator " + currentUser.getUserDTO().getUsername();
			getView().getAdminAnchorButton().setText(adminString);
			showAdminMenuItems();
		} else
		{
			if (currentUser != null)
			{
				getView().getChangePassphrasedAnchorListItem().setEnabled(currentUser.getUserDTO().keyPairExists());
				// Community Bug #11
				String username = currentUser.getUserDTO().getUsername();
				getView().getAdminAnchorButton().setText(username);
			}

			getView().getAuditAnchorButton().setText(glang.history());
			getView().getAuditLogsAli().setText(glang.actionHistory());
			getView().getNavBar().setType(NavbarType.DEFAULT);
			showUserMenuItems();
		}
		if (currentUser != null)
		{
			UserDTO userDTO = currentUser.getUserDTO();
			CapabilityDTO cDTO = currentUser.getUserDTO().getCapabilities();
			gwtLog("++++ in adjustNavBar(): can create global template: " + cDTO.getCreateGlobalTemplate());
			gwtLog("     2FA available? " + userDTO.getTwoFARequired());
			enableCreateNewGlobalTemplateAnchor(cDTO.getCreateGlobalTemplate());

			AnchorListItem changePassAli = getView().getChangePasswordAnchorListItem();
			changePassAli.setEnabled(true);
			changePassAli.setTitle("");
			if (!userDTO.authSourceIsLocal()) // Issue #670
			{
				changePassAli.setEnabled(false);
				changePassAli.setTitle(glang.passwordIsExternal());
			}

			if (Boolean.FALSE.equals(userDTO.getTwoFAPasswordResetEnabled()))
			{
				getView().getTwoFactorAnchorListItem().setIcon(IconType.TIMES);
			} else
			{
				getView().getTwoFactorAnchorListItem().setIcon(IconType.CHECK);
			}
			enableMenuItems(cDTO);
		} else
		{
			gwtLog("ERROR: currentUser is null, it should not be!");
		}
	}

	private void checkCapabilites(UserDTO userDTO)
	{
		CapabilityDTO cDTO = userDTO.getCapabilities();
		if (cDTO != null)
		{
			gwtLog("+++++ Capability to create global template: " + cDTO.getCreateGlobalTemplate());
			enableCreateNewGlobalTemplateAnchor(cDTO.getCreateGlobalTemplate());
		}
	}

	/**
	 * based on capabilities enable/disable menu items
	 *
	 * @param cDTO
	 *            <p>
	 * @author spgdev@spenego.com - Feb 17, 2018
	 */
	private void enableMenuItems(CapabilityDTO cDTO)
	{
		if (cDTO != null)
		{
			enableCreateNewGlobalTemplateAnchor(cDTO.getCreateGlobalTemplate());
		}
	}

	/**
	 * Note: without ProxyEvent at the very first time, the method will not be
	 * called because the event was fired by LoginPresenter before this
	 * presenter is instantiated! The post
	 * http://blog.arcbees.com/2010/08/31/using-proxyevent/ glosses on it and
	 * there seem to be some pitfalls. Apparently using ProxyEvent is not a good
	 * thing, but that's the only way I could make this thing work. GWTP
	 * programmers really suck!
	 */
	@ProxyEvent
	@Override
	public void onSuccessfulLogin(LoginEvent event)
	{

		gwtLog(">>>>>>>>>>>>>>>> Appliacation Presenter received message onSuccessfulLogin.. <<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		// now use CurrentUser singleton for this purpose
		UserDTO userDTO = event.getUserDTO();
		gwtLog("ApplicationPresenter received event: user: " + userDTO.getUsername());
		gwtLog(" admin? " + userDTO.getAdministrator());
		gwtLog("Adjusting navbar...");
		adjustNavBar(userDTO); // do we need that?? it was already done from
								// onReset()
	}

	@ProxyEvent
	@Override
	public void onSendMessageEvent(SendMessageEvent event)
	{
		gwtLog("onSendMessageEvent in ApplicationPresenter");
		if (event == null)
		{
			return;
		}

		MessageDTO messageDTO = event.getMessageDTO();
		if (messageDTO == null)
		{
			return;
		}
		int messageType = messageDTO.getMessageType();
		String message = messageDTO.getMessage();
		TextBox searchBox = getView().getSearchBox();

		switch (messageType)
		{
			case ObidosConstants.SEARCHBOX_TEXT:
			{
				if (message != null)
				{
					gwtLog("Received message to clear searchbox");
					searchBox.setValue(message); // Bug #148
				}
				break;
			}
			case ObidosConstants.SEARCHBOX_PLACEHOLDER:
			{
				if (message != null)
				{
					gwtLog("Received message to set searchbox placeholder");
					searchBox.setPlaceholder(message);
				}
				break;
			}
			case ObidosConstants.PASSPHRASE_CACHED:
			{
				gwtLog("Received message to change cache pp icon");
				changePassphraseRegisteredIcon();
				break;
			}
			case ObidosConstants.REFRESH_PASSPHRASE_CACHED_ICON:
			{
				gwtLog("Received message to refresh passphrase icon");
				changePassphraseRegisteredIcon();
				break;
			}
			case ObidosConstants.SESSION_TIMED_OUT: // GwtAsyncWrapper sends this
													// event
			{
				onLogoutSuccess(messageDTO);
				break;
			}
			case ObidosConstants.TWO_FACTOR_ENABLED:
			{
				gwtLog("ApplicationPresenter received message: " + message);
				getView().getTwoFactorAnchorListItem().setIcon(IconType.CHECK);
				if (currentUser != null)
				{
					gwtLog("Updating UserDTO...");
					currentUser.getUserDTO().setTwoFAPasswordResetEnabled(true);
				}
				getView().getMessageLabel().setText("2FA Enabled");
				break;
			}
			case ObidosConstants.TWO_FACTOR_DISABLED:
			{
				gwtLog("ApplicationPresenter received message: " + message);
				getView().getTwoFactorAnchorListItem().setIcon(IconType.TIMES);
				if (currentUser != null)
				{
					gwtLog("Updating UserDTO...");
					currentUser.getUserDTO().setTwoFAPasswordResetEnabled(false);
				}
				getView().getMessageLabel().setText("2FA Disabled");
				break;
			}
			default:
			{
				gwtLog("Default switch");
			}
		}
	}

	@ProxyEvent
	@Override
	public void onReceiveLogoutMessage(LogoutEvent event)
	{
		gwtLog("onReceiveLogoutMessage");
		if (event == null)
		{
			return;
		}
		gwtLog("Appliacation Presenter onSendMessageEvent");

		MessageDTO messageDTO = event.getMessageDTO();
		if (messageDTO == null)
		{
			return;
		}

		logout(messageDTO);
	}

	@Override
	public void showAboutPage()
	{
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.ABOUT).build());
	}

	@Override
	public void showCreateLDAPSettingsPage()
	{
		menuAction(CREATE_AD_SETTINGS);
	}

	public void showCreateLDAPSettingsPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.LDAPSETTINGS);
	}

	@Override
	public void showCreateSMTPSettingsPage()
	{
		menuAction(CREATE_SMTP_SETTINGS);
	}

	public void showCreateSMTPSettingsPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		// Issue #42 We only support 1 SMTP server, therefore do not display
		// existing config in a list, rather display it in the same page
		ClientUtils.showPage(placeManager, NameTokens.SMTPSETTINGS);
		// currently we only allow one SMTP server. If there is one smtp server
		// already exists, show the server so
		// that it can be updated if desired.
		/*
		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
				ClientUtils.showPage(placeManager, NameTokens.SMTPSETTINGS);
			}

			@Override
			public void uponSuccess(Boolean result)
			{
				// Default SMTP configuration exists, show the list page
				if (Boolean.TRUE.equals(result))
				{
					listSmtpSettings();
				} else
				{
					ClientUtils.showPage(placeManager, NameTokens.SMTPSETTINGS);
				}
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		String smtpConfigName = "default";
		AdminConfigService.Utility.getInstance().smtpConfigExists(authCreds, smtpConfigName, callback);
		*/
	}

	@Override
	public void createNewAdmin()
	{
		menuAction(CREATE_NEW_ADMIN);
	}

	private void createNewAdminReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.NEW_ADMIN);
	}

	@Override
	public void listActiveUsers()
	{
		menuAction(LIST_ACTIVE_USERS);
	}

	@Override
	public void listLoggedInUserUsers()
	{
		menuAction(LIST_LOGGED_IN_USERS);
	}

	@Override
	public void showAdvancedUsersSearchPage()
	{
		menuAction(ADVANCED_SEARCH);
	}

	public void showAdvancedUsersSearchPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.ADVANCED_USER_SEARCH);
	}

	public void listActiveUsersReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.LIST_USERS);
	}

	public void listLoggedInUsersReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.LIST_LOGGED_IN_USERS);
	}

	@Override
	public void listLockedUsers()
	{
		menuAction(LIST_LOCKED_USERS);
	}

	public void listLockedUsersReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.LOCKED_USERS);
	}

	@Override
	public void listLockedAdmins()
	{
		menuAction(LIST_LOCKED_ADMINS);
	}

	public void listLockedAdminsReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.TYPE_ADMINS);
		String nameToken = NameTokens.LOCKED_USERS;
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void listDeletedUsers()
	{
		menuAction(LIST_TOMBSTONED_USERS);
	}

	public void listDeletedUsersReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.DELETED_USERS);
	}

	@Override
	public void listAdmins()
	{
		menuAction(LIST_ACTIVE_ADMINS);
	}

	public void listAdminsReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.TYPE_ADMINS);
		String nameToken = NameTokens.LIST_USERS;
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void listDeletedAdmins()
	{
		menuAction(LIST_TOMBSTONED_ADMINS);
	}

	public void listDeletedAdminsReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.TYPE_ADMINS);
		String nameToken = NameTokens.DELETED_USERS;
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	private void setSearchBoxPlaceHolderText(String text)
	{
		TextBox searchBox = getView().getSearchBox();
		searchBox.setPlaceholder(text);
	}

	@Override
	public void listSmtpSettings()
	{
		menuAction(LIST_SMTP_SETTINGS);
	}

	public void listSmtpSettingsReal()
	{
		if (Boolean.TRUE.equals(ClientUtils.checkIfpasswordHasExpired(currentUser)))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.LIST_SMTP_SETTINGS);
	}

	@Override
	public void searchUsers()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		if (Boolean.TRUE.equals(currentUser.getUserDTO().getAdministrator()))
		{
			String searchString = getView().getSearchBox().getValue();
			if (searchString.length() == 0)
			{
				searchString = null;
			}
			searchUser(searchString);
		}
	}

	private boolean isListTypeKnown(String listType)
	{
		boolean expression = false;
		if (listType == null)
		{
			return expression;
		}
		
		expression = listType.equals(ObidosConstants.LIST_USERS);
		if (expression)
		{
			return expression;
		}
		expression = listType.equals(ObidosConstants.LIST_ADMINS);
		if (expression)
		{
			return expression;
		}
		expression = listType.equals(ObidosConstants.LIST_LOCKED_USERS);
		if (expression)
		{
			return expression;
		}

		expression = listType.equals(ObidosConstants.LIST_DELETED_USERS);
		if (expression)
		{
			return expression;
		}

		expression = listType.equals(ObidosConstants.LIST_DELETED_ADMINS);
		if (expression)
		{
			return expression;
		}


		return expression;
	}

	/**
	 * Show the ListUsers view and fire the event with the search string
	 *
	 * @param search
	 *            <p>
	 * @author spgdev@spenego.com - Apr 1, 2017 Update: we don't fire events
	 *         anymore, it locks up async call at reload when we use history Jun
	 *         03, 2017
	 */
	private void searchUser(String search)
	{
		gwtLog("++++++ ApplicationPresenter search user: " + search);
		gwtLog("searchUser: " + search);

		PlaceRequest currentPlaceRequest = placeManager.getCurrentPlaceRequest();
		String listType = ObidosConstants.LIST_USERS;
		String type = currentPlaceRequest.getParameter(ObidosConstants.TYPE, "");
		if (isListTypeKnown(type))
		{
			listType = type;
		}

		PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.LIST_USERS)
				.with(ObidosConstants.TYPE, listType).with(ObidosConstants.SEARCH, search).build();

		placeManager.revealPlace(placeRequest);
	}

	@Override
	public void showListDAPSettingsPage()
	{
		menuAction(LIST_AD_SETTINGS);
	}

	public void showListDAPSettingsPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.LIST_LDAP_SETTINGS);
	}

	@Override
	public void showGenKeyPairPage()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.KEY_PAIR).build());
	}

	@Override
	public void showCreateNewUserPage()
	{
		menuAction(CREATE_NEW_USER);
	}

	private void showCreateNewUserPageReal()
	{
		ClientUtils.showPage(placeManager, NameTokens.NEW_USER);
	}

	@Override
	public void showGeneratePasswordPage()
	{
		menuAction(GENERATE_PASSWORD);
	}
	
	private void showGeneratePasswordPageReal()
	{
		gwtLog("MMMM >>>>");
		ClientUtils.showPage(placeManager, NameTokens.GENERATE_PASSWORD);
	}

	@Override
	public void showChangePasswordPage()
	{
		menuAction(CHANGE_PASSWORD);
	}

	private void showChangePasswordPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.CHANGE_PASSWORD);
	}

	/**
	 * We display a short busy message whenever navigation is in progress.
	 *
	 * @param event
	 *            The {@link LockInteractionEvent}.
	 */
	@ProxyEvent
	public void onLockInteraction(LockInteractionEvent event)
	{
		getView().showBusyState(event.shouldLock());
		showPassDividers(event.shouldLock());
	}

	@Override
	public void showNewContainerPager()
	{
		menuAction(CREATE_NEW_CONTAINER);
	}

	public void showNewContainerPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		navigateToCreateNewContainerPage();
	}

	private void navigateToCreateNewContainerPage()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
		{

			@Override
			public void uponSuccess(Boolean rc)
			{
				if (Boolean.TRUE.equals(rc))
				{
					currentUser.setPassphraseRegistered(Boolean.TRUE);
					ClientUtils.showPage(placeManager, NameTokens.NEW_CONTAINER);
				} else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.createContainer(),
							NameTokens.NEW_CONTAINER);
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("Nothing can be done");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().isPassphraseCached(authCreds, callback);
	}

	@Override
	public void listMyContainers()
	{
		menuAction(MY_CONTAINERS);
	}

	public void listMyContainersReal()
	{
		navigateToPageAfterKeyPairCheck(NameTokens.LIST_CONTAINERS);
	}

	@Override
	public void listMyContainersSharedWithOthers()
	{
		menuAction(MY_CONTAINERS_SHARED_WITH_OTHERS);
	}

	public void listMyContainersSharedWithOthersReal()
	{
		navigateToListMyContainersSharedWithOthers();
	}

	private void navigateToListMyContainersSharedWithOthers()
	{
		String nameToken = NameTokens.LIST_CONTAINERS;
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.SHARED_WITH_OTHERS);
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void listContainersSharedWithMe()
	{
		menuAction(CONTAINERS_SHARED_WITH_ME);
	}

	public void listContainersSharedWithMeReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		navigateToPageAfterKeyPairCheck(NameTokens.LIST_CONTAINERS_SHARED_WITH_ME);
	}

	@Override
	public void showCreateNewGroupPage()
	{
		menuAction(CREATE_NEW_GROUP);
	}

	public void showCreateNewGroupPageReal()
	{
		String nameToken = NameTokens.NEW_GROUP;
		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
		{

			@Override
			public void uponSuccess(Boolean rc)
			{
				if (rc)
				{
					navigateToPageAfterKeyPairCheck(nameToken);
				} else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.createNewGroup(),
							nameToken);
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().isPassphraseCached(authCreds, callback);

	}

	// entry point for all menu items
	private void menuAction(final int action)
	{
		// check license first
		// Turn off license check in client side: Issue #656

		// Only allow the operation if license is not beyond final grace period
		LicenseStats license = currentUser.getLoginResult().getLicenseStats();
		if (!ClientUtils.isLicenseOk(license))
		{
			ClientUtils.popLicenseWarningDialog(license);
			return;
		}

		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		// for users only, if keypair not created, it will take it to that view
		if (!isAdmin())
		{
			if (!ClientUtils.checkIfKeypairIsCreated(currentUser, placeManager))
			{
				return;
			}
		}
		gwtLog("MMM check action");

		switch (action)
		{
			case CREATE_NEW_NOTE:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				createNewNoteReal();
				break;
			}
			case MY_NOTES:
			{
				listMyNotesReal();
				break;
			}
			case MY_NOTES_SHARED_WITH_OTHERS:
			{
				listNotesSharedWithOthersReal();
				break;
			}
			case NOTES_SHARED_WITH_ME:
			{
				listNotesSharedWithMeReal();
				break;
			}

			case CREATE_NEW_ITEM:
			{
				// show the page but prevent when Add is clicked if license
				// has expired
				createItemReal();
				break;
			}

			case MY_ITEMS:
			{
				listMyItemsReal();
				break;
			}

			case MY_ITEMS_SHARED_WITH_OTHERS:
			{
				listMyItemsSharedWithOthersReal();
				break;
			}

			case ITEMS_SHARED_WITH_ME:
			{
				listItemsSharedWithMeReal();
				break;
			}

			// Container
			case CREATE_NEW_CONTAINER:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showNewContainerPageReal();
				break;
			}

			case MY_CONTAINERS:
			{
				listMyContainersReal();
				break;
			}

			case MY_CONTAINERS_SHARED_WITH_OTHERS:
			{
				listMyContainersSharedWithOthersReal();
				break;
			}

			case CONTAINERS_SHARED_WITH_ME:
			{
				listContainersSharedWithMeReal();
				break;
			}

			// Templates
			case CREATE_NEW_PERSONAL_TEMPLATES:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showCreatePersonalTemplateViewReal();
				break;
			}

			case CREATE_NEW_GLOBAL_TEMPLATES:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showCreateGlobalTemplateViewReal();
				break;
			}

			case PERSONAL_TEMPLATES:
			{
				showListPersonalTemplatesViewReal();
				break;
			}

			case GLOBAL_TEMPLATES:
			{
				showListGlobalTemplatesViewReal();
				break;
			}

			// Group
			case CREATE_NEW_GROUP:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showCreateNewGroupPageReal();
				break;
			}

			case MY_GROUPS:
			{
				showListGroupPageReal();
				break;
			}

			// Activity/Audit
			case ACTIVITY_HISTORY:
			{
				if (! ClientUtils.doesLicenseSupportAuditReport(currentUser, true))
				{
					ClientUtils.showDialog("ERROR", "This feature is not supported by current license");
				}

				showAuditLogsReal();
				break;
			}

			case AUDIT_REPORT:
			{
				if (! ClientUtils.doesLicenseSupportAuditReport(currentUser, true))
				{
					ClientUtils.showDialog("ERROR", "This feature is not supported by current license");
					return;
				}

				showAuditTableReportReal();
				break;
			}

			// Notifications
			case SHOW_NOTIFICATIONS:
			{
				showNotificationsReal();
				break;
			}

			// profile etc
			case EDIT_PROFILE:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}
				showUserSettingsPageReal();
				break;
			}

			case ENABLE_TWO_FACTOR:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				if (ClientUtils.is2FAEnabeld(currentUser))
				{
					ClientUtils.promptForAction(() -> showTwoFactorPageReal(), glang.twoFactorAlreadyConfigured(),
							glang.twoFactorResetHelp());
				} else
				{
					showTwoFactorPageReal();
				}
				break;
			}

			// admin
			case CREATE_NEW_USER:
			{
				if (!isAdmin())
				{
					return;
				}
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
				{
					return;
				}

				showCreateNewUserPageReal();
				break;
			}
			case CREATE_NEW_ADMIN:
			{
				if (!isAdmin())
				{
					return;
				}
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
				{
					return;
				}

				createNewAdminReal();
				break;
			}

			case LIST_ACTIVE_ADMINS:
			{
				if (!isAdmin())
				{
					return;
				}
				listAdminsReal();
				break;
			}

			case LIST_LOCKED_ADMINS:
			{
				if (!isAdmin())
				{
					return;
				}
				listLockedAdminsReal();
				break;
			}
			case LIST_TOMBSTONED_ADMINS:
			{
				if (!isAdmin())
				{
					return;
				}
				listDeletedAdminsReal();
				break;
			}
			case LIST_ACTIVE_USERS:
			{
				if (!isAdmin())
				{
					return;
				}
				listActiveUsersReal();
				break;
			}
			case LIST_LOGGED_IN_USERS:
			{
				if (!isAdmin())
				{
					return;
				}
				listLoggedInUsersReal();
				break;
			}
			case LIST_LOCKED_USERS:
			{
				if (!isAdmin())
				{
					return;
				}
				listLockedUsersReal();
				break;
			}
			case LIST_TOMBSTONED_USERS:
			{
				if (!isAdmin())
				{
					return;
				}
				listDeletedUsersReal();
				break;
			}
			case ADVANCED_SEARCH:
			{
				if (!isAdmin())
				{
					return;
				}
				showAdvancedUsersSearchPageReal();
				break;
			}
			case CREATE_AD_SETTINGS:
			{
				if (!isAdmin())
				{
					return;
				}
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
				{
					return;
				}

				showCreateLDAPSettingsPageReal();
				break;
			}
			case CREATE_SMTP_SETTINGS:
			{
				if (!isAdmin())
				{
					return;
				}
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
				{
					return;
				}

				showCreateSMTPSettingsPageReal();
				break;
			}
			case LIST_AD_SETTINGS:
			{
				if (!isAdmin())
				{
					return;
				}

				showListDAPSettingsPageReal();
				break;
			}
			case LIST_SMTP_SETTINGS:
			{
				if (!isAdmin())
				{
					return;
				}
				listSmtpSettingsReal();
				break;
			}
			case SYSTEM_SETTINGS:
			{
				if (!isAdmin())
				{
					return;
				}
				showSystemSettingsPageReal();
				break;
			}
			case SMS_SETTINGS:
			{
				if (!isAdmin())
				{
					return;
				}
				// TODO: check if license allows that
				showSmsSettingsPageReal();
				break;

				
			}
			case PICK_NOTIFICATION_TEMPLATES:
			{
				if (!isAdmin())
				{
					return;
				}
				showPickNotificationTemplatesPageReal();
				break;
			}

			// Bug #56 
			case GENERATE_PASSWORD:
			{
				showGeneratePasswordPageReal();
				break;
			}

			case CHANGE_PASSWORD:
			{
				if (isAdmin() && !(ClientUtils.doesLicenseAllowToCreateModify(currentUser, true)))
				{
						return;
				}

				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showChangePasswordPageReal();
				break;
			}

			case CHANGE_PASSPHRASE:
			{
				if (isAdmin())
				{
					return;
				}
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showChangePassphrasedPageReal();
				break;
			}

			case FORGOT_PASSPHRASE:
			{
				if (isAdmin())
				{
					if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
					{
						return;
					}
				}
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				showForgotPassphrasePageReal();
				break;
			}

			case SESSION_INFO:
			{
				showSessionInfoPageReal();
				break;
			}
			default:
			{
				gwtLog("N/A");
			}
		}

	}

	@Override
	public void createNewNote()
	{
		menuAction(CREATE_NEW_NOTE);
	}

	public void createNewNoteReal()
	{
		gwtLog("Show create note page");
		if (ClientUtils.isPassphraseRegistered(currentUser))
		{
			navigateToPageAfterKeyPairCheck(NameTokens.NOTE);
		} else
		{
			ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.createNewNote(),
					NameTokens.NOTE);
		}
	}

	@Override
	public void showListGroupPage()
	{
		menuAction(MY_GROUPS);
	}

	private void showListGroupPageReal()
	{
		navigateToPageAfterKeyPairCheck(NameTokens.LIST_GROUPS);
	}

	@Override
	public void showChangePassphrasedPage()
	{
		menuAction(CHANGE_PASSPHRASE);
	}

	private void showChangePassphrasedPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		navigateToPageAfterKeyPairCheck(NameTokens.CHANGE_PASSPHRASE);

	}

	@Override
	public void listMyNotes()
	{
		menuAction(MY_NOTES);
	}

	public void listMyNotesReal()
	{
		navigateToPageAfterKeyPairCheck(NameTokens.LIST_NOTES);
	}

	private void navigateToPageAfterKeyPairCheck(String nameToken)
	{
		// Issue #299
		// Don't check keypair
		ClientUtils.showPage(placeManager, nameToken);
	}

	@Override
	public void listNotesSharedWithMe()
	{
		menuAction(NOTES_SHARED_WITH_ME);
	}

	public void listNotesSharedWithMeReal()
	{
		navigateToPageAfterKeyPairCheck(NameTokens.LIST_NOTES_SHARED_WITH_ME);
	}

	@Override
	public void showSessionInfoPage()
	{
		menuAction(SESSION_INFO);
	}

	private void showSessionInfoPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.SESSION_INFO);
	}

	@Override
	public void listNotesSharedWithOthers()
	{
		menuAction(MY_NOTES_SHARED_WITH_OTHERS);
	}

	public void listNotesSharedWithOthersReal()
	{
		navigateToListNotesSharedWithOthers();
	}

	private void navigateToListNotesSharedWithOthers()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.SHARED_WITH_OTHERS);
		String nameToken = NameTokens.LIST_NOTES;
		ClientUtils.showPage(placeManager, nameToken, with);

	}

	@Override
	public void showBuildInfoPage()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		String nameToken = NameTokens.ABOUT;
		ClientUtils.showPage(placeManager, nameToken);
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	private void showCreateTemplateView(String templateType)
	{
		String action = glang.createPersonalTemplate();
		if (ObidosConstants.GLOBAL.equals(templateType))
		{
			action = glang.createGlobalTemplate();
		}
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TEMPLATE_TYPE, templateType);
		ClientUtils.addPlace(placeManager, with, NameTokens.TYPE_TEMPLATE);

		if (ClientUtils.isPassphraseRegistered(currentUser))
		{
			gwtLog("Passphrase registered already");
			ClientUtils.showPage(placeManager, NameTokens.TYPE_TEMPLATE, with);
		} else
		{
			gwtLog("Show dialog");
			ClientUtils.showRegisterPassphraseDialogWith(placeManager, action, with);
		}
	}

	@Override
	public void showCreatePersonalTemplateView()
	{
		menuAction(CREATE_NEW_PERSONAL_TEMPLATES);
	}

	public void showCreatePersonalTemplateViewReal()
	{
		showCreateTemplateView(ObidosConstants.PERSONAL);
	}

	@Override
	public void showCreateGlobalTemplateView()
	{
		menuAction(CREATE_NEW_GLOBAL_TEMPLATES);
	}

	public void showCreateGlobalTemplateViewReal()
	{
		showCreateTemplateView(ObidosConstants.GLOBAL);
	}

	@Override
	public void showListPersonalTemplatesView()
	{
		menuAction(PERSONAL_TEMPLATES);
	}

	public void showListPersonalTemplatesViewReal()
	{
		gwtLog("Show List Personal Templates View");
		String nameToken = NameTokens.LIST_TEMPLATES;
		Map<String, String> with = new HashMap<>();
		String key = ObidosConstants.TEMPLATE_TYPE;
		String value = ObidosConstants.PERSONAL;
		with.put(key, value);
		ClientUtils.showPage(placeManager, nameToken, with);

	}

	@Override
	public void showListGlobalTemplatesView()
	{
		menuAction(GLOBAL_TEMPLATES);
	}

	public void showListGlobalTemplatesViewReal()
	{
		String nameToken = NameTokens.LIST_GLOBAL_TEMPLATES;
		ClientUtils.showPage(placeManager, nameToken);
	}

	@Override
	public void showRegisterPassphrasePage()
	{
		// after successful passphrase validation, it will send back us to this
		// view
		ClientUtils.showPage(placeManager, NameTokens.REGISTER_PASSPHRASE);

	}

	@Override
	public void listMyItems()
	{
		menuAction(MY_ITEMS);
	}

	public void listMyItemsReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.LIST_ALL_MY_ITEMS);
	}

	@Override
	public void listItemsSharedWithMe()
	{
		menuAction(ITEMS_SHARED_WITH_ME);
	}

	public void listItemsSharedWithMeReal()
	{
		String nameToken = NameTokens.LIST_ITEMS_SHARED_WITH_ME;
		ClientUtils.showPage(placeManager, nameToken);
	}

	@Override
	public void listMyItemsSharedWithOthers()
	{
		menuAction(MY_ITEMS_SHARED_WITH_OTHERS);
	}

	public void listMyItemsSharedWithOthersReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.SHARED_WITH_OTHERS);
		String nameToken = NameTokens.LIST_ALL_MY_ITEMS;
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void createItem()
	{
		menuAction(CREATE_NEW_ITEM);
	}

	public void createItemReal()
	{
		// item can be created inside a container only
		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
		{

			@Override
			public void uponSuccess(Boolean rc)
			{
				if (rc)
				{
					currentUser.setPassphraseRegistered(Boolean.TRUE);
					ClientUtils.showPage(placeManager, NameTokens.LIST_CONTAINERS_ADD_ITEM);
				} else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.createNewItem(),
							NameTokens.LIST_CONTAINERS_ADD_ITEM);

				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("create Item failed");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().isPassphraseCached(authCreds, callback);

	}

	private void checkIfKeyPairCreatedAlready()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		Long userId = null;
		try
		{
			userId = ClientUtils.getLoggedInUserId(currentUser);
		} catch (ObidosException e)
		{
			return;
		}
		gwtLog("User id: " + userId);
		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
		{

			@Override
			public void uponSuccess(Boolean result)
			{
				gwtLog("Key pair exists? " + result);
				if (Boolean.FALSE.equals(result))
				{
					ClientUtils.showBootboxDialog("Create Keypair first", "Please create keypair first");
				} else
				{
					showSendResetPassphraseInstructionsPage();
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("N/A");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().keypairExists(authCreds, userId, callback);
	}

	private void showSendResetPassphraseInstructionsPage()
	{
		ClientUtils.showPage(placeManager, NameTokens.SEND_RESET_PASSPHRASE_REQUEST);
	}

	@Override
	public void showForgotPassphrasePage()
	{
		menuAction(FORGOT_PASSPHRASE);
	}

	private void showForgotPassphrasePageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		checkIfKeyPairCreatedAlready();
	}

	private boolean isCurrentUserNnull()
	{
		if (currentUser == null)
		{
			ObidosMessages lang = ObidosMessages.LANG;
			ClientUtils.showBootboxDialogSimple(lang.error(), lang.couldNotDetermineLoggedInUser());
			return true;
		}
		return false;

	}

	// caller should check if currentUser is null by calling isCurrentUserNull
	// before calling this method
	private boolean isAdmin()
	{
		return ClientUtils.isAdmin(currentUser);
	}

	@Override
	public void showAdminConsole()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		if (currentUser != null)
		{
			if (ClientUtils.isAdmin(currentUser))
			{
				String nameToken = NameTokens.ADMIN_CONSOLE;
				ClientUtils.showPage(placeManager, nameToken);
			} else
			{
				ClientUtils.showPage(placeManager, NameTokens.USER_CONSOLE);
			}
		}
	}

	@Override
	public void showPickNotificationTemplatesPage()
	{
		menuAction(PICK_NOTIFICATION_TEMPLATES);
	}

	public void showPickNotificationTemplatesPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.PICK_NOTIFICAITON_TEMPLATE);
	}

	@Override
	public void showTwoFactorPage()
	{
		menuAction(ENABLE_TWO_FACTOR);
	}

	public void showTwoFactorPageReal()
	{
		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
		{

			@Override
			public void uponSuccess(Boolean rc)
			{
				if (Boolean.TRUE.equals(rc))
				{
					currentUser.setPassphraseRegistered(Boolean.TRUE);
					ClientUtils.showPage(placeManager, NameTokens.TWO_FACTOR);
				} else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.enable2FA(),
							NameTokens.TWO_FACTOR);
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("Error");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().isPassphraseCached(authCreds, callback);
	}

	@Override
	public void notesHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.notesMenuHelp());
	}

	@Override
	public void itemsHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.itemsMenuHelp());
	}

	@Override
	public void containersHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.containersMenuHelp());
	}

	@Override
	public void templatesHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.templatesMenuHelp());
	}

	@Override
	public void groupsHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.groupsMenuHelp());

	}

	@Override
	public void adminAnchorButtonCallback()
	{
		gwtLog("in adminAnchorButtonCallback");
		if (currentUser != null)
		{
			if (Boolean.TRUE.equals(currentUser.getUserDTO().getTwoFAPasswordResetEnabled()))
			{
				gwtLog("Update 2FA icon to checkbox");
				getView().getTwoFactorAnchorListItem().setIcon(IconType.CHECK);
			} else
			{
				gwtLog("Update 2FA icon to X");
				getView().getTwoFactorAnchorListItem().setIcon(IconType.TIMES);
			}

			changePassphraseRegisteredIcon();
		}
	}

	private void showProtectedPage(String nameToken, String message)
	{
		if (isCurrentUserNnull())
		{
			return;
		}
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		if (isAdmin())
		{
			ClientUtils.showPage(placeManager, nameToken);
			return;
		}

		boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
		gwtLog("Passphrase registered? " + rc);

		if (rc)
		{
			gwtLog("Show: " + nameToken);
			ClientUtils.showPage(placeManager, nameToken);
		} else
		{
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, nameToken);
		}
	}

	@Override
	public void showUserSettingsPage()
	{
		menuAction(EDIT_PROFILE);
	}

	public void showUserSettingsPageReal()
	{
		if (!isAdmin())
		{
			if (currentUser == null)
			{
				gwtLog("currentUser is null");
				return;
			}
			boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
			if (rc)
			{
				ClientUtils.showPage(placeManager, NameTokens.USER_SETTINGS);
			} else
			{
				ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.editSettings(),
						NameTokens.USER_SETTINGS);
			}

		} else
		{
			// admin should be able to edit herself
			// EDIT_USER;userid=7296113971657804164
			Long userId = currentUser.getUserDTO().getId();
			if (userId == null)
			{
				gwtLog("User id is null, can't show profile");
				ClientUtils.showBootboxDialog("ERROR", "Could not determine admin id, can not show edit profile page");
				return;
			}
			Map<String, String> with = new HashMap<>();
			with.put(ObidosConstants.USER_ID, userId.toString());
			ClientUtils.showPage(placeManager, NameTokens.EDIT_USER, with);
		}
	}

	// only Admin can see this page
	// Protected by gatekeeper
	@Override
	public void showSystemSettingsPage()
	{
		menuAction(SYSTEM_SETTINGS);
	}

	@Override
	public void showSmsSettingsPage()
	{
		menuAction(SMS_SETTINGS);
	}

	public void showSystemSettingsPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.SYSTEM_SETTINGS);
	}

	public void showSmsSettingsPageReal()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.SMS_CONFIG_PRESENTER);
	}

	@Override
	public void showNotifications()
	{
		menuAction(SHOW_NOTIFICATIONS);
	}

	public void showNotificationsReal()
	{
		showProtectedPage(NameTokens.NOTIFICATION_MESSAGE, ObidosMessages.LANG.viewNotificationMessages());
	}

	private void downloadPDF(LimitedFernetDTO dto)
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		// make sure to import import com.google.gwt.core.client.GWT
		// there is one with shared.client, don't include that one
		String url = GWT.getHostPageBaseURL() + "report?token=" + dto.getToken();
		Window.open(url, "_blank", ""); // Open the document in a tab
	}

	@Override
	public void showAuditTableReport()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		menuAction(AUDIT_REPORT);
	}

	private void showAuditTableReportReal()
	{
		GwtAsyncWrapper<LimitedFernetDTO> callback = new GwtAsyncWrapper<LimitedFernetDTO>(this)
		{

			@Override
			public void uponSuccess(LimitedFernetDTO dto)
			{
				downloadPDF(dto);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				ClientUtils.showBootboxDialog("ERROR",
						"Could not get security token for download: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		FernetService.Utility.getInstance().getFernetToken(authCreds, callback);

	}

	@Override
	public void showAuditLogs()
	{
		menuAction(ACTIVITY_HISTORY);
	}

	public void showAuditLogsReal()
	{
		if (ClientUtils.isAdmin(currentUser))
		{
			ClientUtils.showPage(placeManager, NameTokens.AUDIT_LOGS);
		} else
		{
			ClientUtils.showPage(placeManager, NameTokens.ACTION_HISTORY);
		}
	}

	@Override
	public void showMyCapabilities()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		if (currentUser == null)
		{
			ClientUtils.showBootboxDialog(glang.error(), glang.couldNotDetermineLoggedInUser());
			return;
		}
		ClientUtils.showCapabilities(glang.myCapabilities(), currentUser.getUserDTO());
	}

	@Override
	public void showNavAdminsHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.adminMenuHelp());
	}

	@Override
	public void showNavUsersHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.usersMenuHelp());

	}

	@Override
	public void showNavSettingsHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.settingsMenuHelp());

	}

	@Override
	public void showNavReportsHelp()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		String title = null;
		if (ClientUtils.isAdmin(currentUser))
		{
			ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.auditMenuHelp());
		} else
		{
			ClientUtils.showBootboxDialog(title, ObidosMessages.LANG.historyMenuHelp());
		}
	}

	@Override
	public void showInstallObidosCertificatePage()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.INSTALL_NGINX_CERTS);
		ClientUtils.showPage(placeManager, NameTokens.INSTALL_CERTIFICATE, with);
	}

	@Override
	public void showViewCertificatesPage()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		ClientUtils.showPage(placeManager, NameTokens.VIEW_CERTIFICATE);
	}

	@Override
	public void showInstallAdLdapServerPage()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.TYPE, ObidosConstants.INSTALL_ADLAP_CERTS);
		ClientUtils.showPage(placeManager, NameTokens.INSTALL_CERTIFICATE, with);

	}

	// only admin can install license
	@Override
	public void installLicense()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.INSTALL_LICENSE);
	}

	// anyone can display license
	@Override
	public void displayLicense()
	{
		if (ClientUtils.checkIfpasswordHasExpired(currentUser))
		{
			gwtLog(PASSWORD_EXPIRED);
			return;
		}

		ClientUtils.showPage(placeManager, NameTokens.LICENSE_INFO);
	}

	public static UserInfoModalData getUserInfoModalData()
	{
		if (sModalData == null)
		{
			ClientUtils.gwtLog("ApplicationPresenter", "Create user info modal data");
			sModalData = ClientUtils.createUserInfoModal();
		} else
		{
			ClientUtils.gwtLog("ApplicationPresenter", "Re-use user info modal data");
		}
		return sModalData;
	}
	
	public static TwoFAModalData getTwoFAModalData() 
	{
		if (sTwoFAModalData == null)
		{
			sTwoFAModalData = ClientUtils.createTwoFAModal();
		}
		return sTwoFAModalData;
	}

	public static SendNotificationTestEmailModalData getNotificationTestEmailModalData()
	{
		if (sNotificationTestEmailData == null)
		{
			sNotificationTestEmailData = ClientUtils.createNotificationTestEmailModal();
		}
		return sNotificationTestEmailData;
	}
	
	@Override
	public void pop2FAAuthenticatorModal()
	{
		TwoFAModalData md = getTwoFAModalData();
		md.clear();
		ClientUtils.showTwoFACode(placeManager, md);
	}

}
