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

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * {@link Filter} to add cache control headers for GWT generated files to ensure
 * that the correct files get cached.
 *
 * @author See Wah Cheng
 * @created 24 Feb 2009 Ref:
 *          https://seewah.blogspot.com/2009/02/gwt-tips-2-nocachejs-getting-cached-in.html
 */
/*
    <filter>
        <filter-name>obidosServletFilter</filter-name>
        <filter-class>com.spenego.Obidos.server.ObidosServletFilter</filter-class>
        <async-supported>true</async-supported>
    </filter>
    <filter-mapping>
        <filter-name>obidosServletFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
*/

/*
@WebFilter(
		filterName="ObidosServletFilter",
		description="Filter to add cache control headers for GWT generated files to ensure that the correct files get cached",
		urlPatterns="/*",
		asyncSupported=true)
		*/
// Annotation does not work if deployed to jetty. spgdev@spenego.com - Sep 21, 2020
public class ObidosServletFilter implements Filter
{
	public void init(FilterConfig config) throws ServletException
	{
//		logger.info("in init...");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException
	{
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (((HttpServletRequest) request).getRequestURI().contains(".nocache."))
		{
			Date now = new Date();
			httpResponse.setDateHeader("Date", now.getTime());
			// one day old
			httpResponse.setDateHeader("Expires", now.getTime() - 86400000L);
			httpResponse.setHeader("Pragma", "no-cache");
			httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
			httpResponse.addHeader("Server", "spenego");
		}
//		logger.info("Adding Server headers...");
		// add security headers. spgdev@spenego.com - Jul 19, 2019
		// verify with: https://securityheaders.com
		httpResponse.addHeader("Server", "spenego");
		httpResponse.addHeader("X-XSS-Protection", "1;mode=block");
//		httpResponse.addHeader("X-Frame-Options", "DENY");
		// with DENY file upload in POST does not work
//		X-Frame-Options is set in nginx. So this is duplication.
//              httpResponse.addHeader("X-Frame-Options", "SAMEORIGIN");

		// Issue #721 (Could not upload FF error)
		// if nosniff header is set, file upload does not work in Safari.
		// FF also shows an error but can upload file
		// spgdev@spenego.com - Nov 16, 2020
//		httpResponse.addHeader("X-Content-Type-Options", "nosniff");
		httpResponse.addHeader("Referrer-Policy", "same-origin");
//		httpResponse.addHeader("Strict-Transport-Security", "max-age=31536000");
		httpResponse.addHeader("Feature-Policy", "microphone 'none'; payment 'none';");

		// breaks things
		// It appears CSP is a no-go for GWT
//		httpResponse.addHeader("Content-Security-Policy","connect-src 'self';font-src 'self';script-src 'self';style-src 'self' 'unsafe-inline'; img-src 'self';default-src 'none'");
//		httpResponse.addHeader("Content-Security-Policy","script-src 'unsafe-inline'; img-src 'self';default-src 'none'");
//		httpResponse.addHeader("Content-Security-Policy","connect-src 'self';font-src 'self' 'unsafe-inline';img-src 'self';");
		/*
		int port = request.getLocalPort();

		logger.info("MMM local port: " + port);
		logger.info("MMM protocol: " + request.getProtocol());

		// gwt dev mode uses port 8888
		if (port == 8888)
		{
			logger.info("MMM setting CSP to http");
			httpResponse.addHeader("Content-Security-Policy","default-src http: 'unsafe-inline'");
		}
		else
		{
			logger.info("MMM setting CSP to https");
			httpResponse.addHeader("Content-Security-Policy","default-src https: 'unsafe-inline'");
		}
		*/

		filterChain.doFilter(request, response);
	}

	public void destroy()
	{
//		logger.info("in destroy...");
	}

}
