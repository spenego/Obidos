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

package com.spenego.Obidos.client.application.home;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.place.NameTokens;

/**
 * @author spgdev@spenego.com - Jan 3, 2017
 */
public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy>
{
	interface MyView extends View
	{
	}

	@ProxyStandard
	@NameToken(NameTokens.HOME)
	interface MyProxy extends ProxyPlace<HomePresenter>
	{
	}

	@Inject
	HomePresenter(EventBus eventBus, MyView view, MyProxy proxy)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
	}

	@Override
	protected void onReveal()
	{
		super.onReveal();
	}
}
