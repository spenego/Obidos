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

package com.spenego.Obidos.server.validators;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * A class to validate any bean with constraints specified with annotations
 *
 * @author spgdev@spenego.com - Apr 8, 2017
 *
 */
public final class ValidateBean {
	public ValidateBean() { }

	public static <T> void validate(final T bean) {
		try(ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
			for (final ConstraintViolation<T> violation : vf.getValidator().validate(bean)) {
				if (violation.getMessage() != null) { // throw exception at first violation
					throw new ServerSideException(violation.getPropertyPath() + ": " + violation.getMessage());
				}
			}
		}
	}
}
