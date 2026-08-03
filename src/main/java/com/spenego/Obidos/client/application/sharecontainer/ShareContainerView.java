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

package com.spenego.Obidos.client.application.sharecontainer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;

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

class ShareContainerView extends ViewWithUiHandlers<ShareContainerUiHandlers> implements ShareContainerPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ShareContainerView>
    {
    }
    
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Button listButton;
    
    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    ObidosButtonToolBar buttonToolBarBottom;
    
    @UiField
    ObidosInputGroup containerNameInputGroup;
    
    @UiField
	ObidosMessageRow messageRow;


    @Inject
    ShareContainerView(Binder uiBinder)
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
					getUiHandlers().help();
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

    @UiHandler("shareWithUsersButton")
    void onClickshareWithUsersButton(ClickEvent e)
    {
        getUiHandlers().shareContainerWithUsers();
    }

    @UiHandler("shareWithGroupButton")
    void onClickshareWithGroupButton(ClickEvent e)
    {
        getUiHandlers().shareContainerWithGroup();
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosInputGroup getContainerNameInputGroup()
	{
		return containerNameInputGroup;
	}

	public Button getListButton()
	{
		return listButton;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}


}