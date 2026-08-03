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

package com.spenego.Obidos.client.application.userconsole;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowTop2px;

class UserConsoleView extends ViewWithUiHandlers<UserConsoleUiHandlers> implements UserConsolePresenter.MyView
{
	interface Binder extends UiBinder<Widget, UserConsoleView>
	{
	}

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	PanelBody panelBody;

	@UiField
	ObidosRowTop2px changeLanguageRow;
	
	@UiField
	Select languageChangeSelect;
	

	@Inject
	UserConsoleView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		panelHeader.getHelpButton().setVisible(false);
	}
	
	@UiHandler("languageChangeSelect")
	void onSelectLanguageSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().languageChangeCallbackHandler();
	}


	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}


	public ObidosRowTop2px getChangeLanguageRow()
	{
		return changeLanguageRow;
	}


	public Select getLanguageChangeSelect()
	{
		return languageChangeSelect;
	}

	public PanelBody getPanelBody()
	{
		return panelBody;
	}
}