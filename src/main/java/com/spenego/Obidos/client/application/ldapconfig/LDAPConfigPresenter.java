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

package com.spenego.Obidos.client.application.ldapconfig;

import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class LDAPConfigPresenter extends Presenter<LDAPConfigPresenter.MyView, LDAPConfigPresenter.MyProxy>
		implements LDAPConfigUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    private static LdapDTO ldapDTO = null;
    public static enum FieldNumber
    {
        configName,
        ldapURI,
        baseDN,
        bindDN,
        bindPassword,
        authAttr,
    };
    private static EnumMap<FieldNumber, Boolean> eMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);
   	private Timer busyTimer = null;

	interface MyView extends View, HasUiHandlers<LDAPConfigUiHandlers>
	{
	    public FormLabel getConfigNameLabel();
	    public TextBox getConfigNameTextBox();
		public TextBox getUriTextBox();
		public TextBox getBasednTextBox();
		public TextBox getBinddnTextBox();
		public ObidosPasswordBox getBindpassTextBox();
		public TextBox getAuthAttrTextBox();
		public Button getTestConnectionButton();
		public Button getSaveButton();
		public BlockQuote getHelpBlockQuote();
		public Paragraph getHelpParagraph();
        public FormLabel getLdapUriLabel();
        public FormLabel getBaseDnLabel();
        public FormLabel getBindDnLabel();
        public FormLabel getBindPasswordLabel();
        public FormLabel getAuthAttrLabel();
        public Button getResetButton();
        public ObidosMessageRow getMessageRow();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosPanelHeader getPanelHeader();
        public ObidosMessageRow getTestMessageRow();
        public ObidosButtonToolBar getButtonToolBarTest();
        public ObidosTextBox getTestUsernameTextBox();
        public ObidosPasswordBox getTestPasswordBox();
		public CheckBox getStartTLSCheckBox();
		public HTMLPanel getProcessingPanel();
		public Button getTestAuthenticationButton();
	}

	@NameToken(NameTokens.LDAPSETTINGS)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<LDAPConfigPresenter>
	{
	}

	final PlaceManager placeManager;
	final CurrentUser currentUser;

	@Inject
	LDAPConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		clearTimers();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage(null);
		showTestMessage(null);
		enableTestButtons(true);
		ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
		
		
		getView().getButtonToolBarBottom().adjustButtonsWidth();
		getView().getButtonToolBarTest().adjustButtonsWidth();

		hideHelpArea();
		showBackButton();
		resetForm();
		setHelpMessage();
		fetchLdapSettings();
		resetLabelsColor();
		setSaveButtonTitle();
	}

	private void clearTimers()
    {
		showProcessing(false);
		enableTestButtons(true);
    	if (busyTimer != null)
    	{
    		busyTimer.cancel();
    		busyTimer = null;
    	}
    }

	private void showBackButton()
	{
		getView().getPanelHeader().getBackButton().setVisible(false);
		String action = ClientUtils.getActionFromUrl(placeManager);
		if (ObidosConstants.EDIT.equals(action))
		{
			getView().getPanelHeader().getBackButton().setVisible(true);
		}

	}


	private void enableDisableResetButton(boolean val)
	{
	    getView().getResetButton().setEnabled(val);
	}

	private void updatePanelHeading(String heading)
	{
	    getView().getPanelHeader().setText(heading);
	}

	@Override
	public void fetchLdapSettings()
	{
	    updatePanelHeading("New Active Directory/LDAP Settings");
	    showMessage(null);
	    showTestMessage(null);
	    getView().getStartTLSCheckBox().setValue(false);
	    Long ldapId = getLdapIdFromUrl();
	    if (ldapId == null)
	    {
	        return;
	    }

	    GwtAsyncWrapper<LdapDTO> callback = new GwtAsyncWrapper<LdapDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String errorMessage = "Could not fetch LDAP settings: " + caught.getMessage();
                showErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(LdapDTO dto)
            {
                setOrigLdapDTO(dto);
                updatePanelHeading("Update Active Directory/LDAP Settings");
                populateLdapFields(dto);
            }
        };
    	AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
    	AdminConfigService.Utility.getInstance().getLdapSettings(authCredsDTO,ldapId,callback);
	}

	private void setOrigLdapDTO(LdapDTO dto)
	{
	    ldapDTO = dto;
	}

	private LdapDTO getOrigLdapDTO()
	{
	    return ldapDTO;
	}

	private void disableButtons()
	{
	    getView().getSaveButton().setEnabled(false);
	    getView().getResetButton().setEnabled(false);
	}

	private void enableButtons()
	{
	    getView().getSaveButton().setEnabled(true);
	    getView().getResetButton().setEnabled(true);
	}

	private void setTextField(TextBox textBox, String val)
	{
		if (val != null && val.length() > 0)
		{
			textBox.setValue(val);
		}
	}

	private Long getLdapIdFromUrl()
	{
    	Long ldapId = null;
    	try
        {
            ldapId = ClientUtils.getIdFromUrl(placeManager,ObidosConstants.LDAP_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            return null;
        }
    	return ldapId;
	}

	private void setSaveButtonTitle()
	{
		getView().getSaveButton().setText(glang.saveButtonTitle());
	    if (getLdapIdFromUrl() != null)
	    {
			getView().getSaveButton().setText(glang.update());
	    }
	}

	private void populateLdapFields(LdapDTO ldapDTO)
	{
		showMessage("");

		if (ldapDTO == null)
		{
			return;
		}

		String val = ldapDTO.getLdapuri();
		setTextField(getView().getUriTextBox(),val);

		// #111
		boolean useStartTLS = ClientUtils.fromBoolean(ldapDTO.getStartTls());
		getView().getStartTLSCheckBox().setValue(useStartTLS);

		getView().getConfigNameTextBox().setValue(ldapDTO.getName());
		setTextField(getView().getBasednTextBox(), ldapDTO.getBaseDn());
		setTextField(getView().getBinddnTextBox(), ldapDTO.getBindDn());

		val = ldapDTO.getBindPass();
		if (val != null && val.length() > 0)
		{
			getView().getBindpassTextBox().setValue(val);
		}

		setTextField(getView().getAuthAttrTextBox(), ldapDTO.getAuthAttr());

		disableButtons();
		resetLabelsColor();
	}

	private void resetForm()
	{
		showMessage(null);
	    getView().getConfigNameTextBox().setValue("");
		getView().getUriTextBox().setValue("");
		getView().getBasednTextBox().setValue("");
		getView().getBinddnTextBox().setValue("");
		getView().getBindpassTextBox().setValue("");
		getView().getAuthAttrTextBox().setValue("");
		getView().getTestUsernameTextBox().setValue(null);
		getView().getTestPasswordBox().setValue(null);
	}


	/**
	 * read values from Form and return LdapDTO
	 *
	 * @return LdapDTO on success, null otherwise
	 * <p>
	 * @author spgdev@spenego.com - Feb 21, 2017
	 */
	private LdapDTO getLdapDTOFromForms(boolean forTestConnection)
	{
	    String configName = getView().getConfigNameTextBox().getValue();
		String uri = getView().getUriTextBox().getValue();
		boolean userStartTLS = getView().getStartTLSCheckBox().getValue(); // #111
		String baseDn = getView().getBasednTextBox().getValue();
		String bindDn = getView().getBinddnTextBox().getValue();
		String bindPass = getView().getBindpassTextBox().getValue();
		String authAttr = getView().getAuthAttrTextBox().getValue();

		if (!forTestConnection)
		{
			if (configName.length() == 0)
			{
				showErrorMessage("Please specify: Configuraion Name. Example: AD/LDAP-Development");
				return null;
			}
			if (configName.equals(ObidosConstants.AUTH_SOURCE_LOCAL))
			{
				showErrorMessage("Configuration Name '" + ObidosConstants.AUTH_SOURCE_LOCAL + "' is reserved for Obidos internal Authentication Source");
				return null;
			}
		}

    	if (uri.length() == 0)
    	{
    		showErrorMessage("Please specify: LDAP URI. Example: ldaps://1.2.34:636");
    		return null;
    	}
    	
    	if (! forTestConnection)
    	{
			if (baseDn.length() == 0)
			{
				showErrorMessage("Please specify: Base DN. Example: dc=example,dc=com");
				return null;
			}
			if (authAttr.length() == 0)
			{
				showErrorMessage("Please specify: Auth Attribute. e.g. uid, CN, SMAccountName etc");
				return null;
			}
    	}

    	// TODO: verify input
    	LdapDTO ldapDTO = new LdapDTO();
    	ldapDTO.setName(configName); // it appears the length is only 32 characters long in the schema, increase it to say 256
    	ldapDTO.setLdapuri(uri);
    	// Issue #111 spgdev@spenego.com - Feb 23, 2025
    	ldapDTO.setStartTls(userStartTLS);
    	ldapDTO.setBaseDn(baseDn);
    	ldapDTO.setBindDn(bindDn);
    	ldapDTO.setBindPass(bindPass);
    	ldapDTO.setAuthAttr(authAttr);

		return ldapDTO;
	}

    private void updateLdapSettings(LdapDTO ldapDTO)
    {
    	if (! validateLDAPSettings(ldapDTO))
    	{
    		return;
    	}

    	GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String errorMessage = "Could not update AD/LDAP Settings: " + caught.getMessage();
                showErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(Void arg0)
            {
				String d = new Date().toString();
				String message = "AD/LDAP settings updated successfully on " + d;
				showMessage(message);
				resetLabelsColor();
				disableButtons();
            }
        };
    	AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
    	AdminConfigService.Utility.getInstance().updateLdapConfig(authCredsDTO,ldapDTO,callback);
    }
    
    private boolean validateLDAPSettings(final  LdapDTO dto)
    {
    	String uri = dto.getLdapuri();
    	if (null != uri)
    	{
    		uri = uri.trim().toLowerCase();
    		dto.setLdapuri(uri);
    	}
    	boolean useStartTLS = ClientUtils.fromBoolean(dto.getStartTls());
    	if (useStartTLS == true)
    	{
    		uri = dto.getLdapuri();
    		if (! uri.startsWith("ldap://"))
    		{
    			showErrorMessage("When StartTLS is enabled, LDAP URI must be 'ldap://host-or-ip:standard_port'");
    			return false;
    		}
    	}
    	return true;
    }

	@Override
	public void saveLDAPSettings()
	{
    	LdapDTO ldapDTO = getLdapDTOFromForms(false);
    	if (ldapDTO == null)
    	{
    		return;
    	}
    	Long ldapId = getLdapIdFromUrl();
    	if (ldapId != null)
    	{
            ldapDTO.setId(ldapId);
            updateLdapSettings(ldapDTO);
            return;
    	}
    	
    	if (! validateLDAPSettings(ldapDTO))
    	{
    		return;
    	}
    	

    	GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
                String errorMessage = "Could not save AD/LDAP Settings: " + caught.getMessage();
			    showErrorMessage(errorMessage);
			}

			@Override
			public void uponSuccess(Long rc)
			{
				String d = new Date().toString();
				String message = "AD/LDAP settings saved successfully on " + d;
				showMessage(message);
				resetLabelsColor();
				disableButtons();
			}
		};
    	AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
		AdminConfigService.Utility.getInstance().createLdapSettings(authCredsDTO, ldapDTO, callback);
	}

    private void showProcessing(final boolean show)
    {
		showMessage("");
		showErrorMessage("");
		enableTestButtons(false);
    	getView().getProcessingPanel().setVisible(show);
    }
    
	private void testLDAPAuthenticationReal()
	{
		showMessage("");
		LdapDTO ldapDTO = getLdapDTOFromForms(false);
		if (ldapDTO == null)
		{
			clearTimers();
			return;
		}
		String username = getView().getTestUsernameTextBox().getValue();
		if (username == null || username.length() == 0)
		{
			clearTimers();
			showTestErrorMessage(glang.specifyAUsername());
			return;
		}

		String password = getView().getTestPasswordBox().getValue();
		if (password == null || password.length() == 0)
		{
			clearTimers();
			showTestErrorMessage(glang.specifyAPassword());
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void v)
			{
				clearTimers();
				showTestMessage(glang.ldapAuthenticationSuccessful());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				clearTimers();
				showTestErrorMessage("LDAP Authentication failed: " + caught.getMessage());
			}
		};
    	AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
		AdminConfigService.Utility.getInstance().testLDAPAuthentication(authCredsDTO, ldapDTO, username, password, callback);
	}

    private void testLDAPConnectionReal()
    {
    	showMessage("");
		showTestMessage("");
 		LdapDTO ldapDTO = getLdapDTOFromForms(true);
		if (ldapDTO == null)
		{
			return;
		}
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
				clearTimers();
				showTestErrorMessage("LDAP Connection failed: " + caught.getMessage());
			}

			@Override
			public void uponSuccess(Void v)
			{
				clearTimers();
				showTestMessage(glang.ldapConnectionSuccessful());
			}
		};
    	AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
		AdminConfigService.Utility.getInstance().testLDAPConnection(authCredsDTO, ldapDTO, callback);
    }

	@Override
	public void testLDAPConnection()
	{
		showMessage("");
		LdapDTO ldapDTO = getLdapDTOFromForms(true);
		if (ldapDTO == null)
		{
			return;
		}
		showProcessing(true);
		if (busyTimer != null)
		{
			busyTimer.cancel();
		}
		busyTimer = new Timer() {

			@Override
			public void run()
			{
				showMessage("");
				testLDAPConnectionReal();
			}
		};
    	busyTimer.schedule(ObidosConstants.KEY_STROKE_DELAY_FOR_STRENGTH_CHECK);
	}

	@Override
	public void testLDAPAuthentication()
	{
		gwtLog("MMM In testLDAP Authentication...");
		showMessage("");
		getView().getMessageRow().setVisible(false);
		LdapDTO ldapDTO = getLdapDTOFromForms(true);
		if (ldapDTO == null)
		{
			return;
		}
		String username = getView().getTestUsernameTextBox().getValue();
		if (username == null || username.length() == 0)
		{
			showTestErrorMessage(glang.specifyAUsername());
			return;
		}

		String password = getView().getTestPasswordBox().getValue();
		if (password == null || password.length() == 0)
		{
			showTestErrorMessage(glang.specifyAPassword());
			return;
		}

		showProcessing(true);

		if (busyTimer != null)
		{
			busyTimer.cancel();
		}
		busyTimer = new Timer() {

			@Override
			public void run()
			{
				showMessage("");
				testLDAPAuthenticationReal();
			}
		};
    	busyTimer.schedule(ObidosConstants.KEY_STROKE_DELAY_FOR_STRENGTH_CHECK);
	}

	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void enableTestButtons(final boolean enable)
	{
		showMessage("");
		showErrorMessage("");
		getView().getTestConnectionButton().setEnabled(enable);
		getView().getTestAuthenticationButton().setEnabled(enable);
		
	}

	// Help starts--
    private void hideHelpArea()
    {
        getView().getHelpBlockQuote().setVisible(false);
    }

    private void showHelpArea()
    {
        getView().getHelpBlockQuote().setVisible(true);
    }

    private void setHelpMessage()
    {
        String html = ObidosMessages.LANG.ldapSettingHelp();
        Paragraph p = getView().getHelpParagraph();
        p.setHTML(html);
    }

    @Override
    public void showHideHelp()
    {
    	ClientUtils.showHelp(getView().getHelpBlockQuote());
    }
    // Help ends--

    private void highlightFormLabelOnValueChange(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
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
            gwtLog("Should not be here. origVal: " + origVal + " formVal: " + formVal);
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }
        gwtLog("formval: " + formVal);

        switch(fieldNumber)
        {
            case configName:
            {
                eMap.put(FieldNumber.configName,dirty);
                break;
            }
            case ldapURI:
            {
                eMap.put(FieldNumber.ldapURI,dirty);
                break;
            }
            case baseDN:
            {
                eMap.put(FieldNumber.baseDN,dirty);
                break;
            }
            case bindDN:
            {
                eMap.put(FieldNumber.bindDN,dirty);
                break;
            }

            case bindPassword:
            {
                eMap.put(FieldNumber.bindPassword,dirty);
                break;
            }

            case authAttr:
            {
                eMap.put(FieldNumber.authAttr,dirty);
                break;
            }

            default:
            {
                break;
            }
        }

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
            enableButtons();
        }
        else
        {
            disableButtons();
        }

    }

    private void resetLabelsColor()
    {
        FormLabel label = getView().getConfigNameLabel();
        ClientUtils.resetFormLabelColor(label);
        label = getView().getLdapUriLabel();
        ClientUtils.resetFormLabelColor(label);
        label = getView().getBaseDnLabel();
        ClientUtils.resetFormLabelColor(label);
        label = getView().getBindDnLabel();
        ClientUtils.resetFormLabelColor(label);
        label = getView().getBindPasswordLabel();
        ClientUtils.resetFormLabelColor(label);
        label = getView().getAuthAttrLabel();
        ClientUtils.resetFormLabelColor(label);
    }

    @Override
    public void showFormFieldChange(FieldNumber field)
    {
        LdapDTO dto = getOrigLdapDTO();
        if (dto == null)
        {
            return;
        }
        String ov = null;
        String nv = null;
        FormLabel label = null;
        gwtLog("in showFieldChange()");
        switch(field)
        {
            case configName:
            {
                ov = dto.getName();
                nv = getView().getConfigNameTextBox().getValue();
                label = getView().getConfigNameLabel();
                break;
            }
            case ldapURI:
            {
                ov = dto.getLdapuri();
                nv = getView().getUriTextBox().getValue();
                label = getView().getLdapUriLabel();
                //highlightFormLabelOnValueChange(ov,nv,label,field);
                break;
            }
            case baseDN:
            {
                ov = dto.getBaseDn();
                nv = getView().getBasednTextBox().getValue();
                label = getView().getBaseDnLabel();
                highlightFormLabelOnValueChange(ov,nv,label,field);
                break;
            }

            case bindDN:
            {
                ov = dto.getBaseDn();
                nv = getView().getBinddnTextBox().getValue();
                label = getView().getBindDnLabel();
                highlightFormLabelOnValueChange(ov,nv,label,field);
                break;
            }

            case bindPassword:
            {
                ov = dto.getBindPass();
                nv = getView().getBindpassTextBox().getValue();
                label = getView().getBindPasswordLabel();
                highlightFormLabelOnValueChange(ov,nv,label,field);
                break;
            }

            case authAttr:
            {
                ov = dto.getAttrVal();
                nv = getView().getAuthAttrTextBox().getValue();
                label = getView().getAuthAttrLabel();
                highlightFormLabelOnValueChange(ov,nv,label,field);
                break;
            }

            default:
            {
            }
        }
        formValueChanged(ov, nv, label, field);
    }

    private void formValueChanged(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
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
            gwtLog("Should not be here. origVal: " + origVal + " formVal: " + formVal);
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }
        gwtLog("formval: " + formVal);
        /*
        configName,
        ldapURI,
        baseDN,
        bindDN,
        bindPassword,
        authAttr,
        */

        gwtLog("dirty: " + dirty + "Field: " + fieldNumber);

        switch(fieldNumber)
        {
            default:
            {
                eMap.put(fieldNumber, dirty);
                break;
            }
        }

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

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

	@Override
	public void goBack()
	{
		ClientUtils.goBack(placeManager);
	}

	private void showTestMessage(final String message)
	{
		getView().getTestMessageRow().showMessage(message);
	}
	private void showTestErrorMessage(final String errorMessage)
	{
		getView().getTestMessageRow().showErrorMessage(errorMessage);
	}

	@Override
	public void startTLSCheckBoxClickHandler()
	{
		gwtLog("MMM Checkbox clicked");
	}

}
