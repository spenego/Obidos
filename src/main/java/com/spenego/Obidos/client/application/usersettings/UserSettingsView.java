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

package com.spenego.Obidos.client.application.usersettings;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;

class UserSettingsView extends ViewWithUiHandlers<UserSettingsUiHandlers> implements UserSettingsPresenter.MyView
{
	interface Binder extends UiBinder<Widget, UserSettingsView>
	{
	}
	
	@Inject
	UserSettingsView(Binder uiBinder)
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
				}
			});
		}
	}
	

   // Automatically generated from src/main/java/com/spenego/Obidos/client/application/usersettings/UserSettingsView.ui.xml by mk_unibinder_uifields.rb
   // spgdev@spenego.com 2019-02-03 16:45:46 -0500, written in Copenhagen, Denmark
	
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	FormLabel fullnameLabel;

	@UiField
	TextBox fullnameTextBox;

	@UiField
	FormLabel primaryEmailLabel;

	@UiField
	TextBox primaryEmailTextBox;

	@UiField
	FormLabel secondaryEmailLabel;

	@UiField
	TextBox secondaryEmailTextBox;

	@UiField
	FormLabel primaryPhoneLabel;

	@UiField
	TextBox primaryPhoneTextBox;

	@UiField
	FormLabel mobilePhoneLabel;


	@UiField
	FormLabel officeLabel;
	
	@UiField
	TextBox officeTextBox;

	@UiField
	FormLabel facebookLabel;

	@UiField
	TextBox facebookTextBox;

	@UiField
	FormLabel twitterLabel;

	@UiField
	TextBox twitterTextBox;

	@UiField
	FormLabel requiresTwoFAPasswordResetLabel;

	@UiField
	FormLabel twoFAEnabledLabel;

	@UiField
	FormLabel acceptNotificationLabel;

	@UiField
	FormLabel hideItemLabel;

	@UiField
	ObidosIntegerTextBox hideItemInSecondsTextBox;

	@UiField
	ObidosMessageRow messageRow;

	@UiField
	Button saveButton;

	@UiField
	Button resetButton;

	@UiField
	ToggleSwitch requiresTwoFAPasswordResetSwitch;
	
	@UiField
	ToggleSwitch isTwoFAEnabledSwitch;
	
	@UiField
	ToggleSwitch acceptNotificationSwitch;
	
	@UiField
	Select selectPage;
	
	@UiField
	FormLabel defaltPageLabel;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;
	
	@UiField
	Image profilePreviewImage;

	@UiField
	Image invisibleImage;
	
	@UiField
	Button uploadButton;
	
	@UiField
	FlowPanel deleteProfileFlowPanel;
	
	@UiField
	FormLabel deleteProfilePicLabel;
	
	@UiField
	ToggleSwitch deleteProfilePicSwitch;

	@UiField
	TextBox passwordExpiresTextBox;

	@UiField
	FormLabel passwordExpiresLabel;

	@UiField
	Label licenseLabel;

	@UiField
	Label smsLicenseLabel;
	
	@UiField
	ToggleSwitch acceptSmsSwitch;
	
	@UiField
	FormLabel acceptSmsNotificaitonLabel;

	@UiField
	Select countryCodeSelect;

	@UiField
	ObidosTextBox mobilePhoneTextBox;

	@UiField
	ToggleSwitch highContrastSwitch;
	@UiField
	ToggleSwitch largetTextSwitch;

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
	
	@UiHandler("fullnameTextBox")
	void onclickFullnameTextBox (KeyUpEvent e)
	{
		getUiHandlers().showFullnameChange(e);
	}
	
	@UiHandler("secondaryEmailTextBox")
	void onclickSecondaryEmailTextBox (KeyUpEvent e)
	{
		getUiHandlers().showAltEmailChagne(e);
	}
	
	@UiHandler("primaryPhoneTextBox")
	void onclickprimaryPhoneTextBox (KeyUpEvent e)
	{
		getUiHandlers().showPhoneChange(e);
	}
	
	@UiHandler("mobilePhoneTextBox")
	void onclickmobilePhoneTextBox (KeyUpEvent e)
	{
		getUiHandlers().showMobilePhoneChange(e);
	}
	
	@UiHandler("officeTextBox")
	void onclickOfficeTextBox (KeyUpEvent e)
	{
		getUiHandlers().showOfficeChange(e);
	}
	
	
	@UiHandler("facebookTextBox")
	void onclickfacebookTextBox (KeyUpEvent e)
	{
		getUiHandlers().showFacebookChange(e);
	}

	@UiHandler("twitterTextBox")
	void onclickTwitterTextBox (KeyUpEvent e)
	{
		getUiHandlers().showTwitterChange(e);
	}
	
	@UiHandler("acceptNotificationSwitch")
	void onclickAcceptShareNotificationSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showAcceptNotifiationChange();
	}
	
	@UiHandler("highContrastSwitch")
	void onClickhighContrastSwitch(ValueChangeEvent<Boolean>e)
	{
		getUiHandlers().highContractSwitchCallback();
	}
	
	@UiHandler("largetTextSwitch")
	void onClicklargetTextSwitch(ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().largetTextSwitchCallback();
	}
	
	@UiHandler("acceptSmsSwitch")
	void onClickacceptSmsSwitch(ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showAcceptSmsChange();
	}
	
	@UiHandler("deleteProfilePicSwitch")
	void onclickDeleteProfilePicSwitch (ValueChangeEvent<Boolean> e)
	{
		getUiHandlers().showDeleteProfilePicChagne();
	}
	
	@UiHandler("selectPage")
	void onclickSelectPage (ValueChangeEvent<String> e)
	{
		getUiHandlers().showDefaultPageChange();
	}

	@UiHandler("hideItemInSecondsTextBox")
	void onclickHideItemInSecondsTextBox (KeyUpEvent e)
	{
		getUiHandlers().showHideItemSecondsChange();
	}
	
	@UiHandler("uploadButton")
	void onclickUploadButton (ClickEvent e)
	{
		getUiHandlers().showUploadProfilePicPage();
	}
	
	/* do not insert code in mobile phone text box */
	/* we will add country with the number before submitting */
	@UiHandler("countryCodeSelect")
	void onSelectcountryCodeSelect(ValueChangeEvent<String> e)
	{
		getUiHandlers().countryCodeSelectCallback();
	}
	

	public FormLabel getFullnameLabel()
	{
		return fullnameLabel;
	}

	public TextBox getFullnameTextBox()
	{
		return fullnameTextBox;
	}

	public FormLabel getPrimaryEmailLabel()
	{
		return primaryEmailLabel;
	}

	public TextBox getPrimaryEmailTextBox()
	{
		return primaryEmailTextBox;
	}

	public FormLabel getSecondaryEmailLabel()
	{
		return secondaryEmailLabel;
	}

	public TextBox getSecondaryEmailTextBox()
	{
		return secondaryEmailTextBox;
	}

	public FormLabel getPrimaryPhoneLabel()
	{
		return primaryPhoneLabel;
	}

	public TextBox getPrimaryPhoneTextBox()
	{
		return primaryPhoneTextBox;
	}

	public FormLabel getMobilePhoneLabel()
	{
		return mobilePhoneLabel;
	}

	public TextBox getMobilePhoneTextBox()
	{
		return mobilePhoneTextBox;
	}

	public FormLabel getFacebookLabel()
	{
		return facebookLabel;
	}

	public TextBox getFacebookTextBox()
	{
		return facebookTextBox;
	}

	public FormLabel getTwitterLabel()
	{
		return twitterLabel;
	}

	public TextBox getTwitterTextBox()
	{
		return twitterTextBox;
	}

	public FormLabel getRequiresTwoFAPasswordResetLabel()
	{
		return requiresTwoFAPasswordResetLabel;
	}

	public FormLabel getTwoFAEnabledLabel()
	{
		return twoFAEnabledLabel;
	}

	public FormLabel getAcceptNotificationLabel()
	{
		return acceptNotificationLabel;
	}

	public FormLabel getHideItemLabel()
	{
		return hideItemLabel;
	}

	public ObidosIntegerTextBox getHideItemInSecondsTextBox()
	{
		return hideItemInSecondsTextBox;
	}

	public Button getSaveButton()
	{
		return saveButton;
	}

	public Button getResetButton()
	{
		return resetButton;
	}

	public ToggleSwitch getRequiresTwoFAPasswordResetSwitch()
	{
		return requiresTwoFAPasswordResetSwitch;
	}

	public ToggleSwitch getIsTwoFAEnabledSwitch()
	{
		return isTwoFAEnabledSwitch;
	}

	public ToggleSwitch getAcceptNotificationSwitch()
	{
		return acceptNotificationSwitch;
	}

	public Select getSelectPage()
	{
		return selectPage;
	}

	public FormLabel getDefaltPageLabel()
	{
		return defaltPageLabel;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public TextBox getOfficeTextBox()
	{
		return officeTextBox;
	}

	public FormLabel getOfficeLabel()
	{
		return officeLabel;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Image getProfilePreviewImage()
	{
		return profilePreviewImage;
	}

	public Image getInvisibleImage()
	{
		return invisibleImage;
	}

	public Button getUploadButton()
	{
		return uploadButton;
	}

	public FormLabel getDeleteProfilePicLabel()
	{
		return deleteProfilePicLabel;
	}

	public ToggleSwitch getDeleteProfilePicSwitch()
	{
		return deleteProfilePicSwitch;
	}

	public FlowPanel getDeleteProfileFlowPanel()
	{
		return deleteProfileFlowPanel;
	}

	public TextBox getPasswordExpiresTextBox()
	{
		return passwordExpiresTextBox;
	}

	public FormLabel getPasswordExpiresLabel()
	{
		return passwordExpiresLabel;
	}

	public Label getLicenseLabel()
	{
		return licenseLabel;
	}

	public ToggleSwitch getAcceptSmsSwitch()
	{
		return acceptSmsSwitch;
	}

	public FormLabel getAcceptSmsNotificaitonLabel()
	{
		return acceptSmsNotificaitonLabel;
	}

	public Label getSmsLicenseLabel()
	{
		return smsLicenseLabel;
	}

	public Select getCountryCodeSelect()
	{
		return countryCodeSelect;
	}

	public ToggleSwitch getHighContrastSwitch()
	{
		return highContrastSwitch;
	}

	public ToggleSwitch getLargetTextSwitch()
	{
		return largetTextSwitch;
	}
}