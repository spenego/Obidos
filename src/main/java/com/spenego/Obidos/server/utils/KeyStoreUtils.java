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

package com.spenego.Obidos.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import com.spenego.Obidos.shared.exceptions.ObidosCryptoException;

/**
 * A helper class for Java trusted KeyStore (cacerts) APIs. It is used to import
 * AD/LDAP certificates to keystore
 *
 * @author spgdev@spenego.com - Jul 4, 2020
 */
public class KeyStoreUtils {
	private KeyStoreUtils() { /* No instances needed */ }
	private static final Logger logger = LoggerFactory.getLogger(KeyStoreUtils.class);
	private static final String KEYSTORE_LOAD_ERROR = "Could not load keystore: ";
	private static final String KEYSTORE_UPDATE_ERROR = "Could not update keystore: ";

	private static ObidosCryptoException createException(String msg, Exception ex) {
		return new ObidosCryptoException(msg + ex.getMessage());
	}

	protected static KeyStore loadKeyStore(final String keyStorePath, final String storePass) throws ObidosCryptoException {
		try {
			String password = storePass;
			try(final FileInputStream is = new FileInputStream(keyStorePath)) {
				final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
				keystore.load(is, password.toCharArray());
				return keystore;
			}
		} catch (CertificateException e)     { throw createException("Invalid certificate in keystore: ", e);
		} catch (FileNotFoundException e)    { throw createException("Could not read keystore: ", e);
		} catch (Exception e)                { throw createException(KEYSTORE_LOAD_ERROR, e); }
	}

	private static void saveKeyStore(final KeyStore ks, final String keyStorePath, final String storePass) throws ObidosCryptoException {
		// looks deadly, does the API lock??
		try(FileOutputStream fos = new FileOutputStream(new File(keyStorePath))) {
			ks.store(fos, storePass.toCharArray());
		} catch (Exception e)    { throw createException(KEYSTORE_UPDATE_ERROR, e); }
	}

	public static boolean entryExists(final String alias, final String keyStorePath, final String storePass) throws ObidosCryptoException {
		try {
			KeyStore ks = loadKeyStore(keyStorePath, storePass);
			if (ks.containsAlias(alias)) {
				logger.info(() -> "Alias " + alias + " exists in keystore");
				return true;
			}
		} catch (KeyStoreException e) {
			throw new ObidosCryptoException("Could not search entry in keystore: " + e.getMessage());
		}
		return false;
	}

	public static void deleteEntry(final String alias, final String keyStorePath, final String storePass) throws ObidosCryptoException {
		try {
			KeyStore ks = loadKeyStore(keyStorePath, storePass);
			ks.deleteEntry(alias);
			saveKeyStore(ks, keyStorePath, storePass);
		} catch (KeyStoreException e) {
			throw new ObidosCryptoException("Could not delete entry from keystore: " + e.getMessage());
		}
	}

	public static void importTrustedCertificate(final Certificate cert, final String alias, final String keyStorePath, final String storePass) throws ObidosCryptoException {
		try {
			KeyStore ks = loadKeyStore(keyStorePath, storePass);
			ks.setCertificateEntry(alias, cert);
			saveKeyStore(ks, keyStorePath, storePass);
		} catch (KeyStoreException e) {
			throw new ObidosCryptoException("Could not import certificate to keystore: " + e.getMessage());
		}
	}

}
