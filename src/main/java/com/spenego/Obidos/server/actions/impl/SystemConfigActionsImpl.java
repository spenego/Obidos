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

package com.spenego.Obidos.server.actions.impl;

import static com.spenego.Obidos.server.model.Audit.CERTIFICATE_INSTALLED;
import static com.spenego.Obidos.server.model.Audit.LICENSE_INSTALLED;
import static com.spenego.Obidos.server.model.Audit.UPDATE_SYSTEM_CONFIG;
import static com.spenego.Obidos.server.utils.ServerUtils.validateLicense;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSPHRASE_COMPLEXITY_REQUIREMENTS;
import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;

import com.spenego.Obidos.server.actions.SystemConfigActions;
import com.spenego.Obidos.server.actions.UserManagementActions;
import com.spenego.Obidos.server.model.ComplexityRequirements;
import com.spenego.Obidos.server.model.SystemConfig;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.ComplexityRequirementsOperations;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.operations.SystemConfigOperations;
import com.spenego.Obidos.server.utils.CertificateUtils;
import com.spenego.Obidos.server.utils.KeyStoreUtils;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.MemoryWiper;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.SystemConfigDTO;
import com.spenego.Obidos.shared.exceptions.LicenseKeyException;
import com.spenego.Obidos.shared.exceptions.ObidosCryptoException;
import com.spenego.Obidos.shared.exceptions.ObidosException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

public class SystemConfigActionsImpl extends ObidosActions<SystemConfig> implements SystemConfigActions {
	private static final Logger logger = LoggerFactory.getLogger(SystemConfigActionsImpl.class);

	private static final Boolean NULL_BOOLEAN = null;

	@Autowired private final ComplexityRequirementsOperations	complexityRequirementsOperations = null;
	@Autowired private final SystemConfigOperations				operations = null;
	@Autowired private final UserManagementActions				userManagementActions = null;
	@Autowired protected final MemoryWiper						memoryWiper = null;

	@Override
	protected final Logger getLogger() {
		return logger;
	}

	@Override
	protected final Operations<SystemConfig> getOperations() {
		return operations;
	}

	@Override
	protected final Integer getAuditDeleteAction() {
		throw new ServerSideException("You can't go deleting the system configuration!");
	}

	@Override
	public SystemConfigDTO get() {
		final SystemConfigDTO dto = convert(getModel(1L), SystemConfigDTO.class);
		dto.setPassComplexityDTO(userManagementActions.getPasswordComplexity(dto.getPasswordComplexityName()));
		return dto;
	}

	private void saveComplexityRequirements(final ComplexityRequirements cr) {
		complexityRequirementsOperations.updateSelective(cr);
	}

	private ComplexityRequirements convert(final ComplexityRequirementsDTO crDTO, final byte type) {
		final ComplexityRequirements cr = convert(crDTO, ComplexityRequirements.class);
		cr.setType(type);

		final Long id = cr.getId();

		if (id != null) {
			if (id.equals(1L) || id.equals(2L)) {
				cr.setName(null);	// do not allow changing of default name
			}
		} else {
			cr.setId(type == PASSPHRASE_COMPLEXITY_REQUIREMENTS ? 2L : 1L);
		}

		return cr;
	}

	private void addReason(final StringBuilder sb, final Object reason, final String text) {
		if (reason != null && !reason.toString().isEmpty()) {
			if (sb.length() != 0) {
				sb.append(", ");
			}
			final String msg = text + " changed to " + reason.toString();
			getLogger().info(() -> msg);
			sb.append(msg);
		}
	}

	private void saveComplexityChanges(final StringBuilder sb, final ComplexityRequirementsDTO cr, final byte type) {
		final String t = ComplexityRequirementsDTO.getTypeName(type);
		logger.info(() -> t + " complexity specified");
		addReason(sb, cr.getMaxAgeInDays(),		"Max Age for " + t);
		addReason(sb, cr.getMinimumEntropy(),	"Minimum entropy for " + t);
		addReason(sb, cr.getMinimumLength(),	"Minimum length for " + t);
		addReason(sb, cr.getMinimumLowercase(),	"Minimum lowercase for " + t);
		addReason(sb, cr.getMinimumNumbers(),	"Minimum numbers for " + t);
		addReason(sb, cr.getMinimumSpecial(),	"Minimum special for " + t);
		addReason(sb, cr.getMinimumUppercase(),	"Minimum uppercase for " + t);
		saveComplexityRequirements(convert(cr, type));
	}

	/**
	 * Ensure the specified directory exists, or if it does not, create it. Also ensure we can create sub directories within
	 * the specified directory.
	 */
	private static void establishDirectory(final String documentStorageDirectory) {
		File dataStoreDirectory = null;

		try {
			dataStoreDirectory = ServerUtils.createSecuredDirectory(documentStorageDirectory);
			ServerUtils.deleteFile(ServerUtils.createSecuredDirectory(documentStorageDirectory, ServerUtils.randomString()).toPath());
		} catch (final IOException e) {
			final String msg = "Unable to create " + (dataStoreDirectory == null ? "Document Storage Directory: " : "sub-directories in ") + documentStorageDirectory;
			logger.error(() -> msg);
			throw new ServerSideException(msg);
		}
	}

	@Override
	public byte[] getSMSKey() {
		return getModel(1L).getSmsKey();
	}

	@Override
	public Void updateSMSKey(final byte[] smsKey) {

		final SystemConfig config = new SystemConfig();
		config.setId(1L);
		config.setSmsKey(smsKey);

		return operations.updateSelective(config);
	}

	@Override
	public Void update(final User admin, final SystemConfigDTO configuration) {
		logger.info(() -> "Admin " + admin.getUsername() + " updating system configuration.");

		// validate PassComplexity DTO with our requirements.
		// client side also uses the same validation.
		// spgdev@spenego.com - Jan 14, 2020
		final PassComplexityDTO pc = configuration.getPassComplexityDTO();
		final StringBuilder sb = new StringBuilder();
		if (pc != null) {
			logger.info(() -> "Password or Passphrase complexity specified");
			try {
				pc.validate();
			} catch (final ObidosException e) {
				logger.info(() -> "Password complexity contains invalid values");
				throw new ServerSideException(e.getMessage());
			}
			if (pc.getPasswordComplexityRequirements() != null) {
				saveComplexityChanges(sb, pc.getPasswordComplexityRequirements(), PASSWORD_COMPLEXITY_REQUIREMENTS);
			}
			if (pc.getPassphraseComplexityRequirements() != null) {
				saveComplexityChanges(sb, pc.getPassphraseComplexityRequirements(), PASSPHRASE_COMPLEXITY_REQUIREMENTS);
			}
		}

		if (configuration.getMemoryWipeDelay() != null) {
			memoryWiper.setMemoryWipeDelay(configuration.getMemoryWipeDelay());
		}

		final String documentStorageDirectory = configuration.getDocumentStorageDirectory();
		if (documentStorageDirectory != null) {
			establishDirectory(documentStorageDirectory);
		}

		final SystemConfig config = convert(configuration, SystemConfig.class);
		config.setId(1L);

		logger.info(() -> "FQDN set to " + config.getFqdn());
		logger.info(() -> "Date Format set to " + config.getDateFormat());
		logger.info(() -> "Admin email set to " + config.getAdminEmail());

		addReason(sb, config.getFqdn(),						"FQDN");
		addReason(sb, config.getDateFormat(),				"Date Format");
		addReason(sb, config.getAdminEmail(),				"Admin Email");
		addReason(sb, config.getTwoFactorAuthIssuer(),		"Two factor auth issuer");
		addReason(sb, config.getContextPath(),				"Context Path");
		addReason(sb, config.getScheme(),					"Scheme");
		addReason(sb, config.getSessionTimeoutSeconds(),	"Session Timeout");
		addReason(sb, config.getMemoryWipeDelay(),			"Memory wipe delay");
		addReason(sb, config.getServerPort(),				"Server Port");
		addReason(sb, config.getLicense(),					"License");
		addReason(sb, config.getDocumentStorageDirectory(),	"Document Storage Directory");

		logger.info(() -> "Sent to audit: " + sb.toString());

		audit(UPDATE_SYSTEM_CONFIG, admin.getUsername(), admin.getId(), null, null, null, sb);

		return operations.updateSelective(config);
	}

	@Override
	public LicenseStats installLicense(final User admin, final String base64License) {
		final String trimmedLicense = base64License.trim();

		logger.info(() -> "License: " + trimmedLicense);
		// validate signature in license
		final LicenseKeyDTO license = validateLicense(trimmedLicense, getSystemConfig().getLicensePublicKey());
		logger.info(() -> "License signature validated");

		// don't install expired license
		if (isTrue(license.getHasExpired())) {
			throw new LicenseKeyException("License is expired. You may not install an expired license.");
		}

		if (license.getCompanyName() == null) {
			throw new LicenseKeyException("Company name on license is invalid.");
		}

		// Issue 757 allow 0 max users, meaning no limit
		if (license.getMaxUsers() == null || license.getMaxUsers() < 0) {
			throw new LicenseKeyException("User count on license is invalid.");
		}

		operations.updateSelective(new SystemConfig(1L, trimmedLicense));

		final String expirationDate = license.getExpirationEpoch() == null ? "does not expire" : ("expires at " + new Date(license.getExpirationEpoch()));

		audit(LICENSE_INSTALLED, admin.getUsername(), admin.getId(), license.getCompanyName(), null, null, "configured for " + license.getMaxUsers() + " users that " + expirationDate);

		return null;
	}

	@Override
	public LicenseKeyDTO getCurrentLicense() {
		return super.getCurrentLicense();
	}

	@Override
	public LicenseKeyDTO decodeLicense(final String license) {
		return validateLicense(license, getSystemConfig().getLicensePublicKey());
	}

	private static void stageCertificate(String pemCerts) {
		try {
			String certsFile = ServerUtils.getNginxStagedCertPath();
			logger.info(() -> "Install certificate: " + certsFile);
			ServerUtils.writeToFile(certsFile, pemCerts);
		} catch (IOException e) {
			throw new ServerSideException("Could not stage certificate: " + e.getMessage());
		}
	}

	private static void stagePrivateKey(String pemPrivateKey) {
		try {
			String keyFile = ServerUtils.getNginxStagedKeyPath();
			logger.info(() -> "Install private key: " + keyFile);
			ServerUtils.writeToFile(keyFile, pemPrivateKey);
		} catch (IOException e) {
			throw new ServerSideException("Could not stage private key: " + e.getMessage());
		}
	}

	private static boolean servernameExistsInCertChain(final List<Certificate> certs) {
		final int certsSize = certs.size();

		for (final Certificate cert : certs) {
			final String cn = CertificateUtils.getServerName(cert, certsSize);
			logger.info(() -> "cn: " + cn);
			if (cn != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Void installNginxCertificates(final User admin, final String pemCerts, final String pemPrivateKey) {
		try {
			if (!servernameExistsInCertChain(CertificateUtils.parseChain(pemCerts))) {
				throw new ServerSideException("Could not find server name in the certificate");
			}
			stageCertificate(pemCerts);
			stagePrivateKey(pemPrivateKey);
			audit(CERTIFICATE_INSTALLED, admin.getUsername(), admin.getId(), "SSL Cert", null, null, "SSL certificate installed");
		} catch (ObidosCryptoException e) {
			throw new ServerSideException(e.getMessage());
		}

		return null;
	}

	/**
	 * Install AD/LDAP certificates using KeyStore APIs
	 */
	@Override
	public Void installAdLdapCertificates(User admin, String pemCerts) {
		String keyStorePath = ServerUtils.getCacertsKeyStorePath();
		String storePass;
		try {
			storePass = ServerUtils.getCacertsKeyStorePassword();
		} catch (IOException e2) {
			throw new ServerSideException("Could not keystore properties file: " + e2.getMessage());
		}
		try {
			final List<Certificate> certs = CertificateUtils.parseChain(pemCerts);
			for (final Certificate cert : certs) {
				String commonName = CertificateUtils.getCommonName(cert);
				String alias = ServerUtils.makeAdLdapAliasName(commonName);
				if (KeyStoreUtils.entryExists(alias, keyStorePath, storePass)) {
					logger.info(() -> "Delete alias " + alias);
					KeyStoreUtils.deleteEntry(alias, keyStorePath, storePass);
				}
				logger.info(() -> "Add certificate with alias " + alias);
				KeyStoreUtils.importTrustedCertificate(cert, alias, keyStorePath, storePass);
			}
		} catch (final ObidosCryptoException e1) {
			throw new ServerSideException(e1.getMessage());
		}

		return null;
	}

	@Override
	public Boolean validPEMCert(User admin, String pemCert) {
		try {
			return CertificateUtils.isItValidPEMCert(pemCert);
		} catch (CertificateException | IOException e) {
			throw new ServerSideException("Invalid PEM certificate; " + e.getMessage());
		}
	}

	@Override
	public Boolean adLdapCertInstalled(User admin) {
		return NULL_BOOLEAN;
	}

	/*
	private static String getNginxConfValue(final String key) {
		String nginxDefaultFile = ObidosConstants.NGINX_DEFAULT_FILE;
		try {
			final NgxParam value = NgxConfig.read(nginxDefaultFile).findParam("server", key);
			if (value == null) {
				throw new ServerSideException("Could not find nginx value for key " + key + "in " + nginxDefaultFile);
			}

			final String v = value.getValue();
			logger.info(() -> "nginx certificate: " + v);

			return v;
		} catch (final IOException e) {
			throw new ServerSideException("Could not read nginx default file: " + nginxDefaultFile + " :" + e.getMessage());
		}
	} */

	private static List<CertificateInfoDTO> getCertificateInfo(final Supplier<Collection<Certificate>> supplier) {
		try {
			final List<CertificateInfoDTO> certInfoDTOs = new ArrayList<>();

			for (final Certificate certificate : supplier.get()) {
				certInfoDTOs.add(CertificateUtils.getCertificateInfo(certificate));
			}

			return certInfoDTOs;
		} catch (final ObidosCryptoException e) {
			throw new ServerSideException(e.getMessage());
		}
	}

	@Override
	public List<CertificateInfoDTO> getNginxCertificateInfo(final User admin) {
		//
		// Bug# 843
		// Obidos cannot access the fullchain.pem as the parent directory is
		// owned by root with permission 700.
		// So don't bother to parse the nginx conf file, just
		// execute copy_cert program to get the path of full chain.  The program
		// copy_cert has capabilities set and will copy the cert chain file to
		// obidos space
		final String prog = ObidosConstants.COPY_CERT_PROG;
		if (! ServerUtils.fileExist(prog))
		{
			throw new ServerSideException("Program " + prog + " does not exist");
		}
		// copy_cert prints the path of fullchain.pem on stdout

        try (ByteArrayOutputStream stdout = new ByteArrayOutputStream()) {
        	final PumpStreamHandler psh = new PumpStreamHandler(stdout);
            final DefaultExecutor exec = DefaultExecutor.builder().get(); // new DefaultExecutor(); default constructor is deprecated
            exec.setStreamHandler(psh);
			exec.execute(CommandLine.parse(prog));
			// trim method gets rid of spaces and new lines from anywhere
			final String certPath = stdout.toString("UTF-8").trim();
			// Must remove the new line. not needed. trim() did it
			//final String certPath = path.replace("\n", "").replace("\r", "");
			if (! ServerUtils.fileExist(certPath)) {
				throw new ServerSideException("Certificate path " + certPath + " does not exist");
			}
   		    logger.info(() -> "Cert path: '" + certPath + "'");
     		return getCertificateInfo(() -> CertificateUtils.parseChainFile(certPath));

		} catch (final ExecuteException e) {
			throw new ServerSideException("Could not execute " + prog + " :" + e);
		} catch (final IOException e) {
			throw new ServerSideException("Could not execute " + prog + " :" + e);
		}
	}

	@Override
	public List<CertificateInfoDTO> getAdLdapCertificateInfo(final User admin) {
		return getCertificateInfo(CertificateUtils::getAdLdapCertsFromKeystore);
	}

	@Override
	public List<CertificateInfoDTO> getCertificateInfo(final User admin, final String pemChain) {
		return getCertificateInfo(() -> CertificateUtils.parseChain(pemChain));
	}
}
