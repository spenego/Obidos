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

package com.spenego.Obidos.client.application.listnotessharedwithme;

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
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.NotebookService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.SharedNotesResult;

public class ListNotesSharedWithMePresenter
        extends ObidosPresenter<SharedItemDTO, ListNotesSharedWithMePresenter.MyView, ListNotesSharedWithMePresenter.MyProxy, ListNotesSharedWithMeUiHandlers>
        implements ListNotesSharedWithMeUiHandlers
{
    interface MyView extends View, HasUiHandlers<ListNotesSharedWithMeUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
    	public TextBox getSearchTextBox();
        public DataGrid<SharedItemDTO> getDataGrid();
        public SimplePager getPager();
        public Button getRelinquishButton();
        public Button getClearButton();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.GROUP_ID;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

    @NameToken(NameTokens.LIST_NOTES_SHARED_WITH_ME)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListNotesSharedWithMePresenter>
    {
    }

    @Inject
    ListNotesSharedWithMePresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind() {
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showNotes(selectionModel, grid, this));
    }
    
    private void enableButtons(boolean enabled)
    {
    	getView().getRelinquishButton().setEnabled(enabled);
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
        getView().getSearchTextBox().setValue("");
        showMessage("");
        clearSelections();
        DataGrid<SharedItemDTO> grid = getView().getDataGrid();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
        grid.setColumnWidth(0, "50px");
        grid.setColumnWidth(1, "35%");
    }

    // TODO: This should be moved to ObidosPresenter
    private <T,C> void setColumnUpdater(Column<T, C> col, final Consumer<T> consumer, final Supplier<C> passphraseDialogMessage) {
        col.setFieldUpdater(new FieldUpdater<T, C>() {
            @Override
            public void update(int idx, T dto, C value) {
            	boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
            	currentUser.setPassphraseRegistered(rc);
                if (rc) {
                    gwtLog("Passphrase is registered");
                	consumer.accept(dto);
                } else {
                	ClientUtils.showRegisterPassphraseDialog(placeManager, passphraseDialogMessage.get().toString(), null);
                }
            }});
    }

    private void showNotes(final SelectionModel<SharedItemDTO> selectionModel,
    		final AbstractCellTable<SharedItemDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText(lang.loading());
        grid.setEmptyTableWidget(messageLabel);


        // checkbox
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

        // note name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_ITEM_NAME);
        final Column<SharedItemDTO, String> nameCol = new Column<SharedItemDTO, String>(nameCell)
        {

            @Override
            public String getValue(SharedItemDTO dto)
            {
                return dto.getName();
            }
        };
        grid.addColumn(nameCol, lang.noteNameLabel());


//        addTextColumn(grid, dto -> dto.getOwnerFullname(), lang.sharedBy());
        
         // owner fullname
        ObidosButtonCell ownerFullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<SharedItemDTO, String> ownerFullnameCol = new Column<SharedItemDTO, String>(ownerFullnameCell)
        {

            @Override
            public String getValue(SharedItemDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(ownerFullnameCol, lang.sharedBy());

        
        
        Column<SharedItemDTO, String> editCol = addObidosButtonCellColumn(grid, dto -> lang.editItem(), lang.editButtonTitle(), ObidosConstants.CELL_TYPE_EDIT_SHARED);

        setColumnUpdater(editCol, 			dto -> showEditNotePage(dto),() -> lang.editNote());
        setColumnUpdater(nameCol, 			dto -> showViewNotePage(dto),() -> lang.viewNote());
        setColumnUpdater(ownerFullnameCol,  dto -> showUserInfoPage(dto),() -> lang.viewUserInfo());

        AsyncDataProvider<SharedItemDTO> dataProvider = new AsyncDataProvider<SharedItemDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<SharedItemDTO> noteDTO)
            {
                final Range range = noteDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                showMessage("");
                gwtLog("in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);

                GwtAsyncWrapper<SharedNotesResult> callback = new GwtAsyncWrapper<SharedNotesResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        showErrorMessage("Error:" + e.getMessage());
                    }

                    @Override
                    public void uponSuccess(SharedNotesResult result)
                    {
                        int total = result.getTotal();
                        List<SharedItemDTO> notes = result.getNotes();
                        if (notes != null && notes.size() > 0)
                        {
//                            listMessageLabel.setText("List of AD/LDAP Configuration");
                            updateRowData(start, notes);
                            updateRowCount(total, true);
                        }
                        else
                        {
                            messageLabel.setText("No Notes found");
                            updateRowCount(0, true);
                        }
                    }
                };
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                	searchString = null;
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<SharedItemDTO>) selectionModel);
                NotebookService.Utility.getInstance().getNotesSharedWithMe(authCreds, searchString, preSelectedIds, start, length, getOrderByList(), callback);
            }
        };
        
        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    protected void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    protected void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    public void refreshDataGrid(DataGrid<SharedItemDTO> grid )
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }
    
    private void showViewNotePage(SharedItemDTO dto)
    {
        String nameToken = NameTokens.VIEW_ITEM;
        Map<String, String> with = new HashMap<>();
        with.put(ObidosConstants.ITEM_ID, dto.getId().toString());
        // owner id is not used for anything
        // Bug# 856The above is not true, because of 42 owner details cannot be displayed.
        // Jun-06-2023
        with.put(ObidosConstants.OWNERID, dto.getOwnerId().toString());
        with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.NOTEBOOK);
        with.put(ObidosConstants.TYPE, ObidosConstants.SHARED_WITH_ME);
        with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES_SHARED_WITH_ME);
        ClientUtils.showPage(placeManager, nameToken, with);
    }
    
    private void showUserInfoPage(SharedItemDTO dto)
    {
    	ClientUtils.showUserInfo(placeManager, dto.getOwnerId(), getView().getMessageRow());
    }


	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void searchNoteName()
	{
		refreshDataGrid(getView().getDataGrid());
	}

    private void showEditNotePage(SharedItemDTO dto)
    {
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		
    	String place = NameTokens.getLIST_NOTES_SHARED_WITH_ME();
    	ClientUtils.showEditNoteOrItemPage(dto.getId().toString(),
    			dto.getOwnerId().toString(), 
    			place, placeManager);
    	/*
    	String nameToken = NameTokens.EDIT_ITEM;
        Map<String,String> with = new HashMap<>();

        String key = ObidosConstants.ACTION;
        with.put(key, ObidosConstants.EDIT);

        key = ObidosConstants.ITEM_ID;
        with.put(key, dto.getId().toString());
        key = ObidosConstants.OWNERID;
        with.put(key, dto.getOwnerId().toString());
        with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.NOTEBOOK);
        with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES_SHARED_WITH_ME);
        
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);
        */
    }

	@Override
	public void relinquishNotes()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		if (registered)
		{
			promptRelinquishSharedNotes();
		}
		else
		{
			String message = ObidosMessages.LANG.relinquishNote();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
	}

	@Override
	public void clearSelections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
	}
 
	private void promptRelinquishSharedNotes()
	{
    	ObidosMessages lang = ObidosMessages.LANG;
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null)
		{
			return;
		}
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.NOTE_ID_N);
		int sz = ids.size();
		String title = lang.relinquishSharedNote();
		String message = lang.relinquishSomethingWarning(sz, s, s, s, s);
		ClientUtils.promptForAction(() -> relinquishSharedNotesReal(), title, message);
	}
	
	private void relinquishSharedNotesReal()
	{
        ArrayList<Long> ids = getSelectedIds(selectionModel);
		GwtAsyncWrapper<Void> callback  = new GwtAsyncWrapper<Void>(this)
        {
            @Override
            public void uponFailure(Throwable e)
            {
				String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.NOTE_ID_N);
				String errorMessage = ObidosMessages.LANG.couldNotRelinqushSomething(s) + ": " + e.getMessage();
				showErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(Void arg0)
            {
            	clearSelections();
            	refreshDataGrid(getView().getDataGrid());
            }
        };

        safeInvocationCall(() -> NotebookService.Utility.getInstance().deleteNote(ClientUtils.getAuthCreds(), ids, callback));
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
		setOrderBy(OrderBy.ITEM_NAME_ASC);
       	refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
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
