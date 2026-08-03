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

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.dao.DocumentMapper;
import com.spenego.Obidos.server.dao.Mapper;
import com.spenego.Obidos.server.model.Document;
import com.spenego.Obidos.server.model.DocumentExample;
import com.spenego.Obidos.server.operations.DocumentOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;

public final class DocumentOperationsImpl extends ObidosOperations<Document>  implements DocumentOperations {
	private static final Logger	logger = LoggerFactory.getLogger(DocumentOperationsImpl.class);

	@Autowired
	private final DocumentMapper mapper = null;

	@Override
	protected Logger getLogger()			{ return logger; }
	@Override
	protected Mapper<Document> getMapper()	{ return mapper; }
	@Override
	protected String getModelName()			{ return "document"; }

	@Override
	public Long create(final Document val) {
		return createWithRandomID(val);
	}

	@Override
	public Stream<Document> getDocumentsOfItem(final Long itemId) {
		return mapper.getDocumentsOfItem(itemId).stream();
	}

	@Override
	public Stream<Document> getDocumentsOfGuid(final String guid) {
		return mapper.selectByExample(new DocumentExample(c -> c.andGuidEqualTo(guid)), createRowBounds(null, null)).stream();
	}

	@Override
	public Long getDocumentItemId(final Long documentId) {
		return mapper.getDocumentItemId(documentId);
	}
}
