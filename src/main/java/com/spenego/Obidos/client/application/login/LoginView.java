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

package com.spenego.Obidos.client.application.login;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.util.ClientUtils;

/**
 * @author spgdev@spenego.com - Dec 19, 2016
 */
class LoginView extends ViewWithUiHandlers<LoginUiHandlers> implements LoginPresenter.MyView
{

    interface Binder extends UiBinder<Widget, LoginView>
    {
    }

    @UiField 
    HTMLPanel htmlPanel;

    @UiField
    ObidosTextBox loginNameField;

    @UiField
    Input passwordField;

    @UiField
    FormLabel formError;

    @UiField
    Button signinButton;

    @UiField
    Anchor forgotPasswordAnchor;
    
    @UiField
    Button showHidePasswordButton;
    

	@Inject
    LoginView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("loginNameField")
    void onLoginNameKeyDown(KeyDownEvent e)
    {
    	ClientUtils.showMessage("", formError);
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    		getUiHandlers().loginFieldEnterCallback();
    	}
    }

    @UiHandler("passwordField")
    void onPasswordKeyDown(KeyDownEvent e)
    {
    	ClientUtils.showMessage("", formError);
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    		getUiHandlers().passwordFieldEnterCallback();
    	}
    }

    @UiHandler("signinButton")
    void onClickSigninButton(ClickEvent e)
    {
    	getUiHandlers().login();
    }

    @UiHandler("forgotPasswordAnchor")
    void onClickForgotPasswordAnchor(ClickEvent e)
    {
        getUiHandlers().showResetPasswordRequestView();
    }
    
    @UiHandler("showHidePasswordButton")
    void onClickShowHidePasswordButton(ClickEvent e)
    {
    	// use PRIMARY button instead of INFO to match with Sigin in Button
        ClientUtils.toggleEyeIcon(showHidePasswordButton, passwordField, ButtonType.PRIMARY);
    }

    private void processLoginx()
    {
    	/*
    	ObidosMessages lang = ObidosMessages.LANG;
   		String loginName = loginNameField.getValue();
    	String password = passwordField.getValue();
    	String errorMessage = "";
    	if (loginName == null || (loginName != null && loginName.length() == 0))
    	{
    	    errorMessage = "Specify Login Name.";
    	}

    	if (password == null || (password != null && password.length() == 0))
    	{
    	    errorMessage += " Specify Password";
    	    ClientUtils.showErrorMessage(errorMessage, formError);
    	    return;
    	}
    	if (loginName.length() > 0 && password.length() > 0)
    	{
			getUiHandlers().login(loginName, password);
			// don't reset login form here, it looks strange.
			// we'll dot in onHide() from the presenter
    	}
    	*/
    }

 
    // If enter is pressed on loginNameField change focus to
    // passwordField
    // ref: http://samyem.blogspot.dk/2012/07/gwt-emulate-tab-on-enter.html
//    @UiHandler("loginNameField")
//    public void onKeyDown(KeyDownEvent event)
//    {
//    	formError.setVisible(false);
        // emulate tab on key
//        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
//            focusNext(event.getNativeEvent());
 //   }

    public native void focusNext(NativeEvent event)/*-{
            var inputs = $wnd.$(':input:visible');
            inputs.eq(inputs.index(event.target) + 1).focus();
    }-*/;

	public ObidosTextBox getLoginNameField()
	{
		return loginNameField;
	}


	public Input getPasswordField()
	{
		return passwordField;
	}


	public HTMLPanel getHtmlPanel()
	{
		return htmlPanel;
	}


	public FormLabel getFormError()
	{
		return formError;
	}
	
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	public Button getSigninButton()
	{
		return signinButton;
	}

	public Anchor getForgotPasswordAnchor()
	{
		return forgotPasswordAnchor;
	}
    public Button getShowHidePasswordButton()
	{
		return showHidePasswordButton;
	}

}