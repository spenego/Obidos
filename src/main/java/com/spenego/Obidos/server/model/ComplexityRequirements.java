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

package com.spenego.Obidos.server.model;

import java.io.Serializable;

public final class ComplexityRequirements extends NamedModel implements Serializable {
	private Byte type;
	private Integer minimumLength;
	private Integer minimumUppercase;
	private Integer minimumLowercase;
	private Integer minimumSpecial;
	private Integer minimumNumbers;
	private Integer minimumEntropy;
	private Integer maxAgeInDays;
	private Integer minAgeInSecondsBeforeReset; // password may not be reset
												// until it is at least this
												// many seconds old
	private Long extraChecksEnabled;

	private static final long serialVersionUID = 1L;


	public ComplexityRequirements() {}

	public ComplexityRequirements(Long id, User admin, String name, int version, byte type, int minimumLength, int minimumEntropy, final long extraChecksEnabled) {
		super(id, admin.getId(), name);
		this.type = type;
		this.minimumLength = minimumLength;
		this.minimumEntropy = minimumEntropy;
		this.extraChecksEnabled = extraChecksEnabled;
		setVersion(version);
	}

	public ComplexityRequirements(Long id, User admin, String name, int version, byte type,
			int minimumLength,
			int minimumEntropy,
			int minimumLowercase,
			int minimumUppercase,
			int minimumSpecial,
			int maxAgeInDays,
			int minimumNumbers,
			final Integer minAgeInSecondsBeforeReset,
			final long extraChecksEnabled)
	{
		super(id, admin.getId(), name);

		this.type = type;
		this.minimumLength = minimumLength;
		this.minimumEntropy = minimumEntropy;
		this.extraChecksEnabled = extraChecksEnabled;
		this.minimumLowercase = minimumLowercase;
		this.minimumUppercase = minimumUppercase;
		this.minimumSpecial = minimumSpecial;
		this.maxAgeInDays = maxAgeInDays;
		this.minimumNumbers = minimumNumbers;
		this.minAgeInSecondsBeforeReset = minAgeInSecondsBeforeReset;

		setVersion(version);

	}

	@Override
	public void clear() {
		minAgeInSecondsBeforeReset = null;
		maxAgeInDays = null;
		minimumEntropy = null;
		minimumNumbers = null;
		minimumSpecial = null;
		minimumLowercase = null;
		minimumUppercase = null;
		minimumLength = null;
		extraChecksEnabled = null;
		type = null;
		super.clear();
	}

    public Byte getType() {
        return type;
    }

	public void setType(Byte type) {
		this.type = type;
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
}
