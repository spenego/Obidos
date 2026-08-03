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

package com.spenego.Obidos.client.application.keypair;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
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

class KeyPairView extends ViewWithUiHandlers<KeyPairUiHandlers> implements KeyPairPresenter.MyView
{
	interface Binder extends UiBinder<Widget, KeyPairView>
	{
	}

	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	Span generatedPassPhraseSpan;

	@UiField
	ListBox languageListBox;

	@UiField
	ListBox numberOfWordsListBox;

	@UiField
	Button genStrongPassPhraseButton;

	@UiField
	ObidosPasswordBox passphraseBox;

	@UiField
	ObidosPasswordBox confirmPassphraseBox;

	@UiField
	ObidosMessageRow messageRow;

	@UiField
	InlineCheckBox uppercaseCheckBox;

	@UiField
	InlineCheckBox noSpacesCheckBox;

	@UiField
	InlineCheckBox addSpecialCharCheckBox;

	@UiField
	Button genKeypairButton;

	@UiField
	Button genCollapseButton;

	@UiField
	Collapse genCollapse;

    @UiField
    com.google.gwt.user.client.ui.Label passphraseStrengthLabel;

    @UiField
    Progress passphraseStrengthProgress;

    @UiField
    ProgressBar passphraseStrengthBar;

    @UiField
    Button showHidePassphraseButton;
    
    @UiField
    Button showHideConfirmPassphrasedButton;

    @UiField
    ListGroup passphraseRequirementListGroup;

    @UiField
    Label ncharsLabel;

    @UiField
    HTMLPanel processingPanel;

	@Inject
	KeyPairView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		panelHeader.getHelpButton().setVisible(false);
	}

	@UiHandler("showHidePassphraseButton")
	void onClickShowHIdePassphraseButton(ClickEvent e)
	{
	    ClientUtils.toggleEyeIcon(showHidePassphraseButton, passphraseBox);
	}
	
	@UiHandler("showHideConfirmPassphrasedButton")
	void onclickshowHideConfirmPassphrasedButton (ClickEvent e)
	{
	    ClientUtils.toggleEyeIcon(showHideConfirmPassphrasedButton, confirmPassphraseBox);
	}

	@UiHandler("languageListBox")
	void onClickAuthSourceListBox(ChangeEvent e)
	{
		String value = languageListBox.getSelectedValue();
		int idx = languageListBox.getSelectedIndex();
		if (idx == 0) // English
		{
		}
		else if (idx == 1) // German
		{
		}
	}

	@UiHandler("genStrongPassPhraseButton")
	void onClickGenStrongPassPhraseButton(ClickEvent e)
	{
		getUiHandlers().generateStrongPassPhrase();
	}

	@UiHandler("genCollapseButton")
	void onClickGenCollapseButton(ClickEvent e)
	{
	    boolean collapseState = genCollapse.isHidden();
	    if (collapseState)
	    {
	        genCollapseButton.setIcon(IconType.ARROW_UP);
	    }
	    else
	    {
	        genCollapseButton.setIcon(IconType.ARROW_DOWN);
	    }

	}

	@UiHandler("passphraseBox")
	void onKeyUpPassphraseBox(KeyUpEvent e)
	{
	    getUiHandlers().showPassphraseStrength();
	}

	@UiHandler("confirmPassphraseBox")
	void onKeyUpConfirmPassphraseBox(KeyUpEvent e)
	{
	   getUiHandlers().onConfirmPassphraseKeyUp();
	}

	@UiHandler("passphraseBox")
	void onPasteToPassphraseBox(ValueChangeEvent<String> text)
    {
	    GWT.log("Passphrase pasted...");
	    getUiHandlers().showPassphraseStrengthPasted();
    }

	@UiHandler("genKeypairButton")
	void onClickGenKeyPairButton(ClickEvent e)
	{
	    getUiHandlers().generateAndSaveKeyPair();
	}

	@UiHandler("confirmPassphraseBox")
	void onEnterConfirmPassphraseBox(KeyUpEvent e)
	{
	    if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
	    {
	        getUiHandlers().generateAndSaveKeyPair();
	    }
	}


	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}

	public Button getGenStrongPassPhraseButton()
	{
		return genStrongPassPhraseButton;
	}

	public ObidosPasswordBox getPassphraseBox()
    {
        return passphraseBox;
    }

	@Deprecated
    public FormLabel getFormErrorLabel()
	{
		return null;
	}
	
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Button getShowHideConfirmPassphrasedButton()
	{
		return showHideConfirmPassphrasedButton;
	}

	public Button getGenKeypairButton()
	{
		return genKeypairButton;
	}

	public InlineCheckBox getUppercaseCheckBox()
	{
		return uppercaseCheckBox;
	}

	public InlineCheckBox getNoSpacesCheckBox()
	{
		return noSpacesCheckBox;
	}

    public Collapse getGenCollapse()
    {
        return genCollapse;
    }

    public InlineCheckBox getAddSpecialCharCheckBox()
    {
        return addSpecialCharCheckBox;
    }

    public Button getGenCollapseButton()
    {
        return genCollapseButton;
    }

    public Span getGeneratedPassPhraseSpan()
    {
        return generatedPassPhraseSpan;
    }

    public ListBox getNumberOfWordsListBox()
    {
        return numberOfWordsListBox;
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

    public ObidosPasswordBox getConfirmPassphraseBox()
    {
        return confirmPassphraseBox;
    }

    public Button getShowHidePassphraseButton()
    {
        return showHidePassphraseButton;
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