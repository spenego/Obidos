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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Issue #13
 * label was clickable, now a button was added to remove the message
 * Aug-26-2020
 * @author spgdev@spenego.com - Jun 9, 2019
 * Use ObidosMessagLabelWithCloseButton widget so that the button blends with
 * the text
 * spgdev@spenego.com - Dec 10, 2024
 */
public class ObidosMessageRow extends ObidosRow
{
	private ObidosMessageLabelWithCloseButton messgeLabel;
	private FlowPanel fp;
	
	public ObidosMessageRow()
	{
		super("3px");
		messgeLabel = new ObidosMessageLabelWithCloseButton();
		fp = new FlowPanel();
		fp.addStyleName("col-sm-12 center-block text-center");
		fp.add(messgeLabel);
		add(fp);
		hideMessage(null);
	}
	
	private void show(boolean visible)
	{
		getFp().setVisible(visible);
	}

	public ObidosMessageLabelWithCloseButton getMessageLabel()
	{
		return messgeLabel;
	}
	
	private void hideMessage(final String message)
	{
		if (message == null || message.length() == 0)
		{
			show(false);
		}
		else
		{
			show(true);
		}
	}
	
	
	public void showMessage(String message)
	{
		hideMessage(message);
		getMessageLabel().showMessage(message);
	}
	
	public void clear()
	{
		showMessage("");
	}
	
	public void showErrorMessage(String errorMessage)
	{
		hideMessage(errorMessage);
		getMessageLabel().showErrorMessage(errorMessage);
	}
	
	public void setBgColor(String bgColor)
	{
		getMessageLabel().setBgColor(bgColor);
	}

	public FlowPanel getFp()
	{
		return fp;
	}
}
