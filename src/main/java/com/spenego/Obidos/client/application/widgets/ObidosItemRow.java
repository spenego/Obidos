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
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.InputGroupButton;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;

/**
 * used in edit/viewing an item
 * @author spgdev@spenego.com - Jun 16, 2019
 *
 */
public class ObidosItemRow extends ObidosRow
{
	private String labelText;
	private String fieldText;
	private boolean readOnly;
	private TextBox textBox;
	private boolean isSecure;
	private FormLabel formLabel;
	
	@UiConstructor
	public ObidosItemRow(boolean readOnly, String labelText, String fieldText, boolean isSecure)
	{
		super("2px");
		this.readOnly = readOnly;
		this.labelText = labelText;
		this.fieldText = fieldText;
		this.isSecure = isSecure;
		
		/*
		 	<Row>
		 		<FormLabeL/>
		 		<FlowPanel>
		 			<ObidosInputGroup/>
		 		</FlowPanel>
		 	</Row>
		 */
		makeRow();
	}
	
	private void makeRow()
	{
		ObidosMessages lang = ObidosMessages.LANG;
		// Label
		this.formLabel = new FormLabel();
		FormLabel label = this.formLabel;
		add(label);
		label.addStyleName("col-sm-4");
		label.setText(this.labelText);
		
		// InputGroup with lock/unlock icon
		FlowPanel fp = new FlowPanel();
		add(fp);
		fp.addStyleName("col-sm-4");
		String placeHolder = "";
		ObidosInputGroup oig = new ObidosInputGroup(this.readOnly, isSecure, placeHolder);
		this.textBox = oig.getTextBox();
		fp.add(oig);
		InputGroupAddon inputGroupAddon = oig.getInputGroupAddon();
		// Do not set it at the widget, it must be set after widget is rendered.
		// spgdev@spenego.com - Nov 27, 2024
		inputGroupAddon.setWidth(ObidosConstants.INPUT_GROUP_ADDON_WIDTH);

		// Issue #784. Copy text to clipboard button
		InputGroupButton igb = new InputGroupButton();
		Button copyButton = new Button();
		copyButton.setType(ButtonType.DEFAULT);
		copyButton.setIcon(IconType.COPY);
		copyButton.setTitle(lang.copy());
		copyButton.setDataLoadingText("✓");
		igb.add(copyButton);
		oig.add(igb);
		oig.setText(this.fieldText);
		
		copyButton.addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				int t = Integer.parseInt(ObidosMessages.LANG.copyTooltipTimerSchedule());
				String text = oig.getText();
				if (text != null && text.length() > 0)
				{
					ClientUtils.copyTextToClipboard(text);
					copyButton.state().loading();
					new Timer()
					{
						@Override
						public void run()
						{
						  copyButton.state().reset();
						}
					  }.schedule(t);

				}
			}
		});
	}
	
	public TextBox getTextBox()
	{
		return this.textBox;
	}
	
	public void setText(final String text)
	{
		if (text == null || text.length() == 0)
		{
			return;
		}
		this.textBox.setValue(text);
	}

	// Add support for #75
	public void setLabelText(final String labelText)
	{
		if (labelText == null || labelText.length() == 0)
		{
			return;
		}
		this.formLabel.setText(labelText);
	}
	
}
