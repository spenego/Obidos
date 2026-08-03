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

import com.spenego.Obidos.server.model.ComplexityRequirements;
import com.spenego.Obidos.server.model.ComplexityRequirementsExample;

public interface ComplexityRequirementsMapper  extends Mapper<ComplexityRequirements> {
	int countByExample(ComplexityRequirementsExample example);

	int deleteByExample(ComplexityRequirementsExample example);
	int deleteByPrimaryKey(Long id);
	int insert(ComplexityRequirements r);
	int insertSelective(ComplexityRequirements r);
	List<ComplexityRequirements> selectByExample(ComplexityRequirementsExample example);
	ComplexityRequirements selectByPrimaryKey(Long id);
	int updateByExampleSelective(@Param("r") ComplexityRequirements r, @Param("example") ComplexityRequirementsExample example);
	int updateByExample(@Param("r") ComplexityRequirements r, @Param("example") ComplexityRequirementsExample example);
	int updateByPrimaryKeySelective(ComplexityRequirements r);
	int updateByPrimaryKey(ComplexityRequirements r);
}
