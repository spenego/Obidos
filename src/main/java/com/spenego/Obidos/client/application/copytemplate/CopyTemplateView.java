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

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class CopyTemplateView extends ViewWithUiHandlers<CopyTemplateUiHandlers> implements CopyTemplatePresenter.MyView
{
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph copyTemplateInfoParagraph;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    TextBox templateNameTextBox;

    @UiField
    TextBox newTemplateNameTextBox;

    @UiField
    ObidosMessageRow messageRow;

    @UiField
    Button submitButton;

    @UiField
    ObidosButtonToolBar buttonToolBar;
    
    interface Binder extends UiBinder<Widget, CopyTemplateView>
    {
    }

    @Inject
    CopyTemplateView(Binder uiBinder)
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
        getUiHandlers().copyTemplate();
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Paragraph getCopyTemplateInfoParagraph()
    {
        return copyTemplateInfoParagraph;
    }

    public TextBox getTemplateNameTextBox()
    {
        return templateNameTextBox;
    }

    public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Button getSubmitButton()
    {
        return submitButton;
    }

	public TextBox getNewTemplateNameTextBox()
	{
		return newTemplateNameTextBox;
	}

	public ObidosButtonToolBar getButtonToolBar()
	{
		return buttonToolBar;
	}

        public Button getHelpButton()
        {
                return null;
        }

        public Button getListButton()
        {
                return null;
        }

        public ObidosPanelHeader getPanelHeader()
        {
                        return panelHeader;
        }
}
