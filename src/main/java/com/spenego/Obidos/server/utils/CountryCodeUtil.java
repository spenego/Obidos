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

package com.spenego.Obidos.server.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * A utility class for Parsing phone numbers using google libphonenumber library
 * @author spgdev@spenego.com - Jun 30, 2024
 * With help from Claude Sonnet 3.5
 */
public class CountryCodeUtil
{
	private static final Logger logger = LoggerFactory.getLogger(CountryCodeUtil.class);

	public static final int FORMAT_NATIONAL = 1;
	public static final int FORMAT_INTERNATIONAL = 2;
	public static final int FORMAT_E164 = 3;

	public static String formatNumber(String phoneNumber, final int format) throws ServerSideException
	{
		try
		{
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			PhoneNumber pn = getPhoneNumber(phoneNumber);
			switch (format)
			{
				case FORMAT_NATIONAL:
					return phoneUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
				case FORMAT_INTERNATIONAL:
					return phoneUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
				case FORMAT_E164:
					return phoneUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
				default:
					throw new ServerSideException("Unknown phone number format");
			}
			
		} catch (Exception e)
		{
			throw new ServerSideException("Could not parse phone number phoneNumber: " + e);
		}
	}

	public static String formatToE164(String phoneNumber, String countryCode) throws ServerSideException
	{
		try
		{
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);

			if (phoneUtil.isValidNumber(numberProto)) {
				return phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
			}
			throw new ServerSideException("Invalid phone number");
		} catch (NumberParseException e) {
			throw new ServerSideException("Could not parse phone number phoneNumber: " + e);
		}
	}
	
	public static String formatToE164(String number)
	{
		return formatToE164(number, null);
    }	

	public static Map<String, CountryCodeDTO> getCountryCodesMap()
	{
		final Map<String, CountryCodeDTO> map = new HashMap<String, CountryCodeDTO>();
		List<CountryCodeDTO> countryCodes = getCountryCodes();
		for (CountryCodeDTO cc : countryCodes)
		{
			map.put(cc.getCountryCodeLine(), cc);
		}

		// Create a list of entries
		List<Map.Entry<String, CountryCodeDTO>> entryList = new ArrayList<Map.Entry<String, CountryCodeDTO>>(map.entrySet());

		// Sort the list by country name
		Collections.sort(entryList, new Comparator<Map.Entry<String, CountryCodeDTO>>()
		{
			@Override
			public int compare(Map.Entry<String, CountryCodeDTO> e1, Map.Entry<String, CountryCodeDTO> e2)
			{
                return e1.getValue().getCountryName().compareTo(e2.getValue().getCountryName());

			}
		});

		// Create a new LinkedHashMap to preserve the sorting order
		Map<String, CountryCodeDTO> sortedMap = new LinkedHashMap<String, CountryCodeDTO>();
		for (Map.Entry<String, CountryCodeDTO> entry : entryList)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static List<CountryCodeDTO> getCountryCodes()
	{
		List<CountryCodeDTO> countryCodes = new ArrayList<CountryCodeDTO>();
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

		Set<String> regions = phoneUtil.getSupportedRegions();

		for (String region : regions)
		{
			int code = phoneUtil.getCountryCodeForRegion(region);
			String countryName = new Locale("", region).getDisplayCountry();

			// Create a sample phone number to get the example national number
			PhoneNumber exampleNumber = phoneUtil.getExampleNumber(region);
			String exampleNationalNumber = "";
			CountryCodeDTO cc = new CountryCodeDTO("+" + code, countryName, exampleNationalNumber);
			cc.setNumberRegion(region);
			cc.setCountryName(countryName);
			String line = cc.getCountryName() + " (" + cc.getDialCode() + ")";
			cc.setCountryCodeLine(line);

			// format the example number to national format
			// it can be used as place holder in text box to give some hint
			if (exampleNumber != null)
			{
				exampleNationalNumber = String.valueOf(exampleNumber.getNationalNumber());
				exampleNationalNumber = "+" + code + exampleNationalNumber;
				try
				{
					String formatted = formatNumber(exampleNationalNumber,FORMAT_NATIONAL);
					cc.setExampleNumber(formatted);
				} catch (Exception e)
				{
					logger.info(()-> "XXX Exception caught for  region:" + region + " "+ exampleNumber + ":" + e);
					cc.setExampleNumber("");
					
				}
			}

			countryCodes.add(cc);
		}

		// Sort the list by country name
		// took Claude several attempts to give code that works with jdk 8
		Collections.sort(countryCodes, new Comparator<CountryCodeDTO>()
		{
			public int compare(CountryCodeDTO c1, CountryCodeDTO c2)
			{
				return c1.getCountryName().compareTo(c2.getCountryName());
			}
		});

		return countryCodes;
	}
	
	// return a list with lines like: Country Name (+dial code) e.g.
	// United States (+1), Denmark (+45) etc.
	public static List<String> getCountryCode()
	{
		List<CountryCodeDTO> countryCodelist = getCountryCodes();
		List<String> countryAndCodeList = new ArrayList<String>();
		for (CountryCodeDTO cc : countryCodelist)
		{
			String line = cc.getCountryName() + " (" + cc.getDialCode() + ")";
			countryAndCodeList.add(line);
		}
		return countryAndCodeList;
	}

	
	private static PhoneNumber getPhoneNumber(final String phoneNumber) throws ServerSideException
	{
		try
		{
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			PhoneNumber pn = phoneUtil.parse(phoneNumber, null);
			if (phoneUtil.isValidNumber(pn))
			{
				return pn;
			}
			throw new ServerSideException("Invalid phone number: " + phoneNumber);
		} catch (Exception e)
		{
			throw new ServerSideException("Invalid phone number: " + phoneNumber + ":" + e);
		}
		
	}

	// Give an E.164 number e.g +12153456789 Return the string like:
	// United States (+1)
	public static String getCountryNameAndCode(String e164Number) throws ServerSideException
	{
		try
		{
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			PhoneNumber phoneNumber = phoneUtil.parse(e164Number, null);

			// Get the country code
			int countryCode = phoneNumber.getCountryCode();

			// Get the region code (e.g., "US" for United States)
			String regionCode = phoneUtil.getRegionCodeForNumber(phoneNumber);

			// Get the country name in the default locale
			String countryName = new Locale("", regionCode).getDisplayCountry();

			return String.format("%s (+%d)", countryName, countryCode);
		//} catch (NumberParseException e)
		} catch (Exception e)
		{
			throw new ServerSideException("Invalid E.164 number " + e164Number, e);
		}
	}
	
	public static int getCountryCode(final String phoneNumber) throws ServerSideException
	{
		PhoneNumber pn = getPhoneNumber(phoneNumber);
		return pn.getCountryCode();
	}

	/**
	 * Removes the country code from a phone number.
	 * 
	 * @param phoneNumber
	 *            The full phone number including country code. The country code
	 *            is at the beginning of the phoneNumber without +
	 * @param countryCode
	 *            The country code to remove. e.g +1, +405 etc
	 * @return The phone number without the country code
	 */
	public static String removeCountryCode(String phoneNumber, String countryCode)
	{
		// First, remove any non-digit characters
		String cleanNumber = phoneNumber.replaceAll("\\D", "");
		String cleanCountryCode = countryCode.replaceAll("\\D", "");

		// Check if the number starts with the country code
		if (cleanNumber.startsWith(cleanCountryCode)) {
			return cleanNumber.substring(cleanCountryCode.length());
		}
		// If the number doesn't start with the country code, return the
		// original number
		return cleanNumber;
	}

	public static CountryCodeDTO getPhoneNumberInfo(final String phoneNumber) throws ServerSideException
	{
		final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		final PhoneNumber pn = getPhoneNumber(phoneNumber);
		String regionCode = phoneUtil.getRegionCodeForNumber(pn);
		final CountryCodeDTO cc = new CountryCodeDTO(phoneNumber);
		int code = phoneUtil.getCountryCodeForRegion(regionCode);

		cc.setNumberNationalFormat(formatNumber(phoneNumber,FORMAT_NATIONAL));
		cc.setNumberInternationFormat(formatNumber(phoneNumber,FORMAT_INTERNATIONAL));
		cc.setNumberE164Format(formatNumber(phoneNumber,FORMAT_E164));
		cc.setCountryName(new Locale("", regionCode).getDisplayCountry());
		cc.setNumberRegion(regionCode);
		cc.setDialCode(""+code);
		cc.setNumberWithoutDialCode(removeCountryCode(phoneNumber, "" + code));

		PhoneNumber exampleNumber = phoneUtil.getExampleNumber(regionCode);
		String exampleNationalNumber = "";
		if (exampleNumber != null)
		{
			exampleNationalNumber = String.valueOf(exampleNumber.getNationalNumber());
			exampleNationalNumber = "+" + code + exampleNationalNumber;
			String formatted = formatNumber(exampleNationalNumber,FORMAT_NATIONAL);
			cc.setExampleNumber(formatted);
		}

		return cc;
	}
}
