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

package com.spenego.Obidos.client.application.listusersitemissharedwith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
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
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ListUsersItemIsSharedWithPresenter
		extends Presenter<ListUsersItemIsSharedWithPresenter.MyView, ListUsersItemIsSharedWithPresenter.MyProxy>
		implements ListUsersItemIsSharedWithUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	interface MyView extends View, HasUiHandlers<ListUsersItemIsSharedWithUiHandlers>
	{
		public DataGrid<UserGroupComboDTO> getDataGrid();
		public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public FormLabel getItemNameLabel();
		public TextBox getSearchTextBox();
		public Button getSearchButton();
		public Row getContainerNameRow();
		public ObidosInputGroup getContainerNameInputGroup();
		public ObidosInputGroup getNameInputGroup();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
	}

	@NameToken(NameTokens.LIST_USERS_ITEM_IS_SHARED_WITH)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListUsersItemIsSharedWithPresenter>
	{
	}

	protected final PlaceManager placeManager;
	protected final CurrentUser currentUser;

	@Inject
	ListUsersItemIsSharedWithPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
		showUserList(grid, this);
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
		showMessage("");
		fetchAndUpdateForm();
		updatePanelHeadingListButton();
		hideContainerNameForNote();
		refreshDataGrid();
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
	}
	
	private void hideContainerNameForNote()
	{
		getView().getContainerNameRow().setVisible(true);
		getView().getItemNameLabel().setText(ObidosMessages.LANG.itemName());
		String itemType = ClientUtils.getItemTypeFromUrl(placeManager);
		if (ObidosConstants.NOTEBOOK.equals(itemType))
		{
			getView().getContainerNameRow().setVisible(false);
			getView().getItemNameLabel().setText(ObidosMessages.LANG.noteName());
		}
	}



	private void updatePanelHeadingListButton()
	{
		ObidosMessages lang = ObidosMessages.LANG;
		String itemType = getItemTypeFromUrl();
		if (itemType != null && itemType.equals(ObidosConstants.NOTEBOOK))
		{
			// Issue #395
			getView().getPanelHeader().setHeadingText(lang.listOfUsersNoteIsSharedWith());
		}
		else
		{
			getView().getPanelHeader().setHeadingText(lang.listOfUsersItemIsSharedWith());
		}
	}

	private String getItemTypeFromUrl()
	{

        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        String key = ObidosConstants.ITEM_TYPE;
        String param =  placeRequest.getParameter(key,"");
        if (param.equals(""))
        {
        	return null;
        }
        return param;
	}

	private void fetchAndUpdateForm()
	{
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			return;
		}

		GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
		{

			@Override
			public void uponSuccess(ItemDTO dto)
			{
				ClientUtils.setTextToInputGroup(getView().getNameInputGroup(), dto.getName());
				ClientUtils.setTextToInputGroup(getView().getContainerNameInputGroup(), dto.getContainerName());
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

	protected <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<>(1);
		list.add(t);
		return list;
	}


	private void showUserList(final AbstractCellTable<UserGroupComboDTO> grid, HasHandlers source)
	{
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);

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
        grid.addColumn(nameCol, glang.name());
    

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
				Long itemId = getItemIdFromUrl();
				if (itemId == null)
				{
					showErrorMessage("Could not get Item ID from URL");
					return;
				}
				gwtLog("Item ID: " + itemId);
				String searchString = getView().getSearchTextBox().getValue();
				if (searchString.length() == 0)
				{
					searchString = null;
				}

				UserService.Utility.getInstance().getItemShares(authCreds, itemId, searchString, null, start, length, toArray(OrderBy.USER_GROUP_COMBO_NAME_ASC), callback);
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
            }
        });

		getView().getPager().setDisplay(grid);
		dataProvider.addDataDisplay(grid);
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	protected void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}

	protected void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);

	}

	private Long getItemIdFromUrl()
	{
		try
		{
			String key = ObidosConstants.ITEM_ID;
			return ClientUtils.getIdFromUrl(placeManager, key);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
		}
		return null;

	}
	public void refreshDataGrid()
	{
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
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