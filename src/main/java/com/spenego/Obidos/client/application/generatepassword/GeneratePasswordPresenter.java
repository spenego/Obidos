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

import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosGuessesPerSecondTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.DicewareService;
import com.spenego.Obidos.client.rpc.GenPassService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.HaveIBeenPwnedService;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DicewareDTO;
import com.spenego.Obidos.shared.dto.GenPassDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;

public class GeneratePasswordPresenter
		extends Presenter<GeneratePasswordPresenter.MyView, GeneratePasswordPresenter.MyProxy>
		implements GeneratePasswordUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private PasswordAnalysisResults gPasswordAnalysisResult;

   	// Bug #81
   	// delay between key strokes so the backend will not be overwhelmed
   	// spgdev@spenego.com - Dec 7, 2024
   	private Timer passwordStrengthCheckTimer = null;
   	/*
   	private Timer passwordStrengthCheckTimer = new Timer() {
   	    @Override
   	    public void run() {
   	        gwtLog("MMM strength key up handler ...");
   	        showStrengthReal();
   	    }
   	};
   	*/
   	private String lastPassword = "";
   	
	private enum Algorithm
	{
		PRONOUNCEABLE,
		DICEWARE,
		SECURE_RANDOM,
		UNKNOWN
	}

	interface MyView extends View, HasUiHandlers<GeneratePasswordUiHandlers>
	{
		public PanelBody getPanelBody();
		public Button getGeneratePasswordRepeatButton();
		public Button getCopyToClipboardButton();
		public ObidosTextBox getGeneratePasswordBox();
		public Button getHibpButton();
		public ObidosMessageRow getMessageRow();
		public ProgressBar getPasswordStrengthBar();
		public Select getAlgorithmSelect();
		public Select getRollDieceSelect();
		public ObidosRow getRollDiceRow();
		public CheckBox getCapitalizeCheckBox();
		public CheckBox getNumberCheckBox();
		public CheckBox getSymbolCheckBox();
		public Row getModifierRow();
		public ObidosRow getPassLenRow();
		public ObidosIntegerTextBox getPasswordLengthTextBox();
		public ObidosReadonlyTextBox getEntropyTextBox();
		public FormLabel getPasswordNearProgressBarLabel();
		public ObidosGuessesPerSecondTextBox getGuessPerSecondTextBox();
		public ObidosReadonlyTextBox getEstimatedCrackingTimeTextBox();
		public ObidosReadonlyTextBox getRawEntropyTextBox();
		public BlockQuote getHelpBlockQuote();

		public Row getShowEntropyRow();
		public ObidosRow getHaveIbeenPawnedRow();
		public FormLabel getLongPasswordHintLabel();
		public FormLabel getRawEntropyLabel();
		public FormLabel getAdjustedEntropyLabelHint();
		public FormLabel getWaitLabel();
		public HTMLPanel getDotsPanel();
	}

	@NameToken(NameTokens.GENERATE_PASSWORD)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<GeneratePasswordPresenter>
	{
	}

	private final CurrentUser currentuser;
	@Inject
	GeneratePasswordPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		
		this.currentuser = currentUser;

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
	}

	protected void onReveal()
	{
		super.onReveal();
		ClientUtils.setCopytoClipboardButtonHander(
				getView().getCopyToClipboardButton(),
				getView().getGeneratePasswordBox());
	}

	protected void onHide()
	{
		super.onHide();
		resetThings();
		clearTimers();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showWaitLabel("");
		updateSelectLists();
		showHideRows();
		getView().getPasswordNearProgressBarLabel().setText("");
		ObidosGuessesPerSecondTextBox gtb = getView().getGuessPerSecondTextBox();
		getView().getGeneratePasswordBox().clear();
		adjustFloatForRTL();
		ClientUtils.adjustSelectWidgetLength(getView().getPanelBody(), getView().getRollDieceSelect());
	}

	private void clearTimers()
    {
		lastPassword = "";
    	if (passwordStrengthCheckTimer != null)
    	{
    		passwordStrengthCheckTimer.cancel();
    		passwordStrengthCheckTimer = null;
    	}
    }

	private void adjustCheckBoxForRTL(final CheckBox myCheckBox)
	{
		Element labelElement = null;
		NodeList<Element> labels = myCheckBox.getElement().getElementsByTagName("label");
		if (labels.getLength() > 0) {
			labelElement = labels.getItem(0);
			labelElement.getStyle().setProperty("paddingLeft","5px");
		}
		myCheckBox.getElement().getStyle().setProperty("float", "left");
		Element inputElement = null;
		NodeList<Element> elements = myCheckBox.getElement().getElementsByTagName("input");
		if (elements.getLength() > 0)
		{
			inputElement = elements.getItem(0);
			inputElement.getStyle().setProperty("marginRight","-20px");
		}
	}
	
	private void adjustFloatForRTL()
	{
		if (ClientUtils.isLocaleArabic())
		{
			getView().getLongPasswordHintLabel().getElement().getStyle().setProperty("float", "left");
			getView().getRawEntropyLabel().getElement().getStyle().setProperty("float", "left");
			getView().getPasswordNearProgressBarLabel().getElement().getStyle().setProperty("float", "left");
			getView().getAdjustedEntropyLabelHint().getElement().getStyle().setProperty("float", "left");

			// Capitalize
			adjustCheckBoxForRTL(getView().getCapitalizeCheckBox());
			// Number
			adjustCheckBoxForRTL(getView().getNumberCheckBox());
			// Special character
			adjustCheckBoxForRTL(getView().getSymbolCheckBox());
		}
	}
	
	private void resetThings()
	{
		showMessage(null);
		showWaitLabel("");
		gPasswordAnalysisResult = null;
		showEntropyRow(false);
		getView().getGeneratePasswordBox().clear();
	}

	public void setDefaultGuessesPerSecond(String defaultValue)
	{
		ObidosGuessesPerSecondTextBox gtb = getView().getGuessPerSecondTextBox();
	    try {
	        // Parse the string to a Long
	        Long guessesPerSecond = Long.parseLong(defaultValue);
	        
	        // Format the Long value for display
	        String formattedValue = NumberFormat.getFormat("#,##0").format(guessesPerSecond);
	        
	        // xet the value in the widget
	        gtb.setValue(guessesPerSecond);
	        gtb.setText(formattedValue);
	    } catch (NumberFormatException e) {
	    	gwtLog("Invalid default guess/sec number: " + e);
	    }
	}	
	private void updateSelectLists()
	{
		String[] algorithms  = {
			glang.defaultAlg(),
			glang.diceware(),
			glang.secureRandom()
		};
		Select s = getView().getAlgorithmSelect();
		s.clear();
		for (String algorithm: algorithms)
		{
			Option option = new Option();
			option.setText(algorithm);
			s.add(option);
		}
		s.setValue(glang.defaultAlg());
		s.refresh();
		
		Select dr = getView().getRollDieceSelect();
		dr.setValue("4");
		dr.refresh();
	}
	
	private void genPronounceablePass()
	{
		GenPassDTO dto = new GenPassDTO();
		Integer passLength = getView().getPasswordLengthTextBox().getValue();
		if (passLength == null)
		{
			showErrorMessage("Please specify a password length");
			return;
		}
		if (passLength <= 0)
		{
			showErrorMessage("Please specify a password length");
			return;
		}
		gwtLog("MMM Password length: " + passLength);
		
		dto.setPassLength(passLength);

		dto.setCapitalize(getView().getCapitalizeCheckBox().getValue());
		dto.setNumerals(getView().getNumberCheckBox().getValue());
		dto.setSymbols(getView().getSymbolCheckBox().getValue());

		GwtAsyncWrapper<String> callback = new GwtAsyncWrapper<String>(this)
		{

			@Override
			public void uponFailure(Throwable e)
			{
			    showEntropyRow(false);
				showErrorMessage("Could not generate pronounceable password: " + e.getMessage());
			}

			@Override
			public void uponSuccess(String password)
			{
			    getView().getGeneratePasswordBox().setValue(password);
			    showEntropyRow(true);
			    showStrength();
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		GenPassService.Utility.getInstance().genPronounceablePass(authCreds, dto, callback);
	}
	
	private void genDicewarePass()
	{
		DicewareDTO dto = new DicewareDTO();
		int nrolls = Integer.parseInt(getView().getRollDieceSelect().getSelectedItem().getValue());
		dto.setLanguage(ObidosConstants.DICEWARE_ENGLISH);
		dto.setNumberOfWords(nrolls);

		dto.setUppercaseWords(true);
		dto.setNoSpaces(true);
		dto.setAddSpecialChar(false);
		
		GwtAsyncWrapper<String> callback = new GwtAsyncWrapper<String>(this)
		{

			@Override
			public void uponFailure(Throwable e)
			{
			    showEntropyRow(false);
				showErrorMessage("Could not generate password: " + e.getMessage());
			}

			@Override
			public void uponSuccess(String password)
			{
			    getView().getGeneratePasswordBox().setValue(password);
			    showEntropyRow(true);
			    showStrength();
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		DicewareService.Utility.getInstance().genStrongPassPhrase(authCreds, dto, callback);
	}


	@Override
	public void generatePassword()
	{
		if (getSelectedAlgorithm() == Algorithm.DICEWARE)
		{
			genDicewarePass();
		}
		else if (getSelectedAlgorithm() == Algorithm.PRONOUNCEABLE)
		{
			genPronounceablePass();
		}
		else if (getSelectedAlgorithm() == Algorithm.SECURE_RANDOM)
		{
			generateSecureRandomPass();
		}
	}
	
	private void showEntropyRow(boolean visible)
	{
		getView().getShowEntropyRow().setVisible(visible);
		getView().getHaveIbeenPawnedRow().setVisible(visible);
		
	}
	
	private void generateSecureRandomPass()
	{
		GwtAsyncWrapper<String> callback = new GwtAsyncWrapper<String>(this)
		{

			@Override
			public void uponFailure(Throwable e)
			{
			    showEntropyRow(false);
				showErrorMessage("Could not generate secure random password: " + e.getMessage());
			}

			@Override
			public void uponSuccess(String password)
			{
				gwtLog("MMM SR Pass: " + password);
			    getView().getGeneratePasswordBox().setValue(password);
			    showEntropyRow(true);
			    showStrength();
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		GenPassService.Utility.getInstance().genSecureRandomPass(authCreds, callback);
	}

	@Override
	public void copyPasswordToClipboard()
	{
		gwtLog("MMM copy to clipboard");
		String text = getView().getGeneratePasswordBox().getValue();
		if (text != null && text.length() > 0)
		{
			gwtLog("MMM copy to clipboard");
			ClientUtils.copyTextToClipboard(text);
		}

	}
    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
//    	MessageAlert alert = new MessageAlert();
 //   	alert.showError(errorMessage);
//    	getView().getMessageAlert().showError(errorMessage);
    }


    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
//    	MessageAlert alert = new MessageAlert();
//    	alert.showMessage(message);
//    	getView().getMessageAlert().showMessage(message);
    }
    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }
	@Override
	public void showStrengthPasted()
	{	
		String password = getView().getGeneratePasswordBox().getValue();
		if (password == null || password.length() == 0)
		{
			return;
		}
		if (lastPassword.equals(password))
		{
			gwtLog("MMM password did not change, no need to get strength");
			return;
		}

		showStrengthReal();
	}

    // Bug #81
	@Override
	public void showStrength()
	{
		showWaitLabel(glang.generatingPleaseWait());
    	if (passwordStrengthCheckTimer != null)
    	{
    		passwordStrengthCheckTimer.cancel();
    	}
    	passwordStrengthCheckTimer = new Timer() {

			@Override
			public void run()
			{
				gwtLog("MMM strength key up handler ...");
				showStrengthReal();
				showWaitLabel("");
			}
    	};
    	passwordStrengthCheckTimer.schedule(ObidosConstants.KEY_STROKE_DELAY_FOR_STRENGTH_CHECK);
	}

	public void showStrengthReal()
	{
       	gPasswordAnalysisResult = null;
		showMessage(null);
		String password = getView().getGeneratePasswordBox().getValue();
		if (password == null || password.length() == 0)
		{
			resetProgressBar();
			return;
		}
		lastPassword = password;
		

        GwtAsyncWrapper<PasswordAnalysisResults> callback = new GwtAsyncWrapper<PasswordAnalysisResults>(this)
        {

            @Override
            public void uponFailure(Throwable t)
            {
            	gPasswordAnalysisResult = null;
                showErrorMessage("Error calculating password strength: " + t.getMessage());
            }

            @Override
            public void uponSuccess(PasswordAnalysisResults result)
            {
            	// save for info dialog
            	gPasswordAnalysisResult = result;
            	// set the estimated cracking time
            	getView().getEstimatedCrackingTimeTextBox().setText(result.getCrackingTime());
            	getView().getGuessPerSecondTextBox().setValue(result.getCrackingGuessPerSecond());
//            	gwtLog("MMM entropy: " + result.getEntropy());
//            	gwtLog("MMM ct: " + result.getCrackingTime());

                int score = result.getPasswordScore();
                showMessage("");
                String pass = getView().getGeneratePasswordBox().getValue();
              	getView().getPasswordNearProgressBarLabel().setText("");
                if (pass != null && pass.length() > 0)
                {
                	getView().getPasswordNearProgressBarLabel().setText(pass);
                }

            	String localRequirementsMessage = result.getLocalRequirementsNotMetMessage();
            	if (localRequirementsMessage != null)
            	{
            		//showErrorMessage(localRequirementsMessage);
            	}

                List<String> suggestions = result.getSuggestions();
                if (suggestions != null && suggestions.size() > 0)
                {
                    gwtLog("Sugestion size: " + suggestions.size());
                }
                int adjustedEntropy = 0;
                ProgressBar bar = getView().getPasswordStrengthBar();
                boolean enable = false;
                // entropy is a floating point number
                Float re = result.getRawEntropy();
                if (re != null)
                {
                	gwtLog("MMM raw entropy: " + re);
                	int rawEntropy = Math.round(re);
                	String rawEntropyRoundedStr = rawEntropy + "";
                	String rawEntropyRealStr = re + ""; // show as tooltip
                	resetRawEntropyTextBox(rawEntropyRoundedStr, rawEntropyRealStr);
                }
                
                Float e = result.getEntropy();
                if (e != null)
                {
                	adjustedEntropy = Math.round(e);
                	gwtLog("Entropy: " + e);
                	String entropyRoundedStr = adjustedEntropy + "";
                	String entropyRealStr = e + ""; // show as tooltip
                	resetEntropyTextBox(entropyRoundedStr, entropyRealStr);
                }
                else
                {
                	resetEntropyTextBox(null, null);
                }
                
                // Claude AI came up with the entropy range after looking at
                // our techniquesa and  usage of Argon2id. I did not set in the 
                // server side as it will probably break password/passphrase
                // change code.
                // Sep-22-2024
                ClientUtils.setPasswordStrengthProgressBar(adjustedEntropy, bar);
            }
        };
//        Long crackingGuessesPerSecond = getView().getGuessPerSecondTextBox().getValue();
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        //LoginService.Utility.getInstance().checkPassStrength(authCreds, password, PASSWORD_COMPLEXITY_REQUIREMENTS, crackingGuessesPerSecond, callback);
        LoginService.Utility.getInstance().checkPassStrengthForGeneratedPassword(authCreds, password, callback);
    }
	
	private Algorithm getSelectedAlgorithm()
	{
		Select select = getView().getAlgorithmSelect();
		if (glang.defaultAlg().compareToIgnoreCase(select.getSelectedItem().getValue()) == 0)
		{
			return Algorithm.PRONOUNCEABLE;
		}
		else if (glang.diceware().compareToIgnoreCase(select.getSelectedItem().getValue()) == 0)
		{
			return Algorithm.DICEWARE;
		}
		else if (glang.secureRandom().compareToIgnoreCase(select.getSelectedItem().getValue()) == 0)
		{
			// Secure random
			return Algorithm.SECURE_RANDOM;
		}
		return Algorithm.UNKNOWN;
	}
    private void resetProgressBar()
    {
        ProgressBar bar = getView().getPasswordStrengthBar();
		bar.setPercent(20);
		bar.setText(glang.weak());
		bar.setType(ProgressBarType.DANGER);
    }

    private void resetRawEntropyTextBox(String rawEntropyRounded, String rawEntropyReal)
    {
		ObidosReadonlyTextBox rawEntropyTextBox = getView().getRawEntropyTextBox();
   		rawEntropyTextBox.clear();
    	if (rawEntropyRounded != null)
    	{
    		rawEntropyTextBox.setValue(rawEntropyRounded);
    	}
    	else
    	{
    		rawEntropyTextBox.setValue("");
    	}
    	if (rawEntropyReal != null)
    	{
    		rawEntropyTextBox.setTitle(rawEntropyReal);
    	}
    	else
    	{
    		rawEntropyTextBox.setTitle("");
    	}
    }
   
    private void resetEntropyTextBox(String entropyRounded, String entropyReal)
    {
		ObidosReadonlyTextBox entropyTextBox = getView().getEntropyTextBox();
   		entropyTextBox.clear();
    	if (entropyRounded != null)
    	{
    		entropyTextBox.setValue(entropyRounded);
    	}
    	else
    	{
    		entropyTextBox.setValue("");
    	}
    	if (entropyReal != null)
    	{
    		entropyTextBox.setTitle(entropyReal);
    	}
    	else
    	{
    		entropyTextBox.setTitle("");
    	}
    }

	private void resetPasswordBox()
	{
		getView().getGeneratePasswordBox().setValue(null);
	}
	
	private void resetFields()
	{
		showMessage(null);
		resetPasswordBox();
		resetPasswordBox();
		resetEntropyTextBox(null, null);
		resetRawEntropyTextBox(null, null);
		getView().getPasswordNearProgressBarLabel().setText("");
		showEntropyRow(false);
		showMessage(null);
	}

	private void showHideRows()
	{
		ObidosRow rollDiceRow = getView().getRollDiceRow();
		Row modifierRow       = getView().getModifierRow();
		ObidosRow passLenRow  = getView().getPassLenRow();
		
		resetEntropyTextBox(null, null);
		resetProgressBar();

		rollDiceRow.setVisible(true);
		modifierRow.setVisible(true);
		passLenRow.setVisible(true);
		
		if (getSelectedAlgorithm() == Algorithm.PRONOUNCEABLE)
		{
			// Default algorithm
			passLenRow.setVisible(true);
			rollDiceRow.setVisible(false);
			modifierRow.setVisible(true);
		}
		else if (getSelectedAlgorithm() == Algorithm.DICEWARE)
		{
			// Diceware Algorithm
			passLenRow.setVisible(false);
			rollDiceRow.setVisible(true);
			modifierRow.setVisible(false);
//			ClientUtils.adjustSelectWidgetLength(getView().getPanelBody(), getView().getRollDieceSelect());
		}
		else if (getSelectedAlgorithm() == Algorithm.SECURE_RANDOM)
		{
			// Secure random
			passLenRow.setVisible(false);
			rollDiceRow.setVisible(false);
			modifierRow.setVisible(false);
		}
	}
	

	@Override
	public void algorithmSelectCallback()
	{
		showHideRows();
		resetFields();
		// do not generate password
		// Click Generate button instead
	}

	@Override
	public void diceRollCallback()
	{
		resetFields();
		// do not generate passwords
		// Click Generate button instead
	}

	@Override
	public void showEntropyInfo()
	{
		String pass = getView().getGeneratePasswordBox().getValue();
		if (pass == null || pass.length() == 0)
		{
			return;
		}
				
		PasswordAnalysisResults r = gPasswordAnalysisResult;
		if (r == null)
		{
//			showErrorMessage("Error displaying password strength informatin");
			return;
		}
		ClientUtils.showPasswordAnalysisResult(pass, r);
	}

	@Override
	public void checkWithHaveIBeenPwned()
	{
		String newPass = getView().getGeneratePasswordBox().getValue();
		if (newPass == null || newPass.length() == 0)
		{
//			showErrorMessage(ObidosMessages.LANG.specifyNewPassword());
			return;
		}
		if (passwordStrengthCheckTimer != null)
		{
			passwordStrengthCheckTimer.cancel();
			passwordStrengthCheckTimer = null;
		}
		GwtAsyncWrapper<String> callback = new GwtAsyncWrapper<String>(this)
		{

			@Override
			public void uponSuccess(String n)
			{
				if (n != null && n.length() > 0)
				{
					showErrorMessage("Password found " + n + " times in breached list of passwords");
				}
				else
				{
					showMessage("Password is not in the breached list of passwords");
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not check password status with HaveIBeenPwned.com: "+ caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		HaveIBeenPwnedService.Utility.getInstance().passwordFound(authCreds, newPass, callback);
	}
	
	
	private void showWaitLabel(final String html)
	{
		getView().getWaitLabel().setHTML("");
		boolean b = html.length() > 0;
		getView().getDotsPanel().setVisible(b);
	}

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

}