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

package com.spenego.Obidos.server.actions.impl;

import com.spenego.Obidos.server.actions.CapabilityActions;
import com.spenego.Obidos.server.model.Audit;
import com.spenego.Obidos.server.model.Capability;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.PermissionDeniedException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public final class CapabilityActionsImpl extends ObidosActions<Capability> implements CapabilityActions {
	private static final Logger logger = LoggerFactory.getLogger(CapabilityActionsImpl.class);

	@Override
	protected final Logger getLogger() { return logger; }

	@Override
	protected final Operations<Capability> getOperations() {
		return capabilityOperations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		return Audit.DELETE_CAPABILITY;
	}

	@Override
	public Long create(final User admin, final CapabilityDTO capability) {
		return capabilityOperations.create(convert(capability, Capability.class));
	}

	@Override
	public CapabilityDTO get(final UserDTO user) {
		return convert(getCapability(user), CapabilityDTO.class);
	}

	private void auditCapabilityChange(final User admin, final User user, final boolean currentCapability, final boolean newCapability, final String str) {
		if (currentCapability != newCapability) {
			final String userType = user.isAdmin() ? "Admin" : "User";
			final String action = newCapability ? "enabled" : "disabled";
			final String msg = userType + " ability '" + str + "' is now " + action;
			logger.info(() -> "Admin " + admin.getUsername() + " changed config for " + userType + " " + user.getUsername() + ": " + msg);
			audit(Audit.UPDATE_CAPABILITY, admin.getUsername(), admin.getId(), user.getUsername(), user.getId(), null, msg);
		}
	}

	@Override
	public Void update(final User admin, final CapabilityDTO capability) {
		if (capability.getUserId() == null)					{ throw new ServerSideException("Capability object must have a target user specified."); }
		if (admin.getDeleted() || admin.getLocked())		{ throw new PermissionDeniedException("Your account is locked or deleted."); }
		if (admin.self(capability.getUserId()))				{ throw new PermissionDeniedException("You may not update your own capabilities."); }

		final Capability currentCapability = getModel(capability.getId());
		if (currentCapability.getRootAdmin())				{ throw new PermissionDeniedException("The capabilities of this admin are immutable."); }

		final User user = getUser(capability.getUserId());
		if (isFalse(user.getAdministrator())) {
			capability.disableAdminCapabilities();
		}

		auditCapabilityChange(admin, user, currentCapability.getRootAdmin(),				capability.getRootAdmin(),				"root admin");
		auditCapabilityChange(admin, user, currentCapability.getCreateAdmin(),				capability.getCreateAdmin(),			"create admin");
		auditCapabilityChange(admin, user, currentCapability.getDeleteAdmin(),				capability.getDeleteAdmin(),			"delete admin");
		auditCapabilityChange(admin, user, currentCapability.getCreateUser(),				capability.getCreateUser(),				"create user");
		auditCapabilityChange(admin, user, currentCapability.getDeleteUser(),				capability.getDeleteUser(),				"delete user");
		auditCapabilityChange(admin, user, currentCapability.getLockAdmin(),				capability.getLockAdmin(),				"lock admin");
		auditCapabilityChange(admin, user, currentCapability.getLockUser(),					capability.getLockUser(),				"lock user");
		auditCapabilityChange(admin, user, currentCapability.getChangeAdminCredentials(),	capability.getChangeAdminCredentials(), "change admin credentials");
		auditCapabilityChange(admin, user, currentCapability.getChangeUserCredentials(),	capability.getChangeUserCredentials(),	"change user credentials");
		auditCapabilityChange(admin, user, currentCapability.getCreateGlobalGroups(),		capability.getCreateGlobalGroups(),		"create global groups");
		auditCapabilityChange(admin, user, currentCapability.getCreateGlobalTemplate(),		capability.getCreateGlobalTemplate(),	"create global template");
		auditCapabilityChange(admin, user, currentCapability.getModifyEmailTemplates(),		capability.getModifyEmailTemplates(),	"modify email templates");
		auditCapabilityChange(admin, user, currentCapability.getModifySettings(),			capability.getModifySettings(),			"modify settings");

		logger.info(() -> "In CapabilityActions: Modify Settings DTO: " + capability.getModifySettings());
		Capability c = convert(capability, Capability.class);
		logger.info(() -> "In CapabilityActions: Modify Settings Model: " + c.getModifySettings());
		return capabilityOperations.updateSelective(convert(capability, Capability.class));
	}
}
