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

package com.spenego.Obidos.client.application.resetpasswordrequest;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;

class ResetPasswordRequestView extends ViewWithUiHandlers<ResetPasswordRequestUiHandlers>
        implements ResetPasswordRequestPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ResetPasswordRequestView>
    {
    }

    @UiField
    ObidosTextBox usernameTextBox;
    
    @UiField
    Button sendEmailButton;

    @UiField
    Button cancelButton;
    
    @UiField
	ObidosMessageRow messageRow;
    
    @UiField
    ObidosPanelHeader panelHeader;
    
    @Inject
    ResetPasswordRequestView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        panelHeader.getHelpButton().setVisible(false);
    }

    @UiHandler("sendEmailButton")
    void onClicksendEmailButton(ClickEvent e)
    {
        getUiHandlers().sendResetPasswordRequestEmail();
    }

    @UiHandler("usernameTextBox")
    void onKeyUpUsernameTextBox(KeyDownEvent e)
    {
        if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
        {
            getUiHandlers().sendResetPasswordRequestEmail();
        }
    }
    
    @UiHandler("cancelButton")
    void onclickCancelButton (ClickEvent e)
	{
    	getUiHandlers().showLoginPage();
	}

	public Button getSendEmailButton()
	{
		return sendEmailButton;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Button getCancelButton()
	{
		return cancelButton;
	}

	public ObidosTextBox getUsernameTextBox()
	{
		return usernameTextBox;
	}
}