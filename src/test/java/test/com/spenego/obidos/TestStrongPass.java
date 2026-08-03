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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.Rule;
import org.passay.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.utils.PassModifier;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.GenPassDTO;
import com.spenego.strongpass.EntropyCalculator;
import com.spenego.strongpass.StrengthChecker;

/**
 * @author spgdev@spenego.com - Sep 2, 2019
 */
public class TestStrongPass
{
	private final Logger logger = LoggerFactory.getLogger(TestStrongPass.class);
	private StrengthChecker strengthChecker;
	@Before
	public void setUp() throws Exception
	{
		strengthChecker = new StrengthChecker(true);
	}
	
      @Test
      public void testEntropy24()
      {
          final String pass = "pa$$W0rd";
          // strength checker check dictionary
          // entropy calculate repeat weakened
          float entropy = strengthChecker.calculateEntropy(pass);
          assertEquals(entropy, 2.0, 0.0F);
          boolean strong = strengthChecker.isStrong(pass);
          assertEquals(strong, false);
      }
      
      @Test
      public void testEntropy() 
      {
    	  // According to xkcd, entropy is 28 bits
    	  String pass = "Tr0ub4dor&3";
    	  float entropy = strengthChecker.calculateEntropy(pass);
    	  logger.info("Entropy of " + pass + ": " +  +  entropy);
    	  
    	  // According to xkcd, entropy is 44 bits
    	  pass = "correcthorsebatterystaple";
    	   entropy = strengthChecker.calculateEntropy(pass);
    	  logger.info("Entropy of " + pass + ": " +  +  entropy);
      }
      
    @Test
	public void testPasswordComplexity()
	{
		final String password = "this is a test";
		String propfile = ObidosConstants.PASSAY_MESSAGE_PROPERTIES_FILE;
		logger.info("Prop file: " + propfile);
		Properties props = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try
		{
			props.load(classLoader.getResourceAsStream(propfile));
		} catch (Exception e)
		{
//			throw new ServerSideException("Could not load properties file: " + propfile);
			logger.info("Exception: "+ e.getMessage());
		}
		MessageResolver resolver = new PropertiesMessageResolver(props);

		List<Rule> rules = new ArrayList<>();
		// Length
		rules.add(new LengthRule(5, 512));

		// number of lower case characters
		rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));

		// number of upper case characters
		rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));

		// number of numbers
		rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));

		// number of special characters
		rules.add(new CharacterRule(EnglishCharacterData.Special, 1));
		PasswordValidator validator = new PasswordValidator(resolver, rules);
		PasswordData passwordData = new PasswordData(password);
		RuleResult result = validator.validate(passwordData);
		if (!result.isValid())
		{
			logger.info("Password failed validation");
			logger.info("Password length: " + password.length());
			List<String> msgList = validator.getMessages(result);
			logger.info("Exception: " + msgList);
			String msg = String.join("\n", msgList);
			logger.info("Message: " + msg);
			// throw new ServerSideException(msg);
		}
	}
    
    @Test
    public void testPassDoesNotContain()
    {
    	final String pass = "foobar@gmail.com";
    	final String contains = "bar@gmail.com";
    	try
    	{
    		ServerUtils.validatePassDoesNotContain(pass, contains);
    	}catch(Exception e)
    	{
    		logger.info("Exception: " + e.getMessage());
    	}
    	
    	try
    	{
    		ServerUtils.validatePassDoesNotContain(pass, "bar");
    	} catch(Exception e)
    	{
    		logger.info("Exception: " + e.getMessage());
    	}
    }
    
    @Test
    public void testPassStrength()
    {
    	User user = new User();
    	user.setUsername("msnow");
    	user.setFullname("Mary Snow");
    	user.setEmail1("msnow@example.com");
    	int minEntropy = 18;
    	boolean useDictionary = true;
    	int minWordLength = 4;
		String[] extraDictionaryWords = new String[0];
    	
		StrengthChecker strengthChecker = new StrengthChecker(minEntropy, useDictionary, minWordLength,
					extraDictionaryWords);
		final String pass = "this is a test";
		float entropy = strengthChecker.calculateEntropy(pass);
		logger.info("Entroy of '" + pass + "' is: " + entropy);
    }
    
    @Test
    public void testPassModification()
    {
        String[] passwords = {
			"mypassword",
			"securepassword1234",
			"ALLCAPS",
			"this is a test that is test too",
			"withsymbols!@#",
			"MixedWithNumb3rs",
			"verylongpasswordwithoutanynumeralsorsymbols",
			"wordpass",
			"helloworld"
        };    	
        boolean capitalize = true;
        boolean numerals = true;
        boolean symbols = true;
        GenPassDTO allOptions = new GenPassDTO(capitalize, numerals, symbols, null, null, null);
        GenPassDTO onlyCapitalize = new GenPassDTO(capitalize, false, false, null, null, null);
        GenPassDTO onlyNumbers = new GenPassDTO(false, numerals, false, null, null, null);
        GenPassDTO onlySymbols = new GenPassDTO(false, false, symbols, null, null, null);
        for (String pass : passwords)
        {
        	String modifiedPass = PassModifier.modifyPassword(pass, allOptions);
        	logger.info("Orig: " + pass + " Modified all: " + modifiedPass);

        	modifiedPass = PassModifier.modifyPassword(pass, onlyNumbers);
        	logger.info("Orig: " + pass + " Modified numbers: " + modifiedPass);

        	modifiedPass = PassModifier.modifyPassword(pass, onlySymbols);
        	logger.info("Orig: " + pass + " Modified symbols: " + modifiedPass);
        	
        }
    }
    
    @Test
    public void testXkcdPasswords()
    {
    	String pass = "password";
    	float rawEntropy = EntropyCalculator.calculateRaw(pass);
    	logger.info("Raw entropy of " + pass + " is " + rawEntropy);
    	
    }
	
	@After
	public void tearDown() throws Exception { /* no implementation */ }
}
