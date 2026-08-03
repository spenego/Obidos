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

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasVisibility;

/**
 * A message label with an integrated close button
 * With the help from Claude AI Sonnet 3.5 (new). Previous implementation has
 * the x button out of place at the right side of the message, now it blends
 * with the message, I think it looks nicer.
 * spgdev@spenego.com - Dec 10, 2024 
 */
public class ObidosMessageLabelWithCloseButton extends Span implements HasVisibility
{
	private final Button closeButton;

	// use colors from Alert
	private final String MESSAGE_BG_COLOR = "#DFF0D7";
	private final String MESSAGE_FG_COLOR = "#3A773A";
	private final String ERROR_MESSAGE_BG_COLOR = "#F2DEDE";
	private final String ERROR_MESSAGE_FG_COLOR = "#AB433F";

	public ObidosMessageLabelWithCloseButton()
	{
		super();
		closeButton = new Button();
		closeButton.setSize(ButtonSize.EXTRA_SMALL);
		closeButton.setIcon(IconType.CLOSE);
		closeButton.setType(ButtonType.LINK);
		closeButton.setTitle("Close");

		// Remove button styling
		closeButton.getElement().getStyle().setProperty("border", "none");
		closeButton.getElement().getStyle().setProperty("outline", "none");
		closeButton.getElement().getStyle().setProperty("boxShadow", "none");
		closeButton.getElement().getStyle().setProperty("backgroundColor", "transparent");
		closeButton.getElement().getStyle().setProperty("fontSize", "14px");
		closeButton.getElement().getStyle().setProperty("fontWeight", "bold");

		closeButton.getElement().getStyle().setProperty("borderLeft", "1px solid rgba(0,0,0,0.1)");
		closeButton.getElement().getStyle().setProperty("borderRight", "1px solid rgba(0,0,0,0.1)");
		closeButton.getElement().getStyle().setProperty("borderBottom", "1px solid rgba(0,0,0,0.1)");
		closeButton.getElement().getStyle().setProperty("borderRadius", "4px");

		getElement().getStyle().setProperty("borderRadius", "4px");

		closeButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				setVisible(false);
			}
		});
		add(closeButton);
		setDefaultStyle();
	}

	// do not set any HTML as the widget is not rendered yet and wit will 
	// throw exception
	private void setDefaultStyle()
	{
		getElement().getStyle().setProperty("cursor", "default");
		getElement().getStyle().setProperty("backgroundColor", "transparent");
		// wrap text, default is nowrap
		getElement().getStyle().setProperty("whiteSpace", "normal");
		getElement().getStyle().setProperty("paddingTop", "5px");
		getElement().getStyle().setProperty("paddingBottom", "5px");
		getElement().getStyle().setProperty("paddingLeft", "4px");
		getElement().getStyle().setProperty("fontWeight", "bold");
	}

	private void showMessageReal(final String message, final String bgColor, final String fgColor)
	{
		if (message == null || message.length() == 0)
		{
			return;
		}

		setVisible(true);

		getElement().getStyle().setProperty("backgroundColor", bgColor);
		getElement().getStyle().setProperty("color", fgColor);
		getElement().getStyle().setProperty("fontWeight", "bold");
		closeButton.getElement().getStyle().setProperty("color", fgColor);
		clear();
		String msg = message + "&nbsp;&nbsp;";
		setHTML(msg);
		// re-add the button as HTML has replaced everything in the span
		add(closeButton);
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

	/**
	 * Add a handler for close button clicks
	 */
	public HandlerRegistration addCloseClickHandler(ClickHandler handler)
	{
		return closeButton.addClickHandler(handler);
	}

	/**
	 * Get the close button for additional styling if needed
	 */
	public Button getCloseButton()
	{
		return closeButton;
	}
}