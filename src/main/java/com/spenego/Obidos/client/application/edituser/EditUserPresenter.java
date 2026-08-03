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

package com.spenego.Obidos.client.application.edituser;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.FileUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.events.EditUserEvent;
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
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
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.UserComboResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class EditUserPresenter extends Presenter<EditUserPresenter.MyView, EditUserPresenter.MyProxy>
        implements EditUserUiHandlers,
        EditUserEvent.EditUserEventHandler
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private HashMap<FieldNumber, Boolean> gFieldValueChangedMap = new HashMap<>();
    private UserComboResult gUserComboResult = null;
	// Key is 'Country Name (+code)', value is CountryCode
	private static Map<String, CountryCodeDTO> countryCodeMap = null;
    private enum FieldNumber
    {
        USERNAME,
        FULLNAME,
        PRIMARY_EMAIL,
        PRIMARY_PHONE,
		MOBILE_PHONE,
        AUTH_SOURCE,
        LOCKED_USER,
        RESET_USED_PASSWORD,
        REQUIRES_2FA_PASSWORD_RESET,
        IS_2FA_ENABLED,
        RESET_2FA,
        CAN_CREATE_GLOBAL_TEMPLATE,
        DELETE_PROFILE_PIC,
        PASSWORD_AGE,
    };

    private static EnumMap<FieldNumber, Boolean> eMap = new EnumMap<>(FieldNumber.class);
    private void resetEmap()
    {
        eMap.put(FieldNumber.USERNAME, false);
        eMap.put(FieldNumber.FULLNAME, false);
        eMap.put(FieldNumber.PRIMARY_EMAIL, false);
        eMap.put(FieldNumber.PRIMARY_PHONE, false);
        eMap.put(FieldNumber.AUTH_SOURCE, false);
        eMap.put(FieldNumber.LOCKED_USER, false);
        eMap.put(FieldNumber.RESET_USED_PASSWORD, false);
        eMap.put(FieldNumber.REQUIRES_2FA_PASSWORD_RESET, false);
        eMap.put(FieldNumber.IS_2FA_ENABLED,false);
        eMap.put(FieldNumber.RESET_2FA, false);
        eMap.put(FieldNumber.CAN_CREATE_GLOBAL_TEMPLATE, false);
		eMap.put(FieldNumber.DELETE_PROFILE_PIC,false);
		eMap.put(FieldNumber.PASSWORD_AGE, false);
    }


    interface MyView extends View, HasUiHandlers<EditUserUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public TextBox getUsernameTextBox();
        public TextBox getFullnameTextBox();
        public TextBox getPrimaryemailTextBox();
        public TextBox getPrimaryphoneTextBox();
        public ListBox getAuthSourceListBox();
        public Button getResetButton();
        public Button getSaveButton();
        public Button getListButton();
        public FormLabel getUsernameLabel();
        public FormLabel getFullnameLabel();
        public FormLabel getPrimaryEmailLabel();
        public FormLabel getPrimaryPhoneLabel();
        public FormLabel getAuthSourceLabel();
        public FlowPanel getAuthSourceFlowPanel();
        public ObidosPasswordBox getResetPasswordBox();
        public Button getShowHidePasswordButton();
        public FormLabel getResetPasswordLabel();

        public FormLabel getCanCreateGlobalTemplateLabel();
        public ToggleSwitch getGlobalTemplateToggleSwitch();
      	public FormLabel getRequiresTwoFAPasswordResetLabel();
      	public ToggleSwitch getRequiresTwoFAPasswordResetSwitch();
      	public FormLabel getTwoFAEnabledLabel();
      	public ToggleSwitch getIsTwoFAEnabledSwitch();

      	public Row getSettingsRow();
      	public FlowPanel getReset2FAFlowPanel();
      	public FormLabel getReset2FALabel();
      	public CheckBox getReset2FACheckBox();
      	public ObidosButtonToolBar getButtonToolBarBottom();
      	public ObidosMessageRow getMessageRow();
      	public ObidosPanelHeader getPanelHeader();
      	public Image getProfilePreviewImage();
      	public Button getUploadButton();
      	public Image getInvisibleImage();

		public FlowPanel getDeleteProfileFlowPanel();
		public FormLabel getDeleteProfilePicLabel();
		public ToggleSwitch getDeleteProfilePicSwitch();

		public FormLabel getPasswordExpiresLabel();
		public ObidosIntegerTextBox getPasswordAgeTextBox();
		public ObidosReadonlyTextBox getPasswordExpiresTextBox();

		public ObidosRowBottom2px getChangePasswordRow();
		public Row getPasswordExpiresRow();
		public Row getTwofaRequiredRow();
		public Row getTwofaEnabledRow();
		public FormLabel getPasswordAgeLabel();

		public FormLabel getMobilePhoneLabel();
		public Select getCountryCodeSelect();
		public ObidosTextBox getMobilePhoneTextBox();
    }

    @NameToken(NameTokens.EDIT_USER)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<EditUserPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    EditUserPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        gwtLog("EditUserPresenter onBind()");
    }

    protected void onReveal()
    {
        super.onReveal();
        gwtLog("EditUserPresenter onReveal()");
    }

    protected void onHide()
    {
        super.onHide();
    }

    protected void onUnbind()
    {
        super.onUnbind();
        gwtLog("EditUserPresenter onUnbind()");
        resetFormLabelColors();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage(null);
        resetFieldsMaps();
        getView().getButtonToolBarBottom().adjustButtonsWidth();
        gUserComboResult = null;
        showSettings(false);
        resetFormLabelColors();
        getView().getReset2FACheckBox().setValue(false);
        disableUpdateButton();
        resetUserComboResult();
        fetchUserAndPopulateUserEditForm(null);
        listButtonTitle(ObidosMessages.LANG.listUsers());
    	ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
    }
    
    private void showSettings(boolean visible)
    {
   		getView().getSettingsRow().setVisible(visible);
    }

    private void showAuthSource()
    {
        getView().getAuthSourceLabel().setVisible(true);
        getView().getAuthSourceFlowPanel().setVisible(true);
    }

    private void disableResetButton()
    {
        getView().getResetButton().setEnabled(false);
    }

    private void disableUpdateButton()
    {
        getView().getSaveButton().setEnabled(false);
        getView().getResetButton().setEnabled(false);
    }
    private void enableUpdateButton()
    {
        getView().getSaveButton().setEnabled(true);
        getView().getResetButton().setEnabled(true);
    }
    private void displayDefaultProfilePic()
    {
		getView().getUploadButton().setText(glang.uploadProfilePic());
   		getView().getDeleteProfilePicLabel().setVisible(false);
   		getView().getDeleteProfileFlowPanel().setVisible(false);
   		ClientUtils.displayProfilePlaceHolderImage(getView().getProfilePreviewImage());
    }

    private void displayProfilePic(final UserDTO dto)
    {
    	byte[] profilePicBytes = dto.getProfilePic();
    	if (profilePicBytes == null)
    	{
    		displayDefaultProfilePic();
    		return;
    	}
    	if (profilePicBytes.length == 0)
    	{
    		displayDefaultProfilePic();
    		return;
    	}

		getView().getDeleteProfilePicLabel().setVisible(true);
		getView().getDeleteProfileFlowPanel().setVisible(true);

		getView().getUploadButton().setText(glang.uploadNewProfilePic());
		String dataURL = new String(profilePicBytes);
		Image profilePicImageWidget = getView().getProfilePreviewImage();
		Image invisibleImageWidget = getView().getInvisibleImage();
		ClientUtils.displayProfilePicture(dataURL, profilePicImageWidget, invisibleImageWidget);
    }

   /**
    * Fetch user by id and populate form
    * <p>
    * @author spgdev@spenego.com - Jun 3, 2017
    */
    private void fetchUserAndPopulateUserEditForm(String message)
    {
        String key = ObidosConstants.USER_ID;
        Long userId = null;
        try
        {
            userId = ClientUtils.getIdFromUrl(placeManager, key);
        } catch (NumberFormatException |ParamNotFoundException e1)
        {
            showErrorMessage("Could not obtain user id from URL: " + e1.getMessage());
            return;
        }

        gwtLog("Fetch user with id: " + userId);
        GwtAsyncWrapper<UserComboResult> callback = new GwtAsyncWrapper<UserComboResult>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                gwtLog("Could not fetch user: " + e.getMessage());

                String message = "Could not obtain user: " + e.getMessage();
                UserDTO userDTO = null;
                populateForm(userDTO); // reset all forms
                showErrorMessage(message);
            }

            @Override
            public void uponSuccess(UserComboResult result)
            {
               gUserComboResult = result;
               populateFormUserCombo(result);
               if (message != null)
               {
                   showMessage(message);
               }
               showAuthSourceChange();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getUserCombo(authCreds, userId, callback);
    }

    private void resetUserComboResult()
    {
        gUserComboResult = null;
        resetEmap();
        disableUpdateButton();
    }

    private void showErrorMessage(String errorMessage)
    {
        getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void updatePanelHeading(String text)
    {
        getView().getPanelHeader().setTitle(text);
    }

    /*
     * 1. Admin will only use local auth source. This is a must.
       2. Admin can change only their own password.
       3. The username for the original 'admin' account can be changed. But, the id (maybe 0 - similar to root in linux)
          must be preserved so that we know it is the basic admin account - primary/principal admin account. No other
         admin usernames can be changed. I call these secondary admins.
       4. Secondary admins can change only own credentials, but not username. Cannot change other admin credentials.
       5. How about one more - Only the primary 'admin' account can create/delete admin accounts.
         The purpose of the secondary admins is for backups (in case primary is out) or shared account management.
         But the primary admin is the backbone. The primary admin account cannot be deleted.
     */

    private void populateFormUserCombo(UserComboResult userCommboResult)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        String type = ClientUtils.getTypeFromUrl(placeManager);

        gwtLog("in populateForm...");
        getView().getSaveButton().setText(lang.updateUser());

        UserDTO userDTO = userCommboResult.getUser();
        gwtLog("fetched username: " + userDTO.getUsername());
        populateForm(userDTO);
        String authSource = userDTO.getAuthSource();
        gwtLog(">> Auth source: " + userDTO.getAuthSource());
        gwtLog(">> Password change required: " + userDTO.getPasswordChangeRequired());
        
        CountryCodeDTO countryCodeDTO = userDTO.getCountryCodeDTO();
        if (countryCodeDTO != null)
        {
			String numberNationalFormat = countryCodeDTO.getNumberNationalFormat();
			gwtLog("MMM national format: "+ numberNationalFormat);
			gwtLog("MMMMMMMMMMMMMMMMMMMMMMMMMMM");
			getView().getMobilePhoneTextBox().setValue(numberNationalFormat);
			gwtLog("MMMMMMMMMMMMMMMMMMMMMMMMMMM: " + getView().getMobilePhoneTextBox().getValue());
			String exampleNumber = countryCodeDTO.getExampleNumber();
			if (exampleNumber != null)
			{
				getView().getMobilePhoneTextBox().setPlaceholder(countryCodeDTO.getExampleNumber());
			}
			else
			{
				gwtLog("MMM CountryCodeDTO is null");
				getView().getMobilePhoneTextBox().setPlaceholder(glang.mobilePhoneWithoutCountryCode());
			}
        }
        else
        {
			getView().getMobilePhoneTextBox().setValue(userDTO.getMobile1());
        }

        if (userDTO.getAdministrator())
        {
            updatePanelHeading(lang.editAdmin());
            showSettings(false);
        }
        else
        {
            updatePanelHeading(lang.editUser());
            showSettings(true);
            listButtonTitle(ObidosMessages.LANG.listUsers());
        }

        LdapConfigurationResult ldapConfigResult = userCommboResult.getLdapConfigs();

        ListBox listBox = getView().getAuthSourceListBox();

        if (userDTO.getAdministrator())
        {
            // set auth source to local and disable it
            listBox.clear();
            listBox.addItem(ObidosConstants.AUTH_SOURCE_LOCAL);
            listBox.setEnabled(false);
            disableUsernameTextField();
            updateButtonTitle(ObidosMessages.LANG.updateAdmin());
            listButtonTitle(ObidosMessages.LANG.listAdministrators());
        }
        else
        {
            showAuthSource();
            enableUsernameTextField();
            updateButtonTitle(ObidosMessages.LANG.updateUser());

            listBox.setEnabled(true);
            listBox.clear();
            gwtLog("++++++++++ adding auth source: " + ObidosConstants.AUTH_SOURCE_LOCAL);
            listBox.addItem(ObidosConstants.AUTH_SOURCE_LOCAL);

            // Issue #606
            if (ldapConfigResult.getElements() != null)
            {
				for (LimitedLdapDTO limitedLdapDTO: ldapConfigResult.getElements())
				{
				   listBox.addItem(limitedLdapDTO.getName());
				}
            }


            int nItems = listBox.getItemCount();
            for (int i=0; i < nItems; i++)
            {
                String item = listBox.getItemText(i);
                if (item.equals(authSource))
                {
                    gwtLog(">> Setting auth source to " + authSource);
                    listBox.setSelectedIndex(i);
                    break;
                }
            }

        }
        enableDisablePasswordResetTextBox();

        // settings
        getView().getRequiresTwoFAPasswordResetSwitch().setValue(userDTO.getTwoFARequired());

        Boolean twoFaAvailable = userDTO.getTwoFARequired();
        Boolean twoFaEnabled = userDTO.getTwoFAPasswordResetEnabled();
        gwtLog("Two FA available?" + twoFaAvailable);
        gwtLog("Two FA enabeld?" + twoFaEnabled);

        // 2FA enabled?
        // Must enable switch in order to change value
        getView().getIsTwoFAEnabledSwitch().setEnabled(true);
        getView().getIsTwoFAEnabledSwitch().setValue(twoFaEnabled);
   		getView().getReset2FALabel().setVisible(true);
       	getView().getReset2FAFlowPanel().setVisible(true);
       	if (!twoFaEnabled)
       	{
       		// hide reset 2fa
       		getView().getReset2FALabel().setVisible(false);
       		getView().getReset2FAFlowPanel().setVisible(false);
       	}
        getView().getIsTwoFAEnabledSwitch().setEnabled(false);


        CapabilityDTO cdto = userDTO.getCapabilities();
        if (cdto == null)
        {
        	showErrorMessage("Could not get Cabilities of the user");
        	return;
        }
        boolean canCreateGlobalTemplate = ClientUtils.fromBoolean(cdto.getCreateGlobalTemplate());
        gwtLog("Can create Global Template: " + canCreateGlobalTemplate);
        getView().getGlobalTemplateToggleSwitch().setValue(cdto.getCreateGlobalTemplate());
        
        getView().getPasswordAgeTextBox().setValue(ClientUtils.fromLong(userDTO.getMaximumPasswordAge()).intValue());
        
        int days = ClientUtils.fromInteger(userDTO.getDaysUntilPasswordExpiration());
        gwtLog("Password expires in : " + days + " days");
        ObidosReadonlyTextBox tb = getView().getPasswordExpiresTextBox();
        FormLabel label = getView().getPasswordExpiresLabel();
        label.setText(glang.passwordExpires());
        String message = "";
        if (days == ObidosConstants.PASSWORD_NEVER_EXPIRES)
        {
        	getView().getPasswordExpiresLabel().setText(glang.passwordExpires2());
        	message = glang.never();
        }
        else if (days > 0)
        {
        	getView().getPasswordExpiresLabel().setText(glang.passwordWillExpireIn());
        	message = days + "";
        }
        else
        {
        	label.setText(glang.passwordExpired());
        	if (days == 0)
        	{
        		message = glang.today();
        	}
        	else
        	{
        		message = glang.passwordExpiredDays(Math.abs(days));
        	}
        }
        tb.setText(message);
        ClientUtils.changePasswordExpirationLabelColor(label, days);
    }

    private void updateButtonTitle(String title)
    {
        getView().getSaveButton().setText(title);
    }

    private void listButtonTitle(String title)
    {
        getView().getListButton().setText(title);
    }

    private void disableUsernameTextField()
    {
        getView().getUsernameTextBox().setEnabled(false);
    }

    private void enableUsernameTextField()
    {
        getView().getUsernameTextBox().setEnabled(true);

    }

    // called after onBind()
    // onBind() -> onEditUserButtonClick() -> onReveal() -> onReset()
    @ProxyEvent
    @Override
    public void onEditUserButtonClick(EditUserEvent event)
    {
        if (event == null)
        {
            return;
        }
        LimitedUserDTO limitedUserDTO = event.getUserDTO();
        if (limitedUserDTO == null)
        {
            return;
        }

        /* test sending an event to Application Presenter */
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(MessageDTO.ERROR);
        messageDTO.setMessage("Hello from EditUserPresenter");
        SendMessageEvent.fire(this,  messageDTO);
    }

    private void populateForm(UserDTO userDTO)
    {
        if (userDTO != null)
        {
        	displayProfilePic(userDTO);
        	
        	gwtLog("MMM mobile phone: " + userDTO.getMobile1());

        	// save
        	countryCodeMap = userDTO.getCountryCodesMap();
        	
        	ClientUtils.updateCountryCodeSelectList(userDTO,
        			getView().getMobilePhoneTextBox(),
        			getView().getCountryCodeSelect());

            getView().getUsernameTextBox().setValue(userDTO.getUsername());
            getView().getFullnameTextBox().setValue(userDTO.getFullname());
            getView().getPrimaryemailTextBox().setValue(userDTO.getEmail1());
            getView().getPrimaryphoneTextBox().setValue(userDTO.getPhone());
			getView().getRequiresTwoFAPasswordResetSwitch().setValue(userDTO.getTwoFARequired());
			CapabilityDTO cdto = userDTO.getCapabilities();
			if (cdto == null)
			{
				showErrorMessage("Could not get Cabilities of the user");
				return;
			}
			boolean g = ClientUtils.fromBoolean(cdto.getCreateGlobalTemplate());
			gwtLog("Can create Global Template: " + g);
			getView().getGlobalTemplateToggleSwitch().setValue(g);
			
			int days = ClientUtils.fromInteger(userDTO.getDaysUntilPasswordExpiration());
			gwtLog("Password expires in: "+ days + " days");
			gwtLog("Password expires in: " + userDTO.getDaysUntilPasswordExpiration());

        }
        else
        {
            getView().getUsernameTextBox().setValue("");
            getView().getFullnameTextBox().setValue("");
            getView().getPrimaryemailTextBox().setValue("");
            getView().getPrimaryphoneTextBox().setValue("");
        }
        getView().getResetPasswordBox().setValue("");
        resetFormLabelColors();
        disableUpdateButton();
        showMessage(null);
    }

    private void resetFormLabelColors()
    {
        setFormLabelColorToOriginal(getView().getUsernameLabel());
        setFormLabelColorToOriginal(getView().getFullnameLabel());
        setFormLabelColorToOriginal(getView().getPrimaryEmailLabel());
        setFormLabelColorToOriginal(getView().getPrimaryPhoneLabel());
        setFormLabelColorToOriginal(getView().getAuthSourceLabel());
        setFormLabelColorToOriginal(getView().getResetPasswordLabel());
        setFormLabelColorToOriginal(getView().getRequiresTwoFAPasswordResetLabel());
        setFormLabelColorToOriginal(getView().getCanCreateGlobalTemplateLabel());
        setFormLabelColorToOriginal(getView().getReset2FALabel());
        setFormLabelColorToOriginal(getView().getPasswordAgeLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMobilePhoneLabel());

        resetToggleSwitchLabelColors();
    }
    private void resetToggleSwitchLabelColors()
    {
        setFormLabelColorToOriginal(getView().getDeleteProfilePicLabel());
    }

    private void dumpUserDTO(UserDTO userDTO)
    {
       gwtLog("Dump userDTO-----");
       gwtLog("username: " + userDTO.getUsername());
       gwtLog("email: " + userDTO.getEmail1());
       gwtLog("Dump userDTO-----");
    }

    @Override
    public void showUsernameChange()
    {
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }
        FormLabel label = getView().getUsernameLabel();
        String origUsername = userDTO.getUsername();
        String value = getView().getUsernameTextBox().getValue();
        stringFormValueChanged(origUsername, value, label, FieldNumber.USERNAME);
    }

    @Override
    public void showFullnameChange()
    {
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }
        FormLabel label = getView().getFullnameLabel();

        String orig = userDTO.getFullname();
        String value = getView().getFullnameTextBox().getValue();
        stringFormValueChanged(orig, value, label, FieldNumber.FULLNAME);
    }

    @Override
    public void showPrimaryEmailChange()
    {
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }
        FormLabel label = getView().getPrimaryEmailLabel();
        String orig = userDTO.getEmail1();
        String value = getView().getPrimaryemailTextBox().getValue();
        stringFormValueChanged(orig, value, label, FieldNumber.PRIMARY_EMAIL);
    }


	@Override
	public void showDeleteProfilePicChagne()
	{
		FormLabel label = getView().getDeleteProfilePicLabel();
		Boolean origVal = false;
		Boolean formVal = getView().getDeleteProfilePicSwitch().getValue();
		booleanFormValueChanged(origVal, formVal, label, FieldNumber.DELETE_PROFILE_PIC);
	}

	@Override
	public void showPasswordAgeChange()
	{
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }

		Long origVall = ClientUtils.fromLong(userDTO.getMaximumPasswordAge()); // better be not null
		Integer origVal = (int) (long) origVall; 
		Integer formVal = getView().getPasswordAgeTextBox().getValue();
		if (formVal == null)
		{
			gwtLog("formVal is null");
			formVal = origVal;
		}
		Long formValLong = formVal * 1L;
		Long origValLong = origVal * 1L;

		FormLabel label = getView().getPasswordAgeLabel();
		gwtLog("orig: " + origVal);
		gwtLog("Form: " + formVal);
		gwtLog("origL: " + origValLong);
		gwtLog("FormL: " + formValLong);
        numericFormValueChanged(origValLong, formValLong, label, FieldNumber.PASSWORD_AGE);
		
	}

    private void booleanFormValueChanged(Boolean origVal, Boolean formVal, FormLabel label, FieldNumber fieldNumber)
    {
    	boolean dirty = false;
    	if (origVal != null && ! origVal.equals(formVal))
    	{
    		dirty = true;
    	}
    	eMap.put(fieldNumber, dirty);
    	setFieldValueChagned(fieldNumber, dirty);
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableUpdateButton();
    }

    private void stringFormValueChanged(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
    {
        boolean dirty = false;
        if (origVal != null && formVal != null && formVal.length() == 0)
        {
            dirty = true;
        }
        if (origVal == null && (formVal != null && formVal.length() > 0))
        {
            dirty = true;
        }

        if (origVal == null)
        {
            gwtLog("origVal is null dirty: " + dirty);
        }

        if (origVal != null && !origVal.equals(formVal))
        {
//            gwtLog("Should not be here. origVal: " + origVal + " formVal: " + formVal);
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }
//        gwtLog("formval: " + formVal);

        eMap.put(fieldNumber,dirty);
        setFieldValueChagned(fieldNumber, dirty);

        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableUpdateButton();
    }

	private void numericFormValueChanged(Long origVal, Long formVal, FormLabel label, FieldNumber fieldNumber)
	{
		showMessage(null);
		boolean dirty = false;
		gwtLog("Orig: "+ origVal + " form: " + formVal);
		if (origVal.intValue() != formVal.intValue())
		{
			gwtLog("? " + origVal + " != " + formVal);
			dirty = true;
		}

		gwtLog("dirty: " + dirty);

		eMap.put(fieldNumber, dirty);
		setFieldValueChagned(fieldNumber, dirty);
		
		if (dirty)
		{
			ClientUtils.setFormLabelsColorChanged(label);
		}
		else
		{
			ClientUtils.setFormLabelsColorOriginal(label);
		}
		enableDisableUpdateButton();
	}

    @Override
    public void showPrimaryPhoneChange()
    {
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }

        FormLabel label = getView().getPrimaryPhoneLabel();
        String orig = userDTO.getPhone();
        String value = getView().getPrimaryphoneTextBox().getValue();

        stringFormValueChanged(orig, value, label, FieldNumber.PRIMARY_PHONE);
    }

    private UserDTO getOriginalUserDTO()
    {
        if (gUserComboResult == null)
        {
            ClientUtils.showErrorMessageInMainView(this, "Can not get UserComboResult in form");
            return null;
        }
        UserDTO userDTO = gUserComboResult.getUser();
        if (userDTO == null)
        {
            ClientUtils.showErrorMessageInMainView(this, "Can not get UserDTO in form");
            return null;
        }
        return userDTO;
    }

    @Override
    public void showAuthSourceChange()
    {
        if (gUserComboResult == null)
        {
            showErrorMessage("Can not change auth source at this time");
            return;
        }
        UserDTO userDTO = gUserComboResult.getUser();
        if (userDTO == null)
        {
            showErrorMessage("Can not change auth source at this time (2)");
            return;
        }

        String authSource = userDTO.getAuthSource();
        ListBox listBox = getView().getAuthSourceListBox();
        String value = listBox.getSelectedItemText();
        FormLabel label = getView().getAuthSourceLabel();
        gwtLog(">>> auth source: " + value);
        FieldNumber fn = FieldNumber.AUTH_SOURCE;
        if (!authSource.equals(value))
        {
            eMap.put(fn, true);
            setFieldValueChagned(fn, true);
            setFormLabelColorToChanged(label);
            setFormLabelColorToChanged(label);
        }
        else
        {
            eMap.put(fn, false);
            setFieldValueChagned(fn, false);
            setFormLabelColorToOriginal(label);
        }
        if (value.equals(ObidosConstants.AUTH_SOURCE_LOCAL))
        {
        	gwtLog("Auth soruce is lcoal...");
            getView().getResetPasswordBox().setEnabled(true);
            getView().getChangePasswordRow().setVisible(true);
            getView().getPasswordExpiresRow().setVisible(true);
            getView().getRequiresTwoFAPasswordResetLabel().setText(glang.twoFactorForPasswordReset());
        }
        else
        {
        	gwtLog("Auth soruce is ldap...");
            getView().getResetPasswordBox().setEnabled(false);
            getView().getChangePasswordRow().setVisible(false);
            getView().getPasswordExpiresRow().setVisible(false);
            getView().getRequiresTwoFAPasswordResetLabel().setText(glang.twoFactorForPassphraseReset());

        }
        enableDisableUpdateButton();
    }

    private void enableDisablePasswordResetTextBox()
    {
        ListBox listBox = getView().getAuthSourceListBox();
        String value = listBox.getSelectedItemText();
        gwtLog("xxx Auth source:" + value);
        if (value.equals(ObidosConstants.AUTH_SOURCE_LOCAL))
        {
            getView().getResetPasswordBox().setEnabled(true);
        }
        else
        {
            getView().getResetPasswordBox().setEnabled(false);
        }
    }

    private void setFormLabelColorToOriginal(FormLabel label)
    {
        ClientUtils.setFormLabelsColorOriginal(label);
    }
    private void setFormLabelColorToChanged(FormLabel label)
    {
        ClientUtils.setFormLabelsColorChanged(label);
    }

    @Override
    public void resetForm(String msg)
    {
        if (gUserComboResult == null)
        {
            showErrorMessage("Can not reset form at this time");
            return;
        }
        UserDTO userDTO = gUserComboResult.getUser();
        if (userDTO == null)
        {
            showErrorMessage("Can not reset form at this time (2)");
            return;
        }

        Long userId = userDTO.getId();
        GwtAsyncWrapper<UserComboResult> callback = new GwtAsyncWrapper<UserComboResult>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                String message = "Could not obtain user: " + e.getMessage();
                showErrorMessage(message);
                UserDTO userDTO = null;
                populateForm(userDTO); // reset all forms
            }

            @Override
            public void uponSuccess(UserComboResult result)
            {
               gUserComboResult = result;
               populateFormUserCombo(result);
               enableDisablePasswordResetTextBox();
               showAuthSourceChange();
               showMessage(msg);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getUserCombo(authCreds, userId, callback);
    }
    private void enableDisableUpdateButton()
    {
        Iterator<FieldNumber> enumKeySet = eMap.keySet().iterator();
        boolean dirty = false;
        while(enumKeySet.hasNext())
        {
            FieldNumber fieldNumber = enumKeySet.next();
            boolean val = eMap.get(fieldNumber);
            if (val)
            {
                dirty = true;
                break;
            }
        }

        if (dirty)
        {
            enableUpdateButton();
        }
        else
        {
            disableUpdateButton();
        }
    }

    private UserDTO getUserDTOFromForm()
    {
        if (gUserComboResult == null)
        {
            ClientUtils.showErrorMessageInMainView(this, "Can not get UserComboResult in form");
            return null;
        }
        UserDTO userDTO = gUserComboResult.getUser();
        if (userDTO == null)
        {
            ClientUtils.showErrorMessageInMainView(this, "Can not get UserDTO in form");
            return null;
        }

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setId(userDTO.getId());

        FieldNumber fn = FieldNumber.USERNAME;
        String username = null;
        if (hasFieldChanged(fn))
        {
        	username = getView().getUsernameTextBox().getValue();
        }
        newUserDTO.setUsername(username);
        
        fn = FieldNumber.FULLNAME;
        String fullname = null;
        if (hasFieldChanged(fn))
        {
        	fullname = getView().getFullnameTextBox().getValue();
        }
        newUserDTO.setFullname(fullname);
        
        fn = FieldNumber.AUTH_SOURCE;
        String authSource = null;
        if (hasFieldChanged(fn))
        {
        	authSource = getView().getAuthSourceListBox().getSelectedValue();
        }
        newUserDTO.setAuthSource(authSource);
        
        fn = FieldNumber.PRIMARY_EMAIL;
        String email1 = null;
        if (hasFieldChanged(fn))
        {
        	email1 = getView().getPrimaryemailTextBox().getValue();
        }
        newUserDTO.setEmail1(email1);
        
        fn = FieldNumber.PRIMARY_PHONE;
        String phone = null;
        if (hasFieldChanged(fn))
        {
        	phone = getView().getPrimaryphoneTextBox().getValue();
        }
        newUserDTO.setPhone(phone);
        
        fn = FieldNumber.MOBILE_PHONE;
        String mobile = null;
        if (hasFieldChanged(fn))
        {
        	mobile = getView().getMobilePhoneTextBox().getValue();
        }
        newUserDTO.setMobile1(mobile);
        
        fn = FieldNumber.RESET_USED_PASSWORD;
        String resetPassword = null;
        if (hasFieldChanged(fn))
        {
        	gwtLog("Admin has changed password...");
        	resetPassword = getView().getResetPasswordBox().getValue();
        	newUserDTO.setPassword(resetPassword);
        }

        fn = FieldNumber.REQUIRES_2FA_PASSWORD_RESET;
        Boolean rpr = null;
        if (hasFieldChanged(fn))
        {
        	rpr = getView().getRequiresTwoFAPasswordResetSwitch().getValue();
        }
        newUserDTO.setTwoFARequired(rpr);


        // A check was that a logged in user who is not an admin, which is
        // totally wrong. Only admin can update a user
        // Fix issue #661
        fn = FieldNumber.CAN_CREATE_GLOBAL_TEMPLATE;
        Boolean ccgt = null;
        if (hasFieldChanged(fn))
        {
        	CapabilityDTO cDTO = new CapabilityDTO();
			ccgt = getView().getGlobalTemplateToggleSwitch().getValue();
			gwtLog("XX Set can create Global template: " + ccgt);
			cDTO.setCreateGlobalTemplate(ccgt);
			newUserDTO.setCapabilities(cDTO);
        }

        if (getView().getReset2FACheckBox().isVisible())
        {
        	Boolean cbValue = getView().getReset2FACheckBox().getValue();
        	if (cbValue)
        	{
        		gwtLog("Reset 2FA...............");
        		newUserDTO.setTwoFAPasswordResetEnabled(!cbValue);
        	}
        }
        
        fn = FieldNumber.DELETE_PROFILE_PIC;
        Boolean dpp = null;
        if (hasFieldChanged(fn))
        {
        	dpp = getView().getDeleteProfilePicSwitch().getValue();
        }
		if (dpp != null)
		{
			// delete profile pic
			newUserDTO.setProfilePic(new byte[0]);
		}
		else
		{
			newUserDTO.setProfilePic(null);
		}
		

		fn = FieldNumber.PASSWORD_AGE;
		Long maximumPasswordAge = null;
		if (hasFieldChanged(fn))
		{
			long mpa = ClientUtils.fromInteger(getView().getPasswordAgeTextBox().getValue());
			maximumPasswordAge = mpa;
			
		}
		gwtLog("max password age: "+ maximumPasswordAge);
		newUserDTO.setMaximumPasswordAge(maximumPasswordAge);

        return  newUserDTO;
    }

	private boolean setMobilePhoneToUserDTO(final UserDTO userDTO)
	{
		String mobilePhone = getView().getMobilePhoneTextBox().getValue();
		if (mobilePhone == null || mobilePhone.length() == 0)
		{
			// delete
			userDTO.setMobile1("");
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
    public void updateUser()
    {
        // we're here means update button is clickable, meaning form has values
        UserDTO userDTO = getUserDTOFromForm();
        if (userDTO == null)
        {
            return;
        }

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String message = "Could not update user: " + caught.getMessage();
                showErrorMessage(message);
            }

            @Override
            public void uponSuccess(Void result)
            {
                resetFormLabelColors();
                disableUpdateButton();
                disableResetButton();
                Date now = new Date();
                String msg = glang.userUpdatedAt(now.toString());
                resetForm(msg); // must do that to update combo result
            }
        };
        gwtLog("max password age: " + userDTO.getMaximumPasswordAge());
        gwtLog("Password change required: " + userDTO.getPasswordChangeRequired());
        gwtLog("Max password age: " + userDTO.getMaximumPasswordAge());
        
        if (! setMobilePhoneToUserDTO(userDTO))
        {
        	// something is wrong
        	return;
        }
        
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().modifyUser(authCreds, userDTO, callback);
    }


    @Override
    public void showResetUserPasswordChange()
    {
        FormLabel label = getView().getResetPasswordLabel();
        String value = getView().getResetPasswordBox().getValue();
        boolean dirty = false;
        if (value == null) // all deleted
        {
            dirty = false;
        }
        else if (value.length() >  0)
        {
            dirty = true;
        }
        else
        {
            dirty = false;
        }
        FieldNumber fn = FieldNumber.RESET_USED_PASSWORD;
        eMap.put(fn,dirty);
        setFieldValueChagned(fn, dirty);

        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableUpdateButton();
    }

	@Override
	public void showRequires2FAPasswordResetChange()
	{
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }
        FormLabel label = getView().getRequiresTwoFAPasswordResetLabel();
        Boolean orig = userDTO.getTwoFARequired();
        Boolean value = getView().getRequiresTwoFAPasswordResetSwitch().getValue();
        booleanFormValueChanged(orig, value, label, FieldNumber.REQUIRES_2FA_PASSWORD_RESET);

        enableDisableUpdateButton();

	}

	@Override
	public void showReset2FAChange()
	{
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }
        FormLabel label = getView().getReset2FALabel();
        Boolean value = getView().getReset2FACheckBox().getValue();
        gwtLog("reset: " + value);
       	booleanFormValueChanged(false, value, label, FieldNumber.RESET_2FA);
        enableDisableUpdateButton();

	}


	@Override
	public void showCanCreateGlobalTemplateChange()
	{
        UserDTO userDTO = getOriginalUserDTO();
        if (userDTO == null)
        {
            return;
        }
        FormLabel label = getView().getCanCreateGlobalTemplateLabel();
        Boolean orig = userDTO.getCapabilities().getCreateGlobalTemplate();
        Boolean value = getView().getGlobalTemplateToggleSwitch().getValue();
        booleanFormValueChanged(orig, value, label, FieldNumber.CAN_CREATE_GLOBAL_TEMPLATE);

        enableDisableUpdateButton();

	}

    @Override
    public void listUsers()
    {
    	/*
        String type = null;
        try
        {
            type = ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.TYPE);
        } catch (ParamNotFoundException e)
        {
        }

        Map<String,String> with = new HashMap<>();

        if (type != null)
        {
            with.put(ObidosConstants.TYPE, type);
        }

        String nameToken = NameTokens.LIST_USERS;
        ClientUtils.showPage(placeManager, nameToken, with);
        */
    	ClientUtils.navigateToPlace(placeManager, NameTokens.LIST_USERS);
    }

    private void gwtLog(String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }


	@Override
	public void showIs2FAEnabledChange()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

	@Override
	public void back()
	{
		ClientUtils.back(placeManager);
	}

	@Override
	public void showUploadProfilePicPage()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			return;
		}
		Long userDTOId = userDTO.getId();
		if (userDTOId == null)
		{
			ClientUtils.showBootboxDialog("ERROR", "Could not get UserDTO Id");
			return;
		}
		String message = "";
		if (!Canvas.isSupported())
		{
			message = "This browser does not support HTML5 Canvas.";
		}
		if (!FileUtils.supportsFileAPI())
		{
			if (message.length() > 0)
			{
				message = message + "\n" + "Also this browser does not support HTML5 File API";
			}
			else
			{
				message = message + "\n" + "This browser does not support HTML5 File API";
			}
		}
		if (message.length() > 0)
		{
			message = message + "\n" + "<b>HTML5 Canvas and File API are required to upload profile image</b>";
			ClientUtils.showBootboxDialog("ERROR", message);
			return;
		}
       	Map<String,String> with = new HashMap<>();
       	with.put(ObidosConstants.ID, userDTOId.toString());
       	String nameToken = NameTokens.UPLOAD_PROFILE_PIC;
		ClientUtils.showPage(placeManager, nameToken, with);
	
	}
	private boolean hasFieldChanged(final FieldNumber fn)
	{
		return gFieldValueChangedMap.get(fn);
	}
    private void resetFieldsMaps()
    {
    	for (FieldNumber fn : FieldNumber.values())
    	{
    		eMap.put(fn, false);
    		gFieldValueChangedMap.put(fn, false);
    	}
    }

	private void setFieldValueChagned(FieldNumber fn, boolean dirty)
	{
		gFieldValueChangedMap.put(fn, dirty);
	}

	@Override
	public void countryCodeSelectCallback()
	{
		gwtLog("MMMM ");
		ClientUtils.countryCodeSelectCallback(getView().getMobilePhoneTextBox(),
				getView().getCountryCodeSelect(),
				countryCodeMap);
	}


	private String getTextBoxValue(TextBox textBox)
	{
		String value = textBox.getValue();
		if (value.length() == 0)
		{
			return " ";
		}
		return value;
	}

	@Override
	public void showCountryCodeSlectionChange()
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getMobilePhoneLabel();
		String origVal = dto.getMobile1();
		String formVal = getTextBoxValue(getView().getMobilePhoneTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.MOBILE_PHONE);
	}

}
