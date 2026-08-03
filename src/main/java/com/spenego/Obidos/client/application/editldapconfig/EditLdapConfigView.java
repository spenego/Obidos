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

package com.spenego.Obidos.client.application.editldapconfig;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

class EditLdapConfigView extends ViewWithUiHandlers<EditLdapConfigUiHandlers> implements EditLdapConfigPresenter.MyView
{
    interface Binder extends UiBinder<Widget, EditLdapConfigView>
    {
    }

    @UiField
    SimplePanel main;

    @Inject
    EditLdapConfigView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /*
    @Override
    public void setInSlot(Object slot, IsWidget content)
    {
        if (slot == EditLdapConfigPresenter.SLOT_EditLdapConfig)
        {
            main.setWidget(content);
        } else
        {
            super.setInSlot(slot, content);
        }
    }
    */
}