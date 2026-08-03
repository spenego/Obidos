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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.spenego.Obidos.shared.Selectable;

/**
 * The {@code LimitedUserDTO} class represents other users in the Obidios system.  This
 * contrasts with {@code UserDTO} with represents the currently logged-in user. This
 * class only contains enough information about the other user to supply identity
 * information so as to not leak any potentially personal information.
 *
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.shared.dto.UserDTO
 * @since   Obidos1.0
*/

public class LimitedUserDTO implements HasId, Clearable, Selectable, Serializable
{
	private static final long serialVersionUID = 1L;

	private Long   			id;
	private String 			fullname;
	private String 			office;

	private Boolean			locked;
	private Boolean			inGroup;
	private Boolean			inContainer;
	private Boolean			selected;	// set by Client to indicate that the user has selected this user
	private Boolean			updatePermitted;
	private Boolean			sharePermitted;
	private Boolean			ownershipControl;
	private Boolean			profilePictureEnabled;
	private byte[]			profilePic;
	@NotNull(message="Primary email Address must be specified")
	@NotBlank(message="Primary email Address may not be blank")
	@Email(message="Invalid email Address")
	private String 			email1;
	@Email(message="Invalid email Address")
	private String 			email2;
	@Email(message="Invalid email Address")
	private String 			email3;
	private String 			phone;
	private String 			mobile1;
	private String 			mobile2;
	private String 			mobile3;
	private String 			twitter;
	private String 			facebook;
	private String			employeeId;
	private String			regionId;
	private String			jobTitle;
	private String			department;
	private String			instantMessageId;

	public LimitedUserDTO() {}
	public LimitedUserDTO(final Boolean locked) {
		this.locked = locked;
	}

	public LimitedUserDTO(final Long id) {
		this.id = id;
	}

	@Override
	public void clear() {
		id = null;
		email1 = email2 = email3 = phone = mobile1 = mobile2 = mobile3 = twitter = facebook = employeeId = regionId = jobTitle = department = instantMessageId = fullname = office = null;
		locked = inGroup = inContainer = selected = null;
	}

	@Override
	public final Long getId() {
		return id;
	}
	public final void setId(Long id) {
		this.id = id;
	}
	public final String getFullname() {
		return fullname;
	}
	public final void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public final Boolean getLocked() {
		return locked;
	}

	public final void setLocked(Boolean locked) {
		this.locked = locked;
	}

	/**
	 * Originally named isLocked. We can't use that name since DTO conversion via Dozer looks for it before looking for getLocked.
	 *
	 * @return
	 */
	public final boolean lockedIsTrue() {
		return locked != null && locked.booleanValue();
	}

	public Boolean getInGroup() {
		return inGroup;
	}

	public void setInGroup(Boolean inContainer) {
		this.inGroup = inContainer;
	}

	@Override
	public Boolean getSelected() {
		return selected;
	}

	@Override
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getInContainer() {
		return inContainer;
	}

	public void setInContainer(Boolean inContainer) {
		this.inContainer = inContainer;
	}

	public Boolean getUpdatePermitted() {
		return updatePermitted;
	}

	public void setUpdatePermitted(Boolean updatePermitted) {
		this.updatePermitted = updatePermitted;
	}

	public Boolean getSharePermitted() {
		return sharePermitted;
	}

	public void setSharePermitted(Boolean sharePermitted) {
		this.sharePermitted = sharePermitted;
	}

	public Boolean getOwnershipControl() {
		return ownershipControl;
	}

	public void setOwnershipControl(Boolean ownershipControl) {
		this.ownershipControl = ownershipControl;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}
	public byte[] getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}
	public String getEmail2() {
		return email2;
	}
	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	public String getEmail3() {
		return email3;
	}
	public void setEmail3(String email3) {
		this.email3 = email3;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile1() {
		return mobile1;
	}
	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}
	public String getMobile2() {
		return mobile2;
	}
	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
	public String getMobile3() {
		return mobile3;
	}
	public void setMobile3(String mobile3) {
		this.mobile3 = mobile3;
	}
	public String getTwitter() {
		return twitter;
	}
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
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
	public String getInstantMessageId() {
		return instantMessageId;
	}
	public void setInstantMessageId(String instantMessageId) {
		this.instantMessageId = instantMessageId;
	}
	public Boolean getProfilePictureEnabled() {
		return profilePictureEnabled;
	}
	public void setProfilePictureEnabled(Boolean profilePictureEnabled) {
		this.profilePictureEnabled = profilePictureEnabled;
	}
}
