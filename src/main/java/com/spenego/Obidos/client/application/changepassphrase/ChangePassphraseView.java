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

package com.spenego.Obidos.client.application.changepassphrase;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.util.ClientUtils;

class ChangePassphraseView extends ViewWithUiHandlers<ChangePassphraseUiHandlers>
        implements ChangePassphrasePresenter.MyView
{
    interface Binder extends UiBinder<Widget, ChangePassphraseView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    Paragraph helpParagraph;

    @UiField
    ObidosPasswordBox currentPassphraseBox;

    @UiField
    ObidosPasswordBox newPassphraseBox;

    @UiField
    ObidosPasswordBox repeatPassphraseBox;

    
    @UiField
	ObidosMessageRow messageRow;

    

    @UiField
    Button changePassphraseButton;

    @UiField
    Button showHideCurrentPassphrasedButton;

    @UiField
    Button showHideNewPassphrasedButton;

    @UiField
    com.google.gwt.user.client.ui.Label  passphraseStrengthLabel;

    @UiField
    Progress passphraseStrengthProgress;

    @UiField
    ProgressBar passphraseStrengthBar;

    @UiField
    Button showHideRepeatPassphrasedButton;

    @UiField
    ListGroup passphraseRequirementListGroup;

    @UiField
    Label ncharsLabel;

    @UiField
    HTMLPanel processingPanel;

    @Inject
    ChangePassphraseView(Binder uiBinder)
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
					getUiHandlers().showHideHelp();
				}
			});
		}

    }

    @UiHandler("newPassphraseBox")
    void onKeyUpnewPassphraseBox(KeyUpEvent e)
    {
        getUiHandlers().showPassphraseStrength();
    }

    @UiHandler("newPassphraseBox")
	void onPasteToPassphraseBox(ValueChangeEvent<String> text)
    {
        getUiHandlers().showPassphraseStrengthPasted();
    }

    @UiHandler("showHideCurrentPassphrasedButton")
    void onClickshowHideCurrentPassphrasedButton(ClickEvent e)
    {
       ClientUtils.toggleEyeIcon(showHideCurrentPassphrasedButton, currentPassphraseBox);
    }

    @UiHandler("showHideNewPassphrasedButton")
    void onClickshowHideNewPassphrasedButton(ClickEvent e)
    {
       ClientUtils.toggleEyeIcon(showHideNewPassphrasedButton, newPassphraseBox);
    }

    @UiHandler("showHideRepeatPassphrasedButton")
    void onClickShowHideCurrentPasswordButton(ClickEvent e)
    {
       ClientUtils.toggleEyeIcon(showHideRepeatPassphrasedButton, repeatPassphraseBox);
    }

    @UiHandler("changePassphraseButton")
    void onClickchangePassphraseButton(ClickEvent e)
    {
        getUiHandlers().changePassphrase();
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    public Button getShowHideCurrentPassphrasedButton()
    {
        return showHideCurrentPassphrasedButton;
    }

    public Button getShowHideNewPassphrasedButton()
    {
        return showHideNewPassphrasedButton;
    }

    public ObidosPasswordBox getCurrentPassphraseBox()
    {
        return currentPassphraseBox;
    }

    public ObidosPasswordBox getNewPassphraseBox()
    {
        return newPassphraseBox;
    }

    public ObidosPasswordBox getRepeatPassphraseBox()
    {
        return repeatPassphraseBox;
    }

    public Button getChangePassphraseButton()
    {
        return changePassphraseButton;
    }

    public Button getShowHideRepeatPassphrasedButton()
    {
        return showHideRepeatPassphrasedButton;
    }

    public com.google.gwt.user.client.ui.Label getPassphraseStrengthLabel()
    {
        return passphraseStrengthLabel;
    }

    public Progress getPassphraseStrengthProgress()
    {
        return passphraseStrengthProgress;
    }

    public ProgressBar getPassphraseStrengthBar()
    {
        return passphraseStrengthBar;
    }
    
   	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ListGroup getPassphraseRequirementListGroup()
	{
		return passphraseRequirementListGroup;
	}

	public Label getNcharsLabel()
	{
		return ncharsLabel;
	}

	public HTMLPanel getProcessingPanel()
	{
		return processingPanel;
	}
}