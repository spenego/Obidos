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

public final class LoggerImpl implements Logger {
	private final org.slf4j.Logger logger;
	private final org.slf4j.Marker marker;

	public LoggerImpl(final Class<?> arg) {
		logger = org.slf4j.LoggerFactory.getLogger(arg);
		marker = org.slf4j.MarkerFactory.getMarker("SYSLOG");
	}

	private String string(final LogMessageSupplier s) {
		try {
			return s.get();
		} catch(final Throwable t) {
			logger.error("Caught Exception in LoggerImpl ", t);
			return "Caught Exception in LoggerImpl: " + t;
		}
	}

	/**
	 * If logging is level info or greater (DEBUG), return the evaluation of s, otherwise the defaultValue.
	 * @param s
	 * @param defaultValue
	 * @return
	 */
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(final LogMessageSupplier la) {
		if (logger.isDebugEnabled()) {
			logger.debug(string(la));
		}
	}

	@Override
	public void syslogInfo(final LogMessageSupplier la) {
		if (logger.isInfoEnabled()) {
			logger.info(marker, string(la));
		}
	}


	@Override
	public void info(final LogMessageSupplier la) {
		if (logger.isInfoEnabled()) {
			logger.info(string(la));
		}
	}

	@Override
	public void warn(final LogMessageSupplier la) {
		if (logger.isWarnEnabled()) {
			logger.warn(string(la));
		}
	}

	@Override
	public void syslogWarn(final LogMessageSupplier la) {
		if (logger.isWarnEnabled()) {
			logger.warn(marker, string(la));
		}
	}

	@Override
	public void error(final LogMessageSupplier la) {
		if (logger.isErrorEnabled()) {
			logger.error(string(la));
		}
	}

	@Override
	public void error(final LogMessageSupplier la, Throwable t) {
		if (logger.isErrorEnabled()) {
			logger.error(string(la), t);
		}
	}

	@Override
	public void syslogError(final LogMessageSupplier la) {
		if (logger.isErrorEnabled()) {
			logger.error(marker, string(la));
		}
	}

	@Override
	public void syslogError(final LogMessageSupplier la, Throwable t) {
		if (logger.isErrorEnabled()) {
			logger.error(marker, string(la), t);
		}
	}

	@Override
	public void exception(final Throwable t) {
		logger.error(string(() -> "Caught: " + t), t);
		for(final StackTraceElement ste : t.getStackTrace()) {
			logger.error(ste.toString());
		}
	}
}
