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

package com.spenego.Obidos.client.application.changeinitialpassword;

import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.UserDTO;

public class ChangeInitialPasswordPresenter 
	extends ObidosPresenter<UserDTO, ChangeInitialPasswordPresenter.MyView, ChangeInitialPasswordPresenter.MyProxy, ChangeInitialPasswordUiHandlers>
    implements ChangeInitialPasswordUiHandlers
{
   	private ObidosMessages glang = ObidosMessages.LANG;
   	private PassComplexityDTO gPassComplexityDTO = null;
   	private static boolean buttonsAdjusted = false;
   	
   	// Bug #81
   	// delay between key strokes so the backend will not be overwhelmed
   	// spgdev@spenego.com - Dec 7, 2024
   	private Timer passwordStrengthCheckTimer = null;
   	private String lastPassword = "";
   	
    interface MyView extends View, HasUiHandlers<ChangeInitialPasswordUiHandlers>
    {
        public BlockQuote getHelpBlockQuote();
        public Paragraph getPasswordResetParagraph();

        public ObidosPasswordBox getOldPasswordBox();
        public ObidosPasswordBox getNewPasswordBox();
        public ObidosPasswordBox getConfirmNewPasswordBox();
        public Progress getPasswordStrengthProgress();
        public ProgressBar getPasswordStrengthBar();
        public Button getShowHideCurrentPasswordButton();
        public Button getShowHideNewPasswordButton();
        public Button getShowHideConfirmPasswordButton();

        public Button getChangePasswordButton();
        public com.google.gwt.user.client.ui.Label getPasswordStrengthLabel();
        public ObidosMessageRow getMessageRow();
        public ObidosButtonToolBar getButtonToolBar();
        public ObidosPanelHeader getPanelHeader();
        public ListGroup getPasswordRequirementListGroup();
        public Label getNcharsLabel();
        public HTMLPanel getProcessingPanel();
    }

    /*
     * It is a protected page by gatekeeper, meaning user must authenticate first with initial password
     * to see it. It is not hard to bypass gatekeeper security, but that's only thing it can be done in
     * front-end, real security is in the backend
     */
    @NameToken(NameTokens.CHANGE_INITIAL_PASSWORD)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class) // Issue #539
    interface MyProxy extends ProxyPlace<ChangeInitialPasswordPresenter>
    {
    }

    @Inject
    ChangeInitialPasswordPresenter(
            EventBus eventBus,
            MyView view,
            MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

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
        adjustButtonsWidth();
        setFocus();
        showMessage("");
        resetShowHidePasswordButton();
        ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
        resetForm();
        updatePanelHeading();
        showHidePasswordStrengthWidgets();
        disableChangePasswordButton();
        fetchAndDisplayLocalPasswordPolicy();
        ClientUtils.updateRPCTime("ChangePassword");
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
    
    private void resetShowHidePasswordButton()
    {
    	Button b = getView().getShowHideCurrentPasswordButton();
    	ObidosPasswordBox pbox = getView().getOldPasswordBox();
    	pbox.getElement().setAttribute("type", "password");
		b.setType(ButtonType.INFO);
		b.setIcon(IconType.EYE);

		b = getView().getShowHideNewPasswordButton();
		pbox = getView().getNewPasswordBox();
    	pbox.getElement().setAttribute("type", "password");
		b.setType(ButtonType.INFO);
		b.setIcon(IconType.EYE);

		b = getView().getShowHideConfirmPasswordButton();
		pbox = getView().getConfirmNewPasswordBox();
    	pbox.getElement().setAttribute("type", "password");
		b.setType(ButtonType.INFO);
		b.setIcon(IconType.EYE);
    }
    
    private void adjustButtonsWidth()
    {
    	if (!buttonsAdjusted)
    	{
    		getView().getButtonToolBar().adjustButtonsWidth();
    		buttonsAdjusted = true;
    	}
    }
    private void resetProgressBar()
    {
        ProgressBar bar = getView().getPasswordStrengthBar();
		bar.setPercent(20);
		bar.setText(glang.weak());
		bar.setType(ProgressBarType.DANGER);
    }

    private void setFocus()
    {
        getView().getOldPasswordBox().setFocus(true);
    }

    private void disableChangePasswordButton()
    {
        getView().getChangePasswordButton().setEnabled(false);

    }
    private void enableChangePasswordButton()
    {
        getView().getChangePasswordButton().setEnabled(true);
    }

    private void resetForm()
    {
        getView().getOldPasswordBox().setValue("");
        getView().getNewPasswordBox().setValue("");
        getView().getConfirmNewPasswordBox().setValue("");
        getView().getNcharsLabel().setText(glang.zero());
        resetProgressBar();
    }

    private void updatePanelHeading()
    {
        ObidosPanelHeader header = getView().getPanelHeader();
        if (ClientUtils.isAdmin(currentUser))
        {
            header.setText(glang.changeMyPassword());
        }
        else
        {
            header.setText(glang.changeInitialPassword());
            		
        }
    }

    private void showHidePasswordStrengthWidgets()
    {
        showMessage("");
        /*
        String password = getView().getNewPasswordBox().getValue();
        Label label = getView().getPasswordStrengthLabel();
        Progress progress = getView().getPasswordStrengthProgress();
        ProgressBar progressBar = getView().getPasswordStrengthBar();
        if (password == null || (password != null && password.length() == 0))
        {
            label.setVisible(false);
            progress.setVisible(false);
            progressBar.setVisible(false);
        }
        else
        {
            label.setVisible(true);
            progress.setVisible(true);
            progressBar.setVisible(true);
        }
        */
    }

    public int showNumberOfCharactersInPassword()
    {
        String password = getView().getNewPasswordBox().getValue();
        int len = 0;
        if (password == null || password.length() == 0)
        {
            len = 0;
        }
        else
        {
            len = password.length();
        }
        getView().getNcharsLabel().setText(Integer.toString(len));
        return len;
    }

    private void showProcessing(final boolean show)
    {
    	getView().getProcessingPanel().setVisible(show);
    }

    /**
     * Bug #81
     * Delay a second between key strokes
     * spgdev@spenego.com - Dec 7, 2024
     */
    @Override
    public void showPasswordStrength()
    {
    	showProcessing(true);
    	if (passwordStrengthCheckTimer != null)
    	{
    		passwordStrengthCheckTimer.cancel();
    	}
    	passwordStrengthCheckTimer = new Timer() {

			@Override
			public void run()
			{
				gwtLog("MMM strength key up handler ...");
				showPasswordStrengthReal();
				showProcessing(false);
			}
    	};
    	passwordStrengthCheckTimer.schedule(ObidosConstants.KEY_STROKE_DELAY_FOR_STRENGTH_CHECK);
    }

	@Override
	public void showPasswordStrengthPasted()
	{
        String password = getView().getNewPasswordBox().getValue();
		if (password == null || password.length() == 0)
		{
			return;
		}
		if (lastPassword.equals(password))
		{
			gwtLog("MMM password did not change, no need to get strength");
			return;
		}
		showPasswordStrengthReal();
	}


    public void showPasswordStrengthReal()
    {
    	int len = showNumberOfCharactersInPassword();
    	if (len <= 0)
    	{
    		resetProgressBar();
    	}
    	PassComplexityDTO pcdto = getPassComplexityDTO();
    	if (pcdto != null)
    	{
    		if (len < pcdto.getPasswordComplexityRequirements().getMinimumLength())
    		{
    			resetProgressBar();
    			showMessage(null);
    			return;
    		}
    	}

        showHidePasswordStrengthWidgets();
        showMessage("");
        String password = getView().getNewPasswordBox().getValue();
        if (password == null)
        {
        	showMessage(null);
            return;
        }
        if (password.length() == 0)
        {
        	showMessage(null);
            return;
        }
        GwtAsyncWrapper<PasswordAnalysisResults> callback = new GwtAsyncWrapper<PasswordAnalysisResults>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Error detecting password strength: " + e.getMessage());
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
                List<String> suggestions = result.getSuggestions();
                if (suggestions != null && suggestions.size() > 0)
                {
                    gwtLog("Sugestion size: " + suggestions.size());
                    gwtLog(" Not showing suggestions, it flickers a log");
                    gwtLog("No showing suggestions, it flickers a lot");
                }
                ProgressBar bar = getView().getPasswordStrengthBar();
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
                // enable Change Password button only if password is at least strong
                if (strong)
                {
                    enableChangePasswordButton();
                }
                else
                {
                    disableChangePasswordButton();
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        LoginService.Utility.getInstance().checkPassStrength(authCreds, password, ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS, callback);
    }

    /*
    private void showErrorMessage(String message)
    {
       HTML html = getView().getHtmlMessageWidget();
       SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(
               "<div class=\"obidosErrorMessage\">" +
                       "<b>" + message + "</b>" +
               "</div>");
       html.setHTML(safeHtml);
    }

    private void showMessage(String message)
    {
       HTML html = getView().getHtmlMessageWidget();
       SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(
               "<div class=\"obidosMessage\">" +
                       message +
               "</div>");
       html.setHTML(safeHtml);

    }

    private void showPasswordStrengthSuggestions(List<String> suggestions)
    {
       HTML html = getView().getHtmlMessageWidget();
       String headerString =
               "<div>" +
               "<h4>Suggestions</h4>" +
               "<ul class=\"list-group\">" +
               "<li class=\"list-group-item list-group-item-success\">"+ "Use long passwords for better security!" + "</li>";

       String sList = "";
       int i = 0;
       for (String suggestion:suggestions)
       {
           if (i % 2 == 0)
           {
               sList += "<li class=\"list-group-item list-group-item-info\">" + suggestion + "</li>";
           }
           else
           {
               sList += "<li class=\"list-group-item list-group-item-success\">" + suggestion + "</li>";
           }
           i++;

       }

       String footerString =
               "</ul>" +
               "</div>";

       SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(headerString + sList + footerString);
       html.setHTML(safeHtml);

    }
    */


    @Override
    public void changePassword()
    {
        String currentPassword = getView().getOldPasswordBox().getValue();
        if (currentPassword == null || currentPassword.length() == 0)
        {
            showErrorMessage(glang.specifyCurrentPassword());
            return;
        }

        String newPassword = getView().getNewPasswordBox().getValue();
        if (newPassword == null || newPassword.length() == 0)
        {
            showErrorMessage(glang.specifyNewPassword());
            return;
        }

        String confirmNewPassword = getView().getConfirmNewPasswordBox().getValue();
        if (confirmNewPassword == null || confirmNewPassword.length() == 0)
        {
            showErrorMessage(glang.confirmNewPassword());
            return;
        }

        if (!newPassword.equals(confirmNewPassword))
        {
            showErrorMessage(glang.passwordMismatch());
            return;
        }

        // old and new password must be different
        if (currentPassword.equals(newPassword))
        {
        	showErrorMessage(glang.specifyANewPassword());
        	return;
        }

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                String emsg = glang.couldnotChangePassword() + ": " + e.getMessage();
                showErrorMessage(emsg);
            }

            @Override
            public void uponSuccess(Void result)
            {
            	Date d = new Date();
            	String message = ObidosMessages.LANG.passwordChanged(d.toString());
            	showMessage(message);
                navigateToPasswordChangedPage();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        safeInvocationCall(() -> UserService.Utility.getInstance().changePassword(authCreds, currentPassword, newPassword, callback));
    }

    private void navigateToPasswordChangedPage()
    {
    	currentUser.setPasswordJustChanged(true);
	    ClientUtils.showPage(placeManager, NameTokens.PASSWORD_CHANGED);
    }

    @Override
    public void logout()
    {
        // Don't show password change message in login screen, it's possible they did not change
        // password yet  ( #147 )
        ClientUtils.logout(this, "");
    }

    @Override
    public void showHideHelp()
    {
    	showHelp();
    }

	@Override
	protected String getIdName()
	{
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}


	private void setPassComplexityDTO(PassComplexityDTO dto)
	{
		gPassComplexityDTO = dto;
	}
	
	private PassComplexityDTO getPassComplexityDTO()
	{
		return gPassComplexityDTO;
	}

	private void fetchAndDisplayLocalPasswordPolicy()
	{
		setPassComplexityDTO(null);
		GwtAsyncWrapper<PassComplexityDTO> callback = new GwtAsyncWrapper<PassComplexityDTO>(this)
		{

			@Override
			public void uponSuccess(PassComplexityDTO dto)
			{
				setPassComplexityDTO(dto);
				ClientUtils.displayPasswordPolicy(getView().getPasswordRequirementListGroup(), dto);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch local Password Policy: " + caught.getMessage());
			}
		};
		UserService.Utility.getInstance().getPassComplexity(null, callback);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}


}
