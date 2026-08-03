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

package com.spenego.Obidos.shared;

import java.io.Serializable;

public class PostOpActions implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int SEND_EMAIL	= (1 << 0L);
	public static final int SEND_SMS	= (1 << 1L);

	private Integer postOpActions;

	public Integer getPostOpActions() {
		return postOpActions;
	}

	public void setPostOpActions(final Integer postOpActions) {
		this.postOpActions = postOpActions;
	}

	public boolean sendEmail() {
		return (postOpActions & SEND_EMAIL) != 0;
	}

	public boolean sendSMS() {
		return (postOpActions & SEND_SMS) != 0;
	}
}

