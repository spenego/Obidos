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

package com.spenego.Obidos.client.application.login;

import java.util.Map;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
// Remember: do not import: com.google.gwt.event.shared.EventBus
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import com.spenego.Obidos.client.application.events.LoginEvent;
import com.spenego.Obidos.client.application.events.LogoutMessageEvent;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosSessionTimer;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.LoginType;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.PasswordChangeRequiredException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;

/**
 * @author spgdev@spenego.com - Dec 19, 2016
 */
public class LoginPresenter extends Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy>
		implements LoginUiHandlers, LogoutMessageEvent.LogoutMessageEventHandler {
	private ObidosMessages glang = ObidosMessages.LANG;

	interface MyView extends View, HasUiHandlers<LoginUiHandlers> {
		public ObidosTextBox getLoginNameField();
		public Input getPasswordField();
		public HTMLPanel getHtmlPanel();
		public FormLabel getFormError();
		public Button getSigninButton();
		public Anchor getForgotPasswordAnchor();
		public Button getShowHidePasswordButton();
	}

	@ProxyStandard
	@NameToken(NameTokens.LOGIN)
	@NoGatekeeper
	interface MyProxy extends ProxyPlace<LoginPresenter> {
	}

	private final EventBus eventBus;
	private final PlaceManager placeManager;
	private final CurrentUser currentUser;
	private final TokenFormatter tokenFormatter;

	@Inject
	LoginPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager, CurrentUser currentUser,
			TokenFormatter tokenFormatter) {
		super(eventBus, view, proxy, RevealType.RootLayout);

		this.eventBus = eventBus;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		this.tokenFormatter = tokenFormatter;

		getView().setUiHandlers(this);
	}
	
	private void resetEyeIcon()
	{
		getView().getPasswordField().getElement().setAttribute("type", "password");
		getView().getShowHidePasswordButton().setType(ButtonType.PRIMARY);
		getView().getShowHidePasswordButton().setIcon(IconType.EYE);
	}

	protected void onReveal() {
		super.onReveal();
		resetEyeIcon();
		ClientUtils.setLoggingLevel();

		gwtLog(">>> onReveal()");

		if (ClientUtils.loggedInCookieFound()) {
			gwtLog("login cookie found..");
			tryLoginWithCookie();
		}
	}

	protected void onReset() {
		super.onReset();
		resetEyeIcon();
		showAuthenticatingMessage(false);
		// don't show the form if login cookie exists, otherwise
		// login from flashes by when redirection happens
		// if (!loggedInCookieFound())
		if (!ClientUtils.loggedInCookieFound())
			getView().getHtmlPanel().setVisible(true);
		else
			getView().getHtmlPanel().setVisible(false);

		getView().getLoginNameField().setFocus(true);
		gwtLog("LoginPresenter onReset");
		ObidosTextBox textBox = getView().getLoginNameField();
		textBox.getElement().setAttribute("autocorrect", "off");
		textBox.getElement().setAttribute("autocomplete", "off");
	}

	protected void onHide() {
		super.onHide();
		resetEyeIcon();
		resetLoginForm();
	}

	private void resetLoginForm() {
		showMessage("");
		getView().getLoginNameField().setValue("");
		getView().getPasswordField().setValue("");
	}

	private void callServerToLogin(LoginActionDTO loginAction) {
		gwtLog("callServerToLogin.....");
		gwtLog("XXX");

		showAuthenticatingMessage(true);

		AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>() {

			@Override
			public void onFailure(Throwable caught) {
				gwtLog("Exception caught: " + caught.getMessage());
				gwtLog("Exception class: " + caught.getClass().getSimpleName());
				showAuthenticatingMessage(false);

				try {
					throw caught;
				} catch (LicenseKeyException e) {
					showErrorMessage(glang.licenseHasExpiredAndBeyondGracePeriod());
				} catch (PermissionDeniedException e) {
					showErrorMessage("Permission denied");
				} catch (StatusCodeException e) {
					switch (e.getStatusCode()) {
						case 500: {
							showErrorMessage(glang.serverErrorCode500());
							break;
						}

						case 0: {
							showErrorMessage(glang.serverIsTemporarilyUnavailable());
							break;
						}
						default: {
							showErrorMessage(glang.serverErrorCode(((StatusCodeException) caught).getStatusCode()));
							break;
						}
					}
				} catch (Throwable e) {
					showErrorMessage(glang.invalidLoginOrPassword());
				}

				if (loginAction.getLoginType() == LoginType.VIA_COOKIE) {
					ClientUtils.clearCookies();
					onLoginFailure();
				}
			}

			@Override
			public void onSuccess(LoginResult loginResult) {
				showAuthenticatingMessage(false);
				UserDTO userDTO = loginResult.getUserDTO();

				currentUser.setLoggedIn(true);
				currentUser.setUserDTO(userDTO);
				currentUser.setKeypairExists(userDTO.keyPairExists());
				currentUser.setLoginResult(loginResult);

				gwtLog("Login successful for: " + userDTO.getUsername());
				gwtLog("usertype admin?: " + userDTO.getAdministrator());
				int days = ClientUtils.fromInteger(userDTO.getDaysUntilPasswordExpiration());
				gwtLog(">> Password expires in : " + days + " days");
				// save it to current user
				UserDTO udto = currentUser.getUserDTO();
				if (udto != null) {
					udto.setDaysUntilPasswordExpiration(days);
				}

				// once mistakenly get the stupid xsfr from loginAction and it was undefined
				// took a while to debug that garbage
				String xsrfToken = loginResult.getXsrfToken();
				ClientUtils.setXSRFToken(xsrfToken);
				currentUser.setXsrfToken(xsrfToken);

				if (loginAction.getLoginType() == LoginType.VIA_COOKIE) {
					gwtLog("++++ Logged with via coookie... key pair exists?: " + userDTO.keyPairExists());
					gwtLog("Date format login with cookie: " + loginResult.getDateFormat());
					loginSucceededWithCookie(loginResult);
				} else {
					loginSucceeded(loginResult, false);
				}

				fireSuccessfulLoginEvent(userDTO);

				// start session timeout timer
				// test small timeout hack----remove asap
				//loginResult.setSessionTimeoutSeconds(300);
				// hack----
				long sessionTimeoutMillis = ClientUtils.sessionTimeoutMillis(loginResult.getSessionTimeoutSeconds());
				long sessionCheckMillis = ObidosConstants.SESSION_CHECK_MILLIS; // check in every 1 minute
				long sessionGraceMillis = ObidosConstants.SESSION_GRACE_MILLIS; // pop for 1 minute
				gwtLog("Session start Obidos Session Timer");
				ObidosSessionTimer.start(placeManager, currentUser, sessionTimeoutMillis, sessionCheckMillis,
						sessionGraceMillis);

				gwtLog("Check license state");
				LicenseStats license = loginResult.getLicenseStats();
				int expState = license.getLicenseExpirationState();
				if (expState != LicenseStats.LICENSE_STATE_NOMINAL
						&& (expState != LicenseStats.LICENSE_STATE_BEYOND_GRACE_PERIOD)) {
					Bootbox.hideAll();
					int state = ClientUtils.popLicenseWarningDialog(loginResult.getLicenseStats());
					gwtLog("License state: " + state);
				} else {
					gwtLog("Beyond grace period.. will be popped from About page");
				}

				gwtLog("Session timeout seconds: " + loginResult.getSessionTimeoutSeconds());
				String lmsg = ClientUtils.getLicenseStateMessage(loginResult.getLicenseStats().getLicenseExpirationState());
				gwtLog("License message: " + lmsg);

			}
		};
		LoginService.Utility.getInstance().login(loginAction, callback);
	}

	private void gwtLog(String message) {
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	private void loginSucceededWithCookie(LoginResult loginResult) {
		loginSucceeded(loginResult, true);
	}

	private void loginSucceeded(LoginResult loginResult, boolean withCookie) {
		gwtLog("in login succeeded...");
		UserDTO userDTO = loginResult.getUserDTO();
		currentUser.setLoggedIn(true);

		currentUser.setUserDTO(userDTO);
		currentUser.setSessionTimeoutIntervalSeconds(loginResult.getSessionTimeoutSeconds());
		// Issue #806
		if (userDTO.authSourceIsLocal() &&  userDTO.getPasswordChangeRequired()) {
			redirectToInitialPasswordChangePage(userDTO);
		} else if (ClientUtils.passphraseResetRequest(tokenFormatter, placeManager)) {
			redirectToPassphraseResetPage(userDTO);
		} else {
			String place = ClientUtils.getPlaceFromUrl(placeManager);
			String token = ClientUtils.getTokenFromUrl(placeManager);
			boolean twoFaRequried = ClientUtils.getTwoFaRequiredFromUrl(placeManager);
			gwtLog(">> Place: " + place);
			gwtLog(">> Token: " + token);
			gwtLog(">> 2FA required: " + twoFaRequried);

			redirectToLoggedOnPage(userDTO, withCookie);
		}
	}

	/**
	 * redirect to the application main page Note: we display the HomeView by
	 * default
	 * <p>
	 * 
	 * @author spgdev@spenego.com - Jan 1, 2017 Update: check if key pair is
	 *         generated yet, if no redirect to generate key pair page, otherwise
	 *         redirect to container page Jul-03-2017
	 */
	private void redirectToLoggedOnPage(UserDTO userDTO, boolean withCookie) {
		// a client side hack not to show create key pair view for admin.
		// admin can create keypair but we don't show the screen in UI at this time
		// if some admin bypasses it, it's ok.
		if (userDTO.getAdministrator()) {
			// Issue #656
			if (!ClientUtils.isLicenseOk(currentUser.getLoginResult().getLicenseStats())) {
				gwtLog("Go to ABOUT Page, wanrning dialog will be popped from there");
				ClientUtils.showPage(placeManager, NameTokens.ABOUT);
				return;
			}

			ClientUtils.showPage(placeManager, NameTokens.ADMIN_CONSOLE);
			return;

		}
		else
		{
			// Community Bug #30
			if (!ClientUtils.isLicenseOk(currentUser.getLoginResult().getLicenseStats())) {
				gwtLog("Go to ABOUT Page, wanrning dialog will be popped from there");
				ClientUtils.showPage(placeManager, NameTokens.ABOUT);
				return;
			}
		}

		// regular user
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				gwtLog(">>> Error caught: " + caught.getMessage());
				String exceptionClassName = caught.getClass().getCanonicalName();
				gwtLog(">>>>> Exception: " + exceptionClassName);
				showErrorMessage(caught.getMessage());
				if (caught instanceof PasswordChangeRequiredException)
				{
					placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.CHANGE_INITIAL_PASSWORD).build());
				}
				else
				{
					ClientUtils.showPage(placeManager, NameTokens.ERROR);
				}
					
			}

			@Override
			public void onSuccess(Boolean keyPairCreated) {
				if (!keyPairCreated) {
					gwtLog("Redirect to generate key pair view");
					gwtLog("User logged in: " + currentUser.isLoggedIn());
					placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.KEY_PAIR).build());
//					ClientUtils.showPage(placeManager, NameTokens.ABOUT);
				} else {
					askToRegisterPassphrase(userDTO, false);
					/*
					 * if (withCookie == false) { // logged in with password, ask to register
					 * askToRegisterPassphrase(userDTO, withCookie); } else {
					 * gwtLog("Redirect to container view ----"); showContainerPage(userDTO); }
					 */
				}
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		// gwtLog("username, id in authCreds: " + authCreds.getUsername() + " id: " +
		// authCreds.getUserId());
		UserService.Utility.getInstance().keypairExists(authCreds, userDTO.getId(), callback);

		gwtLog("+++ user id: " + userDTO.getId());
		/*
		 * String token = placeManager .getCurrentPlaceRequest()
		 * .getParameter("redirectTo", NameTokens.ABOUT); PlaceRequest placeRequest =
		 * new Builder().nameToken(token).build();
		 * placeManager.revealPlace(placeRequest);
		 */
	}

	private void showRegisterPassphrasePage() {
		String nameToken = NameTokens.REGISTER_PASSPHRASE;
		Map<String, String> with = ClientUtils.makeMapFromUrl(true, tokenFormatter);
		if (with != null) {
			with.put(ObidosConstants.LOGIN_STAGE, ObidosConstants.LOGIN);
			ClientUtils.showPage(placeManager, nameToken, with);
		} else {
			ClientUtils.showPage(placeManager, nameToken);
		}
	}

	private void askToRegisterPassphrase(UserDTO userDTO, boolean withCookie) {
		GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this) {

			@Override
			public void uponFailure(Throwable caught) {
				gwtLog("ERROR: " + caught.getMessage());
			}

			@Override
			public void uponSuccess(Boolean cached) {
				if (!cached) {
					gwtLog("Passphrase not cached.. ask to register");
					showRegisterPassphrasePage();
				} else {
					currentUser.setPassphraseRegistered(true);
					ClientUtils.showDefaultPage(userDTO, placeManager);
				}
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().isPassphraseCached(authCreds, callback);
	}

	private void redirectToInitialPasswordChangePage(UserDTO userDTO) {
		PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.CHANGE_INITIAL_PASSWORD)
				.with(ObidosConstants.USER_ID, userDTO.getId().toString()).build();

		placeManager.revealPlace(placeRequest);

		// placeManager.revealPlace(new
		// PlaceRequest.Builder().nameToken(NameTokens.CHANGE_INITIAL_PASSWORD).build());
	}

	private void redirectToPassphraseResetPage(UserDTO userDTO) {
		gwtLog("Redirect to passprhase reset page");
		Map<String, String> with = ClientUtils.makeMapFromUrl(tokenFormatter);
		if (with != null) {
			for (Map.Entry<String, String> entry : with.entrySet()) {
				gwtLog("Key: " + entry.getKey() + " Value: '" + entry.getValue() + "'");
			}
			gwtLog("Redirecting to FORGOT_PASSPHRASE PAGE");
			String nameToken = NameTokens.FORGOT_PASSPHRASE;
			ClientUtils.showPage(placeManager, nameToken, with);
		} else {
			showErrorMessage("Incomplete Passphraser reset request");
		}
	}

	private void onLoginFailure() {
		redirectToLoginDialogPage();
	}

	private void redirectToLoginDialogPage() {
		gwtLog(">> redirecting to login page");
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.LOGIN).build());
	}

	// called from onReveal()
	private void tryLoginWithCookie() {
		// String cookie = getLoggedInCookie();
		String xsrfToken = ClientUtils.getXSRFTokenFromCookie();
		LoginActionDTO loginAction = new LoginActionDTO(xsrfToken);
		gwtLog("try Login with Cookike...");
		callServerToLogin(loginAction);
	}

	private boolean loggedInCookieFound() {
		String cookie = Cookies.getCookie(ObidosConstants.OBIDOS_LOGIN_COOKIE);
		if (cookie == null)
			return false;
		if ((cookie != null) && cookie.equals("null"))
			return false;
		return true;
	}

	private String getLoggedInCookie() {
		String cookie = Cookies.getCookie(ObidosConstants.OBIDOS_LOGIN_COOKIE);
		return cookie;
	}

	/**
	 * fire event to tell login is successful. ApplicatonPresenter is listening on
	 * eventbus, after getting the event, it will adjust the nav bar accordingly
	 * 
	 * @param userDTO returned by LoginService
	 *                <p>
	 * @author spgdev@spenego.com - Jan 2, 2017
	 */
	private void fireSuccessfulLoginEvent(UserDTO userDTO) {
		int days = ClientUtils.fromInteger(userDTO.getDaysUntilPasswordExpiration());
		gwtLog("<<<<>>>> Password expires in : " + days + " days");
		// save it to current user
		UserDTO udto = currentUser.getUserDTO();
		if (udto != null) {
			udto.setDaysUntilPasswordExpiration(days);
		}

		gwtLog("LoginPresenter Firing LoginEvent..: " + userDTO.getUsername());
		gwtLog("LoginPresenter Firing LoginEvent..admin: " + userDTO.getAdministrator());
		// LoginEvent loginEvent = new LoginEvent(userDTO);
		// getEventBus().fireEvent(loginEvent);
		LoginEvent.fire(LoginPresenter.this, userDTO);
	}

	@Override
	public void showResetPasswordRequestView() {
		ClientUtils.showPage(placeManager, NameTokens.RESET_PASSWORD_REQUEST);
	}

	// Note: if any of the annotations are even inside comment, stupid GWT compiler
	// seems to find them and
	// compile. bizarre! So be careful. Remove all unneeded annotations.

	/**
	 * Note: without ProxyEvent at the very first time, the method will not be
	 * called because the event was fired by LoginPresenter before this presenter is
	 * instantiated! The post http://blog.arcbees.com/2010/08/31/using-proxyevent/
	 * glosses on it and there seem to be some pitfalls. Apparently using ProxyEvent
	 * is not a good thing, but that's the only way I could make this thing work.
	 * GWTP programmers really suck!
	 */
	@ProxyEvent
	@Override
	public void onReceiveLogoutMessage(LogoutMessageEvent event) {
		gwtLog(">> onReceiveLogoutMessage LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLl");

		gwtLog("Clear cookies");
		ClientUtils.clearCookies();
		gwtLog("Set currentUser to logged out");
		currentUser.setLoggedIn(false);
		gwtLog("Cancel timers");
		ClientUtils.cancelTimers();

		String place = History.getToken();
		gwtLog("Current Place: " + place + " length: " + place.length());

		if (event == null) {
			return;
		}

		MessageDTO messageDTO = event.getMessageDTO();
		if (messageDTO == null) {
			return;
		}
		String message = messageDTO.getMessage();
		if (message != null) {
			gwtLog("Message received.." + message);
			ClientUtils.showLoggedOutMessage(message, getView().getFormError());
		}
	}

	private void showMessage(String message) {
		ClientUtils.showMessage(message, getView().getFormError());
	}

	private void showErrorMessage(String errorMessage) {
		ClientUtils.showErrorMessage(errorMessage, getView().getFormError());
	}

	// DO NOT implement it
	@Override
	public void logout() {
	}

	@Override
	public void loginFieldEnterCallback() {
		showMessage("");
		String loginName = getView().getLoginNameField().getValue();
		if (loginName != null && loginName.length() > 0) {
			getView().getPasswordField().setFocus(true);
		}
	}

	@Override
	public void passwordFieldEnterCallback() {
		String loginName = getView().getLoginNameField().getValue();
		String password = getView().getPasswordField().getValue();
		boolean c = checkLoginPassword(loginName, password);
		if (c) {
			login();
		}
	}

	private boolean checkLoginPassword(String loginName, String password) {
		ObidosMessages lang = ObidosMessages.LANG;
		String errorMessage = "";
		boolean loginNameEmpty = false;
		boolean passwordEmpty = false;
		if (loginName == null || (loginName != null && loginName.length() == 0)) {
			loginNameEmpty = true;
		}

		if (password == null || (password != null && password.length() == 0)) {
			passwordEmpty = true;
		}
		if (loginNameEmpty && passwordEmpty) {
			errorMessage = lang.specifyLoginAndPassword();
			getView().getLoginNameField().setFocus(true);
		} else if (loginNameEmpty) {
			errorMessage = lang.specifyLoginName();
			getView().getLoginNameField().setFocus(true);
		} else if (passwordEmpty) {
			errorMessage = lang.specifyPassword();
			getView().getPasswordField().setFocus(true);
		}
		if (errorMessage.length() > 0) {
			showErrorMessage(errorMessage);
			return false;
		}

		return true;
	}

	/*
	 * @Override public void login(String username, String password) { Log.info(()
	 * -> "in login clearing cookies...."); ClientUtils.clearCookies();
	 * 
	 * LoginActionDTO loginAction = new LoginActionDTO(username, password);
	 * callServerToLogin(loginAction); }
	 */

	@Override
	public void login() {
	    gwtLog("9999 FCK");
		String loginName = getView().getLoginNameField().getValue();
		String password = getView().getPasswordField().getValue();
		boolean rc = checkLoginPassword(loginName, password);
		if (!rc) {
			return;
		}
		ClientUtils.clearCookies();
		LoginActionDTO loginAction = new LoginActionDTO(loginName, password);
		callServerToLogin(loginAction);
	}

	/**
	 * @return ObidosMessages return the glang
	 */
	public ObidosMessages getGlang() {
		return glang;
	}

	/**
	 * @param glang the glang to set
	 */
	public void setGlang(ObidosMessages glang) {
		this.glang = glang;
	}
	
	private void showAuthenticatingMessage(boolean show)
	{
		Button b = getView().getSigninButton();
		showMessage("");
		b.setIcon(null);
		getView().getLoginNameField().setEnabled(! show);
		getView().getPasswordField().setEnabled(! show);
		getView().getForgotPasswordAnchor().setVisible(! show);
		b.setEnabled(! show);
		if (show)
		{
			showMessage(glang.authenticating());
			b.setIcon(IconType.SPINNER);
			b.setIconSpin(true);
		}
	}
}