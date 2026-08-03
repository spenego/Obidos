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

package com.spenego.Obidos.client.application.advancedusersearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class AdvancedUserSearchPresenter
        extends ObidosPresenter<LimitedUserForAdminDTO, AdvancedUserSearchPresenter.MyView, AdvancedUserSearchPresenter.MyProxy, AdvancedUserSearchUiHandlers>
        implements AdvancedUserSearchUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
    interface MyView extends View, HasUiHandlers<AdvancedUserSearchUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public DataGrid<LimitedUserForAdminDTO> getDataGrid();
        public SimplePager getPager();
        public TextBox getUserNameTextBox();
        public TextBox getFullNameTextBox();
        public TextBox getEmailTextBox();
        public FormLabel getFormErrorLabel();
        public ToggleSwitch getAdminSwitch();
        public ToggleSwitch getLockedUserSwitch();
        public ToggleSwitch getDeletedUserSwitch();
        public ObidosButtonToolBar getBottomToolBar();
        public ObidosMessageRow getMessageRow();
        public FormLabel getSearchFilterLabel();
        public ObidosPanelHeader getPanelHeader();
    }

    @NameToken(NameTokens.ADVANCED_USER_SEARCH)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<AdvancedUserSearchPresenter>
    {
    }

    @Inject
    AdvancedUserSearchPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<LimitedUserForAdminDTO> createCheckboxManager());

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
               Set<LimitedUserForAdminDTO> dto = selectionModel.getSelectedSet();
               if (dto.size() > 0)
               {
            	   enableButtons(true);
               }
               else
               {
            	   enableButtons(false);
               }
            }
        });
        showUserList(selectionModel, grid, this);
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
        if (!ClientUtils.isAdmin(currentUser))
        {
            gwtLog("ListUsersPresenter onReset() returning user is not admin");
            return;
        }
        showMessage("");
        ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
        getView().getBottomToolBar().adjustButtonsWidth();
        clearSelections();
        refreshDataGrid();
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
    }
    
    private void enableButtons(boolean enabled)
    {
    }

    @Override
    public void searchUsers()
    {
        refreshDataGrid();
    }
    
    private void showUserList(final SelectionModel<LimitedUserForAdminDTO> selectionModel,
    		final AbstractCellTable<LimitedUserForAdminDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);
	    
	    // Username
//    	addCellColumn(grid, dto -> dto.getUsername(), glang.username(), new ColorCell());	    // Use a custom cell so that we can highlight the cell if the user is blocked
    	addCellColumn(grid, dto -> dto.getUsername(), glang.username(), new TextCell());


    	// Fullname
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserForAdminDTO, String> nameCol = new Column<LimitedUserForAdminDTO, String>(nameCell)
        {

            @Override
            public String getValue(LimitedUserForAdminDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, glang.fullname());


        // Email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<LimitedUserForAdminDTO, String> emailCol = new Column<LimitedUserForAdminDTO, String>(emailCell)
        {

            @Override
            public String getValue(LimitedUserForAdminDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(emailCol, glang.email());

        AsyncDataProvider<LimitedUserForAdminDTO> dataProvider = new AsyncDataProvider<LimitedUserForAdminDTO>()
        {

           @Override
            protected void onRangeChanged(HasData<LimitedUserForAdminDTO> userDTO)
            {
                final Range range = userDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                gwtLog(" in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);
                GwtAsyncWrapper<LimitedUserForAdminResult> callback = new GwtAsyncWrapper<LimitedUserForAdminResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        String message = e.getMessage();
                        Window.alert("Exception received: " + e.getMessage());
                        updateRowCount(0, true);
                        return;
                    }

                    @Override
                    public void uponSuccess(LimitedUserForAdminResult userResult)
                    {
                        int numberOfUsers = userResult.getTotalUsers();
                        List<LimitedUserForAdminDTO> users = userResult.getUsers();
                        if (users != null)
                            gwtLog(">>> returned " + users.size());
                        else
                        {
                        	gwtLog("Count: " + numberOfUsers);
                        	gwtLog("XXXXXXXXXXXXXXXXXXXXXXXXX returned empty list");
//                            messageLabel.setWidth("20000px");
//                            messageLabel.setHeight("2000px");
                        }

                        if (users != null && users.size() > 0)
                        {
                        	gwtLog("UUUUUUUUUUUUUUUUUUUUUUUUUUU to: " + users.size() + " number: " + numberOfUsers);
                            updateRowCount(numberOfUsers, true);
                            updateRowData(start, users);
                        }
                        else
                        {
                            messageLabel.setText("No users found ...");
                            updateRowCount(0, true);
                        }
//                        if (!ClientUtils.isAdmin())
                        if (!ClientUtils.isAdmin(currentUser))
                        {
                            messageLabel.setText("Only admins can search users");
                            updateRowCount(0, true);
                        }
                    }
                };
//                if (!ClientUtils.isAdmin()) // a stupid hack for Bug #144
                if (!ClientUtils.isAdmin(currentUser)) // a stupid hack for Bug #144
                {
                    gwtLog(" Stupid hack... User is not admin, will not make RPC Call...");
                    return;
                }
                if (placeManager == null)
                {
                    messageLabel.setText("Fatal Error, No Place Manager found ...");
                    return;
                }

                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                UserDTO dto = getUserDTO();
                ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<LimitedUserForAdminDTO>) selectionModel);
                safeInvocationCall(() -> UserService.Utility.getInstance().getUsers(authCreds, dto, preSelectedIds, start, length, toArray(OrderBy.USERNAME_ASC), callback));
            } // end onRangeChanged()
        };

        nameCol.setFieldUpdater(new FieldUpdater<LimitedUserForAdminDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserForAdminDTO dto, String value)
            {
        		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
            }

        });

        // noop but the handler must exist
        emailCol.setFieldUpdater(new FieldUpdater<LimitedUserForAdminDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserForAdminDTO dto, String value)
            {
            }
        });

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }
    /**
     * Super simple way to color a cell in DataGrid. I think GWT's example is a complex garbage and very hard
     * to understand.
     * This cell is used to color the bg and fg color of the blocked username cell.
     *
     * context  - LimitedUserForAdminDTO (we have to typecast to get it)
     * value    - The string returned by getValue() method in the Column creation block
     * sb       - We must construct it using value, which will be rendered in the cell
     * @author spgdev@spenego.com - Apr 29, 2017
     */
    static class ColorCell extends AbstractCell<String>
    {
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb)
        {
            if (value == null)
            {
                return;
            }

            LimitedUserForAdminDTO luserDTO = (LimitedUserForAdminDTO) context.getKey();
            if (luserDTO == null)
            {
                return;
            }

            SafeHtml safeHtml = null;
            boolean locked = ClientUtils.fromBoolean(luserDTO.getLocked());
            if (locked)
            {
                // lockUser is defined in obidos.css. Change colors there if desired
                // balloon CSS is defined in balloon.min.css
                safeHtml = SafeHtmlUtils.fromTrustedString(
                "<div class=\"lockedUser\">" +
                "<span data-balloon=\"Locked User\"" + " data-balloon-pos=\"right\">" +
                        value +
                "</span>" +
                "</div>");
            }
            else
            {
                safeHtml = SafeHtmlUtils.fromSafeConstant(value);
            }
            sb.append(safeHtml);
        }
    }
    
    public void showEditUserScreen(LimitedUserForAdminDTO userDTO)
    {
        Map<String,String> with = new HashMap<>();
        String nameToken = NameTokens.EDIT_USER;
        String type = ObidosConstants.TYPE_USERS;
        if (getView().getAdminSwitch().getValue())
        {
            type = ObidosConstants.TYPE_ADMINS;
        }
        with.put(ObidosConstants.USER_ID, userDTO.getId().toString());
        if (type != null)
        {
            with.put(ObidosConstants.TYPE, type);
        }
        ClientUtils.addPlace(placeManager, with, NameTokens.ADVANCED_USER_SEARCH);

        ClientUtils.showPage(placeManager, nameToken, with);
    }

    private UserDTO getUserDTO()
    {
        UserDTO dto = new UserDTO();
        StringBuilder sb = new StringBuilder();

        String username = getView().getUserNameTextBox().getValue();
        if (username == null || username.length() == 0)
        {
            username = null;
        }
        else
        {
        	sb.append(" " + glang.username());
        }
        dto.setUsername(username);
        
        Boolean locked = ClientUtils.fromBoolean(getView().getLockedUserSwitch().getValue());
        if (locked)
        {
        	dto.setLocked(locked);
        	sb.append(" " + glang.locked());
        }

        dto.setAdministrator(getView().getAdminSwitch().getValue());

        Boolean markedDeleted = getView().getDeletedUserSwitch().getValue();
        gwtLog("Search Marked Deleted Users: " + markedDeleted);
        dto.setDeleted(markedDeleted);
        if (ClientUtils.fromBoolean(markedDeleted))
        {
        	dto.setLocked(true);
        	sb.append(" " + glang.tombStoned());
        }


        String fullname = getView().getFullNameTextBox().getValue();
        if (fullname == null || fullname.length() == 0)
        {
            fullname = null;
        }
        else
        {
        	sb.append(" " + glang.fullname());
        }
        dto.setFullname(fullname);

        String email = getView().getEmailTextBox().getValue();
        if (email == null || email.length() == 0)
        {
            email = null;
        }
        else
        {
        	sb.append(" " + glang.email());
        }

        dto.setEmail1(email);
        dto.setEmail2(email);
        dto.setEmail3(email);
        
        String filter = sb.toString();
        gwtLog("Filter: " + filter + " length: " + filter.length());
        if (filter.length() == 0)
        {
        	gwtLog("Active users");
        	filter = glang.users();
        }
        String searchFilter = glang.searchFilter() + ": " + filter;
        getView().getSearchFilterLabel().setText(searchFilter);

        return dto;
    }

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserForAdminDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected String getIdName()
	{
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	public void deleteUsers()
	{
		promptDeleteUsers();
	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
		
	}

    private void promptDeleteUsers()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
       String title = lang.deleteItem();
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.USER_ID_N);
		title = lang.deleteUser();
		String message = lang.deleteUsersWarning(ids.size(), s, s, s, s, s);
       ClientUtils.promptForAction(() -> deleteUsersReal(), title, message);
    }

	private void deleteUsersReal()
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
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				String emsg = glang.couldNotDeleteItems() + ": "  + caught.getMessage();
				showErrorMessage(emsg);
			}
		};
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        safeInvocationCall(() -> UserService.Utility.getInstance().deleteUsers(authCreds, ids, callback));
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
}
