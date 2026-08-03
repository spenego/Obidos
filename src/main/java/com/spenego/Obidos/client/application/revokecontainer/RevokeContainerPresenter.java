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

package com.spenego.Obidos.client.application.revokecontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRevokeButton;
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
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;

public class RevokeContainerPresenter extends ObidosPresenter<UserGroupComboDTO,RevokeContainerPresenter.MyView, RevokeContainerPresenter.MyProxy, RevokeContainerUiHandlers>
		implements RevokeContainerUiHandlers
{

	ObidosMessages glang = ObidosMessages.LANG;
    final SingleSelectionModel<UserGroupComboDTO> selectionModel = new SingleSelectionModel<UserGroupComboDTO>();
	interface MyView extends View, HasUiHandlers<RevokeContainerUiHandlers>
	{
		public DataGrid<UserGroupComboDTO> getDataGrid();
		public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public TextBox getSearchTextBox();
		public Button getSearchButton();
		public Row getContainerNameRow();
		public ObidosInputGroup getContainerNameInputGroup();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public ObidosRevokeButton getRevokeButton();
	}

	@NameToken(NameTokens.REVOKE_CONTAINER)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<RevokeContainerPresenter>
	{
	}

	@Inject
	RevokeContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			final PlaceManager placeManager,
			final CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<UserGroupComboDTO> createCheckboxManager());
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
            	UserGroupComboDTO dto = selectionModel.getSelectedObject();
               if (dto != null)
               {
            	   enableRevokeButton(true);
            	   if (dto.isGroup())
            	   {
            		   gwtLog("Group: " + dto.getName());
            	   }
            	   else
            	   {
            		   gwtLog("Full name: " + dto.getFullname());
            	   }
               }
               else
               {
            	   enableRevokeButton(false);
            	   gwtLog("Unselected");
               }
            }
        });
        showUsersAndGroups(selectionModel,grid, this);
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
		fetchAndUpdateForm();
        refreshDataGrid();
        DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
        setColumnWidth(grid, 0, "55px");  // Checkbox column
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
	}

    private void fetchAndUpdateForm()
    {
    	Long containerId = getContainerIdFromUrl();
    	if (containerId == null)
    	{
    		showErrorMessage("Could not get Container ID from URL");
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
	public void revoke()
	{
		UserGroupComboDTO dto = selectionModel.getSelectedObject();
		gwtLog("revoke item");
		if (dto == null)
		{
			return;
		}
    	if (dto.isGroup())
    	{
    		promptRevokeFromGroup();
    	}
    	else
    	{
    		promptRevokeFromUser();
    	}
	}

	private void promptRevokeFromUser()
    {
    	UserGroupComboDTO dto = selectionModel.getSelectedObject();
    	if (dto == null)
    	{
    		return;
    	}
    	String name = ClientUtils.shortenString(dto.getName());
    	String title = "Revoke sharing container from User";
		String message = "Are you sure to revoke container sharing with user: <b>" + name + "</b>?";
        ClientUtils.promptForAction(() -> revokeSharingContainerFromUsersReal(), title, message);
    }


    private void promptRevokeFromGroup()
    {
    	UserGroupComboDTO dto = selectionModel.getSelectedObject();
    	if (dto == null)
    	{
    		return;
    	}
    	String name = ClientUtils.shortenString(dto.getName());
    	String title = "Revoke sharing container from Group";
		String message = "Are you sure to revoke container sharing with group: <b>" + name + "</b>?";
        ClientUtils.promptForAction(() -> revokeContainerFromGroupReal(), title, message);
    }

    public void revokeSharingContainerFromUsersReal()
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showErrorMessage("Could not get container id from URL");
			return;
		}
	
		UserGroupComboDTO dto = selectionModel.getSelectedObject();
		ArrayList<Long> userIds = new ArrayList<Long>();
		userIds.add(dto.getId());
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				// must clear CheckBox selections or they will accumulate
				refreshDataGrid();
				enableRevokeButton(false);

                String entity = "Container";
                String itemName = ClientUtils.shortenString(getView().getContainerNameInputGroup().getText());
                String name = ClientUtils.shortenString(dto.getName());
                showMessage(entity + " " + "<span style=\"color: purple;\">" + "'" + itemName + "'" + "</span>" + " revoked from " + "user <span style=\"color: purple;\">" + name + "</span>");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
                showErrorMessage("Could not revoke Item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        PostOpActions postRevokeActions = null;
        ContainerService.Utility.getInstance().revokeContainerFromUsers(authCreds, containerId, userIds, null, postRevokeActions, callback);
	}

    private void revokeContainerFromGroupReal()
    {
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showErrorMessage("Could not get container id from URL");
			return;
		}
				
		UserGroupComboDTO dto = selectionModel.getSelectedObject();
		ArrayList<Long> groupIds = new ArrayList<Long>();
		groupIds.add(dto.getId());
		
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				refreshDataGrid();
				enableRevokeButton(false);

                String entity = "Container";
                String itemName = ClientUtils.shortenString(getView().getContainerNameInputGroup().getText());
                String name = ClientUtils.shortenString(dto.getName());
                showMessage(entity + " " + "<span style=\"color: purple;\">" + "'" + itemName + "'" + "</span>" + " revoked from " + "group <span style=\"color: purple;\">" + name + "</span>");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not revoke item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        PostOpActions postRevokeActions = null;
        ContainerService.Utility.getInstance().revokeContainerFromGroups(authCreds, containerId, groupIds, null, postRevokeActions, callback);
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

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

	@Override
	protected String getIdName()
	{
		return ObidosConstants.REVOKE_CONTAINER_SHARING;
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
        DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }
 
    private void showUsersAndGroups(final SelectionModel<UserGroupComboDTO> selectionModel, final AbstractCellTable<UserGroupComboDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText("Loading ...");
        grid.setEmptyTableWidget(messageLabel);

        // Checkbox
        Column<UserGroupComboDTO,Boolean> checkColumn = new Column<UserGroupComboDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(UserGroupComboDTO dto)
            {
                return selectionModel.isSelected(dto);
            }
        };
        grid.addColumn(checkColumn, glang.select());
        
	    // Name (fullname/group name)
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<UserGroupComboDTO, String> nameCol = new Column<UserGroupComboDTO, String>(nameCell)
        {

            @Override
            public String getValue(UserGroupComboDTO dto)
            {
//            	gwtLog("Fullname: " + dto.getFullname() + " x: " + dto.getName());
                return glang.na();
            }
        };
        grid.addColumn(nameCol, glang.name());

        // email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<UserGroupComboDTO, String> emailCol = new Column<UserGroupComboDTO, String>(emailCell)
        {

            @Override
            public String getValue(UserGroupComboDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(emailCol, glang.email());
		AsyncDataProvider<UserGroupComboDTO> dataProvider = new AsyncDataProvider<UserGroupComboDTO>()
		{

			@Override
			protected void onRangeChanged(HasData<UserGroupComboDTO> userDTO)
			{
				final Range range = userDTO.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				gwtLog(" in AsyncDataProvider start: " + start);
				gwtLog("in AsyncDataProvider length: " + length);
				GwtAsyncWrapper<UserGroupComboResult> callback = new GwtAsyncWrapper<UserGroupComboResult>(source)
				{

					@Override
					public void uponFailure(Throwable e)
					{
						Window.alert("Exception received: " + e.getMessage());
						updateRowCount(0, true);
						return;
					}

					@Override
					public void uponSuccess(UserGroupComboResult userResult)
					{
						int numberOfUsers = userResult.getTotalUsers();
						List<UserGroupComboDTO> users = userResult.getUsers();
						if (users != null)
						{
							gwtLog(">>> returned " + users.size());
						}
						else
						{
							gwtLog("Count: " + numberOfUsers);
							// messageLabel.setWidth("20000px");
							// messageLabel.setHeight("2000px");
						}

						if (users != null && users.size() > 0)
						{
							updateRowCount(numberOfUsers, true);
							updateRowData(start, users);
						} else
						{
							messageLabel.setText("");
							updateRowCount(0, true);
						}
					}
				};
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				Long containerId = getContainerIdFromUrl();
				if (containerId == null)
				{
					showErrorMessage("Could not get Container ID from URL");
					return;
				}
                String searchText = getView().getSearchTextBox().getValue();
                String search = (searchText != null && searchText.length() > 0) ? searchText : null;

				safeInvocationCall(() ->UserService.Utility.getInstance().getContainerShares(authCreds, containerId, search, null, start, length, toArray(OrderBy.USER_GROUP_COMBO_NAME_ASC), callback));
			} // end onRangeChanged()
		};

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
        nameCol.setFieldUpdater(new FieldUpdater<UserGroupComboDTO, String>()
        {

            @Override
            public void update(int idx, UserGroupComboDTO dto, String value)
            {
            	sendToCorrectPlace(dto);
            }

        });

        // noop but the handler must exist
        emailCol.setFieldUpdater(new FieldUpdater<UserGroupComboDTO, String>()
        {

            @Override
            public void update(int idx, UserGroupComboDTO dto, String value)
            {
            }
        });


    }

	private void enableRevokeButton(boolean enable)
	{
		getView().getRevokeButton().setEnabled(enable);
	}
	private void sendToCorrectPlace(UserGroupComboDTO dto)
	{

        boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
        if (rc)
        {
        	if (dto.isGroup())
        	{
        		 showUsersInGroup(dto);
        	}
        	else
        	{
        		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
        		
        	}
        }
        else
        {
        	if (dto.isGroup())
        	{
        		ClientUtils.showRegisterPassphraseDialog(placeManager, glang.listUsersInGroup(),null);
        	}
        	else
        	{
        		ClientUtils.showRegisterPassphraseDialog(placeManager, glang.viewUserInfo(),null);
        	}
        	
        }
	}
	private void showUsersInGroup(UserGroupComboDTO dto)
	{
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.GROUP_ID, dto.getId().toString());
        String nameToken = NameTokens.VIEW_USERS_IN_GROUP;
        ClientUtils.showPage(placeManager, nameToken, with);
	}
}