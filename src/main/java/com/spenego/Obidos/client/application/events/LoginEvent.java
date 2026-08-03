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
import com.spenego.Obidos.shared.dto.UserDTO;

/**
 * Event to fire after successful login
 * @author spgdev@spenego.com - Jan 2, 2017
 */
public class LoginEvent extends GwtEvent<LoginEvent.LoginEventHandler>
{
	private static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();

	public interface LoginEventHandler extends EventHandler
	{
		void onSuccessfulLogin(LoginEvent event);
	}

	private final UserDTO userDTO;
	public LoginEvent(final UserDTO userDTO)
	{
		this.userDTO = userDTO;
	}

	public static void fire(HasHandlers source, UserDTO userDTO)
	{
		source.fireEvent(new LoginEvent(userDTO));
	}

	public static Type<LoginEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<LoginEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler)
	{
		handler.onSuccessfulLogin(this);
	}

	public UserDTO getUserDTO()
	{
		return userDTO;
	}
}
