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

import javax.inject.Inject;

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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.util.ClientUtils;

/**
 * @author spgdev@spenego.com - Oct 4, 2017
 *
 */
class NotesView extends ViewWithUiHandlers<NotesUiHandlers> implements NotesPresenter.MyView
{
    interface Binder extends UiBinder<Widget, NotesView>
    {
    }
    
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    InputGroupAddon inputGroupAddon;


    @UiField
    Paragraph helpParagraph;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    FormLabel noteNameLabel;
    
    @UiField
    Span lockIconSpan;

    @UiField
    FormLabel noteContentLabel;

    @UiField
    ObidosTextBox noteNameTextBox;

    @UiField
    Summernote summerNote;

    @UiField
    Button saveButton;

    @UiField
    InlineRadio publicNoteRadio;

    @UiField
    InlineRadio privateNoteRadio;

    @UiField
    FormLabel sharedByNameLabel;

    @UiField
    FlowPanel sharedByFlowPanel;

    @UiField
    DropDown ownerDropDown;

    @UiField
    DropDownHeader emailHeader;

    @UiField
    DropDownHeader phoneHeader;

    @UiField
    Button ownerDropDownAnchor;

    @UiField
	ObidosMessageRow messageRow;

	
    @UiField
    ObidosButtonToolBar bottomToolBar;

   	@UiField
	Row expiresRow;

	@UiField
	DatePicker datePicker;

	@UiField
	ObidosTimeBox timeBox;

	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;

	@UiField
	Button copyToClipboardBtton;

	@UiField
	Button clearButton;
	
	@UiField
	FlowPanel radioButtonsFlowPanel;
	
    @Inject
    NotesView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().showHideHelp();
				}
			});
		}

		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					backButton.setText(ObidosMessages.LANG.listNotes());
					getUiHandlers().listMyNotes();
				}
			});
		}

		ClientUtils.setSummernoteLanguageInputToggle(summerNote,
				languageListBox, languageRow);
    }

    @UiHandler("saveButton")
    void onClickSaveButton(ClickEvent e)
    {
        getUiHandlers().saveNote();
    }

    /*
    @UiHandler("noteNameTextBox")
    void onKeyUpnoteNameTextBox(KeyDownEvent e)
    {
        getUiHandlers().noteNameTextBoxKeyUpCallback();
    }
    */

    /*
    @UiHandler("summerNote")
    void onKeyUpSummerNote(SummernoteKeyUpEvent e)
    {
    	GWT.log("SSS: Fire key up event");
        getUiHandlers().summernoteKeyUpCallback();
    }
    */

    /*
    @UiHandler("summerNote")
    void onChangeSUmmerNote(SummernoteChangeEvent e)
    {
//    	GWT.log("SSS: Fire change event");
//        getUiHandlers().summernoteKeyUpCallback();
    }
    */
    /*
    @UiHandler("summerNote")
    void onPasteSummernote(SummernotePasteEvent e)
    {
    	GWT.log("SSS: Fire paste event");
        getUiHandlers().summernoteKeyUpCallback();
    }
    */
    
    @UiHandler("copyToClipboardBtton")
    void onClickcopyToClipboardBtton(ClickEvent e)
    {
    	String html = summerNote.getCode();
    	getUiHandlers().copyToClipboard();
    	int t = Integer.parseInt(ObidosMessages.LANG.copyTooltipTimerSchedule());
    	if (html != null && html.length() > 0)
    	{
    		copyToClipboardBtton.state().loading();
    		new Timer()
    		{
    		    @Override
    		    public void run()
    		    {
    		      copyToClipboardBtton.state().reset();
    		    }
    		  }.schedule(t);
    	}
    }

    @UiHandler("clearButton")
    void onClickClearButton(ClickEvent e)
    {
    	getUiHandlers().clearNote();
    }

    @UiHandler("publicNoteRadio")
    void onclickPublicNoteRadio (ClickEvent e)
	{
    	getUiHandlers().publicRadioCallback();
	}

    @UiHandler("privateNoteRadio")
    void onclickPrivateNoteRadio (ClickEvent e)
	{
    	getUiHandlers().privateRadioCallback();
	}

    @UiHandler("noteNameTextBox")
    void onKeyUpNoteNameTextBox(KeyUpEvent e)
    {
    	getUiHandlers().nameTypingCallback();
    }
    @UiHandler("noteNameTextBox")
    void onPasteNoteNameTextBox(ValueChangeEvent<String>e)
    {
    	getUiHandlers().namePastedCallback();
    }
    
    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }

    public ObidosTextBox getNoteNameTextBox()
    {
        return noteNameTextBox;
    }

    public Summernote getSummerNote()
    {
        return summerNote;
    }

    @Deprecated
    public FormLabel getFormErrorLabel()
    {
        return null;
    }

    public Button getSaveButton()
    {
        return saveButton;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public FormLabel getNoteNameLabel()
    {
        return noteNameLabel;
    }

    public FormLabel getNoteContentLabel()
    {
        return noteContentLabel;
    }

    public InlineRadio getPublicNoteRadio()
    {
        return publicNoteRadio;
    }

    public InlineRadio getPrivateNoteRadio()
    {
        return privateNoteRadio;
    }

	public DropDown getOwnerDropDown()
	{
		return ownerDropDown;
	}

	public DropDownHeader getEmailHeader()
	{
		return emailHeader;
	}

	public DropDownHeader getPhoneHeader()
	{
		return phoneHeader;
	}

	public Button getOwnerDropDownAnchor()
	{
		return ownerDropDownAnchor;
	}

	public FormLabel getSharedByNameLabel()
	{
		return sharedByNameLabel;
	}

	public FlowPanel getSharedByFlowPanel()
	{
		return sharedByFlowPanel;
	}

	public InputGroupAddon getInputGroupAddon()
	{
		return inputGroupAddon;
	}

	public Span getLockIconSpan()
	{
		return lockIconSpan;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getBottomToolBar()
	{
		return bottomToolBar;
	}

	public Row getExpiresRow()
	{
		return expiresRow;
	}

	public DatePicker getDatePicker()
	{
		return datePicker;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosTimeBox getTimeBox()
	{
		return timeBox;
	}

	public ObidosRowBottom2px getLanguageRow()
	{
		return languageRow;
	}

	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}

	public Button getCopyToClipboardBtton()
	{
		return copyToClipboardBtton;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public FlowPanel getRadioButtonsFlowPanel()
	{
		return radioButtonsFlowPanel;
	}
}

