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

package com.spenego.Obidos.client.application.installcertificate;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Paragraph;

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
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.application.widgets.ObidosTextArea;

class InstallCertificateView extends ViewWithUiHandlers<InstallCertificateUiHandlers>
		implements InstallCertificatePresenter.MyView
{
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	Paragraph helpParagraph;
	
	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	ObidosTextArea certTextArea;
	
	@UiField
	ObidosTextArea keyTextArea;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;
	
	@UiField
	Button submitButton;
	
	@UiField
	Button clearButton;
	
	@UiField
	Button viewButton;
	
	@UiField
	ObidosMessageRow messageRow;

	@UiField
	ObidosRow keyRow;

	interface Binder extends UiBinder<Widget, InstallCertificateView>
	{
	}

	@Inject
	InstallCertificateView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
        panelHeader.getHelpButton().addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				getUiHandlers().help();
			}
		});
	}
	
	@UiHandler("submitButton")
	void onclickSubmitButton (ClickEvent e)
	{
		getUiHandlers().installCerts();
	}
	
	@UiHandler("clearButton")
	void onclickClearButton (ClickEvent e)
	{
		getUiHandlers().clear();
	}
	
	@UiHandler("viewButton")
	void onclickViewButton (ClickEvent e)
	{
		getUiHandlers().viewCertificates();
	}
	
	@UiHandler("certTextArea")
	void onPasteCertTextArea(ValueChangeEvent<String> text)
	{
		getUiHandlers().certsPastedCallback();
	}
	
	@UiHandler("keyTextArea")
	void onPasteKeyTextArea (ValueChangeEvent<String> text)
	{
		getUiHandlers().keyPastedCallback();
	}

	public ObidosTextArea getCertTextArea()
	{
		return certTextArea;
	}

	public ObidosTextArea getKeyTextArea()
	{
		return keyTextArea;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public Button getSubmitButton()
	{
		return submitButton;
	}

	public Button getClearButton()
	{
		return clearButton;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosRow getKeyRow()
	{
		return keyRow;
	}

	public Paragraph getHelpParagraph()
	{
		return helpParagraph;
	}

	public Button getViewButton()
	{
		return viewButton;
	}
}