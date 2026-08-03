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
import com.spenego.Obidos.shared.dto.LimitedUserDTO;

/**
 * Event to fire when Edit user button is clicked in ListUsersView.java
 * @author spgdev@spenego.com - Mar-18-2017
 */
public class EditUserEvent extends GwtEvent<EditUserEvent.EditUserEventHandler>
{
	private static Type<EditUserEventHandler> TYPE = new Type<EditUserEventHandler>();

	public interface EditUserEventHandler extends EventHandler
	{
		void onEditUserButtonClick(EditUserEvent event);
	}

	private final LimitedUserDTO userDTO;
	public EditUserEvent(final LimitedUserDTO userDTO)
	{
		this.userDTO = userDTO;
	}

	public static void fire(HasHandlers source, LimitedUserDTO userDTO)
	{
		source.fireEvent(new EditUserEvent(userDTO));
	}

	public static Type<EditUserEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<EditUserEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(EditUserEventHandler handler)
	{
		handler.onEditUserButtonClick(this);
	}

	public LimitedUserDTO getUserDTO()
	{
		return userDTO;
	}
}
