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

package com.spenego.Obidos.client.application.picknotificationtemplate;
import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;

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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;

public class PickNotificationTemplatePresenter extends
        Presenter<PickNotificationTemplatePresenter.MyView,PickNotificationTemplatePresenter.MyProxy>
        implements PickNotificationTemplateUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
			
    interface MyView extends View,HasUiHandlers<PickNotificationTemplateUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
    	public Button getEditButton();
    	public ObidosButtonToolBar getButtonToolBarBottom();
    	public ObidosMessageRow getMessageRow();
    	public Select getSelectTemplate();
    	public ListBox getSelectTemplateListBox();
    	public ObidosPanelHeader getPanelHeader();
    }

    @NameToken(NameTokens.PICK_NOTIFICAITON_TEMPLATE)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<PickNotificationTemplatePresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;

    @Inject
    PickNotificationTemplatePresenter(EventBus eventBus,MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus,view,proxy,ApplicationPresenter.SLOT_MAIN);

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
        getView().getButtonToolBarBottom().adjustButtonsWidth();
        // don't reset to first item. it's helpful to know which one was edited last time
//        getView().getSelectTemplate().setValue(glang.accountCreatedTemplate());
        
        populateSelectTemplateListBox();
    }
    
    private String getIdFromIndex(int idx)
    {
	   /*
	   <select:Option b:id="171" text="{i18n.editAccountCreatedTemplate}"/>
	   <select:Option b:id="417" text="{i18n.editItemSharedTemplate}"/>
	   <select:Option b:id="524" text="{i18n.editItemRevokedTemplate}"/>
	   <select:Option b:id="633" text="{i18n.editContainerSharedTemplate}"/>
	   <select:Option b:id="744" text="{i18n.editContainerRevokedTemplate}"/>
	   <select:Option b:id="209" text="{i18n.editPasswordResetTemplate}"/>
	   <select:Option b:id="312" text="{i18n.editPasswordResetWarning}"/>  -- not used anymore
	   <select:Option b:id="867" text="{i18n.editPassphraseResetTemplate}"/>
	   <select:Option b:id="869" text="{i18n.editPassphraseResetWarning}"/> -- not used anymore
	   */
    	
    	// update for issue 659
    	switch (idx)
    	{
			case 0:
			{
				return "171"; // account created
			}
			
			case 1:
			{
				return "209"; // password reset request
			}
			
			case 2:
			{
				return "867"; // passphrase reset request
			}
			case 3:
			{
				return "417"; // item shared
			}
			case 4:
			{
				return "633"; // container shared
			}
		
			default:
			{
				return "171";
			}
    	}


    	/*
    	switch (idx)
    	{
			case 0:
			{
				return "171"; // account created
			}
			
			case 1:
			{
				return "417"; // item shared
			}
			
			case 2:
			{
				return "524"; // item revoked
			}
			
			case 3:
			{
				return "633"; // container shared
			}
			
			case 4:
			{
				return "744"; // container revoked
			}
			
			case 5:
			{
				return "209"; // password reset request
			}
			
			// Don't display password and passphrase reset warning 
			// anymore. We don't send these mails anymore as they have
			// potential to mark as spammers.
			
			case 6:
			{
				return "867"; // passphrase reset request
			}
			
			default:
			{
				return "171";
			}
    	}
    	*/
    }

    private void populateSelectTemplateListBox()
    {
    	ListBox lb = getView().getSelectTemplateListBox();
    	if (lb.getVisibleItemCount() == 0)
    	{
			lb.addItem(glang.editAccountCreatedTemplate());
			lb.addItem(glang.editPasswordResetTemplate());
			lb.addItem(glang.editPassphraseResetTemplate());
			lb.addItem(glang.editItemSharedTemplate());
			lb.addItem(glang.editContainerSharedTemplate());
			lb.setVisibleItemCount(5);
			// Issue 659
//			lb.addItem(glang.editItemSharedTemplate());
//			lb.addItem(glang.editItemRevokedTemplate());
//			lb.addItem(glang.editContainerSharedTemplate());
//			lb.addItem(glang.editContainerRevokedTemplate());
//			lb.addItem(glang.editPasswordResetWarning());
//			lb.addItem(glang.editPassphraseResetWarning());
//			lb.setVisibleItemCount(7);
    	}
    	ClientUtils.focusToListBox(lb);
    }


	@Override
	public void showHelp()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void showEditTemplatePage()
	{
		ListBox lb = getView().getSelectTemplateListBox();
		gwtLog("Selected idx: " + lb.getSelectedValue());
		int idx = lb.getSelectedIndex();
		if (idx == -1)
		{
			showErrorMessage("Please select a template");
			return;
		}
		String id = getIdFromIndex(idx);
		gwtLog("Index: " + idx);
		gwtLog("ID: " + lb.getId());
		
		
		/*
		Select s = getView().getSelectTemplate();
		String value = s.getValue();
		gwtLog("Selected: " + value);
		String id = s.getSelectedItem().getId();
		gwtLog("id: " + id);
		*/
		Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TYPE, id);
        ClientUtils.showPage(placeManager,NameTokens.EDIT_NOTIFICATION_TEMPLATES,with);

	}
	
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}
	
	private void gwtLog(final String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	@Override
	public void listBoxClickHandler()
	{
//		getView().getEditButton().setEnabled(true);
	}

}