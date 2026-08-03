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

package com.spenego.Obidos.client.application.edittemplate;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;

class EditTemplateView extends ViewWithUiHandlers<EditTemplateUiHandlers>
        implements EditTemplatePresenter.MyView
{
    interface Binder extends UiBinder<Widget, EditTemplateView>
    {
    }
    
    @UiField
    FormGroup formGroup;

    @UiField
    HTMLPanel htmlPanel;

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    Button submitButton;

    @UiField
    Button resetButton;

    @UiField
    FormGroup addFieldFormGroup;
    
    @UiField
    Row addFieldGroupRow;
    
    @UiField
    FlowPanel addFieldFp;

    @UiField
    FlowPanel addDisplayOrderFp;

    @UiField
    FlowPanel addButtonFp;

    @UiField
    TextBox addFieldTextBox;

    @UiField
    ObidosIntegerTextBox addFieldDisplayOrderTextBox;

    @UiField
    TextBox tempalteNameTextBox;

    @UiField
    ObidosButtonToolBar bottomToolBar;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
    ToggleSwitch attachDocumentToggleSwitch;

    @UiField
    ObidosRowBottom2px uploadRow;
	
    @UiField
    ObidosRowBottom2px twoFARow;

    @UiField
    ToggleSwitch add2FAToggleSwitch;

	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;

    @Inject
    EditTemplateView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
        Button backButton = panelHeader.getBackButton();

        helpButton.addClickHandler(new ClickHandler()
        {
			@Override
			public void onClick(ClickEvent event)
			{
					getUiHandlers().help();

			}
        });

        backButton.addClickHandler(new ClickHandler()
        {
			@Override
			public void onClick(ClickEvent event)
			{
					getUiHandlers().back();
			}
        });
    }

    @UiHandler("submitButton")
    void onClickSubmitButton(ClickEvent e)
    {
        getUiHandlers().updateTemplate();

    }

    @UiHandler("addFieldButton")
    void onCLickAddFieldButton(ClickEvent e)
    {
        getUiHandlers().addFields();
    }

    @UiHandler("addFieldTextBox")
    void onEnterAddFieldTextBox(KeyDownEvent e)
    {
        if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().addFields();
        }
    }

    @UiHandler("resetButton")
    void onClickResetButton(ClickEvent e)
    {
        getUiHandlers().resetForm();
    }

    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }

    public FormGroup getFormGroup()
    {
        return formGroup;
    }

    public HTMLPanel getHtmlPanel()
    {
        return htmlPanel;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Button getSubmitButton()
    {
        return submitButton;
    }

    public Button getResetButton()
    {
        return resetButton;
    }

    public FormGroup getAddFieldFormGroup()
    {
        return addFieldFormGroup;
    }

    public TextBox getAddFieldTextBox()
    {
        return addFieldTextBox;
    }

    public FlowPanel getAddFieldFp()
    {
        return addFieldFp;
    }

    public FlowPanel getAddDisplayOrderFp()
    {
        return addDisplayOrderFp;
    }

    public FlowPanel getAddButtonFp()
    {
        return addButtonFp;
    }


    public ObidosIntegerTextBox getAddFieldDisplayOrderTextBox()
    {
        return addFieldDisplayOrderTextBox;
    }

    public TextBox getTempalteNameTextBox()
    {
        return tempalteNameTextBox;
    }

	public Row getAddFieldGroupRow()
	{
		return addFieldGroupRow;
	}

        public Button getHelpButton()
        {
                return null;
        }

	public ObidosButtonToolBar getBottomToolBar()
	{
		return bottomToolBar;
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

	public ObidosRowBottom2px getUploadRow()
	{
		return uploadRow;
	}

	public ObidosRowBottom2px getTwoFARow()
	{
		return twoFARow;
	}

	public ToggleSwitch getAdd2FAToggleSwitch()
	{
		return add2FAToggleSwitch;
	}

	public ObidosRowBottom2px getLanguageRow()
	{
		return languageRow;
	}

	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}

}

