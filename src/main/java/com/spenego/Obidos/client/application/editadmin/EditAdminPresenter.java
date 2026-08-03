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

package com.spenego.Obidos.client.application.editadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.FileUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
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
import com.spenego.Obidos.shared.dto.UserComboResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class EditAdminPresenter extends
	ObidosPresenter<UserDTO, EditAdminPresenter.MyView, EditAdminPresenter.MyProxy, EditAdminUiHandlers>
		implements EditAdminUiHandlers
{

	private List<ToggleSwitch> gTs = new ArrayList<>();
   	ObidosMessages glang = ObidosMessages.LANG;
    private static UserComboResult userComboResult = null;

    private enum FieldNumber
    {
        USERNAME,
        FULLNAME,
        PRIMARY_EMAIL,
        PRIMARY_PHONE,
        CHANGE_PASSWORD,
        PROMOTE_TO_ROOT_ADMIN_SWITCH,
		CREATE_USER_SWITCH,
		CREATE_ADMIN_SWITCH,
		DELETE_USER_SWITCH,
		DELETE_ADMIN_SWITCH,
		LOCK_USER_SWITCH,
		LOCK_ADMIN_SWITCH,
		CHANGE_USER_CREDENTIALS_SWITCH,
		CHANGE_ADMIN_CREDENTIALS_SWITCH,
		MODIFY_EMAIL_TEMPLATE_SWITCH,
		DELETE_PROFILE_PIC,
		MODIFY_SYSTEM_SETTINGS_SWITCH,
    };

    private static EnumMap<FieldNumber, Boolean> eMap = new EnumMap<>(FieldNumber.class);
    private void resetEmap()
    {
        eMap.put(FieldNumber.USERNAME, false);
        eMap.put(FieldNumber.FULLNAME, false);
        eMap.put(FieldNumber.PRIMARY_EMAIL, false);
        eMap.put(FieldNumber.PRIMARY_PHONE, false);
        eMap.put(FieldNumber.CHANGE_PASSWORD, false);
        eMap.put(FieldNumber.PROMOTE_TO_ROOT_ADMIN_SWITCH, false);
        eMap.put(FieldNumber.MODIFY_SYSTEM_SETTINGS_SWITCH, false);
        resetToggleSwitches();
    }
    
    private void resetToggleSwitches()
    {
		eMap.put(FieldNumber.CREATE_USER_SWITCH, false);
		eMap.put(FieldNumber.CREATE_ADMIN_SWITCH,false);
		eMap.put(FieldNumber.DELETE_USER_SWITCH,false);
		eMap.put(FieldNumber.DELETE_ADMIN_SWITCH,false);
		eMap.put(FieldNumber.LOCK_USER_SWITCH,false);
		eMap.put(FieldNumber.LOCK_ADMIN_SWITCH,false);
		eMap.put(FieldNumber.CHANGE_USER_CREDENTIALS_SWITCH,false);
		eMap.put(FieldNumber.CHANGE_ADMIN_CREDENTIALS_SWITCH,false);
		eMap.put(FieldNumber.MODIFY_EMAIL_TEMPLATE_SWITCH,false);
		eMap.put(FieldNumber.DELETE_PROFILE_PIC,false);
		eMap.put(FieldNumber.MODIFY_SYSTEM_SETTINGS_SWITCH,false);
    }

	interface MyView extends View, HasUiHandlers<EditAdminUiHandlers>
	{
		public Button getHelpButton();
    	public BlockQuote getHelpBlockQuote();
    	public Row getUsernameRow();
    	public TextBox getAdminUsernameTextBox();
        public TextBox getAdminFullnameTextBox();
        public TextBox getEmailTextBox();
        public ObidosPasswordBox getPasswordBox();
        public TextBox getPhoneTextBox();
        public ObidosMessageRow getMessageRow();
		public ToggleSwitch getCreateUserSwitch();
		public ToggleSwitch getCreateAdminSwitch();
		public ToggleSwitch getDeleteUserSwitch();
		public ToggleSwitch getDeleteAdminSwitch();
		public ToggleSwitch getLockUserSwitch();
		public ToggleSwitch getLockAdminSwitch();
		public ToggleSwitch getChangeUserCredentialsSwitch();
		public ToggleSwitch getChangeAdminCredentialsSwitch();
		public ToggleSwitch getModifyEmailTemplatesSwitch();
		public Button getSaveButton();
		public Button getResetButton();
		public FormLabel getAdminUsernameLabel();
		public FormLabel getAdminFullnameLabel();
		public FormLabel getEmailLabel();
		public FormLabel getPhoneLabel();
		public FormLabel getPasswordLabel();
		public FlowPanel getMustChangePasswordFp();
		public FormLabel getMustChangePasswordLabel();
		public Row getMustChangePasswordRow();
		public FormLabel getCreateUserLabel();
		public FormLabel getCreateAdminLabel();
		public InputGroup getChangePassworinputGroup();
		public Button getShowHidePasswordButton();
		public Row getCapabilitiesRow();
		public FormLabel getDeleteUserLabel();
		public FormLabel getDeleteAdminLabel();
		public FormLabel getLockUserLabel();
		public FormLabel getLockAdminLabel();
		public FormLabel getChangeUserCredentialsLabel();
		public FormLabel getChangeAdminCredentialsLabel();
		public FormLabel getModifyEmailTemplatesLabel();
		public Row getRootAdminRow();
		public FormLabel getPrompteToRootAdminLabel();
		public ToggleSwitch getPromoteToRootAdminSwitch();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public ObidosPanelHeader getPanelHeader();
      	public Image getProfilePreviewImage();
      	public Button getUploadButton();
      	public Image getInvisibleImage();

		public FlowPanel getDeleteProfileFlowPanel();
		public FormLabel getDeleteProfilePicLabel();
		public ToggleSwitch getDeleteProfilePicSwitch();
		public ToggleSwitch getChangeSystemSettingSwitch();
		public FormLabel getChangeSystemSettingsLabel();
	}

	@NameToken(NameTokens.EDIT_ADMIN)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<EditAdminPresenter>
	{
	}

	@Inject
	EditAdminPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
	{
        super(eventBus, view, proxy, placeManager, currentUser);

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
		resetForm();
		resetComboResult();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	@Override
	protected void onReset()
	{
		super.onReset();
		showMessage("");
		getView().getButtonToolBarBottom().adjustButtonsWidth();
		resetForm();
		resetComboResult();
		updateToggleSwitchArray();
		updateCapabilitesToggleSwitches();
		String message = null;
		fetchAdminAndPopulateForm(message);
	}

	@Override
	public void save()
	{
        UserDTO userDTO = getUserDTOFromForm();
        if (userDTO == null)
        {
            return;
        }
        CapabilityDTO cdto = userDTO.getCapabilities();
        String name = userDTO.getUsername();
        gwtLog("MMM before saving " + name + " can change system settings in dto: " + cdto.getModifySettings());

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
                resetForm();
                Date now = new Date();
                String message= "Admin updated at: " + ClientUtils.formattedDate(now);
                fetchAdminAndPopulateForm(message);
            }
        };
        safeInvocationCall(() -> UserService.Utility.getInstance().modifyUser(ClientUtils.getAuthCreds(), userDTO, callback));
	}

	@Override
	public void reset()
	{
        if (userComboResult == null)
        {
            showErrorMessage("Can not reset form at this time");
            return;
        }
        UserDTO userDTO = userComboResult.getUser();
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
               userComboResult = result;
               populateFormUserCombo(result);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getUserCombo(authCreds, userId, callback);
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected String getIdName()
	{
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

    private void populateFormUserCombo(UserComboResult userCommboResult)
    {
        UserDTO userDTO = userCommboResult.getUser();
        gwtLog("MMM fetched admin: " + userDTO.getUsername());
        gwtLog("MMM can modify system settings? " + userDTO.getCapabilities().getModifySettings());

        populateForm(userDTO);
        CapabilityDTO cdto = userDTO.getCapabilities();
        if (cdto == null)
        {
        	showErrorMessage("Could not get Cabilities of the user");
        }
    }

    // if userDTO is passed as null, the form will be reset
    private void populateForm(UserDTO userDTO)
    {
    	if (userDTO == null)
    	{
            getView().getAdminUsernameTextBox().setValue("");
            getView().getAdminFullnameTextBox().setValue("");
            getView().getEmailTextBox().setValue("");
            getView().getPhoneTextBox().setValue("");
            updateCapabilitesToggleSwitches();
            return;
    	}

        ToggleSwitch ts = getView().getPromoteToRootAdminSwitch();
        ts.setReadOnly(false);
       	ts.setValue(false);
        ts.setReadOnly(true);
        if (currentUser.getUserDTO().getCapabilities().getRootAdmin())
        {
        	ts.setReadOnly(false);
        	ts.setValue(false);
        	refreshCapabilitesToggleSwitches(userDTO);
        }

   		displayProfilePic(userDTO);

		getView().getAdminUsernameTextBox().setValue(userDTO.getUsername());
		getView().getAdminFullnameTextBox().setValue(userDTO.getFullname());
		getView().getEmailTextBox().setValue(userDTO.getEmail1());
		getView().getPhoneTextBox().setValue(userDTO.getPhone());
        getView().getPasswordBox().setValue("");
        
        resetForm();
    }
    
    private void resetForm()
    {
        resetFormLabelColors();
        enableSaveButton(false);
        getView().getMustChangePasswordRow().setVisible(false);
        getView().getPasswordBox().setValue("");
        getView().getDeleteProfilePicSwitch().setValue(false);
        showMessage("");
    }

    private void resetFormLabelColors()
    {
        setFormLabelColorToOriginal(getView().getAdminUsernameLabel());
        setFormLabelColorToOriginal(getView().getAdminFullnameLabel());
        setFormLabelColorToOriginal(getView().getEmailLabel());
        setFormLabelColorToOriginal(getView().getPhoneLabel());
        setFormLabelColorToOriginal(getView().getPasswordLabel());
        setFormLabelColorToOriginal(getView().getChangeSystemSettingsLabel());

        resetToggleSwitchLabelColors();
    }
    
    private void resetToggleSwitchLabelColors()
    {
        setFormLabelColorToOriginal(getView().getPrompteToRootAdminLabel());
        setFormLabelColorToOriginal(getView().getCreateUserLabel());
        setFormLabelColorToOriginal(getView().getCreateAdminLabel());
        setFormLabelColorToOriginal(getView().getDeleteUserLabel());
        setFormLabelColorToOriginal(getView().getDeleteAdminLabel());
        setFormLabelColorToOriginal(getView().getLockUserLabel());
        setFormLabelColorToOriginal(getView().getLockAdminLabel());
        setFormLabelColorToOriginal(getView().getChangeUserCredentialsLabel());
        setFormLabelColorToOriginal(getView().getChangeAdminCredentialsLabel());
        setFormLabelColorToOriginal(getView().getModifyEmailTemplatesLabel());
        setFormLabelColorToOriginal(getView().getDeleteProfilePicLabel());
        setFormLabelColorToOriginal(getView().getChangeSystemSettingsLabel());
    }
    private void setFormLabelColorToOriginal(FormLabel label)
    {
        ClientUtils.setFormLabelsColorOriginal(label);
    }

    private void fetchAdminAndPopulateForm(String message)
    {
        Long userId = ClientUtils.getUserIdFromUrl(placeManager);
        if (userId == null)
        {
            showErrorMessage("Could not obtain user id from URL");
            return;
        }

        GwtAsyncWrapper<UserComboResult> callback = new GwtAsyncWrapper<UserComboResult>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                gwtLog("Could not fetch user: " + e.getMessage());
                UserDTO userDTO = null;
                populateForm(userDTO); // reset all forms
                String emsg = "Could not fetch admin: " + e.getMessage();
                showErrorMessage(emsg);
            }

            @Override
            public void uponSuccess(UserComboResult result)
            {
            	userComboResult = result;
            	populateFormUserCombo(result);
                if (message != null)
                {
                   showMessage(message);
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getUserCombo(authCreds, userId, callback);
    }
    private void resetComboResult()
    {
        userComboResult = null;
        resetEmap();
        enableSaveButton(false);
    }
    
    private void enableSaveButton(boolean enabled)
    {
    	getView().getSaveButton().setEnabled(enabled);
    }
    
    private UserDTO getUserDTOFromForm()
    {
        if (userComboResult == null)
        {
            ClientUtils.showErrorMessageInMainView(this, "Can not get UserComboResult in form");
            return null;
        }
        UserDTO userDTO = userComboResult.getUser();
        if (userDTO == null)
        {
            ClientUtils.showErrorMessageInMainView(this, "Can not get UserDTO in form");
            return null;
        }

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setId(userDTO.getId());

        newUserDTO.setUsername(getView().getAdminUsernameTextBox().getValue());
        newUserDTO.setFullname(getView().getAdminFullnameTextBox().getValue());
        newUserDTO.setEmail1(getView().getEmailTextBox().getValue());
        newUserDTO.setPhone(getView().getPhoneTextBox().getValue());
        newUserDTO.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
        String resetPassword = getView().getPasswordBox().getValue();
        if (resetPassword != null && resetPassword.length() > 0)
        {
            newUserDTO.setPassword(resetPassword);
            newUserDTO.setPasswordChangeRequired(true); 
        }
        
        CapabilityDTO cdto = new CapabilityDTO();
        if (getView().getPromoteToRootAdminSwitch().getValue())
        {
        	cdto.setRootAdmin(true);
        }
        else
        {
			cdto.setCreateUser(getView().getCreateUserSwitch().getValue());
			cdto.setCreateAdmin(getView().getCreateAdminSwitch().getValue());
			cdto.setDeleteUser(getView().getDeleteUserSwitch().getValue());
			cdto.setDeleteAdmin(getView().getDeleteAdminSwitch().getValue());
			cdto.setLockUser(getView().getLockUserSwitch().getValue());
			gwtLog("LLLLLLLLLLLLLLL: " + getView().getLockUserSwitch().getValue());
			cdto.setLockAdmin(getView().getLockAdminSwitch().getValue());
			cdto.setChangeUserCredentials(getView().getChangeUserCredentialsSwitch().getValue());
			cdto.setChangeAdminCredentials(getView().getChangeAdminCredentialsSwitch().getValue());
			cdto.setModifyEmailTemplates(getView().getModifyEmailTemplatesSwitch().getValue());
			cdto.setModifySettings(getView().getChangeSystemSettingSwitch().getValue());
        }
        newUserDTO.setCapabilities(cdto);
		Boolean value = getView().getDeleteProfilePicSwitch().getValue();
		if (value)
		{
			// delete profile pic
			newUserDTO.setProfilePic(new byte[0]);
		}
        return  newUserDTO;
    }
    private void updateCapabilitesToggleSwitches()
    {
    	ClientUtils.updateAdminCapabilitesToggleSwitchesNew(
    		placeManager,
    		currentUser,
    		getView().getMessageRow(),
			getView().getCreateUserSwitch(),
			getView().getCreateAdminSwitch(),
			getView().getDeleteUserSwitch(),
			getView().getDeleteAdminSwitch(),
			getView().getLockUserSwitch(),
			getView().getLockAdminSwitch(),
			getView().getChangeUserCredentialsSwitch(),
			getView().getChangeAdminCredentialsSwitch(),
			getView().getModifyEmailTemplatesSwitch(),
			getView().getChangeSystemSettingSwitch());
    	
    	CapabilityDTO cdto = currentUser.getUserDTO().getCapabilities();
    	// hide capabilities for non-root admins
    	getView().getCapabilitiesRow().setVisible(cdto.getRootAdmin());
    	
    	// enable/disable username and change password boxes
		getView().getAdminUsernameTextBox().setEnabled(true);
		getView().getAdminUsernameTextBox().setTitle("");
		getView().getPasswordBox().setEnabled(true);
		getView().getPasswordBox().setTitle("");
		getView().getShowHidePasswordButton().setEnabled(true);
    }
    
    /*
    private boolean getToggleSwitchValue(String langKey)
    {
    	ToggleSwitch ts = tsMap.get(langKey);
    	if (ts.isReadOnly())
    	{
    		return false;
    	}
    	return ts.getValue();
    }
    */
    private boolean getCapable(Boolean bv)
    {
    	return ClientUtils.fromBoolean(bv);
    }

    /**
     * If admin does not have certain capability, disable that switch.
     * But before doing that, set the capability of the admin being edited.
     * 
     * <p>
     * @author spgdev@spenego.com - Apr 28, 2019
     */
    private void refreshCapabilitesToggleSwitches(UserDTO userDTO)
    {
    	if (currentUser == null || userDTO == null)
    	{
    		return;
    	}
    	CapabilityDTO editorAdminCdto = currentUser.getUserDTO().getCapabilities();
    	if (editorAdminCdto == null)
    	{
    		return;
    	}

    	// Being edited admin
    	CapabilityDTO adminCdto = userDTO.getCapabilities();
    	getView().getPromoteToRootAdminSwitch().setReadOnly(false);
    	if (adminCdto.getRootAdmin())
    	{
    		getView().getPromoteToRootAdminSwitch().setReadOnly(true);
			for (ToggleSwitch ts : gTs)
			{
				ts.setReadOnly(false);
				ts.setValue(true);
				ts.setReadOnly(true); 
			}
			return;

    	}
    	
    	// 1. Update the switch with users's capability first
    	// 2. Can the Editor admin has the same capability? Enable or Disable
    	//    the swictch based on editor admin's capability
    	
    	// Create User
    	ToggleSwitch ts = getView().getCreateUserSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getCreateUser());
    	boolean editorCap = getCapable(editorAdminCdto.getCreateUser());
    	boolean adminCap = ClientUtils.fromBoolean(adminCdto.getCreateUser());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
   		
    	gwtLog("Editor can create user? " + editorCap);
    	gwtLog("Edited can create user? " + userDTO.getUsername() + ":" + adminCap);

   		// Create Admin
   		ts = getView().getCreateAdminSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getCreateAdmin());
    	editorCap = getCapable(editorAdminCdto.getCreateAdmin());
    	adminCap = getCapable(adminCdto.getCreateAdmin());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
   		
    	gwtLog("Editor can create admin? " + editorCap);
    	gwtLog("Edited can create admin? " + userDTO.getUsername() + ":" + adminCap);

   		// Delete User
   		ts = getView().getDeleteUserSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getDeleteUser());
    	editorCap = getCapable(editorAdminCdto.getDeleteUser());
    	adminCap = getCapable(adminCdto.getDeleteUser());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
    	gwtLog("Editor can delete user? " + editorCap);
    	gwtLog("Edited can delete user? " + adminCap);
    	
    	// Delete Admin
   		ts = getView().getDeleteAdminSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getDeleteAdmin());
    	editorCap = getCapable(editorAdminCdto.getDeleteAdmin());
    	adminCap = getCapable(adminCdto.getDeleteAdmin());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
    	
    	// Lock User
   		ts = getView().getLockUserSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getLockUser());
    	editorCap = getCapable(editorAdminCdto.getLockUser());
    	adminCap = getCapable(adminCdto.getLockUser());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
    	
    	// Lock Admin
   		ts = getView().getLockAdminSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getLockAdmin());
    	editorCap = getCapable(editorAdminCdto.getLockAdmin());
    	adminCap = getCapable(adminCdto.getLockAdmin());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
    	
    	// Change User Credentials
   		ts = getView().getChangeUserCredentialsSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getChangeUserCredentials());
    	editorCap = getCapable(editorAdminCdto.getChangeUserCredentials());
    	adminCap = getCapable(adminCdto.getChangeUserCredentials());
    	if (adminCap && !editorCap)
    	{
    		ts.setReadOnly(true);
    	}
    	else
    	{
    		ts.setReadOnly(false);
    	}

//    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
    	
    	// Change Admin Credentials
   		ts = getView().getChangeAdminCredentialsSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getChangeAdminCredentials());
    	editorCap = getCapable(editorAdminCdto.getChangeAdminCredentials());
    	adminCap = getCapable(adminCdto.getChangeAdminCredentials());
    	if (adminCap && !editorCap)
    	{
    		ts.setReadOnly(true);
    	}
    	else
    	{
    		ts.setReadOnly(false);
    	}
    	gwtLog(">>> Editor cap of admin cred: " + editorCap);
    	gwtLog(">>> admin cred: " + adminCap);
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);
    	

    	// Modify Email Templates
   		ts = getView().getModifyEmailTemplatesSwitch();
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getModifyEmailTemplates());
    	editorCap = getCapable(editorAdminCdto.getModifyEmailTemplates());
    	adminCap = getCapable(adminCdto.getModifyEmailTemplates());
    	ClientUtils.setToggleSwitchValue(ts, adminCap, editorCap);

     	ts = getView().getChangeSystemSettingSwitch();
     	String targetAdminName = userDTO.getUsername();
     	gwtLog("MMM modifying admin user: '" + targetAdminName + "'");
     	gwtLog("MMM After saving, admin user '" + targetAdminName + "' can modify system settings?: " + adminCdto.getModifySettings());
     	
    	ClientUtils.setToggleSwitchValue(ts, adminCdto.getModifySettings());
     	editorCap = getCapable(editorAdminCdto.getChangeAdminCredentials());
    	adminCap = getCapable(adminCdto.getChangeAdminCredentials());
    	if (adminCap && !editorCap)
    	{
    		ts.setReadOnly(true);
    	}
    	else
    	{
    		ts.setReadOnly(false);
    	}
  	
    	gwtLog("Editor? " + editorCap);
    	gwtLog("Edited? " + adminCap);
    }

    private UserDTO getOriginalUserDTO()
    {
        if (userComboResult == null)
        {
//            ClientUtils.showErrorMessageInMainView(this, "Can not get UserComboResult in form");
            return null;
        }
        UserDTO userDTO = userComboResult.getUser();
        if (userDTO == null)
        {
 //           ClientUtils.showErrorMessageInMainView(this, "Can not get UserDTO in form");
            return null;
        }
        return userDTO;
    }


	@Override
	public void showUsernameChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getAdminUsernameLabel();
        String ov = userDTO.getUsername();
        String nv = getView().getAdminUsernameTextBox().getValue();
        stringFormValueChanged(ov, nv, label, FieldNumber.USERNAME);

	}

	@Override
	public void showFullnameChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getAdminFullnameLabel();
        String ov = userDTO.getFullname();
        String nv = getView().getAdminFullnameTextBox().getValue();
        stringFormValueChanged(ov, nv, label, FieldNumber.FULLNAME);
	}

	@Override
	public void showEmailChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getEmailLabel();
        String ov = userDTO.getEmail1();
        String nv = getView().getEmailTextBox().getValue();
        stringFormValueChanged(ov, nv, label, FieldNumber.PRIMARY_EMAIL);
	}

	@Override
	public void showPhoneChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getPhoneLabel();
        String ov = userDTO.getPhone();
        String nv = getView().getPhoneTextBox().getValue();
        stringFormValueChanged(ov, nv, label, FieldNumber.PRIMARY_PHONE);
	}

	@Override
	public void showPasswordChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getPasswordLabel();
        String ov = userDTO.getPassword();
        String nv = getView().getPasswordBox().getValue();
        stringFormValueChanged(ov, nv, label, FieldNumber.CHANGE_PASSWORD);
	}

	@Override
	public void showPromoteToRotoAdminSwitchChange()
	{

		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getPrompteToRootAdminLabel();
        Boolean ov = userDTO.getCapabilities().getRootAdmin();
        Boolean nv = getView().getPromoteToRootAdminSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.PROMOTE_TO_ROOT_ADMIN_SWITCH);
		disableToggleSwitches(getView().getPromoteToRootAdminSwitch().getValue());
	}

	@Override
	public void showCreateUsersChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getCreateUserLabel();
        Boolean ov = userDTO.getCapabilities().getCreateUser();
        Boolean nv = getView().getCreateUserSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.CREATE_USER_SWITCH);
	}

	@Override
	public void showModifySettingsChagne()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getChangeSystemSettingsLabel();
        Boolean ov = userDTO.getCapabilities().getModifySettings();
        Boolean nv = getView().getChangeSystemSettingSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.MODIFY_SYSTEM_SETTINGS_SWITCH);
	}

	@Override
	public void showCreateAdminsChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getCreateAdminLabel();
        Boolean ov = userDTO.getCapabilities().getCreateAdmin();
        Boolean nv = getView().getCreateAdminSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.CREATE_ADMIN_SWITCH);
	}

	@Override
	public void showDeleteUsersChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getDeleteUserLabel();
        Boolean ov = userDTO.getCapabilities().getDeleteUser();
        Boolean nv = getView().getDeleteUserSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.DELETE_USER_SWITCH);
	}

	@Override
	public void showDeleteAdminsChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getDeleteAdminLabel();
        Boolean ov = userDTO.getCapabilities().getDeleteAdmin();
        Boolean nv = getView().getDeleteAdminSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.DELETE_ADMIN_SWITCH);
	}

	@Override
	public void showLockUsersChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getLockUserLabel();
        Boolean ov = userDTO.getCapabilities().getLockUser();
        Boolean nv = getView().getLockUserSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.LOCK_USER_SWITCH);

	}

	@Override
	public void showLockAdminsChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getLockAdminLabel();
        Boolean ov = userDTO.getCapabilities().getLockAdmin();
        Boolean nv = getView().getLockAdminSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.LOCK_ADMIN_SWITCH);
	}

	@Override
	public void showChangeUsersCredentialsChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getChangeUserCredentialsLabel();
        Boolean ov = userDTO.getCapabilities().getChangeUserCredentials();
        Boolean nv = getView().getChangeUserCredentialsSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.CHANGE_USER_CREDENTIALS_SWITCH);
	}

	@Override
	public void showChangeAdminsCredentialsChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getChangeAdminCredentialsLabel();
        Boolean ov = userDTO.getCapabilities().getChangeAdminCredentials();
        Boolean nv = getView().getChangeAdminCredentialsSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.CHANGE_ADMIN_CREDENTIALS_SWITCH);
	}

	@Override
	public void showModifyEmailTemplateChange()
	{
		UserDTO userDTO = getOriginalUserDTO();
		if (userDTO == null)
		{
			gwtLog("Original userDTO in combo result is null");
			return;
		}
        FormLabel label = getView().getModifyEmailTemplatesLabel();
        Boolean ov = userDTO.getCapabilities().getModifyEmailTemplates();
        Boolean nv = getView().getModifyEmailTemplatesSwitch().getValue();
        booleanFormValueChanged(ov, nv, label, FieldNumber.MODIFY_EMAIL_TEMPLATE_SWITCH);
	}

	@Override
	public void showDeleteProfilePicChagne()
	{
		FormLabel label = getView().getDeleteProfilePicLabel();
		Boolean origVal = false;
		Boolean formVal = getView().getDeleteProfilePicSwitch().getValue();
		booleanFormValueChanged(origVal, formVal, label, FieldNumber.DELETE_PROFILE_PIC);
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
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
            if (fieldNumber == FieldNumber.CHANGE_PASSWORD)
            {
            	getView().getMustChangePasswordRow().setVisible(true);
//            	getView().getMustChangePasswordFp().setVisible(true);
//            	getView().getMustChangePasswordLabel().setVisible(true);
            }

        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
            if (fieldNumber == FieldNumber.CHANGE_PASSWORD)
            {
            	getView().getMustChangePasswordRow().setVisible(false);
 //           	getView().getMustChangePasswordFp().setVisible(false);
 //           	getView().getMustChangePasswordLabel().setVisible(false);
            }

        }
        enableDisableSaveButton();
    }

    private void booleanFormValueChanged(Boolean origVal, Boolean formVal, FormLabel label, FieldNumber fieldNumber)
    {
    	boolean dirty = false;
    	if (origVal != null && !origVal.equals(formVal))
    	{
    		dirty = true;
    	}
    	eMap.put(fieldNumber, dirty);
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableSaveButton();
    }


    private void enableDisableSaveButton()
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
                gwtLog("Dirty field: " + fieldNumber);
                break;
            }
        }

        enableSaveButton(dirty);
        if (!formValueChanged())
        {
//        	enableSaveButton(false);
        }
    }
	private void disableToggleSwitches(boolean disabled)
	{
		showMessage("");
		resetToggleSwitchLabelColors();
		gwtLog("Switch value: " + disabled);
		if (disabled)
		{
			showMessage(glang.allCapabilitiesAreAvailable());
		}
		for (ToggleSwitch ts : gTs)
		{
			ts.setReadOnly(false);
			ts.setValue(true);
			ts.setReadOnly(disabled); 
		}
		if (!disabled)
		{
			getView().getSaveButton().setEnabled(false);
			UserDTO dto = getOriginalUserDTO();
			refreshCapabilitesToggleSwitches(dto);
			getView().getSaveButton().setEnabled(true);
			if (!formValueChanged())
			{
				getView().getSaveButton().setEnabled(false);
			}
			resetToggleSwitches();
		}
		
	}

    private void updateToggleSwitchArray()
    {
    	getView().getPromoteToRootAdminSwitch().setValue(false);
    	gTs.clear();
		gTs.add(getView().getCreateUserSwitch());
		gTs.add(getView().getCreateAdminSwitch());
		gTs.add(getView().getDeleteUserSwitch());
		gTs.add(getView().getDeleteAdminSwitch());
		gTs.add(getView().getLockUserSwitch());
		gTs.add(getView().getLockAdminSwitch());
		gTs.add(getView().getChangeUserCredentialsSwitch());
		gTs.add(getView().getChangeAdminCredentialsSwitch());
		gTs.add(getView().getModifyEmailTemplatesSwitch());
		gTs.add(getView().getChangeSystemSettingSwitch());
    }
    
    private boolean formValueChanged()
    {
    	gwtLog("Username changed: " + eMap.get(FieldNumber.USERNAME));
    	gwtLog("Fullname changed: " + eMap.get(FieldNumber.FULLNAME));
    	gwtLog("Email changed; " + eMap.get(FieldNumber.PRIMARY_EMAIL));
    	gwtLog("Phone changed: " + eMap.get(FieldNumber.PRIMARY_PHONE));
    	gwtLog("Password chagnged; " + eMap.get(FieldNumber.CHANGE_PASSWORD));
    	boolean b = eMap.get(FieldNumber.USERNAME) | eMap.get(FieldNumber.FULLNAME) | eMap.get(FieldNumber.PRIMARY_EMAIL) | eMap.get(FieldNumber.PRIMARY_PHONE) | eMap.get(FieldNumber.CHANGE_PASSWORD);
    	gwtLog("Form chagned? " + b);
    	return b;
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
	@Override
	public void showUploadProfilePicPage()
	{
		UserDTO userDTO = getOriginalUserDTO();
		Long userId = null;
		if (userDTO != null && userDTO.getId() != null)
		{
			userId = userDTO.getId();
		}
		if (userId == null)
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
       	with.put(ObidosConstants.ID, userId.toString());
       	String nameToken = NameTokens.UPLOAD_PROFILE_PIC;
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@Override
	public void back()
	{
		ClientUtils.back(placeManager);
	}



}