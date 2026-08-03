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

package com.spenego.Obidos.server.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spenego.Obidos.client.rpc.NotebookService;
import com.spenego.Obidos.server.actions.ContainerActions;
import com.spenego.Obidos.server.actions.ItemActions;
import com.spenego.Obidos.server.actions.UserActions;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.StreamSupplier;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.NoteDTO;
import com.spenego.Obidos.shared.dto.NotesResult;
import com.spenego.Obidos.shared.dto.SecurityClassificationDTO;
import com.spenego.Obidos.shared.dto.SharedNotesResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@Service("notebookService")
public final class NotebookServiceImpl extends ObidosService implements NotebookService {
	private static final Logger logger = LoggerFactory.getLogger(NotebookServiceImpl.class);

	@Autowired private final ItemActions itemActions = null;
	@Autowired private final UserActions userActions = null;
	@Autowired private final ContainerActions containerActions = null;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private Long getNotebookContainerAssignmentId(final User user) throws ServerSideException {
		return containerActions.getContainerAssignmentId(user, ContainerDTO.NOTEBOOK_ID);
	}

 	@Transactional @Override
	public NoteDTO createNote(final AuthCredsDTO creds, final String name, final byte[] notes, final Boolean shareable, final ItemExpiration itemExpiration, final PostOpActions postOpActions) throws ServerSideException {
 		try {
 			return userFunction(creds, "create notes", "createNote", user -> {
 				final ItemDTO item = itemActions.create(user, name, itemExpiration, getNotebookContainerAssignmentId(user), summonPWHash(), postOpActions, shareable, new SecurityClassificationDTO(), itemActions.convertToItemValues(user.getId(), null, notes));
				memoryWiper.addReferent(item);
 				return get(user, itemActions.getItemAssignment(item.getId(), user.getId()).getId());});
 		} finally {
 			Arrays.fill(notes, (byte) 0);
 		}
	}

	private NoteDTO get(final User user, final Long noteId) throws ServerSideException {
		final NoteDTO note = itemActions.get(user, noteId, summonPWHash(), NoteDTO.class, Arrays.asList(OrderBy.UDF_POSITION_ASC));
		note.setNotes(note.getValues().get(0).getFieldValues().get(0).getBlobValue());
		return note;
	}

 	@Transactional(readOnly=true) @Override
	public NoteDTO getNote(final AuthCredsDTO creds, final Long noteId) throws ServerSideException {
		return userFunction(creds, "get note", "getNote", user -> get(user, noteId));
	}

 	@Transactional(readOnly=true) @Override
	public NotesResult getMyNotes(final AuthCredsDTO creds, final Boolean shared, final String search, final List<Long> preSelectedNotes, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get my notes", "getMyNotes", user -> itemActions.getMyNotes(user, shared, search, preSelectedNotes, first, count, orderBy));
	}

 	@Transactional(readOnly=true) @Override
	public SharedNotesResult getNotesSharedWithMe(final AuthCredsDTO creds, final String search, final List<Long> preSelectedNotes, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get notes shared with me", "getNotesSharedWithMe", user -> itemActions.getNotesSharedWithUser(user, search, preSelectedNotes, first, count, orderBy));
	}

	private Long getItemId(final User caller, final Long itemAssignmentId) throws ServerSideException {
		return itemActions.getItemId(caller, itemAssignmentId);
	}

 	@Transactional @Override
	public Void updateNote(final AuthCredsDTO creds, final NoteDTO note) throws ServerSideException {
 		try {
 			return userFunction(creds, "update note", "updateNote", user -> itemActions.update(user, new ItemDTO(getItemId(user, note.getId()), note.getName(), itemActions.convertToItemValues(user.getId(), getItemId(user, note.getId()), note.getNotes())), summonPWHash()));
 		} finally {
 			Arrays.fill(note.getNotes(), (byte) 0);
 		}
	}

 	@Transactional @Override
	public Void deleteNote(final AuthCredsDTO creds, final List<Long> noteIds) throws ServerSideException {
		return userFunction(creds, "delete a note", "deleteNote", user -> itemActions.delete(user, noteIds, summonPWHash()));
	}

 	@Transactional(readOnly=true) @Override
	public LimitedUserResult getUsersForNote(final AuthCredsDTO creds, final Long noteId, final UserDTO userPatterns, final SharedSetQuality sharedSetQuality, final ArrayList<Long> preSelectedUsers, final Integer first, final Integer count, final ArrayList<OrderBy> orderBy) throws ServerSideException {
		return userFunction(creds, "get users of whom the note has been shared with", "getUsersForNote", user -> userActions.getUsersForItem(user, noteId, userPatterns, sharedSetQuality, preSelectedUsers, first, count, orderBy));
	}

 	@Transactional @Override
	public Void shareNote(final AuthCredsDTO creds, final Long noteId, final ArrayList<Long> userIds, final String shareComment, final PostOpActions postOpActions) throws ServerSideException {
		return userFunction(creds, "share a note", "shareNote", user -> itemActions.shareItemWithUsers(user, itemActions.getItemId(user, noteId), summonPWHash(), postOpActions, StreamSupplier.create(userIds), shareComment, Boolean.TRUE));
	}

 	@Transactional @Override
	public Void shareNoteWithGroups(final AuthCredsDTO creds, final Long noteId, final ArrayList<Long> groupIds, final String shareComment, final PostOpActions postOpActions) throws ServerSideException {
		return userFunction(creds, "share note with groups", "shareNoteWithGroups", user -> itemActions.shareItemWithGroups(user, noteId, summonPWHash(), postOpActions, StreamSupplier.create(groupIds), null, Boolean.TRUE, shareComment));
	}

 	@Transactional @Override
	public Void revokeAllSharedNotes(final AuthCredsDTO creds, final Long noteId, final Boolean notifyRecipients, final String revokeComment) throws ServerSideException {
		return userFunction(creds, "revoking all shared notes", "revokeAllSharedNotes", user -> itemActions.revokeItem(user, noteId, summonPWHash(), revokeComment, null));
	}

 	@Transactional @Override
	public Void revokeFromUsers(final AuthCredsDTO creds, final Long noteId, final ArrayList<Long> userIds, final Boolean notifyRecipients, final String revokeComment) throws ServerSideException {
		return userFunction(creds, "revoking note from users", "revokeFromUsers", user -> itemActions.revokeItemFromUsers(user, noteId, userIds, summonPWHash(), Boolean.TRUE, revokeComment, null));
	}
}
