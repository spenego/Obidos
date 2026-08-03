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

package com.spenego.Obidos.client;

import java.util.Date;
import java.util.Map;

import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosSessionTimer;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.PasswordChangeRequiredException;

/**
 * This class executes code after GWTP and gin are initialized but before the
 * default place is shown. Ref:
 * https://dev.arcbees.com/gwtp/get-started/Bootstrap-Code.html
 *
 * Here we initialize our remote services with GWT XSRF servlet to set a token
 * on our RPC endpoints using HasRpcToken interface and have that token included
 * with each RPC call made via that endpoint.
 *
 * Ref: http://www.gwtproject.org/doc/latest/DevGuideSecurityRpcXsrf.html Ref:
 * http://www.gwtproject.org/javadoc/latest/com/google/gwt/user/client/rpc/HasRpcToken.html
 *
 * Note: We do not use GWT's way of protecting from XSRF, we do it our own way,
 * which is cleaner and better.
 *
 * @author spgdev@spenego.com - Dec 17, 2016
 */
public class ObidosBootstrapper implements Bootstrapper
{

	private final CurrentUser currentUser;
	private final String unauthorizedPlace;
	private final PlaceManager placeManager;
	private long lastInteractionTimeMilli = System.currentTimeMillis();
	private Timer inactivityWarningTimer = null;
	private Timer inactivityTimer = null;
	private long defaultTimeoutMilli = 2 * 60 * 1000L;
	private long defaultAdvanceWarningMilli = 60 * 1000L;
	private ObidosMessages glang = ObidosMessages.LANG;

	private final TokenFormatter tokenFormatter;

	// Please Do not delete any unused code, there are nice stuff here I look
	// for reference every now and then.
	// spgdev@spenego.com

	@Inject
	ObidosBootstrapper(PlaceManager placeManager, CurrentUser currentUser, ParameterTokenFormatter ptf,
			TokenFormatter tokenFormatter, @UnauthorizedPlace String unauthorizedPlace)
	{
		if (currentUser != null)
		{
			currentUser.setPassphraseRegistered(Boolean.FALSE);

			UserDTO dto = currentUser.getUserDTO();
			if (dto != null)
			{
				gwtLog("BBB  Current user: " + currentUser.getUserDTO().getUsername());
				gwtLog("BBB  >>>>>>>>>>>>>> hide delay: " + currentUser.getUserDTO().getHideItemDelay());
			} else
			{
				gwtLog("  Current user dto is null");
			}
		} else
		{
			gwtLog("  Current user is null");

		}
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		this.tokenFormatter = tokenFormatter;
		this.unauthorizedPlace = unauthorizedPlace;
	}

	/**
	 * We don't account for user clicking around etc for checking timeout. They
	 * have to actually perform an rpc to update session
	 * 
	 * @deprecated
	 *
	 * @return
	 *         <p>
	 * @author spgdev@spenego.com - Nov 23, 2017
	 */
	@Deprecated
	protected Timer getInactivityWarningTimer()
	{
		if (inactivityWarningTimer != null)
		{
			return inactivityWarningTimer;
		}

		inactivityWarningTimer = new Timer()
		{
			@Override
			public void run()
			{
				long warningTimeout = defaultTimeoutMilli - defaultAdvanceWarningMilli;
				long timeSinceLastInteraction = System.currentTimeMillis() - lastInteractionTimeMilli;
				if (timeSinceLastInteraction >= warningTimeout)
				{
					gwtLog("User warned about inactivity and prompted for action at "
							+ new Date(System.currentTimeMillis()));
				} else
				{
					int nextTimerDelay = (int) (warningTimeout - timeSinceLastInteraction);
					gwtLog("Inactivity warning timer scheduled to run in " + nextTimerDelay + " ms at "
							+ new Date(System.currentTimeMillis()));
					schedule(nextTimerDelay);
				}
			}
		};
		gwtLog("Returning inactivity timer..");
		return inactivityWarningTimer;
	}

	/**
	 * @deprecated
	 *             <p>
	 * @author spgdev@spenego.com - Nov 23, 2017
	 */
	@Deprecated
	protected void startInactivityTimer()
	{
		if (inactivityTimer != null)
		{
			return;
		}

		gwtLog("Starting inactivity timer....");

		inactivityTimer = new Timer()
		{
			@Override
			public void run()
			{
				long userPreferredTimeout = defaultTimeoutMilli;
				long timeSinceLastInteraction = System.currentTimeMillis() - lastInteractionTimeMilli;
				if (timeSinceLastInteraction >= userPreferredTimeout)
				{
					gwtLog("Session has been logged out due to inactivity at " + new Date(System.currentTimeMillis()));
					Window.alert("Your session has timed out due to inactivity.");
					gwtLog("Firing...logout event");
					MessageDTO messageDTO = new MessageDTO();
					messageDTO.setMessageType(MessageDTO.ERROR);
					messageDTO.setMessage("logout");
					placeManager.getEventBus().fireEvent(new SendMessageEvent(messageDTO));
				} else
				{
					int nextTimerDelay = (int) (userPreferredTimeout - timeSinceLastInteraction);
					gwtLog("Inactivity timer scheduled to run in " + nextTimerDelay + " ms at "
							+ new Date(System.currentTimeMillis()));
					schedule(nextTimerDelay);

					// Schedule in the warning timer
					int warningDelay = (int) Math.max(1, nextTimerDelay - defaultAdvanceWarningMilli);
					gwtLog("waring delay: " + warningDelay);
					getInactivityWarningTimer().schedule(warningDelay);
				}
			}
		};

		inactivityTimer.schedule(1000); // start initial timer 1 second after
										// initial call
	}

	private PlaceRequest getPlaceRequest()
	{
		String place = History.getToken();
		if (place != null && place.length() > 0)
		{
			return tokenFormatter.toPlaceRequest(place);
		}
		return null;
	}

	@Override
	public void onBootstrap()
	{
		gwtLog("In onBootstrap");
		// turn off those timers or dialog will start popping every now and then
		ClientUtils.cancelTimers();

		/* application has crashed, show stack trace */
		ClientUtils.setupGWTUmbrellaExceptionHandler();

		String place = History.getToken();
		// check if someone clicked on password reset link
		if (place.length() > 0)
		{
			PlaceRequest placeRequest = getPlaceRequest();
			if (placeRequest != null)
			{
				String nameToken = placeRequest.getNameToken();
				gwtLog("++ place: " + nameToken);
				if (nameToken != null && (nameToken.length() > 0 && nameToken.equals(NameTokens.RESET_PASSWORD)))
				{
					Map<String, String> map = ClientUtils.makeMapFromUrl(tokenFormatter);
					// do not remove any parameter from URL
					ClientUtils.showPage(placeManager, nameToken, map);
					return;
				}
			}
		}
		if (currentUser == null)
		{
			gwtLog("CurrentUser is null in onBootstrap");
		}

		navigate();
	}

	private void navigate()
	{
		gwtLog("Calling LoginService.isCurrentUserLoggedIn() ....");
		AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				gwtLog("Current user is not logged in");
				if (currentUser != null)
				{
					currentUser.setPassphraseRegistered(Boolean.FALSE);
				}
				navigateUserNotLoggedIn();
			}

			@Override
			public void onSuccess(LoginResult loginResult)
			{
				UserDTO userDTO = loginResult.getUserDTO();

				gwtLog(">>> Current user is logged in");
				gwtLog(">> license sate: " + loginResult.getLicenseStats().getLicenseExpirationState());
				gwtLog(" Notifiation count: " + userDTO.getNotificationCount());
				gwtLog(" Accept email notification: " + userDTO.getAcceptEmailNotification());
				gwtLog(" 2fa enabeld? " + userDTO.getTwoFAPasswordResetEnabled());
				gwtLog(" Hide item delay: " + userDTO.getHideItemDelay());

				// must update RPC access time, most cases it is done by our rpc
				// wrapper
				ClientUtils.updateRPCTime("Bootstrapper");

				ClientUtils.updateCurrentUser(currentUser, userDTO);

				if (currentUser != null)
				{
					currentUser.setPassphraseRegistered(Boolean.FALSE);
					// dateformat is in LoginResult now issue #641
					// licensestat is also in LoginReseult now
					currentUser.setLoginResult(loginResult);
				}

				gwtLog(">>+ license sate: " + loginResult.getLicenseStats().getLicenseExpirationState());

				int days = ClientUtils.fromInteger(userDTO.getDaysUntilPasswordExpiration());
				gwtLog(">>> Password expires in : " + days + " days");
				// save it to current user
				if (currentUser != null)
				{
					UserDTO udto = currentUser.getUserDTO();
					if (udto != null)
					{
						udto.setDaysUntilPasswordExpiration(days);
					}
				}
			
				boolean authSourceLocal = userDTO.authSourceIsLocal();
				gwtLog("MMM auth source local: " + authSourceLocal);
				boolean passwordChangeRequired = Boolean.TRUE.equals(userDTO.getPasswordChangeRequired());
				gwtLog("MMM password change required: " + passwordChangeRequired);

				// Issue #806
				// Navigate to password change page only if auth source is local
				// and password change is required
				if (userDTO.authSourceIsLocal() &&  Boolean.TRUE.equals(userDTO.getPasswordChangeRequired()))
				{
					navigateToPasswordChangePage();
				} else
				{
					navigateUserIsLoggedIn(loginResult);
				}
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		LoginService.Utility.getInstance().isCurrentUserLoggedIn(authCreds, callback);
	}

	private void startTimer(UserDTO userDTO)
	{
		Integer sessionTimeout = userDTO.getSessionTimeoutSeconds();
		gwtLog("Session Timeout before: " + sessionTimeout + " seconds");
		long sessionTimeoutMillis = ClientUtils.sessionTimeoutMillis(sessionTimeout);
		gwtLog("Session Timeout after: " + sessionTimeoutMillis + " milli seconds");
		long sessionCheckMillis = ObidosConstants.SESSION_CHECK_MILLIS; // check
																		// in
																		// every
																		// 1
																		// minute
		long sessionGraceMillis = ObidosConstants.SESSION_GRACE_MILLIS; // pop
																		// for 1
																		// minute
		gwtLog("Start session check timers...");
		ObidosSessionTimer.start(placeManager, currentUser, sessionTimeoutMillis, sessionCheckMillis,
				sessionGraceMillis);
	}

	private void navigateUserNotLoggedIn()
	{
		gwtLog("WTF is going on???");
		boolean passphraseResetRequest = handlePassphraseResetRequest(false);
		gwtLog("user is Not logged in");
		gwtLog(" passphrase reset request? " + passphraseResetRequest);
		if (passphraseResetRequest)
		{
			return;
		}
		gwtLog("+++ ---- Reveal login place");

		// If the URL contains a name token, add nameToken=token so that
		// we can redirect there after login etc.
		Map<String, String> with = ClientUtils.makeMapFromUrl(true, tokenFormatter);
		if (with != null && with.size() > 0)
		{
			gwtLog(" with size: " + with.size());
			String nameToken = NameTokens.LOGIN;
			ClientUtils.showPage(placeManager, nameToken, with);

		} else
		{
			gwtLog("Display login page");
			ClientUtils.showPage(placeManager, unauthorizedPlace);
		}
	}

	private void navigateUserIsLoggedIn(LoginResult loginResult)
	{
		UserDTO userDTO = null;
		if (loginResult != null)
		{
			userDTO = loginResult.getUserDTO();
		}
		if (userDTO == null)
		{
			// we're screwed, can't do much. But it can never be
			gwtLog("UserDTO is null, can't do anything");
			return;
		}
		gwtLog("Current user logged in");
		int days = ClientUtils.fromInteger(userDTO.getDaysUntilPasswordExpiration());
		gwtLog(">> Password expires in " + days + " days");
		if (currentUser != null)
		{
			currentUser.setLoggedIn(true);
			gwtLog(">> Send passphrase cached icon refresh message.>>");
			refreshPassphraseCachedIcon();
		}
		startTimer(userDTO);

		if (Boolean.FALSE.equals(userDTO.getAdministrator()))
		{
			// handle passphrase reset first
			boolean passphraseResetRequest = handlePassphraseResetRequest(true);
			if (passphraseResetRequest)
			{
				return;
			}
			gwtLog("Checking if passphrase is generated..");
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
			{
				@Override
				public void onFailure(Throwable caught)
				{
					gwtLog("Error caught: " + caught.getMessage());
					if (caught instanceof PasswordChangeRequiredException)
					{
						placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.CHANGE_INITIAL_PASSWORD).build());
						return;
					}

					if (currentUser != null)
					{
						int state = currentUser.getLoginResult().getLicenseStats().getLicenseExpirationState();
						gwtLog("License state: " + state);
						if (state == LicenseStats.LICENSE_STATE_NOMINAL)
						{
							// license is ok, but some other kind of
							// exception caught
							ClientUtils.showBootboxDialog(glang.error(), caught.getMessage());
						}
					}
					// show the about page
					gwtLog("Show about dialog...");
					ClientUtils.showPage(placeManager, NameTokens.ABOUT);
				}

				@Override
				public void onSuccess(Boolean keyPairCreated)
				{
					if (keyPairCreated != null && Boolean.TRUE.equals(keyPairCreated))
					{
						if (currentUser != null)
						{
							currentUser.setKeypairExists(true);
						}

					} else
					{
						currentUser.setKeypairExists(false);
					}
					if (keyPairCreated != null && Boolean.FALSE.equals(keyPairCreated))
					{
						gwtLog("Redirect to generate key pair view");
						placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.KEY_PAIR).build());
					} else
					{
						gwtLog("Key pair is already generated...");

						// Handle if user has clicked on a notification
						// email link or pasted a link
						final PlaceRequest xPlaceRequest = getPlaceRequest();
						if (xPlaceRequest != null)
						{
							String xNameToken = xPlaceRequest.getNameToken();
							gwtLog("xNameTOken: " + xNameToken);
							Map<String, String> with = ClientUtils.makeMapFromUrlNoPlace(tokenFormatter);
							if (with != null)
							{
								ClientUtils.showPage(placeManager, xNameToken, with);
								return;
							}
						}
						// send to default page as the user has configured
						gwtLog(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Show DEFAULT..............................");
						if (currentUser != null)
						{
							UserDTO userDTO = currentUser.getUserDTO();
							if (userDTO == null)
							{
								gwtLog(">>>>> currentUser > UserDTO is is null, can't do sht");
							} else
							{
								LicenseStats license = loginResult.getLicenseStats();
								gwtLog(">>>>> license found");
								Bootbox.hideAll();
								ClientUtils.popLicenseWarningDialog(license);
							}
						} else
						{
							gwtLog(">>>>> currentUser is null, can't do sht");
						}
						ClientUtils.showDefaultPage(loginResult.getUserDTO(), placeManager);
					}
				}
			};
			AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
			UserService.Utility.getInstance().keypairExists(authCreds, userDTO.getId(), callback);
		} else
		{
			gwtLog("+++++++ ---- ----- Reveal current place for admin");
			if (currentUser != null)
			{
				LicenseStats license = currentUser.getLoginResult().getLicenseStats();
				Bootbox.hideAll();
				int state = ClientUtils.popLicenseWarningDialog(license);
				gwtLog("License state for admin: " + state);
			}
			// Bug #820, show admin console
			ClientUtils.showPage(placeManager, NameTokens.ADMIN_CONSOLE);
			//placeManager.revealCurrentPlace();
		}
	}

	private boolean handlePassphraseResetRequest(boolean isLoggedIn)
	{
		boolean passphraseResetRequst = ClientUtils.passphraseResetRequest(tokenFormatter, placeManager);
		Map<String, String> with = ClientUtils.makeMapFromUrl(tokenFormatter);
		if (passphraseResetRequst)
		{
			if (!isLoggedIn)
			{
				gwtLog("User is not logged in and requested passphrase reset");
				String nameToken = NameTokens.LOGIN;
				ClientUtils.addPlace(placeManager, with, NameTokens.FORGOT_PASSPHRASE);
				ClientUtils.showPage(placeManager, nameToken, with);
			} else
			{
				gwtLog("User is already logged in and requested passphrase reset");
				String nameToken = NameTokens.FORGOT_PASSPHRASE;
				ClientUtils.showPage(placeManager, nameToken, with);
			}
		}
		return passphraseResetRequst;
	}

	public Throwable unwrap(Throwable e)
	{
		if (e instanceof UmbrellaException)
		{
			UmbrellaException ue = (UmbrellaException) e;
			if (ue.getCauses().size() == 1)
			{
				return unwrap(ue.getCauses().iterator().next());
			}
		}
		return e;
	}

	private void refreshPassphraseCachedIcon()
	{
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
		{
			@Override
			public void onSuccess(Boolean rc)
			{
				if (Boolean.TRUE.equals(rc) && currentUser != null)
				{
					gwtLog("Send message to ApplicationPresenter to refresh passphrase cahced icon");
					currentUser.setPassphraseRegistered(true);
				}
			}

			@Override
			public void onFailure(Throwable caught)
			{
				// don't care about error
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().isPassphraseCached(authCreds, callback);
	}

	private void navigateToPasswordChangePage()
	{
		gwtLog("MMMM Reveal password change place");
		ClientUtils.showPage(placeManager, NameTokens.CHANGE_INITIAL_PASSWORD);
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog("ObidosBootstrapper", message);
	}

}
