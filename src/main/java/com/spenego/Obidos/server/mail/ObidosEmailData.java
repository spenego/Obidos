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

import org.springframework.mail.javamail.JavaMailSender;

import com.spenego.Obidos.shared.dto.SmtpEnvelope;

public final class ObidosEmailData {
	/**
	 * @author spgdev@spenego.com - Apr 22, 2018
	 */
	private JavaMailSender javaMailSender;
	private SmtpEnvelope smtpEnvelope;
	private String htmlMessage;
	private String textMessage;

	public ObidosEmailData() {
	}

	public ObidosEmailData(final SmtpEnvelope smtpEnvelope, final String htmlMessage, final String textMessage, final JavaMailSender javaMailSender) {
		this.smtpEnvelope = smtpEnvelope;
		this.htmlMessage = htmlMessage;
		this.textMessage = textMessage;
		this.javaMailSender = javaMailSender;
	}

	public String getHtmlMessage() {
		return htmlMessage;
	}

	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public SmtpEnvelope getSmtpEnvelope() {
		return smtpEnvelope;
	}

	public void setSmtpEnvelope(SmtpEnvelope smtpEnvelope) {
		this.smtpEnvelope = smtpEnvelope;
	}

	public JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
}
