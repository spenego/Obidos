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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxParam;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.muquit.gpw.Gpw;
import com.muquit.gpw.GpwPasswordModifier;
import com.spenego.Obidos.server.diceware.Dice;
import com.spenego.Obidos.server.services.impl.GenPassServiceImpl;
import com.spenego.Obidos.server.utils.CertificateUtils;
import com.spenego.Obidos.server.utils.CountryCodeUtil;
import com.spenego.Obidos.server.utils.KeyStoreUtils;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.FernetDTO;
import com.spenego.Obidos.shared.dto.FernetPayload;
import com.spenego.Obidos.shared.exceptions.ObidosCryptoException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.spenego.strongpass.StrengthChecker;
import com.spenego.strongpass.StrongPassEntropy;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TestMisc
{

	private final static Logger logger = LoggerFactory.getLogger(TestMisc.class);

	@Before
	public void setup()
	{
		/* no implementation */ }

	public static String prettyfyJson(final String uglyJsonStr) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(uglyJsonStr));
	}

	// https://stackoverflow.com/questions/10174898/how-to-check-whether-a-given-string-is-valid-json-in-java
	public static boolean validateJSON(final String jsonStr)
	{
		try
		{
			new Gson().fromJson(jsonStr, Object.class);
			return true;
		} catch (com.google.gson.JsonSyntaxException ex)
		{
			throw new ServerSideException("Invalid JSON string: " + ex.getMessage());
		}
	}

	@Test
	public void testValidateJson()
	{
		String jsonStr = "{\n" + "    \"id\":525,\n"
				+ "    \"contact\": \"Please contact support at xxx-xxx-xxxx if you have any questions.\",\n"
				+ "    \"footer\": \"Example Inc., Copyright 2018, All Rights Reserved 42 Maple Ave Exton, PA 19341 USA\",\n"
				+ "    \"hello\": \"Hi {{name}}\",\n"
				+ "    \"html_message\": \"{{owner_name}} has deleted an secured item shared you with you.. After login, please click on the notification bell for details.\",\n"
				+ "    \"product_name\": \"Spenego Obidos Digital Privacy Management and Sharing Software\",\n"
				+ "    \"product_url\": \"https://spenego.com\",\n"
				+ "    \"subject\": \"A secured shared Item is deleted by owner\",\n"
				+ "    \"text_message\": \"{{owner_name}} has deleted an secured item shared you with you.. After login, please click on the notification bell for details.\",\n"
				+ "    \"title\": \"A shared item is deleted by the owner\"\n" + "}\n" + "";
		boolean rc = validateJSON(jsonStr);
		logger.info("rc: " + rc);

	}
	
	@Test
	public void testObjectToJSON() {
		FernetPayload pl = new FernetPayload();
		pl.setUserId(100L);
		pl.setDocumentId(200L);
		Gson gson = new Gson();
		String jsonStr = gson.toJson(pl);
		logger.info(jsonStr);

		// convert jsonStr to FernetPayload object
		pl = gson.fromJson(jsonStr, FernetPayload.class);
		logger.info("userid: " + pl.getUserId());
		logger.info("document id: " + pl.getDocumentId());
	}
	
	// https://stackoverflow.com/questions/3508050/how-can-i-get-a-list-of-trusted-root-certificates-in-java
/*
	private void printCert(final String cert)
	{
		String lines[] = cert.split("\\r?\\n");
		for (String line : lines)
		{
			if (line.contains("modulus:"))
			{
				String [] temp = line.split(":");
				print("'" + temp[0] + "'");
				String m = temp[1];
				int c = 0;
				for (int i = 0; i < m.length(); i++)
				{
					c = c + 1;
					System.out.printf("%s",m.charAt(i));
					if (c == 20)
					{
						print("\n");
						c = 0;
					}
				}

				print(line);
			}
		}
	} */
	
	private static void print(String msg) { System.out.println(msg); }

	/*
	private void printCertInfo(final X509Certificate cert) throws InvalidNameException
	{
		print("Subject Name ---------------");
		// Ref: https://stackoverflow.com/questions/7933468/parsing-the-cn-out-of-a-certificate-dn
		LdapName ln = new LdapName(cert.getSubjectDN().getName());
		for(Rdn rdn : ln.getRdns())
		{
		    if(rdn.getType().equalsIgnoreCase("CN"))
		    {
		    	print("  Common Name: " + rdn.getValue());
		    }
		}
		print("Issuer Name ----------------");
		ln = new LdapName(cert.getIssuerDN().getName());
		for(Rdn rdn : ln.getRdns())
		{
		    if(rdn.getType().equalsIgnoreCase("C"))
		    {
		    	print("      Country: " + rdn.getValue());
		    }
		    if(rdn.getType().equalsIgnoreCase("O"))
		    {
		    	print(" Organization: " + rdn.getValue());
		    }
		    if(rdn.getType().equalsIgnoreCase("CN"))
		    {
		    	print("  Common Name: " + rdn.getValue());
		    }
		}
		print(" Serial Number: " + cert.getSerialNumber().toString(16));
		print("       Version: " + cert.getVersion());
		print(" Signature Algorithm: " + cert.getSigAlgName() + " (" + cert.getSigAlgOID() + ")");
		print(" Parameter: " + cert.getSigAlgParams());
		print(" Not Valid Before: " + cert.getNotBefore());
		print("  Not Valid After: " + cert.getNotAfter());
		print("Public Key Info ---------------");
		print("Algorithm: " + cert.getPublicKey().getAlgorithm());
	}
	*/
	
	@Test
	public void testConvertToPEM() throws ObidosCryptoException
	{
		String chainFile = "/Users/spgdev/certs/obidos.dev/fullchain.pem";
		List<Certificate> certs = CertificateUtils.parseChainFile(chainFile);
		for (Certificate cert : certs)
		{
			String pem = CertificateUtils.convertToPEM(cert);
			print(pem);
		}
		
	}
	
	@Test
	public void testParseCertificateChain() throws ObidosCryptoException 
	{
		String chainFile = "/Users/spgdev/certs/chain/spenego.com_chain.pem";
		chainFile = "/Users/spgdev/certs/obidos.dev/fullchain.pem";
		List<Certificate> certs = CertificateUtils.parseChainFile(chainFile);
		logger.info("size: " + certs.size());
		int c = 0;
		for (Certificate cert : certs)
		{
			c = c + 1;
			//printCertInfo((X509Certificate) cert);
			//print("===============================");
			print("Certificate " + c);
			CertificateInfoDTO dto = CertificateUtils.getCertificateInfo(cert);
			print(dto.getSubjectNameHeading() + " ===");
			print(" Common Name: " + dto.getSubjectCommonName() + "\n");

			print(dto.getIssuerNameHeading() + " ===");
			if (dto.getIssuerCountry() != null)
			{
				print("      Country: " + dto.getIssuerCountry());
			}
			print(" Organization: " + dto.getIssuerOrganization());
			print("  Common Name: " + dto.getIssuerCommonName());
			
			print(dto.getValidityHeading() + "==");
			print(" Not Before: " + dto.getNotBeforeDate());
			print(" Not After: " + dto.getNotAfterDate());
			
			List<String> ipaAltNames = dto.getIpaAltNames();
			List<String> dnsAltNames = dto.getDnsAltNames();
			if (!ipaAltNames.isEmpty() || !dnsAltNames.isEmpty())
			{
				print(dto.getSubjectAltNamesHeading() + "==");
			}
			for (String an : ipaAltNames)
			{
				print(" IP Name: " + an);
			}
			for (String an : dnsAltNames)
			{
				print(" DNS Name: " + an);
			}
			
			print(dto.getPublicKeyInfoHeading() + "==");
			print(" Algorithm: " + dto.getPublicKeyAlgorithm());
			
			print(dto.getMiscHeading() + "==");
			print(" Serial Number: "  + dto.getSerialNumber());
			print(" Signature Algorithm " + dto.getSignatureAlgorithmString());
			print(" Version: " + dto.getVersion());
			print("\n");

		}
	}
	
	@Test
	public void testParseNginxConf() throws IOException
	{
		String file = "/Users/spgdev/nginx_conf/nginx/sites-available/default";
		NgxConfig conf = NgxConfig.read(file);
		NgxDumper dumper = new NgxDumper(conf);
		dumper.dump(System.out);
		NgxParam ssl_cert = conf.findParam("server", "ssl_certificate");
		logger.info("listen: " + ssl_cert.getValue());
		NgxParam key = conf.findParam("server", "ssl_certificate_key");
		logger.info("key: " + key.getValue());
	}
	
	@Test
	public void testAdLdapCertificateInstalled() throws ObidosCryptoException 
	{
		boolean installed = CertificateUtils.adLdapCertificateInstalled();
		logger.info("Certificate installed: " + installed);
	}
	
	@Test
	public void testKeyStoreAPI() throws ObidosCryptoException, IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException
	{
		String keyStorePath = "/Users/spgdev/safe/cacerts";
		String alias = "obidos_ldap_cert1";
		String storePass = ServerUtils.getCacertsKeyStorePassword();
		boolean rc = KeyStoreUtils.entryExists(alias, keyStorePath, storePass);
		logger.info("Alias " + alias + " exists? " + rc);

		KeyStoreUtils.deleteEntry(alias, keyStorePath, storePass);
		rc = KeyStoreUtils.entryExists(alias, keyStorePath, storePass);
		logger.info("Alias " + alias + " exists? " + rc);

		String chainFile = "/Users/spgdev/certs/obidos.dev/fullchain.pem";
		List<Certificate> certs = CertificateUtils.parseChainFile(chainFile);
		for (Certificate cert : certs)
		{
			String cn = CertificateUtils.getCommonName(cert);
			alias = ObidosConstants.AD_LDAP_ALIAS_STARTS_WITH + "-" + cn;
			boolean exists = KeyStoreUtils.entryExists(alias, keyStorePath, storePass);
			logger.info("Alias " + alias + " exists? " + exists);
			if (!exists)
			{
				logger.info("Import alias " + alias);
				KeyStoreUtils.importTrustedCertificate(cert, alias, keyStorePath, storePass);
			}
		}
	}
	
	@Test
	public void testCreateFernet() throws IOException
	{
		String path = ServerUtils.getFernetRotatingPropertiesFilePath();
			
		FernetDTO dto = ServerUtils.getFernetDTO(path, 5);
		logger.info("Key: "+ dto.getKey());
	}
	
	@Test
	public void testTripLongStringEllipsisIntheMiddle()
	{
		String filename = "a_file_with_a_very_very_very_very_very_very_very_very_very_very_longname.jpg";
		filename = "123456789012345xy23456.jpg";
		logger.info("Length: " + filename.length());
		int maxLen = 25;
		if (filename.length() <= maxLen)
		{
			logger.info("No need to trim");
			return;
		}
		// put ... after 12 characters and take 12 characters from the end
		String firstPart = filename.substring(0, 12);
		int x = filename.length() - 12;
		logger.info("x: " + x);
		String lastPart = filename.substring(x, filename.length());
		logger.info("First part: " + firstPart);
		logger.info("Last part: " + lastPart);
		String shortFilename = firstPart + "..." + lastPart;
		logger.info("Short filename: " + shortFilename);
	}
	
	@Test
	public void testDocumentDirectorySize() 
	{
		String dir = "/usr/local/spenego/obidos/DataStore";
		long size = FileUtils.sizeOfDirectory(new File(dir));
		logger.info("Document dir size; " + size + " bytes");
	}

	/*
	private static  Map<String, String> queryStringMap(final String uris) throws URISyntaxException {
		// Parse Query Sting and create a map of key value
		final Map<String, String> myMap = new HashMap<String, String>();
		final List<NameValuePair> params = URLEncodedUtils.parse(new URI(uris), StandardCharsets.UTF_8);

		for (NameValuePair param : params) {
			logger.info(param.getName() + " : " + param.getValue());
			myMap.put(param.getName().toLowerCase(), param.getValue());
		}
		return myMap;
	}
	*/

	// https://github.com/google/google-authenticator/wiki/Key-Uri-Format
	@Test
	public void testParseTotpUri() throws URISyntaxException {
		final String otpAuthUri = "otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30";
		String uriStr = otpAuthUri.replaceAll(" ", "%20");
		uriStr = uriStr.trim();
		URI uri;
		uri = new URI(uriStr);
		logger.info("uri: "+ uri);
		logger.info("scheme: "+ uri.getScheme());
		logger.info("host: " + uri.getHost());
		logger.info("path: " + uri.getPath());
		logger.info("query:" + uri.getQuery());
		String path = uri.getPath();
		String email = null;
		if (path != null)
		{
			String[] s = path.split(":");
			logger.info("Length: "+ s.length);
			if (s.length == 2)
			{
				email = s[1];
			}
		}
		logger.info("Account: "+ email);
		logger.info("raw path: " + uri.getRawPath());
		logger.info("Authority: " + uri.getAuthority());
	}
	
	@Test
	public void testByteToDisplaySize()
	{
		for (long i=100; i < 9999999999L; i++)
		{
			String s = FileUtils.byteCountToDisplaySize(i);
			logger.info("i= " + i + " " + s);
		}
	}
	
	@Test
	public void testConvertSecsToHrMinSecs()
	{
		long duration = 4000;
	    int hours = (int) duration / 3600;
	    int remainder = (int) duration - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;
	    String timeString = String.format("%02d:%02d:%02d", hours, mins, secs);
		logger.info(timeString);
	}
	
	@Test
	public void testReadExecStdout() throws ExecuteException, IOException
	{
		try (ByteArrayOutputStream stdout = new ByteArrayOutputStream()) {
			PumpStreamHandler psh = new PumpStreamHandler(stdout);
			// https://stackoverflow.com/questions/46937202/unable-to-get-output-from-apache-commons-exec
			// someone posted that he does not get output unless
			// he runs like below. But this solution is not portable
			/*
			CommandLine cl = new CommandLine("/bin/sh");
			String cmd = "/tmp/hello.sh";
			cl.addArguments("-c");
			cl.addArguments("'" + cmd + "'", false);

			DefaultExecutor exec = new DefaultExecutor();
			exec.setStreamHandler(psh);
			exec.execute(cl);
			String path = stdout.toString();
			System.out.println("Path from Cli: '" + path + "'");
			*/

			// default way
			// https://stackoverflow.com/questions/6295866/how-can-i-capture-the-output-of-a-command-as-a-string-with-commons-exec
			String cmd = "/tmp/hello.sh";
			final CommandLine cl = CommandLine.parse(cmd);
			final DefaultExecutor exec = DefaultExecutor.builder().get();
			exec.setStreamHandler(psh);
			exec.execute(cl);
			String path = stdout.toString(StandardCharsets.UTF_8).trim();
			logger.info("Path before stripping new line: '" + path + "'");
			path = path.replace("\n", "").replace("\r", "");
			logger.info("Path after stripping new line: '" + path + "'");
			// remove new line
			if (! ServerUtils.fileExist(path)) {
				logger.error("File " + path + " does not exist");
			}
		}
	}
	
	private static ArrayList<Integer> normalizeDisplayOrderList(ArrayList<Integer> displayOrderList) {
		Collections.sort(displayOrderList);
		int sz = displayOrderList.size();
		ArrayList<Integer> normalizedList = new ArrayList<>();
		normalizedList.add(displayOrderList.get(0));
		for (int i = 1; i < sz; i++) {
			int f1 = normalizedList.get(i-1);
			int f2 = displayOrderList.get(i);
			if (f2 > (f1 + 1))
			{
				normalizedList.add(f1 + 1);
			}
			else
			{
				normalizedList.add(f2);
			}
		}

		return normalizedList;
	}

	@Test
	public void testNormalizeEditTemplateDisplayOrder() 
	{
		/*
		 	Say if the display orders are say
		 	1
		 	3
		 	5
		 	6
		 	7
		 	9
		 	10

		 	The order will be normalized as:
		 	1
		 	2
		 	3
		 	4
		 	5
		 	6
		 	7
		 */
		/*
		  First sort the orders in ascending order, 
		  then if f1+1 > f2
		    f2 = f1 + 1
		   so 1 + 1 > 3, so f2 will be 1 = 1 = 2
		   and so on
		 */
		ArrayList<Integer> orderList = new ArrayList<>();
		orderList.add(10);
		orderList.add(1);
		orderList.add(5);
		orderList.add(7);
		orderList.add(3);
		orderList.add(56000);
		orderList.add(9);
		orderList.add(6);
		orderList.add(100);
		Collections.sort(orderList);
		
		for (Integer order:orderList) {
			logger.info(order + "");
		}
		ArrayList<Integer> normalizedList = normalizeDisplayOrderList(orderList);
		/*
		normalizedList.add(orderList.get(0));
		for (int i = 1; i < sz; i++)
		{
			int f1 = normalizedList.get(i-1);
			int f2 = orderList.get(i);
			if (f2 > (f1 + 1))
			{
				normalizedList.add(f1 + 1);
			}
			else
			{
				normalizedList.add(f2);
			}
		}
		*/
		for (Integer no: normalizedList)
		{
			logger.info("NL: " + no);
		}
		if (orderList.equals(normalizedList) == true)
		{
			logger.info("display order list do not need normalization");
		}
		else
		{
			logger.info("display order list Normalized");
		}

	}
	
	@Test
	public void testCompareDisplayOrderList()
	{
		ArrayList<Integer> orderList = new ArrayList<>();
		orderList.add(1);
		orderList.add(3);
		orderList.add(4);
		orderList.add(2);
		Collections.sort(orderList);
		ArrayList<Integer> normalizedList = normalizeDisplayOrderList(orderList);
		
		if (orderList.equals(normalizedList) == true)
		{
			logger.info("display order list do not need normalization");
		}
	}
	
	// Test outbound sms
	@Test
	public void testTwilioSMS() throws ApiException 
	{
		final String accountSID= System.getenv("TWILIO_ACCOUNT_SID");
		final String authToken = System.getenv("TWILIO_AUTH_TOKEN");
		final String toPhoneNumber = System.getenv("MY_PHONE_NUMBER");
		final String fromPhoneNumber = System.getenv("TWILIO_PHONE_NUMBER");

		// Initialize Twilio
		Twilio.init(accountSID, authToken);
		
		Message message = Message.creator(
				new PhoneNumber(toPhoneNumber),
				new PhoneNumber(fromPhoneNumber),
				"hello, world!").create();
		// id will be returned if message is sent successfully
		logger.info(message.getSid());
	}
	
	@Test
	public void testGetCountrycodes()
	{
		List <String> countryAndCodeList = CountryCodeUtil.getCountryCode();
		for (String line : countryAndCodeList)
		{
			logger.info(line);
		}
		logger.info("Total: " + countryAndCodeList.size());
		List<CountryCodeDTO> countryCodes = 	CountryCodeUtil.getCountryCodes();
		for (CountryCodeDTO cc : countryCodes)
		{
			logger.info(cc.getExampleNumber());
		}
		String number = "+1215-345-6789";
		String e164Number = CountryCodeUtil.formatToE164(number);
		logger.info("number: " + number + "," + e164Number);
//		number = "+4512344567"; // fails
		number = "+4533210772";
		e164Number = CountryCodeUtil.formatToE164(number, "DK");
		logger.info("number: " + number + "," + e164Number);
	}
	
	@Test
	public void testFormatToE164()
	{
		try
		{
			String number = "+1215 272 5534";
			String e164 = CountryCodeUtil.formatToE164(number, null);
			logger.info("Number: " + number + ", e164: "+ e164);
			String countryCode = CountryCodeUtil.getCountryNameAndCode(e164);
			logger.info(countryCode);
			
		}catch (Exception e)
		{
			logger.info("Exception: "+ e);
			
		}
	}
	
	@Test
	public void testCountryRegion()
	{
	    final Map<String, String> countryCodeMap = new HashMap<>();
		for (String iso : Locale.getISOCountries()) {
			Locale locale = new Locale("", iso);
			String country = locale.getDisplayCountry();
			logger.info(country + "," + iso);
			countryCodeMap.put(locale.getDisplayCountry().toLowerCase(), iso);
		}
	}
	
	private static List<String> examplePhoneNumbers() {
		List<String> numbers = new ArrayList<String>();
		numbers.add("+1 (215) 272-5533");
		numbers.add("+1215 272 5534");
		numbers.add("+45 35 35 35 35");
		numbers.add("+2015550123");
		numbers.add("+971-50-1234567");
		numbers.add("+91-1234 5678 21");
		numbers.add("+44 20 7123 4567");
		numbers.add("+880 8626 904");
		return numbers;
	}
	
	@Test
	public void testGetCounteryNameAndCodeFromE164number()
	{
		List<String> numbers = examplePhoneNumbers();
		for (String number : numbers)
		{
			try
			{
				String countryNameAndCode = CountryCodeUtil.getCountryNameAndCode(number);
				logger.info("Number: " + number + " => " + countryNameAndCode);
			} catch (Exception e)
			{
				logger.info("Exception caught: " + e);
				
			}
		}
	}
	
	@Test
	public void testListExampleNumbers()
	{
		List<CountryCodeDTO> countryCodes = CountryCodeUtil.getCountryCodes();
		for (CountryCodeDTO cc : countryCodes)
		{
			String number = "+" + cc.getExampleNumber();
			try
			{
				String countryNameAndCode = CountryCodeUtil.getCountryNameAndCode(number);
				logger.info("Number: " + number + " => " + countryNameAndCode);
			} catch (Exception e)
			{
				//logger.info("Exception caught: " + e);
				
			}

		}
	}
	@Test
	public void testPhoneNumberFormat()
	{
		List<String> numbers = examplePhoneNumbers();
		try
		{
			for (String number: numbers)
			{
				String nationalFormat = CountryCodeUtil.formatNumber(number, CountryCodeUtil.FORMAT_NATIONAL);
				logger.info("National format:" + number + "=>" + nationalFormat);
			}
		} catch (Exception e)
		{
			logger.error("Exception: " + e);
		}
	}
	
	@Test
	public void testPhoneNumberDetails()
	{
		List<String> numbers = examplePhoneNumbers();
		try
		{
			for (String number: numbers)
			{
				CountryCodeDTO cc = CountryCodeUtil.getPhoneNumberInfo(number);
				logger.info("             Country: " + number + " : " + cc.getCountryName());
				logger.info("       Number region: " + number + " : " + cc.getNumberRegion());
				logger.info("           Dial code: " + number + " : " + cc.getDialCode());
				logger.info("     National format: " + number + " : " + cc.getNumberNationalFormat());
				logger.info("International format: " + number + " : " + cc.getNumberInternationFormat());
				logger.info("      Example number: " + number + " : " + cc.getExampleNumber());
				logger.info("   Without dial code: " + number + " : " + cc.getNumberWithoutDialCode());
				logger.info("");
			}
		} catch (Exception e)
		{
			logger.error("Exception: " + e);
		}
	}
	
	@Test
	public void testCountryCodesMap()
	{
		Map<String, CountryCodeDTO> map = CountryCodeUtil.getCountryCodesMap();
		for (Map.Entry<String, CountryCodeDTO> entry : map.entrySet())
		{
		    String key = entry.getKey();
		    CountryCodeDTO cc = entry.getValue();
		    logger.info(key);
		    logger.info("  Country: " + cc.getCountryName());
		    logger.info("  Example: " + cc.getExampleNumber());
		}
	}
	
	@Test
	public void testGenPDFFromAsciidocFiles() throws IOException
	{
	}
	
	@Test
	public void testGenPasswordWithPassay()
	{
		CharacterRule alphabets = new CharacterRule(EnglishCharacterData.Alphabetical);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);
        CharacterRule special = new CharacterRule(EnglishCharacterData.Special);
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String password = passwordGenerator.generatePassword(16, alphabets, digits, special);
        logger.info("Password: " + password);
	}
	
	@Test
	public void testGeneratePasswordDiceware()
	{
		for (int i = 0; i < 6; i++)
		{
			Dice dice = new Dice();
			int face = dice.roll();
			logger.info("Face: " + face);
		}
	}
	
	@Test
	public void testGpw()
	{
		Gpw gpw = new Gpw();
		int npw = 4;
		int pwlen = 8;
		List<String> passwords = gpw.generatePasswords(npw, pwlen);
		for (String password: passwords)
		{
			logger.info(password);
			String modified = GpwPasswordModifier.modifyPassword(password, true, true, true);
			logger.info(" modified all: " + modified);

			modified = GpwPasswordModifier.modifyPassword(password, true, false, false);
			logger.info(" modified caps: " + modified);
		}
	}
	
	@Test
	public void testBooleans()
	{
		boolean capitalize = false;
		boolean numerals = false;
		boolean symbols = false;
		if (capitalize   != false  ||
				numerals != false ||
				symbols  != false)
		{
			logger.info("OK to modify");
		}
		else
		{
			logger.info("No need to modify");
		}
		logger.info("capitalize: " + capitalize);
		logger.info("numerals: " + numerals);
		logger.info("symbols: " + symbols);

	}
	@Test
	public void testPassCrackingTime()
	{
		float entropy = 33.0F;
		Long guessesPerSec = 1000000000L;
		guessesPerSec = 1000000L;
		String ct = GenPassServiceImpl.getPasswordCrackingTime(entropy, guessesPerSec);
		logger.info(ct);
	}
	
	@Test
	public void testRawEntropy()
	{
		String pass = "Troub4dor&3";
		float rawEntropy = ServerUtils.calculateRawEntropy(pass);
		logger.info("Raw entropy of " + pass + " is " + rawEntropy);
	}
	
	@Test
	public void testEntropyAdjustment()
	{
		String pass = "eaddantorryarduc";
		StrengthChecker sc = new StrengthChecker(true);
		StrongPassEntropy e = sc.calculateEntropies(pass);
		logger.info("raw: " + e.getRawEntropy());
	}
	@Test
	public void testCalculateEntropy()
	{
		String pass = "eaddantorryarduc";
		pass="password";
		StrengthChecker sc = new StrengthChecker();
		sc.calculateEntropy(pass);
	}

	
	@After
	public void tearDown()
	{
		/* no implementation */ }
}
