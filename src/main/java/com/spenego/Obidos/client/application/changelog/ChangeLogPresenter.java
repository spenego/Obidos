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

package com.spenego.Obidos.client.application.changelog;

import org.gwtbootstrap3.client.ui.ModalSize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPreModal;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.BuildInfoService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.dto.BuildInfoDTO;

public class ChangeLogPresenter extends Presenter<ChangeLogPresenter.MyView, ChangeLogPresenter.MyProxy>
		implements ChangeLogUiHandlers
{
	interface MyView extends View, HasUiHandlers<ChangeLogUiHandlers>
	{
		public HTML getChangeLogHtml();
		public ObidosPanelHeader getPanelHeader();
		public ObidosMessageRow getMessageRow();
	}

	@NameToken(NameTokens.CHANGELOG)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<ChangeLogPresenter>
	{
	}

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;
	@Inject
	ChangeLogPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		showMessage(null);
		ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
		showChangeLog();
	}
	
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}
	
	private void showChangeLog()
	{
        GwtAsyncWrapper<BuildInfoDTO> callback = new GwtAsyncWrapper<BuildInfoDTO>(this)
        {

            @Override
            public void uponFailure(Throwable e )
            {
                showErrorMessage(e.getMessage());
            }

            @Override
            public void uponSuccess(BuildInfoDTO dto)
            {
            	getView().getChangeLogHtml().setHTML(dto.getChangeLog());
            }
        };
        GWT.log("Calling build info service...");
        BuildInfoService.Utility.getInstance().getChangeLog(callback);
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

}