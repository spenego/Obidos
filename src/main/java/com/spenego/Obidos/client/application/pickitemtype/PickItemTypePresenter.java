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

package com.spenego.Obidos.client.application.pickitemtype;

import java.awt.ContainerOrderFocusTraversalPolicy;
import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineRadio;

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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRowWithStyle;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class PickItemTypePresenter extends Presenter<PickItemTypePresenter.MyView, PickItemTypePresenter.MyProxy>
        implements PickItemTypeUiHandlers
{
    interface MyView extends View, HasUiHandlers<PickItemTypeUiHandlers>
    {
        public InlineRadio getAsNoteRadio();
        public InlineRadio getFreeFormatRadio();
        public InlineRadio getUseGlobalTemplateRadio();
        public InlineRadio getUsePersonalTemplateRado();
        public ObidosMessageRowWithStyle getMessageRow();
        public Button getPickButton();
        public BlockQuote getHelpBlockQuote();
        public Button getListButton();
        public ObidosButtonToolBar getButtonToolBarBottom();
    }

    @NameToken(NameTokens.PICK_ITEM_TYPE)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<PickItemTypePresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;

    @Inject
    PickItemTypePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        
        resetView();
    }

    private void resetView()
    {
        showMessage("");
        resetRadioButtons();
        updateListButtonTitle();
    }

    private void updateListButtonTitle()
    {
    	ClientUtils.updateListButtonTitle(placeManager, getView().getListButton());
    }

    private void resetRadioButtons()
    {
        getView().getAsNoteRadio().setValue(false);
        getView().getFreeFormatRadio().setValue(false);
        getView().getUseGlobalTemplateRadio().setValue(false);
        getView().getUsePersonalTemplateRado().setValue(false);
    }
    @Override
    public void help()
    {
    	getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    private void navigateToAddNoteToContainerView()
    {
        gwtLog("Go to add note to Container page");
        String containerIdString = getContainerIdStringFromUrl();
        if (containerIdString == null)
        {
            return;
        }
        String containerOwnerId = getContainerOwnerIdFromUrl();
        gwtLog("XXX container owner id: " + containerOwnerId);
        /*
        if (containerOwnerId == null)
        {
            gwtLog("MMM could not get container owner id from URL");
            return;
        }
        */

        // same as selecting Notes from Global template
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerIdString);
        if (containerOwnerId != null)
        {
            with.put(ObidosConstants.OWNERID, containerOwnerId);
        }

        with.put(ObidosConstants.ACTION, ObidosConstants.ADD);
        with.put(ObidosConstants.TEMPLATE_TYPE, ObidosConstants.GLOBAL);
        with.put(ObidosConstants.TEMPLATE_ID, Long.toString(UserDefinedTypeDTO.NOTES_ID));
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        if (place != null)
        {
        	with.put(ObidosConstants.PLACE, place);
        }
        String nameToken = NameTokens.ADD_ITEM_TO_CONTAINER;
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    @Override
    public void navigateToAddFreeFormatItemToContainerView()
    {
        String containerIdString = getContainerIdStringFromUrl();
        if (containerIdString == null)
        {
            return;
        }
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerIdString);
        String nameToken = NameTokens.ADD_FREE_FORMAT_ITEM_TO_CONTAINER;
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        if (place != null)
        {
        	with.put(ObidosConstants.PLACE, place);
        }

        ClientUtils.showPage(placeManager, nameToken, with);
    }


    @Override
    public void navigateToSelectGlobalTemplate()
    {
        String containerId = getContainerIdStringFromUrl();
        if (containerId == null)
        {
            return;
        }
        String containerOwnerId = getContainerOwnerIdFromUrl();
        /*
        if (containerOwnerId == null)
        {
            gwtLog("MMM could not get container owner id from URL");
            return;
        }
        */

        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerId);
        if (containerOwnerId != null)
        {
            with.put(ObidosConstants.OWNERID, containerOwnerId);
        }
        with.put(ObidosConstants.ACTION, ObidosConstants.ADD);
        with.put(ObidosConstants.TEMPLATE_TYPE, ObidosConstants.GLOBAL);
        with.put(ObidosConstants.TEMPLATE_ID, Long.toString(UserDefinedTypeDTO.NOTES_ID));

        String nameToken = NameTokens.SELECT_TEMPLATE;
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        if (place != null)
        {
        	with.put(ObidosConstants.PLACE, place);
        }

        ClientUtils.showPage(placeManager, nameToken, with);

    }

    private String getContainerIdStringFromUrl()
    {
        String containerIdString = null;
        String key = ObidosConstants.CONTAINER_ID;
        try
        {
            containerIdString = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " in URL");
            return null;
        }
        return containerIdString;
    }
    
    private String getContainerOwnerIdFromUrl()
    {
        String containerOwnerId = null;
        String key = ObidosConstants.OWNERID;
        try
        {
            containerOwnerId = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " in URL");
            return null;
        }
        return containerOwnerId;
    }

    @Override
    public void navigateToSelectPersonalTemplate()
    {
        String containerId = getContainerIdStringFromUrl();
        if (containerId == null)
        {
            return;
        }
        String containerOwnerId = getContainerOwnerIdFromUrl();
        /*
        if (containerOwnerId == null)
        {
            gwtLog("MMM could not get container owner id from URL");
            return;
        }
        */
        gwtLog("YYY Container owner id: " + containerOwnerId);

        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_TYPE, ObidosConstants.PERSONAL);
        with.put(ObidosConstants.CONTAINER_ID, containerId);
        if (containerOwnerId != null)
        {
            with.put(ObidosConstants.OWNERID, containerOwnerId);
        }
        String nameToken = NameTokens.SELECT_TEMPLATE;
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        if (place != null)
        {
        	with.put(ObidosConstants.PLACE, place);
        }
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    			
    }

    private void setPickButtonTitle(String title)
    {
//        getView().getPickButton().setText(title);
    }

    @Override
    public void navigateToPage()
    {
        boolean noter = getView().getAsNoteRadio().getValue();
        boolean ffr = getView().getFreeFormatRadio().getValue();
        boolean ugtr = getView().getUseGlobalTemplateRadio().getValue();
        boolean uptr = getView().getUsePersonalTemplateRado().getValue();

        boolean r = noter | ffr | ugtr | uptr;
        gwtLog("Radio status: " + r);
        gwtLog("noter: " + noter);
        gwtLog("ffr: " + ffr);
        gwtLog("ugtr: " + ugtr);
        gwtLog("uptr: " + uptr);

        showMessage("");
        if (!r)
        {
            showErrorMessage(ObidosMessages.LANG.selectItemType());
            return;
        }
        if (noter)
        {
            navigateToAddNoteToContainerView();
        }
        else if (ffr)
        {
            gwtLog("Navigate to add free format Item to Container view");
            navigateToAddFreeFormatItemToContainerView();
        }
        else if (ugtr)
        {
            gwtLog("Navigate to select global template");
            navigateToSelectGlobalTemplate();
        }
        else if (uptr)
        {
            gwtLog("Navigate to select personal template");
            navigateToSelectPersonalTemplate();
        }
    }

    @Override
    public void userPersonalTemplateRadioButtonClicked()
    {
        setPickButtonTitle("Go to Pick Personal Template Page");
        showMessage("");
    }

    @Override
    public void userGlobalTemplateRadioButtonClicked()
    {
        setPickButtonTitle("Go to Pick Global Template Page");
        showMessage("");
    }

    @Override
    public void userFreeFormatRadioButtonClicked()
    {
        setPickButtonTitle("Go to Add Item to Container Page");
        showMessage("");
    }

    @Override
    public void userNoteRadioButtonClicked()
    {
        setPickButtonTitle("Go to Add Note to Container Page");
        showMessage("");
    }

	@Override
	public void navigateToLastPage()
	{
		ClientUtils.navigateToPlace(placeManager, NameTokens.LIST_CONTAINERS);
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

}
