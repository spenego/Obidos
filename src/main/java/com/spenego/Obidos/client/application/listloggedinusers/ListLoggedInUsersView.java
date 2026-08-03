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

package com.spenego.Obidos.client.application.listloggedinusers;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
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
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

class ListLoggedInUsersView extends ViewWithUiHandlers<ListLoggedInUsersUiHandlers>
		implements ListLoggedInUsersPresenter.MyView
{
	interface Binder extends UiBinder<Widget, ListLoggedInUsersView>
	{
	}

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	Button searchButton;

	@UiField
	TextBox searchTextBox;

	@UiField
	Row sortRow;

	@UiField
	FormLabel searchLabel;

	@UiField
	Button refreshButton;

	@UiField(provided = true)
	DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(
			ObidosConstants.VISIBLE_GRID_COUNT);

	@UiField(provided = true)
	SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

	@UiField
	ObidosMessageRow messageRow;

	@UiField
	CheckBox refreshCheckBox;

	@UiField
	ProgressBar progressbar;
	
	@Inject
	ListLoggedInUsersView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();

        helpButton.addClickHandler(new ClickHandler()
        {       
			@Override
			public void onClick(ClickEvent event)
			{       
				getUiHandlers().help();
			}
        });
	}
	
	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().search();
	}

	@UiHandler("searchTextBox")
	void onPressEnterserachTextBox(KeyDownEvent e)
	{
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().search();
		}
	}
	
	@UiHandler("refreshCheckBox")
	void onClickrefreshCheckBox(ClickEvent e)
	{
		getUiHandlers().refreshTimerCheckBoxCallback();
	}

	@UiHandler("sortByDate")
	void onSelectsortByDateAnchorList(ClickEvent e)
	{
		getUiHandlers().sortByDate();
	}

	@UiHandler("sortByAZ")
	void onSelectsortByAZAnchorList(ClickEvent e)
	{
		getUiHandlers().sortByAZ();
	}

	@UiHandler("sortByZA")
	void onclickSortByZA(ClickEvent e)
	{
		getUiHandlers().sortByZA();
	}
	
	@UiHandler("refreshButton")
	void onClickRefreshButton(ClickEvent e)
	{
		getUiHandlers().refresh();
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}

	public Row getSortRow()
	{
		return sortRow;
	}

	public FormLabel getSearchLabel()
	{
		return searchLabel;
	}

	public DataGrid<LimitedUserDTO> getDataGrid()
	{
		return dataGrid;
	}

	public SimplePager getPager()
	{
		return pager;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Button getRefreshButton()
	{
		return refreshButton;
	}

	public CheckBox getRefreshCheckBox()
	{
		return refreshCheckBox;
	}

	public ProgressBar getProgressbar()
	{
		return progressbar;
	}

}