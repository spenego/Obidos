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

package com.spenego.Obidos.client.application.viewusersingroup;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
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

public class ViewUsersInGroupPresenter
		extends Presenter<ViewUsersInGroupPresenter.MyView, ViewUsersInGroupPresenter.MyProxy>
		implements ViewUsersInGroupUiHandlers
{

	private ObidosMessages glang = ObidosMessages.LANG;
	interface MyView extends View, HasUiHandlers<ViewUsersInGroupUiHandlers>
	{
		public ObidosInputGroup getGroupNameInputGroup();
        public TextArea getCommentsTextArea();
        public DataGrid<LimitedUserDTO> getDataGrid();
        public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public TextBox getSearchTextBox();
		public Button getSearchButton();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
	}

	@NameToken(NameTokens.VIEW_USERS_IN_GROUP)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<ViewUsersInGroupPresenter>
	{
	}
	
	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

	@Inject
	ViewUsersInGroupPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		
		this.placeManager = placeManager;
		this.currentUser = currentUser;

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        showUsersInGroup(grid, this);
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
		refreshDataGrid();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
	}
	
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
	
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
		
	}
    private void showUsersInGroup(final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
	    grid.setEmptyTableWidget(messageLabel);
	    ObidosMessages lang = ObidosMessages.LANG;
	    
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
                Long groupId = ClientUtils.getGroupIdFromUrl(placeManager);
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
                UserService.Utility.getInstance().getUsersForGroup(authCreds, groupId, udto, SharedSetQuality.ONLY_SHARED_WITH, null, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
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
	protected <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
	}

	private void showUserInfo(final LimitedUserDTO dto)
	{
		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
	}
    public void refreshDataGrid()
    {
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}
    private void updateGroupInfo()
    {
        Long groupId = ClientUtils.getGroupIdFromUrl(placeManager);
        if (groupId == null) {
            return;
        }

        GwtAsyncWrapper<GroupDTO> callback = new GwtAsyncWrapper<GroupDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                GWT.log("Could not get group:" + caught.getMessage());
            }

            @Override
            public void uponSuccess(GroupDTO dto)
            {
                getView().getGroupNameInputGroup().setText(dto.getName());
                getView().getCommentsTextArea().setValue(dto.getComments());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getGroup(authCreds, groupId, callback);
    }

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}



}