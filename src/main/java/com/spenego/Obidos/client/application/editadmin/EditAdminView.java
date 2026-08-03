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

package com.spenego.Obidos.client.application.editadmin;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;

class EditAdminView extends ViewWithUiHandlers<EditAdminUiHandlers> implements EditAdminPresenter.MyView
{
	interface Binder extends UiBinder<Widget, EditAdminView>
	{
	}

	@Inject
	EditAdminView(Binder uiBinder)
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
    @UiField
    BlockQuote helpBlockQuote;
    
    @UiField
    Row usernameRow;

    @UiField
    TextBox adminUsernameTextBox;

    @UiField
    TextBox adminFullnameTextBox;

    @UiField
    ObidosPasswordBox passwordBox;

    @UiField
    TextBox emailTextBox;

    @UiField
    TextBox phoneTextBox;

    @UiField
	ObidosMessageRow messageRow;

	@UiField
	Button showHidePasswordButton;

	@UiField
	ObidosPanelHeader panelHeader;
	
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
	Button saveButton;

	@UiField
	Button resetButton;
	
	@UiField
	FormLabel adminUsernameLabel;
	
	@UiField
	FormLabel adminFullnameLabel;
	
	@UiField
	FormLabel emailLabel;
	
	@UiField
	FormLabel phoneLabel;
	
	@UiField
	FormLabel passwordLabel;
	
	@UiField
	FlowPanel mustChangePasswordFp;
	
	@UiField
	FormLabel mustChangePasswordLabel;
	
	@UiField
	Row mustChangePasswordRow;
	
	@UiField
	FormLabel createUserLabel;
	
	@UiField
	FormLabel createAdminLabel;
	
	@UiField
	FormLabel deleteUserLabel;

	@UiField
	FormLabel deleteAdminLabel;
	
	@UiField
	FormLabel lockUserLabel;
	
	@UiField
	FormLabel lockAdminLabel;
	
	@UiField
	FormLabel changeUserCredentialsLabel;
	
	@UiField
	FormLabel changeAdminCredentialsLabel;
	
	@UiField
	FormLabel modifyEmailTemplatesLabel;
	
	@UiField
	InputGroup changePassworinputGroup;
	
	@UiField
	Row capabilitiesRow;
	
	@UiField
	Row rootAdminRow;
	
	@UiField
	FormLabel prompteToRootAdminLabel;
	
	@UiField
	ToggleSwitch promoteToRootAdminSwitch;
	
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
	ToggleSwitch changeSystemSettingSwitch;

	@UiField
	FormLabel changeSystemSettingsLabel;

	@UiHandler("saveButton")
	void onclickSaveButton (ClickEvent e)
	{
		getUiHandlers().save();
	}
	
	@UiHandler("resetButton")
	void onclickResetButton (ClickEvent e)
	{
		getUiHandlers().reset();
	}
	
	@UiHandler("promoteToRootAdminSwitch")
	void onclickRootAdminSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showPromoteToRotoAdminSwitchChange();
	}

	@UiHandler("adminUsernameTextBox")
	void onclickAdminUsernameTextBox (KeyUpEvent e)
	{
		getUiHandlers().showUsernameChange();
	}

	@UiHandler("adminFullnameTextBox")
	void onclickAdminFullnameTextBox (KeyUpEvent e)
	{
		getUiHandlers().showFullnameChange();
	}

	@UiHandler("emailTextBox")
	void onclickAdminEmailTextBox (KeyUpEvent e)
	{
		getUiHandlers().showEmailChange();
	}

	@UiHandler("phoneTextBox")
	void onclickPhoneTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPhoneChange();
	}
	
	@UiHandler("passwordBox")
	void onclickPasswordBox (KeyUpEvent e)
	{
		getUiHandlers().showPasswordChange();
	}
	
	@UiHandler("createUserSwitch")
	void onclickCreateUserSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showCreateUsersChange();
	}
	@UiHandler("changeSystemSettingSwitch")
	void onclickchangeSystemSettingSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showModifySettingsChagne();
	}
	@UiHandler("createAdminSwitch")
	void onclickcreateAdminSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showCreateAdminsChange();
	}
	
	@UiHandler("deleteUserSwitch")
	void onclickdeleteUserSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showDeleteUsersChange();
	}
	
	@UiHandler("deleteAdminSwitch")
	void onclickdeleteAdminSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showDeleteAdminsChange();
	}
	
	@UiHandler("lockUserSwitch")
	void onclicklockUserSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showLockUsersChange();
	}
	
	@UiHandler("lockAdminSwitch")
	void onclicklockAdminSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showLockAdminsChange();
	}
	
	@UiHandler("changeUserCredentialsSwitch")
	void onclickchangeUserCredentialsSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showChangeUsersCredentialsChange();
	}
	
	@UiHandler("changeAdminCredentialsSwitch")
	void onclickchangeAdminCredentialsSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showChangeAdminsCredentialsChange();
	}

	
	@UiHandler("modifyEmailTemplatesSwitch")
	void onclickmodifyEmailTemplatesSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showModifyEmailTemplateChange();
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

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public TextBox getAdminFullnameTextBox()
	{
		return adminFullnameTextBox;
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

	public Button getSaveButton()
	{
		return saveButton;
	}

	public Button getResetButton()
	{
		return resetButton;
	}
	
	public TextBox getAdminUsernameTextBox()
	{
		return adminUsernameTextBox;
	}

	public FormLabel getAdminUsernameLabel()
	{
		return adminUsernameLabel;
	}

	public FormLabel getAdminFullnameLabel()
	{
		return adminFullnameLabel;
	}

	public FormLabel getEmailLabel()
	{
		return emailLabel;
	}

	public FormLabel getPhoneLabel()
	{
		return phoneLabel;
	}

	public FormLabel getPasswordLabel()
	{
		return passwordLabel;
	}

	public FlowPanel getMustChangePasswordFp()
	{
		return mustChangePasswordFp;
	}

	public FormLabel getMustChangePasswordLabel()
	{
		return mustChangePasswordLabel;
	}

	public Row getMustChangePasswordRow()
	{
		return mustChangePasswordRow;
	}

	public FormLabel getCreateUserLabel()
	{
		return createUserLabel;
	}

	public FormLabel getCreateAdminLabel()
	{
		return createAdminLabel;
	}
	

	public Row getUsernameRow()
	{
		return usernameRow;
	}

	public InputGroup getChangePassworinputGroup()
	{
		return changePassworinputGroup;
	}

	public Row getCapabilitiesRow()
	{
		return capabilitiesRow;
	}

	public FormLabel getDeleteUserLabel()
	{
		return deleteUserLabel;
	}

	public FormLabel getDeleteAdminLabel()
	{
		return deleteAdminLabel;
	}

	public FormLabel getLockUserLabel()
	{
		return lockUserLabel;
	}

	public FormLabel getLockAdminLabel()
	{
		return lockAdminLabel;
	}

	public FormLabel getChangeUserCredentialsLabel()
	{
		return changeUserCredentialsLabel;
	}

	public FormLabel getChangeAdminCredentialsLabel()
	{
		return changeAdminCredentialsLabel;
	}

	public FormLabel getModifyEmailTemplatesLabel()
	{
		return modifyEmailTemplatesLabel;
	}

	public Row getRootAdminRow()
	{
		return rootAdminRow;
	}

	public ToggleSwitch getPromoteToRootAdminSwitch()
	{
		return promoteToRootAdminSwitch;
	}

	public FormLabel getPrompteToRootAdminLabel()
	{
		return prompteToRootAdminLabel;
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

	@Deprecated
	@Override
	public Button getHelpButton()
	{
		return null;
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

	public ToggleSwitch getChangeSystemSettingSwitch()
	{
		return changeSystemSettingSwitch;
	}

	public FormLabel getChangeSystemSettingsLabel()
	{
		return changeSystemSettingsLabel;
	}
}