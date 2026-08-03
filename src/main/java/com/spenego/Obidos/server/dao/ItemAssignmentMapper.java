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

import com.spenego.Obidos.server.model.ItemAssignment;
import com.spenego.Obidos.server.model.ItemAssignmentExample;
import com.spenego.Obidos.server.model.UserGroupCombo;

public interface ItemAssignmentMapper extends Mapper<ItemAssignment> {
    Integer countByExample(ItemAssignmentExample example);
    int deleteByExample(ItemAssignmentExample example);
    int deleteGroupByExample(ItemAssignmentExample example);
    int deleteByPrimaryKey(Long id);
    int insert(ItemAssignment r);
    int insertSelective(ItemAssignment r);
    int decrementShareCount(ItemAssignmentExample example);
    int decrementShareCountForUser(ItemAssignmentExample example);
    int incrementByPrimaryKey(Long id);
    int decrementByPrimaryKey(Long id);
    List<ItemAssignment> selectByExample(ItemAssignmentExample example);
    List<ItemAssignment> selectByItemGroupExample(ItemAssignmentExample example);
    ItemAssignment selectByPrimaryKey(Long id);
    ItemAssignment selectByExampleWithDocument(ItemAssignmentExample example);
    int updateByExampleSelective(@Param("r") ItemAssignment r, @Param("example") ItemAssignmentExample example);
    int updateByExample(@Param("r") ItemAssignment r, @Param("example") ItemAssignmentExample example);
    int updateByPrimaryKeySelective(ItemAssignment r);
    int updateByPrimaryKey(ItemAssignment r);
    int resetCountByExample(@Param("example") ItemAssignmentExample example);
    List<UserGroupCombo> selectItemSharesByExample(ItemAssignmentExample example, ObidosRowBounds rowBounds);
    int countItemSharesByExample(ItemAssignmentExample example);
}
