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

package com.spenego.Obidos.client.application.revokecontainerfromusers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
//import com.spenego.Obidos.client.application.ObidosPresenter.RevokeView;
import com.spenego.Obidos.client.application.ObidosPresenter.RevokeView;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class RevokeContainerFromUsersPresenter
		extends ObidosPresenter<LimitedUserDTO, RevokeContainerFromUsersPresenter.MyView, RevokeContainerFromUsersPresenter.MyProxy, RevokeContainerFromUsersUiHandlers>
		implements RevokeContainerFromUsersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	// Fix #228
	interface MyView extends RevokeView<RevokeContainerFromUsersUiHandlers, LimitedUserDTO> 
	{
		public BlockQuote getHelpBlockQuote();
		public ObidosMessageRow getMessageRow();
		public ObidosInputGroup getContainerNameInputGroup();
		public ToggleSwitch getSendNotificationEmailSwitch();
	}

	@Override
	protected String getIdName() {
		return ObidosConstants.CONTAINER_ID;
	}

	@NameToken(NameTokens.REVOKE_CONTAINER_FROM_USERS)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<RevokeContainerFromUsersPresenter>
	{
	}

	@Inject
	RevokeContainerFromUsersPresenter(EventBus eventBus, MyView view, MyProxy proxy, 
			final PlaceManager placeManager,
			final CurrentUser currentUser)
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
            	   showMessage(dto.size() + " users selected");
                   enableRevokeButton(true);
                   enableClearButton(true);
               }
               else
               {
                   enableRevokeButton(false);
                   enableClearButton(false);
                   gwtLog("Unselected");
               }
            }
        });
        showUsersSharingContainer(selectionModel,grid, this);
        selectCheckBoxByClickingOnTheRow(selectionModel,grid);
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
		enableRevokeButton(false);
        enableClearButton(false);
        updateContainerInfo();
        resetForm();
        refreshDataGrid();
		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
        grid.setColumnWidth(0, "55px");
        clearCheckBoxSelections();
	}

    private void resetForm()
    {
        showMessage("");
        getView().getRevokeCommentTextBox().setValue("");
        getView().getSearchTextBox().setValue("");
        getView().getSendNotificationEmailSwitch().setValue(false);
    }


    private void enableRevokeButton(boolean enabled)
    {
        getView().getRevokeButton().setEnabled(enabled);
    }

    private void showInfoMessgge(String message)
    {
    	showMessage(message);
    }

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void updateContainerInfo()
    {
        Long containerId = null;

        try
        {
            containerId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.CONTAINER_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            showErrorMessage("Could not find " + ObidosConstants.CONTAINER_ID + " from URL");
            return;
        }


        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                gwtLog("Could not get Container " + caught.getMessage());
            }

            @Override
            public void uponSuccess(ContainerDTO dto)
            {
            	getView().getContainerNameInputGroup().setText(dto.getName());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
    }

    @Override
    public void searchUsersToRevoke()
    {
        refreshDataGrid();
    }

    private void showUsersSharingContainer(final SelectionModel<LimitedUserDTO> selectionModel,final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
    {

		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
	    grid.setEmptyTableWidget(messageLabel);

	    Column<LimitedUserDTO,Boolean> checkColumn = new Column<LimitedUserDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(LimitedUserDTO dto)
            {
            	if (dto!= null && dto.getSelected() == null)
            	{
            		dto.setSelected(false);
            	}
            	setSearchedCheckbox(selectionModel, dto);
                return selectionModel.isSelected(dto);
            }
        };
        grid.addColumn(checkColumn, glang.select());

        // fullname
        ObidosButtonCell fullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserDTO, String> fullnameColumn = new Column<LimitedUserDTO, String>(fullnameCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(fullnameColumn, glang.fullname());

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
                        messageLabel.setText("");
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
                            messageLabel.setText("");
                        }
                    }

                };
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                    searchString = null;
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Long containerId = getId();
                if (containerId == null)
                {
                	return;
                }
                String search = getView().getSearchTextBox().getValue();
                if (search == null || search.length() == 0)
                {
                	search = null;
                }
				gwtLog("Search: " + search);
				// Pending support for selected set #227
				final UserDTO userDTO = new UserDTO();
				// Issue #387
				userDTO.setFullname(search);
				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
				if (preSelectedUsers != null)
				{
					for (Long psuid : preSelectedUsers)
					{
						gwtLog(" >>> Sending Preselected ID: " + psuid);
					}
				}

				UserService.Utility.getInstance().getUsersForContainer(authCreds, containerId, userDTO, SharedSetQuality.ONLY_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
            }

        };
        
        fullnameColumn.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
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
    
    private void promptRevoke()
    {
        Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
        if (dtoSet.size() == 0)
        {
            return;
        }
        String title = glang.revokeContainer();
        int n = dtoSet.size();
        String isAre = glang.is();
        String user = glang.user();
        if (n > 1)
        {
        	isAre = glang.are();
        	user = glang.users();
        }
        // TODO: move to catalog
        String message = "You select " + n + " " + user + " to revoke sharing the Container from. Once revoked, the " + user + " will no longer have access to the Container.";
        message = message + "<br/>" + "<b>Are you sure you want to revoke sharing the Container?</b>";
        ClientUtils.promptForAction(() -> revokeContainerReal(), title, message);
    }
    
    private void revokeContainerReal()
    {
    	Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
        if (dtoSet.size() == 0)
        {
            return;
        }


        Long containerId = getId();
        if (containerId == null)
        {
            return;
        }

        gwtLog("Container id: " + containerId);

        gwtLog("Number of rows selected: " + dtoSet.size());
        for (LimitedUserDTO dto:dtoSet)
        {
            gwtLog("Revoke sharing from: " + dto.getEmail1() + ", " + dto.getFullname());
        }

        String shareComment = getView().getRevokeCommentTextBox().getValue();
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not revoke Container: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(Void result)
            {
                Date date = new Date();
                String ds = ClientUtils.formattedDate(date);
                showMessage("Container revoked from " + dtoSet.size() + " users on: " + ds);
				// must clear CheckBox selections or they will accumulate
				clearCheckBoxSelections();
                refreshDataGrid();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        PostOpActions postRevokeActions = null;
        if (getView().getSendNotificationEmailSwitch().getValue())
        {
        	gwtLog("Send email about revoking..........");
        	postRevokeActions = new PostOpActions();
        	postRevokeActions.setPostOpActions(PostOpActions.SEND_EMAIL);
        }
        else
        {
        	gwtLog("Dont send mail about revoking..");
        }

        ContainerService.Utility.getInstance().revokeContainerFromUsers(authCreds, containerId, getIdsFromDTOSet(dtoSet), shareComment, postRevokeActions, callback);
    }

    @Override
    public void revokeContainer()
    {
    	promptRevoke();
    }

    @Override
    public void listMyContainers()
    {
        ClientUtils.showPage(placeManager, NameTokens.LIST_CONTAINERS);
    }

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);
	}

	private void enableClearButton(boolean enabled)
	{
		getView().getClearButton().setEnabled(enabled);
	}

	private void numberOfUsersSelected()
	{
        Set<LimitedUserDTO> selectedSet = selectionModel.getSelectedSet();
        int n = selectedSet.size();
        if (n > 0)
        {
        	showMessage("Number of users selected: "+ n);
        }

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
	public void back()
	{
		ClientUtils.back(placeManager);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
}
