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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PasswordAnalysisResults extends BaseShared implements Serializable {
	private static final long serialVersionUID = 1L;

	// https://blogs.dropbox.com/tech/2012/04/zxcvbn-realistic-password-strength-estimation/
	// https://github.com/dropbox/zxcvbn
	// We will use: https://github.com/nulab/zxcvbn4j
	public static final int ALGORITHM_DROPBOX_ZXCVBN= 1;
	public static final int ALGORITHM2				= 2;
	public static final int ALGORITHM3				= 3;

	public static final int PASSWORD_WEAK			= 0;
	public static final int PASSWORD_SO_SO			= 1;
	public static final int PASSWORD_GOOD			= 2;
	public static final int PASSWORD_STRONG			= 3;
	public static final int PASSWORD_VERY_STRONG	= 4;

	private Integer passwordScore;
	private Float rawEntropy;
	private Float entropyAfterRepeatsWeakened;
	private Float entropyAfterLowerCased;
	private Float entropyAfterQwertyAdjusted;
	private Float entropyAfterDictionaryAdjusted;
	private Float entropy;
	private Integer acceptablePasswordScore;
	private Integer algorithmUsed;
	private String localRequirementsNotMetMessage;
	private Long crackingGuessPerSecond;
	private String crackingTime;

	private ArrayList<String> suggestions;

	public PasswordAnalysisResults() {
		passwordScore = 0;
	}

	public PasswordAnalysisResults(final Integer acceptablePasswordScore, final Integer passwordScore, final String localRequirementsNotMetMessage) {
		this.passwordScore = passwordScore;
		this.acceptablePasswordScore = acceptablePasswordScore;
		this.localRequirementsNotMetMessage = localRequirementsNotMetMessage;
	}

	public PasswordAnalysisResults(Integer acceptablePasswordScore) {
		this.acceptablePasswordScore = acceptablePasswordScore;
		passwordScore = 0;
	}

	public PasswordAnalysisResults(Integer acceptablePasswordScore, String suggestion) {
		this.acceptablePasswordScore = acceptablePasswordScore;
		passwordScore = 0;
	}

	public PasswordAnalysisResults(Integer acceptablePasswordScore, final Integer passwordScore, final Float rawEntropy, final Float entropy, final Integer algorithmUsed, final List<String> suggestions)
	{
		this.acceptablePasswordScore = acceptablePasswordScore;
		this.passwordScore = passwordScore;
		this.rawEntropy = rawEntropy;
		this.entropy = entropy;
		this.algorithmUsed = algorithmUsed;
		this.suggestions = toArrayList(suggestions);
	}

	public PasswordAnalysisResults(final Float entropy, final Float rawEntropy, final Float entropyAfterRepeatsWeakened, final Float entropyAfterLowerCased, final Float entropyAfterQwertyAdjusted, final Float entropyAfterDictionaryAdjusted)
	{
		this.acceptablePasswordScore = null;
		this.passwordScore = 0;
		this.rawEntropy = rawEntropy;
		this.entropy = entropy;
		this.entropyAfterRepeatsWeakened = entropyAfterRepeatsWeakened;
		this.entropyAfterLowerCased = entropyAfterLowerCased;
		this.entropyAfterQwertyAdjusted = entropyAfterQwertyAdjusted;
		this.entropyAfterDictionaryAdjusted = entropyAfterDictionaryAdjusted;
	}

	public Integer getPasswordScore() {
		return passwordScore;
	}

	public void setPasswordScore(Integer passwordScore) {
		this.passwordScore = passwordScore;
	}

	public Integer getAlgorithmUsed() {
		return algorithmUsed;
	}

	public void setAlgorithmUsed(Integer algorithmUsed) {
		this.algorithmUsed = algorithmUsed;
	}

    public List<String> getSuggestions()
    {
        return suggestions;
    }

    public void setSuggestions(final Collection<String> suggestions)
    {
        this.suggestions = new ArrayList<>(suggestions);
    }

	public Integer getAcceptablePasswordScore() {
		return acceptablePasswordScore;
	}

	public void setAcceptablePasswordScore(Integer acceptablePasswordScore) {
		this.acceptablePasswordScore = acceptablePasswordScore;
	}

	public String getLocalRequirementsNotMetMessage()
	{
		return localRequirementsNotMetMessage;
	}

	public void setLocalRequirementsNotMetMessage(String localRequirementsNotMetMessage)
	{
		this.localRequirementsNotMetMessage = localRequirementsNotMetMessage;
	}

	public Float getEntropy()
	{
		return entropy;
	}

	public void setEntropy(Float entropy)
	{
		this.entropy = entropy;
	}

	public Long getCrackingGuessPerSecond()
	{
		return crackingGuessPerSecond;
	}

	public void setCrackingGuessPerSecond(Long crackingGuessPerSecond)
	{
		this.crackingGuessPerSecond = crackingGuessPerSecond;
	}

	public String getCrackingTime()
	{
		return crackingTime;
	}

	public void setCrackingTime(String crackingTime)
	{
		this.crackingTime = crackingTime;
	}

	public Float getRawEntropy()
	{
		return rawEntropy;
	}

	public void setRawEntropy(Float rawEntropy)
	{
		this.rawEntropy = rawEntropy;
	}

	public Float getEntropyAfterRepeatsWeakened()
	{
		return entropyAfterRepeatsWeakened;
	}

	public void setEntropyAfterRepeatsWeakened(Float entropyAfterRepeatsWeakened)
	{
		this.entropyAfterRepeatsWeakened = entropyAfterRepeatsWeakened;
	}

	public Float getEntropyAfterLowerCased()
	{
		return entropyAfterLowerCased;
	}

	public void setEntropyAfterLowerCased(Float entropyAfterLowerCased)
	{
		this.entropyAfterLowerCased = entropyAfterLowerCased;
	}

	public Float getEntropyAfterQwertyAdjusted()
	{
		return entropyAfterQwertyAdjusted;
	}

	public void setEntropyAfterQwertyAdjusted(Float entropyAfterQwertyAdjusted)
	{
		this.entropyAfterQwertyAdjusted = entropyAfterQwertyAdjusted;
	}

	public Float getEntropyAfterDictionaryAdjusted()
	{
		return entropyAfterDictionaryAdjusted;
	}

	public void setEntropyAfterDictionaryAdjusted(Float entropyAfterDictionaryAdjusted)
	{
		this.entropyAfterDictionaryAdjusted = entropyAfterDictionaryAdjusted;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public static int getAlgorithmDropboxZxcvbn()
	{
		return ALGORITHM_DROPBOX_ZXCVBN;
	}

	public static int getAlgorithm2()
	{
		return ALGORITHM2;
	}

	public static int getAlgorithm3()
	{
		return ALGORITHM3;
	}

	public static int getPasswordWeak()
	{
		return PASSWORD_WEAK;
	}

	public static int getPasswordSoSo()
	{
		return PASSWORD_SO_SO;
	}

	public static int getPasswordGood()
	{
		return PASSWORD_GOOD;
	}

	public static int getPasswordStrong()
	{
		return PASSWORD_STRONG;
	}

	public static int getPasswordVeryStrong()
	{
		return PASSWORD_VERY_STRONG;
	}

	public void setSuggestions(ArrayList<String> suggestions)
	{
		this.suggestions = suggestions;
	}

}