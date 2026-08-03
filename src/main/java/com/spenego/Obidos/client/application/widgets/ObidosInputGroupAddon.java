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

import org.gwtbootstrap3.client.ui.InputGroupAddon;

/**
 * @author spgdev@spenego.com - Aug 31, 2019
 */
public class ObidosInputGroupAddon extends InputGroupAddon
{
	public ObidosInputGroupAddon()
	{
		/*
			    padding: 6px 12px;
		font-size: 14px;
		font-weight: 400;
		line-height: 1;
		color: #555;
		text-align: center;
		background-color: #fff;
		border: 0px solid #fff;
		border-radius: 4px;
		*/
		
		getElement().getStyle().setProperty("padding", "6px 12px");
		// only 13px seems to align the input group with the one below which
		// use col-sm-4
		getElement().getStyle().setProperty("fontSize", "13px");
		getElement().getStyle().setProperty("fontWeight", "400");
		getElement().getStyle().setProperty("lineHeight", "1");
		getElement().getStyle().setProperty("color", "#555");
		getElement().getStyle().setProperty("textAlign", "center");
		getElement().getStyle().setProperty("backgroundColor", "#fff");
		getElement().getStyle().setProperty("border","0px");
		getElement().getStyle().setProperty("borderRadius","4px");
		

	}

}
