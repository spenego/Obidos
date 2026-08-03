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

package com.spenego.Obidos.client.application.createtemplate;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTemplateRow;

class CreateTemplateView extends ViewWithUiHandlers<CreateTemplateUiHandlers>
        implements CreateTemplatePresenter.MyView
{
    interface Binder extends UiBinder<Widget, CreateTemplateView>
    {
    }

    @UiField
    Paragraph paragraph;

    @UiField
    FormGroup addFormGroup;

    @UiField
    HTMLPanel htmlPanel;

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    TextBox tempalteNameTextBox;

    @UiField
    Button saveTemplateButton;

    @UiField
	ObidosButtonToolBar bottomToolBar;
    
    @UiField
    ObidosTemplateRow firstRow;
    
    @UiField
    ObidosMessageRow messageRow;
    
    @UiField
    ObidosPanelHeader panelHeader;    

    @UiField
    ToggleSwitch attachDocumentToggleSwitch;
 
    @UiField
    ToggleSwitch add2FAInfoToggleSwitch;

   	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;

	@UiField
	Label licenseLabelDoc;

	@UiField
	Label licenseLabel2FA;

	@Inject
	CreateTemplateView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		Button helpButton = panelHeader.getHelpButton();
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
		Button backButton = panelHeader.getBackButton();
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

    @UiHandler("tempalteNameTextBox")
    void onKeyUpEventtempalteNameTextBox(KeyDownEvent e)
    {
        getUiHandlers().textBoxKeyUpHandler();
    }

    @UiHandler("saveTemplateButton")
    void onClicksubmitButton(ClickEvent e)
    {
        getUiHandlers().saveTemplate();
    }

    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }

    public Paragraph getParagraph()
    {
        return paragraph;
    }

    public FormGroup getAddFormGroup()
    {
        return addFormGroup;
    }

    public HTMLPanel getHtmlPanel()
    {
        return htmlPanel;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public TextBox getTempalteNameTextBox()
    {
        return tempalteNameTextBox;
    }

    public Button getSaveTemplateButton()
    {
        return saveTemplateButton;
    }

	public ObidosButtonToolBar getBottomToolBar()
	{
		return bottomToolBar;
	}

	public ObidosTemplateRow getFirstRow()
	{
		return firstRow;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
			return panelHeader;
	}

	public ToggleSwitch getAttachDocumentToggleSwitch()
	{
		return attachDocumentToggleSwitch;
	}

	public ToggleSwitch getAdd2FAInfoToggleSwitch()
	{
		return add2FAInfoToggleSwitch;
	}

	public ObidosRowBottom2px getLanguageRow()
	{
		return languageRow;
	}

	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}

	public Label getLicenseLabelDoc()
	{
		return licenseLabelDoc;
	}

	public Label getLicenseLabel2FA()
	{
		return licenseLabel2FA;
	}
}
