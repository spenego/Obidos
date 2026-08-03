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

package com.spenego.Obidos.client.application.obidoserrorpage;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.PanelHeader;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class ObidosErrorPageView extends ViewWithUiHandlers<ObidosErrorPageUiHandlers>
		implements ObidosErrorPagePresenter.MyView
{
	interface Binder extends UiBinder<Widget, ObidosErrorPageView>
	{
	}

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    ObidosMessageRow messageRow;
    
    @UiField
    HTML infoHtml;

	@Inject
	ObidosErrorPageView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		panelHeader.getHelpButton().setVisible(false);
	}

	public PanelHeader getPanelHeader()
	{
		return panelHeader;
	}


	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public void setMessageRow(ObidosMessageRow messageRow)
	{
		this.messageRow = messageRow;
	}

	public HTML getInfoHtml()
	{
		return infoHtml;
	}

	public void setInfoHtml(HTML infoHtml)
	{
		this.infoHtml = infoHtml;
	}

}