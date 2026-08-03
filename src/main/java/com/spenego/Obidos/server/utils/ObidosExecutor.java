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
import java.util.concurrent.Executor;

/**
 * Certain operations need to be run after one or more other operations have completed.  This class allows
 * the collection of runnable operations to be collected for later execution.
 *
 * For Example, when we revoke Containers from a Groups, there are ItemAssignment records that have reference
 * counts that have been reduced to zero.  We need to remove those ItemAssignments before we return from the
 * methods, and sometimes before we perform the next operation in the revoke (revoking the actual Container
 * from the Group).
 */
public final class ObidosExecutor implements Executor {
	private final ArrayList<Runnable> runnables;

	public ObidosExecutor() {
		runnables = new ArrayList<>();
	}

	public ObidosExecutor(final Runnable r) {
		runnables = new ArrayList<>();
		runnables.add(r);
	}

	@Override
	public void execute(final Runnable r) {
		if (r != null) {
			runnables.add(r);
		}
	}

	public void clear() {
		runnables.clear();
	}

	/**
	 * Runs all runnables and then clears them from the list.
	 */
	public Void runAll() {
		runnables.forEach(Runnable::run);
		clear();
		return null;
	}
}
