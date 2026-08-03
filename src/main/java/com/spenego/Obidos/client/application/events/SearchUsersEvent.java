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
 * Event to fire when ENTER is pressed on Search TextBox or Search button is clicked
 * @author spgdev@spenego.com - Apr-01-2017
 */
public class SearchUsersEvent extends GwtEvent<SearchUsersEvent.SearchUsersEventHandler>
{
	private static Type<SearchUsersEventHandler> TYPE = new Type<SearchUsersEventHandler>();

	public interface SearchUsersEventHandler extends EventHandler
	{
		void onSearchUsersAction(SearchUsersEvent event);
	}

	private final SearchDTO searchdTO;
	public SearchUsersEvent(final SearchDTO searchdTO)
	{
		this.searchdTO = searchdTO;
	}

	public static void fire(HasHandlers source, SearchDTO searchdTO)
	{
		source.fireEvent(new SearchUsersEvent(searchdTO));
	}

	public static Type<SearchUsersEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<SearchUsersEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SearchUsersEventHandler handler)
	{
		handler.onSearchUsersAction(this);
	}

	public SearchDTO getSearchDTO()
	{
		return searchdTO;
	}
}
