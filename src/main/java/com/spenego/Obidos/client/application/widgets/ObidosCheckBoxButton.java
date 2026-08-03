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

import org.gwtbootstrap3.client.ui.CheckBoxButton;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.spenego.Obidos.client.i18n.ObidosMessages;

/**
 * 
 * @author spgdev@spenego.com - Jul 10, 2019
 *
 */
public class ObidosCheckBoxButton extends CheckBoxButton
{
	public ObidosCheckBoxButton()
	{
		super();
		makeButton();
	}
	
	private void makeButton()
	{
		setText(ObidosMessages.LANG.select());
		setType(ButtonType.LINK);
		getElement().getStyle().setProperty("padding", "1px");
		getElement().getStyle().setProperty("boder", "none !important");
		getElement().getStyle().setProperty("outline","none !important");
		getElement().getStyle().setProperty("outlineStyle","none !important");
		getElement().getStyle().setProperty("background", "none !important");
		getElement().getStyle().setProperty("border", "none !important");
		getElement().getStyle().setProperty("boxShadow", "none !important");
		getElement().getStyle().setProperty("fontWeight", "bold");
//		getElement().getStyle().setProperty("color", "#337ab7 !important");
	}

}
