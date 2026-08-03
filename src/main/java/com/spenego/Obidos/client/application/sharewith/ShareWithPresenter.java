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

package com.spenego.Obidos.client.application.sharewith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;

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
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ShareWithPresenter extends Presenter<ShareWithPresenter.MyView, ShareWithPresenter.MyProxy>
        implements ShareWithUiHandlers
{
	private static boolean buttonsAdjusted = false;

    interface MyView extends View, HasUiHandlers<ShareWithUiHandlers>
    {
        public FormLabel getNameLabel();
        public Button getShareWithUsersButton();
        public Button getShareWithGroupButton();
        public BlockQuote getHelpBlockQuote();
        public ObidosInputGroup getNameInputGroup();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
        public ObidosInputGroup getContainerNameInputGroup();
        public ObidosRow getContainerNameRow();
    }

    @NameToken(NameTokens.SHARE_WITH)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ShareWithPresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;

    @Inject
    ShareWithPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        adjustButtonsWidth();
        enableButtons(true);
        updateForm();
    }

    // in this screen, share with buttons are wide, match the rest with them
    private void adjustButtonsWidth()
    {
    	if (!buttonsAdjusted)
    	{
    		getView().getButtonToolBarBottom().adjustButtonsWidth();
    		buttonsAdjusted = true;
    	}
    }


    private void enableButtons(boolean enabled)
    {
        getView().getShareWithUsersButton().setEnabled(enabled);
        getView().getShareWithGroupButton().setEnabled(enabled);
    }

    private void updateForm()
    {
        String share = null;
        String key = ObidosConstants.SHARE;

        try
        {
            share = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " from URL");
            enableButtons(false);
            return;
        }
        if (share.equals(ObidosConstants.ITEM))
        {
            updateNameLabel(ObidosMessages.LANG.itemName());
            updatePanelHeader(ObidosMessages.LANG.shareItem());
            supdateShareWithUsersButtonTitle(ObidosMessages.LANG.shareItemWithUsers());
            supdateShareWithGroupButtonTitle(ObidosMessages.LANG.shareItemWithGroups());
            if (getConterIdFromUrl() != null)
            {
                updateListButtonTitle(ObidosMessages.LANG.listItemsInContainer());
            }
            else
            {
//                updateListButtonTitle(ObidosMessages.LANG.listMyItems());
                updateListButtonTitle(ObidosMessages.LANG.listItems());
            }
            fetchAndUpdateTextBoxWithItemName();
        }
        else if (share.equals(ObidosConstants.NOTE))
        {
            updateNameLabel(ObidosMessages.LANG.noteName());
            updatePanelHeader(ObidosMessages.LANG.shareNote());
            supdateShareWithUsersButtonTitle(ObidosMessages.LANG.shareNoteWithUsers());
            supdateShareWithGroupButtonTitle(ObidosMessages.LANG.shareNoteWithGroups());
//            updateListButtonTitle(ObidosMessages.LANG.listMyNotes());
            updateListButtonTitle(ObidosMessages.LANG.listNotes());
            fetchAndUpdateTextBoxWithItemName();
        }
        else if (share.equals(ObidosConstants.CONTAINER))
        {
            updatePanelHeader(ObidosMessages.LANG.shareContainer());

        }
        else
        {
            showErrorMessage("Unknown " + key + " type");
            enableButtons(false);
            return;
        }
    }

    private void updateNameLabel(String text)
    {
        getView().getNameLabel().setText(text);
    }

    private void fetchAndUpdateTextBoxWithItemName()
    {
        String key = ObidosConstants.ITEM_ID;
        Long itemId = null;
        Long containerId = null;

        try
        {
            itemId = ClientUtils.getIdFromUrl(placeManager, key);
        } catch (NumberFormatException |ParamNotFoundException e)
        {
        }

        key = ObidosConstants.CONTAINER_ID;
        try
        {
            containerId = ClientUtils.getIdFromUrl(placeManager, key);
        } catch (NumberFormatException |ParamNotFoundException e)
        {
        }

        if (itemId != null)
        {
           fetchAndIpdateItemName(itemId);
        }
        else if (containerId != null)
        {
           fetchAndIpdateContainerName(containerId);
        }

    }


    private void fetchAndIpdateContainerName(Long containerId)
    {

    }

    private void fetchAndIpdateItemName(Long itemId)
    {
        GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
        {

            @Override
            public void uponSuccess(ItemDTO result)
            {
           		getView().getNameInputGroup().setText(result.getName());
            	getView().getContainerNameRow().setVisible(false);
            	String share = ClientUtils.getShareFromUrl(placeManager);
            	if (ObidosConstants.ITEM.equals(share))
            	{
            		getView().getContainerNameRow().setVisible(true);
            		getView().getContainerNameInputGroup().setText(result.getContainerName());
            	}
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not fetch Item: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ItemService.Utility.getInstance().get(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
    }

	protected <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
	}


    private void updatePanelHeader(String title)
    {
        getView().getPanelHeader().setHeadingText(title);
    }

    private void supdateShareWithUsersButtonTitle(String title)
    {
//        getView().getShareWithUsersButton().setText(title);
    }
    private void supdateShareWithGroupButtonTitle(String title)
    {
//        getView().getShareWithGroupButton().setText(title);
    }
    private void updateListButtonTitle(String title)
    {
//      getView().getListButton().setText(title);
    }


    private void showShareWith(String nameToken, String shareType)
    {
        Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
        String key = ObidosConstants.ITEM_ID;
        if (itemId == null)
        {
            showErrorMessage("Could not get " + key + " form URL");
            return;
        }

        Map<String,String> with = new HashMap<>();
        with.put(key, itemId.toString());
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.SHARE);


        Long containerId =  ClientUtils.getContainerIdFromUrl(placeManager);
        if (containerId != null)
        {
            with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        }
        
        with.put(ObidosConstants.SHARE_TYPE, shareType);
        ClientUtils.addPlace(placeManager, with);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    @Override
    public void showShareWithUsersPage()
    {
        String nameToken = NameTokens.SHARE_WITH_USERS;
        showShareWith(nameToken,ObidosConstants.SHARE_WITH_USERS);
    }

    @Override
    public void showShareWIthGroupsPage()
    {
        String nameToken = NameTokens.SHARE_WITH_GROUPS;
        showShareWith(nameToken, ObidosConstants.SHARE_WITH_GROUPS);
        /*
        String itemId = null;
        String key = ObidosConstants.ITEM_ID;
        try
        {
            itemId = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " form URL");
            return;
        }

        Map<String,String> with = new HashMap<>();
        key = ObidosConstants.SHARE;
        String value = ObidosConstants.ITEM;
        with.put(key, value);

        key = ObidosConstants.ITEM_ID;
        value = itemId;
        with.put(key, value);

       Long containerId =  getConterIdFromUrl();
        if (containerId != null)
        {
            with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        }

        String nameToken = NameTokens.SHARE_WITH_GROUPS;
        ClientUtils.showPage(placeManager, nameToken, with);
        */
    }

    @Override
    public void list()
    {
    	/*
        Long containerId = getConterIdFromUrl();
        if (containerId != null)
        {
            Map<String,String> with = new HashMap<>();
            with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
            ClientUtils.showPage(placeManager, NameTokens.LIST_ITEMS, with);
        }
        else
        {
            ClientUtils.showPage(placeManager, NameTokens.LIST_ALL_MY_ITEMS);
        }
        */
    	ClientUtils.navigateToPlace(placeManager);
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    private Long getConterIdFromUrl()
    {
        Long containerId = null;
        String key = ObidosConstants.CONTAINER_ID;
        try
        {
            containerId = ClientUtils.getIdFromUrl(placeManager, key);
            return containerId;
        } catch (NumberFormatException |ParamNotFoundException e)
        {
            return null;
        }
    }

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

        @Override
        public void back()
        {
                ClientUtils.goBack(placeManager);
        }
}
