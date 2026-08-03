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

package com.spenego.Obidos.client.application.grantpermissionstousersforitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.UserDTO;

public class GrantPermissionsToUsersForItemPresenter extends
		ObidosPresenter<LimitedUserDTO,GrantPermissionsToUsersForItemPresenter.MyView,
		GrantPermissionsToUsersForItemPresenter.MyProxy,
		GrantPermissionsToUsersForItemUiHandlers>
		implements GrantPermissionsToUsersForItemUiHandlers
{
    interface MyView extends View, HasUiHandlers<GrantPermissionsToUsersForItemUiHandlers>
	{
    	DataGrid<LimitedUserDTO> getDataGrid();
    	SimplePager 	getPager();
		ObidosPanelHeader	getPanelHeader();
		ObidosInputGroup getContainerNameInputGroup();
		Button			getGrantButton();
		Button			getClearButton();
		ObidosInputGroup getNameInputGroup();
		TextBox 		getContainerTypeTextBox();
		Row 			getContainerRow();
		BlockQuote      getHelpBlockQuote();
		InlineCheckBox 	getMayUpdateCheckBox();
		InlineCheckBox 	getMayTakeOwnershipCheckBox();
		InlineCheckBox  getNoneCheckBox();
		TextBox 		getSearchTextBox();
		Button 			getHelpButton();
		ObidosMessageRow getMessageRow();
	}

	@Override
	protected String getIdName()
	{
		return ObidosConstants.CONTAINER_ID;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@NameToken(NameTokens.GRANT_PERMISSIONS_TO_USERS_FOR_ITEMS)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<GrantPermissionsToUsersForItemPresenter>
	{
	}

	@Inject
	GrantPermissionsToUsersForItemPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
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
				if (! anyCheckBoxSelected())
				{
					enableGrantButton(false);
					enableClearButton(false);
					return;
				}
				Set<LimitedUserDTO> dto = selectionModel.getSelectedSet();
				if (dto.size() > 0)
				{
					enableGrantButton(true);
					enableClearButton(true);
//					enableMultiSelectPermisisons(true);
				} else
				{
					enableGrantButton(false);
					enableClearButton(false);
//					enableMultiSelectPermisisons(false);
					gwtLog("Unselected");
				}
			}
		});

		showUserList(selectionModel, grid, this);
		selectCheckBoxByClickingOnTheRow(selectionModel, grid);

	}

	private void enableGrantButton(boolean enabled)
	{
		getView().getGrantButton().setEnabled(enabled);
	}
	private void enableClearButton(boolean enabled)
	{
		getView().getClearButton().setEnabled(enabled);
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
		updateContainerNameInForm();
		updateItemNameInForm();
		refreshDataGrid();

		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
        grid.setColumnWidth(0, "50px");
	}
	
	private void disable_all_grant_permission_check_boxes()
	{
	    getView().getMayUpdateCheckBox().setEnabled(false);
	    getView().getMayTakeOwnershipCheckBox().setEnabled(false);
	}

	private void updateContainerNameInForm()
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showContainerGroup(false);
//			getView().getListButton().setText(ObidosMessages.LANG.listMyItems());
			return;
		} else
		{
			showContainerGroup(true);
//			getView().getListButton().setText(ObidosMessages.LANG.listItemsInContainer());
		}

		GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
		{

			@Override
			public void uponSuccess(ContainerDTO result)
			{
				getView().getContainerNameInputGroup().setText(result.getName());
				Boolean privateContainer = result.getIsPrivate();
				if (privateContainer != null && privateContainer.booleanValue() == true)
				{
					getView().getContainerTypeTextBox().setValue("PRIVATE");
				} else
				{
					getView().getContainerTypeTextBox().setValue("SHAREABLE");
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch Container");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		safeInvocationCall(() -> ContainerService.Utility.getInstance().get(authCreds, containerId, callback));
	}
	private void showHideContainerGroup()
	{
		if (getContainerIdFromUrl() != null)
		{
			showContainerGroup(true);
		} else
		{
			showContainerGroup(false);
		}

	}

	private void updateItemNameInForm()
	{
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			return;
		}

		showHideContainerGroup();

		GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
		{

			@Override
			public void uponSuccess(ItemDTO result)
			{
				getView().getNameInputGroup().setText(result.getName());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get Item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().get(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
	}

	private void showContainerGroup(boolean visible)
	{
		getView().getContainerRow().setVisible(visible);
	}

	public void refreshDataGrid()
	{
		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}
	
	private static String getPermissions(LimitedUserDTO dto)
	{
		boolean update = ClientUtils.fromBoolean(dto.getUpdatePermitted());
		boolean own = ClientUtils.fromBoolean(dto.getOwnershipControl());
		
		ObidosMessages lang = ObidosMessages.LANG;
				
		// caught by sonarqube
		if (update || own)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(lang.read());
			if (update)
			{
			    sb.append(",");
				sb.append(lang.edit());
			}
			if (own)
			{
				if (update)
				{
					sb.append(",");
				}
				sb.append(lang.mayTakeOwnershipControl());
			}
			return sb.toString();
			
		}
		return lang.read();
	}


	private void showUserList(final SelectionModel<LimitedUserDTO> selectionModel,
			final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
	{
		ObidosMessages lang = ObidosMessages.LANG;
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(lang.loading());
		grid.setEmptyTableWidget(messageLabel);

		Column<LimitedUserDTO, Boolean> checkColumn = new Column<LimitedUserDTO, Boolean>(new CheckboxCell(true, false))
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

		};
		grid.addColumn(checkColumn, ObidosMessages.LANG.selectLabel());
		
		// Permissions
		TextColumn<LimitedUserDTO> permissionColumn = new TextColumn<LimitedUserDTO>()
		{

			@Override
			public String getValue(LimitedUserDTO dto)
			{
				gwtLog(">>>User: " + dto.getFullname());
				gwtLog("	Update: " + dto.getUpdatePermitted());
				gwtLog("	Delete: " + dto.getSharePermitted());
				gwtLog("	   Own: " + dto.getOwnershipControl());

				return getPermissions(dto);
			}
		};
		grid.addColumn(permissionColumn, ObidosMessages.LANG.permissions());


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
				GWT.log(" in AsyncDataProvider start: " + start);
				GWT.log("in AsyncDataProvider length: " + length);
				GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
				{

					@Override
					public void uponFailure(Throwable caught)
					{
						GWT.log("Exception caught: " + caught.getMessage());
						updateRowCount(0, true);
						messageLabel.setText("");
					}

					@Override
					public void uponSuccess(LimitedUserResult userResult)
					{
						int numberOfUsers = userResult.getTotalUsers();
						GWT.log("Found number of users: " + numberOfUsers);
						List<LimitedUserDTO> users = userResult.getUsers();
						if (users != null && users.size() > 0)
						{
							updateRowCount(numberOfUsers, true);
							updateRowData(start, users);
						}
						else
						{
							updateRowCount(0, true);
							messageLabel.setText(lang.couldNotFindAnyUsers());
							// disable all grant permissions check boxes
							// Oct-31-2025
							disable_all_grant_permission_check_boxes();
						}
					}

				};
				Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
				if (itemId == null)
				{
					showErrorMessage("Could not find user id in URL");
					return;
				}
				String searchString = getView().getSearchTextBox().getValue();
				if (searchString.length() == 0)
				{
					searchString = null;
				}
				gwtLog("Search: " + searchString);
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				UserDTO userDTO = new UserDTO();
				userDTO.setDeleted(false);
				// Allow interact with locked users: Issue #456
//				userDTO.setLocked(false);
				userDTO.setAdministrator(false);
				// Issue #387
				userDTO.setFullname(searchString);

				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
				if (preSelectedUsers != null)
				{
					for (Long psuid : preSelectedUsers)
					{
						gwtLog(" >>> Sending Preselected ID: " + psuid);
					}
				}
				//UserService.Utility.getInstance().getUsers(authCreds, userDTO, preSelectedUsers, start, length, OrderBy.USERNAME_ASC, callback);
				UserService.Utility.getInstance().getUsersForItem(authCreds, itemId, userDTO, SharedSetQuality.ONLY_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
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

	private void resetForm()
	{
		showMessage("");
		enableGrantButton(false);
		enableClearButton(false);
		clearCheckBoxSelections();
		resetCheckBoxes();
	}

	@Override
	public void help()
	{
		gwtLog("help");
		showHelp();
	}

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);

	}

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
	}

	@Override
	public void grantPermissions()
	{
		Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
		if (dtoSet.size() == 0)
		{
			return;
		}
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			return;
		}

		CheckBox muCb = getView().getMayUpdateCheckBox();
		CheckBox mtoCb = getView().getMayTakeOwnershipCheckBox();
		
		boolean mayUpdate = ClientUtils.fromBoolean(muCb.getValue());
		boolean hasOwnershipControl = ClientUtils.fromBoolean(mtoCb.getValue());
		PermissionDTO permission = null;
		gwtLog("Update: " + mayUpdate);
		gwtLog("Own: " + hasOwnershipControl);
		
		String p = "Read";
		if (mayUpdate)
		{
			p = "Read,Edit";
		}
		if (hasOwnershipControl)
		{
			p = "Read,Edit,Own";
		}

		permission = new PermissionDTO();
		if (mayUpdate)
		{
			gwtLog("Setting may update perm");
			permission.setMayUpdate(true);
		}

		if (hasOwnershipControl)
		{
			gwtLog("Setting update and ownership perm");
			// give update permission explicitly
			permission.setMayUpdate(true);
			permission.setHasOwnershipControl(true);
		}
		final String permissionStr = p;
		
		/*
		if (getView().getNoneCheckBox().getValue())
		{
			gwtLog("Setting update and ownership to false");
			permission.setMayUpdate(false);
			permission.setHasOwnershipControl(false);
		}
		*/

		gwtLog(" Set Edit: " + mayUpdate);
		gwtLog(" Set Own: " + hasOwnershipControl);
	
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				String item = getView().getNameInputGroup().getText();
				resetForm();
				refreshDataGrid();
				showMessage("<span style=\"color: purple;\">" + "'" + permissionStr + "'" + "</span>" +
                                             " permission(s) granted on " + "<span style=\"color: purple;\">" + 
                                             "'" + item + "'" + "</span>" + ".");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not grant permissions: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().grantPermission(authCreds, itemId, getIdsFromDTOSet(dtoSet), permission, callback);
	}

	@Override
	public void multiSelectPermissionValueChagned()
	{
		showMessage("");
	}

	public void list()
	{
		Long containerId = getContainerIdFromUrl();
		String nameToken = NameTokens.LIST_ITEMS;
		String key = null;
		String value = null;
		if (containerId != null)
		{
			Map<String, String> with = new HashMap<>();
			key = ObidosConstants.CONTAINER_ID;
			value = containerId.toString();
			with.put(key, value);
			ClientUtils.showPage(placeManager, nameToken, with);
		} else
		{
			nameToken = NameTokens.LIST_ALL_MY_ITEMS;
			ClientUtils.showPage(placeManager, nameToken);
		}

	}

	@Override
	public void back()
	{
        placeManager.navigateBack();
	}

	private int nCheckBoxSelected()
	{
		return selectionModel.getSelectedSet().size();
	}
	
	private boolean anyCheckBoxSelected()
	{
		return getView().getNoneCheckBox().getValue() | getView().getMayTakeOwnershipCheckBox().getValue() | getView().getMayUpdateCheckBox().getValue();
	}

	@Override
	public void mayUpdateCheckBoxCallback()
	{
		showMessage("");
		// don't do any complex garbage, just enable Grant button 
		// Oct-26-2029
		/*
		// unset all other than update button
		clearCheckBoxSelections();
		getView().getMayUpdateCheckBox().setValue(true);

		getView().getNoneCheckBox().setValue(false);
		getView().getMayTakeOwnershipCheckBox().setValue(false);
		*/
		// if both Edit and Update checkboxes are unset
		// disable Grant button
		boolean b1 = getView().getMayUpdateCheckBox().getValue();
		boolean b2 = getView().getMayTakeOwnershipCheckBox().getValue();
		boolean b = b1 | b2;
		enableGrantButton(b);
	}

	@Override
	public void mayTakeOwnershipCheckBoxCallback()
	{
		showMessage("");
		// don't do any complex garbage, just enable Grant button 
		// Oct-26-2029
		/*
		clearCheckBoxSelections();
		getView().getMayTakeOwnershipCheckBox().setValue(true);

		getView().getNoneCheckBox().setValue(false);
		getView().getMayUpdateCheckBox().setValue(false);
		*/
		// if both Edit and Update checkboxes are unset
		// disable Grant button
		boolean b1 = getView().getMayUpdateCheckBox().getValue();
		boolean b2 = getView().getMayTakeOwnershipCheckBox().getValue();
		boolean b = b1 | b2;
	    enableGrantButton(b);
	}

	private void resetCheckBoxes()
	{
		getView().getMayUpdateCheckBox().setEnabled(true);
		getView().getMayUpdateCheckBox().setValue(false);
		getView().getMayTakeOwnershipCheckBox().setEnabled(true);
		getView().getMayTakeOwnershipCheckBox().setValue(false);
		getView().getNoneCheckBox().setValue(true);
		
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}


}
