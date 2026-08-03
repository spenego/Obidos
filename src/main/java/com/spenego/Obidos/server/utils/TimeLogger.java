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
 * A class to help track the total time of server operations.
 *
 * The calling class logger is passed into the constructor so that it appears as though
 * the caller is logging the time duration (hopefully this will make more sense in the
 * log file than seeing log entries from TimeLogger).
 *
 * @author mmorgan
 *
 */
public final class TimeLogger
{
	private static final TimeLogger defaultLogger = new TimeLogger();
	private static final ThreadLocal<TimeLogger> localTimeLogger = ThreadLocal.withInitial(TimeLogger::new);

	private static TimeLogger set(final TimeLogger timeLogger, final Logger logger, final String methodName) {
		timeLogger.start = System.currentTimeMillis();
		timeLogger.sb.setLength(0);						// sb will be non-empty when using TLS
		timeLogger.sb.append(methodName);
		timeLogger.sb.append(" STARTING");
		logger.info(timeLogger.sb::toString);
		timeLogger.sb.setLength(methodName.length());

		return timeLogger;
	}

	private static TimeLogger get(final Logger logger, final String methodName) {
		return set(localTimeLogger.get(), logger, methodName);
	}

	/**
	 * Returns a ThreadLocal TimeLogger object.  Only one TimeLogger object is ever created per thread.
	 * This method is only intended to be used by the ObidosService class.
	 *
	 * @param logger
	 * @param methodName
	 * @return a TimeLogger object when logger has a logging level of info or greater.
	 */
	public static final TimeLogger getTLSTimeLogger(final Logger logger, final String methodName) {
		return logger != null && methodName != null && logger.isInfoEnabled() ? get(logger, methodName) : defaultLogger;
	}

	/**
	 * Returns a new TimeLogger object.
	 *
	 * @param logger
	 * @param methodName
	 * @return a TimeLogger object when logger has a logging level of info or greater.
	 */
	public static final TimeLogger createTimeLogger(final Logger logger, final String methodName) {
		if (logger == null || methodName == null) {
			throw new IllegalArgumentException("Neither logger nor methodName may be null");
		}
		return logger.isInfoEnabled() ? new TimeLogger(logger, methodName) : defaultLogger;
	}

	private long				start;
	private final StringBuilder	sb;

	protected TimeLogger() {
		this.start	= 0L;
		this.sb		= new StringBuilder(64);
	}

	private TimeLogger(final Logger logger, final String methodName) {
		this();
		set(this, logger, methodName);
	}

	public final void complete(final Logger logger) {
		logger.info(() -> sb.append(" COMPLETE. Duration: ").append(System.currentTimeMillis() - start).append("ms").toString());
	}
}
