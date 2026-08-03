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

package com.spenego.Obidos.client.application.about;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListGroupItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class AboutView extends ViewWithUiHandlers<AboutUiHandlers> implements AboutPresenter.MyView
{
    interface Binder extends UiBinder<Widget, AboutView>
    {
    }

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    ObidosMessageRow messageRow;
    
    @UiField
    HTML infoHtml;

    @UiField
    HTML buildHtml;
    
    @UiField
    ListGroupItem userGuideLGI;

    @UiField
	ListGroupItem adminGuideLGI;

    @UiField
	Button userGuideButton;

    @UiField
	Button adminGuideButton;

    @UiField
	FormPanel formPanel;

    @UiField
    Button changeLogButton;
    
    @Inject
    AboutView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        panelHeader.getHelpButton().setVisible(false);
    }
    
    @UiHandler("userGuideButton")
    void onClickuserGuideButton(ClickEvent e)
    {
    	getUiHandlers().downloadUserGuide();
    }
    
    @UiHandler("adminGuideButton")
    void onClickadminGuideButton(ClickEvent e)
    {
    	getUiHandlers().downloadAdminGuide();
    }

    @UiHandler("changeLogButton")
    void onClickChangeLogButton(ClickEvent e)
    {
    	getUiHandlers().showChangeLogDialog();
    }

    public HTML getInfoHtml()
    {
        return infoHtml;
    }

    public HTML getBuildHtml()
    {
        return buildHtml;
    }

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ListGroupItem getUuserGuideLGI()
	{
		return userGuideLGI;
	}

	public ListGroupItem getAdminGuideLGI()
	{
		return adminGuideLGI;
	}

	public Button getUserGuideButton()
	{
		return userGuideButton;
	}

	public Button getAdminGuideButton()
	{
		return adminGuideButton;
	}

	public FormPanel getFormPanel()
	{
		return formPanel;
	}

	public Button getChangeLogButton()
	{
		return changeLogButton;
	}

}