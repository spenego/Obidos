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

import com.spenego.Obidos.server.model.Group;
import com.spenego.Obidos.server.model.GroupExample;

public interface GroupMapper extends Mapper<Group> {

	Integer countByExample(GroupExample example);
	int deleteByPrimaryKey(Long id);
	int insert(Group o);

	List<Group> selectByExample(GroupExample example);
	Group selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Group r);
	int updateByPrimaryKey(Group r);

	List<Group>		selectByExample(GroupExample example, ObidosRowBounds rowBounds);
	List<Group>		selectItemGroupsByExample(GroupExample example, ObidosRowBounds rowBounds);
	List<Long>		selectItemGroupIdsByExample(GroupExample example);
	Integer			countItemGroupsByExample(GroupExample example);
	List<Group>		selectGroupsSharingContainerByExample(GroupExample example, ObidosRowBounds rowBounds);
	List<Long>		selectGroupIdsSharingContainerByExample(GroupExample example, ObidosRowBounds rowBounds);
	Integer			countGroupsSharingContainerByExample(GroupExample example);
}
