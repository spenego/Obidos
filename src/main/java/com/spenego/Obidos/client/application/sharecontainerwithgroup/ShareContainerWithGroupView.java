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

package com.spenego.Obidos.client.application.sharecontainerwithgroup;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.GroupDTO;

class ShareContainerWithGroupView extends ViewWithUiHandlers<ShareContainerWithGroupUiHandlers>
        implements ShareContainerWithGroupPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ShareContainerWithGroupView>
    {
    }
    
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    ObidosInputGroup containerNameInputGroup;

    @UiField
    TextBox serachTextBox;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
    Button shareContainerButton;

    @UiField
    CheckBox notifyCheckBox;

    @UiField
    TextBox shareCommentTextBox;

    @UiField
    Button clearButton;
    
	@UiField
	ToggleSwitch sendNotificationEmailSwitch;

    @UiField(provided = true)
    DataGrid<GroupDTO> dataGrid = new DataGrid<GroupDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @Inject
    ShareContainerWithGroupView(Binder uiBinder)
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
                                getUiHandlers().showHideHelp();
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
        if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().searchGroupsToShare();

        }
    }

    @UiHandler("searchButton")
    void onClickSearchButton(ClickEvent e)
    {
        getUiHandlers().searchGroupsToShare();
    }

    @UiHandler("shareContainerButton")
    void onClickshareConainerButton(ClickEvent e)
    {
        getUiHandlers().shareContainer();
    }

    @UiHandler("clearButton")
    void onClickClearButton(ClickEvent e)
    {
    	getUiHandlers().clearCheckBoxSelections();
    }
    
    public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	public DataGrid<GroupDTO> getDataGrid()
    {
        return dataGrid;
    }


    public SimplePager getPager()
    {
        return pager;
    }

    public TextBox getSearchTextBox()
    {
        return serachTextBox;
    }

	public CheckBox getNotifyCheckBox()
	{
		return notifyCheckBox;
	}

	public TextBox getShareCommentTextBox()
	{
		return shareCommentTextBox;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public TextBox getSerachTextBox()
	{
		return serachTextBox;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Button getShareContainerButton()
	{
		return shareContainerButton;
	}

	@Override
	public Button getHelpButton()
	{
		return null;
	}

	@Deprecated
	@Override
	public FormLabel getFormErrorLabel()
	{
		return null;
	}

	@Deprecated
	@Override
	public TextBox getShareContainerTextBox()
	{
		return null;
	}

	@Deprecated
	@Override
	public Heading getPanelHeading()
	{
			return null;
	}

	public ToggleSwitch getSendNotificationEmailSwitch()
	{
		return sendNotificationEmailSwitch;
	}
}
