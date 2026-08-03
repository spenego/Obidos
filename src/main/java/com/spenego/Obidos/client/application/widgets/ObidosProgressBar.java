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

package com.spenego.Obidos.client.application.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;


// I probably will not use it, we already a nice one from bootstrap
// But is it interesting that HTML5's progress element can be used
// in GWT -- spgdev@spenego.com - Mar 28, 2019
// Ref: http://jamies-gwt.blogspot.com/2015/01/gwt-progress-bar.html
public class ObidosProgressBar extends HTML
{
    public ObidosProgressBar()
    {
        super("<progress style='width: 98%;'></progress>");
    }
 
    /**
     * Set the progress indicator the the specified values
     * @param value Current progress value
     * @param max Target/complete value
     */
    public void setProgress(int value, int max)
    {
        Element progressElement = getElement().getFirstChildElement();
        progressElement.setAttribute("value", String.valueOf(value));
        progressElement.setAttribute("max", String.valueOf(max));
    }
 
    /**
     * Remove the progress indicator values.  On firefox, this causes the
     * progress bar to sweep back and forth.
     */
    public void clearProgress()
    {
        Element progressElement = getElement().getFirstChildElement();
        progressElement.removeAttribute("value");
        progressElement.removeAttribute("max");
    }
}
