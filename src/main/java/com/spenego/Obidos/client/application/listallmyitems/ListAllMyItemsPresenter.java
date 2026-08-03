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

package com.spenego.Obidos.client.application.listallmyitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
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
import com.google.gwt.user.cellview.client.TextColumn;
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
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemsResult;
import com.spenego.Obidos.shared.dto.LimitedItemDTO;

public class ListAllMyItemsPresenter extends 
	ObidosPresenter<LimitedItemDTO, ListAllMyItemsPresenter.MyView, ListAllMyItemsPresenter.MyProxy, ListAllMyItemsUiHandlers>
        implements ListAllMyItemsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    private ArrayList<OrderBy> sOrderbyList = new ArrayList<>();

    interface MyView extends View, HasUiHandlers<ListAllMyItemsUiHandlers>
    {
    	public ObidosPanelHeader getPanelHeader();
        public DataGrid<LimitedItemDTO> getDataGrid();
        public SimplePager getPager();
        public TextBox getSearchTextBox();
        public BlockQuote getHelpBlockQuote();
        public Button getDeleteButton();
        public Button getClearButton();
        public ObidosMessageRow getMessageRow();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

    @NameToken(NameTokens.LIST_ALL_MY_ITEMS)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListAllMyItemsPresenter>
    {
    }

    @Inject
    ListAllMyItemsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);
        sOrderBy = OrderBy.UPDATE_TIME_DESC;

        getView().setUiHandlers(this);
    }

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showItemsDatagrid(selectionModel, grid, this));
    }

    private void enableButtons(boolean enabled)
    {
    	getView().getDeleteButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
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
        updatePanelHeading();
        resetForm();
        setDefaultSortOrder();
        DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
        setColumnWidth(grid, 0, "55px");  // Checkbox column
        setColumnWidth(grid, 1, "25%");  
        setColumnWidth(grid, 2, "25%");  
    }
    
    private void setDefaultSortOrder()
    {
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
    }

    private void updatePanelHeading()
    {
    	Boolean shared = showItemsSharedWithOthers();
    	Heading heading = getView().getPanelHeader().getHeading();
    	if (shared)
    	{
    		heading.setText(ObidosMessages.LANG.listMyItemsSharedWithOthers());
    	}
    	else
    	{
    		heading.setText(ObidosMessages.LANG.listMyItems());
    	}
    }

    private void resetForm()
    {
    	getView().getSearchTextBox().setValue("");
    }

    // TODO: This should be moved to ObidosPresenter
    private <T,C> void setColumnUpdater(Column<T, C> col, final Consumer<T> consumer, final Supplier<C> passphraseDialogMessage) {
        col.setFieldUpdater(new FieldUpdater<T, C>() {
            @Override
            public void update(int idx, T dto, C value) {
                if (ClientUtils.isPassphraseRegistered(currentUser)) {
                    gwtLog("Passphrase is cached");
                	consumer.accept(dto);
                } else {
                	ClientUtils.showRegisterPassphraseDialog(placeManager, passphraseDialogMessage.get().toString(), null);
                }
            }});
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
        
        // Select CheckBox
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

        // item name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_NAME);
        final Column<LimitedItemDTO, String> nameCol = new Column<LimitedItemDTO, String>(nameCell)
        {

            @Override
            public String getValue(LimitedItemDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, lang.itemNameLabel());

        // Bug # 117
        // Make the container name clickable. when clicked, list all the 
        // items in the container, otherwise it requires more steps to add
        // items to this container
        // spgdev, Apr-01-2025
        ObidosButtonCell containerNameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_CONTAINER_NAME_IN_LIST_ITEMS);
        final Column<LimitedItemDTO, String> listItemsCol = new Column<LimitedItemDTO, String>(containerNameCell)
   		{

			@Override
			public String getValue(LimitedItemDTO dto)
			{
				return dto.getContainerName();
			}
   		};
        grid.addColumn(listItemsCol, glang.containerName());
    	
        final Column<LimitedItemDTO,String> shareCol =    addObidosButtonCellColumn(grid, dto -> lang.shareButtonTitle(),  lang.shareButtonTitle(),  ObidosConstants.CELL_TYPE_SHARE);
        final Column<LimitedItemDTO,String> revokeCol =   addObidosButtonCellColumn(grid, dto -> lang.revokeButtonTitle(), lang.revokeButtonTitle(),     ObidosConstants.CELL_TYPE_REVOKE);
        final Column<LimitedItemDTO,String> editCol =     addObidosButtonCellColumn(grid, dto -> lang.editButtonTitle(),   lang.editButtonTitle(),       ObidosConstants.CELL_TYPE_EDIT);

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
       
        
        // click handlers
        setColumnUpdater(nameCol,  		dto -> showViewItemPage(dto),    					() -> lang.viewAnItem());
        setColumnUpdater(listItemsCol,	dto -> showItemsInContainerPage(dto),  				() -> lang.viewItems());
        setColumnUpdater(shareCol,    	dto -> showShareWithPage(dto),                     	() -> lang.shareAnItem());
        setColumnUpdater(revokeCol,   	dto -> showRevokeItemPage(dto),         		    () -> lang.revokeSharingAItemFromUsers());
        setColumnUpdater(editCol,     	dto -> showEditItemPage(dto, ObidosConstants.EDIT),	() -> lang.editAnItem());
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
                        showErrorMessage(glang.couldNotFetchItems(e.getMessage()));
                    }

                    @Override
                    public void uponSuccess(ItemsResult result)
                    {
                        int numberOfItems = result.getTotalItems();
                        gwtLog(">> >       Total: " + numberOfItems);
                        gwtLog(">>>  Range Start: " + start);
                        gwtLog(">>> Range length: " + range.getLength());
                        gwtLog("<<<hello again >>>");
                        if (numberOfItems == 0)
                        {
                            messageLabel.setText("No Items found");
                            updateRowCount(0, true);
                            return;
                        }
                        List<LimitedItemDTO> items = result.getElements();
                        if (items != null && items.size() > 0)
                        {
                            updateRowCount(numberOfItems, true);
                            updateRowData(start, items);
                        }
                        else
                        {
                            messageLabel.setText("No Groups found...");
                            updateRowCount(0, true);
                        }
                    }
                };
                String searchText = getView().getSearchTextBox().getValue();
                String search = (searchText != null && searchText.length() > 0) ? searchText : null;
                gwtLog(">>> Search item: " + search);

                boolean is_shared = showItemsSharedWithOthers();
                gwtLog("shared: " + is_shared);
                Boolean shared = is_shared ? true : null;
                Long containerId = null;
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<LimitedItemDTO>) selectionModel);
                ArrayList<OrderBy> orderByList = getOrderByList();
                for (OrderBy orderBy : orderByList) {
                	gwtLog("++ Orderby: " + orderBy);
				}
                safeInvocationCall(() -> ItemService.Utility.getInstance().getMyItems(authCreds, containerId, shared, search, preSelectedIds, start, length, orderByList, callback));
            }

        };
        
        // handler for shareCol
        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }
   /**
    * Indicate that this item is shared with someone
    */
    static class SharedWithOthersCell extends AbstractCell<String>
    {
        public SharedWithOthersCell()
        {
            super("click");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder safeHtmlBuilder)
        {
            if (value == null)
            {
                return;
            }

            LimitedItemDTO dto = (LimitedItemDTO) context.getKey();
            if (dto == null)
            {
                return;
            }
            // Warning: dto.getShared() is a Boolean object, don't compare
            // it as boolean otherwise null pointer exception will be thrown
            // if the object is null
            SafeHtml safeHtml = null;
            Boolean shared = dto.getShared();
            boolean isSharedWithSomeone = false;
            if (shared != null)
            {
                isSharedWithSomeone = shared.booleanValue();
            }
            if (isSharedWithSomeone)
            {
                String html = ClientUtils.getSharedItemSpan(dto, value);
                safeHtml = SafeHtmlUtils.fromTrustedString(html);

            }
            else
            {
            	String title = "<span title=\"" + value + "\"</span>";
                safeHtml = SafeHtmlUtils.fromTrustedString(title + value);
            }

            safeHtmlBuilder.append(safeHtml);

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
                    LimitedItemDTO dto = (LimitedItemDTO) context.getKey();
                    Boolean sharedWithOthers = dto.getShared();
                    // set updater only if the note is sharable
                    if (sharedWithOthers == null) // just in case
                    {
                        sharedWithOthers = Boolean.FALSE;
                    }
                    else
                    {
                        sharedWithOthers = Boolean.TRUE;
                    }

                    gwtLogStatic(">>>> Item is shared with others: " + sharedWithOthers);
                    if (sharedWithOthers == Boolean.TRUE)
                    {
                        valueUpdater.update(value);
                    }
                }
            }
        }
    }

        /**
     * Make a custom cell for "Share". We need to disable "Share" button if the note is not shareable
     *
     * @author spgdev@spenego.com - Nov-20-2017
     */
    static class ShareableCell extends AbstractCell<String>
    {
        // must do that or onBrowserEvent does not fire
        // ref: // https://stackoverflow.com/questions/29672523/safehtmlcell-column-is-not-firing-the-handler-onbrowserevent/29680390
        public ShareableCell()
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

            LimitedItemDTO dto = (LimitedItemDTO) context.getKey();
            if (dto == null)
            {
                return;
            }

            Boolean shareable = dto.getShareable();
            gwtLogStatic(">>>> Item is shareable: " + shareable);
            if (shareable == null) // just in case
            {
                shareable = Boolean.FALSE;
            }
            gwtLogStatic("Item is shareable: " + shareable);
            if (shareable == Boolean.FALSE)
            {
//                sb.appendHtmlConstant("<button type=\"button\" disabled='disabled' class=\"btn btn-default\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Private Notes can not be shared\"><i class=\"fa fa-user-secret\" aria-hidden=\"true\" style=\"color:green\"></i> <strike>Share</strike></button>");
            	String text = ObidosMessages.LANG.privateItem();
            	String tooltip = ObidosMessages.LANG.privateItemCannotBeShared();
            	String button = ClientUtils.makeDisabledText(text, tooltip);
            	sb.appendHtmlConstant(button);
            
            }
            else
            {
            	sb.appendHtmlConstant(ClientUtils.makeShareButton());
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
                    LimitedItemDTO dto = (LimitedItemDTO) context.getKey();
//                    Boolean shareable = dto.g
                    Boolean shareable = dto.getShareable();
                    gwtLogStatic(">>>>X Item is shareable: " + shareable);
                    // set updater only if the note is sharable
                    if (shareable == null) // just in case
                    {
                        shareable = Boolean.FALSE;
                    }
                    if (shareable == Boolean.TRUE)
                    {
                        valueUpdater.update(value);
                    }
                }
            }
        }
    }

    private static void gwtLogStatic(String message)
    {
        ClientUtils.gwtLog("ListItemsPresenter", message);
    }

    private void promptDeleteItems()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ITEM_ID_N);
		String title = lang.deleteNote();
		String message = lang.deleteSomethingWarning(ids.size(), s, s, s);

       ClientUtils.promptForAction(() -> deleteItemsReal(), title, message);
    }

    private void refreshDataGrid(DataGrid<LimitedItemDTO>grid)
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void showContainerPageWithItems(LimitedItemDTO dto)
    {
    	String nameToken = NameTokens.LIST_ITEMS;
        Map<String,String> with = new HashMap<>();
        String key = ObidosConstants.CONTAINER_ID;
        String value = dto.getId().toString();
        with.put(key, value);

        key = ObidosConstants.PLACE;
        with.put(ObidosConstants.PLACE, NameTokens.LIST_CONTAINERS);
        ClientUtils.showPage(placeManager, nameToken, with);
    }
   
    private void showViewItemPage(LimitedItemDTO dto)
    {
    	String nameToken = NameTokens.VIEW_ITEM;
        Map<String,String> with = new HashMap<>();
        String key = ObidosConstants.ITEM_ID;
        String value = dto.getId().toString();
        with.put(key, value);

        key = ObidosConstants.PLACE;
        value = NameTokens.LIST_ALL_MY_ITEMS;
        with.put(key, value);
        with.put(ObidosConstants.ACTION, ObidosConstants.VIEW);
        with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.ITEM);
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ALL_MY_ITEMS);
        
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);
    }
//    http://127.0.0.1:8888/index.html#LIST_ITEMS;containerid=389;place=LIST_CONTAINERS 
    private void showItemsInContainerPage(LimitedItemDTO dto)
    {
    	String nameToken = NameTokens.LIST_ITEMS;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, dto.getcontainerAssignmentId().toString());
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ALL_MY_ITEMS);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    private void showShareWithPage(LimitedItemDTO dto)
    {
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
    	String nameToken = NameTokens.SHARE_WITH;
        Map<String,String> with = new HashMap<>();

        String key = ObidosConstants.ITEM_ID;
        with.put(key, dto.getId().toString());
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ALL_MY_ITEMS);

        key = ObidosConstants.SHARE;
        String value = ObidosConstants.ITEM;
        with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.ITEM);
        with.put(key, value);
        
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);

    	
    	/*
        Map<String,String> with = new HashMap<>();
        String key = ObidosConstants.SHARE;
        String value = ObidosConstants.ITEM;
        with.put(key, value);
        key = ObidosConstants.ITEM_ID;
        value = dto.getId().toString();
        with.put(key, value);
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ALL_MY_ITEMS);
        ClientUtils.showPage(placeManager, NameTokens.SHARE_WITH, with);
        */
    }

    private void showEditItemPage(LimitedItemDTO dto, String actionValue)
    {
    	if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
    	{
    		return;
    	}

        gwtLog("Show edit item page");
        gwtLog("Item ID: " + dto.getId());

        String nameToken = NameTokens.EDIT_ITEM;
        Map<String,String> with = new HashMap<>();

        String key = ObidosConstants.ACTION;
        with.put(key, actionValue);

        key = ObidosConstants.ITEM_ID;
        with.put(key, dto.getId().toString());
        with.put(ObidosConstants.PLACE, NameTokens.LIST_ALL_MY_ITEMS);
        boolean shared = ClientUtils.fromBoolean(dto.getShared());
        if (shared)
        {
        	with.put(ObidosConstants.SHARED_WITH_OTHERS, ObidosMessages.LANG.yes());
        }
        
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);


        /*
        GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
        {

            @Override
            public void uponSuccess(ItemDTO result)
            {
                ArrayList<UserDefinedTypeValueDTO> typeValues = result.getValues();
                gwtLog("  TypeValues Size: " + typeValues.size());
                gwtLog("        Item name: " + result.getName());
                for (UserDefinedTypeValueDTO typeValue:typeValues)
                {
                    ArrayList<UserDefinedFieldValueDTO> fieldValues = typeValue.getFieldValues();
                    gwtLog(">> Field Values size: " + fieldValues.size());
                    gwtLog(">> is it a note?: " + typeValue.isNote());
                    for (UserDefinedFieldValueDTO fieldValue: fieldValues)
                    {
                        gwtLog("Field ID:   " + fieldValue.getId());
                        gwtLog("VALUE: " + fieldValue.getStringValue());
                        byte[] noteBytes = fieldValue.getBlobValue();
                        try
                        {
                            String note = new String(noteBytes, "UTF-8");
                            gwtLog("String: "+ note);
                        } catch (UnsupportedEncodingException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not get Item: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        byte[] passPhrase = null;
        ItemService.Utility.getInstance().get(authCreds, dto.getId(), passPhrase, callback);
        */
    }

    @Override
    public void searchItemName()
    {
        refreshDataGrid(getView().getDataGrid());
    }
    private void showListOfUsersTheItemIsSharedWith(LimitedItemDTO dto)
    {
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

    private boolean showItemsSharedWithOthers()
    {
        String type = ClientUtils.getTypeFromUrl(placeManager);
		if (type != null && type.equals(ObidosConstants.SHARED_WITH_OTHERS)) {
			return true;
		}
        return false;
    }

    private void showRevokeItemPage(LimitedItemDTO dto)
    {
    	if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
    	{
    		return;
    	}
    	// Issue #752
    	// we don't go to pick between user and grup anymore
    	// spgdev@spenego.com - Jan 16, 2021
		String nameToken = NameTokens.REVOKE_ITEM;
        Map<String,String> with = new HashMap<>();
		with.put(ObidosConstants.ITEM_ID,dto.getId().toString());
		with.put(ObidosConstants.ACTION,ObidosConstants.REVOKE_ITEM_SHARING);
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
		ClientUtils.addPlace(placeManager, with, NameTokens.LIST_ALL_MY_ITEMS);
		ClientUtils.showPage(placeManager, nameToken, with);

    }

	@Override
	public void help()
	{
		showHelp();
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
		return getView().getHelpBlockQuote();
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
				refreshDataGrid(getView().getDataGrid());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				String emsg = glang.couldNotDeleteItems() + ": " + caught.getMessage();
				showErrorMessage(emsg);
			}
		};

		safeInvocationCall(() -> ItemService.Utility.getInstance().delete(ClientUtils.getAuthCreds(), ids, callback));
	}

	@Override
	public void deleteItems()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		if (registered)
		{
			promptDeleteItems();
		}
		else
		{
			String message = ObidosMessages.LANG.deleteItem();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
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
	public void sortItemByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortItemReverseByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_ASC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortItemByAZ()
	{
		setOrderBy(OrderBy.ITEM_NAME_ASC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortItemByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortContainerByAZ()
	{
		setOrderBy(OrderBy.CONTAINER_NAME_ASC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		sOrderbyList.add(OrderBy.ITEM_NAME_ASC);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortContainerByZA()
	{
		setOrderBy(OrderBy.CONTAINER_NAME_DESC);
		sOrderbyList.clear();
		sOrderbyList.add(sOrderBy);
		sOrderbyList.add(OrderBy.ITEM_NAME_DESC);
		refreshDataGrid(getView().getDataGrid());
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
