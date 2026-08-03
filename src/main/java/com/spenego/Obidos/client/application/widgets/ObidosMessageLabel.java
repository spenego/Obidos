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

package com.spenego.Obidos.client.application.widgets;

import org.gwtbootstrap3.client.ui.Label;

/**
 * @author spgdev@spenego.com - May 31, 2019
 * 
 */
public class ObidosMessageLabel extends Label
{
	// bootstrap3 SUCCESS and DANGER colors
//	private final String MESSAGE_BG_COLOR       = "#28a745";
//	private final String ERROR_MESSAGE_BG_COLOR = "#dc3545";
	
	// use colors from Alert
	private final String MESSAGE_BG_COLOR       = "#DFF0D7";
	private final String MESSAGE_FG_COLOR       = "#3A773A";
	private final String ERROR_MESSAGE_BG_COLOR = "#F2DEDE";
	private final String ERROR_MESSAGE_FG_COLOR = "#AB433F";

	public ObidosMessageLabel()
	{
		super();
		setDefaults();
	}
	
	private void setDefaults()
	{
		getElement().getStyle().setProperty("cursor", "default");
		getElement().getStyle().setProperty("backgroundColor", "transparent");
		// wrap text, default is nowrap
		getElement().getStyle().setProperty("whiteSpace", "normal");
		setHTML("&nbsp;");
		setVisible(true);
	}

	private void showMessageReal(final String message, final String bgColor, final String fgColor)
	{
		if (message == null || message.length() == 0)
		{
			setDefaults();
			return;
		}
		getElement().getStyle().setProperty("backgroundColor", bgColor);
		getElement().getStyle().setProperty("color", fgColor);
		getElement().getStyle().setProperty("cursor", "default");
		getElement().getStyle().setProperty("fontSize", "14px");
		setHTML(message);
	}
	
	public void showMessage(String message)
	{
		showMessageReal(message, MESSAGE_BG_COLOR, MESSAGE_FG_COLOR);
	}
	
	public void showErrorMessage(String errorMessage)
	{
		showMessageReal(errorMessage, ERROR_MESSAGE_BG_COLOR, ERROR_MESSAGE_FG_COLOR);
	}
	
	public void setBgColor(String bgColor)
	{
		getElement().getStyle().setProperty("backgroundColor", bgColor);
	}
}
