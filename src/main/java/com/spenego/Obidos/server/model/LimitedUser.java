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

package com.spenego.Obidos.server.model;

import java.util.Date;

import com.spenego.Obidos.shared.dto.Clearable;

public interface LimitedUser extends Clearable, ContainerTarget, Model {
	Long	getId();
	Boolean	getDeleted();
	Boolean getLocked();
	Boolean getPasswordChangeRequired();
	Boolean getInGroup();
	Boolean getUpdatePermitted();
	Boolean getSharePermitted();
	Boolean getOwnershipControl();
	Boolean getInContainer();
	Boolean getProfilePictureEnabled();
	Date	getCreatedAt();
	Date	getUpdatedAt();
	String	getFullname();
	String	getEmployeeId();
	String	getRegionId();
	String	getJobTitle();
	String	getDepartment();
	String	getOffice();
	String	getInstantMessageId();
	boolean	equals(Object o);
	boolean	self(Long userId);

	void	setId(Long id);
	void	setDeleted(Boolean deleted);
	void	setLocked(Boolean locked);
	void	setCreatedAt(Date createdAt);
	void	setUpdatedAt(Date updatedAt);
	void	setFullname(String fullname);
	void	setInGroup(Boolean val);
	void	setInContainer(Boolean val);
	void	setProfilePictureEnabled(Boolean val);
	void	setPasswordChangeRequired(Boolean passwordChangeRequired);
	void	setUpdatePermitted(Boolean updatePermitted);
	void	setSharePermitted(Boolean sharePermitted);
	void	setOwnershipControl(Boolean ownershipControl);
	void	setEmployeeId(String employeeId);
	void 	setRegionId(String regionId);
	void	setJobTitle(String jobTitle);
	void	setDepartment(String department);
	void	setOffice(String office);
	void	setInstantMessageId(String instantMessageId);
}
