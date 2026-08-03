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

package com.spenego.Obidos.client.application.listcontainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.ListBox;
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
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
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

public class ListContainersPresenter extends 
	ObidosPresenter<ContainerDTO, ListContainersPresenter.MyView, ListContainersPresenter.MyProxy, ListContainersUiHandlers>
        implements ListContainersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;

    interface MyView extends View, HasUiHandlers<ListContainersUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
    	public ObidosPanelHeader getPanelHeader();
        public TextBox getSearchTextBox();
        public Button getSearchButton();
        public SimplePager getPager();
        public DataGrid<ContainerDTO> getDataGrid();
        public Button getDeleteButton();
        public Button getClearButton();
        public ObidosMessageRow getMessageRow();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

    @NameToken(NameTokens.LIST_CONTAINERS)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListContainersPresenter>
    {
    }

    @Inject
    ListContainersPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);
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

    private native void initTooltips() /*-{
    	$wnd.jQuery("[data-toggle='tooltip']").tooltip();
	}-*/;

	private native boolean isInjected() /*-{
		return !(typeof $wnd.jQuery === "undefined") && !(null === $wnd.jQuery);
	}-*/;

    protected void onReset()
    {
        super.onReset();

        clearSelections();
        resetForm();
        updatePanelHeading();
        DataGrid<ContainerDTO> grid = getView().getDataGrid();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        
        // an experiment
        grid.setColumnWidth(0, "50px");
        grid.setColumnWidth(1, "35%");
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
		ClientUtils.focusToWidegt(getView().getSearchTextBox());
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
    	String type = ClientUtils.getTypeFromUrl(placeManager);

    	if (ObidosConstants.SHARED_WITH_OTHERS.equals(type))
    	{
    		getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.listMyContainersSharedWithOthers());
    	}
    	else
    	{
    		getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.myContainers());
    	}
    }

    private void resetForm()
    {
        showMessage("");
        getView().getSearchTextBox().setValue("");
    }

    public void refreshDataGrid(DataGrid<ContainerDTO> grid)
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);

    }

    /**
     * If Keypair is already cached in server side, go to share container view otherwise
     * show the page to register key pair
     */
    @Override
    public void showShareContainerPage(ContainerDTO containerDTO)
    {
    	gwtLog("XXX in showShareContainerPage...");
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		gotoShareContainerPage(containerDTO);
    }

    private void gotoKeypairRegisterPage(ContainerDTO containerDTO)
    {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.REGISTER_PASSPHRASE)
                .with(ObidosConstants.ACTION,ObidosConstants.SHARE)
                .with(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString())
                .build();

        placeManager.revealPlace(placeRequest);
    }

    private void gotoShareContainerPage(ContainerDTO containerDTO)
    {
        String nameToken = NameTokens.SHARE_CONTAINER;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.ACTION,ObidosConstants.SHARE);
        with.put(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString());
        with.put(ObidosConstants.SHARE, ObidosConstants.CONTAINER);
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
        ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
        
        ClientUtils.showPage(placeManager, nameToken, with);
    }

        @Override
        public void help()
        {
                ClientUtils.showHelp(getView().getHelpBlockQuote());
        }

    private void showListItemsPage(ContainerDTO containerDTO)
    {
        Long ownerId = containerDTO.getUserId();
    	String nameToken = NameTokens.LIST_ITEMS;
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
        with.put(ObidosConstants.PLACE, NameTokens.LIST_CONTAINERS);
        if (ownerId != null)
        {
            with.put(ObidosConstants.OWNERID,ownerId.toString());
        }
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    @Override
    public void showAddSystemPage(ContainerDTO containerDTO, boolean firstSystem)
    {
        PlaceRequest placeRequest = null;
        if (firstSystem)
        {
            placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.NEW_SYSTEM)
                .with(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString())
                .with(ObidosConstants.ACTION, ObidosConstants.CREATE_SYSTEM)
                .build();
        }
        else
        {
             placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.NEW_SYSTEM)
                .with(ObidosConstants.CONTAINER_ID,containerDTO.getId().toString())
                .build();
        }
		placeManager.revealPlace(placeRequest);
    }

    // TODO: This should be moved to ObidosPresenter
    private void setColumnUpdater(Column<ContainerDTO, String> col, final Consumer<ContainerDTO> consumer, final Supplier<String> passphraseDialogMessage) {
        col.setFieldUpdater(new FieldUpdater<ContainerDTO, String>() {
            @Override
            public void update(int idx, ContainerDTO dto, String value) {
                if (currentUser == null)
                {
                    gwtLog("Ooops currentUser is null, it can't be!");
                }
                
                if (ClientUtils.isPassphraseRegistered(currentUser)) {
                    gwtLog("Passphrase is registered");
                	consumer.accept(dto);
                } else {
                	ClientUtils.showRegisterPassphraseDialog(placeManager, passphraseDialogMessage.get(), null);
                }
            }});
    }

    private void showContainerList(final SelectionModel<ContainerDTO> selectionModel,
    		final AbstractCellTable<ContainerDTO> grid, HasHandlers source)
    {
        gwtLog("LLL ZZZ in showContainerList Page xxxx");
    	ObidosMessages lang = ObidosMessages.LANG;
    	
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText(lang.loading());
        grid.setEmptyTableWidget(messageLabel);

        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

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
        grid.addColumn(nameCol, lang.containerNameLabel());
        
        ObidosButtonCell shareCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_SHARE, currentUser);
        final Column<ContainerDTO, String> shareCol = new Column<ContainerDTO, String>(shareCell)
        {

            @Override
            public String getValue(ContainerDTO object)
            {
                return lang.share();
            }
        };
        grid.addColumn(shareCol, lang.shareButtonTitle());


//        Column<ContainerDTO, String> shareCol     = addObidosButtonCellColumn(grid, dto -> lang.shareButtonTitle(),  lang.shareContainer(), ObidosConstants.CELL_TYPE_SHARE);
        Column<ContainerDTO, String> revokeCol    = addObidosButtonCellColumn(grid, dto -> lang.revokeButtonTitle(), lang.revokeButtonTitle(),         ObidosConstants.CELL_TYPE_REVOKE);
        Column<ContainerDTO, String> addItemCol   = addObidosButtonCellColumn(grid, dto -> lang.addButtonTitle(),    lang.addItem(),        ObidosConstants.CELL_TYPE_ADD);
        Column<ContainerDTO, String> editCol      = addObidosButtonCellColumn(grid, dto -> lang.editButtonTitle(),   lang.editButtonTitle(),  ObidosConstants.CELL_TYPE_EDIT);
        
        ObidosButtonCell listUsersCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_SHARED_WITH_USERS_GROUPS);
        final Column<ContainerDTO, String> listUsersCol = new Column<ContainerDTO, String>(listUsersCell)
        {

            @Override
            public String getValue(ContainerDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(listUsersCol, lang.sharedWith());


        setColumnUpdater(nameCol,     dto -> showListItemsPage(dto),                       () -> lang.viewItems()); // handler for viewSystems button
        setColumnUpdater(shareCol,    dto -> showShareContainerPage(dto),                  () -> lang.shareAContainer());    // handler for Share button
        setColumnUpdater(revokeCol,   dto -> showPickRevokeUsersGroupsPage(dto),           () -> lang.revokeSharingAContainerFromUsers());   // handler for Revoke button
        setColumnUpdater(addItemCol,  dto -> navigateToPickItemTypeView(dto),              () -> lang.addItemToAContainer());  // handler for add item button
        setColumnUpdater(editCol,     dto -> showEditContainerPage(dto),                   () -> lang.editAContainer());     // handler for edit button
        setColumnUpdater(listUsersCol, dto -> showListOfUsersTheContainerIsSharedWith(dto), () -> lang.sharedWith());     // handler for name column

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
                        showErrorMessage(glang.couldNotFetchContainers(caught.getMessage()));
                    }

                    @Override
                    public void uponSuccess(ContainerResult result)
                    {
                        int numberOfContainers = result.getTotalContainers();
                        if (numberOfContainers == 0)
                        {
                            messageLabel.setText("No Containers found");
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
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Long userId = currentUser.getUserDTO().getId();
                gwtLog("User id got from CurrentUser: " + userId);
                String type = ClientUtils.getTypeFromUrl(placeManager);
                boolean shared = ObidosConstants.SHARED_WITH_OTHERS.equals(type);

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

                ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<ContainerDTO>) selectionModel);
                safeInvocationCall(() -> ContainerService.Utility.getInstance().getMyContainers(authCreds, search, preSelectedIds, shared ? true : null, null, start, length, getOrderByList(), callback));
            }
        };


        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    private void promptDeleteContainer(ContainerDTO containerDTO)
    {
    	String message = ObidosMessages.LANG.deleteContainerWarning();
        message = message + ObidosMessages.LANG.deleteContainer() + ":" + containerDTO.getName() + ObidosMessages.LANG.questionMark();
        String title = ObidosMessages.LANG.deleteAContainer();
    	
		ClientUtils.promptForAction(() -> deleteContainer(containerDTO), title, message);
    }


    private void deleteContainer(ContainerDTO dto)
    {
    	if (dto == null) {
    		return;
    	}
    	/*
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String errorMessage = "Could not delete Container: " + caught.getMessage();
                showErrorMessageInSpan(errorMessage);
            }

            @Override
            public void uponSuccess(Void arg0)
            {
//                showListContainerPage();
                refreshDataGrid();
            }
        };
        */
        //AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
//        ContainerService.Utility.getInstance().delete(authCreds,dto.getId(),callback);

    }

    public void showEditContainerPage(ContainerDTO containerDTO)
    {
    	if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

        gwtLog("Container ID: " + containerDTO.getId());
        String nameToken = NameTokens.NEW_CONTAINER;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerDTO.getId().toString());
        ClientUtils.addType(placeManager, with);
        ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    private void navigateToPickItemTypeView(ContainerDTO containerDTO)
    {
    	if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
    	Long ownerId = containerDTO.getUserId();

    	String nameToken = NameTokens.PICK_ITEM_TYPE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerDTO.getId().toString());
        if (ownerId != null)
        {
            with.put(ObidosConstants.OWNERID, ownerId.toString());
        }
        ClientUtils.addType(placeManager, with);
        ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    private void showErrorMessageInSpan(String errorMessage)
    {
        showErrorMessage(errorMessage);
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
    public void searchContainerName()
    {
		refreshDataGrid(getView().getDataGrid());
    }

    private void showPickRevokeUsersGroupsPage(ContainerDTO dto)
    {
		String nameToken = NameTokens.REVOKE_CONTAINER;
    	gwtLog("XXX show revode page: " + nameToken);
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID,dto.getId().toString());
		with.put(ObidosConstants.ACTION,ObidosConstants.REVOKE_CONTAINER_SHARING);
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
        ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

	private void showRevokeContainerFromUsersView(ContainerDTO dto)
	{
		gwtLog("Show revoke Container from Users view");
        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.REVOKE_CONTAINER_FROM_USERS)
                .with(ObidosConstants.CONTAINER_ID,dto.getId().toString())
                .build();
		placeManager.revealPlace(placeRequest);

	}
	private void showRevokeContainerFromGroupsView(ContainerDTO dto)
	{
		gwtLog("Show revoke Container from Groups view");
        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.REVOKE_CONTAINER_FROM_GROUPS)
                .with(ObidosConstants.CONTAINER_ID,dto.getId().toString())
                .build();
		placeManager.revealPlace(placeRequest);

	}

	private void showListOfUsersTheContainerIsSharedWith(ContainerDTO dto)
	{
        String nameToken = NameTokens.LIST_USERS_CONTAINER_IS_SHARED_WITH;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, dto.getId().toString());
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	gwtLog("Add type: " + type);
        	with.put(ObidosConstants.TYPE, type);
        }
        ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
        ClientUtils.showPage(placeManager, nameToken, with);
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
    private void enableButtons(boolean enabled)
    {
    	getView().getDeleteButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
    	
    }
    
    private String getContainersString(ArrayList<Long> ids)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	if (ids == null)
    	{
    		return lang.containers();
    	}
    	String s = "";
		if (ids.size() == 1)
		{
			s = lang.container();
		}
		else if (ids.size() > 1)
		{
			s = lang.containers();
		}
		return s;
    }


	@Override
	public void deleteContainers()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		gwtLog("Registered: " + registered);
		if (registered)
		{
			promptDeleteContainers();
		}
		else
		{
			String message = ObidosMessages.LANG.deleteContainer();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
	}
	
	private void deleteContainersReal()
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
				showErrorMessage(ObidosMessages.LANG.couldNotDelete(getContainersString(ids), caught.getMessage()));
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().delete(authCreds, ids, callback);

	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
	}

	private void promptDeleteContainers()
	{
    	ObidosMessages lang = ObidosMessages.LANG;
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = getContainersString(ids);
		gwtLog("s: " + s);
		String title = lang.deleteX(s);
		String message = lang.deleteContainersWarning(ids.size(), s, s, s);
		ClientUtils.promptForAction(() -> deleteContainersReal(), title, message);
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
