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

package com.spenego.Obidos.client.application.listusersingroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class ListUsersInGroupPresenter
        extends ObidosPresenter<LimitedUserDTO, ListUsersInGroupPresenter.MyView, ListUsersInGroupPresenter.MyProxy, ListUsersInGroupUiHandlers>
        implements ListUsersInGroupUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	// Fix #228
    interface MyView extends View, HasUiHandlers<ListUsersInGroupUiHandlers>
    {
        public ObidosTextBox getGroupNameTextBox();
        public DataGrid<LimitedUserDTO> getDataGrid();
        public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public TextBox getSearchTextBox();
		public Button getSearchButton();
		public ObidosMessageRow getMessageRow();
		public Button getRemoveButton();
		public Button getClearButton();
		public ObidosPanelHeader getPanelHeader();
		public ObidosTextBox getCommentsTextBox();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.GROUP_ID;
	}

    @NameToken(NameTokens.LIST_USERS_IN_GROUP)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ListUsersInGroupPresenter>
    {
    }

    @Inject
    ListUsersInGroupPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
    		final CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    private void selectedUser(Set<LimitedUserDTO> dtoSet) {
	if (dtoSet.size() > 0)
	{
		enableButtons(true);
		getView().getSearchTextBox().setValue("");
	} else {
		showMessage("");
		enableButtons(false);
	}
    }

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> selectedUser(dto), grid -> showUsersInGroup(selectionModel, grid, this));
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
        updateGroupInfo();
        enableButtons(false);
        clearSelections();
        refreshDataGrid();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        setColumnWidth(grid, 0, "55px");  // Checkbox column
    }
    
    private void enableButtons(boolean enabled)
    {
    	getView().getRemoveButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
    }

    private void updateGroupInfo()
    {
        Long groupId = getId();
        if (groupId == null) {
            return;
        }

        GwtAsyncWrapper<GroupDTO> callback = new GwtAsyncWrapper<GroupDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not retrieve group: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(GroupDTO dto)
            {
                getView().getGroupNameTextBox().setValue(dto.getName());
                getView().getCommentsTextBox().setValue(dto.getComments());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getGroup(authCreds, groupId, callback);
    }

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }
    private void showUsersInGroup(final SelectionModel<LimitedUserDTO> selectionModel,
    		final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
	    grid.setEmptyTableWidget(messageLabel);
	    ObidosMessages lang = ObidosMessages.LANG;
	    
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

        // fullname
        ObidosButtonCell fullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserDTO, String> fullnameCol = new Column<LimitedUserDTO, String>(fullnameCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(fullnameCol, glang.fullname());


        // email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<LimitedUserDTO, String> emailCol = new Column<LimitedUserDTO, String>(emailCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return "n/a";
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
                GWT.log(" in AsyncDataProvider start: " + start);
                GWT.log("in AsyncDataProvider length: " + length);
                GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        String message = e.getMessage();
                        GWT.log("Error: " + message);
                        //("Exception received: " + e.getMessage());
                        updateRowCount(0, true);
                        // TODO: use an exception type instead of a message
                        // TODO: exceptions are not serialized for some reason
//                        if (message.equals("Session error"))
//                        if ((e instanceof SessionErrorException)    ||
//                            (e instanceof SessionMismatchException))
//                        {
//                            Log.info()->"Redirect to login page");
//                            getUiHandlers().redirectToLoginDialogPage();
//                        }
//                        else
//                        {
                            // TODO: show error
//                        }
                        return;
                    }

                    @Override
                    public void uponSuccess(LimitedUserResult userResult)
                    {
                        int numberOfUsers = userResult.getTotalUsers();
                        List<LimitedUserDTO> users = userResult.getUsers();
                        if (users != null && users.size() > 0)
                        {
                            updateRowCount(numberOfUsers, true);
                            updateRowData(start, users);
                        }
                        else
                        {
                            updateRowCount(0, true);
                            messageLabel.setText("No Users found in the Group");
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Long groupId = getId();
                if (groupId == null) {
                    return;
                }
                String search = getView().getSearchTextBox().getValue();
                UserDTO udto = null;
                if (search.length() > 0)
                {
                	udto = new UserDTO();
                	udto.setDeleted(false);
                	// Issue #387
                	udto.setFullname(search);
                	
                }
				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
                UserService.Utility.getInstance().getUsersForGroup(authCreds, groupId, udto, SharedSetQuality.ONLY_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
            } // end onRangeChanged()
        };
        
        fullnameCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserDTO dto, String value)
            {
            	showUserInfo(dto);
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

    private void promptRemoveUsers()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        String title = lang.removeUserFromGroup();
        String message = lang.removeUsersFromGroupWarning(getView().getGroupNameTextBox().getValue());
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
            }
        });

        // No
       options.addButton(lang.no(), ButtonType.DEFAULT.getCssName(), new SimpleCallback()
       {
            @Override
            public void callback()
            {
            }
       });

        // Yes
        options.addButton(lang.yes(), ButtonType.DANGER.getCssName(), new SimpleCallback()
        {

            @Override
            public void callback()
            {
                removeUsersFromGroup();
            }
        });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    private void removeUsersFromGroup()
    {
    	ArrayList<Long> ids = getSelectedIds(selectionModel);
    	for (Long id : ids)
		{
    		gwtLog("Select id: " + id);
		}
    	if (ids == null)
    	{
    		return;
    	}
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Could not remove users from Group: " + e.getMessage());
            }

            @Override
            public void uponSuccess(Void e)
            {
            	clearSelections(); // Issue #638
                refreshDataGrid();
            }
        };
        Long groupId = getId();
        if (groupId == null) {
            return;
        }
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().removeUsersFromGroup(authCreds, ids, groupId, callback);
    }

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
	}

	@Override
	public void removeUsers()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		promptRemoveUsers();
	}

	private void showUserInfo(final LimitedUserDTO dto)
	{
		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
	}

	@Override
	public void listGroups()
	{
		ClientUtils.showPage(placeManager, NameTokens.LIST_GROUPS);
	}

	@Override
	public void back()
	{
			ClientUtils.goBack(placeManager);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
}
