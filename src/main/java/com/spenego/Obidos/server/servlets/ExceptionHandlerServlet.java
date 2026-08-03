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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ExceptionHandlerServlet
 */
public class ExceptionHandlerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExceptionHandlerServlet()
	{
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		sendErrorMessageToClient(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		sendErrorMessageToClient(request, response);
	}

	private static void sendErrorMessageToClient(HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		// Bug #820
		// It's possible that someone is probing before login, only show minimal info
		// and be vague.
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		response.setContentType("text/plain");
		try (final PrintWriter out = response.getWriter())
		{
			if (statusCode == 404)
			{
				out.write("file does not exist");
			} else
			{
				out.write("unknown error");
			}
			out.flush();
		}
	}
}
