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
import com.spenego.Obidos.shared.dto.SearchDTO;

/**
 * Event is fired currently when when deleted user or admin is listed/searched
 * @author spgdev@spenego.com - Apr-16-2017
 */
public class SearchDeletedUsersEvent extends GwtEvent<SearchDeletedUsersEvent.SearchDeletedUsersEventHandler>
{
	private static Type<SearchDeletedUsersEventHandler> TYPE = new Type<SearchDeletedUsersEventHandler>();

	public interface SearchDeletedUsersEventHandler extends EventHandler
	{
		void onSearchDeletedUsersAction(SearchDeletedUsersEvent event);
	}

	private final SearchDTO searchDTO;
	public SearchDeletedUsersEvent(final SearchDTO searchDTO)
	{
		this.searchDTO = searchDTO;
	}

	public static void fire(HasHandlers source, SearchDTO searchDTO)
	{
		source.fireEvent(new SearchDeletedUsersEvent(searchDTO));
	}

	public static Type<SearchDeletedUsersEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<SearchDeletedUsersEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SearchDeletedUsersEventHandler handler)
	{
		handler.onSearchDeletedUsersAction(this);
	}

	public SearchDTO getSearchDTO()
	{
		return searchDTO;
	}
}
