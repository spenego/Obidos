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
 * It is not really a DTO at this time used for passing search information to GUI
 * @author spgdev@spenego.com - Apr 13, 2017
 */
public final class SearchDTO implements Serializable
{
    /**
     * @author spgdev@spenego.com - Apr 13, 2017
     */
    private static final long serialVersionUID = 1L;
    private String  searchString;
    private Boolean administrator;
    private Boolean showDeletedUsers;
    private Boolean showDeletedAdmins;
    private Boolean showLockedUsers;

    public SearchDTO() { /* default constructor is sufficient */ }

    public String getSearchString()
    {
        return searchString;
    }

    public void setSearchString(String searchString)
    {
        this.searchString = searchString;
    }

    public Boolean getAdministrator()
    {
        return administrator;
    }

    public void setAdministrator(Boolean administrator)
    {
        this.administrator = administrator;
    }

    public Boolean getShowDeletedUsers()
    {
        return showDeletedUsers;
    }

    public void setShowDeletedUsers(Boolean showDeletedUsers)
    {
        this.showDeletedUsers = showDeletedUsers;
    }

    public Boolean getShowDeletedAdmins()
    {
        return showDeletedAdmins;
    }

    public void setShowDeletedAdmins(Boolean showDeletedAdmins)
    {
        this.showDeletedAdmins = showDeletedAdmins;
    }

    public Boolean getShowLockedUsers()
    {
        return showLockedUsers;
    }

    public void setShowLockedUsers(Boolean showLockedUsers)
    {
        this.showLockedUsers = showLockedUsers;
    }

}
