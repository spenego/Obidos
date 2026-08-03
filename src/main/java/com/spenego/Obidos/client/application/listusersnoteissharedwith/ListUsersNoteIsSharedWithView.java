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

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

class ListUsersNoteIsSharedWithView extends ViewWithUiHandlers<ListUsersNoteIsSharedWithUiHandlers>
        implements ListUsersNoteIsSharedWithPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ListUsersNoteIsSharedWithView>
    {
    }
    
    @UiField
    Button helpButton;
   
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    TextBox noteNameTextBox;

    @UiField
    Summernote summerNote;
    
    @UiField
	ObidosMessageRow messageRow;


    @UiField
    Button unshareNoteButton;


    @UiField(provided = true)
    DataGrid<LimitedUserDTO> dataGrid = new DataGrid<LimitedUserDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER,GWT.create(SimplePager.Resources.class),false,0,true);

    @Inject
    ListUsersNoteIsSharedWithView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("listNotesButton")
    void onClicklistNotesButton(ClickEvent e)
    {
        getUiHandlers().listMyNotes();
    }

    @UiHandler("unshareNoteButton")
    void onClickunshareNoteButton(ClickEvent e)
    {
        getUiHandlers().unshareNote();
    }

    public DataGrid<LimitedUserDTO> getDataGrid()
    {
        return dataGrid;
    }

    public SimplePager getPager()
    {
        return pager;
    }

    public TextBox getNoteNameTextBox()
    {
        return noteNameTextBox;
    }

    public Summernote getSummerNote()
    {
        return summerNote;
    }

    public Button getUnshareNoteButton()
    {
        return unshareNoteButton;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getHelpButton()
	{
		return helpButton;
	}
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

}