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

package com.spenego.Obidos.client.application.revokecontainerfromgroups;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.shared.GWT;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.GroupResult;
//import com.spenego.Obidos.client.application.ObidosPresenter.RevokeView
import com.spenego.Obidos.client.application.ObidosPresenter.RevokeView;

public class RevokeContainerFromGroupsPresenter
		extends ObidosPresenter<GroupDTO, RevokeContainerFromGroupsPresenter.MyView, RevokeContainerFromGroupsPresenter.MyProxy, RevokeContainerFromGroupsUiHandlers>
		implements RevokeContainerFromGroupsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	interface MyView extends RevokeView<RevokeContainerFromGroupsUiHandlers, GroupDTO>
	{
		public BlockQuote getHelpBlockQuote();
		public Button getHelpButton();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public ToggleSwitch getSendNotificationEmailSwitch();
	}

	@Override
	protected String getIdName() {
		return ObidosConstants.CONTAINER_ID;
	}

	@NameToken(NameTokens.REVOKE_CONTAINER_FROM_GROUPS)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<RevokeContainerFromGroupsPresenter>
	{
	}

	@Inject
	RevokeContainerFromGroupsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			final PlaceManager placeManager, final CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		/*
		onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0),
				grid -> showGroupsSharingContainer(selectionModel, grid, this));
				*/
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
                   enableButtons(true);
               }
               else
               {
                   enableButtons(false);
                   GWT.log("Unselected");
               }
            }
        });
        showGroupsSharingContainer(selectionModel,grid, this);
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
		showMessage(null);
        enableButtons(false);
        updateContainerInfo();
        resetForm();

        refreshDataGrid();

        // Note: checkbox will not be cleared if the user clicks back button
        // if will be cleared if we move this call above refreshDataGrid
        // Issue #640 
        clearCheckBoxSelections();

		DataGrid<GroupDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
   		setColumnWidth(grid, 0, "55px");  // Checkbox column
        enableClearAndShareButtons();
	}
	
    private void enableClearAndShareButtons()
    {
    	if (getNumberOfGroupsSelected() > 0)
    	{
    		enableButtons(true);
    	}
    }

    private void enableButtons(boolean enabled)
    {
	   enableRevokeButton(enabled);
	   enableClearButton(enabled);
    }

	private int getNumberOfGroupsSelected()
	{
        Set<GroupDTO> selectedSet = selectionModel.getSelectedSet();
        int n = selectedSet.size();
        if (n > 0)
        {
        	showMessage("Number of groups selected: "+ n);
        }
        return n;
	}

    private void resetForm()
    {
        showMessage("");
        getView().getRevokeCommentTextBox().setValue("");
        getView().getSearchTextBox().setValue("");
        getView().getSendNotificationEmailSwitch().setValue(false);
    }


    private void enableRevokeButton(boolean enabled)
    {
        getView().getRevokeButton().setEnabled(enabled);
    }

    public void refreshDataGrid()
    {
        DataGrid<GroupDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void updateContainerInfo()
    {
        getView().getPanelHeader().setText(glang.revokeContainerFromGroups());
        Long containerId = getId();
        if (containerId == null) {
        	return;
        }


        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                gwtLog("Could not get Container " + caught.getMessage());
            }

            @Override
            public void uponSuccess(ContainerDTO dto)
            {
                getView().getShareContainerTextBox().setValue(dto.getName());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
    }

    @Override
    public void searchGroupsToRevoke()
    {
        refreshDataGrid();
    }

    private void showGroupsSharingContainer(final SelectionModel<GroupDTO> selectionModel,final AbstractCellTable<GroupDTO> grid, HasHandlers source)
    {
        gwtLog("In showUserList..");
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
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
                        GWT.log("Exception caught: " + caught.getMessage());
                        updateRowCount(0, true);
                        messageLabel.setText("");
                    }

                    @Override
                    public void uponSuccess(GroupResult result)
                    {
                        int numberOfGroups = result.getTotal();
                        GWT.log("Found number of grups: " + numberOfGroups);
                        List<GroupDTO> groups = result.getGroups();
                        if (groups != null && groups.size() > 0)
                        {
                            updateRowCount(numberOfGroups, true);
                            updateRowData(start, groups);
                        }
                        else
                        {
                            updateRowCount(0, true);
                            messageLabel.setText("Could not find any group to revoke");
                        }
                    }

                };
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                    searchString = null;
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                ArrayList<Long> preSelectedGroups = getSelectedIds((MultiSelectionModel<GroupDTO>) selectionModel);
                String search = getView().getSearchTextBox().getValue();
                if (search == null || search.length() == 0)
                {
                	search = null;
                }
               	UserService.Utility.getInstance().getGroupsForContainer(authCreds, getId(), search, SharedSetQuality.ONLY_SHARED_WITH, preSelectedGroups, start, length, toArray(OrderBy.CONTAINER_NAME_ASC), callback);
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

    private ArrayList<Long> getGroupIds(final Set<GroupDTO> s) {
    	final ArrayList<Long> list = new ArrayList<Long>(s.size());
    	for(final GroupDTO g : s) { list.add(g.getId()); }
    	return list;
    }

    @Override
    public void revokeContainer()
    {
    	promptRevoke();
    }

    private void promptRevoke()
    {
    	String title = glang.revokeContainerFromGroups();
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String groups = glang.group();
		if (ids.size() > 1)
		{
			groups = glang.groups();
		}
		int n = ids.size();

		String message = "You selected " + n + " " + groups + " to revoke the container from. " 
				+ "When revoked, the container and the items in the container will no longer be accessible to the " + groups + "."
				+ "<br/>" 
				+ "<b>Are you sure you want to rovoke sharing the container?</b>";
        ClientUtils.promptForAction(() -> revokeSharingReal(), title, message);
    }
    
    private void revokeSharingReal()
    {
   	    Set<GroupDTO> dtoSet = selectionModel.getSelectedSet();
        if (dtoSet.size() == 0)
        {
            return;
        }

        // works with SingleSelectionModel as well
        /*
        Set<GroupDTO> selectedDTOs = selectionModel.getSelectedSet();
        GWT.log("Number of rows selected: " + selectedDTOs.size());
        for (GroupDTO dto:selectedDTOs)
        {
            GWT.log("Selected: " + dto.getUsername() + ", " + dto.getFullname());
        }
        */

        Long containerId = getId();
        if (containerId == null)
        {
            return;
        }

        Set<GroupDTO> selectedDTOs = selectionModel.getSelectedSet();
        for (GroupDTO dto:selectedDTOs)
        {
            GWT.log(">> Selected: " + dto.getName());
        }

        String shareComment = getView().getRevokeCommentTextBox().getValue();
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not revoke Container: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(Void result)
            {
                Date date = new Date();
                showMessage("Container revoked from " + dtoSet.size() + " groups on " + ClientUtils.formattedDate(date));
                clearCheckBoxSelections();
                refreshDataGrid();
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
        ContainerService.Utility.getInstance().revokeContainerFromGroups(authCreds, containerId, getGroupIds(dtoSet), shareComment, postRevokeActions, callback);

    }

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);
	}

	private void enableClearButton(boolean enabled)
	{
		getView().getClearButton().setEnabled(enabled);
	}


	@Override
	public void help()
	{
		showHelp();
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
    private void showListUsersInGroupPage(GroupDTO groupDTO)
    {
    	ClientUtils.showUsersInGroupPage(placeManager, groupDTO.getId());
    }

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

}
