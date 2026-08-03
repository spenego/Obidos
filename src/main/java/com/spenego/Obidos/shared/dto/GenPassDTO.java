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

public final class GenPassDTO implements Serializable
{
	/**
	 * @author spgdev@spenego.com - Sep-07-2024 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Language {
		ENGLISH,
		GERMAN,
	}

    public GenPassDTO() {}

	private Boolean capitalize;
	private Boolean numerals;
	private Boolean symbols;
	private Integer numberOfWords;
	private Integer passLength;
	private Language  language;

	private float entropy;
	private Long crackingGuessesPerSec;
	
	public GenPassDTO(Boolean capitalize, Boolean numerals,
			Boolean symbols,
			Integer numberOfWords,
			Integer passLength,
			Language language)
	{
		this.capitalize = capitalize;
		this.numerals = numerals;
		this.symbols = symbols;
		this.numberOfWords = numberOfWords;
		this.passLength = passLength;
		this.language = language;
	}
	

	public Language getLanguage()
	{
		return language;
	}
	public void setLanguage(Language lang)
	{
		this.language = lang;
	}


	public Boolean getCapitalize()
	{
		return capitalize;
	}


	public Boolean getNumerals()
	{
		return numerals;
	}


	public void setNumerals(Boolean numerals)
	{
		this.numerals = numerals;
	}


	public void setCapitalize(Boolean capitalize)
	{
		this.capitalize = capitalize;
	}


	public Boolean getSymbols()
	{
		return symbols;
	}


	public void setSymbols(Boolean symbols)
	{
		this.symbols = symbols;
	}


	public Integer getNumberOfWords()
	{
		return numberOfWords;
	}


	public void setNumberOfWords(Integer numberOfWords)
	{
		this.numberOfWords = numberOfWords;
	}


	public Integer getPassLength()
	{
		return passLength;
	}


	public void setPassLength(Integer passLength)
	{
		this.passLength = passLength;
	}


	public float getEntropy()
	{
		return entropy;
	}

	public void setEntropy(float entropy)
	{
		this.entropy = entropy;
	}


	public Long getCrackingGuessesPerSec()
	{
		return crackingGuessesPerSec;
	}


	public void setCrackingGuessesPerSec(Long crackingGuessesPerSec)
	{
		this.crackingGuessesPerSec = crackingGuessesPerSec;
	}
}
