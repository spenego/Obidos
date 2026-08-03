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

package com.spenego.Obidos.client.application.viewcertificate;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.AnchorListItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;

class ViewCertificateView extends ViewWithUiHandlers<ViewCertificateUiHandlers>
		implements ViewCertificatePresenter.MyView
{
	
	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	HTMLPanel certHtmlPanel;
	
	@UiField
	HTML certHtml;

	@UiField
	AnchorListItem obidosCertsAli;
	
	@UiField
	AnchorListItem adldapCertsAli;

	interface Binder extends UiBinder<Widget, ViewCertificateView>
	{
	}

	@Inject
	ViewCertificateView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("obidosCertsAli")
	void onclickObidosCertsAli (ClickEvent e)
	{
		getUiHandlers().viewObidosCerts();
	}
	
	@UiHandler("adldapCertsAli")
	void onclickAdldapCertsAli (ClickEvent e)
	{
		getUiHandlers().viewAdLdapCerts();
	}

	public AnchorListItem getObidosCertsAli()
	{
		return obidosCertsAli;
	}

	public AnchorListItem getAdldapCertsAli()
	{
		return adldapCertsAli;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public HTMLPanel getCertHtmlPanel()
	{
		return certHtmlPanel;
	}

	public HTML getCertHtml()
	{
		return certHtml;
	}
}