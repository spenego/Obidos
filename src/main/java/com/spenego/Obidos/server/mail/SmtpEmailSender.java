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

package com.spenego.Obidos.server.mail;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

/**
 *
 * @author spgdev@spenego.com - Apr 22, 2018
 */
public class SmtpEmailSender implements EmailSender<ObidosEmailData> {
	private static final Logger logger = LoggerFactory.getLogger(SmtpEmailSender.class);

	@Override
	@Async
	public Void sendAsync(ObidosEmailData email) {
		logger.info(() -> ">>>>>>> sending mail asynchronously");
		/*
		 * try { int n = 20; logger.info(() -> "Mailsender Sleeping for " + n +
		 * " Seconds for testing async"); Thread.sleep(n * 1000); } catch
		 * (InterruptedException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
		final JavaMailSender ms = email.getJavaMailSender();
		MimeMessage mimeMessage = ms.createMimeMessage();
		MimeMessageHelper helper;

		String htmlMessage = email.getHtmlMessage();
		String textMssage = email.getTextMessage();

		logger.info(() -> "+++++++++++++++ Sending mail ++++++++++++++++++=+++");

		try {
			helper = new MimeMessageHelper(mimeMessage, true);
			helper.setSubject(email.getSmtpEnvelope().getSubject());
			// helper.setText(htmlMessage,true);
			helper.setText(textMssage, htmlMessage);
			// helper.setTo(email.getSmtpEnvelope().getTos());
			helper.setTo(InternetAddress.parse(email.getSmtpEnvelope().getTos()));
			helper.setSubject(email.getSmtpEnvelope().getSubject());
			helper.setFrom(email.getSmtpEnvelope().getFrom());
		} catch (MessagingException e) {
			logger.info(() -> "Runtime exception: " + e.getMessage());
			throw new RuntimeException(e);
		}

		ms.send(mimeMessage);

		return null;
	}
}
