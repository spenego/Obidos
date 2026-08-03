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

import java.util.List;

import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.SharedItemExample;

public interface SharedItemMapper {
	Integer countByExample(SharedItemExample example);
	Integer countByGroupExample(SharedItemExample example);
//	SharedItem selectByPrimaryKey(Long id);
	List<SharedItem> selectByExample	 (SharedItemExample example, ObidosRowBounds rowBounds);
	List<SharedItem> selectTerseByExample(SharedItemExample example, ObidosRowBounds rowBounds);
	List<SharedItem> selectByGroupExample(SharedItemExample example, ObidosRowBounds rowBounds);
}
