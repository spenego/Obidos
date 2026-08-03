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

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonToolBar;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.spenego.Obidos.client.util.ClientUtils;

public class ObidosButtonLeftToolBar extends ButtonToolBar
{
	private String maxWidth;
	private int maxWidthInt;
	
	public ObidosButtonLeftToolBar()
	{
		super();
		getElement().getStyle().setProperty("justifyContent", "left");
		getElement().getStyle().setProperty("display", "flex");
		getElement().getStyle().setProperty("marginBottom", "0px");
	}
	
	public void adjustButtonsWidth()
	{
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{

			@Override
			public void execute()
			{
				int maxWidth = getMaxWidthInt();
				int maxHeight = getMaxHeightInt();
				maxWidth = maxWidth/2;
				gwtLog("Max button width: " + maxWidth);
				if (maxWidth > 0)
				{
					String width = String.valueOf(maxWidth + "px");
					setMaxWidth(width);
					setMaxWidthInt(maxWidth);
					for (int i = 0; i < getWidgetCount(); i++)
					{
						Button b = (Button) getWidget(i);
						b.setWidth(width);
						b.setPaddingTop(0);
						b.setPaddingBottom(0);
					}
				}
				if (maxHeight > 0)
				{
					String width = String.valueOf(maxHeight + "px");
					setMaxWidth(width);
					setMaxWidthInt(maxWidth);
					for (int i = 0; i < getWidgetCount(); i++)
					{
						Button b = (Button) getWidget(i);
						b.setHeight(width);
					}
				}
			}
	
		});
	}

	
	public String getMaxWidth()
	{
		int maxWidth = getMaxWidthInt();
		if (maxWidth > 0)
		{
			String width = String.valueOf(maxWidth + "px");
			return width;
		}
		return null;
	}

	public String getMaxHeight()
	{
		int maxWidth = getMaxHeightInt();
		if (maxWidth > 0)
		{
			String width = String.valueOf(maxWidth + "px");
			return width;
		}
		return null;
	}

	public void setMaxWidth(String maxWidth)
	{
		this.maxWidth = maxWidth;
	}

	public int getMaxWidthInt()
	{
		int maxWidth = 127;
		for (int i = 0; i < getWidgetCount(); i++)
		{
			Button b = (Button) getWidget(i);
//			b.getElement().getClientWidth();
//			int width = b.getOffsetWidth();
			int width = b.getElement().getClientWidth();
			gwtLog("WWWWWWWWWWWWWWWWWWWWW " + width);
			if (width > maxWidth)
			{
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	public int getMaxHeightInt()
	{
		int maxWidth = 1;
		for (int i = 0; i < getWidgetCount(); i++)
		{
			Button b = (Button) getWidget(i);
			int height = b.getElement().getClientHeight();
			gwtLog("WWWWWWWWWWWWWWWWWWWWW " + height);
			if (height > maxWidth)
			{
				maxWidth = height;
			}
		}
		return maxWidth;
	}

	public void setMaxWidthInt(int maxWidthInt)
	{
		this.maxWidthInt = maxWidthInt;
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(getClass().getSimpleName(), message);
	}
}
