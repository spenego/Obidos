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

package com.spenego.Obidos.client.application.auditlogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
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
import com.spenego.Obidos.client.security.LoggedInAdminLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuditDTO;
import com.spenego.Obidos.shared.dto.AuditResult;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DateRange;

public class AuditLogsPresenter extends ObidosPresenter<AuditDTO, AuditLogsPresenter.MyView, AuditLogsPresenter.MyProxy, AuditLogsUiHandlers> implements AuditLogsUiHandlers {
	ObidosMessages glang = ObidosMessages.LANG;

	interface MyView extends View, HasUiHandlers<AuditLogsUiHandlers> {
		public DataGrid<AuditDTO> getDataGrid();
		public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public Button getHelpButton();
		public TextBox getUsernameSearchTextBox();
		public ListBox getActionSearchListBox();
		public TextBox getObjectSearchTextBox();
	    public FlowPanel getActionSearchFlowPanel();
	    public MultipleSelect getSelectActions();
	    public ObidosMessageRow getMessageRow();
	    public Row getUsernameRow();
	    public ObidosPanelHeader getPanelHeader();
		public ObidosTimeBox getStartTimeBox();
		public ObidosTimeBox getEndTimeBox();
		public DatePicker getsDatePicker();
		public DatePicker geteDatePicker();

	}

	@NameToken(NameTokens.AUDIT_LOGS)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<AuditLogsPresenter> {
	}

	@Inject
	AuditLogsPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager, CurrentUser currentUser) {
		super(eventBus, view, proxy, placeManager, currentUser);
		getView().setUiHandlers(this);
	}

	protected void onBind() {
		super.onBind();
		DataGrid<AuditDTO> grid = getView().getDataGrid();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		showAuditLogs(selectionModel, grid, this);
	}

	protected void onReveal() {
		super.onReveal();
	}

	protected void onHide() {
		super.onHide();
	}

	protected void onUnbind() {
		super.onUnbind();
	}

	protected void onReset() {
		super.onReset();
		setTimeBoxValues();
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
		setColumnWidth(grid, 1, "10%");
	}

	private void setTimeBoxValues()
	{
		getView().getStartTimeBox().setText("12:00 AM");
		getView().getEndTimeBox().setText("12:00 AM");
	}
	
	private void setDateFormat()
	{
		String placeHolder = ClientUtils.getDateFormat(currentUser);
		getView().getsDatePicker().setPlaceholder(placeHolder);
		getView().geteDatePicker().setPlaceholder(placeHolder);
	}

	private void updatePanelHeader()
	{
		getView().getPanelHeader().setText(glang.actionHistory());
		if (ClientUtils.isAdmin(currentUser))
		{
			getView().getPanelHeader().setText(glang.auditLogs());
		}
	}
	
	private List<Option> getSelectItems()
	{
		List<Option> list = ClientUtils.getAuditLogSearchSelectItems(currentUser);
		return list;
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
	
	private void populateActionListBoxX()
	{
		ListBox lb = getView().getActionSearchListBox();
		lb.clear();
		lb.addItem(glang.auditLogLogin());
		lb.addItem(glang.auditLogSystem());
		lb.addItem(glang.auditLogLockUser());
		lb.addItem(glang.auditLogUnLockUser());
		lb.addItem(glang.auditLogTombstoneUser());
		lb.addItem(glang.auditLogRestoreUser());
		lb.addItem(glang.auditLogUser());
		lb.addItem(glang.auditLogItem());
		lb.addItem(glang.auditLogContainer());
		lb.addItem(glang.auditLogGroup());
		lb.addItem(glang.auditLogCreate());
		lb.addItem(glang.auditLogCreateItem());
		lb.addItem(glang.auditLogCreateContainer());
		lb.addItem(glang.auditLogUpdate());
		lb.addItem(glang.auditLogUpdateItem());
		lb.addItem(glang.auditLogUpdateContainer());
		lb.addItem(glang.auditLogDelete());
		lb.addItem(glang.auditLogDeleteItem());
		lb.addItem(glang.auditLogDeleteContainer());
		lb.addItem(glang.auditLogDeleteGroup());
		lb.addItem(glang.auditLogPasswordManagement());
		lb.addItem(glang.auditLogRevoke());
		lb.addItem(glang.auditLogRevokeItem());
		lb.addItem(glang.auditLogRevokeContainer());
		lb.addItem(glang.auditLogRelinquish());
		lb.addItem(glang.auditLogRelinquishItem());
		lb.addItem(glang.auditLogRelinquishContainer());
		lb.addItem(glang.auditLogShare());
		lb.addItem(glang.auditLogShareItem());
		lb.addItem(glang.auditLogShareContainer());
		lb.setMultipleSelect(true);
		lb.setItemSelected(0, false);
	}
	
	@Override
	public void clearForm() {
		showMessage(null);
		MyView v = getView();
		ListBox lb = v.getActionSearchListBox();
		for(int i=0; i < lb.getItemCount(); i++) {
			lb.setItemSelected(i, false);
		}
		v.getUsernameSearchTextBox().clear();
		v.getObjectSearchTextBox().clear();

		v.getsDatePicker().setValue(null);
		v.geteDatePicker().setValue(null);
		setTimeBoxValues();

		getView().getSelectActions().deselectAll();
	}

	@Override
	public void help() {
		showHelp();
	}

	@Override
	protected String getIdName() {
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote() {
		return getView().getHelpBlockQuote();
	}

	private ArrayList<String> convertToList(final TextBox textBox) {
		final String s = textBox.getValue();
		return (s == null || s.isEmpty()) ? null : new ArrayList<>(Arrays.asList(s.split("[ ,]+")));
	}

	/*
	private ArrayList<String> convertToList(final ListBox listBox) {
		final ArrayList<String> list = new ArrayList<>();

		for(int i=0; i < listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				list.add(listBox.getItemText(i));
			}
		}
		return list;
	}
	*/
	private ArrayList<String> convertToList(final MultipleSelect select)
	{
		final ArrayList<String> list =  (ArrayList<String>) select.getValue();
		return list;
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


	private void showAuditLogs(final SelectionModel<AuditDTO> selectionModel, final AbstractCellTable<AuditDTO> grid, HasHandlers source) {
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(glang.loading());
		grid.setEmptyTableWidget(messageLabel);

		addDateTimeButtonCellColumn(grid);
    	addTextColumn(grid, dto -> dto.getUsername(), glang.username());
    	
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

		new AsyncDataProvider<AuditDTO>() {
			@Override
			protected void onRangeChanged(HasData<AuditDTO> dto) {
				final Range range = dto.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				GwtAsyncWrapper<AuditResult> callback = new GwtAsyncWrapper<AuditResult>(source) {

					@Override
					public void uponFailure(Throwable caught) {
						gwtLog("Exception caught: " + caught.getMessage());
						updateRowCount(0, true);
						showErrorMessage(glang.couldNotFetchAuditLogs(caught.getMessage()));
					}

					@Override
					public void uponSuccess(AuditResult result) {
						List<AuditDTO> logs = result.getElements();
						if (logs != null && logs.size() > 0) {
							updateRowCount(result.getTotal(), true);
							updateRowData(start, logs);
						} else {
							messageLabel.setText(glang.noLogsFound());
							updateRowCount(0, true);
						}
					}
				};

				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				ArrayList<String> usernames = convertToList(getView().getUsernameSearchTextBox());
				ArrayList<String> objects   = convertToList(getView().getObjectSearchTextBox());
//				ArrayList<String> actions   = convertToList(getView().getActionSearchListBox());
				ArrayList<String> actions   = convertToList(getView().getSelectActions());
				gwtLog("Values: "+ actions);
				safeInvocationCall(() -> AuditService.Utility.getInstance().getAuditRecords(authCreds, actions, usernames, objects, getDateRange(), start, length, toArray(OrderBy.CREATE_TIME_DESC), callback));
			}
		}.addDataDisplay(grid);
	}

	@Override
	public void search() {
		refreshDataGrid(getView().getDataGrid());
	}

    private void refreshDataGrid(DataGrid<AuditDTO> grid) {
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}
    
	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
	private Date getStartDate()
	{
		return ClientUtils.getDateFromDatePickerAndTimeBox(currentUser, 
				getView().getsDatePicker(), 
				getView().getStartTimeBox(),
				getView().getMessageRow());
	}
	private Date getEndDate()
	{
		return ClientUtils.getDateFromDatePickerAndTimeBox(currentUser, 
				getView().geteDatePicker(), 
				getView().getEndTimeBox(),
				getView().getMessageRow());
	}
}

