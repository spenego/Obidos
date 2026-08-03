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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 *
 * @author spgdev@spenego.com - Jan 25, 2017
 */
public class WordlistDiceware implements Wordlist
{
	private static final Logger logger = LoggerFactory.getLogger(WordlistDiceware.class);

	private HashMap<String, String> wordlist = new HashMap<String, String>();
	private String filePath = "diceware.wordlist";

	public WordlistDiceware(String filePath) {
		if (filePath != null) {
			this.filePath = filePath;
		}
		logger.info(()-> "wordlist path: " + filePath);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream(this.filePath);
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if(line != null) {
					String[] str = line.split(" ");
					wordlist.put(str[0], str[1]);
				}
			}
		} catch (final IOException e) {
			throw new ServerSideException(">>>>>>>>>>>>>>>> Could not read wordlist" + e.getMessage());
		}
	}

	public String getWord(String index)
	{
		boolean found = this.wordlist.containsKey(index);
		String word = null;
		if (found) {
			word = this.wordlist.get(index);
		}

		return word;
	}
}
