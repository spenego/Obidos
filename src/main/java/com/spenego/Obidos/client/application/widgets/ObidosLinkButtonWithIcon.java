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

import com.google.gwt.uibinder.client.UiConstructor;

/**
 * @author spgdev@spenego.com - Aug 18, 2019
 */
public class ObidosLinkButtonWithIcon extends Button
{
	private String text;
	private String tooltip;
	private String icon;
	private String color;

	@UiConstructor
	public ObidosLinkButtonWithIcon(final String text, final String tooltip, final String icon, final String color)
	{
		super();
		this.text = text;
		this.tooltip = tooltip;
		this.icon = icon;
		this.color = color;
		makeButton();
	}
	
	private void makeButton()
	{
		setType(ButtonType.LINK);
		setSize(ButtonSize.DEFAULT);
		setColor(this.color);
		setText(this.text);
		if (this.tooltip.length() > 0)
		{
			setTitle(this.tooltip);
		}
		if (icon.length() > 0)
		{
			IconType iconType = IconType.fromStyleName(icon);
			setIcon(iconType);
		}
		getElement().getStyle().setProperty("width", "0px !important");
		getElement().getStyle().setProperty("display", "none !important");
		getElement().getStyle().setProperty("padding", "1px 1px 1px 1px");
		getElement().getStyle().setProperty("outline","none !important");
		getElement().getStyle().setProperty("outlineStyle","none !important");
		getElement().getStyle().setProperty("background", "none !important");
		getElement().getStyle().setProperty("border", "none !important");
		getElement().getStyle().setProperty("boxShadow", "none !important");
		getElement().getStyle().setProperty("fontWeight", "bold");
	}


}
