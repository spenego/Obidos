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

import static com.spenego.Obidos.server.utils.ServerUtils.bytesToString;
import static com.spenego.Obidos.server.utils.ServerUtils.parseJSONTemplate;
import static com.spenego.Obidos.server.utils.ServerUtils.toJson;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.NotificationTemplateActions;
import com.spenego.Obidos.server.model.NotificationTemplate;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.NotificationTemplateOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.EmailMessageTemplateDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;

public class NotificationTemplateActionsImpl extends ObidosActions<NotificationTemplate> implements NotificationTemplateActions {
	private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateActionsImpl.class);

	@Autowired private final NotificationTemplateOperations notificationTemplateOperations = null;

	@Override
	protected final Operations<NotificationTemplate> getOperations() {
		return notificationTemplateOperations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return null;
	}

	private static void ensureCallerHasEmailTemplateModifyCapability(final User caller) {
		if (!caller.getModifyEmailTemplates()) {
			throw new PermissionDeniedException("You do not have Email Template Modification privledge. ");
		}
	}

	private static NotificationTemplate setId(final NotificationTemplate model, final Long id) {
		model.setId(id);
		return model;
	}

	private Void update(final User caller, final EmailMessageTemplateDTO template, final Long id) {
		ensureCallerHasEmailTemplateModifyCapability(caller);
		return notificationTemplateOperations.updateSelective(setId(convert(template, NotificationTemplate.class), id));
	}

	private EmailMessageTemplateDTO getEmailMessage(final Long id) {
		return convert(getModel(id), EmailMessageTemplateDTO.class);
	}

	@Override
	public NotificationTemplateJSONDTO getNotificationTemplateJSONDTO(final Long templateType) {
		return parseJSONTemplate(bytesToString(getEmailMessage(templateType).getMessage()), NotificationTemplateJSONDTO.class);
	}


	@Override
	public NotificationTemplateJSONDTO getPasswordResetNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE);
	}

	@Override
	public NotificationTemplateJSONDTO getPassphraseResetNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE);
	}

	@Override
	public NotificationTemplateJSONDTO getPassphraseResetWarningNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}

	// convert dto to JSON
	private static final EmailMessageTemplateDTO createTemplate(final NotificationTemplateJSONDTO dto) {
		return new EmailMessageTemplateDTO(toJson(dto));
	}

	@Override
	public Void updatePasswordResetNotificatonTempalte(final User caller, final NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE);
	}

	// passphrase reset starts---
	@Override
	public Void updatePassphraseResetNotificatonTemplate(User caller, NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE);
	}

	@Override
	public Void updatePassphraseResetWarningNotificatonTemplate(User caller, NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}
	// passphrase reset starts---


	@Override
	public NotificationTemplateJSONDTO getPasswordResetWarningNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}

	@Override
	public Void updatePasswordResetWarningNotificatonTempalte(final User caller, final NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}

	@Override
	public NotificationTemplateJSONDTO getItemSharedNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void updateItemSharedNotificationTemplate(final User caller, final NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public NotificationTemplateJSONDTO getItemRevokedNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE);
	}


	@Override
	public Void updateItemRevokedNotificationTemplate(final User caller, final NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE);
	}


	@Override
	public NotificationTemplateJSONDTO getAccountCreatedNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void updateAccountCreatedNotificationTemplate(final User caller, final NotificationTemplateJSONDTO dto) {
		return update(caller, createTemplate(dto), ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE);
	}

	public Void updatePasswordResetEmailMessage(final User caller, final EmailMessageTemplateDTO template) {
		return update(caller, template, ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE);
	}


	@Override
	public Void updatePasswordResetWarningEmailMessage(final User caller, final EmailMessageTemplateDTO template) {
		return update(caller, template, ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}

	@Override
	public EmailMessageTemplateDTO getItemSharedEmailMessage() {
		return getEmailMessage(ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public EmailMessageTemplateDTO getItemRevokedEmailMessage() {
		return getEmailMessage(ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE);
	}
	@Override
	public EmailMessageTemplateDTO getItemDeletedEmailMessage()
	{
		return getEmailMessage(ObidosConstants.ITEM_DELETED_NOTIFICATION_TEMPLATE);
	}


	@Override
	public Void updateContainerSharingEmailMessage(final User caller, final EmailMessageTemplateDTO template)
	{
		return update(caller, template, ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public EmailMessageTemplateDTO getAccountCreatedEmailMessage() {
		return getEmailMessage(ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public EmailMessageTemplateDTO getPasswordResetEmailMessage() {
		return getEmailMessage(ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE);
	}

	@Override
	public EmailMessageTemplateDTO getPasswordResetWarningEmailMessage() {
		return getEmailMessage(ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}

	@Override
	public EmailMessageTemplateDTO getPassphraseResetEmailMessage()
	{
		return getEmailMessage(ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE);
	}

	@Override
	public EmailMessageTemplateDTO getPassphraseResetWarningEmailMessage()
	{
		return getEmailMessage(ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE);
	}

	@Override
	public Void updateAccountCreatedEmailMessage(final User caller, final EmailMessageTemplateDTO template) {
		return update(caller, template, ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public NotificationTemplateJSONDTO getContainerSharedNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void updateContainerSharedNotificationTemplate(final User caller, final NotificationTemplateJSONDTO template) {
		return update(caller, createTemplate(template), ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public EmailMessageTemplateDTO getContainerSharedEmailMessage() {
		return getEmailMessage(ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void updateContainerSharedEmailMessage(final User caller, final EmailMessageTemplateDTO template) {
		return update(caller, template, ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public NotificationTemplateJSONDTO getContainerRevokedNotificationTemplateJSONDTO() {
		return getNotificationTemplateJSONDTO(ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void updateContainerRevokedNotificationTemplate(final User caller, final NotificationTemplateJSONDTO template) {
		return update(caller, createTemplate(template), ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public EmailMessageTemplateDTO getContainerRevokedEmailMessage() {
		return getEmailMessage(ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void updateContainerRevokedEmailMessage(final User caller, final EmailMessageTemplateDTO template) {
		return update(caller, template, ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public EmailMessageTemplateDTO getEmailMessageTemplateMessage(final Long templateType) {
		if (templateType == null) {
			return null;
		}

		switch (templateType.intValue()) {
		case (int) ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE: {
			return getAccountCreatedEmailMessage();
		}

		case (int) ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE: {
			return getPasswordResetEmailMessage();
		}

		case (int) ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE: {
			return getPasswordResetWarningEmailMessage();
		}

		case (int) ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE: {
			return getItemSharedEmailMessage();
		}

		case (int) ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE: {
			return getItemRevokedEmailMessage();
		}

		case (int) ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE: {
			return getContainerSharedEmailMessage();
		}

		case (int) ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE: {
			return getPassphraseResetEmailMessage();
		}

		case (int) ObidosConstants.PASSPHRASE_RESET_WARNING_NOTIFICATION_TEMPALTE: {
			return getPassphraseResetWarningEmailMessage();
		}

		case (int) ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE: {
			return getContainerRevokedEmailMessage();
		}

		case (int) ObidosConstants.ITEM_DELETED_NOTIFICATION_TEMPLATE: {
			return getItemDeletedEmailMessage();
		}

		default: {
			return null;
		}
		}
	}
}
