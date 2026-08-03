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

package com.spenego.Obidos.client.util;

import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.user.client.Random;

/**
 * Client side image filters
 * @author spgdev@spenego.com - Oct 20, 2019
 *
 */
public class ImageFilters
{
	public static void grayScalePixels(CanvasPixelArray array)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);
			// calculate monochrome luminance
			int gray = (int) ((0.3 * r) + (0.59 * g) + (0.11 * b));
			array.set(i, gray);
			array.set(i + 1, gray);
			array.set(i + 2, gray);

		}
	}

	public static void negativePixels(CanvasPixelArray array)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);

			array.set(i, 255 - r);
			array.set(i + 1, 255 - g);
			array.set(i + 2, 255 - b);
		}
	}

	// adjust is between 0 and 100
	public static void sepiaPixels(CanvasPixelArray array, int adjust)
	{
		adjust = adjust / 100;

		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);
			r = (int) Math.min(255, (r * (1 - (0.607 * adjust))) + (g * (0.769 * adjust)) + (b * (0.189 * adjust)));
			g = (int) Math.min(255, (r * (0.349 * adjust)) + (g * (1 - (0.314 * adjust))) + (b * (0.168 * adjust)));
			b = (int) Math.min(255, (r * (0.272 * adjust)) + (g * (0.534 * adjust)) + (b * (1 - (0.869 * adjust))));
			array.set(i, r);
			array.set(i + 1, g);
			array.set(i + 2, b);
		}
	}
// adjust is from 1 - 100
	public static void noisePixels(CanvasPixelArray array, int adjust)
	{
		if (adjust < 0)
		{
			adjust = 1;
		}
		if (adjust > 100)
		{
			adjust = 100;
		}
		adjust = (int) (Math.abs(adjust) * 2.55);

		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);

			int low = adjust * -1;
			int high = adjust;
			int rand = Random.nextInt(high - low) + low;

			array.set(i, r + rand);
			array.set(i + 1, g + rand);
			array.set(i + 2, b + rand);
		}
	}
	// for filters,look at:
	// camanjs demo
	// http://zaak.github.io/CamanJS-demo/

	// adjust is -100 to 100
	// < 0 will darken > 0 will brighten
	public static void brightenPixels(CanvasPixelArray array, int adjust)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);
			array.set(i, r + adjust);
			array.set(i + 1, g + adjust);
			array.set(i + 2, b + adjust);
		}
	}
	
	public static void solarizePixels(CanvasPixelArray array)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);
			
			array.set(i,  r > 127 ? 255 - r : r);
			array.set(i + 1,  g > 127 ? 255 - g : g);
			array.set(i + 2,  b > 127 ? 255 - b : b);
		}
	}
	
	public static void redPixels(CanvasPixelArray array)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			
			array.set(i,  r);
			array.set(i + 1,  0);
			array.set(i + 2,  0);
		}
	}
	public static void greenPixels(CanvasPixelArray array)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int g = array.get(i + 1);
			
			array.set(i,  0);
			array.set(i + 1,  g);
			array.set(i + 2,  0);
		}
	}
	public static void bluePixels(CanvasPixelArray array)
	{
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int b = array.get(i + 2);
			
			array.set(i,  0);
			array.set(i + 1,  0);
			array.set(i + 2,  b);
		}
	}



}
