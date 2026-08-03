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

import com.spenego.Obidos.client.i18n.ObidosMessages;

public class ObidosHideButton extends Button
{
	public ObidosHideButton()
	{
		super();

		setText(ObidosMessages.LANG.hide());
		setSize(ButtonSize.DEFAULT);
		setIcon(IconType.EYE_SLASH);
		setType(ButtonType.LINK);
		getElement().getStyle().setProperty("display", "none !important");
		getElement().getStyle().setProperty("padding", "0px 0px");
		getElement().getStyle().setProperty("outline","none !important");
		getElement().getStyle().setProperty("outlineStyle","none !important");
		getElement().getStyle().setProperty("background", "none !important");
		getElement().getStyle().setProperty("border", "none !important");
		getElement().getStyle().setProperty("boxShadow", "none !important");
		getElement().getStyle().setProperty("color", "#CD2026");
		getElement().getStyle().setProperty("fontWeight", "bold");
	}

}
