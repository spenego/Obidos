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

package com.spenego.Obidos.client.resources;
import javax.inject.Inject;
import com.google.gwt.core.client.ScriptInjector;
import com.spenego.Obidos.client.util.ClientUtils;
public class ResourceLoader {
    @Inject
    ResourceLoader(AppResources appResources) {
        // Inject base stylesheets
        appResources.normalize().ensureInjected();
        appResources.style().ensureInjected();
        
        // Ensure high contrast CSS is available (but not activated yet)
        appResources.highContrast().ensureInjected();
        
        // Ensure large font CSS is available (but not activated yet)
        appResources.largeFont().ensureInjected();
        
        // Inject theme overrides AFTER base styles to ensure they take precedence
        // This is important for overriding Bootstrap defaults like hover colors
        appResources.themeOverrides().ensureInjected();
        
        // Load saved theme settings
        ThemeManager.loadSavedSettings();
        
        if (ClientUtils.isJQueryLoaded())
        {
            ScriptInjector.fromString(AppResources.INSTANCE.JQueryBangla().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
        }
    }
}