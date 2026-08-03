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

package com.spenego.Obidos.server.model;

import java.io.Serializable;

public class Ldap extends NamedModel implements Model, Serializable {
	private static final long serialVersionUID = 1L;
	private String ldapuri;
	private String baseDn;
	private String bindDn;
	private String bindPass;
	private String authAttr;
	private String authorizationMode;
	private String attrVal;
	private String filter;
	private String lGroup;
	private String groupAttr;
	private String keystorePath;
	private String keystorePassword;
	private Boolean startTls;
	private Boolean authorize;

	@Override
	public String toString() {
		return getName();
	}

	public String getLdapuri() {
		return ldapuri;
	}

	public void setLdapuri(String ldapuri) {
		this.ldapuri = ldapuri == null ? null : ldapuri.trim();
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn == null ? null : baseDn.trim();
	}

	public String getBindDn() {
		return bindDn;
	}

	public void setBindDn(String bindDn) {
		this.bindDn = bindDn == null ? null : bindDn.trim();
	}

	public String getBindPass() {
		return bindPass;
	}

	public void setBindPass(String bindPass) {
		this.bindPass = bindPass == null ? null : bindPass.trim();
	}

	public String getAuthAttr() {
		return authAttr;
	}

	public void setAuthAttr(String authAttr) {
		this.authAttr = authAttr == null ? null : authAttr.trim();
	}

	public Boolean getStartTls() {
		return startTls;
	}

	public void setStartTls(Boolean startTls) {
		this.startTls = startTls;
	}

	public Boolean getAuthorize() {
		return authorize;
	}

	public void setAuthorize(Boolean authorize) {
		this.authorize = authorize;
	}

	public String getAuthorizationMode() {
		return authorizationMode;
	}

	public void setAuthorizationMode(String authorizationMode) {
		this.authorizationMode = authorizationMode == null ? null : authorizationMode.trim();
	}

	public String getAttrVal() {
		return attrVal;
	}

	public void setAttrVal(String attrVal) {
		this.attrVal = attrVal == null ? null : attrVal.trim();
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter == null ? null : filter.trim();
	}

	public String getlGroup() {
		return lGroup;
	}

	public void setlGroup(String lGroup) {
		this.lGroup = lGroup == null ? null : lGroup.trim();
	}

	public String getGroupAttr() {
		return groupAttr;
	}

	public void setGroupAttr(String groupAttr) {
		this.groupAttr = groupAttr == null ? null : groupAttr.trim();
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath == null ? null : keystorePath.trim();
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword == null ? null : keystorePassword.trim();
	}

	@Override
	public void setVersion(Integer version) { /* ldap record types do not have this record */ }
}