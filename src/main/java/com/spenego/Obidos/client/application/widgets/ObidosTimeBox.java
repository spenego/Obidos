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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.spenego.Obidos.shared.ObidosConstants;
import com.tractionsoftware.gwt.user.client.ui.UTCTimeBox;

public class ObidosTimeBox extends UTCTimeBox
{
	private static final String timeFormat = ObidosConstants.TIME_FORMAT;
	public ObidosTimeBox()
	{
		super(DateTimeFormat.getFormat(timeFormat));
	}
	
	public String getTimeFormat()
	{
		return timeFormat;
	}

}
