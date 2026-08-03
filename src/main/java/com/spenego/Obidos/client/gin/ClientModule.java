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

package com.spenego.Obidos.client.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.spenego.Obidos.client.application.ApplicationModule;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.resources.ResourceLoader;
import com.spenego.Obidos.client.security.CurrentUser;

public class ClientModule extends AbstractPresenterModule {
    @Override
    protected void configure()
    {
        install(new DefaultModule
                .Builder()
                .defaultPlace(NameTokens.ABOUT)  // Issue #675
                .errorPlace(NameTokens.OBIDOS_ERROR_PAGE)
                .unauthorizedPlace(NameTokens.LOGIN)
                .build());

        bind(CurrentUser.class).asEagerSingleton();

        install(new ApplicationModule());

        bind(ResourceLoader.class).asEagerSingleton();
    }
}
