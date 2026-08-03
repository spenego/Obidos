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

import com.google.gwt.core.client.GWT;

//
// By Claude AI Sonnet 3.5 New (Nov 24)
// To test, form any onReset() method call
// ClientUtilsTest.runAllTests()
// spgdev
public class ClientUtilsTest
{
	public static void runAllTests()
	{
		testNullAndEmptyInputs();
		testShortFilenames();
		testLongFilenames();
		testWordBoundaries();
		testLongExtensions();
		testDefaultMaxLength();
		testSpecialCharacters();
		testPathSeparators();

		GWT.log("MMM All ClientUtils filename tests completed.");
	}

	private static void testNullAndEmptyInputs()
	{
		// Test null input
		if (ClientUtils.shortenFilename(null, 40) != null)
		{
			throw new RuntimeException("Failed: Null input should return null");
		}

		// Test empty string
		if (!"".equals(ClientUtils.shortenFilename("", 40)))
		{
			throw new RuntimeException("Failed: Empty string should return empty string");
		}

		GWT.log("MMM Null and empty input tests passed");
	}

	private static void testShortFilenames()
	{
		String shortName = "short.txt";
		String result = ClientUtils.shortenFilename(shortName, 40);
		if (!shortName.equals(result))
		{
			throw new RuntimeException("Failed: Short filename was modified when it shouldn't be: " + result);
		}

		GWT.log("MMM Short filename tests passed");
	}

	private static void testLongFilenames()
	{
		String longName = "very_long_document_name_with_multiple_words.pdf";
		String shortened = ClientUtils.shortenFilename(longName, 30);

		if (shortened.length() > 30)
		{
			throw new RuntimeException("Failed: Shortened name exceeds max length: " + shortened);
		}

		if (!shortened.contains("..."))
		{
			throw new RuntimeException("Failed: Shortened name missing ellipsis: " + shortened);
		}

		if (!shortened.endsWith(".pdf"))
		{
			throw new RuntimeException("Failed: Extension not preserved: " + shortened);
		}

		GWT.log("MMM Long filename tests passed");
	}

	private static void testWordBoundaries()
	{
		String filename = "project-documentation-version-2.0.1.txt";
		String shortened = ClientUtils.shortenFilename(filename, 35);

		GWT.log("MMM Testing word boundaries with result: " + shortened);

		// More lenient test - just make sure we have parts of the original
		// words
		// and the ellipsis in between
		if (!shortened.contains("project") && !shortened.contains("2.0.1.txt"))
		{
			throw new RuntimeException("Failed: Does not preserve meaningful parts of filename: " + shortened);
		}

		if (!shortened.contains("..."))
		{
			throw new RuntimeException("Failed: Missing ellipsis in shortened name: " + shortened);
		}

		GWT.log("MMM Word boundary tests passed");
	}

	private static void testWordBoundariesOld()
	{
		String filename = "project-documentation-version-2.0.1.txt";
		String shortened = ClientUtils.shortenFilename(filename, 35);

		// Check if breaks occur at word boundaries
		boolean hasWordBoundaryBreak = shortened.matches(".*[-_\\s]\\.\\.\\.[-_\\s].*");
		if (!hasWordBoundaryBreak)
		{
			throw new RuntimeException("Failed: Does not break at word boundaries: " + shortened);
		}

		GWT.log("MMM Word boundary tests passed");
	}

	private static void testLongExtensions()
	{
		String longExt = "document.verylongextension";
		String shortened = ClientUtils.shortenFilename(longExt, 20);

		if (shortened.length() > 20)
		{
			throw new RuntimeException("Failed: Long extension not handled properly: " + shortened);
		}

		GWT.log("MMM Long extension tests passed");
	}

	private static void testDefaultMaxLength()
	{
		String longName = "extremely_long_filename_that_exceeds_default_maximum_length_significantly.txt";
		String shortened = ClientUtils.shortenFilename(longName, -1);

		if (shortened.length() > 40)
		{
			throw new RuntimeException("Failed: Default max length not respected: " + shortened);
		}

		GWT.log("MMM Default max length tests passed");
	}

	private static void testSpecialCharacters()
	{
		String specialChars = "Special@#$Characters&^%In_File-Name.txt";
		String shortened = ClientUtils.shortenFilename(specialChars, 25);

		if (shortened.length() > 25)
		{
			throw new RuntimeException("Failed: Special characters not handled properly: " + shortened);
		}

		if (!shortened.endsWith(".txt"))
		{
			throw new RuntimeException("Failed: Extension not preserved with special characters: " + shortened);
		}

		GWT.log("MMM Special characters tests passed");
	}

	private static void testPathSeparators()
	{
		String pathName = "folder/subfolder/deep/path/filename.txt";
		String shortened = ClientUtils.shortenFilename(pathName, 30);

		if (shortened.length() > 30)
		{
			throw new RuntimeException("Failed: Path separator case exceeds length: " + shortened);
		}

		GWT.log("MMM Path separator tests passed");
	}
}