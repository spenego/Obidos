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

import com.spenego.Obidos.server.model.GroupMember;
import com.spenego.Obidos.server.model.GroupMemberExample;

public interface GroupMemberMapper extends Mapper<GroupMember> {
	Integer countByExample(GroupMemberExample example);
	int deleteByExample(GroupMemberExample example);
	int deleteByPrimaryKey(Long id);
	int insert(GroupMember o);
	int insertSelective(GroupMember r);
	List<GroupMember> selectByExample(GroupMemberExample example);
	GroupMember selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") GroupMember r, @Param("example") GroupMemberExample example);
	int updateByExample(@Param("r") GroupMember r, @Param("example") GroupMemberExample example);
	int updateByPrimaryKeySelective(GroupMember r);
	int updateByPrimaryKey(GroupMember r);
	List<GroupMember> selectByExample(GroupMemberExample example, ObidosRowBounds rowbounds);
}
