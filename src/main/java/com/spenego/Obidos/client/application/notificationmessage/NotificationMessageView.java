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

package com.spenego.Obidos.client.application.notificationmessage;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.NotificationDTO;

class NotificationMessageView extends ViewWithUiHandlers<NotificationMessageUiHandlers>
		implements NotificationMessagePresenter.MyView
{
    @UiField(provided = true)
    DataGrid<NotificationDTO> dataGrid = new DataGrid<NotificationDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);
    
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosMessageRow messageRow;
    
    @UiField
    Button markReadButton;
    
    @UiField
    Button deleteButton;

    @UiField
    Row selectCheckBoxRow;
    
    @UiField
    Button selectButton;
    
    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    Button unreadMessageButton;

    @UiField
    Button readMessageButton;
    
    @UiField
    InlineCheckBox markReadAfterViewCheckBox;
    
    
    
	interface Binder extends UiBinder<Widget, NotificationMessageView>
	{
	}

	@UiHandler("markReadButton")
	void onclickMarkReadButton (ClickEvent e)
	{
		getUiHandlers().markReadUnread();
	}
	
	@UiHandler("deleteButton")
	void onclickDeleteButton (ClickEvent e)
	{
		getUiHandlers().delete();
	}
	
	
	@UiHandler("unreadMessageButton")
	void onclickUnreadMessageButton (ClickEvent e)
	{
		getUiHandlers().showUnreadMessages();
	}
	
	@UiHandler("readMessageButton")
	void onclickReadMessageButton (ClickEvent e)
	{
		getUiHandlers().showReadMessages();
	}

	@UiHandler("selectButton")
	void onclickSelectCheckBoxButton (ClickEvent e)
	{
		getUiHandlers().selectButtonCallback();
	}

	@Inject
	NotificationMessageView(Binder uiBinder)
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

	public DataGrid<NotificationDTO> getDataGrid()
	{
		return dataGrid;
	}

	public SimplePager getPager()
	{
		return pager;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getMarkReadButton()
	{
		return markReadButton;
	}

	public Button getDeleteButton()
	{
		return deleteButton;
	}

	public Row getSelectCheckBoxRow()
	{
		return selectCheckBoxRow;
	}

	@Override
	public FormLabel getFormErrorLabel()
	{
		// not used
		return null;
	}

	public Button getSelectButton()
	{
		return selectButton;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Button getUnreadMessageButton()
	{
		return unreadMessageButton;
	}

	public Button getReadMessageButton()
	{
		return readMessageButton;
	}

	public InlineCheckBox getMarkReadAfterViewCheckBox()
	{
		return markReadAfterViewCheckBox;
	}
}
