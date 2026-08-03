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

package com.spenego.Obidos.server.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muquit.gpw.Gpw;
import com.muquit.gpw.GpwPasswordModifier;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.SodiumUtils;
import com.spenego.Obidos.client.rpc.GenPassService;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.GenPassDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * @author spgdev@spenego.com - Sep-07-2024
 */
@Service("genpassService")
public final class GenPassServiceImpl implements GenPassService
{
	private static final Logger logger = LoggerFactory.getLogger(GenPassServiceImpl.class);

	@Autowired
	private Authenticator authenticator;

	@Override
	public String genSecureRandomPass(AuthCredsDTO creds) throws ServerSideException
	{
		try
		{
			authenticator.checkLoggedIn(creds);

		} catch (ServerSideException e)
		{
			throw new ServerSideException("Denied!");
		}
		byte[] bytes = SodiumLibrary.randomBytes(16);
		String hex = SodiumUtils.binary2Hex(bytes);
		logger.info(() -> "MMMM secure random password: " + hex);

		return hex;
	}

	public static String getPasswordCrackingTime(final float entropy, Long guessesPerSec) throws ServerSideException
	{
		if (guessesPerSec == null || guessesPerSec <= 0)
		{
			throw new ServerSideException("Invalid guesses per second");
		}

		double seconds = Math.pow(2, entropy) / guessesPerSec;

		if (seconds < 1)
		{
			return "Instantly";
		}

		long years = (long) (seconds / (365 * 24 * 60 * 60));
		seconds %= (365 * 24 * 60 * 60);
		long days = (long) (seconds / (24 * 60 * 60));
		seconds %= (24 * 60 * 60);
		long hours = (long) (seconds / (60 * 60));
		seconds %= (60 * 60);
		long minutes = (long) (seconds / 60);
		seconds %= 60;

		StringBuilder result = new StringBuilder();
		if (years > 0)
		{
			result.append(years).append(years == 1 ? " year" : " years");
		}
		if (days > 0)
		{
			if (result.length() > 0)
				result.append(", ");
			result.append(days).append(days == 1 ? " day" : " days");
		}
		if (hours > 0)
		{
			if (result.length() > 0)
				result.append(", ");
			result.append(hours).append(hours == 1 ? " hour" : " hours");
		}
		if (minutes > 0)
		{
			if (result.length() > 0)
				result.append(", ");
			result.append(minutes).append(minutes == 1 ? " minute" : " minutes");
		}
		if (seconds > 0 || result.length() == 0)
		{
			if (result.length() > 0)
				result.append(", ");
			result.append(String.format("%.2f seconds", seconds));
		}

		return result.toString();
	}

	@Override
	public String genPronounceablePass(AuthCredsDTO creds, GenPassDTO dto) throws ServerSideException
	{
		try
		{
			authenticator.checkLoggedIn(creds);
		} catch (ServerSideException e)
		{
			throw new ServerSideException("Denied!");
		}
		Gpw gpw = new Gpw();
		String pass = gpw.generateOnePassword(dto.getPassLength());
		if (pass == null || pass.length() == 0)
		{
			throw new ServerSideException("Could not generate pronounceable password");
		}
		String modified = pass;

		// modify if needed
		boolean capitalize = false;
		boolean numerals = false;
		boolean symbols = false;

		if (dto.getCapitalize() != null)
		{
			capitalize = dto.getCapitalize();
		}
		if (dto.getNumerals() != null)
		{
			numerals = dto.getNumerals();
		}
		if (dto.getSymbols() != null)
		{
			symbols = dto.getSymbols();
		}
		if (capitalize != false || numerals != false || symbols != false)
		{
			modified = GpwPasswordModifier.modifyPassword(pass, capitalize, numerals, symbols);
		}
		return modified;
	}
}
