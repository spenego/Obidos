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

import org.gwtbootstrap3.client.ui.TextArea;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;

public class ObidosTextArea extends TextArea implements HasHandlers
{
	@UiConstructor
	public ObidosTextArea(String maxLength)
	{
		super();
		int len = ClientUtils.stringToInt(maxLength);
		if (len > 0)
		{
			setMaxLength(ObidosConstants.TEXTAREA_MAX_LENGTH);
		}
		getElement().setAttribute("spellcheck", "false");
		sinkEvents(Event.ONPASTE);
	}
	
	public ObidosTextArea(Element element)
	{
		super(element);
	}

	@Override
	public void onBrowserEvent(Event event)
	{
		super.onBrowserEvent(event);
	    switch(event.getTypeInt())
        {
            case Event.ONPASTE:
            {
                Scheduler.get().scheduleDeferred(new ScheduledCommand()
                {
                    @Override
                    public void execute()
                    {
                        ValueChangeEvent.fire(ObidosTextArea.this, getText());
                    }
                });

                break;
            }

            default:
            {
            }
        }
	}
}
