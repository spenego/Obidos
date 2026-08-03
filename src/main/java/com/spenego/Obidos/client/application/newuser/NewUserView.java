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

package com.spenego.Obidos.client.application.newuser;


import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.util.ClientUtils;

class NewUserView extends ViewWithUiHandlers<NewUserUiHandlers> implements NewUserPresenter.MyView
{
	interface Binder extends UiBinder<Widget, NewUserView>
	{
	}

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	TextBox userNameTextBox;

	@UiField
	TextBox fullNameTextBox;

	@UiField
	ListBox authSourceListBox;

	@UiField
	FormLabel passwordLabel;

	@UiField
	ObidosPasswordBox passwordBox;

	@UiField
	TextBox primaryEmailTextBox;

	@UiField
	TextBox primaryPhoneBox;

	@UiField
	FormLabel mustChangePasswordLabel;

	@UiField
	Button showHidePasswordButton;

	@UiField
	InputGroup inputGroup;

	@UiField
	FlowPanel passwordFlowPanel;

	@UiField
	CheckBox sendEmailCheckBox;

	@UiField
	TextArea emailCommentTextArea;

	@UiField
	ToggleSwitch globalTemplateToggleSwitch;

	@UiField
	ToggleSwitch twoFAPasswordResetSwitch;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;
	
	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	Button createUserButton;

	@UiField
	ObidosIntegerTextBox passwordExpiresTextBox;

	@UiField
	ObidosRowBottom2px passwordRow;

	@UiField
	ObidosRowBottom2px expireRow;

	@UiField
	Row twofarow;

	@UiField
	FormLabel twoFAPasswordResetLabel;
	
	@UiField
	Select countryCodeSelect;
	
	@UiField
	ObidosTextBox mobilePhoneTextBox;
	
	
	private void clearMessageTextbox(TextBox tbox)
	{
		tbox.addKeyDownHandler(new KeyDownHandler()
		{
			
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				messageRow.showMessage(null);
			}
		});
		
	}

	@Inject
	NewUserView(Binder uiBinder)
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
					getUiHandlers().showHideHelp();
				}
			});
		}
		clearMessageTextbox(userNameTextBox);
		
		Button backButton = panelHeader.getBackButton();
		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().listUsers();
				}
			});
		}

	}
	
	@UiHandler("userNameTextBox")
	void onTypeuserNameTextBox(KeyUpEvent e)
	{
		getUiHandlers().clearMessage();
	}
	@UiHandler("fullNameTextBox")
	void onTypefullNameTextBox(KeyUpEvent e)
	{
		getUiHandlers().clearMessage();
	}
	@UiHandler("passwordBox")
	void onTypepasswordBox(KeyUpEvent e)
	{
		getUiHandlers().clearMessage();
	}
	@UiHandler("primaryEmailTextBox")
	void onTypeprimaryEmailTextBox(KeyUpEvent e)
	{
		getUiHandlers().clearMessage();
	}
	@UiHandler("primaryPhoneBox")
	void onTypeprimaryPhoneBox(KeyUpEvent e)
	{
		getUiHandlers().clearMessage();
	}
	@UiHandler("passwordExpiresTextBox")
	void onTypepasswordExpiresTextBox(KeyUpEvent e)
	{
		getUiHandlers().clearMessage();
	}

	@UiHandler("sendEmailCheckBox")
	void onClicksendEmailCheckBox(ClickEvent e)
	{
	   getUiHandlers().sendEmailCheckBoxClickHandler();
	}

	@UiHandler("authSourceListBox")
	void onClickAuthSourceListBox(ChangeEvent e)
	{
		getUiHandlers().authSourceChanged();
		getUiHandlers().clearMessage();
	}

	@UiHandler("showHidePasswordButton")
    void onClickShowHidePasswordButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHidePasswordButton, passwordBox);
    }

	@UiHandler("createUserButton")
	void onClickUiHandler(ClickEvent e)
	{
		getUiHandlers().createUser();
	}

	@UiHandler("countryCodeSelect")
	void onSelectcountryCodeSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().countryCodeSelectCallback();
	}


	public TextBox getUserNameTextBox()
	{
		return userNameTextBox;
	}

	public TextBox getFullNameTextBox()
	{
		return fullNameTextBox;
	}

	public ListBox getAuthSourceListBox()
	{
		return authSourceListBox;
	}

	public FormLabel getPasswordLabel()
	{
		return passwordLabel;
	}

	public Input getPasswordBox()
	{
		return passwordBox;
	}

	public FormLabel getChangePasswordLabel()
	{
		return mustChangePasswordLabel;
	}

	public Button getCreateUserButton()
	{
		return createUserButton;
	}

    public TextBox getPrimaryEmailTextBox()
    {
        return primaryEmailTextBox;
    }

    public TextBox getPrimaryPhoneBox()
    {
        return primaryPhoneBox;
    }

    public FormLabel getMustChangePasswordLabel()
    {
        return mustChangePasswordLabel;
    }

    public CheckBox getSendEmailCheckBox()
    {
        return sendEmailCheckBox;
    }

    // Help starts--
    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph helpParagraph;

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    // Help ends--

    public Button getShowHidePasswordButton()
    {
        return showHidePasswordButton;
    }

    public InputGroup getInputGroup()
    {
        return inputGroup;
    }

    public FlowPanel getPasswordFlowPanel()
    {
        return passwordFlowPanel;
    }

    public TextArea getEmailCommentTextArea()
    {
        return emailCommentTextArea;
    }

    public ToggleSwitch getGlobalTemplateToggleSwitch()
    {
        return globalTemplateToggleSwitch;
    }

	public ToggleSwitch getTwoFAPasswordResetSwitch()
	{
		return twoFAPasswordResetSwitch;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosIntegerTextBox getPasswordExpiresTextBox()
	{
		return passwordExpiresTextBox;
	}

	public ObidosRowBottom2px getPasswordRow()
	{
		return passwordRow;
	}

	public ObidosRowBottom2px getExpireRow()
	{
		return expireRow;
	}

	public Row getTwofarow()
	{
		return twofarow;
	}

	public FormLabel getTwoFAPasswordResetLabel()
	{
		return twoFAPasswordResetLabel;
	}

	public Select getCountryCodeSelect()
	{
		return countryCodeSelect;
	}

	public ObidosTextBox getMobilePhoneTextBox()
	{
		return mobilePhoneTextBox;
	}

}