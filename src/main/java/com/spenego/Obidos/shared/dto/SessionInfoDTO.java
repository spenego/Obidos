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
 * @author spgdev@spenego.com - Nov 11, 2017
 */
public final class SessionInfoDTO implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String sessionId;

    /* A hack for AJAX applications to keep track of RPC access. In AJAX Apps, any call to session updates
     * the session's last access time, which makes timeout difficult. Because a call to find out if session
     * is valid also updates the last access time. So we keep track of real RPC calls by saving the epoch
     * in a attribute
     */
    private Long ourLastAccessEpoch; // we set that manually

    private Integer maxInactiveInterval; // seconds
    private Long creationTimeEpoch;
    private Long expireEpoch;

    public SessionInfoDTO() { /* default constructor is sufficient */ }

    public SessionInfoDTO(final String sessionId) {
    	this.sessionId = sessionId;
    	maxInactiveInterval = 0;
    	ourLastAccessEpoch = creationTimeEpoch = 0L;
    	expireEpoch = null;
    }

    public SessionInfoDTO(final String sessionId, final Long lastEpoch, final Integer maxInactiveInterval, final Long creationTimeEpoch, final Long expireEpoch) {
    	this.sessionId = sessionId;
    	this.ourLastAccessEpoch = lastEpoch;
    	this.maxInactiveInterval = maxInactiveInterval;
    	this.creationTimeEpoch = creationTimeEpoch;
    	this.expireEpoch = expireEpoch;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public Integer getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(Integer maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public Long getCreationTimeEpoch()
    {
        return creationTimeEpoch;
    }

    public void setCreationTimeEpoch(Long creationTimeEpoch)
    {
        this.creationTimeEpoch = creationTimeEpoch;
    }

    public Long getExpireEpoch()
    {
        return expireEpoch;
    }

    public void setExpireEpoch(Long expireEpoch)
    {
        this.expireEpoch = expireEpoch;
    }

    public Long getOurLastAccessEpoch()
    {
        return ourLastAccessEpoch;
    }

    public void setOurLastAccessEpoch(Long ourLastAccessEpoch)
    {
        this.ourLastAccessEpoch = ourLastAccessEpoch;
    }
}

