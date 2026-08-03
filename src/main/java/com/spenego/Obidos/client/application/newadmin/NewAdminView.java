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

package com.spenego.Obidos.client.application.newadmin;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.util.ClientUtils;

class NewAdminView extends ViewWithUiHandlers<NewAdminUiHandlers> implements NewAdminPresenter.MyView
{
    interface Binder extends UiBinder<Widget, NewAdminView>
    {
    }
    
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    TextBox adminNameTextBox;

    @UiField
    TextBox adminFullnameTextBox;

    @UiField
    ObidosPasswordBox passwordBox;

    @UiField
    TextBox emailTextBox;

    @UiField
    TextBox phoneTextBox;

	@UiField
	Button showHidePasswordButton;

	@UiField
	CheckBox sendEmailCheckBox;

	@UiField
	TextArea emailCommentTextArea;
	
	@UiField
	ToggleSwitch createUserSwitch;

	@UiField
	ToggleSwitch createAdminSwitch;

	@UiField
	ToggleSwitch deleteUserSwitch;

	@UiField
	ToggleSwitch deleteAdminSwitch;

	@UiField
	ToggleSwitch lockUserSwitch;

	@UiField
	ToggleSwitch lockAdminSwitch;

	@UiField
	ToggleSwitch changeUserCredentialsSwitch;

	@UiField
	ToggleSwitch changeAdminCredentialsSwitch;

	@UiField
	ToggleSwitch modifyEmailTemplatesSwitch;
	
	@UiField
	ToggleSwitch rootAdminSwitch;

	@UiField
	ToggleSwitch changeSystemSettingSwitch;
	
	@UiField
	FlowPanel capFp;
	
	@UiField
	Row rootAdminRow;
	
	@UiField
	Button createAdminButton;
	
	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	ObidosButtonToolBar buttonToolBar;
	
	@UiField
	ObidosPanelHeader panelHeader;

    @Inject
    NewAdminView(Binder uiBinder)
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
		Button backButton = panelHeader.getBackButton();
		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().listAdmins();
				}
			});
		}
    }

    @UiHandler("createAdminButton")
    void onClickCreateAdminButton(ClickEvent e)
    {
        getUiHandlers().createAdmin();
    }

	@UiHandler("showHidePasswordButton")
    void onClickShowHidePasswordButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHidePasswordButton, passwordBox);
    }
	
	@UiHandler("rootAdminSwitch")
	void onclickRootAdminSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().rootAdminSwichCallback();
	}
	
    public TextBox getAdminNameTextBox()
    {
        return adminNameTextBox;
    }

    public ObidosPasswordBox getPasswordBox()
    {
        return passwordBox;
    }

    public TextBox getEmailTextBox()
    {
        return emailTextBox;
    }

    public TextBox getPhoneTextBox()
    {
        return phoneTextBox;
    }

    public Button getShowHidePasswordButton()
    {
        return showHidePasswordButton;
    }

    public TextBox getAdminFullnameTextBox()
    {
        return adminFullnameTextBox;
    }

    @Deprecated
	public Button getHelpButton()
	{
		return null;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public CheckBox getSendEmailCheckBox()
	{
		return sendEmailCheckBox;
	}

	public TextArea getEmailCommentTextArea()
	{
		return emailCommentTextArea;
	}

	public ToggleSwitch getCreateUserSwitch()
	{
		return createUserSwitch;
	}

	public ToggleSwitch getCreateAdminSwitch()
	{
		return createAdminSwitch;
	}

	public ToggleSwitch getDeleteUserSwitch()
	{
		return deleteUserSwitch;
	}

	public ToggleSwitch getDeleteAdminSwitch()
	{
		return deleteAdminSwitch;
	}

	public ToggleSwitch getLockUserSwitch()
	{
		return lockUserSwitch;
	}

	public ToggleSwitch getLockAdminSwitch()
	{
		return lockAdminSwitch;
	}

	public ToggleSwitch getChangeUserCredentialsSwitch()
	{
		return changeUserCredentialsSwitch;
	}

	public ToggleSwitch getChangeAdminCredentialsSwitch()
	{
		return changeAdminCredentialsSwitch;
	}

	public ToggleSwitch getModifyEmailTemplatesSwitch()
	{
		return modifyEmailTemplatesSwitch;
	}

	public ToggleSwitch getRootAdminSwitch()
	{
		return rootAdminSwitch;
	}

	public FlowPanel getCapFp()
	{
		return capFp;
	}

	public Row getRootAdminRow()
	{
		return rootAdminRow;
	}

	public Button getCreateAdminButton()
	{
		return createAdminButton;
	}
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getButtonToolBar()
	{
		return buttonToolBar;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ToggleSwitch getChangeSystemSettingSwitch()
	{
		return changeSystemSettingSwitch;
	}
}