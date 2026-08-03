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

import java.util.Date;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

/**
 * Manages application theming and accessibility options including high contrast mode
 * and large font mode.
 */
public class ThemeManager {
    
    // Cookie names for persistence
    private static final String HIGH_CONTRAST_COOKIE = "high-contrast";
    private static final String LARGE_FONT_COOKIE = "large-font";
    
    // Cookie expiration (1 year)
    private static final int COOKIE_EXPIRES_DAYS = 365;
    
    /**
     * Toggles high contrast mode on/off
     */
    public static void toggleHighContrast() {
        boolean isEnabled = isHighContrastEnabled();
        if (isEnabled) {
            disableHighContrast();
        } else {
            enableHighContrast();
        }
    }
    
    /**
     * Checks if high contrast mode is currently enabled
     * 
     * @return true if high contrast mode is enabled
     */
    public static boolean isHighContrastEnabled() {
        String value = Cookies.getCookie(HIGH_CONTRAST_COOKIE);
        return "enabled".equals(value);
    }
    
    /**
     * Enables high contrast mode
     */
    public static void enableHighContrast() {
        // Ensure the CSS resource is injected
        AppResources.INSTANCE.highContrast().ensureInjected();
        
        // Add high contrast CSS class to body
        Document.get().getBody().addClassName(
                AppResources.INSTANCE.highContrast().highContrast());
        
        // Save setting in cookie
        Date expires = new Date();
        expires.setTime(expires.getTime() + COOKIE_EXPIRES_DAYS * 24 * 60 * 60 * 1000);
        Cookies.setCookie(HIGH_CONTRAST_COOKIE, "enabled", expires);
    }
    
    /**
     * Disables high contrast mode
     */
    public static void disableHighContrast() {
        // Remove high contrast CSS class from body
        Document.get().getBody().removeClassName(
                AppResources.INSTANCE.highContrast().highContrast());
        
        // Save setting in cookie
        Date expires = new Date();
        expires.setTime(expires.getTime() + COOKIE_EXPIRES_DAYS * 24 * 60 * 60 * 1000);
        Cookies.setCookie(HIGH_CONTRAST_COOKIE, "disabled", expires);
    }
    
    /**
     * Toggles large font mode on/off
     */
    public static void toggleLargeFont() {
        boolean isEnabled = isLargeFontEnabled();
        if (isEnabled) {
            disableLargeFont();
        } else {
            enableLargeFont();
        }
    }
    
    /**
     * Checks if large font mode is currently enabled
     * 
     * @return true if large font mode is enabled
     */
    public static boolean isLargeFontEnabled() {
        String value = Cookies.getCookie(LARGE_FONT_COOKIE);
        return "enabled".equals(value);
    }
    
    /**
     * Enables large font mode
     */
    public static void enableLargeFont() {
        // Ensure the CSS resource is injected
        AppResources.INSTANCE.largeFont().ensureInjected();
        
        // Add large font CSS class to body
        Document.get().getBody().addClassName(
                AppResources.INSTANCE.largeFont().largeText());
        
        // Save setting in cookie
        Date expires = new Date();
        expires.setTime(expires.getTime() + COOKIE_EXPIRES_DAYS * 24 * 60 * 60 * 1000);
        Cookies.setCookie(LARGE_FONT_COOKIE, "enabled", expires);
    }
    
    /**
     * Disables large font mode
     */
    public static void disableLargeFont() {
        // Remove large font CSS class from body
        Document.get().getBody().removeClassName(
                AppResources.INSTANCE.largeFont().largeText());
        
        // Save setting in cookie
        Date expires = new Date();
        expires.setTime(expires.getTime() + COOKIE_EXPIRES_DAYS * 24 * 60 * 60 * 1000);
        Cookies.setCookie(LARGE_FONT_COOKIE, "disabled", expires);
    }
    
    /**
     * Load saved theme settings from cookies
     * Call this during application initialization
     */
    public static void loadSavedSettings() {
        // Load high contrast setting
        if (isHighContrastEnabled()) {
            enableHighContrast();
        }
        
        // Load large font setting
        if (isLargeFontEnabled()) {
            enableLargeFont();
        }
    }
}