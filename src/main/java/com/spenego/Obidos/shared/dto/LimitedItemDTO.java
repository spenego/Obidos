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

import com.spenego.Obidos.shared.Selectable;

public class LimitedItemDTO extends BaseShared implements HasId, Selectable, Clearable, Serializable {
	private static final long serialVersionUID = 1L;

	private Long			id;
	private Long			containerAssignmentId;
	private String			name;
	private Integer			shareCount;
	private Integer			version;
	private Boolean			shareable;
	private Boolean			shared;
	private Boolean			selected;
	private ItemExpiration	itemExpiration;
	private String			containerName;

	public LimitedItemDTO() {}

	public LimitedItemDTO(final Long id, final String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public void clear() {
		containerName = null;
		if (itemExpiration != null) {
			itemExpiration.clear();
			itemExpiration = null;
		}
		selected = shared = shareable = null;
		version = shareCount = null;
		name = null;
		containerAssignmentId = id = null;
	}

	public final void ensureIdIsSet(final Long id) {
		if (this.id == null) {
			this.id = id;
		}
	}

	public Long getId()								{ return id; }
	public String getName()							{ return name; }
	public Integer getShareCount()					{ return shareCount; }
	public Boolean getShared()						{ return shared; }
	public Boolean getShareable()					{ return shareable; }
	public Integer getVersion()						{ return version; }
	@Override
	public Boolean getSelected()					{ return selected; }
	public String getContainerName()				{ return containerName; }
	public Long getcontainerAssignmentId()			{ return containerAssignmentId; }
	public boolean isExpirationSet()				{ return getItemExpiration() != null; }
	public boolean isShareExpired()					{ return isExpirationSet() && getItemExpiration().isExpired(); }
	public ItemExpiration getItemExpiration()		{ return itemExpiration; }

	public void setId(final Long id)					{ this.id = id; }
	public void setName(final String name)				{ this.name = name; }
	public void setShareCount(final Integer shareCount)	{ this.shareCount = shareCount; }
	public void setShared(final Boolean shared)			{ this.shared = shared; }
	public void setShareable(final Boolean shareable)	{ this.shareable = shareable; }
	public void setVersion(final Integer version)		{ this.version = version; }
	@Override
	public void setSelected(final Boolean selected)							{ this.selected = selected; }
	public void setContainerName(final String containerName)				{ this.containerName = containerName; }
	public void setcontainerAssignmentId(final Long containerAssignmentId)	{ this.containerAssignmentId = containerAssignmentId; }
	public void setItemExpiration(final ItemExpiration itemExpiration)		{ this.itemExpiration = itemExpiration; }

}
