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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;

import com.spenego.Obidos.shared.ObidosConstants;

public class SmsKeyDTO extends BaseDTO implements HasId, Clearable, Serializable
{
	/**
	 * @author spgdev@spenego.com - Sep 28, 2023
	 */
	private static final long serialVersionUID = 1L;
	
	private int smsProvider; // SMS_PROVIDER_TWILIO|SMS_PROVIDER_VONAGE
	private String smsProviderSpecifiedPhoneNumber; // from phone number
	
	private String apiSecret; // Twilio calls it accountSid
	private String apiKey;    // Twilio calls it authToken
	
	public SmsKeyDTO() {}
	
	public SmsKeyDTO(final int smsProvider, 
			final String fromPhoneNumer,
			final String apiSecret, final String apiKey)
	{
		this.smsProvider = smsProvider;
		this.smsProviderSpecifiedPhoneNumber = fromPhoneNumer;
		this.apiSecret = apiSecret;
		this.apiKey = apiKey;
	}
	
	public String getTwilioAccountSid()
	{
		return this.apiSecret;
	}

	public String getTwilioAuthToken()
	{
		return this.apiKey;
	}
	
	public int getSmsProvider()
	{
		return smsProvider;
	}

	public void setSmsProvider(int smsProvider)
	{
		this.smsProvider = smsProvider;
	}

	public String getSmsProviderSpecifiedPhoneNumber()
	{
		return smsProviderSpecifiedPhoneNumber;
	}

	public String getApiSecret()
	{
		return apiSecret;
	}

	public void setApiSecret(String apiSecret)
	{
		this.apiSecret = apiSecret;
	}

	public String getApiKey()
	{
		return apiKey;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public void setSmsProviderSpecifiedPhoneNumber(String smsProviderSpecifiedPhoneNumber)
	{
		this.smsProviderSpecifiedPhoneNumber = smsProviderSpecifiedPhoneNumber;
	}

	@Override
	public void clear()
	{
		smsProvider = ObidosConstants.SMS_PROVIDER_TWILIO; // default
		apiSecret = apiKey = null;
	}
}
