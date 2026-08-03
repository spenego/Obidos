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

public final class PermissionDTO implements Clearable {
	private static final long serialVersionUID = 1L;

	private Boolean mayUpdate;
	private Boolean mayAdd;
	private Boolean hasOwnershipControl;  // user is granted permission to take ownership of item

	@Override
	public void clear() { mayUpdate = hasOwnershipControl = mayAdd = null; }

	public PermissionDTO() { /* Default constructor is sufficient */ }

	public PermissionDTO(final Boolean mayUpdate, final Boolean hasOwnershipControl) {
		this.mayAdd = false;
		this.mayUpdate = mayUpdate;
		this.hasOwnershipControl = hasOwnershipControl;
	}

	public PermissionDTO(final Boolean mayUpdate, final Boolean hasOwnershipControl, final Boolean mayAdd) {
		this.mayUpdate = mayUpdate;
		this.mayAdd = mayAdd;
		this.hasOwnershipControl = hasOwnershipControl;
	}

	public String toString()				{ return "Permission: update = " + mayUpdate + ", add = " + mayAdd + ", ownership control = " + hasOwnershipControl; }
	public String message()					{ return Boolean.TRUE.equals(hasOwnershipControl) ? "take ownership of" : Boolean.TRUE.equals(mayUpdate) ? "update" : "read"; }

	public Boolean getMayAdd()				{ return mayAdd; }
	public Boolean getMayUpdate()			{ return mayUpdate; }
	public Boolean getHasOwnershipControl() { return hasOwnershipControl; }
	public boolean somethingIsSet()			{ return mayUpdate != null || mayAdd != null || hasOwnershipControl != null; }

	public void setMayAdd(Boolean mayAdd)							{ this.mayAdd = mayAdd; }
	public void setMayUpdate(Boolean mayUpdate)						{ this.mayUpdate = mayUpdate; }
	public void setHasOwnershipControl(Boolean hasOwnershipControl) { this.hasOwnershipControl = hasOwnershipControl; }
}
