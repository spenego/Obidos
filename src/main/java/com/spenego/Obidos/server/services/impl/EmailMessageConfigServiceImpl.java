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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.EmailMessageConfigService;
import com.spenego.Obidos.server.actions.NotificationTemplateActions;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("emailMessageConfigService")
public final class EmailMessageConfigServiceImpl extends ObidosService implements EmailMessageConfigService {
	private static final Logger logger = LoggerFactory.getLogger(EmailMessageConfigServiceImpl.class);

	@Autowired private final NotificationTemplateActions actions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	//////////////////////
	// password reset
	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getPasswordResetNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a password reset email template", "getPasswordResetNotificationTemplateJSONDTO", false, admin -> actions.getPasswordResetNotificationTemplateJSONDTO());
	}

	@Transactional @Override
	public Void updatePasswordResetNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update a password reset email template", "updatePasswordResetNotificationTemplateJSONDTO", true, admin -> actions.updatePasswordResetNotificatonTempalte(admin, dto));
	}

	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getPasswordResetWarningNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a password reset warning email template", "getPasswordResetWarningNotificationTemplateJSONDTO", false, admin -> actions.getPasswordResetWarningNotificationTemplateJSONDTO());
	}

	@Transactional @Override
	public Void updatePasswordResetWarningNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update a password reset warning email template", "updatePasswordResetWarningNotificationTemplateJSONDTO", true, admin -> actions.updatePasswordResetWarningNotificatonTempalte(admin, dto));
	}

	// Passphrase reset -starts-
	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getPassphraseResetNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a passphrase reset email template", "getPassphraseResetNotificationTemplateJSONDTO", false, admin -> actions.getPassphraseResetNotificationTemplateJSONDTO());
	}

	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getPassphraseResetWarningNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a passphrase reset warning email template", "getPassphraseResetWarningNotificationTemplateJSONDTO", false, admin -> actions.getPassphraseResetWarningNotificationTemplateJSONDTO());
	}

	@Transactional @Override
	public Void updatePassphraseResetNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update a passphrase reset email template", "updatePassphraseResetNotificationTemplateJSONDTO", true, admin -> actions.updatePassphraseResetNotificatonTemplate(admin, dto));
	}

	@Transactional @Override
	public Void updatePassphraseResetWarningNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update Passphrase Reset Warning Email template", "updatePassphraseResetNotificationTemplateJSONDTO", true, admin -> actions.updatePassphraseResetWarningNotificatonTemplate(admin, dto));
	}
	// Passphrase reset -ends-

	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getItemSharedNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get an Item Shared email template", "getItemSharedNotificationTemplateJSONDTO", false, admin -> actions.getItemSharedNotificationTemplateJSONDTO());
	}

 	@Transactional @Override
	public Void updateItemSharedNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update Item Shared Email template", "updateItemSharedNotificationTemplateJSONDTO", true, admin -> actions.updateItemSharedNotificationTemplate(admin, dto));
	}

	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getItemRevokedNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get an Item Revoked email template", "getItemRevokedNotificationTemplateJSONDTO", false, admin -> actions.getItemRevokedNotificationTemplateJSONDTO());
	}

 	@Transactional @Override
	public Void updateItemRevokedNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update Item Revoked Email template", "updateItemRevokedNotificationTemplateJSONDTO", true, admin -> actions.updateItemRevokedNotificationTemplate(admin, dto));
	}


 	// Container shared
	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getContainerSharedNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a Container Shared email template", "getContainerSharedNotificationTemplateJSONDTO", false, admin -> actions.getContainerSharedNotificationTemplateJSONDTO());
	}

	// Container revoked
	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getContainerRevokedNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get a Container Revoked email template", "getContainerRevokedNotificationTemplateJSONDTO", false, admin -> actions.getContainerRevokedNotificationTemplateJSONDTO());
	}

 	@Transactional @Override
	public Void updateContainerSharedNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update a Container Shared email template", "updateContainerSharedNotificationTemplateJSONDTO", true, admin -> actions.updateContainerSharedNotificationTemplate(admin, dto));
	}

  	@Transactional @Override
	public Void updateContainerRevokedNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException
 	{
		return adminFunction(creds, "update a Container Revoked email template", "updateContainerRevokedNotificationTemplateJSONDTO", true, admin -> actions.updateContainerRevokedNotificationTemplate(admin, dto));
	}

 	@Transactional(readOnly=true) @Override
	public NotificationTemplateJSONDTO getAccountCreatedNotificationTemplateJSONDTO(final AuthCredsDTO creds) throws ServerSideException {
		return adminFunction(creds, "get an Account Created email template", "getAccountCreatedNotificationTemplateJSONDTO", false, admin -> actions.getAccountCreatedNotificationTemplateJSONDTO());
	}

 	@Transactional @Override
	public Void updateAccountCreatedNotificationTemplateJSONDTO(final AuthCredsDTO creds, final NotificationTemplateJSONDTO dto) throws ServerSideException {
		return adminFunction(creds, "update an Account Created email template", "updateAccountCreatedNotificationTemplateJSONDTO", true, admin -> actions.updateAccountCreatedNotificationTemplate(admin, dto));
	}
}
