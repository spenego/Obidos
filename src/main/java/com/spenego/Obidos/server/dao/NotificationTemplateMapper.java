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

import com.spenego.Obidos.server.model.NotificationTemplate;
import com.spenego.Obidos.server.model.NotificationTemplateExample;

public interface NotificationTemplateMapper extends Mapper<NotificationTemplate> {
	int countByExample(NotificationTemplateExample example);
	int deleteByExample(NotificationTemplateExample example);
	int deleteByPrimaryKey(Long id);
	int insert(NotificationTemplate r);
	int insertSelective(NotificationTemplate r);
	List<NotificationTemplate> selectByExampleWithBLOBs(NotificationTemplateExample example);
	List<NotificationTemplate> selectByExample(NotificationTemplateExample example);
	NotificationTemplate selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") NotificationTemplate r, @Param("example") NotificationTemplateExample example);
	int updateByExampleWithBLOBs(@Param("r") NotificationTemplate r, @Param("example") NotificationTemplateExample example);
	int updateByExample(@Param("r") NotificationTemplate r, @Param("example") NotificationTemplateExample example);
	int updateByPrimaryKeySelective(NotificationTemplate r);
	int updateByPrimaryKeyWithBLOBs(NotificationTemplate r);
	int updateByPrimaryKey(NotificationTemplate r);
}
