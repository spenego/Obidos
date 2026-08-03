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

package com.spenego.Obidos.client.application.smsconfig;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.E164PhoneTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.util.ClientUtils;

class SMSConfigView extends ViewWithUiHandlers<SMSConfigUiHandlers> implements SMSConfigPresenter.MyView
{
	interface Binder extends UiBinder<Widget, SMSConfigView>
	{
	}

    @UiField
    ObidosPanelHeader panelHeader;

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	ListBox smsProviderListBox;

	@UiField
	Row twilioRow;

	@UiField
	ObidosTextBox sidTextBox;

	@UiField
	ObidosPasswordBox twilioPhoneNumberTextBox;

	@UiField
	Button showTwilioPhoneNumberButton;
	
	@UiField
	ObidosMessageRow messageRowTwilio;

	@UiField
	Button saveButtonTwilio;
	
	@UiField
	Button resetButtonTwilio;
	
	
	@UiField
	Row vonageRow;
	
	@UiField
	ObidosTextBox apiSecretTextBox;

	@UiField
	ObidosTextBox apiKeyTextBox;
	
	@UiField
	ObidosTextBox vonagePhoneNumberTextBox;

	@UiField
	ObidosMessageRow messageRowVonage;

	@UiField
	Button saveButtonVonage;
	
	@UiField
	Button resetButtonVonage;

	@UiField
	Button testSendSmsButton;

	@UiField
	ObidosButtonToolBar buttonToolBarBottom;

	@UiField
	ObidosButtonToolBar buttonToolBarBottomVonage;
	
	@UiField
	ObidosButtonToolBar testSmsBar;

	@UiField
	E164PhoneTextBox toPhoneNumberTextBox;

	@UiField
	ObidosTextBox smsTextBox;

	@UiField
	Button deleteButtonTwilio;

	@UiField
	Button deleteButtonVonage;

	@UiField
	ObidosPasswordBox authTokenSecretBox;

	@UiField
	Button showAuthTokenButton;

	@UiField
	Select countryCodeSelect;

	@UiHandler("countryCodeSelect")
	void onSelectcountryCodeSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().showCountryCodeSlectionChange();
	}

	@Inject
	SMSConfigView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
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

		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().back();
				}
			});
		}

	}
	
	@UiHandler("smsProviderListBox")
	void onSelectSmsProvider(ChangeEvent e)
	{
		getUiHandlers().smsProviderSelectionCalback();
	}
	@UiHandler("saveButtonTwilio")
	void onClickSaveButtonTwilio(ClickEvent e)
	{
		getUiHandlers().saveTwilioSmsSettings();
	}

	@UiHandler("saveButtonVonage")
	void onClickSaveButtonVonage(ClickEvent e)
	{
		getUiHandlers().saveVonageSmsSettings();
	}

	@UiHandler("resetButtonVonage")
	void onClickResetButtonVonage(ClickEvent e)
	{
		getUiHandlers().resetSmsSettings();
	}

	@UiHandler("resetButtonTwilio")
	void onClickResetButtonTwilio(ClickEvent e)
	{
		getUiHandlers().resetSmsSettings();
	}

	@UiHandler("deleteButtonTwilio")
	void onClickdeleteButtonTwilio(ClickEvent e)
	{
		getUiHandlers().deleteSmsSettings();
	}

	@UiHandler("deleteButtonVonage")
	void onClickdeleteButtonVonage(ClickEvent e)
	{
		getUiHandlers().deleteSmsSettings();
	}
	
	@UiHandler("testSendSmsButton")
	void onClicktestSendSmsButton(ClickEvent e)
	{
		getUiHandlers().sendTestSms();
	}

	@UiHandler("showAuthTokenButton")
	void onClickshowAuthTokenButton(ClickEvent e)
	{
	    ClientUtils.toggleEyeIcon(showAuthTokenButton, authTokenSecretBox);
	}
	
	@UiHandler("showTwilioPhoneNumberButton")
	void onClickshowTwilioPhoneNumberButton(ClickEvent e)
	{
	    ClientUtils.toggleEyeIcon(showTwilioPhoneNumberButton, twilioPhoneNumberTextBox);
	}
	
	public ListBox getSmsProviderListBox()
	{
		return smsProviderListBox;
	}

	public Row getTwilioRow()
	{
		return twilioRow;
	}

	public Row getVonageRow()
	{
		return vonageRow;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosTextBox getSidTextBox()
	{
		return sidTextBox;
	}

	public ObidosPasswordBox getTwilioPhoneNumberTextBox()
	{
		return twilioPhoneNumberTextBox;
	}

	public ObidosMessageRow getMessageRowTwilio()
	{
		return messageRowTwilio;
	}

	public Button getSaveButtonTwilio()
	{
		return saveButtonTwilio;
	}

	public Button getResetButtonTwilio()
	{
		return resetButtonTwilio;
	}

	public ObidosTextBox getApiSecretTextBox()
	{
		return apiSecretTextBox;
	}

	public ObidosTextBox getApiKeyTextBox()
	{
		return apiKeyTextBox;
	}

	public ObidosMessageRow getMessageRowVonage()
	{
		return messageRowVonage;
	}

	public Button getSaveButtonVonage()
	{
		return saveButtonVonage;
	}

	public Button getResetButtonVonage()
	{
		return resetButtonVonage;
	}

	public Button getTestSendSmsButton()
	{
		return testSendSmsButton;
	}

	public ObidosButtonToolBar getTestSmsBar()
	{
		return testSmsBar;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosTextBox getVonagePhoneNumberTextBox()
	{
		return vonagePhoneNumberTextBox;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Button getShowTwilioPhoneNumberButton()
	{
		return showTwilioPhoneNumberButton;
	}

	public E164PhoneTextBox getToPhoneNumberTextBox()
	{
		return toPhoneNumberTextBox;
	}

	public ObidosTextBox getSmsTextBox()
	{
		return smsTextBox;
	}

	public ObidosButtonToolBar getButtonToolBarBottomVonage()
	{
		return buttonToolBarBottomVonage;
	}

	public Button getDeleteButtonTwilio()
	{
		return deleteButtonTwilio;
	}

	public Button getDeleteButtonVonage()
	{
		return deleteButtonVonage;
	}

	public ObidosPasswordBox getAuthTokenSecretBox()
	{
		return authTokenSecretBox;
	}

	public Button getShowAuthTokenButton()
	{
		return showAuthTokenButton;
	}

	public Select getCountryCodeSelect()
	{
		return countryCodeSelect;
	}
}

		// TODO Auto-generated method stub