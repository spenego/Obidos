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

package com.spenego.Obidos.client.application.changepassword;

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;

import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.HaveIBeenPwnedService;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;

public class ChangePasswordPresenter extends Presenter<ChangePasswordPresenter.MyView, ChangePasswordPresenter.MyProxy>
        implements ChangePasswordUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
   	private PassComplexityDTO gPassComplexityDTO = null;

   	// Bug #81
   	// delay between key strokes so the backend will not be overwhelmed
   	// spgdev@spenego.com - Dec 7, 2024
   	private Timer passwordStrengthCheckTimer = null;
   	private String lastPassword = "";

    interface MyView extends View, HasUiHandlers<ChangePasswordUiHandlers>
    {
        public ObidosPasswordBox getOldPasswordBox();
        public ObidosPasswordBox getNewPasswordBox();
        public ObidosPasswordBox getConfirmNewPasswordBox();
        public Progress getPasswordStrengthProgress();
        public ProgressBar getPasswordStrengthBar();
        public Button getChangePasswordButton();
        public com.google.gwt.user.client.ui.Label getPasswordStrengthLabel();
        public BlockQuote getHelpBlockQuote();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
        public ListGroup getPasswordRequirementListGroup();
        public Button getHibpButton();
        public Label getNcharsLabel();
        public HTMLPanel getProcessingPanel();
    }

    @NameToken(NameTokens.CHANGE_PASSWORD)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInGatekeeper.class)
    interface MyProxy extends ProxyPlace<ChangePasswordPresenter>
    {
    }

    private final CurrentUser currentUser;

    @Inject
    ChangePasswordPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

        this.currentUser = currentUser;

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        gwtLog("onBind()");
    }

    protected void onReveal()
    {
        super.onReveal();
        gwtLog("onReveal()");
    }

    protected void onHide()
    {
        super.onHide();
        gwtLog("onHide() reset form");
        resetForm();
		showProcessing(false);
        clearTimers();
    }

    protected void onUnbind()
    {
        super.onUnbind();
        gwtLog("onUnbind()");
    }

    protected void onReset()
    {
        super.onReset();
        gwtLog("onReset..()");
		showProcessing(false);
        resetForm();
        disableChangePasswordButton();
        updatePanelHeading();
        resetProgressBar();
        showMessage(null);
        ClientUtils.focusToWidegt(getView().getOldPasswordBox());
        fetchAndDisplayLocalPasswordPolicy();

        // Schedule the width calculation for after layout
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute() {
                int w = getView().getHibpButton().getOffsetWidth();
                if (w > 0) {
                    getView().getChangePasswordButton().setWidth(w + "px");
                }
            }
        });
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
    	ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
    }

    private void showHidePasswordStrengthWidgets()
    {
    	/*
        showMessage("");
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

    private void resetProgressBar()
    {
        ProgressBar bar = getView().getPasswordStrengthBar();
		bar.setPercent(20);
		bar.setText(glang.weak());
		bar.setType(ProgressBarType.DANGER);
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
        lastPassword = password;
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

            	String localRequirementsMessage = result.getLocalRequirementsNotMetMessage();
            	if (localRequirementsMessage != null)
            	{
            		showErrorMessage(localRequirementsMessage);
            	}

                List<String> suggestions = result.getSuggestions();
                if (suggestions != null && suggestions.size() > 0)
                {
                    gwtLog("Sugestion size: " + suggestions.size());
                }
                ProgressBar bar = getView().getPasswordStrengthBar();
                boolean enable = false;
                switch(score)
                {
                    case PasswordAnalysisResults.PASSWORD_WEAK:
                    {
                        bar.setPercent(20);
                        bar.setText(glang.weak());
                        bar.setType(ProgressBarType.DANGER);
                        enable = false;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_SO_SO:
                    {
                        bar.setPercent(20);
                        bar.setText(glang.soso());
                        bar.setType(ProgressBarType.DANGER);
                        enable = false;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_GOOD:
                    {
                        bar.setPercent(60);
                        bar.setText(glang.good());
                        bar.setType(ProgressBarType.INFO);
                        enable = false;
                        break;

                    }
                    case PasswordAnalysisResults.PASSWORD_STRONG:
                    {
                        bar.setPercent(80);
                        bar.setText(glang.strong());
                        bar.setType(ProgressBarType.INFO);
                        enable = true;
                        break;
                    }

                    case PasswordAnalysisResults.PASSWORD_VERY_STRONG:
                    {
                        bar.setPercent(100);
                        bar.setText(glang.veryStrong());
                        bar.setType(ProgressBarType.SUCCESS);
                        enable = true;
                        break;
                    }
                }
                gwtLog("Entropy: " + result.getEntropy());
                if (enable)
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
        LoginService.Utility.getInstance().checkPassStrength(authCreds, password, PASSWORD_COMPLEXITY_REQUIREMENTS, callback);
    }


    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    /*
    private void showMessageOld(String message)
    {
       HTML html = getView().getHtmlMessageWidget();
       SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(
               "<div class=\"obidosMessage\">" +
                       message +
               "</div>");
       html.setHTML(safeHtml);
    }
    */

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    /*
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
            showErrorMessage("Please specify current password!");
            return;
        }
        String newPassword = getView().getNewPasswordBox().getValue();
        if (newPassword == null || newPassword.length() == 0)
        {
            showErrorMessage("New Password is emtpy");
            return;
        }
        String confirmNewPassword = getView().getConfirmNewPasswordBox().getValue();
        if (confirmNewPassword == null || confirmNewPassword.length() == 0)
        {
            showErrorMessage("Please confirm the password!");
            return;
        }
        if (!newPassword.equals(confirmNewPassword))
        {
            showErrorMessage("Passwords do not match!");
            return;
        }

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Could not change Password: " + e.getMessage());
            }

            @Override
            public void uponSuccess(Void result)
            {
                showMessage("Password changed successfully");
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().changePassword(authCreds, currentPassword, newPassword, callback);
    }

    private void disableChangePasswordButton()
    {
        getView().getChangePasswordButton().setEnabled(false);

    }
    private void enableChangePasswordButton()
    {
        getView().getChangePasswordButton().setEnabled(true);
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

	@Override
	public void checkWithHaveIBeenPwned()
	{
		String newPass = getView().getNewPasswordBox().getValue();
		if (newPass == null || newPass.length() == 0)
		{
			showErrorMessage(ObidosMessages.LANG.specifyNewPassword());
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

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}
	
	@Override
	public void gotoNewPasswordField()
	{
		showMessage(null);
		String pass = getView().getOldPasswordBox().getValue();
		if (pass != null && pass.equals("42")) // easter egg
		{
			Button b = getView().getHibpButton();
			b.setVisible(!b.isVisible());
		}
		getView().getNewPasswordBox().setFocus(true);
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



}
