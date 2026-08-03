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

/*
 * The Operations package is an abstraction layer between the Obidos Actions layer and the MyBatis persistance layer.
 * It is invoked from the Actions layer and it relies on the DAO/MyBatis.
 *
 * Layers are organized as: Services -> Actions -> Operations -> DAO/MyBatis -> Database
 *
 * @since 1.0
 * @author Mike Morgan
 * @version 1.0
 */
package com.spenego.Obidos.server.operations;
