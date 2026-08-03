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
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
public interface AppResources extends ClientBundle {
    AppResources INSTANCE = GWT.create(AppResources.class);
    
    interface Normalize extends CssResource {
    }
    
    interface Style extends CssResource {
    }
    
    /**
     * Interface for high contrast mode styling
     */
    interface HighContrastResource extends CssResource {
        String highContrast();
    }
    
    /**
     * Interface for large font mode styling
     */
    interface LargeFontResource extends CssResource {
        String largeText();
    }
    
    /**
     * Interface for theme override styles
     */
    interface ThemeOverridesResource extends CssResource {
    }
    
    @Source("css/normalize.gss")
    Normalize normalize();
    
    @Source("css/style.gss")
    Style style();
    
    /**
     * High contrast stylesheet resource
     */
    @Source("css/high-contrast.gss")
    HighContrastResource highContrast();
    
    /**
     * Large font stylesheet resource
     */
    @Source("css/large-font.gss")
    LargeFontResource largeFont();
    
    /**
     * Theme overrides stylesheet resource
     * These styles override Bootstrap defaults regardless of theme
     */
    @Source("css/theme-overrides.gss")
    ThemeOverridesResource themeOverrides();
    
    @Source("js/jquery.bangla.js")
    TextResource JQueryBangla();
}