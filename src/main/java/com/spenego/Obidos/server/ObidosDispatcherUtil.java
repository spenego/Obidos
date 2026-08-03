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

package com.spenego.Obidos.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

public final class ObidosDispatcherUtil {
	private static final Logger logger = LoggerFactory.getLogger(ObidosDispatcherUtil.class);

	private static final ThreadLocal<HttpServletRequest>	perThreadRequest  = new ThreadLocal<HttpServletRequest>();
	private static final ThreadLocal<HttpServletResponse>	perThreadResponse = new ThreadLocal<HttpServletResponse>();
	private static final ThreadLocal<ServletContext>		perThreadContext  = new ThreadLocal<ServletContext>();

	/**
	 * Sets the
	 * <code>HttpServletRequest,HttpServletResponse,ServletContext</code>
	 * objects for the current call. It is stored thread-locally so that
	 * simultaneous invocations can have different request objects.
	 */
	protected static void setThreadLocals(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		perThreadRequest.set(request);
		perThreadResponse.set(response);
		perThreadContext.set(context);
	}

	/**
	 * Gets the <code>HttpServletRequest</code> object for the current call. It
	 * is stored thread-locally so that simultaneous invocations can have
	 * different request objects.
	 */
	public static HttpServletRequest getThreadLocalRequest() {
		return perThreadRequest.get();
	}

	/**
	 * Gets the <code>HttpServletResponse</code> object for the current call. It
	 * is stored thread-locally so that simultaneous invocations can have
	 * different response objects.
	 */
	public static HttpServletResponse getThreadLocalResponse() {
		return perThreadResponse.get();
	}

	/**
	 * Clears out the ThreadLocals. This is a convenience method.
	 */
	public static void clearThreadLocals() {
		perThreadRequest.set(null);
		perThreadResponse.set(null);
		perThreadContext.set(null);
	}

	/**
	 * Gets the <code>HttpSession</code> object for the current call. It is
	 * stored thread-locally so that simultaneous invocations can have different
	 * context objects. This is a convenience method, it's equivalent to
	 * getThreadLocalRequest().getSession() with null checking.
	 * @return HttpSession
	 */
	public static HttpSession getThreadLocalSession() {
		final HttpServletRequest request = perThreadRequest.get();
		if (request != null) {
			final HttpSession session = request.getSession();
			if (session != null)
				logger.debug(() -> "Session found: " + session.getId());
			return session;
		}
		return null;
	}

	public static String getRemoteAddr() {
		final HttpServletRequest tlr = getThreadLocalRequest();
		return (tlr == null) ? "unknown" : tlr.getRemoteAddr();
	}
}
