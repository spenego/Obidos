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

public final class ComplexityRequirementsDTO extends BaseDTO implements HasId, Clearable, Serializable {
	private String name;
	private Integer version;
	private Integer minimumLength;
	private Integer minimumUppercase;
	private Integer minimumLowercase;
	private Integer minimumSpecial;
	private Integer minimumNumbers;
	private Integer minimumEntropy;
	private Integer maxAgeInDays;
	private Integer minAgeInSecondsBeforeReset; // password may not be reset  until it is at least this many seconds old
	private Long extraChecksEnabled;

	private static final long serialVersionUID = 1L;

	public static final byte PASSWORD_COMPLEXITY_REQUIREMENTS = 1;
	public static final byte PASSPHRASE_COMPLEXITY_REQUIREMENTS = 2;

	public static final long EXTRA_CHECK_HAVE_I_BEEN_PWNED 	= (1L <<  0);	// check password against haveibeenpwned.com
	public static final long EXTRA_CHECK_CONTAINS_USERNAME 	= (1L <<  1);	// check if password contains user's username
	public static final long EXTRA_CHECK_CONTAINS_EMAIL1 	= (1L <<  2);	// check if password contains user's email address
	public static final long EXTRA_CHECK_CONTAINS_FULLNAME 	= (1L <<  3);	// check if password contains user's full name
	public static final long EXTRA_CHECK_CONTAINS_PHONE 	= (1L <<  4);	// check if password contains user's phone

	public static String getTypeName(final byte type) {
		return (type == PASSWORD_COMPLEXITY_REQUIREMENTS) ? "Password" : (type == PASSPHRASE_COMPLEXITY_REQUIREMENTS) ? "Passphrase" : "Unknown"; // NOSONAR -- this seems simple enough
	}

	@Override
	public void clear() {
		extraChecksEnabled = null;
		version = minimumLength = minimumUppercase = minimumLowercase = minimumSpecial = minimumNumbers = minimumEntropy = maxAgeInDays = minAgeInSecondsBeforeReset = null;
		name = null;
		super.clear();
	}

	public Integer getMinimumLength() {
		return minimumLength;
	}

	public void setMinimumLength(Integer minimumLength) {
		this.minimumLength = minimumLength;
	}

	public Integer getMinimumUppercase() {
		return minimumUppercase;
	}

	public void setMinimumUppercase(Integer minimumUppercase) {
		this.minimumUppercase = minimumUppercase;
	}

	public Integer getMinimumLowercase() {
		return minimumLowercase;
	}

	public void setMinimumLowercase(Integer minimumLowercase) {
		this.minimumLowercase = minimumLowercase;
	}

	public Integer getMinimumSpecial() {
		return minimumSpecial;
	}

	public void setMinimumSpecial(Integer minimumSpecial) {
		this.minimumSpecial = minimumSpecial;
	}

	public Integer getMinimumNumbers() {
		return minimumNumbers;
	}

	public void setMinimumNumbers(Integer minimumNumbers) {
		this.minimumNumbers = minimumNumbers;
	}

	public Integer getMinimumEntropy() {
		return minimumEntropy;
	}

	public void setMinimumEntropy(Integer minimumEntropy) {
		this.minimumEntropy = minimumEntropy;
	}

	public Integer getMaxAgeInDays() {
		return maxAgeInDays;
	}

	public void setMaxAgeInDays(Integer maxAgeInDays) {
		this.maxAgeInDays = maxAgeInDays;
	}

	public Integer getMinAgeInSecondsBeforeReset() {
		return minAgeInSecondsBeforeReset;
	}

	public void setMinAgeInSecondsBeforeReset(Integer minAgeInSecondsBeforeReset) {
		this.minAgeInSecondsBeforeReset = minAgeInSecondsBeforeReset;
	}

	public Long getExtraChecksEnabled() {
		return extraChecksEnabled;
	}

	public void setExtraChecksEnabled(Long extraChecksEnabled) {
		this.extraChecksEnabled = extraChecksEnabled;
	}

	private void setBit(long bit) {
		if (extraChecksEnabled == null) {
			extraChecksEnabled = 0L;
		}
		extraChecksEnabled |= bit;
	}

	private void clearBit(long bit) {
		if (extraChecksEnabled == null) {
			extraChecksEnabled = 0L;
		}
		extraChecksEnabled &= ~bit;
	}

	private boolean isEnabled(long bit) {
		return (extraChecksEnabled & bit) != 0;
	}

	public boolean getHaveIBeenPwnedCheck() {
		return isEnabled(EXTRA_CHECK_HAVE_I_BEEN_PWNED);
	}

	public void setHaveIBeenPwnedCheck() {
		setBit(EXTRA_CHECK_HAVE_I_BEEN_PWNED);
	}

	public void clearHaveIBeenPwnedCheck() {
		clearBit(EXTRA_CHECK_HAVE_I_BEEN_PWNED);
	}

	public boolean getContainsUsernameCheck() {
		return isEnabled(EXTRA_CHECK_CONTAINS_USERNAME);
	}

	public void setContainsUsernameCheck() {
		setBit(EXTRA_CHECK_CONTAINS_USERNAME);
	}

	public void clearContainsUsernameCheck() {
		clearBit(EXTRA_CHECK_CONTAINS_USERNAME);
	}

	public boolean getContainsEmailCheck() {
		return isEnabled(EXTRA_CHECK_CONTAINS_EMAIL1);
	}

	public void setContainsEmailCheck() {
		setBit(EXTRA_CHECK_CONTAINS_EMAIL1);
	}

	public void clearContainsEmailCheck() {
		clearBit(EXTRA_CHECK_CONTAINS_EMAIL1);
	}

	public boolean getContainsPhoneCheck() {
		return isEnabled(EXTRA_CHECK_CONTAINS_PHONE);
	}

	public void setContainsPhoneCheck() {
		setBit(EXTRA_CHECK_CONTAINS_PHONE);
	}

	public void clearContainsPhoneCheck() {
		clearBit(EXTRA_CHECK_CONTAINS_PHONE);
	}

	public boolean getContainsFullnameCheck() {
		return isEnabled(EXTRA_CHECK_CONTAINS_FULLNAME);
	}

	public void setContainsFullnameCheck() {
		setBit(EXTRA_CHECK_CONTAINS_FULLNAME);
	}

	public void clearContainsFullnameCheck() {
		clearBit(EXTRA_CHECK_CONTAINS_FULLNAME);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
