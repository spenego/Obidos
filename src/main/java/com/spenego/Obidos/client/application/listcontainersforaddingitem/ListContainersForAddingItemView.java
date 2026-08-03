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

package com.spenego.Obidos.client.application.listcontainersforaddingitem;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.core.shared.GWT;
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
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.ContainerDTO;

class ListContainersForAddingItemView extends ViewWithUiHandlers<ListContainersForAddingItemUiHandlers>
		implements ListContainersForAddingItemPresenter.MyView
{
	interface Binder extends UiBinder<Widget, ListContainersForAddingItemView>
	{
	}

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	TextBox searchTextBox;

	@UiField
	Button searchButton;

	@UiField
	ObidosMessageRow messageRow;

	@UiField(provided = true)
	DataGrid<ContainerDTO> dataGrid = new DataGrid<ContainerDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

	@UiField(provided = true)
	SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

	@Inject
	ListContainersForAddingItemView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		panelHeader.getHelpButton().addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				getUiHandlers().help();
			}
		});

		Button backButton = panelHeader.getBackButton();
		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					backButton.setText(ObidosMessages.LANG.listItems());
					getUiHandlers().listMyItems();
				}
			});
		}
	}

	@UiHandler("searchTextBox")
	void onPreseeEnterSearchTextBox(KeyDownEvent e)
	{
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().searchContainerName();
		}
	}

	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().searchContainerName();
	}
    @UiHandler("sortByDate")
    void onSelectsortByDateAnchorList(ClickEvent e)
    {
        getUiHandlers().sortByDate();
    }

    @UiHandler("sortReverseByDate")
    void onSelectsortReverseByDateAnchorList(ClickEvent e)
    {
        getUiHandlers().sortReverseByDate();
    }

    @UiHandler("sortByAZ")
    void onSelectsortByAZAnchorList(ClickEvent e)
    {
        getUiHandlers().sortByAZ();
    }
   
    @UiHandler("sortByZA")
    void onclickSortByZA (ClickEvent e)
    {
        getUiHandlers().sortByZA();
    }

	public DataGrid<ContainerDTO> getDataGrid()
	{
		return dataGrid;
	}

	public SimplePager getPager()
	{
		return pager;
	}

	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
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
