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

package com.spenego.Obidos.client.application.systemsettings;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;

class SystemSettingsView extends ViewWithUiHandlers<SystemSettingsUiHandlers> implements SystemSettingsPresenter.MyView
{
	ObidosMessages glang = ObidosMessages.LANG;
	interface Binder extends UiBinder<Widget, SystemSettingsView>
	{
	}
	
	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;
	
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	Button saveButton;
	
	@UiField
	Button resetButton;
	
	
	@UiField
	TextBox twoFAIssuerTextBox;
	
	@UiField
	FormLabel twoFAIssuerLabel;
	
	@UiField
	TextBox httpFqdnTextBox;
	
	@UiField
	ObidosIntegerTextBox httpsPortBox;
	
	@UiField
	FormLabel fqdnLabel;
	
	@UiField
	FormLabel portLabel;
	
	@UiField
	FormLabel maxPasswordAgetLabel;
	
	@UiField
	ObidosIntegerTextBox pwMinLengthTextBox;
	
	@UiField
	ObidosIntegerTextBox minimumLowercaseTextBox;

	@UiField
	ObidosIntegerTextBox minimumUppercaseTextBox;
	
	@UiField
	ObidosIntegerTextBox minimumNumericTextBox;
	
	@UiField
	ObidosIntegerTextBox minimumSpecialTextBox;

	@UiField
	ObidosIntegerTextBox minPasswordEntropyTextBox;

	@UiField
	ObidosIntegerTextBox maxPasswordAgeTextBox;

	@UiField
	FormLabel minPasswordEntropyLabel;
	
	@UiField
	FormLabel minPasswordLengthLabel;

	@UiField
	FormLabel minPasswordLowercaseLabel;
	
	@UiField
	FormLabel minPasswordUppercaseLabel;
	
	@UiField
	FormLabel minPasswordNumericCharLabel;

	@UiField
	FormLabel minPasswordSpecialCharLabel;
	
	@UiField
	FormLabel passwordLengthWarningLabel;
	
	@UiField
	FormLabel passwordEntropyLengthWarningLabel;

	@UiField
	ObidosReadonlyTextBox httpSchemeTextBox;
	
	// passphrase
	@UiField
	FormLabel minLengthPPLabel;

	@UiField
	FormLabel minLowerPPLabel;

	@UiField
	FormLabel minUpperPPLabel;
	
	@UiField
	FormLabel minNumberPPLabel;

	@UiField
	FormLabel minSpecialPPLabel;
	
	@UiField
	FormLabel minPPEntropyLabel;

	@UiField
	ObidosIntegerTextBox mininumPPLengthTextBox;
	
	@UiField
	FormLabel passphraseLengthWarningLabel;

	@UiField
	ObidosIntegerTextBox minimumPPLowercaseTextBox;
	
	@UiField
	ObidosIntegerTextBox minimumPPUppercaseTextBox;
	
	@UiField
	ObidosIntegerTextBox minimumPPNumericTextBox;
	
	@UiField
	ObidosIntegerTextBox minimumPPSpecialTextBox;
	
	@UiField
	ObidosIntegerTextBox minPassphraseEntropyTextBox;

	@UiField
	FormLabel passphraseEntropyLengthWarningLabel;
	
	@UiField
	Select dateFormatSelect;

	@UiField
	FormLabel dateFormatLabel;

	@UiField
	ListBox passwordStrengthList;

	@UiField
	ListBox passphraseStrengthList;
	
	@UiField
	ObidosTextBox documentDirTextBox;

	@UiField
	FormLabel documentDirLabel;
	

	@Inject
	SystemSettingsView(Binder uiBinder)
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
		passwordStrengthList.addItem(glang.weak());
		passwordStrengthList.addItem(glang.strengthStrong());
		passwordStrengthList.addItem(glang.strengthVeryStrong());

		passphraseStrengthList.addItem(glang.weak());
		passphraseStrengthList.addItem(glang.strengthStrong());
		passphraseStrengthList.addItem(glang.strengthVeryStrong());
	}
	
	@UiHandler("saveButton")
	void onclickSaveButton (ClickEvent e)
	{
		getUiHandlers().save();
	}
	
	@UiHandler("resetButton")
	void onclickResetButton (ClickEvent e)
	{
		getUiHandlers().resetForm(ObidosMessages.LANG.formResetSuccessfully());
	}
	
	@UiHandler("twoFAIssuerTextBox")
	void onKeyUpTwoFAIssuerTextBox (KeyUpEvent e)
	{
		getUiHandlers().show2FAUssuerChange();
	}
	
	@UiHandler("httpFqdnTextBox")
	void onclickFqdnTextBox (KeyUpEvent e)
	{
		getUiHandlers().showHttpsFqdnChange();
	}
	
	@UiHandler("httpsPortBox")
	void onclickHttpsPortBox (KeyUpEvent e)
	{
		getUiHandlers().showHttpsPortChange();
	}
	
	@UiHandler("pwMinLengthTextBox")
	void onclickpwMinLengthTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordMinLengthChagne();
	}
	
	@UiHandler("minimumLowercaseTextBox")
	void onclickminimumLowercaseTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordMinLowercaseChange();
	}
	
	@UiHandler("minimumUppercaseTextBox")
	void onclickminimumUppercaseTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordUppercaseChange();
	}
	
	@UiHandler("minimumNumericTextBox")
	void onclickminimumNumericTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordNumericChange();
	}
	
	@UiHandler("minimumSpecialTextBox")
	void onclickminimumSpecialTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordSpecialChange();
	}
	
	@UiHandler("maxPasswordAgeTextBox")
	void onTypeMaxPasswordAgeTextBox(KeyUpEvent e)
	{
		getUiHandlers().showMaxPasswordInDaysChange();
	}
	
	@UiHandler("minPasswordEntropyTextBox")
	void onTypeminPasswordEntropyTextBox(KeyUpEvent e)
	{
		getUiHandlers().showPasswordEntropyChange();
	}
	
	// Passphrase
	@UiHandler("mininumPPLengthTextBox")
	void onclickmininumPPLengthTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPassphraseMinLengthChagne();
	}
	
	@UiHandler("minimumPPLowercaseTextBox")
	void onclickminimumPPLowercaseTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPassphraseMinLowercaseChange();
	}
	
	@UiHandler("minimumPPUppercaseTextBox")
	void onclickminimumPPUppercaseTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPassphraseUppercaseChange();
	}
	
	@UiHandler("minimumPPNumericTextBox")
	void onclickminimumPPNumericTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPassphraseNumericChange();
	}
	
	@UiHandler("minimumPPSpecialTextBox")
	void onclickminimumPPSpecialTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPassphraseSpecialChange();
	}
	
	@UiHandler("minPassphraseEntropyTextBox")
	void onclickminPassphraseEntropyTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPassphraseEntropyChange();
	}
	
	@UiHandler("dateFormatSelect")
	void onclickDateFormSelect (ValueChangeEvent<String> e)
	{
		getUiHandlers().showDateFormatSelectionChange();
	}
	
	@UiHandler("documentDirTextBox")
	void onTypeDocumentDirTextBox (KeyUpEvent e)
	{
		getUiHandlers().showDataStoreChange();
	}
	

	@UiHandler("passwordStrengthList")
	void onclickPasswordStrengthListSelected (ChangeEvent e)
	{
		getUiHandlers().passwordStrengthListBoxHandler();
	}

	@UiHandler("passphraseStrengthList")
	void onclickPassphraseStrengthListSelected (ChangeEvent e)
	{
		getUiHandlers().passphraseStrengthListBoxHandler();
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Button getSaveButton()
	{
		return saveButton;
	}

	public TextBox getTwoFAIssuerTextBox()
	{
		return twoFAIssuerTextBox;
	}

	public FormLabel getTwoFAIssuerLabel()
	{
		return twoFAIssuerLabel;
	}

	public Button getResetButton()
	{
		return resetButton;
	}

	public TextBox getHttpFqdnTextBox()
	{
		return httpFqdnTextBox;
	}

	public ObidosIntegerTextBox getHttpsPortBox()
	{
		return httpsPortBox;
	}

	public FormLabel getFqdnLabel()
	{
		return fqdnLabel;
	}

	public FormLabel getPortLabel()
	{
		return portLabel;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public FormLabel getMaxPasswordAgetLabel()
	{
		return maxPasswordAgetLabel;
	}

	public ObidosIntegerTextBox getMaxPasswordAgeTextBox()
	{
		return maxPasswordAgeTextBox;
	}

	public ObidosIntegerTextBox getPwMinLengthTextBox()
	{
		return pwMinLengthTextBox;
	}

	public ObidosIntegerTextBox getMinimumLowercaseTextBox()
	{
		return minimumLowercaseTextBox;
	}

	public ObidosIntegerTextBox getMinimumUppercaseTextBox()
	{
		return minimumUppercaseTextBox;
	}

	public ObidosIntegerTextBox getMinimumNumericTextBox()
	{
		return minimumNumericTextBox;
	}

	public ObidosIntegerTextBox getMinimumSpecialTextBox()
	{
		return minimumSpecialTextBox;
	}

	public ObidosIntegerTextBox getMinPasswordEntropyTextBox()
	{
		return minPasswordEntropyTextBox;
	}

	public FormLabel getMinPasswordEntropyLabel()
	{
		return minPasswordEntropyLabel;
	}

	public FormLabel getMinPasswordLengthLabel()
	{
		return minPasswordLengthLabel;
	}

	public FormLabel getMinPasswordLowercaseLabel()
	{
		return minPasswordLowercaseLabel;
	}

	public FormLabel getMinPasswordUppercaseLabel()
	{
		return minPasswordUppercaseLabel;
	}

	public FormLabel getMinPasswordNumericCharLabel()
	{
		return minPasswordNumericCharLabel;
	}

	public FormLabel getMinPasswordSpecialCharLabel()
	{
		return minPasswordSpecialCharLabel;
	}

	public FormLabel getPasswordLengthWarningLabel()
	{
		return passwordLengthWarningLabel;
	}

	public FormLabel getPasswordEntropyLengthWarningLabel()
	{
		return passwordEntropyLengthWarningLabel;
	}

	public ObidosReadonlyTextBox getHttpSchemeTextBox()
	{
		return httpSchemeTextBox;
	}
	
	//
	public FormLabel getMinLengthPPLabel()
	{
		return minLengthPPLabel;
	}

	public FormLabel getMinLowerPPLabel()
	{
		return minLowerPPLabel;
	}

	public FormLabel getMinUpperPPLabel()
	{
		return minUpperPPLabel;
	}

	public FormLabel getMinNumberPPLabel()
	{
		return minNumberPPLabel;
	}

	public FormLabel getMinSpecialPPLabel()
	{
		return minSpecialPPLabel;
	}

	public FormLabel getMinPPEntropyLabel()
	{
		return minPPEntropyLabel;
	}

	public ObidosIntegerTextBox getMininumPPLengthTextBox()
	{
		return mininumPPLengthTextBox;
	}

	public ObidosIntegerTextBox getMinimumPPLowercaseTextBox()
	{
		return minimumPPLowercaseTextBox;
	}

	public ObidosIntegerTextBox getMinimumPPUppercaseTextBox()
	{
		return minimumPPUppercaseTextBox;
	}

	public ObidosIntegerTextBox getMinimumPPNumericTextBox()
	{
		return minimumPPNumericTextBox;
	}

	public ObidosIntegerTextBox getMinimumPPSpecialTextBox()
	{
		return minimumPPSpecialTextBox;
	}

	public ObidosIntegerTextBox getMinPassphraseEntropyTextBox()
	{
		return minPassphraseEntropyTextBox;
	}

	public FormLabel getPassphraseLengthWarningLabel()
	{
		return passphraseLengthWarningLabel;
	}

	public FormLabel getPassphraseEntropyLengthWarningLabel()
	{
		return passphraseEntropyLengthWarningLabel;
	}

	public Select getDateFormatSelect()
	{
		return dateFormatSelect;
	}

	public FormLabel getDateFormatLabel()
	{
		return dateFormatLabel;
	}

	public ObidosMessages getGlang()
	{
		return glang;
	}

	public ListBox getPasswordStrengthList()
	{
		return passwordStrengthList;
	}

	public ListBox getPassphraseStrengthList()
	{
		return passphraseStrengthList;
	}

	public ObidosTextBox getDocumentDirTextBox()
	{
		return documentDirTextBox;
	}

	public FormLabel getDocumentDirLabel()
	{
		return documentDirLabel;
	}
}