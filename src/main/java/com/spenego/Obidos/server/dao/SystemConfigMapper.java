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

import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.SystemConfigExample;

public interface SystemConfigMapper extends Mapper<SystemConfig> {
	int countByExample(SystemConfigExample example);
	int deleteByExample(SystemConfigExample example);
	int deleteByPrimaryKey(Long id);
	int insert(SystemConfig r);
	int insertSelective(SystemConfig r);
	List<SystemConfig> selectByExample(SystemConfigExample example);
	SystemConfig selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") SystemConfig r, @Param("example") SystemConfigExample example);
	int updateByExample(@Param("r") SystemConfig r, @Param("example") SystemConfigExample example);
	int updateByPrimaryKeySelective(SystemConfig r);
	int updateByPrimaryKey(SystemConfig r);
}
