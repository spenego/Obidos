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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;

/**
 * Maps JSON notification template. None of the string member should be null,
 * instead they should have "" if there is no value.
 * @author spgdev@spenego.com, Copenhagen, Denmark
 * Jun 20, 2018 11:49:13 AM - first cut
 */
public final class NotificationTemplateJSONDTO implements Serializable
{
    private static final long serialVersionUID=1L;

    // the variable names reflect the keys in JSON
    private Long id;
    private String action_url;
    private String button_title;
    private String button_trouble;
    private String comment;
    private String contact;
    private String footer;
    private String hello;
    private String name;
    private String product_name;
    private String product_url;
    private String subject;
    private String title;
    private String html_message;
    private String warning_message;
    private String text_message;
    private String owner_name;
    private String container_name;
    private String item_name;


    public NotificationTemplateJSONDTO() {}

    public NotificationTemplateJSONDTO(final String product_name, final String title, final String subject, final String hello, final String action_url, final String html_message,
 final String warning_message,
    									final String button_title, final String button_trouble, final String text_message, final String contact, final String footer)
    {
    	this.product_name = product_name;
    	this.title = title;
    	this.subject = subject;
    	this.hello = hello;
    	this.action_url = action_url;
    	this.html_message = html_message;
    	this.warning_message = warning_message;
    	this.button_title = button_title;
    	this.button_trouble = button_trouble;
    	this.text_message = text_message;
    	this.contact = contact;
    	this.footer = footer;
    }

    public NotificationTemplateJSONDTO(final Long id, final String product_name, final String title, final String subject, final String hello, final String action_url, final String html_message,
 final String warning_message,
    									final String button_title, final String button_trouble, final String text_message, final String contact, final String footer)
    {
    	this.id = id;
    	this.product_name = product_name;
    	this.title = title;
    	this.subject = subject;
    	this.hello = hello;
    	this.action_url = action_url;
    	this.html_message = html_message;
    	this.warning_message = warning_message;
    	this.button_title = button_title;
    	this.button_trouble = button_trouble;
    	this.text_message = text_message;
    	this.contact = contact;
    	this.footer = footer;
    }

    public String getAction_url()
    {
        return action_url;
    }
    public void setAction_url(String action_url)
    {
        this.action_url=action_url;
    }
    public String getButton_title()
    {
        return button_title;
    }
    public void setButton_title(String button_title)
    {
        this.button_title=button_title;
    }
    public String getButton_trouble()
    {
        return button_trouble;
    }
    public void setButton_trouble(String button_trouble)
    {
        this.button_trouble=button_trouble;
    }
    public String getComment()
    {
        return comment;
    }
    public void setComment(String comment)
    {
        this.comment=comment;
    }
    public String getContact()
    {
        return contact;
    }
    public void setContact(String contact)
    {
        this.contact=contact;
    }
    public String getFooter()
    {
        return footer;
    }
    public void setFooter(String footer)
    {
        this.footer=footer;
    }
    public String getHello()
    {
        return hello;
    }
    public void setHello(String hello)
    {
        this.hello=hello;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public String getProduct_name()
    {
        return product_name;
    }
    public void setProduct_name(String product_name)
    {
        this.product_name=product_name;
    }
    public String getSubject()
    {
        return subject;
    }
    public void setSubject(String subject)
    {
        this.subject=subject;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title=title;
    }

    public String getHtml_message()
    {
        return html_message;
    }
    public void setHtml_message(String html_message)
    {
        this.html_message=html_message;
    }

    public String getWarning_message()
    {
        return warning_message;
    }
    public void setWarning_message(String html_message)
    {
        this.warning_message = html_message;
    }

    public String getText_message()
    {
        return text_message;
    }
    public void setText_message(String text_message)
    {
        this.text_message=text_message;
    }

    public String getProduct_url()
    {
        return product_url;
    }

    public void setProduct_url(String product_url)
    {
        this.product_url = product_url;
    }

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getOwner_name()
	{
		return owner_name;
	}

	public void setOwner_name(String owner_name)
	{
		this.owner_name = owner_name;
	}

	public String getContainer_name()
	{
		return container_name;
	}

	public void setContainer_name(String container_name)
	{
		this.container_name = container_name;
	}

	public String getItem_name()
	{
		return item_name;
	}

	public void setItem_name(String item_name)
	{
		this.item_name = item_name;
	}
}

