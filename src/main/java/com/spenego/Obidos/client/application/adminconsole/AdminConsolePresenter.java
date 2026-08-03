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

package com.spenego.Obidos.client.application.adminconsole;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;

public class AdminConsolePresenter extends Presenter<AdminConsolePresenter.MyView, AdminConsolePresenter.MyProxy>
        implements AdminConsoleUiHandlers
{
    interface MyView extends View, HasUiHandlers<AdminConsoleUiHandlers>
    {
    	public ObidosPanelHeader getPanelHeader();
    }

    @NameToken(NameTokens.ADMIN_CONSOLE)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<AdminConsolePresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    AdminConsolePresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

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
        ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
        // update rpc time
        ClientUtils.updateRPCTime("AdminConsole");
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }
}