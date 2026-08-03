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

import java.util.Date;

import com.spenego.Obidos.shared.HasName;

/**
 * All Obidos model classes implement this interface. Most model classes
 * will have a meaningful implementation of each method in this interface.
 *
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.server.model.HasName
 * @since   Obidos1.0
 *
 */
public interface Model extends HasName {
	Long getId();
	Long getUserId();
	Date getUpdatedAt();
	Date getCreatedAt();
	Integer getVersion();
	void setId(Long id);
	void setCreatedAt(Date createdAt);
	void setUpdatedAt(Date updatedAt);
	void setVersion(Integer version);
}
