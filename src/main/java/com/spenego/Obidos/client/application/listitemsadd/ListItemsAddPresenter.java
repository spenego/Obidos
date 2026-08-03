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

package com.spenego.Obidos.client.application.listitemsadd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
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
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroupAddon;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.LimitedItemDTO;

public class ListItemsAddPresenter extends ObidosPresenter<LimitedItemDTO, ListItemsAddPresenter.MyView, ListItemsAddPresenter.MyProxy, ListItemsAddUiHandlers>
		implements ListItemsAddUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	interface MyView extends View, HasUiHandlers<ListItemsAddUiHandlers>
	{
		public DataGrid<LimitedItemDTO> getDataGrid();
		public SimplePager getPager();
		public TextBox getContainerNameTextBox();
		public TextBox getSearchTextBox();
		public BlockQuote getHelpBlockQuote();
		public Button getNewItemButton();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public ObidosInputGroupAddon getContainerTypeAddon();
	}

	@NameToken(NameTokens.LIST_ITEMS_ADD)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListItemsAddPresenter>
	{
	}
	
	@Inject
	ListItemsAddPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
		
		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
		DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
		showItemsDatagrid(grid, this);
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		refreshDataGrid();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage(null);
		fetchAndPopulateForm();
		refreshDataGrid();
		DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
	}
	
	private void fetchAndPopulateForm()
	{
		Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
		if (containerId == null)
		{
			return;
		}

		GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
		{

			@Override
			public void uponSuccess(ContainerDTO dto)
			{
				getView().getContainerNameTextBox().setValue(dto.getName());
				boolean isPrivate = ClientUtils.fromBoolean(dto.getIsPrivate());
				ObidosInputGroupAddon addon = getView().getContainerTypeAddon();
				ClientUtils.setTypeAddon(addon, false);
				if (!isPrivate)
				{
					ClientUtils.setTypeAddon(addon, true);
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch Container");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
	}

	protected void showMessage(final String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	protected void showErrorMessage(final String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
		
	}

	@Override
	public void sort()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void search()
	{
		refreshDataGrid();
	}

	@Override
	protected String getIdName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void showItemsDatagrid(final AbstractCellTable<LimitedItemDTO> grid, HasHandlers source)
	{
		ObidosMessages lang = ObidosMessages.LANG;
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(lang.loading());
		grid.setEmptyTableWidget(messageLabel);
		
		// cItem name
		ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_NAME);
		final Column<LimitedItemDTO, String> nameCol = new Column<LimitedItemDTO, String>(nameCell)
		{

			@Override
			public String getValue(LimitedItemDTO dto)
			{
				return dto.getName();
			}
		};
		grid.addColumn(nameCol, lang.itemName());
		
        // Item Type, Private or Shareable
        ObidosButtonCell ctc = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_SHAREABLE_OR_PRIVATE);
        final Column<LimitedItemDTO, String> itemTypeCol = new Column<LimitedItemDTO, String>(ctc)
        {

            @Override
            public String getValue(LimitedItemDTO dto)
            {
                return "noteUsed";
            }

        };
        grid.addColumn(itemTypeCol, glang.itemType());

		AsyncDataProvider<LimitedItemDTO> dataProvider = new AsyncDataProvider<LimitedItemDTO>()
		{

			@Override
			protected void onRangeChanged(HasData<LimitedItemDTO> dto)
			{
				final Range range = dto.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				GwtAsyncWrapper<ItemsResult> callback = new GwtAsyncWrapper<ItemsResult>(source)
				{

					@Override
					public void uponFailure(Throwable e)
					{
						String message = e.getMessage();
						showErrorMessage(ObidosMessages.LANG.couldNotFetchItems(message));
					}

					@Override
					public void uponSuccess(ItemsResult result)
					{
						int numberOfItems = result.getTotalItems();
						gwtLog("Number of items: " + numberOfItems);
						if (numberOfItems == 0)
						{
							messageLabel.setText(glang.containerIsEmpty());
							updateRowCount(0, true);
							return;
						}
						List<LimitedItemDTO> items = result.getElements();
						if (items != null && items.size() > 0)
						{
							updateRowCount(numberOfItems, true);
							updateRowData(start, items);
						} else
						{
							messageLabel.setText(glang.containerIsEmpty());
							updateRowCount(0, true);
						}
					}
				};
				String search = getView().getSearchTextBox().getValue();
				if (search == null || search.length() == 0)
				{
					search = null;
				}
				Boolean shared = null;
				Long containerId = getContainerIdFromUrl();
				if (containerId == null)
				{
					return;
				}
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				gwtLog("Container id: " + containerId);
				ArrayList<Long> preSelectedIds = null;
//				ItemService.Utility.getInstance().getMyItems(authCreds, containerId, shared, search, preSelectedIds, start, length, toArray(OrderBy.CREATE_TIME_DESC), callback);
				ItemService.Utility.getInstance().getMyItems(authCreds, containerId, shared, search, preSelectedIds, start, length, getOrderByList(), callback);
			}
		};

        nameCol.setFieldUpdater(new FieldUpdater<LimitedItemDTO, String>()
        {

            @Override
            public void update(int idx, LimitedItemDTO dto, String value)
            {
                sendToCorrectPlace(dto);
            }

        });

		getView().getPager().setDisplay(grid);
		dataProvider.addDataDisplay(grid);

	}
	
	private void refreshDataGrid()
	{
		DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
		Range range = new Range(0, ObidosConstants.VISIBLE_GRID_COUNT);
		grid.setVisibleRangeAndClearData(range, true);
	}

	private void sendToCorrectPlace(LimitedItemDTO dto)
	{
		boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
		if (rc)
		{
			showViewItemPage(dto);
		}
		else
		{
			ClientUtils.showRegisterPassphraseDialog(placeManager, glang.viewItem(), null);
		}
		
	}
	private void showViewItemPage(LimitedItemDTO dto)
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			return;
		}
		String nameToken = NameTokens.VIEW_ITEM;
		Map<String, String> with = new HashMap<>();

		String key = ObidosConstants.CONTAINER_ID;
		with.put(key, containerId.toString());

		key = ObidosConstants.ITEM_ID;
		with.put(key, dto.getId().toString());

		key = ObidosConstants.PLACE;
		with.put(key, NameTokens.LIST_ITEMS);
		
		String place = ClientUtils.getPlaceFromUrl(placeManager);
		if (place != null)
		{
			with.put(ObidosConstants.FROM,place);
		}

		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void addItem()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

		Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
		if (containerId == null)
		{
			showErrorMessage("Could not get " + ObidosConstants.CONTAINER_ID + " from URL");
			return;
		}
		String nameToken = NameTokens.PICK_ITEM_TYPE;
		Map<String, String> with = new HashMap<>();
		with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
		with.put(ObidosConstants.PLACE, NameTokens.LIST_ITEMS_ADD);
		ClientUtils.showPage(placeManager, nameToken, with);
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
	public void sortReverseByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByAZ()
	{
		setOrderBy(OrderBy.ITEM_NAME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
		refreshDataGrid();
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}


}