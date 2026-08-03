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

import static com.spenego.Obidos.server.model.Audit.EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.FAILED_LOGIN_ATTEMPT;
import static com.spenego.Obidos.server.model.Audit.LOGIN;
import static com.spenego.Obidos.server.model.Audit.LOGOUT;
import static com.spenego.Obidos.server.model.Audit.PASSWORD_EXPIRED;
import static com.spenego.Obidos.server.utils.ServerUtils.isCurrentTransactionReadOnly;
import static com.spenego.Obidos.shared.dto.LicenseStats.LICENSE_STATE_BEYOND_GRACE_PERIOD;
import static com.spenego.Obidos.shared.dto.LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH;
import static com.spenego.Obidos.shared.dto.LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH;
import static com.spenego.Obidos.shared.dto.PasswordAnalysisResults.ALGORITHM_DROPBOX_ZXCVBN;
import static com.spenego.Obidos.shared.dto.PasswordAnalysisResults.PASSWORD_STRONG;
import static com.spenego.Obidos.shared.dto.PasswordAnalysisResults.PASSWORD_WEAK;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.nulabinc.zxcvbn.Feedback;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.spenego.Obidos.server.ObidosDispatcherUtil;
import com.spenego.Obidos.server.actions.CapabilityActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Model;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.services.impl.GenPassServiceImpl;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LoginActionDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.LoginType;
import com.spenego.Obidos.shared.dto.NotificationDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.ObidosAuthenticationException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.SessionErrorException;
import com.spenego.strongpass.StrengthChecker;
import com.spenego.strongpass.StrongPassEntropy;

public final class LoginActionsImpl extends ObidosActions<Model> implements LoginActions {
	private static final Logger logger = LoggerFactory.getLogger(LoginActionsImpl.class);

	@Autowired private final Authenticator				authenticator = null;
	@Autowired private final CapabilityActions			capabilityActions = null;
	@Autowired private final SystemConfigActions		systemConfigActions = null;
	@Autowired protected final UserManagementActions	userManagementActions = null;

	private static final ThreadLocal<LicenseCache> localLicenseCache = new ThreadLocal<>();

	private static final long LICENSE_CACHE_EXPIRE_TIME = MS_PER_MIN * 5;	// cache license for maximum of 5 minutes

	private static final class LicenseCache {
		Long			cacheExpireTime;
		LicenseStats	licenseStats;

		LicenseCache(final Long cacheExpireTime, final LicenseStats licenseStats) {
			this.cacheExpireTime	= cacheExpireTime;
			this.licenseStats		= licenseStats;
		}
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	protected final Operations<Model> getOperations() {
		return null;
	}

	protected final Integer getAuditDeleteAction() {
		return EXCEPTION; // delete does not make sense for this action
	}

	/**
	 * returns UserDTO if the user can be authenticated from her credentials
	 *
	 * @param username
	 * @param password
	 * @return UserDTO on success null on failure
	 *         <p>
	 * @author spgdev@spenego.com - Dec 30, 2016
	 */
	private User getUserFromCredentials(final String username, final String password) {
		return authenticator.authenticateCredentials(username, password, true);
	}

	/**
	 * returns User if the user can be authenticated from session
	 *
	 * @param loggedInCookie
	 *            The session id which was sent previously when the user
	 *            authenticated with her credentials. The user stored the
	 *            session id to a cookie
	 * @return UserDTO on success, null on failure
	 *         <p>
	 * @author spgdev@spenego.com - Dec 30, 2016
	 */
	private User getUserFromCookie(final String loggedInCookie) {
		return authenticator.authenticateCookie(loggedInCookie);
	}

	private void updateUserSelective(final User u) {
		try {
			updateUser(u);
		} catch (final Exception ex) {
			logger.exception(ex);
		}
	}

	private User updateLastLogin(final User user, final boolean viaCookie) {
		updateUserSelective(new User(user.getId(), user.getVersion() + 1, viaCookie, user.getLoginCount() + 1));
		return user;
	}

	private void updateLoginAttemptFailure(final String username) {
		final User user = userOperations.getUserByUsername(username);
		updateUserSelective(new User(user.getId(), user.getVersion() + 1, user.getUnsuccessfulLoginAttempts() + 1));
	}

	private User updatePasswordChangeRequired(final User user) {
		final User u = new User(user.getId(), user.getVersion() + 1);
		u.setPasswordChangeRequired(true);
		updateUserSelective(u);
		return user;
	}

	private void notifyUserOfPendingPasswordExpiration(final User caller, int daysUntilPasswordExpiration) {
		final String day = daysUntilPasswordExpiration > 1 ? " days." : " day.";
		postCommitQueue(() -> notifyRecipient(NotificationDTO.PASSWORD_EXPIRING_SOON, caller, caller.getId(), null, null, null,
				"Your password will expire in " + daysUntilPasswordExpiration + day, null));
	}

	private User getUserFromLoginAction(final LoginActionDTO loginAction, final String remoteAddr) {
		try {
			if (loginAction.getLoginType() == LoginType.VIA_COOKIE) {
				logger.info(() -> "Login type is VIA_COOKIE");
				return updateLastLogin(getUserFromCookie(loginAction.getXsrfToken()), true).adjustRootAdminCapabilities();
			}

			logger.info(() -> "Login type is VIA_CREDENTIALS, username = " + loginAction.getUsername());
			return updateLastLogin(getUserFromCredentials(loginAction.getUsername(), loginAction.getPassword()), false).adjustRootAdminCapabilities();
		} catch (final Exception ex) {
			logger.info(() -> "Failed login attempt: " + ex);
			final String username = loginAction.getUsername();
			if (username != null) {
				updateLoginAttemptFailure(username);
			}
			auditExceptionAction(FAILED_LOGIN_ATTEMPT, username, null, remoteAddr, null, null, ex.getMessage());
			throw ex;
		}
	}

	private UserDTO setNotificationCount(final UserDTO user) {
		user.setNotificationCount(getNotificationCount(user.getId()));
		return user;
	}

	private UserDTO setCapabilities(final UserDTO user) {
		user.setCapabilities(capabilityActions.get(user));
		return user;
	}

	@Override
	public UserDTO setDaysUntilPasswordExpiration(final UserDTO user, final int daysUntilPasswordExpiration) {
		user.setDaysUntilPasswordExpiration(daysUntilPasswordExpiration);
		return user;
	}

	@Override
	public int passwordAgeInDays(final User user) {
		final Date lastPasswordResetTime = user.getLastPasswordResetTime();

		return lastPasswordResetTime == null ? 0 : (int) ((System.currentTimeMillis() - lastPasswordResetTime.getTime()) / MS_PER_DAY);
	}

	private static String getPasswordExpirationString(int daysUntilPasswordExpiration, final User user) {
		return "" + (-daysUntilPasswordExpiration) + " days ago, last changed at " + user.getLastPasswordResetTime();
	}

	private static int passwordExpirationWarningThreshold(int maxPasswordAgeInDays) {
		int d1 = (int) (maxPasswordAgeInDays * 0.1);
		return d1 > 2 ? d1 : 2;
	}

	private int getDaysUntilPasswordExpiration(final User user) {
		return (user.getMaximumPasswordAge() == null || user.getMaximumPasswordAge().equals(0L)) ? ObidosConstants.PASSWORD_NEVER_EXPIRES
				: (int) ((long) user.getMaximumPasswordAge()) - passwordAgeInDays(user);
	}

	private LoginResult setLicense(final LoginResult lr, final boolean admin) {
		try {
			final LicenseStats ls = currentLicenseStats();
			lr.getUserDTO().setLicense(ls);
			lr.setLicenseStats(ls);
		} catch(final LicenseKeyException ex) {
			final LicenseStats ls = new LicenseStats();
			lr.getUserDTO().setLicense(ls);
			lr.setLicenseStats(ls);
		} catch(final ServerSideException ex) {
			if (!admin) {
				throw ex;	// we need to allow admins to login when there is a license exception in order to fix the license.
			}
			logger.warn(() -> "Caught license exception: " + ex.getMessage() + " but continuing since user is admin.");
			lr.setLicenseStats(new LicenseStats());
		}
		return lr;
	}

	private int maxPasswordAgeInDays(final String name) {
		final Integer days = userManagementActions.getPasswordComplexity(name).getPasswordComplexityRequirements().getMaxAgeInDays();
		return days == null ? 0 : days;
	}

	@Override
	public LoginResult createLoginResult(final User user, final SystemConfig sc, final int sessionTimeout) throws ObidosAuthenticationException {
		final int days = getDaysUntilPasswordExpiration(user);
		final UserDTO userDTO = convert(user, UserDTO.class);

		userDTO.setDaysUntilPasswordExpiration(days);
		userDTO.setSessionTimeoutSeconds(sessionTimeout);
		final SystemConfig msc = sc == null ? getSystemConfig() : sc;

		return setLicense(new LoginResult(authenticator.getSavedSessionHash(), authenticator.getSessionInfo().getMaxInactiveInterval(), maxPasswordAgeInDays(msc.getPasswordComplexityName()), setCapabilities(setNotificationCount(userDTO)), msc.getDateFormat()), user.isAdmin());
	}

	private final LicenseCache cacheLicense(final long now) {
		final LicenseKeyDTO license = systemConfigActions.getCurrentLicense();

		if (license == null) {
			throw new LicenseKeyException("Unable to load current license.");
		}

		final LicenseCache licenseCache = new LicenseCache(now + LICENSE_CACHE_EXPIRE_TIME, new LicenseStats(now, license.getExpirationEpoch(), license.getMaxUsers(), userManagementActions.getTotalUserCount(null), license.getHasExpired(),
				license.getAllowAuditing(), license.getAllowSMTP(), license.getSnmpSupport(), license.getAllowDocumentUploading(), license.getAllowContainerSharing(), license.getAllowQRCodeUploading(), license.getSmsSupport()));

		localLicenseCache.set(licenseCache);

		return licenseCache;
	}

	public LicenseStats updateCachedLicense() {
		LicenseCache cache = cacheLicense(System.currentTimeMillis());
		return cache.licenseStats;
	}

	private final LicenseCache currentLicense(final long now) {
		final LicenseCache licenseCache = localLicenseCache.get();

		return (licenseCache != null && now < licenseCache.cacheExpireTime) ? licenseCache : cacheLicense(now);
	}

	@Override
	public final LicenseStats currentLicenseStats() {
		return currentLicense(System.currentTimeMillis()).licenseStats;
	}

	@Override
	public final boolean licenseHasEnteredSecondGracePeriod() {
		final LicenseStats licenseStats = currentLicenseStats();

		return licenseStats.getExpirationEpoch() != null && licenseStats.getHasExpired() && licenseStats.getLicenseExpirationState() != LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH;
	}

	@Override
	public final void validateLicense() {
		final LicenseStats licenseStats = currentLicenseStats();

		if (licenseStats.getExpirationEpoch() != null && licenseStats.getHasExpired()) {
			if (licenseStats.getLicenseExpirationState() == LICENSE_STATE_BEYOND_GRACE_PERIOD) {
				throw new LicenseKeyException("Your license has expired and is beyond its grace period.");
			}

			if (licenseStats.getLicenseExpirationState() == LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH && !isCurrentTransactionReadOnly()) {
				throw new LicenseKeyException("Your license has expired. Only read operations are permitted");
			}
		}
	}

	@Override
	public User validateCredentialsAndLicense(final AuthCredsDTO creds) {
		validateLicense();

		return authenticator.checkLoggedInUser(creds);
	}

	@Override
	public boolean licenseIsExpired() {
		return currentLicenseStats().getHasExpired();
	}

	@Override
	public LoginResult login(final LoginActionDTO loginAction, final int sessionTimeout) throws ObidosAuthenticationException {
		final String remoteAddr = ObidosDispatcherUtil.getRemoteAddr();
		final User user = getUserFromLoginAction(loginAction, remoteAddr);

		if (user == null) {
			audit(FAILED_LOGIN_ATTEMPT, loginAction.getUsername(), null, remoteAddr, null, null, "password invalid");
			logger.info(() -> "ERROR: Authentication failed");
			throw new ObidosAuthenticationException();
		}

		final String username = user.getUsername();
		logger.info(() -> "Authenticaton of " + username + " successful, returning userDTO");

		if (isTrue(user.getDeleted())) {
			audit(FAILED_LOGIN_ATTEMPT, username, null, remoteAddr, null, null, "account is tombstoned");
			throw new PermissionDeniedException("This user has been deleted.");
		}

		if (isTrue(user.getLocked())) {
			audit(FAILED_LOGIN_ATTEMPT, username, null, remoteAddr, null, null, "account is locked");
			throw new PermissionDeniedException("This account is locked.");
		}

		if (user.authSourceIsLocal() && isTrue(user.getPasswordChangeRequired())) {
			audit(FAILED_LOGIN_ATTEMPT, username, null, remoteAddr, null, null, "initial password change required");
			// throwing a PasswordChangeRequiredException does not work
		}

		final SystemConfig sc = getSystemConfig();
		final int maxPasswordAge = maxPasswordAgeInDays(sc.getPasswordComplexityName());
		final boolean infinitePasswordExpiration = maxPasswordAge == 0;
		final int daysUntilPasswordExpiration = getDaysUntilPasswordExpiration(user);

		if (daysUntilPasswordExpiration <= 0 && isFalse(user.getPasswordChangeRequired())) {
			audit(PASSWORD_EXPIRED, username, user.getId(), getPasswordExpirationString(daysUntilPasswordExpiration, user));
			user.setPasswordChangeRequired(true);
			updatePasswordChangeRequired(user);
		}

		if (!infinitePasswordExpiration && daysUntilPasswordExpiration > 0 && daysUntilPasswordExpiration < passwordExpirationWarningThreshold(maxPasswordAge) && !licenseIsExpired()) {
			notifyUserOfPendingPasswordExpiration(user, daysUntilPasswordExpiration);
		}
		// set the session timeout in result, we need this to start client side session idle timer
		audit(LOGIN, username, user.getId(), remoteAddr, (long) user.getLoginCount());

		return createLoginResult(user, sc, sessionTimeout);
	}

	@Override
	public Void logout(final User user) throws SessionErrorException {
		userManagementActions.logout(user);
		audit(LOGOUT, user.getUsername(), user.getId(), ObidosDispatcherUtil.getRemoteAddr(), null);
		authenticator.logout();
		return null;
	}

	/**
	 * TODO use some kind of fast db lookup
	 *
	 * @param password
	 * @return
	 *         <p>
	 * @author spgdev@spenego.com - Jul 9, 2017 private boolean
	 *         passwordIsStupid(String password) { return
	 *         org.apache.commons.lang3.StringUtils.containsIgnoreCase("this is
	 *         a test", password); }
	 */

	// get entropy using StrongPass -- spgdev@spenego.com - Sep 2, 2019
	// This has a side-effect of setting strength score.
	private static float getEntropyViaStrongpass(final Strength strength, final String pass, final StrengthChecker strengthChecker)
	{
		logger.info(() -> "Strength Score according to Zxcvbn: " + strength.getScore());
		logger.info(() -> "Re-checking strength with StrongPass...");

		final float entropy = strengthChecker.calculateEntropy(pass);
		if (!strengthChecker.isStrong(pass))
		{
			logger.info(() -> "According to StrongPass, it is a weak password, setting score to PASSWORD_WEAK");
			strength.setScore(PASSWORD_WEAK);
		}
		return entropy;
	}

	/**
	 * Check strength of password or passphrase
	 */
	@Override
	public PasswordAnalysisResults checkPassStrength(final User user, final String pass, final int type) {
		try {
			userManagementActions.validatePassComplexity(user, pass, (byte) type);	// Check complexity first
		} catch (final ServerSideException e) {
			return new PasswordAnalysisResults(0, PASSWORD_WEAK, e.getMessage());
		}

		// If password is >= PASSWORD_STRONG, re-check with Strong pass, because Zxcvbn seems to give
		// strong score to stupid passwords. That's why we (Mike and I) wrote Strongpass to begin with.
		final String[]	extraDictionaryWords = new String[0];
		final int		minWordLength = 4;
		final boolean	useDictionary = true;
		final int		minEntropy = userManagementActions.getPasswordComplexity(null).get((byte) type).getMinimumEntropy();
		final Strength	strength = new Zxcvbn().measure(pass);
		final float		entropy = (strength.getScore() >= PASSWORD_STRONG) ? getEntropyViaStrongpass(strength, pass, new StrengthChecker(minEntropy, useDictionary, minWordLength, extraDictionaryWords)) : 0;
		final Feedback	feedback = strength.getFeedback();
		final List<String> suggestions = feedback.getSuggestions();

		if (logger.isInfoEnabled()) {
			logger.info(() -> "Strength Score     : " + strength.getScore());
			logger.info(() -> "Feedback warning   : " + feedback.getWarning());

			for (final String suggestion : suggestions) {
				logger.info(() -> "Feedback Suggestion: " + suggestion);
			}
		}

		float rawEntropy = ServerUtils.calculateRawEntropy(pass);

		StrengthChecker sc = new StrengthChecker(minEntropy, useDictionary, minWordLength, extraDictionaryWords);
		StrongPassEntropy entropies = sc.calculateEntropies(pass);

		PasswordAnalysisResults r = new PasswordAnalysisResults(minEntropy, strength.getScore(), rawEntropy, entropy, ALGORITHM_DROPBOX_ZXCVBN, suggestions);
		logger.info(()-> "MMM raw entropy: " + entropies.getRawEntropy());
		logger.info(()-> "MMM after repeats weakened: " + entropies.getEntropyAfterRepeatsWeakened());
		logger.info(()-> "MMM after lower cased: " + entropies.getEntropyAfterLowerCased());
		logger.info(()-> "MMM after qwerty: " + entropies.getEntropyAfterQwertyAdjusted());
		logger.info(()-> "MMM after dictionary: " + entropies.getEntropyAfterDictionaryAdjusted());
		r.setRawEntropy(entropies.getRawEntropy());
		r.setEntropyAfterRepeatsWeakened(entropies.getEntropyAfterRepeatsWeakened());
		r.setEntropyAfterLowerCased(entropies.getEntropyAfterLowerCased());
		r.setEntropyAfterQwertyAdjusted(entropies.getEntropyAfterQwertyAdjusted());
		r.setEntropyAfterDictionaryAdjusted(entropies.getEntropyAfterDictionaryAdjusted());

//		return new PasswordAnalysisResults(minEntropy, strength.getScore(), rawEntropy, entropy, ALGORITHM_DROPBOX_ZXCVBN, suggestions);
		return r;
	}

	@Override
	public PasswordAnalysisResults checkPassStrength(final User user, final String password, int type, final Long guessesPerSecond) {
		final PasswordAnalysisResults result = checkPassStrength(user, password, type);
		final Long gps = (guessesPerSecond == null || guessesPerSecond <= 0) ? ObidosConstants.PASSWORD_CRACKING_GUESSES_PER_SECOND : guessesPerSecond;
		result.setCrackingGuessPerSecond(gps);
		final String crackingTime = GenPassServiceImpl.getPasswordCrackingTime(result.getEntropy(), gps);
		logger.info(() -> "MMM cracking time:" + crackingTime);
		result.setCrackingTime(crackingTime);
		return result;
	}

	/**
	 * do not care about weak, strong password, just return the result
	 * 
	 * @param pass
	 * @return PasswordAnalysisResults
	 * <p>
	 * @author spgdev@spenego.com - Sep 24, 2024
	 */
	private static PasswordAnalysisResults checkPassStrength(final String pass) {
		boolean useDictionary = true;
		final StrengthChecker sc = new StrengthChecker(useDictionary);
		final StrongPassEntropy entropies = sc.calculateEntropies(pass);

		return new PasswordAnalysisResults(entropies.getFinalAdjustedEntropy(), entropies.getRawEntropy(), entropies.getEntropyAfterRepeatsWeakened(),
					entropies.getEntropyAfterLowerCased(), entropies.getEntropyAfterQwertyAdjusted(), entropies.getEntropyAfterDictionaryAdjusted());
	}

	@Override
	public PasswordAnalysisResults checkPassStrengthForGeneratedPassword(User user, String pass)
	{
		return checkPassStrength(pass);
	}
}
