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

package com.spenego.Obidos.server.actions;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.shared.dto.EmailMessageTemplateDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;

public interface NotificationTemplateActions {

	EmailMessageTemplateDTO getAccountCreatedEmailMessage();
	EmailMessageTemplateDTO getContainerSharedEmailMessage();
	EmailMessageTemplateDTO getContainerRevokedEmailMessage();
	EmailMessageTemplateDTO getItemSharedEmailMessage();
	EmailMessageTemplateDTO getItemRevokedEmailMessage();
	EmailMessageTemplateDTO getItemDeletedEmailMessage();
	EmailMessageTemplateDTO getPasswordResetEmailMessage();
	EmailMessageTemplateDTO getPasswordResetWarningEmailMessage();
	EmailMessageTemplateDTO getPassphraseResetEmailMessage();
	EmailMessageTemplateDTO getPassphraseResetWarningEmailMessage();

	EmailMessageTemplateDTO getEmailMessageTemplateMessage(Long templateType);

	NotificationTemplateJSONDTO getAccountCreatedNotificationTemplateJSONDTO();
	NotificationTemplateJSONDTO getContainerSharedNotificationTemplateJSONDTO();
	NotificationTemplateJSONDTO getContainerRevokedNotificationTemplateJSONDTO();
	NotificationTemplateJSONDTO getItemSharedNotificationTemplateJSONDTO();
	NotificationTemplateJSONDTO getItemRevokedNotificationTemplateJSONDTO();

	// password reset
	NotificationTemplateJSONDTO getPasswordResetNotificationTemplateJSONDTO();
	NotificationTemplateJSONDTO getPasswordResetWarningNotificationTemplateJSONDTO();


	// passphrase reset
	NotificationTemplateJSONDTO getPassphraseResetNotificationTemplateJSONDTO();
	NotificationTemplateJSONDTO getPassphraseResetWarningNotificationTemplateJSONDTO();

	NotificationTemplateJSONDTO getNotificationTemplateJSONDTO(Long templateType);


	Void updateAccountCreatedEmailMessage				(User caller, EmailMessageTemplateDTO template);
	Void updateContainerSharedEmailMessage				(User caller, EmailMessageTemplateDTO template);
	Void updateContainerRevokedEmailMessage				(User caller, EmailMessageTemplateDTO template);
	Void updateContainerSharingEmailMessage				(User caller, EmailMessageTemplateDTO template);
	Void updatePasswordResetEmailMessage				(User caller, EmailMessageTemplateDTO template);
	Void updatePasswordResetWarningEmailMessage			(User caller, EmailMessageTemplateDTO template);

	Void updateAccountCreatedNotificationTemplate		(User caller, NotificationTemplateJSONDTO template);
	Void updateContainerSharedNotificationTemplate		(User caller, NotificationTemplateJSONDTO template);
	Void updateContainerRevokedNotificationTemplate		(User caller, NotificationTemplateJSONDTO template);
	Void updateItemSharedNotificationTemplate			(User caller, NotificationTemplateJSONDTO template);
	Void updateItemRevokedNotificationTemplate			(User caller, NotificationTemplateJSONDTO template);

	// Password reset
	Void updatePasswordResetNotificatonTempalte			(User caller, NotificationTemplateJSONDTO template);
	Void updatePasswordResetWarningNotificatonTempalte	(User caller, NotificationTemplateJSONDTO template);

	// Passphrase reset
	Void updatePassphraseResetNotificatonTemplate			(User caller, NotificationTemplateJSONDTO template);
	Void updatePassphraseResetWarningNotificatonTemplate	(User caller, NotificationTemplateJSONDTO template);
}
