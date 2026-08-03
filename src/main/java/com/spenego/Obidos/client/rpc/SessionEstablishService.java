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

package com.spenego.Obidos.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.google.gwt.user.server.rpc.NoXsrfProtect;

@RemoteServiceRelativePath("rpc/sessionEstablishService")
public interface SessionEstablishService extends XsrfProtectedService
{
    @NoXsrfProtect
    public String establishSession();

    // GWT's XSRF servlet is a POS! It totally fails when the browser is refreshed.
    // So I will do it myself.
    @NoXsrfProtect
    public String getXsrfToken();

    @NoXsrfProtect
    public Boolean sessionExists();

    public static class Utility
    {
        private static SessionEstablishServiceAsync instance;
        public static SessionEstablishServiceAsync getInstance()
        {
            if (instance == null)
            {
                instance = (SessionEstablishServiceAsync) GWT.create(SessionEstablishService.class);
            }
            return instance;
        }
    }

}
