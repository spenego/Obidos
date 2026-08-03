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

import static com.spenego.Obidos.server.model.Audit.DELETE_PASSWORD_RESET;
import static com.spenego.Obidos.server.model.Audit.EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.PASSPHRASE_RESET_REQUEST;
import static com.spenego.Obidos.server.model.Audit.PASSWORD_RESET_IGNORED;
import static com.spenego.Obidos.server.model.Audit.PASSWORD_RESET_REQUEST;
import static com.spenego.Obidos.server.model.Audit.PASSWORD_RESET_REQUEST_IGNORED;
import static com.spenego.Obidos.server.model.Audit.PASS_RESET_EXCEPTION;
import static com.spenego.Obidos.server.model.Audit.SENT_WARNING_EMAIL;
import static com.spenego.Obidos.server.model.Audit.UPDATE_USER;
import static com.spenego.Obidos.server.model.PasswordReset.EMAIL_SENT;
import static com.spenego.Obidos.server.model.PasswordReset.NON_EXISTANT_EMAIL_REQUEST;
import static com.spenego.Obidos.server.model.PasswordReset.PASSPHRASE_RESET_REQUESTED;
import static com.spenego.Obidos.server.model.PasswordReset.PASSWORD_RESET_REQUESTED;
import static com.spenego.Obidos.server.utils.ServerUtils.nextRandomLong;
import static com.spenego.Obidos.shared.ObidosConstants.AUTH_SOURCE_LOCAL;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;
import static com.spenego.Obidos.shared.dto.NotificationDTO.PASSWORD_RESET;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjLongConsumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.ObidosDispatcherUtil;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.PasswordResetActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.PasswordReset;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.PasswordResetOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.Obidos.shared.exceptions.TooManyOutstandingRequestsException;


public final class PasswordResetActionsImpl extends CryptoActions<PasswordReset> implements PasswordResetActions, Runnable {
	protected static final Logger logger = LoggerFactory.getLogger(PasswordResetActionsImpl.class);

	@Autowired protected final PasswordResetOperations passwordResetOperations = null;
	@Autowired private final UserManagementActions userManagementActions = null;
	@Autowired private final EmailActions emailActions = null;
	@Autowired private final LoginActions loginActions = null;

	private final Thread emailSenderThread = getThreadFactory("Email Sender").newThread(this);
	private final List<Byte> requestTypes = new ArrayList<>(3);
	private int sleepTime = 7000;
	private static final long EXPIRE_DURATION_MS = MS_PER_MIN * 10; // expire requests in 10 minutes
	private static final int MAX_OUTSTANDING_PASSWORD_RESET_REQUESTS = 3;

	public PasswordResetActionsImpl() {
		super(null, null);
	}

	@Override
	protected final String elementName() {
		return "password reset request";
	}

	@PostConstruct @Override
	protected final synchronized void init() {
		logger.info(() -> "Starting Email Sender Thread");
		emailSenderThread.start();
		Executors.newScheduledThreadPool(1, getThreadFactory("Password Reset Request Reaper")).scheduleWithFixedDelay(new PasswordResetRequestReaperRunnable(), 0, 61, SECONDS);
		initialized = true;
	}

	@Override
	protected final Logger getLogger() { return logger; }

	protected class PasswordResetRequestReaperRunnable implements Runnable {
		@Override
		public void run() {
			logger.debug(() -> "Running Password Reset Request Reaper");
			try {
				final int deleted = passwordResetOperations.deleteExpiredRequests(new Date(System.currentTimeMillis() - EXPIRE_DURATION_MS));
				if (deleted > 0) {
					logger.info(() -> "Deleted " + deleted + " password reset requests");
				} else {
					logger.debug(() -> "No Password Reset Requests to delete.");
				}
			} catch(final Exception ex) {
				logger.error(() -> "Caught " + ex);
				logger.exception(ex);
			}
		}
	}

	@Override
	protected final Operations<PasswordReset> getOperations() {
		return passwordResetOperations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return DELETE_PASSWORD_RESET;
	}

	private String getToken(final String prefix) {
		final String token = prefix + Long.toString(nextRandomLong(Long.MAX_VALUE), Character.MAX_RADIX);
		return (token.length() >= 64) ? token.substring(0, 63) : getToken(token);
	}

	/**
	 * This is sent when a user enters an email address on the password reset screen and that email
	 * address does not exist within the system.  We let the user that they have not used the
	 * email account as a contact point for this system.
	 *
	 * See: https://www.troyhunt.com/everything-you-ever-wanted-to-know/
	 *
	 * @param emailAddress
	 */
	private Void processPasswordResetWarning(final PasswordReset request) {
		passwordResetOperations.delete(request.getId());
		logger.info(() -> "Sending password request warning to " + request.getEmailAddress());

		// use responsive HTML template + text file for sending mail
		// spgdev@spenego.com May-12-2018
		final String subject = "Account Access Attempted";
		emailActions.sendPasswordResetWarningEmail(request.getEmailAddress(), subject);
		immediateAudit(SENT_WARNING_EMAIL, null, null, null, null, null, request.getEmailAddress());
		return null;
	}

	private Void processResetRequest(final PasswordReset request, final String subject, final BiConsumer<User, String> consumer, final int action ) {
		final Long userId = request.getUserId();
		final User user = getUser(userId);
		final String username = user.getUsername();
		final String token = getToken("");

		logger.info(() -> "Sending " + subject + " reset link to " + request.getEmailAddress() + " for user " + username);

		consumer.accept(user, token);
		passwordResetOperations.updateSelective(new PasswordReset(request.getId(), EMAIL_SENT, token));
		immediateAudit(action, username, userId, username, userId, null, request.getEmailAddress());
		return null;
	}

	// This method should have another argument which is the subject. Server side can not read i18n stuff from
	// properties file at this time.
	// The subject is passed from client side to sendPasswordResetWithAccountLookupEmail().
	private Void processPasswordResetRequest(final PasswordReset request) {
		final String subject = "Password Reset Request";  // Hard-coding for now, it should be an argument

		return processResetRequest(request, subject, (user, token) -> emailActions.sendPasswordResetEmail(user, request.getUserId(), subject, token, user.getTwoFARequired() || user.getTwoFAPasswordResetEnabled()), Audit.SENT_PASSWORD_RESET_EMAIL);
	}

	private Void processPassphraseResetRequest(final PasswordReset request) {
		final String subject = "Passphrase Reset Request";  // Hard-coding for now, it should be an argument

		return processResetRequest(request, subject, (user, token) -> emailActions.sendPassphraseResetEmail(user, request.getUserId(), subject, token, user.getTwoFARequired() || user.getTwoFAPasswordResetEnabled()), Audit.SENT_PASSPHRASE_RESET_EMAIL);
	}

	private Void processPasswordRequest(final PasswordReset request) {
		return request.isRequestForNonExistantEmail() ? processPasswordResetWarning(request) : processPasswordResetRequest(request);
	}

	private Void processResetRequest(final PasswordReset request) {
		logger.info(() -> "Processing reset requests for token " + request.getToken() + " for email " + request.getEmailAddress());
		return request.isRequestForPassphraseReset() ? processPassphraseResetRequest(request) : processPasswordRequest(request);
	}

	private void handleException(final ServerSideException ex) throws InterruptedException {
		logger.error(() -> "Failed while getting list of requests: " + ex);
		logger.exception(ex);
		logger.error(() -> "sleeping for " + sleepTime + "ms");
		auditExceptionAction(EXCEPTION, "Email Sender Thread", null, null, null, null, ex.getLocalizedMessage());
		sleepRandomAmount(5000, sleepTime);
		sleepTime = sleepTime * 2;
	}

	private Collection<PasswordReset> getPasswordResetRequestsInternal() {
		logger.info(() -> "Looking for password reset requests");
		return passwordResetOperations.getList(requestTypes);
	}

	private synchronized Collection<PasswordReset> getPasswordResetRequests() throws InterruptedException {
		Collection<PasswordReset> resetRequests;
		while ((resetRequests = getPasswordResetRequestsInternal()) == null || resetRequests.isEmpty()) {
			logger.info(() -> "No password requests left, waiting for password reset notification");
			wait();
		}

		return resetRequests;
	}

	private void processPasswordResetRequest() throws InterruptedException {
		try {
			final Collection<PasswordReset> requests = getPasswordResetRequests();  // list guaranteed to not be empty
			logger.info(() -> "Processing list of " + requests.size() + " password reset requests.");
			processStream(() -> "processing password reset request", requests::stream, this::processResetRequest);
			sleepTime = 7000; // reset on success
		} catch (final ServerSideException ex) {
			handleException(ex);
		}
	}

	@Override
	public void run() {
		if (requestTypes.isEmpty()) {
			requestTypes.add(NON_EXISTANT_EMAIL_REQUEST);
			requestTypes.add(PASSWORD_RESET_REQUESTED);
			requestTypes.add(PASSPHRASE_RESET_REQUESTED);
		}

		try {
			while(!shouldTerminateThread()) {
				processPasswordResetRequest();
			}
		} catch (final InterruptedException ex) {
			auditExceptionAction(EXCEPTION, "Email Sender Thread", null, null, null, null, ex.getLocalizedMessage());
			logger.error(() -> "Caught " + ex + " while waiting on thread.");
			Thread.currentThread().interrupt();
		}
	}

	/*****************************************************************************************************************
	         Above are server methods to actually send the email.
	         Below this comment are queuing calls made by client requests.
	 *****************************************************************************************************************/
	private synchronized void synchronizedNotify() {
		if (!initialized) {
			init();
		}

		logger.info(() -> "Notifying Email Sender Thread");
		notifyAll();
	}

	private Void notifyEmailSenderThread() {
		// We need to notify only after the transaction is committed. Otherwise, the consumer thread could look for
		// password requests in the database that do not exist yet.
		postCommitAction(this::synchronizedNotify);
		return null;
	}

	private void throttleResetRequests(final String emailAddress) throws TooManyOutstandingRequestsException {
		try {
			if (passwordResetOperations.getList(emailAddress).size() >= MAX_OUTSTANDING_PASSWORD_RESET_REQUESTS) {
				logger.warn(() -> "Too many password reset requests for email " + emailAddress);
				throw new TooManyOutstandingRequestsException("There are currently too many outstanding password reset requests for this address.");
			}
		} catch (final TooManyOutstandingRequestsException e) {
			throw e;
		} catch (final ServerSideException e) { /* Hide other exceptions generated. */ }
	}

	private void requestResetEmail(final User user, final String emailAddress, final byte state, final ObjLongConsumer<String> auditor) {
		if (state == PASSWORD_RESET_REQUESTED && !user.getAuthSource().equals(AUTH_SOURCE_LOCAL)) {
			logger.warn(() -> "Auth source is not local, will not send password reset email for user: " + user.getUsername());
			return;
		}
		if (isTrue(user.getLocked())) {
			logger.warn(() -> "This account is locked. You may not reset the password for " + user.getUsername());
			return;
		}
		if (isTrue(user.getDeleted())) {
			logger.warn(() -> "This account is tombstoned. You may not reset the password." + user.getUsername());
			return;
		}

		try {
			throttleResetRequests(emailAddress);

			final Long id = passwordResetOperations.create(new PasswordReset(user.getId(), emailAddress, state));
			auditor.accept(emailAddress, id);
		} catch(final TooManyOutstandingRequestsException ex) {
			// do not let the user know of pending requests
		} catch (final ServerSideException ex) {
			logger.error(() -> "Failed to create password reset request: " + ex.getMessage());
			immediateAudit(PASS_RESET_EXCEPTION, user.getUsername(), user.getId(), user.getEmail1(), null, null, ex.getMessage());
		}
	}

	private Void requestResetEmail(final User user, final byte state, final ObjLongConsumer<String> auditor) throws TooManyOutstandingRequestsException {
		if (user.getEmail1() != null) {
			requestResetEmail(user, user.getEmail1(), state, auditor);
		}
		return null;
	}

	private void auditPassResetRequest(final int action, final User user, final String emailAddress, final Long id, final String description) {
		immediateAudit(action, user.getUsername(), user.getId(), emailAddress, id, null, description);
	}

	@Override
	public Void queuePasswordResetEmail(final String username) throws TooManyOutstandingRequestsException {
		try {
			final User user = userOperations.getUserByUsername(username);
			requestResetEmail(user, PASSWORD_RESET_REQUESTED, (emailAddress, id) -> auditPassResetRequest(PASSWORD_RESET_REQUEST, user, emailAddress, id, "Password Reset Request"));
			return notifyEmailSenderThread();
		} catch(final Exception ex) {
			// possible side-channel attack here since timing of existent vs. non-existent user may be different, worth fixing?
			logger.warn(() -> "Someone attempted to reset the password for the non-existant user " + username);
			// we DO NOT INFORM the user that the username does not exist (prevents username phishing)
			return null;
		}
	}

	@Override
	public Void queuePassphraseResetEmail(final User user) throws TooManyOutstandingRequestsException {
		try {
			requestResetEmail(user, PASSPHRASE_RESET_REQUESTED, (emailAddress, id) -> auditPassResetRequest(PASSPHRASE_RESET_REQUEST, user, emailAddress, id, "sent to " + emailAddress));
			return notifyEmailSenderThread();
		} catch(final Exception ex) {
			logger.warn(() -> "Someone attempted to reset the password for the non-existant user " + user.getUsername());
			return null;
		}
	}

	@Override
	public Void queuePasswordResetWithAccountLookupEmail(final String emailAddress, final String subject) {
		if (loginActions.licenseIsExpired()) {
			logger.warn(() -> "A password reset request was made but ignored due to expired license.");
			immediateAudit(PASSWORD_RESET_REQUEST_IGNORED, null, null, emailAddress, null, null, "Password Reset Request Ignored due to expired license");

			return null; // silently fail to do this when password is expired
		}

		final String remoteAddr = ObidosDispatcherUtil.getRemoteAddr();

		try {
			throttleResetRequests(emailAddress);
			processStream(null, () -> userOperations.getUsersByEmail(emailAddress),
					user -> requestResetEmail(user, PASSWORD_RESET_REQUESTED, (x, id) -> auditPassResetRequest(PASSWORD_RESET_REQUEST, user, emailAddress, id, "via email address (" + emailAddress + ") made from IP Address " + remoteAddr)),
					ce -> exceptionLoggerThrower("queing password reset", ce));
			return notifyEmailSenderThread();
		} catch(final TooManyOutstandingRequestsException ex) {
			return null;
		} catch(final Exception ex) { /* email address lookup failed, just audit and log the failure */ }

		immediateAudit(PASSWORD_RESET_REQUEST, "UNKNOWN", null, emailAddress, null, null, "for unknown email address (" + emailAddress + ") made from IP Address " + remoteAddr);

		logger.warn(() -> "Someone attempted to reset an account with the non-existant email address: " + emailAddress);

		return null; // queuePasswordResetWarningEmail(emailAddress);
	}

	private void resetPassword(final Long userId, final String password, final byte[] twoFactorAuthCode) {
		final User user = getUser(userId);

		if (!user.getAuthSource().equals(ObidosConstants.AUTH_SOURCE_LOCAL))		{ throw new ServerSideException("Account does not use local authentication."); }

		// do not save password unless it is at least strong
		final PasswordAnalysisResults result = loginActions.checkPassStrength(null, password, PASSWORD_COMPLEXITY_REQUIREMENTS);
		/*
		logger.info(() -> "score: " + result.getPasswordScore());
		logger.info(() -> "Entropy: "+ result.getEntropy());
		logger.info(() -> "Minimum required entropy: " + userManagementActions.getPasswordComplexity(null).getPasswordComplexityRequirements().getMinimumEntropy());
		logger.info(() -> "Strong password score: " + PasswordAnalysisResults.PASSWORD_STRONG);
		*/

		// score and entropy are two different things. don't compare them
		// score is a zxcvbn thing, entropy is a Strongpass thing
		// we calculate entropy using StrongPass if zxcvbn thinks password is strong.

		// Issue #694
		if (result.getPasswordScore() < PasswordAnalysisResults.PASSWORD_STRONG)
		{
			String msg = result.getLocalRequirementsNotMetMessage();
			if (msg == null)
			{
				msg = "Password is not strong enough";
			}
			throw new ServerSideException(msg);
		}

		// I don't think we need the following code
		/*
		if (result.getEntropy() < userManagementActions.getPasswordComplexity(null).getPasswordComplexityRequirements().getMinimumEntropy())
		{
			throw new ServerSideException("Password does not meet complexity requirements.");
		}
		*/


		if (user.getTwoFAPasswordResetEnabled() || user.getTwoFARequired()) {
			authenticateTwoFactorCode(user, twoFactorAuthCode);
		}

		UserDTO userDTO = new UserDTO(user.getId(), password);
		// user may send password reset request without login with the initial
		// password. In that case after resetting password successfully, it
		// ask to reset password again. Issue #691
		// spgdev@spenego.com - May 17, 2020
		userDTO.setPasswordChangeRequired(false);

		userManagementActions.modifyUser(user, userDTO);
		createNotification(user.getId(), PASSWORD_RESET);
		immediateAudit(UPDATE_USER, user.getUsername(), user.getId(), user.getUsername(), user.getId(), null, "Password Reset");
	}

	@Override
	public Void processPasswordResetToken(final String token, final Consumer<PasswordReset> consumer) {
		final PasswordReset resetRequest = passwordResetOperations.getByToken(token);

		if (loginActions.licenseIsExpired()) {
			logger.warn(() -> "A password reset attempt was made but denied due to expired license.");
			immediateAudit(PASSWORD_RESET_IGNORED, getUser(resetRequest.getUserId()).getUsername(), resetRequest.getUserId(), resetRequest.getEmailAddress(), null, null, "Password Reset ignored due to expired license");
			throw new ServerSideException("The Obidos license has expired. Unable to reset password.");
		}

		consumer.accept(resetRequest);
		return passwordResetOperations.delete(resetRequest.getId());
	}

	@Override
	public Void resetPassword(final String token, final String newPassword, final byte[] twoFactorAuthCode) {
		return processPasswordResetToken(token, resetRequest -> resetPassword(resetRequest.getUserId(), newPassword, twoFactorAuthCode));
	}

	@Override
	public String get2FAIssuer() {
		final String issuer = getSystemConfig().getTwoFactorAuthIssuer();

		if (issuer == null || issuer.isEmpty()) {
			throw new ServerSideException("Two-Factor Authentication Issuer has not yet been configured.");
		}

		return issuer;
	}
}
