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
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

// Show a dialog box with fixed width font
// Adapted from wisepersist file upload demo code
public class ObidosSourceCodeDialogBox extends DialogBox
{
	private HTML html = new HTML();

	public ObidosSourceCodeDialogBox(final String title)
	{
		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(true);
		// Set the dialog box's caption.
		setText(title);

		// Enable animation.
		setAnimationEnabled(true);

		// Enable glass background.
		setGlassEnabled(true);
		Button btnClose = new Button("Close ChangeLog");
		btnClose.setType(ButtonType.PRIMARY);
		btnClose.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				hide();
			}
		});
		VerticalPanel panel = new VerticalPanel();
		panel.add(html);
		panel.add(btnClose);
		panel.setCellHorizontalAlignment(btnClose, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(panel);
	}

	public void showSourceCode(String sourceCode)
	{
		int width = (Window.getClientWidth() - 500);
		int height = (Window.getClientHeight() - 500);
		html.setHTML("<pre style='width: " + width + "px; height: " + height + "px; overflow: scroll;'>" + sourceCode
				+ "</pre>");
		setPopupPositionAndShow(new PopupPanel.PositionCallback()
		{
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int left = (Window.getClientWidth() - offsetWidth) / 2;
				int top = (Window.getClientHeight() - offsetHeight) / 2;
				GWT.log("top: " + top);
				top = 150;
				setPopupPosition(left, top);
			}
		});
	}
}
