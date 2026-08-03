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
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class NotificationsResult extends ObidosResult<NotificationDTO> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer unreadMessageCount;
	private Integer readMessageCount;

	protected Class<NotificationDTO> getElementClass() { return NotificationDTO.class; }

	public NotificationsResult() {}

	public NotificationsResult(final Integer pageSize, final Integer startRow) {
		super(pageSize, startRow);
	}

	public <T extends Clearable> NotificationsResult(final Integer pageSize, final Integer startRow, final Collection<Long> preSelectedElements, final BiFunction<Supplier<Stream<T>>,Class<NotificationDTO>,List<NotificationDTO>> converter, final IntSupplier totalSupplier, final Supplier<Stream<T>> elementSupplier) {
		super(pageSize, startRow, preSelectedElements, converter, totalSupplier, elementSupplier);
	}

	public Integer getTotalItems() {
		return getTotal();
	}

	public Integer getUnreadMessageCount() {
		return unreadMessageCount;
	}

	public void setUnreadMessageCount(Integer unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}

	public Integer getReadMessageCount() {
		return readMessageCount;
	}

	public void setReadMessageCount(Integer readMessageCount) {
		this.readMessageCount = readMessageCount;
	}
}
