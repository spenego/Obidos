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

package com.spenego.Obidos.client.application.userconsole;

import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosRowTop2px;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.util.ClientUtils;

public class UserConsolePresenter extends Presenter<UserConsolePresenter.MyView, UserConsolePresenter.MyProxy>
		implements UserConsoleUiHandlers
{
	interface MyView extends View, HasUiHandlers<UserConsoleUiHandlers>
	{
		public ObidosRowTop2px getChangeLanguageRow();
		public Select getLanguageChangeSelect();
		public PanelBody getPanelBody();
	}

	@NameToken(NameTokens.USER_CONSOLE)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<UserConsolePresenter>
	{
	}

	@Inject
	UserConsolePresenter(EventBus eventBus, MyView view, MyProxy proxy)
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
		// show language change selection only if easter egg is set
		// set privacy timer to 502 from profile page to activate the
		// easter egg
		ClientUtils.showLanguageSwitchSelect(getView().getChangeLanguageRow(),
				getView().getLanguageChangeSelect(),
				getView().getPanelBody());
	}

	@Override
	public void languageChangeCallbackHandler()
	{
		Select s = getView().getLanguageChangeSelect();
		String l = s.getSelectedItem().getValue();
		gwtLog("MMM lang: " + l);
		switch(l)
		{
			case "English":
			{
				ClientUtils.updateURLAndRefresh("en");
				break;
			}

			case "Arabic":
			{
				ClientUtils.updateURLAndRefresh("ar");
				break;
			}

			case "Bangla":
			{
				ClientUtils.updateURLAndRefresh("bn");
				break;
			}
			
			case "German":
			{
				ClientUtils.updateURLAndRefresh("de");
				break;
			}
			case "Hindi":
			{
				ClientUtils.updateURLAndRefresh("hi");
				break;
			}
			case "Japanese":
			{
				ClientUtils.updateURLAndRefresh("ja");
				break;
			}

			case "Malayalam":
			{
				ClientUtils.updateURLAndRefresh("ml");
				break;
			}

		}
	}
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

}