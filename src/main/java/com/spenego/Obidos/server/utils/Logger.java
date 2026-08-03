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

/**
 * A functional interface for logging.  No exceptions are every thrown from these methods.
 * Exception are logged to the logging facility.
 *
 * @author mmorgan
 *
 */
public interface Logger {
	@FunctionalInterface
	public interface LogMessageSupplier {	// we use this instead of Supplier so that caller can generate exceptions
		String get() throws Exception;		// NOSONAR -- we really want this to be a generic exception
	}

	/**
	 * @return true if debug level is set to INFO or DEBUG, otherwise false
	 */
	boolean isInfoEnabled();
	/**
	 * @return true if debug level is set to DEBUG, otherwise false
	 */
	boolean isDebugEnabled();

	void debug			(LogMessageSupplier lms);
	void info			(LogMessageSupplier lms);
	void warn			(LogMessageSupplier lms);
	void error			(LogMessageSupplier lms);
	void error			(LogMessageSupplier lms, Throwable t);

	void syslogInfo		(LogMessageSupplier lms);
	void syslogWarn		(LogMessageSupplier lms);
	void syslogError	(LogMessageSupplier lms);
	void syslogError	(LogMessageSupplier lms, Throwable t);

	void exception		(Throwable t);
}
