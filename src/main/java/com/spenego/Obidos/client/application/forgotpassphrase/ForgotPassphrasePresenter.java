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

package com.spenego.Obidos.client.application.forgotpassphrase;

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.user.client.ui.FlowPanel;
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

public class ForgotPassphrasePresenter
        extends Presenter<ForgotPassphrasePresenter.MyView, ForgotPassphrasePresenter.MyProxy>
        implements ForgotPassphraseUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
   	private PassComplexityDTO gPassComplexityDTO = null;

    interface MyView extends View, HasUiHandlers<ForgotPassphraseUiHandlers>
    {
		public Span getGeneratedPassPhraseSpan();
		public ListBox getLanguageListBox();
		public Button getGenStrongPassPhraseButton();
		public ObidosPasswordBox getPassphraseBox();
		public ObidosPasswordBox getConfirmPassphraseBox();
		public ObidosMessageRow getMessageRow();
		public InlineCheckBox getUppercaseCheckBox();
		public InlineCheckBox getNoSpacesCheckBox();
		public Button getGenKeypairButton();
		public Collapse getGenCollapse();
		public InlineCheckBox getAddSpecialCharCheckBox();
		public ListBox getNumberOfWordsListBox();
        public com.google.gwt.user.client.ui.Label  getPassphraseStrengthLabel();
        public Progress getPassphraseStrengthProgress();
        public ProgressBar getPassphraseStrengthBar();
        public ObidosPasswordBox getCurrentPasswordBox();
        public TextBox getTwofaCodeBox();
        public FlowPanel getRequire2faFp();
        public Row getTwofaCodeRow();
        public ToggleSwitch getRequiresTwoFAPasswordResetSwitch();
        public ListGroup getPassphraseRequirementListGroup();
        public Label getNcharsLabel();
    }

    @NameToken(NameTokens.FORGOT_PASSPHRASE)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ForgotPassphrasePresenter>
    {
    }

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

    @Inject
    ForgotPassphrasePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        super.onBind();
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage(null);
        fetchAndDisplayLocalPassphrasePolicy();
		setFocus();
		// GWT has a bug, the item text can not be set in ui.xml from properties file
		// so we've to do it here, ugly!
		initializeLanguageListBox();
		initializeNumberOfWordsListBox();

		resetForm();
//		showHidePassphraseStrengthWidgets();
		disableSubmitButton();

    }
   	private void setFocus()
	{
	    getView().getPassphraseBox().setFocus(true);
	}
    	
    private void show2faFields()
    {
    	boolean rc = ClientUtils.checkTwoFaRequiredInUrl(placeManager);
    	ClientUtils.setToggleSwitchValue(getView().getRequiresTwoFAPasswordResetSwitch(), rc);
    	
    	if (rc)
    	{
    		getView().getTwofaCodeRow().setVisible(true);
    	}
    	else
    	{
    		getView().getTwofaCodeRow().setVisible(false);
    	}
    }

	private void resetForm()
	{
		showMessage("");
	    getView().getPassphraseBox().setValue("");
	    getView().getConfirmPassphraseBox().setValue("");
	    getView().getNcharsLabel().setText(glang.zero());
	    show2faFields();
	    resetProgressBar();
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

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
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

    @Override
    public void showPassphraseStrength()
    {
    	int len = showNumberOfCharactersInPassword();
    	if (len == 0)
    	{
    		gwtLog("Reset progress bar");
    		resetProgressBar();
    	}
    	PassComplexityDTO pcdto = getPassComplexityDTO();
    	if (pcdto != null)
    	{
    		if (len < pcdto.getPassphraseComplexityRequirements().getMinimumLength())
    		{
    			showMessage(null);
    			resetProgressBar();
    			return;
    		}
    	}

//        showHidePassphraseStrengthWidgets();
        showMessage("");
        String passphrase = getView().getPassphraseBox().getValue();
        if (passphrase == null)
        {
            return;
        }
        if (passphrase.length() == 0)
        {
            return;
        }
        GwtAsyncWrapper<PasswordAnalysisResults> callback = new GwtAsyncWrapper<PasswordAnalysisResults>(this)
        {

            @Override
            public void uponFailure(Throwable t)
            {
                showErrorMessage("Error calculating password strength: " + t.getMessage());
            }

            @Override
            public void uponSuccess(PasswordAnalysisResults result)
            {
                int score = result.getPasswordScore();
                showMessage("");
                // ignore suggestions
                ProgressBar bar = getView().getPassphraseStrengthBar();
                boolean strong = false;
                switch(score)
                {
                    case PasswordAnalysisResults.PASSWORD_WEAK:
                    {
                        bar.setPercent(20);
                        bar.setText("Weak");
                        bar.setType(ProgressBarType.DANGER);
                        strong = false;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_SO_SO:
                    {
                        bar.setPercent(20);
                        bar.setText("So-So");
                        bar.setType(ProgressBarType.DANGER);
                        strong = false;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_GOOD:
                    {
                        bar.setPercent(60);
                        bar.setText("Good");
                        bar.setType(ProgressBarType.INFO);
                        strong = false;
                        break;

                    }
                    case PasswordAnalysisResults.PASSWORD_STRONG:
                    {
                        bar.setPercent(80);
                        bar.setText("Strong");
                        bar.setType(ProgressBarType.INFO);
                        strong = true;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_VERY_STRONG:
                    {
                        bar.setPercent(100);
                        bar.setText("Very Strong");
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

    @Override
	public void reGenerateAndSaveKeyPair()
    {
        String passphrase = getView().getPassphraseBox().getValue();
        String confirmPassphrase = getView().getConfirmPassphraseBox().getValue();
        ObidosMessages lang = ObidosMessages.LANG;
        if (passphrase == null)
        {
            showErrorMessage(lang.passphraseEmptyWarning());
            return;
        }
        if (confirmPassphrase == null || confirmPassphrase.length() == 0)
        {
            showErrorMessage(lang.confirmPassphraseWarning());
            return;
        }

        if (!passphrase.equals(confirmPassphrase))
        {
            showErrorMessage(lang.passphraseMismatchWarning());
            return;
        }
        String currentPassword = getView().getCurrentPasswordBox().getValue();
        if (currentPassword == null || currentPassword.length() == 0)
        {
        	showErrorMessage(lang.pleaseEnterCurrentPassword());
        	return;
        }
        byte[] twoFACode = null;
        // Issue #609
        if (getView().getRequire2faFp().isVisible())
        {
        	if (getView().getRequiresTwoFAPasswordResetSwitch().getValue())
        	{
				String code = getView().getTwofaCodeBox().getValue();
				if (code.length() == 0)
				{
					showErrorMessage(lang.pleaseEnter2FACode());
					return;
				}
				twoFACode = code.getBytes();
        	}
        }

        String passwordResetToken = ClientUtils.getTokenFromUrl(placeManager);
        if (passwordResetToken == null)
        {
        	showErrorMessage(lang.couldNotFindResetToken());
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
                showMessage(lang.keyPairReCreatedSuccessfully());
                cachePassphrase(passphrase.getBytes());
                disableSubmitButton();
                getView().getPassphraseBox().setValue("");
                getView().getConfirmPassphraseBox().setValue("");
                getView().getCurrentPasswordBox().setValue("");
                getView().getTwofaCodeBox().setValue("");
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().resetPassphrase(authCreds, currentPassword, passphrase.getBytes(), passwordResetToken, twoFACode, callback);
    }

    @Override
    public void onConfirmPassphraseKeyUp()
    {
        showMessage("");
    }

	private void showHidePassphraseStrengthWidgets()
	{
	    String passphrase = getView().getPassphraseBox().getValue();
	    com.google.gwt.user.client.ui.Label  label = getView().getPassphraseStrengthLabel();
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
	}
	private void disableSubmitButton()
	{
	    getView().getGenKeypairButton().setEnabled(false);
	}

	private void enableSubmitButton()
	{
	    getView().getGenKeypairButton().setEnabled(true);
	}

    private void cachePassphrase(byte[] passphrase)
    {
    	String nameToken = null;
        ClientUtils.cachePassphrase(this.getClass().getSimpleName(), placeManager, currentUser, getView().getMessageRow(), passphrase, nameToken);
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
    private void resetProgressBar()
    {
        ProgressBar bar = getView().getPassphraseStrengthBar();
		bar.setPercent(20);
		bar.setText(glang.weak());
		bar.setType(ProgressBarType.DANGER);
    }

}
