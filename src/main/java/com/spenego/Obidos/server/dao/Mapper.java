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

public interface Mapper<T> {
	int insert(T t);
	T selectByPrimaryKey(Long id);
	int updateByPrimaryKey(T o);
	int updateByPrimaryKeySelective(T r);
	int deleteByPrimaryKey(Long id);
	<X> List<T> selectByExample(X xc, ObidosRowBounds rb);
	<X> Integer countByExample(X example);
}

