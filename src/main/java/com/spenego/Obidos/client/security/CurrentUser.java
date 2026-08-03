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

package com.spenego.Obidos.client.security;

import java.util.Date;

import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.UserDTO;

/**
 * This class can be injected in any presenter to find out information about a
 * logged in user.
 * @author spgdev@spenego.com - May 4, 2017
 */
public class CurrentUser
{
    private boolean loggedIn;
    private boolean keypairExists;
    private UserDTO userDTO;
    private int 	sessionTimeoutIntervalSeconds;
    private int 	sessionWillTimeoutInSeconds;
    private Date    sessionTimeoutAt;
    private String  xsrfToken;
    private Boolean passphraseRegistered;
    private Boolean keyPairCreated;
    private Integer notificationCount;
    private LoginResult loginResult;
    private boolean passwordJustChanged;

    public CurrentUser()
    {
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

    public UserDTO getUserDTO()
    {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO)
    {
        this.userDTO = userDTO;
    }

    public String getXsrfToken()
    {
        return xsrfToken;
    }

    public void setXsrfToken(String xsrfToken)
    {
        this.xsrfToken = xsrfToken;
    }

    public Boolean getPassphraseRegistered()
    {
        return passphraseRegistered;
    }

    public void setPassphraseRegistered(Boolean passphraseRegistered)
    {
        this.passphraseRegistered = passphraseRegistered;
    }

    public boolean isKeypairExists()
    {
        return keypairExists;
    }

    public void setKeypairExists(boolean keypairExists)
    {
        this.keypairExists = keypairExists;
    }

	public Integer getNotificationCount()
	{
		return notificationCount;
	}

	public void setNotificationCount(Integer notificationCount)
	{
		this.notificationCount = notificationCount;
	}


	public Date getSessionTimeoutAt()
	{
		return sessionTimeoutAt;
	}

	public void setSessionTimeoutAt(Date sessionTimeoutAt)
	{
		this.sessionTimeoutAt = sessionTimeoutAt;
	}

	public int getSessionTimeoutIntervalSeconds()
	{
		return sessionTimeoutIntervalSeconds;
	}

	public void setSessionTimeoutIntervalSeconds(int sessionTimeoutIntervalSeconds)
	{
		this.sessionTimeoutIntervalSeconds = sessionTimeoutIntervalSeconds;
	}

	public int getSessionWillTimeoutInSeconds()
	{
		return sessionWillTimeoutInSeconds;
	}

	public void setSessionWillTimeoutInSeconds(int sessionWillTimeoutInSeconds)
	{
		this.sessionWillTimeoutInSeconds = sessionWillTimeoutInSeconds;
	}

	public LoginResult getLoginResult()
	{
		return loginResult;
	}

	public void setLoginResult(LoginResult loginResult)
	{
		this.loginResult = loginResult;
	}

	public Boolean getKeyPairCreated()
	{
		return keyPairCreated;
	}

	public void setKeyPairCreated(Boolean keyPairCreated)
	{
		this.keyPairCreated = keyPairCreated;
	}

	public boolean isPasswordJustChanged()
	{
		return passwordJustChanged;
	}

	public void setPasswordJustChanged(boolean passwordJustChanged)
	{
		this.passwordJustChanged = passwordJustChanged;
	}
}
