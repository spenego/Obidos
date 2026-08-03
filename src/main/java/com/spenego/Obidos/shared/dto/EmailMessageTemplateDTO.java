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
import java.util.Arrays;

/**
 * Specifies the template for an email message.
 *
 * Substitution is done for the following strings:
 *
 * <FULLNAME> -- replaced with the recipients full name. <USERNAME> -- replaced
 * with the recipients username. <EXPIRE_TIME> -- if the action has an
 * expiration date, this string is replaced with the expiration date.
 * <CURRENT_TIME> -- replaced with the current date/time of the servers
 * time/timezone <TEXT|HYPERLINK> -- injects a hyperlink <SOURCE_FULLNAME> --
 * replaced with the full name of the user that shared an item
 *
 * @author mmorgan
 *
 */
public final class EmailMessageTemplateDTO implements Clearable, Serializable
{
    private static final long serialVersionUID = 1L;

    private byte[] message;

    public EmailMessageTemplateDTO() {}

    public EmailMessageTemplateDTO(final String json) {
    	message = json.getBytes();
    }

    @Override
    public void clear() {
    	Arrays.fill(message, (byte) 0);
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message)
    {
        this.message = message;
    }
}
