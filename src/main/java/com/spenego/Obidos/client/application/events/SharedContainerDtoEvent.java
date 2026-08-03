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
import com.spenego.Obidos.shared.dto.SharedContainerDTO;

/**
 * The event is fired when clicked on a container in containers shared with me 
 * presenter. The event is received in ListItemsInSharedContainerPresenter.java 
 * which uses the permission in the SharedContainerDTO to activate Edit button
 * spgdev@spenego.com Oct-20-2025 
 */
public class SharedContainerDtoEvent extends GwtEvent<SharedContainerDtoEvent.SharedContainerDtoEventHandler>
{
	private static Type<SharedContainerDtoEventHandler> TYPE = new Type<SharedContainerDtoEventHandler>();

	public interface SharedContainerDtoEventHandler extends EventHandler
	{
		void onClickSharedContair(SharedContainerDtoEvent event);
	}

	private final SharedContainerDTO sharedContainerDTO;
	public SharedContainerDtoEvent(final SharedContainerDTO sharedContainerDTO)
	{
		this.sharedContainerDTO = sharedContainerDTO;
	}

	public static void fire(HasHandlers source, SharedContainerDTO sharedContainerDTO)
	{
		source.fireEvent(new SharedContainerDtoEvent(sharedContainerDTO));
	}

	public static Type<SharedContainerDtoEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<SharedContainerDtoEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SharedContainerDtoEventHandler handler)
	{
		handler.onClickSharedContair(this);
	}

	public SharedContainerDTO getSharedContainerDTO()
	{
		return sharedContainerDTO;
	}
}
