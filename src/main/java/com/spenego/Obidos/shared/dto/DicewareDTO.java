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

public final class DicewareDTO implements Serializable
{
	/**
	 * @author spgdev@spenego.com - Feb 22, 2017
	 */
	private static final long serialVersionUID = 1L;

	private Boolean uppercaseWords;
	private Boolean noSpaces;
	private Boolean addSpecialChar;
	private int     numberOfWords;
	private String  language;

	public Boolean getUppercaseWords()
	{
		return uppercaseWords;
	}
	public void setUppercaseWords(Boolean uppercaseWords)
	{
		this.uppercaseWords = uppercaseWords;
	}
	public Boolean getNoSpaces()
	{
		return noSpaces;
	}
	public void setNoSpaces(Boolean noSpaces)
	{
		this.noSpaces = noSpaces;
	}
	public int getNumberOfWords()
	{
		return numberOfWords;
	}
	public void setNumberOfWords(int numberOfWords)
	{
		this.numberOfWords = numberOfWords;
	}
	public String getLanguage()
	{
		return language;
	}
	public void setLanguage(String language)
	{
		this.language = language;
	}
    public Boolean getAddSpecialChar()
    {
        return addSpecialChar;
    }
    public void setAddSpecialChar(Boolean addSpecialChar)
    {
        this.addSpecialChar = addSpecialChar;
    }
}
