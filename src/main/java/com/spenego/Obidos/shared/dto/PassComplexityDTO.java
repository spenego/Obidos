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

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;

import java.io.Serializable;

import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.exceptions.ObidosException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class PassComplexityDTO implements Clearable, Serializable {
	private static final long serialVersionUID = 1L;

	private ComplexityRequirementsDTO passwordComplexityRequirements;
	private ComplexityRequirementsDTO passphraseComplexityRequirements;

	public PassComplexityDTO() {
	}

	public PassComplexityDTO(final ComplexityRequirementsDTO passwordComplexityRequirements, final ComplexityRequirementsDTO passphraseComplexityRequirements) {
		this.passwordComplexityRequirements = passwordComplexityRequirements;
		this.passphraseComplexityRequirements = passphraseComplexityRequirements;
	}

	@Override
	public void clear() {
		if (passphraseComplexityRequirements != null) {
			passphraseComplexityRequirements.clear();
			passphraseComplexityRequirements = null;
		}
		if (passwordComplexityRequirements != null) {
			passwordComplexityRequirements.clear();
			passphraseComplexityRequirements = null;
		}
	}

	public ComplexityRequirementsDTO get(byte type) {
		if (PASSPHRASE_COMPLEXITY_REQUIREMENTS != type && PASSWORD_COMPLEXITY_REQUIREMENTS != type) {
			throw new ServerSideException("Unknown pass type");
		}
		return type == PASSPHRASE_COMPLEXITY_REQUIREMENTS ? passphraseComplexityRequirements : passwordComplexityRequirements;
	}

	public ComplexityRequirementsDTO getPasswordComplexityRequirements() {
		return passwordComplexityRequirements;
	}

	public void setPasswordComplexityRequirements(ComplexityRequirementsDTO passwordComplexityRequirements) {
		this.passwordComplexityRequirements = passwordComplexityRequirements;
	}

	public ComplexityRequirementsDTO getPassphraseComplexityRequirements() {
		return passphraseComplexityRequirements;
	}

	public void setPassphraseComplexityRequirements(ComplexityRequirementsDTO passphraseComplexityRequirements) {
		this.passphraseComplexityRequirements = passphraseComplexityRequirements;
	}

	private static void validate(Integer min, Integer val, String error)
	{
		if (val != null && (val < min))
		{
			throw new ObidosException(error + min);
		}
	}

	public void validate() throws ObidosException {
		validate(ObidosConstants.ALLOWABLE_MIN_PASSWORD_LENGTH,		passwordComplexityRequirements.getMinimumLength(), "Password length can not be shorter than ");
		validate(ObidosConstants.PASSWORD_ENTROPY_STRONG,			passwordComplexityRequirements.getMinimumEntropy(), "Password entropy can not be less than ");
		validate(ObidosConstants.ALLOWABLE_MIN_PASSPHRASE_LENGTH,	passphraseComplexityRequirements.getMinimumLength(), "Passphrase length can not be shorter than ");
		validate(ObidosConstants.PASSPHRASE_ENTROPY_STRONG,			passphraseComplexityRequirements.getMinimumEntropy(), "Passphrase entropy can not be less than ");
	}
}
