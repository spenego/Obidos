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

package com.spenego.Obidos.client.application.sharewith;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;

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
import com.spenego.Obidos.client.application.widgets.ObidosRow;

class ShareWithView extends ViewWithUiHandlers<ShareWithUiHandlers> implements ShareWithPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ShareWithView>
    {
    }
    
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosPanelHeader panelHeader;
    
//    @UiField
//    Heading panelHeading;

    @UiField
    FormLabel nameLabel;

    @UiField
	ObidosMessageRow messageRow;


    @UiField
    Button shareWithUsersButton;

    @UiField
    Button shareWithGroupButton;

//    @UiField
//    Button listButton;

//    @UiField
//    ObidosButtonLeftToolBar buttonToolBar;

    @UiField
    ObidosButtonToolBar buttonToolBarBottom;
    
//    @UiField
//    Button helpButton;
    
    @UiField
    ObidosInputGroup nameInputGroup;
    
    @UiField
    ObidosInputGroup containerNameInputGroup;
    
    @UiField
    ObidosRow containerNameRow;
    
    @Inject
    ShareWithView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
        Button backButton = panelHeader.getBackButton();
                
        helpButton.addClickHandler(new ClickHandler()
        {       
                        
                @Override
                public void onClick(ClickEvent event)
                {       
                        getUiHandlers().help();
                }
        });
                
        backButton.addClickHandler(new ClickHandler()
        {
                
                @Override
                public void onClick(ClickEvent event)
                {       
                        getUiHandlers().back();
                }
        });
    }

    @UiHandler("shareWithUsersButton")
    void onClickshareWithUsersButton(ClickEvent e)
    {
        getUiHandlers().showShareWithUsersPage();
    }

    @UiHandler("shareWithGroupButton")
    void onClickshareWithGroupButton(ClickEvent e)
    {
        getUiHandlers().showShareWIthGroupsPage();
    }

//    @UiHandler("listButton")
//    void onCLickListButton(ClickEvent e)
//    {
//        getUiHandlers().list();
//
//    }
    
//  @UiHandler("helpButton")
//  void onclickHelpButton (ClickEvent e)
//  {
//  	getUiHandlers().help();
// }

//    public Heading getPanelHeading()
//    {
//        return panelHeading;
//    }

    public FormLabel getNameLabel()
    {
        return nameLabel;
    }

    public Button getShareWithUsersButton()
    {
        return shareWithUsersButton;
    }

    public Button getShareWithGroupButton()
    {
        return shareWithGroupButton;
    }

//    public Button getListButton()
//    {
//        return listButton;
//    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

// public ObidosButtonLeftToolBar getButtonToolBar()
// {
//return buttonToolBar;
//}

// public Button getHelpButton()
//{
//return helpButton;
//}

	public ObidosInputGroup getNameInputGroup()
	{
		return nameInputGroup;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}
	
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
			return panelHeader;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	public ObidosRow getContainerNameRow()
	{
		return containerNameRow;
	}
}
