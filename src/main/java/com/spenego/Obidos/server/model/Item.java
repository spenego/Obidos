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

package com.spenego.Obidos.server.model;

import java.io.Serializable;
import java.util.Date;

import com.spenego.Obidos.shared.dto.Clearable;
import com.spenego.Obidos.shared.dto.ItemDTO;

public class Item extends NamedModel implements Clearable, Model, Serializable, LimitedItem {
	private static final long serialVersionUID = 1L;
	private Long containerAssignmentId;
	private Integer securityClassification;
	private Boolean shareable;
	private Boolean shared;
	private Boolean isContainerPrivate;
	private Date sharesExpireAt;
	private String containerName;

	public Item() {}

	public Item(final Long id, final Boolean shared) {
		super(id);
		this.shared = shared;
	}

	public Item(final Long id, final Long userId) {
		super(id, userId);
	}

	public Item(final Long id, final Long userId, final Long containerAssignmentId) {
		super(id, userId);
		this.containerAssignmentId = containerAssignmentId;
	}

	public Item(final Long userId, final String name, final Long containerAssignmentId, final Boolean shareable) {
		super(null, userId, name);
		this.containerAssignmentId = containerAssignmentId;
		this.shareable = shareable;
		shared = false;
		securityClassification = 0;
	}

	@Override
	public void clear() {
		isContainerPrivate = shareable = shared = null;
		containerName = null;
		containerAssignmentId = null;
		sharesExpireAt = null;
		super.clear();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Long getItemId() {
		return getId();
	}

	private static boolean changed(final Object o1, final Object o2) {
		return o1 != null && !o1.equals(o2);
	}

	public final boolean attributesDiffer(final ItemDTO item) {
		return changed(item.getName(), getName()) || changed(item.getShareable(), shareable) ||
				(item.isExpirationSet() && changed(item.getItemExpiration().getExpiresAt(), sharesExpireAt)) ||
				(sharesExpireAt != null && item.isExpireTimeCleared() != null);
	}

	public Long getContainerAssignmentId() {
		return containerAssignmentId;
	}

	public void setContainerAssignmentId(Long containerAssignmentId) {
		this.containerAssignmentId = containerAssignmentId;
	}

	public Boolean getShareable() {
		return shareable;
	}

	public void setShareable(final Boolean shareable) {
		this.shareable = shareable;
	}

	@Override
	public Boolean getShared() {
		return shared;
	}

	public void setShared(final Boolean shared) {
		this.shared = shared;
	}

	public Date getSharesExpireAt() {
		return sharesExpireAt;
	}

	public void setSharesExpireAt(final Date expiresAt) {
		this.sharesExpireAt = expiresAt;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(final String containerName) {
		this.containerName = containerName;
	}

	public Boolean getIsContainerPrivate() {
		return isContainerPrivate;
	}

	public void setIsContainerPrivate(final Boolean isPrivate) {
		this.isContainerPrivate = isPrivate;
	}

	public Integer getSecurityClassification() {
		return securityClassification;
	}

	public void setSecurityClassification(Integer securityClassification) {
		this.securityClassification = securityClassification;
	}
}