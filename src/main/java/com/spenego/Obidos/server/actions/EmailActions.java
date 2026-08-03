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
//import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;

public interface EmailActions {
	public interface EmailMessageGenerator {
		String get(String scheme, String serverName, int port, String contextPath);
	}

	Void		sendUserCreationEmail(User requestor, Long userId);
	Void		sendUserCreationEmail(User requestor, Long userId, String message);
	Void		sendPasswordResetEmail(User requestor, Long userId, String subject, String token, Boolean requires2FACode);
	Void		sendPassphraseResetEmail(User requestor, Long userId, String subject, String token, Boolean requires2FACode);
	Void		sendPasswordResetWarningEmail(String to, String subject);
	Void		sendItemSharedEmail(Long itemAssignmentId, User itemOwner, User itemRecipient, String shareComment, boolean isNote);
	Void		sendItemRevokedEmail(User itemOwner, User itemRecipient, String revokeComment);
	Void		sendItemDeletedEmail(User itemOwner, User itemRecipient);
	Void		sendContainerSharedEmail(Long containerId, User containerOwner, User containerRecipient, String shareComment);
	Void		sendContainerRevokedEmail(User containerOwner, User containerRecipient, String shareComment);
	Void		sendSimple(SmtpEnvelope envelope, SmtpConfigDTO config);

	Void		sendEmail(User admin, SmtpConfigDTO smtpConfig, SmtpEnvelope envelope);
	Void		sendNotificationTemplateTestEmail(User admin, SmtpEnvelope envelope, Long templateType);
//	Void		sendEmail(User admin, SmtpEnvelope envelope);
//	Void        sendEmail(User admin, SmtpEnvelope envelope, NotificationTemplateJSONDTO dto);
}
