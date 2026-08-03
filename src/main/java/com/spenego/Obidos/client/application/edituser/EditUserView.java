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

package com.spenego.Obidos.client.application.edituser;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.E164PhoneTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.util.ClientUtils;

class EditUserView extends ViewWithUiHandlers<EditUserUiHandlers> implements EditUserPresenter.MyView
{
    interface Binder extends UiBinder<Widget, EditUserView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    TextBox usernameTextBox;

    @UiField
    TextBox fullnameTextBox;

    @UiField
    TextBox primaryemailTextBox;

    @UiField
    TextBox primaryphoneTextBox;

    @UiField
    ListBox authSourceListBox;

    @UiField
    Button resetButton;

    @UiField
    Button saveButton;

    @UiField
    FormLabel usernameLabel;

    @UiField
    FormLabel fullnameLabel;

    @UiField
    FormLabel primaryEmailLabel;

    @UiField
    FormLabel primaryPhoneLabel;

    @UiField
    FormLabel authSourceLabel;

    @UiField
    FlowPanel authSourceFlowPanel;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    ObidosPasswordBox resetPasswordBox;

    @UiField
    Button showHidePasswordButton;

    @UiField
    FormLabel resetPasswordLabel;

    @UiField
    Button listButton;

    @UiField
    Row settingsRow;
    
    // Requires 2FA
    @UiField
    FormLabel requiresTwoFAPasswordResetLabel;
    @UiField
    ToggleSwitch requiresTwoFAPasswordResetSwitch;

    // 2FA Enabled?
    @UiField
    FormLabel twoFAEnabledLabel;
    @UiField
    ToggleSwitch isTwoFAEnabledSwitch;

    // reset 2FA
    @UiField
    FlowPanel reset2FAFlowPanel;
    @UiField
    FormLabel reset2FALabel;
    @UiField
    CheckBox reset2FACheckBox;

    // can create global template
    @UiField
    FormLabel canCreateGlobalTemplateLabel;

    @UiField
    ToggleSwitch globalTemplateToggleSwitch;
    
    @UiField
    ObidosButtonToolBar buttonToolBarBottom;

	@UiField
	Image profilePreviewImage;

	@UiField
	Button uploadButton;

	@UiField
	Image invisibleImage;

	@UiField
	FlowPanel deleteProfileFlowPanel;
	
	@UiField
	FormLabel deleteProfilePicLabel;
	
	@UiField
	ToggleSwitch deleteProfilePicSwitch;

	@UiField
	FormLabel passwordExpiresLabel;
	
	@UiField
	ObidosReadonlyTextBox passwordExpiresTextBox;
	
	@UiField
	Row passwordExpiresRow;
	
	@UiField
	Row twofaRequiredRow;
	
	@UiField
	Row twofaEnabledRow;
	
	@UiField
	ObidosRowBottom2px changePasswordRow;

	@UiField
	ObidosIntegerTextBox passwordAgeTextBox;

	@UiField
	FormLabel passwordAgeLabel;

	@UiField
	Select countryCodeSelect;

	@UiField
	ObidosTextBox mobilePhoneTextBox;

	@UiField
	FormLabel mobilePhoneLabel;

    @Inject
    EditUserView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
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

		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().back();
				}
			});
		}
    }

    @UiHandler("showHidePasswordButton")
    void onClickShowHidePasswordButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHidePasswordButton, resetPasswordBox);
    }

    @UiHandler("resetButton")
    void onClickResetButton(ClickEvent e)
    {
        getUiHandlers().resetForm(null);
    }

    @UiHandler("saveButton")
    void onClickSaveButton(ClickEvent e)
    {
        getUiHandlers().updateUser();

    }

    @UiHandler("listButton")
    void onClickListButton(ClickEvent e)
    {
        getUiHandlers().listUsers();

    }

    @UiHandler("usernameTextBox")
    void onKeyUpUsernameTextBox(KeyUpEvent e)
    {
        getUiHandlers().showUsernameChange();
    }

    @UiHandler("fullnameTextBox")
    void onKeyUpFullnameTextBox(KeyUpEvent e)
    {
        getUiHandlers().showFullnameChange();
    }

    @UiHandler("primaryemailTextBox")
    void onKeyUpPrimaryMailTextBox(KeyUpEvent e)
    {
        getUiHandlers().showPrimaryEmailChange();
    }

    @UiHandler("primaryphoneTextBox")
    void onKeyUpPrimaryPhoneextBox(KeyUpEvent e)
    {
        getUiHandlers().showPrimaryPhoneChange();
    }

    @UiHandler("resetPasswordBox")
    void onKeyUpResetPasswordBox(KeyUpEvent e)
    {
        getUiHandlers().showResetUserPasswordChange();
    }

    @UiHandler("authSourceListBox")
    void onClickAuthSourceListBox(ChangeEvent e)
    {
        getUiHandlers().showAuthSourceChange();
    }

    @UiHandler("requiresTwoFAPasswordResetSwitch")
    void onclickRequiresTwoFAPasswordResetSwitch (ValueChangeEvent<Boolean> e)
	{
    	getUiHandlers().showRequires2FAPasswordResetChange();
	}

    @UiHandler("reset2FACheckBox")
    void onclickReset2FACheckBox (ClickEvent e)
	{
    	getUiHandlers().showReset2FAChange();
	}

    @UiHandler("globalTemplateToggleSwitch")
    void onclickGlobalTemplateToggleSwich (ValueChangeEvent<Boolean> e)
	{
    	getUiHandlers().showCanCreateGlobalTemplateChange();
	}
    
    @UiHandler("uploadButton")
    void onclickUploadButton (ClickEvent e)
	{
    	getUiHandlers().showUploadProfilePicPage();
	}

	@UiHandler("deleteProfilePicSwitch")
	void onclickDeleteProfilePicSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showDeleteProfilePicChagne();
	}
	
	@UiHandler("passwordAgeTextBox")
	void onclickpasswordExpiresTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordAgeChange();
	}

	@UiHandler("countryCodeSelect")
	void onSelectcountryCodeSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().countryCodeSelectCallback();
	}

	@UiHandler("mobilePhoneTextBox")
	void onclickmobilePhoneTextBox (KeyUpEvent e)
	{
		getUiHandlers().showCountryCodeSlectionChange();
	}

    public TextBox getUsernameTextBox()
    {
        return usernameTextBox;
    }


    public TextBox getFullnameTextBox()
    {
        return fullnameTextBox;
    }


    public TextBox getPrimaryemailTextBox()
    {
        return primaryemailTextBox;
    }


    public TextBox getPrimaryphoneTextBox()
    {
        return primaryphoneTextBox;
    }


    public ListBox getAuthSourceListBox()
    {
        return authSourceListBox;
    }


    public Button getResetButton()
    {
        return resetButton;
    }


    public Button getSaveButton()
    {
        return saveButton;
    }

    public FormLabel getUsernameLabel()
    {
        return usernameLabel;
    }

    public FormLabel getFullnameLabel()
    {
        return fullnameLabel;
    }

    public FormLabel getPrimaryEmailLabel()
    {
        return primaryEmailLabel;
    }

    public FormLabel getPrimaryPhoneLabel()
    {
        return primaryPhoneLabel;
    }

    public FormLabel getAuthSourceLabel()
    {
        return authSourceLabel;
    }

    public FlowPanel getAuthSourceFlowPanel()
    {
        return authSourceFlowPanel;
    }

    public ObidosPasswordBox getResetPasswordBox()
    {
        return resetPasswordBox;
    }

    public Button getShowHidePasswordButton()
    {
        return showHidePasswordButton;
    }

    public FormLabel getResetPasswordLabel()
    {
        return resetPasswordLabel;
    }

    public Button getListButton()
    {
        return listButton;
    }

	public ToggleSwitch getGlobalTemplateToggleSwitch()
	{
		return globalTemplateToggleSwitch;
	}

	public FormLabel getRequiresTwoFAPasswordResetLabel()
	{
		return requiresTwoFAPasswordResetLabel;
	}

	public ToggleSwitch getRequiresTwoFAPasswordResetSwitch()
	{
		return requiresTwoFAPasswordResetSwitch;
	}

	public FormLabel getTwoFAEnabledLabel()
	{
		return twoFAEnabledLabel;
	}

	public ToggleSwitch getIsTwoFAEnabledSwitch()
	{
		return isTwoFAEnabledSwitch;
	}

	public FlowPanel getReset2FAFlowPanel()
	{
		return reset2FAFlowPanel;
	}

	public FormLabel getReset2FALabel()
	{
		return reset2FALabel;
	}

	public CheckBox getReset2FACheckBox()
	{
		return reset2FACheckBox;
	}

	public FormLabel getCanCreateGlobalTemplateLabel()
	{
		return canCreateGlobalTemplateLabel;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Row getSettingsRow()
	{
		return settingsRow;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Image getProfilePreviewImage()
	{
		return profilePreviewImage;
	}

	public Button getUploadButton()
	{
		return uploadButton;
	}

	public Image getInvisibleImage()
	{
		return invisibleImage;
	}
	public FlowPanel getDeleteProfileFlowPanel()
	{
		return deleteProfileFlowPanel;
	}

	public FormLabel getDeleteProfilePicLabel()
	{
		return deleteProfilePicLabel;
	}

	public ToggleSwitch getDeleteProfilePicSwitch()
	{
		return deleteProfilePicSwitch;
	}

	public FormLabel getPasswordExpiresLabel()
	{
		return passwordExpiresLabel;
	}

	public ObidosReadonlyTextBox getPasswordExpiresTextBox()
	{
		return passwordExpiresTextBox;
	}

	public Row getPasswordExpiresRow()
	{
		return passwordExpiresRow;
	}

	public Row getTwofaRequiredRow()
	{
		return twofaRequiredRow;
	}

	public Row getTwofaEnabledRow()
	{
		return twofaEnabledRow;
	}

	public ObidosRowBottom2px getChangePasswordRow()
	{
		return changePasswordRow;
	}

	public ObidosIntegerTextBox getPasswordAgeTextBox()
	{
		return passwordAgeTextBox;
	}

	public FormLabel getPasswordAgeLabel()
	{
		return passwordAgeLabel;
	}

	public Select getCountryCodeSelect()
	{
		return countryCodeSelect;
	}

	public ObidosTextBox getMobilePhoneTextBox()
	{
		return mobilePhoneTextBox;
	}

	public FormLabel getMobilePhoneLabel()
	{
		return mobilePhoneLabel;
	}
}