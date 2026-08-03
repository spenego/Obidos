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

package com.spenego.Obidos.server.diceware;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 *
 * @author spgdev@spenego.com - Jan 24, 2017
 */
public class Dice
{
	private SecureRandom rand;

	public Dice() {
		try {
			// ref: https://www.cigital.com/blog/proper-use-of-javas-securerandom/
			// ref: http://www.componentix.com/blog/6/using-cryptographically-strong-random-number-generator-with-securerandom-in-java
			this.rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new ServerSideException("Could not generate SecureRandom for Dice");
		}
	}

	public int roll() {
		return roll(6);
	}

	public int roll(int max) {
		return 1 + rand.nextInt(max);
	}
}
