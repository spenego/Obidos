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

package com.spenego.Obidos.client.application.sharewithusers;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Row;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroupAddon;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

class ShareWithUsersView extends ViewWithUiHandlers<ShareWithUsersUiHandlers> implements ShareWithUsersPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ShareWithUsersView>
    {
    }
    
//  @UiField
//  Button helpButton;
    
    @UiField
    BlockQuote helpBlockQuote;

        @UiField
        ObidosPanelHeader panelHeader;

    @UiField
    FormLabel nameLabel;

    @UiField
    TextBox serachTextBox;

    @UiField
	ObidosMessageRow messageRow;

    @UiField
    Button shareButton;
    
    @UiField
    Button clearButton;

    @UiField
    Row containerRow;

    @UiField
    TextBox containerNameTextBox;

    @UiField
    TextBox shareCommentTextBox;

    @UiField
    ObidosInputGroup nameInputGroup;

    @UiField
    Label licenseLabel;

	@UiField
	ObidosInputGroupAddon  containerTypeAddon;

	@UiField
	ToggleSwitch sendNotificationEmailSwitch;

	@UiField
	ToggleSwitch sendNotificationSmsSwitch;

    @UiField
    Label licenseLabelSms;

    @UiField(provided = true)
    DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @Inject
    ShareWithUsersView(Binder uiBinder)
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

    @UiHandler("shareButton")
    void onClickShareButton(ClickEvent e)
    {
        getUiHandlers().share();
    }

    @UiHandler("serachTextBox")
    void onPressEnterserachTextBox(KeyDownEvent e)
    {
        if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().searchUsersToShare();

        }
    }

    @UiHandler("searchButton")
    void onClickSearchButton(ClickEvent e)
    {
        getUiHandlers().searchUsersToShare();
    }

//    @UiHandler("backButton")
//    void onClickListButton(ClickEvent e)
//    {
//        getUiHandlers().showListItemsPage();
//    }

    @UiHandler("clearButton")
    void onClickClearButton(ClickEvent e)
    {
        getUiHandlers().clearCheckBoxSelections();
    }
    
//  @UiHandler("helpButton")
//  void onclickHelpButton (ClickEvent e)
//  {
// 	getUiHandlers().help();
// }

    public TextBox getSearchTextBox()
    {
        return serachTextBox;
    }

    @Deprecated
    public FormLabel getFormErrorLabel()
    {
        return null;
    }

    public Button getShareButton()
    {
        return shareButton;
    }

    public DataGrid<LimitedUserDTO> getDataGrid()
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

    public Row getContainerRow()
	{
		return containerRow;
	}

	public TextBox getContainerNameTextBox()
    {
        return containerNameTextBox;
    }

    public TextBox getShareCommentTextBox()
    {
        return shareCommentTextBox;
    }

	public TextBox getSerachTextBox()
	{
		return serachTextBox;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public ObidosInputGroup getNameInputGroup()
	{
		return nameInputGroup;
	}

	@Override
	public TextBox getNameTextBox()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public Button getHelpButton()
	{
              return null;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
			return panelHeader;
	}

	public ObidosInputGroupAddon getContainerTypeAddon()
	{
		return containerTypeAddon;
	}

	@Deprecated
	public TextBox getContainerTypeTextBox()
	{
		return null;
	}

	public ToggleSwitch getSendNotificationEmailSwitch()
	{
		return sendNotificationEmailSwitch;
	}

	public Label getLicenseLabel()
	{
		return licenseLabel;
	}

	public ToggleSwitch getSendNotificationSmsSwitch()
	{
		return sendNotificationSmsSwitch;
	}

	public Label getLicenseLabelSms()
	{
		return licenseLabelSms;
	}

}
