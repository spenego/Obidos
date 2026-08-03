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

package com.spenego.Obidos.client.application.generatepassword;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.extras.select.client.ui.Select;

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
import com.spenego.Obidos.client.application.widgets.ObidosGuessesPerSecondTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;

class GeneratePasswordView extends ViewWithUiHandlers<GeneratePasswordUiHandlers>
		implements GeneratePasswordPresenter.MyView
{
    @UiField
    PanelBody panelBody;

    @UiField
    BlockQuote helpBlockQuote;
    
	@UiField
	Button generatePasswordRepeatButton;

	@UiField
	Button copyToClipboardButton;

	@UiField
	ObidosTextBox generatePasswordBox;
	
	@UiField
	Button hibpButton;

	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	Progress passwordStrengthProgress;

	@UiField
	ProgressBar passwordStrengthBar;
	
	@UiField
	Select algorithmSelect;

	@UiField
	Select rollDieceSelect;

	@UiField
	ObidosRow rollDiceRow;

	@UiField
	CheckBox capitalizeCheckBox;

	@UiField
	CheckBox numberCheckBox;

	@UiField
	CheckBox symbolCheckBox;

	@UiField
	Row modifierRow;

	@UiField
	ObidosRow passLenRow;
	
	@UiField
	ObidosIntegerTextBox passwordLengthTextBox;
	
	@UiField
	ObidosReadonlyTextBox entropyTextBox;

	@UiField
	ObidosReadonlyTextBox rawEntropyTextBox;

	@UiField
	FormLabel passwordNearProgressBarLabel;
	
	@UiField
	FormLabel longPasswordHintLabel;

	@UiField
	ObidosGuessesPerSecondTextBox guessPerSecondTextBox;
	
	@UiField
	ObidosReadonlyTextBox estimatedCrackingTimeTextBox;

	@UiField
	Button entropyInfoButton;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    Row showEntropyRow;
    
    @UiField
    ObidosRow haveIbeenPawnedRow;

    @UiField
    FormLabel rawEntropyLabel;

    @UiField
    FormLabel adjustedEntropyLabelHint;

    @UiField
    FormLabel waitLabel;

    @UiField
    HTMLPanel dotsPanel;

	interface Binder extends UiBinder<Widget, GeneratePasswordView>
	{
	}

	@Inject
	GeneratePasswordView(Binder uiBinder)
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
	
	@UiHandler("generatePasswordRepeatButton")
	void onClickgeneratePasswordRepeatButton(ClickEvent e)
	{
		getUiHandlers().generatePassword();
	}
	
	@UiHandler("copyToClipboardButton")
	void onClickcopyToClipboardButton(ClickEvent e)
	{
		getUiHandlers().copyPasswordToClipboard();
	}
	
	// show strength if password is changed
	@UiHandler("generatePasswordBox")
	void onKeyUpGeneratePasswordBox(KeyUpEvent e)
	{
		String p = generatePasswordBox.getValue();
		boolean hasText = (p != null && p.length() > 0);
		showEntropyRow.setVisible(hasText);
		haveIbeenPawnedRow.setVisible(hasText);
		if (!hasText)
		{
			passwordNearProgressBarLabel.setText("");
		} else
		{
			if (e.getNativeKeyCode() > 0)
			{
				getUiHandlers().showStrength();
			}
		}
	}
	
	// if a password is pasted show strength as well
	@UiHandler("generatePasswordBox")
	void onPasteGeneratePasswordBox(ValueChangeEvent<String> text)
	{
		getUiHandlers().showStrengthPasted();
	}

	// Remove value changed event. it fires only if a selection has changed.
	// we cannot use that, as user can select the same algorithm more than
	// once. Well we will not calculate passwords for event but we need to 
	// turn on off rows
	@UiHandler("algorithmSelect")
	void onSelectalgorithmSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().algorithmSelectCallback();
	}

	@UiHandler("rollDieceSelect")
	void onSelectrollDieceSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().diceRollCallback();
	}
	
	@UiHandler("entropyInfoButton")
	void onClickentropyInfoButton(ClickEvent e)
	{
		getUiHandlers().showEntropyInfo();
	}

	@UiHandler("hibpButton")
	void onClickHibpButton(ClickEvent e)
	{
		getUiHandlers().checkWithHaveIBeenPwned();
	}

	public Button getGeneratePasswordRepeatButton()
	{
		return generatePasswordRepeatButton;
	}

	public Button getCopyToClipboardButton()
	{
		return copyToClipboardButton;
	}

	public ObidosTextBox getGeneratePasswordBox()
	{
		return generatePasswordBox;
	}

	public Button getHibpButton()
	{
		return hibpButton;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Progress getPasswordStrengthProgress()
	{
		return passwordStrengthProgress;
	}

	public Select getAlgorithmSelect()
	{
		return algorithmSelect;
	}

	public Select getRollDieceSelect()
	{
		return rollDieceSelect;
	}

	public ObidosRow getRollDiceRow()
	{
		return rollDiceRow;
	}

	public CheckBox getCapitalizeCheckBox()
	{
		return capitalizeCheckBox;
	}

	public CheckBox getNumberCheckBox()
	{
		return numberCheckBox;
	}

	public CheckBox getSymbolCheckBox()
	{
		return symbolCheckBox;
	}

	public Row getModifierRow()
	{
		return modifierRow;
	}

	public ObidosRow getPassLenRow()
	{
		return passLenRow;
	}

	public ObidosIntegerTextBox getPasswordLengthTextBox()
	{
		return passwordLengthTextBox;
	}

	public ObidosReadonlyTextBox getEntropyTextBox()
	{
		return entropyTextBox;
	}

	public ProgressBar getPasswordStrengthBar()
	{
		return passwordStrengthBar;
	}

	public FormLabel getPasswordNearProgressBarLabel()
	{
		return passwordNearProgressBarLabel;
	}

	public ObidosGuessesPerSecondTextBox getGuessPerSecondTextBox()
	{
		return guessPerSecondTextBox;
	}

	public ObidosReadonlyTextBox getEstimatedCrackingTimeTextBox()
	{
		return estimatedCrackingTimeTextBox;
	}

	public ObidosReadonlyTextBox getRawEntropyTextBox()
	{
		return rawEntropyTextBox;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getEntropyInfoButton()
	{
		return entropyInfoButton;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Row getShowEntropyRow()
	{
		return showEntropyRow;
	}

	public ObidosRow getHaveIbeenPawnedRow()
	{
		return haveIbeenPawnedRow;
	}

	public FormLabel getLongPasswordHintLabel()
	{
		return longPasswordHintLabel;
	}

	public FormLabel getRawEntropyLabel()
	{
		return rawEntropyLabel;
	}

	public FormLabel getAdjustedEntropyLabelHint()
	{
		return adjustedEntropyLabelHint;
	}

	public PanelBody getPanelBody()
	{
		return panelBody;
	}

	public FormLabel getWaitLabel()
	{
		return waitLabel;
	}

	public HTMLPanel getDotsPanel()
	{
		return dotsPanel;
	}
}