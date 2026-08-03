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

package com.spenego.Obidos.server.servlets;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.TokenValidationException;
import com.macasaet.fernet.Validator;
import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.AuditReport;
import com.spenego.Obidos.shared.dto.FernetDTO;

/**
 * A servlet to serve a PDF document as a stream. The GET request will must
 * accompany a Fernet token which the user has obtained before making GET
 * request. The lifetime of the token is TOKEN_LIFETIME defined below.
 *
 * @author spgdev@spenego.com - Apr 19, 2019
 */
// don't need to modify web.xml with servlet and servlet-mapping
// Thanks to: https://www.baeldung.com/register-servlet
/*
@WebServlet(
		name = "ReporterServlet",
		description = "Servlet to generate PDF report",
		urlPatterns = "/report",
		loadOnStartup=1,
		asyncSupported = true)
*/
// Annotation does not work if deployed to jetty . spgdev@spenego.com - Sep 21, 2020
public class ReporterServlet extends HttpServlet
{

	private static final Logger logger = LoggerFactory.getLogger(ReporterServlet.class);
	private static final long serialVersionUID = -3949700523455516467L;

	private final String PROPERTIES_FILE_NAME = "fernet_crypto.properties";
	private final int TOKEN_LIFETIME = 5; // seconds

	private ApplicationContext actx;
	private AuditActions auditactions;

	public void init(ServletConfig servletConfig) throws ServletException
	{
		actx = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		auditactions = (AuditActions) actx.getBean("auditActions");
		// authenticator = (Authenticator) spring.getBean("authenticator");
	}

	private Validator<String> validator = new StringValidator()
	{
		/* use default implementation */ };

	/**
	 * If token is invalid, set 403 forbidden code and return false. Caller must
	 * return if null is returned.
	 *
	 * @param dto
	 * @param req
	 *            http request object
	 * @param res
	 *            Will be updated in case of error
	 * @return string secret in the token if validated. null otherwise
	 *         <p>
	 * @author spgdev@spenego.com - Apr 21, 2019
	 * @throws IOException
	 */
	private final String validateToken(FernetDTO dto, HttpServletRequest req, HttpServletResponse res)
			throws IOException
	{
		if (dto == null)
		{
			// ref:
			// https://www.javamex.com/tutorials/servlets/http_status_code.shtml
			String emsg = "Invalid Request. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader("X-Error-Message", emsg);
			res.sendError(SC_FORBIDDEN, emsg);
			return null;
		}

		// Create the Fernet key from the one we saved in our properties file
		// in base64 format
		final Key fernetKey = new Key(dto.getKey());

		// get the fernet token from the GET request
		String stringToken = req.getParameter("token");
		if (stringToken == null)
		{
			String emsg = "No token in request. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader("X-Error-Message", emsg);
			res.sendError(SC_FORBIDDEN, emsg);
			return null;
		}

		logger.info(() -> "Validating Token");
		// create the Fernet token object
		Token token = null;
		try
		{
			token = Token.fromString(stringToken);
		} catch (Exception e)
		{
			logger.info(() -> "Exception received: " + e.getMessage());
			String emsg = "Invalid Token. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader("X-Error-Message", emsg);
			res.sendError(SC_FORBIDDEN, emsg);
			return null;
		}

		try
		{
			final String secret = token.validateAndDecrypt(fernetKey, validator);
			logger.debug(() -> "Secret " + secret);
			logger.info(() -> "Token is valid. Secret found in the token: " + secret);
			// now check for expiration
			final long tokenEpoch = token.getTimestamp().getEpochSecond(); // seconds
			final long nowEpoch = Instant.now().getEpochSecond();

			if ((nowEpoch - tokenEpoch) > TOKEN_LIFETIME)
			{
				logger.info(() -> "Token has expired. Secrets: " + secret + ". Access Denied!");
				String emsg = "Token has expired. Access Denied!";
				logger.info(() -> emsg);
				res.setHeader("X-Error-Message", emsg);
				res.sendError(SC_FORBIDDEN, emsg);
				return null;
			}

			return secret;
		} catch (TokenValidationException e)
		{
			logger.info(() -> "Token validation failed");
			String emsg = "Invalid Token. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader("X-Error-Message", emsg);
			res.sendError(HttpServletResponse.SC_FORBIDDEN, emsg);
			return null;
		}
	}

	private static void dumpReport(AuditReport report)
	{
		logger.info(() -> "number of users: " + report.getTotalUsers());
		logger.info(() -> "number of admins: " + report.getTotalAdmins());
		logger.info(() -> "number of locked users: " + report.getTotalLockedUsers());
		logger.info(() -> "number of tomstoned users: " + report.getTotalTombstonedUsers());
		logger.info(() -> "number of items: " + report.getTotalItems());
	}

	private static void setResponseError(final HttpServletResponse res, final String s) throws IOException
	{
		logger.info(() -> s);
		res.sendError(SC_FORBIDDEN, s);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		if (auditactions != null)
		{
			logger.info(() -> ">>>> OK!! auditactions bean is available");
		}

		logger.info(() -> "Getting Fernet key...");
		FernetDTO fdto = ServerUtils.getFernetDTO(PROPERTIES_FILE_NAME);
		if (fdto == null)
		{
			setResponseError(res, "Could not get Ferent key");
			return;
		}
		logger.info(() -> "Validating token......");
		String secret = validateToken(fdto, req, res);
		if (secret == null)
		{
			setResponseError(res, "Could not validate PDF downlaod request. Access Denied!");
			return;
		}

		StringTokenizer st = new StringTokenizer(secret, ",");
		int n = st.countTokens();
		logger.info(() -> "Number of comma separated secrets: " + n);
		if (n != 3)
		{
			setResponseError(res, "Invalid secrets in token. Access Denied!");
			return;
		}
		String adminId = st.nextToken();
		String username = st.nextToken();
		String fullname = st.nextToken();

		logger.info(() -> "Serving PDF document to: " + username + "," + fullname + "," + adminId);

		User admin = new User();
		Long id = Long.parseLong(adminId);
		admin.setId(id);
		admin.setUsername(username);
		admin.setFullname(fullname);

		AuditReport report = auditactions.getAuditReport(admin);
		if (report != null)
		{
			logger.info(() -> "Audit report obtianed");
			dumpReport(report);
		} else
		{
			logger.info(() -> "Could not retrieve Audit reports");
		}

		logger.info(() -> "Serving PDF document to: " + fullname);
		logger.info(() -> "Serving PDF document to: " + admin.getUsername() + "," + admin + "," + admin.getId());

		// now serve the PDF
		res.setHeader("Expires", "0");
		res.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		res.setHeader("Pragma", "public");
		res.setContentType("application/pdf");
  		
  		report.setVersion("Version 1.0.1");
  		report.setDate(new Date());
		try (final OutputStream os = res.getOutputStream())
		{
			GenerateAuditReport.genPDFAuditReport(report).writeTo(os);
		}
	}
}
/*
 * @WebServlet( name = "ReporerServlet", description =
 * "Servlet to stream PDF securely", urlPatterns = {"/report"} )
 */
