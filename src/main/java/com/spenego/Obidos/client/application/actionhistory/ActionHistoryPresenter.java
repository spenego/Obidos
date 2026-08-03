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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.select.client.ui.MultipleSelect;
import org.gwtbootstrap3.extras.select.client.ui.Option;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AuditService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuditDTO;
import com.spenego.Obidos.shared.dto.AuditResult;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DateRange;

public class ActionHistoryPresenter extends
		ObidosPresenter<AuditDTO, ActionHistoryPresenter.MyView, ActionHistoryPresenter.MyProxy, ActionHistoryUiHandlers>
		implements ActionHistoryUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;

	interface MyView extends View, HasUiHandlers<ActionHistoryUiHandlers>
	{
		public DataGrid<AuditDTO> getDataGrid();
		public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public ListBox getActionSearchListBox();
		public TextBox getObjectSearchTextBox();
		public FlowPanel getActionSearchFlowPanel();
		public MultipleSelect getSelectActions();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public ObidosTimeBox getStartTimeBox();
		public ObidosTimeBox getEndTimeBox();
		public DatePicker getsDatePicker();
		public DatePicker geteDatePicker();
	}

	@NameToken(NameTokens.ACTION_HISTORY)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<ActionHistoryPresenter>
	{
	}

	@Inject
	ActionHistoryPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
		DataGrid<AuditDTO> grid = getView().getDataGrid();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		showAuditLogs(selectionModel, grid, this);
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		setTimeboxValues();
		setDateFormat();
		ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
		clearForm();
		updatePanelHeader();
		populateActionList();
		DataGrid<AuditDTO> grid = getView().getDataGrid();
		refreshDataGrid(grid);
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
		setColumnWidth(grid, 0, "20%");
	}
	
	private void setTimeboxValues()
	{
//		getView().getStartTimeBox().getElement().setAttribute("placeHolder", glang.time());
		getView().getStartTimeBox().setText("12:00 AM");
		getView().getEndTimeBox().setText("12:00 AM");
	}
	
	private void setDateFormat()
	{
		String placeHolder = ClientUtils.getDateFormat(currentUser);
		getView().getsDatePicker().setPlaceholder(placeHolder);
		getView().geteDatePicker().setPlaceholder(placeHolder);
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void search()
	{
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void clearForm()
	{
		showMessage(null);
		MyView v = getView();
		ListBox lb = v.getActionSearchListBox();
		for (int i = 0; i < lb.getItemCount(); i++)
		{
			lb.setItemSelected(i, false);
		}
		v.getObjectSearchTextBox().clear();
		v.getsDatePicker().setValue(null);
		v.geteDatePicker().setValue(null);
		setTimeboxValues();

		getView().getSelectActions().deselectAll();

	}

	@Override
	protected String getIdName()
	{
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	private void showAuditLogs(final SelectionModel<AuditDTO> selectionModel, final AbstractCellTable<AuditDTO> grid,
			HasHandlers source)
	{
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(glang.loading());
		grid.setEmptyTableWidget(messageLabel);

		addDateTimeButtonCellColumn(grid);

    	// use a custom cell for message. Otherwise long messages can not be
    	// seen. Now show log messages as tooltip.
    	ObidosButtonCell messageCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_AUDIT_LOG);
        final Column<AuditDTO, String> messageCol = new Column<AuditDTO, String>(messageCell)
        {
            @Override
            public String getValue(AuditDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(messageCol, glang.message());

		getView().getPager().setDisplay(grid);

		new AsyncDataProvider<AuditDTO>()
		{
			@Override
			protected void onRangeChanged(HasData<AuditDTO> dto)
			{
				final Range range = dto.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				GwtAsyncWrapper<AuditResult> callback = new GwtAsyncWrapper<AuditResult>(source)
				{

					@Override
					public void uponFailure(Throwable caught)
					{
						updateRowCount(0, true);
						showErrorMessage(glang.couldNotFetchHistory(caught.getMessage()));
					}

					@Override
					public void uponSuccess(AuditResult result)
					{
						List<AuditDTO> logs = result.getElements();
						if (logs != null && logs.size() > 0)
						{
							updateRowCount(result.getTotal(), true);
							updateRowData(start, logs);
						} else
						{
							messageLabel.setText(glang.noLogsFound());
							updateRowCount(0, true);
						}
					}
				};

				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				ArrayList<String> objects = convertToList(getView().getObjectSearchTextBox().getValue());
				ArrayList<String> usernames = convertToList(currentUser.getUserDTO().getUsername());
				ArrayList<String> actions = convertToList(getView().getSelectActions());
				gwtLog("Calling getAuditRecords, usernames: " + usernames);
				gwtLog("Calling getAuditRecords, Actions: " + actions);
				safeInvocationCall(() -> AuditService.Utility.getInstance().getAuditRecords(authCreds, actions, usernames, objects, getDateRange(), start, length, toArray(OrderBy.CREATE_TIME_DESC), callback));
			}
		}.addDataDisplay(grid);
	}

	private ArrayList<String> convertToList(String s)
	{
		return (s == null || s.isEmpty()) ? null : new ArrayList<>(Arrays.asList(s.split("[ ,]+")));
	}

	private ArrayList<String> convertToList(final MultipleSelect select)
	{
		final ArrayList<String> list = (ArrayList<String>) select.getValue();
		return list;
	}
	
	private Date getStartDate()
	{
		return ClientUtils.getDateFromDatePickerAndTimeBox(currentUser, 
				getView().getsDatePicker(), 
				getView().getStartTimeBox(),
				getView().getMessageRow());
				
		/*
		String dateString = getView().getsDatePicker().getTextBox().getValue();
		Date startDate = getView().getsDatePicker().getValue();
		if (!dateString.isEmpty())
		{
			gwtLog("Start Date string: "+ dateString);
			if (startDate == null)
			{
				showErrorMessage(glang.invalidStartDate());
				return null;
			}
		}
		if (startDate == null)
		{
			gwtLog("Start date is null");
			return null;
		}

		ObidosTimeBox startTimeBox = getView().getStartTimeBox();
		String dateFormat = ClientUtils.getDateFormat(currentUser);

		//bootstrap and gwt date formats are different. 
		// bootstrap uses mm for day for month, gwt uses MM
		String gwtDateFormat = ClientUtils.getBootstrap2GwtDateFormat(dateFormat);
		String timeFormat = startTimeBox.getTimeFormat();
		String format = gwtDateFormat + " " + timeFormat;
		Long timeVal = startTimeBox.getValue();
		if (timeVal == null)
		{
			showErrorMessage("Invalid Start time value");
			return null;
		}

		String dateTimeString = dateString + " " + startTimeBox.getText();
		gwtLog("format: " + gwtDateFormat);
		gwtLog("dateString: " + dateTimeString);
        
		return DateTimeFormat.getFormat(format).parse(dateTimeString);
		*/
	}

	private Date getEndDate()
	{
		return ClientUtils.getDateFromDatePickerAndTimeBox(currentUser, 
				getView().geteDatePicker(), 
				getView().getEndTimeBox(),
				getView().getMessageRow());

		/*
		String dateString = getView().geteDatePicker().getTextBox().getValue();
		Date endDate = getView().geteDatePicker().getValue();
		if (!dateString.isEmpty())
		{
			if (endDate == null)
			{
				showErrorMessage(glang.invalidEndDate());
				return null;
			}
		}
		if (endDate == null)
		{
			return null;
		}

		ObidosTimeBox endTimeBox = getView().getEndTimeBox();
		String dateFormat = ClientUtils.getDateFormat(currentUser);
		//bootstrap and gwt date formats are different. 
		// bootstrap uses mm for day for month, gwt uses MM
		String gwtDateFormat = ClientUtils.getBootstrap2GwtDateFormat(dateFormat);
		String timeFormat = endTimeBox.getTimeFormat();
		String format = gwtDateFormat + " " + timeFormat;

		Long timeVal = endTimeBox.getValue();
		if (timeVal == null)
		{
			showErrorMessage("Invalid End time value");
			return null;
		}

		String dateTimeString = dateString + " " + endTimeBox.getText();
		return DateTimeFormat.getFormat(format).parse(dateTimeString);
		*/
	}

	private DateRange getDateRange()
	{
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		
		if (startDate == null || endDate == null)
		{
			return null;
		}
		gwtLog("SD: " + startDate);
		gwtLog("ED: " + endDate);
		return new DateRange(startDate, endDate);
	}

	private void updatePanelHeader()
	{
		getView().getPanelHeader().setText(glang.actionHistory());
		if (ClientUtils.isAdmin(currentUser))
		{
			getView().getPanelHeader().setText(glang.auditLogs());
		}
	}

	private void populateActionList()
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				MultipleSelect multiSelect = getView().getSelectActions();
				multiSelect.clear();
				List<Option> items = getSelectItems();
				for (Option item : items)
				{
					multiSelect.add(item);
				}
				multiSelect.render();
				multiSelect.refresh();
			}

		});

	}

	private void refreshDataGrid(DataGrid<AuditDTO> grid)
	{
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}

	private List<Option> getSelectItems()
	{
		List<Option> list = ClientUtils.getAuditLogSearchSelectItems(currentUser);
		return list;
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
}