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
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.uibinder.client.UiConstructor;
import com.spenego.Obidos.client.i18n.ObidosMessages;

/**
 * @author spgdev@spenego.com - Sep 23, 2019
 */
public class ObidosPanelHeader extends PanelHeader
{
	private ObidosMessages lang = ObidosMessages.LANG;
	private Button helpButton;
	private Button backButton;
	private Heading heading;

	@UiConstructor
	public ObidosPanelHeader(boolean showBackButton, String headerText)
	{
		makePanelHeader(showBackButton, headerText);
	}
	
	private Button makeLinkButton(final IconType icon, final String text)
	{
		Button button = new Button();
		button.setType(ButtonType.LINK);
		button.setSize(ButtonSize.DEFAULT);
		button.setIcon(icon);
		button.setText(text);
		
        button.getElement().getStyle().setProperty("width", "auto");
        button.getElement().getStyle().setProperty("outline","none !important");
        button.getElement().getStyle().setProperty("outlineStyle","none !important");
        button.getElement().getStyle().setProperty("background", "none !important");
        button.getElement().getStyle().setProperty("border", "none !important");
        button.getElement().getStyle().setProperty("boxShadow", "none !important");
        button.getElement().getStyle().setProperty("fontSize", "16px");
        button.getElement().getStyle().setProperty("color", "#ffffff");
        // must set padding to 0px or help moves down
        button.getElement().getStyle().setProperty("padding", "0px");
        
		return button;
	}

	private void makePanelHeader(boolean showBackButton, String headerText)
	{

		addStyleName("text-center");

		this.helpButton = makeLinkButton(IconType.INFO_CIRCLE, lang.help());
		this.helpButton.addStyleName("pull-right");
		add(this.helpButton);

		this.backButton = makeLinkButton(IconType.ARROW_CIRCLE_LEFT, lang.back());
		this.backButton.addStyleName("pull-left");
		add(this.backButton);
		if (showBackButton)
		{
			this.backButton.setVisible(true);
		}
		else
		{
			this.backButton.setVisible(false);
		}
		
		this.heading = new Heading(HeadingSize.H3);
        this.heading.getElement().getStyle().setProperty("display", "inline-block");
        this.heading.setText(headerText);
		add(this.heading);
	}

	public Button getHelpButton()
	{
		return helpButton;
	}

	public Button getBackButton()
	{
		return backButton;
	}

	public Heading getHeading()
	{
		return heading;
	}
	
	public void setHeadingText(final String text)
	{
		this.heading.setText(text);
	}
	
	public void setText(final String text)
	{
		setHeadingText(text);
	}
	
	public void setTitle(final String text)
	{
		setHeadingText(text);
	}
}
