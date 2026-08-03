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

package com.spenego.Obidos.client.application.passwordchanged;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

class PasswordChangedView extends ViewWithUiHandlers<PasswordChangedUiHandlers>
        implements PasswordChangedPresenter.MyView
{
	@UiField
	Heading panelHeading;
	
	@UiField
	Button loginButton;
	
    interface Binder extends UiBinder<Widget, PasswordChangedView>
    {
    }

    @Inject
    PasswordChangedView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("loginButton")
    void onClickLoginButton(ClickEvent e)
    {
        getUiHandlers().logoutAndShowLoginPage();

    }

	public Heading getPanelHeading()
	{
		return panelHeading;
	}

	public Button getLoginButton()
	{
		return loginButton;
	}
}