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

import com.spenego.Obidos.server.model.ContainerGroupAssignment;
import com.spenego.Obidos.server.model.ContainerGroupAssignmentExample;

public interface ContainerGroupAssignmentMapper extends Mapper<ContainerGroupAssignment> {
	int countByExample(ContainerGroupAssignmentExample example);
	int deleteByExample(ContainerGroupAssignmentExample example);
	int deleteByPrimaryKey(Long id);
	int insert(ContainerGroupAssignment r);
	int insertSelective(ContainerGroupAssignment r);
	List<ContainerGroupAssignment> selectByExample(ContainerGroupAssignmentExample example);
	ContainerGroupAssignment selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") ContainerGroupAssignment r, @Param("example") ContainerGroupAssignmentExample example);
	int updateByExample(@Param("r") ContainerGroupAssignment r, @Param("example") ContainerGroupAssignmentExample example);
	int updateByPrimaryKeySelective(ContainerGroupAssignment r);
	int updateByPrimaryKey(ContainerGroupAssignment r);
	int hasGroupUpdate(@Param("uid") Long uid, @Param("cid") Long cid);
	int hasGroupAdd(@Param("uid") Long uid, @Param("cid") Long cid);
}
