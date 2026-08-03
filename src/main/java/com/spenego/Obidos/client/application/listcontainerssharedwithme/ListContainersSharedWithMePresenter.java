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

package com.spenego.Obidos.client.application.listcontainerssharedwithme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
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
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.events.SharedContainerDtoEvent;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
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
import com.spenego.Obidos.shared.dto.SharedContainerDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;

public class ListContainersSharedWithMePresenter extends
        ObidosPresenter<SharedContainerDTO, ListContainersSharedWithMePresenter.MyView,ListContainersSharedWithMePresenter.MyProxy, ListContainersSharedWithMeUiHandlers>
        implements ListContainersSharedWithMeUiHandlers
{

	private ObidosMessages glang = ObidosMessages.LANG;
	
    private enum FieldNumber
    {
    	nameCol,
    	sharedByCol,
    };
    interface MyView extends View,HasUiHandlers<ListContainersSharedWithMeUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public ObidosPanelHeader getPanelHeader();
        public SimplePager getPager();
        public DataGrid<SharedContainerDTO> getDataGrid();
        public TextBox getSearchTextBox();
        public FormLabel getFormErrorLabel();
        public Button getRelinquishButton();
        public Button getClearButton();
        public Button getHelpButton();
        public ObidosMessageRow getMessageRow();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

    @NameToken(NameTokens.LIST_CONTAINERS_SHARED_WITH_ME)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListContainersSharedWithMePresenter>
    {
    }

    @Inject
    ListContainersSharedWithMePresenter(EventBus eventBus,MyView view,
            MyProxy proxy, PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus,view,proxy,placeManager, currentUser);
        getView().setUiHandlers(this);
    }

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showContainerList(selectionModel, grid, this));
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
        ClientUtils.resetLanguage(getView().getLanguageRow());
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage(null);
        clearSelections();
        updatePanelHeading(ObidosMessages.LANG.listContainersSharedWithMe());
        DataGrid<SharedContainerDTO> grid = getView().getDataGrid();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        grid.setColumnWidth(0, "50px");
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }

    private void enableButtons(boolean enabled)
    {
    	getView().getRelinquishButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
    	
    }
 
    private void updatePanelHeading(String text)
    {
        getView().getPanelHeader().setHeadingText(text);
    }

    public void refreshDataGrid(DataGrid<SharedContainerDTO> grid)
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void showContainerList(final SelectionModel<SharedContainerDTO> selectionModel,
    		final AbstractCellTable<SharedContainerDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText(glang.loading());
        grid.setEmptyTableWidget(messageLabel);


        // checkbox
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
        
        
        // container name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_NAME);
        final Column<SharedContainerDTO, String> nameCol = new Column<SharedContainerDTO, String>(nameCell)
        {

            @Override
            public String getValue(SharedContainerDTO dto)
            {
                return dto.getName();
            }
        };
        grid.addColumn(nameCol, glang.containerNameLabel());

        
         // owner fullname
        ObidosButtonCell ownerFullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<SharedContainerDTO, String> ownerFullnameCol = new Column<SharedContainerDTO, String>(ownerFullnameCell)
        {

            @Override
            public String getValue(SharedContainerDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(ownerFullnameCol, glang.sharedBy());
        
        // Add item if modify permission is given
        ObidosButtonCell addItemCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ADD);
        final Column<SharedContainerDTO, String> addItemCol = new Column<SharedContainerDTO, String>(addItemCell)
        {

            @Override
            public String getValue(SharedContainerDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(addItemCol, glang.addItem());
       
        
        // take ownership
        ObidosButtonCell takeOwnershipCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_TAKE_CONTAINER_OWNERSHIP);
        final Column<SharedContainerDTO, String> takeOwnershipCol = new Column<SharedContainerDTO, String>(takeOwnershipCell)
        {

            @Override
            public String getValue(SharedContainerDTO dto)
            {
                return glang.na();
            }
        };
        grid.addColumn(takeOwnershipCol, glang.takeOwnership());


        AsyncDataProvider<SharedContainerDTO> dataProvider = new AsyncDataProvider<SharedContainerDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<SharedContainerDTO> containerDTO)
            {
                final Range range = containerDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();

                GwtAsyncWrapper<SharedContainerResult> callback = new GwtAsyncWrapper<SharedContainerResult>(source)
                {
                    @Override
                    public void uponFailure(Throwable caught)
                    {
                    }

                    @Override
                    public void uponSuccess(SharedContainerResult result)
                    {
                        int numberOfContainers = result.getTotal();
                        if (numberOfContainers == 0)
                        {
                            messageLabel.setText(ObidosMessages.LANG.noContainersFound());
                            updateRowCount(0, true);
                            return;
                        }
                        List<SharedContainerDTO> containers = result.getElements();
                        if (containers != null && containers.size() > 0)
                        {
                            updateRowCount(numberOfContainers, true);
                            updateRowData(start, containers);
                        }
                        else
                        {
                            messageLabel.setText(ObidosMessages.LANG.noContainersFound());
                            updateRowCount(0, true);
                        }
                    }
                };
                String searchStr = getView().getSearchTextBox().getValue();
                String search = (searchStr.length() == 0) ? null : searchStr;
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                safeInvocationCall(() -> ContainerService.Utility.getInstance().getContainersSharedWithMe(authCreds,search,null, start,length,getOrderByList(),callback));
            }
        };

        // handler for listItems button
        nameCol.setFieldUpdater(new FieldUpdater<SharedContainerDTO, String>()
        {

            @Override
            public void update(int index, SharedContainerDTO containerDTO, String value)
            {
                sendToCorrectPlace(containerDTO, FieldNumber.nameCol);
            }
        });

        ownerFullnameCol.setFieldUpdater(new FieldUpdater<SharedContainerDTO, String>()
        {

            @Override
            public void update(int idx, SharedContainerDTO dto, String value)
            {
                sendToCorrectPlace(dto, FieldNumber.sharedByCol);
            }
        });

        addItemCol.setFieldUpdater(new FieldUpdater<SharedContainerDTO, String>()
		{

			@Override
			public void update(int index, SharedContainerDTO dto, String value)
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				String nameToken = NameTokens.PICK_ITEM_TYPE;
				Map<String,String> with = new HashMap<>();
				with.put(ObidosConstants.CONTAINER_ID, dto.getId().toString());
				ClientUtils.addType(placeManager, with);
				ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
				ClientUtils.showPage(placeManager, nameToken, with);
			}
		});

        takeOwnershipCol.setFieldUpdater(new FieldUpdater<SharedContainerDTO, String>()
        {

            @Override
            public void update(int idx, SharedContainerDTO dto, String value)
            {
            	gwtLog("XX take ownership of container: " + dto.getName() + " Id: " + dto.getId());
            	String title = glang.takeOwnershipOfContainer();
            	String message = glang.takeOwnershipOfContainerDialogMessage(dto.getOwnerFullname());
            	String containerName = ClientUtils.shortenString(dto.getName());

            	ClientUtils.promptForAction(() -> takeContainerOwnershipReal(dto.getId(),containerName), title, message);
            }
        });

        /*
        // handler for viewSystems button
        viewSystemsCol.setFieldUpdater(new FieldUpdater<ContainerDTO, String>()
        {

            @Override
            public void update(int index, ContainerDTO containerDTO, String value)
            {
                showListSystemsPage(containerDTO);
            }
        });
        */


        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    static class ShareContainerCell extends AbstractCell<String>
    {
        // must do that or onBrowserEvent does not fire
        // ref: // https://stackoverflow.com/questions/29672523/safehtmlcell-column-is-not-firing-the-handler-onbrowserevent/29680390
        public ShareContainerCell()
        {
            super("click");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb)
        {
            if (value == null)
            {
                return;
            }

            SharedContainerDTO containerDTO = (SharedContainerDTO) context.getKey();
            if (containerDTO == null)
            {
                return;
            }

            Boolean isPrivate = containerDTO.getIsPrivate();
            if (isPrivate == null) // just in case
            {
                isPrivate = Boolean.TRUE;
            }
            GWT.log("Container is private: " + isPrivate);
            if (isPrivate)
            {
                sb.appendHtmlConstant(
                        "<button type=\"button\" disabled='disabled' class=\"btn btn-default\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Private Containers can not be shared\"><i class=\"fa fa-user-secret\" aria-hidden=\"true\" style=\"color:green\"></i> <strike>Share</strike></button>");
            } else
            {
                sb.appendHtmlConstant(
                        "<button type=\"button\" class=\"btn btn-info\"><i class=\"fa fa-share-alt\"></i> Share</button>");
            }
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                ValueUpdater<String> valueUpdater)
        {

            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("click".equals(event.getType()))
            {
                EventTarget eventTarget = event.getEventTarget();
                if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget)))
                {
                    valueUpdater.update(value);
                }
            }
        }
    }

    static class SharedContainerCell extends AbstractCell<String>
    {

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb)
        {
            if (value == null)
            {
                return;
            }

            SharedContainerDTO containerDTO = (SharedContainerDTO) context.getKey();
            if (containerDTO == null)
            {
                return;
            }

            SafeHtml safeHtml = null;
            // Note: getShare() is a Boolean object, so never test
            // if (containerDTO.getShare()), it will throw
            // null pointer exception if the object is null
            Boolean shared = containerDTO.getShared();
            GWT.log("Shared: " + shared);
            boolean isShared = false;
            if (shared != null)
            {
                isShared = shared.booleanValue();
            }
            if (isShared)
            {

                 safeHtml = SafeHtmlUtils.fromTrustedString("<div><span><i class=\"fa fa-share-alt-square\" style=\"color:green\"></i>&nbsp;" + value + "</span></div>");
            }
            else
            {
                safeHtml = SafeHtmlUtils.fromTrustedString(value);
            }


            sb.append(safeHtml);
        }

    }

    private void sendToCorrectPlace(final SharedContainerDTO dto, FieldNumber fieldNumber)
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
                            ClientUtils.showRegisterPassphraseDialog(placeManager, glang.viewItems(),null);
                        }
                        break;
                    }
                    
                    case sharedByCol:
                    {
                    	if (rc)
                    	{
                    		ClientUtils.showUserInfo(placeManager, dto.getOwnerId(), getView().getMessageRow());
                    	}
                    	else
                    	{
                            ClientUtils.showRegisterPassphraseDialog(placeManager, glang.viewUserInfo(),null);
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
    private void showListItemsPage(SharedContainerDTO containerDTO)
    {
    	 SharedContainerDtoEvent.fire(ListContainersSharedWithMePresenter.this, containerDTO);
         PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER)
                .with(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString())
                .with(ObidosConstants.CONTAINER_TYPE,ObidosConstants.SHARED)
                .build();
		placeManager.revealPlace(placeRequest);

    }

    private void relinquishContainersReal()
    {
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage("No Containers Selected");
			return;
		}

    	GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearSelections();
				refreshDataGrid(getView().getDataGrid());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not relinquish container: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ContainerService.Utility.getInstance().delete(authCreds, ids, callback);
    }

	@Override
	public void searchContainerName()
	{
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void relinquishContainers()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		gwtLog("Registered: " + registered);
		if (registered)
		{
			promptRelinquishContainers();
		}
		else
		{
			String message = glang.relinquishContainer();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
		
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
		return ObidosConstants.CONTAINER_ID;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	private void promptRelinquishContainers()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.CONTAINER_ID_N);
		gwtLog("s: " + s);
		String title = glang.relinquishContainer();
		String message = glang.relinquishContainersWarning(ids.size(), s, s, s);
		ClientUtils.promptForAction(() -> relinquishContainersReal(), title, message);
	}
	
	private void takeContainerOwnershipReal(final Long containerId, String containerName)
	{

    	GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this) {

			@Override
			public void uponSuccess(Void result)
			{
				showMessage(glang.ownershipOfContainerTaken(containerName));
				refreshDataGrid(getView().getDataGrid());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(caught.getMessage());
			}
    	};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().takeOwnership(authCreds, containerId, null, callback);
	}
	
	private void setOrderBy(OrderBy orderBy)
	{
		sOrderBy = orderBy;
	}
	
	@Override
	public void sortByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortReverseByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_ASC);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortByAZ()
	{
		setOrderBy(OrderBy.CONTAINER_NAME_ASC);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.CONTAINER_NAME_DESC);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@Override
	public void languageListBoxCallback()
	{
		ListBox lb = getView().getLanguageListBox();
		String lang = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		gwtLog("index: "+ idx);
		gwtLog("Lang: " + lang);
		// only support English and Bangla for editing at this time
		switch(idx)
		{
			case 0: // English
			{
				ClientUtils.enableBanglaEditing(false, getView().getLanguageRow());
				break;
			}
			case 1: // Bangla
			{
				ClientUtils.enableBanglaEditing(true, getView().getLanguageRow());
				break;
			}
		}
	}

}
