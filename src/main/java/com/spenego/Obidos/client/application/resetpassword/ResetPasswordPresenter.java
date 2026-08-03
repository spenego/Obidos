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

package com.spenego.Obidos.client.application.resetpassword;

import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class ResetPasswordPresenter extends Presenter<ResetPasswordPresenter.MyView, ResetPasswordPresenter.MyProxy>
        implements ResetPasswordUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    interface MyView extends View, HasUiHandlers<ResetPasswordUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public ObidosPasswordBox getPasswordBox();
        public Button getShowHidePasswordButton();
        public ObidosPasswordBox getConfirmPasswordBox();
        public ObidosMessageRow getMessageRow();
        public Button getResetButton();
        public Button getLoginButton();
        public Label getNcharsLabel();
        public FlowPanel getRequire2faFp();
        public TextBox getTwofaCodeBox();
        public ObidosButtonToolBar getButtonToolBar();
        public ObidosRowBottom2px getTwofaRow();
        public ListGroup getPasswordRequirementListGroup();
    }

    @NameToken(NameTokens.RESET_PASSWORD)
    @ProxyCodeSplit
    @NoGatekeeper
    interface MyProxy extends ProxyPlace<ResetPasswordPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    ResetPasswordPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, RevealType.RootLayout);

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
        clearPasswords();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage(null);
        getView().getButtonToolBar().adjustButtonsWidth();
        hideLoginButton();
        // we will not check password strength check in client as the service requires authentication.
        // we will do the check in server side after submission and will throw exception
        checkResetToken();
        clearPasswords();
        updateForm();
        fetchAndDisplayLocalPasswordPolicy();
    }

	private void fetchAndDisplayLocalPasswordPolicy()
	{
//		setPassComplexityDTO(null);
		GwtAsyncWrapper<PassComplexityDTO> callback = new GwtAsyncWrapper<PassComplexityDTO>(this)
		{

			@Override
			public void uponSuccess(PassComplexityDTO dto)
			{
//				setPassComplexityDTO(dto);
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
    
    private void show2FAStuff(boolean visible)
    {
    	getView().getTwofaRow().setVisible(visible);
    	getView().getRequire2faFp().setVisible(visible);
    }
    
    private void updateForm()
    {
    	boolean twoFaCodeRequried = ClientUtils.getTwoFaRequiredFromUrl(placeManager);
    	if (twoFaCodeRequried)
    	{
    		gwtLog("2FA code requried");
    		show2FAStuff(true);
    	}
    	else
    	{
    		show2FAStuff(false);
    	}
    }

    private void hideLoginButton()
    {
        getView().getLoginButton().setVisible(false);
    }
    private void showLoginButton()
    {
        getView().getLoginButton().setVisible(true);
    }

    private void enableResetButton(boolean enabled)
    {
    	getView().getResetButton().setEnabled(enabled);
    }

    private void checkResetToken()
    {
        String token = ClientUtils.getResetTokenFromUrl(placeManager);
        if (token == null)
        {
        	gwtLog("No reset token found in URL");
        	ClientUtils.goBack(placeManager);
        }
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
    public void resetPassword()
    {
        // we will not check password strength check in client as the service requires authentication.
        // we will do the check in server side after submission and will throw exception
        String password = getView().getPasswordBox().getValue();
        if (password == null || (password != null && password.length() == 0))
        {
            showErrorMessage(glang.specifyNewPassword());
            return;
        }
        String confirmPassword = getView().getConfirmPasswordBox().getValue();
        if (confirmPassword == null || (confirmPassword != null && confirmPassword.length() == 0))
        {
            showErrorMessage(glang.confirmPassword());
            return;
        }
        if (!password.equals(confirmPassword))
        {
            showErrorMessage(glang.passwordMismatch());
            return;
        }
        showErrorMessage("");
        String token = null;
        try
        {
            token = ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.RESET_TOKEN);
        } catch (ParamNotFoundException e)
        {
        }
        if (token == null)
        {
            showErrorMessage(glang.couldNotFindResetToken());
            return;
        }
        String twoFaVerificationCode = null;
		if (getView().getTwofaRow().isVisible())
		{
			gwtLog("2FA Row is visible");
		}
		else
		{
			gwtLog("2FA Row is not visible");
			
		}
		
		// Even if 2FA row is not visible, the following code says the 
		// 2fa TextBox is visible, it must be a bug!!!
		// so I will use the row
		if (getView().getTwofaRow().isVisible())
		{
        	twoFaVerificationCode = getView().getTwofaCodeBox().getValue();
        	if (twoFaVerificationCode.length() == 0)
        	{
        		showErrorMessage(glang.enter2FaVerificationCode());
        		return;
        	}
		}

		/*
        if (getView().getTwofaCodeBox().isVisible())
        {
        	gwtLog("WTF...............");
        	twoFaVerificationCode = getView().getTwofaCodeBox().getValue();
        	if (twoFaVerificationCode.length() == 0)
        	{
        		showErrorMessage(ObidosMessages.LANG.enter2FaVerificationCode());
        		return;
        	}
        }
        */

        AsyncCallback<Void> callback = new AsyncCallback<Void>()
        {

            @Override
            public void onFailure(Throwable t)
            {
                showErrorMessage(glang.couldNotResetPassword() + ": " +  t.getMessage());
            }

            @Override
            public void onSuccess(Void arg0)
            {
                clearPasswords();
                showMessage(glang.passwordResetSuccessful());
                enableResetButton(false);
                show2FAStuff(false);
                navigateToPasswordChangedPage();
            }
        };
        UserService.Utility.getInstance().resetPassword(token, password, twoFaVerificationCode == null ? null: twoFaVerificationCode.getBytes(), callback);
    }

    private void clearPasswords()
    {
        getView().getPasswordBox().setValue("");
        getView().getConfirmPasswordBox().setValue("");
        getView().getNcharsLabel().setText("0");

    }

    @Override
    public void showLoginPage()
    {
        ClientUtils.showPage(placeManager, NameTokens.LOGIN);
    }

    @Override
    public void showNumberOfCharactersInPassword()
    {
        String password = getView().getPasswordBox().getValue();
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
    }
    
    private void gwtLog(String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

    private void navigateToPasswordChangedPage()
    {
    	if (currentUser != null)
    	{
    		currentUser.setPasswordJustChanged(true);
    	}
       	Map<String,String> with = new HashMap<>();
       	with.put(ObidosConstants.PASSWORD_IS_RESET, glang.yes());

	    ClientUtils.showPage(placeManager, NameTokens.PASSWORD_CHANGED, with);
    }

}