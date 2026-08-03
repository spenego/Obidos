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

import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.constants.InputType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Event;

/**
 * Extend Input to catch text paste event. When a text is pasted, a ValueChangeEvent with the
 * text will be fired. The caller will implement ValueChangeEvent handler to receive the text.
 * Also there will be no need to specify input type in ui xml file.
 * @author spgdev@spenego.com - May 24, 2017
 */
public class ObidosPasswordBox extends Input implements HasHandlers
{

    public ObidosPasswordBox()
    {
        super();
        setType(InputType.PASSWORD);
        sinkEvents(Event.ONPASTE);
    }

    public ObidosPasswordBox(InputType type)
    {
        super(type);
    }

    public ObidosPasswordBox(Renderer<String> renderer, Parser<String> parser)
    {
        super(renderer, parser);
    }
    
    public void clear()
    {
    	setValue("");
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
                        ValueChangeEvent.fire(ObidosPasswordBox.this, getText());
                    }
                });
                break;
            }
        }
    }
}
