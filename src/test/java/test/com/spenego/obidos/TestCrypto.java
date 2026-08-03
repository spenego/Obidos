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

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.Validator;
import com.spenego.Obidos.server.security.LibSodiumPasswordSecurity;
import com.spenego.Obidos.server.security.PasswordSecurity;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.FernetDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public class TestCrypto
{
    private final static Logger logger=LoggerFactory
            .getLogger(TestCrypto.class);

    @Before
    public void before() { /* Nothing to do */ }

    @Test
    public void testVerifyPassword()
    {
        logger.info("Verifying password hash");
        PasswordSecurity passwordSecurity=new LibSodiumPasswordSecurity();
        String password="test";
        String hashedPassword=passwordSecurity.hashPassword(password);
        System.out.println("test-> " + "'" + hashedPassword + "'" + "("
                + hashedPassword.length() + ")");
        boolean rc=passwordSecurity.verifyPassword(password,hashedPassword);
        assertEquals(true,rc);
        rc=passwordSecurity.verifyPassword("foo",hashedPassword);
        assertEquals(false,rc);
    }

    @Test
    public void testBase64()
    {
        String passPhrase="this is a test";
        byte[] p=passPhrase.getBytes();
        String base64=Base64.encodeBase64String(p);
        logger.info("base64: " + base64);
    }
    
    @Test
    public void testFernetKey()
    {
    	Key key = Key.generateKey();
    	String key1 = key.serialise();
    	logger.info(" Key: "+ key1);
    	key = new Key(key1);
    	String key2 = key.serialise();
    	logger.info("Key2: "+ key2);
    	assertEquals(key1, key2);
    }
    
    @Test
    public void testCreateFernetKeyFile() throws ServerSideException, IOException
    {
    	FernetDTO dto = ServerUtils.getFernetDTO("fernet_crypto.properties");
    	logger.info("Key: " + dto.getKey());
    }
    
    private Validator<String> validator = new StringValidator() { /* default impl*/  };
    
    @Test
    public void testFernetToken() throws ServerSideException, IOException, InterruptedException
    {

    	final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    	FernetDTO dto = ServerUtils.getFernetDTO("fernet_crypto.properties");
    	final Key fernetKey = new Key(dto.getKey());
    	final Token token = Token.generate(fernetKey, "This is our secret message");
    	logger.info("Token: " + token.serialise());
    	Instant now = Instant.now();
    	Instant timeStamp = token.getTimestamp();
    	logger.info("Timestamp: " + timeStamp);

    	Duration between = Duration.between(now, token.getTimestamp());
    	logger.info("Between: " + between);
    	Long seconds = between.getSeconds();
    	logger.info("Seconds: " + seconds);

    	final String result = token.validateAndDecrypt(fernetKey, validator);
    	logger.info("secret: "+ result);
    	
    	long tokenEpoch = token.getTimestamp().getEpochSecond();
    	logger.info("SLeeping...");
    	Instant backInstant = Instant.from(formatter.parse("1985-10-26T01:20:00-07:00"));
    	Long backEpoch = backInstant.getEpochSecond();
    	Date d = new Date(backEpoch);
    	logger.info("Back date: " + d.toString());
    	
    	Thread.sleep(1000);
    	logger.info("Back...");
    	long nowEpoch = Instant.now().getEpochSecond();
    	long diff = nowEpoch - tokenEpoch;
    	if (diff > 5)
    	{
    		logger.info("Token has expired");
    	}
    	logger.info("Diff: " + diff + " seconds");
    	// validate
    }

    @After
    public void after() { /* no-op */  }
}
