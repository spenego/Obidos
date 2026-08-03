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

import com.spenego.Obidos.server.model.UserDefinedField;
import com.spenego.Obidos.server.model.UserDefinedFieldExample;

public interface UserDefinedFieldMapper extends Mapper<UserDefinedField> {
	Integer countByExample(UserDefinedFieldExample example);
	int deleteByExample(UserDefinedFieldExample example);
	int deleteByPrimaryKey(Long id);
	int insert(UserDefinedField r);
	int insertSelective(UserDefinedField r);

	List<UserDefinedField> selectByExample(UserDefinedFieldExample example);
	UserDefinedField selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") UserDefinedField r, @Param("example") UserDefinedFieldExample example);
	int updateByExample(@Param("r") UserDefinedField r, @Param("example") UserDefinedFieldExample example);
	int updateByPrimaryKeySelective(UserDefinedField r);
	int updateByPrimaryKey(UserDefinedField r);
}
