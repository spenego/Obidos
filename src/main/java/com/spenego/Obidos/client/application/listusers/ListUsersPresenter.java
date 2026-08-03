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

package com.spenego.Obidos.client.application.listusers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBoxButton;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
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
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
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
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;



public class ListUsersPresenter
	extends ObidosPresenter<LimitedUserForAdminDTO, ListUsersPresenter.MyView, 
	ListUsersPresenter.MyProxy, ListUsersUiHandlers>
    implements ListUsersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private int nSelected = -1;
	private boolean mSelected = false;

    interface MyView extends View, HasUiHandlers<ListUsersUiHandlers>
    {
        public DataGrid<LimitedUserForAdminDTO> getDataGrid();
        public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public Button getDeleteButton();
		public FormLabel getFormErrorLabel();
		public Button getSearchButton();
		public TextBox getSearchTextBox();
		public Button getLockButton();
		public Row getSelectCheckBoxRow();
		public CheckBoxButton getSelectCheckBoxButton();
		public ObidosButtonToolBar getButtonToolBar();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public FormLabel getSearchLabel();
    }

    @NameToken(NameTokens.LIST_USERS)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListUsersPresenter>
    {
    }


    @Inject
    ListUsersPresenter(EventBus eventBus,
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
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size()), grid -> showUserList(selectionModel, grid, this));
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
        gwtLog(this.getClass().getSimpleName() + " onHide()");
        clearForm();
        clearSelections();
    }

    protected void onUnbind()
    {
        super.onUnbind();
        gwtLog(this.getClass().getSimpleName() + " onUnbind()");
    }

    protected void onReset()
    {
        super.onReset();
        showMessage("");
        clearForm();
        getView().getSearchTextBox().setValue("");
        getView().getButtonToolBar().adjustButtonsWidth();
        updateSearchLabel();
        clearSelections();
        updateForm();
        // Note we can not clear selections in this presenter as this is the
        // main ApplicationPresenter it will wipe out selection when a search
        // is done
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        refreshDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        grid.setColumnWidth(0, "50px");
    }
	private void clearForm()
	{
		nSelected = -1;
		mSelected = false;
	}
	
	private void updateSearchLabel()
	{
		FormLabel label = getView().getSearchLabel();
		label.setText(glang.searchUsers());
		String type = ClientUtils.getTypeFromUrl(placeManager);
		if ("admins".equals(type))
		{
			label.setText(glang.searchAdmins());
		}
	}

    private void updateForm()
    {
    	getView().getPanelHeader().setTitle(glang.activeUsers());
    	if (ClientUtils.listingAdmins(placeManager))
    	{
    	getView().getPanelHeader().setTitle(glang.activeAdmins());
    	}
    }
    
    private void enableButtons(int size)
    {
    	boolean enabled = size > 0;
    	boolean canDelete = false;
    	boolean canLock = false;
   		getView().getDeleteButton().setEnabled(enabled);
   		getView().getLockButton().setEnabled(enabled);

   		getView().getDeleteButton().setText(glang.markAsDeleted());
   		getView().getLockButton().setText(glang.markAsLocked());
   		getView().getDeleteButton().setTitle("");
   		getView().getLockButton().setTitle("");
    	if (ClientUtils.hasDeleteCapability(placeManager, currentUser))
    	{
    		canDelete = true;
    	}

    	if (ClientUtils.hasLockCapability(placeManager, currentUser))
    	{
    		canLock = true;
    	}

    	if (!canDelete && !canLock)
    	{
    		clearCheckBoxSelections(selectionModel);
    		getView().getDeleteButton().setText("✗ " + glang.markAsDeleted());
    		getView().getLockButton().setText("✗ " + glang.markAsLocked());

    		getView().getDeleteButton().setTitle(glang.noPermissionToDelete());
    		getView().getLockButton().setTitle(glang.noPermissionToLock());
    	}
    	else if (!canDelete)
    	{
    		getView().getDeleteButton().setEnabled(false);
    		getView().getDeleteButton().setText("✗ " + glang.markAsDeleted());
    		getView().getLockButton().setTitle(glang.noPermissionToDelete());
    	}
    	else if (!canLock)
    	{
    		getView().getLockButton().setEnabled(false);
    		getView().getLockButton().setText("✗ " + glang.markAsLocked());
    		getView().getLockButton().setTitle(glang.noPermissionToLock());
    	}
    	CheckBoxButton cbb = getView().getSelectCheckBoxButton();
    	
    	if (size > 0)
    	{
    		nSelected = size;
			cbb.setText(glang.clear());
			cbb.setValue(true);
//    		showMessage(glang.rowSelected(size, glang.users()));
    	}
    	else
    	{
    		showMessage("");
			nSelected = -1;
			cbb.setText(glang.select());
			cbb.setValue(false);
    	}
    }



    @Override
    public void redirectToLoginDialogPage()
    {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.LOGIN).build());
    }

    @Override
    public void showEditUserScreen(LimitedUserDTO userDTO)
    {
        // Bug #78, do not use events
        String type = null;
        try
        {
            type = ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.TYPE);
        } catch (ParamNotFoundException e)
        {
        }

        String nameToken = NameTokens.EDIT_USER;

        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.USER_ID, userDTO.getId().toString());
        if (type != null)
        {
            with.put(ObidosConstants.TYPE, type);
            if (type.equals(ObidosConstants.LIST_ADMINS))
            {
            	nameToken = NameTokens.EDIT_ADMIN;
            }
        }
        
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    @Override
    public void listUsers()
    {
    }

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(getView().getDataGrid().getVisibleRange(),true);
    }

    private void showUserList(final SelectionModel<LimitedUserForAdminDTO> selectionModel,
    		final AbstractCellTable<LimitedUserForAdminDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);
	    

        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
    	addCellColumn(grid, dto -> dto.getUsername(), glang.username(), new ColorCell());	    // Use a custom cell so that we can highlight the cell if the user is blocked
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

//    	addTextColumn(grid, dto -> dto.getFullname(), glang.fullname());
//    	addTextColumn(grid, dto -> dto.getEmail1(),   glang.email());
        // email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<LimitedUserForAdminDTO, String> emailCol = new Column<LimitedUserForAdminDTO, String>(emailCell)
        {

            @Override
            public String getValue(LimitedUserForAdminDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(emailCol, glang.email());

    	// LimitedUserForAdminDTO does not have much info, therefore we can not
    	// make Edit button active or inactive! That means it will be on for
    	// any admin which is not correct behavior.
    	final Column<LimitedUserForAdminDTO, String> editCol = addObidosButtonCellColumn(grid, dto -> glang.editButtonTitle(), glang.editButtonTitle(), ObidosConstants.CELL_TYPE_EDIT);

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
   				String type = ClientUtils.getTypeFromUrl(placeManager);
   				UserDTO dto = new UserDTO();
   				dto.setUsername(searchString);
   				dto.setLocked(false);
   				dto.setDeleted(false);
   				dto.setAdministrator(false);
   				if (ObidosConstants.TYPE_ADMINS.equals(type))
   				{
   					dto.setAdministrator(true);
   				}
				UserService.Utility.getInstance().getUsers(authCreds, dto, preSelectedIds, start, length, getOrderByList(), callback);
            } // end onRangeChanged()
        };


         // handler for Edit button
         editCol.setFieldUpdater(new FieldUpdater<LimitedUserForAdminDTO, String>()
         {
            @Override
            public void update(int idx, LimitedUserForAdminDTO userDTO, String value)
            {
                resetSearchBox();
                showEditUserScreen(userDTO);
            }
         });
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

    private void resetSearchBox()
    {
        gwtLog("Resetting searchbox");
        ClientUtils.setSearchboxText(this, "");
    }
    /**
     * Super simple way to color a cell in DataGrid. I think GWT's example is a complex garbage and very hard
     * to understand.
     * This cell is used to color the bg and fg color of the blocked username cell.
     *
     * context  - LimitedUserDTO (we have to typecast to get it)
     * value    - The string returned by getValue() method in the Column creation block
     * sb       - We must construct it using value, which will be rendered in the cell
     * @author spgdev@spenego.com - Apr 29, 2017
     */
    static class ColorCell extends AbstractCell<String>
    {
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb)
        {
            if (value == null)
            {
                return;
            }

            LimitedUserDTO luserDTO = (LimitedUserDTO) context.getKey();
            if (luserDTO == null)
            {
                return;
            }

            SafeHtml safeHtml = null;
            if (luserDTO.getLocked() != null && luserDTO.getLocked())
            {
                // lockUser is defined in obidos.css. Change colors there if desired
                // balloon CSS is defined in balloon.min.css
                safeHtml = SafeHtmlUtils.fromTrustedString(
                "<div class=\"lockedUser\">" +
                "<span data-balloon=\"Locked User\"" + " data-balloon-pos=\"right\">" +
                        value +
                "</span>" +
                "</div>");
            }
            else
            {
                safeHtml = SafeHtmlUtils.fromSafeConstant(value);
            }
            sb.append(safeHtml);
        }
    }
    /*private boolean isAdminSearch()
    {
        String key = ObidosConstants.TYPE;
        try
        {
            String param = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }*/

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

	@Override
	public void tombstoneUsers()
	{
		promptTombstoneUsers();
	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(0);
        showMessage("");
	}

	@Override
	public void help()
	{
		showHelp();
	}
    private void promptTombstoneUsers()
    {
    	LicenseStats license = currentUser.getLoginResult().getLicenseStats();
    	gwtLog("License state: " + license.getLicenseExpirationState());
    		
        String title = glang.markAsDeleted();
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		int sz = ids.size();
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.USER_ID_N);

		String message = glang.deleteUsersWarning(sz, s, s, s, s, s);
    	String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.TYPE_ADMINS.equals(type))
		{
			s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ADMIN_ID_N);
			message = glang.deleteAdminsWarning(sz, s, s, s);
		}
       ClientUtils.promptForAction(() -> tombstoneUsersReal(), title, message);
    }

    private void promptLockUsers()
    {
        String title = glang.lockUser();
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		int sz = ids.size();
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.USER_ID_N);
		String message = glang.lockUsersWarning(ids.size(), s, s, s, s, s);
    	String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.TYPE_ADMINS.equals(type))
		{
			s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ADMIN_ID_N);
			message = glang.lockAdminsWarning(sz, s, s, s);
		}
       ClientUtils.promptForAction(() -> lockUsersReal(), title, message);
    }
    
    private String makeTombstoneMessage(int size, String errorMessage)
    {
    	String type = ClientUtils.getTypeFromUrl(placeManager);
    	if (ObidosConstants.TYPE_ADMINS.equals(type))
    	{
    		return glang.couldNotTombstone(size > 1 ? glang.admins() : glang.admin(), errorMessage);
    	}
    	else
    	{
    		return glang.couldNotTombstone(size > 1 ? glang.users() : glang.user(), errorMessage);
    	}
    }

	private void tombstoneUsersReal()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage(glang.noItemsSelected());
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				gwtLog("User deleted......");
				clearSelections();
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				String emsg = makeTombstoneMessage(ids.size(), caught.getMessage());
				gwtLog(emsg);
				showErrorMessage(emsg);
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().deleteUsers(authCreds, ids, callback);
	}

	private void lockUsersReal()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage(glang.noItemsSelected());
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearSelections();
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not Lock: "+ caught.getMessage());
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().lockUsers(authCreds, ids, callback);
	}
	@Override
	public void search()
	{
		refreshDataGrid();
	}
	
	/*private SearchParams getSearchParams()
	{
		SearchParams s = new SearchParams();
		String type = ClientUtils.getTypeFromUrl(placeManager);
		String locked = ClientUtils.getLockedFromUrl(placeManager);
		String deleted = ClientUtils.getDeletedFromUrl(placeManager);
		s.setListUsers(true);
		if (ObidosConstants.LIST_ADMINS.equals(type))
		{
			s.setListUsers(false);
		}
		if (ObidosConstants.TRUE.equals(locked))
		{
			s.setLocked(true);
		}
		if (ObidosConstants.TRUE.equals(deleted))
		{
			s.setLocked(true);
			s.setDeleted(true);
		}
		return s;
	}*/

	@Override
	public void lockUsers()
	{
		promptLockUsers();
	}
	
	private int getNumberOfSelectedRows()
	{
		Set<LimitedUserForAdminDTO> dtos = selectionModel.getSelectedSet();
		return dtos.size();
	}

	@Override
	public void selectCheckBoxCallback()
	{
		CheckBoxButton cbb  = getView().getSelectCheckBoxButton();
		boolean select = cbb.getValue();
		int n = getNumberOfSelectedRows();
		
		gwtLog("XXX select: " + select + " n: " + n + " label:" + cbb.getText());
		gwtLog("XXX mSelect: " + mSelected + " nSelected: " + nSelected + " label:" + cbb.getText());
		if (nSelected != -1 && (n == nSelected && mSelected == select))
		{
			if (nSelected > 0 && mSelected == false)
			{
				clearSelections();
			}
			gwtLog("Duplicate...........");
			return;
		}
//		gwtLog("Last select: " + mSelected + " Last n: " + nSelected);
		gwtLog("<<<>>>> select: " + select + " n: " + n + " label:" + cbb.getText());
		gwtLog("<<<>>>> mSelect: " + mSelected + " nSelected: " + nSelected + " label:" + cbb.getText());
		
		for (LimitedUserForAdminDTO dto : getView().getDataGrid().getVisibleItems())
		{
			selectionModel.setSelected(dto, select);
		}
		
		n = getNumberOfSelectedRows();
		gwtLog(">>> select: " + select + " n: " + n + " label:" + cbb.getText());
		mSelected = select;
		nSelected = n;


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

