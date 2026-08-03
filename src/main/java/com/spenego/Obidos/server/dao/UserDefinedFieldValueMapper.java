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

import com.spenego.Obidos.server.model.UserDefinedFieldValue;
import com.spenego.Obidos.server.model.UserDefinedFieldValueExample;

public interface UserDefinedFieldValueMapper extends Mapper<UserDefinedFieldValue> {

	Integer countByExample(UserDefinedFieldValueExample example);
	int deleteByExample(UserDefinedFieldValueExample example);
	int deleteByPrimaryKey(Long id);
	int insert(UserDefinedFieldValue r);
	int insertSelective(UserDefinedFieldValue r);
	List<UserDefinedFieldValue> selectByExample(UserDefinedFieldValueExample example);
	List<UserDefinedFieldValue> selectByItemExample(UserDefinedFieldValueExample example);
	UserDefinedFieldValue selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") UserDefinedFieldValue r,
			@Param("example") UserDefinedFieldValueExample example);
	int updateByExample(@Param("r") UserDefinedFieldValue r,
			@Param("example") UserDefinedFieldValueExample example);
	int updateByPrimaryKeySelective(UserDefinedFieldValue r);
	int updateByPrimaryKey(UserDefinedFieldValue r);
}
