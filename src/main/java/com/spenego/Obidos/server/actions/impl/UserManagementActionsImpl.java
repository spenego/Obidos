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

package com.spenego.Obidos.server.actions.impl;

import static com.spenego.Obidos.server.model.Audit.ACTION_DENIED_BY_CAPABILITY;
import static com.spenego.Obidos.server.model.Audit.CAPABILITY_GRANTED;
import static com.spenego.Obidos.server.model.Audit.CAPABILITY_RESCINDED;
import static com.spenego.Obidos.server.model.Audit.CREATE_USER;
import static com.spenego.Obidos.server.model.Audit.DELETE_USER;
import static com.spenego.Obidos.server.model.Audit.LICENSE_LIMIT_REACHED;
import static com.spenego.Obidos.server.model.Audit.UPDATE_CAPABILITY;
import static com.spenego.Obidos.server.model.Audit.UPDATE_USER;
import static com.spenego.Obidos.server.model.Audit.USER_LOCKED;
import static com.spenego.Obidos.server.model.Audit.USER_RESTORED;
import static com.spenego.Obidos.server.model.Audit.USER_TOMBSTONED;
import static com.spenego.Obidos.server.model.Audit.USER_UNLOCKED;
import static com.spenego.Obidos.server.model.Audit.USER_UNTOMBSTONED;
import static com.spenego.Obidos.server.utils.ServerUtils.validateLicense;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.CapabilityActions;
import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.HaveIBeenPwnedActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.PasswordResetActions;
import com.spenego.Obidos.server.actions.TwoFactorAuthenticationActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.ComplexityRequirements;
import com.spenego.Obidos.server.model.Item;
import com.spenego.Obidos.server.model.LimitedUser;
import com.spenego.Obidos.server.model.SharedItem;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.ComplexityRequirementsOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.security.PasswordSecurity;
import com.spenego.Obidos.server.utils.CountryCodeUtil;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.server.validators.ValidateBean;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminResult;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.NotificationDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;
import com.spenego.Obidos.shared.exceptions.NotImplementedException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.RecordModifiedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class UserManagementActionsImpl extends CryptoActions<User> implements UserManagementActions {
	private static final Logger logger = LoggerFactory.getLogger(UserManagementActionsImpl.class);
	private static final String USERNAME_SEPERATOR = ":-:";
	private static final int MIN_PASSWORD_STRENGTH = PasswordAnalysisResults.PASSWORD_STRONG;

	@Autowired private final Authenticator						authenticator = null;
	@Autowired private final CapabilityActions					capabilityActions = null;
	@Autowired private final ComplexityRequirementsOperations	complexityRequirementsOperations = null;
	@Autowired private final ContainerActions					containerActions = null;
	@Autowired private final EmailActions						emailActions = null;
	@Autowired private final HaveIBeenPwnedActions				haveIBeenPwnedActions = null;
	@Autowired private final ItemActions						itemActions = null;
	@Autowired private final LoginActions						loginActions = null;
	@Autowired private final PasswordSecurity					passwordSecurity = null;
	@Autowired private final PasswordResetActions				passwordResetActions = null;
	@Autowired private final TwoFactorAuthenticationActions		twoFactorAuthenticationActions = null;

	private Map<Long, Long> activeUsers;

	public UserManagementActionsImpl() {
		super(null, null);
		activeUsers = new ConcurrentHashMap<>();
	}

	@Override
	protected final Operations<User> getOperations() {
		return userOperations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_USER;
	}

	@Override
	protected final String elementName() {
		return "user";
	}

	private User convertUser(final UserDTO user) {
		return convert(user, User.class);
	}

	private Integer getPasswordScore(final String password, final User user) {
		return loginActions.checkPassStrength(user, password, ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS).getPasswordScore();
	}

	private boolean verifyPassword(final String oldPassword, final String passwordHash) {
		return passwordSecurity.verifyPassword(oldPassword, passwordHash);
	}

	private String getPasswordHash(final String password) {
		return passwordSecurity.hashPassword(password);
	}

	private String getPasswordHash(final UserDTO user) {
		return getPasswordHash(user.getPassword());
	}

	private void authenticate2FA(final User user, final byte[] twoFACode) {
		twoFactorAuthenticationActions.authenticate2FA(user, twoFACode);
	}

	private User getAuthenticatedUser(final String username, final String password, final boolean createNewSessionState) {
		return authenticator.authenticateCredentials(username, password, createNewSessionState);
	}

	private void createDefaultUserContainers(final User newUser) {
		containerActions.createDefaultPrivateContainer(newUser);
		containerActions.createDefaultPublicContainer(newUser);
		containerActions.createContainerAssignment(newUser.getId(), ContainerDTO.NOTEBOOK_ID, null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
	}

	@Override
	public Boolean keypairExists(final User requestor, final Long userId) {
		if (isFalse(requestor.self(userId)) && isFalse(requestor.getAdministrator())) {
			throw new PermissionDeniedException("You may not ask this about other users.");
		}
		return getModel(userId).getPublickey() != null;
	}

	private void licenseCheck(final User admin, final SystemConfig sc, final String action) {
		final LicenseKeyDTO license = validateLicense(sc.getLicense(), sc.getLicensePublicKey());
		final int licensedUsersMax = isTrue(license.getHasExpired()) ? 10 : license.getMaxUsers();
		final int activeUserCount = userOperations.getCount(admin.getId(), new User(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE), null);

		// Issue #757
		// If max users is 0, that means there is no limit on users
		// spgdev - 05/28/2022
		if (licensedUsersMax != 0 && licensedUsersMax <= activeUserCount) {

			final String expired = isTrue(license.getHasExpired()) ? "has EXPIRED and" : "";
			logger.error(() -> "License " + expired + " has a limit of " + licensedUsersMax + "active users, unable to " + action);
			audit(LICENSE_LIMIT_REACHED, admin.getUsername(), admin.getUserId(), null, null, null, "unable to " + action);
			throw new LicenseKeyException("Your license " + expired + " only permits " + license.getMaxUsers() + " active users and you currently have " + activeUserCount
					+ ". Please contact Spenego Software to upgrade your license.");
		}
	}

	private static void imposeUsernameRestrictions(final String username) {
		if (username == null || username.isEmpty()) {
			throw new ServerSideException("Username may not be empty.");
		}
		if (username.contains(USERNAME_SEPERATOR)) {
			throw new ServerSideException("Your username may not container the characters " + USERNAME_SEPERATOR);
		}
		if (username.length() > 60) {
			throw new ServerSideException("Username exceeds length restriction of 60 characters.");
		}
		// Load username restrictions
		// validate username
	}

	private void sendEmailNotification(final User caller, final Long userId, String emailComment) {
		try {
			emailActions.sendUserCreationEmail(caller, userId, emailComment);
		} catch (final ServerSideException e) {
			logger.error(() -> "ERROR: Could not send user creation email: " + e.getMessage());
		}
	}

	private SystemConfig getValidSystemConfig() {
		final SystemConfig sc = getSystemConfig();

		if (sc.getLicensePublicKey() == null) {
			throw new ServerSideException("License Public Key has not been initialized.");
		}

		if (sc.getLicense() == null) {
			throw new ServerSideException("License has not been initialized.");
		}

		return sc;
	}

	private User getInitializedUser(final UserDTO user) {
		final User newUser = convertUser(user);

		if (newUser.authSourceIsLocal()) {
			newUser.setPasswordChangeRequired(true);
		}

		if (user.getPassword() != null) {
			newUser.setPasswordDigest(getPasswordHash(user));
		}

		if (newUser.getDefaultPage() == null) {
			newUser.setDefaultPage(UserDTO.DEFAULT_PAGE_LIST_CONTAINERS);
		}

		if (newUser.getHideItemDelay() == null) {
			newUser.setHideItemDelay(10);
		}

		if (newUser.getMaximumPasswordAge() == null) {
			newUser.setMaximumPasswordAge((long) getPasswordComplexity(null).getPasswordComplexityRequirements().getMaxAgeInDays());
		}

		newUser.setId(null);		// Bug #94: Ensure objects created after initialization use dynamic IDs (do not let caller specify ID)
		newUser.setLoginCount(0);
		newUser.setUnsuccessfulLoginAttempts(0);
		newUser.adjustProfilePicEnabledFlag();

		return newUser;
	}

	/**
	 * Prevent deputy admins from enabling capabilities of other deputy admins, which they do not already posses themselves.
	 *
	 */
	private void disableNonAuthorizedCapabilities(final CapabilityDTO capabilities, final User caller) {
		logger.info(() -> "Caller is NOT a Root admin, disabling capabilities not owned by caller.");
		capabilities.disableNonAuthorizedCapabilities(convert(new Capability(null, caller.getRawCapabilities()), CapabilityDTO.class));
	}

	private void createCapabilities(final User admin, final CapabilityDTO capabilities, final Long userId, final boolean newUserIsAnAdmin) {
		if (capabilities != null) {
			capabilities.setUserId(userId);
			if (!newUserIsAnAdmin) {
				capabilities.disableAdminCapabilities();
			} else {
				boolean callerIsRootAdmin = admin.getRootAdmin();

				if (!callerIsRootAdmin) {
					capabilities.setRootAdmin(false);
				}

				if (capabilities.getRootAdmin()) {
					capabilities.enableEverything();
				} else if (!callerIsRootAdmin) {	// Root Admins can make any changes they want, no need to disable capabilities
					disableNonAuthorizedCapabilities(capabilities, admin);
				}
			}
		}

		if (capabilities != null) {
			logger.info(() -> "capability Create User = " + capabilities.getCreateUser());
			logger.info(() -> "capability Modify Settings = " + capabilities.getModifySettings());
		}

		capabilityActions.create(admin, capabilities == null ? new CapabilityDTO(userId) : capabilities);
	}

	private static String getUserType(final CapabilityDTO capabilities) {
		return (capabilities == null || !capabilities.getRootAdmin()) ? "DEPUTY admin" : "ROOT admin";
	}

	private static void throwAdminCapabilityLimitationException(String action) {
		throw new PermissionDeniedException("Since you do not have the capability to " + action + ", you may not grant other admins that capability.");
	}

	private void checkDeputyAdminCapabilities(final User admin, final UserDTO user, final CapabilityDTO capabilities) {
		if (capabilities.getRootAdmin()) {
			throw new PermissionDeniedException("You do not have the capability to create Root Admins.");
		}

		final boolean newUserIsAnAdmin = user.getAdministrator();

		if (newUserIsAnAdmin ? !admin.getCreateAdmin() : !admin.getCreateUser()) {
			final String userType = newUserIsAnAdmin ? "an admin" : "a user";

			audit(ACTION_DENIED_BY_CAPABILITY, admin.getUsername(), admin.getId(), user.getUsername(), null, null, "no capability to create " + userType);
			throw new PermissionDeniedException("You do not have the capability to create " + userType + ".");
		}

		// Changes for Bug #69 were reverted.  Here we let the deputy admin know they can not grant a new admin more capability than they have.
		if (newUserIsAnAdmin && !capabilities.getRootAdmin()) {
			if (capabilities.getDeleteAdmin() && !admin.getDeleteAdmin()) {
				throwAdminCapabilityLimitationException("delete admins");
			}

			if (capabilities.getLockAdmin() && !admin.getLockAdmin()) {
				throwAdminCapabilityLimitationException("lock admins");
			}

			if (capabilities.getModifySettings() && !admin.getModifySettings()) {
				throwAdminCapabilityLimitationException("modify settings");
			}
		}
	}

	@Override
	public Long createUser(final User admin, final UserDTO user, final CapabilityDTO capabilities, final Boolean notifyUser, final String emailComment) {
		logger.info(() -> "createUser: capability Create User = " + capabilities.getCreateUser());
		logger.info(() -> "createUser: capability Modify Settings = " + capabilities.getModifySettings());

		if (user.getAdministrator() == null) {
			user.setAdministrator(false);
		}

		/**
		 * Should deputy admins implicitly have the right to create new admins that have the capability to create more admins?
		 */

		if (!admin.getRootAdmin()) {
			checkDeputyAdminCapabilities(admin, user, capabilities);
		}

		imposeUsernameRestrictions(user.getUsername());

		final boolean newUserIsAnAdmin = user.getAdministrator();

		if (!newUserIsAnAdmin) { // ensure the license is still active
			licenseCheck(admin, getValidSystemConfig(), "create new user");
		}

		String mobileNumber = user.getMobile1();
		// Feature #43
		if (mobileNumber != null && mobileNumber.length() > 0) {
			ServerUtils.validatePhoneNumber(mobileNumber);
		}

		final User newUser = getInitializedUser(user);

		// convert mobile number to E.164
		setMobileE164Number(newUser);

		final Long userId = userOperations.create(newUser);

		createDefaultUserContainers(newUser);
		createCapabilities(admin, capabilities, userId, newUserIsAnAdmin);

		if (isTrue(notifyUser)) {
			sendEmailNotification(admin, userId, emailComment);
		}

		final String userType = newUserIsAnAdmin ? getUserType(capabilities) : "user";

		audit(CREATE_USER, admin.getUsername(), admin.getId(), user.getUsername(), userId, null, userType);

		return userId;
	}

	private User getUserPublicProfile(final Long userId) {
		final User u = userOperations.getUserPublicProfile(userId);
		if (u.getDeleted() != null && u.getDeleted()) {
			throw new ServerSideException("User has been deleted.");
		}
		return u;
	}

	private Stream<User> getUsers(final Long userId, final User userPatterns, final Collection<Long> preSelectedUsers, final Integer offset, final Integer count,
			final List<OrderBy> orderby) {
		return userOperations.getUsers(userId, userPatterns, preSelectedUsers, offset, count, orderby);
	}

	private static LimitedUser cleanedUsername(final User u) {
		u.setUsername(u.getUsername().replaceAll(USERNAME_SEPERATOR + ".*", ""));
		return u;
	}

	@Override
	public LimitedUserForAdminResult getUsers(final User caller, final UserDTO userPatterns, final Collection<Long> preSelectedUsers, final Integer offset,
			final Integer count, final List<OrderBy> orderby) {
		if (!caller.isAdmin()) {
			throw new PermissionDeniedException("This method is reserved for Admins only.");
		}

		final User u = (userPatterns == null) ? null : convertUser(userPatterns);

		return new LimitedUserForAdminResult(offset, count, preSelectedUsers, (a,b) -> convert(a,b),
				() -> userOperations.getCount(caller.getId(), u, preSelectedUsers),
				() -> getUsers(caller.getId(), u, preSelectedUsers, offset, count, orderby).map(usr -> isTrue(usr.getDeleted()) ? cleanedUsername(usr) : usr));
	}

	private UserDTO setPasswordResetInDays(final UserDTO user, final User u) {
		final boolean infinitePasswordExpiration = u.getMaximumPasswordAge() == null || u.getMaximumPasswordAge().equals(0L);
		final Integer daysUntilPasswordExpiration = infinitePasswordExpiration ? ObidosConstants.PASSWORD_NEVER_EXPIRES
				: ((int) ((long) u.getMaximumPasswordAge()) - loginActions.passwordAgeInDays(u));

		loginActions.setDaysUntilPasswordExpiration(user, daysUntilPasswordExpiration);

		return user;
	}

	// If there is a valid E.164 mobile number, construct the
	// 'County Name (+code)' from it. We'll need to select the item from
	// Select list in front end
	private static void setUserMobileNumber(final UserDTO userDTO, final String mobileNumber) {
		try {
			final String e164Number = CountryCodeUtil.formatToE164(mobileNumber);

			// for the specific number
			userDTO.setCountryCodeDTO(CountryCodeUtil.getPhoneNumberInfo(e164Number));

			final String countryNameAndCode = CountryCodeUtil.getCountryNameAndCode(e164Number);
			if (countryNameAndCode != null) {
				userDTO.setCountryNameAndCode(countryNameAndCode);
			}
		} catch (final Exception e) { // just log
			logger.error(() -> "Mobile number " + mobileNumber + " not E.164 compliant");
		}
	}

	@Override
	public UserDTO getUser(final User caller, final Long id) {
		logger.info(() -> "Getting user " + id);
		final boolean onlyPublicInfo = !caller.isAdmin() && !caller.self(id);
		final User user = onlyPublicInfo ? getUserPublicProfile(id) : getModel(id);
		final Map<String, CountryCodeDTO> countryCodesMap = CountryCodeUtil.getCountryCodesMap();
		final UserDTO userDTO = convert(user, UserDTO.class);

		if (user.isAdmin() && !caller.getModifySettings()) {
			userDTO.setCapabilities(new CapabilityDTO(0L));			// per Bug #69 -- hide capabilities if caller can not modify them
		}

		userDTO.setCountryCodesMap(countryCodesMap);
		userDTO.setCountryNameAndCode(null);
		final String mobileNumber = user.getMobile1();
		if (mobileNumber != null) {
			setUserMobileNumber(userDTO, mobileNumber);
		}

		return onlyPublicInfo ? userDTO : setPasswordResetInDays(userDTO, user);
	}

	@Override
	public LimitedUserDTO getUserByUsername(final String username) {
		logger.info(() -> "Getting user " + username);
		return convert(userOperations.getUserByUsername(username), LimitedUserDTO.class);
	}

	private CapabilityDTO setTargetUser(final CapabilityDTO capability, final UserDTO user) {
		capability.setId(getCapability(user).getId());
		capability.setUserId(user.getId());
		return capability;
	}

	private void notifyAdminOfCapabilityModification(final User caller, final UserDTO user, final String capabilityType, final boolean capabilityGranted) {
		int action = capabilityGranted ? NotificationDTO.CAPABILITY_GRANTED : NotificationDTO.CAPABILITY_RESCINDED;
		final String actionStr = capabilityGranted ? "granted" : "rescinded";
		final String details = capabilityType + " capability " + actionStr;
		final Long userId = user.getId();

		postCommitQueue(() -> notifyRecipient(action, caller, userId, null, "capability", capabilityType, details, null));

		int auditAction = capabilityGranted ? CAPABILITY_GRANTED : CAPABILITY_RESCINDED;
		final String username = getUser(userId).getUsername();
		logger.info(() -> "Admin " + caller.getUsername() + " updated capability " + capabilityType + " of admin " + username + " to " + capabilityGranted);

		audit(auditAction, caller.getUsername(), caller.getId(), username, userId, null, details);
	}

	private void updateCapabilities(final User caller, final UserDTO user, final CapabilityDTO capabilities) {
		final User u = getUser(user.getId());
		final Capability originalCapabilities = getCapability(u);
		final Capability newCapabilities = convert(capabilities, Capability.class);

		if (!caller.getRootAdmin()) {	// Root Admins can make any changes they want, no need to disable capabilities
			disableNonAuthorizedCapabilities(capabilities, caller);
		}
		capabilityActions.update(caller, setTargetUser(capabilities, user));
		audit(UPDATE_CAPABILITY, caller.getUsername(), caller.getId(), getUser(user.getId()).getName(), user.getId());

		if (newCapabilities.createUserModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "create User", newCapabilities.getCreateUser());
		}
		if (newCapabilities.createAdminModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "create Admin", newCapabilities.getCreateAdmin());
		}
		if (newCapabilities.deleteUserModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "delete User", newCapabilities.getDeleteUser());
		}
		if (newCapabilities.deleteAdminModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "delete Admin", newCapabilities.getDeleteAdmin());
		}
		if (newCapabilities.lockUserModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "lock user", newCapabilities.getLockUser());
		}
		if (newCapabilities.lockAdminModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "lock Admin", newCapabilities.getLockAdmin());
		}
		if (newCapabilities.changeUserCredentialsModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "change user credentials", newCapabilities.getChangeUserCredentials());
		}
		if (newCapabilities.changeAdminCredentialsModified(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "change admin credentials", newCapabilities.getChangeAdminCredentials());
		}
		if (newCapabilities.changeRootAdmin(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "root admin", newCapabilities.getRootAdmin());
		}
		if (newCapabilities.changeCreateGlobalTemplate(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "manage global templates", newCapabilities.getCreateGlobalTemplate());
		}
		if (newCapabilities.changeModifySettings(originalCapabilities)) {
			notifyAdminOfCapabilityModification(caller, user, "modify settings", newCapabilities.getModifySettings());
		}
	}

	private static boolean passwordExpirationIsInFuture(final Long maxAgeInDays, final User user) {
		return maxAgeInDays.equals(0L)
				|| (user.getLastPasswordResetTime() != null && (System.currentTimeMillis() - user.getLastPasswordResetTime().getTime()) / MS_PER_DAY < maxAgeInDays);
	}

	// Admins may not be converted to Users or vice-versa
	private static void ensureUserTypeIsNotChanging(final UserDTO user, final User currentUser) {
		if (isTrue(user.getAdministrator()) && !currentUser.isAdmin()) {
			throw new PermissionDeniedException("Users may not be converted to Admins.");
		}
		if (currentUser.isAdmin() && isFalse(user.getAdministrator())) {
			throw new PermissionDeniedException("Admins may not be converted to Users.");
		}
	}

	private void validateUsernameChange(final User caller, final UserDTO user, final User currentUser) {
		if (currentUser.getUsername().equals(user.getUsername())) {
			user.setUsername(null);
		} else {
			if (!caller.getRootAdmin()) {
				throw new PermissionDeniedException("Username changes must be performed by a root admin.");
			}
			if (user.getUsername().contains(USERNAME_SEPERATOR)) {
				throw new ServerSideException("Usernames may not container the characters " + USERNAME_SEPERATOR);
			}
			audit(UPDATE_USER, caller.getUsername(), caller.getId(), currentUser.getUsername(), currentUser.getId(), null, "new username is " + user.getUsername());
		}
	}

	private void validateCredentialChangeCapability(final User caller, final UserDTO user, final User currentUser) {
		if (currentUser.isAdmin()) {
			if (!caller.getChangeAdminCredentials()) {
				audit(ACTION_DENIED_BY_CAPABILITY, caller.getUsername(), caller.getId(), currentUser.getUsername(), currentUser.getId(), null,
						"no capability to update credentials of admin " + user.getUsername());
				throw new PermissionDeniedException("You do not have the capability to change administrator credentials.");
			}
		} else if (!caller.getChangeUserCredentials()) {
			audit(ACTION_DENIED_BY_CAPABILITY, caller.getUsername(), caller.getId(), currentUser.getUsername(), currentUser.getId(), null,
					"no capability to update credentials of user " + user.getUsername());
			throw new PermissionDeniedException("You do not have the capability to change user credentials.");
		}
	}

	private void validateLockUnlockAction(final User caller, final UserDTO user, final User currentUser) {
		if (currentUser.getLocked().equals(user.getLocked())) {
			user.setLocked(null);
		} else if (currentUser.isAdmin()) {
			if (!caller.getLockAdmin()) {
				audit(ACTION_DENIED_BY_CAPABILITY, caller.getUsername(), caller.getId(), currentUser.getUsername(), currentUser.getId(), null,
						"no capability to unlock admin " + user.getUsername());
				throw new PermissionDeniedException("You do not have the capability to (un)lock an admin account.");
			}
		} else if (!caller.getLockUser()) {
			audit(ACTION_DENIED_BY_CAPABILITY, caller.getUsername(), caller.getId(), currentUser.getUsername(), currentUser.getId(), null,
					"no capability to unlock user " + user.getUsername());
			throw new PermissionDeniedException("You do not have the capability to (un)lock a user account.");
		}
	}

	/**
	 * Only Admins may change auth sources. Admins may not have an auth
	 * source other than AUTH_SOURCE_LOCAL. Things get tricky when an admin
	 * is changing a user to/from admin status.
	 */
	private static void ensureAuthSourceChangeIsValid(final UserDTO user, final User currentUser) {
		if (currentUser.isAdmin()) {
			if (user.getAdministrator() == null || user.getAdministrator()) {
				user.setAuthSource(null); // no-op
			}
		} else if (user.getAdministrator() != null && user.getAdministrator()) {
			// current user is becoming an admin
			user.setAuthSource(ObidosConstants.AUTH_SOURCE_LOCAL);
		}
	}

	/**
	 * This validates user but also makes appropriate changes to the user object.
	 *
	 * @param caller
	 * @param user
	 * @param currentUser
	 * @param callerCapability
	 */
	private void validateUserChanges(final User caller, final UserDTO user, final User currentUser) {
		if (user.getAdministrator() != null) {
			ensureUserTypeIsNotChanging(user, currentUser);
		}

		if (user.getUsername() != null) {
			validateUsernameChange(caller, user, currentUser);
		}

		final boolean updateOfSelf = caller.self(user.getId());

		if (!updateOfSelf && (user.getPassword() != null || user.getUsername() != null)) { // Admin  is attempting to  reset the  credentials of another user/admin
			validateCredentialChangeCapability(caller, user, currentUser);
		}

		// Admin is locking/unlocking account
		if (user.getLocked() != null) {
			validateLockUnlockAction(caller, user, currentUser);
		}

		if (user.getAuthSource() != null) {
			ensureAuthSourceChangeIsValid(user, currentUser);
		}
	}

	private void setProfilePictureAttr(final User user) {
		if (loginActions.licenseIsExpired()) {
			throw new LicenseKeyException("Your license has expired. You may not set profile pictures. Please contact Spenego Software to update your license.");
		}
		user.adjustProfilePicEnabledFlag();
	}

	private static void setPasswordChangeRequiredAttr(final UserDTO user, final User currentUser, final User updatedUser) {
		if (passwordExpirationIsInFuture(user.getMaximumPasswordAge(), currentUser)) {
			if (isTrue(currentUser.getPasswordChangeRequired())) {
				updatedUser.setPasswordChangeRequired(false);
			}
		} else {
			if (isFalse(currentUser.getPasswordChangeRequired())) {
				updatedUser.setPasswordChangeRequired(true);
			}
		}
	}

	private void setPasswordAttrs(final User caller, final UserDTO user, final User updatedUser) {
		if (loginActions.licenseIsExpired()) {
			throw new LicenseKeyException("Your license has expired. You may not change passwords. Please contact Spenego Software to update your license.");
		}
		updatedUser.setPasswordChangeRequired(user.getPasswordChangeRequired());
		updatedUser.setPasswordDigest(getPasswordHash(user));
		updatedUser.setLastPasswordDigest3(caller.getLastPasswordDigest2());
		updatedUser.setLastPasswordDigest2(caller.getLastPasswordDigest1());
		updatedUser.setLastPasswordDigest1(caller.getPasswordDigest());
		updatedUser.setLastPasswordResetTime(new Date());

	}

	// An update done BY an Admin (could be updating a User or an Admin or herself)
	private Void adminUserUpdate(final User caller, final UserDTO user) throws RecordModifiedException, ServerSideException {
		final Long userId = user.getId();
		final boolean updateOfSelf = caller.self(userId);
		final User targetUser = getUser(userId);
		final CapabilityDTO capabilities = user.getCapabilities();

		if (updateOfSelf) {
			if (user.lockedIsTrue()) {
				throw new PermissionDeniedException("A separate admin is required to (un)lock this account.");
			}
		} else if (!caller.getRootAdmin() && targetUser.isAdmin()) {
			throw new PermissionDeniedException("Deputy Admins may not modify Admin accounts.");
		}

		validateUserChanges(caller, user, targetUser);

		final User updatedUser = convertUser(user);

		if (updatedUser.emptyFieldsExist()) {
			userOperations.update(getUser(userId).nullifyEmptyFields(updatedUser));
			updatedUser.nullifyEmptyFields(updatedUser);
		}

		if (updatedUser.getProfilePic() != null) {
			setProfilePictureAttr(updatedUser);
		}

		// Clear the 'password change required' flag if the admin has changed
		// the password age config
		if (user.getMaximumPasswordAge() != null) {
			setPasswordChangeRequiredAttr(user, targetUser, updatedUser);
		}

		if (user.getPassword() != null) {
			setPasswordAttrs(caller, user, updatedUser);
		}

		updatedUser.setVersion(targetUser.getVersion() + 1);
		updateUser(updatedUser);

		if (capabilities != null && (caller.getRootAdmin() || !targetUser.getAdministrator())) { // only root admins may modify admin capabilities
			if (isFalse(targetUser.getAdministrator()) && user.getAdministrator() == null) {
				capabilities.disableAdminCapabilities();
			}

			// This is just a safety check.  Deputy admins will never be able to modify other admins, regardless of capabilities. Those capabilities are deprecated. See Bug #69.
			if (!targetUser.getRootAdmin() && (capabilities.getCreateAdmin() || capabilities.getDeleteAdmin() || capabilities.getLockAdmin() || capabilities.getModifySettings())) {
				throw new PermissionDeniedException("You may not grant Deputy admins the capabilities to modify other admins.");
			}

			updateCapabilities(caller, user, capabilities);
		}

		audit(UPDATE_USER, caller.getUsername(), caller.getId(), user.getUsername(), userId);
		return null;
	}

	private static void addMod(final StringBuilder sb, final Object o, final String att) {
		if (o != null) {
			if (sb.length() != 0) {
				sb.append(", ");
			}
			sb.append(att);
		}
	}

	private static void validateFields(final UserDTO user) {
		// validate userDTO Bug #87
		final UserDTO validateDTO = new UserDTO();
		// we can not validate passed user DTO as it may have some fields null during modify.
		// so make a copy of it and fill up must have fields with fake data (it is not harmful as data in
		// database is already validated during creation and have those fields)
		BeanUtils.copyProperties(user, validateDTO);
		// during modify password will be null
		if (user.getPassword() == null) { // ugly hack
			validateDTO.setPassword("This is a fake password to pass validation");
		}
		if (validateDTO.getUsername() == null) { // ugly hack
			validateDTO.setUsername("something");
		}
		if (validateDTO.getEmail1() == null) { // ugly hack
			validateDTO.setEmail1("joe@example.com");
		}
		ValidateBean.validate(validateDTO);// we most certainly need to validate
	}

	private void ensureSelfUpdatesArePermitted(final User caller, final UserDTO user) {
		final boolean updateOfSelf = caller.self(user.getId());

		if (!updateOfSelf) {
			throw new PermissionDeniedException("You may not modify this user.");
		}
		if (user.getLocked() != null) {
			throw new PermissionDeniedException("Only admins may change lock status of accounts.");
		}
		if (user.getUsername() != null) {
			throw new PermissionDeniedException("You may not change your username.");
		}
		if (user.adminIsEnabled()) {
			throw new PermissionDeniedException("You may not make yourself an administrator.");
		}
		if (user.getAuthSource() != null) {
			throw new PermissionDeniedException("You may not change your authorization source.");
		}
		if (user.getEmail1() != null) {
			throw new PermissionDeniedException("You may not change your primary email address.");
		}

		if (loginActions.licenseHasEnteredSecondGracePeriod()) {
			throw new LicenseKeyException("Your license has expired. You may not set user preferences. Please contact Spenego Software to update your license.");
		}
	}

	private User updateUser(final User caller, final UserDTO user) {
		final User sanitizedUser = new User(convertUser(user));

		if (user.getPassword() != null) {
			sanitizedUser.setPasswordChangeRequired(user.getPasswordChangeRequired());
			sanitizedUser.setLastPasswordDigest3(caller.getLastPasswordDigest2());
			sanitizedUser.setLastPasswordDigest2(caller.getLastPasswordDigest1());
			sanitizedUser.setLastPasswordDigest1(caller.getPasswordDigest());
			sanitizedUser.setPasswordDigest(getPasswordHash(user));
			sanitizedUser.setLastPasswordResetTime(new Date());
		}

		if (sanitizedUser.emptyFieldsExist()) {
			userOperations.update(getUser(user.getId()).nullifyEmptyFields(sanitizedUser));
			sanitizedUser.nullifyEmptyFields(sanitizedUser);
		}
		sanitizedUser.setVersion(caller.getVersion() + 1);
		updateUser(sanitizedUser);

		return sanitizedUser;
	}

	private static void setMobileE164Number(final User user) {
		String mobilePhone = user.getMobile1();
		if (mobilePhone != null && mobilePhone.length() > 0)
		{
			// Exception will be thrown if phone number is no E.164 format
			logger.info(() -> "MMM validate mobile number; " + mobilePhone);
			String e164Number = ServerUtils.validatePhoneNumber(mobilePhone);
			user.setMobile1(e164Number);
		}
	}

	private static void logUserCapabilities(final UserDTO user) {
		final CapabilityDTO cdto = user.getCapabilities();
		logger.info(()-> "MMM in UserManagementActionImpl " + user.getUsername() + " can modify system settings: " + cdto.getModifySettings());
		logger.info(() -> "User accept notification = " + user.getAcceptEmailNotification());
	}

	@Override
	public Void modifyUser(final User caller, final UserDTO user) {
		if (isTrue(caller.getLocked())) {
			throw new PermissionDeniedException("Your account is locked.");
		}

		if (!caller.isAdmin() && !caller.self(user.getId())) {
			throw new PermissionDeniedException("You may not modify other users.");
		}

		if (user.getUsername() != null) {
			imposeUsernameRestrictions(user.getUsername());
		}

		logUserCapabilities(user);
		validateFields(user);

		logger.info(() -> "MMM phone: " + "'" + user.getPhone() + "'");
		String mobilePhone = user.getMobile1();
		if (mobilePhone != null && mobilePhone.length() > 0) {
			// Exception will be thrown if phone number is no E.164 format
			logger.info(() -> "MMM validate mobile number; " + mobilePhone);
			user.setMobile1(ServerUtils.validatePhoneNumber(mobilePhone));
		}

		if (caller.isAdmin()) {
			// If admin has changed the password, set password change required.
			// spgdev@spenego.com - May 21, 2020
			if (user.getPassword() != null) {
				logger.info(() -> "Password is changed, set password change required field");
				user.setPasswordChangeRequired(true);
			}
			return adminUserUpdate(caller, user);
		}

		ensureSelfUpdatesArePermitted(caller, user);

		final User updatedUser = updateUser(caller, user);
		final StringBuilder sb = new StringBuilder();

		if (user.getPassword() != null) {
			sb.append("password, ");
		}

		addMod(sb, updatedUser.getEmail2(), "email2");
		addMod(sb, updatedUser.getComments(), "comments");
		addMod(sb, updatedUser.getDefaultPage(), "default page");
		audit(UPDATE_USER, caller.getUsername(), caller.getId(), user.getUsername(), user.getId(), null, sb);

		return null;
	}

	/**
	 * Deleted usernames must be unique and preserve as much of the original
	 * username as possible. We append the current id as a means to keep the
	 * username unique.
	 *
	 * @param currentUsername
	 * @param id
	 * @return
	 */
	private static String deletedUsername(final String currentUsername, final Long id) {
		final String suffix = USERNAME_SEPERATOR + id;
		int endIndex = 63 - suffix.length();
		return ((endIndex < currentUsername.length()) ? currentUsername.substring(0, endIndex) : currentUsername) + suffix;
	}

	private String restoredUsername(final String currentUsername, final Long suffix) {
		final String newName = (currentUsername.contains(USERNAME_SEPERATOR) || suffix == null) ? currentUsername.replaceAll(USERNAME_SEPERATOR + ".*", suffix == null ? "" : suffix.toString()) : currentUsername + suffix;

		try {
			userOperations.getUserByUsername(newName);
			logger.info(() -> "Could not restore username " + newName + ", it is in use by another user.");
			return restoredUsername(currentUsername, suffix == null ? 1L : suffix + 1);
		} catch (final NoSuchRecordException ex) {
			return newName;
		}
	}

	private static void userDeleteCapabilityCheck(final User admin, final User user) {
		if (user.isAdmin()) {
			if (!admin.getDeleteAdmin()) {
				throw new PermissionDeniedException("You do not have the capability to delete an admin.");
			}
		} else if (!admin.getDeleteUser()) {
			throw new PermissionDeniedException("You do not have the capability to delete a user.");
		}
	}

	private void ensureSharedFlagIsStillAccurate(final SharedItem sharedItem) {
		if (itemAssignmentOperations.getItemShareCount(sharedItem.getItemId()).equals(1)) {
			itemOperations.updateSelective(new Item(sharedItem.getItemId(), false));
		}
	}

	private void deleteAllEmptyContainersForUser(final User user) {
		containerOperations.getAssignedContainers(user.getId())
				.filter(c -> !c.getContainerId().equals(ContainerDTO.NOTEBOOK_ID) && itemOperations.countItemsInContainer(c.getId()).equals(0))
				.forEach(c -> containerOperations.delete(c.getContainerId()));
	}

	private void tombstone(final User caller, final User user) {
		final Long userId = user.getId();

		if (caller.self(userId)) {
			throw new PermissionDeniedException("You may not delete yourself.");
		}

		if (!caller.getRootAdmin() && isTrue(user.getAdministrator())) {
			throw new PermissionDeniedException("You do not have permission to other admins.");
		}

		userDeleteCapabilityCheck(caller, user);

		if (isTrue(user.getDeleted())) {
			throw new ServerSideException("This user has already been tombstoned. Specify 'delete permanently' to remove from database.");
		}

		logger.syslogInfo(() -> "Marking user " + user.getUsername() + "(" + user.getUsername() + ") as tombstoned.");
		updateUser(new User(userId, null, user.getVersion() + 1, true, true));
		audit(USER_TOMBSTONED, caller.getUsername(), caller.getId(), user.getUsername(), userId);
	}

	/**
	 * Users are never really deleted.  We keep them and just rename the usernames. This is required so that
	 * Audit logs make sense.
	 *
	 * @param admin
	 * @param capability
	 * @param user
	 */
	private void permDelete(final User admin, final User user) {
		final Long userId = user.getId();

		if (userId.equals(ObidosConstants.FIRST_ROOT_ADMIN_ID)) {
			return; // silently ignore tombstoning of Initial Root Admin
		}

		updateUser(new User(userId, deletedUsername(user.getUsername(), userId), user.getVersion() + 1, true, true));

		// delete all items/containers/assignments of this user
		final Stream<SharedItem> sharedItemStream = itemOperations.getItemsSharedWithUser(user.getId(), null, null, null, null, null, null, null);
		itemOperations.deleteAllUnsharedUserItems(userId);
		groupMemberOperations.deleteAllUserMemberships(userId);
		deleteAllEmptyContainersForUser(user);
		sharedItemStream.forEach(this::ensureSharedFlagIsStillAccurate);
		audit(USER_TOMBSTONED, admin.getUsername(), admin.getId(), user.getUsername(), userId);
	}

	/**
	 * Updates users to have the delete flag set to true. Leaving the user
	 * account in-tact permits audit logs to be usable until the username is
	 * actually deleted.
	 *
	 * @param caller
	 * @param userId
	 * @return
	 */
	private Void tombstone(final User admin, final Collection<Long> ids) {
		return processStream(() -> "tombstoning users", ids::stream, id -> tombstone(admin, getModel(id)), () -> "marking user deleted: ");
	}

	private Void deleteUsers(final User admin, final Collection<Long> ids) {
		return processStream(() -> "deleting users", ids::stream, id -> permDelete(admin, getModel(id)), () -> "marking user deleted: ");
	}

	@Override
	public Void deleteUsers(final User admin, final Collection<Long> ids, final Boolean deletePermanently) {
		if (ids == null || ids.isEmpty()) {
			throw new ServerSideException("No users to delete were specified.");
		}

		if (isFalse(deletePermanently)) {
			return tombstone(admin, new ArrayList<>(ids));
		}

		// ensure all users have previously been tombstoned
		final int userCount = userOperations.getCount(admin.getId(), new User(false), ids);

		if (userCount > 0 && !admin.getDeleteUser()) {
			throw new PermissionDeniedException("You do not have the capability to delete users.");
		}

		if (userCount < ids.size()) {
			final int adminCount = userOperations.getCount(admin.getId(), new User(true), ids);

			if (adminCount + userCount < ids.size()) {
				throw new ServerSideException("Users in list are fewer than available in database. Perhaps users were already deleted.");
			}

			if (!admin.getDeleteAdmin()) {
				throw new PermissionDeniedException("You do not have the capability to delete admins.");
			}
		}

		final int diff = ids.size() - userOperations.countTombstonedUsers(ids);

		if (diff > 0) {
			throw new PermissionDeniedException(diff == 1 ? "That user must be tombstoned first." : ("" + diff + " users have not yet been tombstoned."));
		}

		if (isTrue(deletePermanently)) {
			throw new NotImplementedException("Permanent user deletion feature is unavailable.");
		}

		return deleteUsers(admin, ids);
	}

	@Override
	public Void deleteUsersByUsername(final User admin, final Collection<String> userNames, final Boolean deletePermanently) {
		return deleteUsers(admin, userOperations.getUserIds(userNames), deletePermanently);
	}

	private void restoreUser(final User admin, final Long userId, final String requestedUsername) {
		final User user = getUser(userId);

		userDeleteCapabilityCheck(admin, user);

		if (isFalse(user.getDeleted()))	{ throw new ServerSideException("This user has not been marked deleted."); }

		boolean userRestored = false;

		if (!user.isAdmin()) {
			licenseCheck(admin, getSystemConfig(), "restore user");
		}

		try {
			updateUser(new User(user.getId(), requestedUsername, user.getVersion() + 1, false, false));
			userRestored = true;
		} catch (final ServerSideException ex) {
			logger.info(() -> "Caught " + ex);

			/*
			 * if we were unable to restore the user with the requested
			 * username, pass through so we can try a generated one
			 */
		}

		if (!userRestored) {
			updateUser(new User(user.getId(), restoredUsername(user.getUsername(), null), user.getVersion() + 1, false, false));
		}

		final User restoredUser = getUser(user.getId());
		logger.info(() -> "Restoring deleted user " + user + "(" + restoredUser.getUsername() + ").");
		audit(USER_RESTORED, admin.getUsername(), admin.getId(), restoredUser.getUsername(), user.getId());
	}

	@Override
	public Void restoreUsers(final User admin, final List<Long> userIds, final List<String> requestedUsernames) {
		int i = 0;

		for (final Long userId : userIds) {
			restoreUser(admin, userId, requestedUsernames == null ? null : requestedUsernames.get(i++));
		}

		return null;
	}

	private void userLockAction(final User admin, final User user, final Boolean locked, final Integer action) {
		if (user.isAdmin()) {
			if (user.getId().equals(ObidosConstants.FIRST_ROOT_ADMIN_ID)) {
				return; // silently ignore locking of Initial Root Admin
			}
			if (!admin.getLockAdmin()) {
				throw new PermissionDeniedException("You do not have the capability to lock an admin.");
			}
		} else if (!admin.getLockUser()) {
			throw new PermissionDeniedException("You do not have the capability to lock a user.");
		}

		logger.syslogInfo(() -> (isFalse(locked) ? "Locking" : "Unlocking") + " user " + user.getUsername());
		if (isFalse(locked) && !user.isAdmin()) {
			licenseCheck(admin, getSystemConfig(), "unlock user");
		}
		updateUser(new User(user.getId(), null, user.getVersion() + 1, null, locked));
		audit(action, admin.getUsername(), admin.getId(), user.getUsername(), user.getId());
	}

	private void userTombstoneAction(final User caller, final User user, final Integer action) {
		if (user.isAdmin()) {
			if (!caller.getRootAdmin()) {
				throw new PermissionDeniedException("You do not have the capability to untombstone an admin.");
			}
		} else if (!caller.getDeleteUser()) { // May only un-timbstone if they can tombstone or delete users
			throw new PermissionDeniedException("You do not have the capability to untombstone a user.");
		}

		logger.syslogInfo(() -> "Untombstoning user " + user.getUsername());
		if (!user.isAdmin()) {
			licenseCheck(caller, getSystemConfig(), "untombstone user");
		}
		updateUser(new User(user.getId(), null, user.getVersion() + 1, false, false));
		audit(action, caller.getUsername(), caller.getId(), user.getUsername(), user.getId());
	}

	private Void processUsers(final Collection<Long> userIds, final Supplier<String> actionSupplier, final Consumer<Long> consumer, final String exceptionMsg) {
		return processStream(actionSupplier, userIds::stream, consumer, () -> exceptionMsg);
	}

	@Override
	public Void lockUsers(final User admin, final Collection<Long> userIds) {
		return processUsers(userIds, () -> "locking users", id -> userLockAction(admin, getUser(id), true, USER_LOCKED), "marking user locked: ");
	}

	@Override
	public Void unLockUsers(final User admin, final Collection<Long> userIds) {
		return processUsers(userIds, () -> "un-locking users", id -> userLockAction(admin, getUser(id), false, USER_UNLOCKED), "marking user unlocked: ");
	}

	@Override
	public Void unTombstoneUsers(final User admin, final Collection<Long> userIds) {
		return processUsers(userIds, () -> "un-tombstoning users", id -> userTombstoneAction(admin, getUser(id), USER_UNTOMBSTONED), "marking user untombstoned: ");
	}

	@Override
	public Void changePassword(final User user, final String oldPassword, final String newPassword) {
		if (!user.getAuthSource().equals(ObidosConstants.AUTH_SOURCE_LOCAL)) {
			throw new ServerSideException("Account does not use local authentication.");
		}
		if (oldPassword == null || newPassword == null || newPassword.isEmpty()) {
			throw new ServerSideException("Password may not be null.");
		}
		if (!verifyPassword(oldPassword, user.getPasswordDigest())) {
			throw new ServerSideException("Old password does not match.");
		}
		if (oldPassword.equals(newPassword)) {
			throw new ServerSideException("New password must be different from old password.");
		}
		if (getPasswordScore(newPassword, user) < MIN_PASSWORD_STRENGTH) {
			throw new ServerSideException("Password is not strong enough.");
		}

		validatePassComplexity(user, newPassword, PASSWORD_COMPLEXITY_REQUIREMENTS);

		updateUser(new User(user.getId(), user.getVersion() + 1, false, getPasswordHash(newPassword), user.getPasswordDigest(), user.getLastPasswordDigest1(),
				user.getLastPasswordDigest2()));
		audit(UPDATE_USER, user.getUsername(), user.getId(), user.getUsername(), user.getId(), null, "password");

		return null;
	}

	private Void updateKeyPair(final User user, final String publicKey, final String privateKey, final String nonce, final String salt)
			throws RecordModifiedException, ServerSideException {
		updateUser(new User(user.getId(), user.getVersion() + 1, publicKey, privateKey, nonce, salt));
		return null;
	}

	private static void validatePassphrase(final byte[] passphrase) {
		if (passphrase == null || passphrase.length == 0) {
			throw new ServerSideException("Passphrase may not be empty");
		}
	}

	private void createKeyPairInternal(final User user, final byte[] passphrase, final String action, final Runnable r) {
		validatePassphrase(passphrase);
		createKeypair(passphrase,
				(final String publicKey, final String privateKey, final String nonce, final String salt) -> updateKeyPair(user, publicKey, privateKey, nonce, salt));
		if (r != null) {
			r.run();
		}
		audit(UPDATE_USER, user.getUsername(), user.getId(), user.getUsername(), user.getId(), null, action);
	}

	@Override
	public Void createKeypair(final User user, final byte[] passphrase) {
		if (isTrue(keypairExists(user, user.getId()))) {
			throw new ServerSideException("Keypair already exists.");
		}
		createKeyPairInternal(user, passphrase, "keypair", null);

		return null;
	}

	private void createNewKeypair(final User user, final byte[] newPassphrase) {
		createKeyPairInternal(user, newPassphrase, "new keypair", () -> {
			itemActions.deleteAllUserItems(user);
			createNotification(user.getId(), NotificationDTO.PASSPHRASE_RESET);
		});
	}

	private void createNewKeypairWith2FA(final User user, final byte[] twoFACode, final byte[] newPassphrase) {
		if (isFalse(user.getTwoFAPasswordResetEnabled())) {
			throw new PermissionDeniedException("Before you reset your password, you must enable Two-Factor Authentication");
		}
		if (twoFACode == null) {
			throw new PermissionDeniedException("Passphrase reset requires Two-Factor Authentication, but no code was supplied.");
		}
		logger.info(() -> "Resetting Passphrase for user " + user.getUsername() + " via 2FA code");
		authenticate2FA(user, twoFACode);
		createNewKeypair(user, newPassphrase);
	}

	private Void processPasswordResetToken(final User user, final String passwordResetToken, final byte[] newPassphrase) {
		logger.info(() -> "Resetting Passphrase for user " + user.getUsername() + " via Password Reset Token");
		return passwordResetActions.processPasswordResetToken(passwordResetToken, resetRequest -> {
			if (!user.self(resetRequest.getUserId())) {
				logger.warn(() -> "User " + user.getUsername() + " attempted to use password reset token for user " + getModel(resetRequest.getUserId()).getUsername());
				throw new PermissionDeniedException("Password Reset Token was for another user");
			}
			createNewKeypair(user, newPassphrase);
		});
	}

	@Override
 	public Void resetPassphrase(final User user, final String password, final byte[] newPassphrase, final String passwordResetToken, final byte[] twoFACode, final PassphraseHash passphraseHash) {
		if (passphraseHash != null) {
			boolean passphraseCorrect = false;
			try {
				validatePassphraseHash(user, passphraseHash);
				passphraseCorrect = true;
			} catch (final Exception ex) {
			/*
			 * do not let the validation exception pass through here, just
			 * leave passphraseCorrect flag as false
			 */ }

			if (passphraseCorrect) {
				throw new ServerSideException("Your current passphrase is valid. Update your passphrase instead of resetting it. Resetting discards all items.");
			}
		}

		final User autenticatedUser = getAuthenticatedUser(user.getUsername(), password, false);

		if (!user.self(autenticatedUser.getId())) { // shouldn't be possible, just a sanity check
			throw new PermissionDeniedException("Authenticated to alternate user!");
		}

		// If password/passphrase reset via 2FA is required, it must be used.
		if (user.getTwoFARequired() || user.getTwoFAPasswordResetEnabled()) {
			createNewKeypairWith2FA(user, twoFACode, newPassphrase);
		} else {
			if (passwordResetToken != null) {
				return processPasswordResetToken(user, passwordResetToken, newPassphrase);
			}

			logger.warn(() -> "User " + user.getUsername() + " attempted to reset passphrase, however, neither password reset token nor 2FA codes were supplied.");
		}

		return null;
	}

	@Override
	public Void updatePassphrase(final User user, final PassphraseHash passphraseHash, final byte[] newPassphrase, final byte[] twoFACode, final Consumer<User> consumer) {
		if (isFalse(keypairExists(user, user.getId()))) {
			throw new ServerSideException("Keypair has not been created yet.");
		}
		validatePassphrase(newPassphrase);
		validatePassComplexity(user, new String(newPassphrase), PASSPHRASE_COMPLEXITY_REQUIREMENTS);

		updatePrivateKey(encryption.decryptPrivateKey(user, passphraseHash), newPassphrase,
				(final String publicKey, final String encryptedPrivateKey, final String nonce, final String salt) -> {
					updateKeyPair(user, publicKey, encryptedPrivateKey, nonce, salt);
					user.setSalt(salt);
					user.setPrivatekey(encryptedPrivateKey);
					user.setNonce(nonce);
					if (consumer != null) {
						consumer.accept(user);
					}
				});

		audit(UPDATE_USER, user.getUsername(), user.getId(), user.getUsername(), user.getId(), null, "passphrase");

		return null;
	}

	@Override
	public Integer getTotalUserCount(final User caller) {
		return userOperations.getCount(caller == null ? null : caller.getId(), null, null, null);
	}

	private ComplexityRequirementsDTO get(final List<ComplexityRequirements> lcr, final Byte type) {
		return convert(lcr.get(lcr.get(0).getType().equals(type) ? 0 : 1), ComplexityRequirementsDTO.class);
	}

	@Override
	public PassComplexityDTO getPasswordComplexity(final String name) {
		final List<ComplexityRequirements> lcr = complexityRequirementsOperations.get(name == null ? getSystemConfig().getPasswordComplexityName() : name);

		return new PassComplexityDTO(get(lcr, PASSWORD_COMPLEXITY_REQUIREMENTS), get(lcr, PASSPHRASE_COMPLEXITY_REQUIREMENTS));
	}

	private ComplexityRequirements convert(final ComplexityRequirementsDTO dto, byte type) {
		final ComplexityRequirements v = convert(dto, ComplexityRequirements.class);

		v.setType(type);

		return v;
	}

	@Override
	public Void createPasswordComplexity(final User admin, final PassComplexityDTO rules) {
		final ComplexityRequirementsDTO pwCr = rules.getPasswordComplexityRequirements();
		final ComplexityRequirementsDTO ppCr = rules.getPassphraseComplexityRequirements();

		if (pwCr == null || ppCr == null) {
			throw new ServerSideException("You must populate both Password and Passphrase complexity requirements.");
		}

		if (pwCr.getName() == null || ppCr.getName() == null) {
			throw new ServerSideException("You must specify a name for both Password and Passphrase complexity requirements.");
		}

		if (!pwCr.getName().equals(ppCr.getName())) {
			throw new ServerSideException("The Password and Passphrase complexity requirements names must match.");
		}

		complexityRequirementsOperations.create(convert(pwCr, PASSWORD_COMPLEXITY_REQUIREMENTS));
		complexityRequirementsOperations.create(convert(ppCr, PASSPHRASE_COMPLEXITY_REQUIREMENTS));

		return null;
	}

	private void validateExtraPassComplexity(final User user, final String pass, final ComplexityRequirementsDTO complexityRequirements, final String passTypeStr) {
		if (complexityRequirements.getContainsPhoneCheck() && user.getPhone() != null) {
			// Phone can be null
			ServerUtils.validatePassDoesNotContain(pass, user.getPhone());
		}
		if (complexityRequirements.getContainsFullnameCheck()) {
			ServerUtils.validatePassDoesNotContain(pass, user.getFullname());
		}
		if (complexityRequirements.getContainsEmailCheck()) {
			ServerUtils.validatePassDoesNotContain(pass, user.getEmail1());
		}
		if (complexityRequirements.getContainsUsernameCheck()) {
			ServerUtils.validatePassDoesNotContain(pass, user.getUsername());
		}

		// not sure about that, as it makes network connection witch
		// every character typed. UI has an option, user can check if
		// desired.
		if (complexityRequirements.getHaveIBeenPwnedCheck() && haveIBeenPwnedActions.checkPassword(pass) != null) {
			throw new ServerSideException(passTypeStr + " likely exists in Have I Been Pwned database.");
		}
	}

	@Override
	public Void validatePassComplexity(final User user, final String pass, final byte passType) throws ServerSideException {
		final ComplexityRequirementsDTO complexityRequirements = getPasswordComplexity(null).get(passType);
		final String passTypeStr = ComplexityRequirementsDTO.getTypeName(passType);

		ServerUtils.validateComplexity(pass, passTypeStr, complexityRequirements);

		// enable extra checks by default, it is silly to allow these in
		// password. Why should any admin allow these in password?
		final Long extraChecks = 1L;
		complexityRequirements.setContainsFullnameCheck();
		complexityRequirements.setContainsEmailCheck();
		complexityRequirements.setContainsUsernameCheck();
		complexityRequirements.setContainsPhoneCheck();

		if (user != null && !extraChecks.equals(0L)) {
			validateExtraPassComplexity(user, pass, complexityRequirements, passTypeStr);
		}

		return null;
	}

	@Override
	public LimitedUserResult getLoggedInUsers(final User admin, final Integer secondsSinceLastActivity) {
		final long time_threshold = System.currentTimeMillis() - (secondsSinceLastActivity * 1000);
		final int size = activeUsers.size();
		final List<Long> active   = new ArrayList<Long>(size);
		final List<Long> inActive = new ArrayList<Long>(size);

		activeUsers.forEach((id, last_activity_timestamp) -> { if (last_activity_timestamp >= time_threshold) { active.add(id); } else { inActive.add(id);}});

		// The only time the list of users is cleaned is when this function is called.  This will allow us to also report how many users just never logged out.
		if (inActive.size() > 0) {
			logger.info(() -> "" + inActive.size() + " users found to have left sessions inactive without logging out.");
			inActive.forEach(id -> activeUsers.remove(id));
		}

		return new LimitedUserResult(0, 0, null, (a,b) -> convert(a,b), () -> active.size(), () -> active.stream().map(id -> userOperations.get(id)));
	}

	@Override
	public Void refreshUser(final User user) {
		activeUsers.put(user.getId(), System.currentTimeMillis());

		return null;
	}

	@Override
	public Void logout(final User user) {
		activeUsers.remove(user.getId());
		return null;
	}
}
