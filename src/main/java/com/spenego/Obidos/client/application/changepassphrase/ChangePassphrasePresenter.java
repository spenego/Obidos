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

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;

import java.util.Date;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.core.client.GWT;
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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;

public class ChangePassphrasePresenter
        extends Presenter<ChangePassphrasePresenter.MyView, ChangePassphrasePresenter.MyProxy>
        implements ChangePassphraseUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
   	private PassComplexityDTO gPassComplexityDTO = null;

   	// Bug #81
   	// delay between key strokes so the backend will not be overwhelmed
   	// spgdev@spenego.com - Dec 7, 2024
   	private Timer passphraseStrengthCheckTimer = null;
   	private String lastPass = "";

    interface MyView extends View, HasUiHandlers<ChangePassphraseUiHandlers>
    {
        public BlockQuote getHelpBlockQuote();
        public Paragraph getHelpParagraph();
        public ObidosPasswordBox getCurrentPassphraseBox();
        public ObidosPasswordBox getNewPassphraseBox();
        public ObidosPasswordBox getRepeatPassphraseBox();
        public com.google.gwt.user.client.ui.Label  getPassphraseStrengthLabel();
        public Progress getPassphraseStrengthProgress();
        public ProgressBar getPassphraseStrengthBar();
        public Button getChangePassphraseButton();
        public ObidosMessageRow getMessageRow();
        public ListGroup getPassphraseRequirementListGroup();
        public Label getNcharsLabel();
        public HTMLPanel getProcessingPanel();
    }

    @NameToken(NameTokens.CHANGE_PASSPHRASE)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ChangePassphrasePresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    ChangePassphrasePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		showProcessing(false);
		clearTimers();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
		showProcessing(false);
        resetForm();
        ClientUtils.focusToWidegt(getView().getCurrentPassphraseBox());
        fetchAndDisplayLocalPassphrasePolicy();
    }
    private void clearTimers()
    {
    	lastPass = "";
    	if (passphraseStrengthCheckTimer != null)
    	{
    		passphraseStrengthCheckTimer.cancel();
    		passphraseStrengthCheckTimer = null;
    	}
    }

    private void resetProgressBar()
    {
        ProgressBar bar = getView().getPassphraseStrengthBar();
		bar.setPercent(20);
		bar.setText(glang.weak());
		bar.setType(ProgressBarType.DANGER);
    }

    private void resetForm()
    {
    	showMessage(null);
        getView().getCurrentPassphraseBox().setValue("");
        getView().getNewPassphraseBox().setValue("");
        getView().getRepeatPassphraseBox().setValue("");
        getView().getNcharsLabel().setText(glang.zero());
        resetProgressBar();
    }

    private void enableChangePassphraseButton()
    {
        getView().getChangePassphraseButton().setEnabled(true);
    }

    private void disableChangePassphraseButton()
    {
        getView().getChangePassphraseButton().setEnabled(false);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }


	// Help starts--
    @Override
    public void showHideHelp()
    {
    	ClientUtils.showHelp(getView().getHelpBlockQuote());
    }
    // Help ends--

    private boolean checkEmpty(String text)
    {
       if (text == null)
       {
           return true;
       }
       if (text.length() == 0)
       {
           return true;
       }
       return false;
    }

    @Override
    public void changePassphrase()
    {
        String currentPassphrase = getView().getCurrentPassphraseBox().getValue();
        if (checkEmpty(currentPassphrase))
        {
            showErrorMessage("Current Passphrase is empty");
            return;
        }
        String newPassphrase = getView().getNewPassphraseBox().getValue();
        if (checkEmpty(newPassphrase))
        {
            showErrorMessage("New Passphrase is empty");
            return;
        }
        if (currentPassphrase != null &&  currentPassphrase.equals(newPassphrase))
        {
        	// Issue #252
        	showErrorMessage("Current and New Passphrases are the same");
        	return;
        }
        String repeatPassphrase = getView().getRepeatPassphraseBox().getValue();
        if (checkEmpty(repeatPassphrase))
        {
            showErrorMessage("Confirm Passphrase is empty");
            return;
        }
        if (!newPassphrase.equals(repeatPassphrase))
        {
            showErrorMessage("Passphrases do not match!");
            return;
        }

        GWT.log("Chaning passphrase");

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Could not change passphrase: " + e.getMessage());
            }

            @Override
            public void uponSuccess(Void arg0)
            {
                Date date = new Date();
                showMessage("Passphrase has changed on " + date.toString());
                // Issue #169
//                cachePassphrase(newPassphrase.getBytes());
                //ClientUtils.cachePassphrase(this.getClass().getSimpleName(), placeManager, currentUser, getView().getFormErrorLabel(), newPassphrase.getBytes());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().updatePassphrase(authCreds,newPassphrase.getBytes(),null,callback);
    }

    private void cachePassphrase(byte[] passphrase)
    {
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                gwtLog("ERROR: Could not cache passphrase");
                showErrorMessage("Could not cache passphrase: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(Void result)
            {
                gwtLog("New Passphrase cached");
                if (currentUser != null)
                {
                    currentUser.setPassphraseRegistered(Boolean.TRUE);
                    ClientUtils.sendMessageToAppViewToRefreshPassphraseCachedIcon(placeManager);
                }
            }
        };
        AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().cachePassphrase(authCredsDTO, passphrase, null, callback);
    }

    @Override
    public void showPassphraseStrengthPasted()
    {
        String pp = getView().getNewPassphraseBox().getValue();
        if (pp == null || pp.length() == 0)
        {
        	return;
        }
        if (lastPass.equals(pp))
        {
        	gwtLog("MMM passphrase did not change, not need to check strength");
        	return;
        }
        showPassphraseStrengthReal();
    }

    private void showProcessing(final boolean show)
    {
    	getView().getProcessingPanel().setVisible(show);
    }

    // Bug# 81
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
				gwtLog("MMM strength key up handler ...");
				showPassphraseStrengthReal();
				showProcessing(false);
			}
    	};
    	passphraseStrengthCheckTimer.schedule(ObidosConstants.KEY_STROKE_DELAY_FOR_STRENGTH_CHECK);
    }

    public void showPassphraseStrengthReal()
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

        showMessage("");
        String passphrase = getView().getNewPassphraseBox().getValue();
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

        lastPass = passphrase;

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
                    enableChangePassphraseButton();
                }
                else
                {
                    disableChangePassphraseButton();
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        LoginService.Utility.getInstance().checkPassStrength(authCreds, passphrase, PASSPHRASE_COMPLEXITY_REQUIREMENTS, callback);

    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);

    }
    public int showNumberOfCharactersInPassword()
    {
        String passphrase = getView().getNewPassphraseBox().getValue();
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
