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

package com.spenego.Obidos.client.application.listitems;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.LimitedItemDTO;

class ListItemsView extends ViewWithUiHandlers<ListItemsUiHandlers> implements ListItemsPresenter.MyView
{
	interface Binder extends UiBinder<Widget, ListItemsView>
	{
	}

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	TextBox containerNameTextBox;

	@UiField
	TextBox searchTextBox;

	@UiField
	Button deleteButton;

	@UiField
	Button clearButton;

	@UiField
	Button newItemButton;

	@UiField(provided = true)
	DataGrid<LimitedItemDTO> dataGrid = new DataGrid<LimitedItemDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

	@UiField(provided = true)
	SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

	@UiField
	ObidosMessageRow messageRow;

	@Inject
	ListItemsView(Binder uiBinder)
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

	@UiHandler("newItemButton")
	void onclickNewItemButton(ClickEvent e)
	{
		getUiHandlers().addNewItem();
	}

	@UiHandler("deleteButton")
	void onclickDeleteButton(ClickEvent e)
	{
		getUiHandlers().deleteItems();
	}

	@UiHandler("clearButton")
	void onclickClearButton(ClickEvent e)
	{
		getUiHandlers().clearSelections();
	}

	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().searchItemName();
	}

	@UiHandler("searchTextBox")
	void onPressEnterserachTextBox(KeyDownEvent e)
	{
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().searchItemName();
		}
	}

    @UiHandler("sortByDate")
    void onSelectsortByDateAnchorList(ClickEvent e)
    {   
        getUiHandlers().sortItemByDate();
    }

    @UiHandler("sortReverseByDate")
    void onSelectsortReverseByDateAnchorList(ClickEvent e)
    {   
        getUiHandlers().sortItemReverseByDate();
    }

    @UiHandler("sortByAZ")
    void onSelectsortByAZAnchorList(ClickEvent e)
    {
        getUiHandlers().sortItemByAZ();
    }
   
    @UiHandler("sortByZA")
    void onclickSortByZA (ClickEvent e)
    {
        getUiHandlers().sortItemByZA();
    }

	public DataGrid<LimitedItemDTO> getDataGrid()
	{
		return dataGrid;
	}

	public SimplePager getPager()
	{
		return pager;
	}

	public TextBox getContainerNameTextBox()
	{
		return containerNameTextBox;
	}

	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	@Override
	public Button getHelpButton()
	{
		return null;
	}

	@Override
	public Button getListButton()
	{
		return null;
	}

	@Deprecated
	public FormLabel getFormErrorLabel()
	{
		return null;
	}

	public Button getDeleteButton()
	{
		return deleteButton;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public Button getNewItemButton()
	{
		return newItemButton;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

}
