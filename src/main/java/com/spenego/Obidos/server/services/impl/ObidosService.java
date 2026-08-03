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

package com.spenego.Obidos.server.services.impl;


import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.AuditActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.actions.UserActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.Authenticator;
import com.spenego.Obidos.server.security.Encryption;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.MemoryWiper;
import com.spenego.Obidos.server.utils.TimeLogger;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.Clearable;
import com.spenego.Obidos.shared.dto.ObidosResult;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.ObidosWrapperException;
import com.spenego.Obidos.shared.exceptions.PassphraseRequiredException;
import com.spenego.Obidos.shared.exceptions.PasswordChangeRequiredException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public abstract class ObidosService {
	public static final Long NOTEBOOK_SYSTEM_ID = 42L;

	protected abstract Logger getLogger();

	@Autowired private final AuditActions				auditActions = null;
	@Autowired protected final Authenticator			authenticator = null;
	@Autowired private final Encryption					encryption = null;
	@Autowired private final LoginActions				loginActions = null;
	@Autowired protected final MemoryWiper				memoryWiper = null;
	@Autowired private final SystemConfigActions		systemConfigActions = null;
	@Autowired private final UserActions				userActions = null;
	@Autowired private final UserManagementActions		userManagementActions = null;

	protected static final <T, C extends Clearable> T callWithWipe(final Supplier<T> supplier, final Collection<C> values) {
		try {
			return supplier.get();
		} finally {
			if (values != null) { values.forEach(Clearable::clear); }
		}
	}

	protected final <T extends ObidosResult<?>> T log(final T r) throws ServerSideException {
		getLogger().info(() -> "Total result set consists of " + r.getTotal() + " total objects");
		if (r.getElements() != null) {
			getLogger().info(() -> "This result contains " + r.getElements().size() + " objects");
		}
		return r;
	}

	/**
	 * This method ensures that only exceptions of type ServersideException are
	 * thrown by service methods that use it. A closure is passed in and the
	 * return value of it is returned from this method. This method is also used
	 * to time all service calls. The results are written to the log.
	 *
	 * @param action     A description of what the lambda will do.
	 * @param methodName The name of the method that contains the lambda. This is used for logging.
	 * @param supplier   Supplies the return value.
	 * @throws ServersideException
	 */
	private <R> R mapExceptions(final String action, final String methodName, final Supplier<R> supplier) {
		final Logger logger = getLogger();
		final TimeLogger timeLogger = TimeLogger.getTLSTimeLogger(logger, methodName);

		try {
			final R r = supplier.get();
			if (r instanceof Clearable) {
				memoryWiper.addReferent((Clearable) r);
			}
			return r;
		} catch (final ServerSideException ex) {
			logger.warn(() -> "Throwing exception: " + ex);
			throw ex;
		} catch (final Throwable t) {
			logger.exception(t);
			auditActions.queueAuditAction(() -> auditActions.exception(action + " generated exception: " + t));
			final ObidosWrapperException ex = new ObidosWrapperException("Unable to " + action + ".");
			logger.warn(() -> "Throwing exception: " + ex);
			throw ex;
		} finally {
			timeLogger.complete(logger);
		}
	}

	/**
	 * This method is used for actions that may be performed by a temporary (anonymous)
	 * user. Primarily for actions related to password reset.
	 *
	 * @param action
	 * @param methodName
	 * @param checkLicense
	 *            When true, an exception is thrown if the license has expired.  Most actions that modify data should set this to true.
	 *            One exception is the set of calls that operate on the license (getting/installing/etc).
	 * @param supplier
	 * @return
	 * @throws ServersideException
	 * @see {@link #adminFunction(AuthCredsDTO, String, String, boolean, Function)}, {@link #userFunction(AuthCredsDTO, String, String, Function)},
	 * {@link #userOrAdminFunction(AuthCredsDTO, String, String, boolean, Function)}
	 */
	protected final <R> R anonymousFunction(final String action, final String methodName, final boolean checkLicense, final Supplier<R> supplier) {
		if (checkLicense) {
			loginActions.validateLicense();
		}

		return mapExceptions(action, methodName, supplier);
	}

	/**
	 * Ensures that the User's password is current (does not require changing). PasswordChangeRequiredException is thrown if the caller
	 * is required to change their password.
	 *
	 * @param caller
	 * @return
	 */
	private User passwordIsCurrent(final User caller) {
		if (caller.authSourceIsLocal() && Boolean.TRUE.equals(caller.getPasswordChangeRequired())) {
			throw new PasswordChangeRequiredException("Your password has expired. You must change it.");
		}
		userManagementActions.refreshUser(caller);

		return caller;
	}

	/**
	 * This method is invoked for all service functions that require password authentication. It ensures that the users'
	 * password has not expired. Some service functions that do not call this are: checkPassStrength,
	 * anonymousFunction, and changePassword.
	 *
	 * @param action
	 *            A description of what the function will do.
	 * @param methodName
	 *            The name of the method that contains the lambda. This is used for logging.
	 * @param f A function that accepts a user and returns an object of R
	 * @param userSupplier supplies an authenticated user to be passed to the function f
	 * @throws ServerSideException
	 */
	private <R> R serviceFunction(final String action, final String methodName, final Function<User,R> f, final Supplier<User> userSupplier) {
		return mapExceptions(action, methodName, () -> f.apply(passwordIsCurrent(userSupplier.get())));
	}

	private void ensureLicenseIsNotExpired(final String action) {
		if (Boolean.TRUE.equals(systemConfigActions.getCurrentLicense().getHasExpired())) {
			getLogger().warn(() -> "License has expired, unable to " + action);
			throw new LicenseKeyException("Your license has expired, you may not " + action + ". Please contact Spenego Software to refresh your license.");
		}
	}

	/**
	 * This method is used for actions that require an administrator to be performed.
	 *
	 * @param credentials
	 * @param action
	 *            A description of what the function will do.
	 * @param methodName
	 *            The name of the method that contains the lambda. This is used when logging.
	 * @param checkLicense
	 *            When true, an exception is thrown if the license has expired.  Most actions that modify data should set this to true.
	 *            One exception is the set of calls that operate on the license (getting/installing/etc).
	 * @param f A function that accepts a user and returns an object
	 * @throws ServersideException
	 * @see {@link #anonymousFunction(String, String, boolean, Supplier)}, {@link #userFunction(AuthCredsDTO, String, String, Function)},
	 * {@link #userOrAdminFunction(AuthCredsDTO, String, String, boolean, Function)}
	 */
	protected final <R> R adminFunction(final AuthCredsDTO creds, final String action, final String methodName, final boolean checkLicense, final Function<User,R> f) {
		if (checkLicense) {
			ensureLicenseIsNotExpired(action);
		}

		return serviceFunction(action, methodName, f, () -> authenticator.checkLoggedInAdmin(creds));
	}

	/**
	 * This method is used for actions that require an administrator, with Modify Settings set, to be performed.
	 *
	 * @param credentials
	 * @param action
	 *            A description of what the function will do.
	 * @param methodName
	 *            The name of the method that contains the lambda. This is used when logging.
	 * @param checkLicense
	 *            When true, an exception is thrown if the license has expired.  Most actions that modify data should set this to true.
	 *            One exception is the set of calls that operate on the license (getting/installing/etc).
	 * @param f A function that accepts a user and returns an object
	 * @throws PermissionDeniedException User is a deputy Admin without the Modify Settings capability.
	 * @throws ServersideException
	 * @see {@link #anonymousFunction(String, String, boolean, Supplier)}, {@link #userFunction(AuthCredsDTO, String, String, Function)},
	 * {@link #userOrAdminFunction(AuthCredsDTO, String, String, boolean, Function)}
	 */
	protected final <R> R adminSettingsFunction(final AuthCredsDTO creds, final String action, final String methodName, final boolean checkLicense, final Function<User,R> f) {
		if (checkLicense) {
			ensureLicenseIsNotExpired(action);
		}

		return serviceFunction(action, methodName, f, () -> {
					final User u = authenticator.checkLoggedInAdmin(creds);
					if (!u.getModifySettings()) {
						throw new PermissionDeniedException("You do not have permission to modify settings.");
					}
					return u;
		});
	}

	/**
	 * This method is used for actions that require an authenticated user to be performed.
	 *
	 * @param credentials
	 * @param action
	 *            A description of what the function will do.
	 * @param methodName
	 *            The name of the method that contains the lambda. This is used for debugging.
	 * @param f A function that accepts a user and returns an object
	 * @throws ServersideException
	 * @see {@link #adminFunction(AuthCredsDTO, String, String, boolean, Function)}, {@link #anonymousFunction(String, String, boolean, Supplier)},
	 * {@link #userOrAdminFunction(AuthCredsDTO, String, String, boolean, Function)}
	 */
	protected final <R> R userFunction(final AuthCredsDTO creds, final String action, final String methodName, final Function<User,R> f) {
		return serviceFunction(action, methodName, f, () -> loginActions.validateCredentialsAndLicense(creds));
	}

	/**
	 * This method is used for actions that require an authenticated user or administrator to be performed.
	 *
	 * @param credentials
	 * @param action
	 *            A description of what the function will do.
	 * @param methodName
	 *            The name of the method that contains the lambda. This is used
	 *            for debugging.
	 * @param f A function that accepts a user and returns an object
	 * @throws ServersideException
	 * @see {@link #adminFunction(AuthCredsDTO, String, String, boolean, Function)}, {@link #anonymousFunction(String, String, boolean, Supplier)},
	 * {@link #userFunction(AuthCredsDTO, String, String, Function)},
	 */
	protected final <R> R userOrAdminFunction(final AuthCredsDTO creds, final String action, final String methodName, final boolean checkLicense, final Function<User,R> f) {
		if (checkLicense) {
			loginActions.validateLicense();
		}

		return serviceFunction(action, methodName, f, () -> authenticator.checkLoggedIn(creds));
	}

	private Void validateAndCachePassphraseHash(final User caller, final PassphraseHash hash, final boolean bypassRateLimiter) {
		userActions.validatePassphraseHash(caller, hash);
		return authenticator.setPassphraseHash(hash, bypassRateLimiter);
	}

	private void checkPassphrase(final User caller, final byte[] passphrase, final byte[] twoFactorAuthCode) {
		if (passphrase == null || passphrase.length == 0) {
			throw new PassphraseRequiredException("You must provide a passphrase.");
		}

		if (caller.requireTwoFaAfterPassphrase()) {
			if (twoFactorAuthCode == null) {
				throw new PermissionDeniedException("You must supply an Authentication Code");
			}
			userActions.authenticateTwoFactorCode(caller, twoFactorAuthCode);
		}
	}

	/**
	 * If the passphrase is null, we attempt to retrieve the passphrase saved in the session.
	 *
	 * @param user
	 * @param passphrase
	 * @return
	 * @throws ServerSideException
	 * @throws PassphraseRequiredException The passphrase was not supplied but it is required.
	 */
	protected final Void cachePassphraseHash(final User caller, final byte[] passphrase, final byte[] twoFactorAuthCode, final boolean bypassRateLimiter) {
		checkPassphrase(caller, passphrase, twoFactorAuthCode);

		return validateAndCachePassphraseHash(caller, encryption.secureHashCompute(caller, passphrase), bypassRateLimiter);
	}

	/**
	 * Cache a new passphrase. This call avoids the validation of the passphrase hash since it has already been done.
	 *
	 * @param caller
	 * @param passphrase
	 * @param twoFactorAuthCode
	 * @param bypassRateLimiter
	 * @return
	 * @throws ServerSideException
	 */
	protected final Void cacheNewPassphraseHash(final User caller, final byte[] passphrase, final byte[] twoFactorAuthCode, final boolean bypassRateLimiter) {
		checkPassphrase(caller, passphrase, twoFactorAuthCode);

		return authenticator.setPassphraseHash(encryption.secureHashCompute(caller, passphrase), bypassRateLimiter);
	}

	protected final PassphraseHash summonPWHash() {
		return authenticator.getPassphraseHash();
	}
}
