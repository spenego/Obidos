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

package com.spenego.Obidos.client.application.revokeitemfromgroups;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.GroupDTO;

class RevokeItemFromGroupsView extends ViewWithUiHandlers<RevokeItemFromGroupsUiHandlers>
		implements RevokeItemFromGroupsPresenter.MyView
{
    @UiField(provided = true)
    DataGrid<GroupDTO> dataGrid = new DataGrid<GroupDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    FormLabel itemNameLabel;

    @UiField
    TextBox itemNameTextBox;

    @UiField
    ObidosMessageRow messageRow;

    @UiField
    Button revokeButton;

    @UiField
    TextBox containerNameTextBox;

    @UiField
    Button clearButton;

    @UiField
    TextBox revokeCommentTextBox;

    @UiField
    TextBox searchTextBox;

    @UiField
    Button searchButton;
    
    @UiField
    ObidosRow containerRow;
    
	@UiField
	ToggleSwitch sendNotificationEmailSwitch;

	interface Binder extends UiBinder<Widget, RevokeItemFromGroupsView>
	{
	}

	@Inject
	RevokeItemFromGroupsView(Binder uiBinder)
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


    @UiHandler("revokeButton")
    void onClickUnshareButton(ClickEvent e)
    {
        getUiHandlers().revokeSharingItemFromGroups();
    }

    @UiHandler("clearButton")
    void onClickClearButton(ClickEvent e)
    {
    	getUiHandlers().clearCheckBoxSelections();
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

    public DataGrid<GroupDTO> getDataGrid()
    {
        return dataGrid;
    }

    public SimplePager getPager()
    {
        return pager;
    }


    public FormLabel getFormErrorLabel()
    {
        return null;
    }


    public Button getRevokeButton()
	{
		return revokeButton;
	}

    public FormLabel getItemNameLabel()
    {
        return itemNameLabel;
    }

    public TextBox getItemNameTextBox()
    {
        return itemNameTextBox;
    }

    public TextBox getContainerNameTextBox()
    {
        return containerNameTextBox;
    }

	public Button getClearButton()
	{
		return clearButton;
	}

	public TextBox getRevokeCommentTextBox()
	{
		return revokeCommentTextBox;
	}
	public TextBox getSearchTextBox()
	{
		return searchTextBox;
	}
	public Button getSearchButton()
	{
		return searchButton;
	}

	// dummies - not used
	public TextBox getShareContainerTextBox()
	{
		return containerNameTextBox;
	}
	public Button getRevokeContainerButton()
	{
		return revokeButton;
	}
	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosRow getContainerRow()
	{
		return containerRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}
	
	@Deprecated
	@Override
	public Heading getPanelHeading()
	{
		return null;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}


	public ToggleSwitch getSendNotificationEmailSwitch()
	{
		return sendNotificationEmailSwitch;
	}


}