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

package com.spenego.Obidos.server.utils;

/**
 * Template for filling up mustache templates. The variable must be named as
 * they are used in the templates. The templates located at
 * src/main/resources/email_templates/
 *
 * @author spgdev@spenego.com - Feb 4, 2018
 */
public class TemplateInfo {
	private String product_name;
	private String subject;
	private String name;
	private String account;
	private String comment;
	private String action_url;
	private String message;
	private String contact;
	private String footer;
	private String button_title;
	private String button_trouble;

	public TemplateInfo() { /* Default construction is sufficient */ }

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAction_url() {
		return action_url;
	}

	public void setAction_url(String action_url) {
		this.action_url = action_url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getButton_title() {
		return button_title;
	}

	public void setButton_title(String button_title) {
		this.button_title = button_title;
	}

	public String getButton_trouble() {
		return button_trouble;
	}

	public void setButton_trouble(String button_trouble) {
		this.button_trouble = button_trouble;
	}
}
