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

package com.spenego.Obidos.client.application.sharewithgroups;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.GroupResult;
import com.spenego.Obidos.shared.dto.ItemDTO;
// import com.spenego.Obidos.client.application.ObidosPresenter.ItemShareView;
import com.spenego.Obidos.client.application.ObidosPresenter.ItemShareView;

public class ShareWithGroupsPresenter
        extends ObidosPresenter<GroupDTO, ShareWithGroupsPresenter.MyView, ShareWithGroupsPresenter.MyProxy, ShareWithGroupsUiHandlers>
        implements ShareWithGroupsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    interface MyView extends ItemShareView<ShareWithGroupsUiHandlers, GroupDTO>
    {
    	public BlockQuote getHelpBlockQuote();
		public Button	getShareButton();
		public TextBox	getSearchTextBox();
		public TextBox getShareCommentTextBox();
		public Button getClearButton();
		public Button getListButton();
		public FormLabel getNameLabel();
		public ObidosButtonToolBar getButtonToolBar();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public ToggleSwitch getSendNotificationEmailSwitch();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.ITEM_ID;
	}

    @NameToken(NameTokens.SHARE_WITH_GROUPS)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ShareWithGroupsPresenter>
    {
    }

    @Inject
    ShareWithGroupsPresenter(EventBus eventBus, MyView view, MyProxy proxy, 
    		final PlaceManager placeManager,
    		final CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        super.onBind();
        DataGrid<GroupDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<GroupDTO> createCheckboxManager());

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
               Set<GroupDTO> dto = selectionModel.getSelectedSet();
               if (dto.size() > 0)
               {
                   showMessage("");
                   enableButtons(true);
               }
               else
               {
                   enableButtons(false);
               }
            }
        });
        showGroupList(selectionModel,grid, this);
		selectCheckBoxByClickingOnTheRow(selectionModel, grid);
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
        getView().getButtonToolBar().adjustButtonsWidth();
        getView().getSendNotificationEmailSwitch().setValue(true);
        enableButtons(false);
        clearCheckBoxSelections();
        showMessage(null);
        fetchAndPopulateForm();
        updateTitles();
        refreshDataGrid();
        DataGrid<GroupDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        setColumnWidth(grid, 0, "55px");  // Checkbox column
    }
    
    private void updateTitles()
    {
    	String share = ClientUtils.getShareFromUrl(placeManager);
    	String shareType = ClientUtils.getShareTypeFromUrl(placeManager);
    	
    	if (ObidosConstants.ITEM.equals(share) && ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
    	{
    		getView().getListButton().setText(glang.items());
    		getView().getPanelHeader().setHeadingText(glang.shareItemWithGroups());
    		getView().getNameLabel().setText(glang.itemName());
    	}
    	else if (ObidosConstants.NOTE.equals(share) && ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
    	{
    		getView().getListButton().setText(glang.notes());
    		getView().getPanelHeader().setHeadingText(glang.shareNoteWithGroups());
    		getView().getNameLabel().setText(glang.noteName());
    	}
    }
    
    private void fetchAndPopulateForm()
    {
    	Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
    	if (itemId == null)
    	{
    		showErrorMessage(ObidosMessages.LANG.couldNotGetItemId());
    		return;
    	}
    	GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
		{

			@Override
			public void uponSuccess(ItemDTO dto)
			{
				getView().getNameTextBox().setValue(dto.getName());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch Item");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().get(authCreds, itemId, toArray(OrderBy.ITEM_NAME_ASC), callback);
    }

    private void showGroupList(final SelectionModel<GroupDTO> selectionModel,final AbstractCellTable<GroupDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
	    grid.setEmptyTableWidget(messageLabel);

	    // checkbox
	    Column<GroupDTO,Boolean> checkColumn = new Column<GroupDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(GroupDTO dto)
            {
            	if (dto != null && dto.getSelected() == null)
            	{
            		dto.setSelected(false);
            	}
            	setSearchedCheckbox(selectionModel, dto);
                return selectionModel.isSelected(dto);
            }
        };
        grid.addColumn(checkColumn, ObidosMessages.LANG.selectLabel());

        // Group name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_GROUP);
        final Column<GroupDTO, String> nameCol = new Column<GroupDTO, String>(nameCell)
        {

            @Override
            public String getValue(GroupDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, glang.groupname());

        AsyncDataProvider<GroupDTO> dataProvider = new AsyncDataProvider<GroupDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<GroupDTO> dto)
            {
                final Range range = dto.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                gwtLog(" in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);
                GwtAsyncWrapper<GroupResult> callback = new GwtAsyncWrapper<GroupResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable caught)
                    {
                        gwtLog("Exception caught: " + caught.getMessage());
                        updateRowCount(0, true);
                        messageLabel.setText("");
                    }

                    @Override
                    public void uponSuccess(GroupResult result)
                    {
                        int numberOfGroups = result.getTotalGroups();
                        gwtLog("Found number of groups: " + numberOfGroups);
                        List<GroupDTO> groups = result.getGroups();
                        if (groups != null && groups.size() > 0)
                        {
                            updateRowCount(numberOfGroups, true);
                            updateRowData(start, groups);
                        }
                        else
                        {
                            updateRowCount(0, true);
                            messageLabel.setText("");
                        }
                    }

                };
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                    gwtLog("Search string empty, search all");
                    searchString = null;
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                ArrayList<Long> preSelectedGroups = getSelectedIds((MultiSelectionModel<GroupDTO>) selectionModel);
                UserService.Utility.getInstance().getGroupsForItem(authCreds,getId(), searchString,
                		SharedSetQuality.NOT_SHARED_WITH,
                		preSelectedGroups,
                		start,length,getOrderByList(),callback);
            }

        };
        
        nameCol.setFieldUpdater(new FieldUpdater<GroupDTO, String>()
        {
            @Override
            public void update(int index, GroupDTO groupDTO, String value)
            {
				showListUsersInGroupPage(groupDTO);
            }
        });

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    private void enableButtons(boolean enabled)
    {
    	getView().getShareButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
    }
    
    
    private void setShareButtonTitle(String title)
    {
        // don't use setTitle()
        getView().getShareButton().setText(title);

    }

    private void showInfoMessgge(String message)
    {
    	showMessage(message);
    }

    public void refreshDataGrid()
    {
        DataGrid<GroupDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

//    @Override
//    public void listItems()
//    {
//    	ClientUtils.navigateToPlace(placeManager);
//    }

    @Override
    public void shareItem()
    {
        Set<GroupDTO> dtoSet = selectionModel.getSelectedSet();
        if (dtoSet.size() == 0)
        {
        	showErrorMessage("No users selected..");
        	return;
        }

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                Date date = new Date();
                int sz = dtoSet.size();
                String itemName = getView().getNameTextBox().getValue();
                if (sz > 1)
                {
//                	showMessage("Item shared with " + dtoSet.size() + " groups on " + date.toString());
                	showMessage(ObidosMessages.LANG.item() + " " + "<span style=\"color: purple;\">" +
                                    "'" +itemName + "'"+ "</span>" + " shared with " +
                                    "<span style=\"color: purple;\">" + dtoSet.size() + "</span>" + " groups.");
                }
                else
                {
//                	showMessage("Item shared with " + dtoSet.size() + " group on " + date.toString());
                        String groupname = dtoSet.iterator().next().getName();
                	showMessage(ObidosMessages.LANG.item() + " " + "<span style=\"color: purple;\">" +
                                    "'" +itemName + "'"+ "</span>" + " shared with " +
                                    "<span style=\"color: purple;\">" + "'" + groupname + "'" + "</span>" + ".");
                }
                // must clear checkBox selections or they will accumulate
                clearCheckBoxSelections();
                refreshDataGrid();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not share item: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        String shareComment = getView().getShareCommentTextBox().getValue();
        PostOpActions postShareActions = null;
        if (getView().getSendNotificationEmailSwitch().getValue())
        {
        	gwtLog("Send email about sharing..........");
        	postShareActions = new PostOpActions();
        	postShareActions.setPostOpActions(PostOpActions.SEND_EMAIL);
        }
        else
        {
        	gwtLog("Dont send mail about sharing..");
        }

        ItemService.Utility.getInstance().shareItemWithGroups(authCreds, getId(), getIdsFromDTOSet(dtoSet), shareComment, postShareActions, callback);
    }

    @Override
    public void help()
    {
    	showHelp();
    }

    @Override
    public void searchGroups()
    {
        refreshDataGrid();
    }

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}
	
    @Override
    public void showListItemsPage()
    {
        String nameToken = NameTokens.LIST_ITEMS;
        ClientUtils.navigateToPlace(placeManager, nameToken);
    }

    private void showListUsersInGroupPage(GroupDTO groupDTO)
    {
    	ClientUtils.showUsersInGroupPage(placeManager, groupDTO.getId());
    }

	@Override
	public void back()
	{
			ClientUtils.goBack(placeManager);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
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
		setOrderBy(OrderBy.GROUP_NAME_ASC);
		refreshDataGrid();
		
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.GROUP_NAME_DESC);
		refreshDataGrid();
	}
}

