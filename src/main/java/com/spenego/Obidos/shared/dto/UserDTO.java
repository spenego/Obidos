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
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.spenego.Obidos.shared.ObidosConstants;

/**
 * The {@code UserDTO} class represents the currently logged-in user. This
 * class contains almost all information that is stored in the database
 * about the user.  It will not contain the user's private key.
 *
 * @since   Obidos1.0
 * @author  Mike Morgan
 * @see     com.spenego.Obidos.shared.dto.LimitedUserDTO
 */

public final class UserDTO extends LimitedUserDTO implements Clearable, Serializable
{
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_PAGE_LIST_CONTAINERS				= 0;
	public static final int DEFAULT_PAGE_LIST_CONTAINERS_SHARED_WITH_ME	= 1;
	public static final int DEFAULT_PAGE_LIST_MY_ITEMS					= 2;
	public static final int DEFAULT_PAGE_LIST_ITEMS_SHARED_WITH_ME		= 3;
	public static final int DEFAULT_PAGE_LIST_MY_NOTES					= 4;
	public static final int DEFAULT_PAGE_LIST_NOTES_SHARED_WITH_ME		= 5;

	// User Preferences
	public static final long FLAG_HIDE_HELP_BUTTON						= (1L << 0);
	public static final long REQUIRE_TWO_FA_AFTER_AUTHENTICATION		= (1L << 1);
	public static final long PROFILE_PICTURE_ENABLED					= (1L << 2);
	public static final long OBIDOS_NOTIFICATIONS_ENABLED				= (1L << 3);
	public static final long EMAIL_NOTIFICATIONS_ENABLED				= (1L << 4);
	public static final long SMS_NOTIFICATIONS_ENABLED					= (1L << 5);
	public static final long EXTERNAL_NOTIFICATIONS_ENABLED				= (EMAIL_NOTIFICATIONS_ENABLED | SMS_NOTIFICATIONS_ENABLED);

	private Integer 			hideItemDelay;			// time in seconds until item contents are hidden
	private Integer 			defaultPage;
	private Integer				notificationCount;
	private Integer				daysUntilPasswordExpiration;
	private long				preferenceFlags;
	private String 				authSource;
	private String 				authurn;
	private String 				comments;
	private String				publickey;

	@NotNull(message="Please specify the username")
	@Pattern(regexp="\\b[a-zA-Z][a-zA-Z0-9\\-._]{1,}(|@([a-zA-Z0-9\\-]{2,}\\.)+[a-zA-Z]{2,})\\b", message="Invalid username")
	private String 				username;

	@NotNull(message="Password must be specified")
	private String 				password;


	private String  		    lastLoginIPAddress;
	private Long      			lastActivityPerformedTime;
	private Long      			maximumPasswordAge;
	private Long      			minimumPasswordAge;
	private Boolean				updatePermitted;
	private Boolean				sharePermitted;
	private Boolean				ownershipControl;
	private Boolean				administrator;
	private Boolean				deleted;	// only ever set for admins
	private Boolean				passwordChangeRequired;
	private Boolean				twoFARequired;
	private Boolean				twoFAPasswordResetEnabled;
	private Integer				sessionTimeoutSeconds;
	private CapabilityDTO		capabilities;
	private Date 				createdAt;
	private Date 				updatedAt;
	private byte[]				profilePic;
	private LicenseStats		license;

	// Key in Map is 'Country Name (+code)', value is CountryCodeDTO
	private Map<String, CountryCodeDTO> countryCodesMap;
	private String              countryNameAndCode;
	private String              phoneNumberE164Format;
	private String              phoneNumberNationalFormat;
	private String              phoneNumberInternationFormat;
	private String              phoneNumberRegion;
	private CountryCodeDTO      countryCodeDTO;

	public UserDTO() {}

	public UserDTO(final Long id, final Boolean is_administrator) {
		super(id);
		this.administrator = is_administrator;
	}

	public UserDTO(final Long id, final String password) {
		super(id);
		this.password = password;
	}

	public UserDTO(final String username, final Boolean deleted) {
		this.username = username;
		this.deleted = deleted;
	}

	public UserDTO(final String username, final Boolean deleted, final Boolean locked) {
		super(locked);
		this.username = username;
		this.deleted = deleted;
	}

	@Override
	public void clear() {
		if (profilePic != null) {
			Arrays.fill(profilePic, (byte) 0);
			profilePic = null;
		}

		authSource = authurn = comments = publickey = password = lastLoginIPAddress = null;
		lastActivityPerformedTime = null;
		updatePermitted = sharePermitted = ownershipControl = administrator = deleted =
				passwordChangeRequired = twoFARequired = twoFAPasswordResetEnabled = null;
		sessionTimeoutSeconds = null;
		capabilities.clear();
		capabilities = null;
		createdAt = updatedAt = null;
		super.clear();
	}

	public final String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public String getAuthurn() {
		return authurn;
	}
	public void setAuthurn(String authurn) {
		this.authurn = authurn;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLastLoginIPAddress() {
		return lastLoginIPAddress;
	}
	public void setLastLoginIPAddress(String lastLoginIPAddress) {
		this.lastLoginIPAddress = lastLoginIPAddress;
	}
	public Long getLastActivityPerformedTime() {
		return lastActivityPerformedTime;
	}
	public void setLastActivityPerformedTime(Long lastActivityPerformedTime) {
		this.lastActivityPerformedTime = lastActivityPerformedTime;
	}

	public String getAuthSource() {
		return authSource;
	}

	public void setAuthSource(String authSource) {
		this.authSource = authSource == null ? null : authSource.trim();
	}

	public boolean authSourceIsLocal() {
		return authSource != null && authSource.equals(ObidosConstants.AUTH_SOURCE_LOCAL);
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}

	public boolean keyPairExists() {
		return this.publickey != null;
	}

	// called after creating keypair to indicate that key pair is created
	public void setKeyPairExists(boolean b) {
		if (b && this.publickey == null) {
			this.publickey = "something";
		}
	}

	public CapabilityDTO getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(final CapabilityDTO capabilities) {
		this.capabilities = capabilities;
	}

	public Integer getSessionTimeoutSeconds()
	{
		return sessionTimeoutSeconds;
	}

	public void setSessionTimeoutSeconds(Integer sessionTimeoutSeconds)
	{
		this.sessionTimeoutSeconds = sessionTimeoutSeconds;
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

	public void setTwoFAPasswordResetEnabled(Boolean twoFAPasswordResetEnabled) {
		this.twoFAPasswordResetEnabled = twoFAPasswordResetEnabled;
	}

	public static Boolean	getAcceptEmailNotification(long preferenceFlags) { return (preferenceFlags & EMAIL_NOTIFICATIONS_ENABLED) != 0; }

	public Boolean			getAcceptEmailNotification() { return getAcceptEmailNotification(preferenceFlags); }

	@Override
	public Boolean getUpdatePermitted() {
		return updatePermitted;
	}

	@Override
	public void setUpdatePermitted(final Boolean updatePermitted) {
		this.updatePermitted = updatePermitted;
	}

	@Override
	public Boolean getSharePermitted() {
		return sharePermitted;
	}

	@Override
	public void setSharePermitted(final Boolean sharePermitted) {
		this.sharePermitted = sharePermitted;
	}

	@Override
	public Boolean getOwnershipControl() {
		return ownershipControl;
	}

	@Override
	public void setOwnershipControl(final Boolean ownershipControl) {
		this.ownershipControl = ownershipControl;
	}

	/*
	public Boolean getAcceptEmailNotification() {
		return (preferenceFlags & EMAIL_NOTIFICATIONS_ENABLED) != 0;
	}
	*/

	public void setAcceptEmailNotification() {
		this.preferenceFlags |= EMAIL_NOTIFICATIONS_ENABLED;
	}

	public void clearAcceptEmailNotification() {
		this.preferenceFlags &= ~EMAIL_NOTIFICATIONS_ENABLED;
	}

	public Boolean getAcceptSMSNotification() {
		return (preferenceFlags & SMS_NOTIFICATIONS_ENABLED) != 0;
	}

	public void setAcceptSMSNotification() {
		this.preferenceFlags |= SMS_NOTIFICATIONS_ENABLED;
	}

	public void clearAcceptSMSNotification() {
		this.preferenceFlags &= ~SMS_NOTIFICATIONS_ENABLED;
	}

	public Boolean getAdministrator() {
		return administrator;
	}

	public void setAdministrator(final Boolean administrator) {
		this.administrator = administrator;
	}

	public boolean adminIsEnabled() {
		return administrator != null && administrator.booleanValue();
	}

	public boolean adminisDisabled() {
		return administrator != null && !administrator.booleanValue();
	}

	public Boolean getPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	public void setPasswordChangeRequired(final Boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public final Boolean getDeleted() {
		return deleted;
	}

	public final void setDeleted(final Boolean deleted) {
		this.deleted = deleted;
	}

	public final Date getCreatedAt() {
		return createdAt;
	}

	public final void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public final Date getUpdatedAt() {
		return updatedAt;
	}

	public final void setUpdatedAt(final Date updatedAt) {
		this.updatedAt = updatedAt;
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

	public Integer getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(Integer notificationCount) {
		this.notificationCount = notificationCount;
	}

	@Override
	public byte[] getProfilePic() {
		return profilePic;
	}

	@Override
	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	public Integer getDaysUntilPasswordExpiration() {
		return daysUntilPasswordExpiration;
	}

	public void setDaysUntilPasswordExpiration(Integer daysUntilPasswordExpiration) {
		this.daysUntilPasswordExpiration = daysUntilPasswordExpiration;
	}

	public long getPreferenceFlags() {
		return preferenceFlags;
	}

	public void setPreferenceFlags(long preferenceFlags) {
		this.preferenceFlags = preferenceFlags;
	}

	public Boolean requireTwoFaAfterPassphrase() {
		return (this.preferenceFlags & REQUIRE_TWO_FA_AFTER_AUTHENTICATION) != 0;
	}

	public void setRequireTwoFaAfterPassphrase() {
		this.preferenceFlags |= REQUIRE_TWO_FA_AFTER_AUTHENTICATION;
	}

	public void clearRequireTwoFaAfterPassphrase() {
		this.preferenceFlags &= ~REQUIRE_TWO_FA_AFTER_AUTHENTICATION;
	}

	public Boolean getHasProfilePicture() {
		return (this.preferenceFlags & PROFILE_PICTURE_ENABLED) != 0;
	}

	public void setHasProfilePicture() {
		this.preferenceFlags |= PROFILE_PICTURE_ENABLED;
	}

	public void clearHasProfilePicture() {
		this.preferenceFlags &= ~PROFILE_PICTURE_ENABLED;
	}

	public LicenseStats getLicense() {
		return license;
	}

	public void setLicense(LicenseStats license) {
		this.license = license;
	}

	public Long getMaximumPasswordAge() {
		return maximumPasswordAge;
	}

	public void setMaximumPasswordAge(Long maximumPasswordAge) {
		this.maximumPasswordAge = maximumPasswordAge;
	}

	public Long getMinimumPasswordAge() {
		return minimumPasswordAge;
	}

	public void setMinimumPasswordAge(Long minimumPasswordAge) {
		this.minimumPasswordAge = minimumPasswordAge;
	}

	public String getCountryNameAndCode()
	{
		return countryNameAndCode;
	}

	public void setCountryNameAndCode(String countryNameAndCode)
	{
		this.countryNameAndCode = countryNameAndCode;
	}

	public String getPhoneNumberE164Format()
	{
		return phoneNumberE164Format;
	}

	public void setPhoneNumberE164Format(String phoneNumberE164Format)
	{
		this.phoneNumberE164Format = phoneNumberE164Format;
	}

	public String getPhoneNumberNationalFormat()
	{
		return phoneNumberNationalFormat;
	}

	public void setPhoneNumberNationalFormat(String phoneNumberNationalFormat)
	{
		this.phoneNumberNationalFormat = phoneNumberNationalFormat;
	}

	public String getPhoneNumberInternationFormat()
	{
		return phoneNumberInternationFormat;
	}

	public void setPhoneNumberInternationFormat(String phoneNumberInternationFormat)
	{
		this.phoneNumberInternationFormat = phoneNumberInternationFormat;
	}

	public String getPhoneNumberRegion()
	{
		return phoneNumberRegion;
	}

	public void setPhoneNumberRegion(String phoneNumberRegion)
	{
		this.phoneNumberRegion = phoneNumberRegion;
	}

	public Map<String, CountryCodeDTO> getCountryCodesMap()
	{
		return countryCodesMap;
	}

	public void setCountryCodesMap(Map<String, CountryCodeDTO> countryCodesMap)
	{
		this.countryCodesMap = countryCodesMap;
	}

	public CountryCodeDTO getCountryCodeDTO()
	{
		return countryCodeDTO;
	}

	public void setCountryCodeDTO(CountryCodeDTO countryCodeDTO)
	{
		this.countryCodeDTO = countryCodeDTO;
	}
}

