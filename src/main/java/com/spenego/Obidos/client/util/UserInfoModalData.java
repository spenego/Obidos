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

import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;

import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;

/**
 * @author spgdev@spenego.com - Sep 9, 2019
 */
public class UserInfoModalData
{
	private Modal 				modal;
	private Image   			profileImage;
	private Image               invisibleImage;
	private TextBox 			fullnameTextBox;
	private TextBox 			emailTextBox;
	private TextBox 			acceptEmailNotificationTextBox;
	private TextBox 			phoneTextBox;
	private TextBox             mobilePhoneTextBox;
	private TextBox             acceptSMSNotificationTextBox;
	private TextBox 			officeTextBox;
	private TextBox 			facebookTextBox;
	private TextBox 			twitterTextBox;
	private ObidosMessageRow 	messageRow;
	public Modal getModal()
	{
		return modal;
	}
	public void setModal(Modal modal)
	{
		this.modal = modal;
	}
	public Image getProfileImage()
	{
		return profileImage;
	}
	public void setProfileImage(Image profileImage)
	{
		this.profileImage = profileImage;
	}
	public TextBox getFullnameTextBox()
	{
		return fullnameTextBox;
	}
	public void setFullnameTextBox(TextBox fullnameTextBox)
	{
		this.fullnameTextBox = fullnameTextBox;
	}
	public TextBox getEmailTextBox()
	{
		return emailTextBox;
	}
	public void setEmailTextBox(TextBox emailTextBox)
	{
		this.emailTextBox = emailTextBox;
	}
	public TextBox getPhoneTextBox()
	{
		return phoneTextBox;
	}
	public void setPhoneTextBox(TextBox phoneTextBox)
	{
		this.phoneTextBox = phoneTextBox;
	}
	public TextBox getOfficeTextBox()
	{
		return officeTextBox;
	}
	public void setOfficeTextBox(TextBox officeTextBox)
	{
		this.officeTextBox = officeTextBox;
	}
	public TextBox getFacebookTextBox()
	{
		return facebookTextBox;
	}
	public void setFacebookTextBox(TextBox facebookTextBox)
	{
		this.facebookTextBox = facebookTextBox;
	}
	public TextBox getTwitterTextBox()
	{
		return twitterTextBox;
	}
	public void setTwitterTextBox(TextBox twitterTextBox)
	{
		this.twitterTextBox = twitterTextBox;
	}
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}
	public void setMessageRow(ObidosMessageRow messageRow)
	{
		this.messageRow = messageRow;
	}
	public Image getInvisibleImage()
	{
		return invisibleImage;
	}
	public void setInvisibleImage(Image invisibleImage)
	{
		this.invisibleImage = invisibleImage;
	}
	public TextBox getMobilePhoneTextBox()
	{
		return mobilePhoneTextBox;
	}
	public void setMobilePhoneTextBox(TextBox mobilePhoneTextBox)
	{
		this.mobilePhoneTextBox = mobilePhoneTextBox;
	}
	public TextBox getAcceptEmailNotificationTextBox()
	{
		return acceptEmailNotificationTextBox;
	}
	public void setAcceptEmailNotificationTextBox(TextBox acceptEmailNotificationTextBox)
	{
		this.acceptEmailNotificationTextBox = acceptEmailNotificationTextBox;
	}
	public TextBox getAcceptSMSNotificationTextBox()
	{
		return acceptSMSNotificationTextBox;
	}
	public void setAcceptSMSNotificationTextBox(TextBox acceptSMSNotificationTextBox)
	{
		this.acceptSMSNotificationTextBox = acceptSMSNotificationTextBox;
	}
}
