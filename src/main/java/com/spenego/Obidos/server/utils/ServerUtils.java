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

import static com.spenego.Obidos.shared.ObidosConstants.CONFIG_DIR_LINUX;
import static com.spenego.Obidos.shared.ObidosConstants.CONFIG_DIR_WINDOWS;
import static com.spenego.Obidos.shared.ObidosConstants.PASSAY_MESSAGE_PROPERTIES_FILE;
import static com.spenego.Obidos.shared.ObidosConstants.SODIUM_LIB_PATH_LINUX;
import static com.spenego.Obidos.shared.ObidosConstants.SODIUM_LIB_PATH_MAC;
import static com.spenego.Obidos.shared.ObidosConstants.SODIUM_LIB_PATH_WINDOWS;
import static org.passay.EnglishCharacterData.Digit;
import static org.passay.EnglishCharacterData.LowerCase;
import static org.passay.EnglishCharacterData.Special;
import static org.passay.EnglishCharacterData.UpperCase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.macasaet.fernet.Key;
import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.TokenValidationException;
import com.macasaet.fernet.Validator;
import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.spenego.Obidos.server.config.ObidosConfiguration;
import com.spenego.Obidos.server.security.SecureCompatibleEncryptionExamples;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.FernetDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;
import com.spenego.Obidos.shared.dto.SmsKeyDTO;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.sun.jna.Platform; // NOSONAR -- there is no Java API for this class
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

/**
 * Among other functions, this class serves to isolate various dependencies away
 * from the rest of Obidos code. Ideally, the rest of Obidos code would have no
 * external dependencies. This makes it easier to refactor code to change
 * external dependencies to other implementations.
 *
 * @author mmorgan
 *
 */
public class ServerUtils {
	private static final Logger logger = LoggerFactory.getLogger(ServerUtils.class);

	public static final String LAST_SESSION_ACCESS_EPOCH_ATTR = "LAST_SESSION_ACCESS_EPOCH";
	public static final String SPENEGO_TMP_DIR = "/usr/local/spenego/tmp";

	protected ServerUtils() {
		/* No members */ }

	public static final void postCommitAction(final Runnable action, final Logger logger) {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					try {
						action.run();
					} catch (final Throwable t) { // since the transaction is complete, we do not want to send any
													// exception up the the user
						logger.error(() -> "Caught exception during post-commit action: ", t);
						logger.exception(t);
					}
				}
			});
		}
	}

	public static void deleteFile(final Path path) {
		if (Files.exists(path)) {
			try {
				Files.delete(path);
			} catch (final IOException e) {
				logger.error(() -> "Failed to delete " + path + ": " + e);
			}
		}
	}

    public static void deleteEmptyParentDirectory(final Path filePath)
    {
        try
        {
            Path parentDir = filePath.getParent();
            if (parentDir != null && Files.isDirectory(parentDir))
            {
                String[] files = parentDir.toFile().list();
                if (files != null && files.length == 0)
                {
                    Files.delete(parentDir);
                    logger.info(() -> "Deleted empty directory: " + parentDir);
                }
            }
        } catch (IOException e)
        {
            logger.error(() -> "Failed to delete empty parent directory for " + filePath);
            logger.exception(e);
        }
    }

	public static final boolean isCurrentTransactionReadOnly() {
		return TransactionSynchronizationManager.isCurrentTransactionReadOnly();
	}

	public static String getLastSessionAccessEpochAttr() {
		return LAST_SESSION_ACCESS_EPOCH_ATTR;
	}

	public static final long nextRandomLong(final long max) {
		return ThreadLocalRandom.current().nextLong(max);
	}

	public static final int nextRandomInt(int n) {
		return ThreadLocalRandom.current().nextInt(n);
	}

	public static final String getUuidString() {
		// Based on RFC 1422

		return UUID.randomUUID().toString();
	}

	public static String encodeToBase64(final byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	public static byte[] decodeFromBase64(final String base64) {
		try {
			return Base64.getDecoder().decode(base64.trim());
		} catch (IllegalArgumentException e) {
			throw new ServerSideException("Could not decode base64: " + e.getMessage());
		}
	}

	public static String bytesToString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	// caller must check for null to see if conversion failed
	public static Integer stringToInteger(final String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static String sha256Hex(final String str) {
		return DigestUtils.sha256Hex(str.getBytes());
	}

	public static String randomString() {
		return UUID.randomUUID().toString();
	}

	public static String stringToHex(final String str) {
		return Hex.encodeHexString(str.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Use Gson to map JSON to object without any parsing
	 *
	 * @param jsonStr
	 * @return NotificationTemplateJSONDTO
	 *         <p>
	 * @author spgdev@spenego.com - Jul 1, 2018
	 */
	public static final <T> T parseJSONTemplate(final String jsonStr, Class<T> clazz) {
		return new Gson().fromJson(jsonStr, clazz);
	}

	public static final String toJson(final Object o) {
		final String jsonStr = new Gson().toJson(o);
		// remove the following log ASAP as JSON can have sensitive info
		logger.info(() -> "WARNING: remove the following JSON ASAP as it can have sesitive info");
		logger.info(() -> "JSON: " + jsonStr);
		return jsonStr;
	}

	private static void addCharacterRule(final Integer limit, final EnglishCharacterData cd, final List<Rule> rules) {
		if (limit > 0)
			rules.add(new CharacterRule(cd, limit));
	}

	private static List<Rule> getRules(final ComplexityRequirementsDTO cr) {
		final List<Rule> rules = new ArrayList<>();

		rules.add(new LengthRule(cr.getMinimumLength(), 512));
		addCharacterRule(cr.getMinimumLowercase(), LowerCase, rules);
		addCharacterRule(cr.getMinimumUppercase(), UpperCase, rules);
		addCharacterRule(cr.getMinimumNumbers(), Digit, rules);
		addCharacterRule(cr.getMinimumSpecial(), Special, rules);

		return rules;
	}

	/**
	 * validate a password using Passay
	 *
	 * @param password
	 * @param cdto
	 * @throws ServerSideException
	 *                             <p>
	 * @author spgdev@spenego.com - Jan 19, 2020
	 */
	public static void validateComplexity(final String p, final String s, final ComplexityRequirementsDTO cr) {
		final PasswordValidator validator = new PasswordValidator(
				getPassayMessageResolver(PASSAY_MESSAGE_PROPERTIES_FILE), getRules(cr));
		final RuleResult result = validator.validate(new PasswordData(p));
		if (!result.isValid()) {
			logger.info(() -> s + " failed validation");
			logger.info(() -> s + " length: " + p.length());
			final List<String> msgList = validator.getMessages(result);
			logger.info(() -> "Exception: " + msgList);
			throw new ServerSideException(String.join(" *<br/> ", msgList));
		}
		logger.info(() -> s + " complexity passed");
	}

	/**
	 * If password/passphrase contains the string throw exception
	 *
	 * @param pass
	 * @param contains
	 * @throws ServerSideException
	 *                             <p>
	 * @author spgdev@spenego.com - Jan 29, 2020
	 */
	private static void validatePassDoesNotContain(final String pass, final String contains,
			final MessageResolver resolver) throws ServerSideException {
		final PasswordValidator validator = new PasswordValidator(resolver, new UsernameRule());
		final RuleResult result = validator.validate(new PasswordData(contains.toLowerCase(), pass.toLowerCase()));

		if (!result.isValid()) {
			logger.info(() -> "failed validation");
			List<String> msgList = validator.getMessages(result);
			logger.info(() -> "Exception: " + msgList);
			throw new ServerSideException(String.join(" *<br/> ", msgList));
		}
	}

	public static void validatePassDoesNotContain(final String pass, final String contains) throws ServerSideException {
		if (contains == null) {
			return;
		}
		validatePassDoesNotContain(pass, contains,
				getPassayMessageResolver(ObidosConstants.PASSAY_MESSAGE_PROPERTIES_FILE));
	}

	private static MessageResolver getPassayMessageResolver(final String propfilePath) {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (final InputStream is = classLoader.getResourceAsStream(propfilePath)) {
			final Properties props = new Properties();
			props.load(is);
			return new PropertiesMessageResolver(props);
		} catch (final IOException e) {
			throw new ServerSideException("Could not load properties file: " + propfilePath);
		}
	}

	/*
	 * What Safelist.basic() Allows
	 * Text Formatting: <b>, <strong>, <i>, <em>, <u>
	 * Links: <a> with href attribute
	 * Lists: <ul>, <ol>, <li>
	 * Headings: <h1>, <h2>, <h3>, <h4>, <h5>, <h6>
	 * Paragraphs and Breaks: <p>, <br>
	 */
	/* updated for security May-26-2024 */
	public static String sanitize(final String unsafeHTML) {
		return Jsoup.clean(unsafeHTML, Safelist.basic());
	}

	// requires apache commons codec 1.7+
	public static String getHexSha1Digest(String str) {
		return DigestUtils.sha1Hex(str);
	}

	private static final File currentDir = new File(".");

	private static File obidosDir() {
		return new File(isWindows() ? CONFIG_DIR_WINDOWS : CONFIG_DIR_LINUX);
	}

	public static String getSodiumLibPath() {
		return isWindows() ? SODIUM_LIB_PATH_WINDOWS : isMac() ? SODIUM_LIB_PATH_MAC : SODIUM_LIB_PATH_LINUX; // NOSONAR
																												// --
																												// this
																												// is
																												// simple
																												// enough
	}

	public static String getSpenegoPath() {
		if (isWindows()) {
			return ObidosConstants.SPENEGO_DIR_WINDOWS;
		}
		return ObidosConstants.SPENEGO_DIR_LINUX;
	}

	// don't use pathSeperator. it is : for unix and ; for windows
	public static String getObidosPath() {
		return getSpenegoPath() + File.separator + "obidos";
	}

	// where user/admin guide pdfs are
	public static String getDocumentDirPath() {
		return getObidosPath() + File.separator + "docs";
	}

	public static String getAdminGuidePath() {
		return getDocumentDirPath() + File.separator + "admin_guide.pdf";
	}

	public static String getUserGuidePath() {
		return getDocumentDirPath() + File.separator + "user_guide.pdf";
	}

	public static String getChangeLogPath() {
		return getDocumentDirPath() + File.separator + "ChangeLog.html";
	}

	public static File getPropertiesFile(final String propertiesFilename) {
		final File spenegoDir = obidosDir();
		return new File(spenegoDir.exists() ? spenegoDir : currentDir, propertiesFilename);
	}

	/**
	 * Generate Fernet key and save to the specified file
	 *
	 * @param propertiesFile
	 * @return FernetDTO
	 * @throws IOException
	 *                     <p>
	 * @author spgdev@spenego.com - Apr 21, 2019
	 */
	private static FernetDTO createFernetFile(final File propertiesFile) throws IOException {
		final StringBuilder comments = new StringBuilder(512);
		comments.append("########################################################################\n")
				.append("# 128 bit AES key used to generate fernet token for PDF reports\n")
				.append("# download The token is generated by FernetService. The GET request to\n")
				.append("# download PDF must accompany the token. The ReporterServlet will verify\n")
				.append("# the token with this key. The key should be rotated when the Obidos starts\n")
				.append("# The can be generated as:\n")
				.append("# dd if=/dev/urandom bs=32 count=1 2>/dev/null | openssl base64\n")
				.append("# Fernet spec: https://github.com/fernet/spec/blob/master/Spec.md\n")
				.toString();

		try (final FileOutputStream fos = new FileOutputStream(propertiesFile)) {
			final Key key = Key.generateKey(new SecureRandom());
			final Properties props = new Properties();
			props.setProperty("key", key.serialise());
			props.store(fos, comments.toString());
			return new FernetDTO(key.serialise());
		}
	}

	// chmod 0600 file
	public static void setReadWritePermissions(final String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			setReadWritePermission(new File(path));
		}
	}

	// chmod 0600 file
	public static void setReadWritePermission(final File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		Files.setPosixFilePermissions(file.toPath(), perms);
	}

	// delete file if it exists
	public static FernetDTO createFernetFile(final String path) throws IOException {
		File file = new File(path);
		ServerUtils.deleteFile(file.toPath());

		String comments = "rotating file with fernet token";
		try (final FileOutputStream fos = new FileOutputStream(file)) {
			final Key key = Key.generateKey(new SecureRandom());
			final Properties props = new Properties();
			props.setProperty("key", key.serialise());
			props.store(fos, comments);

			setReadWritePermission(file);

			return new FernetDTO(key.serialise());
		}
	}

	public static void deleteFile(final String filePath) {
		if (fileExist(filePath)) {
			deleteFile(new File(filePath).toPath());
		}
	}

	private static FernetDTO loadFernetFile(final File propertiesFile) {
		try (final FileInputStream fileInput = new FileInputStream(propertiesFile)) {
			final Properties props = new Properties();
			props.load(fileInput);
			return new FernetDTO(props.getProperty("key"));
		} catch (final FileNotFoundException e) {
			throw new ServerSideException("Could not find properties file");
		} catch (final IOException e) {
			throw new ServerSideException("Could not read properties file");
		}
	}

	public static boolean fileExist(final String path) {
		return new File(path).exists();
	}

	/**
	 * Return FerentDTO with the key
	 *
	 * fernet_crypto.properties will be created if it does not exist. The file
	 * have key=base64_key
	 *
	 * @param propertiesFilename
	 * @return FernetDTO
	 * @throws IOException
	 *                     <p>
	 * @author spgdev@spenego.com - Apr 21, 2019
	 */
	public static FernetDTO getFernetDTO(final String propertiesFilename) throws IOException {
		final File propertiesFile = getPropertiesFile(propertiesFilename);
		logger.info(() -> "Path: " + propertiesFile.getPath());
		return propertiesFile.exists() ? loadFernetFile(propertiesFile) : createFernetFile(propertiesFile);
	}

	public static boolean fileIsOld(final String path, final long oldSecs) {
		// return (System.currentTimeMillis() - new File(path).lastModified())/1000 >
		// oldSecs;
		return false;
	}

	/*
	 * If file does not exist, create the file and return FernetDTO
	 * If file is older than oldSecs, update file and return FernetDTO
	 */
	public static synchronized FernetDTO getFernetDTO(final String propFilePath, final int oldSecs) throws IOException {
		final String path = propFilePath;
		logger.info(() -> "Path: " + path);
		return fileExist(path) && !fileIsOld(path, oldSecs) ? loadFernetFile(new File(path)) : createFernetFile(path);
	}

	public static long getUnixEpochSecond() {
		return Instant.now().getEpochSecond();
	}

	/**
	 * This method should be called with the DTO returned from validateLicense
	 *
	 * @param licenseDTO
	 *                   - the DTO returned from validateLicense()
	 * @return true or false
	 *         <p>
	 * @author spgdev@spenego.com - Jun 29, 2019
	 */
	private static boolean licenseHasExpired(final LicenseKeyDTO licenseDTO) {
		return (licenseDTO == null) || ((licenseDTO.getExpirationEpoch() != null)
				&& (getUnixEpochSecond() > licenseDTO.getExpirationEpoch()));
	}

	/**
	 * Validate a Spenego license. A license is valid if the following
	 * conditions. are true: - It is signed by Spenego private key.
	 *
	 * If a license is properly signed, the caller can use the information in
	 * DTO to restrict access to API. For example, caller can check max users,
	 * or if license has expired etc to make decision before serving a API
	 * request.
	 *
	 * @param base64License
	 *                      - from database
	 * @param publicKey
	 *                      - public key of signing pair
	 * @return LicenseDTO
	 *         <p>
	 * @author spgdev@spenego.com - Jun 23, 2019
	 *
	 *         Stop using jackson for serializing YAMLl icense. It pulled
	 *         snakeyaml for android which was causing serialization issue on
	 *         deploy host. So I am going to use the latest version of snakeyaml
	 *         directly. When I tried to use snakeyaml to map YAML license to
	 *         LicenseKeyDTO, it turned out that the DTO was not a valid Java
	 *         bean and snakeyaml threw serialization exception. A valid java
	 *         bean must have a empty constructor and simple getters and
	 *         setters. The setters were not void. Update: spgdev@spenego.com -
	 *         Jun 29, 2019
	 */
	public static LicenseKeyDTO validateLicense(final String base64LicenseYaml, final byte[] publicKey) {
		if (publicKey == null) {
			logger.warn(() -> "public key is null");
			throw new ServerSideException("Public key is null");
		}

		// Map YAML to DTO. The DTO must be a valid java bean
		final Yaml yaml = new Yaml();

		try {
			final LicenseKeyDTO licenseDTO = yaml.loadAs(new String(decodeFromBase64(base64LicenseYaml)),
					LicenseKeyDTO.class);

			if (licenseDTO.getSignature() == null) {
				throw new ServerSideException("No signature found");
			}

			// Verify Signature
			final byte[] signature = decodeFromBase64(licenseDTO.getSignature());
			licenseDTO.setSignature(null);
			SodiumLibrary.cryptoSignVerifyDetached(signature, yaml.dump(licenseDTO).getBytes(), publicKey);
			licenseDTO.setHasExpired(licenseHasExpired(licenseDTO));
			return licenseDTO;
		} catch (final YAMLException ex) {
			logger.error(() -> "Unable to decode license: " + base64LicenseYaml);
			logger.exception(ex);
			throw new ServerSideException("The Obidos license is corrupt. Contact support.");
		} catch (final SodiumLibraryException e) {
			throw new ServerSideException("Invalid License: " + e.getMessage());
		}
	}

	public static ObidosConfiguration loadObidosConfigFile() throws ServerSideException {
		try {
			ObidosConfiguration config = ObidosConfiguration.getInstance();
			config.getPropConfig().reload();
			return config;
		} catch (ConfigurationException e) {
			throw new ServerSideException("Could not load configuration file: " + e.getMessage());
		}
	}

	public static boolean isMac() {
		return Platform.isMac();
	}

	public static boolean isWindows() {
		return Platform.isWindows();
	}

	public static String getJdbcXMLFilePath() {
		return isWindows() ? ObidosConstants.JDBC_XML_FILE_WINDOWS : ObidosConstants.JDBC_XML_FILE_UNIX;
	}

	public static String getJdbcXMLMFilePath() {
		return isWindows() ? ObidosConstants.JDBC_XMLM_FILE_WINDOWS : ObidosConstants.JDBC_XMLM_FILE_UNIX;
	}

	public static String getAdLdapPropersFilePath() {
		return isWindows() ? ObidosConstants.AD_LDAP_PROPERTIES_WINDOWS : ObidosConstants.AD_LDAP_PROPERTIES_UNIX;
	}

	public static String getFernetRotatingPropertiesFilePath() {
		return isWindows() ? ObidosConstants.FERNET_ROTATING_WINDOWS : ObidosConstants.FERNET_ROTATING_UNIX;
	}

	public static String getNginxStagedCertPath() {
		return isWindows() ? ObidosConstants.NGINX_TEMP_CERT_PATH_WINDOWS : ObidosConstants.NGINX_TEMP_CERT_PATH_UNIX;
	}

	public static String getNginxStagedKeyPath() {
		return isWindows() ? ObidosConstants.NGINX_TEMP_PRIVATE_KEY_PATH_WINDOWS
				: ObidosConstants.NGINX_TEMP_PRIVATE_KEY_PATH_UNIX;
	}

	public static String getNginxDefaultFilePath() {
		return ObidosConstants.NGINX_DEFAULT_FILE;
	}

	public static String getCacertsKeyStorePath() {
		return ObidosConstants.SYSTEM_CACERTS;
	}

	public static String getCacertsKeyStorePassword() throws IOException {
		try {
			return getAdLdapProperties().getProperty("keystore_password");
		} catch (IOException e) {
			throw new IOException("Could not read AD/LDAP properties file: " + e.getMessage());
		}
	}

	// using apache commons config 1.10
	public static XMLConfiguration loadXMLFile(final String path) throws ConfigurationException {
		logger.info(() -> "Load xml file: " + path);
		final XMLConfiguration xmlConfig = new XMLConfiguration(path);
		xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
		xmlConfig.load();
		return xmlConfig;
	}

	/*
	 * The password in jdbc.xml file is encrypted with the key derived from
	 * master password in jdbcm.xml file. Decrypt the password and return it
	 */
	private static String decryptJdbcPassword(final String encryptedPassword)
			throws ConfigurationException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		return SecureCompatibleEncryptionExamples.decryptString(encryptedPassword,
				loadXMLFile(getJdbcXMLMFilePath()).getString("password"));
	}

	/**
	 * The password in jdbc.xml file is encrypted with the key derived from
	 * master password in jdbcm.xml file. Decrypt the password and put it in
	 * password property.
	 *
	 * @return Properties can be used in Spring datasource
	 * @throws ConfigurationException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchPaddingException
	 *                                            <p>
	 * @author spgdev@spenego.com - Feb 7, 2020
	 */
	public static Properties getJdbcProperties()
			throws ConfigurationException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		XMLConfiguration xmlConfig = loadXMLFile(getJdbcXMLFilePath());

		Properties props = new Properties();
		String key = "jdbc.driverClassName";
		props.put(key, xmlConfig.getString("driverClassName"));

		key = "jdbc.url";
		props.put(key, xmlConfig.getString("url"));

		key = "jdbc.username";
		props.put(key, xmlConfig.getString("username"));

		// password is encrypted, decrypt it and set to jdbc.password property
		key = "jdbc.password";
		String password = decryptJdbcPassword(xmlConfig.getString("password"));
		props.put(key, password);

		return props;
	}

	public static Properties getAdLdapProperties() throws IOException {
		try (InputStream inStream = new FileInputStream(getAdLdapPropersFilePath())) {
			Properties props = new Properties();
			props.load(inStream);
			return props;
		}
	}

	public static long copy(final InputStream is, final OutputStream os) throws IOException {
		return IOUtils.copyLarge(is, os);
	}

	public static void writeToFile(final String filePath, final String content) throws IOException {
		try (FileWriter writer = new FileWriter(filePath)) {
			writer.write(content);
			writer.write("\n");
		}
	}

	public static byte[] encryptWithPublicKey(final byte[] plainText, final byte[] publicKey)
			throws SodiumLibraryException {
		return SodiumLibrary.cryptoBoxSeal(plainText, publicKey);
	}

	public static byte[] randomBytes(int size) {
		return SodiumLibrary.randomBytes(size);
	}

	public static byte[] readFile(final String path) throws IOException {
		File file = new File(path);
		byte[] bytesArray = new byte[(int) file.length()];

		try (final FileInputStream fis = new FileInputStream(file)) {
			int br = fis.read(bytesArray);
			if (br != bytesArray.length) {
				logger.warn(() -> "Attempted to read " + bytesArray.length + " but only read " + br);
			}
		}
		return bytesArray;
	}

	private static <T> String getResourcePathname(final Class<T> clazz, final String dirName, final String filename) {
		final String pathname = dirName + File.separator + filename;
		final File file = new File(clazz.getClassLoader().getResource(pathname).getFile());

		return file.getParent().toString() + File.separator + filename;
	}

	public static String readTextFile(final String path) throws IOException {
		return new String(readFile(path), StandardCharsets.UTF_8);
	}

	public static <T> String readTextFile(final Class<T> clazz, final String dirName, final String filename) throws IOException {
		return readTextFile(getResourcePathname(clazz, dirName, filename));
	}

	public static byte[] cryptoBoxSealOpen(final byte[] cipherText, final SodiumKeyPair keyPair)
			throws SodiumLibraryException {
		if (cipherText == null) {
			throw new ServerSideException("Cipher text is empty");
		}
		if (keyPair == null) {
			throw new ServerSideException("Key pair can not be null");
		}
		if (keyPair.getPublicKey() == null) {
			throw new ServerSideException("Public key can not be null");
		}
		if (keyPair.getPrivateKey() == null) {
			throw new ServerSideException("Private key can not be null");
		}
		return SodiumLibrary.cryptoBoxSealOpen(cipherText, keyPair.getPublicKey(), keyPair.getPrivateKey());
	}

	public static String makeAdLdapAliasName(final String commonName) {
		return ObidosConstants.AD_LDAP_ALIAS_STARTS_WITH + "-" + commonName;
	}

	private static Validator<String> validator = new StringValidator() {
		public TemporalAmount getTimeToLive() {
			return Duration.ofHours(240); // 10 days = 240 hours
		}
	};

	// validate fernet token, decrypt and return the payload on success
	// return null on failure
	public static final String validateFernetToken(final FernetDTO dto,
			final HttpServletRequest req,
			final HttpServletResponse res,
			final int tokenLifeTime) throws IOException {
		final String EMSG = "Not Authorized";
		final String X_ERROR_MESSAGE = "X-Error-Message";

		if (dto == null) {
			// ref:
			// https://www.javamex.com/tutorials/servlets/http_status_code.shtml
			String emsg = "Invalid Request. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader(X_ERROR_MESSAGE, EMSG);
			res.sendError(HttpServletResponse.SC_FORBIDDEN, EMSG);
			return null;
		}

		// Create the Fernet key from the one we saved in our properties file
		// in base64 format
		final Key fernetKey = new Key(dto.getKey());

		// get the fernet token from the request
		String stringToken = req.getParameter("token");
		if (stringToken == null) {
			String emsg = "No token in request. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader(X_ERROR_MESSAGE, EMSG);
			res.sendError(HttpServletResponse.SC_FORBIDDEN, EMSG);
			return null;
		}

		logger.info(() -> "Validating Token");
		// create the Fernet token object
		Token token = null;
		try {
			token = Token.fromString(stringToken);
		} catch (Exception e) {
			logger.info(() -> "Exception received: " + e.getMessage());
			String emsg = "Invalid Token. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader(X_ERROR_MESSAGE, EMSG);
			res.sendError(HttpServletResponse.SC_FORBIDDEN, EMSG);
			return null;
		}
		// check if fernet token sha1 hex file exist
		// if token file does not exist, don't bother to validate.
		// this file gets created when token is obtained before upload request
		// was to the upload servlet
		if (fernetTokenFileExists(stringToken)) {
			logger.info(() -> "Fernet sha1 hex file exists, Will allow access!");
			logger.info(() -> "Removing sha1 hex file");
			removeFernetTokenFile(stringToken);
		} else {
			logger.error(() -> "Ferent SHA1 hex file does not exist. Access defined.");
			res.setHeader(X_ERROR_MESSAGE, EMSG);
			res.sendError(HttpServletResponse.SC_FORBIDDEN, EMSG);
			return null;
		}

		try {
			// pay load has no secret right now
			final String payload = token.validateAndDecrypt(fernetKey, validator);
			logger.info(() -> "Token is valid");
			// now check for expiration
			final long tokenEpoch = token.getTimestamp().getEpochSecond(); // seconds
			final long nowEpoch = Instant.now().getEpochSecond();

			if ((nowEpoch - tokenEpoch) > tokenLifeTime) {
				String emsg = "Token has expired. But access is granted because token semaphore file exists";
				logger.info(() -> emsg);
			}

			return payload;
		} catch (TokenValidationException e) {
			logger.info(() -> "Token validation failed: " + e.getMessage());
			String emsg = "Invalid Token. Access Denied!";
			logger.info(() -> emsg);
			res.setHeader(X_ERROR_MESSAGE, EMSG);
			res.sendError(HttpServletResponse.SC_FORBIDDEN, EMSG);
			return null;
		}
	}

	public static String fileExtension(final String fileName) {
		int index = fileName.lastIndexOf('.');
		return (index > 0) ? fileName.substring(index + 1) : "";
	}

	private static final FileAttribute<Set<PosixFilePermission>> DIRECTORY_ATTRIBUTE = PosixFilePermissions
			.asFileAttribute(PosixFilePermissions.fromString("rwx------"));

	public static boolean directoryExists(final String dirPath) {
		return new File(dirPath).isDirectory();
	}

	/**
	 * Creates the directory specified by parentDir/childDir.
	 *
	 * @param parentDir
	 * @param childDir
	 * @return
	 * @throws IOException If unable to create directory.
	 */
	public static File createSecuredDirectory(final String parentDir, final String childDir) throws IOException {
		final File directory = new File(parentDir, childDir);

		if (!directory.exists()) {
			final File parent = new File(directory.getParent());

			if (!parent.exists()) {
				logger.info(() -> "Parent directory " + directory.getParent() + " does not yet exist.");
				createSecuredDirectory(null, directory.getParent());
			}
			logger.info(() -> "Creating directory " + directory.getPath());
			Files.createDirectory(Paths.get(directory.getPath()), DIRECTORY_ATTRIBUTE);
		}

		return directory;
	}

	/**
	 * Creates the specified directory if is does not exist.
	 * 
	 * @throws IOException If unable to create directory.
	 */
	public static File createSecuredDirectory(final String directory) throws IOException {
		return createSecuredDirectory(null, directory);
	}

	/**
	 * Return size of a directory
	 *
	 * @param directory
	 * @return size in bytes
	 * @throws IOException
	 *                     <p>
	 * @author spgdev@spenego.com - Dec 21, 2020
	 */
	public static long directorySize(final String directory) throws IOException {
		final File directoryFile = new File(directory);
		if (!directoryFile.exists()) {
			throw new IOException("Directory: " + directory + " does not exist");
		}
		return FileUtils.sizeOfDirectory(directoryFile);
	}

	public static String spenegoTmpDir() {
		if (directoryExists(SPENEGO_TMP_DIR)) {
			return SPENEGO_TMP_DIR;
		}
		return "/tmp";
	}

	public static String fernetTokenFilePath(final String base64ToekString) {
		String path = spenegoTmpDir() + File.separator + fernetTokenFileName(base64ToekString);
		logger.info(() -> "Token file path: " + path);
		return path;
	}

	public static String fernetTokenFileName(final String base64TokenString) {
		return DigestUtils.sha1Hex(base64TokenString);
	}

	public static void createFerenetTokenFile(final String base64TokenString, final String val) throws IOException {
		String dir = spenegoTmpDir();
		createSecuredDirectory(dir);
		String filename = fernetTokenFileName(base64TokenString);
		String filePath = dir + File.separator + filename;
		logger.info(() -> "Creating token semaphore file: " + filePath);
		writeToFile(filePath, "0");
	}

	public static boolean fernetTokenFileExists(final String base64TokenString) {
		return fileExist(fernetTokenFilePath(base64TokenString));
	}

	public static void removeFernetTokenFile(final String base64ToeknString) {
		deleteFile(fernetTokenFilePath(base64ToeknString));
	}

	public static String decodeQRCodeImage(final BufferedImage bufferedImage) throws ServerSideException {
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Map<DecodeHintType, Object> hints = null;
		Result result = null;

		// Try# 1: without any hint
		try {
			result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			logger.error(() -> "ERROR: Could not decode QR Code image without any hints:" + e.getMessage());
		}

		// Try# 2: with hint DecodeHintType.PURE_BARCODE
		try {
			logger.info(() -> "Trying to decode with hint pure barcode ...");
			hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
			hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
			result = reader.decode(bitmap, hints);
			logger.info(() -> "OK: Decoding succeeded with hint pure barcode.");
			return result.getText();
		} catch (NotFoundException | ChecksumException | FormatException e) {
			logger.error(() -> "ERROR: Could not decode QR Code image with hint PURE BARCODE:" + e.getMessage());
			hints = null;
		}
		// Try#3 : with hint DecodeHintType.TRY_HARDER
		try {
			logger.info(() -> "Trying to decode with hint try harder ...");
			hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
			hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
			result = reader.decode(bitmap, hints);
			logger.info(() -> "OK: Decoding succeeded with try harder.");
			return result.getText();
		} catch (NotFoundException | ChecksumException | FormatException e) {
			logger.info(() -> "ERROR: Could not decode QR Code image with hint TRY HARDER:" + e.getMessage());
			hints = null;
		}

		// Try# 4: with hint DecodeHintType.POSSIBLE_FORMATS
		// Don't try that
		/*
		 * try
		 * {
		 * logger.info(() -> "Trying to decode with hint possible formats ...");
		 * hints = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
		 * hints.put(DecodeHintType.POSSIBLE_FORMATS, Boolean.TRUE);
		 * result = reader.decode(bitmap, hints);
		 * logger.info(() -> "OK: Decoding succeeded with possible formats.");
		 * return result.getText();
		 * } catch (NotFoundException | ChecksumException | FormatException e)
		 * {
		 * // TODO Auto-generated catch block
		 * logger.info(() ->
		 * "ERROR: Could not decode QR Code image with hint POSSIBLE FORMATS:" +
		 * e.getMessage());
		 * hints = null;
		 * }
		 */
		throw new ServerSideException("Could not decode QR Code Image");

	}

	// Try to decode with various type hints
	// return the decoded text on success
	public static String decodeQRCodeImage(final File qrCodeImage) throws ServerSideException {
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(qrCodeImage);
		} catch (IOException e) {
			System.out.println("Could not decode qrcode image: " + e);
			throw new ServerSideException("Could not read QR Code Image: " + e.getMessage());
		}
		return decodeQRCodeImage(bufferedImage);
	}

	// Return the text if the QR Code image can be decoded
	public static String decodeQRCodeImage(final String imageFilePath) throws ServerSideException {
		return decodeQRCodeImage(new File(imageFilePath));
	}

	public static boolean sendSMSMessageWithTwilio(final String accountSID,
			final String authToken,
			final String toPhoneNumber,
			final String twilioPhoneNumber,
			final String smsMessage) throws ApiException {
		Twilio.init(accountSID, authToken);

		Message message = Message.creator(
				new PhoneNumber(toPhoneNumber),
				new PhoneNumber(twilioPhoneNumber),
				smsMessage).create();

		String messageId = message.getSid();
		logger.info(() -> "Twilio SMS message id: " + messageId);
		return true;
	}

	// https://dashboard.nexmo.com/getting-started/sms
	public static boolean sendSMSMessageWithVonage(final String apiSecret,
			final String apiKey,
			final String recipientPhoneNumber,
			final String vonagePhoneNumber,
			final String smsMessage) throws ServerSideException {
		final VonageClient client = VonageClient.builder().apiKey(apiKey).apiSecret(apiSecret).build();
		final TextMessage message = new TextMessage(vonagePhoneNumber, recipientPhoneNumber, smsMessage);
		final SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
		if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
			return (true);
		}
		throw new ServerSideException("Could not send SMS via Vonage: " + response.getMessages().get(0).getErrorText());
	}

	//
	// Send SMS text message via Twilio or Vonage
	//
	public static boolean sendSmsMessage(final SmsKeyDTO smsKeyDTO, final String message,
			final String recipientPhoneNumber) throws ServerSideException {
		logger.info(() -> "sendSmsMessage: provider: " + smsKeyDTO.getSmsProvider());
		switch (smsKeyDTO.getSmsProvider()) {
			case ObidosConstants.SMS_PROVIDER_TWILIO:
				logger.info(() -> "sendSmsMessage: Sending SMS using Twilio");
				return sendSMSMessageWithTwilio(smsKeyDTO.getTwilioAccountSid(),
						smsKeyDTO.getTwilioAuthToken(),
						recipientPhoneNumber,
						smsKeyDTO.getSmsProviderSpecifiedPhoneNumber(),
						message);
			case ObidosConstants.SMS_PROVIDER_VONAGE:
				logger.info(() -> "sendSmsMessage: Sending SMS using Vonage");
				return sendSMSMessageWithVonage(
						smsKeyDTO.getApiSecret(),
						smsKeyDTO.getApiKey(),
						recipientPhoneNumber,
						smsKeyDTO.getSmsProviderSpecifiedPhoneNumber(),
						message);
			default:
				throw new ServerSideException("Could not send SMS, unknown SMS Provider");
		}

	}
	
	/**
	 * Checks if a phone number can be converted to E.164 format. The number
	 * must be a valid E.164 validated number in order to send SMS
	 * 
	 * @param phoneNumber
	 * @return E.164 formatted phone number on success
	 * <p>
	 * @author spgdev@spenego.com - Jul 4, 2024
	 */
	public static String validatePhoneNumber(final String phoneNumber) throws ServerSideException {
		if (phoneNumber == null) {
			return "";
		}
		try {
			return CountryCodeUtil.formatToE164(phoneNumber);
		} catch (final Exception e) {
			throw new ServerSideException("Not a valid E.164 phone number: " + phoneNumber);
		}
	}

	public static float calculateRawEntropy(String password) {
		int L = password.length();
		int N = calculateCharacterSetSize(password);

		return (float) (L * (Math.log(N) / Math.log(2)));
	}

	private static int calculateCharacterSetSize(String password) {
		boolean hasLowercase = false;
		boolean hasUppercase = false;
		boolean hasDigits = false;
		boolean hasSpecialChars = false;

		for (char c : password.toCharArray()) {
			if (Character.isLowerCase(c))
				hasLowercase = true;
			else if (Character.isUpperCase(c))
				hasUppercase = true;
			else if (Character.isDigit(c))
				hasDigits = true;
			else
				hasSpecialChars = true;
		}

		int N = 0;
		if (hasLowercase)
			N += 26;
		if (hasUppercase)
			N += 26;
		if (hasDigits)
			N += 10;
		if (hasSpecialChars)
			N += 33; // Assuming a set of common special characters

		return N;
	}
	
	public static boolean isSingleDigit(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}

		// Check if the string has only one character
		if (str.length() == 1) {
			// Check if that character is a digit
			return Character.isDigit(str.charAt(0));
		}

		// If the string starts with a minus sign, check if it's followed by a
		// single digit
		if (str.length() == 2 && str.startsWith("-")) {
			return Character.isDigit(str.charAt(1));
		}

		return false;
	}

	public static byte[] createQRCodeImageBytes(final String qrCodeData) throws WriterException, ServerSideException {
		try {
			// tests showed a QR length of apx. 560 bytes for 200x200 image
			final ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream(600);
			MatrixToImageWriter.writeToStream(new QRCodeWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200),
					"PNG", pngOutputStream);

			return pngOutputStream.toByteArray();
		} catch (final IOException e) {
			throw new ServerSideException("Failed to write Matrix Image to Stream: " + e.getMessage());
		}
	}


	/**** C O D E   G R A V E Y A R D ****/ // The following code may be useful,
											// but it is no longer used.

	/**
	 * Create a stream of PDF from asciidoc files in a directory
	 * 
	 * @param baseDir
	 * @param masterFile
	 * @return
	 * @throws IOException
	 * <p>
	 * @author spgdev@spenego.com - Jul 29, 2024
	 */
	/** DO NOT USE THE PIECE OF GARBAGE CALLED AsciidoctorJ **/
	/** It simply does not work from servlet */
	/** Leaving the code for future reference **/
	/*
	public static ByteArrayOutputStream generatePDFFromAsciidoc(String baseDir, String masterFile) throws IOException
	{
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		final String backend = "pdf";

		Attributes attributes = Attributes
				.builder()
				.backend(backend)
				.build();

		Options options = Options.builder()
				.safe(SafeMode.UNSAFE)
				.baseDir(new File(baseDir))
				.backend(backend)
				.attributes(attributes)
				.build();

		String content = new String(Files.readAllBytes(Paths.get(baseDir, masterFile)));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] pdfContent = asciidoctor.convert(content, options).getBytes();
		outputStream.write(pdfContent);

		return outputStream;
	}
	*/
	
	/**
	 * Create a PDF file from asciidoc files in a directory
	 * 
	 * @param baseDir
	 * @param masterFile
	 * @param outputFilePath
	 * @throws IOException
	 * <p>
	 * @author spgdev@spenego.com - Jul 29, 2024
	 */
	/*
	public static void generatePDFFromAsciidoc(final String baseDir, final String masterFile, final String outputFilePath) throws ServerSideException
	{
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		try {

			File inputFile = Paths.get(baseDir, masterFile).toFile();
            File outputFile = new File(outputFilePath);
            
            logger.info(()-> "Input file: " + inputFile.getAbsolutePath());
            logger.info(()-> "Output file: " + outputFile.getAbsolutePath());

			Attributes attributes = Attributes.builder()
					.backend("pdf")
					.build();

			Options options = Options.builder().safe(SafeMode.UNSAFE)
					.baseDir(new File(baseDir))
					.attributes(attributes)
					.backend("pdf") // must set explicitly
					.toFile(outputFile)
					.build();
			
			asciidoctor.convertFile(inputFile, options);
		} catch(Exception e) {
			logger.error(() -> "Caught exception : " + e.getMessage());
			throw new ServerSideException("Could not create PDF file: " + e);
		} finally {
			asciidoctor.shutdown();
		}
	}
	*/
	/**
	 *
	 * @param format
	 *               Valid formats are: mm/dd/yyyy Example: 02/27/2020 - Feb 27
	 *               2020 dd/mm/yyyy - 27/02/2020 mm-dd-yyyy - 02-27-2020
	 *               dd-mm-yyyy - 27-02-2020 mm.dd.yyyy dd.mm.yyyy
	 *
	 *               Doc from date picker widget we user: String. Default:
	 *               “mm/dd/yyyy”
	 *
	 *               The date format, combination of d, dd, D, DD, m, mm, M, MM,
	 *               yy, yyyy.
	 *
	 *               d, dd: Numeric date, no leading zero and leading zero,
	 *               respectively. Eg, 5, 05. D, DD: Abbreviated and full weekday
	 *               names, respectively. Eg, Mon, Monday. m, mm: Numeric month, no
	 *               leading zero and leading zero, respectively. Eg, 7, 07. M, MM:
	 *               Abbreviated and full month names, respectively. Eg, Jan,
	 *               January yy, yyyy: 2- and 4-digit years, respectively. Eg, 12,
	 *               2012.
	 *
	 * @throws ServerSideException
	 *                             <p>
	 * @author spgdev@spenego.com - Feb 27, 2020
	 *
	 *         public static void validateDateFormat(final String format) throws
	 *         ServerSideException { if (format == null) { throw new
	 *         ServerSideException("Date format is null"); } if
	 *         (format.equals("mm/dd/yyyy") || format.equals("dd/mm/yyy") ||
	 *         format.equals("mm-dd-yyyy") || format.equals("dd-mm-yyyy") ||
	 *         format.equals("mm.dd.yyyy") || format.equals("dd.mm.yyyy")) {
	 *         return ; } throw new ServerSideException("Invalid date format: "
	 *         + format); }
	 */
	/*
	 * public static byte[] decodeFromBase64(final byte[] base64) { try { return
	 * Base64.getDecoder().decode(base64); } catch (IllegalArgumentException e)
	 * { throw new ServerSideException("Could not decode base64: " +
	 * e.getMessage()); } }
	 */

	/*
	 * public static byte[] stringToBytes(final String str) { return
	 * str.getBytes(StandardCharsets.UTF_8); // needs jdk 1.7+ }
	 */

	/**
	 * fast case insensitive comparison Note: it is very much like apache
	 * commons lang3's method
	 *
	 * @param src
	 * @param what
	 * @return
	 *         <p>
	 * @author spgdev@spenego.com - Jul 9, 2017 Taken from:
	 *         https://stackoverflow.com/questions/86780/how-to-check-if-a-string-contains-another-string-in-a-case-insensitive-manner-in
	 *         public static boolean containsIgnoreCase(String src, String what)
	 *         { final int length = what.length(); if (length == 0) return true;
	 *         // Empty string is contained
	 *
	 *         final char firstLo = Character.toLowerCase(what.charAt(0)); final
	 *         char firstUp = Character.toUpperCase(what.charAt(0));
	 *
	 *         for (int i = src.length() - length; i >= 0; i--) { // Quick check
	 *         before calling the more expensive regionMatches() // method:
	 *         final char ch = src.charAt(i); if (ch != firstLo && ch !=
	 *         firstUp) continue;
	 *
	 *         if (src.regionMatches(true, i, what, 0, length)) return true; }
	 *
	 *         return false; }
	 */

	/**
	 * return the value from JSON.
	 *
	 * @param jsonObj
	 * @param key
	 * @return The value if it exists, otherwise "" private static String
	 *         getStringValueFromJson(final JSONObject jsonObj, final String
	 *         key) { final String val = (String) jsonObj.get(key); return (val
	 *         == null) ? "" : val; }
	 */
	/**
	 * @Deprecated
	 *
	 * @param jsonStr
	 * @return
	 *         <p>
	 * @author spgdev@spenego.com - Jul 1, 2018 public static
	 *         NotificationTemplateJSONDTO parseJSONTemplateOld(final String
	 *         jsonStr) { try { final JSONObject jsonObj = (JSONObject) new
	 *         JSONParser().parse(jsonStr);
	 *
	 *         return new
	 *         NotificationTemplateJSONDTO(getStringValueFromJson(jsonObj,
	 *         "product_name"), getStringValueFromJson(jsonObj, "title"),
	 *         getStringValueFromJson(jsonObj, "subject"),
	 *         getStringValueFromJson(jsonObj, "hello"),
	 *         getStringValueFromJson(jsonObj, "action_url"),
	 *         getStringValueFromJson(jsonObj, "html_message"),
	 *         getStringValueFromJson(jsonObj, "button_title"),
	 *         getStringValueFromJson(jsonObj, "button_trouble"),
	 *         getStringValueFromJson(jsonObj, "text_message"),
	 *         getStringValueFromJson(jsonObj, "contact"),
	 *         getStringValueFromJson(jsonObj, "footer")); } catch (final
	 *         ParseException e) { throw new ServerSideException("Failed to
	 *         parse JSON template: " + e.getMessage()); } }
	 */
	/**
	 * create JSON bytes from NotificationTempalteJSONDTO
	 *
	 * @param dto
	 * @return
	 *         <p>
	 * @author spgdev@spenego.com - Jul 1, 2018
	 *
	 *         public static EmailMessageTemplateDTO
	 *         mkEmailMessageTemplateDTO(NotificationTemplateJSONDTO dto) {
	 *         return new EmailMessageTemplateDTO(); }
	 */

	/*
	 * //
	 * https://stackoverflow.com/questions/15781174/this-getthreadlocalrequest-
	 * returns-null-in-gwt public static HttpSession
	 * getHttpSessionFromRequestAttributes() { final ServletRequestAttributes
	 * requestAttributes = (ServletRequestAttributes)
	 * RequestContextHolder.currentRequestAttributes(); return
	 * (requestAttributes != null) ? requestAttributes.getRequest().getSession()
	 * : null; }
	 */
	/*
	 * // gzip compress a string, returns compressed array of bytes // adapted
	 * from: // https://gist.github.com/yfnick/227e0c12957a329ad138 public
	 * static byte[] compressGzip(final String data) throws IOException {
	 * try(final ByteArrayOutputStream bos = new
	 * ByteArrayOutputStream(data.length())) { GZIPOutputStream gzip = new
	 * GZIPOutputStream(bos, true); gzip.write(data.getBytes()); gzip.flush();
	 * return bos.toByteArray(); } }
	 *
	 * // gunzip compressed bytes and return uncompressed strings // adapted
	 * from: // https://gist.github.com/yfnick/227e0c12957a329ad138 public
	 * static String uncompressGzip(final byte[] compressed) throws IOException
	 * { return new String(IOUtils.toByteArray(new GZIPInputStream(new
	 * ByteArrayInputStream(compressed))), "UTF-8"); }
	 *
	 * public static String dumpObjectToString(Object o) { return
	 * ToStringBuilder.reflectionToString(o, ToStringStyle.MULTI_LINE_STYLE); }
	 */
	/*
	 * public static Date epochToDate(long epochSecond) { return new
	 * Date(epochSecond * 1000L); }
	 */
	/*
	 * private static void validatePassDoesNotContain(final String pass, final
	 * String contains, final MessageResolver resolver, final PasswordValidator
	 * validator, final PasswordData passData) throws ServerSideException {
	 * RuleResult result = validator.validate(passData); if (!result.isValid())
	 * { logger.info(() -> "failed validation"); List<String> msgList =
	 * validator.getMessages(result); logger.info(() -> "Exception: " +
	 * msgList); String msg = String.join(" *<br/> ", msgList); throw new
	 * ServerSideException(msg); } }
	 */
}
