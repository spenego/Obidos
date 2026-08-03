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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import com.spenego.Obidos.server.utils.bouncycastlepem.PemObject;
import com.spenego.Obidos.server.utils.bouncycastlepem.PemReader;
import com.spenego.Obidos.server.utils.bouncycastlepem.PemWriter;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.exceptions.ObidosCryptoException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

/**
 *
 * @author spgdev@spenego.com - Jul 1, 2020
 */
public final class CertificateUtils {

	private static final Logger logger = LoggerFactory.getLogger(CertificateUtils.class);

	private CertificateUtils() { /* all methods are static */ }

	public static List<Certificate> getCertsFromKeystore(final CharSequence s) throws ObidosCryptoException {
		try {
			final List<Certificate> certList = new ArrayList<>();
			final Properties props = ServerUtils.getAdLdapProperties();
			final String keystorePassword = props.getProperty("keystore_password");
			if (keystorePassword == null) {
				throw new ServerSideException("Could not find keystore password");
			}
			final String keystorePath = ObidosConstants.SYSTEM_CACERTS;
			final KeyStore keystore = KeyStoreUtils.loadKeyStore(keystorePath, keystorePassword);
			final Enumeration<String> enumeration = keystore.aliases();
			while (enumeration.hasMoreElements()) {
				final String alias = enumeration.nextElement();
				if (alias.contains(s)) {
					certList.add(keystore.getCertificate(alias));
				}
			}

			return certList;
		} catch (KeyStoreException e) {
			throw new ObidosCryptoException("Could not load keystore: " + e.getMessage());
		} catch (IOException e) {
			throw new IllegalStateException("Could not load AD/LDAP properties file: " + e.getMessage());
		}
	}

	public static List<Certificate> getAdLdapCertsFromKeystore() throws ObidosCryptoException {
		final List<Certificate> certs = getCertsFromKeystore(ObidosConstants.AD_LDAP_ALIAS_STARTS_WITH);

		if (certs.isEmpty()) {
			throw new IllegalStateException("Could not find any AD/LDAP certificate in keystore");
		}

		return certs;
	}

	private static Collection<? extends Certificate> generateCertificates(final InputStream is) throws CertificateException {
		return CertificateFactory.getInstance("X509").generateCertificates(is);
	}

	public static List<Certificate> parseChain(final String chain) throws ObidosCryptoException {
		final String CHAIN_DECODE_ERROR = "Could not decode certificate chain";
		final List<Certificate> certList = new ArrayList<>();
		try (final PemReader pemReader = new PemReader(new StringReader(chain));) {
			PemObject pemObject = null;
			while ((pemObject = pemReader.readPemObject()) != null) {
				try (final ByteArrayInputStream bais = new ByteArrayInputStream(pemObject.getContent())) {
					for (final Certificate cert : generateCertificates(bais)) {
						if (cert instanceof X509Certificate) {
							certList.add(cert);
						}
					}
				}

				if (certList.isEmpty()) {
					logger.info(() -> CHAIN_DECODE_ERROR);
					throw new ObidosCryptoException(CHAIN_DECODE_ERROR);
				}
			}
		} catch (CertificateException e) {
			throw new ObidosCryptoException("Could not parse certificate: " + e.getMessage());
		} catch (IOException e1) {
			throw new ObidosCryptoException("Could not parse PEM certificate: " + e1.getMessage());
		}

		if (certList.isEmpty()) {
			throw new IllegalStateException(CHAIN_DECODE_ERROR);
		}
		return certList;
	}

	// This method mirrors the one above it but the logic is slightly different.  Should this method loop through all the PemObjects too?
	public static Boolean isItValidPEMCert(final String pemCert) throws IOException, CertificateException {
		try (final PemReader pemReader = new PemReader(new StringReader(pemCert));) {
			try (final ByteArrayInputStream bais = new ByteArrayInputStream(pemReader.readPemObject().getContent())) {
				return generateCertificates(bais).iterator().next() instanceof X509Certificate;
			}
		}
	}

	private static void writePemObject(final PemWriter pemWriter, final PemObject pemObject) {
		try {
			pemWriter.writeObject(pemObject);
			pemWriter.flush();
		} catch (IOException e) {
			throw new ObidosCryptoException("Could not write certificate: " + e.getMessage());
		}
	}

	public static String convertToPEM(final Certificate cert) throws ObidosCryptoException {
		final StringWriter stringWriter = new StringWriter();
		try(final PemWriter pemWriter = new PemWriter(stringWriter)) {
			final PemObject pemObject = new PemObject("CERTIFICATE", cert.getEncoded());
			writePemObject(pemWriter, pemObject);
			return stringWriter.toString();
		} catch (IOException | CertificateEncodingException e) {
			throw new ObidosCryptoException("Invalid certificate: " + e.getMessage());
		}
	}

	private static String fileContents(final String filename) throws IOException {
		final File file = new File(filename);

		try (final FileInputStream inputStream = new FileInputStream(file)) {
			final int file_length = (int) file.length();
			final byte[] data = new byte[file_length];
			final int bytes_read = inputStream.read(data);

			if (bytes_read != file_length) {
				logger.warn(() -> "Expected to read " + file_length + " bytes from file, but only read " + bytes_read);
			}

			return new String(data, StandardCharsets.UTF_8);
		}
	}

	public static Boolean isItValidPEMCertFile(final String pemCertFile) throws IOException, CertificateException {
		return isItValidPEMCert(fileContents(pemCertFile));
	}

	public static List<String> splitCertChain(final String chain) {
		final String END_MARKER = "-----END CERTIFICATE-----";
		return Arrays.asList(chain.split(END_MARKER));
	}

	public static List<Certificate> parseChainFile(final String chainFile) throws ObidosCryptoException {
		try {
			return parseChain(fileContents(chainFile));
		} catch (FileNotFoundException e) {
			throw new ObidosCryptoException("Certificate file " + chainFile + " not found: " + e.getMessage());
		} catch (final UnsupportedEncodingException e) {
			throw new ObidosCryptoException("Unsupported certificate encoding: " + e.getMessage());
		} catch (final IOException e) {
			throw new ObidosCryptoException("Could not read certificate file: " + e.getMessage());
		}
	}

	public static boolean adLdapCertificateInstalled() throws ObidosCryptoException {
		return !getCertsFromKeystore(ObidosConstants.AD_LDAP_ALIAS_STARTS_WITH).isEmpty();
	}

	public static CertificateInfoDTO getCertificateInfo(final Certificate certificate) throws ObidosCryptoException {
		CertificateInfoDTO dto = new CertificateInfoDTO();
		X509Certificate cert = (X509Certificate) certificate;
		dto.setSubjectNameHeading("Subject Name");
		LdapName ldapName;
		try {
			ldapName = new LdapName(cert.getSubjectDN().getName());
			// Common Name
			for (Rdn rdn : ldapName.getRdns()) {
				if (rdn.getType().equalsIgnoreCase("CN")) {
					dto.setSubjectCommonName(rdn.getValue().toString());
				}
			}

		} catch (InvalidNameException e) {
			throw new ObidosCryptoException("Invalid Subject DN: " + e.getMessage());
		}

		// Issuer
		dto.setIssuerNameHeading("Issuer Name");
		try {
			logger.info(() -> "Issuer: " + cert.getIssuerDN().getName());
			ldapName = new LdapName(cert.getIssuerDN().getName());
			for (Rdn rdn : ldapName.getRdns()) {
				logger.info(() -> "RDNC: " + rdn.toString());
				if (rdn.getType().equalsIgnoreCase("C")) {
					dto.setIssuerCountry(rdn.getValue().toString());
				}
				if (rdn.getType().equalsIgnoreCase("O")) {
					dto.setIssuerOrganization(rdn.getValue().toString());
				}
				if (rdn.getType().equalsIgnoreCase("CN")) {
					logger.info(() -> "CN: " + rdn.getValue().toString());

					dto.setIssuerCommonName(rdn.getValue().toString());
				}
			}
		} catch (InvalidNameException e) {
			throw new ObidosCryptoException("Invalid Issuer DN: " + e.getMessage());
		}

		// Validity
		dto.setValidityHeading("Validity");
		dto.setNotBeforeDate(cert.getNotBefore());
		dto.setNotAfterDate(cert.getNotAfter());

		// Subject Alt Names
		dto.setSubjectAltNamesHeading("Subject Alt Names");
		dto.setIpaAltNames(getSubjectAltName(cert, 7));
		dto.setDnsAltNames(getSubjectAltName(cert, 2));

		// is CA?
		if (cert.getBasicConstraints() != -1) {
			dto.setIsCA(true);
		} else {
			String defaultFile = ServerUtils.getNginxDefaultFilePath();
			File file = new File(defaultFile);
			dto.setInstallDate(new Date(file.lastModified()));
			dto.setIsCA(null);
		}

		// Public key info
		dto.setPublicKeyInfoHeading("Public Key Info");
		dto.setPublicKeyAlgorithm(cert.getPublicKey().getAlgorithm());

		// Misc
		dto.setMiscHeading(" Miscellaneous");
		dto.setSerialNumber(cert.getSerialNumber().toString(16));
		dto.setSignatureAlgorithmString(cert.getSigAlgName());
		dto.setVersion(cert.getVersion());

		return dto;
	}

	private static List<String> getSubjectAltName(final X509Certificate cert, final int type) {
		List<String> result = new ArrayList<>();
		try {
			Collection<?> altNames = cert.getSubjectAlternativeNames();
			if (altNames == null) {
				return Collections.emptyList();
			}
			for (Object altName : altNames) {
				List<?> l = (List<?>) altName;
				if (l != null && l.size() > 1) {
					Integer altNameType = (Integer) l.get(0);
					if (altNameType != null && altNameType == type) {
						String aname = (String) l.get(1);
						if (aname != null) {
							result.add(aname);
						}
					}
				}
			}
			return result;
		} catch (CertificateParsingException e) {
			return Collections.emptyList();
		}
	}

	public static String getServerName(final Certificate cert, int n) {
		try {
			X509Certificate x509Cert = (X509Certificate) cert;
			int basicContraints = x509Cert.getBasicConstraints();
			if (n > 1 && basicContraints != -1) {
				return null; // CA certificate
			}

			LdapName ldapName = new LdapName(x509Cert.getSubjectDN().getName());
			for (Rdn rdn : ldapName.getRdns()) {
				if (rdn.getType().equalsIgnoreCase("CN")) {
					return rdn.getValue().toString();
				}
			}

		} catch (InvalidNameException e) {
			throw new ServerSideException("Could not parse Subject CN in certificate: " + e.getMessage());
		}

		return null;
	}

	public static String getCommonName(final Certificate cert) {
		try {
			X509Certificate x509Cert = (X509Certificate) cert;
			LdapName ldapName = new LdapName(x509Cert.getSubjectDN().getName());
			for (Rdn rdn : ldapName.getRdns()) {
				if (rdn.getType().equalsIgnoreCase("CN")) {
					return rdn.getValue().toString();
				}
			}
		} catch (InvalidNameException e) {
			throw new ServerSideException("Could not parse Subject CN in certificate: " + e.getMessage());
		}

		return null;
	}
}
