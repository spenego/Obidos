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

package com.spenego.Obidos.client.application.registerpassphrase;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.util.ClientUtils;

class RegisterPassphraseView extends ViewWithUiHandlers<RegisterPassphraseUiHandlers>
        implements RegisterPassphrasePresenter.MyView
{
    interface Binder extends UiBinder<Widget, RegisterPassphraseView>
    {
    }
    
    @UiField
    ObidosPanelHeader panelHeader;
    
    
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph passphraseParagraph;

    @UiField
    ObidosPasswordBox passphraseBox;

    @UiField
    Button showHidePassphrasedButton;

    @UiField
    Button laterButton;
    
    @UiField
	ObidosMessageRow messageRow;
    
    @UiField
    ObidosButtonToolBar buttonToolBarBottom;

    
    @Inject
    RegisterPassphraseView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().showHideHelp();
				}
			});
		}

		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
				}
			});
		}
    }

    @UiHandler("passphraseBox")
    void onPressEnterpassphraseBox(KeyDownEvent e)
    {
    	getUiHandlers().keyDownCallback(e);
    	/*
    	ClientUtils.showMessage("", formErrorLabel);
    	passphraseBox.setFocus(true);
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    	    getUiHandlers().registerPassphrase();
    	}
    	*/
    }

    @UiHandler("showHidePassphrasedButton")
	void onClickShowHIdePassphraseButton(ClickEvent e)
	{
	    ClientUtils.toggleEyeIcon(showHidePassphrasedButton, passphraseBox);
	}

    @UiHandler("submitButton")
    void onClickSaveButton(ClickEvent e)
    {
        getUiHandlers().registerPassphrase();
    }

    @UiHandler("laterButton")
    void onClickLaterButton(ClickEvent e)
    {
        getUiHandlers().registerPassphraseLater();
    }

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

    public ObidosPasswordBox getPassphraseBox()
    {
        return passphraseBox;
    }

    public Paragraph getPassphraseParagraph()
    {
        return passphraseParagraph;
    }

    public Button getShowHidePassphrasedButton()
    {
        return showHidePassphrasedButton;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Button getLaterButton()
    {
        return laterButton;
    }

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}
}