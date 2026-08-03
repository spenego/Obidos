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

package com.spenego.Obidos.client.application.editnotificationtemplates;

import java.util.Date;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
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
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.client.rpc.EmailMessageConfigService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.SendNotificationTestEmailModalData;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class EditNotificationTemplatesPresenter extends Presenter<EditNotificationTemplatesPresenter.MyView,EditNotificationTemplatesPresenter.MyProxy>
implements EditNotificationTemplatesUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private NotificationTemplateJSONDTO sNotificationTemplateJSONDTO = null;
	private HandlerRegistration sSendmailButtonClickHandler = null;
	
    interface MyView
    extends View,HasUiHandlers<EditNotificationTemplatesUiHandlers>
    {
        // Automatically generated from src/main/java/com/spenego/Obidos/client/application/editnotificationtemplates/EditNotificationTemplatesView.ui.xml by mk_uifield.rb
        // spgdev@spenego.com 2018-06-19 13:25:08 +0200, Copenhagen, Denmark
        public BlockQuote getHelpBlockQuote();
        public Paragraph getHelpParagraph();
        public FormGroup getEditNotificationTemplateGroup();
        public TextBox getProductTextBox();
        public TextBox getProductUrlTextBox();
        public TextBox getSubjectTextBox();
        public FormLabel getHelloLabel();
        public FlowPanel getHelloFlowPanel();
        public TextBox getHelloTextBox();
        public TextArea getHtmlTextArea();
        public TextArea getWarningTextArea();
        public FormLabel getButtonLabel();
        public FlowPanel getButtonFlowPanel();
        public FlowPanel getWarningFlowPanel();
        public FormLabel getWarningLabel();
        public TextBox getButtonTitleTextBox();
        public FormLabel getHelpLabel();
        public FlowPanel getHelpFlowPanel();
        public TextArea getAlternateTextArea();
        public TextArea getTextTextArea();
        public TextArea getContactTextArea();
        public TextArea getFooterTextArea();
        public ObidosMessageRow getMessageRow();
        public Button getSubmitButton();
        public Button getResetButton();
        public ObidosPanelHeader getPanelHeader();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosRowBottom2px getButtonRow();
        public ObidosRowBottom2px getButtonHelpRow();


    }

    @NameToken(NameTokens.EDIT_NOTIFICATION_TEMPLATES)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<EditNotificationTemplatesPresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;

    @Inject
    EditNotificationTemplatesPresenter(EventBus eventBus,MyView view,
            MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus,view,proxy,ApplicationPresenter.SLOT_MAIN);

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
        showMessage("");
        updatePanelHeaderColor();
       	ObidosButtonToolBar tb = getView().getButtonToolBarBottom();
       	tb.adjustButtonsWidth(134); //otherwise button shrinks few pixels with every back button click
       	gwtLog("Send mail button size: "+ tb.getMaxWidth());
        resetForm();
        updateForm();
    }
    private void updatePanelHeaderColor()
    {
    	ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
    }

    private void resetForm()
    {
    	if (sSendmailButtonClickHandler != null)
    	{
    		sSendmailButtonClickHandler.removeHandler();
    		sSendmailButtonClickHandler = null;
    	}
        getView().getProductTextBox().setValue("");
        getView().getProductUrlTextBox().setValue("");
        getView().getSubjectTextBox().setValue("");
        getView().getHelloTextBox().setValue("");
        getView().getHtmlTextArea().setText("");
        getView().getWarningTextArea().setText("");
        getView().getButtonTitleTextBox().setValue("");
        getView().getAlternateTextArea().setText("");
        getView().getTextTextArea().setText("");
        getView().getContactTextArea().setText("");
        getView().getFooterTextArea().setText("");
    }

    private Long getNotificationTemplateTypeFromURL()
    {
        Long templateType = null;

        try
        {
            templateType = ClientUtils.getIdFromUrl(placeManager,ObidosConstants.TYPE);
            return templateType;
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            showErrorMessage(ObidosMessages.LANG.couldNotGetNotificationTemplateType());
            return null;
        }
    }

    private void updatePanelHeading(String text)
    {
        getView().getPanelHeader().getHeading().setText(text);
    }

    private void updateForm()
    {
        ObidosMessages lang = ObidosMessages.LANG;

        Long templateType = getNotificationTemplateTypeFromURL();
        if (templateType == null)
        {
            return;
        }
        setFetchedNotificationTemplateJSONDTO(null);

        showHello(true);
        showButton(true);
        showWarning(true);
        switch(templateType.intValue())
        {
            case (int) ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE:
            {
                gwtLog("Show help");
                getView().getHelpParagraph().setHTML(lang.editAccountCreatedNotificationTemplateHelp());
                updatePanelHeading(lang.editAccountCreatedNotificationTemplatePanelHeader());
                showWarning(false);
                break;
            }

            case (int) ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE:
            {
                updatePanelHeading(lang.editItemSharedNotificationTemplatePanelHeader());
                showButton(false);
                showWarning(false);
                break;
            }

            case (int) ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE:
            {
                updatePanelHeading(lang.editItemRevokedNotificationTemplatePanelHeader());
                showButton(false);
                showWarning(false);
                break;
            }

            case (int) ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE:
            {
                updatePanelHeading(lang.editPasswordResetNotificationTemplatePanelHeader());
                showWarning(false);
                break;
            }

            case (int) ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
                updatePanelHeading(lang.editPasswordResetWarningNotificationTemplatePanelHeader());
                showHello(false);
                showButton(false);
                showWarning(false);
                break;
            }

            case (int) ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE:
            {
                updatePanelHeading(lang.editPassphraseResetNotificationTemplatePanelHeader());
                break;
            }

            case (int) ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
                updatePanelHeading(lang.editPassphraseResetWarningNotificationTemplatePanelHeader());
                showWarning(false);
                break;
            }
            
            case (int) ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE:
            {
                updatePanelHeading(lang.editContainerSharedNotificationTemplatePanelHeader());
                showButton(false);
                showWarning(false);
            	break;
            }
            case (int) ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE:
            {
                updatePanelHeading(lang.editContainerRevokedNotificationTemplatePanelHeader());
                showButton(false);
                showWarning(false);
            	break;
            }
           
            default:
            {
                showErrorMessage(lang.unknownNotificatoinTemplateType());
                return;
            }
        }
        fetchAndPopulateForm(templateType.intValue());
    }


    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(),message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    	
    }

    @Override
    public void showHideHelp()
    {
    	ClientUtils.showHelp(getView().getHelpBlockQuote());
    }
    
    private void showButton(boolean visible)
    {
//        getView().getButtonLabel().setVisible(visible);
 //       getView().getButtonFlowPanel().setVisible(visible);
    	getView().getButtonRow().setVisible(visible);
    	getView().getButtonHelpRow().setVisible(visible);
    }


    private void showHello(boolean visible)
    {
        getView().getHelloLabel().setVisible(visible);
        getView().getHelloFlowPanel().setVisible(visible);
    }


    private void showWarning(boolean visible)
    {
        getView().getWarningLabel().setVisible(visible);
        getView().getWarningFlowPanel().setVisible(visible);
        getView().getWarningTextArea().setVisible(visible);
    }

    private void populateForm(NotificationTemplateJSONDTO nt)
    {
    	gwtLog(">>>>>>>>>>>>>>> Product name: " + nt.getProduct_name());
    	
        getView().getProductTextBox().setValue(nt.getProduct_name());
        getView().getProductUrlTextBox().setValue(nt.getProduct_url());
        getView().getSubjectTextBox().setValue(nt.getSubject());
        getView().getHelloTextBox().setValue(nt.getHello());
        getView().getHtmlTextArea().setText(nt.getHtml_message());
        getView().getWarningTextArea().setText(nt.getWarning_message());
        getView().getButtonTitleTextBox().setValue(nt.getButton_title());
        getView().getAlternateTextArea().setText(nt.getButton_trouble());
        getView().getTextTextArea().setText(nt.getText_message());
        getView().getContactTextArea().setText(nt.getContact());
        getView().getFooterTextArea().setText(nt.getFooter());
    }

    // it will come from database, for testing use the JSON file now
    private void fetchAndPopulateForm(int templateType)
    {
        GwtAsyncWrapper<NotificationTemplateJSONDTO> callabck = new GwtAsyncWrapper<NotificationTemplateJSONDTO>(this)
        {

            @Override
            public void uponSuccess(NotificationTemplateJSONDTO nt)
            {
                // save so that we can reuse it while updating
            	setFetchedNotificationTemplateJSONDTO(nt);
                populateForm(nt);
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not obtain notifiation template: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        gwtLog("Template type: " + templateType);
        switch(templateType)
        {
            case (int) ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().getAccountCreatedNotificationTemplateJSONDTO(authCreds, callabck);
                break;
            }

            case (int) ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().getItemSharedNotificationTemplateJSONDTO(authCreds, callabck);
                break;
            }

            case (int) ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().getItemRevokedNotificationTemplateJSONDTO(authCreds, callabck);
                break;
            }


            case (int) ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().getPasswordResetNotificationTemplateJSONDTO(authCreds, callabck);
                break;
            }

            case (int) ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().getPasswordResetWarningNotificationTemplateJSONDTO(authCreds, callabck);
                break;
            }
            case (int) ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().getPassphraseResetNotificationTemplateJSONDTO(authCreds, callabck);
            	break;
            }
            case (int) ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().getPassphraseResetWarningNotificationTemplateJSONDTO(authCreds, callabck);
                break;
            }
            
            case (int) ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().getContainerSharedNotificationTemplateJSONDTO(authCreds, callabck);
            	break;
            }

            case (int) ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().getContainerRevokedNotificationTemplateJSONDTO(authCreds, callabck);
            	break;
            }
           
            default:
            {
            	showErrorMessage("Unknown template type: " + templateType);
                return;
            }
        }
    }

    private String getTextBoxValue(TextBox tbox)
    {
        String val = tbox.getValue();
        if (val == null || val.length() == 0)
        {
            return "";
        }
        return val;
    }

    private String getTextAreaValue(TextArea tArea)
    {
        String val = tArea.getText();
        if (val == null || val.length() == 0)
        {
            return "";
        }
        return val;

    }
    
    private void setTextAreaStyle(TextArea textBox)
    {
    	textBox.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        textBox.getElement().getStyle().setWidth(1000, Style.Unit.PX);
        textBox.getElement().getStyle().setHeight(500, Style.Unit.PX);
        textBox.getElement().getStyle().setDisplay(Style.Display.INLINE);
        textBox.getElement().getStyle().setTop(50, Style.Unit.PX);
        textBox.getElement().getStyle().setBackgroundColor("#664477");
        textBox.setEnabled(true);
    }
    
    
    private NotificationTemplateJSONDTO createJSONDTOFromForm()
    {
       	NotificationTemplateJSONDTO dto = getFetchedNotificationTemplateJSONDTO();
       	if (dto.getId() == null)
       	{
       		showErrorMessage("Could not determine Notification Template type");
       		return null;
       	}
       	gwtLog("Template Id: " + dto.getId());
       	
        NotificationTemplateJSONDTO nt = new NotificationTemplateJSONDTO();
        nt.setId(dto.getId());

        String val = getTextBoxValue(getView().getProductTextBox());
        nt.setProduct_name(val);

        val = getTextBoxValue(getView().getProductUrlTextBox());
        nt.setProduct_url(val);

        val = getTextBoxValue(getView().getSubjectTextBox());
        nt.setSubject(val);

        val = getTextBoxValue(getView().getHelloTextBox());
        nt.setHello(val);

        val = getTextAreaValue(getView().getHtmlTextArea());
        nt.setHtml_message(val);

        val = getTextAreaValue(getView().getWarningTextArea());
        nt.setWarning_message(val);

        val = getTextBoxValue(getView().getButtonTitleTextBox());
        nt.setButton_title(val);

        val = getTextAreaValue(getView().getAlternateTextArea());
        nt.setButton_trouble(val);


        val = getTextAreaValue(getView().getTextTextArea());
        nt.setText_message(val);

        val = getTextAreaValue(getView().getContactTextArea());
        gwtLog("Contact: " + val);
        nt.setContact(val);

        val = getTextAreaValue(getView().getFooterTextArea());
        nt.setFooter(val);

        return nt;
    }

    private String getTestEmailSubject(final int templateType)
    {
        switch(templateType)
        {
            case (int) ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE:
            {
            	return glang.testAccountCreatedSubject();
            }

            case (int) ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE:
            {
            	return glang.testItemSharedSubject();
            }

            case (int) ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE:
            {
            	return glang.testItemRevokedSubject();
            }

            case (int) ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE:
            {
            	return glang.testContainerSharedSubject();
            }

            case (int) ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE:
            {
            	return glang.testContainerRevokedSubject();
            }

            case (int) ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE:
            {
            	return glang.testPasswordResetRequestSubject();
            }

            /*
            case (int) ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
            	return "TODO";
            }
            */

            case (int) ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE:
            {
            	return glang.testPassphraseResetRequestSubject();
            }
            /*
            case (int) ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
            	return "TODO";
            }
            */

            default:
            {
                return glang.unknown();
            }
        }
    }


    @Override
    public void updateNotificationTemplate()
    {
    	NotificationTemplateJSONDTO nt = createJSONDTOFromForm();
    	if (nt == null)
    	{
    		return;
    	}

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                showMessage(glang.notificationTemplateUpdatedAt(new Date().toString()));
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(glang.couldNotUpdateNotificationTemplate(caught.getMessage()));
            }

        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        int templateType = nt.getId().intValue();
        switch(templateType)
        {
            case (int) ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().updateAccountCreatedNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            case (int) ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().updateItemSharedNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            case (int) ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().updateItemRevokedNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            case (int) ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().updateContainerSharedNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            case (int) ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE:
            {
                EmailMessageConfigService.Utility.getInstance().updateContainerRevokedNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            case (int) ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().updatePasswordResetNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            case (int) ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().updatePasswordResetWarningNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }
            case (int) ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().updatePassphraseResetNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }
            case (int) ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE:
            {
                EmailMessageConfigService.Utility.getInstance().updatePassphraseResetWarningNotificationTemplateJSONDTO(authCreds, nt, callback);
                break;
            }

            default:
            {
            	showErrorMessage("No method defined for template type: " + templateType);
                return;
            }
        }
    }

	@Override
	public void reset()
	{
		showMessage(null);
		updateForm();
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
		
	}
	
	private NotificationTemplateJSONDTO getFetchedNotificationTemplateJSONDTO()
	{
		return sNotificationTemplateJSONDTO;
	}
	
	private void setFetchedNotificationTemplateJSONDTO(NotificationTemplateJSONDTO dto)
	{
		sNotificationTemplateJSONDTO = dto;
	}
	
	private void sendTestEmailReal(final SendNotificationTestEmailModalData md)
	{
    	NotificationTemplateJSONDTO jsonDTO = createJSONDTOFromForm();
    	if (jsonDTO == null)
    	{
    		return;
    	}
    	String to = md.getToTextBox().getValue();
    	if (to == null || to.length() == 0)
    	{
    		showErrorMessage("Please specify To email address");
    		return;
    	}
    	String from = md.getFromTextBox().getValue();
    	String subject = md.getSubjectTextBox().getValue();
    			
    	SmtpEnvelope envelope = new SmtpEnvelope();
    	envelope.setTos(to);
    	envelope.setFrom(from);
    	envelope.setSubject(subject);

    	gwtLog("TO: " + to);

    	GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				String d = new Date().toString();
				showMessage("Test mail is sent on " + d);
				md.getModal().hide();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not send test email: "+ caught.getMessage());
			}
		};
		Long templateType = getNotificationTemplateTypeFromURL();
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		AdminConfigService.Utility.getInstance().sendNotificationTemplateTestEmail(authCreds, envelope, templateType, callback);
	}

	public void sendTestEmail()
	{
    	if (currentUser == null)
    	{
    		showErrorMessage("Could not determine current logged in user");
    		return;
    	}
		SendNotificationTestEmailModalData md = ApplicationPresenter.getNotificationTestEmailModalData();
		Button sendMailButton = md.getSendMailButton();
		if (sSendmailButtonClickHandler == null)
		{
			sSendmailButtonClickHandler = sendMailButton.addClickHandler(new ClickHandler()
			{
				
				@Override
				public void onClick(ClickEvent event)
				{
					gwtLog("Send mail......");
					sendTestEmailReal(md);
				}
			});
		}
		else
		{
			gwtLog("Click handler is already created.....");
	//		sendTestEmailReal(md);
		}
		
       	NotificationTemplateJSONDTO ndto = getFetchedNotificationTemplateJSONDTO();
       	if (ndto == null)
       	{
       		gwtLog("JSON DTO is null");
       		return;
       	}
       	String subject = getTestEmailSubject(ndto.getId().intValue());
    	UserDTO userDTO = currentUser.getUserDTO();
    	md.getSubjectTextBox().setValue(subject);
		md.getToTextBox().setValue(userDTO.getEmail1());
		md.getFromTextBox().setValue(userDTO.getEmail1());
		md.getUrlTextBox().setValue("http://example.com/");
		md.getModal().show();
				
		
	}
}
