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

import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.util.ClientUtils;

public class ResetPasswordRequestPresenter
        extends Presenter<ResetPasswordRequestPresenter.MyView, ResetPasswordRequestPresenter.MyProxy>
        implements ResetPasswordRequestUiHandlers
{
    interface MyView extends View, HasUiHandlers<ResetPasswordRequestUiHandlers>
    {
    	public Button getSendEmailButton();
        public ObidosTextBox getUsernameTextBox();
        public ObidosMessageRow getMessageRow();

    }

    @NameToken(NameTokens.RESET_PASSWORD_REQUEST)
    @ProxyCodeSplit
    @NoGatekeeper
    interface MyProxy extends ProxyPlace<ResetPasswordRequestPresenter>
    {
    }

    private final PlaceManager placeManager;

    @Inject
    ResetPasswordRequestPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager)
    {
        super(eventBus, view, proxy, RevealType.RootLayout);

        this.placeManager = placeManager;

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
        showMessage(null);
        getView().getUsernameTextBox().clear();
        getView().getSendEmailButton().setEnabled(true);
        ClientUtils.focusToWidegt(getView().getUsernameTextBox());
    }

    @Override
    public void sendResetPasswordRequestEmail()
    {
        String username = getView().getUsernameTextBox().getValue();
        if (username == null || username.length() == 0)
        {
            showErrrorMessage(ObidosMessages.LANG.resetPasswordError());
            return;
        }

        AsyncCallback<Void> callback = new AsyncCallback<Void>()
        {

            @Override
            public void onFailure(Throwable t)
            {
                showErrrorMessage("Error: " + t.getMessage());
            }

            @Override
            public void onSuccess(Void arg0)
            {
                String message = ObidosMessages.LANG.resetPasswordAnEmailIsSent();
                showMessage(message);
                gwtLog("disable button");
                getView().getSendEmailButton().setEnabled(false);
            }
        };
        // Email address is no longer unique, so figure out email address
        // by username. Jul-14-2022
        UserService.Utility.getInstance().sendPasswordResetEmail(username, callback);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }
    
    private void gwtLog(String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

	@Override
	public void showLoginPage()
	{
		ClientUtils.showPage(placeManager, NameTokens.LOGIN);
	}
}