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
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.spenego.Obidos.shared.ObidosConstants;

public class ObidosAdhocItemRow extends Row
{
	private ObidosTextBox fieldNameTextBox;
	private ObidosTextBox valueTextBox;
	private ObidosIntegerTextBox displayOrderTextBox;
	private Button iconButton;
	private HandlerRegistration clickHandler;

	@UiConstructor
	public ObidosAdhocItemRow(IconType icon)
	{
		super();
		makeRow(icon);
		
		/*
		 	<Row>
				 <FlowPanel>
					 <FieldnameTextBox/>
				 </FlowPanel>
				 <FlowPanel>
					 <ValueTextBox/>
				 </FlowPanel>
				 <FlowPanel>
					 <DisplayOrder/>
				 </FlowPanel>
				 <FlowPanel>
					 <IconButton/>
				 </FlowPanel>
		 	</Row>
		 */
		
	}

	private void makeRow(IconType iconType)
	{
		// Field name
		FlowPanel fp1 = new FlowPanel();
		fp1.addStyleName("col-sm-offset-2 col-sm-3");
		fieldNameTextBox = new ObidosTextBox();
		fieldNameTextBox.setPlaceholder("Field Name");
		fieldNameTextBox.setMaxLength(ObidosConstants.FIELD_NAME_LENGTH);
		fp1.add(fieldNameTextBox);
		add(fp1);

		// Value
		FlowPanel fp2 = new FlowPanel();
		fp2.addStyleName("col-sm-3");
		valueTextBox = new ObidosTextBox();
		valueTextBox.setPlaceholder("Field Value");
		fp2.add(valueTextBox);
		add(fp2);

		// Display order
		FlowPanel fp3 = new FlowPanel();
		fp3.addStyleName("col-sm-1");
		displayOrderTextBox = new ObidosIntegerTextBox();
		displayOrderTextBox.setPlaceholder("Display Order");
		fp3.add(displayOrderTextBox);
		add(fp3);
		
		// icon button
		FlowPanel fp4 = new FlowPanel();
		fp4.addStyleName("col-sm-1");
		iconButton = new Button();
		iconButton.setType(ButtonType.INFO);
		iconButton.setIcon(iconType);
		fp4.add(iconButton);
		add(fp4);
		getElement().getStyle().setMarginBottom(4, Unit.PX);
	}

	public ObidosTextBox getFieldNameTextBox()
	{
		return fieldNameTextBox;
	}

	public void setFieldNameTextBox(ObidosTextBox fieldNameTextBox)
	{
		this.fieldNameTextBox = fieldNameTextBox;
	}

	public ObidosTextBox getValueTextBox()
	{
		return valueTextBox;
	}

	public void setValueTextBox(ObidosTextBox valueTextBox)
	{
		this.valueTextBox = valueTextBox;
	}

	public ObidosIntegerTextBox getDisplayOrderTextBox()
	{
		return displayOrderTextBox;
	}

	public void setDisplayOrderTextBox(ObidosIntegerTextBox displayOrderTextBox)
	{
		this.displayOrderTextBox = displayOrderTextBox;
	}

	public Button getIconButton()
	{
		return iconButton;
	}

	public void setIconButton(Button iconButton)
	{
		this.iconButton = iconButton;
	}

	public HandlerRegistration getClickHandler()
	{
		return clickHandler;
	}

	public void setClickHandler(HandlerRegistration clickHandler)
	{
		this.clickHandler = clickHandler;
	}

}
