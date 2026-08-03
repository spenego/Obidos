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

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;

import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.client.ui.html.Span;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.DicewareService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DicewareDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.UserDTO;

public class KeyPairPresenter extends Presenter<KeyPairPresenter.MyView, KeyPairPresenter.MyProxy>
		implements KeyPairUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
   	private PassComplexityDTO gPassComplexityDTO = null;

   	// Bug #81
   	// delay between key strokes so the backend will not be overwhelmed
   	// spgdev@spenego.com - Dec 7, 2024
   	private Timer passphraseStrengthCheckTimer = null;
   	private String lastPassphrase = "";

			
	interface MyView extends View, HasUiHandlers<KeyPairUiHandlers>
	{
		public Span getGeneratedPassPhraseSpan();
		public ListBox getLanguageListBox();
		public Button getGenStrongPassPhraseButton();
		public ObidosPasswordBox getPassphraseBox();
		public ObidosPasswordBox getConfirmPassphraseBox();
		public InlineCheckBox getUppercaseCheckBox();
		public InlineCheckBox getNoSpacesCheckBox();
		public Button getGenKeypairButton();
		public Collapse getGenCollapse();
		public InlineCheckBox getAddSpecialCharCheckBox();
		public ListBox getNumberOfWordsListBox();
        public com.google.gwt.user.client.ui.Label getPassphraseStrengthLabel();
        public Progress getPassphraseStrengthProgress();
        public ProgressBar getPassphraseStrengthBar();
        public ObidosMessageRow getMessageRow();
        public ListGroup getPassphraseRequirementListGroup();
        public Label getNcharsLabel();
        public HTMLPanel getProcessingPanel();
	}

	@NameToken(NameTokens.KEY_PAIR)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<KeyPairPresenter>
	{
	}

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

	@Inject
	KeyPairPresenter(EventBus eventBus, MyView view, MyProxy proxy,
	        PlaceManager placeManager,
	        CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

		this.placeManager = placeManager;
		this.currentUser = currentUser;

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		gwtLog("onBind");
		super.onBind();
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		resetForm();
		clearTimers();
		showProcessing(false);
	}

	protected void onUnbind()
	{
		gwtLog("onUnbind");
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showProcessing(false);
		showMessage(null);

		setFocus();
		// GWT has a bug, the item text can not be set in ui.xml from properties file
		// so we've to do it here, ugly!
		initializeLanguageListBox();
		initializeNumberOfWordsListBox();

		resetForm();
		showHidePassphraseStrengthWidgets();
		disableSubmitButton();
		fetchAndDisplayLocalPassphrasePolicy();
	}

    private void clearTimers()
    {
    	lastPassphrase = "";
    	if (passphraseStrengthCheckTimer != null)
    	{
    		passphraseStrengthCheckTimer.cancel();
    		passphraseStrengthCheckTimer = null;
    	}
    }

	private void setFocus()
	{
	    getView().getPassphraseBox().setFocus(true);
	}

	private void initializeNumberOfWordsListBox()
	{
	    ListBox lBox = getView().getNumberOfWordsListBox();
	    int size = lBox.getItemCount();
	    gwtLog("NUmber of items: " + size);
        lBox.setItemText(0, ObidosMessages.LANG.keyPairNumbeOfOnes6());
        lBox.setItemText(1, ObidosMessages.LANG.keyPairNumbeOfOnes7());
        lBox.setItemText(2, ObidosMessages.LANG.keyPairNumbeOfOnes8());
        lBox.setItemText(3, ObidosMessages.LANG.keyPairNumbeOfOnes9());
        lBox.setItemText(4, ObidosMessages.LANG.keyPairNumbeOfOnes10());

	}
	private void initializeLanguageListBox()
	{
	    ListBox lBox = getView().getLanguageListBox();
	    int size = lBox.getItemCount();
	    gwtLog("NUmber of items: " + size);
        lBox.setItemText(0, ObidosMessages.LANG.keyPairLanguageEnglish());
        lBox.setItemText(1, ObidosMessages.LANG.keyPairLanguageGerman());
	}

	private void resetForm()
	{
		showMessage("");
	    getView().getPassphraseBox().setValue("");
	    getView().getConfirmPassphraseBox().setValue("");
        getView().getNcharsLabel().setText(glang.zero());
	    resetProgressbarStrength();
	}

	private void disableSubmitButton()
	{
	    getView().getGenKeypairButton().setEnabled(false);
	}

	private void enableSubmitButton()
	{
	    getView().getGenKeypairButton().setEnabled(true);
	}

	private void showHidePassphraseStrengthWidgets()
	{
		/*
	    String passphrase = getView().getPassphraseBox().getValue();
	    Label label = getView().getPassphraseStrengthLabel();
	    Progress progress = getView().getPassphraseStrengthProgress();
	    ProgressBar progressBar = getView().getPassphraseStrengthBar();

	    if (passphrase == null || (passphrase != null && passphrase.length() == 0))
	    {
	        label.setVisible(false);
	        progress.setVisible(false);
	        progressBar.setVisible(false);
	        disableSubmitButton();
	    }
	    else
	    {
	        label.setVisible(true);
	        progress.setVisible(true);
	        progressBar.setVisible(true);
	    }
	    */
	}

	@Override
	public void generateStrongPassPhrase()
	{
		showMessage("");
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
//    	String userName = authCreds.getUsername();
    	String userName = currentUser.getUserDTO().getUsername();
    	gwtLog("Gen strong passpharse: " + userName);
    	String xsrfToken = authCreds.getXsrfToken();
    	if (xsrfToken == null)
    	{
    		showErrorMessage(ObidosMessages.LANG.sessionHasExpired()); // ??? look at it again, do we still do the stupid xsrfToken thing?
    		return;
    	}

		DicewareDTO dto = new DicewareDTO();
		dto.setLanguage(ObidosConstants.DICEWARE_ENGLISH);
		int idx = getView().getLanguageListBox().getSelectedIndex();
		if (idx == 0) // English
		{
			dto.setLanguage(ObidosConstants.DICEWARE_ENGLISH);
		}
		else if (idx == 1) // German
		{
			dto.setLanguage(ObidosConstants.DICEWARE_GERMAN);
		}

		String numberOfWords = getView().getNumberOfWordsListBox().getSelectedValue();
		int n = 6;

		if (numberOfWords != null && numberOfWords.length() > 0)
			n = Integer.parseInt(numberOfWords);


		if (n < 6)
		{
			showErrorMessage("Minimum number of words is 6");
			return;
		}
		if (n > 10)
		{
			showErrorMessage("Maximum number of words is 10");
			return;
		}
		dto.setNumberOfWords(n);

		dto.setUppercaseWords(getView().getUppercaseCheckBox().getValue());
		dto.setNoSpaces(getView().getNoSpacesCheckBox().getValue());
		dto.setAddSpecialChar(getView().getAddSpecialCharCheckBox().getValue());

		GwtAsyncWrapper<String> callback = new GwtAsyncWrapper<String>(this)
		{

			@Override
			public void uponFailure(Throwable e)
			{
				showErrorMessage("Could not generate pass phrase: " + e.getMessage());
			}

			@Override
			public void uponSuccess(String passPhrase)
			{
			    getView().getGeneratedPassPhraseSpan().setText(passPhrase);
			}
		};
		DicewareService.Utility.getInstance().genStrongPassPhrase(authCreds, dto, callback);
	}

	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}

    private void showProcessing(final boolean show)
    {
    	getView().getProcessingPanel().setVisible(show);
    }

	// Bug #81
    @Override
    public void showPassphraseStrength()
    {
    	showProcessing(true);
    	if (passphraseStrengthCheckTimer != null)
    	{
    		passphraseStrengthCheckTimer.cancel();
    	}
    	passphraseStrengthCheckTimer = new Timer() {
			@Override
			public void run()
			{
				gwtLog("MMM passphrase strength key up handler ...");
				showPassphraseStrengthReal();
				showProcessing(false);
			}
    	};
    	passphraseStrengthCheckTimer.schedule(ObidosConstants.KEY_STROKE_DELAY_FOR_STRENGTH_CHECK);
    }

	@Override
	public void showPassphraseStrengthPasted()
	{
        String passphrase = getView().getPassphraseBox().getValue();
        if (passphrase == null || passphrase.length() == 0)
        {
        	return;
        }
        if (lastPassphrase.equals(passphrase))
        {
        	gwtLog("MMM passprhase did not change, no need to get strength");
        	return;
        }
        showPassphraseStrengthReal();
	}

    public void showPassphraseStrengthReal()
    {
    	int len = showNumberOfCharactersInPassword();
    	if (len <= 0)
    	{
    		resetProgressbarStrength();
    	}
    	PassComplexityDTO pcdto = getPassComplexityDTO();
    	if (pcdto != null)
    	{
    		if (len < pcdto.getPassphraseComplexityRequirements().getMinimumLength())
    		{
    			resetProgressbarStrength();
    			showMessage(null);
    			return;
    		}
    	}

        showHidePassphraseStrengthWidgets();
        showMessage("");
        String passphrase = getView().getPassphraseBox().getValue();
        if (passphrase == null)
        {
   			showMessage(null);
            return;
        }
        if (passphrase.length() == 0)
        {
   			showMessage(null);
            return;
        }
        lastPassphrase = passphrase;

        GwtAsyncWrapper<PasswordAnalysisResults> callback = new GwtAsyncWrapper<PasswordAnalysisResults>(this)
        {

            @Override
            public void uponFailure(Throwable t)
            {
                showErrorMessage("Error calculating passphrase strength: " + t.getMessage());
            }

            @Override
            public void uponSuccess(PasswordAnalysisResults result)
            {
                int score = result.getPasswordScore();
                showMessage("");
            	String localRequirementsMessage = result.getLocalRequirementsNotMetMessage();
            	if (localRequirementsMessage != null)
            	{
            		showErrorMessage(localRequirementsMessage);
            	}

                // ignore suggestions
                ProgressBar bar = getView().getPassphraseStrengthBar();
                boolean strong = false;
                switch(score)
                {
                    case PasswordAnalysisResults.PASSWORD_WEAK:
                    {
                        bar.setPercent(20);
                        bar.setText(glang.weak());
                        bar.setType(ProgressBarType.DANGER);
                        strong = false;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_SO_SO:
                    {
                        bar.setPercent(21);
                        bar.setText(glang.soso());
                        bar.setType(ProgressBarType.DANGER);
                        strong = false;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_GOOD:
                    {
                        bar.setPercent(60);
                        bar.setText(glang.good());
                        bar.setType(ProgressBarType.INFO);
                        strong = false;
                        break;

                    }
                    case PasswordAnalysisResults.PASSWORD_STRONG:
                    {
                        bar.setPercent(80);
                        bar.setText(glang.strong());
                        bar.setType(ProgressBarType.INFO);
                        strong = true;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_VERY_STRONG:
                    {
                        bar.setPercent(100);
                        bar.setText(glang.veryStrong());
                        bar.setType(ProgressBarType.SUCCESS);
                        strong = true;
                        break;
                    }
                }
                if (strong)
                {
                    enableSubmitButton();
                }
                else
                {
                    disableSubmitButton();
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        LoginService.Utility.getInstance().checkPassStrength(authCreds, passphrase, PASSPHRASE_COMPLEXITY_REQUIREMENTS, callback);
    }
    
    private void resetProgressbarStrength()
    {
        ProgressBar bar = getView().getPassphraseStrengthBar();
		bar.setPercent(20);
		bar.setText(glang.weak());
		bar.setType(ProgressBarType.DANGER);
    }
    
    private boolean promptFor2FA()
    {
		UserDTO dto = currentUser.getUserDTO();
		boolean twoFactorRequired = ClientUtils.fromBoolean(dto.getTwoFARequired());
		boolean twoFactorEnabled = ClientUtils.fromBoolean(dto.getTwoFAPasswordResetEnabled());
		String message = ObidosMessages.LANG.twoFactorNotEnabledWarning();

		// required but not enabled. User will not be able to reset
		// password or passphrase
		gwtLog("2FA required: " + twoFactorRequired);
		gwtLog("2FA enabled: " + twoFactorEnabled);
		if (twoFactorRequired && !twoFactorEnabled)
		{
			// required but not enabled
			message = ObidosMessages.LANG.twoFactorRequiredButNotEnabledWarning();
			gwtLog(message);
		}
		
		if (!twoFactorRequired && !twoFactorEnabled)
		{
			// not required and not enabled, this is deadly!
			// black hat can reset password or passphrase  if the
			// user's email account is compromised.
			message = ObidosMessages.LANG.twoFactorNotEnabledWarning();
			gwtLog(message);
		}

		if (twoFactorRequired && !twoFactorEnabled)
		{
			promptToEnable2FA(message);
			return true;
		}
		else
		{
           	showListContainersPage();
		}

    	return true;
    }

    @Override
    public void generateAndSaveKeyPair()
    {
        String passphrase = getView().getPassphraseBox().getValue();
        String confirmPassphrase = getView().getConfirmPassphraseBox().getValue();
        if (passphrase == null)
        {
            showErrorMessage(ObidosMessages.LANG.passphraseEmptyWarning());
            return;
        }
        if (confirmPassphrase == null || confirmPassphrase.length() == 0)
        {
            showErrorMessage(ObidosMessages.LANG.confirmPassphraseWarning());
            return;
        }

        if (!passphrase.equals(confirmPassphrase))
        {
            showErrorMessage(ObidosMessages.LANG.passphraseMismatchWarning());
            return;
        }
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(ObidosMessages.LANG.errorCreatingKeyPair() + caught.getMessage());
            }

            @Override
            public void uponSuccess(Void result)
            {
                showMessage(ObidosMessages.LANG.keyPairCreatedSuccessfully());
                // Can not seem to cache passphrase after creation of the pair.
                // At this time I will prompt to logout if it is hard to fix in server side.
                // May-11-2019
                gwtLog("Passphrase cached before: " + currentUser.getPassphraseRegistered());
                cachePassphrase(passphrase.getBytes());
                currentUser.getUserDTO().setKeyPairExists(true);
                gwtLog("Passphrase cached after: " + currentUser.getPassphraseRegistered());
                // show list of containers page
            	boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
            	if (rc)
            	{
            		gwtLog("YYYYYYYYYYYYYYYYYY passphrase cached.......");
            	}
            	else
            	{
            		// possible due to async nature of rpc calls
            		gwtLog("XXX passhrase is not cached...how come?");
            	}
            	promptFor2FA();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().createKeypair(authCreds, passphrase.getBytes(), callback);
    }
    
    @Override
    public void onConfirmPassphraseKeyUp()
    {
        showMessage("");
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    private void cachePassphrase(byte[] passphrase)
    {
    	String nameToken = NameTokens.LIST_CONTAINERS;
        ClientUtils.cachePassphrase(this.getClass().getSimpleName(), placeManager, currentUser, getView().getMessageRow(), passphrase, nameToken);
    }
    
    private void showListContainersPage()
    {
       	ClientUtils.showPage(placeManager, NameTokens.LIST_CONTAINERS);
    }
    
    private void promptToEnable2FA(String message)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	String title = lang.warning();

        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
            	// catch Esc or click on x
            	promptToEnable2FA(message);
            	return;
            }
        });

        // No
       options.addButton(lang.enable2FALater(), ButtonType.DANGER.getCssName(), new SimpleCallback()
       {
            @Override
            public void callback()
            {
            	showListContainersPage();
            }
       });

        // Yes
        options.addButton(lang.enable2FANow(), ButtonType.SUCCESS.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
            	String nameToken = NameTokens.TWO_FACTOR;
            	String place = ClientUtils.getPlaceFromUrl(placeManager);
            	if (place != null)
            	{
            		Map<String,String> with = new HashMap<>();
            		with.put(ObidosConstants.PLACE, place);
            		ClientUtils.showPage(placeManager, nameToken, with);
            	}
            	else
            	{
            		ClientUtils.showPage(placeManager, nameToken);
            	}
            	/*
            	Map<String,String> with = ClientUtils.makeMapFromUrl(tokenFormatter);
            	if (with != null)
            	{
            		ClientUtils.showPage(placeManager, nameToken, with);
            	}
            	else
            	{
            		ClientUtils.showPage(placeManager, nameToken);
            	}
            	*/

            }
        });
        options.setAnimate(true);
        Bootbox.dialog(options);
    }
    public int showNumberOfCharactersInPassword()
    {
        String passphrase = getView().getPassphraseBox().getValue();
        int len = 0;
        if (passphrase == null || passphrase.length() == 0)
        {
            len = 0;
        }
        else
        {
            len = passphrase.length();
        }
        getView().getNcharsLabel().setText(Integer.toString(len));
        return len;
    }

	private void setPassComplexityDTO(PassComplexityDTO dto)
	{
		gPassComplexityDTO = dto;
	}
	
	private PassComplexityDTO getPassComplexityDTO()
	{
		return gPassComplexityDTO;
	}

	private void fetchAndDisplayLocalPassphrasePolicy()
	{
		setPassComplexityDTO(null);
		GwtAsyncWrapper<PassComplexityDTO> callback = new GwtAsyncWrapper<PassComplexityDTO>(this)
		{

			@Override
			public void uponSuccess(PassComplexityDTO dto)
			{
				setPassComplexityDTO(dto);
				ClientUtils.displayPassphrasePolicy(getView().getPassphraseRequirementListGroup(), dto);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch local Passphrase Policy: " + caught.getMessage());
			}
		};
		UserService.Utility.getInstance().getPassComplexity(null, callback);
	}


}
