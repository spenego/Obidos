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

package com.spenego.Obidos.server.actions;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.Encryption.PublicKeyDecryptor;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ContainerResult;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedContainerResult;

public interface ContainerActions {
	void					setBlockAudit(boolean blockAudit);
	Long					createContainerAssignment(Long recipientId, Long containerId, Collection<Long> recipients, Boolean sharedExplicitly, Boolean ownershipControl, Boolean addPermitted, Boolean updatePermitted);
	Long					getContainerAssignmentId(User caller, Long containerId);
	Long					createDefaultPrivateContainer(User caller);
	Long					createDefaultPublicContainer(User caller);
	Long					createContainer(User caller, String containerName, Boolean isPrivate, PassphraseHash passphraseHash);
	/**
	 * Deletes all assignments that have a reference count of zero. A reference count of zero means the container is no longer shared with that user.
	 * Notifications are sent to users who no longer have access to the containers.
	 */
	void					deleteArtifacts(User caller, String revokeComment, Long containerId);

	// Deletes the container if it is empty.
	void					deleteIfEmpty(Long containerAssignmentId);
	Void					update(User caller, ContainerDTO container, PassphraseHash passPhrase);
	ContainerDTO			get(User caller, Long containerAssignmentId);
	Boolean					containerIsPrivate(Long containerAssignmentId);
	Void					delete(User caller, Collection<Long> ids, PassphraseHash passphraseHash);
	ContainerResult			getMyContainers(User caller, String search, Collection<Long> preSelectedContainers, Boolean shared, Boolean shareable, Integer first, Integer count, List<OrderBy> orderBy);
	SharedContainerResult	getContainersSharedWithMe(User caller, String search, Collection<Long> preSelectedContainers, Integer first, Integer count, List<OrderBy> orderBy);
	Void					shareContainerWithUsers(User caller, Long containainerId, Long containerAssignmentId,  Supplier<Stream<Long>> recipientStreamSupplier, Supplier<PublicKeyDecryptor> pkdSupplier, String shareComment, PostOpActions postOpActions);
	Void					shareContainerWithUsers(User caller, Long containerAssignmentId,  Supplier<Stream<Long>> recipientStreamSupplier, PassphraseHash passphraseHash, String shareComment, PostOpActions postOpActions);
	Void					shareContainerWithGroups(User caller, Long containerAssignmentId, Supplier<Stream<Long>> groupIdStreamSupplier, PassphraseHash passphraseHash, String shareComment, PostOpActions postOpActions);
	Void					revokeContainerFromUsers(User caller, Long containerAssignmentId,  Supplier<Stream<Long>> recipientIdSupplier, PassphraseHash passphraseHash, String revokeComment, PostOpActions postOpActions);
	Void					revokeContainerFromGroups(User caller, Long containerAssignmentId,  Collection<Long> groupIdSupplier, PassphraseHash passphraseHash, String shareComment);
	void					setRunningInUnitTest();
	Void					shareContainerWithUsersWithoutNotify(User caller, Long containerId, Long containerAssignmentId, Supplier<Stream<Long>> recipientStreamSupplier, Supplier<PublicKeyDecryptor> pkdSupplier, Collection<Long> recipients, Boolean sharedExplicitly);
	Void					grantUsersPermission (User caller, Long containerAssignmentId, PermissionDTO permission, Collection<Long> recipients, PassphraseHash passphraseHash);
	Void					grantGroupsPermission(User caller, Long containerAssignmentId, PermissionDTO permission, Collection<Long> groups, PassphraseHash passphraseHash);
	Void					takeOwnership		 (User caller, Long containerAssignmentId, PassphraseHash passphraseHash, PostOpActions postOpActions);
}
