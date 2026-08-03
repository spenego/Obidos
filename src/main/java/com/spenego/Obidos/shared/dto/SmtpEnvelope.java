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

public final class SmtpEnvelope implements Serializable
{
    /**
     * @author spgdev@spenego.com - May 10, 2017
     */
    private static final long serialVersionUID = 1L;
    private String from;
    private String tos;  // can be comma separated
    private String ccs;  // can be comma separated
    private String bccs; // can be comma separated
    private String subject;
    private String htmlMessage;
    private String textMessage;

    public SmtpEnvelope(final String from, final String tos, final String subject,
    		final String htmlMessage,
    		final String textMessage)
    {
    	this.from = from;
    	this.tos = tos;
    	this.subject = subject;
    	this.htmlMessage = htmlMessage;
    	this.textMessage = textMessage;
    }


    public SmtpEnvelope()
    {
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTos()
    {
        return tos;
    }

    public void setTos(String tos)
    {
        this.tos = tos;
    }

    public String getCcs()
    {
        return ccs;
    }

    public void setCcs(String ccs)
    {
        this.ccs = ccs;
    }

    public String getBccs()
    {
        return bccs;
    }

    public void setBccs(String bccs)
    {
        this.bccs = bccs;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }


	public String getHtmlMessage()
	{
		return htmlMessage;
	}


	public void setHtmlMessage(String htmlMessage)
	{
		this.htmlMessage = htmlMessage;
	}


	public String getTextMessage()
	{
		return textMessage;
	}


	public void setTextMessage(String textMessage)
	{
		this.textMessage = textMessage;
	}
}
