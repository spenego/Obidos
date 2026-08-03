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

package com.spenego.Obidos.client.application.pickitemtype;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineRadio;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRowWithStyle;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class PickItemTypeView extends ViewWithUiHandlers<PickItemTypeUiHandlers> implements PickItemTypePresenter.MyView
{
    interface Binder extends UiBinder<Widget, PickItemTypeView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    InlineRadio asNoteRadio;

    @UiField
    InlineRadio freeFormatRadio;

    @UiField
    InlineRadio useGlobalTemplateRadio;

    @UiField
    InlineRadio usePersonalTemplateRado;

    @UiField
	ObidosMessageRowWithStyle messageRow;


    @UiField
    Button pickButton;

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    Button listButton;
    
    @UiField
    ObidosButtonToolBar buttonToolBarBottom;

    @Inject
    PickItemTypeView(Binder uiBinder)
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
					getUiHandlers().help();
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

    @UiHandler("asNoteRadio")
    void onClickAsNoteRadio(ClickEvent e)
    {
        getUiHandlers().userNoteRadioButtonClicked();

    }

    @UiHandler("freeFormatRadio")
    void onClickFreeFormatRadio(ClickEvent e)
    {
        getUiHandlers().userFreeFormatRadioButtonClicked();
    }

    @UiHandler("usePersonalTemplateRado")
    void onClickusePersonalTemplateRado(ClickEvent e)
    {
        getUiHandlers().userPersonalTemplateRadioButtonClicked();
    }

    @UiHandler("useGlobalTemplateRadio")
    void onClickuseGlobalTemplateRadio(ClickEvent e)
    {
        getUiHandlers().userGlobalTemplateRadioButtonClicked();
    }

    @UiHandler("pickButton")
    void onClickPickButton(ClickEvent e)
    {
        getUiHandlers().navigateToPage();
    }

    @UiHandler("listButton")
    void onClickListButton(ClickEvent e)
    {
        getUiHandlers().navigateToLastPage();
    }

    public InlineRadio getFreeFormatRadio()
    {
        return freeFormatRadio;
    }

    public InlineRadio getUseGlobalTemplateRadio()
    {
        return useGlobalTemplateRadio;
    }

    public InlineRadio getUsePersonalTemplateRado()
    {
        return usePersonalTemplateRado;
    }

    public Button getPickButton()
    {
        return pickButton;
    }

    public InlineRadio getAsNoteRadio()
    {
        return asNoteRadio;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getListButton()
	{
		return listButton;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosMessageRowWithStyle getMessageRow()
	{
		return messageRow;
	}
}