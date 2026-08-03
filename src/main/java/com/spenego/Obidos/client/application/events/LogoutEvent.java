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
 * Event to fire after logout. LoginPresenter will receive the event in case session error etc and write an
 * error message in the form
 * @author spgdev@spenego.com - Mar-22-2017
 */
public class LogoutEvent extends GwtEvent<LogoutEvent.LogoutEventHandler>
{
	private static Type<LogoutEventHandler> TYPE = new Type<LogoutEventHandler>();

	public interface LogoutEventHandler extends EventHandler
	{
		void onReceiveLogoutMessage(LogoutEvent event);
	}

	private final MessageDTO messageDTO;
	public LogoutEvent(final MessageDTO messageDTO)
	{
		this.messageDTO = messageDTO;
	}

	public static void fire(HasHandlers source, MessageDTO messageDTO)
	{
		source.fireEvent(new LogoutEvent(messageDTO));
	}

	public static Type<LogoutEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<LogoutEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(LogoutEventHandler handler)
	{
		handler.onReceiveLogoutMessage(this);
	}
	public MessageDTO getMessageDTO()
	{
		return messageDTO;
	}
}
