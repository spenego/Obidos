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

import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ObidosButtonCellNew extends ButtonCell
{
	private IconType icon;

    private ButtonType type = ButtonType.DEFAULT;

    private ButtonSize size = ButtonSize.DEFAULT;
    
    private String iconColor;

    private boolean enabled = true;
    
    public ObidosButtonCellNew(ButtonType type, IconType icon)
    {
        this.type = type;
        this.icon = icon;
    }

    public ObidosButtonCellNew(ButtonType type, IconType icon, String iconColor)
    {
        this.type = type;
        this.icon = icon;
        this.iconColor = iconColor;
    }


    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb)
    {
        String cssClasses = new StringBuilder("btn") //
                .append(" ") //
                .append(type.getCssName()) //
                .append(" ") //
                .append(size.getCssName()) //
                .toString();

        String disabled = "";
        if (!enabled)
        {
            disabled = " disabled=\"disabled\"";
        }

        sb.appendHtmlConstant("<button type=\"button\" style=\"padding:0px\"  class=\"" + cssClasses + "\" tabindex=\"-1\"" + disabled + ">");
        if (icon != null)
        {
            StringBuilder iconHtml = new StringBuilder(256);
			iconHtml.append("<i class=\"");
			iconHtml.append(Styles.FONT_AWESOME_BASE) ;
			iconHtml.append(" ") ;
			iconHtml.append(icon.getCssName());
			iconHtml.append("\"");
			iconHtml.append(" ") ;
            if (iconColor != null)
            {
				iconHtml.append("area-hidden=\"true\" style=\"color:\"");
				iconHtml.append(iconColor);
				iconHtml.append("\"");
            }
			iconHtml.append("></i> ");
            
            sb.appendHtmlConstant(iconHtml.toString());
        }
        if (data != null)
        {
            sb.append(data);
        }
        sb.appendHtmlConstant("</button>");
    }

}
