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

package com.spenego.Obidos.client.application.selectcontainer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.SharedItemDTO;

public class SelectContainerPresenter
		extends ObidosPresenter<ContainerDTO, SelectContainerPresenter.MyView, SelectContainerPresenter.MyProxy, SelectContainerUiHandlers>
		implements SelectContainerUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	ProvidesKey<ContainerDTO> keyProvider = new ProvidesKey<ContainerDTO>()
	{
	    public Object getKey(ContainerDTO dto)
	    {
	      return dto == null ? null : dto.getId();
	    }
	};

    final SingleSelectionModel<ContainerDTO> selectionModel = new SingleSelectionModel<ContainerDTO>(keyProvider);

	interface MyView extends View, HasUiHandlers<SelectContainerUiHandlers>
	{
		public BlockQuote getHelpBlockQuote();
		public Heading getPanelHeading();
		public TextBox getSearchTextBox();
		public Button getSearchButton();
		public DataGrid<ContainerDTO> getDataGrid();
		public SimplePager getPager();
		public Button getTakeOwnershipButton();
		public Row getSharedByRow();
		public Row getSharedOnRow();
		public DropDownHeader getEmailHeader();
		public DropDownHeader getPhoneHeader();
		public Button getOwnerDropDownAnchor();
		public TextBox getItemNameTextBox();
		public TextBox getSharedOnTextBox();
		public ObidosButtonToolBar getButtonToolBar();
		public ObidosMessageRow getMessageRow();
		public Button getHelpButton();
	}

	@NameToken(NameTokens.SELECT_CONTAINER)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<SelectContainerPresenter>
	{
	}

	@Inject
	SelectContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			final PlaceManager placeManager,
			final CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
		
		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<ContainerDTO> createCheckboxManager());

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
            	ContainerDTO dto = selectionModel.getSelectedObject();
            	if (dto != null)
            	{
            		enableButtons(true);
            	}
            	else
            	{
            		enableButtons(false);
            	}
            }
        });
        showContainerList(selectionModel, grid, this);
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
		showMessage("");
		getView().getButtonToolBar().adjustButtonsWidth();
		clearSelection();
		enableButtons(true);
		showButtons(true);
		updateFormWithItemInfo();
		refreshDataGrid();
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        grid.setColumnWidth(0, "50px");
        popHelpDialog();
	}
	
	// Need to pop a attention grabbing dialog to tell that in order to take
	// ownership a Container must be selected.
	private void popHelpDialog()
	{
		ClientUtils.showBootboxDialog(glang.attention(), glang.selectContainerBeforeTakingOwnership());
	}
	
	private void clearSelection()
	{
		selectionModel.clear();
	}
	
	private void showButtons(boolean visible)
	{
		getView().getTakeOwnershipButton().setVisible(visible);
		
	}
	
	private void enableButtons(boolean enabled)
	{
		getView().getTakeOwnershipButton().setEnabled(enabled);
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

	@Override
	public void takeOwnership()
	{
		ObidosMessages lang = ObidosMessages.LANG;
		String title = lang.takeOwnership();
		ContainerDTO dto = selectionModel.getSelectedObject();
		String message = lang.takeOwnershipOfItemDialogMessage(dto.getName());
		ClientUtils.promptForAction(() -> takeOwnershipReal(), title, message);
		
	}
	public void takeOwnershipReal()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage("Could not find " + ObidosConstants.ITEM_ID + " from URL");
			return;
		}
		
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				Date d = new Date();
				enableButtons(false);
				clearSelection();
				refreshDataGrid();
				showMessage(ObidosMessages.LANG.ownershipOfItemAquired() + " on " + d.toString());
				showButtons(false);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(ObidosMessages.LANG.couldNotTakeOwnershipOfItem() + ":" + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		Long containerId = selectionModel.getSelectedObject().getId();
		ItemService.Utility.getInstance().takeOwnership(authCreds, itemId, containerId, null, callback);
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void search()
	{
		refreshDataGrid();
	}

    public void refreshDataGrid()
    {
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        Range range = new Range(0,ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range,true);
    }
    private void showContainerList(final SelectionModel<ContainerDTO> selectionModel,
    		final AbstractCellTable<ContainerDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText(lang.loading());
        grid.setEmptyTableWidget(messageLabel);

        // Select CheckBox
		Column<ContainerDTO,Boolean> checkColumn =
		new Column<ContainerDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(ContainerDTO dto)
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

        // Container Name
	    TextColumn<ContainerDTO> containerNameCol = new TextColumn<ContainerDTO>()
        {

            @Override
            public String getValue(ContainerDTO dto)
            {
            	if (dto != null)
            	{
            		return dto.getName();
            	}
            	else
            	{
            		return ObidosMessages.LANG.na();
            	}
            }
        };
        grid.addColumn(containerNameCol, ObidosMessages.LANG.containerName());
        		

        AsyncDataProvider<ContainerDTO> dataProvider = new AsyncDataProvider<ContainerDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<ContainerDTO> containerDTO)
            {
                final Range range = containerDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();

                GwtAsyncWrapper<ContainerResult> callback = new GwtAsyncWrapper<ContainerResult>(source)
                {
                    @Override
                    public void uponFailure(Throwable caught)
                    {
                        String message = "Could not retrieve Containers: " + caught.getMessage();
                        showErrorMessage(message);
                    }

                    @Override
                    public void uponSuccess(ContainerResult result)
                    {
                        int numberOfContainers = result.getTotalContainers();
                        if (numberOfContainers == 0)
                        {
                            messageLabel.setText("Could not fetch any containers");
                            updateRowCount(0, true);
                            return;
                        }
                        List<ContainerDTO> containers = result.getContainers();
                        if (containers != null && containers.size() > 0)
                        {
                            updateRowCount(numberOfContainers, true);
                            updateRowData(start, containers);
                        }
                        else
                        {
                            String msg = "No Containers found...";
                            messageLabel.setText(msg);
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Long userId = currentUser.getUserDTO().getId();
                String search = getView().getSearchTextBox().getText(); // why note getValue()?
                gwtLog("Search: '" + search + "'");
                if (search.length() > 0)
                {
                    showMessage("Search: " + search);
                }
                else
                {
                    showMessage("");
                }
                // We can't place a shared item into a Private Container.
                // Issue # 429
                Boolean shareable = Boolean.TRUE;
                ContainerService.Utility.getInstance().getMyContainers(authCreds, search, null, null, shareable, start, length, toArray(OrderBy.CREATE_TIME_DESC), callback);
            }
        };
        
        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

	@Override
	public void viewItem()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage(ObidosMessages.LANG.couldNotGetItemId());
			return;
		}
		Long ownerId = ClientUtils.getOwnerIdFromUrl(placeManager);
		
		String nameToken = NameTokens.VIEW_ITEM;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.ITEM_ID, itemId.toString());
        if (ownerId != null)
        {
        	with.put(ObidosConstants.OWNERID, ownerId.toString());
        }
        String place = NameTokens.LIST_ITEMS_SHARED_WITH_ME;
        ClientUtils.addPlace(placeManager, with, place);
        ClientUtils.showPage(placeManager, nameToken, with);
	}
	
	private void updateFormWithItemInfo()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			return;
		}
		GwtAsyncWrapper<SharedItemDTO> callback = new GwtAsyncWrapper<SharedItemDTO>(this)
		{

			@Override
			public void uponSuccess(SharedItemDTO dto)
			{
				gwtLog("SelectContainerPresenter: shared item name = " + dto.getName());

				if (dto.getOwnerFullname() != null)
				{
					getView().getOwnerDropDownAnchor().setText(dto.getOwnerFullname());
					getView().getEmailHeader().setText(dto.getEmail());
					getView().getPhoneHeader().setText(dto.getPhone());
					Date d = dto.getSharedAt();
					if (d != null)
					{
						getView().getSharedOnTextBox().setValue(d.toString());
					}
					else
					{
						getView().getSharedOnTextBox().setValue(ObidosMessages.LANG.unknown());
					}
				}

				getView().getItemNameTextBox().setValue(dto.getName());
				
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("ERROR: " + caught.getMessage());
			}
			
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().getSharedItem(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
	}
	
	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
}