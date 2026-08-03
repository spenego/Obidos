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
import com.spenego.Obidos.shared.dto.SmsKeyDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public interface SMSActions {
	SmsKeyDTO getSMSConfig(User admin) throws ServerSideException;
	Void updateSMSConfig(User admin, SmsKeyDTO smsKey) throws ServerSideException;
	Void sendTestSms(UserDTO recipient, String message) throws ServerSideException;
	Void sendTestSms(String recipientPhoneNumber, String message) throws ServerSideException;
	Void sendSms(User recipient, String message) throws ServerSideException;
	Void sendItemDeletedSms(User owner, User recipient, String comment, boolean isNote) throws ServerSideException;
	Void sendItemRevokedSms(User owner, User recipient, String comment, boolean isNote) throws ServerSideException;
	Void sendItemSharedSms(Long itemAssignmentId, User owner, User recipient, String comment, boolean isNote);
	Void sendContainerSharedSms(Long containerAssignmentId, User owner, User recipient, String comment);
	Void deleteSmsConfig(User admin) throws ServerSideException;
}
