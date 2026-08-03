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

package com.spenego.Obidos.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@RemoteServiceRelativePath("rpc/emailMessageConfigService")
public interface EmailMessageConfigService extends RemoteService {
	public static class Utility {
		private Utility() { /* no instances */ }
		private static final EmailMessageConfigServiceAsync instance = (EmailMessageConfigServiceAsync) GWT.create(EmailMessageConfigService.class);
		public static final EmailMessageConfigServiceAsync getInstance() { return instance; }
	}

	// password reset request
	public NotificationTemplateJSONDTO getPasswordResetNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updatePasswordResetNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;
	

	// password reset request warning
	public NotificationTemplateJSONDTO getPasswordResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updatePasswordResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;

	// passphrase reset request
	public NotificationTemplateJSONDTO getPassphraseResetNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updatePassphraseResetNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;

	// password reset request warning
	public NotificationTemplateJSONDTO getPassphraseResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updatePassphraseResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;


	// item shared
	public NotificationTemplateJSONDTO getItemSharedNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updateItemSharedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;
	
	// item revoked
	public NotificationTemplateJSONDTO getItemRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updateItemRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;


	// account created
	public NotificationTemplateJSONDTO getAccountCreatedNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updateAccountCreatedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;
	
	// container shared
	public NotificationTemplateJSONDTO getContainerSharedNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updateContainerSharedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;
	
	// container revoked
	public NotificationTemplateJSONDTO getContainerRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds) throws ServerSideException;
	public Void updateContainerRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto) throws ServerSideException;
}
