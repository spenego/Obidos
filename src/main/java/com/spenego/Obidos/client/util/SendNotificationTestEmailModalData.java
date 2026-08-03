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

package com.spenego.Obidos.client.util;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author spgdev@spenego.com - Sep 24, 2019
 */
public class SendNotificationTestEmailModalData
{
	private Modal	modal;
	private TextBox subjectTextBox;
	private TextBox	toTextBox;
	private TextBox fromTextBox;
	private TextBox urlTextBox;
	private Button 	sendMailButton;

	public Modal getModal()
	{
		return modal;
	}
	public void setModal(Modal modal)
	{
		this.modal = modal;
	}
	public TextBox getToTextBox()
	{
		return toTextBox;
	}
	public void setToTextBox(TextBox toTextBox)
	{
		this.toTextBox = toTextBox;
	}
	public TextBox getFromTextBox()
	{
		return fromTextBox;
	}
	public void setFromTextBox(TextBox fromTextBox)
	{
		this.fromTextBox = fromTextBox;
	}

	public TextBox getUrlTextBox()
	{
		return urlTextBox;
	}
	public void setUrlTextBox(TextBox urlTextBox)
	{
		this.urlTextBox = urlTextBox;
	}
	public Button getSendMailButton()
	{
		return sendMailButton;
	}
	public void setSendMailButton(Button sendMailButton)
	{
		this.sendMailButton = sendMailButton;
	}
	public TextBox getSubjectTextBox()
	{
		return subjectTextBox;
	}
	public void setSubjectTextBox(TextBox subjectTextBox)
	{
		this.subjectTextBox = subjectTextBox;
	}
}
