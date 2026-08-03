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

import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;

/**
 * 
 * @author spgdev@spenego.com - Jun 30, 2024
 * with the help from Claude Sonnet 3.5
 */
/*
 * The length is restricted to 16 characters (including the '+' sign) based on
 * the E.164 standard for international phone numbers. Here's the reasoning:
 * 
 * 1. E.164 specifies that the maximum length of an international phone number
 * is 15 digits. 2. We add 1 more character to account for the '+' sign at the
 * beginning.
 * 
 * So, 15 digits + 1 '+' sign = 16 characters total.
 * 
 * This limit ensures that the phone number adheres to the E.164 standard, which
 * is widely used for international phone number formatting. It accommodates
 * even the longest valid international phone numbers while preventing entry of
 * excessively long, invalid numbers.
 * 
 * If you need to adjust this for any reason, perhaps to allow for certain
 * country-specific formats, we can modify the MAX_LENGTH constant in the code.
 */

public class E164PhoneTextBox extends TextBox
{
	private static final RegExp PHONE_REGEX = RegExp.compile("^\\d{1,15}$");
	private static final int MAX_LENGTH = 15; // Up to 15 digits

	public E164PhoneTextBox()
	{
		super();
		setPlaceholder("Enter phone number");
		addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				String currentValue = getValue();
				char charCode = event.getCharCode();
				if (currentValue.length() >= MAX_LENGTH && charCode != '\b')
				{
					((TextBox) event.getSource()).cancelKey();
				} else if (!Character.isDigit(charCode) && charCode != '\b')
				{
					((TextBox) event.getSource()).cancelKey();
				}
			}
		});
	}

	@Override
	public void setValue(String value, boolean fireEvents)
	{
		if (value == null || value.isEmpty())
		{
			super.setValue("", fireEvents);
		} else
		{
			super.setValue(value.replaceAll("\\D", ""), fireEvents);
		}
	}

	public boolean isValidPhoneNumber()
	{
		return isValidPhoneNumber(getValue());
	}

	private boolean isValidPhoneNumber(String value)
	{
		return PHONE_REGEX.test(value);
	}
}