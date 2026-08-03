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

package com.spenego.Obidos.client.application.newuser;

import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.core.shared.GWT;
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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LimitedLdapDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.dto.UserDTO;

public class NewUserPresenter extends Presenter<NewUserPresenter.MyView, NewUserPresenter.MyProxy>
		implements NewUserUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	// Key is 'Country Name (+code)', value is CountryCode
	private static Map<String, CountryCodeDTO> sCountryCodesMap = null;

	interface MyView extends View, HasUiHandlers<NewUserUiHandlers>
	{
        public BlockQuote getHelpBlockQuote();
        public Paragraph getHelpParagraph();

		public TextBox getUserNameTextBox();
		public TextBox getFullNameTextBox();
		public ListBox getAuthSourceListBox();
		public FormLabel getPasswordLabel();
		public Input getPasswordBox();
		public TextBox getPrimaryEmailTextBox();
		public TextBox getPrimaryPhoneBox();
		public FormLabel getChangePasswordLabel();
		public CheckBox getSendEmailCheckBox();
		public TextArea getEmailCommentTextArea();
		public ToggleSwitch getGlobalTemplateToggleSwitch();
		public ToggleSwitch getTwoFAPasswordResetSwitch();
		public ObidosMessageRow getMessageRow();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public Button getCreateUserButton();
		public ObidosIntegerTextBox getPasswordExpiresTextBox();
		public ObidosRowBottom2px getPasswordRow();
		public ObidosRowBottom2px getExpireRow();
		public Row getTwofarow();
		public FormLabel getTwoFAPasswordResetLabel();
		public ObidosPanelHeader getPanelHeader();
		public Select getCountryCodeSelect();
		public ObidosTextBox getMobilePhoneTextBox();

	}
	

	@NameToken(NameTokens.NEW_USER)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<NewUserPresenter>
	{
	}

	final PlaceManager placeManager;
	final CurrentUser currentUser;
	
	@Inject
	NewUserPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		
		this.placeManager = placeManager;
		this.currentUser = currentUser;
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
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		resetForm();
		showMessage(null);
		focusToUsernameField();
		getView().getPanelHeader().getBackButton().setText(glang.users());
		getView().getButtonToolBarBottom().adjustButtonsWidth();
		resetToggleSwitches();
		updateLdapAuthSourceList();
		updatePasswordPolicy();
		authSourceChanged();
		fetchAndUpdateCountryCodes();
	}
	
	private void focusToUsernameField()
	{
		getView().getUserNameTextBox().setFocus(true);
	}
	
	private void updatePasswordPolicy()
	{
		GwtAsyncWrapper<SystemConfigDTO> callback = new GwtAsyncWrapper<SystemConfigDTO>(this)
		{

			@Override
			public void uponSuccess(SystemConfigDTO cdto)
			{
				PassComplexityDTO pcdto = cdto.getPassComplexityDTO();
				int passwordExpireDays = pcdto.getPasswordComplexityRequirements().getMaxAgeInDays();
				getView().getPasswordExpiresTextBox().setValue(passwordExpireDays);
				/*
				if (passwordExpireDays == 0)
				{
					getView().getPasswordExpiresTextBox().setValue(glang.never());
				}
				else
				{
					getView().getPasswordExpiresTextBox().setValue(passwordExpireDays + " " + glang.days());
				}
				*/
			}

			@Override
			public void uponFailure(Throwable caught)
			{
//				showErrorMessage(glang.couldNotRetrieveFormValues(caught.getMessage()));
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().get(authCreds, callback);
	}

	private void resetToggleSwitches()
	{
		getView().getTwoFAPasswordResetSwitch().setValue(false);
		getView().getGlobalTemplateToggleSwitch().setValue(false);
	}

	private void resetForm()
	{
	    getView().getUserNameTextBox().setValue("");
	    getView().getFullNameTextBox().setValue("");
	    getView().getPasswordBox().setValue("");
	    getView().getPrimaryEmailTextBox().setValue("");
	    getView().getPrimaryPhoneBox().setValue("");
	    getView().getSendEmailCheckBox().setValue(true);
	    getView().getEmailCommentTextArea().setValue("");
	    getView().getPasswordExpiresTextBox().setValue(0);
	}

	/**
	 * Update the AD/LDAP authentication list
	 * <p>
	 * @author spgdev@spenego.com - Apr 20, 2017
	 */
	private void updateLdapAuthSourceList()
	{
	    ListBox ldapListBox = getView().getAuthSourceListBox();
	    ldapListBox.clear();
	    ldapListBox.addItem(ObidosConstants.AUTH_SOURCE_LOCAL);
        GwtAsyncWrapper<LdapConfigurationResult> callback = new GwtAsyncWrapper<LdapConfigurationResult>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Error:" + e.getMessage());
            }

            @Override
            public void uponSuccess(LdapConfigurationResult result)
            {
                int total = result.getTotal();
                List<LimitedLdapDTO> settings = result.getElements();
                if (settings != null)
                {
                	for (LimitedLdapDTO limitedLdapDTO: settings)
                	{
                    ldapListBox.addItem(limitedLdapDTO.getName());
                	}
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        AdminConfigService.Utility.getInstance().getAllLdapSettings(authCreds, null, null, null, callback);

	}
	
	private void fetchAndUpdateCountryCodes()
	{
		final Select countryCodeSelect = getView().getCountryCodeSelect();
		countryCodeSelect.setValue(null);
		getView().getMobilePhoneTextBox().setPlaceholder(glang.mobilePhoneWithoutCountryCode());
		getView().getMobilePhoneTextBox().clear();

        GwtAsyncWrapper<Map<String, CountryCodeDTO>> callback = new GwtAsyncWrapper<Map<String, CountryCodeDTO>>(this)
		{

			@Override
			public void uponSuccess(Map<String, CountryCodeDTO> countryCodesMap)
			{
				sCountryCodesMap = countryCodesMap;
				ClientUtils.updateCountyCodeSelectListWithMap(countryCodeSelect,countryCodesMap);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch list of Country Codes for SMS");
				countryCodeSelect.clear();
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().getCountryCodesMap(authCreds,callback);
	}

	private boolean setMobilePhoneToUserDTO(final UserDTO userDTO)
	{
		String mobilePhone = getView().getMobilePhoneTextBox().getValue();
		if (mobilePhone == null || mobilePhone.length() == 0)
		{
			// delete
			userDTO.setMobile1(null);
			return true;
		}
		
		Option option = getView().getCountryCodeSelect().getSelectedItem();
		gwtLog("MMM selected item: " + option);
		if (mobilePhone.length() > 0 && option == null)
		{
			showErrorMessage("Please select a Country Code for Phone Number of SMS");
			return false;
		}
		if (option != null)
		{
			// construct the number with countrycode+number
			String countryCode = getView().getCountryCodeSelect().getSelectedItem().getValue();
			String dialCode = ClientUtils.extractDialCode(countryCode);
			gwtLog("MMM country code: " + countryCode);
			gwtLog("MMM dial code: " + dialCode);
			// dial code is already has +
			mobilePhone = dialCode + " " + mobilePhone;
			userDTO.setMobile1(mobilePhone);
		}
		gwtLog("MMM mobile: " + userDTO.getMobile1());
		gwtLog("MMM phone: " + userDTO.getPhone());
		return true;
	}

	@Override
	public void createUser()
	{
	    // validation will be done in server side, we just to simple stuff here
		String userName = getView().getUserNameTextBox().getValue();
		if (userName == null || userName.length() == 0)
		{
			showErrorMessage(ObidosMessages.LANG.specifyUsername());
			return;
		}
		String fullName = getView().getFullNameTextBox().getValue();
		if (fullName == null || fullName.length() == 0)
		{
			showErrorMessage(ObidosMessages.LANG.specifyFullname());
			return;
		}


		// it's the initial login password, we won't check strength
		String password = getView().getPasswordBox().getValue();

		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(userName);
		userDTO.setFullname(fullName);


		ListBox listBox = getView().getAuthSourceListBox();
		String authSource = listBox.getSelectedValue();
		if (authSource.equals(ObidosConstants.AUTH_SOURCE_LOCAL))
		{
		    if (password == null || password.length() == 0)
			{
				showErrorMessage(ObidosMessages.LANG.specifyPassword());
				return;
			}
		    long passwordExpireDays =  ClientUtils.fromInteger(getView().getPasswordExpiresTextBox().getValue());
		    gwtLog("Set max password expiration days: " + passwordExpireDays);
		    userDTO.setMaximumPasswordAge(passwordExpireDays);

			userDTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
			userDTO.setPassword(password);
		}
		else
		{
		    // must be one of the LDAP configurations. We can have more than one LDAP configuration name
			userDTO.setAuthSource(authSource);
		}

		gwtLog("Authsource); " + authSource);

		String primaryEmail = getView().getPrimaryEmailTextBox().getValue();
		if (primaryEmail == null || primaryEmail.length() == 0)
		{
		    showErrorMessage(ObidosMessages.LANG.specifyUsersPrimaryEmailAddress());
		    return;
		}
		userDTO.setEmail1(primaryEmail);

		Boolean notifyUser = getView().getSendEmailCheckBox().getValue();
		String emailComment = getView().getEmailCommentTextArea().getValue();
		String primaryPhone = getView().getPrimaryPhoneBox().getValue();
		if (primaryPhone != null && primaryPhone.length() > 0)
		{
		    userDTO.setPhone(primaryPhone);
		}


		Long userId = null;
		// this constructor initializes all caps to false, so we will send a null userid to use it
		CapabilityDTO capabilityDTO = new CapabilityDTO(userId);

		boolean twoFAPasswordResetAvailable = getView().getTwoFAPasswordResetSwitch().getValue();
		gwtLog("2FA password reset available? " + twoFAPasswordResetAvailable);
		userDTO.setTwoFARequired(twoFAPasswordResetAvailable);

		boolean canCreateGlobalTemplates = getView().getGlobalTemplateToggleSwitch().getValue();
		capabilityDTO.setCreateGlobalTemplate(canCreateGlobalTemplates);
		
		// for local auth only
		if (isAuthSourceLocal())
		{
			long maximumPasswordAge = ClientUtils.fromInteger(getView().getPasswordExpiresTextBox().getValue());
			gwtLog("Maximum password age: " + maximumPasswordAge);
			userDTO.setMaximumPasswordAge(maximumPasswordAge);
		}
		
		GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
		{

			@Override
			public void uponFailure(Throwable e)
			{
				showErrorMessage(glang.couldNotCreateUser(e.getMessage()));
			}

			@Override
			public void uponSuccess(Long id)
			{
			    GWT.log("User " + userName + " created successfully");
				String message = "User '" + userName + "' created successfully. ";
				if (notifyUser)
				{
				    showMessage(message + " An email will be sent to the user.");
//				    String emailComment = getView().getEmailCommentTextArea().getValue();
//				    sendEmailToUser(emailComment, id);
				}
				else
				{
				    showMessage(message + " No email will be sent to the user.");
				}
				resetForm();
			}
		};

		if ( ! setMobilePhoneToUserDTO(userDTO))
		{
			// mobile number specified but something is wrong
			return;
		}

		userDTO.setHideItemDelay(300);

		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().createUser(authCreds, userDTO, capabilityDTO, notifyUser, emailComment, callback);
	}

	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    @Override
    public void showHideHelp()
    {
    	ClientUtils.showHelp(getView().getHelpBlockQuote());
    }

    @Override
    public void sendEmailCheckBoxClickHandler()
    {
        CheckBox checkBox = getView().getSendEmailCheckBox();
        TextArea textArea = getView().getEmailCommentTextArea();
        boolean v = checkBox.getValue();
        GWT.log("Value: " + v);
        if (v)
        {
           textArea.setEnabled(true);
        }
        else
        {
           textArea.setEnabled(false);
        }
    }
    
    private boolean isAuthSourceLocal()
    {
		ListBox lb = getView().getAuthSourceListBox();
		String authSource = lb.getSelectedValue();
		if (authSource.equals(ObidosConstants.AUTH_SOURCE_LOCAL))
		{
			return true;
		}
		return false;
    	
    }
    
    private void showLocalPasswordFormFields(boolean visible)
    {
		getView().getPasswordRow().setVisible(visible);
		getView().getExpireRow().setVisible(visible);
    }

	@Override
	public void authSourceChanged()
	{
		if (isAuthSourceLocal())
		{
			getView().getTwoFAPasswordResetLabel().setText(glang.twoFactorForPasswordReset());
			showLocalPasswordFormFields(true);
		}
		else
		{
			showLocalPasswordFormFields(false);
			getView().getTwoFAPasswordResetLabel().setText(glang.twoFactorForPassphraseReset());
		}

	}

	@Override
	public void listUsers()
	{
		ClientUtils.showPage(placeManager, NameTokens.LIST_USERS);
	}

	@Override
	public void countryCodeSelectCallback()
	{
		ClientUtils.countryCodeSelectCallback(getView().getMobilePhoneTextBox(),
				getView().getCountryCodeSelect(),
				sCountryCodesMap);
	}

	@Override
	public void clearMessage()
	{
		gwtLog("MMM clear message");
		showMessage(null);
	}
}
