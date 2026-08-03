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

package com.spenego.Obidos.client.rpc;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.NoteDTO;
import com.spenego.Obidos.shared.dto.NotesResult;
import com.spenego.Obidos.shared.dto.SharedNotesResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

@RemoteServiceRelativePath("rpc/notebookService")
public interface NotebookService extends RemoteService {
	public static class Utility {
		private final static NotebookServiceAsync instance = (NotebookServiceAsync) GWT.create(NotebookService.class);
		public static NotebookServiceAsync getInstance() { return instance; }
	}

	/**
	 * Create a new note.
	 *
	 * @param creds
	 * @param name
	 * @param notes
	 * @param itemExpiration TODO
	 * @return the ID of the new note.
	 * @throws ServerSideException
	 */
	NoteDTO createNote(AuthCredsDTO creds, String name, byte[] notes, Boolean shareable, ItemExpiration itemExpiration, PostOpActions postOpActions) throws ServerSideException;

	/**
	 * Returns the requested note.
	 *
	 * @param creds
	 * @param noteId
	 * @return
	 * @throws ServerSideException
	 */
	NoteDTO getNote(AuthCredsDTO creds, Long noteId) throws ServerSideException;

	/**
	 * Returns a list of notes owned by the user. The notes in the list DO NOT have the note contents, just names.
	 * They can be obtained by calling getNote().
	 *
	 * @param creds
	 * @param shared If true, only notes that have been shared to other users are returned. Conversely, if false,
	 *               only notes that have not been shared are returned. If null, all notes are returned.
	 * @param search - search string, pass null to not filter
	 * @param first
	 * @param count
	 * @param orderBy Orders the result set according to credential name, update time, create time
	 * @return
	 * @throws ServerSideException
	 */
	NotesResult getMyNotes(AuthCredsDTO creds, Boolean shared, String search, List<Long> preSelectedItems, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Returns a list of notes that have been shared with this user.
	 *
	 * @param creds
	 * @param containerId Only show items in this container. Pass null to show all containers.
	 * @param search - search string, pass null to not filter
	 * @param first
	 * @param count
	 * @param orderBy Orders the result set according to credential name, update time, create time
	 * @return
	 * @throws ServerSideException
	 */
	SharedNotesResult getNotesSharedWithMe(AuthCredsDTO creds, String search, List<Long> preSelectedItems, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Determine all the users with whom have access to the specified note.
	 *
	 * @param creds
	 * @param noteId
	 * @param userPattern TODO
	 * @param sharedSetQuality TODO
	 * @param preSelectedUsers Include these users in the result set. The users are marked as 'selected' on the user object.
	 * @param first
	 * @param count
	 * @param orderBy
	 * @return A UserResult that contains a list of users of whom have access to this note.
	 */
	LimitedUserResult getUsersForNote(AuthCredsDTO creds, Long noteId, UserDTO userPatterns, SharedSetQuality sharedSetQuality, ArrayList<Long> preSelectedUsers, Integer first, Integer count, ArrayList<OrderBy> orderBy) throws ServerSideException;

	/**
	 * Update the contents of a note. If no change for a given secret is desired, pass null. Calling this method will update
	 * all shared notes.
	 *
	 * @param creds
	 * @param credId
	 * @param username
	 * @param password
	 * @param comment
	 * @throws ServerSideException
	 */
	Void updateNote(AuthCredsDTO creds, NoteDTO note) throws ServerSideException;

	/**
	 * Share a note with another user.
	 *
	 * @param creds
	 * @param noteId The note you wish to share.
	 * @param recipientIds The IDs of the users you would like to share note with.
	 * @return The ID of the new Note record.
	 * @throws ServerSideException A SharingProhibitedException is thrown if the user attempts to share a credential
	 *                             that is not owned by them.
	 */
	Void shareNote(AuthCredsDTO creds, Long noteId, ArrayList<Long> recipientIds, String shareComment, PostOpActions postOpActions) throws ServerSideException;

	/**
	 * Share credentials with a group of users.
	 *
	 * @param creds
	 * @param credentialsId The credentials you wish to share.
	 * @param userId The user ID of the user you would like to share credentials with.
	 * @return The ID of the new Note record.
	 * @throws ServerSideException
	 */
	Void shareNoteWithGroups(AuthCredsDTO creds, Long noteId, ArrayList<Long> groupId, String shareComment, PostOpActions postOpActions) throws ServerSideException;

	/**
	 * For the specified note, all recipients have access revoked. This will remove the credential from all groups
	 * that have been shared.
	 *
	 * @param creds
	 * @param noteId
	 * @return
	 * @throws ServerSideException
	 */
	Void revokeAllSharedNotes(AuthCredsDTO creds, Long noteId, Boolean notifyRecipients, String revokeComment) throws ServerSideException;

	/**
	 * For the specified note, revoke the access to the specified user.
	 *
	 * @param creds
	 * @param noteId
	 * @return
	 * @throws ServerSideException
	 */
	Void revokeFromUsers(AuthCredsDTO creds, Long noteId, ArrayList<Long> userIds, Boolean notifyRecipients, String revokeComment) throws ServerSideException;

	/**
	 * Deletes the specified note and all shared instances of this note.
	 *
	 * @param creds
	 * @param noteId
	 * @return
	 * @throws ServerSideException
	 */
	Void deleteNote(AuthCredsDTO creds, List<Long> noteIds) throws ServerSideException;
}
