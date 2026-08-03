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

package com.spenego.Obidos.client.application.licenseinfo;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;

class LicenseInfoView extends ViewWithUiHandlers<LicenseInfoUiHandlers> implements LicenseInfoPresenter.MyView
{
	
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	TextBox companyTextBox;
	
	@UiField
	TextBox emailTextBox;
	
	@UiField
	TextBox signedOnTextBox;
	
	@UiField
	ObidosRowBottom2px expiresInRow;
	
	@UiField
	TextBox expiresInDaysTextBox;
	
	
	@UiField
	TextBox expiresTextBox;
	
	@UiField
	TextBox maxUsersTextBox;
	
	@UiField
	ObidosMessageRow messageRow;

	@UiField
	Paragraph licenseTermsPara;
	
	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	FormLabel expireLabel;

	@UiField
	ObidosTextBox customerIdTextBox;

	@UiField
	HTML featuresHTML;

	@UiField
	HTML yourLicenseHtml;
	
	interface Binder extends UiBinder<Widget, LicenseInfoView>
	{
	}

	@Inject
	LicenseInfoView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		Button helpButton = panelHeader.getHelpButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().help();
				}
			});
		}
	}
	
	public Paragraph getLicenseTermsPara()
	{
		return licenseTermsPara;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public TextBox getCompanyTextBox()
	{
		return companyTextBox;
	}

	public TextBox getEmailTextBox()
	{
		return emailTextBox;
	}

	public TextBox getExpiresTextBox()
	{
		return expiresTextBox;
	}

	public TextBox getMaxUsersTextBox()
	{
		return maxUsersTextBox;
	}

	public ObidosRowBottom2px getExpiresInRow()
	{
		return expiresInRow;
	}

	public TextBox getExpiresInDaysTextBox()
	{
		return expiresInDaysTextBox;
	}

	public FormLabel getExpireLabel()
	{
		return expireLabel;
	}

	public TextBox getSignedOnTextBox()
	{
		return signedOnTextBox;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosTextBox getCustomerIdTextBox()
	{
		return customerIdTextBox;
	}

	public HTML getFeaturesHTML()
	{
		return featuresHTML;
	}

	public HTML getYourLicenseHtml()
	{
		return yourLicenseHtml;
	}
}
