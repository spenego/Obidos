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

package com.spenego.Obidos.client.application.listitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
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
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.spenego.Obidos.client.application.ObidosPresenter;
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
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ListItemsPresenter extends
		ObidosPresenter<LimitedItemDTO, ListItemsPresenter.MyView, ListItemsPresenter.MyProxy, ListItemsUiHandlers>
		implements ListItemsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    private ArrayList<OrderBy> sOrderbyList = new ArrayList<>();

	interface MyView extends View, HasUiHandlers<ListItemsUiHandlers>
	{
		public DataGrid<LimitedItemDTO> getDataGrid();
		public SimplePager getPager();
		public TextBox getContainerNameTextBox();
		public TextBox getSearchTextBox();
		public BlockQuote getHelpBlockQuote();
		public Button getListButton();
		public FormLabel getFormErrorLabel();
		public Button getDeleteButton();
		public Button getClearButton();
		public Button getNewItemButton();
		public Button getHelpButton();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
	}

	@NameToken(NameTokens.LIST_ITEMS)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListItemsPresenter>
	{
	}

	@Inject
	ListItemsPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0),
				grid -> showItemsDatagrid(selectionModel, grid, this));
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
		clearSelections();
		updatePanelHeading();
		resetForm();
		setDefaultSortOrder();
		fetchAndPopulateForm();
		refreshDataGrid();
		DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);

		grid.setColumnWidth(0, "50px");
		grid.setColumnWidth(1, "35%");
	}

    private void setDefaultSortOrder()
    {
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
    }

	private void enableButtons(boolean enabled)
	{
		getView().getDeleteButton().setEnabled(enabled);
		getView().getClearButton().setEnabled(enabled);
	}

	// TODO: This should be moved to ObidosPresenter
	private <T, C> void setColumnUpdater(Column<T, C> col, final Consumer<T> consumer,
			final Supplier<C> passphraseDialogMessage)
	{
		col.setFieldUpdater(new FieldUpdater<T, C>()
		{
			@Override
			public void update(int idx, T dto, C value)
			{
				boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
				currentUser.setPassphraseRegistered(rc);
				if (rc)
				{
					gwtLog("Passphrase is registered");
					consumer.accept(dto);
				} else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, passphraseDialogMessage.get().toString(),
							null);
				}
			}
		});
	}

	private String getActionFromURL()
	{
		String key = ObidosConstants.ACTION;
		try
		{
			return ClientUtils.getParameterFromUrl(placeManager, key);
		} catch (ParamNotFoundException e)
		{
		}
		return null;
	}

	// Issue #288
	private void updatePanelHeading()
	{
		String action = getActionFromURL();
		if (action != null && action.equals(ObidosConstants.SHARED_WITH_OTHERS))
		{
			String txt = glang.listItemsInContainerSharedWithOthers();
			getView().getPanelHeader().setText(txt);
		}
		else
		{
			getView().getPanelHeader().setText(glang.listOfItemsInContainer());
		}
		Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
		String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.SHARED_WITH_OTHERS.equals(type) && containerId != null)
		{
			getView().getPanelHeader().setText(glang.itemsInSharedContainer());
		}
	}

	private void resetForm()
	{
		showMessage("");
		getView().getSearchTextBox().setValue("");
	}

	private void fetchAndPopulateForm()
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			return;
		}

		GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
		{

			@Override
			public void uponSuccess(ContainerDTO result)
			{
				getView().getContainerNameTextBox().setValue(result.getName());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch Container: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
	}

	private void showItemsDatagrid(final SelectionModel<LimitedItemDTO> selectionModel,
			final AbstractCellTable<LimitedItemDTO> grid, HasHandlers source)
	{
		ObidosMessages lang = ObidosMessages.LANG;
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(lang.loading());
		grid.setEmptyTableWidget(messageLabel);

		addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
		// Column<LimitedItemDTO, String> nameColumn =
		// addObidosButtonCellColumn(grid, dto -> dto.getName(),
		// lang.itemName(), ObidosConstants.CELL_TYPE_SHARED_WITH_OTHERS);

		ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_NAME);
		final Column<LimitedItemDTO, String> nameCol = new Column<LimitedItemDTO, String>(nameCell)
		{

			@Override
			public String getValue(LimitedItemDTO dto)
			{
				return dto.getName();
			}
		};
		grid.addColumn(nameCol, lang.itemNameLabel());

		final Column<LimitedItemDTO, String> shareCol = addObidosButtonCellColumn(grid, dto -> lang.shareButtonTitle(), lang.shareButtonTitle(), ObidosConstants.CELL_TYPE_SHARE);
		final Column<LimitedItemDTO, String> revokeCol = addObidosButtonCellColumn(grid, dto -> lang.revokeButtonTitle(), lang.revokeButtonTitle(), ObidosConstants.CELL_TYPE_REVOKE);
		final Column<LimitedItemDTO, String> editCol = addObidosButtonCellColumn(grid, dto -> lang.editButtonTitle(), lang.editButtonTitle(), ObidosConstants.CELL_TYPE_EDIT);
		
		
        ObidosButtonCell listUsersCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_SHARED_WITH_USERS_GROUPS);
        final Column<LimitedItemDTO, String> listUsersCol = new Column<LimitedItemDTO, String>(listUsersCell)
        {

            @Override
            public String getValue(LimitedItemDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(listUsersCol, lang.sharedWith());

		
		
		// Column<LimitedItemDTO, String> viewItemCol =
		// addObidosButtonCellColumn(grid, dto -> lang.view(), lang.view(),
		// ObidosConstants.CELL_TYPE_VIEW);

		// update cell is no longer clickable. we use revoke buttons now
		// Jul-29-2018
		// It's clickable again: Issue #276
		// setColumnUpdater(nameColumn, dto ->
		// showListOfUsersTheItemIsSharedWith(dto), () ->
		// lang.listOfUsersItemIsSharedWith());
		setColumnUpdater(nameCol, dto -> showViewItemPage(dto), () -> lang.viewItem());
		setColumnUpdater(shareCol, dto -> showShareWithPage(dto), () -> lang.shareAnItem());
		setColumnUpdater(revokeCol, dto -> showPickRevokeUsersGroupsPage(dto), () -> lang.revokeSharingAItemFromUsers());
		setColumnUpdater(editCol, dto -> showEditItemPage(dto, ObidosConstants.EDIT), () -> lang.editAnItem());
        setColumnUpdater(listUsersCol, 	dto -> showListOfUsersTheItemIsSharedWith(dto), 	() -> lang.listOfUsersItemIsSharedWith());

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

				Long containerId = getContainerIdFromUrl();
				if (containerId == null)
				{
					return;
				}

                String searchText = getView().getSearchTextBox().getValue();
                String search = (searchText != null && searchText.length() > 0) ? searchText : null;
                gwtLog(">>> Search item: " + search);
				Boolean shared = null;
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				gwtLog("Container id: " + containerId);
				ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<LimitedItemDTO>) selectionModel);
                ArrayList<OrderBy> orderByList = getOrderByList();
                safeInvocationCall(() -> ItemService.Utility.getInstance().getMyItems(authCreds, containerId, shared, search, preSelectedIds, start, length, orderByList, callback));
			}
		};
		// handler for shareCol
		getView().getPager().setDisplay(grid);
		dataProvider.addDataDisplay(grid);
	}

	private void refreshDataGrid()
	{
		DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
	}

	@Override
	public void listMyContainers()
	{
		/*
		 * String action = getActionFromURL(); String place =
		 * ClientUtils.getPlaceFromUrl(placeManager); if (place != null) {
		 * ClientUtils.showPage(placeManager, place); return;
		 * 
		 * } String nameToken = NameTokens.LIST_CONTAINERS; if (action != null
		 * && action.equals(ObidosConstants.SHARED_WITH_OTHERS)) {
		 * Map<String,String> with = new HashMap<>();
		 * with.put(ObidosConstants.ACTION,action);
		 * ClientUtils.showPage(placeManager, nameToken, with); } else {
		 * ClientUtils.showPage(placeManager, NameTokens.LIST_CONTAINERS); }
		 */
		ClientUtils.navigateToPlace(placeManager, NameTokens.LIST_CONTAINERS);
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

	private void showEditItemPage(LimitedItemDTO dto, String actionValue)
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		gwtLog("Show edit Item page");
		gwtLog("Item ID: " + dto.getId());

		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			return;
		}
		String nameToken = NameTokens.EDIT_ITEM;
		Map<String, String> with = new HashMap<>();

		String key = ObidosConstants.CONTAINER_ID;
		with.put(key, containerId.toString());

		key = ObidosConstants.ACTION;
		with.put(key, actionValue);

		key = ObidosConstants.ITEM_ID;
		with.put(key, dto.getId().toString());
		with.put(ObidosConstants.PLACE, NameTokens.LIST_ITEMS);

		ClientUtils.showPage(placeManager, nameToken, with);

		/*
		 * GwtAsyncWrapper<ItemDTO> callback = new
		 * GwtAsyncWrapper<ItemDTO>(this) {
		 * 
		 * @Override public void uponSuccess(ItemDTO result) {
		 * ArrayList<UserDefinedTypeValueDTO> typeValues = result.getValues();
		 * gwtLog("  TypeValues Size: " + typeValues.size());
		 * gwtLog("        Item name: " + result.getName()); for
		 * (UserDefinedTypeValueDTO typeValue:typeValues) {
		 * ArrayList<UserDefinedFieldValueDTO> fieldValues =
		 * typeValue.getFieldValues(); gwtLog(">> Field Values size: " +
		 * fieldValues.size()); gwtLog(">> is it a note?: " +
		 * typeValue.isNote()); for (UserDefinedFieldValueDTO fieldValue:
		 * fieldValues) { gwtLog("Field ID:   " + fieldValue.getId());
		 * gwtLog("VALUE: " + fieldValue.getStringValue()); byte[] noteBytes =
		 * fieldValue.getBlobValue(); try { String note = new String(noteBytes,
		 * "UTF-8"); gwtLog("String: "+ note); } catch
		 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } }
		 * 
		 * }
		 * 
		 * @Override public void uponFailure(Throwable caught) {
		 * showErrorMessage("Could not get Item: " + caught.getMessage()); } };
		 * AuthCredsDTO authCreds = ClientUtils.getAuthCreds(); byte[]
		 * passPhrase = null; ItemService.Utility.getInstance().get(authCreds,
		 * dto.getId(), passPhrase, callback);
		 */
	}

	private void showShareWithPage(LimitedItemDTO dto)
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

		Long containerId = getContainerIdFromUrl();

		Map<String, String> with = new HashMap<>();
		String key = ObidosConstants.SHARE;
		String value = ObidosConstants.ITEM;
		with.put(key, value);
		key = ObidosConstants.ITEM_ID;
		value = dto.getId().toString();
		with.put(key, value);

		with.put(ObidosConstants.PLACE, NameTokens.LIST_ITEMS);

		if (containerId != null)
		{
			with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
		}
		ClientUtils.showPage(placeManager, NameTokens.SHARE_WITH, with);
	}



	@Override
	public void searchItemName()
	{
		refreshDataGrid();
	}

	private void showPickRevokeUsersGroupsPage(LimitedItemDTO dto)
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showErrorMessage("Could not get " + ObidosConstants.CONTAINER_ID + " from URL");
			return;
		}
		/*
		PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.PICK_REVOKE_SHARING)
				.with(ObidosConstants.CONTAINER_ID, containerId.toString())
				.with(ObidosConstants.ITEM_ID, dto.getId().toString())
				.with(ObidosConstants.ACTION, ObidosConstants.REVOKE_ITEM_SHARING)
				.with(ObidosConstants.PLACE, NameTokens.LIST_ITEMS).build();
		placeManager.revealPlace(placeRequest);
		*/
		// Issue #789. Click on Revoke was showing About page because the
		// NameToken was pointing to a non-existing page PICK_REVOKE_SHARING,
		// it should have been REVOKE_ITEM 
		String nameToken = NameTokens.REVOKE_ITEM;
        Map<String,String> with = new HashMap<>();
		with.put(ObidosConstants.CONTAINER_ID,containerId.toString());
		with.put(ObidosConstants.ITEM_ID,dto.getId().toString());
		with.put(ObidosConstants.ACTION,ObidosConstants.REVOKE_ITEM_SHARING);
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
		ClientUtils.addPlace(placeManager, with, NameTokens.LIST_ITEMS);
		ClientUtils.showPage(placeManager, nameToken, with);

	}

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

	@Override
	public void addNewItem()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showErrorMessage("Could not get " + ObidosConstants.CONTAINER_ID + " from URL");
			return;
		}

		PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.PICK_ITEM_TYPE)
				.with(ObidosConstants.CONTAINER_ID, containerId.toString())
				.with(ObidosConstants.PLACE, NameTokens.LIST_ITEMS).build();
		placeManager.revealPlace(placeRequest);

	}

	@Override
	public void deleteItems()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		gwtLog("Registered: " + registered);
		if (registered)
		{
			promptDeleteItems();
		} else
		{
			String message = ObidosMessages.LANG.deleteItem();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
	}

	private void promptDeleteItems()
	{
		ObidosMessages lang = ObidosMessages.LANG;
		String title = lang.deleteItem();
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ITEM_ID_N);
		title = lang.deleteNote();
		String message = lang.deleteSomethingWarning(ids.size(), s, s, s);
		ClientUtils.promptForAction(() -> deleteItemsReal(), title, message);
	}

	private void deleteItemsReal()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage(ObidosMessages.LANG.noItemsSelected());
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearSelections();
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				String errorMessage = glang.couldNotDeleteItems() + ":" + caught.getMessage();
				showErrorMessage(errorMessage);
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().delete(authCreds, ids, callback);
	}

	@Override
	public void clearSelections()
	{

		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
	}

	@Override
	protected String getIdName()
	{
		return ObidosConstants.ITEM_ID;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	public void back()
	{
		String place = ClientUtils.getPlaceFromUrl(placeManager);
		if (place != null)
		{
			String nameToken = place;
			ClientUtils.showPage(placeManager, nameToken);
		}
		else
		{
			ClientUtils.goBack(placeManager);
		}
	}

	private void setOrderBy(OrderBy orderBy)
	{
		sOrderBy = orderBy;
	}
	
	@Override
	public void sortItemByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid();
	}

	@Override
	public void sortItemReverseByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_ASC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid();
	}

	@Override
	public void sortItemByAZ()
	{
		setOrderBy(OrderBy.ITEM_NAME_ASC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid();
	}

	@Override
	public void sortItemByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid();
	}


    protected ArrayList<OrderBy> getOrderByList()
    {
    	return sOrderbyList;
    }

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
     private void showListOfUsersTheItemIsSharedWith(LimitedItemDTO dto)
    {
    	gwtLog("MMM getShareable: " + dto.getShareable());
    	gwtLog("MMM getShared: " + dto.getShared());
    	if (! dto.getShared())
    	{
    		gwtLog("MMM not shared, return");
    		return;
    	}
    	
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.ITEM_ID, dto.getId().toString());
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ALL_MY_ITEMS);
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }
        String nameToken = NameTokens.LIST_USERS_ITEM_IS_SHARED_WITH;
        ClientUtils.showPage(placeManager, nameToken, with);
    }
   
}
