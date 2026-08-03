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
 * Various Presenters use this DTO to send message to ApplicationPresenter
 * @author spgdev@spenego.com - Jun 4, 2017
 */
public final class MessageDTO implements Serializable
{

    /**
     * @author spgdev@spenego.com - Jun 4, 2017
     */
    private static final long serialVersionUID = 1L;
    public MessageDTO() {}
    public MessageDTO(final int messageType, final String message) {
    	this.messageType = messageType;
    	this.message = message;
    }

    public static final int INFO    = 1;
    public static final int WARNING = 2;
    public static final int ERROR   = 3;

    private int messageType;
    private String message;
    public int getMessageType()
    {
        return messageType;
    }
    public void setMessageType(int messageType)
    {
        this.messageType = messageType;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }
    public static int getInfo()
    {
        return INFO;
    }
    public static int getWarning()
    {
        return WARNING;
    }
    public static int getError()
    {
        return ERROR;
    }
}
