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

package com.spenego.Obidos.server.dao;

import org.apache.ibatis.session.RowBounds;

public final class ObidosRowBounds extends RowBounds {
	  public ObidosRowBounds(Integer offset, Integer limit) {
		  super(offset == null ? RowBounds.NO_ROW_OFFSET : offset,  limit == null ? RowBounds.NO_ROW_LIMIT : limit);
	  }
}
