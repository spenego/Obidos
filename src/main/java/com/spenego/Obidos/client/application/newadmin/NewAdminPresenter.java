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

package com.spenego.Obidos.client.application.newadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

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
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
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
import com.spenego.Obidos.shared.dto.UserDTO;

public class NewAdminPresenter 
	extends ObidosPresenter<UserDTO, NewAdminPresenter.MyView, NewAdminPresenter.MyProxy, NewAdminUiHandlers>
    implements NewAdminUiHandlers
{
	private List<ToggleSwitch> gTs = new ArrayList<ToggleSwitch>();
	private ObidosMessages gLang = ObidosMessages.LANG;
    interface MyView extends View, HasUiHandlers<NewAdminUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public TextBox getAdminNameTextBox();
        public TextBox getAdminFullnameTextBox();
        public ObidosPasswordBox getPasswordBox();
        public TextBox getEmailTextBox();
        public TextBox getPhoneTextBox();
        public ObidosMessageRow getMessageRow();
        public CheckBox getSendEmailCheckBox();
        public TextArea getEmailCommentTextArea();
		public ToggleSwitch getCreateUserSwitch();
		public ToggleSwitch getCreateAdminSwitch();
		public ToggleSwitch getDeleteUserSwitch();
		public ToggleSwitch getDeleteAdminSwitch();
		public ToggleSwitch getLockUserSwitch();
		public ToggleSwitch getLockAdminSwitch();
		public ToggleSwitch getChangeUserCredentialsSwitch();
		public ToggleSwitch getChangeAdminCredentialsSwitch();
		public ToggleSwitch getModifyEmailTemplatesSwitch();
		public ToggleSwitch getRootAdminSwitch();
		public ToggleSwitch getChangeSystemSettingSwitch();
		public FlowPanel getCapFp();
		public Row getRootAdminRow();
		public Button getCreateAdminButton();
		public ObidosButtonToolBar getButtonToolBar();
		public ObidosPanelHeader getPanelHeader();
    }

    @NameToken(NameTokens.NEW_ADMIN)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<NewAdminPresenter>
    {
    }

    @Inject
    NewAdminPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            final PlaceManager placeManager,
            final CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);
        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        gwtLog("onBind()");
    }

    protected void onReveal()
    {
        super.onReveal();
        gwtLog("onReveal()");
    }

    protected void onHide()
    {
        super.onHide();
        clearArray();
    }

    protected void onUnbind()
    {
        super.onUnbind();
        gwtLog("onUnbind()");
    }
    
    protected void onReset()
    {
        super.onReset();
        showMessage("");
        getView().getPanelHeader().getBackButton().setText(gLang.admins());
        getView().getButtonToolBar().adjustButtonsWidth();
        clearForm();
        updateCapabilitesToggleSwitches();
        updateToggleSwitchArray();
        getView().getChangeSystemSettingSwitch().setReadOnly(false);
    }
    
    private void clearArray()
    {
    	gTs.clear();
    }
    
    private void turnOnCapabilites(boolean b)
    {
	// Issue #718. Turn off all capabilites by default
		for (ToggleSwitch ts : gTs)
		{
			ts.setValue(b);
		}
    }
    
    private void updateToggleSwitchArray()
    {
    	getView().getRootAdminSwitch().setValue(false);
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

		// Issue #718. Turn off all capabilities by default
		turnOnCapabilites(false);
    }
    
    private void enableSwitch(ToggleSwitch ts, boolean enable)
    {
    	if (enable)
    	{
    		// must enable first
    		ts.setReadOnly(false);
    		ts.setValue(true);
    	}
    	else
    	{
    		// must enable to set the value
    		ts.setReadOnly(false);
    		ts.setValue(false);
    		// turn disable it
    		ts.setReadOnly(true);
    	}
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
    	
    	CapabilityDTO cdto = ClientUtils.getCapabilitesDTO(currentUser);
   		getView().getRootAdminRow().setVisible(true);
    	if (cdto != null)
    	{
    		if (!cdto.getRootAdmin())
    		{
    			getView().getRootAdminRow().setVisible(false);
    		}
    	}
    }
    
    private void clearForm()
    {
    	getView().getAdminNameTextBox().setValue("");
    	getView().getAdminFullnameTextBox().setValue("");
    	getView().getPasswordBox().setValue("");
    	getView().getEmailTextBox().setValue("");
    	getView().getPhoneTextBox().setValue("");
    	getView().getEmailCommentTextArea().setValue("");
    }

    private boolean isEmpty(String str, String message)
    {
        if (str == null || (str != null && str.length() == 0))
        {
            showErrorMessage(message);
            return true;
        }
        return false;
    }
    
    @Override
    public void createAdmin()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        String adminName = getView().getAdminNameTextBox().getValue();
        boolean rc = isEmpty(adminName, lang.pleaseSpecifyAdminUsername());
        if (rc) return;

        String fullName = getView().getAdminFullnameTextBox().getValue();
        rc = isEmpty(fullName, lang.pleaseSpecifyFullname());
        if (rc) return;

        String password = getView().getPasswordBox().getValue();
        rc = isEmpty(password, lang.pleaseSpecifyPassword());
        if (rc) return;

        String email = getView().getEmailTextBox().getValue();
        rc = isEmpty(email, lang.pleaseSpecifyPrimaryEmail());
        if (rc) return;

        String phone = getView().getPhoneTextBox().getValue();
        rc = isEmpty(email, lang.pleaseSpecifyPrimaryPhone());
        if (rc) return;
        

		Boolean notifyUser = getView().getSendEmailCheckBox().getValue();
		String emailComment = getView().getEmailCommentTextArea().getValue();

        UserDTO dto = new UserDTO();
        dto.setAdministrator(Boolean.TRUE);
        dto.setUsername(adminName);
        dto.setFullname(fullName);
        dto.setPassword(password);
        dto.setEmail1(email);
        dto.setPhone(phone);
        // must set auth source to local for admins
        // Issue #437
        dto.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
        
		CapabilityDTO cdto = new CapabilityDTO(null);
        if (getView().getRootAdminSwitch().getValue())
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
			cdto.setLockAdmin(getView().getLockAdminSwitch().getValue());
			cdto.setChangeUserCredentials(getView().getChangeUserCredentialsSwitch().getValue());
			cdto.setChangeAdminCredentials(getView().getChangeAdminCredentialsSwitch().getValue());
			cdto.setModifyEmailTemplates(getView().getModifyEmailTemplatesSwitch().getValue());
			cdto.setModifySettings(getView().getChangeSystemSettingSwitch().getValue());
		}
        
        GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                String emsg = gLang.couldnotCreateAdmin() + ": " + e.getMessage();
                showErrorMessage(emsg);
            }

            @Override
            public void uponSuccess(Long id)
            {
            	Date d = new Date();
            	showMessage(ObidosMessages.LANG.adminAccountCreatedOn(d.toString()));
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().createUser(authCreds, dto, cdto, notifyUser, emailComment, callback);
    }

    private void showListOfAdminsPage()
    {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.LIST_USERS)
                .with(ObidosConstants.TYPE,ObidosConstants.LIST_ADMINS)
                .build();
		placeManager.revealPlace(placeRequest);

    }

    @Override
    public void listAdmins()
    {
        showListOfAdminsPage();
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

	@Override
	public void help()
	{
		showHelp();
	}

	private void disableToggleSwitches(boolean disabled)
	{
		showMessage("");
		if (disabled)
		{
			showMessage(gLang.allCapabilitiesAreAvailable());
		}
		for (ToggleSwitch ts : gTs)
		{
			ts.setReadOnly(false);
			ts.setValue(disabled);
			ts.setReadOnly(disabled); 
		}
		
	}

	@Override
	public void rootAdminSwichCallback()
	{
		disableToggleSwitches(getView().getRootAdminSwitch().getValue());
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

}
