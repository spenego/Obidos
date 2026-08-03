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

package com.spenego.Obidos.client.application.revokecontainerfromusers;

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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

class RevokeContainerFromUsersView extends ViewWithUiHandlers<RevokeContainerFromUsersUiHandlers>
		implements RevokeContainerFromUsersPresenter.MyView
{
    @UiField(provided = true)
    DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

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
    Button revokeButton;

    @UiField
    TextBox revokeCommentTextBox;

    @UiField
    Button clearButton;

    @UiField
    Button searchButton;
    
	@UiField
	ToggleSwitch sendNotificationEmailSwitch;
	
	interface Binder extends UiBinder<Widget, RevokeContainerFromUsersView>
	{
	}

	@Inject
	RevokeContainerFromUsersView(Binder uiBinder)
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
	void onClickrevokeConainerButton(ClickEvent e)
	{
		getUiHandlers().revokeContainer();
	}

	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().searchUsersToRevoke();
	}

	@UiHandler("serachTextBox")
	void onPressEnter(KeyDownEvent e)
	{
		if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
		{
			getUiHandlers().searchUsersToRevoke();
		}
	}


	@UiHandler("clearButton")
	void onClickClearButton(ClickEvent e)
	{
		getUiHandlers().clearCheckBoxSelections();
	}


	public DataGrid<LimitedUserDTO> getDataGrid()
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

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public TextBox getRevokeCommentTextBox()
	{
		return revokeCommentTextBox;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public TextBox getSerachTextBox()
	{
		return serachTextBox;
	}

	public Button getRevokeButton()
	{
		return revokeButton;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	@Deprecated
	@Override
	public FormLabel getFormErrorLabel()
	{
		// TODO Auto-generated method stub
		return null; // not used
	}

	@Deprecated
	@Override
	public Heading getPanelHeading()
	{
		return null;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	@Deprecated
	@Override
	public TextBox getShareContainerTextBox()
	{
		return null;
	}

	public ToggleSwitch getSendNotificationEmailSwitch()
	{
		return sendNotificationEmailSwitch;
	}
}