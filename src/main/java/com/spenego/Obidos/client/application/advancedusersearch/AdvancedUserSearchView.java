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

package com.spenego.Obidos.client.application.advancedusersearch;

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
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;

class AdvancedUserSearchView extends ViewWithUiHandlers<AdvancedUserSearchUiHandlers>
        implements AdvancedUserSearchPresenter.MyView
{
    interface Binder extends UiBinder<Widget, AdvancedUserSearchView>
    {
    }
    
    @UiField 
    BlockQuote helpBlockQuote;

    @UiField(provided = true)
    DataGrid<LimitedUserForAdminDTO> dataGrid = new DataGrid<LimitedUserForAdminDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @UiField
    TextBox userNameTextBox;

    @UiField
    TextBox fullNameTextBox;

    @UiField
    TextBox emailTextBox;

    @UiField
    ToggleSwitch adminSwitch;

    @UiField
    ToggleSwitch lockedUserSwitch;

    @UiField
    ToggleSwitch deletedUserSwitch;
    
    @UiField
    ObidosButtonToolBar bottomToolBar;

	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	FormLabel searchFilterLabel;

    @Inject
    AdvancedUserSearchView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
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
    }

    @UiHandler("userNameTextBox")
    void onEnterUsrNameTextbox(KeyDownEvent e)
    {
       if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
       {
           getUiHandlers().searchUsers();
       }
    }

    @UiHandler("fullNameTextBox")
    void onEnterFullNameTextBox(KeyDownEvent e)
    {
       if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
       {
           getUiHandlers().searchUsers();
       }
    }

    @UiHandler("emailTextBox")
    void onEnterEmailTextBox(KeyDownEvent e)
    {
       if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
       {
           getUiHandlers().searchUsers();
       }
    }

    @UiHandler("searchButton")
    void onClickSearchButton(ClickEvent e)
    {
        getUiHandlers().searchUsers();
    }
    
    public DataGrid<LimitedUserForAdminDTO> getDataGrid()
    {
        return dataGrid;
    }

    public SimplePager getPager()
    {
        return pager;
    }

    public TextBox getUserNameTextBox()
    {
        return userNameTextBox;
    }

    public TextBox getFullNameTextBox()
    {
        return fullNameTextBox;
    }

    public TextBox getEmailTextBox()
    {
        return emailTextBox;
    }

    @Deprecated
    public FormLabel getFormErrorLabel()
    {
        return null;
    }

    public ToggleSwitch getAdminSwitch()
    {
        return adminSwitch;
    }

    public ToggleSwitch getLockedUserSwitch()
    {
        return lockedUserSwitch;
    }

    public ToggleSwitch getDeletedUserSwitch()
    {
        return deletedUserSwitch;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosButtonToolBar getBottomToolBar()
	{
		return bottomToolBar;
	}


	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public FormLabel getSearchFilterLabel()
	{
		return searchFilterLabel;
	}

}