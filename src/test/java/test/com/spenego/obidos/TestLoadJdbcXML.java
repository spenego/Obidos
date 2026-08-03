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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spenego.Obidos.server.utils.ServerUtils;

public class TestLoadJdbcXML
{
	private final static Logger logger = LoggerFactory .getLogger(TestLoadJdbcXML.class);
	@Before
	public void setUp()  { /* no implementation */ }
	
	@Test 
	public void testLoadJdbcXML()
	{
		String xmlFile = ServerUtils.getJdbcXMLFilePath();
		logger.info("XML file: "+ xmlFile);
		String key = "driverClassName";
		XMLConfiguration xmlConfig;
		try
		{
			xmlConfig = ServerUtils.loadXMLFile(xmlFile);
			/*String str = ServerUtils.dumpObjectToString(xmlConfig);
			logger.info("Dump: "+  str);*/
			logger.info("driver class: " + xmlConfig.getString(key));
		} catch (ConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Exception: " + e.getMessage());
		}
	}
	@After
	public void tearDown() throws Exception { /* no implementation */ }
}
