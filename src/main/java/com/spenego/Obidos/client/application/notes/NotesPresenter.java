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

package com.spenego.Obidos.client.application.notes;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.application.widgets.summernote.event.SummernoteKeyDownEvent;
import com.spenego.Obidos.client.application.widgets.summernote.event.SummernoteKeyDownHandler;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.NotebookService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseCanCreateShareGateKeepr;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.NoteDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class NotesPresenter extends ObidosPresenter<NoteDTO, NotesPresenter.MyView, NotesPresenter.MyProxy, NotesUiHandlers> implements NotesUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    private NoteDTO origNoteDTO;

    private enum FieldNumber
    {
        noteName,
        noteContent
    };
    private EnumMap<FieldNumber, Boolean> eMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);

    private NoteDTO getOrigNoteDTO()
    {
        return origNoteDTO;
    }

    interface MyView extends View, HasUiHandlers<NotesUiHandlers>
    {
    	public Span getLockIconSpan();
    	public InputGroupAddon getInputGroupAddon();
        public BlockQuote getHelpBlockQuote();
        public Paragraph getHelpParagraph();
        public FormLabel getNoteNameLabel();
        public FormLabel getNoteContentLabel();

        public ObidosTextBox getNoteNameTextBox();
        public Summernote getSummerNote();
        public Button getSaveButton();
        public InlineRadio getPublicNoteRadio();
        public InlineRadio getPrivateNoteRadio();

        public FormLabel getSharedByNameLabel();
        public FlowPanel getSharedByFlowPanel();
       	public DropDown getOwnerDropDown();
       	public DropDownHeader getEmailHeader();
       	public DropDownHeader getPhoneHeader();
       	public Button getOwnerDropDownAnchor();

		public ObidosMessageRow getMessageRow();
		public ObidosButtonToolBar getBottomToolBar();

		public Row getExpiresRow();
		public DatePicker getDatePicker();
		public ObidosTimeBox getTimeBox();
		public ObidosPanelHeader getPanelHeader();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();

       	public Button getCopyToClipboardBtton();
       	public Button getClearButton();
       	public FlowPanel getRadioButtonsFlowPanel();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.NOTE_ID;
	}

    @NameToken(NameTokens.NOTE)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseCanCreateShareGateKeepr.class)
    interface MyProxy extends ProxyPlace<NotesPresenter>
    {
    }

    @Inject
    NotesPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager, CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        Summernote summernote = getView().getSummerNote();
        ClientUtils.customizeSummernoteToolbar(summernote);
        summernote.setDefaultHeight(200);
    }

    protected void onReveal()
    {
        super.onReveal();

        /*
		ClientUtils.setSummernoteLanguageInputToggle(getView().getSummerNote(),
			getView().getLanguageListBox(), getView().getLanguageRow());
			*/
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

        if (ClientUtils.isAdmin(currentUser)) return;
        
		resetExpiration();
        if (!ClientUtils.isPassphraseRegistered(currentUser))
        {
        	ClientUtils.showRegisterPassphraseDialog(glang.createNote(),
        			()->ClientUtils.goBack(placeManager),
        			()->ClientUtils.goToRegisterPassphrasePage(placeManager, NameTokens.NOTE));
        	return;
        }
        showMessage(null);
        getView().getPanelHeader().getBackButton().setText(glang.listNotes());
        getView().getBottomToolBar().adjustButtonsWidth();
        getView().getExpiresRow().setVisible(false);
        showSharedBy(false);
        updateNoteIcon();
        setHelpMessage();
        setPanelTitle(ObidosMessages.LANG.createNote());
        setSaveButtonTitle(ObidosMessages.LANG.create());

        resetForm();

        enableNoteNameTextBox();
        enableSummernote();
        showSaveButton(true);
        displayNote();
        updateTitles();
        fetchAndDisplayNoteOwner();
		ClientUtils.setStartDateToTomorrow(getView().getDatePicker());
        enableSaveButton(false);

        // Show language input selection if Bangla is enabled
        // It's an undocumented feature
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
		ClientUtils.setSummernoteBottomMargin("2px");
		// Show copy to clip board if Bangla is enabled
		ClientUtils.showCopyToClipbaordButtom(getView().getCopyToClipboardBtton());
		ClientUtils.showClearButton(getView().getClearButton());
		if (ClientUtils.isBanglaEditingEnabled())
		{
			languageListBoxCallback();
		}
		adjustRadioButtonsAlignmentForRTL();
		
    }
    
    private void adjustRadioBoxesForRTL(final InlineRadio r)
    {
    	Element labelElement = null;
		NodeList<Element> labels = r.getElement().getElementsByTagName("label");
		if (labels.getLength() > 0)
		{
			labelElement = labels.getItem(0);
			labelElement.getStyle().setProperty("paddingLeft","5px");
		}
		r.getElement().getStyle().setProperty("float", "left");
		Element inputElement = null;
		NodeList<Element> elements = r.getElement().getElementsByTagName("input");
		if (elements.getLength() > 0)
		{
			inputElement = elements.getItem(0);
			inputElement.getStyle().setProperty("marginRight","-20px");
		}


    }
    private void applyForcedLeftAlign(FlowPanel panel)
    {
    	gwtLog("MMM apply rtl");
        panel.getElement().getStyle().setProperty("float","left");
        panel.getElement().getStyle().setProperty("textAlign","left");
    }
    private void adjustRadioButtonsAlignmentForRTL()
    {
    	FlowPanel fp = getView().getRadioButtonsFlowPanel();
    	Element containerElement = fp.getElement();
    	Style style = containerElement.getStyle();
    	if (ClientUtils.isLocaleArabic())
    	{
    		applyForcedLeftAlign(fp);
    	}
    	else
    	{
    		style.setFloat(Float.LEFT);
    	}
    	adjustRadioBoxesForRTL(getView().getPublicNoteRadio());
    	adjustRadioBoxesForRTL(getView().getPrivateNoteRadio());
    }
    
    private void setSummenoteKeydownHandler()
    {
    	Summernote summernote = getView().getSummerNote();
        summernote.addSummernoteKeyDownHandler(new SummernoteKeyDownHandler() 
        {
            @Override
            public void onSummnernoteKeyDown(SummernoteKeyDownEvent event) 
            {
                NativeEvent nativeEvent = event.getNativeEvent();
                Window.alert("XXXX");
                if (nativeEvent.getCtrlKey() && nativeEvent.getKeyCode() == 76) // L = 76
                { 
                	Window.alert("CTRL+L pressed");
  //                  int idx = lb.getSelectedIndex();
                    // Toggle index
 //                   idx = (idx == 0) ? 1 : 0;
//                    lb.setSelectedIndex(idx);
                    // Callback will correctly set the input language based on the index of the selection box
 //                   languageListBoxCallback(lb, row);
                }
            }
        });
    }

    private void resetExpiration()
	{
		getView().getDatePicker().setValue(null);
		getView().getTimeBox().setText("12:00 AM");
	}

    private void updateDateFormat()
    {
		ClientUtils.configureDateFormat(getView().getDatePicker(), currentUser);
    }

    
    private void updateNoteIcon()
    {
		String note = ObidosMessages.LANG.note();
    	String action = ClientUtils.getActionFromUrl(placeManager);
		Span span = getView().getLockIconSpan();
    	if (action == null)
    	{
			note = ObidosMessages.LANG.noteHtml();
    		ClientUtils.setNoteSpanHtml(span, note);
    		return;
    	}
    	if (action != null)
    	{
    		if (action.equals(ObidosConstants.VIEW_NOTE))
    		{
    			getView().getNoteNameLabel().setHTML(ObidosMessages.LANG.noteName());
    		}
    		else
    		{
    			// edit
				note = ObidosMessages.LANG.noteHtml();
				ClientUtils.setNoteSpanHtml(span, note);
    			getView().getNoteNameLabel().setHTML(ObidosMessages.LANG.noteNameLabelHtml());
    		}
    	}
    }
    
    private void updateTitles()
    {
    	String type = ClientUtils.getTypeFromUrl(placeManager);
    	if (type != null)
    	{
    		if (type.equals(ObidosConstants.SHARED_WITH_OTHERS))
    		{
    			getView().getPanelHeader().setHeadingText(glang.viewNoteSharedWithOthers());
    		}
    		else if (type.equals(ObidosConstants.SHARED_WITH_ME))
    		{
    			getView().getPanelHeader().setHeadingText(glang.viewNoteSharedWithMe());
    		}
    	}
    }

    private native void toggleBanglaSummernote() /*-{
//    	$wnd.$('.note-editable').bangla('toggle');
//    	$wnd.$('.note-editable').bangla('on');
    }-*/;

    private native void toggleBanglaText() /*-{
//    	$wnd.$('input[type="text"').bangla('toggle');
    	$wnd.$('input[type="text"]').bangla('on');
    }-*/;
    
    private native void enableBanglaEditing() /*-{
    	$wnd.$('.note-editable').bangla('on');
    	$wnd.$('input[type="text"]').bangla('on');
    }-*/;

    private native void disableBanglaEditing() /*-{
    	$wnd.$('.note-editable').bangla('off');
    	$wnd.$('input[type="text"]').bangla('off');
    }-*/;

    void showSharedBy(boolean visible)
    {
    	getView().getSharedByNameLabel().setVisible(visible);
    	getView().getSharedByFlowPanel().setVisible(visible);
    }

    private void enableSaveButton(boolean enabled)
    {
        getView().getSaveButton().setEnabled(enabled);
    }

    private void showNotesTypeRadioButtons()
    {
        getView().getPublicNoteRadio().setVisible(true);
        getView().getPrivateNoteRadio().setVisible(true);

    }

    private void hideNotesTypeRadioButtons()
    {
        getView().getPublicNoteRadio().setVisible(false);
        getView().getPrivateNoteRadio().setVisible(false);

    }

    /*
    Window.addWindowClosingHandler(new Window.ClosingHandler()
    {
        boolean unsavedDate = true;
        @Override
        public void onWindowClosing(ClosingEvent event) {
            if (unsavedData) {
                event.setMessage("There is unsaved data. Do you really want to leave?");
            }
        }
    });
    */

    private void addWindowCloseHandler()
    {
        Window.addWindowClosingHandler(new Window.ClosingHandler()
        {
            boolean unsavedData = true;
            @Override
            public void onWindowClosing(ClosingEvent event)
            {
                if (unsavedData) {
                    gwtLog("There is unsaved data....");
                    event.setMessage("There is unsaved data. Do you really want to leave?");
                }
            }
        });

    }


    private String sharedBy()
    {
        String action = null;
        String sharedBy = null;

        try
        {
            action = ClientUtils.getParameterFromUrl(placeManager,ObidosConstants.ACTION);
        } catch (ParamNotFoundException e)
        {
        }

        try
        {
            sharedBy = ClientUtils.getParameterFromUrl(placeManager,ObidosConstants.SHARED_BY);
        } catch (ParamNotFoundException e)
        {
        }
        if (action != null && sharedBy != null)
        {
            return sharedBy;
        }
        return null;
    }
    private void setHelpMessage()
    {
    	/*
        Paragraph p = getView().getHelpParagraph();
        String html =
                        "<li><b>Note Name</b> is not encrypted and can be used in search</li>" +
                        "<li><b>Note Content</b> is kept encrypted and can not be searched</li>" +
                        "<li>Only a Shareable Note can be shared</li>" +
                        "<li>If note contains any XSS, it will be cleaned up before saving</li>";
        p.setHTML(html);
        */
    }

    private void setSaveButtonTitle(String title)
    {
        getView().getSaveButton().setText(title);
    }

    private void setPanelTitle(String title)
    {
        getView().getPanelHeader().setHeadingText(title);
    }

    private void showSaveButton(boolean visible)
    {
        getView().getSaveButton().setVisible(visible);
    }

    private void resetForm()
    {
    	showMessage(null);
    	updateDateFormat();
        getView().getNoteNameTextBox().setValue("");
        getView().getSummerNote().setCode("");
        getView().getSummerNote().setHeight("200");
        getView().getPublicNoteRadio().setValue(false);
        getView().getPrivateNoteRadio().setValue(false);
        resetExpiration();
        resetLabelColors();
        enableSaveButton(false);
    }

    private void displayNote()
    {
        showNotesTypeRadioButtons();
        enableSaveButton(true);

        Long noteId = null;
        String action = null;
        try
        {
            action = ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.ACTION);
        }
        catch (ParamNotFoundException e)
        {
            return;
        }

        try
        {
            noteId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.NOTE_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            return;
        }

        if (action.equals(ObidosConstants.VIEW_NOTE))
        {
            gwtLog("View note....");
            setPanelTitle(ObidosMessages.LANG.viewNote());
            showSaveButton(false);
            disableNoteNameTextBox();
            disableSummernote();
            hideNotesTypeRadioButtons();
            fetchNote(noteId);
        }
        else if (action.equals(ObidosConstants.EDIT_NOTE))
        {
        	showExpires(true);
            setPanelTitle(ObidosMessages.LANG.updateNote());
            setSaveButtonTitle(ObidosMessages.LANG.update());
            enableNoteNameTextBox();
            enableSummernote();
            hideNotesTypeRadioButtons();
            fetchNote(noteId);
            enableSaveButton(false);
        }
        else
        {
            // create note
            gwtLog("Create Note........");
            enableSaveButton(true);
        }
    }

    private void fetchNote(Long noteId)
    {
        if (noteId == null)
        {
            return;
        }
        gwtLog("Note id: " + noteId);
        GwtAsyncWrapper<NoteDTO> callback = new GwtAsyncWrapper<NoteDTO>(this)
        {

            @Override
            public void uponSuccess(NoteDTO result)
            {
                origNoteDTO = result;
                resetForm(); // must do before populate
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
        ItemExpiration exp = noteDTO.getItemExpiration();
        if (exp != null)
        {
        	gwtLog("Expires until: " + exp.getDaysUntilExpire() + " days");
        	Date expirationDate = exp.getExpiresAt();
        	getView().getDatePicker().setValue(expirationDate);
        }
        else
        {
        	gwtLog("Expiration is null");
        }
        getView().getNoteNameTextBox().setValue(noteDTO.getName());
        getView().getSummerNote().setCode(new String(noteDTO.getNotes()));
    }

    private void disableNoteNameTextBox()
    {
        getView().getNoteNameTextBox().setReadOnly(true);
    }

    private void enableNoteNameTextBox()
    {
        getView().getNoteNameTextBox().setReadOnly(false);
    }

    private void disableSummernote()
    {
        getView().getSummerNote().setEnabled(false);
    }

    private void enableSummernote()
    {
        getView().getSummerNote().setEnabled(true);
    }

    @Override
    public void saveNote()
    {
        String noteName = getView().getNoteNameTextBox().getValue();
        if (noteName == null)
        {
//            showErrorMessage("Please specify a name for your Note");
            showErrorMessage("Please specify a name for your Note");
            return;
        }

        if (noteName.length() == 0)
        {
            showErrorMessage("Please specify a name for your Note");
            return;
        }
        String note = getView().getSummerNote().getCode();
        gwtLog("Note: '" + note + "'");
         if (note == null)
        {
            showErrorMessage("Please specify a Note");
            return;
        }

        if (note.length() == 0)
        {
            showErrorMessage("Please specify a Note");
            return;
        }
        if (note.equals("<p><br></p>"))
        {
            showErrorMessage("Please specify a Note");
            return;
        }


//        SafeHtml noteSanitized = SimpleHtmlSanitizer.sanitizeHtml(note);
//        String safeNote = noteSanitized.asString();
//        gwtLog("Sanitized: " + safeNote);
        /*
        if (!note.equals(safeNote))
        {
            // We will clean it in the server side
           showErrorMessage("Your Note has XSS vulnerability, Please clean it.");
           return;
        }
        */
        String safeNote = note;
        String action = null;
        try
        {
            action = ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.ACTION);
        }
        catch (ParamNotFoundException e)
        {
        }

        if (action != null && action.equals(ObidosConstants.EDIT_NOTE))
        {
            updateNote(noteName, safeNote);
        }
        else
        {
            createNote(noteName, safeNote);
        }
    }
    
    private void clearForm()
    {
    	getView().getNoteNameTextBox().clear();
    	getView().getSummerNote().clear();
    }
    
    private void updateRadioButtons()
    {
    	getView().getPublicNoteRadio().setValue(false);
    	getView().getPrivateNoteRadio().setValue(false);
    }
    private void createNote(String noteName, String note)
    {
        Boolean isPublicContainer = getView().getPublicNoteRadio().getValue();
        Boolean isPrivateContainer = getView().getPrivateNoteRadio().getValue();

        if (isPublicContainer == false && isPrivateContainer == false)
        {
            showErrorMessage(glang.selectNoteType());
            return;
        }

        GwtAsyncWrapper<NoteDTO> callback = new GwtAsyncWrapper<NoteDTO>(this)
        {

            @Override
            public void uponSuccess(NoteDTO noteDTO)
            {
            	showMessage(glang.noteCreatedSuccessFully(noteName));
            	clearForm();
            	updateRadioButtons();
            	// note created now upload
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(glang.couldNotSaveNote(caught.getMessage()));
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        Boolean shareable = getView().getPublicNoteRadio().getValue();
        byte[] noteNameBytes = noteName.getBytes();
        String hex = ClientUtils.getHexString(noteNameBytes);
        byte[] utf8BytesNoteName = null;
        try
        {
            utf8BytesNoteName = noteName.getBytes("UTF-8");
            gwtLog("XXX get bytes as UTF-8 size: " + utf8BytesNoteName.length);
            hex = ClientUtils.getHexString(utf8BytesNoteName);
            gwtLog("XXX hex: " + hex);
        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            String noteNameUtf8 = new String(utf8BytesNoteName,"UTF-8");
            gwtLog("XXX noteNameUtf8: " + noteNameUtf8);
        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ItemExpiration itemExpiration = null;
       	Date expireDate = ClientUtils.getDateFromDatePickerAndTimeBox(
       			currentUser,
    			getView().getDatePicker(), 
    			getView().getTimeBox(),
    			getView().getMessageRow());
    	if (expireDate != null)
    	{
    		itemExpiration = new ItemExpiration(expireDate);
    	}
   		gwtLog("Expire date: " + expireDate);

        NotebookService.Utility.getInstance().createNote(authCreds, noteName, note.getBytes(), shareable, itemExpiration, null, callback);

    }


    private void updateNote(String noteName, String noteContent)
    {
        Long noteId = null;
        try
        {
            noteId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.NOTE_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
//            showErrorMessage("Could not get " + ObidosConstants.NOTE_ID + " from URL");
            showErrorMessage("Could not get " + ObidosConstants.NOTE_ID + " from URL");
            return;
        }
        NoteDTO dto = new NoteDTO();
        dto.setId(noteId);
        dto.setName(noteName);
        gwtLog("Updated Note Content: " + noteContent);
        dto.setNotes(noteContent.getBytes());

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                String date = ClientUtils.formattedDate(new Date());
                showMessage("Note Saved at: " + date);
                gwtLog("note saved..");

                resetLabelColors();
                enableSaveButton(false);

                // No do not go to list view, user is updating note, how do we know that she is done?
//                listNotes();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
//                showErrorMessage("Could not update note");
                showErrorMessage("Could not update note");
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        NotebookService.Utility.getInstance().updateNote(authCreds, dto, callback);
    }


    /*
    @Override
    public void listNotes()
    {
    	String type = ClientUtils.getTypeFromUrl(placeManager);
    	if (isNoteSharedbySomeone())
    	{
    		ClientUtils.showPage(placeManager, NameTokens.LIST_NOTES_SHARED_WITH_ME);
    	}
    	else if (type != null)
    	{
    		if (type.equals(ObidosConstants.SHARED_WITH_OTHERS))
    		{
            	Map<String,String> with = new HashMap<>();
            	with.put(ObidosConstants.ACTION,type);
            	ClientUtils.showPage(placeManager, NameTokens.LIST_NOTES, with);
    		}
    	}
    	else
    	{
    		ClientUtils.showPage(placeManager, NameTokens.LIST_NOTES);
    	}

    }
    */

    @Override
    public void showHideHelp()
    {
    	showHelp();
    }

    @Override
    public void noteNameTextBoxKeyUpCallback()
    {
    	showMessage(null);
        NoteDTO oDTO = getOrigNoteDTO();
        if (oDTO == null)
        {
            return;
        }
        FormLabel label = getView().getNoteNameLabel();
        String oN = oDTO.getName();
        String nN = getView().getNoteNameTextBox().getValue();

        showFormValueChange(oN, nN, label, FieldNumber.noteName);
    }

    @Override
    public void summernoteKeyUpCallback()
    {
    	showMessage(null);
        NoteDTO oDTO = getOrigNoteDTO();
        if (oDTO == null)
        {
            return;
        }
        FormLabel label = getView().getNoteContentLabel();
        String oN = new String(oDTO.getNotes());
        String nN = getView().getSummerNote().getCode();

        showFormValueChange(oN, nN, label, FieldNumber.noteContent);

    }

    private void showFormValueChange(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
    {
        boolean dirty = false;
        if (origVal != null && formVal != null && formVal.length() == 0)
        {
            dirty = true;
        }
        if (origVal == null && (formVal != null && formVal.length() > 0))
        {
            dirty = true;
        }

        if (origVal == null)
        {
            gwtLog("origVal is null dirty: " + dirty);
        }

        if (origVal != null && !origVal.equals(formVal))
        {
            gwtLog("Someone is typing in the form...");
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }

        switch(fieldNumber)
        {
            case noteName:
            {
                eMap.put(FieldNumber.noteName, dirty);
                break;
            }
            case noteContent:
            {
                eMap.put(FieldNumber.noteContent, dirty);
                break;
            }
            default:
            {
                break;
            }
        }
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableModifyButton();
    }

    private void enableModifyButton()
    {
        enableSaveButton(true);
    }

     private void disableModifyButton()
     {
        enableSaveButton(false);
     }

    private void enableDisableModifyButton()
    {
        Iterator<FieldNumber> enumKeySet = eMap.keySet().iterator();
        boolean dirty = false;
        while(enumKeySet.hasNext())
        {
            FieldNumber fieldNumber = enumKeySet.next();
            boolean val = eMap.get(fieldNumber);
            if (val)
            {
                dirty = true;
                break;
            }
        }
        if (dirty)
        {
            enableModifyButton();
        }
        else
        {
            disableModifyButton();
        }
    }

    private void setFormLabelColorToOriginal(FormLabel label)
    {
        ClientUtils.setFormLabelsColorOriginal(label);
    }


    private void resetLabelColors()
    {
        setFormLabelColorToOriginal(getView().getNoteNameLabel());
        setFormLabelColorToOriginal(getView().getNoteContentLabel());
    }

    @Override
    public void resetFormRpc()
    {
        fetchNote(getId());
    }

    private boolean isNoteSharedbySomeone()
    {
        Long noteId = null;;
        Long ownerId = null;
		try
		{
			noteId = getIdFromUrl(ObidosConstants.NOTE_ID);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
		}

		try
		{
			ownerId = getIdFromUrl(ObidosConstants.OWNERID);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
		}

		if (noteId != null && ownerId != null)
		{
			return true;
		}
		return false;
    }

    private void fetchAndDisplayNoteOwner()
    {
        Long noteId = null;;
        Long ownerId = null;
		try
		{
			noteId = getIdFromUrl(ObidosConstants.NOTE_ID);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
		}

		try
		{
			ownerId = getIdFromUrl(ObidosConstants.OWNERID);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
		}

		if (noteId != null && ownerId != null)
		{
			showSharedBy(true);
		}
		else
		{
			return;
		}
        GwtAsyncWrapper<SharedItemDTO> callback = new GwtAsyncWrapper<SharedItemDTO>(this)
        {

            @Override
            public void uponSuccess(SharedItemDTO dto)
            {
				gwtLog("NotesPresenter: shared item name = " + dto.getName());

                getView().getOwnerDropDownAnchor().setText(dto.getOwnerFullname());
                getView().getEmailHeader().setText(dto.getEmail());
                getView().getPhoneHeader().setText(dto.getPhone());

            }

            @Override
            public void uponFailure(Throwable caught)
            {
//                showErrorMessage("Could not obtain Shared Item: " + caught.getMessage());
                showErrorMessage("Could not obtain Shared Item: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ItemService.Utility.getInstance().getSharedItem(authCreds, noteId, toArray(OrderBy.ITEM_NAME_ASC), callback);
    }

    private void showExpires(boolean visible)
    {
    	getView().getExpiresRow().setVisible(visible);
    }

	@Override
	public void publicRadioCallback()
	{
		showMessage(null);
		showExpires(true);
	}

	@Override
	public void privateRadioCallback()
	{
		showMessage(null);
		showExpires(false);
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}
	
	private boolean nameEmpty()
	{
		String text = getView().getNoteNameTextBox().getValue();
		if (text != null && text.length() > 0)
		{
			return false;
		}
		return true;
	}

	@Override
	public void namePastedCallback()
	{
		gwtLog("name pasted..");
		enableSaveButton(false);
		if (!nameEmpty())
		{
			enableSaveButton(true);
		}
	}

	@Override
	public void nameTypingCallback()
	{
		enableSaveButton(false);
		if (!nameEmpty())
		{
			enableSaveButton(true);
		}

		
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}

	@Override
	public void listMyNotes()
	{
		ClientUtils.showPage(placeManager, NameTokens.LIST_NOTES);
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

	@Override
	public void copyToClipboard()
	{
        String htmlString = getView().getSummerNote().getCode();
        if (htmlString != null && htmlString.length() > 0)
        {
        	HTML html = new HTML(SafeHtmlUtils.fromTrustedString(htmlString));
        	String txt = html.getText();
        	ClientUtils.copyTextToClipboard(txt);
        }
	}

	@Override
	public void clearNote()
	{
		getView().getSummerNote().setCode("");
	}

}

