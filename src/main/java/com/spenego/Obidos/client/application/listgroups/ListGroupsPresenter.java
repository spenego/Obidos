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

package com.spenego.Obidos.client.application.listgroups;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.shared.GWT;
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
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.GroupResult;

public class ListGroupsPresenter
        extends ObidosPresenter<GroupDTO, ListGroupsPresenter.MyView, ListGroupsPresenter.MyProxy, ListGroupsUiHandlers>
        implements ListGroupsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private enum FieldNumber
	{
		nameCol,
		addCol,
		editCol,
	};

    interface MyView extends View, HasUiHandlers<ListGroupsUiHandlers>
    {
        public DataGrid<GroupDTO> getDataGrid();
        public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public TextBox getSearchTextBox();
		public Button getSearchButton();
		public FormLabel getFormErrorLabel();
		public Button getDeleteButton();
		public Button getClearButton();
		public Button getHelpButton();
		public ObidosMessageRow getMessageRow();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

    @NameToken(NameTokens.LIST_GROUPS)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListGroupsPresenter>
    {
    }

    @Inject
    ListGroupsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showGroupsDatagrid(selectionModel, grid, this));
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
        DataGrid<GroupDTO> grid = getView().getDataGrid();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        // an experiment
        grid.setColumnWidth(0, "50px");
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }

    private void refreshDataGrid(DataGrid<GroupDTO> grid )
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void showGroupsDatagrid(final SelectionModel<GroupDTO> selectionModel,
    		final AbstractCellTable<GroupDTO> grid, 
    		HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	
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
        grid.addColumn(checkColumn,  lang.selectLabel());


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
        grid.addColumn(nameCol, lang.groupname());
       

        // Add users button
        ObidosButtonCell addCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ADD);
        final Column<GroupDTO, String> addUsersCol = new Column<GroupDTO, String>(addCell)
        {

            @Override
            public String getValue(GroupDTO object)
            {
                return lang.add();
            }
        };
        grid.addColumn(addUsersCol, lang.addUsers());

        // Edit group button
//        bCell = new ButtonCell(ButtonType.PRIMARY, IconType.EDIT);
        ObidosButtonCell editCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EDIT);
        final Column<GroupDTO, String> editCol = new Column<GroupDTO, String>(editCell)
        {

            @Override
            public String getValue(GroupDTO object)
            {
                return lang.edit();
            }
        };
        grid.addColumn(editCol, lang.editGroup());

        /*
        // List users button
        ObidosButtonCell listCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_USERS);
        final Column<GroupDTO, String> listUsersCol = new Column<GroupDTO, String>(listCell)
        {

            @Override
            public String getValue(GroupDTO object)
            {
                return lang.list();
            }
        };
        grid.addColumn(listUsersCol, lang.users());
        */


        AsyncDataProvider<GroupDTO> dataProvider = new AsyncDataProvider<GroupDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<GroupDTO> groupDTO)
            {
                final Range range = groupDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                GwtAsyncWrapper<GroupResult> callback = new GwtAsyncWrapper<GroupResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        showErrorMessage(glang.couldNotFetchGroups(e.getMessage()));
                    }

                    @Override
                    public void uponSuccess(GroupResult result)
                    {
                        int numberOfGroups = result.getTotalGroups();
                        GWT.log("Number of groups: " + numberOfGroups);
                        if (numberOfGroups == 0)
                        {
                            messageLabel.setText("No Groups found");
                            updateRowCount(0, true);
                            return;
                        }
                        List<GroupDTO> groups = result.getGroups();
                        if (groups != null && groups.size() > 0)
                        {
                            updateRowCount(numberOfGroups, true);
                            updateRowData(start, groups);
                        }
                        else
                        {
                            messageLabel.setText("No Groups found...");
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                String searchStr = getView().getSearchTextBox().getValue();
                String search = (searchStr.length() == 0) ? null : searchStr;
                ArrayList<Long> preSelectedGroups = getSelectedIds((MultiSelectionModel<GroupDTO>) selectionModel);
                safeInvocationCall(() -> UserService.Utility.getInstance().getGroups(authCreds, search, preSelectedGroups, start, length, getOrderByList(), callback));
            }
        };
        
        
        
        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);

        // handler for Add users button
        addUsersCol.setFieldUpdater(new FieldUpdater<GroupDTO, String>()
        {

            @Override
            public void update(int index, GroupDTO groupDTO, String value)
            {
                sendToCorrectPlace(groupDTO, FieldNumber.addCol);
            }

        });

        // handler for Edit group button
        editCol.setFieldUpdater(new FieldUpdater<GroupDTO, String>()
        {

            @Override
            public void update(int index, GroupDTO groupDTO, String value)
            {
                sendToCorrectPlace(groupDTO, FieldNumber.editCol);
            }
        });

        nameCol.setFieldUpdater(new FieldUpdater<GroupDTO, String>()
        {

            @Override
            public void update(int index, GroupDTO groupDTO, String value)
            {
                sendToCorrectPlace(groupDTO, FieldNumber.nameCol);
            }
        });
    }

    private void showListUsersInGroupPage(GroupDTO groupDTO)
    {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
        .nameToken(NameTokens.LIST_USERS_IN_GROUP)
        .with(ObidosConstants.GROUP_ID,groupDTO.getId().toString())
        .build();
		placeManager.revealPlace(placeRequest);
    }
    
    private String getGroupsString(ArrayList<Long> ids)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	if (ids == null)
    	{
    		return lang.groups();
    	}
    	String groups = "";
		if (ids.size() == 1)
		{
			groups = lang.group();
		}
		else if (ids.size() > 1)
		{
			groups = lang.groups();
		}
		return groups;
    }
    private void promptDeleteGroups()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String title = "";
		String groups = getGroupsString(ids);
		title = lang.deleteGroups(groups);
		String message = lang.deleteGroupsWarning(ids.size(), groups, groups);
		ClientUtils.promptForAction(() -> deleteGroupsReal(), title, message);
    }

    private void showAddUsersToGroupPage(GroupDTO groupDTO)
    {

        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.ADD_USERS_TO_GROUP)
                .with(ObidosConstants.ACTION,ObidosConstants.SHARE)
                .with(ObidosConstants.GROUP_ID,groupDTO.getId().toString())
                .build();
		placeManager.revealPlace(placeRequest);

    }

    private void showEditGroupPage(GroupDTO groupDTO)
    {
         PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.NEW_GROUP)
                .with(ObidosConstants.ACTION,ObidosConstants.EDIT)
                .with(ObidosConstants.GROUP_ID,groupDTO.getId().toString())
                .build();
		placeManager.revealPlace(placeRequest);

    }

    private void sendToCorrectPlace(GroupDTO groupDTO, FieldNumber fieldNumber)
    {
    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
    	ObidosMessages lang = ObidosMessages.LANG;
    	String message = "";
    	switch (fieldNumber)
		{
			case addCol:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}
				if (registered)
				{
					showAddUsersToGroupPage(groupDTO);
				}
				else
				{
					message = lang.addUsersToGroup();
					ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
				}
				break;
			}
			
			case editCol:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}
				if (registered)
				{
					showEditGroupPage(groupDTO);
				}
				else
				{
					message = lang.editGroup();
					ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
				}
				break;
			}
			
			case nameCol:
			{
				if (registered)
				{
					showListUsersInGroupPage(groupDTO);
				}
				else
				{
					message = lang.listUsersInGroup();
					ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
				}
				break;
			}

			default:
				break;
		}

    }

	@Override
	public void search()
	{
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected String getIdName()
	{
		return ObidosConstants.GROUP_ID;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	public void deleteGroups()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		if (registered)
		{
			promptDeleteGroups();
		}
		else
		{
			String message = ObidosMessages.LANG.deleteGroup();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
	}
	
	private void deleteGroupsReal()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage("No groups selected");
			return;
		}
		for (Long id : ids)
		{
			gwtLog("Delete group id: " + id);
		}
		
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				refreshDataGrid(getView().getDataGrid());
				clearSelections(); // Issue #518
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(ObidosMessages.LANG.couldNotDeleteGroups(getGroupsString(ids), caught.getMessage()));
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().deleteGroups(authCreds, ids, callback);

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
		setOrderBy(OrderBy.GROUP_NAME_ASC);
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.GROUP_NAME_DESC);
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

