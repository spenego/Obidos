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

import static com.spenego.Obidos.server.utils.ServerUtils.parseJSONTemplate;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.server.actions.EmailActions;
import com.spenego.Obidos.server.actions.LoginActions;
import com.spenego.Obidos.server.actions.NotificationTemplateActions;
import com.spenego.Obidos.server.actions.SmtpConfigActions;
import com.spenego.Obidos.server.mail.ObidosEmailData;
import com.spenego.Obidos.server.mail.SmtpEmailSender;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.validators.ValidateBean;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.EmailMessageTemplateDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;
import com.spenego.Obidos.shared.dto.SmtpEnvelope;

// This class cannot be final. It is sub-classed by Spring for the asynchronous mail sending functionality.
public class EmailActionsImpl extends ObidosActions<User> implements EmailActions {
	private static final Logger logger = LoggerFactory.getLogger(EmailActionsImpl.class);

	private static final String ERROR_STRING = "errorString";

	@Autowired private final NotificationTemplateActions	notificationTemplateActions = null;
	@Autowired private final SmtpConfigActions				smtpConfigActions = null;
	@Autowired private final SmtpEmailSender				smtpEmailSender = null;
	@Autowired private final LoginActions					loginActions = null;

	private final HashMap<String, Mustache>					mustacheMap = new HashMap<>();

	public EmailActionsImpl() { /* members are Autowired or initialized at declaration */ }

	@Override
	public Void sendUserCreationEmail(final User requestor, final Long userId) {
		return sendUserCreationEmail(requestor, userId, null);
	}

	private String getFromStr() {
		return getSystemConfig().getAdminEmail();
	}

	private String getUrl() {
		final SystemConfig sc = getSystemConfig();
		final String scheme = sc.getScheme() == null ? "https" : sc.getScheme();
		final String systemContextPath = sc.getContextPath();
		final String contextPath = systemContextPath == null ? "" : new File(systemContextPath).getAbsoluteFile().toString();
		Integer port = sc.getServerPort();
		if (port == null) {
			port = 443;
		}
		StringBuilder urlBuilder = new StringBuilder(scheme).append("://").append(sc.getFqdn());
		// if port is 443, don't add it in URL
		if (port != 443) {
			urlBuilder.append(":");
			urlBuilder.append(sc.getServerPort());
		}

		return urlBuilder.append(contextPath).toString();
	}

	private String constructLoginUrl() {
		return getUrl() + "#LOGIN";
	}

	private String constructResetUrl(final String token, final Boolean requires2FACode, final String tokenName) {
		return getUrl() + "#" + tokenName + ";token=" + token + ";" + ObidosConstants.TWO_FACTOR_CODE_REQUIRED + "=" + requires2FACode;
	}

	private static String typeDescription(final String id_type) {
		return id_type.equals(ObidosConstants.CONTAINER_ID) ? NameTokens.LIST_CONTAINERS_SHARED_WITH_ME : NameTokens.LIST_ITEMS_SHARED_WITH_ME;
	}

	private String constructUrl(final String id_type, final Long id, final Long ownerId, final Supplier<String> urlComponentSupplier) {
		return getUrl() + "/index.html#"
				+ ((id == null) ? typeDescription(id_type) : (NameTokens.VIEW_ITEM + ";" + ObidosConstants.ACTION + "=" + ObidosConstants.VIEW + ";" + getString(urlComponentSupplier)
								+ id_type + "=" + id))
				+ ";" + ObidosConstants.OWNERID + "=" + ownerId + ";" + ObidosConstants.TYPE + "=sharedWithMe;" + ObidosConstants.PLACE + "=NOTIFICATION_MESSAGE";
	}

	private String constructItemActionUrl(final Long itemAssignmentId, final Long ownerId, final boolean isNote) {
		return constructUrl(ObidosConstants.ITEM_ID, itemAssignmentId, ownerId, () -> isNote ? "itemType=noteBook;" : "");
	}

	private String constructContainerActionUrl(final Long containerAssinmentId, final Long ownerId) {
		return constructUrl(ObidosConstants.CONTAINER_ID, containerAssinmentId, ownerId, null);
	}

	private SmtpConfigDTO getSmtpConfig() {
		final String configName = null;
		return smtpConfigActions.get(configName); // use default configuration
	}

	private static String getMustacheStr(final Mustache mustache, final NotificationTemplateJSONDTO dto) {
		try(final StringWriter writer = new StringWriter()) {
			try(final Writer mustacheWriter = mustache.execute(writer, dto)) {
				mustacheWriter.flush();
				return writer.toString();
			}
		} catch (final IOException e) {
			logger.exception(e);
			return null;
		}
	}

	private static NotificationTemplateJSONDTO getNotificationTemplateDTO2(final NotificationTemplateJSONDTO nt, final String name, final String action, final boolean fixHello) {
		// fill out specific things as well. This dto will be used as template for Mustache.
		if (name != null) {
			nt.setName(name);
		}
		if (action != null) {
			nt.setAction_url(action);
		}
		if (fixHello) {
			String hello = nt.getHello();
			if (hello != null) {
				nt.setHello(getMustacheStr(new DefaultMustacheFactory().compile(new StringReader(hello), ERROR_STRING), nt));
			}
		}

		return nt;
	}

	private static NotificationTemplateJSONDTO getNotificationTemplateDTO(final EmailMessageTemplateDTO templateDTO, final String name, final String action, final boolean fixHello) {
		return getNotificationTemplateDTO2(parseJSONTemplate(new String(templateDTO.getMessage()), NotificationTemplateJSONDTO.class), name, action, fixHello);
	}

	private Mustache getMustache(final String htmlTemplatePath) {
		final Mustache m = mustacheMap.get(htmlTemplatePath);
		if (m != null) {
			return m;
		}

		final Mustache mustache = new DefaultMustacheFactory().compile(htmlTemplatePath);

		mustacheMap.put(htmlTemplatePath, mustache);

		return mustache;
	}

	private static String getTextAttachment(final NotificationTemplateJSONDTO dto) {
		final StringBuilder sb = new StringBuilder(dto.getHello().length() + dto.getText_message().length() + dto.getContact().length() + dto.getFooter().length() + 16);

		return sb.append(dto.getHello()).append(",\n").append(dto.getText_message()).append("\n\n").append(dto.getContact()).append("\n\n").append(dto.getFooter()).toString();
	}

	@Override
	public Void sendSimple(final SmtpEnvelope envelope, final SmtpConfigDTO smtpConfig) {
		final String htmlMessage = envelope.getHtmlMessage();
		final String textMessage = envelope.getTextMessage();
		return smtpEmailSender.sendAsync(new ObidosEmailData(envelope, htmlMessage, textMessage, getMailSender()));
	}

	private static Properties getMailProperties(final SmtpConfigDTO smtpConfig, final String from) {
		final Properties p = new Properties();

		if (from != null) {
			p.setProperty("mail.from", from);
		}

		if (isTrue(smtpConfig.getUseStartTls())) {
			p.setProperty("mail.smtp.starttls.enable", "true");
			p.setProperty("mail.smtp.ssl.trust", "*");
		} else if (isTrue(smtpConfig.getUseSsl())) {
			p.setProperty("mail.smtp.ssl.enable", "true");
			p.setProperty("mail.smtp.ssl.trust", "*");
		}

		if (logger.isInfoEnabled()) {
			p.setProperty("mail.debug", "true");
		}
		return p;
	}

	private static JavaMailSender createMailSender(final SmtpConfigDTO smtpConfig, final String from) {
		final JavaMailSenderImpl ms = new JavaMailSenderImpl();
		ms.setHost(smtpConfig.getSmtpServer());

		if (smtpConfig.getSmtpUsername() != null) {
			ms.setUsername(smtpConfig.getSmtpUsername());
		}

		if (smtpConfig.getSmtpPassword() != null) {
			ms.setPassword(smtpConfig.getSmtpPassword());
		}

		ms.setPort(smtpConfig.getSmtpPort());
		ms.setProtocol("smtp");
		ms.setJavaMailProperties(getMailProperties(smtpConfig, from));

		return ms;
	}

	private JavaMailSender getMailSender() {
		return createMailSender(getSmtpConfig(), null);
	}

	@Override
	public Void sendUserCreationEmail(final User requestor, final Long recipientId, final String comment) {
		// Use in-lined responsive HTML template and also text as attachment
		final User user = getUser(recipientId);
		final String subject = "Spenego Obidos Account created"; // will be replaced by template
		final String to = user.getEmail1();
		final String from = requestor.getEmail1();
		String htmlMessage = "N/A";
		String textMessage = "N/A";
		final SmtpEnvelope envelope = new SmtpEnvelope(from, to, subject, htmlMessage, textMessage); // messages are not used, we use templates instead
		final NotificationTemplateJSONDTO dto = getNotificationTemplateDTO(notificationTemplateActions.getAccountCreatedEmailMessage(), user.getFullname(), constructLoginUrl(), true);
		// Issue # 363
		if (comment != null && comment.length() > 0) {
			dto.setComment(comment);
		}

		return sendMailAsync(envelope, ObidosConstants.ACCOUNT_CREATED_NOTIFICATION_TEMPLATE, dto);
	}

	private Void sendEmail(final User owner, final User recipient, final String comment, final Supplier<String> urlSupplier, final NotificationTemplateJSONDTO template, final long templateType) {
		final NotificationTemplateJSONDTO dto = getNotificationTemplateDTO2(template, recipient.getFullname(), urlSupplier.get(), true);
		dto.setComment(comment);
		fixOwnername(dto, owner.getFullname());

		final SmtpEnvelope envelope = new SmtpEnvelope(owner.getEmail1(), recipient.getEmail1(), dto.getSubject(), null, null);
		return sendMailAsync(envelope, templateType, dto);
	}

	// fix {{owner_name}} in text and html message
	private static void fixOwnername(final NotificationTemplateJSONDTO dto, final String ownerFullname) {
		if (ownerFullname != null && ownerFullname.length() > 0) {
			// fill owner_name
			dto.setOwner_name(ownerFullname);

			// now replace {{owner_name}} in html_message with real owner name
			final MustacheFactory factory = new DefaultMustacheFactory();
			String html = dto.getHtml_message();
			if (html != null)
			{
				final Mustache mustache = factory.compile(new StringReader(html), ERROR_STRING);
				String str = getMustacheStr(mustache, dto);
				dto.setHtml_message(str);
			}
			else
			{
				logger.error(()-> "DTO does not contain any html_message");
			}

			String text = dto.getText_message();
			if (text != null)
			{
				final Mustache mustache2 = factory.compile(new StringReader(text), ERROR_STRING);
				String str = getMustacheStr(mustache2, dto);
				dto.setText_message(str);
			}
			else
			{
				logger.error(()-> "DTO does not contain any text_message");
			}
		}
	}

	private boolean licenseSupportsNotificationEmail() {
		return isTrue(loginActions.currentLicenseStats().getSupportsNotificationEmails());
	}

	private Void sendItemEmail(final Long itemAssignmentId, final User itemOwner, final User itemRecipient, final String shareComment, final long templateType, final boolean isNote) {
		if (!licenseSupportsNotificationEmail()) {
			return null;
		}
		final NotificationTemplateJSONDTO dto = notificationTemplateActions.getNotificationTemplateJSONDTO(templateType);
		// replace owner_name
		fixOwnername(dto, itemOwner.getFullname());
		return sendEmail(itemOwner, itemRecipient, shareComment, () -> constructItemActionUrl(itemAssignmentId, itemOwner.getId(), isNote), dto, templateType);
	}

	@Override
	public Void sendItemSharedEmail(final Long itemAssignmentId, final User itemOwner, final User itemRecipient, final String shareComment, final boolean isNote) {
		return sendItemEmail(itemAssignmentId, itemOwner, itemRecipient, shareComment, ObidosConstants.ITEM_SHARED_NOTIFICATION_TEMPLATE, isNote);
	}

	@Override
	public Void sendItemRevokedEmail(final User itemOwner, final User itemRecipient, final String shareComment) {
		logger.info(() -> " Sending item revoked email...owner: " + itemOwner);
		return sendItemEmail(null, itemOwner, itemRecipient, shareComment, ObidosConstants.ITEM_REVOKED_NOTIFICATION_TEMPLATE, false);
	}

	@Override
	public Void sendItemDeletedEmail(final User itemOwner, final User itemRecipient) {
		return sendItemEmail(null, itemOwner, itemRecipient, "The owner of the item deleted the item.", ObidosConstants.ITEM_DELETED_NOTIFICATION_TEMPLATE, false);
	}

	@Override
	public Void sendPasswordResetEmail(final User requestor, final Long recipientId, final String subject, final String token, final Boolean requires2FACode) {
		return sendEmail(requestor, getUser(recipientId), null, () -> constructResetUrl(token, requires2FACode, NameTokens.RESET_PASSWORD),
				notificationTemplateActions.getPasswordResetNotificationTemplateJSONDTO(), ObidosConstants.PASSWORD_RESET_NOTIFICATION_TEMPALTE);
	}

	@Override
	public Void sendPassphraseResetEmail(final User requestor, final Long recipientId, final String subject, final String token, final Boolean requires2FACode) {
		return sendEmail(requestor, getUser(recipientId), null, () -> constructResetUrl(token, requires2FACode, NameTokens.RESET_PASSPHRASE), notificationTemplateActions.getPassphraseResetNotificationTemplateJSONDTO(), ObidosConstants.PASSPHRASE_RESET_NOTIFICATION_TEMPALTE);
	}

	// this email is sent when someone enters an email address not in our
	// database
	// therefore, the user won't exist
	@Override
	public Void sendPasswordResetWarningEmail(final String to, final String subject) {
		// fill out specific things as well. This dto will be used as template
		// for Mustache.
		logger.info(() -> " SPREW to: " + to);
		final NotificationTemplateJSONDTO dto = getNotificationTemplateDTO2(notificationTemplateActions.getPasswordResetWarningNotificationTemplateJSONDTO(), null, null, false);

		// we don't use messages, we use templates
		String htmlMessage = null;
		String textMessage = null;
		final SmtpEnvelope envelope = new SmtpEnvelope(getFromStr(), to, subject, htmlMessage, textMessage);

		return sendMailAsync(envelope, ObidosConstants.PASSWORD_RESET_WARNING_NOTIFICATION_TEMPALTE, dto);
	}

	private Void sendContainerEmail(final Long containerAssignmentId, final User containerOwner, final User containerRecipient, final String shareComment, final NotificationTemplateJSONDTO template, final long templateType) {
		if (!licenseSupportsNotificationEmail()) {
			return null;
		}
		return sendEmail(containerOwner, containerRecipient, shareComment, () -> constructContainerActionUrl(containerAssignmentId, containerOwner.getId()), template, templateType);
	}

	@Override
	public Void sendContainerSharedEmail(final Long containerAssignmentId, final User containerOwner, final User containerRecipient, final String shareComment) {
		return sendContainerEmail(containerAssignmentId, containerOwner, containerRecipient, shareComment, notificationTemplateActions.getContainerSharedNotificationTemplateJSONDTO(), ObidosConstants.CONTAINER_SHARED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void sendContainerRevokedEmail(final User containerOwner, final User containerRecipient, final String shareComment) {
		return sendContainerEmail(null, containerOwner, containerRecipient, shareComment, notificationTemplateActions.getContainerRevokedNotificationTemplateJSONDTO(), ObidosConstants.CONTAINER_REVOKED_NOTIFICATION_TEMPLATE);
	}

	@Override
	public Void sendEmail(final User admin, final SmtpConfigDTO smtpConfig, final SmtpEnvelope envelope) {
		ValidateBean.validate(smtpConfig);
		return sendSimple(envelope, smtpConfig);
	}

	/**
	 * Use SmtpEmailSender spring bean to send mail asynchronously.
	 *
	 * @param envelope
	 * @param templateType
	 * @param dto
	 * @return null
	 *         <p>
	 * @author spgdev@spenego.com - Aug 25, 2018
	 */
	private Void sendMailAsync(final SmtpEnvelope envelope, final long templateType, final NotificationTemplateJSONDTO dto) {
		final String htmlTemplateFile = ObidosConstants.getTemplatePathname(templateType) + ".html";
		logger.info(()-> "HTML template file: " + htmlTemplateFile);
		final Mustache mustache = getMustache(htmlTemplateFile);
		final String htmlMessage = getMustacheStr(mustache, dto);
		logger.info(()-> "HTML Message: " + htmlMessage);
		final String textMessage = getTextAttachment(dto);
		logger.info(() -> "owner: " + dto.getOwner_name());
		return smtpEmailSender.sendAsync(new ObidosEmailData(envelope, htmlMessage, textMessage, getMailSender()));
	}

	@Override
	protected final Operations<User> getOperations() {
		return userOperations;
	}

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return null;
	}

	/**
	 * This method is used to send test email with notification template by
	 * admin
	 */
	@Override
	public Void sendNotificationTemplateTestEmail(final User admin, final SmtpEnvelope envelope, final Long templateType) {
		if (templateType == null) {
			logger.info(()-> "Template type is null, will not send test email");
		}

		final EmailMessageTemplateDTO emailMessageTemplateDTO = notificationTemplateActions.getEmailMessageTemplateMessage(templateType);
		final NotificationTemplateJSONDTO dto = getNotificationTemplateDTO(emailMessageTemplateDTO, "Test User", "https://example.com/params", true);
		fixOwnername(dto, "Test User");
		return sendMailAsync(envelope, templateType, dto);
	}

	/**          Code Graveyard
	@Override
	public Void sendEmail(final User admin, final SmtpEnvelope envelope) {
		String configName = null;
		return sendEmail(admin, smtpConfigActions.get(admin, configName), envelope);
	}


	@Override
	public Void sendEmail(final User admin, final SmtpEnvelope envelope, final NotificationTemplateJSONDTO dto) {
		final String htmlTemplateFile = ObidosConstants.getTemplatePathname(dto.getId()) + ".html";
		final Mustache mustache = getMustache(htmlTemplateFile);
		final String htmlMessage = getMustacheStr(mustache, dto);
		final String textMessage = getTextAttachment(dto);
		return smtpEmailSender.sendAsync(new ObidosEmailData(envelope, htmlMessage, textMessage, getMailSender()));
	}
	**/
}
