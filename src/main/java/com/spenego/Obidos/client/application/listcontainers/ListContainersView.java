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

package com.spenego.Obidos.client.application.listcontainers;


import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.ContainerDTO;

class ListContainersView extends ViewWithUiHandlers<ListContainersUiHandlers> implements ListContainersPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ListContainersView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    TextBox searchTextBox;

    @UiField
    Button searchButton;
    
    @UiField
	ObidosMessageRow messageRow;


    @UiField
    Button deleteButton;
    
    @UiField
    Button clearButton;

   	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;

    @UiField(provided = true)
    DataGrid<ContainerDTO> dataGrid = new DataGrid<ContainerDTO>(ObidosConstants.VISIBLE_GRID_COUNT);

    @UiField(provided = true)
    SimplePager pager = new SimplePager(TextLocation.CENTER, GWT.create(SimplePager.Resources.class), false, 0, true);

    @Inject
    ListContainersView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        panelHeader.getHelpButton().addClickHandler(new ClickHandler()
                {

                        @Override
                        public void onClick(ClickEvent event)
                        {
                                getUiHandlers().help();
                        }
                });
    }

    @UiHandler("searchTextBox")
    void onPreseeEnterSearchTextBox(KeyDownEvent e)
    {
        if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().searchContainerName();
        }
    }

    @UiHandler("sortByDate")
    void onSelectsortByDateAnchorList(ClickEvent e)
    {
        getUiHandlers().sortByDate();
    }

    @UiHandler("sortReverseByDate")
    void onSelectsortReverseByDateAnchorList(ClickEvent e)
    {
        getUiHandlers().sortReverseByDate();
    }

    @UiHandler("sortByAZ")
    void onSelectsortByAZAnchorList(ClickEvent e)
    {
        getUiHandlers().sortByAZ();
    }
   
    @UiHandler("sortByZA")
    void onclickSortByZA (ClickEvent e)
    {
        getUiHandlers().sortByZA();
    }

    @UiHandler("searchButton")
    void onClickSearchButton(ClickEvent e)
    {
        getUiHandlers().searchContainerName();
    }

//    @UiHandler("helpButton")
//    void onclickHelpButton (ClickEvent e)
// {
//  	getUiHandlers().showHideHelp();
// }

    @UiHandler("deleteButton")
    void onclickDeleteButton (ClickEvent e)
	{
    	getUiHandlers().deleteContainers();
	}
    
    @UiHandler("clearButton")
    void onclickClearButton (ClickEvent e)
	{
    	getUiHandlers().clearSelections();
	}

    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }


    // not used
    static class BadgeCell extends AbstractCell<String>
    {
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb)
        {
            if (value == null)
            {
                return;
            }

            ContainerDTO containerDTO = (ContainerDTO) context.getKey();
            if (containerDTO == null)
            {
                return;
            }

            SafeHtml safeHtml = null;
            safeHtml = SafeHtmlUtils.fromTrustedString("<div>" + value + "&nbsp;" + "<span class=\"badge\">10</span></div>");
            sb.append(safeHtml);
        }
    }

    public DataGrid<ContainerDTO> getDataGrid()
    {
        return dataGrid;
    }

    public SimplePager getPager()
    {
        return pager;
    }

    public TextBox getSearchTextBox()
    {
        return searchTextBox;
    }

    public Button getSearchButton()
    {
        return searchButton;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getDeleteButton()
	{
		return deleteButton;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosRowBottom2px getLanguageRow()
	{
		return languageRow;
	}

	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}
}
