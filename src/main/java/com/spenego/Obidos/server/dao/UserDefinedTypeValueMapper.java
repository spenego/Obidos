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

import com.spenego.Obidos.server.model.UserDefinedTypeValue;
import com.spenego.Obidos.server.model.UserDefinedTypeValueExample;

public interface UserDefinedTypeValueMapper extends Mapper<UserDefinedTypeValue> {

	Integer countByExample(UserDefinedTypeValueExample example);
	Integer countItemReferencesByExample(UserDefinedTypeValueExample example);
	int deleteByExample(UserDefinedTypeValueExample example);
	int deleteItemReferencesByExample(UserDefinedTypeValueExample example);
	int deleteByPrimaryKey(Long id);
	int insert(UserDefinedTypeValue r);
	int insertSelective(UserDefinedTypeValue r);
	List<UserDefinedTypeValue> selectByExample(UserDefinedTypeValueExample example);
	UserDefinedTypeValue selectByPrimaryKey(Long id);
	int updateByExampleSelectiveJoin(@Param("r") UserDefinedTypeValue r, @Param("example") UserDefinedTypeValueExample example);
	int updateByExampleSelective(@Param("r") UserDefinedTypeValue r, @Param("example") UserDefinedTypeValueExample example);
	int updateByExample(@Param("r") UserDefinedTypeValue r, @Param("example") UserDefinedTypeValueExample example);
	int updateByPrimaryKeySelective(UserDefinedTypeValue r);
	int updateByPrimaryKey(UserDefinedTypeValue r);
}
