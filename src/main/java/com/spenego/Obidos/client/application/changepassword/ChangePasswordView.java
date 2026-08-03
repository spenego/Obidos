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

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.util.ClientUtils;

class ChangePasswordView extends ViewWithUiHandlers<ChangePasswordUiHandlers> implements ChangePasswordPresenter.MyView
{
    interface Binder extends UiBinder<Widget, ChangePasswordView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    ObidosPanelHeader panelHeader;
    
    @UiField
    ObidosPasswordBox oldPasswordBox;

    @UiField
    Button showHideCurrentPasswordButton;

    @UiField
    Button showHideNewPasswordButton;

    @UiField
    ObidosPasswordBox newPasswordBox;

    @UiField
    ObidosPasswordBox confirmNewPasswordBox;

    @UiField
    Progress passwordStrengthProgress;

    @UiField
    ProgressBar passwordStrengthBar;

    @UiField
    Button changePasswordButton;

    @UiField
    com.google.gwt.user.client.ui.Label passwordStrengthLabel;

    @UiField
    Button showHideConfirmPasswordButton;

    @UiField
	ObidosMessageRow messageRow;

    @UiField
    ListGroup passwordRequirementListGroup;

    @UiField
    Button hibpButton;
    
    @UiField
    Label ncharsLabel;

    @UiField
    HTMLPanel processingPanel;

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

    @Inject
    ChangePasswordView(Binder uiBinder)
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
    
    @UiHandler("oldPasswordBox")
    void onclickCurretnPassword (KeyUpEvent e)
	{
    	if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
    	{
    		getUiHandlers().gotoNewPasswordField();
    	}
	}

    @UiHandler("showHideCurrentPasswordButton")
    void onClickShowHideCurrentPasswordButton(ClickEvent e)
    {
       ClientUtils.toggleEyeIcon(showHideCurrentPasswordButton, oldPasswordBox);
    }

    @UiHandler("showHideNewPasswordButton")
    void onClickShowHideNewPasswordButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHideNewPasswordButton, newPasswordBox);
    }
    
    @UiHandler("showHideConfirmPasswordButton")
    void onclickShowHideConfirmPasswordButton (ClickEvent e)
	{
        ClientUtils.toggleEyeIcon(showHideConfirmPasswordButton, confirmNewPasswordBox);
	}

    @UiHandler("newPasswordBox")
    void onKeyUpNewPasswordBox(KeyUpEvent e)
    {
        getUiHandlers().showPasswordStrength();
    }

    @UiHandler("newPasswordBox")
    void onPasteToNewPasswordBox(ValueChangeEvent<String> text)
    {
        getUiHandlers().showPasswordStrengthPasted();
    }

    @UiHandler("changePasswordButton")
    void onCLickChangePasswordButton(ClickEvent e)
    {
        getUiHandlers().changePassword();
    }

    @UiHandler("hibpButton")
    void onclickHibpButton (ClickEvent e)
	{
    	getUiHandlers().checkWithHaveIBeenPwned();
	}

    public ObidosPasswordBox getOldPasswordBox()
    {
        return oldPasswordBox;
    }

    public ObidosPasswordBox getNewPasswordBox()
    {
        return newPasswordBox;
    }

    public ObidosPasswordBox getConfirmNewPasswordBox()
    {
        return confirmNewPasswordBox;
    }

    public Progress getPasswordStrengthProgress()
    {
        return passwordStrengthProgress;
    }

    public ProgressBar getPasswordStrengthBar()
    {
        return passwordStrengthBar;
    }

    public Button getChangePasswordButton()
    {
        return changePasswordButton;
    }

    public com.google.gwt.user.client.ui.Label getPasswordStrengthLabel()
    {
        return passwordStrengthLabel;
    }

    public Button getShowHideCurrentPasswordButton()
    {
        return showHideCurrentPasswordButton;
    }

    public Button getShowHideNewPasswordButton()
    {
        return showHideNewPasswordButton;
    }

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Button getShowHideConfirmPasswordButton()
	{
		return showHideConfirmPasswordButton;
	}

	public ListGroup getPasswordRequirementListGroup()
	{
		return passwordRequirementListGroup;
	}

	public Button getHibpButton()
	{
		return hibpButton;
	}

	public Label getNcharsLabel()
	{
		return ncharsLabel;
	}

	public HTMLPanel getProcessingPanel()
	{
		return processingPanel;
	}
}
    