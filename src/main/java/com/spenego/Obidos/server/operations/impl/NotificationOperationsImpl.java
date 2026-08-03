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

package com.spenego.Obidos.server.operations.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.NotificationMapper;
import com.spenego.Obidos.server.model.Notification;
import com.spenego.Obidos.server.model.NotificationExample;
import com.spenego.Obidos.server.operations.NotificationOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class NotificationOperationsImpl extends ObidosOperations<Notification> implements NotificationOperations {
	private static final Logger	logger = LoggerFactory.getLogger(NotificationOperationsImpl.class);

	@Autowired
	private final NotificationMapper	mapper = null;
	private final Map<Long, WeakReference<NotificationExample>> exampleCache = new ConcurrentHashMap<>();
	private long nextCleanTime = 0;
	private int activeUserThreshold = 128;

	@Override
	protected Logger getLogger()				{ return logger; }
	@Override
	protected NotificationMapper getMapper()	{ return mapper; }
	@Override
	protected String getModelName()				{ return "notification"; }

	@Override
	public Long create(final Notification notification) {
		return createWithRandomID(notification);
	}

	// we occasionally clean the cache of empty references
	private synchronized void cleanCache() {
		logger.info(() -> "Cleaning Notification Example cache. Size before clean: " + exampleCache.size());
		final Collection<Long> keys = new ArrayList<>();
		exampleCache.forEach((k,v) -> { if (v.get() == null) { keys.add(k); }});
		keys.forEach(exampleCache::remove);
		logger.info(() -> "Cleaned Notification Example cache. Size after clean: " + exampleCache.size());
		activeUserThreshold = exampleCache.size() * 2;
		nextCleanTime = System.currentTimeMillis() + 1000 * 60 * 15;  // clean once every fifteen minutes
	}

	private void cache(final Long userId, final NotificationExample example) {
		if (exampleCache.size() > activeUserThreshold && System.currentTimeMillis() >= nextCleanTime) {
			cleanCache();
		}
		exampleCache.put(userId, new WeakReference<NotificationExample>(example));
	}

	private NotificationExample getCachedExample(final Long userId) {
		final WeakReference<NotificationExample> ref = exampleCache.get(userId);
		return ref == null ? null : ref.get();
	}

	@Override
	public Integer getNotificationCount(final Long userId, final Boolean unread, final String msgSearch) {
		final boolean typicalCountCall = msgSearch == null && Boolean.TRUE.equals(unread);
		final NotificationExample ce = typicalCountCall ? getCachedExample(userId) : null;
		final boolean usingCachedExample = ce != null;
		final NotificationExample example = usingCachedExample ? ce : new NotificationExample(c -> c.andUserIdEqualTo(userId).andUnreadEqualTo(unread).andMessageLike(msgSearch));
		try {
			return mapper.countByExample(example);
		} finally {
			if (typicalCountCall && !usingCachedExample) {	// no need to re-cache it
				cache(userId, example);
			}
		}
	}

	@Override
	public Stream<Notification>	getNotifications(final Long userId, final Boolean unread, final String msgSearch, final Integer first, final Integer count, final List<OrderBy> orderBy) {
		return mapper.selectByExample(createExample(() -> new NotificationExample(c -> c.andUserIdEqualTo(userId).andUnreadEqualTo(unread).andMessageLike(msgSearch)), orderBy), createRowBounds(first, count)).stream();
	}

	@Override
	public Notification getNotification(final Integer action, final Long ownerId, final Long userId, final Long targetId) {
		final List<Notification> list = mapper.selectByExample(new NotificationExample(c -> c.andUserIdEqualTo(userId).andOwnerIdEqualTo(ownerId).andTargetIdEqualTo(targetId).andActionEqualTo(action)));

		if (list == null || list.isEmpty()) {
			throw new NoSuchRecordException();
		}

		return list.get(0);
	}

	private static NotificationExample getExample(final Long userId, final Collection<Long> notificationIds) {
		return new NotificationExample(c -> c.andUserIdEqualTo(userId).andIdIn(notificationIds));
	}

	@Override
	public Void markUnreadAs(final Long userId, final Collection<Long> notificationIds, final Boolean unRead) {
		mapper.updateByExampleSelective(new Notification(unRead), getExample(userId, notificationIds));
		return null;
	}

	@Override
	public Void update(final Notification notification) throws NoSuchRecordException {
		if (mapper.updateByPrimaryKeySelective(notification) != 1) {
			throw new NoSuchRecordException(getModelName(), notification.getId());
		}
		return null;
	}

	@Override
	public Integer delete(final Long userId, final Collection<Long> notificationIds) {
		return mapper.deleteByExample(getExample(userId, notificationIds));
	}
}
