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

package com.spenego.Obidos.client.application.sharecontainer;

import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ShareContainerPresenter extends Presenter<ShareContainerPresenter.MyView, ShareContainerPresenter.MyProxy>
        implements ShareContainerUiHandlers
{
    interface MyView extends View, HasUiHandlers<ShareContainerUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
    	public ObidosInputGroup getContainerNameInputGroup();
        public Button getListButton();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosMessageRow getMessageRow();
    }

    @NameToken(NameTokens.SHARE_CONTAINER)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ShareContainerPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    ShareContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        getView().getButtonToolBarBottom().adjustButtonsWidth();
        updateContainerInfo();
    }

    public void enableDisableShareButton(boolean v)
    {
    }

    private void updateContainerInfo()
    {
        Long containerId = null;

        try
        {
            containerId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.CONTAINER_ID);
        } catch (NumberFormatException |ParamNotFoundException e)
        {
            showErrorMessage("Could not find " + ObidosConstants.CONTAINER_ID + " from URL");
            return;
        }

        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                GWT.log("Could not get Container " + caught.getMessage());
            }

            @Override
            public void uponSuccess(ContainerDTO dto)
            {
                getView().getContainerNameInputGroup().setText(dto.getName());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }
    
    private void showShareWith(String nameToken, String shareType)
    {
    	Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
    	if (containerId == null)
    	{
    		showErrorMessage("Could not get Container ID from URL");
    		return;
    	}
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        with.put(ObidosConstants.SHARE_TYPE, shareType);
        ClientUtils.addShare(placeManager, with);
        ClientUtils.addPlace(placeManager, with);
        ClientUtils.showPage(placeManager, nameToken, with);
    }


    @Override
    public void shareContainerWithUsers()
    {
        String nameToken = NameTokens.SHARE_CONTAINER_WITH_USERS;
        showShareWith(nameToken,ObidosConstants.SHARE_WITH_USERS);
    }

    @Override
    public void shareContainerWithGroup()
    {
    	String nameToken = NameTokens.SHARE_CONTAINER_WITH_GROUP;
        showShareWith(nameToken,ObidosConstants.SHARE_WITH_GROUPS);
    }

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}


}
