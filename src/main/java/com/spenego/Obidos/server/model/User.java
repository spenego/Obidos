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

import static com.spenego.Obidos.shared.dto.CapabilityDTO.ALL_CAPABILTIES;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CHANGE_ADMIN_CREDENTIALS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CHANGE_USER_CREDENTIALS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_ADMIN;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_GLOBAL_GROUPS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_GLOBAL_TEMPLATES;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.CREATE_USER;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.DELETE_ADMIN;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.DELETE_USER;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.LOCK_ADMIN;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.LOCK_USER;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.MODIFY_EMAIL_TEMPLATES;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.MODIFY_SETTINGS;
import static com.spenego.Obidos.shared.dto.CapabilityDTO.ROOT_ADMIN;

import java.io.Serializable;
import java.util.Date;

import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.HasId;
import com.spenego.Obidos.shared.dto.HasSecurityClearance;
import com.spenego.Obidos.shared.dto.SecurityClassificationDTO;
import com.spenego.Obidos.shared.dto.UserDTO;

public class User extends SharedItemUser implements HasId, HasOwner, ContainerTarget, LimitedUser, HasSecurityClearance, Serializable {
	private static final long serialVersionUID = 1L;
	private Boolean twoFARequired;
	private Boolean twoFAPasswordResetEnabled;
	private Integer hideItemDelay;				// time in seconds until item contents are hidden
	private Integer defaultPage;
	private Integer securityClearance;
	private Integer loginCount;
	private Integer unsuccessfulLoginAttempts;
	private Long    rawCapabilities;
	private Long	preferenceFlags;
	private Long	minimumPasswordAge;
	private Long	maximumPasswordAge;
	private Date	lastLoginViaCookie;
	private Date	lastLoginViaCredentials;
	private Date	lastPasswordResetTime;
	private String username;
	private String authSource;
	private String twoFactorAuthSecret;
	private String email2;
	private String email3;
	private String phone;
	private String mobile1;
	private String mobile2;
	private String mobile3;
	private String twitter;
	private String facebook;
	private String comments;
	private String passwordDigest;
	private String lastPasswordDigest1;
	private String lastPasswordDigest2;
	private String lastPasswordDigest3;
	private long lastActivityTime;

	@Override
	public void clear() {
		// Since this object is cached, we can not queue it for clearing during a convert call
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username == null ? null : username.trim();
	}

	public String getAuthSource() {
		return authSource;
	}

	public void setAuthSource(String authSource) {
		this.authSource = authSource == null ? null : authSource.trim();
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2 == null ? null : email2.trim();
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3 == null ? null : email3.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1 == null ? null : mobile1.trim();
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2 == null ? null : mobile2.trim();
	}

	public String getMobile3() {
		return mobile3;
	}

	public void setMobile3(String mobile3) {
		this.mobile3 = mobile3 == null ? null : mobile3.trim();
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter == null ? null : twitter.trim();
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook == null ? null : facebook.trim();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments == null ? null : comments.trim();
	}

	public String getPasswordDigest() {
		return passwordDigest;
	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest == null ? null : passwordDigest.trim();
	}

	@Override
	public boolean isAdmin() {
		return getAdministrator() != null && getAdministrator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
	    if (o == null) { return false; }
	    if (!(o instanceof User)) { return false; }

	    final Long id = ((User) o).getId();

		return (getId() == null && id == null) || (getClass().equals(o.getClass()) && getId() != null && getId().equals(id));
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	public User() {
		hideItemDelay = 10;
		defaultPage = 0;
	}

	public User(final String username) {
		this.username = username;
	}

	public User(final Boolean administrator) {
		super(administrator);
	}

	public User(final Boolean administrator, final Boolean locked, final Boolean deleted) {
		super(administrator, locked, deleted);
	}

	public User(final Long id, final String username) {
		super(id);
		this.username = username;
		hideItemDelay = 10;
		defaultPage = 0;
	}

	public User(final Long id, final String username, final Integer version, final Boolean deleted, final Boolean locked) {
		super(id, version, deleted, locked);
		this.username = username;
	}

	public User(final Long id, final Integer version, final String publickey, final String privatekey, final String nonce, final String salt) {
		super(id, version, publickey, privatekey, nonce, salt);
	}

	public User(final Long id, final Integer version) {
		super(id, version);
	}

	public User(final Long id, final Integer version, final Boolean viaCookie, final Integer loginCount) {
		super(id, version);

		final Date now = new Date();
		this.loginCount = loginCount;
		this.unsuccessfulLoginAttempts = 0;

		if (Boolean.TRUE.equals(viaCookie)) {
			this.lastLoginViaCookie = now;
		} else {
			this.lastLoginViaCredentials = now;
		}
	}

	public User(final Long id, final Integer version, final Integer unsuccessfulLoginAttempts) {
		super(id, version);
		this.unsuccessfulLoginAttempts = unsuccessfulLoginAttempts;
	}

	// Creates a user with only fields that a regular user may modify being set. Sanitized User Copy constructor.
	public User(final User user) {
		super(user);
		this.preferenceFlags = user.preferenceFlags;
		this.hideItemDelay = user.hideItemDelay;
		this.defaultPage = user.defaultPage;
		this.email2 = user.email2;
		this.email3 = user.email3;
		this.phone = user.phone;
		this.mobile1 = user.mobile1;
		this.mobile2 = user.mobile2;
		this.mobile3 = user.mobile3;
		this.twitter = user.twitter;
		this.facebook = user.facebook;
		adjustProfilePicEnabledFlag();
	}

	/**
	 * A constructor invoked when changing the password of a user.
	 *
	 * @param id
	 * @param version
	 * @param passwordChangeRequired
	 * @param passwordDigest
	 * @param lastPasswordDigest
	 * @param lastPasswordDigest1
	 * @param lastPasswordDigest2
	 */
	public User(final Long id, final Integer version, final Boolean passwordChangeRequired, final String passwordDigest,
					final String lastPasswordDigest, final String lastPasswordDigest1, final String lastPasswordDigest2) {
		super(id, version, passwordChangeRequired);
		this.passwordDigest = passwordDigest;
		this.lastPasswordResetTime = new Date();
		this.lastPasswordDigest1 = lastPasswordDigest; // shift last password digests down
		this.lastPasswordDigest2 = lastPasswordDigest1;
		this.lastPasswordDigest3 = lastPasswordDigest2;
	}

	public void adjustProfilePicEnabledFlag() {
		byte[] pic = getProfilePic();
		if (pic != null && pic.length != 0) {
			setProfilePictureEnabled();
			super.setProfilePictureEnabled(true);
		} else {
			super.setProfilePictureEnabled(false);
			clearProfilePictureEnabled();
		}		
	}

	// Users may delete certain fields
	@Override
	public boolean emptyFieldsExist() {
		return super.emptyFieldsExist() || empty(email2) || empty(email3) || empty(phone) || empty(mobile1) || empty(mobile2)
				|| empty(mobile3) || empty(twitter) || empty(facebook);
	}

	/**
	 * In order to clear values in the database, if fields are set to empty strings, they are set to null.
	 *
	 * @param user
	 * @return
	 */
	public User nullifyEmptyFields(final User user) {
		super.nullifyEmptyFields(user);
		if (empty(user.email2))		{ email2 = null; }
		if (empty(user.email3))		{ email3 = null; }
		if (empty(user.phone))		{ phone = null; }
		if (empty(user.mobile1))	{ mobile1 = null; }
		if (empty(user.mobile2))	{ mobile2 = null; }
		if (empty(user.mobile3))	{ mobile3 = null; }
		if (empty(user.twitter))	{ twitter = null; }
		if (empty(user.facebook))	{ facebook = null; }
		return this;
	}

	public boolean authSourceIsLocal() {
		return authSource != null && authSource.equals(ObidosConstants.AUTH_SOURCE_LOCAL);
	}

	@Override
	public String getName() {
		return getUsername();
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

	@Override
	public Long getUserId() {
		return getId();
	}

	public boolean getAcceptExternalNotification() {
		return preferenceFlags == null ? Boolean.FALSE : (preferenceFlags.longValue() & UserDTO.EXTERNAL_NOTIFICATIONS_ENABLED) != 0;
	}

	public Boolean getAcceptEmailNotification() {
		return preferenceFlags == null ? Boolean.FALSE : (preferenceFlags.longValue() & UserDTO.EMAIL_NOTIFICATIONS_ENABLED) != 0;
	}

	public Boolean getAcceptSMSNotification() {
		return preferenceFlags == null ? Boolean.FALSE : (preferenceFlags.longValue() & UserDTO.SMS_NOTIFICATIONS_ENABLED) != 0;
	}

	public void setAcceptEmailNotification(final Boolean acceptShareNotification) {
		if (Boolean.TRUE.equals(acceptShareNotification)) {
			this.preferenceFlags |= UserDTO.EMAIL_NOTIFICATIONS_ENABLED;
		} else {
			this.preferenceFlags &= ~UserDTO.EMAIL_NOTIFICATIONS_ENABLED;
		}
	}

	public Date getLastLoginViaCredentials() {
		return lastLoginViaCredentials;
	}

	public void setLastLoginViaCredentials(final Date lastLoginViaCredentials) {
		this.lastLoginViaCredentials = lastLoginViaCredentials;
	}

	public Date getLastLoginViaCookie() {
		return lastLoginViaCookie;
	}

	public void setLastLoginViaCookie(final Date lastLoginViaCookie) {
		this.lastLoginViaCookie = lastLoginViaCookie;
	}

	public Date getLastPasswordResetTime() {
		return lastPasswordResetTime;
	}

	public void setLastPasswordResetTime(final Date lastPasswordResetTime) {
		this.lastPasswordResetTime = lastPasswordResetTime;
	}

	public String getLastPasswordDigest1() {
		return lastPasswordDigest1;
	}

	public void setLastPasswordDigest1(final String lastPasswordDigest1) {
		this.lastPasswordDigest1 = lastPasswordDigest1;
	}

	public String getLastPasswordDigest2() {
		return lastPasswordDigest2;
	}

	public void setLastPasswordDigest2(final String lastPasswordDigest2) {
		this.lastPasswordDigest2 = lastPasswordDigest2;
	}

	public String getLastPasswordDigest3() {
		return lastPasswordDigest3;
	}

	public void setLastPasswordDigest3(final String lastPasswordDigest3) {
		this.lastPasswordDigest3 = lastPasswordDigest3;
	}

	public String getTwoFactorAuthSecret() {
		return twoFactorAuthSecret;
	}

	public void setTwoFactorAuthSecret(final String twoFactorAuthSecret) {
		this.twoFactorAuthSecret = twoFactorAuthSecret;
	}

	public Boolean getTwoFARequired() {
		return twoFARequired;
	}

	public void setTwoFARequired(final Boolean twoFARequired) {
		this.twoFARequired = twoFARequired;
	}

	public Boolean getTwoFAPasswordResetEnabled() {
		return twoFAPasswordResetEnabled;
	}

	public void setTwoFAPasswordResetEnabled(final Boolean twoFAPasswordResetEnabled) {
		this.twoFAPasswordResetEnabled = twoFAPasswordResetEnabled;
	}

	public Integer getHideItemDelay() {
		return hideItemDelay;
	}

	public void setHideItemDelay(Integer hideItemDelay) {
		this.hideItemDelay = hideItemDelay;
	}

	public Integer getDefaultPage() {
		return defaultPage;
	}

	public void setDefaultPage(Integer defaultPage) {
		this.defaultPage = defaultPage;
	}

	@Override
	public Integer getSecurityClearance() {
		return securityClearance;
	}

	public void setSecurityClearance(Integer securityClearance) {
		this.securityClearance = securityClearance;
	}

	public long getLastActivityTime() {
		return lastActivityTime;
	}

	public void setLastActivityTime(long lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}

	private boolean hasCapability(final long capability) {
		return (rawCapabilities & (capability | ROOT_ADMIN)) != 0;		
	}

	public boolean getRootAdmin()	{ return hasCapability(ROOT_ADMIN); }
	public boolean getCreateAdmin()	{ return hasCapability(CREATE_ADMIN); }
	public boolean getCreateUser()	{ return hasCapability(CREATE_USER); }
	public boolean getLockAdmin()	{ return hasCapability(LOCK_ADMIN); }
	public boolean getLockUser()	{ return hasCapability(LOCK_USER); }
	public boolean getDeleteUser()	{ return hasCapability(DELETE_USER); }
	public boolean getDeleteAdmin()	{ return hasCapability(DELETE_ADMIN); }
	public boolean getModifyEmailTemplates()	{ return hasCapability(MODIFY_EMAIL_TEMPLATES); }
	public boolean getChangeUserCredentials()	{ return hasCapability(CHANGE_USER_CREDENTIALS); }
	public boolean getChangeAdminCredentials()	{ return hasCapability(CHANGE_ADMIN_CREDENTIALS); }
	public boolean getCreateGlobalTemplates()	{ return hasCapability(CREATE_GLOBAL_TEMPLATES); }
	public boolean getCreateGlobalGroups()		{ return hasCapability(CREATE_GLOBAL_GROUPS); }

	public boolean getModifySettings() { return hasCapability(MODIFY_SETTINGS); }

	public void setRawCapabilities(final Long capabilities) {
		this.rawCapabilities = capabilities;
	}

	public Long getRawCapabilities() {
		return rawCapabilities;
	}

	public User adjustRootAdminCapabilities() {
		if (hasCapability(ROOT_ADMIN)) {
			setRawCapabilities(ALL_CAPABILTIES);
		}
		return this;
	}

	public boolean hasSufficientSecurityClearance(final SecurityClassificationDTO sc) {
		return sc == null || securityClearance >= sc.getLevel();
	}

	private void ensurePreferenceFlagsInit() {
		if (this.preferenceFlags == null) {
			this.preferenceFlags = 0L;
		}		
	}

	public Long getPreferenceFlags() {
		return preferenceFlags;
	}

	public void setPreferenceFlags(final Long preferenceFlags) {
		ensurePreferenceFlagsInit();
		this.preferenceFlags = preferenceFlags;
	}

	public void setProfilePictureEnabled() {
		ensurePreferenceFlagsInit();
		this.preferenceFlags |= UserDTO.PROFILE_PICTURE_ENABLED;
	}

	public void clearProfilePictureEnabled() {
		if (this.preferenceFlags != null) {
			this.preferenceFlags &= ~UserDTO.PROFILE_PICTURE_ENABLED;
		}
	}

	public boolean acceptsSMSMessages() {
		ensurePreferenceFlagsInit();
		return (preferenceFlags.longValue() & UserDTO.SMS_NOTIFICATIONS_ENABLED) != 0;
	}


	public Integer getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public Integer getUnsuccessfulLoginAttempts() {
		return unsuccessfulLoginAttempts;
	}

	public void setUnsuccessfulLoginAttempts(Integer unsuccessfulLoginAttempts) {
		this.unsuccessfulLoginAttempts = unsuccessfulLoginAttempts;
	}

	public boolean requireTwoFaAfterPassphrase() {
		return (preferenceFlags & UserDTO.REQUIRE_TWO_FA_AFTER_AUTHENTICATION) != 0;
	}

	public Long getMaximumPasswordAge() {
		return maximumPasswordAge;
	}

	public void setMaximumPasswordAge(final Long maximumPasswordAge) {
		this.maximumPasswordAge = maximumPasswordAge;
	}

	public Long getMinimumPasswordAge() {
		return minimumPasswordAge;
	}

	public void setMinimumPasswordAge(final Long minimumPasswordAge) {
		this.minimumPasswordAge = minimumPasswordAge;
	}

	@Override
	public Boolean getProfilePictureEnabled() {
		return this.preferenceFlags == null ? super.getProfilePictureEnabled() : (this.preferenceFlags & UserDTO.PROFILE_PICTURE_ENABLED) != 0;
	}

	@Override
	public void setProfilePictureEnabled(Boolean val) {
		super.setProfilePictureEnabled(val);
		if (Boolean.TRUE.equals(val)) {
			setProfilePictureEnabled();
		} else {
			clearProfilePictureEnabled();			
		}
	}
}
