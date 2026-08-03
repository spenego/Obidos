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

package com.spenego.Obidos.client.application.listusersnoteissharedwith;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.NotebookService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.NoteDTO;


public class ListUsersNoteIsSharedWithPresenter
        extends ObidosPresenter<LimitedUserDTO, ListUsersNoteIsSharedWithPresenter.MyView, ListUsersNoteIsSharedWithPresenter.MyProxy, ListUsersNoteIsSharedWithUiHandlers>
        implements ListUsersNoteIsSharedWithUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
    final SingleSelectionModel<LimitedUserDTO> selectionModel = new SingleSelectionModel<LimitedUserDTO>();
    interface MyView extends View, HasUiHandlers<ListUsersNoteIsSharedWithUiHandlers>
    {
    	public Button getHelpButton();
    	public BlockQuote getHelpBlockQuote();
        public DataGrid<LimitedUserDTO> getDataGrid();
        public SimplePager getPager();
        public ObidosMessageRow getMessageRow();
        public TextBox getNoteNameTextBox();
        public Summernote getSummerNote();
        public Button getUnshareNoteButton();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.GROUP_ID;
	}

	@NameToken(NameTokens.LIST_USERS_NOTE_IS_SHARED)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ListUsersNoteIsSharedWithPresenter>
    {
    }

    @Inject
    ListUsersNoteIsSharedWithPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
    		final CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    private void setShareButtons() {
        LimitedUserDTO dto = selectionModel.getSelectedObject();
        if (dto != null)
        {
            gwtLog("Selected: " + dto.getFullname());
            String title = "Unshare Note with:" + dto.getFullname();
            setUnshareButtonTitle(title);
            enableUnshareButton();
        }
        else
        {
            disableUnshareButton();
            gwtLog("Unselected");
        }
    }

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> setShareButtons(), grid -> showUserList(grid, this));
    }

    private void enableUnshareButton()
    {
        getView().getUnshareNoteButton().setEnabled(true);
    }

    private void disableUnshareButton()
    {
        getView().getUnshareNoteButton().setEnabled(false);
    }

    private void setUnshareButtonTitle(String title)
    {
        // don't use setTitle()
        getView().getUnshareNoteButton().setText(title);

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
        disableUnshareButton();
        updateNoteInfo();
        refreshDataGrid();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
    }

    private void updateNoteInfo()
    {
        Long noteId = getId();
        fetchNote(noteId);

    }
    private void fetchNote(Long noteId)
    {
        if (noteId == null)
        {
            showErrorMessage("Could not find Note ID in URL");
            return;
        }
        gwtLog("Note id: " + noteId);
        GwtAsyncWrapper<NoteDTO> callback = new GwtAsyncWrapper<NoteDTO>(this)
        {

            @Override
            public void uponSuccess(NoteDTO result)
            {
                populateForm(result);
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not fetch Note: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        NotebookService.Utility.getInstance().getNote(authCreds, noteId, callback);
    }

    private void populateForm(NoteDTO noteDTO)
    {
        gwtLog("Populating note...");
        getView().getNoteNameTextBox().setValue(noteDTO.getName());
        getView().getSummerNote().setCode(new String(noteDTO.getNotes()));
    }

    public void refreshDataGrid()
    {
    	gwtLog(">>>>>>>>>>>>>>>>>>> Refreshing grid... <<<<<<<<<<<<<<<<<<");
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void showUserList(final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);


  	    Column<LimitedUserDTO,Boolean> checkColumn =
	            new Column<LimitedUserDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(LimitedUserDTO dto)
            {
                return selectionModel.isSelected(dto);
            }
        };
        grid.addColumn(checkColumn, "Select User To Unshare");

	    // Should LimitedUserDTO have username?
	    // Any user can obtain all the username and login attempt can be made. Just curious
	    TextColumn<LimitedUserDTO> emailColumn = new TextColumn<LimitedUserDTO>()
        {

            @Override
            public String getValue(LimitedUserDTO luserDTO)
            {
                if (luserDTO != null)
                {
                    return luserDTO.getEmail1();
                }
                else
                {
                    return "N/A";
                }
            }
        };
        grid.addColumn(emailColumn, "Username");


	    TextColumn<LimitedUserDTO> fullnameColumn = new TextColumn<LimitedUserDTO>()
        {

            @Override
            public String getValue(LimitedUserDTO userDTO)
            {
                if (userDTO != null)
                    return userDTO.getFullname();
                else
                    return "N/A";
            }
        };
        grid.addColumn(fullnameColumn, "Full Name");

        // Delete Button. Handler is at the bottom
        /*
        final Column<LimitedUserDTO, String> delCol = new Column<LimitedUserDTO, String> (
                new ButtonCell(ButtonType.DANGER, IconType.REMOVE))
        {

                    @Override
                    public String getValue(LimitedUserDTO userDTO)
                    {
                        return "Remove";
                    }

         };
         grid.addColumn(delCol,"Remove User From Sharing List");
         */

        AsyncDataProvider<LimitedUserDTO> dataProvider = new AsyncDataProvider<LimitedUserDTO>()
        {

           @Override
            protected void onRangeChanged(HasData<LimitedUserDTO> userDTO)
            {
                final Range range = userDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                gwtLog(" in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);
                GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        Window.alert("Exception received: " + e.getMessage());
                        updateRowCount(0, true);
                        return;
                    }

                    @Override
                    public void uponSuccess(LimitedUserResult userResult)
                    {
                        int numberOfUsers = userResult.getTotalUsers();
                        List<LimitedUserDTO> users = userResult.getUsers();
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
                        	showMessage("Note is shared with " + numberOfUsers + " users");
                            updateRowCount(numberOfUsers, true);
                            updateRowData(start, users);
                        }
                        else
                        {
                            messageLabel.setText("No users found ...");
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Long noteId = getId();
                if (noteId == null)
                {
                    return;
                }
                gwtLog("Note ID: " + noteId);
				final ArrayList<Long> preSelectedUsers = null;
                NotebookService.Utility.getInstance().getUsersForNote(authCreds, noteId, null, null, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);


            } // end onRangeChanged()
        };


        // handler for Delete button
        /*
        delCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserDTO userDTO, String value)
            {
//                promptDeleteUser(grid, idx, userDTO);
                grid.redraw();
            }

         });
         */

         getView().getPager().setDisplay(grid);
         dataProvider.addDataDisplay(grid);
    }

    @Override
    public void listMyNotes()
    {
        ClientUtils.showPage(placeManager, NameTokens.LIST_NOTES);
    }

    @Override
    public void unshareNote()
    {
        gwtLog("in unshareNote()");
        LimitedUserDTO selectedDTO = selectionModel.getSelectedObject();
        if (selectedDTO == null)
        {
            return;
        }
        gwtLog("Unshare note with: " + selectedDTO.getFullname());
        Long noteId = getId();
        if (noteId == null)
        {
            return;
        }
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                showMessage("User " + selectedDTO.getFullname() + " removed from Note sharing list");
                disableUnshareButton();
                refreshDataGrid();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not remove user " + selectedDTO.getFullname() + " from sharing list: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        NotebookService.Utility.getInstance().revokeFromUsers(authCreds, noteId, toArray(selectedDTO.getId()), false, null, callback);
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
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

}
