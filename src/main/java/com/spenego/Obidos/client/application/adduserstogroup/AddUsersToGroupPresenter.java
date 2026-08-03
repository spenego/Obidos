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

package com.spenego.Obidos.client.application.adduserstogroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
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
import com.spenego.Obidos.shared.exceptions.DuplicateRecordException;

public class AddUsersToGroupPresenter

        extends ObidosPresenter<LimitedUserDTO, AddUsersToGroupPresenter.MyView, AddUsersToGroupPresenter.MyProxy, AddUsersToGroupUiHandlers>
        implements AddUsersToGroupUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    interface MyView extends View, HasUiHandlers<AddUsersToGroupUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public TextArea getCommentsTextArea();
        public TextBox getSerachTextBox();
        public Button getAddUserButton();
        public DataGrid<LimitedUserDTO> getDataGrid();
        public SimplePager getPager();
        public Button getClearButton();
        public ObidosMessageRow getMessageRow();
        public ObidosInputGroup getGroupNameInputGroup();
        public ObidosPanelHeader getPanelHeader();
    }

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	protected String getIdName() {
		return ObidosConstants.GROUP_ID;
	}

    @NameToken(NameTokens.ADD_USERS_TO_GROUP)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<AddUsersToGroupPresenter>
    {
    }

    @Inject
    AddUsersToGroupPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
    		CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<LimitedUserDTO> createCheckboxManager());

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
               Set<LimitedUserDTO> dto = selectionModel.getSelectedSet();
               if (dto.size() > 0)
               {
            	   enableButtons(true);
               }
               else
               {
            	   enableButtons(false);
                   gwtLog("Unselected");
               }
            }
        });
        showUserList(selectionModel,grid, this);
        selectCheckBoxByClickingOnTheRow(selectionModel,grid);
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
        clearCheckBoxSelections();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage(null);
        enableButtons(false);
        clearCheckBoxSelections();
        resetForm();
        updateGroupInfo();
        resetForm();
        refreshDataGrid();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        setColumnWidth(grid, 0, "55px");  // Checkbox column
    }
    
    private void enableButtons(boolean enabled)
    {
    	getView().getClearButton().setEnabled(enabled);
    	getView().getAddUserButton().setEnabled(enabled);
    }
    
    private void resetForm()
    {
    	showMessage("");
        getView().getSerachTextBox().setValue("");
    }


    private void updateGroupInfo()
    {
        GwtAsyncWrapper<GroupDTO> callback = new GwtAsyncWrapper<GroupDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                gwtLog("Could not get group:" + caught.getMessage());
            }

            @Override
            public void uponSuccess(GroupDTO dto)
            {
                getView().getGroupNameInputGroup().setText(dto.getName());
                getView().getCommentsTextArea().setValue(dto.getComments());
            }
        };
        safeInvocationCall(() -> UserService.Utility.getInstance().getGroup(ClientUtils.getAuthCreds(), getId(), callback));
    }

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    // duplicate of the one in ShareContainerWithUsersPresentr
    private void showUserList(final SelectionModel<LimitedUserDTO> selectionModel,final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
	    grid.setEmptyTableWidget(messageLabel);
	    
	    ObidosMessages lang = ObidosMessages.LANG;

	    Column<LimitedUserDTO,Boolean> checkColumn =
	            new Column<LimitedUserDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(LimitedUserDTO dto)
            {
            	if (dto != null && dto.getSelected() == null)
            	{
            		dto.setSelected(false);
            	}
            	setSearchedCheckbox(selectionModel, dto);
                return selectionModel.isSelected(dto);
            }
            // https://stackoverflow.com/questions/6089302/disable-checkboxcell-in-a-celltable
            // don't remove commented out code
            /*
            @Override
            public void render(Context context, LimitedUserDTO dto, SafeHtmlBuilder sb)
            {
            	if (dto.getUsername().equals("morgan"))
            	{
                  super.render(context, dto, sb);
            	}
            }
            */
        };
        grid.addColumn(checkColumn,  lang.selectLabel());

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
        grid.addColumn(fullnameCol, lang.fullname());

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
        grid.addColumn(emailCol, lang.email());

        AsyncDataProvider<LimitedUserDTO> dataProvider = new AsyncDataProvider<LimitedUserDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<LimitedUserDTO> dto)
            {
                final Range range = dto.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                gwtLog(" in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);

                GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable caught)
                    {
                        gwtLog("Exception caught: " + caught.getMessage());
                        updateRowCount(0, true);
                        messageLabel.setText("Exception: " + caught.getMessage());
                    }

                    @Override
                    public void uponSuccess(LimitedUserResult userResult)
                    {
                        int numberOfUsers = userResult.getTotalUsers();
                        gwtLog("Found number of users: " + numberOfUsers);
                        List<LimitedUserDTO> users = userResult.getUsers();
                        if (users != null && users.size() > 0)
                        {
                            updateRowCount(numberOfUsers, true);
                            updateRowData(start, users);
                        }
                        else
                        {
                            updateRowCount(0, true);
                            messageLabel.setText("No users found");
                        }
                    }

                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                boolean showDeleted = false;
                String searchString = getView().getSerachTextBox().getValue();
                if (searchString.length() == 0)
                {
                    searchString = null;
                }
                UserDTO searchDTO = new UserDTO();
                searchDTO.setDeleted(false);
                searchDTO.setFullname(searchString);
				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
				if (preSelectedUsers != null)
				{
					for (Long uid : preSelectedUsers)
					{
						gwtLog(">> Pre selected user id: " + uid);
					}
				}
                UserService.Utility.getInstance().getUsersForGroup(authCreds, getId(), searchDTO, SharedSetQuality.NOT_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
            }

        };

        fullnameCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
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

    @Override
    public void searchUsers()
    {
        gwtLog("Search users to add to group");
        refreshDataGrid();
    }

    private ArrayList<Long> getUserIds(final Set<LimitedUserDTO> dtoSet) {
        final ArrayList<Long> userIds = new ArrayList<Long>(dtoSet.size());
        for(final LimitedUserDTO user : dtoSet) {
        	userIds.add(user.getId());
        }
        return userIds;
    }

    @Override
    public void addUserToGroup()
    {
        Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
        int n = dtoSet.size();
        if (n == 0)
        {
            return;
        }

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {
            @Override
            public void uponFailure(Throwable e)
            {
                if (e instanceof DuplicateRecordException)
                {
                    showErrorMessage(glang.userExitsInGroup());
                }
                else
                {
                    showErrorMessage(glang.couldNotAddUserToGroup(e.getMessage()));
                }
            }

            @Override
            public void uponSuccess(Void result)
            {
//            	Date date = new Date();
                String groupName = getView().getGroupNameInputGroup().getText();
//            	String msg = glang.usersAddedToGroup(n, date.toString());
              	String msg = glang.usersAddedToGroup(n, groupName);
            	showMessage(msg);
            	clearCheckBoxSelections();
            	refreshDataGrid();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().addUsersToGroup(authCreds, getUserIds(dtoSet), getId(), null, null, callback);
    }

    @Override
	public void clearCheckBoxSelections()
	{
    	clearCheckBoxSelections(selectionModel);
	}

	@Override
	public void help()
	{
		showHelp();
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
