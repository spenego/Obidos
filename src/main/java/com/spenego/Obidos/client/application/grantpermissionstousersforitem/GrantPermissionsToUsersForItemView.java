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

package com.spenego.Obidos.client.application.grantpermissionstousersforitem;


import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
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
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

class GrantPermissionsToUsersForItemView extends ViewWithUiHandlers<GrantPermissionsToUsersForItemUiHandlers>
		implements GrantPermissionsToUsersForItemPresenter.MyView
{
	interface Binder extends UiBinder<Widget, GrantPermissionsToUsersForItemView>
	{
	}
    @UiField(provided = true)
    DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);
    
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    Row containerRow;

    @UiField
    FormLabel itemNameLabel;

    @UiField
    ObidosInputGroup containerNameInputGroup;

    @UiField
    ObidosInputGroup nameInputGroup;
    
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
    MultipleSelect selectPermissions;
    */
    
    @UiField
    InlineCheckBox mayUpdateCheckBox;
    
    @UiField
    InlineCheckBox mayTakeOwnershipCheckBox;
    
    @UiField
    InlineCheckBox noneCheckBox;
    
	@Inject
	GrantPermissionsToUsersForItemView(Binder uiBinder)
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
    
    /*
    @UiHandler("selectPermissions")
    void onValueChangeMultiple(ValueChangeEvent<List<String>> event)
    {
    	// just reset error message
    	getUiHandlers().multiSelectPermissionValueChagned();
    }
    */

    @UiHandler("mayUpdateCheckBox")
    void onclickMayUpdateCheckBox (ClickEvent e)
	{
    	getUiHandlers().mayUpdateCheckBoxCallback();
	}
    
    @UiHandler("mayTakeOwnershipCheckBox")
    void onclickMayTakeOwnershipCheckBox (ClickEvent e)
	{
    	getUiHandlers().mayTakeOwnershipCheckBoxCallback();
	}
    
    /*
    @UiHandler("noneCheckBox")
    void onclickNoneCheckBox (ClickEvent e)
	{
    	getUiHandlers().noneCheckBoxCallback();
	}
	*/

	public DataGrid<LimitedUserDTO> getDataGrid()
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

	public FormLabel getItemNameLabel()
	{
		return itemNameLabel;
	}

	public ObidosInputGroup getNameInputGroup()
	{
		return nameInputGroup;
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

	public InlineCheckBox getMayUpdateCheckBox()
	{
		return mayUpdateCheckBox;
	}

	public InlineCheckBox getMayTakeOwnershipCheckBox()
	{
		return mayTakeOwnershipCheckBox;
	}

	public InlineCheckBox getNoneCheckBox()
	{
		return noneCheckBox;
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

}
