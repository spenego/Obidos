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

package com.spenego.Obidos.client.application.smtpconfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

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
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public class SmtpConfigPresenter extends ObidosPresenter<SmtpConfigDTO, SmtpConfigPresenter.MyView, SmtpConfigPresenter.MyProxy, SmtpConfigUiHandlers>
    implements SmtpConfigUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    private static SmtpConfigDTO dtoOrig = null;

    private enum FieldNumber
    {
        smtpServer,
        smtpPort,
        connectionType,
        smtpAuthUser,
        smtpAuthPassword
    };
    private static EnumMap<FieldNumber, Boolean> eMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);

    private SmtpConfigDTO getOrignalSmtpConfigDTO()
    {
        return dtoOrig;
    }
    
    private void setOriginalSmtpConfigDTO(SmtpConfigDTO dto)
    {
    	dtoOrig = dto;
    }

    private void resetSmtpConfig()
    {
        dtoOrig = null;
    }

    interface MyView extends View, HasUiHandlers<SmtpConfigUiHandlers>
    {
		public BlockQuote getHelpBlockQuote();

        public FormLabel getSmtpServerFormLabel();
        public FormLabel getSmtpPortLabel();
        public FormLabel getSecureConnectionLabel();

        public FormGroup getFormGroupSmtpServer();
        public TextBox getSmtpServerTextBox();
        public ListBox getConnectionTypeListBox();
        public ObidosIntegerTextBox getSmtpPortTextBox();
        public TextBox getSmtpAuthUserTextBox();
        public Input getSmtpAuthUserPasswordBox();
        public TextBox getTestToTextBox();
        public TextBox getTestFromTextBox();
        public FormLabel getFormErrorLabel();
        public Button getTestEmailButton();
        public Button getSaveButton();
        public Button getResetButton();
        public TextBox getSubjectTextBox();
        public TextBox getMessgaeTextBox();
        public InputGroup getInputGroup();
        public ObidosMessageRow getMessageRow();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosPanelHeader getPanelHeader();
        public ObidosRowBottom2px getAuthUserRow();
        public ObidosRow getAuthPassRow();
        public FormLabel getSmtpAuthUserLabel();
        public FormLabel getSmtpAuthUsrPassLabel();
        public ObidosMessageRow getMessageRowSendEmail();
        public Button getDeleteButton();
    }

    @NameToken(NameTokens.SMTPSETTINGS)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<SmtpConfigPresenter>
    {
    }

    @Inject
    SmtpConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        resetForm();
    }

    protected void onHide()
    {
        super.onHide();
        gwtLog("onHide()");
        resetForm();
    }

    protected void onUnbind()
    {
        super.onUnbind();
        gwtLog("onUnbind()");
    }

    protected void onReset()
    {
        super.onReset();
        getView().getButtonToolBarBottom().adjustButtonsWidth();
        resetForm();
        hideHelpArea();
        showMessage(null);
        showEmailSentMessage(null);
        resetSmtpConfig();
        setPanelHeading(glang.smtpSettings());
        setSaveButtonTitle(glang.saveButtonTitle());
        getView().getSaveButton().setEnabled(true);
        enableDeleteButton(false);
        fetchAndUpdateForm();
    }

    private void fetchAndUpdateForm()
    {
    	showMessage(null);
    	GwtAsyncWrapper<SmtpConfigDTO> callback = new GwtAsyncWrapper<SmtpConfigDTO>(this)
        {

            @Override
            public void uponFailure(Throwable t)
            {
            	if (t instanceof NoSuchRecordException)
            	{
            		gwtLog("MMM smtp is not configured yet");
            		updateConnectionTypeListBox();
            		
            	}
            	else
            	{
            		gwtLog("MMMM ex: " + t.getStackTrace());
            		showErrorMessage("Could not get SMTP configuration: " + t.getMessage());
            	}
            }

            @Override
            public void uponSuccess(SmtpConfigDTO dto)
            {
           		updateConnectionTypeListBox();
            	gwtLog("MMM: " + dto.getName());
                setOriginalSmtpConfigDTO(dto);

				enableUpdateButton(true);
				enableResetButton(true);
				enableDeleteButton(true);
				enableTestEmailButton();
				setSaveButtonTitle(glang.update());

                populateForm(dto);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        AdminConfigService.Utility.getInstance().getSmtpConfig(authCreds, "default", callback);
    }
    
    private void fetchAndUpdateFormOld()
    {
    	resetForm();
        populateFormOnEditRequest();
        if (isEditing())
        {
            enableUpdateButton(false);
            enableTestEmailButton();
        }
        else
        {
            enableUpdateButton(true);
            disableTestEmailButton();
        }
        enableResetButton(false);
        updateConnectionTypeListBox();
        showAuthRows(true);
        resetTestmailForm();
   	
    }


    private void updateConnectionTypeListBox()
    {
   	    ListBox ctlb = getView().getConnectionTypeListBox();
        int n = ctlb.getItemCount();
        gwtLog("Number of items in connection type lb: "+ n);
       	ctlb.setItemText(0, ObidosMessages.LANG.startTLS());
       	ctlb.setItemText(1, ObidosMessages.LANG.ssl());
       	ctlb.setItemText(2, ObidosMessages.LANG.notSecure());

    }

    private void enableResetButton(boolean enabled)
    {
        getView().getResetButton().setEnabled(enabled);
    }


    private void disableTestEmailButton()
    {
        getView().getTestEmailButton().setEnabled(false);
    }

    private void enableTestEmailButton()
    {
        getView().getTestEmailButton().setEnabled(true);
    }


    private void setPanelHeading(String heading)
    {
        getView().getPanelHeader().setText(heading);
    }

    private void setSaveButtonTitle(String title)
    {
        getView().getSaveButton().setText(title);
    }
    
    private boolean checkFormInputs()
    {
    	String smtpServer = getView().getSmtpServerTextBox().getValue();
        ListBox ctlb = getView().getConnectionTypeListBox();
        Integer smtpPort = getView().getSmtpPortTextBox().getValue();
        String authUser = getView().getSmtpAuthUserTextBox().getValue();
        String authPass = getView().getSmtpAuthUserPasswordBox().getValue();
        
        if (smtpServer == null || smtpServer.length() == 0)
        {
        	showErrorMessage("Please specify SMTP Server IP Address/FQDN");
        	return false;
        }
        gwtLog("MMM SMTP port: " + smtpPort);
        if (smtpPort == null)
        {
        	showErrorMessage("Please specify SMTP Server Port");
        	return false;
        }
        if ( ! ClientUtils.isValidPort(smtpPort))
        {
        	showErrorMessage("Invalid SMTP port: " + smtpPort);
        	return false;
        }
        	
    	return true;
    }

    private SmtpConfigDTO getSmtpConfigDtoFromForm()
    {
         boolean rc = checkFormInputs();
        if (!rc) return null;
        gwtLog("MMM rc: " + rc);

        String smtpServer = getView().getSmtpServerTextBox().getValue();
        ListBox ctlb = getView().getConnectionTypeListBox();
        Integer smtpPort = getView().getSmtpPortTextBox().getValue();
        String authUser = getView().getSmtpAuthUserTextBox().getValue();
        String authPass = getView().getSmtpAuthUserPasswordBox().getValue();

        SmtpConfigDTO dto = new SmtpConfigDTO();
        dto.setSmtpServer(smtpServer);;
        dto.setSmtpPort(smtpPort);

        boolean secure = true;
        int idx = ctlb.getSelectedIndex();
        if (idx == ObidosConstants.SMTP_AUTH_NONE)
        {
            secure = false;
            dto.setUseAuthentication(false);
            dto.setUseStartTls(false);
            dto.setUseSsl(false);
        }
        else
        {
            dto.setUseAuthentication(true);
            if (idx == ObidosConstants.SMTP_AUTH_STARTTLS)
            {
                dto.setUseStartTls(true);
                dto.setUseSsl(false);
            }
            else if (idx == ObidosConstants.SMTP_AUTH_SSL)
            {
                dto.setUseSsl(true);
                dto.setUseStartTls(false);
            }
        }
        if (secure)
        {
            dto.setSmtpUsername(authUser);
            dto.setSmtpPassword(authPass);
        }

        return dto;
    }

    @Override
    public void sendTestEmail()
    {
    	showMessage(null);
        showEmailSentMessage(null);
    	
        String to = getView().getTestToTextBox().getValue();
        String from = getView().getTestFromTextBox().getValue();
        String subj = getView().getSubjectTextBox().getValue();
        String message = getView().getMessgaeTextBox().getValue();
        boolean rc = verifyTestEmail(from, glang.specifyFromAddress());
        if (!rc) return;

        rc = verifyTestEmail(to, glang.specifyToAddress());
        if (!rc) return;

        rc = verifyTestEmail(subj, glang.specifySubject());
        if (!rc) return;

        rc = verifyTestEmail(message, glang.specifyMessage());
        if (!rc) return;


        SmtpConfigDTO dto = getSmtpConfigDtoFromForm();
        if (dto == null)
        {
            return;
        }

        SmtpEnvelope envelope = new SmtpEnvelope();
        envelope.setFrom(from);
        envelope.setTos(to);
        envelope.setSubject(subj);
        envelope.setTextMessage(message);
        envelope.setHtmlMessage("<b>" + message + "</b>"); // HTML message can not be null at this time

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                String errorMessage = ObidosMessages.LANG.couldnotSendTestMail() + ": "  + e.getMessage();
                showEmailSentErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(Void result)
            {
            	String message = glang.testMailSent(ClientUtils.formattedDate(new Date()));
                showEmailSentMessage(message);
                resetTestmailForm();
            }
        };
        showEmailSentMessage(glang.sendingEmail());
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        AdminConfigService.Utility.getInstance().sendEmail(authCreds, dto, envelope, callback);
    }
    
    private void resetTestmailForm()
    {
    	getView().getTestFromTextBox().setValue(null);
		getView().getTestToTextBox().setValue(null);
		getView().getTestFromTextBox().setValue(null);
		getView().getSubjectTextBox().setValue(null);
		getView().getMessgaeTextBox().setValue(null);
		getView().getTestEmailButton().setEnabled(false);
    }
    
    private void createSmtpConfig()
    {
    	gwtLog("MMM create SMTP config");
    	showMessage(null);
        showEmailSentMessage(null);
		SmtpConfigDTO dto = getSmtpConfigDtoFromForm();
		if (dto == null)
		{
			gwtLog("MMM Could not create SMTP config from form");
			return;
		}
		GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(ObidosMessages.LANG.couldNotSaveSmtpConfiguration() + ": " + caught.getMessage());
			}

			@Override
			public void uponSuccess(Long result)
			{
				Date d = new Date();
				gwtLog("SMTP Configuration saved...");
				enableUpdateButton(true);
				enableResetButton(true);
				enableDeleteButton(true);
				enableTestEmailButton();
				setSaveButtonTitle(glang.update());
				fetchAndUpdateForm();
				showMessage(ObidosMessages.LANG.smtpConfigurationSaved() + " on: " + d.toString());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		gwtLog("MMMM create new SMTP settings");
		AdminConfigService.Utility.getInstance().createSmtpConfig(authCreds, dto, callback);
    }
    
    private void updateSMTPConfig()
    {
     	showMessage(null);
        showEmailSentMessage(null);
		SmtpConfigDTO dto = getSmtpConfigDtoFromForm();
		if (dto == null)
		{
			gwtLog("MMM Could not create SMTP config from form");
			return;
		}
 		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(ObidosMessages.LANG.couldNotUpdateSmtpConfiguration() + ": " + caught.getMessage());
			}

			@Override
			public void uponSuccess(Void result)
			{
				gwtLog("SMTP Configuration updated...");
				enableUpdateButton(true);
				enableResetButton(true);
				enableDeleteButton(true);
				enableTestEmailButton();
				fetchAndUpdateForm();
				showMessage(ObidosMessages.LANG.smtpConfigurationUpdated() + ": on " + ClientUtils.formattedDate(new Date()));
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		gwtLog("MMM Updating SMTP config");
		// must set the ID for update
		SmtpConfigDTO odto = getOrignalSmtpConfigDTO();
		if (odto  != null)
		{
			dto.setId(odto.getId());
			gwtLog("MMM Updating SMTP config after");
			AdminConfigService.Utility.getInstance().updateSmtpConfig(authCreds, dto, callback);
		}
    }

    @Override
    public void saveSMTPSettings()
    {
		SmtpConfigDTO odto = getOrignalSmtpConfigDTO();
		if (odto != null)
		{
			updateSMTPConfig();
		}
		else
		{
			createSmtpConfig();
		}
    }

    public void saveSMTPSettingsOrig()
    {
    	showMessage(null);
        showEmailSentMessage(null);
        if (!isEditing())
        {
            GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
            {

                @Override
                public void uponFailure(Throwable caught)
                {
                    showErrorMessage(ObidosMessages.LANG.couldNotSaveSmtpConfiguration() + ": " + caught.getMessage());
                }

                @Override
                public void uponSuccess(Long result)
                {
                	Date d = new Date();
                	gwtLog("SMTP Configuration saved...");
                    enableUpdateButton(false);
                    enableResetButton(false);
                    enableTestEmailButton();
                    showMessage(ObidosMessages.LANG.smtpConfigurationSaved() + " on: " + d.toString());
                }
            };
            SmtpConfigDTO dto = getSmtpConfigDtoFromForm();
            if (dto != null)
            {
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                gwtLog("MMMM create new SMTP settings");
//                AdminConfigService.Utility.getInstance().createSmtpConfig(authCreds, dto, callback);
            }
        }
        else
        {
            GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
            {

                @Override
                public void uponFailure(Throwable caught)
                {
                    showErrorMessage(ObidosMessages.LANG.couldNotUpdateSmtpConfiguration() + ": " + caught.getMessage());
                }

                @Override
                public void uponSuccess(Void result)
                {
                	gwtLog("SMTP Configuration updated...");
                    enableUpdateButton(false);
                    enableTestEmailButton();
                    fetchAndUpdateForm();
                    showMessage(ObidosMessages.LANG.smtpConfigurationUpdated() + ": on " + ClientUtils.formattedDate(new Date()));
                }
            };
            SmtpConfigDTO dto = getSmtpConfigDtoFromForm();
            if (dto != null)
            {
                gwtLog("Updating SMTP config");
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                // must set the ID for update
                dto.setId(getOrignalSmtpConfigDTO().getId());
                AdminConfigService.Utility.getInstance().updateSmtpConfig(authCreds, dto, callback);
            }
        }
    }

    private boolean verify(String str, String msg)
    {
        if (str == null || str.length() == 0)
        {
        	showErrorMessage(msg);
            return false;
        }
        return true;
    }
    private boolean verifyTestEmail(String str, String msg)
    {
        if (str == null || str.length() == 0)
        {
            showEmailSentErrorMessage(msg);
            return false;
        }
        return true;
    }

    private boolean isEditing()
    {
    	/*
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        String action = placeRequest.getParameter(ObidosConstants.ACTION, "N/A");
        if (action.equals(ObidosConstants.EDIT))
        {
            return true;
        }
        return false;
        */
    	return false;
    }

    private void populateFormOnEditRequest()
    {
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        String action = placeRequest.getParameter(ObidosConstants.ACTION, "N/A");
        String name = placeRequest.getParameter(ObidosConstants.NAME, "N/A");

        if (action.equals(ObidosConstants.EDIT) && !name.equals("N/A"))
        {
            setPanelHeading(glang.updateSMTPSettings());
            setSaveButtonTitle(glang.update());
            getView().getFormGroupSmtpServer().setValidationState(ValidationState.ERROR);
        }
        else
        {
            resetForm();
            resetTestmailForm();
            return;
        }
        GwtAsyncWrapper<SmtpConfigDTO> callback = new GwtAsyncWrapper<SmtpConfigDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not get SMTP configuration to update: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(SmtpConfigDTO dto)
            {
                setOriginalSmtpConfigDTO(dto);
                populateForm(dto);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        AdminConfigService.Utility.getInstance().getSmtpConfig(authCreds, name, callback);
    }

    private void populateForm(SmtpConfigDTO dto)
    {
    	if (dto == null)
    	{
    		return;
    	}
        getView().getSmtpServerTextBox().setValue(dto.getSmtpServer());
        getView().getSmtpPortTextBox().setValue(dto.getSmtpPort());
        ListBox ctlb = getView().getConnectionTypeListBox();
        if (dto.getUseSsl())
        {
            ctlb.setSelectedIndex(ObidosConstants.SMTP_AUTH_SSL);
        	showAuthRows(true);
        }
        else if (dto.getUseStartTls())
        {
        	showAuthRows(true);
        }
        else
        {
            ctlb.setSelectedIndex(ObidosConstants.SMTP_AUTH_NONE);
            showAuthRows(false);
        }
        if (dto.getUseAuthentication())
        {
            getView().getSmtpAuthUserTextBox().setValue(dto.getSmtpUsername());
            getView().getSmtpAuthUserPasswordBox().setValue(dto.getSmtpPassword());
        }
    }

    private void setSmtpServer(String value)
    {
        getView().getSmtpServerTextBox().setValue(value);
    }

    private void setSmtpPort(Integer value)
    {
        getView().getSmtpPortTextBox().setValue(value);
    }

    private void setConnectionType(int value)
    {
        getView().getConnectionTypeListBox().setSelectedIndex(value);
    }

    private void setAuthUser(String value)
    {
        getView().getSmtpAuthUserTextBox().setValue(value);
    }

    private void setAuthPassword(String value)
    {
        getView().getSmtpAuthUserPasswordBox().setValue(value);
    }

    private void resetAllFormFields()
    {
        if (!isEditing())
        {
            setSmtpServer("");
            setSmtpPort(null);
            setConnectionType(ObidosConstants.SMTP_AUTH_STARTTLS);
            setAuthUser("");
            setAuthPassword("");
        }
        else
        {
             populateFormOnEditRequest();
        }
    }

    private void showAuthRows(boolean visible)
    {
        showEmailSentMessage(null);
    	getView().getAuthUserRow().setVisible(visible);
    	getView().getAuthPassRow().setVisible(visible);
    }


    public void resetForm()
    {
        resetAllFormFields();
        enableUpdateButton(false);
        enableResetButton(false);
        enableDeleteButton(false);
        showAuthRows(true);
        resetTestmailForm();
        resetSmtpConfig();
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
            enableUpdateButton(true);
        }
        else
        {
            enableUpdateButton(false);
        }

    }

    private void enableUpdateButton(boolean enabled)
    {
        getView().getSaveButton().setEnabled(enabled);
        getView().getResetButton().setEnabled(enabled);
    }
    
    private void enableDeleteButton(boolean enabeld)
    {
    	getView().getDeleteButton().setEnabled(enabeld);
    }
    
    private void enableButtons(boolean enabled)
    {
        getView().getSaveButton().setEnabled(enabled);
        getView().getResetButton().setEnabled(enabled);
    	getView().getDeleteButton().setEnabled(enabled);
    	getView().getTestEmailButton().setEnabled(enabled);
    }


    private void hideHelpArea()
    {
        getView().getHelpBlockQuote().setVisible(false);
    }

    private void showHelpArea()
    {
        getView().getHelpBlockQuote().setVisible(true);
    }

	@Override
	public void showHideHelp()
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

	
	private void showEmailSentMessage(String message)
	{
		getView().getMessageRowSendEmail().showMessage(message);
	}

	private void showEmailSentErrorMessage(String errorMessage)
	{
		getView().getMessageRowSendEmail().showErrorMessage(errorMessage);
	}


	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

	@Override
	public void fetchAndResetForm()
	{
        fetchAndUpdateForm();
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@Override
	public void deleteConfig()
	{
		SmtpConfigDTO dto = getOrignalSmtpConfigDTO();
		if (dto == null)
		{
			gwtLog("MMM orig config is null");
			return;
		}
		showMessage(null);
		promptDelete(dto);
	}
	
	private void deleteSmtpConfigReal(final SmtpConfigDTO dto)
	{
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {
            @Override
            public void uponFailure(Throwable e)
            {
                String errorMessage = "Could not delete SMTP configuration: " + e.getMessage();
                showErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(Void rc)
            {
            	resetForm();
            	setSaveButtonTitle(glang.saveButtonTitle());
            	enableUpdateButton(true);
            	enableResetButton(false);
            	showMessage("SMTP configuration deleted");
            }
        };
        AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
        //Boolean deletePermanently = Boolean.TRUE;
        // Issue #420 TODO
        // we only have 1 setting right now
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(dto.getId());
        AdminConfigService.Utility.getInstance().deleteSmtpConfig(authCredsDTO, ids, callback);
	}

    private void promptDelete(final SmtpConfigDTO smtpDTO)
    {
        String title = glang.warning();
        String message = glang.smtpSettingsDeleteWarningSimple();
		ClientUtils.promptForAction(() -> deleteSmtpConfigReal(smtpDTO), title, message);
    }

	@Override
	public void showConnectionTypeChange()
	{
        ListBox lb = getView().getConnectionTypeListBox();
        int idx = lb.getSelectedIndex();
        gwtLog("Index: " + idx);
        String item = lb.getSelectedItemText();
        gwtLog("Item: " +  "'" + item + "'");

        if (!glang.notSecure().equals(item))
        {
            gwtLog("Show auth rows");
            showAuthRows(true);
        }
        else
        {
            showAuthRows(false);
        }

        SmtpConfigDTO dto = getOrignalSmtpConfigDTO();
        if (dto == null)
        {
            gwtLog("dtoOrig is null..");
            return;
        }
        boolean useAuthentication = dto.getUseAuthentication();
        boolean useStartTLS = dto.getUseStartTls();
        boolean useSsl = dto.getUseSsl();
        int current = ObidosConstants.SMTP_AUTH_NONE;
        if (!useAuthentication)
        {
            current = ObidosConstants.SMTP_AUTH_NONE;
        }
        else
        {
            if (useStartTLS)
            {
                current = ObidosConstants.SMTP_AUTH_STARTTLS;
            }
            else
            {
                current = ObidosConstants.SMTP_AUTH_SSL;
            }
        }
	}

	@Override
	public void showPortChange()
	{
		gwtLog("MMM MMM");
	}


}
