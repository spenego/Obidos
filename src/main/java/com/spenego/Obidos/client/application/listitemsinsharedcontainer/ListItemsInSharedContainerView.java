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

package com.spenego.Obidos.client.application.listitemsinsharedcontainer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.SharedItemDTO;

class ListItemsInSharedContainerView extends ViewWithUiHandlers<ListItemsInSharedContainerUiHandlers>
		implements ListItemsInSharedContainerPresenter.MyView
{
	interface Binder extends UiBinder<Widget, ListItemsInSharedContainerView>
	{
	}

	@UiField
	HTMLPanel mainHtmlPanel;

	@UiField
	ObidosInputGroup nameInputGroup;

	@UiField(provided = true)
	DataGrid<SharedItemDTO> dataGrid = new DataGrid<SharedItemDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

	@UiField(provided = true)
	SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	TextBox searchTextBox;

	@UiField
	Button searchButton;

	@UiField
	Button relinquishButton;

	@UiField
	Button clearButton;

	@UiField
	ObidosMessageRow messageRow;

	@Inject
	ListItemsInSharedContainerView(Binder uiBinder)
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

	@UiHandler("searchTextBox")
	void onPreseeEnterSearchTextBox(KeyDownEvent e)
	{
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().search();
		}
	}

	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().search();
	}

	@UiHandler("relinquishButton")
	void onclickRelinquishButton(ClickEvent e)
	{
		getUiHandlers().relinquishSharedItems();
	}

	@UiHandler("clearButton")
	void onclickClearButton(ClickEvent e)
	{
		getUiHandlers().clearSelections();
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
	void onclickSortByZA(ClickEvent e)
	{
		getUiHandlers().sortByZA();
	}

	public DataGrid<SharedItemDTO> getDataGrid()
	{
		return dataGrid;
	}

	public SimplePager getPager()
	{
		return pager;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public FormLabel getFormErrorLabel()
	{
		return null;
	}

	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public Button getRelinquishButton()
	{
		return relinquishButton;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosInputGroup getNameInputGroup()
	{
		return nameInputGroup;
	}

	public Button getHelpButton()
	{
		return null;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public HTMLPanel getMainHtmlPanel()
	{
		return mainHtmlPanel;
	}
}
