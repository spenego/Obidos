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

import java.util.Random;
import java.util.regex.Pattern;

import com.spenego.Obidos.shared.dto.GenPassDTO;

/**
 * Modify a password/passphrase by adding upper case, numbers or symbols
 * With assistance from Calude AI
 * @author spgdev@spenego.com - Sep 7, 2024
 */
public class PassModifier
{
	// use a mostly used subsets
	private static final String SYMBOLS = "!@#$%^&*()+";
	private static final Random random = new Random();
	private static final int    BASE_LENGTH = 8;
	private static final double MAX_ELEMENT_PERCENTAGE = 0.25;
	private static final int    MAX_ELEMENT_COUNT = 5;

	private boolean atleastOneUppercase;
	private boolean atleastOneNumber;
	private boolean atleastOneSymbol;

	public PassModifier(boolean atleastOneUppercase, boolean atleastOneNumber, boolean atleastOneSymbol)
	{
		this.atleastOneUppercase = atleastOneUppercase;
		this.atleastOneNumber = atleastOneNumber;
		this.atleastOneSymbol = atleastOneSymbol;
	}

	public static String modifyPassword(final String pass, final GenPassDTO options)
	{
		StringBuilder modifiedPassword = new StringBuilder(pass);
		int passwordLength = pass.length();

		int requiredCapitals = calculateRequiredElements(passwordLength, options.getCapitalize());
		int requiredNumerals = calculateRequiredElements(passwordLength, options.getNumerals());
		int requiredSymbols = calculateRequiredElements(passwordLength, options.getSymbols());

		addRequiredCapitals(modifiedPassword, requiredCapitals);
		addRequiredNumerals(modifiedPassword, requiredNumerals);
		addRequiredSymbols(modifiedPassword, requiredSymbols);

		return modifiedPassword.toString();
	}

	private static int calculateRequiredElements(int passwordLength, boolean optionEnabled)
	{
		if (!optionEnabled)
			return 0;

		int baseCount = 1;
		if (passwordLength > BASE_LENGTH)
		{
			double complexityFactor = (double) (passwordLength - BASE_LENGTH) / BASE_LENGTH;
			int additionalCount = (int) Math.floor(complexityFactor);
			int totalCount = baseCount + additionalCount;

			int maxCount = (int) Math.min(passwordLength * MAX_ELEMENT_PERCENTAGE, MAX_ELEMENT_COUNT);
			return Math.min(totalCount, maxCount);
		}
		return baseCount;
	}

	private static boolean containsUppercase(String str)
	{
		return !str.equals(str.toLowerCase());
	}

	private static boolean containsNumeral(String str)
	{
		return str.matches(".*\\d.*");
	}

	private static boolean containsSymbol(String str)
	{
		// return str.matches(".*[" + SYMBOLS + "].*");
		return Pattern.compile(".*[" + Pattern.quote(SYMBOLS) + "].*").matcher(str).matches();
	}

	private static void addRequiredCapitals(StringBuilder password, int count)
	{
		for (int i = 0; i < count; i++)
		{
			if (!containsUppercase(password.toString()))
			{
				int position = findAvailablePosition(password, true);
				if (position != -1)
				{
					password.setCharAt(position, Character.toUpperCase(password.charAt(position)));
				} else
				{
					System.out.println("Debug: No available position for capitalization");
					break;
				}
			}
		}
	}

	private static void addRequiredNumerals(StringBuilder password, int count)
	{
		for (int i = 0; i < count; i++)
		{
			if (!containsNumeral(password.toString()))
			{
				int position = findAvailablePosition(password, false);
				if (position != -1)
				{
					char numeral = (char) ('0' + random.nextInt(10));
					password.setCharAt(position, numeral);
				} else
				{
					System.out.println("Debug: No available position for adding numeral");
					break;
				}
			}
		}
	}

	private static void addRequiredSymbols(StringBuilder password, int count)
	{
		for (int i = 0; i < count; i++)
		{
			if (!containsSymbol(password.toString()))
			{
				int position = findAvailablePosition(password, false);
				if (position != -1)
				{
					char symbol = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));
					password.setCharAt(position, symbol);
				} else
				{
					System.out.println("Debug: No available position for adding symbol");
					break;
				}
			}
		}
	}

	private static int findAvailablePosition(StringBuilder password, boolean forCapitalization)
	{
		int attempts = 0;
		int maxAttempts = password.length() * 2; // Arbitrary limit to prevent
													// infinite loop
		while (attempts < maxAttempts)
		{
			int position = random.nextInt(password.length());
			if (isPositionAvailable(password, position, forCapitalization))
			{
				return position;
			}
			attempts++;
		}
		System.out.println("Debug: Couldn't find available position after " + attempts + " attempts");
		return -1;
	}

	private static boolean isPositionAvailable(StringBuilder password, int position, boolean forCapitalization)
	{
		char c = password.charAt(position);
		if (forCapitalization) {
			return Character.isLetter(c) && Character.isLowerCase(c);
		}
		return Character.isLetter(c); // Allow replacing any letter with a number or symbol
	}

	public boolean isAtleastOneUppercase()
	{
		return atleastOneUppercase;
	}

	public void setAtleastOneUppercase(boolean atleastOneUppercase)
	{
		this.atleastOneUppercase = atleastOneUppercase;
	}

	public boolean isAtleastOneNumber()
	{
		return atleastOneNumber;
	}

	public void setAtleastOneNumber(boolean atleastOneNumber)
	{
		this.atleastOneNumber = atleastOneNumber;
	}

	public boolean isAtleastOneSymbol()
	{
		return atleastOneSymbol;
	}

	public void setAtleastOneSymbol(boolean atleastOneSymbol)
	{
		this.atleastOneSymbol = atleastOneSymbol;
	}
}