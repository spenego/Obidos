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

package com.spenego.Obidos.client.util;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.shared.dto.ContainerDTO;

/**
 * 
 * @author spgdev@spenego.com - Dec 31, 2018
 */
public class ContainerSharedWithOthersCell extends AbstractCell<String>
{
    public ContainerSharedWithOthersCell()
	{
		super("click");
	}


	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder safeHtmlBuilder)
	{
		if (value == null)
		{
			return;
		}

		ContainerDTO containerDTO = (ContainerDTO) context.getKey();
		if (containerDTO == null)
		{
			return;
		}

		SafeHtml safeHtml = null;
		// Note: getShare() is a Boolean object, so never test
		// if (containerDTO.getShare()), it will throw
		// null pointer exception if the object is null
		//Boolean shared = containerDTO.getShare();
		boolean shared = ClientUtils.fromBoolean(containerDTO.getShared());
		String title = "<span title=\"" + value + "\"</span>";
		if (shared)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("<span style='cursor:pointer' data-placement='right' data-toggle='tooltip'");
			sb.append(" ");
			sb.append("title=");
			sb.append("'");
			sb.append(ObidosMessages.LANG.containerSharedWithOthers());
			sb.append("'");
			sb.append("</span>");

			sb.append("<div>");
			sb.append("	<span>");
//			sb.append("		​<i class='fa fa-share-alt-square' style='color:green'></i>");
			// Issue #324
			sb.append("		​<i class='fa fa-share-square' style='color:green'></i>");
			sb.append("&nbsp;");
			sb.append(value);
			sb.append("	</span>");
			sb.append("</div>");
			String html = sb.toString();
			safeHtml = SafeHtmlUtils.fromTrustedString(html);
			safeHtmlBuilder.append(safeHtml);
		}
		else
		{
		   safeHtml = SafeHtmlUtils.fromTrustedString(title + value);
		   safeHtmlBuilder.append(safeHtml);
		}
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater)
	{

		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if ("click".equals(event.getType()))
		{
			EventTarget eventTarget = event.getEventTarget();
			if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget)))
			{
				ContainerDTO dto = (ContainerDTO) context.getKey();
				boolean sharedWithOthers = ClientUtils.fromBoolean(dto.getShared());
				if (sharedWithOthers)
				{
					gwtLog("Set value updater");
					valueUpdater.update(value);
				}
				else
				{
					gwtLog("Don't set value updater");
				}
			}
		}
	}
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
}
