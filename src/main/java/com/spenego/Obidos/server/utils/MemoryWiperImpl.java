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

package com.spenego.Obidos.server.utils;

import java.util.ArrayList;
import java.util.List;

import com.spenego.Obidos.shared.dto.Clearable;

public final class MemoryWiperImpl implements Runnable, MemoryWiper {
	private static final Logger logger = LoggerFactory.getLogger(MemoryWiperImpl.class);

	private final List<ClearableWrapper> clearables;
	private final List<ClearableWrapper> stagingList;
	private int memoryWipeDelay;		// in ms, this is controlled by the system_config db table
	private boolean terminateLoop = false;

	public MemoryWiperImpl() {
		clearables = new ArrayList<>();
		stagingList = new ArrayList<>();
		memoryWipeDelay = 10000;
		final Thread t = new Thread(this, "Memory Wiper");
		t.setDaemon(true);
		t.start();
	}

	private class ClearableWrapper {
		final long insertionTime;
		final Clearable clearable;

		public ClearableWrapper(final Clearable c) {
			insertionTime = System.currentTimeMillis();
			clearable = c;
		}
	}

	private void wipeExpiredItems() {
		synchronized(clearables) {
			stagingList.addAll(clearables);	// use a staging list to minimize the duration we hold the synchronization semaphore
			clearables.clear();
		}

		if (!stagingList.isEmpty()) {
			final Thread thread = Thread.currentThread();
			int priority = thread.getPriority();
			thread.setPriority((Thread.MIN_PRIORITY + priority) / 2);

			try {
				logger.info(() -> "Memory wipe staging list has " + stagingList.size() + " objects");
				final long now = System.currentTimeMillis(); // objects can only persist for memoryWipeDelay

				for(final ClearableWrapper ref : stagingList) {
					if (ref.insertionTime + memoryWipeDelay <= now) {
						try {
							logger.debug(() -> "clearing " + ref.clearable);
							ref.clearable.clear();
						} catch(final Throwable t) { /* clear methods should never throw exceptions, ignore them if they do */ }
					}
				}
				stagingList.removeIf(r -> (r.insertionTime + memoryWipeDelay) <= now);
			} finally {
				thread.setPriority(priority);
			}
		}
	}

	private void safeSleep() {
		try {
			Thread.sleep(memoryWipeDelay == 0 ? 5000 : memoryWipeDelay < 6000 ? 3000 : memoryWipeDelay / 2);  // NOSONAR - splitting ternary would increase complexity
		} catch (final InterruptedException e) {
			logger.error(() -> "Caught " + e + " while trying to sleep.");
		}
	}

	@Override
	public void setMemoryWipeDelay(int msDelay) {
		memoryWipeDelay = msDelay;
	}

	@Override
	public <T extends Clearable> void addReferent(final T clearable) {
		if (memoryWipeDelay != 0) {
			synchronized(clearables) {
				clearables.add(new ClearableWrapper(clearable));
			}
		}
	}

	@Override
	public void run() {
		while(!terminateLoop) {
			safeSleep();
			wipeExpiredItems();
		}
	}
}
