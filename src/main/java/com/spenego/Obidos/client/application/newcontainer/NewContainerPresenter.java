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

package com.spenego.Obidos.client.application.newcontainer;


import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseCanCreateShareGateKeepr;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class NewContainerPresenter extends Presenter<NewContainerPresenter.MyView, NewContainerPresenter.MyProxy>
        implements NewContainerUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private String sContainerName = null;
    interface MyView extends View, HasUiHandlers<NewContainerUiHandlers>
    {
        public Button getSaveButton();
        public InlineRadio getPublicContainerRadio();
        public InlineRadio getPrivateContainerRadio();
        public BlockQuote getHelpBlockQuote();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosMessageRow getMessageRow();
        public ObidosInputGroup getContainerNameInputGroup();
        public ObidosPanelHeader getPanelHeader();
        public Button getGrantPermissionsButton();
        public Button getSharedWithUsersButton();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

    @NameToken(NameTokens.NEW_CONTAINER)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseCanCreateShareGateKeepr.class)
    interface MyProxy extends ProxyPlace<NewContainerPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    NewContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        TextBox tbox = getView().getContainerNameInputGroup().getTextBox();
        tbox.addKeyDownHandler(new KeyDownHandler()
		{
			
			@Override
			public void onKeyDown(KeyDownEvent e)
			{
				showMessage(null);
				if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					saveContainer();
				}
			}
		});
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
        ClientUtils.resetLanguage(getView().getLanguageRow());
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();

        if (ClientUtils.isAdmin(currentUser)) return;

        if (!ClientUtils.isPassphraseRegistered(currentUser))
        {
        	ClientUtils.showRegisterPassphraseDialog(glang.createContainer(), 
        			()->ClientUtils.goBack(placeManager),
        			()->ClientUtils.goToRegisterPassphrasePage(placeManager, NameTokens.NEW_CONTAINER));
        	return;
        }

        showMessage(null);
        hideBackButton();
        resetForm();
        fetchAndPopulateFormForEdit();
        ClientUtils.focusToWidegt(getView().getContainerNameInputGroup().getTextBox());
        adjustButtonsWidth();
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }
    
    private void adjustButtonsWidth()
    {
    	Button b1 = getView().getSaveButton();
    	Button b2 = getView().getGrantPermissionsButton();
    	Button b3 = getView().getSharedWithUsersButton();
    	ClientUtils.adjustButtonWidth(141, b1, b2, b3);
//    	ClientUtils.adjustButtonsWidth(141,b1,b2,b3);
    }

    private void hideBackButton()
    {
    	Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
        getView().getPanelHeader().getBackButton().setText(glang.listContainers());
    	if (containerId != null)
    	{
    		getView().getPanelHeader().getBackButton().setText(glang.back());
    	}
    }

    private void resetForm()
    {
    	getView().getContainerNameInputGroup().setText(null);
        getView().getPanelHeader().setTitle(glang.createContainer());
        getView().getSaveButton().setText(ObidosMessages.LANG.create());

        enableRadioButtons();

        getView().getPublicContainerRadio().setValue(false);
        getView().getPrivateContainerRadio().setValue(false);

        getView().getGrantPermissionsButton().setVisible(false);
        getView().getSharedWithUsersButton().setVisible(false);
    }

    private void enableRadioButtons()
    {
        getView().getPublicContainerRadio().setEnabled(true);
        getView().getPrivateContainerRadio().setEnabled(true);
    }

    private void disableRadioButtons()
    {
        getView().getPublicContainerRadio().setEnabled(false);
        getView().getPrivateContainerRadio().setEnabled(false);
    }

    private void fetchAndPopulateFormForEdit()
    {
        Long containerId = null;
        try
		{
			containerId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.CONTAINER_ID);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
			return;
		}
        disableRadioButtons();

        // fetch container
        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not fetch Container: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(ContainerDTO dto)
            {
                getView().getPanelHeader().setTitle(glang.editContainer());
                sContainerName = dto.getName();
                getView().getContainerNameInputGroup().getTextBox().setValue(dto.getName());
                getView().getSaveButton().setText(ObidosMessages.LANG.update());
                boolean isPrivate = dto.getIsPrivate();
                GWT.log("isPrivate: " + isPrivate);
                if (isPrivate)
                {
                    getView().getPublicContainerRadio().setValue(false);
                    getView().getPrivateContainerRadio().setValue(true);
                }
                else
                {
                    getView().getPublicContainerRadio().setValue(true);
                    getView().getPrivateContainerRadio().setValue(false);
                }
                boolean shared = ClientUtils.fromBoolean(dto.getShared());
                boolean groupShared = ClientUtils.fromBoolean(dto.getGroupShared());
                getView().getGrantPermissionsButton().setVisible(shared);
                getView().getSharedWithUsersButton().setVisible(shared);
                gwtLog("MMM shared: " + shared);
                gwtLog("MMM group group: " + groupShared);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
    }

    private void disableSaveButton()
    {
        getView().getSaveButton().setEnabled(false);
    }

    private void enableSaveButton()
    {
        getView().getSaveButton().setEnabled(true);
    }
    
    @Override
    public void saveContainer()
    {

    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
    	if (registered)
    	{
    		saveContainerReal();
    	}
    	else
    	{
    		ClientUtils.showRegisterPassphraseDialog(placeManager, ObidosMessages.LANG.createContainer(),null);
    	}
    }

    private void saveContainerReal()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
        if (containerId != null)
        {
            updateContainer(containerId);
            return;

        }
        String containerName = getView().getContainerNameInputGroup().getText();
        if (containerName == null || containerName.length() == 0)
        {
        	showErrorMessage(lang.specifyContainerName());
        	return;
        }

        Boolean isPublicContainer = getView().getPublicContainerRadio().getValue();
        Boolean isPrivateContainer = getView().getPrivateContainerRadio().getValue();

        if (isPublicContainer == false && isPrivateContainer == false)
        {
            showErrorMessage(lang.selectContainerType());
            return;
        }


        Boolean isPrivate = isPrivateContainer;

        GWT.log("XX isPrivate: " + isPrivate);
        GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(ObidosMessages.LANG.couldNotCreateContainer() + ": " + caught.getMessage());
            }

            @Override
            public void uponSuccess(Long id)
            {
            	showMessage(ObidosMessages.LANG.containerCreated(containerName));
            	getView().getContainerNameInputGroup().getTextBox().setValue(null);
            	getView().getPublicContainerRadio().setValue(false);
            	getView().getPrivateContainerRadio().setValue(false);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().create(authCreds, containerName, isPrivate, callback);
    }
    

    private void updateContainer(Long contaierId)
    {
        String containerName = getView().getContainerNameInputGroup().getText();
        if (containerName == null || containerName.length() == 0)
        {
            return;
        }

        Boolean isPublicContainer = getView().getPublicContainerRadio().getValue();
        Boolean isPrivateContainer = getView().getPrivateContainerRadio().getValue();
        if (isPublicContainer == false && isPrivateContainer == false)
        {
            showErrorMessage(ObidosMessages.LANG.selectContainerType());
            return;
        }

        Boolean isPrivate = isPrivateContainer;


        ContainerDTO containerDTO = new ContainerDTO();
        containerDTO.setIsPrivate(isPrivate);
        containerDTO.setName(containerName);
        containerDTO.setId(contaierId);

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(ObidosMessages.LANG.couldNotUpdateContainer() + ": " + caught.getMessage());
                return;
            }

            @Override
            public void uponSuccess(Void result)
            {
//            	Date d = new Date();
           	showMessage(ObidosMessages.LANG.containerNameUpdated(containerName));
//            	showMessage(ObidosMessages.LANG.containerNameUpdated(d.toString()));
//              showListContainersPage();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().update(authCreds, containerDTO, callback);
    }

    @Override
    public void showTextBoxChange()
    {
    	showMessage("");
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
	public void showHelp()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void back()
	{
		ClientUtils.showPage(placeManager, NameTokens.LIST_CONTAINERS);
		
	}

	@Override
	public void radioButtonClickHandler()
	{
		showMessage(null);
	}
	
	private void gwtLog(String msg)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), msg);
	}

	@Override
	public void showGrantPermissionsPage()
	{
		String nameToken = NameTokens.GRANT_PERMISSIONS_FOR_CONTAINER;
		Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
        Map<String,String> with = new HashMap<>();
        if (containerId != null)
        {
        	with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        	ClientUtils.addParamToMap(placeManager, with, NameTokens.NEW_CONTAINER);
        	ClientUtils.showPage(placeManager, nameToken, with);
        }
        else
        {
        	showErrorMessage("Could not find container id in URL");
        }
	}
	
	@Override
	public void showListOfUsersTheContainerIsSharedWith()
	{
		Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
		if (containerId == null)
		{
			return;
		}
        String nameToken = NameTokens.LIST_USERS_CONTAINER_IS_SHARED_WITH;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
        	gwtLog("Add type: " + type);
        	with.put(ObidosConstants.TYPE, type);
        }
        ClientUtils.addPlace(placeManager, with, NameTokens.NEW_CONTAINER);
        ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void languageListBoxCallback()
	{
		ListBox lb = getView().getLanguageListBox();
		String lang = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		gwtLog("index: "+ idx);
		gwtLog("Lang: " + lang);
		// only support English and Bangla for editing at this time
		switch(idx)
		{
			case 0: // English
			{
				ClientUtils.enableBanglaEditing(false, getView().getLanguageRow());
				break;
			}
			case 1: // Bangla
			{
				ClientUtils.enableBanglaEditing(true, getView().getLanguageRow());
				break;
			}
		}
	}

}
