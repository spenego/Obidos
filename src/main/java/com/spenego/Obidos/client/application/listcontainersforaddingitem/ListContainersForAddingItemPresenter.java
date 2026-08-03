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

package com.spenego.Obidos.client.application.listcontainersforaddingitem;

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
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ListContainersForAddingItemPresenter
		extends Presenter<ListContainersForAddingItemPresenter.MyView, ListContainersForAddingItemPresenter.MyProxy>
		implements ListContainersForAddingItemUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private OrderBy sOrderBy;
    private enum FieldNumber
    {
        nameCol,
        addItemCol,
    };

	interface MyView extends View, HasUiHandlers<ListContainersForAddingItemUiHandlers>
	{
    	public BlockQuote getHelpBlockQuote();
        public TextBox getSearchTextBox();
        public Button getSearchButton();
        public SimplePager getPager();
        public DataGrid<ContainerDTO> getDataGrid();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
	}

	@NameToken(NameTokens.LIST_CONTAINERS_ADD_ITEM)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<ListContainersForAddingItemPresenter>
	{
	}

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;
	@Inject
	ListContainersForAddingItemPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        super.onBind();
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        showContainerList(grid, this);

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
        getView().getPanelHeader().getBackButton().setText(glang.listItems());
        resetForm();
        refreshDataGrid();
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        
        /*
        if (ClientUtils.isjQueryInjected())
        {
        	gwtLog(">>>>>>>>>>>> jQuery alreaded loaded");
        	ClientUtils.activatejQueryTooltips();
        }
        else
        {
        	gwtLog(">>>>>>>>>> jQuery NOT loaded");
        }
        */
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

    private void resetForm()
    {
        showMessage("");
        getView().getSearchTextBox().setValue("");
    }

    public void refreshDataGrid()
    {
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        Range range = new Range(0,ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range,true);

    }
    private <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
	}


    private void showContainerList(final AbstractCellTable<ContainerDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText("Loading ...");
        grid.setEmptyTableWidget(messageLabel);

         // clickable container name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_NAME);
        final Column<ContainerDTO, String> nameCol = new Column<ContainerDTO, String>(nameCell)
        {

            @Override
            public String getValue(ContainerDTO dto)
            {
                return dto.getName();
            }
        };
        grid.addColumn(nameCol, glang.containerNameLabel());

        
        // Container Type, Private or Shareable
        ObidosButtonCell ctc = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_SHAREABLE_NO_TOOLTIP);
        final Column<ContainerDTO, String> containerTypeCol = new Column<ContainerDTO, String>(ctc)
        {

            @Override
            public String getValue(ContainerDTO containerDTO)
            {
                return ObidosMessages.LANG.containerTypeLabel();
            }

        };
        grid.addColumn(containerTypeCol, ObidosMessages.LANG.containerTypeLabel());

        // Container Status. Not Shared or Shared
        /*
        ObidosButtonCell csc = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_SHARED_NO_TOOLTIP);
        final Column<ContainerDTO, String> containerStatusCol = new Column<ContainerDTO, String>(csc)
        {

            @Override
            public String getValue(ContainerDTO containerDTO)
            {
                return ObidosMessages.LANG.containerStatus();
            }

        };
        grid.addColumn(containerStatusCol, ObidosMessages.LANG.containerStatus());
        */

        		

        // Add
        ObidosButtonCell addItemToContainerCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ADD);
        final Column<ContainerDTO, String> addItemCol = new Column<ContainerDTO, String>(addItemToContainerCell)
        {

            @Override
            public String getValue(ContainerDTO containerDTO)
            {
                return ObidosMessages.LANG.addButtonTitle();
            }

        };
        grid.addColumn(addItemCol, ObidosMessages.LANG.addItem());

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
                        ClientUtils.showBootboxDialog("ERROR", message);
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
//                            String msg = "Found " + numberOfContainers + " Containers";
//                            showMessage(msg);
                        }
                        else
                        {
                            String msg = "No Containers found...";
                            messageLabel.setText(msg);
                            updateRowCount(0, true);
                        }
                    }
                };
                Long userId = currentUser.getUserDTO().getId();
                gwtLog("User id got from CurrentUser: " + userId);
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
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                ContainerService.Utility.getInstance().getMyContainers(authCreds, search, null, null, null, start, length, toArray(sOrderBy), callback);
            }
        };
        // handler for name column
        nameCol.setFieldUpdater(new FieldUpdater<ContainerDTO, String>()
        {

            @Override
            public void update(int idx, ContainerDTO containerDTO, String value)
            {
                sendToCorrectPlace(containerDTO, FieldNumber.nameCol);
            }

        });


        // handler for add item button
        addItemCol.setFieldUpdater(new FieldUpdater<ContainerDTO, String>()
        {

            @Override
            public void update(int idx, ContainerDTO containerDTO, String value)
            {
                sendToCorrectPlace(containerDTO, FieldNumber.addItemCol);
            }

        });


        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
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
	@Override
	public void help()
	{
		BlockQuote bq = getView().getHelpBlockQuote();
		if (bq.isVisible())
		{
			bq.setVisible(false);
		}
		else
		{
			bq.setVisible(true);
		}
	}
    @Override
    public void searchContainerName()
    {
        refreshDataGrid();
    }

    private void sendToCorrectPlace(final ContainerDTO dto, FieldNumber fieldNumber)
    {
        if (currentUser != null)
        {
            gwtLog("Passphrase cached: " + currentUser.getPassphraseRegistered());
        }
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
                switch(fieldNumber)
                {
					case nameCol:
					{
						if (rc)
						{
							showListItemsPage(dto);
						}
						else
						{
                            ClientUtils.showRegisterPassphraseDialog(placeManager, glang.listItemsInContainer(), null);
						}
						break;
					}
                    case addItemCol:
                    {
//                    	int licenseState = ClientUtils.popLicenseWarningDialog(currentUser.getUserDTO().getLicense());
 //                   	String msg = ClientUtils.getLicenseStateMessage(licenseState);
//                    	gwtLog("License msg: " + msg);
                    	if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
                    	{
                    		return;
                    	}
                        if (rc)
                        {
                            navigateToPickItemTypeView(dto);
                        }
                        else
                        {
                            ClientUtils.showRegisterPassphraseDialog(placeManager, glang.addItemToAContainer(),null);
                        }
                        break;
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
	private void showListOfUsersTheContainerIsSharedWith(ContainerDTO dto)
	{
        String nameToken = NameTokens.LIST_USERS_CONTAINER_IS_SHARED_WITH;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, dto.getId().toString());
        with.put(ObidosConstants.PLACE, NameTokens.LIST_CONTAINERS_ADD_ITEM);
        ClientUtils.showPage(placeManager, nameToken, with);
	}
    private void navigateToPickItemTypeView(ContainerDTO containerDTO)
    {
         PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.PICK_ITEM_TYPE)
                .with(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString())
                .with(ObidosConstants.PLACE, NameTokens.LIST_CONTAINERS_ADD_ITEM)
                .build();
		placeManager.revealPlace(placeRequest);
    }

    private void showListItemsPage(ContainerDTO containerDTO)
    {
    	String nameToken = NameTokens.LIST_ITEMS_ADD;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString());
        String action = getActionFromURL();
        if (action != null)
        {
        	with.put(ObidosConstants.ACTION,action);
        }
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }
        with.put(ObidosConstants.PLACE, NameTokens.LIST_CONTAINERS_ADD_ITEM);
        ClientUtils.showPage(placeManager, nameToken, with);
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
		setOrderBy(OrderBy.CONTAINER_NAME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.CONTAINER_NAME_DESC);
		refreshDataGrid();
	}

	@Override
	public void listMyItems()
	{
		ClientUtils.showPage(placeManager, NameTokens.LIST_ALL_MY_ITEMS);
	}

}
