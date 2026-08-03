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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.SodiumUtils;
import com.spenego.Obidos.client.rpc.DicewareService;
import com.spenego.Obidos.server.diceware.Dice;
import com.spenego.Obidos.server.diceware.Wordlist;
import com.spenego.Obidos.server.diceware.WordlistDiceware;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DicewareDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * A spring service to generate strong pass phrase according to diceware
 * algorithm
 *
 * @author spgdev@spenego.com - Jan 26, 2017
 */
@Service("dicewareService")
public final class DicewareServiceImpl implements DicewareService {
	private static final Logger logger = LoggerFactory.getLogger(DicewareServiceImpl.class);
	private static final int DICE_FACE = 6;
	private static final String ENGLISH_WORD_FILE = "words/diceware.wordlist";
	private static final String GERMAN_WORD_FILE = "words/german.wordlist";
	private static Wordlist englishWordList = null;
	private static Wordlist germanWordList = null;
	private Wordlist wordList = null;

	@Autowired private Authenticator authenticator;

	private static final void setEnglishWordList() {
		englishWordList = new WordlistDiceware(ENGLISH_WORD_FILE);
		logger.info(() -> "English world list loaded");
	}

	private static final void setGermanWordList() {
		germanWordList = new WordlistDiceware(GERMAN_WORD_FILE);
		logger.info(() -> "German world list loaded");
	}

	private static void addSpecialCharacter(List<String> words) {
		logger.info(() -> "Add special character");
		Dice dice = new Dice();
		int wordNum = dice.roll(words.size());
		if (wordNum >= 1) {
			wordNum = wordNum - 1;
		}
		String word = words.get(wordNum);
		String changedWord = addAChar(word);
		words.set(wordNum, changedWord);
	}

	private static final String createPassPhrase(Collection<String> words, final boolean noSpaces) {
		final StringBuilder stringBuilder = new StringBuilder();

		for (final String w : words) {
			stringBuilder.append(w);
			if (!noSpaces) { stringBuilder.append(" "); }
		}

		return stringBuilder.toString();
	}

	private static Wordlist getWordList(String language) {
		if (language.equals(ObidosConstants.DICEWARE_ENGLISH)) {
			logger.debug(() -> ">>>>>>>>>>> ENGLISH");
			if (englishWordList == null) {
				setEnglishWordList();
			}
			return englishWordList;
		}
		if (language.equals(ObidosConstants.DICEWARE_GERMAN)) {
			logger.debug(() -> ">>>>>>>>>>> GERMAN");
			if (germanWordList == null) {
				setGermanWordList();
			}
			return germanWordList;
		}

		throw new ServerSideException("Unknown language " + language);
	}

	@Override
	public String genStrongPassPhrase(final AuthCredsDTO creds, final DicewareDTO dicewareDTO) throws ServerSideException {
		// make sure the logged in user is a regular user
		try {
			// any logged in user should be able to generate password
			authenticator.checkLoggedIn(creds);
		} catch (ServerSideException e) {
			throw new ServerSideException("Denied!");
		}
		logger.info(() -> "user is ok");

		if (dicewareDTO == null) {
			throw new ServerSideException("No Configuration specified");
		}

		wordList = getWordList(dicewareDTO.getLanguage());

		int numberOfWords = dicewareDTO.getNumberOfWords();
		if (numberOfWords <= 0) {
			numberOfWords = 4;
		}
		if (numberOfWords > 8) {
			numberOfWords = 8;
		}
		final int finalNumberOfWords = numberOfWords;
		logger.debug(() -> ">>>>>>>>>>>>>>>>>>>>. number of words: " + finalNumberOfWords);

		ArrayList<String> words = new ArrayList<>(numberOfWords);
		for (int n = 0; n < numberOfWords; n++) {
			String word = getDicewareWord();
			if (Boolean.TRUE.equals(dicewareDTO.getUppercaseWords())) {
				word = capitalize(word);
			}

			words.add(word);
		}
		logger.info(() -> "---- x");
		logger.info(() -> "add special char: " + dicewareDTO.getAddSpecialChar());
		logger.info(() -> "---- y");

		// Try to make secure as per diceware doc.
		// Pick a random word from the list
		boolean addSpecialChar = true;
		if (dicewareDTO.getAddSpecialChar() != null) {
			addSpecialChar = dicewareDTO.getAddSpecialChar();
		}
		if (addSpecialChar) {
			addSpecialCharacter(words);
		}

		return createPassPhrase(words, Boolean.TRUE.equals(dicewareDTO.getNoSpaces()));
	}

	public static String addAChar(String word) throws ServerSideException {
		// Ref: http://world.std.com/~reinhold/diceware.html
		/*
		 * For extra security without adding another word, insert one special
		 * character or digit chosen at random into your passphrase. Here is how
		 * to do this securely: Roll one die to choose a word in your
		 * passphrase, roll again to choose a letter in that word. Roll a third
		 * and fourth time to pick the added character from the following table:
		 *
		 * Third Roll
		 *
		 * 1 2 3 4 5 6 F 1 ~ ! # $ % ^ o 2 & * ( ) - = u 3 + [ ] \ { } r 4 : ; "
		 * ' < > t 5 ? / 0 1 2 3 h 6 4 5 6 7 8 9
		 */
		char[][] chars = { { '~', '!', '#', '$', '%', '^' }, { '&', '*', '(', ')', '-', '=' }, { '+', '[', ']', '\\', '{', '}' }, { ':', ';', '"', '\'', '<', '>' },
				{ '?', '/', '0', '1', '2', '3' }, { '4', '5', '6', '7', '8', '9' }, };
		int wordLen = word.length();
		Dice dice = new Dice();
		int third = dice.roll(wordLen);
		int fourth = dice.roll(wordLen);
		char c = chars[third - 1][fourth - 1];
		int rc = dice.roll(wordLen);
		char cc = word.charAt(rc - 1);
		logger.info(() -> "Picked word: " + word);
		logger.info(() -> "Insert before: " + cc);
		logger.info(() -> "Secure char: " + c);
		String firstPart = word.substring(0, (rc - 1));
		String lastPart = word.substring(rc - 1);
		String changedWord = firstPart + c + lastPart;

		logger.info(() -> "First part: " + firstPart);
		logger.info(() -> "Last part: " + lastPart);

		logger.info(() -> "Chagned word: " + changedWord);
		return changedWord;
	}

	private static String capitalize(final String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	private String getDicewareWord() throws ServerSideException {
		String index = rollDice();
		return wordList.getWord(index);
	}

	private static String rollDice() {
		try {
			Dice dice = new Dice();
			StringBuilder index = new StringBuilder();

			for (int i = 0; i < (DICE_FACE - 1); i++) {
				index.append(dice.roll());
			}

			return index.toString();
		} catch (ServerSideException e) {
			throw new ServerSideException("Could not roll Dice");
		}
	}

	@Override
	public String genSecureRandomPass(AuthCredsDTO creds) throws ServerSideException
	{
		try
		{
			// any logged in user should be able to generate password
			authenticator.checkLoggedIn(creds);
		} catch (ServerSideException e) {
			throw new ServerSideException("Denied!");
		}
		byte[] bytes = SodiumLibrary.randomBytes(16);
		String hex = SodiumUtils.binary2Hex(bytes);

		return hex;
	}
}
