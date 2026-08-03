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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;

public interface EmailMessageConfigServiceAsync {

	//password reset
	void getPasswordResetNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updatePasswordResetNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

	void getPasswordResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updatePasswordResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

	// passphrase reset
	void getPassphraseResetNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updatePassphraseResetNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

	void getPassphraseResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updatePassphraseResetWarningNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

	void getItemSharedNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updateItemSharedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

	void getItemRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updateItemRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

	void getAccountCreatedNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updateAccountCreatedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);
	
	// Container shared Feb-24-2019
	void getContainerSharedNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updateContainerSharedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);
	// Container revoked Feb-24-2019
	void getContainerRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds, AsyncCallback<NotificationTemplateJSONDTO> callabck);
	void updateContainerRevokedNotificationTemplateJSONDTO(AuthCredsDTO creds, NotificationTemplateJSONDTO dto, AsyncCallback<Void> callback);

}
