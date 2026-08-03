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

package com.spenego.Obidos.client.application.obidoserrorpage;

import org.gwtbootstrap3.client.ui.PanelHeader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
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
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;

public class ObidosErrorPagePresenter
		extends Presenter<ObidosErrorPagePresenter.MyView, ObidosErrorPagePresenter.MyProxy>
		implements ObidosErrorPageUiHandlers
{
	interface MyView extends View, HasUiHandlers<ObidosErrorPageUiHandlers>
	{
		public PanelHeader getPanelHeader();
	    public HTML getInfoHtml();
	}

	@NameToken(NameTokens.OBIDOS_ERROR_PAGE)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<ObidosErrorPagePresenter>
	{
	}

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;
	@Inject
	ObidosErrorPagePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		show404Error();
	}
	
	private void show404Error()
	{
		ObidosMessages lang = ObidosMessages.LANG;
		HTML html = getView().getInfoHtml();
		html.setHTML(lang.pageNotFoundMessage());
	}
}