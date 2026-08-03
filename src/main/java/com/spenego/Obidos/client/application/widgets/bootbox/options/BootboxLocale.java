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

package com.spenego.Obidos.client.application.widgets.bootbox.options;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2016 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * XBootbox locale.
 *
 * @author Xiaodong Sun
 */
public enum BootboxLocale {

    BG_BG("bg_BG"),
    BR("br"),
    CS("cs"),
    DA("da"),
    DE("de"),
    EL("el"),
    EN("en"),
    ES("es"),
    ET("et"),
    FA("fa"),
    FI("fi"),
    FR("fr"),
    HE("he"),
    HU("hu"),
    HR("hr"),
    ID("id"),
    IT("it"),
    JA("ja"),
    LT("lt"),
    LV("lv"),
    NL("nl"),
    NO("no"),
    PL("pl"),
    PT("pt"),
    RU("ru"),
    SQ("sq"),
    SV("sv"),
    TH("th"),
    TR("tr"),
    ZH_CN("zh_CN"),
    ZH_TW("zh_TW"),
    ;

    private final String locale;

    private BootboxLocale(final String locale) {
        this.locale = locale;
    }

    /**
     * Returns the locale.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Returns the default locale: {@link #EN}.
     *
     * @return the default locale.
     */
    public static BootboxLocale getDefault() {
        return EN;
    }

}
