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

package com.spenego.Obidos.client.application.listdeletedusers;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
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
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class ListDeletedUsersPresenter
		extends ObidosPresenter<LimitedUserForAdminDTO, ListDeletedUsersPresenter.MyView, ListDeletedUsersPresenter.MyProxy, ListDeletedUsersUiHandlers>
		implements ListDeletedUsersUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
	ProvidesKey<LimitedUserForAdminDTO> keyProvider = new ProvidesKey<LimitedUserForAdminDTO>()
	{
	    public Object getKey(LimitedUserForAdminDTO dto)
	    {
	      return dto == null ? null : dto.getId();
	    }
	};

	interface MyView extends View, HasUiHandlers<ListDeletedUsersUiHandlers>
	{
		public Button getRestoreButton();
		public ObidosMessageRow getMessageRow();
		public BlockQuote getHelpBlockQuote();
		public DataGrid<LimitedUserForAdminDTO> getDataGrid();
		public SimplePager getPager();
		public Button getSearchButton();
		public TextBox getSearchTextBox();
		public TextBox getNewUsernameTextBox();
		public ObidosPanelHeader getPanelHeader();
		public Button getSortButton();
		public Button getClearButton();
	}

	@NameToken(NameTokens.DELETED_USERS)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListDeletedUsersPresenter>
	{
	}

	@Inject
	ListDeletedUsersPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);

		getView().setUiHandlers(this);
	}

    protected void onBind()
    {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showUserList(selectionModel, grid, this));
    	/*
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		showUserList(selectionModel, grid, this);
		*/
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
		resetForm();
        adjustButtons();
        updateButtonTitle();
        clearSelections();
        refreshDataGrid();
        
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        // In admin view, the width of the button becomes 127px in element.style
        // this seems to overwrite it
        ClientUtils.adjustButtonWidth(getView().getClearButton(), "auto");
//        ClientUtils.adjustButtonWidth(getView().getSortButton(), "auto");
        grid.setColumnWidth(0, "50px");
	}
	
	private void resetForm()
	{
        showMessage(null);
        getView().getSearchTextBox().setValue(null);
        getView().getNewUsernameTextBox().setValue(null);
		getView().getNewUsernameTextBox().setEnabled(true);
	}
	
	private void adjustButtons()
	{
		Button b1 = getView().getRestoreButton();
//		ClientUtils.adjustButtonsWidth(b1, b2);
	}
	
	private void updateButtonTitle()
	{
		String type = ClientUtils.getTypeFromUrl(placeManager);
		Button b = getView().getRestoreButton();
		b.setText(glang.restoreUser());
		getView().getPanelHeader().setText(glang.tombstonedUsers());
		if (ObidosConstants.TYPE_ADMINS.equals(type))
		{
			b.setText(glang.restoreAdmin());
			getView().getPanelHeader().setText(glang.tombstonedAdmins());
		}
	}
	private void enableButtons(boolean enabled)
	{
		getView().getNewUsernameTextBox().setEnabled(true);
		getView().getRestoreButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids != null && ids.size() > 1)
		{
			getView().getNewUsernameTextBox().setEnabled(false);
		}
	}

	@Override
	public void search()
	{
		refreshDataGrid();
	}

	@Override
	public void help()
	{
		showHelp();
	}
	
	// a new username can be specified only if oner user is selected, otherwise
	// names will be appended with a number in case of conflict
	private ArrayList<String> getNewUsernames(final ArrayList<Long> userIds)
	{
		ArrayList<String> names = null;
		String username = getView().getNewUsernameTextBox().getValue();
		if (username != null && username.length() > 0)
		{
			if (userIds.size() == 1)
			{
				gwtLog("New username: " + username);
				names = new ArrayList<>();
				names.add(username);
			}
		}
		return names;
	}

	@Override
	public void undeleteUser()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
		{
			return;
		}
		ArrayList<Long> userIds = getSelectedIds(selectionModel);
		if (userIds == null)
		{
			showErrorMessage("No users selected..");
			return;
		}


		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void x)
			{
				clearSelections();
				refreshDataGrid();
				String s = " user";
				if (userIds.size() > 1)
				{
					s = " users";
				}
				showMessage("Successfully restored " + userIds.size() + s);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(glang.couldNotRestoreUser() + ": " + caught.getMessage());
			}
		};

		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		safeInvocationCall(() -> UserService.Utility.getInstance().restoreUsers(authCreds, userIds, getNewUsernames(userIds), callback));
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
    public void refreshDataGrid()
    {
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void showUserList(final SelectionModel<LimitedUserForAdminDTO> selectionModel,
    		final AbstractCellTable<LimitedUserForAdminDTO> grid, HasHandlers source)
    {
        gwtLog("Entering showUserList");

    	
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);

        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

        addObidosButtonCellColumn(grid, dto -> dto.getUsername(), glang.username(), ObidosConstants.CELL_TYPE_MARKED_DELETED_USER);
   	    // username
        /*
        TextColumn<LimitedUserForAdminDTO> usernameCol = new TextColumn<LimitedUserForAdminDTO>()
		{

			@Override
			public String getValue(LimitedUserForAdminDTO dto)
			{
				if (dto != null)
				{
					return dto.getUsername();
				}
				else
				{
					return glang.na();
				}
			}
		};
        grid.addColumn(usernameCol, glang.username());
        */


   	    // Name (fullname)
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserForAdminDTO, String> nameCol = new Column<LimitedUserForAdminDTO, String>(nameCell)
        {

            @Override
            public String getValue(LimitedUserForAdminDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(nameCol, glang.fullname());

        // email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<LimitedUserForAdminDTO, String> emailCol = new Column<LimitedUserForAdminDTO, String>(emailCell)
        {

            @Override
            public String getValue(LimitedUserForAdminDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(emailCol, glang.email());

        /*
        TextColumn<LimitedUserForAdminDTO> emailCol = new TextColumn<LimitedUserForAdminDTO>()
		{

			@Override
			public String getValue(LimitedUserForAdminDTO dto)
			{
				if (dto != null)
				{
					return dto.getEmail1();
				}
				else
				{
					return glang.na();
				}
			}
		};
        grid.addColumn(emailCol, glang.email());
        */

        AsyncDataProvider<LimitedUserForAdminDTO> dataProvider = new AsyncDataProvider<LimitedUserForAdminDTO>()
        {
           @Override
            protected void onRangeChanged(HasData<LimitedUserForAdminDTO> userDTO)
            {
                final Range range = userDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                GwtAsyncWrapper<LimitedUserForAdminResult> callback = new GwtAsyncWrapper<LimitedUserForAdminResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        Window.alert("Exception received: " + e.getMessage());
                        updateRowCount(0, true);
                        return;
                    }

                    @Override
                    public void uponSuccess(LimitedUserForAdminResult userResult)
                    {
                        int numberOfUsers = userResult.getTotalUsers();
                        List<LimitedUserForAdminDTO> users = userResult.getUsers();
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
                            messageLabel.setText("No users found ...");
                            updateRowCount(0, true);
                        }
                        if (!ClientUtils.isAdmin(currentUser))
                        {
                            messageLabel.setText("Only admins can search users");
                            updateRowCount(0, true);
                        }
                    }
                };
                if (!ClientUtils.isAdmin(currentUser)) // a stupid hack for Bug #144
                {
                    gwtLog(" Stupid hack... User is not admin, will not make RPC Call...");
                    return;
                }

   				String searchString = getView().getSearchTextBox().getValue();
   				if (searchString == null || searchString.length() == 0)
   				{
   					searchString = null;
   				}
   				UserDTO dto = new UserDTO(searchString, true, true);
   				ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<LimitedUserForAdminDTO>) selectionModel);
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                String type = ClientUtils.getTypeFromUrl(placeManager);
               	dto.setAdministrator(false); // Issue #666
                if (ObidosConstants.TYPE_ADMINS.equals(type))
                {
                	dto.setAdministrator(true);
                }
				safeInvocationCall(()->UserService.Utility.getInstance().getUsers(authCreds, dto, preSelectedIds, start, length, getOrderByList(), callback));
            } // end onRangeChanged()
        };
        nameCol.setFieldUpdater(new FieldUpdater<LimitedUserForAdminDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserForAdminDTO dto, String value)
            {
        		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
            }

        });

        // noop but the handler must exist
        emailCol.setFieldUpdater(new FieldUpdater<LimitedUserForAdminDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserForAdminDTO dto, String value)
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
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
		showMessage("");
	}

}