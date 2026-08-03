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

package com.spenego.Obidos.server.security;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import com.spenego.Obidos.server.actions.LdapConfigActions;
import com.spenego.Obidos.server.operations.LdapConfigOperations;
import com.spenego.Obidos.server.operations.UserOperations;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.shared.dto.LdapDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 * Authenticate a user from Active Directory or LDAP server
 *
 * @author spgdev@spenego.com - Feb 19, 2017
 * Updated for StartTLS #111, Mar-3-2025
 */
@Component
public final class LDAPSecurity
{
	private static final Logger logger = LoggerFactory.getLogger(LDAPSecurity.class);

	@Autowired
	protected UserOperations userOperations;

	@Autowired
	protected LdapConfigOperations ldapConfigOperations;

	@Autowired
	protected LdapConfigActions ldapConfigActions;

	boolean check(String username, String password, String ldapAuthName)
	{
		return authenticate(username, password, ldapAuthName);
	}

	private static void checkNullParams(final String param, final String name) throws ServerSideException
	{
		if (param == null)
		{
			throw new ServerSideException(name + " can not be empty. AD/LDAP Authentication failed.");
		}
	}

	public static void testConnection(final LdapDTO ldapDTO) throws ServerSideException
	{
	    int connectTimeout = 5000;  // 5 seconds
	    int readTimeout = 5000;     // 5 seconds

		final Hashtable<String, Object> env = new Hashtable<>();

		// Basic LDAP connection properties
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapDTO.getLdapuri());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		// Add credentials if provided
		if (ldapDTO.getBindDn() != null)
		{
			env.put(Context.SECURITY_PRINCIPAL, ldapDTO.getBindDn());
		}
		if (ldapDTO.getBindPass() != null)
		{
			env.put(Context.SECURITY_CREDENTIALS, ldapDTO.getBindPass());
		}

		// Add timeout settings
	    // Get custom timeouts if provided in the DTO
	    Integer dtoConnectTimeout = ldapDTO.getCONNECT_TIMEOUT();
	    Integer dtoReadTimeout = ldapDTO.getREAD_TIMEOUT();
	    
		if (dtoConnectTimeout != null && dtoConnectTimeout > 0)
		{
			connectTimeout = dtoConnectTimeout;
		}

		if (dtoReadTimeout != null && dtoReadTimeout > 0)
		{
			readTimeout = dtoReadTimeout;
		}

		final String connectTimeoutStr = String.valueOf(connectTimeout);
		final String readTimeoutStr = String.valueOf(readTimeout);
		env.put("com.sun.jndi.ldap.connect.timeout", connectTimeoutStr); // 5 seconds connection timeout
		env.put("com.sun.jndi.ldap.read.timeout", readTimeoutStr); // 5 seconds read timeout

		// Handle certificate validation
		System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
        try
        {
            DirContext ctx = new InitialDirContext(env);
            ctx.close();
            logger.info(() -> "Connected to LDAP server successfully");
        } catch (final Throwable e)
        {
            throw new ServerSideException("Could not connect to LDAP server: " + e.getMessage());
        }

		try
		{
			logger.info(() -> "Attempting to connect to LDAP server: " + ldapDTO.getLdapuri());
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
			logger.info(() -> "Connected to LDAP server successfully");
		} catch (javax.naming.CommunicationException ce)
		{
			logger.error(() -> "LDAP connection timeout or network error: " + ce.getMessage());
			throw new ServerSideException(
					"Could not connect to LDAP server: Connection timeout or network error. Please check server address and firewall settings and make sure certificate is in the keystore");
		} catch (javax.naming.AuthenticationException ae)
		{
			logger.error(() -> "LDAP authentication failed: " + ae.getMessage());
			throw new ServerSideException(
					"Could not connect to LDAP server: Authentication failed. Please check bind DN and password.");
		} catch (Throwable e)
		{
			logger.error(() -> "LDAP connection failed: " + e.getMessage(), e);
			throw new ServerSideException("Could not connect to LDAP server: " + e.getMessage());
		}
	}
	
	public static void testAuthenticate(final LdapDTO dto, String username, String password) throws ServerSideException
	{
	    // Set default timeouts
	    int connectTimeout = 5000;  // 5 seconds
	    int readTimeout = 5000;     // 5 seconds
	    
	    if (dto == null)
	    {
	        throw new ServerSideException("LDAP DTO is empty. AD/LDAP Authentication failed");
	    }
	    
	    String ldapUri = dto.getLdapuri();
	    String baseDN = dto.getBaseDn();
	    String bindDN = dto.getBindDn();
	    String bindPass = dto.getBindPass();
	    String authAttribute = dto.getAuthAttr();
	    Boolean useStartTLS = dto.getStartTls();
	    
	    // Get custom timeouts if provided in the DTO
	    Integer dtoConnectTimeout = dto.getCONNECT_TIMEOUT();
	    Integer dtoReadTimeout = dto.getREAD_TIMEOUT();
	    
		if (dtoConnectTimeout != null && dtoConnectTimeout > 0)
		{
			connectTimeout = dtoConnectTimeout;
		}

		if (dtoReadTimeout != null && dtoReadTimeout > 0)
		{
			readTimeout = dtoReadTimeout;
		}

	    if (useStartTLS == null)
	    {
	        useStartTLS = Boolean.FALSE;
	    }

	    checkNullParams(ldapUri, "AD/LDAP URI");
	    checkNullParams(baseDN, "AD/DLAP Base DN");
	    checkNullParams(authAttribute, "AD/LDAP Authentication attribue");

	    // handle UPN style Bind DN. UP Style Bind DN is like
	    // anything@ad.domain. We will replace the anything part with the
	    // username
	    // and bind password will be changed to password
	    if (bindDN != null && bindDN.contains("@") && bindPass == null)
	    {
	        String parts[] = bindDN.split("\\@");
	        if (parts.length == 2)
	        {
	            logger.info(() -> "AD style domain found");
	            bindDN = username + "@" + parts[1];
	            bindPass = password;
	        }
	    }
	    
	    try {
	    	final int ct = connectTimeout;
	    	final int rt = readTimeout;
	    	final String bindDNF = bindDN;
	        logger.info(() -> "Testing authentication with timeouts - connect: " + ct + "ms, read: " + rt + "ms");
	        logger.info(()-> "base DN: " + baseDN);
	        logger.info(()-> "bind DN: " + bindDNF);
	        logger.info(()-> "atttr DN: " + authAttribute);
	        boolean authenticated = authenticate(ldapUri, baseDN, bindDN, bindPass, authAttribute, username, password, useStartTLS, connectTimeout, readTimeout);
	        if (!authenticated) {
	            logger.error(() -> "Authentication failed (returned false)");
	            throw new ServerSideException("Authentication failed");
	        }

	    } catch (ServerSideException e) {
	        logger.error(() -> "Authentication test failed: " + e.getMessage());
	        throw e;
	    }
	}	



	public boolean authenticate(String username, String password, String ldapAuthName)
	{
		LdapDTO dto = ldapConfigActions.get(ldapAuthName);
		String ldapUri = dto.getLdapuri();
		String baseDN = dto.getBaseDn();
		String bindDN = dto.getBindDn();
		String bindPass = dto.getBindPass();
		String authAttribute = dto.getAuthAttr();
		Boolean useStartTLS = dto.getStartTls();
		if (useStartTLS == null)
		{
			useStartTLS = Boolean.FALSE;
		}

		checkNullParams(ldapUri, "AD/LDAP URI");
		checkNullParams(baseDN, "AD/DLAP Base DN");
		checkNullParams(authAttribute, "AD/LDAP Authentication attribue");
		// handle UPN style Bind DN. UP Style Bind DN is like
		// anything@ad.domain. We will replace the anything part with the
		// username
		// and bind password will be changed to password
		if (bindDN != null && bindDN.contains("@") && bindPass == null)
		{
			String parts[] = bindDN.split("\\@");
			if (parts.length == 2)
			{
				logger.info(() -> "AD style domain found");
				bindDN = username + "@" + parts[1];
				bindPass = password;
			}
		}
		return authenticate(ldapUri, baseDN, bindDN, bindPass, authAttribute, username, password, useStartTLS,
				dto.getCONNECT_TIMEOUT(), dto.getREAD_TIMEOUT());
	}

	public static boolean authenticate(final String ldapURI, final String baseDN, final String bindDN,
		final String bindPass, final String authAttribute, final String username, final String password,
		final Boolean useStartTLS,
		final int connectTimeout,
		final int readTimeout) throws ServerSideException
    {
        Map<String, Object> baseEnv = new Hashtable<>();
        baseEnv.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(connectTimeout));
        baseEnv.put("com.sun.jndi.ldap.read.timeout", String.valueOf(readTimeout));
        baseEnv.put("java.naming.ldap.attributes.binary", "");

        // The following works for ldap and ldaps
        if (!useStartTLS)
        {
        	final LdapContextSource cs = new LdapContextSource();
        	cs.setBaseEnvironmentProperties(baseEnv);
        	cs.setUrl(ldapURI);
        	// Don't set the base here if it's causing issues
        	// cs.setBase(baseDN); 

        	if (bindDN != null)
        	    cs.setUserDn(bindDN);
        	if (bindPass != null)
        	    cs.setPassword(bindPass);

        	cs.setReferral("follow");
        	cs.afterPropertiesSet();

        	LdapTemplate ldapTemplate = new LdapTemplate(cs);
        	ldapTemplate.setIgnorePartialResultException(true);
        	ldapTemplate.setDefaultTimeLimit(connectTimeout);

        	try
        	{
        	    logger.info(() -> "Authenticating username: " + username + " attribute: " + authAttribute + " in base DN: " + baseDN);
        	    
        	    // Explicitly provide the baseDN in the authenticate call
        	    boolean authenticated = ldapTemplate.authenticate(baseDN, "(" + authAttribute + "=" + username + ")", password);
        	    if (!authenticated) {
        	        logger.error(() -> "Authentication failed (returned false)");
        	        throw new ServerSideException("Authentication failed");
        	    }
        	    return true;
        	}
        	catch (Exception e)
        	{
        	    logger.warn(() -> "LDAP Authentication failed: " + e.getMessage());
        	    throw new ServerSideException("Could not authenticate user: " + e.getMessage());
        	}
        }

        // StartTLS implementation with dynamic protocol selection and fallback
        try {
        	// Create a permissive SSL context for testing
        	// If the following block is uncommented and used, authentication
        	// will bypass certificate. It should be used for testing only.
        	/*
                TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
                };
        	 */

        	// Create context with minimal settings
        	Hashtable<String, Object> env = new Hashtable<>();
        	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        	env.put(Context.PROVIDER_URL, ldapURI);
        	env.put(Context.SECURITY_AUTHENTICATION, "simple");
        	env.put(Context.SECURITY_PRINCIPAL, bindDN);
        	env.put(Context.SECURITY_CREDENTIALS, bindPass);
        	env.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(connectTimeout));
        	env.put("com.sun.jndi.ldap.read.timeout", String.valueOf(readTimeout));

        	// Create initial context
        	LdapContext ctx = new InitialLdapContext(env, null);

        	// Start TLS
        	StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
        	tls.setHostnameVerifier((hostname, session) -> true);  // Skip hostname verification for now

        	// Dynamically get all supported TLS protocols from the JVM
        	SSLContext tempContext = SSLContext.getDefault();
        	List<String> supportedProtocols = Arrays.stream(tempContext.createSSLEngine().getSupportedProtocols())
        			.filter(p -> p.startsWith("TLS"))  // Only use TLS protocols
        			.sorted(Comparator.reverseOrder()) // Sort by version, newest first
        			.collect(Collectors.toList());

        	// Make sure we always try TLSv1 as last resort
        	if (!supportedProtocols.contains("TLSv1")) {
        		supportedProtocols.add("TLSv1");
        	}

        	logger.info(() -> "Available TLS protocols: " + String.join(", ", supportedProtocols));

        	boolean negotiated = false;
        	String negotiatedProtocol = null;

        	for (String tlsVersion : supportedProtocols) {
        		try {
        			logger.info(() -> "Attempting StartTLS with " + tlsVersion);
        			SSLContext sslContext = SSLContext.getInstance(tlsVersion);
        			// Look above, use to test without cert vertification
        			// use it for testing only
        			//                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        			sslContext.init(null, null, new java.security.SecureRandom());
        			tls.negotiate(sslContext.getSocketFactory());
        			negotiatedProtocol = tlsVersion;
        			logger.info(() -> "Successfully negotiated StartTLS with " + tlsVersion);
        			negotiated = true;
        			break;
        		} catch (Exception e) {
        			logger.info(() -> "Failed to negotiate StartTLS with " + tlsVersion + ": " + e.getMessage());
        			// Continue to next protocol version
        		}
        	}

        	if (!negotiated) {
        		throw new Exception("Failed to negotiate StartTLS with any available protocol version");
        	}

        	// Now perform the authentication
        	String searchFilter = "(" + authAttribute + "=" + username + ")";
        	SearchControls searchControls = new SearchControls();
        	searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        	NamingEnumeration<SearchResult> results = ctx.search(baseDN, searchFilter, searchControls);

        	if (results.hasMore()) {
        		SearchResult result = results.next();
        		String userDN = result.getNameInNamespace();

        		try {
        			// Create new context with user credentials to verify password
        			Hashtable<String, Object> authEnv = new Hashtable<>(env);
        			authEnv.put(Context.SECURITY_PRINCIPAL, userDN);
        			authEnv.put(Context.SECURITY_CREDENTIALS, password);

        			// new InitialLdapContext(authEnv, null);

        			// Save the successful protocol for future reference (could be stored in config/cache)
        			final String np = negotiatedProtocol;
        			logger.info(() -> "Authentication successful using protocol: " + np);

        			// Close the original context
        			ctx.close();
        			return true;
        		} catch (Exception e) {
        			logger.warn(() -> "LDAP StartTLS Authentication failed - invalid credentials: " + e.getMessage());
        			ctx.close();
        			return false;
        		}
        	}

        	logger.warn(() -> "LDAP StartTLS Authentication failed - user not found: " + username);
        	ctx.close();
        	return false;
        } catch (Exception e) {
        	logger.warn(() -> "LDAP StartTLS Authentication failed: " + e.getMessage());
        	throw new ServerSideException("Could not authenticate user with StartTLS: " + e.getMessage());
        }
    }
}

	
