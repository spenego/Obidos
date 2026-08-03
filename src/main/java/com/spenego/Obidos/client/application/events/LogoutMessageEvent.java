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
 * This event carries logout status message for LoginPresenter.
 * ApplicationPresenter fires this event after succeesful logout.
 * LoginPresenter is listening for this event, after receiving this
 * message, LoginPresenter shows the status message.
 * spgdev@spenego.com Dec-25-2017
 */
public class LogoutMessageEvent extends GwtEvent<LogoutMessageEvent.LogoutMessageEventHandler>
{
	private static Type<LogoutMessageEventHandler> TYPE = new Type<LogoutMessageEventHandler>();

	public interface LogoutMessageEventHandler extends EventHandler
	{
		void onReceiveLogoutMessage(LogoutMessageEvent event);
	}

	private final MessageDTO messageDTO;
	public LogoutMessageEvent(final MessageDTO messageDTO)
	{
		this.messageDTO = messageDTO;
	}

	public static void fire(HasHandlers source, MessageDTO messageDTO)
	{
		source.fireEvent(new LogoutMessageEvent(messageDTO));
	}

	public static Type<LogoutMessageEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<LogoutMessageEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(LogoutMessageEventHandler handler)
	{
		handler.onReceiveLogoutMessage(this);
	}
	public MessageDTO getMessageDTO()
	{
		return messageDTO;
	}
}
