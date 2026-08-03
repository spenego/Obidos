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

import com.spenego.Obidos.server.model.SmtpConfig;
import com.spenego.Obidos.server.model.SmtpConfigExample;

public interface SmtpConfigMapper extends Mapper<SmtpConfig> {
	int countByExample(SmtpConfigExample example);
	int deleteByExample(SmtpConfigExample example);
	int deleteByPrimaryKey(Long id);
	int insert(SmtpConfig r);
	int insertSelective(SmtpConfig r);
	List<SmtpConfig> selectByExample(SmtpConfigExample example);
	SmtpConfig selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") SmtpConfig r, @Param("example") SmtpConfigExample example);
	int updateByExample(@Param("r") SmtpConfig r, @Param("example") SmtpConfigExample example);
	int updateByPrimaryKeySelective(SmtpConfig r);
	int updateByPrimaryKey(SmtpConfig r);
	List<SmtpConfig> selectByExample(SmtpConfigExample example, ObidosRowBounds rowBounds);
}
