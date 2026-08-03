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

package com.spenego.Obidos.client.application.revokeitemfromusers;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

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

class RevokeItemFromUsersView extends ViewWithUiHandlers<RevokeItemFromUsersUiHandlers>
		implements RevokeItemFromUsersPresenter.MyView
{
    @UiField(provided = true)
    DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @UiField
    BlockQuote helpBlockQuote;
    
//  @UiField
//  Heading panelHeading;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    FormLabel itemNameLabel;

    @UiField
    ObidosInputGroup itemNameInputGroup;

    @UiField
    ObidosMessageRow messageRow;
    

    @UiField
    Button revokeButton;

    @UiField
    ObidosInputGroup containerNameInputGroup;

    @UiField
    Button clearButton;

    @UiField
    TextBox revokeCommentTextBox;

    @UiField
    TextBox searchTextBox;

    @UiField
    Button searchButton;
    
//    @UiField
//    Button helpButton;
    
    @UiField
    Row containerRow;

	@UiField
	ToggleSwitch sendNotificationEmailSwitch;

	interface Binder extends UiBinder<Widget, RevokeItemFromUsersView>
	{
	}

	@Inject
	RevokeItemFromUsersView(Binder uiBinder)
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

//    @UiHandler("listButton")
//    void onClickListButton(ClickEvent e)
//    {
//        getUiHandlers().listItems();
//    }

    @UiHandler("revokeButton")
    void onClickUnshareButton(ClickEvent e)
    {
        getUiHandlers().revokeSharingItemFromUsers();
    }

    @UiHandler("clearButton")
    void onClickClearButton(ClickEvent e)
    {
    	getUiHandlers().clearCheckBoxSelections();
    }

//    @UiHandler("helpButton")
//    void onClickHelpButton(ClickEvent e)
//    {
//        getUiHandlers().help();
//    }

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

    public DataGrid<LimitedUserDTO> getDataGrid()
    {
        return dataGrid;
    }

    public SimplePager getPager()
    {
        return pager;
    }


    public Button getRevokeButton()
	{
		return revokeButton;
	}

    public FormLabel getItemNameLabel()
    {
        return itemNameLabel;
    }

	public Button getClearButton()
	{
		return clearButton;
	}

	public TextBox getRevokeCommentTextBox()
	{
		return revokeCommentTextBox;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public Heading getPanelHeading()
	{
// 		return panelHeading;
 		return null;
	}

	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getHelpButton()
	{
//		return helpButton;
		return null;
	}

	public Row getContainerRow()
	{
		return containerRow;
	}

	@Override
	public TextBox getShareContainerTextBox()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormLabel getFormErrorLabel()
	{
		return null;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	public ObidosInputGroup getItemNameInputGroup()
	{
		return itemNameInputGroup;
	}

	public ObidosPanelHeader getPanelHeader()
	{
			return panelHeader;
	}

	public ToggleSwitch getSendNotificationEmailSwitch()
	{
		return sendNotificationEmailSwitch;
	}
}
