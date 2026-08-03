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
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;

class EditNotificationTemplatesView
        extends ViewWithUiHandlers<EditNotificationTemplatesUiHandlers>
        implements EditNotificationTemplatesPresenter.MyView
{
	@UiField
	ObidosRowBottom2px buttonRow;

	@UiField
	ObidosRowBottom2px buttonHelpRow;
	
    interface Binder extends UiBinder<Widget,EditNotificationTemplatesView>
    {
    }

    @Inject
    EditNotificationTemplatesView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        // Use pheader snippet instead of typing 
        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().showHideHelp();
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

    @UiHandler("submitButton")
    void onClickSubmitButton(ClickEvent e)
    {
        getUiHandlers().updateNotificationTemplate();
    }
    
    @UiHandler("resetButton")
    void onclickResetButton (ClickEvent e)
	{
    	getUiHandlers().reset();
	}
    
    @UiHandler("emailButton")
    void onclickEmailButton (ClickEvent e)
	{
    	getUiHandlers().sendTestEmail();
	}
    
    @UiField
    TextBox productTextBox;

    @UiField
    TextBox productUrlTextBox;

    public TextBox getProductTextBox()
    {
        return productTextBox;
    }

    public TextBox getProductUrlTextBox()
    {
        return productUrlTextBox;
    }

    // Automatically generated from src/main/java/com/spenego/Obidos/client/application/editnotificationtemplates/EditNotificationTemplatesView.ui.xml by mk_uifield.rb
   // spgdev@spenego.com 2018-06-19 13:25:08 +0200, Copenhagen, Denmark
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph helpParagraph;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    FormGroup editNotificationTemplateGroup;

    @UiField
    TextBox subjectTextBox;

    @UiField
    FormLabel helloLabel;

    @UiField
    FormLabel warningLabel;

    @UiField
    FlowPanel helloFlowPanel;

    @UiField
    FlowPanel warningFlowPanel;

    @UiField
    TextBox helloTextBox;

    @UiField
    TextArea htmlTextArea;

    @UiField
    TextArea warningTextArea;

    @UiField
    FormLabel buttonLabel;

    @UiField
    FlowPanel buttonFlowPanel;

    @UiField
    TextBox buttonTitleTextBox;

    @UiField
    FormLabel helpLabel;

    @UiField
    FlowPanel helpFlowPanel;

    @UiField
    TextArea alternateTextArea;

    @UiField
    TextArea textTextArea;

    @UiField
    TextArea contactTextArea;

    @UiField
    TextArea footerTextArea;

    @UiField
	ObidosMessageRow messageRow;

    @UiField
    Button submitButton;

    @UiField
    Button resetButton;

    @UiField
    Button emailButton;

    @UiField
    ObidosButtonToolBar buttonToolBarBottom;

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    public FormGroup getEditNotificationTemplateGroup()
    {
        return editNotificationTemplateGroup;
    }

    public TextBox getSubjectTextBox()
    {
        return subjectTextBox;
    }
    public TextBox getHelloTextBox()
    {
        return helloTextBox;
    }

    public TextArea getHtmlTextArea()
    {
        return htmlTextArea;
    }

    public TextArea getWarningTextArea()
    {
        return warningTextArea;
    }

    public TextBox getButtonTitleTextBox()
    {
        return buttonTitleTextBox;
    }

    public TextArea getAlternateTextArea()
    {
        return alternateTextArea;
    }

    public TextArea getTextTextArea()
    {
        return textTextArea;
    }

    public TextArea getContactTextArea()
    {
        return contactTextArea;
    }

    public TextArea getFooterTextArea()
    {
        return footerTextArea;
    }

    public Button getSubmitButton()
    {
        return submitButton;
    }

    public Button getResetButton()
    {
        return resetButton;
    }

    public FormLabel getButtonLabel()
    {
        return buttonLabel;
    }

    public FlowPanel getButtonFlowPanel()
    {
        return buttonFlowPanel;
    }

    public FormLabel getHelloLabel()
    {
        return helloLabel;
    }

    public FormLabel getWarningLabel()
    {
        return warningLabel;
    }

    public FlowPanel getWarningFlowPanel()
    {
        return warningFlowPanel;
    }

    public FlowPanel getHelloFlowPanel()
    {
        return helloFlowPanel;
    }

    public FormLabel getHelpLabel()
    {
        return helpLabel;
    }

    public FlowPanel getHelpFlowPanel()
    {
        return helpFlowPanel;
    }

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosRowBottom2px getButtonRow()
	{
		return buttonRow;
	}

	public ObidosRowBottom2px getButtonHelpRow()
	{
		return buttonHelpRow;
	}

}
