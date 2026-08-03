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

package com.spenego.Obidos.client.application.newgroup;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosResetButton;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;

class NewGroupView extends ViewWithUiHandlers<NewGroupUiHandlers> implements NewGroupPresenter.MyView
{
    interface Binder extends UiBinder<Widget, NewGroupView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    FormLabel groupNameLabel;

    @UiField
    FormLabel groupCommentLabel;

    @UiField
    ObidosInputGroup groupNameInputGroup;

    @UiField
    ObidosInputGroup groupCommentInputGroup;

    @UiField
    Button saveButton;

    @UiField
    ObidosResetButton resetButton;

    @UiField
    ObidosMessageRow messageRow;

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    ObidosButtonToolBar bottomToolBar;
    
	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;


    @Inject
    NewGroupView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        groupNameInputGroup.getTextBox().addKeyUpHandler(new KeyUpHandler()
		{
			
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				getUiHandlers().showGroupNameChange();
			}
		});
        
        groupCommentInputGroup.getTextBox().addKeyUpHandler(new KeyUpHandler()
		{
			
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				getUiHandlers().showGroupCommentChange();
			}
		});

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
					getUiHandlers().back();
				}
			});
		}
    }

    @UiHandler("saveButton")
    void onClicksaveButton(ClickEvent e)
    {
        getUiHandlers().createUpdateGroup();
    }

    @UiHandler("resetButton")
    void onClickfetchResetButton(ClickEvent e)
    {
        getUiHandlers().reset();
    }

    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }

    public Button getSaveButton()
    {
        return saveButton;
    }

    public FormLabel getGroupNameLabel()
    {
        return groupNameLabel;
    }

    public ObidosResetButton getResetButton()
	{
		return resetButton;
	}

	public FormLabel getGroupCommentLabel()
    {
        return groupCommentLabel;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getBottomToolBar()
	{
		return bottomToolBar;
	}

	public ObidosInputGroup getGroupNameInputGroup()
	{
		return groupNameInputGroup;
	}

	public ObidosPanelHeader getPanelHeader()
	{
			return panelHeader;
	}

	public ObidosInputGroup getGroupCommentInputGroup()
	{
		return groupCommentInputGroup;
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
