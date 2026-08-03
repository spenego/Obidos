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

import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.uibinder.client.UiConstructor;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.shared.ObidosConstants;

/**
 * @author spgdev@spenego.com - Jun 1, 2019
 * <InputGroup>
 * 	<InputGroupAddon>
 * 	<TextBox>
 * </InputGroup>
 *
 */
public class ObidosInputGroup extends InputGroup
{
	private TextBox textBox;
	private boolean readOnly;
	private boolean isSecure;
	private String placeHolder;
	private InputGroupAddon inputGroupAddon;
	
	@UiConstructor
	public ObidosInputGroup(boolean readOnly, boolean isSecure, String placeHolder)
	{
		this.readOnly = readOnly;
		this.isSecure = isSecure;
		this.placeHolder = placeHolder;
		

		InputGroupAddon inputGroupAddon = new InputGroupAddon();
		this.inputGroupAddon = inputGroupAddon;
		if (!isSecure)
		{
			inputGroupAddon.setIcon(IconType.UNLOCK);
			inputGroupAddon.setIconColor("red");
			inputGroupAddon.setTitle(ObidosMessages.LANG.notEncrypted());
		}
		else
		{
			inputGroupAddon.setIcon(IconType.LOCK);
			inputGroupAddon.setIconColor("green");
			inputGroupAddon.setTitle(ObidosMessages.LANG.encrypted());
		}
		inputGroupAddon.setIconPosition(IconPosition.RIGHT);
		// The following shortens the field for some reason.
		// Therefore, do not set it here, caller can set it after adding
		// the widget to FlowPanel. I think the issue is that the widget has
		// to be rendered first and then set the width.
//		inputGroupAddon.setWidth(ObidosConstants.INPUT_GROUP_ADDON_WIDTH);
		

		textBox = new TextBox();
		textBox.setMaxLength(ObidosConstants.TEXTFIELD_MAX_LENGTH);
		if (placeHolder != null && placeHolder.length() > 0)
		{
			textBox.setPlaceholder(placeHolder);
		}
		textBox.setReadOnly(readOnly);
		if (readOnly)
		{
			textBox.getElement().getStyle().setProperty("color", "#000000");
			textBox.getElement().getStyle().setProperty("fontWeight", "bold");
		}
		else
		{
			
		}
		
		add(inputGroupAddon);
		add(textBox);
	}

	public TextBox getTextBox()
	{
		return textBox;
	}
	
	public void setText(String value)
	{
		textBox.setValue(value);
	}
	
	public String getText()
	{
		return textBox.getValue();
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
		this.textBox.setReadOnly(readOnly);
	}

	public boolean isSecure()
	{
		return isSecure;
	}

	public String getPlaceHolder()
	{
		return placeHolder;
	}
	
	public void setIcon(IconType icon)
	{
		this.inputGroupAddon.setIcon(icon);
	}
	
	public InputGroupAddon getInputGroupAddon()
	{
		return inputGroupAddon;
	}
	
	public void animateIcon(boolean animate)
	{
		this.inputGroupAddon.setIconSpin(animate);
	}
}