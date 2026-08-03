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

package com.spenego.Obidos.server.services.impl;

import static com.spenego.Obidos.shared.ObidosConstants.OBIDOS_SESSION_COOKIE;
import static com.spenego.Obidos.shared.ObidosConstants.XSRF_COOKIE_NAME;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.google.gwt.user.server.Util;
import com.google.gwt.util.tools.shared.Md5Utils;
import com.google.gwt.util.tools.shared.StringUtils;
import com.spenego.Obidos.client.rpc.SessionEstablishService;
import com.spenego.Obidos.server.ObidosDispatcherUtil;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

/**
 *
 * @author spgdev@spenego.com - Dec 17, 2016
 */
@Service("sessionEstablishService")
public final class SessionEstablishServiceImpl implements SessionEstablishService {
	private static final Logger logger = LoggerFactory.getLogger(SessionEstablishServiceImpl.class);

	public String getXsrfToken() {
		final HttpServletRequest request = ObidosDispatcherUtil.getThreadLocalRequest();
		// check session is Established yet
		if (request.getSession(false) == null) {
			logger.info(() -> "session is null");
			return null;
		}

		final HttpSession session = request.getSession();
		logger.info(() -> "Session: " + session);
		String sessionId = ObidosDispatcherUtil.getThreadLocalRequest().getRequestedSessionId();
		logger.info(() -> "Session id: " + sessionId);
		String cookieName = OBIDOS_SESSION_COOKIE;
		Cookie cookie = new Cookie(cookieName, session.getId());
		byte[] cookieBytes = cookie.getValue().getBytes();
		String tokenValue = StringUtils.toHexString(Md5Utils.getMd5Digest(cookieBytes));
		logger.info(() -> "xsrf token value: " + tokenValue);

		return tokenValue;
	}

	private static void logXSRFToken(Cookie sessionCookie) {
		byte[] cookieBytes = sessionCookie.getValue().getBytes();
		String tokenValue = StringUtils.toHexString(Md5Utils.getMd5Digest(cookieBytes));
		logger.info(() -> "xsrf token value: " + tokenValue);
	}

	private static void logSession(final HttpSession s) {
		if (s.isNew()) {
			logger.info(() -> "Session is new, no session established yet. session id: " + s.getId());
		} else {
			logger.info(() -> "Session is not new. Session id: " + s.getId());
		}
	}

	private static void sendXSRFCookie(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		session.setAttribute(XSRF_COOKIE_NAME, session.getId());
		// set our xsrf cookie
		String cookieName = XSRF_COOKIE_NAME;
		Cookie cookie = new Cookie(cookieName, session.getId());
		cookie.setHttpOnly(true);
		// logger.info(() -> "Sending cookie: " + session.getId());
		String contextPath = request.getContextPath();
		logger.info(() -> "Context path: " + contextPath);
		// if the path is set other than /, it does not seem to work
		cookie.setPath("/"); // seems to work with jetty and tomcat
		// cookie.setPath(contextPath + "/");
		logger.info(() -> "response: " + response);

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie xcookie : cookies) {
				logger.info(() -> ">>>>>>>>>>>> Compare cookie: " + cookieName + " and " + xcookie.getName());
				logger.info(() -> cookie.getName() + " Value =" + xcookie.getValue());
				logger.info(() -> cookie.getName() + " Path =" + xcookie.getPath());
				logger.info(() -> cookie.getName() + " Domain =" + xcookie.getDomain());
				logger.info(() -> cookie.getName() + " Age =" + xcookie.getMaxAge());
			}
		}

		// send our XSRF cookie back
		response.addCookie(cookie);
	}

	@Override
	public String establishSession() {
		HttpServletRequest request = ObidosDispatcherUtil.getThreadLocalRequest();
		Cookie sessionCookie = Util.getCookie(request, OBIDOS_SESSION_COOKIE, false);
		if (sessionCookie == null || sessionCookie.getValue() == null || sessionCookie.getValue().length() == 0) {
			logger.info(() -> "Session cookie is not set");
		} else {
			logXSRFToken(sessionCookie);
		}
		HttpServletResponse response = ObidosDispatcherUtil.getThreadLocalResponse();
		HttpSession session = request.getSession(false);

		if (session == null) {
			// No session exists, create one
			logger.info(() -> "No session exists, creating one..");
			final HttpSession s = request.getSession();
			logger.info(() -> "   Created session: " + s.getId());
			session = s;
		} else {
			logSession(session);
		}

		String sessionId = session.getId();
		// now we have a session object.
		// set our XSRF cookie if session is new of our cookie is not set yet
		// TODO: Do I need to check if our cookie is set or not? Why not send it
		// if the session is new?
		if (session.isNew() || session.getAttribute(XSRF_COOKIE_NAME) == null) {
			sendXSRFCookie(session, request, response);
		} else {
			logger.info(() -> XSRF_COOKIE_NAME + " is already set, will not set again");
		}

		return sessionId;
	}

	@Override
	public Boolean sessionExists() {
		final HttpServletRequest request = ObidosDispatcherUtil.getThreadLocalRequest();
		// check session is Established yet
		if (request == null) {
			return null;
		}

		final HttpSession session = request.getSession(false);
		if (session == null) {
			logger.info(() -> "HTTP Session not established yet");
			return false;
		}

		logger.info(() -> "HTTP Session is established: " + session.getId());
		return true;
	}
}
