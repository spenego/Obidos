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

package com.spenego.Obidos.client.application.systemsettings;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;

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
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.exceptions.ObidosException;

public class SystemSettingsPresenter extends Presenter<SystemSettingsPresenter.MyView, SystemSettingsPresenter.MyProxy>
		implements SystemSettingsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private boolean passwordLengthFatalWarning = false;
	private boolean passphraseLengthFatalWarning = false;
	private boolean passwordEntropyFatalWarning = false;
	private boolean passphraseEntropyFatalWarning = false;
	
	private SystemConfigDTO gSystemConfigDTO = null;
	private HashMap<FieldNumber, Boolean> gFieldValueChangedMap = new HashMap<FieldNumber, Boolean>();
	private HashMap<FieldNumber, Boolean> gFieldValueComplexityChangedMap = new HashMap<FieldNumber, Boolean>();
	private enum FieldNumber
	{
		httpsFqdn,
		httpsPort,
		twoFAIssuer,
		minPasswordLength,
		minPasswordLowercase,
		minPasswordUppercase,
		minPasswordNumeric,
		minPasswordSpecial,
		minPasswordEntropy,
		maxPasswordAge,
		minPassphraseLength,
		minPassphraseLowercase,
		minPassphraseUppercase,
		minPassphraseNumeric,
		minPassphraseSpecial,
		minPassphraseEntropy,
		dateFormat,
		documentStorePath,
	};
    private static EnumMap<FieldNumber, Boolean> gEMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);
    private void resetFieldsMaps()
    {
    	for (FieldNumber fn : FieldNumber.values())
    	{
    		gEMap.put(fn, false);
    		gFieldValueChangedMap.put(fn, false);
    		gFieldValueComplexityChangedMap.put(fn, false);
    	}
    }
	
	interface MyView extends View, HasUiHandlers<SystemSettingsUiHandlers>
	{
		public ObidosMessageRow getMessageRow();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public BlockQuote getHelpBlockQuote();
		public Button getSaveButton();
		public TextBox getTwoFAIssuerTextBox();
		public FormLabel getTwoFAIssuerLabel();
		public Button getResetButton();
		public TextBox getHttpFqdnTextBox();
		public ObidosReadonlyTextBox getHttpSchemeTextBox();
		public ObidosIntegerTextBox getHttpsPortBox();
		public FormLabel getFqdnLabel();
		public FormLabel getPortLabel();
		public FormLabel getMaxPasswordAgetLabel();
		public ObidosIntegerTextBox getMaxPasswordAgeTextBox();
		public ObidosIntegerTextBox getPwMinLengthTextBox();
		public ObidosIntegerTextBox getMinimumLowercaseTextBox();
		public ObidosIntegerTextBox getMinimumUppercaseTextBox();
		public ObidosIntegerTextBox getMinimumNumericTextBox();
		public ObidosIntegerTextBox getMinimumSpecialTextBox();
		public ObidosIntegerTextBox getMinPasswordEntropyTextBox();
		public FormLabel getMinPasswordEntropyLabel();
		public FormLabel getMinPasswordLengthLabel();
		public FormLabel getMinPasswordLowercaseLabel();
		public FormLabel getMinPasswordUppercaseLabel();
		public FormLabel getMinPasswordNumericCharLabel();
		public FormLabel getMinPasswordSpecialCharLabel();
		public FormLabel getPasswordLengthWarningLabel();
		public FormLabel getPasswordEntropyLengthWarningLabel();

		public FormLabel getMinLengthPPLabel();
		public FormLabel getMinLowerPPLabel();
		public FormLabel getMinUpperPPLabel();
		public FormLabel getMinNumberPPLabel();
		public FormLabel getMinSpecialPPLabel();
		public FormLabel getMinPPEntropyLabel();
		public ObidosIntegerTextBox getMininumPPLengthTextBox();
		public FormLabel getPassphraseLengthWarningLabel();
		public ObidosIntegerTextBox getMinimumPPLowercaseTextBox();
		public ObidosIntegerTextBox getMinimumPPUppercaseTextBox();
		public ObidosIntegerTextBox getMinimumPPNumericTextBox();
		public ObidosIntegerTextBox getMinimumPPSpecialTextBox();
		public ObidosIntegerTextBox getMinPassphraseEntropyTextBox();
		public FormLabel getPassphraseEntropyLengthWarningLabel();
		public Select getDateFormatSelect();
		public FormLabel getDateFormatLabel();
		public ListBox getPasswordStrengthList();
		public ListBox getPassphraseStrengthList();
		public FormLabel getDocumentDirLabel();
		public ObidosTextBox getDocumentDirTextBox();
	};


	@NameToken(NameTokens.SYSTEM_SETTINGS)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInAdminLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<SystemSettingsPresenter>
	{
	}

	@Inject
	SystemSettingsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

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
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage(null);
		resetFieldsMaps();
		getView().getButtonToolBarBottom().adjustButtonsWidth();
		resetFormLabelColors();
		fetchAndPopulateForm(null);
	}
	
	private void updatePasswordPassphraseStrengthList()
	{
		ListBox passwordlb = getView().getPasswordStrengthList();
		Integer entropy = getView().getMinPasswordEntropyTextBox().getValue();
		gwtLog("Password Entropy: " + entropy);
		if (entropy < ObidosConstants.PASSWORD_ENTROPY_STRONG)
		{
			gwtLog("Weak");
			passwordlb.setSelectedIndex(0); // Weak
		}
		else if (entropy == ObidosConstants.PASSWORD_ENTROPY_STRONG)
		{
			gwtLog("Strong");
			passwordlb.setSelectedIndex(1); // Strong
		}
		else
		{
			passwordlb.setSelectedIndex(2); // Very strong
			gwtLog("Very trong");
		}
		
		ListBox passphraselb = getView().getPassphraseStrengthList();
		entropy = getView().getMinPassphraseEntropyTextBox().getValue();
		if (entropy < ObidosConstants.PASSPHRASE_ENTROPY_STRONG)
		{
			gwtLog("Weak");
			passphraselb.setSelectedIndex(0); // Weak
		}
		else if (entropy == ObidosConstants.PASSPHRASE_ENTROPY_STRONG)
		{
			gwtLog("Strong");
			passphraselb.setSelectedIndex(1); // Strong
		}
		else
		{
			passphraselb.setSelectedIndex(2); // Very strong
			gwtLog("Very trong");
		}
	}

	private void resetFormLabelColors()
    {
		ClientUtils.setFormLabelsColorOriginal(getView().getFqdnLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getPortLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getTwoFAIssuerLabel());
		ClientUtils.setFormLabelsColorOriginal(getView(). getMinPasswordLengthLabel());
		ClientUtils.setFormLabelsColorOriginal(getView(). getMinPasswordLowercaseLabel());
		ClientUtils.setFormLabelsColorOriginal(getView(). getMinPasswordUppercaseLabel());
		ClientUtils.setFormLabelsColorOriginal(getView(). getMinPasswordNumericCharLabel());
		ClientUtils.setFormLabelsColorOriginal(getView(). getMinPasswordSpecialCharLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMaxPasswordAgetLabel());
		ClientUtils.setFormLabelsColorOriginal(getView(). getMinPasswordEntropyLabel());
		
		ClientUtils.setFormLabelsColorOriginal(getView().getMinLengthPPLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMinLowerPPLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMinUpperPPLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMinNumberPPLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMinSpecialPPLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getMinPPEntropyLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getDateFormatLabel());
		ClientUtils.setFormLabelsColorOriginal(getView().getDocumentDirLabel());
    }
	
	private void saveDTO(SystemConfigDTO dto)
	{
		gSystemConfigDTO = dto;
	}
	
	private void reset()
	{
		resetFormLabelColors();
		enableSaveButton(false);
		showMessage(null);
		
	}
	
	private void resetNotAcceptableWarnings()
	{
		passwordEntropyFatalWarning = false;
		passwordLengthFatalWarning = false;
		passphraseLengthFatalWarning = false;
		passphraseEntropyFatalWarning = false;
	}
	
	private void showPasswordEntropyLengthWarning()
	{
		FormLabel label = getView().getPasswordEntropyLengthWarningLabel();
		Integer v = getView().getMinPasswordEntropyTextBox().getValue();
		label.setVisible(false);
		int n = ObidosConstants.PASSWORD_ENTROPY_STRONG;
		if (v == null || v < n)
		{
			label.setText(glang.passwordEntropyLengthWarning(n));
			label.setVisible(true);
			passwordEntropyFatalWarning = true;
			getView().getPasswordStrengthList().setSelectedIndex(0); // weak
		}
		else
		{
			if (v >= ObidosConstants.PASSWORD_ENTROPY_STRONG && v < ObidosConstants.PASSWORD_ENTROPY_VERY_STRONG)
			{
				getView().getPasswordStrengthList().setSelectedIndex(1); // Strong
			}
			else
			{
				getView().getPasswordStrengthList().setSelectedIndex(2); // Very Strong
			}
			passwordEntropyFatalWarning = false;
		}
	}
	private void showPassphraseEntropyLengthWarning()
	{
		FormLabel label = getView().getPassphraseEntropyLengthWarningLabel();
		Integer v = getView().getMinPassphraseEntropyTextBox().getValue();
		label.setVisible(false);
		int n = ObidosConstants.PASSPHRASE_ENTROPY_STRONG;
		if (v == null || v < n)
		{
			label.setText(glang.passwordEntropyLengthWarning(n));
			label.setVisible(true);
			passphraseEntropyFatalWarning = true;
			getView().getPassphraseStrengthList().setSelectedIndex(0); // weak
		}
		else
		{
			if (v >= ObidosConstants.PASSPHRASE_ENTROPY_STRONG && v < ObidosConstants.PASSPHRASE_ENTROPY_VERY_STRONG)
			{
				getView().getPassphraseStrengthList().setSelectedIndex(1); // Strong
			}
			else
			{
				getView().getPassphraseStrengthList().setSelectedIndex(2); // Very Strong
			}

			passphraseEntropyFatalWarning = false;
		}
	}

	private void showPasswordLengthWarning()
	{
		FormLabel label = getView().getPasswordLengthWarningLabel();
		Integer passwordLength = getView().getPwMinLengthTextBox().getValue();
		label.setVisible(false);
		if (passwordLength == null || passwordLength < ObidosConstants.MIN_PASSWORD_LENGTH)
		{
			label.setText(glang.passwordLengthWarning(ObidosConstants.MIN_PASSWORD_LENGTH));
			label.setVisible(true);
		}

		if (passwordLength == null || passwordLength < ObidosConstants.ALLOWABLE_MIN_PASSWORD_LENGTH)
		{
			label.setText(glang.acceptablePassLengthWarning(ObidosConstants.ALLOWABLE_MIN_PASSWORD_LENGTH));
			label.setVisible(true);
			passwordLengthFatalWarning = true;
		}
		else
		{
			passwordLengthFatalWarning = false;
		}
	}

	private void showPassphraseLengthWarning()
	{
		FormLabel label = getView().getPassphraseLengthWarningLabel();
		Integer length = getView().getMininumPPLengthTextBox().getValue();
		label.setVisible(false);
		if (length == null || length < ObidosConstants.MIN_PASSPHRASE_LENGTH)
		{
			label.setText(glang.passwordLengthWarning(ObidosConstants.MIN_PASSPHRASE_LENGTH));
			label.setVisible(true);
		}
		if (length == null || length < ObidosConstants.ALLOWABLE_MIN_PASSPHRASE_LENGTH)
		{
			label.setText(glang.acceptablePassLengthWarning(ObidosConstants.ALLOWABLE_MIN_PASSPHRASE_LENGTH));
			label.setVisible(true);
			passphraseLengthFatalWarning = true;
		}
		else
		{
			passphraseLengthFatalWarning = false;
		}
	}
	
	private void populatePassComplexity(SystemConfigDTO configDTO)
	{
		gwtLog("Pass complexity changed? " + hasPassComplexityChanged());
		
		PassComplexityDTO dto = configDTO.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}

		final ComplexityRequirementsDTO passwordComplexity = dto.getPasswordComplexityRequirements();
		getView().getPwMinLengthTextBox().setValue(passwordComplexity.getMinimumLength());
		showPasswordLengthWarning();
		getView().getMinimumLowercaseTextBox().setValue(passwordComplexity.getMinimumLowercase());
		getView().getMinimumUppercaseTextBox().setValue(passwordComplexity.getMinimumUppercase());
		getView().getMinimumNumericTextBox().setValue(passwordComplexity.getMinimumNumbers());
		getView().getMinimumSpecialTextBox().setValue(passwordComplexity.getMinimumSpecial());
		getView().getMinPasswordEntropyTextBox().setValue(passwordComplexity.getMinimumEntropy());
		showPasswordEntropyLengthWarning();
		getView().getMaxPasswordAgeTextBox().setValue(passwordComplexity.getMaxAgeInDays());
		
		// passphrase
		final ComplexityRequirementsDTO passphraseComplexity = dto.getPassphraseComplexityRequirements();
		getView().getMininumPPLengthTextBox().setValue(passphraseComplexity.getMinimumLength());
		getView().getMinimumPPLowercaseTextBox().setValue(passphraseComplexity.getMinimumLowercase());
		getView().getMinimumPPUppercaseTextBox().setValue(passphraseComplexity.getMinimumUppercase());
		getView().getMinimumPPNumericTextBox().setValue(passphraseComplexity.getMinimumNumbers());
		getView().getMinimumPPSpecialTextBox().setValue(passphraseComplexity.getMinimumSpecial());
		getView().getMinPassphraseEntropyTextBox().setValue(passphraseComplexity.getMinimumEntropy());
		getView().getDocumentDirTextBox().setValue(configDTO.getDocumentStorageDirectory());
	}
	
	private void populateForm(SystemConfigDTO dto)
	{
		reset();
		if (dto == null)
		{
			getView().getTwoFAIssuerTextBox().clear();
		}
		else
		{
			if (dto.getScheme() == null)
			{
				getView().getHttpSchemeTextBox().setValue(glang.https());
			}
			String fqdn = dto.getFqdn();
			if (fqdn != null && fqdn.length() > 0)
			{
				getView().getHttpFqdnTextBox().setValue(fqdn);
			}
			
			Integer port = dto.getServerPort();
			if (port != null)
			{
				getView().getHttpsPortBox().setValue(port);
			}
			
			getView().getTwoFAIssuerTextBox().setValue(dto.getTwoFactorAuthIssuer());

			populatePassComplexity(dto);

			if ((dto.getDateFormat() == null || dto.getDateFormat().length() == 0))
			{
				gwtLog("Set data format to default: " + ObidosConstants.DEFAULT_DATEPICKER_DATE_FORMAT);
				dto.setDateFormat(ObidosConstants.DEFAULT_DATEPICKER_DATE_FORMAT);
			}
			if (dto.getDateFormat() != null)
			{
				gwtLog("Set date format: " + dto.getDateFormat());
				getView().getDateFormatSelect().setValue(dto.getDateFormat());
				getView().getDateFormatSelect().refresh();
			}
			
			PassComplexityDTO pcDTO = dto.getPassComplexityDTO();
			if (pcDTO != null)
			{
				gwtLog("min password length: " + pcDTO.getPasswordComplexityRequirements().getMinimumLength());
				gwtLog("max password get days: " + pcDTO.getPasswordComplexityRequirements().getMaxAgeInDays());
			}
		}

		showPasswordLengthWarning();
		showPasswordEntropyLengthWarning();
		showPassphraseEntropyLengthWarning();
		showPassphraseLengthWarning();
	}

	private void fetchAndPopulateForm(String message)
	{
		enableSaveButton(false);
		String maxPasswordDays = Integer.toString(ObidosConstants.MAX_PASSWORD_AGE_IN_DAYS);
		getView().getMaxPasswordAgeTextBox().setPlaceholder(maxPasswordDays);

		GwtAsyncWrapper<SystemConfigDTO> callback = new GwtAsyncWrapper<SystemConfigDTO>(this)
		{

			@Override
			public void uponSuccess(SystemConfigDTO dto)
			{
				gwtLog("> Date format: " + dto.getDateFormat());
				saveDTO(dto);
				populateForm(dto);
				updatePasswordPassphraseStrengthList();
				if (message != null)
				{
					showMessage(message);
				}
				getView().getResetButton().setEnabled(false);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				reset();
				showErrorMessage(glang.couldNotRetrieveFormValues(caught.getMessage()));
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().get(authCreds, callback);
	}

	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}
	
	
	// null will be returned if field value has not changed
	private Integer getComplexityValue(final FieldNumber fn)
	{
		switch (fn)
		{
			case minPasswordLength:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getPwMinLengthTextBox().getValue();
				}
				break;
			}
			case minPasswordEntropy:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinPasswordEntropyTextBox().getValue();
				}
				break;
			}
			
			case minPasswordLowercase:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumLowercaseTextBox().getValue();
				}
				break;
			}
			
			case minPasswordUppercase:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumUppercaseTextBox().getValue();
				}
				break;
			}
			
			case minPasswordNumeric:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumNumericTextBox().getValue();
				}

				break;
			}
			
			case minPasswordSpecial:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumSpecialTextBox().getValue();
				}
				break;
			}
			
			case maxPasswordAge:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMaxPasswordAgeTextBox().getValue();
				}
				break;
			}
			
			// passphrase
			case minPassphraseLength:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMininumPPLengthTextBox().getValue();
				}
				break;
			}
			
			case minPassphraseEntropy:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinPassphraseEntropyTextBox().getValue();
				}
				break;
			}

			case minPassphraseLowercase:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumPPLowercaseTextBox().getValue();
				}
				break;
			}
			
			case minPassphraseUppercase:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumPPUppercaseTextBox().getValue();
				}
				break;
			}
			
			case minPassphraseNumeric:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumPPNumericTextBox().getValue();
				}
				break;
			}
			
			case minPassphraseSpecial:
			{
				if (hasFieldChanged(fn))
				{
					return getView().getMinimumPPSpecialTextBox().getValue();
				}
				break;
			}

		default:
			break;
		}
		gwtLog("Returning null for FieldNumber." + fn);
		return null;
	}
	
	private PassComplexityDTO makePassComplexDTOFromForm()
	{
		final ComplexityRequirementsDTO wordCr = new ComplexityRequirementsDTO();
		// Password
		wordCr.setMinimumLength(getComplexityValue(FieldNumber.minPasswordLength));
		wordCr.setMinimumEntropy(getComplexityValue(FieldNumber.minPasswordEntropy));
		wordCr.setMinimumLowercase(getComplexityValue(FieldNumber.minPasswordLowercase));
		wordCr.setMinimumUppercase(getComplexityValue(FieldNumber.minPasswordUppercase));
		wordCr.setMinimumNumbers(getComplexityValue(FieldNumber.minPasswordNumeric));
		wordCr.setMinimumSpecial(getComplexityValue(FieldNumber.minPasswordSpecial));
		wordCr.setMaxAgeInDays(getComplexityValue(FieldNumber.maxPasswordAge));

		// Passphrase
		final ComplexityRequirementsDTO phraseCr = new ComplexityRequirementsDTO();
		phraseCr.setMinimumLength(getComplexityValue(FieldNumber.minPassphraseLength));
		phraseCr.setMinimumEntropy(getComplexityValue(FieldNumber.minPassphraseEntropy));
		phraseCr.setMinimumLowercase(getComplexityValue(FieldNumber.minPassphraseLowercase));
		phraseCr.setMinimumUppercase(getComplexityValue(FieldNumber.minPassphraseUppercase));
		phraseCr.setMinimumNumbers(getComplexityValue(FieldNumber.minPassphraseNumeric));
		phraseCr.setMinimumSpecial(getComplexityValue(FieldNumber.minPassphraseSpecial));
		
		return new PassComplexityDTO(wordCr, phraseCr);
	}
	
	private SystemConfigDTO makeDTOFromForm()
	{
		// first make sure required fields are specified
		String fqdn = getView().getHttpFqdnTextBox().getValue();
		if (fqdn == null || fqdn.length() == 0)
		{
			showErrorMessage(glang.specifyHttpsFqdn());
			return null;
		}
		
		Integer port = getView().getHttpsPortBox().getValue();
		if (port == null)
		{
			showErrorMessage(glang.specifyHttpsPort());
			return null;
		}
		
		
		String issuer = getView().getTwoFAIssuerTextBox().getValue();
		if (issuer == null || issuer.length() == 0)
		{
			showErrorMessage(glang.specify2FAIssuer());
			return null;
		}
		
		
		// only set changed fields

		
		SystemConfigDTO dto = new SystemConfigDTO();
		// https never changes.
		dto.setScheme(null);

		// FQDN
		FieldNumber fn = FieldNumber.httpsFqdn;
		if (!hasFieldChanged(fn))
		{
			gwtLog(" FQDN did not change");
			fqdn = null;
		}
		dto.setFqdn(fqdn);

		// HTTPS Port
		fn = FieldNumber.httpsPort;
		if (!hasFieldChanged(fn))
		{
			gwtLog(" Port did not chagne");
			port = null;
		}
		dto.setServerPort(port);
		
		// 2FA issuer
		fn = FieldNumber.twoFAIssuer;
		if (!hasFieldChanged(fn))
		{
			gwtLog(" 2FA issuer did not chagne");
			issuer = null;
		}
		dto.setTwoFactorAuthIssuer(issuer);
		
		// Date format
		String dateFormat = getView().getDateFormatSelect().getValue();
		fn = FieldNumber.dateFormat;
		if (!hasFieldChanged(fn))
		{
			gwtLog("Dateformat has not changed");
			dateFormat = null;
		}
		dto.setDateFormat(dateFormat);
		
		// Document directory
		String documentDirectory = getView().getDocumentDirTextBox().getValue();
		fn = FieldNumber.documentStorePath;
		if (!hasFieldChanged(fn))
		{
			gwtLog("Document directory has not changed");
			documentDirectory = null;
		}
		dto.setDocumentStorageDirectory(documentDirectory);
		
		// populate PassComplexity DTO
		if (hasPassComplexityChanged())
		{
			gwtLog("Complexity has changed!!");
			PassComplexityDTO passComplexityDTO = makePassComplexDTOFromForm();
			dto.setPassComplexityDTO(passComplexityDTO);
		}
		else
		{
			gwtLog(">> Complexity did not change");
			dto.setPassComplexityDTO(null);
		}
		
		dto.setDocumentStorageDirectory(getView().getDocumentDirTextBox().getValue());
		
		
		return dto;
	}

	@Override
	public void save()
	{
		SystemConfigDTO dto = makeDTOFromForm();
		if (dto == null)
		{
			gwtLog("SystemConfig dto is null");
			return;
		}
		
		try
		{
			PassComplexityDTO cdto = dto.getPassComplexityDTO(); 
			if (cdto != null)
			{
				cdto.validate();
			}
			
		}catch(final ObidosException e)
		{
			showErrorMessage(e.getMessage());
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				String message = glang.updatedSuccessfully(ClientUtils.formattedDate(new Date()));
				resetForm(message);
				getView().getResetButton().setEnabled(false);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				String errorMessage = glang.couldNotUpdateSystemSettings(caught.getMessage());
				showErrorMessage(errorMessage);
			}
		};

		gwtLog("Date format: " + dto.getDateFormat());
		
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().update(authCreds, dto, callback);
	}
	
	private boolean hasPassComplexityChanged()
	{
    	for (FieldNumber fn : FieldNumber.values())
    	{
    		if (gFieldValueComplexityChangedMap.get(fn))
    		{
    			gwtLog("Field number: " + fn + " changed");
    			return true;
    		}
    	}
    	return false;
	}
	
	private void setFieldValueChagned(FieldNumber fn, boolean dirty)
	{
		gFieldValueChangedMap.put(fn, dirty);
	}
	
	private void setPassComplexityChanged(FieldNumber fn, boolean dirty)
	{
		gFieldValueComplexityChangedMap.put(fn, dirty);
	}
	
	private boolean hasFieldChanged(final FieldNumber fn)
	{
		return gFieldValueChangedMap.get(fn);
	}

	@Override
	public void help()
	{
		boolean visible = getView().getHelpBlockQuote().isVisible();
		getView().getHelpBlockQuote().setVisible(!visible);
	}
	
	private SystemConfigDTO getOriginalDTO()
	{
		return gSystemConfigDTO;
	}

	@Override
	public void show2FAUssuerChange()
	{
		SystemConfigDTO dto = getOriginalDTO();
		if (dto == null)
		{
			return;
		}
		String origVal = dto.getTwoFactorAuthIssuer();
		
		String formVal = getView().getTwoFAIssuerTextBox().getValue();
		if (formVal == null || formVal.length() == 0)
		{
			if (origVal != null)
			{
				formVal = origVal;
			}
		}
		
		stringFormValueChanged(dto.getTwoFactorAuthIssuer(), 
				getView().getTwoFAIssuerTextBox().getValue(), 
				getView().getTwoFAIssuerLabel(), 
				FieldNumber.twoFAIssuer);
	}

	@Override
	public void showDataStoreChange()
	{
		SystemConfigDTO dto = getOriginalDTO();
		if (dto == null)
		{
			return;
		}
		String origVal = dto.getFqdn();
		String formValue = getView().getHttpFqdnTextBox().getValue();
		if (formValue == null || formValue.length() == 0)

		{
			if (origVal != null)
			{
				formValue = origVal;
			}	
		}
		stringFormValueChanged(dto.getDocumentStorageDirectory(),
				getView().getDocumentDirTextBox().getValue(),
				getView().getDocumentDirLabel(),
				FieldNumber.documentStorePath);
	}

	@Override
	public void showHttpsFqdnChange()
	{
		SystemConfigDTO dto = getOriginalDTO();
		if (dto == null)
		{
			return;
		}
		String origVal = dto.getFqdn();
		if (origVal == null)
		{
			origVal = "127.0.0.1";
		}
		String formValue = getView().getHttpFqdnTextBox().getValue();
		if (formValue == null || formValue.length() == 0)
		{
			formValue = origVal;
		}
		stringFormValueChanged(dto.getFqdn(),
				getView().getHttpFqdnTextBox().getValue(),
				getView().getFqdnLabel(),
				FieldNumber.httpsFqdn);
	}


	@Override
	public void showHttpsPortChange()
	{
		SystemConfigDTO dto = getOriginalDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getServerPort();
		if (origVal == null)
		{
			origVal = 443;
		}
		Integer formVal = getView().getHttpsPortBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getPortLabel();
		gwtLog("orig port: " + origVal);
		gwtLog("form port: " + formVal);
        integerFormValueChanged(origVal, formVal, label, FieldNumber.httpsPort, false);
	}

	@Override
	public void showPasswordMinLengthChagne()
	{
		gwtLog("min password length");
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}

		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}

		Integer origVal = dto.getPasswordComplexityRequirements().getMinimumLength();
		Integer formVal = getView().getPwMinLengthTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		showPasswordLengthWarning();
		gwtLog("Password min length: " + formVal);
		FormLabel label = getView().getMinPasswordLengthLabel();
		FieldNumber fn = FieldNumber.minPasswordLength;
        integerFormValueChanged(origVal, formVal, label, fn, true);
	}
	
	private boolean isFatalWarningOn()
	{
		/*
		if (passwordEntropyFatalWarning  || 
			passwordLengthFatalWarning   ||
			passphraseLengthFatalWarning ||
			passphraseEntropyFatalWarning)
		{
			return true;
		}
		*/
		return false;
	}



	private void enableSaveButton(boolean enabled)
	{
		gwtLog("passwordEntropyFatalWarning: "  + passwordEntropyFatalWarning);
		
		getView().getResetButton().setEnabled(true); // always enabled
		if (isFatalWarningOn())
		{
			gwtLog("MMM fatal warning is on");
			getView().getSaveButton().setEnabled(false);
			return;
		}
		gwtLog("MMM enable save button: " + enabled);
		getView().getSaveButton().setEnabled(enabled);
		getView().getResetButton().setEnabled(enabled);
	}
    private void enableDisableUpdateButton()
    {
        Iterator<FieldNumber> enumKeySet = gEMap.keySet().iterator();
        boolean dirty = false;
        while(enumKeySet.hasNext())
        {
            FieldNumber fieldNumber = enumKeySet.next();
            boolean val = gEMap.get(fieldNumber);
            if (val)
            {
            	gwtLog("MMM val: " + fieldNumber);
                dirty = true;
                break;
            }
        }
        gwtLog("MMM dirty: " + dirty);
       	enableSaveButton(dirty);
    }
    private void stringFormValueChanged(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
    {
		showMessage(null);
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
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }

        gEMap.put(fieldNumber,dirty);
    
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        setFieldValueChagned(fieldNumber, dirty);
        enableDisableUpdateButton();
    }
    
	private boolean integerFormValueChanged(Integer origVal, Integer formVal, FormLabel label, FieldNumber fieldNumber, boolean complexity)
	{
		showMessage(null);
		boolean dirty = false;
		gwtLog("Orig: "+ origVal + " form: " + formVal);
		if (origVal.intValue() != formVal.intValue())
		{
			gwtLog("? " + origVal + " != " + formVal);
			dirty = true;
		}

		gwtLog("dirty: " + dirty);
		if (dirty && fieldNumber == FieldNumber.minPasswordEntropy)
		{
			gwtLog("pass entropy");
			if (formVal < ObidosConstants.PASSWORD_ENTROPY_STRONG)
			{
				enableDisableUpdateButton();
				return false;
			}
		}
		
		if (dirty && fieldNumber == FieldNumber.minPassphraseEntropy)
		{
			if (formVal < ObidosConstants.PASSPHRASE_ENTROPY_STRONG)
			{
				enableDisableUpdateButton();
				return false;
			}
		}

		gEMap.put(fieldNumber, dirty);
		
		if (dirty)
		{
			ClientUtils.setFormLabelsColorChanged(label);
		}
		else
		{
			ClientUtils.setFormLabelsColorOriginal(label);
		}
		enableDisableUpdateButton();
        setFieldValueChagned(fieldNumber, dirty);
		if (complexity)
		{
			setPassComplexityChanged(fieldNumber, dirty);
		}
		return dirty;
	}

   
    private void gwtLog(String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

	@Override
	public void resetForm(final String message)
	{
 		resetNotAcceptableWarnings();
 		fetchAndPopulateForm(message);
 		getView().getResetButton().setEnabled(false);
	}


	@Override
	public void showPasswordMinLowercaseChange()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPasswordComplexityRequirements().getMinimumLowercase();
		Integer formVal = getView().getMinimumLowercaseTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		gwtLog("min password lc: " + formVal);
		FormLabel label = getView().getMinPasswordLowercaseLabel();
		FieldNumber fn = FieldNumber.minPasswordLowercase;
        integerFormValueChanged(origVal, formVal, label, fn, true);
	}

	@Override
	public void showPasswordUppercaseChange()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPasswordComplexityRequirements().getMinimumUppercase();
		Integer formVal = getView().getMinimumUppercaseTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		gwtLog("min password uc: " + formVal);
		FormLabel label = getView().getMinPasswordUppercaseLabel();
		FieldNumber fn = FieldNumber.minPasswordUppercase;
        integerFormValueChanged(origVal, formVal, label, fn, true);
	}

	@Override
	public void showPasswordNumericChange()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPasswordComplexityRequirements().getMinimumNumbers();
		Integer formVal = getView().getMinimumNumericTextBox().getValue();
		if (formVal == null)
		{
			gwtLog("formval is null");
			formVal = origVal;
		}
		FormLabel label = getView().getMinPasswordNumericCharLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPasswordNumeric, true);
	}

	@Override
	public void showPasswordSpecialChange()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPasswordComplexityRequirements().getMinimumSpecial();
		Integer formVal = getView().getMinimumSpecialTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getMinPasswordSpecialCharLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPasswordSpecial, true);
	
	}
	@Override
	public void showPasswordEntropyChange()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			gwtLog("pdto is null");
			return;
		}
		int origVal = dto.getPasswordComplexityRequirements().getMinimumEntropy();
		Integer formVal = getView().getMinPasswordEntropyTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		gwtLog("orig pw entropy: " + origVal);
		gwtLog("form pw val: " + formVal);
		showPasswordEntropyLengthWarning();
		FormLabel label = getView().getMinPasswordEntropyLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPasswordEntropy, true);
	}

 	@Override
	public void showMaxPasswordInDaysChange()
	{
		SystemConfigDTO dto = getOriginalDTO();
		if (dto == null)
		{
			return;
		}
		PassComplexityDTO pcdto = dto.getPassComplexityDTO();
//		Integer origVal = dto.getMaxPasswordAgeInDays();
		int origVal = pcdto.getPasswordComplexityRequirements().getMaxAgeInDays();
		Integer formVal = getView().getMaxPasswordAgeTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getMaxPasswordAgetLabel();
		gwtLog("orig password exp days: " + origVal);
		gwtLog("form passwod exp days: " + formVal);
        integerFormValueChanged(origVal, formVal, label, FieldNumber.maxPasswordAge, true);

	}
	@Override
	public void showPassphraseMinLengthChagne()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		PassComplexityDTO dto = sdto.getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPassphraseComplexityRequirements().getMinimumLength();
		Integer formVal = getView().getMininumPPLengthTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		showPassphraseLengthWarning();
		FormLabel label = getView().getMinLengthPPLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPassphraseLength, true);
	}

	@Override
	public void showDateFormatSelectionChange()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return;
		}
		String origVal = sdto.getDateFormat();
		String formVal = getView().getDateFormatSelect().getValue();
		FormLabel label = getView().getDateFormatLabel();
		stringFormValueChanged(origVal, formVal, label, FieldNumber.dateFormat);
	}

	private PassComplexityDTO getPassComplexityDTO()
	{
		SystemConfigDTO sdto = getOriginalDTO();
		if (sdto == null)
		{
			return null;
		}
		return sdto.getPassComplexityDTO();
	}

	@Override
	public void showPassphraseMinLowercaseChange()
	{
		PassComplexityDTO dto = getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPassphraseComplexityRequirements().getMinimumLowercase();
		Integer formVal = getView().getMinimumPPLowercaseTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getMinLowerPPLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPassphraseLowercase, true);
	}

	@Override
	public void showPassphraseUppercaseChange()
	{
		PassComplexityDTO dto = getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPassphraseComplexityRequirements().getMinimumUppercase();
		Integer formVal = getView().getMinimumPPUppercaseTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getMinUpperPPLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPassphraseUppercase, true);
	}

	@Override
	public void showPassphraseNumericChange()
	{
		PassComplexityDTO dto = getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPassphraseComplexityRequirements().getMinimumNumbers();
		Integer formVal = getView().getMinimumPPNumericTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getMinNumberPPLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPassphraseNumeric, true);

		
	}

	@Override
	public void showPassphraseSpecialChange()
	{
		PassComplexityDTO dto = getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPassphraseComplexityRequirements().getMinimumSpecial();
		Integer formVal = getView().getMinimumPPSpecialTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		FormLabel label = getView().getMinSpecialPPLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPassphraseSpecial, true);
	}

	@Override
	public void showPassphraseEntropyChange()
	{
		PassComplexityDTO dto = getPassComplexityDTO();
		if (dto == null)
		{
			return;
		}
		Integer origVal = dto.getPassphraseComplexityRequirements().getMinimumEntropy();
		Integer formVal = getView().getMinPassphraseEntropyTextBox().getValue();
		if (formVal == null)
		{
			formVal = origVal;
		}
		showPassphraseEntropyLengthWarning();
		FormLabel label = getView().getMinPPEntropyLabel();
        integerFormValueChanged(origVal, formVal, label, FieldNumber.minPassphraseEntropy, true);
	}

	@Override
	public void passwordStrengthListBoxHandler()
	{
		int i = getView().getPasswordStrengthList().getSelectedIndex();
		gwtLog("Index: " + i);
		ObidosIntegerTextBox ebox = getView().getMinPasswordEntropyTextBox();
		if (i == 0) // Weak
		{
			ebox.setValue(ObidosConstants.PASSWORD_ENTROPY_STRONG - 1);
		}
		else if ( i == 1) // Strong
		{
			ebox.setValue(ObidosConstants.PASSWORD_ENTROPY_STRONG);
		}
		else
		{
			ebox.setValue(ObidosConstants.PASSWORD_ENTROPY_VERY_STRONG);
		}
		showPasswordEntropyChange();;
	}

	@Override
	public void passphraseStrengthListBoxHandler()
	{
		int i = getView().getPassphraseStrengthList().getSelectedIndex();
		gwtLog("Index: " + i);
		ObidosIntegerTextBox ebox = getView().getMinPassphraseEntropyTextBox();
		if (i == 0) // Weak
		{
			ebox.setValue(ObidosConstants.PASSPHRASE_ENTROPY_STRONG - 1);
		}
		else if ( i == 1) // Strong
		{
			ebox.setValue(ObidosConstants.PASSPHRASE_ENTROPY_STRONG);
		}
		else
		{
			ebox.setValue(ObidosConstants.PASSPHRASE_ENTROPY_VERY_STRONG);
		}
		showPassphraseEntropyChange();;

		
	}


}
