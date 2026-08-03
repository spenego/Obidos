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

package com.spenego.Obidos.client.application.actionhistory;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.select.client.ui.MultipleSelect;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuditDTO;

class ActionHistoryView extends ViewWithUiHandlers<ActionHistoryUiHandlers> implements ActionHistoryPresenter.MyView
{
	@UiField(provided = true)
	DataGrid<AuditDTO> dataGrid = new DataGrid<AuditDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

	@UiField(provided = true)
	SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	FlowPanel actionSearchFlowPanel;

	@UiField
	ListBox actionSearchListBox;

	@UiField
	TextBox objectsSearchTextBox;

	@UiField
	Button searchButton;

	@UiField
	MultipleSelect selectActions;

	@UiField
	ObidosMessageRow messageRow;

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	ObidosTimeBox startTimeBox;

	@UiField
	ObidosTimeBox endTimeBox;

	@UiField
	DatePicker sDatePicker;

	@UiField
	DatePicker eDatePicker;

	interface Binder extends UiBinder<Widget, ActionHistoryView>
	{
	}

	@Inject
	ActionHistoryView(Binder uiBinder)
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
		startTimeBox.addValueChangeHandler(new ValueChangeHandler<Long>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Long> event)
			{
				messageRow.showMessage(null);
			}
		});
		endTimeBox.addValueChangeHandler(new ValueChangeHandler<Long>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Long> event)
			{
				messageRow.showMessage(null);
			}
		});

	}

	public DataGrid<AuditDTO> getDataGrid()
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

	@UiHandler("searchButton")
	void onClickSearchButton(ClickEvent e)
	{
		getUiHandlers().search();
	}

	@UiHandler("clearButton")
	void onclickClearButton(ClickEvent e)
	{
		getUiHandlers().clearForm();
	}
	
	public TextBox getObjectSearchTextBox()
	{
		return objectsSearchTextBox;
	}

	public FlowPanel getActionSearchFlowPanel()
	{
		return actionSearchFlowPanel;
	}

	public MultipleSelect getSelectActions()
	{
		return selectActions;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public TextBox getObjectsSearchTextBox()
	{
		return objectsSearchTextBox;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	@Override
	public ListBox getActionSearchListBox()
	{
		return actionSearchListBox;
	}
	
	public DatePicker getsDatePicker()
	{
		return sDatePicker;
	}

	public ObidosTimeBox getStartTimeBox()
	{
		return startTimeBox;
	}

	public ObidosTimeBox getEndTimeBox()
	{
		return endTimeBox;
	}

	public DatePicker geteDatePicker()
	{
		return eDatePicker;
	}
}