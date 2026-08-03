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

import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.E164PhoneTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.SmsKeyDTO;

public class SMSConfigPresenter extends Presenter<SMSConfigPresenter.MyView, SMSConfigPresenter.MyProxy>
		implements SMSConfigUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private SmsKeyDTO sSmsKeyDTO = null;

	interface MyView extends View, HasUiHandlers<SMSConfigUiHandlers>
	{
		public ListBox getSmsProviderListBox();
		public Row getTwilioRow();
		public Row getVonageRow();
		public BlockQuote getHelpBlockQuote();
		public ObidosTextBox getSidTextBox();
		public ObidosPasswordBox getTwilioPhoneNumberTextBox();
		public ObidosMessageRow getMessageRowTwilio();
		public Button getSaveButtonTwilio();
		public Button getResetButtonTwilio();
		public ObidosTextBox getApiSecretTextBox();
		public ObidosTextBox getApiKeyTextBox();
		public ObidosMessageRow getMessageRowVonage();
		public Button getSaveButtonVonage();
		public Button getResetButtonVonage();
		public Button getTestSendSmsButton();
		public ObidosButtonToolBar getTestSmsBar();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public ObidosButtonToolBar getButtonToolBarBottomVonage();
		public ObidosTextBox getVonagePhoneNumberTextBox();
		public ObidosTextBox getSmsTextBox();
		public Button getDeleteButtonTwilio();
		public Button getDeleteButtonVonage();
		public ObidosPasswordBox getAuthTokenSecretBox();
		public Button getShowAuthTokenButton();
		public Select getCountryCodeSelect();
		public E164PhoneTextBox getToPhoneNumberTextBox();
	}

	@NameToken(NameTokens.SMS_CONFIG_PRESENTER)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<SMSConfigPresenter>
	{
	}

	final PlaceManager placeManager;
	@Inject
	SMSConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		this.placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		sSmsKeyDTO = null;
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage(null);
		resetForm();
		getView().getButtonToolBarBottom().adjustButtonsWidth();
		getView().getButtonToolBarBottomVonage().adjustButtonsWidth();
		getView().getTestSmsBar().adjustButtonsWidth();
		setSaveButtonTitle(glang.saveButtonTitle());
		enableButtons(false);
		fetchAndUpdateForm();
		fetchAndUpdateCountryCodes();
	}
	
	private void resetForm()
	{
		getView().getSidTextBox().setValue("");
		getView().getAuthTokenSecretBox().setValue("");
		getView().getTwilioPhoneNumberTextBox().setValue("");

		getView().getApiSecretTextBox().setValue("");
		getView().getApiKeyTextBox().setValue("");
		getView().getVonagePhoneNumberTextBox().setValue("");

		
	}
	
	private void enableButtons(final boolean enable)
	{
		getView().getResetButtonTwilio().setEnabled(enable);
		getView().getResetButtonVonage().setEnabled(enable);
		
		getView().getDeleteButtonTwilio().setEnabled(enable);
		getView().getDeleteButtonVonage().setEnabled(enable);
		
		getView().getToPhoneNumberTextBox().setEnabled(enable);
		getView().getSmsTextBox().setEnabled(enable);
		getView().getTestSendSmsButton().setEnabled(enable);
	}
	
	private void setSaveButtonTitle(final String title)
	{
		getView().getSaveButtonTwilio().setText(title);
		getView().getSaveButtonVonage().setText(title);
	}
	
	private int smsProviderType()
	{
		ListBox lb = getView().getSmsProviderListBox();
		String provider = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		Row twilioRow = getView().getTwilioRow();
		Row vonageRow = getView().getVonageRow();
		switch(idx)
		{
			case 0: // Twilio
			{
				return ObidosConstants.SMS_PROVIDER_TWILIO;
			}
			case 1: // Vonage
			{
				return ObidosConstants.SMS_PROVIDER_VONAGE;
			}
		}
		return (-1);
	}

	@Override
	public void smsProviderSelectionCalback()
	{
		ListBox lb = getView().getSmsProviderListBox();
		String provider = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		Row twilioRow = getView().getTwilioRow();
		Row vonageRow = getView().getVonageRow();
		switch(idx)
		{
			case 0: // Twilio
			{
				vonageRow.setVisible(false);
				twilioRow.setVisible(true);
				break;
			}
			case 1: // Vonage
			{
				twilioRow.setVisible(false);
				vonageRow.setVisible(true);
				break;
			}
		}
	}

	@Override
	public void help()
	{
		boolean visible = getView().getHelpBlockQuote().isVisible();
		getView().getHelpBlockQuote().setVisible(!visible);
	}

	private void showMessage(final String message)
	{
		int provider = smsProviderType();
		switch(provider)
		{
			case ObidosConstants.SMS_PROVIDER_TWILIO:
			{
				getView().getMessageRowTwilio().showMessage(message);
				break;
			}
			case ObidosConstants.SMS_PROVIDER_VONAGE:
			{
				getView().getMessageRowVonage().showMessage(message);
				break;
			}

		}
	}

	private void showErrorMessage(final String emsg)
	{
		int provider = smsProviderType();
		switch(provider)
		{
			case ObidosConstants.SMS_PROVIDER_TWILIO:
			{
				getView().getMessageRowTwilio().showErrorMessage(emsg);
				break;
			}
			case ObidosConstants.SMS_PROVIDER_VONAGE:
			{
				getView().getMessageRowVonage().showErrorMessage(emsg);
				break;
			}
		}
	}
	
	private void populateFormTwilio(final SmsKeyDTO dto)
	{
		getView().getSidTextBox().setValue(dto.getApiSecret());
		getView().getAuthTokenSecretBox().setValue(dto.getApiKey());
		getView().getTwilioPhoneNumberTextBox().setValue(dto.getSmsProviderSpecifiedPhoneNumber());
		
	}
	private void populateFormVonage(final SmsKeyDTO dto)
	{
		getView().getApiSecretTextBox().setValue(dto.getApiSecret());
		getView().getApiKeyTextBox().setValue(dto.getApiKey());
		getView().getVonagePhoneNumberTextBox().setValue(dto.getSmsProviderSpecifiedPhoneNumber());
	}
	
	private void populateForm(SmsKeyDTO dto)
	{
		gwtLog("Id: "+ dto.getId());
		gwtLog("SMS Provider: " + dto.getSmsProvider());
		setSaveButtonTitle(glang.updateButtonTitle());
		enableButtons(true);
		int providerType = dto.getSmsProvider();
		switch(providerType)
		{
			case ObidosConstants.SMS_PROVIDER_TWILIO:
			{
				populateFormTwilio(dto);
				break;
			}
			case ObidosConstants.SMS_PROVIDER_VONAGE:
			{
				populateFormVonage(dto); // TODO
				break;
			}
			default:
			{
				enableButtons(false);
				break;
			}
		}
	}
	
	private void fetchAndUpdateForm()
	{
        GwtAsyncWrapper<SmsKeyDTO> callback = new GwtAsyncWrapper<SmsKeyDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String emsg = caught.getMessage();
                String cn = caught.getClass().getSimpleName();
                // showErrorMessage("Could not get SMS Key configuration to update: " + emsg + ":" + cn);
            }

            @Override
            public void uponSuccess(SmsKeyDTO dto)
            {
            	gwtLog("fetchAndUpdateForm: provider: " + dto.getSmsProvider());
            	sSmsKeyDTO = dto; // save
                populateForm(dto);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        SystemConfigService.Utility.getInstance().getSmsConfig(authCreds, callback);
	}
	
	private void gwtLog(final String msg)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), msg);
	}
	
	@Override
	public void sendTestSms()
	{
		if (sSmsKeyDTO == null)
		{
			showErrorMessage("SMS does not seem to have configured");
			return;
		}
		int providerType = sSmsKeyDTO.getSmsProvider();
		String smsProvider = null;
		switch(providerType)
		{
			case ObidosConstants.SMS_PROVIDER_TWILIO:
			{
				smsProvider = glang.twilio();
				break;
			}
			case ObidosConstants.SMS_PROVIDER_VONAGE:
			{
				smsProvider = glang.vonage();
				break;
			}
			default:
			{
				showErrorMessage("Unknown SMS Provider");
				return;
			}
		}


		gwtLog("Send TEST SMS");
		/*
		String accountSid = getView().getSidTextBox().getValue();
		if (accountSid == null || accountSid.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioAccountSid());
			return;
		}
		String authToken = getView().getAuthTokenTextBox().getValue();
		if (authToken == null || authToken.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioAuthToken());
			return;
		}
		String twilioPhoneNumber = getView().getTwilioPhoneNumberTextBox().getValue();
		if (twilioPhoneNumber == null || twilioPhoneNumber.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioPhoneNumber());
			return;
		}
		*/
		String recipientNumber = getView().getToPhoneNumberTextBox().getValue();
		if (recipientNumber == null || recipientNumber.length() == 0 || "+".equals(recipientNumber))
		{
			showErrorMessage("Please specify recipient's SMS enabled phone number");
			return;
		}
		String smsMessage = getView().getSmsTextBox().getValue();
		if (smsMessage == null || smsMessage.length() == 0)
		{
			showErrorMessage("Please specify a message for SMS");
			return;
		}

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{
			@Override
			public void uponSuccess(Void result)
			{
				showMessage("Test SMS Message sent successfully. Please look at SMS provider's console for more info.");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not send test SMS message: " + caught.getMessage());
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        SystemConfigService.Utility.getInstance().sendTestSMS(authCreds, recipientNumber, smsMessage, callback);
	}
	
	private void saveSmsSettings(final SmsKeyDTO dto)
	{
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String emsg = caught.getMessage();
                showErrorMessage("Could not Save SMS settings: " + emsg);
            }

            @Override
            public void uponSuccess(Void result)
            {
            	showMessage("SMS setting saved successfully");
                populateForm(dto);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        SystemConfigService.Utility.getInstance().updateSMSConfig(authCreds, dto, callback);
	}

	@Override
	public void saveTwilioSmsSettings()
	{
		String accountSid = getView().getSidTextBox().getValue();
		if (accountSid == null || accountSid.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioAccountSid());
			return;
		}
		String authToken = getView().getAuthTokenSecretBox().getValue();
		if (authToken == null || authToken.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioAuthToken());
			return;
		}
		String twilioPhoneNumber = getView().getTwilioPhoneNumberTextBox().getValue();
		if (twilioPhoneNumber == null || twilioPhoneNumber.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioPhoneNumber());
			return;
		}
		SmsKeyDTO dto = new SmsKeyDTO(ObidosConstants.SMS_PROVIDER_TWILIO,
				twilioPhoneNumber,
				accountSid,
				authToken);
		
		saveSmsSettings(dto);
	}

	@Override
	public void saveVonageSmsSettings()
	{
		String apiSecret = getView().getSidTextBox().getValue();
		if (apiSecret == null || apiSecret.length() == 0)
		{
			showErrorMessage("Please specify " + glang.vonageApiSecret());
			return;
		}
		String apiKey = getView().getApiKeyTextBox().getValue();
		if (apiKey == null || apiKey.length() == 0)
		{
			showErrorMessage("Please specify " + glang.vonageApiKey());
			return;
		}
		String vonagePhoneNumber = getView().getVonagePhoneNumberTextBox().getValue();
		if (vonagePhoneNumber == null || vonagePhoneNumber.length() == 0)
		{
			showErrorMessage("Please specify " + glang.twilioPhoneNumber());
			return;
		}
		SmsKeyDTO dto = new SmsKeyDTO(ObidosConstants.SMS_PROVIDER_VONAGE,
				vonagePhoneNumber,
				apiSecret,
				apiKey);
		
		saveSmsSettings(dto);

		
	}

	@Override
	public void resetSmsSettings()
	{
		fetchAndUpdateForm();
	}
	
	private void deleteSmsSettingsReal()
	{
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				resetForm();
				enableButtons(false);
				showMessage("SMS Settings deleted successfully!");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Coult not delete SMS Settings: " + caught.getMessage());
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        SystemConfigService.Utility.getInstance().deleteSmsConfig(authCreds, callback);
	}

	private void promptDeleteSmsSettings()
	{
		String title = glang.deleteSmsSettings();
		String message = glang.deleteSmsSettingsWaringMessage();
        ClientUtils.promptForAction(() -> deleteSmsSettingsReal(), title, message);
	}

	@Override
	public void deleteSmsSettings()
	{
		gwtLog("Delete SMS settings");
		promptDeleteSmsSettings();
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
		
	}

	@Override
	public void showCountryCodeSlectionChange()
	{
		Select select = getView().getCountryCodeSelect();
		String line = select.getSelectedItem().getText();
		String dialCode = ClientUtils.extractDialCode(line);
		E164PhoneTextBox tbox = getView().getToPhoneNumberTextBox();
		tbox.setValue(dialCode);
		tbox.setFocus(true);
		
	}

	private void fetchAndUpdateCountryCodes()
	{
		final Select countryCodeSelect = getView().getCountryCodeSelect();

        GwtAsyncWrapper<List<String>> callback = new GwtAsyncWrapper<List<String>>(this)
		{

			@Override
			public void uponSuccess(List<String> countryCodes)
			{
				ClientUtils.updateCountryCodeSelectList(countryCodeSelect, countryCodes);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch list of Country Codes for SMS");
				countryCodeSelect.clear();
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().getCountryCodes(authCreds,callback);
	}

}