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

package com.spenego.Obidos.client.application.revokeitemfromgroups;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemGroupsResult;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
//import com.spenego.Obidos.client.application.ObidosPresenter.RevokeView
import com.spenego.Obidos.client.application.ObidosPresenter.RevokeView;

public class RevokeItemFromGroupsPresenter
		extends ObidosPresenter<GroupDTO, RevokeItemFromGroupsPresenter.MyView, RevokeItemFromGroupsPresenter.MyProxy,RevokeItemFromGroupsUiHandlers>
		implements RevokeItemFromGroupsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	interface MyView extends RevokeView<RevokeItemFromGroupsUiHandlers,GroupDTO>
	{
		public TextBox getContainerNameTextBox();
		public TextBox getItemNameTextBox();
		public FormLabel getItemNameLabel();
		public Button getClearButton();
		public BlockQuote getHelpBlockQuote();
		public ObidosMessageRow getMessageRow();
		public ObidosRow getContainerRow();
		public ObidosPanelHeader getPanelHeader();
		public ToggleSwitch getSendNotificationEmailSwitch();
	}


	@NameToken(NameTokens.REVOKE_ITEM_FROM_GROUPS)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<RevokeItemFromGroupsPresenter>
	{
	}
	@Override
	protected String getIdName()
	{
		return ObidosConstants.ITEM_ID;
	}

	@Inject
	RevokeItemFromGroupsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			final PlaceManager placeManager, final CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showGroupsSharingItem(selectionModel, grid, this));
	}
	
	private void enableButtons(boolean enabled)
	{
		getView().getRevokeButton().setEnabled(enabled);
		getView().getClearButton().setEnabled(enabled);
	}
	

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		clearCheckBoxSelections();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage(null);
        getView().getSendNotificationEmailSwitch().setValue(false);
		enableUnshareButton(false);
		updateContainerNameInForm();
		enableButtons(false);
		updateTitles();
		fetchAndPopulateForm();
		refreshDataGrid();
		showMessage(null);
		DataGrid<GroupDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
       	ClientUtils.addWindowResizeHandler(grid);
   		setColumnWidth(grid, 0, "55px");  // Checkbox column

	}
	
	private void updateTitles()
	{
		ObidosPanelHeader panelHeader = getView().getPanelHeader();
		String action = ClientUtils.getActionFromUrl(placeManager);
		if (ObidosConstants.REVOKE_ITEM_SHARING.equals(action))
		{
			panelHeader.setText(glang.revokeItemSharedWithGroups());
			getView().getContainerRow().setVisible(true);
			getView().getItemNameLabel().setText(glang.itemName());
		}
		else if (ObidosConstants.REVOKE_NOTE_SHARING.equals(action))
		{
			panelHeader.setText(glang.revokeNoteSharedWithGroups());
			getView().getContainerRow().setVisible(false);
			getView().getItemNameLabel().setText(glang.noteName());
		}
	}
	
	public void refreshDataGrid()
	{
		gwtLog(">>>>>>>>>>>>>>>>>>> Refreshing grid... <<<<<<<<<<<<<<<<<<");
		DataGrid<GroupDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
		clearCheckBoxSelections();
	}

	private void fetchAndPopulateForm()
	{
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			return;
		}

		TextBox itemNameTextBox = getView().getItemNameTextBox();
		// setPanelHeader(ObidosMessages.LANG.viewtemPanelHeading());
		itemNameTextBox.setEnabled(false);
		// summernote.setEnabled(false);
		GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
		{

			@Override
			public void uponSuccess(ItemDTO result)
			{

				List<UserDefinedTypeValueDTO> typeValues = result.getValues();
				gwtLog("  TypeValues Size: " + typeValues.size());
				gwtLog("        Item name: " + result.getName());
				gwtLog("        >>> ItemDTO id: " + result.getId());

				Boolean b = result.getShareable();
				if (b != null)
				{
					boolean bValue = b.booleanValue();
				}

				Long templateId = result.getValues().get(0).getUserDefinedTypeId();
				gwtLog(" Template id: " + templateId);
				getView().getContainerNameTextBox().setValue(result.getContainerName());
				getView().getItemNameTextBox().setValue(result.getName());

			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get Item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().get(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
	}

	private void updateContainerNameInForm()
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
//			getView().getListButton().setText(ObidosMessages.LANG.listMyItems());
			return;
		} else
		{
//			getView().getListButton().setText(ObidosMessages.LANG.listItemsInContainer());
		}

		GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
		{

			@Override
			public void uponSuccess(ContainerDTO result)
			{
				getView().getContainerNameTextBox().setValue(result.getName());
				Boolean privateContainer = result.getIsPrivate();
				if (privateContainer != null && privateContainer.booleanValue() == true)
				{
				} else
				{
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
    private void showGroupsSharingItem(final SelectionModel<GroupDTO> selectionModel,final AbstractCellTable<GroupDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

            ObidosMessages lang = ObidosMessages.LANG;

            Code messageLabel = new Code();
            messageLabel.setText(lang.loading());
            grid.setEmptyTableWidget(messageLabel);

	    Column<GroupDTO,Boolean> checkColumn =
	            new Column<GroupDTO, Boolean>(new CheckboxCell(true,false))
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
        grid.addColumn(checkColumn, "Select");

   	    TextColumn<GroupDTO> usernameColumn = new TextColumn<GroupDTO>()
        {

            @Override
            public String getValue(GroupDTO dto)
            {
                if (dto != null)
                    return dto.getName();
                else
                    return "N/A";
            }
        };
        grid.addColumn(usernameColumn, "Groupname");


        /*
	    TextColumn<GroupDTO> fullnameColumn = new TextColumn<GroupDTO>()
        {

            @Override
            public String getValue(GroupDTO userDTO)
            {
                if (userDTO != null)
                    return userDTO.getFullname();
                else
                    return "N/A";
            }
        };
        grid.addColumn(fullnameColumn, "Full Name");
        */

        AsyncDataProvider<GroupDTO> dataProvider = new AsyncDataProvider<GroupDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<GroupDTO> dto)
            {
                final Range range = dto.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                GWT.log(" in AsyncDataProvider start: " + start);
                GWT.log("in AsyncDataProvider length: " + length);
                GwtAsyncWrapper<ItemGroupsResult> callback = new GwtAsyncWrapper<ItemGroupsResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable caught)
                    {
                        GWT.log("Exception caught: " + caught.getMessage());
                        updateRowCount(0, true);
                        showErrorMessage("ERROR: " + caught.getMessage());
                        messageLabel.setText("");
                    }

                    @Override
                    public void uponSuccess(ItemGroupsResult result)
                    {
                        int numberOfGroups = result.getTotal();
                        gwtLog("Found number of groups: " + numberOfGroups);
                        List<GroupDTO> groups = result.getElements();
                        if (groups != null && groups.size() > 0)
                        {
                            updateRowCount(numberOfGroups, true);
                            updateRowData(start, groups);
//                            showInfoMessgge("Found " + numberOfGroups + " groups sharing this Container");
                        }
                        else
                        {
                            messageLabel.setText("Could not find any more groups to revoke ...");
                            updateRowCount(0, true);
//                          messageLabel.setText("");
                        }
                    }

                };
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                    searchString = null;
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Long itemId = getId();
                String search = getView().getSearchTextBox().getValue();
                if (search == null || search.length() == 0)
                {
                	search = null;
                }
                gwtLog(">>>>>>>>>>>>>>> Item id: " + itemId);
                if (itemId == null)
                {
                	showErrorMessage("Could not get " + getIdName() + " from URL");
                	return;
                }
				// Broken Issue #225
				ArrayList<Long> preSelectedGroups = getSelectedIds((MultiSelectionModel<GroupDTO>) selectionModel);
				if (preSelectedGroups != null)
				{
					for (Long psgid : preSelectedGroups)
					{
						gwtLog(" >>>>>>>> Sending Preselected ID: " + psgid);
					}
				}

				ItemService.Utility.getInstance().getGroupsSharingItem(authCreds, itemId, search, preSelectedGroups, start, length, toArray(OrderBy.GROUP_NAME_ASC), callback);
            }
        };
        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }
    
    private void promptRevoke()
    {
    	String title = "Revoke Item";
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String action = ClientUtils.getActionFromUrl(placeManager);
		String itemOrNote = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ITEM_ID_N);
		if (ObidosConstants.REVOKE_NOTE_SHARING.equals(action))
		{
			title = glang.revokeNote();
			itemOrNote = ClientUtils.getSelectedTypeString(ids, ObidosConstants.NOTE_ID_N);
					
		}
		String isAre = glang.is();
		if (ids.size() > 1)
		{
			isAre = glang.are();
		}
		int n = ids.size();
		String usersGroups = glang.groups();
		String message = glang.revokeSomethingWarning(n, itemOrNote, 
				itemOrNote, usersGroups, itemOrNote, isAre, itemOrNote);
        ClientUtils.promptForAction(() -> revokeSharingReal(), title, message);
    }
    
    private void revokeSharingReal()
    {
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			showErrorMessage("Could not get itemd id from URL");
			return;
		}
				
				
		ArrayList<Long> groupIds = getSelectedIds(selectionModel);
		for (Long gid : groupIds)
		{
			gwtLog("Revoke id: " + gid);
		}
		
		String revokeComment = getView().getRevokeCommentTextBox().getValue();
		
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearCheckBoxSelections();
				refreshDataGrid();
//              Date date = new Date();
//              showMessage("Items revoked from " + groupIds.size() + " group on " + date.toString());
		                String action = ClientUtils.getActionFromUrl(placeManager);
                                String entity = "Note";
		                if (ObidosConstants.REVOKE_ITEM_SHARING.equals(action))
		                {
                                     entity = "Item";
		                }
				String itemName = getView().getItemNameTextBox().getValue();
                                showMessage(entity + " " + "<span style=\"color: purple;\">" + "'" + itemName + "'" + "</span>"
                                            + " revoked from " + "<span style=\"color: purple;\">" + groupIds.size() 
                                            + "</span>" + " group(s).");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not revoke item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        PostOpActions postRevokeActions = null;
        if (getView().getSendNotificationEmailSwitch().getValue())
        {
        	gwtLog("Send email about revoking..........");
        	postRevokeActions = new PostOpActions();
        	postRevokeActions.setPostOpActions(PostOpActions.SEND_EMAIL);
        }
        else
        {
        	gwtLog("Dont send mail about revoking..");
        }

		ItemService.Utility.getInstance().revokeFromGroups(authCreds, itemId, groupIds, revokeComment, postRevokeActions, callback);
    }

	@Override
	public void revokeSharingItemFromGroups()
	{
		promptRevoke();
		/*
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			showErrorMessage("Could not get itemd id from URL");
			return;
		}
				
				
		ArrayList<Long> groupIds = getSelectedIds(selectionModel);
		for (Long gid : groupIds)
		{
			gwtLog("Revoke id: " + gid);
		}
		
		String revokeComment = getView().getRevokeCommentTextBox().getValue();
		
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearCheckBoxSelections();
				refreshDataGrid();
                Date date = new Date();
                showMessage("Items revoked from " + groupIds.size() + " group on " + date.toString());

				showMessage("Item revoked");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not revoke item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().revokeFromGroups(authCreds, itemId, groupIds, revokeComment, callback);
	*/
	}

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
	}


	private void enableUnshareButton(boolean enabled)
	{
		getView().getRevokeButton().setEnabled(enabled);

	}
	
	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	public void back()
	{
		ClientUtils.back(placeManager);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

}
