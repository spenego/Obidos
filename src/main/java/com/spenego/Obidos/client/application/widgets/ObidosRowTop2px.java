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

import org.gwtbootstrap3.client.ui.Row;

import com.google.gwt.uibinder.client.UiConstructor;

/**
 * create row with 2px top margin
 * @author spgdev@spenego.com - Jun 23, 2019
 *
 */
public class ObidosRowTop2px extends Row
{
	@UiConstructor
	public ObidosRowTop2px()
	{
		getElement().getStyle().setProperty("marginTop", "2px");
	}
}
