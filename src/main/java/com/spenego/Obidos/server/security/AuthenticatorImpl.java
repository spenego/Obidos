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

package com.spenego.Obidos.server.security;

import static com.spenego.Obidos.shared.ObidosConstants.AUTH_SOURCE_LOCAL;
import static com.spenego.Obidos.shared.ObidosConstants.OBIDOS_COOKIE_PATH;
import static com.spenego.Obidos.shared.ObidosConstants.OBIDOS_SESSION_COOKIE;
import static com.spenego.Obidos.shared.ObidosConstants.SESSION_ERROR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.ObidosDispatcherUtil;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.SessionInfoDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.SessionErrorException;

public final class AuthenticatorImpl extends ServerUtils implements Authenticator {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticatorImpl.class);
	private static final String stateInfoKey = ObidosConstants.OBIDOS_SESSION;
	private static final long ONE_HOUR_MS = 1000L * 60 * 60;

	@Autowired
	private UserOperations userOperations;
	@Autowired
	private LdapConfigOperations ldapConfigOperations;
	@Autowired
	private LDAPSecurity ldapSecurity;
	@Autowired
	private PasswordSecurity passwordSecurity;
	private Supplier<HttpSession> sessionSupplier;
	private Map<Long, User> activeUsers = new HashMap<>(31);
	private long activeUserRefreshTime; // flush stale users

	public AuthenticatorImpl() {
		sessionSupplier = ObidosDispatcherUtil::getThreadLocalSession;
	}

	/**
	 * This class contains items we need to store with the user's session.
	 *
	 * @author mmorgan
	 *
	 */
	private class SessionState {
		User user; // we load a fresh user object for each service call
		String sessionHash; // the user is validated by matching this string
		PassphraseHash passphraseHash; // instead of having the user pass this
										// each time
		UUID sessionUUID;
		Long lastPassphraseUpdateTime;
		Integer passphraseUpdateCount;

		SessionState(final User user) throws SessionErrorException {
			this.user = user;
			sessionUUID = UUID.randomUUID();
			sessionHash = generateSessionHash(this.sessionUUID);
			lastPassphraseUpdateTime = 0L;
			passphraseUpdateCount = 0;
		}
	}

	@Override
	public void setSessionSupplier(final Supplier<HttpSession> supplier) {
		sessionSupplier = supplier;
	}

	/**
	 * This method will create session if needed and in the process session will
	 * be refreshed
	 *
	 * @return
	 * @throws SessionErrorException
	 *             <p>
	 * @author spgdev@spenego.com - Nov 11, 2017
	 */
	private HttpSession getSession() throws SessionErrorException {
		final HttpSession session = sessionSupplier.get();
		if (session == null) {
			throw new SessionErrorException("Invalid Session");
		}
		return session;
	}

	private static HttpSession getExistingSession() {
		HttpServletRequest httpRequest = ObidosDispatcherUtil.getThreadLocalRequest();
		return httpRequest == null ? null : httpRequest.getSession(false);
	}

	@Override
	public PassphraseHash getPassphraseHash() {
		return getSessionState().passphraseHash;
	}

	@Override
	public Void setPassphraseHash(final PassphraseHash passphraseHash, final boolean bypassRateLimiter) {
		final SessionState ss = getSessionState();
		final long now = System.currentTimeMillis();
		final long t = ss.passphraseUpdateCount - (now - ss.lastPassphraseUpdateTime) / 10000;

		if (!bypassRateLimiter && t > 0) {
			throw new ServerSideException("You set your passphrase too recently. You may set it again in " + t * 10 + " seconds");
		}

		ss.lastPassphraseUpdateTime = now;
		++ss.passphraseUpdateCount;
		ss.passphraseHash = passphraseHash;

		return null;
	}

	protected String generateSessionHash(final UUID uuid) throws SessionErrorException {
		return sha256Hex(getSession().getId() + uuid);
	}

	@Override
	public String getSessionHash() {
		return sha256Hex(getSession().getId() + getSessionState().sessionUUID);
	}

	private void persistSessionState(final SessionState stateInfo) throws SessionErrorException {
		getSession().setAttribute(stateInfoKey, stateInfo);
	}

	private static Long getLastSessionAccessEpoch(final HttpSession session) {
		final Long epoch = (Long) session.getAttribute(getLastSessionAccessEpochAttr());
		if (epoch == null) {
			final Long newEpoch = (new Date().getTime() / 1000);
			session.setAttribute(getLastSessionAccessEpochAttr(), newEpoch);
			return newEpoch;
		}
		return epoch;
	}

	/**
	 * return null if session is null
	 *
	 * @return
	 *         <p>
	 * @author spgdev@spenego.com - Nov 12, 2017
	 */
	private static Long getOurLastAccessEpoch() {
		final HttpSession session = getExistingSession();
		return (session == null) ? 0L : getLastSessionAccessEpoch(session);
	}

	@Override
	public void insertMockState() throws SessionErrorException {
		persistSessionState(new SessionState(new User(42L, "mockesuseres")));
	}

	private SessionState getSessionState() {
		final SessionState ss = (SessionState) getSession().getAttribute(stateInfoKey);
		if (ss == null) {
			logger.error(() -> "Could not find state information in session, throwing SESSION_ERROR exception");
			throw new SessionErrorException(SESSION_ERROR);
			// if this problem happens, it does never recover. So will clear
			// out everything to see if it makes any difference.
			// spgdev, 08/22/17, Copenhagen
			// throw new SessionErrorException();
		}
		return ss;
	}

	private static SessionState getSessionStateFromExistingSession() {
		HttpSession session = getExistingSession();
		if (session == null) {
			return null;
		}
		final SessionState ss = (SessionState) session.getAttribute(stateInfoKey);
		if (ss == null) {
			logger.error(() -> "Could not find state information in existing session");
		}
		return ss;
	}

	private void clearSessionState() throws SessionErrorException {
		getSession().removeAttribute(stateInfoKey);
	}

	private static User dumpUser(final User user) {
		if (logger.isDebugEnabled()) {
			logger.debug(() -> "Dumping user...start--");
			logger.debug(() -> " username:    " + user.getUsername());
			logger.debug(() -> " auth source: " + user.getAuthSource());
			logger.debug(() -> " is Admin:    " + user.getAdministrator());
			logger.debug(() -> " deleted:     " + user.getDeleted());
			logger.debug(() -> "Dumping userDTO...end--");
		}

		return user;
	}

	@Override
	public String getSavedSessionHash() {
		return getSessionState().sessionHash;
	}

	// Returns a user that is guaranteed to not be deleted.
	private User nonDeletedUser(final User user) {
		if (user.getDeleted()) {
			logger.error(() -> "User " + user.getUsername() + " has been deleted, clearing sesion state and throwing exception.");
			clearSessionState();
			throw new PermissionDeniedException("User " + user.getUsername() + " has been deleted.");
		}
		return user;
	}

	// Returns a user that is guaranteed to not be locked.
	private User nonLockedUser(final User user) {
		if (user.getLocked()) {
			logger.error(() -> "Account for " + user.getUsername() + " is locked, clearing sesion state and throwing exception.");
			clearSessionState();
			throw new PermissionDeniedException("This account has been locked.");
		}
		return user;
	}

	private void dumpSession() throws SessionErrorException {
		final Enumeration<String> names = getSession().getAttributeNames();
		while (names.hasMoreElements()) {
			final String s = names.nextElement();	// do not put this call within the logger closure, it needs to be called
			logger.debug(() -> " Attribute: " + s);
		}
	}

	private Cookie createCookie() {
		final HttpServletRequest tlr = ObidosDispatcherUtil.getThreadLocalRequest();
		final String c = tlr.getParameter(OBIDOS_SESSION_COOKIE);

		// Suddenly session was -always- new. The code was working for ever.
		// Any RPC request sent to server was throwing exception
		// "Could not find state information in session." while session was
		// validated.
		// But when it started to happen, it was happening to chrome and ff.
		// After that recompiling, restarting browser, clearing cookies
		// nothing helped. I do not recall changing anything in this file.
		// The following code seems to solve the problem, found at:
		// https://stackoverflow.com/questions/2138245/session-is-lost-and-created-as-new-in-every-servlet-request
		// spgdev, Copenhagen, 8/22/2017

		final Cookie cookie = new Cookie(OBIDOS_SESSION_COOKIE, c != null ? c : getSession().getId());

		cookie.setComment("SameSite=Strict;");

		if (c == null) {
			logger.info(() -> "Session new, add session cookie to response header");
			final String path = tlr.getContextPath();
			if (path != null) {
				cookie.setPath(path);
			}
			cookie.setHttpOnly(true);
		} else {
			logger.info(() -> "Session exists, session cookie added to response header");
		}

		return cookie;
	}

	@Override
	public User authenticateCredentials(final String username, final String password, final boolean createNewSessionState) {
		logger.info(() -> "Looking for user: " + username);

		final User user = nonLockedUser(nonDeletedUser(userOperations.getUserByUsername(username)));

		logger.info(() -> "User " + username + " found");

		// local authentication with password we stored
		if (user.getAuthSource().equals(AUTH_SOURCE_LOCAL)) {
			if (!passwordSecurity.verifyPassword(password, user.getPasswordDigest())) {
				logger.error(() -> "Could not verify password for user: " + username);
				throw new ServerSideException("Username/password combination does not match.");
			}
		} else {
			logger.info(() -> "ldapConfigOperations: " + ldapConfigOperations);

			try {
				if (!ldapSecurity.authenticate(username, password, user.getAuthSource())) {
					throw new ServerSideException("Unable to process login request.");
				}
				logger.info(() -> "User " + username + " authenticated from LDAP");
			} catch (final ServerSideException e) {
				logger.exception(e);
				throw e;
			}
		}

		logger.info(() -> "User " + username + " authenticated");

		ObidosDispatcherUtil.getThreadLocalResponse().addCookie(createCookie());

		if (createNewSessionState) {
			persistSessionState(new SessionState(user));
		}

		return dumpUser(user);
	}

	@Override
	public synchronized Collection<User> getActiveUsers() {
		return new ArrayList<User>(activeUsers.values());
	}

	private void refreshActiveUsers(final long now) {
		final Map<Long, User> refreshedUsers = new HashMap<>(activeUsers.size() + 31);

		activeUsers.forEach((id, u) -> {
			if (u.getLastActivityTime() + ONE_HOUR_MS > now) { // keep users for 1 hour
				refreshedUsers.put(id, u);
			}});
		activeUsers.clear();
		activeUsers = refreshedUsers;
		activeUserRefreshTime = now + ONE_HOUR_MS / 10; // check every 6 minutes
	}

	private synchronized void updateActiveUsers(final User user, final long now) {
		user.setLastActivityTime(now);
		activeUsers.put(user.getId(), user);

		if (now >= activeUserRefreshTime) {
			refreshActiveUsers(now);
		}
	}

	private User cachedUser(final SessionState state) throws NoSuchRecordException {
		if (userOperations.newerUserExists(state.user)) { // update user cache
			state.user = dumpUser(userOperations.get(state.user.getId()));
		}

		updateActiveUsers(state.user, System.currentTimeMillis());

		return state.user;
	}

	/**
	 * There are three session hashes: 1 - calculated from session ID 2 - passed
	 * in to method from client (sessionId) 3 - Stored locally with session (see
	 * persistSessionState)
	 *
	 * This method ensures they all match.
	 */
	private User getSessionValidatedUser(final String sessionId, final SessionState sessionState) {
		final String calculatedSessionHash = getSessionHash();

		if (logger.isDebugEnabled()) {
			dumpSession();
		}

		// I noticed something is null in the following call
		if (sessionId == null) {
			throw new ServerSideException("Session ID is empty");
		}

		if (sessionState == null) {
			throw new ServerSideException("Session stateInfo is empty");
		}

		if (!sessionId.equals(sessionState.sessionHash) || !calculatedSessionHash.equals(sessionState.sessionHash)) {
			logger.error(() -> "Session Id mismatch");
			logger.error(() -> "Session hash calculated    : " + calculatedSessionHash);
			logger.error(() -> "Session hash stored locally: " + sessionState.sessionHash);
			logger.error(() -> "Session hash from client   : " + sessionId);
			throw new ServerSideException("Session ID mismatch");
		}

		return nonLockedUser(nonDeletedUser(cachedUser(sessionState)));
	}

	private User getUserFromSession(final String sessionId) {
		logger.debug(() -> "In getUserFromSession");

		return getSessionValidatedUser(sessionId, getSessionState());
	}

	private User getUserFromExistingSession(final String sessionId) {
		logger.debug(() -> "In getUserFromExistingSession");

		return getSessionValidatedUser(sessionId, getSessionStateFromExistingSession());
	}

	private static void checkCredentials(final AuthCredsDTO creds) {
		if (creds == null) {
			throw new PermissionDeniedException("Denied! Credentials not supplied");
		}
	}

	@Override
	public User authenticateCookie(final String sessionId) {
		final User user = getUserFromSession(sessionId);
		// TODO: We do not check cookies when authenticating via cookies!
		// TODO: We need to store cookies in DB.
		// This is only session validation, not authentication via cookies.
		persistSessionState(new SessionState(user));

		return user;
	}

	@Override
	public User checkLoggedInAdmin(final AuthCredsDTO creds) {
		checkCredentials(creds);

		// make sure user is logged in
		final User admin = getUserFromSession(creds.getXsrfToken());

		// make sure user is admin, users are always loaded fresh from the DB
		if (!admin.isAdmin()) {
			throw new ServerSideException("Denied! User is not an administrator.");
		}

		return admin;
	}

	@Override
	public User checkLoggedInUser(final AuthCredsDTO creds) {
		checkCredentials(creds);

		// make sure user is logged in
		final User user = getUserFromSession(creds.getXsrfToken());

		if (user.isAdmin()) {
			throw new PermissionDeniedException(ObidosConstants.ADMIN_DENIED);
		}

		return user;
	}

	@Override
	public User checkLoggedIn(final AuthCredsDTO creds) {
		checkCredentials(creds);
		return getUserFromSession(creds.getXsrfToken());
	}

	private User checkExistingSessionLogin(final AuthCredsDTO creds) {
		checkCredentials(creds);

		return getUserFromExistingSession(creds.getXsrfToken());
	}

	private static void addCookieToResponse(final HttpServletResponse response, final Cookie cookie, String path) {
		cookie.setMaxAge(0);
		cookie.setValue(null);
		cookie.setPath(OBIDOS_COOKIE_PATH);
		response.addCookie(cookie);		
	}

	private static void clearSessionCookie() {
		final HttpServletResponse response = ObidosDispatcherUtil.getThreadLocalResponse();
		final Cookie[] cookies = ObidosDispatcherUtil.getThreadLocalRequest().getCookies();

		for (final Cookie cookie : cookies) {
			logger.info(() -> "At logout Clearing Cookie: " + cookie.getName());
			addCookieToResponse(response, cookie, OBIDOS_COOKIE_PATH);
		}

		logger.info(() -> "-- clearSessionCookie()");
		addCookieToResponse(response, new Cookie(OBIDOS_SESSION_COOKIE, null), OBIDOS_COOKIE_PATH);
		logger.info(() -> "-- clearSessionCookie()");
	}

	@Override
	public void logout() throws SessionErrorException {
		clearSessionState();
		clearSessionCookie();
		logger.info(() -> "Invalidating session...");
		getSession().invalidate();
		logger.info(() -> "Clearing dispatcher thread locals");
		ObidosDispatcherUtil.clearThreadLocals();
	}

	private static int getRemainingTimeInSession() throws SessionErrorException {
		final HttpSession session = getExistingSession();

		return (session == null) ? 0 :
			 	(int) (session.getMaxInactiveInterval() - ((System.currentTimeMillis() - session.getLastAccessedTime()) / 1000));
	}

	@Override
	public Integer remainingTimeInSession(AuthCredsDTO creds) {
		return (checkExistingSessionLogin(creds) != null) ? getRemainingTimeInSession() : null; // NOSONAR -- bug in SonarQuebe
	}

	private static void updateSession() throws SessionErrorException {
		final HttpSession session = getExistingSession();
		if (session != null) {
			session.setAttribute(getLastSessionAccessEpochAttr(), (new Date().getTime() / 1000));
		}
	}

	@Override
	public void pingSession(AuthCredsDTO creds) {
		if (checkExistingSessionLogin(creds) != null) {
			updateSession();
		}
	}

	/**
	 * Return Session info without refreshing session
	 */
	@Override
	public SessionInfoDTO sessionInfo() {
		return getSessionInfo();
	}

	@Override
	public SessionInfoDTO getSessionInfo() {
		final HttpSession session = getExistingSession();
		if (session == null) {
			logger.info(() -> "-- no session yet --");
			return new SessionInfoDTO("<Invalid Session>");
		}

		final Long ourLastAccessEpoch = getOurLastAccessEpoch();
		final Long expireEpoch = ourLastAccessEpoch + session.getMaxInactiveInterval();

		// Do not send Session ID anymore.
		// spgdev@spenego.com - Jan 4, 2019
		// dto.setSessionId(session.getId());

		return new SessionInfoDTO("<N/A>", ourLastAccessEpoch, session.getMaxInactiveInterval(), session.getCreationTime() / 1000, expireEpoch);
	}
}
