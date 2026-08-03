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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.dao.PasswordResetMapper;
import com.spenego.Obidos.server.model.PasswordReset;
import com.spenego.Obidos.server.model.PasswordResetExample;
import com.spenego.Obidos.server.operations.PasswordResetOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public final class PasswordResetOperationsImpl extends ObidosOperations<PasswordReset> implements PasswordResetOperations  {
	private static final Logger	logger = LoggerFactory.getLogger(PasswordResetOperationsImpl.class);

	@Autowired
	private final PasswordResetMapper passwordResetMapper = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Mapper<PasswordReset> getMapper() {
		return passwordResetMapper;
	}

	@Override
	protected String getModelName() {
		return "passwordReset";
	}

	@Override
	public Long create(final PasswordReset record) {
		return createWithRandomID(record);
	}

	@Override
	public List<PasswordReset> getList(final byte state) {
		return passwordResetMapper.selectByExample(new PasswordResetExample(c -> c.andStateEqualTo(state)));
	}

	@Override
	public List<PasswordReset> getList(final List<Byte> states) {
		return passwordResetMapper.selectByExample(new PasswordResetExample(c -> c.andStateIn(states)));
	}

	@Override
	public List<PasswordReset> getList(final String emailAddress) {
		return passwordResetMapper.selectByExample(new PasswordResetExample(c -> c.andEmailAddressEqualTo(emailAddress)));
	}

	@Override
	public int deleteExpiredRequests(final Date expireDate) {
		return passwordResetMapper.deleteByExample(new PasswordResetExample(c -> c.andUpdatedAtLessThan(expireDate)));
	}

	@Override
	public PasswordReset getByToken(final String token) {
		final List<PasswordReset> list = passwordResetMapper.selectByExample(new PasswordResetExample(c -> c.andTokenEqualTo(token)));
		if (list == null || list.size() != 1) {
			throw new NoSuchRecordException("That password reset token is invalid.");
		}

		return list.get(0);
	}
}
