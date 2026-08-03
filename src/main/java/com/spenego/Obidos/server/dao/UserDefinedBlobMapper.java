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

import com.spenego.Obidos.server.model.UserDefinedBlob;
import com.spenego.Obidos.server.model.UserDefinedBlobExample;

public interface UserDefinedBlobMapper extends Mapper<UserDefinedBlob> {
	Integer countByExample(UserDefinedBlobExample example);
	int deleteByExample(UserDefinedBlobExample example);
	int deleteByPrimaryKey(Long id);
	int insert(UserDefinedBlob r);
	int insertSelective(UserDefinedBlob r);

	List<UserDefinedBlob> selectByExampleWithBLOBs(UserDefinedBlobExample example);
	List<UserDefinedBlob> selectByExample(UserDefinedBlobExample example);
	UserDefinedBlob selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") UserDefinedBlob r, @Param("example") UserDefinedBlobExample example);
	int updateByExampleWithBLOBs(@Param("r") UserDefinedBlob r, @Param("example") UserDefinedBlobExample example);
	int updateByExample(@Param("r") UserDefinedBlob r, @Param("example") UserDefinedBlobExample example);
	int updateByPrimaryKeySelective(UserDefinedBlob r);
	int updateByPrimaryKeyWithBLOBs(UserDefinedBlob r);
}
