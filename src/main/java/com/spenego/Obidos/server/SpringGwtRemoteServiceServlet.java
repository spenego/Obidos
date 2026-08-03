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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/*
 * This file is adapted from spring4gwt project.
 * It intercepts and converts all spring services to GWT remote services at run time.
 * spgdev 11/30/2016
 * spring4gwt taken from: https://github.com/ramonqueiroga/spring4gwt
 * public class SpringGwtRemoteServiceServlet extends RemoteServiceServlet
 * User annotation: 9/20/2020
 */
/*
@WebServlet(name = "SpringGwtRemoteServiceServlet",
	description = "Servlet intercept and convert spring services to GWT remote servlets at run time",
	urlPatterns = "/Delegator/rpc/*")
*/
// Annotation does not work if deployed to jetty . spgdev@spenego.com - Sep 21, 2020
public class SpringGwtRemoteServiceServlet extends RemoteServiceServlet
{
	private static final long serialVersionUID = -2398603162106518117L;

	@Override
	public String processCall(String payload) throws SerializationException
	{
		try
		{
			Object handler = getBean(getThreadLocalRequest());
			RPCRequest rpcRequest = RPC.decodeRequest(payload, handler.getClass(), this);
			onAfterRequestDeserialized(rpcRequest);

			// taken from Predictionator
			// spgdev
			ObidosDispatcherUtil.setThreadLocals(getThreadLocalRequest(), getThreadLocalResponse(),
					getServletContext());

			return RPC.invokeAndEncodeResponse(handler, rpcRequest.getMethod(), rpcRequest.getParameters(),
					rpcRequest.getSerializationPolicy());
		} catch (IncompatibleRemoteServiceException ex)
		{
			log("An IncompatibleRemoteServiceException was thrown while processing this call.", ex);
			// taken from Predictionator
			// spgdev
			return RPC.encodeResponseForFailure(null, ex);
		} finally
		{
			ObidosDispatcherUtil.clearThreadLocals();
		}
	}

	protected Object getBean(HttpServletRequest request)
	{
		String service = getService(request);
		Object bean = getBean(service);
		return bean;
	}

	protected String getService(HttpServletRequest request)
	{
		String url = request.getRequestURI();
		return url.substring(url.lastIndexOf("/") + 1);
	}

	protected Object getBean(String name)
	{
		WebApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		if (applicationContext == null)
		{
			throw new IllegalStateException("No Spring web application context found");
		}
		if (!applicationContext.containsBean(name))
		{
			throw new IllegalArgumentException("Spring bean not found: " + name);
		}
		return applicationContext.getBean(name);
	}
	/**
	 * I'm overriding the method for testing. The original method is in:
	 * src/com/google/gwt/user/server/rpc/XsrfProtectedServiceServlet.java in
	 * GWT source
	 */
	// public class SpringGwtRemoteServiceServlet extends
	// XsrfProtectedServiceServlet
	/*
	 * @Override protected void validateXsrfToken(RpcToken token,Method method)
	 * throws RpcTokenException { HttpServletRequest request =
	 * ObidosDispatcherUtil.getThreadLocalRequest(); logger.info("RpcToken: " +
	 * token); if (token == null) { //throw new
	 * RpcTokenException("XSRF token is null"); logger.info(() ->
	 * "XSRF Token is null!!!"); return; } String sessionCookieName =
	 * ObidosConstants.DELEGATOR_SESSION_COOKIE;
	 * logger.info("Sesson cookie name: " + sessionCookieName); Cookie
	 * sessionCookie = Util.getCookie(getThreadLocalRequest(),
	 * sessionCookieName, false); if (sessionCookie == null ||
	 * sessionCookie.getValue() == null || sessionCookie.getValue().length() ==
	 * 0) { logger.info(() ->
	 * " XXXXXXXXXXX WARNING XXXXXXXX Session cookie is missing or empty!");
	 * return; }
	 *
	 * logger.info(()-> "Session cookie: " + sessionCookie.getName() + "=" +
	 * sessionCookie.getValue());
	 *
	 * String expectedToken =
	 * StringUtils.toHexString(Md5Utils.getMd5Digest(sessionCookie.getValue().
	 * getBytes())); XsrfToken xsrfToken = (XsrfToken) token; String realToken =
	 * xsrfToken.getToken(); logger.info(() -> ">>>>>>> Expected XSRF token: " +
	 * expectedToken); logger.info(() -> ">>>>>>>	 Real XSRF token: " +
	 * realToken); if (!expectedToken.equals(xsrfToken.getToken())) { throw new
	 * RpcTokenException("Invalid XSRF token. Tokens mismatch!"); } }
	 */
}
