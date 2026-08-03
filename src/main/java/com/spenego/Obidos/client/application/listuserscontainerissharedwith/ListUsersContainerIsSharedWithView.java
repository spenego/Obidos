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

package com.spenego.Obidos.client.application.listuserscontainerissharedwith;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;

class ListUsersContainerIsSharedWithView extends ViewWithUiHandlers<ListUsersContainerIsSharedWithUiHandlers>
		implements ListUsersContainerIsSharedWithPresenter.MyView
{
    @UiField(provided = true)
    DataGrid<UserGroupComboDTO> dataGrid = new DataGrid<UserGroupComboDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosInputGroup containerNameInputGroup;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
	ObidosPanelHeader panelHeader;

    @UiField
    TextBox searchTextBox;
    
	interface Binder extends UiBinder<Widget, ListUsersContainerIsSharedWithView>
	{
	}

	@Inject
	ListUsersContainerIsSharedWithView(Binder uiBinder)
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

	@UiHandler("searchButton")
	void onclickSearchButton (ClickEvent e)
	{
		getUiHandlers().searchUsers();
	}

    @UiHandler("searchTextBox")
    void onEnterSearchTextBox(KeyDownEvent e)
	{
		if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().searchUsers();
		}
	}

	public DataGrid<UserGroupComboDTO> getDataGrid()
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

	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}
}
