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

package com.spenego.Obidos.client.application.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.spenego.Obidos.shared.dto.MessageDTO;

/**
 * Event to fire when it is needed to send a message to ApplicationPresenter
 * from other views.
 * Application Presenter then can show the message in a Label
 * spgdev@spenego.com Jun-04-2017
 */
public class SendSessionExtendMessageEvent extends GwtEvent<SendSessionExtendMessageEvent.SendSessionExtendMessageEventHandler>
{
	private static Type<SendSessionExtendMessageEventHandler> TYPE = new Type<SendSessionExtendMessageEventHandler>();

	public interface SendSessionExtendMessageEventHandler extends EventHandler
	{
		void onSendSessionExtendMessageEvent(SendSessionExtendMessageEvent event);
	}

	private final MessageDTO messageDTO;
	public SendSessionExtendMessageEvent(final MessageDTO messageDTO)
	{
		this.messageDTO = messageDTO;
	}

	public static void fire(HasHandlers source, MessageDTO messageDTO)
	{
		source.fireEvent(new SendSessionExtendMessageEvent(messageDTO));
	}

	public static Type<SendSessionExtendMessageEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<SendSessionExtendMessageEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SendSessionExtendMessageEventHandler handler)
	{
		handler.onSendSessionExtendMessageEvent(this);
	}

	public MessageDTO getMessageDTO()
	{
		return messageDTO;
	}
}
