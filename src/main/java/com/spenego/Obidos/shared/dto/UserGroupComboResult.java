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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class UserGroupComboResult extends UsersResult<UserGroupComboDTO> implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<Long, UserGroupComboDTO> groups;
	private HashMap<Long, UserGroupComboDTO> users;
	

	protected Class<UserGroupComboDTO> getElementClass() { return UserGroupComboDTO.class; }

	public UserGroupComboResult() {}

	public <T extends Clearable> UserGroupComboResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedContainers, final BiFunction<Supplier<Stream<T>>,Class<UserGroupComboDTO>,List<UserGroupComboDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedContainers, converter, totalSupplier, elementSupplier);
		final List<UserGroupComboDTO> list = getElements();
		groups = new HashMap<>();
		users  = new HashMap<>();
		if (list != null) { list.forEach(e -> (e.isUser() ? users : groups).put(e.getId(), e)); }
	}

	public List<UserGroupComboDTO>	getUsers()							{ return getElements(); }
	public Integer					getTotalUsers()						{ return getTotal(); }
	public UserGroupComboDTO		getGroupCombo(final Long group_id)	{ return groups.get(group_id); }
	public UserGroupComboDTO		getUserCombo(final Long user_id)	{ return users.get(user_id); }
}
