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

import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Event;
import com.spenego.Obidos.shared.ObidosConstants;


/**
 * Extend TextBox to catch text paste event. When a text is pasted, a ValueChangeEvent with the
 * text will be fired. The caller will implement ValueChangeEvent handler to receive the text.
 * @author spgdev@spenego.com - May 24, 2017
 */
public class ObidosTextBox extends TextBox implements HasHandlers
{

    public ObidosTextBox()
    {
        super();
        setMaxLength(ObidosConstants.TEXTFIELD_MAX_LENGTH); // Issue# 585
        sinkEvents(Event.ONPASTE);
    }

    public ObidosTextBox(Element element)
    {
        super(element);
    }

    public ObidosTextBox(Element element, Renderer<String> renderer, Parser<String> parser)
    {
        super(element, renderer, parser);
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
                        ValueChangeEvent.fire(ObidosTextBox.this, getText());
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
