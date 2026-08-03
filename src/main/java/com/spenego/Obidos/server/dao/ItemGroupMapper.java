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

import com.spenego.Obidos.server.model.ItemGroup;
import com.spenego.Obidos.server.model.ItemGroupExample;

public interface ItemGroupMapper extends Mapper<ItemGroup> {
	Integer countByExample(ItemGroupExample example);
	int deleteByExample(ItemGroupExample example);
	int deleteByPrimaryKey(Long id);
	int insert(ItemGroup r);
	int insertSelective(ItemGroup r);
	List<ItemGroup> selectByExample(ItemGroupExample example);
	ItemGroup selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") ItemGroup r, @Param("example") ItemGroupExample example);
	int updateByExample(@Param("r") ItemGroup r, @Param("example") ItemGroupExample example);
	int updateByPrimaryKeySelective(ItemGroup r);
	int updateByPrimaryKey(ItemGroup r);
}
