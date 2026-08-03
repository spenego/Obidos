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

package com.spenego.Obidos.client.application.listitemsinsharedcontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.events.SharedContainerDtoEvent;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
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
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.SharedContainerDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedItemsResult;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public class ListItemsInSharedContainerPresenter
		extends ObidosPresenter<SharedItemDTO, ListItemsInSharedContainerPresenter.MyView, ListItemsInSharedContainerPresenter.MyProxy, ListItemsInSharedContainerUiHandlers>
		implements ListItemsInSharedContainerUiHandlers,
		SharedContainerDtoEvent.SharedContainerDtoEventHandler
{
	private static boolean sCanEdit = false; 
	private SharedContainerDTO sSharedContainerDTO;

	private ObidosMessages glang = ObidosMessages.LANG;
    private enum FieldNumber
    {
    	itemNameCol,
    	ownerFullnameCol,
        editCol,
    };

	interface MyView extends View, HasUiHandlers<ListItemsInSharedContainerUiHandlers>
	{
		public HTMLPanel getMainHtmlPanel();
        public DataGrid<SharedItemDTO> getDataGrid();
        public SimplePager getPager();
        public BlockQuote getHelpBlockQuote();
        public Button getHelpButton();
        public TextBox getSearchTextBox();
        public Button getRelinquishButton();
        public Button getClearButton();
        public ObidosMessageRow getMessageRow();
        public ObidosInputGroup getNameInputGroup();
	}

	@NameToken(NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<ListItemsInSharedContainerPresenter>
	{
	}

	@Inject
	ListItemsInSharedContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
		getView().setUiHandlers(this);
	}

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showItems(selectionModel, grid, this));
    }

	protected void onReveal()
	{
		super.onReveal();
		if (sSharedContainerDTO != null)
		{
			sCanEdit = ClientUtils.fromBoolean(sSharedContainerDTO.getUpdatePermitted());
		}
	}

	protected void onHide()
	{
		sCanEdit=false;
		super.onHide();
	}

	protected void onUnbind()
	{
		sCanEdit=false;
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMainHemlPanel(true);
		showMessage(null);
		getView().getNameInputGroup().setText("");
		clearSelections();
        fetchAndPopulateForm();
        refreshDataGrid();
        DataGrid<SharedItemDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        setColumnWidth(grid, 0, "55px");  // Checkbox column
	}
	
	private void enableButtons(boolean enabled)
    {
		getView().getRelinquishButton().setEnabled(enabled);
		getView().getClearButton().setEnabled(enabled);
    }

	
    private void fetchAndPopulateForm()
    {
    	getView().getNameInputGroup().setText("");
        Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
        if (containerId == null)
        {
            return;
        }

        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponSuccess(ContainerDTO cdto)
            {
            	//boolean canEdit = cdto.getModify();
            	//gwtLog("MMM can edit? " + canEdit);

                getView().getNameInputGroup().setText(cdto.getName());
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

    private void showItems(final SelectionModel<SharedItemDTO> selectionModel,
    		final AbstractCellTable<SharedItemDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText(lang.loading());
        grid.setEmptyTableWidget(messageLabel);

        // Select checkbox
   		Column<SharedItemDTO,Boolean> checkColumn =
		new Column<SharedItemDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(SharedItemDTO dto)
            {
            	if (dto != null && dto.getSelected() == null)
            	{
            		dto.setSelected(false);
            	}
            	setSearchedCheckbox(selectionModel, dto);
                return selectionModel.isSelected(dto);
            }
        };
        grid.addColumn(checkColumn,  lang.selectLabel());

        // Item name Name
         // item name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_NAME);
        final Column<SharedItemDTO, String> nameCol = new Column<SharedItemDTO, String>(nameCell)
        {

            @Override
            public String getValue(SharedItemDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, lang.itemName());


        // owner fullname
        ObidosButtonCell ownerFullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<SharedItemDTO, String> ownerFullnameCol = new Column<SharedItemDTO, String>(ownerFullnameCell)
        {

            @Override
            public String getValue(SharedItemDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(ownerFullnameCol, lang.sharedBy());


         // Edit if allowed
        ObidosButtonCell editCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EDIT_SHARED);
        final Column<SharedItemDTO, String> edCol = new Column<SharedItemDTO, String> (editCell)
        {
				@Override
				public String getValue(SharedItemDTO dto)
				{
					if (sCanEdit)
					{
						dto.setUpdatePermitted(sCanEdit);
					}
					else
					{
						boolean canEdit = ClientUtils.fromBoolean(dto.getUpdatePermitted());
						dto.setUpdatePermitted(canEdit);
					}
					return glang.editItem();
				}

         };
         grid.addColumn(edCol, glang.editItem());

        AsyncDataProvider<SharedItemDTO> dataProvider = new AsyncDataProvider<SharedItemDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<SharedItemDTO> dto)
            {
                final Range range = dto.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                GwtAsyncWrapper<SharedItemsResult> callback = new GwtAsyncWrapper<SharedItemsResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable caught)
                    {
        				try
        				{
        					throw caught;
        				}
        				catch(NoSuchRecordException e)
        				{
        					showMainHemlPanel(false);
        					ClientUtils.showAlertDialogWithCallback(glang.error(), e.getMessage(),
        							()->ClientUtils.goBack(placeManager));
        					
        					return;
        				}
        				catch (Throwable e)
        				{
        					gwtLog("EEE: " + e.getMessage());
        				}
                    	
                        String message = caught.getMessage();
                        showErrorMessage(glang.couldNotFetchItems(message));
                    }

                    @Override
                    public void uponSuccess(SharedItemsResult result)
                    {
                        int numberOfItems = result.getTotal();
                        gwtLog("Number of Items: " + numberOfItems);
                        if (numberOfItems == 0)
                        {
                            messageLabel.setText(glang.containerIsEmpty());
                            updateRowCount(0, true);
                            return;
                        }
                        List<SharedItemDTO> items = result.getElements();
                        if (items != null && items.size() > 0)
                        {
                            updateRowCount(numberOfItems, true);
                            updateRowData(start, items);
                        }
                    }
                };
                Long containerId = getContainerIdFromUrl();
                if (containerId == null)
                {
                    return;
                }
                String search = getView().getSearchTextBox().getValue();
                if (search.length() == 0)
                {
                	search = null;
                }

                gwtLog("Container ID: " + containerId);

                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                ItemService.Utility.getInstance().getItemsSharedWithMe(authCreds, containerId, search, null, start, length, getOrderByList(), callback);
            }

        };

        ownerFullnameCol.setFieldUpdater(new FieldUpdater<SharedItemDTO, String>()
        {

            @Override
            public void update(int idx, SharedItemDTO dto, String value)
            {
                sendToCorrectPlace(dto, FieldNumber.ownerFullnameCol);
            }

         });


        edCol.setFieldUpdater(new FieldUpdater<SharedItemDTO, String>()
        {

            @Override
            public void update(int idx, SharedItemDTO dto, String value)
            {
                sendToCorrectPlace(dto, FieldNumber.editCol);
            }

         });


        nameCol.setFieldUpdater(new FieldUpdater<SharedItemDTO, String>()
        {

            @Override
            public void update(int idx, SharedItemDTO dto, String value)
            {
                gwtLog("Send to corrent place");
                sendToCorrectPlace(dto, FieldNumber.itemNameCol);
            }

        });


        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }


    private void sendToCorrectPlace(SharedItemDTO dto, FieldNumber fieldNumber)
    {
        GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
        {

            @Override
            public void uponFailure(Throwable t)
            {
            }

            @Override
            public void uponSuccess(Boolean rc)
            {
                gwtLog("passphrase cached: " + rc);
                if (rc)
                {
                    currentUser.setPassphraseRegistered(Boolean.TRUE);
                }
                else
                {
                    currentUser.setPassphraseRegistered(Boolean.FALSE);
                }
                switch(fieldNumber)
                {

					case editCol:
					{
						if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
						{
							return;
						}

						if (rc)
						{
							showEditItemPage(dto);
						}
						else
						{
							ClientUtils.showRegisterPassphraseDialog(placeManager,glang.editItem(), null);
						}
						break;
					}
					
                    case itemNameCol:
                    {
                        if (rc)
                        {
                        	viewItemSharedInContainer(dto);
                        }
                        else
                        {
                            ClientUtils.showRegisterPassphraseDialog(placeManager,glang.viewAnItem(),null);
                        }
                        break;
                    }
                    
                    case ownerFullnameCol:
                    {
                    	if (rc)
                    	{
                //    		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
                    		ClientUtils.showUserInfo(placeManager, dto.getOwnerId(), getView().getMessageRow());
                    	}
                    	else
                    	{
                            ClientUtils.showRegisterPassphraseDialog(placeManager,glang.viewUserInfo(),null);
                    	}
                    }

                    default:
                    {
                        break;
                    }
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().isPassphraseCached(authCreds,callback);
    }

    private void refreshDataGrid()
    {
        DataGrid<SharedItemDTO> grid = getView().getDataGrid();
        Range range = new Range(0,ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range,true);
    }

    @Override
    public void listItems()
    {
		String nameToken = NameTokens.LIST_CONTAINERS_SHARED_WITH_ME;
    	ClientUtils.navigateToPlace(placeManager, nameToken);
    }

    private void viewItemSharedInContainer(SharedItemDTO dto)
    {
    	String place = ClientUtils.getPlaceFromUrl(placeManager);
        String nameToken = NameTokens.VIEW_ITEM;
        Map<String,String> with = new HashMap<>();

        String key = ObidosConstants.ITEM_ID;
        with.put(key, dto.getId().toString());
        
        Long containerId = getContainerIdFromUrl();
        if (containerId != null)
        {
        	with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        }
        
        String containerType = ClientUtils.getContainerTypeFromUrl(placeManager);
        if (containerType != null && containerType.equals(ObidosConstants.SHARED))
        {
        	with.put(ObidosConstants.CONTAINER_TYPE, ObidosConstants.SHARED);
        }

        key = ObidosConstants.OWNERID;
        with.put(key,  dto.getOwnerId().toString());
        
        ClientUtils.addPlace(placeManager, with);
        if (place != null)
        {
        	ClientUtils.addPlace(placeManager, with);
        }
        else
        {
        	ClientUtils.addPlace(placeManager, with, NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER);
        }

        ClientUtils.showPage(placeManager, nameToken, with);

    }

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

	@Override
	public void search()
	{
		refreshDataGrid();
	}

    private void showEditItemPage(SharedItemDTO dto)
    {
    	Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
    	String nameToken = NameTokens.EDIT_ITEM;
        Map<String,String> with = new HashMap<>();
        
        if (containerId != null)
        {
        	with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        }

        String key = ObidosConstants.ACTION;
        with.put(key, ObidosConstants.EDIT);

        key = ObidosConstants.ITEM_ID;
        with.put(key, dto.getId().toString());
        key = ObidosConstants.OWNERID;
        with.put(key, dto.getOwnerId().toString());
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER);
        
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);
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

	private void promptRelinquishSharedItems()
	{
    	ObidosMessages lang = ObidosMessages.LANG;
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null)
		{
			return;
		}
		String title = lang.relinquishItem();
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ITEM_ID_N);
		String  message = lang.relinquishSomethingWarning(ids.size(), s, s, s, s);

		ClientUtils.promptForAction(() -> relinquishSharedItemsReal(), title, message);
	}

	@Override
	public void relinquishSharedItems()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		if (registered)
		{
			promptRelinquishSharedItems();
		}
		else
		{
			String message = ObidosMessages.LANG.deleteSharedItem();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
	}

	private void relinquishSharedItemsReal()
    {
        ArrayList<Long> ids = getSelectedIds(selectionModel);
		GwtAsyncWrapper<Void> callback  = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
            	String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ITEM_ID_N);
            	String message = ObidosMessages.LANG.couldNotRelinqushSomething(s) + ": " + e.getMessage();
            	showErrorMessage(message);
            }

            @Override
            public void uponSuccess(Void arg0)
            {
            	clearSelections();
                refreshDataGrid();
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
	
	private void setOrderBy(OrderBy orderBy)
	{
		sOrderBy = orderBy;
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

	private void showMainHemlPanel(boolean visible)
	{
		getView().getMainHtmlPanel().setVisible(visible);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@ProxyEvent
	@Override
	public void onClickSharedContair(SharedContainerDtoEvent event)
	{
		if (event == null)
		{
			return;
		}
		SharedContainerDTO dto = event.getSharedContainerDTO();
		this.sSharedContainerDTO = dto;
		sCanEdit = ClientUtils.fromBoolean(dto.getUpdatePermitted());
		gwtLog("MMM MMM XXX: canEdit? " + sCanEdit);
		refreshDataGrid();
	}

}
