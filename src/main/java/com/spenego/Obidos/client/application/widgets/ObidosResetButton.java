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

import com.spenego.Obidos.client.i18n.ObidosMessages;

/**
 * 
 * @author spgdev@spenego.com - Jul 7, 2019
 */
public class ObidosResetButton extends Button
{
	public ObidosResetButton()
	{
		super();
		makeButton();
	}
	
	private void makeButton()
	{
		// setType(ButtonType.DEFAULT);
		// setSize(ButtonSize.DEFAULT);
		setText(ObidosMessages.LANG.resetButtonTitle());
		getElement().getStyle().setProperty("outlineStyle","none !important");

// Per http://web-accessibility.carnegiemuseums.org/design/color/, #ffffff on #5b616b is fine.
		getElement().getStyle().setProperty("color", "#ffffff");
		getElement().getStyle().setProperty("backgroundColor", "#5b616b");

		getElement().getStyle().setProperty("boxShadow", "none !important");
	}

}
