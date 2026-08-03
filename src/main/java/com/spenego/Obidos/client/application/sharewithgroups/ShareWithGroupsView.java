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

package com.spenego.Obidos.client.application.sharewithgroups;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.GroupDTO;

class ShareWithGroupsView extends ViewWithUiHandlers<ShareWithGroupsUiHandlers>
        implements ShareWithGroupsPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ShareWithGroupsView>
    {
    }

    @UiField(provided = true)
    DataGrid<GroupDTO> dataGrid = new DataGrid<GroupDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

//    @UiField
//    Button helpButton;
    
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    FormLabel nameLabel;

    @UiField
    TextBox nameTextBox;

    @UiField
    ObidosMessageRow messageRow;

    @UiField
    Button shareButton;
    
    @UiField
    Button clearButton;

    @UiField
    Button listButton;

    @UiField
    TextBox searchTextBox;

    @UiField
    Button searchButton;
    
    @UiField
    TextBox shareCommentTextBox;
    
    @UiField
	ObidosButtonToolBar buttonToolBar;
    
	@UiField
	ToggleSwitch sendNotificationEmailSwitch;


    @Inject
    ShareWithGroupsView(Binder uiBinder)
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

    @UiHandler("searchButton")
    void onClickSearchButton(ClickEvent e)
    {
        getUiHandlers().searchGroups();
    }

    @UiHandler("searchTextBox")
    void onEnterSearchTextBox(KeyDownEvent e)
    {
        if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().searchGroups();
        }
    }

//    @UiHandler("listButton")
//    void onClickListButton(ClickEvent e)
//    {
//        getUiHandlers().listItems();
//    }

    @UiHandler("shareButton")
    void onClickShareButton(ClickEvent e)
    {
        getUiHandlers().shareItem();
    }
    
    @UiHandler("clearButton")
    void onclickClearButton (ClickEvent e)
	{
    	getUiHandlers().clearCheckBoxSelections();
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

//    @UiHandler("helpButton")
//    void onclickHellpButton (ClickEvent e)
//	{
//   	getUiHandlers().help();
//	}


    public DataGrid<GroupDTO> getDataGrid()
    {
        return dataGrid;
    }


    public SimplePager getPager()
    {
        return pager;
    }


    public FormLabel getNameLabel()
    {
        return nameLabel;
    }


    public TextBox getNameTextBox()
    {
        return nameTextBox;
    }


    @Deprecated
    public FormLabel getFormErrorLabel()
    {
        return null;
    }

    public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}
    
	public Button getShareButton()
    {
        return shareButton;
    }


    public Button getListButton()
    {
        return listButton;
    }


    public TextBox getSearchTextBox()
    {
        return searchTextBox;
    }

    public Button getSearchButton()
    {
        return searchButton;
    }

	public TextBox getShareCommentTextBox()
	{
		return shareCommentTextBox;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

// public Heading getPanelHeading()
//{
//return panelHeading;
//}

	public ObidosButtonToolBar getButtonToolBar()
	{
		return buttonToolBar;
	}

	public Button getHelpButton()
	{
		return null;
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
