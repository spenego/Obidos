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

package com.spenego.Obidos.client.application.grantpermissionstousersforcontainer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Row;
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
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;

class GrantPermissionsToUsersForContainerView extends ViewWithUiHandlers<GrantPermissionsToUsersForContainerUiHandlers>
		implements GrantPermissionsToUsersForContainerPresenter.MyView
{
	interface Binder extends UiBinder<Widget, GrantPermissionsToUsersForContainerView>
	{
	}

    @UiField(provided = true)
    DataGrid<UserGroupComboDTO> dataGrid = new DataGrid<UserGroupComboDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);
    
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    Row containerRow;

    @UiField
    ObidosInputGroup containerNameInputGroup;

    @UiField
    ObidosMessageRow messageRow;

    @UiField
    Button grantButton;

    @UiField
    TextBox containerTypeTextBox;

    @UiField
    Button clearButton;

    @UiField
    TextBox searchTextBox;

    @UiField
    Button searchButton;

    /*
    @UiField
    InlineCheckBox mayTakeOwnershipCheckBox;
    
    @UiField
    InlineCheckBox mayUpdateCheckBox;
    */

    @UiField
    InlineCheckBox readCheckBox;

    @UiField
    InlineCheckBox addCheckBox;

    @UiField
    InlineCheckBox updateCheckBox;

    @UiField
    InlineCheckBox ownCheckBox;

	@Inject
	GrantPermissionsToUsersForContainerView(Binder uiBinder)
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
	@UiHandler("grantButton")
	void onclickGratnButton (ClickEvent e)
	{
		getUiHandlers().grantPermissions();
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
    
    @UiHandler("ownCheckBox")
    void onclickMayTakeOwnershipCheckBox (ClickEvent e)
	{
    	getUiHandlers().mayTakeOwnershipCheckBoxCallback();
	}
    
    @UiHandler("updateCheckBox")
    void onclickMayUpdateCheckBox (ClickEvent e)
	{
    	getUiHandlers().mayUpdateCheckBoxCallback();
	}
    @UiHandler("addCheckBox")
    void onClickAddCheckBox(ClickEvent e)
    {
    	getUiHandlers().addCheckBoxCallback();
    	
    }

    @UiHandler("readCheckBox")
    void onclickReadCheckBox (ClickEvent e)
	{
    	getUiHandlers().readCheckBoxCallback();
	}

	public DataGrid<UserGroupComboDTO> getDataGrid()
	{
		return dataGrid;
	}

	public SimplePager getPager()
	{
		return pager;
	}

	public Row getContainerRow()
	{
		return containerRow;
	}

	public Button getGrantButton()
	{
		return grantButton;
	}

	public TextBox getContainerTypeTextBox()
	{
		return containerTypeTextBox;
	}

	public Button getClearButton()
	{
		return clearButton;
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

	@Override
	public Button getHelpButton()
	{
		return null;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	/*
	public InlineCheckBox getMayTakeOwnershipCheckBox()
	{
		return mayTakeOwnershipCheckBox;
	}
	public InlineCheckBox getMayUpdateCheckBox()
	{
		return mayUpdateCheckBox;
	}
	*/
	public InlineCheckBox getReadCheckBox()
	{
		return readCheckBox;
	}
	public InlineCheckBox getAddCheckBox()
	{
		return addCheckBox;
	}
	public InlineCheckBox getUpdateCheckBox()
	{
		return updateCheckBox;
	}
	public InlineCheckBox getOwnCheckBox()
	{
		return ownCheckBox;
	}

}

