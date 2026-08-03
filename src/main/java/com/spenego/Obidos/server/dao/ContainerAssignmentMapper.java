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

import com.spenego.Obidos.server.model.ContainerAssignment;
import com.spenego.Obidos.server.model.ContainerAssignmentExample;
import com.spenego.Obidos.server.model.UserGroupCombo;

public interface ContainerAssignmentMapper extends Mapper<ContainerAssignment> {
	Integer countByExample(ContainerAssignmentExample example);
	int deleteByExample(ContainerAssignmentExample example);
	int deleteByPrimaryKey(Long id);
	int insert(ContainerAssignment r);
	int insertSelective(ContainerAssignment r);
	List<ContainerAssignment> selectByExample(ContainerAssignmentExample example);
	List<ContainerAssignment> selectByContainerGroupExample(ContainerAssignmentExample example);
	ContainerAssignment selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") ContainerAssignment r, @Param("example") ContainerAssignmentExample example);
	int updateByExample(@Param("r") ContainerAssignment r, @Param("example") ContainerAssignmentExample example);
	int updateByPrimaryKeySelective(ContainerAssignment r);
	int updateByPrimaryKey(ContainerAssignment r);
    int incrementByPrimaryKey(Long id);
    int decrementShareCountByExample(ContainerAssignmentExample example);
    int decrementShareCountByGroupExample(ContainerAssignmentExample example);
    void deleteArtifacts(Long containerId);
    List<UserGroupCombo> selectContainerSharesByExample(ContainerAssignmentExample example, ObidosRowBounds rowBounds);
    int countContainerSharesByExample(ContainerAssignmentExample example);
	int resetCountByExample(@Param("example") ContainerAssignmentExample example);
}
