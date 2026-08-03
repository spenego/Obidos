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

package com.spenego.Obidos.client.application.installlicense;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class InstallLicenseModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(InstallLicensePresenter.class, InstallLicensePresenter.MyView.class, InstallLicenseView.class, InstallLicensePresenter.MyProxy.class);
    }
}