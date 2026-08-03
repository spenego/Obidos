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

import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.CapabilityExample;

public interface CapabilityMapper extends Mapper<Capability> {
	int countByExample(CapabilityExample example);
	int deleteByExample(CapabilityExample example);
	int deleteByPrimaryKey(Long id);
	int insert(Capability o);
	int insertSelective(Capability r);
	List<Capability> selectByExample(CapabilityExample example);
	Capability selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") Capability r, @Param("example") CapabilityExample example);
	int updateByExample(@Param("r") Capability r, @Param("example") CapabilityExample example);
	int updateByPrimaryKeySelective(Capability r);
	int updateByPrimaryKey(Capability r);
}
