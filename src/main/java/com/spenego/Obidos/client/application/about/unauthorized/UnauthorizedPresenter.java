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

package com.spenego.Obidos.client.application.about.unauthorized;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.place.NameTokens;

public class UnauthorizedPresenter extends Presenter<UnauthorizedPresenter.MyView, UnauthorizedPresenter.MyProxy>
        implements UnauthorizedUiHandlers
{
    interface MyView extends View, HasUiHandlers<UnauthorizedUiHandlers>
    {
    }

    /*
    @ContentSlot
    public static final Type<RevealContentHandler<?>> SLOT_Unauthorized = new Type<RevealContentHandler<?>>();
    */

    @NameToken(NameTokens.UNAUTHORIZED)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<UnauthorizedPresenter>
    {
    }

    @Inject
    UnauthorizedPresenter(EventBus eventBus, MyView view, MyProxy proxy)
    {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

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
    }
}