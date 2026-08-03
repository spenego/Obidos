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

package com.spenego.Obidos.client.application.widgets;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonDismiss;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class ObidosPreModal extends Modal
{
	private HTML html = new HTML();
	private String title;
	private ModalBody modalBody = new ModalBody();
	private ModalFooter modalFooter = new ModalFooter();
	int width = (Window.getClientWidth() - 500);
	int height = (Window.getClientHeight() - 500);
	
	public ObidosPreModal(final String title)
	{
		super();
//		setWidth(width +"px");
//		setHeight(height + "px");
		setTitle(title);
		setClosable(true);
		setFade(true);
		setDataKeyboard(true);
//		modalBody.getElement().getStyle().setProperty("height", "80vh");
//		modalBody.getElement().getStyle().setProperty("overflowY","auto");
		add(modalBody);
		add(modalFooter);
		Button closeButton = new Button("Close");
		closeButton.setType(ButtonType.PRIMARY);
		closeButton.setDataDismiss(ButtonDismiss.MODAL);
		modalFooter.add(closeButton);
	}
	
	public void showModal(String text)
	{

//		int width = (Window.getClientWidth() - 500);
//		int height = (Window.getClientHeight() - 500);
		int width = (Window.getClientWidth() - 10);
		int height = (Window.getClientHeight() - 10);
		GWT.log("wxh: " + width + "x" + height);
		/*
		html.setHTML("<pre style='width: " + width + "px; height: " + height + "px; overflow: scroll;'>" + text
				+ "</pre>");
				*/
		html.setHTML("<pre>" + text + "</pre>");
		modalBody.add(html);
		show();
	}
}
