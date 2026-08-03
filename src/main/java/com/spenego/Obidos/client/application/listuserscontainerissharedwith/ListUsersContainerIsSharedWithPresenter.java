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

package com.spenego.Obidos.client.application.listuserscontainerissharedwith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboResult;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ListUsersContainerIsSharedWithPresenter extends
		Presenter<ListUsersContainerIsSharedWithPresenter.MyView, ListUsersContainerIsSharedWithPresenter.MyProxy>
		implements ListUsersContainerIsSharedWithUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
			
	interface MyView extends View, HasUiHandlers<ListUsersContainerIsSharedWithUiHandlers>
	{
		public DataGrid<UserGroupComboDTO> getDataGrid();
		public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public TextBox getSearchTextBox();
		public ObidosInputGroup getContainerNameInputGroup();
		public ObidosMessageRow getMessageRow();
	}

	@NameToken(NameTokens.LIST_USERS_CONTAINER_IS_SHARED_WITH)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<ListUsersContainerIsSharedWithPresenter>
	{
	}

	protected final PlaceManager placeManager;
	@Inject
	ListUsersContainerIsSharedWithPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		this.placeManager = placeManager;

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
		updateContainerInfo();
		refreshDataGrid();
		DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);

//        grid.setColumnWidth(0, "100px");
	}

    private void updateContainerInfo()
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
                GWT.log("Could not get Container " + caught.getMessage());
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

	private Long getContainerIdFromUrl()
	{
		String key = ObidosConstants.CONTAINER_ID;
		try
		{
			return ClientUtils.getIdFromUrl(placeManager, key);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
		}
		return null;

	}

	private void showUserList(final AbstractCellTable<UserGroupComboDTO> grid, HasHandlers source)
	{
		gwtLog("In showUserList..");
		/* don't delete
		// fill 80% of the window
		int h = Window.getClientHeight();
		double hh = h * 0.80;
		h = (int) hh;
		String hs = Integer.toString(h) + "px";
		grid.setHeight(hs);
		*/

		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(ObidosMessages.LANG.loading());
		grid.setEmptyTableWidget(messageLabel);
		
		// Type
		/*
        ObidosButtonCell typeCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_USER_GROUP);
        final Column<UserGroupComboDTO, String> typeCol = new Column<UserGroupComboDTO, String>(typeCell)
        {

            @Override
            public String getValue(UserGroupComboDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(typeCol, glang.type());
        */

		// Name (fullname/group name)
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<UserGroupComboDTO, String> nameCol = new Column<UserGroupComboDTO, String>(nameCell)
        {

            @Override
            public String getValue(UserGroupComboDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, glang.name());

        /*
		TextColumn<UserGroupComboDTO> nameCol = new TextColumn<UserGroupComboDTO>()
		{

			@Override
			public String getValue(UserGroupComboDTO dto)
			{
				if (dto != null)
				{
					return dto.getName();
				}
				else
				{
					return glang.na();
				}
			}
		};
		grid.addColumn(nameCol, glang.name());
		*/


		// Should UserGroupComboDTO have username?
		// Any user can obtain all the username and login attempt can be made.
		// Just curious
        /*
		TextColumn<UserGroupComboDTO> emailColumn = new TextColumn<UserGroupComboDTO>()
		{

			@Override
			public String getValue(UserGroupComboDTO luserDTO)
			{
				if (luserDTO != null)
				{
					return luserDTO.getEmail1();
				} else
				{
					return ObidosMessages.LANG.na();
				}
			}
		};
		grid.addColumn(emailColumn, ObidosMessages.LANG.email());
		*/

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
							gwtLog("XXXXXXXXXXXXXXXXXXXXXXXXX returned empty list");
							// messageLabel.setWidth("20000px");
							// messageLabel.setHeight("2000px");
						}

						if (users != null && users.size() > 0)
						{
							gwtLog("UUUUUUUUUUUUUUUUUUUUUUUUUUU to: " + users.size() + " number: " + numberOfUsers);
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
					showErrorMessage("Could not get Container ID from URL");
					return;
				}
				gwtLog("Container ID: " + containerId);
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
            	if (dto.isUser())
            	{
            		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
            	}
            	else
            	{
            		showListUsersInGroupPage(dto);
            	}
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

	protected <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
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

    public void refreshDataGrid()
    {
        DataGrid<UserGroupComboDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
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

    private void showListUsersInGroupPage(UserGroupComboDTO dto)
    {
    	String nameToken = NameTokens.VIEW_USERS_IN_GROUP;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.GROUP_ID, dto.getId().toString());
        ClientUtils.showPage(placeManager, nameToken, with);
    }
        @Override
        public void back()
        {
                ClientUtils.goBack(placeManager);
        }
}
