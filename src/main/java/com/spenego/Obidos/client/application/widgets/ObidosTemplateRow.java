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
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.shared.ObidosConstants;

/**
 * 
 * @author spgdev@spenego.com - Jun 8, 2019
 *
 */
public class ObidosTemplateRow extends Row
{
	private TextBox fieldNameTextBox;
	private ObidosIntegerTextBox displayOrderTextBox;
	private Button iconButton;
	private String bottomMargin;
	private String topMargin;
	private HandlerRegistration handler;
	
	public ObidosTemplateRow()
	{
		super();
		makeRow(IconType.PLUS);
	}
		/*
		 	<Row>
		 		FormLabel
		 		<FormGroup>
		 			<FlowPanel>
		 				<InputGroup>
		 					TextBox
		 					InputGroupAddon
		 					TextBox
		 				</InputGroup>
					</FlowPanel>
		 			
		 			<FlowPanel>
		 				IconButton
		 			</FlowPanel>
		 		</FormGroup>
		 	</Row>
		 		
		 */

	private void makeRow(IconType icon)
	{
		// FormGroup
		FormGroup formGroup = new FormGroup();
		add(formGroup);
		
		// Field Name
		FlowPanel flowPanel = new FlowPanel();
//		flowPanel.getElement().getStyle().setProperty("paddingRight", "1px");
		formGroup.add(flowPanel);
        flowPanel.addStyleName("col-sm-offset-4 col-sm-3");
        fieldNameTextBox = new ObidosTextBox();
		fieldNameTextBox.getElement().getStyle().setProperty("fontWeight", "bold");
        fieldNameTextBox.setMaxLength(ObidosConstants.FIELD_NAME_LENGTH);
        flowPanel.add(fieldNameTextBox);
        fieldNameTextBox.setPlaceholder(ObidosMessages.LANG.nameOfField());
        
        // Display Order
        FlowPanel doFlowPanel = new FlowPanel();
//		doFlowPanel.getElement().getStyle().setProperty("paddingLeft", "1px");
//		doFlowPanel.getElement().getStyle().setProperty("paddingRight", "1px");
        formGroup.add(doFlowPanel);
        doFlowPanel.addStyleName("col-sm-1");
		displayOrderTextBox = new ObidosIntegerTextBox();
		doFlowPanel.add(displayOrderTextBox);

		// + - icon
		FlowPanel iconFlowPanel = new FlowPanel();
		add(iconFlowPanel);
		formGroup.add(iconFlowPanel);
		iconFlowPanel.addStyleName("col-sm-1");
		iconButton = new Button();
		iconFlowPanel.add(iconButton);
		iconButton.setType(ButtonType.INFO);
		iconButton.setIcon(icon);
	}
	
	public void saveClickHandler(HandlerRegistration handler)
	{
		this.handler = handler;
	}
	
	public HandlerRegistration getClickHandler()
	{
		return this.handler;
	}
	
	public void setIcon(IconType icon)
	{
		iconButton.setIcon(icon);
	}

	
	@UiConstructor
	public ObidosTemplateRow(IconType icon)
	{
		super();
		//this.bottomMargin = bottomMargin;
		
//		getElement().getStyle().setProperty("marginBottom", bottomMargin);
		makeRow(icon);
	}

	
	public void setTopMargin(String topMargin)
	{

		getElement().getStyle().setProperty("marginTop", topMargin);
	}

	public TextBox getFieldNameTextBox()
	{
		return fieldNameTextBox;
	}

	public ObidosIntegerTextBox getDisplayOrderTextBox()
	{
		return displayOrderTextBox;
	}

	public Button getIconButton()
	{
		return iconButton;
	}

	public String getBottomMargin()
	{
		return bottomMargin;
	}

	public String getTopMargin()
	{
		return topMargin;
	}

	public void setBottomMargin(String bottomMargin)
	{
		this.bottomMargin = bottomMargin;
	}

	public void clear()
	{
		this.fieldNameTextBox.clear();
	}
	
	public String fieldValue()
	{
		return getFieldNameTextBox().getValue();
	}
	
	public int displayOrderValue()
	{
		Integer x =  getDisplayOrderTextBox().getValue();
		if (x == null)
		{
			return 0;
		}
		return x;
	}
}
