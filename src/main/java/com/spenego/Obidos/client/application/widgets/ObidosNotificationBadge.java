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

import org.gwtbootstrap3.client.ui.Badge;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.BadgePosition;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.spenego.Obidos.client.i18n.ObidosMessages;

/**
 * @author spgdev@spenego.com - Nov 18, 2019
 */
public class ObidosNotificationBadge extends Button
{
	final String DEFAULT_BADGE_BG_COLOR 		 = "#777";
	final String NEW_NOTIFICATION_BADGE_BG_COLOR = "#f00";
	final String DEFAULT_BELL_COLOR 			 = "#337AB7";
	
	private Badge badge;
	public ObidosNotificationBadge()
	{
		super();
		getElement().getStyle().setProperty("textAlign", "center");
		getElement().getStyle().setProperty("padding", "11px 0px");
		makeBadge();
	}
	
	private void makeBadge()
	{
		setTitle(ObidosMessages.LANG.newNotificationMessages());
		setType(ButtonType.LINK);
		setIcon(IconType.BELL);
		setIconPosition(IconPosition.RIGHT);
		setBadgePosition(BadgePosition.LEFT);

		badge = new Badge("0");
		add(badge);
	}
	
	public void setMessageCount(int n)
	{
		badge.setText(Integer.toString(n));
		if (n > 0)
		{
			badge.getElement().getStyle().setProperty("backgroundColor", NEW_NOTIFICATION_BADGE_BG_COLOR);
//			setIconSpin(true);
			setIconColor("green");
		}
		else
		{
//			setIconSpin(false);
			badge.getElement().getStyle().setProperty("backgroundColor", DEFAULT_BADGE_BG_COLOR);
			setIconColor(DEFAULT_BELL_COLOR);
		}
	}
	
}
