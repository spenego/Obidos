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
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class LdapDTO extends LimitedLdapDTO implements Clearable, Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @NotBlank(message = "Please specify an LDAP Base DN")
    private String baseDn;
    private String bindDn;
    private String bindPass;

    @NotNull
    @NotBlank(message = "Please specify an LDAP Attribute for Authentication")
    private String authAttr;

    private Boolean startTls;
    private Boolean authorize;
    private String authorizationMode;
    private String attrVal;
    private String filter;
    private String lGroup;
    private String groupAttr;
    private Date createdAt;
    private Date updatedAt;
    private String keystorePath;
    private String keystorePassword;
    private String certRequirement;
    private Integer CONNECT_TIMEOUT = 5000; // 5 seconds
    private Integer READ_TIMEOUT = 5000; // 5 seconds

    @Override
    public void clear() {
        baseDn =
        bindDn =
        bindPass =
        authAttr =
        authorizationMode =
        attrVal =
        filter =
        lGroup =
        groupAttr =
        keystorePath =
        certRequirement = 
        keystorePassword = null;
        startTls = authorize = null;
        createdAt = updatedAt = null;
		super.clear();
    }

    @Override
    public String toString() {
    	return getName();
    }

    public String getBaseDn()
    {
        return baseDn;
    }

    public void setBaseDn(String baseDn)
    {
        this.baseDn = baseDn;
    }

    public String getBindDn()
    {
        return bindDn;
    }

    public void setBindDn(String bindDn)
    {
        this.bindDn = bindDn;
    }

    public String getBindPass()
    {
        return bindPass;
    }

    public void setBindPass(String bindPass)
    {
        this.bindPass = bindPass;
    }

    public String getAuthAttr()
    {
        return authAttr;
    }

    public void setAuthAttr(String authAttr)
    {
        this.authAttr = authAttr;
    }

    public Boolean getStartTls()
    {
        return startTls;
    }

    public void setStartTls(Boolean startTls)
    {
        this.startTls = startTls;
    }

    public Boolean getAuthorize()
    {
        return authorize;
    }

    public void setAuthorize(Boolean authorize)
    {
        this.authorize = authorize;
    }

    public String getAuthorizationMode()
    {
        return authorizationMode;
    }

    public void setAuthorizationMode(String authorizationMode)
    {
        this.authorizationMode = authorizationMode;
    }

    public String getAttrVal()
    {
        return attrVal;
    }

    public void setAttrVal(String attrVal)
    {
        this.attrVal = attrVal;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public String getlGroup()
    {
        return lGroup;
    }

    public void setlGroup(String lGroup)
    {
        this.lGroup = lGroup;
    }

    public String getGroupAttr()
    {
        return groupAttr;
    }

    public void setGroupAttr(String groupAttr)
    {
        this.groupAttr = groupAttr;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public String getKeystorePath()
    {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath)
    {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword()
    {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword)
    {
        this.keystorePassword = keystorePassword;
    }

	public String getCertRequirement()
	{
		return certRequirement;
	}

	public void setCertRequirement(String certRequirement)
	{
		this.certRequirement = certRequirement;
	}

	public Integer getCONNECT_TIMEOUT()
	{
		return CONNECT_TIMEOUT;
	}

	public Integer getREAD_TIMEOUT()
	{
		return READ_TIMEOUT;
	}


}
