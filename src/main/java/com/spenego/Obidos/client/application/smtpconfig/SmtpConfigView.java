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

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.util.ClientUtils;

class SmtpConfigView extends ViewWithUiHandlers<SmtpConfigUiHandlers> implements SmtpConfigPresenter.MyView
{
    interface Binder extends UiBinder<Widget, SmtpConfigView>
    {
    }

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph helpParagraph;

    @UiField
    FormGroup formGroupSmtpServer;

    @UiField
    FormLabel smtpServerFormLabel;

    @UiField
    FormLabel smtpPortLabel;

    @UiField
    FormLabel secureConnectionLabel;

    @UiField
    TextBox smtpServerTextBox;

    @UiField
    ListBox connectionTypeListBox;

    @UiField
    ObidosIntegerTextBox smtpPortTextBox;

    @UiField
    TextBox smtpAuthUserTextBox;

    @UiField
    Input smtpAuthUserPasswordBox;

    @UiField
    Button showHidePasswordButton;

    @UiField
    TextBox testToTextBox;

    @UiField
    TextBox testFromTextBox;

    @UiField
    Button testEmailButton;

    @UiField
    Button saveButton;

    @UiField
    InputGroup inputGroup;

    @UiField
    TextBox subjectTextBox;

    @UiField
    TextBox messgaeTextBox;

    @UiField
    Button resetButton;
    
    @UiField
	ObidosMessageRow messageRow;
    
    @UiField
	ObidosMessageRow messageRowSendEmail;

    @UiField
    ObidosButtonToolBar buttonToolBarBottom;
    
    @UiField
    ObidosButtonToolBar testEmailBar;

    @UiField
    ObidosRowBottom2px authUserRow;

    @UiField
    ObidosRow authPassRow;

    @UiField
    FormLabel smtpAuthUserLabel;
    
    @UiField
    FormLabel smtpAuthUsrPassLabel;
    
    @UiField
    Button deleteButton;
    
    @UiHandler("showHidePasswordButton")
    void onClickShowHidePasswordButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHidePasswordButton, smtpAuthUserPasswordBox);
    }

    /*
    @UiHandler("smtpPortTextBox")
    void onKeyUpSmtpPortTextBox(KeyUpEvent e)
    {
        getUiHandlers().showPortChange();
    }
    */

    @UiHandler("connectionTypeListBox")
    void onClickConnectionTypeListBox(ChangeEvent e)
    {
       getUiHandlers().showConnectionTypeChange();
    }

    @UiHandler("testEmailButton")
    void onClickTestEmailButton(ClickEvent e)
    {
        getUiHandlers().sendTestEmail();
    }

    @UiHandler("resetButton")
    void onClickResetButton(ClickEvent e)
    {
        getUiHandlers().fetchAndResetForm();
    }

    @UiHandler("saveButton")
    void onClickSaveButton(ClickEvent e)
    {
        getUiHandlers().saveSMTPSettings();
    }

    @UiHandler("deleteButton")
    void onClickDeleteButton(ClickEvent e)
    {
    	getUiHandlers().deleteConfig();
    }

    @Inject
    SmtpConfigView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
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

    // getters
    public TextBox getSmtpServerTextBox()
    {
        return smtpServerTextBox;
    }

    public ListBox getConnectionTypeListBox()
    {
        return connectionTypeListBox;
    }



    public ObidosIntegerTextBox getSmtpPortTextBox()
    {
        return smtpPortTextBox;
    }

    public TextBox getSmtpAuthUserTextBox()
    {
        return smtpAuthUserTextBox;
    }


    public Input getSmtpAuthUserPasswordBox()
    {
        return smtpAuthUserPasswordBox;
    }

    public TextBox getTestToTextBox()
    {
        return testToTextBox;
    }


    public TextBox getTestFromTextBox()
    {
        return testFromTextBox;
    }

    @Deprecated
    public FormLabel getFormErrorLabel()
    {
        return null;
    }

    public Button getTestEmailButton()
    {
        return testEmailButton;
    }


    public Button getSaveButton()
    {
        return saveButton;
    }

    public TextBox getSubjectTextBox()
    {
        return subjectTextBox;
    }

    public TextBox getMessgaeTextBox()
    {
        return messgaeTextBox;
    }

    public FormGroup getFormGroupSmtpServer()
    {
        return formGroupSmtpServer;
    }

    public FormLabel getSmtpServerFormLabel()
    {
        return smtpServerFormLabel;
    }

    public FormLabel getSmtpPortLabel()
    {
        return smtpPortLabel;
    }

    public FormLabel getSecureConnectionLabel()
    {
        return secureConnectionLabel;
    }

    public InputGroup getInputGroup()
    {
        return inputGroup;
    }

    public Button getShowHidePasswordButton()
    {
        return showHidePasswordButton;
    }

    public Button getResetButton()
    {
        return resetButton;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Paragraph getHelpParagraph()
	{
		return helpParagraph;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosButtonToolBar getTestEmailBar()
	{
		return testEmailBar;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosRowBottom2px getAuthUserRow()
	{
		return authUserRow;
	}

	public ObidosRow getAuthPassRow()
	{
		return authPassRow;
	}

	public FormLabel getSmtpAuthUserLabel()
	{
		return smtpAuthUserLabel;
	}

	public FormLabel getSmtpAuthUsrPassLabel()
	{
		return smtpAuthUsrPassLabel;
	}

	public ObidosMessageRow getMessageRowSendEmail()
	{
		return messageRowSendEmail;
	}

	public Button getDeleteButton()
	{
		return deleteButton;
	}
}
