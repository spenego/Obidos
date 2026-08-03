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

package com.spenego.Obidos.client.application.grantpermissionstousersforcontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
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
import com.google.gwt.user.cellview.client.TextColumn;
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
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;

public class GrantPermissionsToUsersForContainerPresenter extends
		ObidosPresenter<UserGroupComboDTO, GrantPermissionsToUsersForContainerPresenter.MyView,
		GrantPermissionsToUsersForContainerPresenter.MyProxy,
		GrantPermissionsToUsersForContainerUiHandlers>
		implements GrantPermissionsToUsersForContainerUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    final SingleSelectionModel<UserGroupComboDTO> selectionModel = new SingleSelectionModel<UserGroupComboDTO>();

	interface MyView extends View, HasUiHandlers<GrantPermissionsToUsersForContainerUiHandlers>
	{
		public DataGrid<UserGroupComboDTO> getDataGrid();

		public SimplePager 	  		  getPager();
		public ObidosPanelHeader	  getPanelHeader();
		public ObidosInputGroup 	  getContainerNameInputGroup();
		public Button				  getGrantButton();
		public Button				  getClearButton();
		public TextBox 			  	  getContainerTypeTextBox();
		public Row 			      	  getContainerRow();
		public BlockQuote     		  getHelpBlockQuote();
//		public InlineCheckBox 		  getMayTakeOwnershipCheckBox();
//		public InlineCheckBox 		  getMayUpdateCheckBox();
		public InlineCheckBox 		  getReadCheckBox();
		public InlineCheckBox 		  getAddCheckBox();
		public InlineCheckBox 		  getUpdateCheckBox();
		public InlineCheckBox 		  getOwnCheckBox();

		public TextBox 			  	  getSearchTextBox();
		public Button 				  getHelpButton();
		public ObidosMessageRow 	  getMessageRow();
	}

	@NameToken(NameTokens.GRANT_PERMISSIONS_FOR_CONTAINER)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<GrantPermissionsToUsersForContainerPresenter>
	{
	}

	@Inject
	GrantPermissionsToUsersForContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
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
//				Set<UserGroupComboDTO> dto = selectionModel.getSelectedSet();
				UserGroupComboDTO dto = selectionModel.getSelectedObject();
				if (dto != null)
				{
					gwtLog("MMM selected: " + dto.getName());
					if (anyCheckBoxSelected())
					{
					gwtLog("MMM OK selected: " + dto.getName());
						enableGrantButton(true);
						enableClearButton(true);
					}
				} else
				{
					enableGrantButton(false);
					enableClearButton(false);
					gwtLog("MMM Unselected");
				}
			}
		});

        showUsersAndGroups(selectionModel,grid, this);
		selectCheckBoxByClickingOnTheRow(selectionModel, grid);

	}
	
	private int nCheckBoxSelected()
	{
		return selectionModel.getSelectedSet().size();
	}
	
	private boolean anyCheckBoxSelected()
	{
//		return getView().getReadCheckBox().getValue() | getView().getMayTakeOwnershipCheckBox().getValue() | getView().getMayUpdateCheckBox().getValue();
		return getView().getOwnCheckBox().getValue() | getView().getUpdateCheckBox().getValue() | getView().getOwnCheckBox().getValue() | getView().getAddCheckBox().getValue();
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
		refreshDataGrid();
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
        setColumnWidth(grid, 0, "55px");  // Checkbox column
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
//        grid.setColumnWidth(0, "50px");
	}
	private void showContainerGroup(boolean visible)
	{
		getView().getContainerRow().setVisible(visible);
	}

	private void updateContainerNameInForm()
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showContainerGroup(false);
			return;
		} else
		{
			showContainerGroup(true);
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
	private void resetForm()
	{
		showMessage("");
		enableGrantButton(false);
		enableClearButton(false);
		clearCheckBoxSelections();
		resetCheckBoxes();
	}
	private void resetCheckBoxes()
	{
//		getView().getMayTakeOwnershipCheckBox().setValue(false);
		// Bug #131 Read CheckBox should be selected but grayed out
		InlineCheckBox rcb = getView().getReadCheckBox();
		rcb.setValue(true);
		rcb.setEnabled(false);
		getView().getAddCheckBox().setValue(false);
		getView().getUpdateCheckBox().setValue(false);
		getView().getOwnCheckBox().setValue(false);
	}

	@Override
	protected String getIdName()
	{
		return ObidosConstants.CONTAINER_ID;
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
	
	private String getPermissionStr(final PermissionDTO pDTO)
	{
		String pStr = glang.read();
		if (pDTO != null)
		{
			boolean add    = ClientUtils.fromBoolean(pDTO.getMayAdd());
			if (add)
			{
				pStr += "," + glang.add();
			}
			boolean update = ClientUtils.fromBoolean(pDTO.getMayUpdate());
			if (update)
			{
				pStr += "," + glang.update();
			}
			boolean own = ClientUtils.fromBoolean(pDTO.getHasOwnershipControl());
			if (own)
			{
				pStr += "," + glang.own();
			}
		}
		else
		{
			gwtLog("MMM Permission DTO is null");
		}
		return pStr;
	}

	private void grantPermissionToGroup(final PermissionDTO permission, final String groupName)
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			return;
		}
		ArrayList<Long> groupIds = new ArrayList<Long>();
		UserGroupComboDTO dto = selectionModel.getSelectedObject();
		groupIds.add(dto.getId());
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				refreshDataGrid();
				enableGrantButton(false);
				clearCheckBoxSelections();
				resetCheckBoxes();
				String ps = getPermissionStr(permission);
				showMessage("'" + ps + "'" + " permission granted to the group '" + groupName + "'");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not grant permission to group: " + caught.getMessage());
			}
	
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ContainerService.Utility.getInstance().grantGroupPermission(authCreds, containerId, groupIds, permission, callback);
	}

	private void grantPermissionToUsers(final PermissionDTO permission, final String name)
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			return;
		}
		ArrayList<Long> userIds = new ArrayList<Long>();
		UserGroupComboDTO dto = selectionModel.getSelectedObject();
		userIds.add(dto.getId());

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				refreshDataGrid();
				enableGrantButton(false);
				clearCheckBoxSelections();
				resetCheckBoxes();
				String ps = getPermissionStr(permission);
				showMessage("'" + ps + "'" + " permission granted to the user '" + name + "'");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not grant permission to user: " + caught.getMessage());
			}
	
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ContainerService.Utility.getInstance().grantPermission(authCreds, containerId, userIds, permission, callback);
	}

	@Override
	public void grantPermissions()
	{
		UserGroupComboDTO dto = selectionModel.getSelectedObject();
		if (dto == null)
		{
			return;
		}
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			return;
		}
		
		// Bug #131
		PermissionDTO pDTO = new PermissionDTO();
		pDTO.setMayAdd(ClientUtils.fromBoolean(getView().getAddCheckBox().getValue()));
		pDTO.setMayUpdate(ClientUtils.fromBoolean(getView().getUpdateCheckBox().getValue()));
		pDTO.setHasOwnershipControl(ClientUtils.fromBoolean(getView().getOwnCheckBox().getValue()));
		String pStr = getPermissionStr(pDTO);

		if (dto.isGroup())
		{
			grantPermissionToGroup(pDTO, dto.getName());
		}
		else
		{
			grantPermissionToUsers(pDTO, dto.getName());
		}
	}

	@Override
	public void clearCheckBoxSelections()
	{
	  Set<UserGroupComboDTO> selectedSet = selectionModel.getSelectedSet();
	  if (selectedSet.size() > 0)
	  {
			for (UserGroupComboDTO dto : selectedSet) {
				dto.setSelected(false);
				selectionModel.setSelected(dto, false);
			}
	  }
	}

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
		
	}

	public void refreshDataGrid()
	{
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}


	@Override
	public void back()
	{
        placeManager.navigateBack();
	}

	@Override
	public void mayTakeOwnershipCheckBoxCallback()
	{
		showMessage("");
		enableGrantButton(nCheckBoxSelected() > 0);
		/*
		enableGrantButton(nCheckBoxSelected() > 0);
		getView().getReadCheckBox().setValue(false);
		*/
	}

	@Override
	public void mayUpdateCheckBoxCallback()
	{
		showMessage("");
		enableGrantButton(nCheckBoxSelected() > 0);
		//getView().getReadCheckBox().setValue(false);
		//getView().getMayTakeOwnershipCheckBox().setValue(false);
	}

	@Override
	public void addCheckBoxCallback()
	{
		showMessage("");
		enableGrantButton(nCheckBoxSelected() > 0);
	}

	// not used anymore
	@Override
	public void readCheckBoxCallback()
	{
		showMessage("");
		enableGrantButton(nCheckBoxSelected() > 0);
		//getView().getMayUpdateCheckBox().setValue(false);
		//getView().getMayTakeOwnershipCheckBox().setValue(false);
	}

	@Override
	public void help()
	{
		showHelp();
	}

	private void enableGrantButton(boolean enabled)
	{
		getView().getGrantButton().setEnabled(enabled);
		if (enabled)
		{
//			boolean b = getView().getReadCheckBox().getValue() | getView().getMayTakeOwnershipCheckBox().getValue() | getView().getMayUpdateCheckBox().getValue();
			boolean b = getView().getAddCheckBox().getValue() | getView().getUpdateCheckBox().getValue() | getView().getOwnCheckBox().getValue();
			if (b)
			{
				getView().getGrantButton().setEnabled(true);
			}
			else
			{
				getView().getGrantButton().setEnabled(false);
			}
		}
	}

	private void enableClearButton(boolean enabled)
	{
		getView().getClearButton().setEnabled(enabled);
	}

	private String getPermissions(UserGroupComboDTO dto)
	{
		String pStr = glang.read();
		if (dto != null)
		{
			boolean add = ClientUtils.fromBoolean(dto.getAddPermitted());
			if (add)
			{
				pStr += ", " + glang.add();
			}
			boolean update = ClientUtils.fromBoolean(dto.getUpdatePermitted());
			if (update)
			{
				pStr += ", " + glang.edit();
			}
			boolean own = ClientUtils.fromBoolean(dto.getOwnershipControl());
			if (own)
			{
				pStr += ", " + glang.own();
			}
		}
		else
		{
			gwtLog("MMM getPermissions UserGroupComboDTO is null");
			
		}
		gwtLog("MMM getPermissions for " + dto.getName() + pStr);
		return pStr;
	}

    private void showUsersAndGroups(final SelectionModel<UserGroupComboDTO> selectionModel, final AbstractCellTable<UserGroupComboDTO> grid, HasHandlers source)
	{
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);
	    
	    //Checkbox
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
            	gwtLog("Fullname: " + dto.getFullname() + " x: " + dto.getName());
                return glang.na();
            }
        };
        grid.addColumn(nameCol, glang.userGroupName());

  		// Permissions
		TextColumn<UserGroupComboDTO> permissionColumn = new TextColumn<UserGroupComboDTO>()
		{

			@Override
			public String getValue(UserGroupComboDTO dto)
			{
				gwtLog(">>>User: " + dto.getFullname());
				gwtLog("     >>>update: " + dto.getUpdatePermitted());
				gwtLog("	   >>> Own: " + dto.getOwnershipControl());

				return getPermissions(dto);
			}
		};
		grid.addColumn(permissionColumn, ObidosMessages.LANG.currentPermissions());


		// Should UserGroupComboDTO have username?
		// Any user can obtain all the username and login attempt can be made.
		// Just curious

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
					}

					@Override
					public void uponSuccess(UserGroupComboResult userResult)
					{
						int numberOfUsers = userResult.getTotalUsers();
						List<UserGroupComboDTO> users = userResult.getUsers();
						if (users != null && ! users.isEmpty())
						{
							updateRowCount(numberOfUsers, true);
							updateRowData(start, users);
						} else
						{
							messageLabel.setText("No users found ...");
							updateRowCount(0, true);
						}
					}
				};
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				Long containerId = getContainerIdFromUrl();
				if (containerId == null)
				{
					showErrorMessage("Could not get Container Id from URL");
					return;
				}
				String searchString = getView().getSearchTextBox().getValue();
				if (searchString.length() == 0)
				{
					searchString = null;
				}

				UserService.Utility.getInstance().getContainerShares(authCreds, containerId, searchString, null, start, length, toArray(OrderBy.USER_GROUP_COMBO_NAME_ASC), callback);
			} // end onRangeChanged()
		};
		
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
            	// noop
            }
        });

		getView().getPager().setDisplay(grid);
		dataProvider.addDataDisplay(grid);
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
