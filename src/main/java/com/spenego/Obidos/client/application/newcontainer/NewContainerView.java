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

package com.spenego.Obidos.client.application.newcontainer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.ListBox;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;

/**
 * @author spgdev@spenego.com - Jul 25, 2017
 *
 */
class NewContainerView extends ViewWithUiHandlers<NewContainerUiHandlers> implements NewContainerPresenter.MyView
{
    interface Binder extends UiBinder<Widget, NewContainerView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosInputGroup containerNameInputGroup;

    @UiField
    Button saveButton;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    InlineRadio publicContainerRadio;

    @UiField
    InlineRadio privateContainerRadio;
    
    @UiField
    ObidosButtonToolBar buttonToolBarBottom;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
    Button grantPermissionsButton;

    @UiField
    Button sharedWithUsersButton;
    
	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;


    @UiHandler("saveButton")
    void onClickSaveButton(ClickEvent e)
    {
        getUiHandlers().saveContainer();
    }

    @Inject
    NewContainerView(Binder uiBinder)
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
					getUiHandlers().showHelp();
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
					backButton.setText(ObidosMessages.LANG.listContainers());
					getUiHandlers().back();
				}
			});
		}
    }
    
    @UiHandler("publicContainerRadio")
    void onclickPublicRadio (ClickEvent e)
	{
    	getUiHandlers().radioButtonClickHandler();
	}
    
    @UiHandler("privateContainerRadio")
    void onclickPrivateRadio (ClickEvent e)
	{
    	getUiHandlers().radioButtonClickHandler();
	}
    
    @UiHandler("grantPermissionsButton")
    void onClickgrantPermissionsButton(ClickEvent e)
    {
    	getUiHandlers().showGrantPermissionsPage();
    }
    
    @UiHandler("sharedWithUsersButton")
    void onClicksharedWithUsersButton(ClickEvent e)
    {
    	getUiHandlers().showListOfUsersTheContainerIsSharedWith();
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

    public InlineRadio getPublicContainerRadio()
    {
        return publicContainerRadio;
    }

    public InlineRadio getPrivateContainerRadio()
    {
        return privateContainerRadio;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}
	
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Button getGrantPermissionsButton()
	{
		return grantPermissionsButton;
	}

	public Button getSharedWithUsersButton()
	{
		return sharedWithUsersButton;
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