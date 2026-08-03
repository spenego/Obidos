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

package com.spenego.Obidos.server.utils;

import java.util.Date;

import org.dozer.CustomConverter;
import org.dozer.MappingException;

import com.spenego.Obidos.shared.dto.ItemExpiration;

public class CustomDozerConverter implements CustomConverter {
	@Override
	public Object convert(final Object existingDestinationFieldValue, final Object sourceFieldValue, final Class<?> destinationClass, final Class<?> sourceClass) {
		if (sourceFieldValue == null) {
			return null;
		}

		if (sourceFieldValue instanceof Date && destinationClass.equals(ItemExpiration.class)) {
			if (existingDestinationFieldValue == null) {
				return new ItemExpiration((Date) sourceFieldValue);
			}

			final ItemExpiration dest = (ItemExpiration) existingDestinationFieldValue;
			dest.setExpiresAt((Date) sourceFieldValue);
			return dest;
		} else if (sourceFieldValue instanceof ItemExpiration && destinationClass.equals(Date.class)) {
				if (existingDestinationFieldValue == null) {
					return ((ItemExpiration) sourceFieldValue).getExpiresAt();
				}
				throw new MappingException("Unexpected destination in mapper");
		} else {
			throw new MappingException("Converter TestCustomConverter "
					+ "used incorrectly. Arguments passed in were:"
					+ existingDestinationFieldValue + " and " + sourceFieldValue);
		}
	}
}
