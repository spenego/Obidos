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

package com.spenego.Obidos.client.application.listlockedusers;

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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
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
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class ListLockedUsersPresenter
		extends ObidosPresenter<LimitedUserForAdminDTO, ListLockedUsersPresenter.MyView, ListLockedUsersPresenter.MyProxy, ListLockedUsersUiHandlers>
		implements ListLockedUsersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	interface MyView extends View, HasUiHandlers<ListLockedUsersUiHandlers>
	{
		public Button getUnlockButton();
		public Button getClearButton();
		public BlockQuote getHelpBlockQuote();
		public DataGrid<LimitedUserForAdminDTO> getDataGrid();
		public SimplePager getPager();
		public Button getSearchButton();
		public TextBox getSearchTextBox();
		public ObidosButtonToolBar getButtonToolBar();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
	}

	@NameToken(NameTokens.LOCKED_USERS)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListLockedUsersPresenter>
	{
	}

	@Inject
	ListLockedUsersPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
		sOrderBy = OrderBy.USERNAME_ASC;
		getView().setUiHandlers(this);
	}

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showUserList(selectionModel, grid, this));
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
        showMessage(null);
        getView().getSearchTextBox().setValue("");
        getView().getUnlockButton().setText(ObidosMessages.LANG.unlockUsers());
        getView().getButtonToolBar().adjustButtonsWidth();
        getView().getPanelHeader().setTitle(glang.listLockedUsers());
        clearSelections();
        // Issue #719
        enableUnlockButton();
        adjustButtonsWidth();
        
        refreshDataGrid();
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        
        // In admin view, the width of the button becomes 127px in element.style
        // this seems to overwrite it
        ClientUtils.adjustButtonWidth(getView().getClearButton(), "auto");
        grid.setColumnWidth(0, "50px");
	}
	
	private void enableUnlockButton()
	{
		if (currentUser == null)
		{
			return;
		}
		UserDTO userDTO = currentUser.getUserDTO();
		if (userDTO == null)
		{
			return;
		}
		CapabilityDTO cdto = userDTO.getCapabilities();
		if (cdto == null)
		{
			return;
		}
    	String type = ClientUtils.getTypeFromUrl(placeManager);
    	boolean unlockAdmins = false;
    	if (ObidosConstants.TYPE_ADMINS.equals(type))
    	{
    		unlockAdmins = true;
    	}

		Button b = getView().getUnlockButton();
		b.setEnabled(true);
		b.setTitle("");

		if (unlockAdmins)
		{
			if (! cdto.getLockAdmin())
			{
				b.setEnabled(false);
				b.setTitle(glang.nocapToUnlockAdmins());
			}
		}
		else
		{
			if (! cdto.getLockUser())
			{
				b.setEnabled(false);
				b.setTitle(glang.nocapToUnlockUsers());
			}
		}
	}
	
	private void adjustButtonsWidth()
	{
	}

	private void unlockAccounts()
	{
		gwtLog("Unlock accounts..");
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null)
		{
			showErrorMessage("No users selected..");
			return;
		}

		for (Long id : ids)
		{
			gwtLog("Unlock id: " + id);
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearSelections();
				refreshDataGrid();
				showMessage("Successfully unlocked: " + ids.size() + " users");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not unlock users: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().unlockUsers(authCreds, ids, callback);
	}
	
	@Override
	public void unlockUsers()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, true))
		{
			return;
		}
		unlockAccounts();
	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
		showMessage("");
	}

    private void enableButtons(boolean enabled)
    {
   		Button b = getView().getUnlockButton();
    	getView().getUnlockButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
    	String type = ClientUtils.getTypeFromUrl(placeManager);
    	boolean unlockAdmins = false;
    	if (ObidosConstants.TYPE_ADMINS.equals(type))
    	{
    		unlockAdmins = true;
    		getView().getPanelHeader().setTitle(glang.listLockedAdmins());
    	}
    	else
    	{
    		getView().getPanelHeader().setTitle(glang.listLockedUsers());
    	}
    	if (enabled)
    	{
    		int sz = selectionModel.getSelectedSet().size();
    		if(sz == 0)
    		{
    			return;
    		}
    		if (selectionModel.getSelectedSet().size() == 1)
    		{
    			b.setText(glang.unlockUser());
    			if (unlockAdmins)
    			{
    				b.setText(glang.unlockAdmin());
    			}
    		}
    		else
    		{
    			b.setText(ObidosMessages.LANG.unlockUsers());
    			if (unlockAdmins)
    			{
    				b.setText(glang.unlockAdmins());
    			}

    		}
    	}
    	else
    	{
   			b.setText(ObidosMessages.LANG.unlockUsers());
			if (unlockAdmins)
			{
				b.setText(glang.unlockAdmins());
			}

    	}
    	enableUnlockButton();
    }


	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void search()
	{
		refreshDataGrid();
	}

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
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
    private void showUserList(final SelectionModel<LimitedUserForAdminDTO> selectionModel,
    		final AbstractCellTable<LimitedUserForAdminDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(lang.loading());
	    grid.setEmptyTableWidget(messageLabel);

        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
        addObidosButtonCellColumn(grid, dto -> dto.getUsername(), lang.username(), ObidosConstants.CELL_TYPE_LOCKED_USER);
        
   	    // Name (fullname)
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserForAdminDTO, String> nameCol = new Column<LimitedUserForAdminDTO, String>(nameCell)
        {

            @Override
            public String getValue(LimitedUserForAdminDTO dto)
            {
                return "n/a";
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

                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();

   				ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<LimitedUserForAdminDTO>) selectionModel);
   				if (preSelectedIds != null)
   				{
				    for (Long id : preSelectedIds)
					{
						   gwtLog("XXXX >>> Sending preselected id: " + id);
					}
   				}
   				else
   				{
   					gwtLog(">>>>>>>>>>>>>>>> Pre selected ids are null");
   				}
   				String searchString = getView().getSearchTextBox().getValue();
   				if (searchString == null || searchString.length() == 0)
   				{
   					searchString = null;
   				}
   				UserDTO dto = new UserDTO();
   				dto.setUsername(searchString);
   				dto.setLocked(true);
   				dto.setDeleted(false);
   				String type = ClientUtils.getTypeFromUrl(placeManager);
				dto.setAdministrator(false); // Issue #666
   				if (ObidosConstants.TYPE_ADMINS.equals(type))
   				{
   					dto.setAdministrator(true);
   				}
   				
   				safeInvocationCall(() -> UserService.Utility.getInstance().getUsers(authCreds, dto, preSelectedIds, start, length, getOrderByList(), callback));
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
	
	private OrderBy getOrderBy()
	{
		return sOrderBy;
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


    
}