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

/**
 * @author spgdev@spenego.com - Jul 13, 2024
 */
public class CountryCodeDTO implements Serializable
{
	private static final long serialVersionUID = -1856300509426986605L;

	private String phoneNumber;

	private String dialCode;
	private String countryName;

	private String exampleNumber;
	private String numberE164Format;
	private String numberNationalFormat;
	private String numberInternationFormat;
	private String numberRegion;
	private String countryCodeLine; // Country name (+code)
	private String numberWithoutDialCode;
	
	public CountryCodeDTO() {}

	public CountryCodeDTO(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	

	public CountryCodeDTO(String dialCode, String countryName, String exampleNumber)
	{
		this.dialCode = dialCode;
		this.countryName = countryName;
		this.exampleNumber = exampleNumber;
	}

	public String getNumberE164Format()
	{
		return numberE164Format;
	}

	public void setNumberE164Format(String numberE164Format)
	{
		this.numberE164Format = numberE164Format;
	}

	public String getNumberNationalFormat()
	{
		return numberNationalFormat;
	}

	public void setNumberNationalFormat(String numberNationalFormat)
	{
		this.numberNationalFormat = numberNationalFormat;
	}

	public String getNumberInternationFormat()
	{
		return numberInternationFormat;
	}

	public void setNumberInternationFormat(String numberInternationFormat)
	{
		this.numberInternationFormat = numberInternationFormat;
	}

	public String getNumberRegion()
	{
		return numberRegion;
	}

	public void setNumberRegion(String numberRegion)
	{
		this.numberRegion = numberRegion;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getDialCode()
	{
		return dialCode;
	}

	public String getCountryName()
	{
		return countryName;
	}

	public String getExampleNumber()
	{
		return exampleNumber;
	}

	public void setDialCode(String dialCode)
	{
		this.dialCode = dialCode;
	}

	public void setCountryName(String countryName)
	{
		this.countryName = countryName;
	}

	public void setExampleNumber(String exampleNumber)
	{
		this.exampleNumber = exampleNumber;
	}
	public String getCountryCodeLine()
	{
		return countryCodeLine;
	}

	public void setCountryCodeLine(String countryCodeLine)
	{
		this.countryCodeLine = countryCodeLine;
	}

	public String getNumberWithoutDialCode()
	{
		return numberWithoutDialCode;
	}

	public void setNumberWithoutDialCode(String numberWithoutDialCode)
	{
		this.numberWithoutDialCode = numberWithoutDialCode;
	}

}
