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

package com.spenego.Obidos.client.application.copytemplate;

import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class CopyTemplatePresenter extends Presenter<CopyTemplatePresenter.MyView, CopyTemplatePresenter.MyProxy>
        implements CopyTemplateUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
    interface MyView extends View, HasUiHandlers<CopyTemplateUiHandlers>
    {
        public BlockQuote getHelpBlockQuote();
        public Paragraph getCopyTemplateInfoParagraph();
        public ObidosPanelHeader getPanelHeader();
        public TextBox getTemplateNameTextBox();
        public TextBox getNewTemplateNameTextBox();
        public Button getSubmitButton();
        public Button getListButton();
        public Button getHelpButton();
        public ObidosButtonToolBar getButtonToolBar();
        public ObidosMessageRow getMessageRow();
    }

    @NameToken(NameTokens.COPY_TEMPLATE)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<CopyTemplatePresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    CopyTemplatePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        showMessage("");
        getView().getButtonToolBar().adjustButtonsWidth();
    	getView().getTemplateNameTextBox().setValue("");
    	getView().getNewTemplateNameTextBox().setValue("");
        renameTitles();
        resetForm();
        fetchAndPopulateTemplateName();
    }

    private void renameTitles()
    {
        String key = ObidosConstants.TEMPLATE_TYPE;
        String templateType = getTemplateTypeFromURL();
        if (templateType == null)
        {
        	return;
        }
        Button submitButton = getView().getSubmitButton();
        Button listButton = getView().getListButton();

        if (templateType.equals(ObidosConstants.PERSONAL))
        {
        	getView().getPanelHeader().setHeadingText(glang.copyPersonalTemplate());
//            submitButton.setText(ObidosMessages.LANG.copyTemplateTitle());
//           listButton.setText(ObidosMessages.LANG.listPersonalTemplates());
//            getView().getCopyTemplateInfoParagraph().setHTML(ObidosMessages.LANG.copyPersonalTemplateHelp());
        }
        else if (templateType.equals(ObidosConstants.GLOBAL))
        {
        	getView().getPanelHeader().setHeadingText(glang.copyGlobalTemplate());
//            submitButton.setText(ObidosMessages.LANG.copyToPersonalTemplate());
//            listButton.setText(ObidosMessages.LANG.listGlobalTemplates());
//            getView().getCopyTemplateInfoParagraph().setHTML(ObidosMessages.LANG.copyGlobalTemplateToPersonalTemplateHelp());
        }

    }

    private String getTemplateTypeFromURL()
    {
        String key = ObidosConstants.TEMPLATE_TYPE;
        String templateType = null;
        try
        {
            templateType = ClientUtils.getParameterFromUrl(placeManager, key);
            return templateType;
        } catch (ParamNotFoundException e)
        {
            showErrorMessage(ObidosMessages.LANG.couldNoteGetTemplateTypeFromURL());
            return null;
        }
    }

    private Long getTemplateIdFromUrl()
    {
         Long templateId = null;
        try
        {
            String key = ObidosConstants.TEMPLATE_ID;
            templateId = ClientUtils.getIdFromUrl(placeManager, key);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            showErrorMessage(ObidosMessages.LANG.couldNoteGetTemplateIdFromUrl());
            return null;
        }
        return templateId;
    }

    private void fetchAndPopulateTemplateName()
    {
        Long templateId = getTemplateIdFromUrl();
        if (templateId == null)
        {
            return;
        }
        GwtAsyncWrapper<UserDefinedTypeDTO> callback = new GwtAsyncWrapper<UserDefinedTypeDTO>(this)
        {

            @Override
            public void uponSuccess(UserDefinedTypeDTO dto)
            {
                getView().getTemplateNameTextBox().setValue(dto.getName());
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(ObidosMessages.LANG.couldNotFetchTemplate());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().getUserDefinedType(authCreds, templateId, callback);
    }

    @Override
    public void copyTemplate()
    {
        Long templateId = getTemplateIdFromUrl();
        if (templateId == null)
        {
            return;
        }
        String templateType = getTemplateTypeFromURL();
        if (templateType == null)
        {
        	return;
        }
        String templateName = getView().getTemplateNameTextBox().getValue();
        String newTemplateName = getView().getNewTemplateNameTextBox().getValue();
		if (newTemplateName == null || newTemplateName.length() == 0)
		{
			showErrorMessage(ObidosMessages.LANG.specifyNewTemplateName());
			return;
		}

		// caught by Sonarqube
        if (templateType != null && templateType.equals(ObidosConstants.PERSONAL))
        {
			if (templateName.equals(newTemplateName))
			{
				showErrorMessage(ObidosMessages.LANG.templateNamesAreTheSame());
				return;
			}
        }

        GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
        {

            @Override
            public void uponSuccess(Long result)
            {
                showMessage("Template copied successfully to: " + newTemplateName);
                // go to list Personal template
                showListTemplatesPage(false);
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(ObidosMessages.LANG.failedToCopyTemplate() + ": "  + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().duplicateType(authCreds, templateId, newTemplateName, callback);
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    public void resetForm()
    {
    	getView().getNewTemplateNameTextBox().setValue("");
    }

    @Override
    public void listTemplates()
    {
        String listButtonTitle = getView().getListButton().getText();
        if (listButtonTitle.equals(ObidosMessages.LANG.listGlobalTemplates()))
        {
            showListTemplatesPage(true);
        }
        else
        {
            showListTemplatesPage(false);
        }
    }
    private void showListTemplatesPage(boolean globalTemplate)
    {
        if (globalTemplate)
        {
            ClientUtils.showPage(placeManager, NameTokens.LIST_GLOBAL_TEMPLATES);
        }
        else
        {
            String nameToken = NameTokens.LIST_TEMPLATES;
            Map<String,String> with = new HashMap<>();
            String key = ObidosConstants.TEMPLATE_TYPE;
            String value = ObidosConstants.PERSONAL;
            with.put(key, value);
            ClientUtils.showPage(placeManager, nameToken, with);
        }
    }

        @Override
        public void help()
        {
                ClientUtils.showHelp(getView().getHelpBlockQuote());
        }

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}
}
