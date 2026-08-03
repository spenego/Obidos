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

package com.spenego.Obidos.client.application.listloggedinusers;

import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Timer;
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
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;

public class ListLoggedInUsersPresenter
	extends ObidosPresenter<LimitedUserDTO, ListLoggedInUsersPresenter.MyView,
	ListLoggedInUsersPresenter.MyProxy, ListLoggedInUsersUiHandlers>
		implements ListLoggedInUsersUiHandlers
{

	private ObidosMessages glang = ObidosMessages.LANG;
	private Timer refreshTimer = null;

	interface MyView extends View, HasUiHandlers<ListLoggedInUsersUiHandlers>
	{
        public DataGrid<LimitedUserDTO> getDataGrid();
        public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public Button getSearchButton();
		public TextBox getSearchTextBox();
		public Row getSortRow();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public FormLabel getSearchLabel();
		public CheckBox getRefreshCheckBox();
		public ProgressBar getProgressbar();
	}

	@NameToken(NameTokens.LIST_LOGGED_IN_USERS)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListLoggedInUsersPresenter>
	{
	}

	@Inject
	ListLoggedInUsersPresenter(EventBus eventBus,
			MyView view,
			MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
        sOrderBy = OrderBy.USERNAME_ASC;
		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
		getView().getRefreshCheckBox().setValue(true);
		getView().getRefreshCheckBox().setText("");
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		showLoggedInUsers(selectionModel, grid, this);
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		cancelTimers();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage("");
        refreshDataGrid();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        startRefreshTimer();
	}
	
	private void cancelTimers()
	{
		cancelRefreshTimer();
	}
	
	private void cancelRefreshTimer()
	{
		if (refreshTimer != null)
		{
			gwtLog("Cancel refresh timer ...");
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}

	
	private void startRefreshTimer()
	{
		int delaySecs = 31;

		CheckBox cb = getView().getRefreshCheckBox();
		cb.setValue(true);
		ProgressBar pb = getView().getProgressbar();
		pb.setPercent(100);

//		getView().getRefreshCheckBox().setText("");

		cancelTimers();
		
		refreshTimer = new Timer() {
			int count = delaySecs;
			@Override
			public void run()
			{
				count--;
				if (count < 0)
				{
					count = 0;
				}
				double percent = 0.0;
				if (count > 0)
				{
					percent = 100 * (count * 1.0 / delaySecs);
					pb.setPercent(percent);
				}
				cb.setText(Integer.toString(count));
				if (count == 0)
				{
					pb.setPercent(0);
					count = delaySecs;
					gwtLog("Refresh ...........");
					refreshDataGrid();
				}
			}
		};
		refreshTimer.scheduleRepeating(1000);
	}

	@Override
	protected String getIdName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(getView().getDataGrid().getVisibleRange(),true);
		printRefreshTime();
    }

	private void showLoggedInUsers(final SelectionModel<LimitedUserDTO> selectionModel,
			final AbstractCellTable<LimitedUserDTO> grid,
			HasHandlers source)
	{
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);

	    // username
	    // dto does not have any username

	    // full name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserDTO, String> nameCol = new Column<LimitedUserDTO, String>(nameCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(nameCol, glang.fullname());
        
        // phone
    	ObidosButtonCell phoneCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_PHONE_NUMBER);
        final Column<LimitedUserDTO, String> phoneCol = new Column<LimitedUserDTO, String>(phoneCell)
        {
            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(phoneCol, glang.phone());

        // email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<LimitedUserDTO, String> emailCol = new Column<LimitedUserDTO, String>(emailCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(emailCol, glang.email());

        AsyncDataProvider<LimitedUserDTO> dataProvider = new AsyncDataProvider<LimitedUserDTO>()
		{

			@Override
			protected void onRangeChanged(HasData<LimitedUserDTO> userDTO)
			{
                final Range range = userDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
				{

					@Override
					public void uponSuccess(LimitedUserResult userResult)
					{
                        int numberOfUsers = userResult.getTotalUsers();
                        List<LimitedUserDTO> users = userResult.getUsers();
                        if (users != null)
                            gwtLog(">>> returned " + users.size());
                        else
                        {
                        	gwtLog("Count: " + numberOfUsers);
                        }

                        if (users != null && users.size() > 0)
                        {
                            updateRowCount(numberOfUsers, true);
                            updateRowData(start, users);
                        }
                        else
                        {
                        	if (ObidosConstants.TYPE_ADMINS.equals(ClientUtils.getTypeFromUrl(placeManager)))
                        	{
                        		messageLabel.setText("No admins found ...");
                        	}
                        	else
                        	{
                        		messageLabel.setText("No users found ...");
                        	}
                            updateRowCount(0, true);
                        }
                        if (!ClientUtils.isAdmin(currentUser))
                        {
                            messageLabel.setText("Only admins can search users");
                            updateRowCount(0, true);
                        }
					}

					@Override
					public void uponFailure(Throwable caught)
					{
                        updateRowCount(0, true);
                        return;
					}
			
				};

                if (!ClientUtils.isAdmin(currentUser)) // a stupid hack for Bug #144
                {
                    gwtLog(" Stupid hack... User is not admin, will not make RPC Call...");
                    return;
                }
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                    searchString = null;
                }

                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                // API does not support sorting
                // API does not support search either
                UserService.Utility.getInstance().getLoggedInUsers(authCreds, callback);
			} // end onRangeChanged()
		};

        nameCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {
            @Override
            public void update(int idx, LimitedUserDTO dto, String value)
            {
        		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
            }

        });

        // noop but the handler must exist
        emailCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {
            @Override
            public void update(int idx, LimitedUserDTO dto, String value)
            {
            }
        });

		
        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
	}
	private void setOrderBy(OrderBy orderBy)
	{
		sOrderBy = orderBy;
	}

	@Override
	public void help()
	{
		gwtLog("Show help");
		showHelp();
	}

	@Override
	public void search()
	{
		refreshDataGrid();
	}

	@Override
	public void sortByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		refreshDataGrid();
	}

	@Override
	public void sortByAZ()
	{
		setOrderBy(OrderBy.USERNAME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.USERNAME_DESC);
		refreshDataGrid();
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}
	
	private void printRefreshTime()
	{	
		Date date = new Date();
		String fdate = ClientUtils.formattedDate(date);
		String msg = "Last refreshed on: " + fdate;
		showMessage(msg);
	}

	@Override
	public void refresh()
	{
		refreshDataGrid();
	
	}

	@Override
	public void refreshTimerCheckBoxCallback()
	{
		CheckBox cb = getView().getRefreshCheckBox();
		boolean on = cb.getValue();
		if (! on)
		{
//			cb.setText(glang.off());
			cancelRefreshTimer();
//			getView().getProgressbar().setPercent(100);
		}
		else
		{
			startRefreshTimer();
		}
	}


}