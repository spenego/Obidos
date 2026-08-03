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

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.SMSActions;
import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.SMSMessage;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.SmsKeyDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.NotYetConfiguredException;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class SMSActionsImpl extends CryptoActions<SMSMessage> implements SMSActions {
	private static final Logger logger = LoggerFactory.getLogger(SmtpConfigActionsImpl.class);
	private final Thread smsSenderThread = getThreadFactory("SMS").newThread(this);
	@Autowired private final LoginActions			loginActions = null;
	@Autowired private final SystemConfigActions	systemConfigActions = null;

	@Override
	protected String elementName() {
		return "smskey";
	}

	public SMSActionsImpl() {
		super("sms_crypto.properties", "SMS Crypto Config");
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@PostConstruct
	@Override
	protected final void init() {
		logger.info(() -> "Starting SMS Thread");
		smsSenderThread.start();
		initialized = true;
	}

	@Override
	protected Operations<SMSMessage> getOperations() {
		return null;
	}

	@Override
	protected Integer getAuditDeleteAction() {
		return Audit.EXCEPTION;
	}

	private boolean smsEnabledInLicense() {
		return isTrue(loginActions.currentLicenseStats().getSupportsSMS());
	}

	private void licenseCheck() {
		if (!smsEnabledInLicense()) {
			throw new PermissionDeniedException("SMS is not enabled in the current License.");
		}
	}

	private byte[] getSMSKey() {
		final byte[] smsKey = systemConfigActions.getSMSKey();

		if (smsKey == null || smsKey.length == 0) {
			throw new NotYetConfiguredException("The SMS key has not yet been configured.");
		}
		return smsKey;
	}

	@Override
	public SmsKeyDTO getSMSConfig(final User admin) {
		licenseCheck();
		return ServerUtils.parseJSONTemplate(decrypt(new String(getSMSKey())), SmsKeyDTO.class);
	}

	@Override
	public Void updateSMSConfig(final User admin, final SmsKeyDTO smsKey) throws ServerSideException {
		licenseCheck();

		return systemConfigActions.updateSMSKey(encrypt(ServerUtils.toJson(smsKey)).getBytes());
	}

	private String getUrl() {
		final SystemConfigDTO dto = systemConfigActions.get();
		final String scheme = dto.getScheme() == null? "https"  : dto.getScheme();
		final String systemContextPath = dto.getContextPath();
		final String contextPath = systemContextPath == null ? "" : new File(systemContextPath).getAbsoluteFile().toString();
		Integer port = dto.getServerPort();
		if (port == null) {
			port = 443;
		}
		StringBuilder urlBuilder = new StringBuilder(scheme).append("://").append(dto.getFqdn());
		// if port is 443, don't add it in URL
		if (port != 443) {
			urlBuilder.append(":");
			urlBuilder.append(dto.getServerPort());
		}

		return urlBuilder.append(contextPath).toString();
	}

	@Override
	public Void sendSms(final User recipient, final String message) throws ServerSideException {
		logger.info(() -> "SMSActionsImpl SMS smsEnabledInLicense: " + smsEnabledInLicense());
		logger.info(() -> "SMSActionsImpl SMS recipient accepts SMS: " + recipient.acceptsSMSMessages());
		logger.info(() -> "SMSActionsImpl SMS message: " + message);
		if (smsEnabledInLicense() && recipient.acceptsSMSMessages()) {
			logger.info(() -> "SMSActionsImpl SMS calling ServerUtils.sendSmsMessage to send SMS");
			if (getSMSConfig(null) == null) {
				logger.error(() -> "SMSActionsImpl SMS SmsKeyDTO is null, SMS send will fail");
			}
			final String msg = message + " in Obidos. Log into " + getUrl() + " to view it.";
			ServerUtils.sendSmsMessage(getSMSConfig(null), msg, recipient.getMobile1());
		} else {
			logger.error(() -> "SMSActionsImpl SMS Will not send SMS because smsEnabledInLicense: " + smsEnabledInLicense());
			logger.error(() -> "SMSActionsImpl SMS will not send SMS recipient accepts SMS: " + recipient.acceptsSMSMessages());
		}
		return null;
	}

	@Override
	public Void deleteSmsConfig(final User admin) throws ServerSideException {
		licenseCheck();

		return updateSMSConfig(admin, new SmsKeyDTO());
	}

	@Override
	public Void sendItemDeletedSms(User owner, User recipient, String message, boolean isNote) throws ServerSideException {
		return sendSms(recipient, "" + owner.getFullname() + " deleted " + (isNote ? "a note " : "an item ") + (message == null ? "" : (": " + message)));
	}

	@Override
	public Void sendItemRevokedSms(User owner, User recipient, String message, boolean isNote) throws ServerSideException {
		return sendSms(recipient, "" + owner.getFullname() + " revoked " + (isNote ? "a note " : "an item ") + (message == null ? "" : (": " + message)));
	}

	@Override
	public Void sendItemSharedSms(final Long itemAssignmentId, final User owner, final User recipient, final String comment, final boolean isNote) {
//		return sendSms(recipient, "" + owner.getFullname() + " shared " + (isNote ? "note " : "item ") + itemAssignmentOperations.get(itemAssignmentId).getName() + (comment == null ? "" : (": " + comment)));
		return sendSms(recipient, "" + owner.getFullname() + " shared " + (isNote ? "note " : "item ") + (comment == null ? "" : (": " + comment)));
	}

	@Override
	public Void sendContainerSharedSms(Long containerAssignmentId, User owner, User recipient, String comment) {
		return sendSms(recipient, "" + owner.getFullname() + " shared container" + containerAssignmentOperations.get(containerAssignmentId).getName() + (comment == null ? "" : (": " + comment)));
	}

	@Override
	public Void sendTestSms(final UserDTO recipient, final String message) {
		licenseCheck();

		final User u = userOperations.get(recipient.getId());
		String mobilePhone = u.getMobile1();
		ServerUtils.validatePhoneNumber(mobilePhone);
		// won't here if phone number is invalid (not a valid E.164 number)
		if (!u.acceptsSMSMessages()) {
			throw new PermissionDeniedException("User does not accept SMS messages.");
		}
		return sendSms(u, message);
	}

	// Test SMS message is sent my admin and can be sent to any phone number
	// Only check if license allow it
	@Override
	public Void sendTestSms(final String recipientPhoneNumber, final String message) throws ServerSideException {
		licenseCheck();
		ServerUtils.sendSmsMessage(getSMSConfig(null), message, recipientPhoneNumber);
		return null;
	}
}
