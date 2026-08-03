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

package com.spenego.Obidos.client.application.sessioninfo;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class SessionInfoView extends ViewWithUiHandlers<SessionInfoUiHandlers> implements SessionInfoPresenter.MyView
{
    interface Binder extends UiBinder<Widget, SessionInfoView>
    {
    }

    @UiField
    TextBox sessionIdTextBox;

    @UiField
    TextBox lastAccessTextBox;

    @UiField
    TextBox expiresTextBox;

    @UiField
    TextBox createdTextBox;

    @UiField
    TextBox maxInactiveTextBox;

    @UiField
    TextBox nowTextBox;

    @UiField
    TextBox dialogPopsTextBox;

    @UiField
    TextBox sessionCheckTimerTextBox;

    @UiField
    TextBox graceTimerTextBox;
    
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
    ObidosPanelHeader panelHeader;
    
    @Inject
    SessionInfoView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
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

    }

    public TextBox getSessionIdTextBox()
    {
        return sessionIdTextBox;
    }

    public TextBox getLastAccessTextBox()
    {
        return lastAccessTextBox;
    }

    public TextBox getExpiresTextBox()
    {
        return expiresTextBox;
    }

    public TextBox getCreatedTextBox()
    {
        return createdTextBox;
    }

    public TextBox getMaxInactiveTextBox()
    {
        return maxInactiveTextBox;
    }

    public TextBox getNowTextBox()
    {
        return nowTextBox;
    }

    public TextBox getDialogPopsTextBox()
    {
        return dialogPopsTextBox;
    }

    public TextBox getSessionCheckTimerTextBox()
    {
        return sessionCheckTimerTextBox;
    }

    public TextBox getGraceTimerTextBox()
    {
        return graceTimerTextBox;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}
}
