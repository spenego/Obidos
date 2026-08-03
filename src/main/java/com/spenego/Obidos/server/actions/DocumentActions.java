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

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.security.PassphraseHash;

public interface DocumentActions {
	Integer	getUploadState(User caller, Long documentId);
	Void	update(User caller, Document document, String filename, String guidFilename, byte[] fileSize, byte[] compressedFileSize, SodiumKeyPair kp, PassphraseHash passphraseHash);
	Void	delete(Document document);
	Path	getPath(final String filename) throws IOException;
	Stream<Document> getDocumentsOfItem(final Long itemId);
}
