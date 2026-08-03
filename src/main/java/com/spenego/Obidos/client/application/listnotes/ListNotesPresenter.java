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

package com.spenego.Obidos.client.application.listnotes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
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
import com.spenego.Obidos.shared.dto.LimitedItemDTO;
import com.spenego.Obidos.shared.dto.NotesResult;

public class ListNotesPresenter 
	extends ObidosPresenter<LimitedItemDTO, ListNotesPresenter.MyView, ListNotesPresenter.MyProxy, ListNotesUiHandlers>
	implements ListNotesUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    private enum FieldNumber
    {
    	checkBoxCol,
        nameCol,
        shareCol,
        revokeCol,
        viewCol,
        editCol,
    };

    interface MyView extends View, HasUiHandlers<ListNotesUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public DataGrid<LimitedItemDTO> getDataGrid();
        public SimplePager getPager();
        public TextBox getSearchTextBox();
        public Button getSearchButton();
        public Button getDeleteButton();
        public Button getClearButton();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

    @NameToken(NameTokens.LIST_NOTES)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListNotesPresenter>
    {
    }

    @Inject
    ListNotesPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        showMessage("");
        clearSelections();
        resetSearchBox();
        updatePanelHeading(glang.listMyNotes());
        DataGrid<LimitedItemDTO> grid = getView().getDataGrid();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
        setColumnWidth(grid, 0, "55px");  // Checkbox column
        setColumnWidth(grid, 1, "35%");  
    }
    
    private void resetSearchBox()
    {
        getView().getSearchTextBox().setValue("");

    }

    private void updatePanelHeading(String title)
    {
    	getView().getPanelHeader().setTitle(title);
    }

    public void refreshDataGrid(DataGrid<LimitedItemDTO> grid )
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private boolean notesSharedWithothers()
    {
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type == null)
        {
        	return false;
        }
        if (type.equals(ObidosConstants.SHARED_WITH_OTHERS))
        {
            return true;
        }
        return false;
    }

    private void showNotes(final SelectionModel<LimitedItemDTO> selectionModel, 
    		final AbstractCellTable<LimitedItemDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText(lang.loading());
        grid.setEmptyTableWidget(messageLabel);
        
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

        // note name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_NOTE_NAME);
        final Column<LimitedItemDTO, String> nameCol = new Column<LimitedItemDTO, String>(nameCell)
        {

            @Override
            public String getValue(LimitedItemDTO dto)
            {
                return dto.getName();
            }
        };
        grid.addColumn(nameCol, lang.noteNameLabel());

        
        Column<LimitedItemDTO, String> shareCol                = addObidosButtonCellColumn(grid, dto -> lang.shareButtonTitle(),  lang.shareButtonTitle(),     ObidosConstants.CELL_TYPE_SHARE);
        Column<LimitedItemDTO, String> revokeCol               = addObidosButtonCellColumn(grid, dto -> lang.revokeButtonTitle(), lang.revokeButtonTitle(), ObidosConstants.CELL_TYPE_REVOKE);
        final Column<LimitedItemDTO, String> editCol           = addObidosButtonCellColumn(grid, dto -> lang.edit(),              lang.editButtonTitle(),          ObidosConstants.CELL_TYPE_EDIT);

        ObidosButtonCell listUsersCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_NOTE_SHARED_WITH_USERS_GROUPS);
        final Column<LimitedItemDTO, String> listUsersCol = new Column<LimitedItemDTO, String>(listUsersCell)
        {

            @Override
            public String getValue(LimitedItemDTO dto)
            {
                return dto.getName();
            }
        };
        grid.addColumn(listUsersCol, lang.sharedWith());
       

        AsyncDataProvider<LimitedItemDTO> dataProvider = new AsyncDataProvider<LimitedItemDTO>()
        {
            @Override
            protected void onRangeChanged(HasData<LimitedItemDTO> noteDTO)
            {
                final Range range = noteDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                showMessage("");
                gwtLog("in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);

                GwtAsyncWrapper<NotesResult> callback = new GwtAsyncWrapper<NotesResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        showErrorMessage(glang.couldNotFetchNotes(e.getMessage()));
                    }

                    @Override
                    public void uponSuccess(NotesResult result)
                    {
                        int total = result.getTotal();
                        List<LimitedItemDTO> notes = result.getNotes();
                        if (notes != null && notes.size() > 0)
                        {
                            if (notesSharedWithothers())
                            {
                                updatePanelHeading(ObidosMessages.LANG.myNotesSharedWithOthers());
                            }
                            updateRowData(start, notes);
                            updateRowCount(total, true);
                        }
                        else
                        {
                            if (notesSharedWithothers())
                            {
                                updatePanelHeading(ObidosMessages.LANG.myNotesSharedWithOthers());
                            }
                            messageLabel.setText(lang.noNotesFound());
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                Boolean shared = null; // show all my notes Issue #130
                if (notesSharedWithothers())
                {
                    shared = true;
                }
                String search = getView().getSearchTextBox().getText();
                gwtLog("Search: '" + search + "'");
                ArrayList<Long> preSelectedIds = getSelectedIds((MultiSelectionModel<LimitedItemDTO>) selectionModel);
                NotebookService.Utility.getInstance().getMyNotes(authCreds, shared, search, preSelectedIds,  start, length, getOrderByList(), callback);
            }
        };

        nameCol.setFieldUpdater(new FieldUpdater<LimitedItemDTO, String>()
        {
            @Override
            public void update(int idx, LimitedItemDTO dto, String value)
            {
                gwtLog("Clicked on: " + dto.getName() + " value: " + value + " Note ID: " + dto.getId());
//                sendToCorrrectPlace(dto,FieldNumber.nameCol);
                sendToCorrrectPlace(dto,FieldNumber.viewCol);
            }

        });


        // handler for Share button
        shareCol.setFieldUpdater(new FieldUpdater<LimitedItemDTO, String>()
        {

            @Override
            public void update(int idx, LimitedItemDTO dto, String value)
            {
                sendToCorrrectPlace(dto,FieldNumber.shareCol);
            }

        });

        // handler for Revoke button
        revokeCol.setFieldUpdater(new FieldUpdater<LimitedItemDTO, String>()
        {

            @Override
            public void update(int idx, LimitedItemDTO dto, String value)
            {
                sendToCorrrectPlace(dto,FieldNumber.revokeCol);
            }
        });

        // handler for Edit button
        editCol.setFieldUpdater(new FieldUpdater<LimitedItemDTO, String>()
        {

            @Override
            public void update(int idx, LimitedItemDTO credentialsDTO, String value)
            {
//                showEditNotePage(noteDTO);
                sendToCorrrectPlace(credentialsDTO,FieldNumber.editCol);
            }
         });

        // handler for view button
        listUsersCol.setFieldUpdater(new FieldUpdater<LimitedItemDTO, String>()
        {
            @Override
            public void update(int index, LimitedItemDTO dto, String value)
            {
                gwtLog("showCredentialPage()");
//                sendToCorrrectPlace(credentialsDTO,FieldNumber.viewCol);
                sendToCorrrectPlace(dto,FieldNumber.nameCol);
            }
        });


        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }
    
    private void sendToCorrrectPlace(LimitedItemDTO lidto,FieldNumber fieldNumber)
    {
        if (currentUser != null)
        {
            gwtLog("Passphrase cached: " + currentUser.getPassphraseRegistered());
        }
        boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
        
		switch(fieldNumber)
		{
			case nameCol:
			{
				if (rc)
				{
					gwtLog("show list of users the note is shaerd with");
					showListOfUsersTheNoteIsSharedWith(lidto);
				}
				else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.listOfUsersNoteIsSharedWith(),null);
				}
				break;
			}
			case shareCol:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				if (rc)
				{
					gwtLog("Passphrase is cached, goto share credential page");
					showShareWithPage(lidto);
				}
				else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.shareNote(),null);
				}
				break;
			}
			case revokeCol:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}

				if (rc)
				{
					showRevokeItemPage(lidto);
				}
				else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.revokeSharingNote(),null);
				}
				break;
			}
			case viewCol:
			{
				if (rc)
				{
					showNotePage(lidto, fieldNumber);
				}
				else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.viewNote(),null);
				}
				break;
			}
			case editCol:
			{
				if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
				{
					return;
				}
				if (rc)
				{
					showNotePage(lidto, fieldNumber);
				}
				else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.editNote(),null);
				}
				break;
			}
		default:
			break;
		}
    }



    public void showNotePage(LimitedItemDTO lidto, FieldNumber fieldNumber)
    {
        String nameToken = NameTokens.NOTE;
        switch(fieldNumber)
        {
            case viewCol:
            {
            	nameToken = NameTokens.VIEW_ITEM;
            	Map<String,String> with = new HashMap<>();
                with.put(ObidosConstants.ITEM_ID,lidto.getId().toString());
                with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.NOTEBOOK);
                ClientUtils.addType(placeManager, with);
                with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES);
                ClientUtils.showPage(placeManager, nameToken, with);
                return; 
            }
            case editCol:
            {
				nameToken = NameTokens.EDIT_ITEM;
				Map<String,String> with = new HashMap<>();

				String key = ObidosConstants.ACTION;
				with.put(key, ObidosConstants.EDIT);

				key = ObidosConstants.ITEM_ID;
				with.put(key, lidto.getId().toString());
                with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.NOTEBOOK);
                ClientUtils.addType(placeManager, with);
				with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES);

				ClientUtils.showPage(placeManager, nameToken, with);
                return;
            }
            default:
            {
                return;
            }
        }
    }

    private void showShareWithPage(LimitedItemDTO dto)
    {
    	String nameToken = NameTokens.SHARE_WITH;
		Map<String,String> with = new HashMap<>();
		with.put(ObidosConstants.ITEM_ID, dto.getId().toString());
		with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES);
		with.put(ObidosConstants.SHARE, ObidosConstants.NOTE);
		ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
		ClientUtils.addParamToMap(placeManager, with, ObidosConstants.ACTION);
		ClientUtils.showPage(placeManager, nameToken, with);

    }
    
   /**
    * Indicate that this credential is shared with someone
    * @author spgdev@spenego.com - Aug 14, 2017
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
            String title = "<span title=\"" + value + "\"</span>";
            if (isSharedWithSomeone)
            {
                // add share icon for shared credentials
                gwtLogStatic("++++++++++++++ Item: '" + value + "' is shared with someone");
                // Note pointer is the hand cursor here
            	StringBuilder sb = new StringBuilder();
                sb.append("<span style='cursor:pointer'");
                sb.append(" ");
                sb.append("title=");
                sb.append("'");
                sb.append(ObidosMessages.LANG.noteSharedWithOthers());
                sb.append("'");
                sb.append("</span>");

                sb.append("<div>");
                sb.append("	<span>");
                sb.append("		​<i class='fa fa-share-alt-square' style='color:green'></i>");
                sb.append("&nbsp;");
                sb.append(value);
                sb.append("	</span>");
                sb.append("</div>");
                String html = sb.toString();
                safeHtml = SafeHtmlUtils.fromTrustedString(html);
                safeHtmlBuilder.append(safeHtml);

            }
            else
            {
                gwtLogStatic("-------------- Private Note: '" + value + "'");
                // note it is important to wrap the value with div and span
                safeHtml = SafeHtmlUtils.fromTrustedString(title + value);
                safeHtmlBuilder.append(safeHtml);
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
                    Boolean sharedWithOthers = dto.getShared();
                    gwtLogStatic(">>>> Note is shared with others: " + sharedWithOthers);
                    // set updater only if the note is sharable
                    if (sharedWithOthers == null) // just in case
                    {
                        sharedWithOthers = Boolean.FALSE;
                    }
                    if (sharedWithOthers == Boolean.TRUE)
                    {
                        valueUpdater.update(value);
                    }
                }
            }
        }
    }

    /**
     * @Deprecated
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
            gwtLogStatic(">>>> Note is shareable: " + shareable);
            if (shareable == null) // just in case
            {
                shareable = Boolean.FALSE;
            }
            gwtLogStatic("Note is shareable: " + shareable);
            if (shareable == Boolean.FALSE)
            {
            	String text = ObidosMessages.LANG.privateNote();
            	String tooltip = ObidosMessages.LANG.privateNoteCannotBeShared();
            	sb.appendHtmlConstant(ClientUtils.makeDisabledText(text, tooltip));
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
                    Boolean shareable = dto.getShareable();
                    gwtLogStatic(">>>> Note is shareable: " + shareable);
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
        ClientUtils.gwtLog("ListNotesPresenter", message);
    }

    private void showListOfUsersTheNoteIsSharedWith(LimitedItemDTO dto)
    {
        Long noteId = dto.getId();
        String nameToken = NameTokens.LIST_USERS_ITEM_IS_SHARED_WITH;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.ITEM_ID, noteId.toString());
        with.put(ObidosConstants.ITEM_TYPE,ObidosConstants.NOTEBOOK);
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	with.put(ObidosConstants.TYPE, type);
        }
        with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    @Override
    public void searchNoteName()
    {
        gwtLog("Search Note Name");
        refreshDataGrid(getView().getDataGrid());
    }

    private void showRevokeItemPage(LimitedItemDTO dto)
    {
    	// Issue 752
    	String nameToken = NameTokens.REVOKE_ITEM;
       	Map<String,String> with = new HashMap<>();
       	with.put(ObidosConstants.ITEM_ID,dto.getId().toString());
		with.put(ObidosConstants.ACTION,ObidosConstants.REVOKE_NOTE_SHARING);
		with.put(ObidosConstants.PLACE, NameTokens.LIST_NOTES);
		ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
    	
		ClientUtils.showPage(placeManager, nameToken, with);
    }

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void deleteNotes()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		if (registered)
		{
			promptDeleteNotes();
		}
		else
		{
			String message = ObidosMessages.LANG.deleteNote();
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
		return ObidosConstants.ITEM_ID;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	private void promptDeleteNotes()
	{
    	ObidosMessages lang = ObidosMessages.LANG;
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String title = "";
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.NOTE_ID_N);
		title = lang.deleteNote();
		String message = lang.deleteSomethingWarning(ids.size(), s, s, s);
       ClientUtils.promptForAction(() -> deleteNotesReal(), title, message);
	}
	
	
	private void deleteNotesReal()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage(ObidosMessages.LANG.noNotesSelected());
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
				String errorMessage = glang.couldNotDeleteNotes() + ": " + caught.getMessage();
				showErrorMessage(errorMessage);
				
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
