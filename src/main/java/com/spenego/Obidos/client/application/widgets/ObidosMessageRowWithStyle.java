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

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author spgdev@spenego.com - Aug 18, 2019
 */
public class ObidosMessageRowWithStyle extends ObidosRow
{
	private ObidosMessageLabel messgeLabel;
	
	@UiConstructor
	public ObidosMessageRowWithStyle(final String style)
	{
		super("3px");
		this.messgeLabel = new ObidosMessageLabel();
		FlowPanel fp = new FlowPanel();
		if (style.length() > 0)
		{
			fp.addStyleName(style);
		}
		fp.add(messgeLabel);
		add(fp);
	}

	public ObidosMessageLabel getMessgeLabel()
	{
		return messgeLabel;
	}
	
	public void showMessage(String message)
	{
		getMessgeLabel().showMessage(message);
	}
	
	public void showErrorMessage(String errorMessage)
	{
		getMessgeLabel().showErrorMessage(errorMessage);
	}
	
	public void setBgColor(String bgColor)
	{
		getMessgeLabel().setBgColor(bgColor);
	}

}
