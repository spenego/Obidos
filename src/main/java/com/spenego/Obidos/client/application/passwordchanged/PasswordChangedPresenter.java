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

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;

public class PasswordChangedPresenter
        extends Presenter<PasswordChangedPresenter.MyView, PasswordChangedPresenter.MyProxy>
        implements PasswordChangedUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    interface MyView extends View, HasUiHandlers<PasswordChangedUiHandlers>
    {
    	public Heading getPanelHeading();
    	public Button getLoginButton();
    }
//    @UseGatekeeper(LoggedInGatekeeper.class) // Issue #539

    @NameToken(NameTokens.PASSWORD_CHANGED)
    @ProxyCodeSplit
    @NoGatekeeper
    interface MyProxy extends ProxyPlace<PasswordChangedPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    PasswordChangedPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, RevealType.RootLayout);

        this.placeManager = placeManager;
        this.currentUser = currentUser;

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        updatePanel();
        checkPasswordJustChanged();
        focusLoginButton();
    }
    
    private void focusLoginButton()
    {
    	ClientUtils.focusToButton(getView().getLoginButton());
    }
    
    private void updatePanel()
    {
    	String p = ClientUtils.getPasswordIsResetFromUrl(placeManager);
    	if (glang.yes().equals(p))
    	{
    		getView().getPanelHeading().setText(glang.passwordResetPanelHeading());
    	}
    }
    
    // go back to the view the user came from  if the user just didn't change 
    // the initial password. Just a stupid hack!
    private void checkPasswordJustChanged()
    {
    	gwtLog("Check if password just changed...");
    	if (currentUser != null)
    	{
    		gwtLog("Current user is not null");
    		if (!currentUser.isPasswordJustChanged())
    		{
    			ClientUtils.back(placeManager);
    		}
    	}
    }

    @Override
    public void logoutAndShowLoginPage()
    {
        String message = null; // Bug #147. Do not show the message
        if (currentUser != null)
        {
        	ClientUtils.logout(this, message);
        }
        else
        {
        	ClientUtils.showPage(placeManager, NameTokens.LOGIN);
        }
    }
    
    private void gwtLog(final String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }
}