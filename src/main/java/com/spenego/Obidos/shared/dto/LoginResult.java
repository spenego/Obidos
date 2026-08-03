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

public final class LoginResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String xsrfToken;
    private UserDTO userDTO;
    private Integer sessionTimeoutSeconds;
	private Integer	maxPasswordAgeInDays;
	private String dateFormat;
	private LicenseStats licenseStats;

    public LoginResult() {}

    public LoginResult(final String xsrfToken, final Integer sessionTimeoutSeconds, final Integer maxPasswordAgeInDays, final UserDTO userDTO, final String dateFormat) {
        this.xsrfToken = xsrfToken;
        this.userDTO = userDTO;
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
        this.maxPasswordAgeInDays = maxPasswordAgeInDays;
        this.dateFormat = dateFormat;
    }

    public String getXsrfToken() {
        return xsrfToken;
    }

    public void setXsrfToken(String xsrfToken) {
        this.xsrfToken = xsrfToken;
    }

    public Integer getSessionTimeoutSeconds() {
        return sessionTimeoutSeconds;
    }

    public void setSessionTimeoutSeconds(Integer sessionTimeoutSeconds) {
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

	public Integer getMaxPasswordAgeInDays() {
		return maxPasswordAgeInDays;
	}

	public void setMaxPasswordAgeInDays(Integer maxPasswordAgeInDays) {
		this.maxPasswordAgeInDays = maxPasswordAgeInDays;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public LicenseStats getLicenseStats() {
		return licenseStats;
	}

	public void setLicenseStats(LicenseStats licenseStats) {
		this.licenseStats = licenseStats;
	}
}
