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

import org.apache.ibatis.annotations.Param;

import com.spenego.Obidos.server.model.Container;
import com.spenego.Obidos.server.model.ContainerExample;

public interface ContainerMapper extends Mapper<Container> {

	Integer countByExample(ContainerExample example);
	int deleteByExample(ContainerExample example);
	int deleteByPrimaryKey(Long id);
	int insert(Container o);
	int insertSelective(Container o);
	List<Container> selectByExample(ContainerExample example);
	Container selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") Container r, @Param("example") ContainerExample example);
	int updateByExample(@Param("r") Container r, @Param("example") ContainerExample example);
	int updateByPrimaryKeySelective(Container r);
	int updateByPrimaryKey(Container r);
	int hasGroupOwnershipControl(@Param("uid") Long userId, @Param("cid") Long containerId);

	List<Container> selectByExample(ContainerExample example, ObidosRowBounds rowBounds);
}
