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

package com.spenego.Obidos.client.application.installlicense;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosTextArea;

class InstallLicenseView extends ViewWithUiHandlers<InstallLicenseUiHandlers> implements InstallLicensePresenter.MyView
{
	interface Binder extends UiBinder<Widget, InstallLicenseView>
	{
	}
	
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;
	
	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	ObidosTextArea licenseTextArea;
	
	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	Button submitButton;


	@Inject
	InstallLicenseView(Binder uiBinder)
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
					getUiHandlers().help();
				}
			});
		}
	}
	
    @UiHandler("licenseTextArea")
	void onPasteToTextArea(ValueChangeEvent<String> text)
	{
		getUiHandlers().licensePasted();
	}
    
    @UiHandler("submitButton")
    void onclickSubmitButton (ClickEvent e)
	{
    	getUiHandlers().install();
	}
    
    @UiHandler("clearButton")
    void onclickClearButton (ClickEvent e)
	{
    	getUiHandlers().clearTextArea();
	}
    
	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}


	public ObidosTextArea getLicenseTextArea()
	{
		return licenseTextArea;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getSubmitButton()
	{
		return submitButton;
	}
}