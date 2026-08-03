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

package com.spenego.Obidos.server.utils.bouncycastlepem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author spgdev@spenego.com - Jul 1, 2020
 */
public class CertificateUtils {

	/**
	 * chain is in PEM format
	 * 
	 * @param chain
	 * @return
	 * @throws IOException
	 * @throws CertificateException
	 *             <p>
	 * @author spgdev@spenego.com - Jul 1, 2020
	 */
	/*
	 * public static List<X509Certificate> parseChainX(final String chain)
	 * throws IOException, CertificateException { final List<X509Certificate>
	 * x509Certs = new ArrayList<X509Certificate>(); PemObject pemObject = null;
	 * try (final PemReader pemReader = new PemReader(new StringReader(chain));)
	 * { while ((pemObject = pemReader.readPemObject()) != null) { final
	 * CertificateFactory certificateFactory =
	 * CertificateFactory.getInstance("X509"); final ByteArrayInputStream bais =
	 * new ByteArrayInputStream(pemObject.getContent());
	 * 
	 * for (final Certificate cert :
	 * certificateFactory.generateCertificates(bais)) { if (cert instanceof
	 * X509Certificate) { x509Certs.add((X509Certificate) cert); } } if
	 * (x509Certs.isEmpty()) { throw new
	 * IllegalStateException("Unable to decode certificate chain"); } } } return
	 * x509Certs; }
	 */

	public static List<Certificate> parseChain(final String chain) throws IOException, CertificateException {
		final List<Certificate> Certs = new ArrayList<Certificate>();
		try (final PemReader pemReader = new PemReader(new StringReader(chain));) {
			PemObject pemObject = null;
			while ((pemObject = pemReader.readPemObject()) != null) {
				final CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
				try (final ByteArrayInputStream bais = new ByteArrayInputStream(pemObject.getContent())) {
					for (final Certificate cert : certificateFactory.generateCertificates(bais)) {
						if (cert instanceof X509Certificate) {
							Certs.add(cert);
						}
					}
					if (Certs.isEmpty()) {
						throw new IllegalStateException("Unable to decode certificate chain");
					}
				}
			}
		}
		return Certs;
	}

	public static Boolean isItValidPEMCert(final String pemCert) throws IOException, CertificateException {
		/*
		 * Boolean rc = Boolean.FALSE;
		 * 
		 * try (final PemReader pemReader = new PemReader(new
		 * StringReader(pemCert));) { final PemObject pemObject =
		 * pemReader.readPemObject(); final CertificateFactory
		 * certificateFactory = CertificateFactory.getInstance("X509"); final
		 * ByteArrayInputStream bais = new
		 * ByteArrayInputStream(pemObject.getContent()); for (final Certificate
		 * cert : certificateFactory.generateCertificates(bais)) { if (cert
		 * instanceof X509Certificate) { rc = Boolean.TRUE; } else { rc =
		 * Boolean.FALSE; } } } if (!rc) { throw new
		 * ServerSideException("Invalid PEM encoded certificate"); } return rc;
		 */
		return false;
	}

	public static Boolean isItValidPEMCertFile(final String pemCertFile) throws IOException, CertificateException {
		File file = new File(pemCertFile);
		try (FileInputStream inputStream = new FileInputStream(file)) {
			byte[] data = new byte[(int) file.length()];
			inputStream.read(data); // NOSONAR

			String pemCert = new String(data, "UTF-8");
			return isItValidPEMCert(pemCert);
		}
	}

	public static List<String> splitCertChain(final String chain) throws IOException, CertificateException {
		final String END_MARKER = "-----END CERTIFICATE-----";
		final List<String> pemCerts = new ArrayList<String>();
		String[] certs = chain.split(END_MARKER);
		for (String cert : certs) {
			pemCerts.add(cert);
		}
		return pemCerts;
	}

	@Deprecated
	public static List<X509Certificate> parseChainFileX(final String chainFile) throws IOException, CertificateException {
		/*
		 * File file = new File(chainFile); FileInputStream inputStream = new
		 * FileInputStream(file); byte[] data = new byte[(int) file.length()];
		 * inputStream.read(data); inputStream.close();
		 * 
		 * String chain = new String(data, "UTF-8"); return parseChainX(chain);
		 */
		return null;
	}

	public static List<Certificate> parseChainFile(final String chainFile) throws IOException, CertificateException {
		File file = new File(chainFile);
		try (FileInputStream inputStream = new FileInputStream(file)) {
			byte[] data = new byte[(int) file.length()];
			inputStream.read(data); // NOSONAR
			inputStream.close();

			String chain = new String(data, "UTF-8");
			return parseChain(chain);
		}
	}

	/*
	 * public static boolean adLdapCertificateInstalled() throws
	 * CertificateException, NoSuchAlgorithmException { try { Properties props =
	 * ServerUtils.getAdLdapProperties(); String keystorePassword =
	 * props.getProperty("keystore_password"); if (keystorePassword == null) {
	 * throw new ServerSideException("Could not find keystore password"); }
	 * String keystorePath = ObidosConstants.SYSTEM_CACERTS; FileInputStream is
	 * = new FileInputStream(keystorePath); try { KeyStore keystore =
	 * KeyStore.getInstance(KeyStore.getDefaultType()); keystore.load(is,
	 * keystorePassword.toCharArray()); Enumeration<String> enumeration =
	 * keystore.aliases(); while (enumeration.hasMoreElements()) { String alias
	 * = enumeration.nextElement(); if
	 * (alias.contains(ObidosConstants.AD_LDAP_ALIAS_STARTS_WITH)) { return
	 * true; } }
	 * 
	 * } catch (KeyStoreException e) { throw new
	 * ServerSideException("Could not load keystore: " + e.getMessage()); }
	 * 
	 * } catch (IOException e) { throw new
	 * ServerSideException("Could not load AD/LDAP properties file: " +
	 * e.getMessage()); } return false; }
	 */
}
