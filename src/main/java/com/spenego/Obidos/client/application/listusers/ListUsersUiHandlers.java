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

package com.spenego.Obidos.client.application.listusers;

import com.gwtplatform.mvp.client.UiHandlers;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

interface ListUsersUiHandlers extends UiHandlers
{
    void redirectToLoginDialogPage();
    void listUsers();
    void showEditUserScreen(LimitedUserDTO userDTO);
    void tombstoneUsers();
    void lockUsers();
    void clearSelections();
    void help();
    void search();
    void selectCheckBoxCallback();
    void sortByDate();
    void sortByAZ();
    void sortByZA();
}