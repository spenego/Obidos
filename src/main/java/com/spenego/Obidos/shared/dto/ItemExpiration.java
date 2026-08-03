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
import java.util.Date;

public final class ItemExpiration implements Clearable, Serializable {
	private static final long serialVersionUID = 1L;
	private static final long MS_PER_MINUTE  = 1000L * 60;
	private static final long MS_PER_HOUR    = MS_PER_MINUTE * 60;
	private static final long MS_PER_DAY     = MS_PER_HOUR * 24;
	private static final int MAX_EXPIRE_DAYS = 3650; // 10 years

	private Date    expireDate;
	private Integer daysUntilExpire;
	private Integer hoursUntilExpire;
	private Integer minutesUntilExpire;
	private Boolean expired;

	public ItemExpiration() { }

	private static long hoursInMs(final Integer days) {
		return ((days != null) ? 23 : 0) * MS_PER_HOUR;
	}

	private static long minutesInMs(final Integer hours) {
		return ((hours != null) ? 59 : 0) * MS_PER_MINUTE;
	}

	public ItemExpiration(final Integer days, final Integer hours, final Integer minutes) {
		daysUntilExpire = days > MAX_EXPIRE_DAYS ? MAX_EXPIRE_DAYS : days;
		hoursUntilExpire = hours > 24 ? 24 : hours;
		minutesUntilExpire = minutes > 59 ? 59 : minutes;

		final long daysMs = val(days) * MS_PER_DAY;
		final long hoursMs = hours != null ? hours : hoursInMs(days);
		final long minutesMs = minutes != null ? minutes : minutesInMs(hours);
		final long secsMs = (daysUntilExpire == 0 && hoursUntilExpire == 0 && minutes == 0) ? 0L : 59000L; // we add 59 seconds to avoid the immediate decrement minute decrement
		expireDate = new Date(System.currentTimeMillis() + daysMs + hoursMs + minutesMs + secsMs);
		expired = (System.currentTimeMillis() >= expireDate.getTime());
	}

	public ItemExpiration(final Date expireDate) {
		this.expireDate = expireDate;
		calculateDays();
	}

	@Override
	public void clear() {
		expired = null;
		daysUntilExpire = hoursUntilExpire = minutesUntilExpire = null;
		expireDate = null;
	}

	private void calculateDays() {
		final long now = System.currentTimeMillis();
		final long expireTime = expireDate.getTime();
		final boolean xpired = (now >= expireTime);

		this.expired = xpired;

		if (xpired) {
			final long diff = expireTime - now;
			final long days = diff / MS_PER_DAY;
			final long days_in_ms = days * MS_PER_DAY;
			final long hours = days == 0 ? 0 : (diff % days_in_ms) / MS_PER_HOUR;
			final long hours_in_ms = hours * MS_PER_HOUR;
			final long minutes = (hours == 0) ? 0 : ((diff - days_in_ms) % hours_in_ms) / MS_PER_MINUTE;
			daysUntilExpire = (int) days;
			hoursUntilExpire = (int) hours;
			minutesUntilExpire = (int) minutes;
		}
	}

	@Override
	public String toString() {
		return Boolean.TRUE.equals(expired) ? "expired at " + expireDate : expireDate + ": " + daysUntilExpire + " days, " + hoursUntilExpire + "hours, " + minutesUntilExpire + " minutes, ";
	}

	public Date getExpiresAt() {
		return expireDate;
	}

	public void setExpiresAt(final Date expiresAt) {
		this.expireDate = expiresAt;
	}

	public Integer getDaysUntilExpire() {
		if (daysUntilExpire == null) {
			calculateDays();
		}
		return daysUntilExpire;
	}

	public void setDaysUntilExpire(Integer daysUntilExpire) {
		this.daysUntilExpire = daysUntilExpire;
	}

	public Integer getHoursUntilExpire() {
		if (hoursUntilExpire == null) {
			calculateDays();
		}
		return hoursUntilExpire;
	}

	public void setHoursUntilExpire(Integer hoursUntilExpire) {
		this.hoursUntilExpire = hoursUntilExpire;
	}

	public Integer getMinutesUntilExpire() {
		if (minutesUntilExpire == null) {
			calculateDays();
		}
		return minutesUntilExpire;
	}

	public void setMinutesUntilExpire(Integer minutesUntilExpire) {
		this.minutesUntilExpire = minutesUntilExpire;
	}

	public boolean expirationSpecified() {
		return expireDate != null || getDaysUntilExpire() != null || getHoursUntilExpire() != null || getMinutesUntilExpire() != null;
	}

	private static long val(final Integer v) {
		return v == null ? 0L : v;
	}

	public boolean isExpired() {
		return expired;
	}
}
