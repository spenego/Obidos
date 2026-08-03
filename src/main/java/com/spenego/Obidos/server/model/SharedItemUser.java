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
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;

import com.spenego.Obidos.shared.exceptions.ServerSideException;

public class SharedItemUser extends BaseModel implements PublicKeyCryptoProperties, LimitedUser, Serializable {
	private static final long serialVersionUID = 1L;

	private Long groupId;			// used when getting a list of users, specifies that you want users not in this group
	private Boolean deleted;
	private Boolean locked;
	private Boolean administrator;
	private Boolean passwordChangeRequired;
	private Boolean updatePermitted;
	private Boolean sharePermitted;
	private Boolean ownershipControl;
	private Boolean inGroup;
	private Boolean profilePictureEnabled;
	private Date	lastLogin;
	private Date shareExpiresAt;
	private String email1;
	private String fullname;
	private String employeeId;
	private String regionId;
	private String jobTitle;
	private String department;
	private String office;
	private String instantMessageId;
	private byte[] profilePic;

	// These crypto fields are required when a user updates the fields of a shared item. Those fields need to be re-encrypted for shared items.
	private String salt;
	private String nonce;
	private String authurn;
	private String publickey;
	private String privatekey;
	private byte[] decodedSalt;
	private byte[] decodedNonce;
	private byte[] decodedPrivateKey;
	private byte[] decodedPublicKey;
	private static Decoder decoder = Base64.getDecoder();

	public SharedItemUser(final Long id) {
		super(id);
	}

	public SharedItemUser(final Boolean administrator) {
		this.administrator = administrator;
	}

	public SharedItemUser(final Boolean administrator, final Boolean locked, final Boolean deleted) {
		this.administrator = administrator;
		this.locked = locked;
		this.deleted = deleted;
	}

	public SharedItemUser(final Long id, final Integer version) {
		super(id, version);
		this.lastLogin = new Date();
	}

	public SharedItemUser(final Long id, final Integer version, final Boolean passwordChangeRequired) {
		super(id, version);
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public SharedItemUser(final Long id, final Integer version, final Boolean deleted, final Boolean locked) {
		super(id, version);
		this.deleted = deleted;
		this.locked = locked;
	}

	public SharedItemUser(final Long id, final Integer version, final String publickey, final String privatekey, final String nonce, final String salt) {
		super(id, version);
		this.publickey = publickey;
		this.privatekey = privatekey;
		this.nonce = nonce;
		this.salt = salt;
	}

	// Creates a user with only fields that a regular user may modify being set. Sanitized User Copy constructor.
	// email1 omitted explicitly
	public SharedItemUser(final SharedItemUser user) {
		super(user.getId());
		this.instantMessageId = user.instantMessageId;
		this.office = user.office;
		this.profilePic = user.profilePic;
		// this.fullname = user.fullname; // we do not permit the user to update their own name
	}

	@Override
	public void clear() {
		// Since this object is cached, we can not queue it for clearing during a convert call
	}

	protected boolean empty(final String v) {
		return v != null && v.isEmpty();
	}

	public boolean emptyFieldsExist() {
		return empty(fullname);
	}

	public SharedItemUser nullifyEmptyFields(final SharedItemUser user) {
		if (empty(user.fullname))	{ fullname = null; }
		return this;
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

	/**
	 * Originally named isLocked. We can't use that name since DTO conversion via Dozer looks for it before looking for getLocked.
	 *
	 * @return
	 */
	public final boolean lockedIsTrue() {
		return locked != null && locked.booleanValue();
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Boolean getAdministrator() {
		return administrator;
	}

	public void setAdministrator(Boolean administrator) {
		this.administrator = administrator;
	}

	public Boolean getPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	public void setPasswordChangeRequired(Boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1 == null ? null : email1.trim();
	}

	public String getFullname() {
		return fullname;
	}

	@Override
	public String toString() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname == null ? null : fullname.trim();
	}

	public boolean isAdmin() {
		return getAdministrator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
	    if (o == null) { return false; }
	    if (!(o instanceof SharedItemUser)) { return false; }

	    final Long id = ((SharedItemUser) o).getId();

		return (getId() == null && id == null) || (getClass().equals(o.getClass()) && getId() != null && getId().equals(id));
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	public SharedItemUser() { }

	@Override
	public String getName() {
		return fullname;
	}

	/**
	 * Returns true if true if the user specified by userId is the same as this user.
	 *
	 * @param val
	 * @return
	 */
	@Override
	public boolean self(final Long userId) {
		return getId().equals(userId);
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Override
	public Boolean getInGroup() {
		return inGroup;
	}

	@Override
	public void setInGroup(final Boolean val) {
		inGroup = val;
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

	public Date getShareExpiresAt() {
		return shareExpiresAt;
	}

	public void setShareExpiresAt(Date shareExpiresAt) {
		this.shareExpiresAt = shareExpiresAt;
	}

	@Override
	public Boolean getInContainer() {
		return Boolean.FALSE;
	}

	@Override
	public void setInContainer(Boolean val)  { /* this method is required because we implement LimitedUser, but it's not really used here */ }

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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public byte[] getDecodedSalt() {
		if (decodedSalt == null && salt != null) {
			decodedSalt = decoder.decode(salt);
		}
		return decodedSalt;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt == null ? null : salt.trim();
		decodedSalt = null;	// clear cache
	}

	public String getNonce() {
		return nonce;
	}

	@Override
	public byte[] getDecodedNonce() {
		if (decodedNonce == null) {
			if (nonce == null) { throw new ServerSideException("Nonce is null!"); }
			decodedNonce = decoder.decode(nonce);
		}

		return decodedNonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce == null ? null : nonce.trim();
	}

	public String getAuthurn() {
		return authurn;
	}

	public void setAuthurn(String authurn) {
		this.authurn = authurn;
	}

	public String getPublickey() {
		return publickey;
	}

	@Override
	public byte[] getDecodedPublicKey() {
		if (decodedPublicKey == null) {
			if (publickey == null)	{ throw new ServerSideException("No public key was available."); }
			decodedPublicKey = decoder.decode(publickey);
		}
		return decodedPublicKey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	@Override
	public byte[] getDecodedPrivateKey() {
		if (decodedPrivateKey == null) {
			if (privatekey == null)	{ throw new ServerSideException("No private key was available."); }
			decodedPrivateKey = decoder.decode(privatekey);
		}
		return decodedPrivateKey;
	}

	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}

	public byte[] getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	public Boolean getProfilePictureEnabled() {
		return profilePictureEnabled;
	}

	public void setProfilePictureEnabled(Boolean profilePictureEnabled) {
		this.profilePictureEnabled = profilePictureEnabled;
	}
}
