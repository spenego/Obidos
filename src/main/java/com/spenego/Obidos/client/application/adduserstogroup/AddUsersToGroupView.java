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

package com.spenego.Obidos.client.application.adduserstogroup;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextArea;
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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

class AddUsersToGroupView extends ViewWithUiHandlers<AddUsersToGroupUiHandlers>
        implements AddUsersToGroupPresenter.MyView
{
    interface Binder extends UiBinder<Widget, AddUsersToGroupView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosPanelHeader  panelHeader;

    @UiField
    ObidosInputGroup groupNameInputGroup;

    @UiField
    TextArea commentsTextArea;

    @UiField
    TextBox serachTextBox;
    
    @UiField
	ObidosMessageRow messageRow;


    @UiField
    Button addUserButton;

    @UiField
    Button searchUserButton;

    @UiField
    Button clearButton;
    
    @UiField(provided = true)
    DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @Inject
    AddUsersToGroupView(Binder uiBinder)
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

    @UiHandler("serachTextBox")
    void onPressEnterserachTextBox(KeyDownEvent e)
    {
        if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().searchUsers();
        }
    }

    @UiHandler("searchUserButton")
    void onClickSearchUserButton(ClickEvent e)
    {
        getUiHandlers().searchUsers();
    }

    @UiHandler("addUserButton")
    void onClickaddUserButton(ClickEvent e)
    {
        getUiHandlers().addUserToGroup();
    }
    
    @UiHandler("clearButton")
    void onClickClearButton(ClickEvent e)
    {
    	getUiHandlers().clearCheckBoxSelections();
    }
    
    public TextBox getSerachTextBox()
    {
        return serachTextBox;
    }

    public Button getAddUserButton()
    {
        return addUserButton;
    }

    public DataGrid<LimitedUserDTO> getDataGrid()
    {
        return dataGrid;
    }

    public SimplePager getPager()
    {
        return pager;
    }

    public TextArea getCommentsTextArea()
    {
        return commentsTextArea;
    }

    public Button getSearchUserButton()
    {
        return searchUserButton;
    }

	public Button getClearButton()
	{
		return clearButton;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosInputGroup getGroupNameInputGroup()
	{
		return groupNameInputGroup;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}
}
