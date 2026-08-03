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
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class PickNotificationTemplateView extends ViewWithUiHandlers<PickNotificationTemplateUiHandlers>
        implements PickNotificationTemplatePresenter.MyView
{

	@UiField
	Panel panel;
	
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	Button editButton;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;
	
	@UiField
	ObidosMessageRow messageRow;

	@UiField
	Select selectTemplate;
	
	@UiField
	ListBox selectTemplateListBox;

	@UiField
	ObidosPanelHeader panelHeader;
	
	
	
    interface Binder extends UiBinder<Widget,PickNotificationTemplateView>
    {
    }

    @Inject
    PickNotificationTemplateView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().showHelp();
				}
			});
		}
    }
    
    @UiHandler("editButton")
    void onclickEditButton (ClickEvent e)
	{
    	getUiHandlers().showEditTemplatePage();
	}

    @UiHandler("selectTemplateListBox")
    void onclickSelectTemplateListBox (ClickEvent e)
	{
    	getUiHandlers().listBoxClickHandler();
	}
    
	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getEditButton()
	{
		return editButton;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Select getSelectTemplate()
	{
		return selectTemplate;
	}

	public ListBox getSelectTemplateListBox()
	{
		return selectTemplateListBox;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

}