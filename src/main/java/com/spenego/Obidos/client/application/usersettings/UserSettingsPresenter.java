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

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.FileUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.resources.ThemeManager;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.ObidosMap;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.vonage.client.meetings.Theme;

public class UserSettingsPresenter extends Presenter<UserSettingsPresenter.MyView, UserSettingsPresenter.MyProxy>
		implements UserSettingsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private UserDTO sUserDTO = null;
	private ObidosMap<String,Integer> defaultPageMap = ClientUtils.defaultPageAfterLoginMap();
	
	// Key is 'Country Name (+code)', value is CountryCode
	private static Map<String, CountryCodeDTO> countryCodeMap = null;

	private enum FieldNumber
	{
		fullname,
		alternateEmail,
		phone,
		mobilePhone,
		office,
		facebook,
		twitter,
		defaultPage,
		requiresTwoFA,
		twoFAEnabled,
		acceptNotification,
		hideItemSeconds,
		deleteProfilePic,
		acceptSmsNotification,
	};
	
    private static EnumMap<FieldNumber, Boolean> eMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);
	private void resetEmap()
	{
		eMap.put(FieldNumber.fullname,false);
		eMap.put(FieldNumber.alternateEmail,false);
		eMap.put(FieldNumber.phone,false);
		eMap.put(FieldNumber.mobilePhone,false);
		eMap.put(FieldNumber.office,false);
		eMap.put(FieldNumber.facebook,false);
		eMap.put(FieldNumber.twitter,false);
		eMap.put(FieldNumber.defaultPage,false);
		eMap.put(FieldNumber.requiresTwoFA,false);
		eMap.put(FieldNumber.twoFAEnabled,false);
		eMap.put(FieldNumber.acceptNotification,false);
		eMap.put(FieldNumber.hideItemSeconds,false);
		eMap.put(FieldNumber.deleteProfilePic,false);
		eMap.put(FieldNumber.acceptSmsNotification, false);
	}
	
	private UserDTO getOriginalUserDTO()
	{
		return sUserDTO;
	}
	
	private void setOriginalUserDTO(UserDTO dto)
	{
		sUserDTO = dto;
	}

	interface MyView extends View, HasUiHandlers<UserSettingsUiHandlers>
	{
		// Automatically generated from src/main/java/com/spenego/Obidos/client/application/usersettings/UserSettingsView.ui.xml by mk_unibinder_uifields.rb
		// spgdev@spenego.com 2019-02-03 16:50:44 -0500, Copenhagen, Denmark
		public FormLabel getFullnameLabel();
		public FormLabel getPrimaryEmailLabel();
		public FormLabel getSecondaryEmailLabel();
		public FormLabel getPrimaryPhoneLabel();
		public FormLabel getMobilePhoneLabel();
		public FormLabel getOfficeLabel();
		public FormLabel getFacebookLabel();
		public FormLabel getTwitterLabel();
		public FormLabel getRequiresTwoFAPasswordResetLabel();
		public FormLabel getTwoFAEnabledLabel();
		public FormLabel getAcceptNotificationLabel();
		public Select getSelectPage();
		public FormLabel getHideItemLabel();

		public TextBox getFullnameTextBox();
		public TextBox getPrimaryEmailTextBox();
		public TextBox getSecondaryEmailTextBox();
		public TextBox getPrimaryPhoneTextBox();
		public TextBox getOfficeTextBox();
		public TextBox getFacebookTextBox();
		public TextBox getTwitterTextBox();
		public ObidosIntegerTextBox getHideItemInSecondsTextBox();
		public Button getSaveButton();
		public Button getResetButton();
		public ToggleSwitch getRequiresTwoFAPasswordResetSwitch();
		public ToggleSwitch getIsTwoFAEnabledSwitch();
		public ToggleSwitch getAcceptNotificationSwitch();
		public FormLabel getDefaltPageLabel();
		public ObidosMessageRow getMessageRow();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public ObidosPanelHeader getPanelHeader();
		public BlockQuote getHelpBlockQuote();
		public Image getProfilePreviewImage();
		public Image getInvisibleImage();
		public Button getUploadButton();
		public FormLabel getDeleteProfilePicLabel();
		public ToggleSwitch getDeleteProfilePicSwitch();
		public FlowPanel getDeleteProfileFlowPanel();
		public TextBox getPasswordExpiresTextBox();
		public FormLabel getPasswordExpiresLabel();
		public Label getLicenseLabel();
		public Label getSmsLicenseLabel();
		public ToggleSwitch getAcceptSmsSwitch();
		public FormLabel getAcceptSmsNotificaitonLabel();
		public Select getCountryCodeSelect();
		public TextBox getMobilePhoneTextBox();

		public ToggleSwitch getHighContrastSwitch();
		public ToggleSwitch getLargetTextSwitch();
	}

	@NameToken(NameTokens.USER_SETTINGS)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<UserSettingsPresenter>
	{
	}

	final PlaceManager placeManager;
	final CurrentUser currentUser;
	@Inject
	UserSettingsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		
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
		resetForm();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage("");
		getView().getButtonToolBarBottom().adjustButtonsWidth();
		enableButtons(false);
		resetForm();
		getView().getAcceptNotificationSwitch().setEnabled(true);
		getView().getAcceptSmsSwitch().setEnabled(true);
		checkLicense(null);
    	getView().getUploadButton().setText(glang.uploadProfilePic());
		fetchAndPopulateForm();
		getView().getAcceptNotificationSwitch().setEnabled(true);
		getView().getAcceptSmsSwitch().setEnabled(true);
	}

    private void checkLicense(final UserDTO dto)
    {
    	gwtLog("MMM in checkLicense");
		CurrentUser cuser = this.currentUser;
		LicenseStats license = null;
		if (cuser != null)
		{
			UserDTO userDTO = cuser.getUserDTO();
			if (userDTO != null)
			{
				license = userDTO.getLicense();
			}
		}
		ToggleSwitch ts = getView().getAcceptNotificationSwitch();
		ts.setEnabled(true);
		ts.setTitle("");

		// accept email notificatoon
		boolean supportEmailNotifiation = ClientUtils.doesLicenseSupportEmailNotifiation(license);
		Label label = getView().getLicenseLabel();
		label.setVisible(false);
		String t = glang.notSupportedByLicense();
		if (! supportEmailNotifiation)
		{
			if (dto != null)
			{
				// license might have changed, update database
				dto.clearAcceptEmailNotification();
			}
			ts.setValue(false);
			ts.setEnabled(false);
			ts.setTitle(t);
			label.setVisible(true);
			label.setText(t);
		}
		
		// accept SMS notification
		ts = getView().getAcceptSmsSwitch();
		ts.setEnabled(true);
		ts.setTitle("");

		gwtLog("MMM check license, SMS: " + ts.isEnabled());

		label = getView().getSmsLicenseLabel();
		label.setVisible(false);
		boolean supportSmsNotification = ClientUtils.doesLicenseSupportSMSNotification(license);
		if (! supportSmsNotification)
		{
			if (dto != null)
			{
				// license might have changed, update database
				dto.clearAcceptSMSNotification();
			}
			gwtLog("MMM SMS not supported by license");
			ts.setValue(false);
			ts.setEnabled(false);
			ts.setTitle(t);
			label.setVisible(true);
			label.setText(t);
		}
		gwtLog("MMM check license, SMS: " + ts.isEnabled());
	}

	private void enableButtons(boolean enabled)
	{
		getView().getSaveButton().setEnabled(enabled);
		getView().getResetButton().setEnabled(enabled);
	}
	
	private void enableResetButton(boolean enabled)
	{
		getView().getResetButton().setEnabled(enabled);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}
	
	private void resetForm()
	{
		sUserDTO = null;
		resetEmap();
		resetFormLabelColors();
		resetToggleSwitches();
	}
	
	private void setDefaultProfilePreviewPic()
	{
		int width = ObidosConstants.PROFILE_PIC_MAX_WIDTH;
		int height = ObidosConstants.PROFILE_PIC_MAX_HEIGHT;
		Image image = getView().getProfilePreviewImage();
		image.setUrl(ObidosConstants.PROFILE_PLACEHOLDER_IMAGE);
		image.setPixelSize(width, height);
		image.getElement().getStyle().setWidth(width, Unit.PX);
		image.getElement().getStyle().setHeight(height, Unit.PX);
		String style = "1px solid #ddd";
		image.getElement().getStyle().setProperty("border", style);
		image.setTitle(glang.profilePicPreviewArea());
	}
	
	private void resetToggleSwitches()
	{
		getView().getAcceptNotificationSwitch().setReadOnly(false);
        getView().getDeleteProfilePicSwitch().setValue(false);
	}
	
    private void resetFormLabelColors()
    {
		ClientUtils.setFormLabelsColorOriginal(getView().getFullnameLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getPrimaryEmailLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getSecondaryEmailLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getPrimaryPhoneLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMobilePhoneLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getOfficeLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getFacebookLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getTwitterLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getRequiresTwoFAPasswordResetLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getTwoFAEnabledLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getAcceptNotificationLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getDefaltPageLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getHideItemLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getDefaltPageLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getDeleteProfilePicLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getAcceptSmsNotificaitonLabel());
    }
    
    private void displayDefaultProfilePic()
    {
		getView().getUploadButton().setText(glang.uploadProfilePic());
   		getView().getDeleteProfilePicLabel().setVisible(false);
   		getView().getDeleteProfileFlowPanel().setVisible(false);
   		ClientUtils.displayProfilePlaceHolderImage(getView().getProfilePreviewImage());
    }
    
    private void updateCountryCodeList(final UserDTO dto)
    {
    	// save
    	countryCodeMap = dto.getCountryCodesMap();
    	ClientUtils.updateCountryCodeSelectList(dto,
    			getView().getMobilePhoneTextBox(),
    			getView().getCountryCodeSelect());
    	/*
    	if (countryCodeMap != null)
    	{
			for (Map.Entry<String, CountryCodeDTO> entry : countryCodeMap.entrySet())
			{
				String key = entry.getKey();
				CountryCodeDTO cc = entry.getValue();
				gwtLog("MMM key:" +  key);
				gwtLog("  MMM Country: " + cc.getCountryName());
				gwtLog("  MMM Example: " + cc.getExampleNumber());
				gwtLog("  MMM line: " + cc.getCountryCodeLine());
				gwtLog("  MMM code: " + cc.getDialCode());
				gwtLog("  MMM region: " + cc.getNumberRegion());
			}
    	}
    	*/
    }
    
    private void displayProfilePic(final UserDTO dto)
    {
    	byte[] profilePicBytes = dto.getProfilePic();
    	if (profilePicBytes == null)
    	{
    		displayDefaultProfilePic();
    		return;
    	}
    	if (profilePicBytes.length == 0)
    	{
    		displayDefaultProfilePic();
    		return;
    	}

		gwtLog("Didplay profile pic...........");
		getView().getUploadButton().setText(glang.uploadNewProfilePic());

		getView().getDeleteProfilePicLabel().setVisible(true);
		getView().getDeleteProfileFlowPanel().setVisible(true);

		getView().getDeleteProfilePicSwitch().setValue(false);
		String dataURL = new String(profilePicBytes);
		Image profilePicImageWidget = getView().getProfilePreviewImage();
		Image invisibleImageWidget = getView().getInvisibleImage();
		ClientUtils.displayProfilePicture(dataURL, profilePicImageWidget, invisibleImageWidget);
    	
    }
	private void fetchAndPopulateForm()
	{
		if (currentUser == null)
		{
			ClientUtils.showBootboxDialog(ObidosMessages.LANG.error() , ObidosMessages.LANG.couldNotDetermineLoggedInUser()); 
			return;
		}
		GwtAsyncWrapper<UserDTO> callback = new GwtAsyncWrapper<UserDTO>(this)
		{

			@Override
			public void uponSuccess(UserDTO dto)
			{
				setOriginalUserDTO(dto);
				displayProfilePic(dto);
				
				// select the country code line in Select list
				updateCountryCodeList(dto);
				

				getView().getFullnameTextBox().setValue(dto.getFullname());
				getView().getPrimaryEmailTextBox().setValue(dto.getEmail1());
				getView().getSecondaryEmailTextBox().setValue(dto.getEmail2());
				getView().getPrimaryPhoneTextBox().setValue(dto.getPhone());

				// remove country code from the beginning of mobile phone
				// Note the number will have + at the beginning but our
				// widget does not allow that, so only the dial code will be
				// at the beginning
				String mobilePhone = dto.getMobile1();
				gwtLog("MMM in fetch: mobile: " + mobilePhone);
				
				CountryCodeDTO countryCodeDTO = dto.getCountryCodeDTO();
				if (countryCodeDTO != null)
				{
					String numberNationalFormat = countryCodeDTO.getNumberNationalFormat();
					gwtLog("MMM national format: "+ numberNationalFormat);
					gwtLog("MMMMMMMMMMMMMMMMMMMMMMMMMMM");
					getView().getMobilePhoneTextBox().setValue(numberNationalFormat);
					gwtLog("MMMMMMMMMMMMMMMMMMMMMMMMMMM: " + getView().getMobilePhoneTextBox().getValue());
					String exampleNumber = countryCodeDTO.getExampleNumber();
					if (exampleNumber != null)
					{
						getView().getMobilePhoneTextBox().setPlaceholder(countryCodeDTO.getExampleNumber());
					}
					else
					{
						gwtLog("MMM CountryCodeDTO is null");
						getView().getMobilePhoneTextBox().setPlaceholder(glang.mobilePhoneWithoutCountryCode());
					}
				}
				else
				{
					getView().getMobilePhoneTextBox().setValue(dto.getMobile1());
				}

				// get it from the widget + will be gone
				mobilePhone = getView().getMobilePhoneTextBox().getValue();
				gwtLog("MMM in fetch: mobile phone in widget: " + mobilePhone);
				// remove the dial code from the beginning
				Option countryCodeOption = getView().getCountryCodeSelect().getSelectedItem();
				gwtLog("MMM country code option: " + countryCodeOption);
				// example valid E.164 number for denmark +4523456789
				/*
				if (countryCodeOption != null)
				{
					gwtLog("MMM MMM MMM ");
					String countryCode = countryCodeOption.getValue();
					gwtLog("MMM country code line" + countryCode);
					String dialCode = ClientUtils.extractDialCode(countryCode);
					gwtLog("MMM in fetch dial code: " + dialCode);
					mobilePhone = ClientUtils.removeCountryCode(dto.getMobile1(), dialCode);
					gwtLog("MMM phone without country code: " + mobilePhone);
					getView().getMobilePhoneTextBox().setValue(mobilePhone);
				}
				*/

				getView().getOfficeTextBox().setValue(dto.getOffice());
				getView().getFacebookTextBox().setValue(dto.getFacebook());
				getView().getTwitterTextBox().setValue(dto.getTwitter());

				ToggleSwitch ts = getView().getRequiresTwoFAPasswordResetSwitch();
				ClientUtils.setToggleSwitchValue(ts, dto.getTwoFARequired());

				ts = getView().getIsTwoFAEnabledSwitch();
				gwtLog("2FA Enabled " + dto.getTwoFAPasswordResetEnabled());
				ClientUtils.setToggleSwitchValue(ts, dto.getTwoFAPasswordResetEnabled());
				
				gwtLog("MMM After fetch: accept email notificaiton: " + dto.getAcceptEmailNotification());
				gwtLog("MMM After fetch: accempt SMS: " + dto.getAcceptSMSNotification());

				getView().getRequiresTwoFAPasswordResetSwitch().setValue(dto.getTwoFARequired());
				getView().getIsTwoFAEnabledSwitch().setValue(dto.getTwoFAPasswordResetEnabled());
				getView().getAcceptNotificationSwitch().setValue(dto.getAcceptEmailNotification());
				getView().getAcceptSmsSwitch().setValue(dto.getAcceptSMSNotification());

				// accept email notification
				ts = getView().getAcceptNotificationSwitch();
				//ClientUtils.setToggleSwitchValue(ts, dto.getAcceptEmailNotification());
				
				ts = getView().getAcceptSmsSwitch();
				//ClientUtils.setToggleSwitchValue(ts, dto.getAcceptSMSNotification());
				
				checkLicense(dto);
				
				Integer delay = ClientUtils.fromInteger(dto.getHideItemDelay());
				if (delay != null)
				{
					getView().getHideItemInSecondsTextBox().setValue(delay);
					if (delay == 501 && Cookies.isCookieEnabled())
					{
						// easter egg to enable Bangla editing
						// set cookie for 30 days
						Date now = new Date();
						long nowLong = now.getTime();
						nowLong = nowLong + (1000L * 60 * 60 * 24 * 30);
						now.setTime(nowLong);
						Cookies.setCookie(ObidosConstants.OBIDOS_EBE_COOKIE,"yes", now);
					}
					else
					{
						if (Cookies.isCookieEnabled())
						{
							Cookies.removeCookie(ObidosConstants.OBIDOS_EBE_COOKIE);
						}
					}
					if (delay == 502 && Cookies.isCookieEnabled())
					{
						// easter egg to change language
						// set cookie for 30 days
						Date now = new Date();
						long nowLong = now.getTime();
						nowLong = nowLong + (1000L * 60 * 60 * 24 * 30);
						now.setTime(nowLong);
						Cookies.setCookie(ObidosConstants.OBIDOS_LANGUAGE_COOKIE,"yes", now);
					}
					else
					{
						if (Cookies.isCookieEnabled())
						{
							Cookies.removeCookie(ObidosConstants.OBIDOS_LANGUAGE_COOKIE);
						}
					}

				}
				
				// update default page
				Integer defaultPage = dto.getDefaultPage();
				if (defaultPage == null)
				{
					gwtLog("ERROR: default page is null");
					defaultPage = UserDTO.DEFAULT_PAGE_LIST_CONTAINERS;
				}
				String page = defaultPageMap.getKey(defaultPage);
				gwtLog("Setting page in Selct to: " + page);
				getView().getSelectPage().setValue(page);

				// dto does not have this at this time
				int days = ClientUtils.fromInteger(currentUser.getUserDTO().getDaysUntilPasswordExpiration());
				gwtLog("Password expires in: "+ days + " days");

				FormLabel label = getView().getPasswordExpiresLabel();
				ClientUtils.changePasswordExpirationTexts(label, getView().getPasswordExpiresTextBox(), days);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
			
				showErrorMessage("Could not fetch User: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().getUser(authCreds, currentUser.getUserDTO().getId(), callback);
	}

	private void updateCurrentUserWithDTO(UserDTO userDTO)
	{
		if (currentUser == null)
		{
			return;
		}
		gwtLog("Updating UserDTO in currentUser");
		UserDTO dtoInCurrentUser = currentUser.getUserDTO();
		// right now just update hide item delay
		dtoInCurrentUser.setHideItemDelay(userDTO.getHideItemDelay());
	}
	
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}
	
	private void updateUserDTO(UserDTO dto, FieldNumber fn)
	{
		switch(fn)
		{
			case fullname:
			{
				String value = getView().getFullnameTextBox().getValue();
				dto.setFullname(value);
				break;
			}
			case alternateEmail:
			{
				String value = getView().getSecondaryEmailTextBox().getValue();
				dto.setEmail2(value);
				break;
			}
			case phone:
			{
				String value = getView().getPrimaryPhoneTextBox().getValue();
				gwtLog("MMM changed phone: " + "'" + value + "'");
				dto.setPhone(value);
				break;
			}
			case mobilePhone:
			{
				String value = getView().getMobilePhoneTextBox().getValue();
				dto.setMobile1(value);
				break;
			}
			
			case office:
			{
				String value = getView().getOfficeTextBox().getValue();
				dto.setOffice(value);
				break;
			}
			case facebook:
			{
				String value = getView().getFacebookTextBox().getValue();
				dto.setFacebook(value);
				break;
			}
			case twitter:
			{
				String value = getView().getTwitterTextBox().getValue();
				dto.setTwitter(value);
				break;
			}
			case defaultPage:
			{
				String value = getView().getSelectPage().getValue();
				Integer pageNumber = defaultPageMap.get(value);
				if (pageNumber == null)
				{
					gwtLog("ERROR: page number is, setting to list my container page");
					pageNumber = UserDTO.DEFAULT_PAGE_LIST_CONTAINERS;
				}
				gwtLog("Default page number: " + pageNumber);
				dto.setDefaultPage(pageNumber);
				break;
			}
			case acceptNotification:
			{
				Boolean value = getView().getAcceptNotificationSwitch().getValue();
				gwtLog("accept notification: " + value);
				if (value) {
					dto.setAcceptEmailNotification();
				} else {
					dto.clearAcceptEmailNotification();
				}
				break;
			}
			case acceptSmsNotification:
			{
				Boolean value = getView().getAcceptSmsSwitch().getValue();
				if (value) 
				{
					dto.setAcceptSMSNotification();
				}
				else
				{
					dto.clearAcceptSMSNotification();
				}
				break;
			}
			
			case hideItemSeconds:
			{
				Integer value = getView().getHideItemInSecondsTextBox().getValue();
				if (value == null)
				{
					gwtLog("Hide delay is null, setting to 0");
					value = 0;
				}
				dto.setHideItemDelay(value);
				break;
			}
			
			default:
			{
				break;
			}
		}
	}
	
	private boolean setMobilePhoneToUserDTO(final UserDTO userDTO)
	{
		String mobilePhone = getView().getMobilePhoneTextBox().getValue();
		if (mobilePhone == null || mobilePhone.length() == 0)
		{
			// delete
			userDTO.setMobile1("");
			return true;
		}
		
		Option option = getView().getCountryCodeSelect().getSelectedItem();
		gwtLog("MMM selected item: " + option);
		if (mobilePhone.length() > 0 && option == null)
		{
			showErrorMessage("Please select a Country Code for Phone Number of SMS");
			return false;
		}
		if (option != null)
		{
			// construct the number with countrycode+number
			String countryCode = getView().getCountryCodeSelect().getSelectedItem().getValue();
			String dialCode = ClientUtils.extractDialCode(countryCode);
			gwtLog("MMM country code: " + countryCode);
			gwtLog("MMM dial code: " + dialCode);
			// dial code is already has +
			mobilePhone = dialCode + " " + mobilePhone;
			userDTO.setMobile1(mobilePhone);
		}
		gwtLog("MMM mobile: " + userDTO.getMobile1());
		gwtLog("MMM phone: " + userDTO.getPhone());
		return true;
	}
	
	@Override
	public void save()
	{
        Iterator<FieldNumber> enumKeySet = eMap.keySet().iterator();
        
		UserDTO origUserDTO = getOriginalUserDTO();
		UserDTO userDTO = new UserDTO();
		userDTO.setId(origUserDTO.getId());
		boolean changed = false;
		while(enumKeySet.hasNext())
		{
			FieldNumber fieldNumber = enumKeySet.next();
			if (eMap.get(fieldNumber))
			{
				gwtLog("MMM fieldNumber changed: " + fieldNumber);
				gwtLog(" MMM > accept email: " + userDTO.getAcceptEmailNotification());
				gwtLog(" MMM > accept sma: " + userDTO.getAcceptSMSNotification());
				changed = true;
				updateUserDTO(userDTO, fieldNumber);
			}
		}
		Boolean value = getView().getAcceptNotificationSwitch().getValue();
		gwtLog("MMM ae: " + value);
		if (value)
		{
			userDTO.setAcceptEmailNotification();
		}
		else
		{
			userDTO.clearAcceptEmailNotification();
		}
		value = getView().getAcceptSmsSwitch().getValue();
		if (value)
		{
			userDTO.setAcceptSMSNotification();
		}
		else
		{
			userDTO.clearAcceptSMSNotification();
		}
			
		gwtLog("MMM sm: "+ value);

		value = getView().getDeleteProfilePicSwitch().getValue();
		// check if profile pic should be deleted
		gwtLog("Del profile pic switch value: " + value);
		if (value)
		{
			gwtLog(">>>>>>>>>>>>> Set profile pic array to zero length, setting null means don't update");
			userDTO.setProfilePic(new byte[0]);
			changed = true;
		}
		if (!changed)
		{
			showErrorMessage("Nothing changed in the form");
			return;
		}
		if (getView().getAcceptSmsSwitch().getValue())
		{
			String mobilePhone = getView().getMobilePhoneTextBox().getValue();
			if (mobilePhone == null || mobilePhone.length() == 0)
			{
				showErrorMessage(glang.smsPhoneNumberError());
				return;
			}
		}
		gwtLog("Phone: " + "'" + userDTO.getPhone() + "'");

		if ( ! setMobilePhoneToUserDTO(userDTO))
		{
			// something is wrong
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{
			@Override
			public void uponSuccess(Void dto)
			{
				Date d = new Date();
				showMessage("Profile settings saved successfully on " + d.toString());
				// update CurrentUser singleton
				updateCurrentUserWithDTO(userDTO);
				// get new UserDTO
				resetForm();
				fetchAndPopulateForm();
				enableButtons(false);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				getView().getCountryCodeSelect().setValue(null);
				showErrorMessage("Could not save settings: " + caught.getMessage());
			}
		};
		gwtLog("Save Office: " + userDTO.getOffice());
		gwtLog("Hide item delay: " + ClientUtils.fromInteger(userDTO.getHideItemDelay()));
		if (getOriginalUserDTO().getProfilePic() != null)
		{
			gwtLog("Original profile pic is set");
		}
		gwtLog("MMM save email notification: " + userDTO.getAcceptEmailNotification());
		gwtLog("MMM save accept SMS: " + userDTO.getAcceptSMSNotification());
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().modifyUser(authCreds, userDTO, callback);
	}

	@Override
	public void reset()
	{
		showMessage("");
		resetForm();
		fetchAndPopulateForm();
		enableButtons(false);
	}
	
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
	
	private String getTextBoxValue(TextBox textBox)
	{
		String value = textBox.getValue();
		if (value.length() == 0)
		{
			return " ";
		}
		return value;
	}
	
	@Override
	public void showFullnameChange(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getFullnameLabel();
		String origVal = dto.getFullname();
		String formVal = getTextBoxValue(getView().getFullnameTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.fullname);
	}
	
	

	@Override
	public void showAltEmailChagne(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getSecondaryEmailLabel();
		String origVal = dto.getEmail2();
		String formVal = getTextBoxValue(getView().getSecondaryEmailTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.alternateEmail);
	}

	@Override
	public void showPhoneChange(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getPrimaryPhoneLabel();
		String origVal = dto.getPhone();
		String formVal = getTextBoxValue(getView().getPrimaryPhoneTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.phone);
	}

	@Override
	public void showMobilePhoneChange(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getMobilePhoneLabel();
		String origVal = dto.getMobile1();
		String formVal = getTextBoxValue(getView().getMobilePhoneTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.mobilePhone);
	}

	@Override
	public void showOfficeChange(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getOfficeLabel();
		String origVal = dto.getOffice();
		String formVal = getTextBoxValue(getView().getOfficeTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.office);
	}


	@Override
	public void showFacebookChange(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getFacebookLabel();
		String origVal = dto.getFacebook();
		String formVal = getTextBoxValue(getView().getFacebookTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.facebook);
	
	}

	@Override
	public void showTwitterChange(KeyUpEvent e)
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getTwitterLabel();
		String origVal = dto.getTwitter();
		String formVal = getTextBoxValue(getView().getTwitterTextBox());
		stringFormValueChanged(origVal, formVal, label, FieldNumber.twitter);
	}
	
	@Override
	public void showDefaultPageChange()
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		ObidosMessages lang = ObidosMessages.LANG;

		Integer origVal = dto.getDefaultPage();
		if (origVal == null)
		{
			origVal = UserDTO.DEFAULT_PAGE_LIST_CONTAINERS;
		}
		Select select = getView().getSelectPage();
		String selectedPage = select.getValue();
		gwtLog("Selected page: " + selectedPage);
		Integer formVal = defaultPageMap.get(selectedPage);
		if (formVal == null)
		{
			formVal = 0;
		}
		gwtLog("Selected Integer: " + formVal);
		String page = defaultPageMap.getKey(formVal);
		if (formVal < 0)
		{
			String message = page + " is not supported in UserdTO yet";
			ClientUtils.showBootboxDialog(lang.error(), message);
			return;
		}
		gwtLog("Default page in dto: " + defaultPageMap.getKey(origVal));
		gwtLog("Selected page number: " + formVal);
		gwtLog("Selected page: " +page);
		FormLabel label = getView().getDefaltPageLabel();
		integerFormValueChanged(origVal, formVal, label, FieldNumber.defaultPage);
	}

	@Override
	public void showAcceptNotifiationChange()
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getAcceptNotificationLabel();
		Boolean origVal = dto.getAcceptEmailNotification(); 
		if (origVal == null)
		{
			origVal = false;
		}
		Boolean formVal = getView().getAcceptNotificationSwitch().getValue();
		booleanFormValueChanged(origVal, formVal, label, FieldNumber.acceptNotification);
	}

	@Override
	public void showAcceptSmsChange()
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		FormLabel label = getView().getAcceptSmsNotificaitonLabel();
		Boolean origVal = dto.getAcceptSMSNotification();
		if (origVal == null)
		{
			origVal = false;
		}
		Boolean formVal = getView().getAcceptSmsSwitch().getValue();
		booleanFormValueChanged(origVal, formVal, label, FieldNumber.acceptSmsNotification);
	}

	@Override
	public void showDeleteProfilePicChagne()
	{
		FormLabel label = getView().getDeleteProfilePicLabel();
		Boolean origVal = false;
		Boolean formVal = getView().getDeleteProfilePicSwitch().getValue();
		booleanFormValueChanged(origVal, formVal, label, FieldNumber.deleteProfilePic);

	}
	
	@Override
	public void showHideItemSecondsChange()
	{
		UserDTO dto = getOriginalUserDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getHideItemDelay();
		if (origVal == null)
		{
			origVal = 0;
		}
		Integer formVal = getView().getHideItemInSecondsTextBox().getValue();
		if (formVal == null)
		{
			gwtLog("form value is null");
			return;
		}
		FormLabel label = getView().getHideItemLabel();
		integerFormValueChanged(origVal, formVal, label, FieldNumber.hideItemSeconds);
	}

	private void booleanFormValueChanged(Boolean origVal, Boolean formVal, FormLabel label, FieldNumber fieldNumber)
    {
		gwtLog(" orig: " + origVal + " new val: " + formVal);

    	boolean dirty = false;
		if (origVal != formVal)
		{
			dirty = true;
		}
    	
    	gwtLog("dirty: " + dirty);
    		
    	eMap.put(fieldNumber, dirty);
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableSaveButton();
    }

	private void integerFormValueChanged(Integer origVal, Integer formVal, FormLabel label, FieldNumber fieldNumber)
    {
    	boolean dirty = false;
    	if (origVal.intValue() != formVal.intValue())
    	{
    		dirty = true;
    	}
    	eMap.put(fieldNumber, dirty);
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableSaveButton();
    }
	
	private String getFormValue(FieldNumber fieldNumber)
	{
		if (fieldNumber == FieldNumber.alternateEmail)
		{
			return getView().getSecondaryEmailTextBox().getValue();
		}
		return null;
	}

    private void stringFormValueChanged(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
    {
    	gwtLog(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
    	
        boolean dirty = false;
        if (origVal != null && formVal != null && formVal.length() == 0)
        {
            dirty = true;
        }
        if (origVal == null && (formVal != null && formVal.length() > 0))
        {
            dirty = true;
        }

        if (origVal == null)
        {
            gwtLog("origVal is null dirty: " + dirty);
        }

        if (origVal != null && !origVal.equals(formVal))
        {
            gwtLog("Should not be here. origVal: " + origVal + " formVal: " + formVal);
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }
        gwtLog("formval: " + formVal);

        eMap.put(fieldNumber,dirty);
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableSaveButton();
    }
    
    private void enableDisableSaveButton()
    {
        Iterator<FieldNumber> enumKeySet = eMap.keySet().iterator();
        boolean dirty = false;
        while(enumKeySet.hasNext())
        {
            FieldNumber fieldNumber = enumKeySet.next();
            boolean val = eMap.get(fieldNumber);
            if (val)
            {
                dirty = true;
                break;
            }
        }

        if (dirty)
        {
        	enableButtons(true);
        }
        else
        {
        	enableButtons(false);
        }
    }



	// For that piece of crap called IE
	private static native int ieWidth(Element elt) /*-{
		return elt.naturalWidth;
    }-*/;

	private static native int ieHeight(Element elt) /*-{
		return elt.naturalHeight;
  	}-*/;

	@Override
	public void showUploadProfilePicPage()
	{
		Long userDTOId = getOriginalUserDTO().getId();
		if (userDTOId == null)
		{
			ClientUtils.showBootboxDialog("ERROR", "Could not get UserDTO Id");
			return;
		}
		String message = "";
		if (!Canvas.isSupported())
		{
			message = "This browser does not support HTML5 Canvas.";
		}
		if (!FileUtils.supportsFileAPI())
		{
			if (message.length() > 0)
			{
				message = message + "\n" + "Also this browser does not support HTML5 File API";
			}
			else
			{
				message = message + "\n" + "This browser does not support HTML5 File API";
			}
		}
		if (message.length() > 0)
		{
			message = message + "\n" + "<b>HTML5 Canvas and File API are required to upload profile image</b>";
			ClientUtils.showBootboxDialog("ERROR", message);
			return;
		}
       	Map<String,String> with = new HashMap<>();
       	with.put(ObidosConstants.ID, userDTOId.toString());
       	String nameToken = NameTokens.UPLOAD_PROFILE_PIC;
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	/**
	 *  set example number in national format as place holder
	 */
	@Override
	public void countryCodeSelectCallback()
	{
		ClientUtils.countryCodeSelectCallback(getView().getMobilePhoneTextBox(),
				getView().getCountryCodeSelect(),
				countryCodeMap);
	}

	@Override
	public void highContractSwitchCallback()
	{
		ToggleSwitch ts = getView().getHighContrastSwitch();
		gwtLog("MMM Hign Contrast TS v: " + ts.getValue());
		ThemeManager.toggleHighContrast();
	}

	@Override
	public void largetTextSwitchCallback()
	{
		ToggleSwitch ts = getView().getLargetTextSwitch();
		gwtLog("MMM Large TS v: " + ts.getValue());
		ThemeManager.toggleLargeFont();
		
	}
}