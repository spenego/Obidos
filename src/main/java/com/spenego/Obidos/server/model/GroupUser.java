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

import java.io.Serializable;

import com.spenego.Obidos.shared.dto.Clearable;

public final class GroupUser extends BaseModel implements Clearable, LimitedUser, Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean deleted;
	private Boolean locked;
	private Boolean passwordChangeRequired;
	private Boolean inGroup;
	private Boolean profilePictureEnabled;
	private String email1;
	private String fullname;
	private String employeeId;
	private String regionId;
	private String jobTitle;
	private String department;
	private String office;
	private String instantMessageId;

	@Override
	public void clear() {
		office = null;
		fullname = email1 = null;
		inGroup = passwordChangeRequired = locked = deleted = profilePictureEnabled = null;
		super.clear();
	}

	@Override
	public String toString() {
		return "Group user " + getName();
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	public void setPasswordChangeRequired(Boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname == null ? null : fullname.trim();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
	    if (o == null) { return false; }
	    if (!(o instanceof GroupUser)) { return false; }

	    final Long id = ((GroupUser) o).getId();

		return (getId() == null && id == null) || (getClass().equals(o.getClass()) && getId() != null && getId().equals(id));
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	public GroupUser() { /* object is typically created via MyBatis */ }

	@Override
	public String getName() {
		return getFullname();
	}

	@Override
	public Integer getVersion() {
		return null;
	}

	@Override
	public void setVersion(Integer version) { /* record has no version column */ }

	@Override
	public Long getUserId() {
		return null;
	}

	@Override
	public Boolean getInGroup() {
		return inGroup;
	}

	@Override
	public void setInGroup(final Boolean val) {
		inGroup = val;
	}

	@Override
	public Boolean getUpdatePermitted() {
		return Boolean.FALSE;
	}

	@Override
	public Boolean getSharePermitted() {
		return Boolean.FALSE;
	}

	@Override
	public Boolean getOwnershipControl() {
		return Boolean.FALSE;
	}

	@Override
	public void setUpdatePermitted(Boolean updatePermitted) { /* needed since we implement LimitedUser, not used in this class */ }

	@Override
	public void setSharePermitted(Boolean sharePermitted) { /* needed since we implement LimitedUser, not used in this class */ }

	@Override
	public void setOwnershipControl(Boolean ownershipControl)  { /* needed since we implement LimitedUser, not used in this class */ }

	@Override
	public Boolean getInContainer() {
		return Boolean.FALSE;
	}

	@Override
	public void setInContainer(Boolean val) { /* needed since we implement LimitedUser, not used in this class */ }

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getInstantMessageId() {
		return instantMessageId;
	}

	public void setInstantMessageId(String instantMessageId) {
		this.instantMessageId = instantMessageId;
	}

	public String getEmail1() {
		return email1;
	}

	public String setEmail1(String email1) {
		this.email1 = email1;
		return email1;
	}

	public Boolean getProfilePictureEnabled() {
		return profilePictureEnabled;
	}

	public void setProfilePictureEnabled(Boolean profilePictureEnabled) {
		this.profilePictureEnabled = profilePictureEnabled;
	}
}
