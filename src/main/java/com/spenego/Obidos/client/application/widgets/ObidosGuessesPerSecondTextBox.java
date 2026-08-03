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

import org.gwtbootstrap3.client.ui.LongBox;
import org.gwtbootstrap3.client.ui.base.HasPlaceholder;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * @author spgdev@spenego.com - Sep 12, 2024
 */
public class ObidosGuessesPerSecondTextBox extends LongBox implements HasPlaceholder
{
	private Long minValue;
	private Long maxValue;

	private NumberFormat displayFormat;
	private boolean isUpdating = false;

	// largest possible 15 digit number 999,999,999,999,999
	// It's also very close to the maximum value that can be stored in a
	// Java long, which is 9,223,372,036,854,775,807 (2^63 - 1)
	// I think it is large enough for guesses/sec
	private int maxDigits = 15;

	public ObidosGuessesPerSecondTextBox()
	{
		super();
		displayFormat = NumberFormat.getFormat("#,##0");

		addKeyUpHandler(event ->
		{
			if (!isUpdating)
			{
				formatInput();
			}
		});

		addValueChangeHandler(event ->
		{
			if (!isUpdating)
			{
				formatInput();
			}
		});
	}

	private void formatInput()
	{
		isUpdating = true;
		String currentText = getText();
		String cleanedText = currentText.replaceAll("[^0-9]", "");

		// Limit the number of digits
		if (cleanedText.length() > maxDigits)
		{
			cleanedText = cleanedText.substring(0, maxDigits);
		}

		Long value = parseValue(cleanedText);
		if (value != null)
		{
			String formattedValue = displayFormat.format(value);
			setText(formattedValue);
			setCursorPos(formattedValue.length());
		}
		isUpdating = false;
	}

	private Long parseValue(String text)
	{
		if (text.isEmpty())
		{
			return null;
		}
		try
		{
			return Long.parseLong(text);
		} catch (NumberFormatException e)
		{
			return null;
		}
	}

	@Override
	public Long getValue()
	{
		return parseValue(getText().replaceAll("[^0-9]", ""));
	}

	// Add methods for setting/getting default values, min/max values, etc.
	public void setDefaultValue(long defaultValue)
	{
		setValue(defaultValue);
	}

	public void setMinValue(long minValue)
	{
		this.minValue = minValue;
		validateCurrentValue();
	}

	public void setMaxValue(long maxValue)
	{
		this.maxValue = maxValue;
		validateCurrentValue();
	}

	private void validateCurrentValue()
	{
		Long currentValue = getValue();
		if (currentValue != null)
		{
			if (minValue != null && currentValue < minValue)
			{
				setValue(minValue);
			} else if (maxValue != null && currentValue > maxValue)
			{
				setValue(maxValue);
			}
		}
	}


	@Override
	public void setValue(Long value)
	{
		super.setValue(value);
		if (value != null)
		{
			getElement().setPropertyString("value", displayFormat.format(value));
		} else
		{
			getElement().setPropertyString("value", "");
		}
	}

	public void setMaxDigits(int maxDigits)
	{
		this.maxDigits = maxDigits;
	}

	public void setValueOd(Long value)
	{
		super.setValue(value);
		if (value != null)
		{
			getElement().setPropertyString("value", displayFormat.format(value));
		} else
		{
			getElement().setPropertyString("value", "");
		}
	}
}