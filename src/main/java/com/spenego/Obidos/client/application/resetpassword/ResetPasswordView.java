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

package com.spenego.Obidos.client.application.resetpassword;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.util.ClientUtils;

class ResetPasswordView extends ViewWithUiHandlers<ResetPasswordUiHandlers> implements ResetPasswordPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ResetPasswordView>
    {
    }

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    ObidosPasswordBox passwordBox;

    @UiField
    Button showHidePasswordButton;

    @UiField
    ObidosPasswordBox confirmPasswordBox;

    @UiField
    Button resetButton;

    @UiField
    Button loginButton;

    @UiField
    Label ncharsLabel;
    
    @UiField
    FlowPanel require2faFp;
    
    @UiField
    TextBox twofaCodeBox;

    @UiField
	ObidosMessageRow messageRow;
    
    @UiField
	ObidosButtonToolBar buttonToolBar;
    
    @UiField
    Button showHideConfirmPasswordButton;
    
    @UiField
    ObidosRowBottom2px twofaRow;
    
    @UiField
    ListGroup passwordRequirementListGroup;

    @Inject
    ResetPasswordView(Binder uiBinder)
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

    @UiHandler("showHidePasswordButton")
    void onClickshowHidePasswordButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHidePasswordButton, passwordBox);
    }
    
    @UiHandler("showHideConfirmPasswordButton")
    void onclickshowHideConfirmPasswordButton (ClickEvent e)
	{
        ClientUtils.toggleEyeIcon(showHideConfirmPasswordButton, confirmPasswordBox);
	}

    @UiHandler("passwordBox")
    void onCkeyUpPasswordBox(KeyUpEvent e)
    {
        getUiHandlers().showNumberOfCharactersInPassword();
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    	    confirmPasswordBox.setFocus(true);
    	}
    }

    @UiHandler("resetButton")
    void onClickResetButton(ClickEvent e)
    {
        getUiHandlers().resetPassword();
    }

    @UiHandler("confirmPasswordBox")
    void onPressEnter(KeyUpEvent e)
    {
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    	    getUiHandlers().resetPassword();
    	}
    }

    @UiHandler("loginButton")
    void onClickLoginButton(ClickEvent e)
    {
        getUiHandlers().showLoginPage();
    }
    
    public ObidosPasswordBox getPasswordBox()
    {
        return passwordBox;
    }


    public Button getShowHidePasswordButton()
    {
        return showHidePasswordButton;
    }


    public ObidosPasswordBox getConfirmPasswordBox()
    {
        return confirmPasswordBox;
    }

    

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

    public Button getResetButton()
    {
        return resetButton;
    }

    public Button getLoginButton()
    {
        return loginButton;
    }

    public Label getNcharsLabel()
    {
        return ncharsLabel;
    }

	public FlowPanel getRequire2faFp()
	{
		return require2faFp;
	}

	public TextBox getTwofaCodeBox()
	{
		return twofaCodeBox;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosButtonToolBar getButtonToolBar()
	{
		return buttonToolBar;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Button getShowHideConfirmPasswordButton()
	{
		return showHideConfirmPasswordButton;
	}

	public ObidosRowBottom2px getTwofaRow()
	{
		return twofaRow;
	}

	public ListGroup getPasswordRequirementListGroup()
	{
		return passwordRequirementListGroup;
	}
}