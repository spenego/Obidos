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

package test.com.spenego.obidos;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.sun.jna.Platform;

public class TestLicense

{    
	private final static Logger logger = LoggerFactory .getLogger(TestLicense.class);


	@Before
	public void setUp() throws Exception
	{
		String libraryPath = null;
		if (Platform.isMac())
		{
		    // MacOS
		    libraryPath = "/usr/local/lib/libsodium.dylib";
		    logger.info("Library path in Mac: " + libraryPath);
		}
		else if (Platform.isWindows())
		{
		    // Windows
		    libraryPath = "C:/libsodium/libsodium.dll";
		    logger.info("Library path in Windows: " + libraryPath);
		}
		else
		{
		    // Linux
		    libraryPath = "/usr/local/lib/libsodium.so";
		    logger.info("Library path: " + libraryPath);
		}

		logger.info("loading libsodium...");
		SodiumLibrary.setLibraryPath(libraryPath);
		// To check the native library is actually loaded, print the version of 
		// native sodium library
		String v = SodiumLibrary.libsodiumVersionString();
		System.out.println("libsodium version: " + v);
	}

	@After
	public void tearDown() throws Exception  { /* no implementation */ }
	
	// parse license yaml string as key value pair
	// it can be used to simple validation of base64 license to see if it
	// is actually a Obidos license
	@Test
	public void testParseLicenseString()
	{
		String base64License = "LS0tCnByb2R1Y3ROYW1lOiAiU3BlbmVnbyBPYmlkb3MiCmNvbXBhbnlOYW1lOiAic3BlbmVnby5jb20iCmNvbXBhbnlFbWFpbDogInN1cHBvcnRAc3BlbmVnby5jb20iCm1heFVzZXJzOiAxMDAKc2lnbmluZ0Vwb2NoOiAxNTYxNjg4Mjg1CmxpY2Vuc2VUZXJtczogIlRoaXMgbGljZW5zZSBpcyBpc3N1ZWQgdG8gc3BlbmVnby5jb20gKHN1cHBvcnRAc3BlbmVnby5jb20pLiBJdCBpc1wKICBcIG5vdCBhbGxvd2VkIHRvIHVzZSBvciB0cmFuc2ZlciB0aGlzIGxpY2Vuc2UgdG8gYW55IG90aGVyIG9yZ2FuaXphdGlvbi4iCm5vbmNlOiAiSDNCb2dpK25XbFc1cjFPSzg3UlZQWElxU3RBRHpWUEJSYmkyODcrUlJQdz0iCnNpZ25hdHVyZTogIkJBYldLNjBwQlVoKzRCcmlUampWT0RpMXFWWUtRYzRwNVhZcVBiN01ZZFZlM3owUjMvTEpRWk9ONFBnMVB3TVdDNlJ2RTJVT1dkanhHMHN5dHQyWkJRPT0iCg==";
		byte[] licenseBytes = ServerUtils.decodeFromBase64(base64License);
		String yamlLicense = ServerUtils.bytesToString(licenseBytes);
//		System.out.println(yamlLicense);
		String[] lines = yamlLicense.split("\n");
		int score = 0;
		logger.info("Print license line by line");
		for (String line : lines) {
			if (line.equals("---"))
			{
				score++;
				continue;
			}
			String[] parts = line.split(":");
			int len = parts.length;
			if (len == 2)
			{
				logger.info("Key= " + parts[0]);
				String key = parts[0].toLowerCase();
				if (key.equals("nonce"))
				{
					score = score + 10;
				}
				if (key.equals("signature"))
				{
					score = score + 10;
				}
				if (key.equals("productname"))
				{
					score++;
				}
				if (key.equals("companayname"))
				{
					score++;
				}
				if (key.equals("signingepoch"))
				{
					score = score + 10;
				}
						
			}
			logger.info("Parts length:" + len);
		    System.out.println(line);
		}
		logger.info("Score: " + score);
	}

	@Test
	public void testValidateLicenseSignature()
	{
		String publicKeyBase64 = "QzfayX41K2/Scrd5g6wFvvqpK18pJrImc5/mQ8U5VG8=";
		String base64License = "LS0tCnByb2R1Y3ROYW1lOiAiU3BlbmVnbyBPYmlkb3MiCmNvbXBhbnlOYW1lOiAic3BlbmVnby5jb20iCmNvbXBhbnlFbWFpbDogInN1cHBvcnRAc3BlbmVnby5jb20iCm1heFVzZXJzOiAxMDAKc2lnbmluZ0Vwb2NoOiAxNTYxNjg4Mjg1CmxpY2Vuc2VUZXJtczogIlRoaXMgbGljZW5zZSBpcyBpc3N1ZWQgdG8gc3BlbmVnby5jb20gKHN1cHBvcnRAc3BlbmVnby5jb20pLiBJdCBpc1wKICBcIG5vdCBhbGxvd2VkIHRvIHVzZSBvciB0cmFuc2ZlciB0aGlzIGxpY2Vuc2UgdG8gYW55IG90aGVyIG9yZ2FuaXphdGlvbi4iCm5vbmNlOiAiSDNCb2dpK25XbFc1cjFPSzg3UlZQWElxU3RBRHpWUEJSYmkyODcrUlJQdz0iCnNpZ25hdHVyZTogIkJBYldLNjBwQlVoKzRCcmlUampWT0RpMXFWWUtRYzRwNVhZcVBiN01ZZFZlM3owUjMvTEpRWk9ONFBnMVB3TVdDNlJ2RTJVT1dkanhHMHN5dHQyWkJRPT0iCg==";
		
		byte[] publicKey = ServerUtils.decodeFromBase64(publicKeyBase64);
		ServerUtils.validateLicense(base64License, publicKey);
		logger.info("License signature verified");
	}
}
