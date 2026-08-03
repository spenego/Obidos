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

import static com.spenego.Obidos.server.utils.ServerUtils.encodeToBase64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import com.spenego.Obidos.server.actions.TwoFactorAuthenticationActions;
import com.spenego.Obidos.server.model.BaseModel;
import com.spenego.Obidos.server.model.User;
import com.spenego.Obidos.server.operations.Operations;
import com.spenego.Obidos.server.security.PassphraseHash;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ServerUtils;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;
import com.spenego.Obidos.shared.exceptions.ObidosException;
import com.spenego.Obidos.shared.exceptions.ServerSideException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

/**
 *
 * @author Mike Morgan
 *
 */
public final class TwoFactorAuthenticationActionsImpl extends CryptoActions<BaseModel>
		implements TwoFactorAuthenticationActions
{
	private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationActionsImpl.class);

	public TwoFactorAuthenticationActionsImpl()
	{
		super("2fa_crypto.properties", "Two Factor Authentication");
	}

	@Override
	protected final Operations<BaseModel> getOperations()
	{
		return null;
	}

	@Override
	protected final Logger getLogger()
	{
		return logger;
	}

	@Override
	protected final Integer getAuditDeleteAction()
	{
		return null;
	}

	@Override
	protected final String elementName()
	{
		return "two factor auth";
	}

	private void updateUser(final User user, final Consumer<User> consumer)
	{
		final User newUser = new User(user.getId(), user.getVersion() + 1);
		consumer.accept(newUser);
		updateUser(newUser);
	}

	public static byte[] createQRCodeImageBytes(final String qrCodeData) throws WriterException, ServerSideException
	{
		try
		{
			// tests showed a QR length of apx. 560 bytes for 200x200 image
			final ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream(600);
			MatrixToImageWriter.writeToStream(new QRCodeWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200),
					"PNG", pngOutputStream);

			return pngOutputStream.toByteArray();
		} catch (final IOException e)
		{
			throw new ServerSideException("Failed to write Matrix Image to Stream: " + e.getMessage());
		}
	}

	@Override
	public TwoFactorDTO create2FASecret(final User user, final PassphraseHash passphraseHash)
	{
		logger.info(() -> "create2FASecret: Validating passphrase");
		validatePassphraseHash(user, passphraseHash); // ensures user has active
														// public key as an
														// additional level of
														// authentication
		logger.info(() -> "create2FASecret: creating Google Authenticator");
		final GoogleAuthenticatorKey key = new GoogleAuthenticator().createCredentials();
		logger.info(() -> "create2FASecret: Updating user");
		updateUser(user, newUser ->
		{
			newUser.setTwoFactorAuthSecret(encrypt(key.getKey()));
			newUser.setTwoFAPasswordResetEnabled(false);
		});
		logger.info(() -> "create2FASecret: User updated");

		try
		{
			final String issuer = getSystemConfig().getTwoFactorAuthIssuer();
			final String userEmail = user.getEmail1();
			final String otpUri = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuer, userEmail, key);

			return new TwoFactorDTO(issuer, userEmail, key.getKey(), otpUri,
					encodeToBase64(createQRCodeImageBytes(otpUri)), key.getScratchCodes());
		} catch (final WriterException e)
		{
			throw new ServerSideException("failed to create QR Code for 2FA: " + e.getMessage());
		}
	}

	public static String bytesToString(final byte[] bytes)
	{
		return ServerUtils.bytesToString(bytes);
	}

	public static Void authenticate2FA(final byte[] secretBytes, final byte[] codeBytes)
	{
		final String codeStr = bytesToString(codeBytes);
		final Integer code = ServerUtils.stringToInteger(codeStr);

		if (code == null)
		{
			throw new ServerSideException("Failed to convert " + codeStr + " to Integer");
		}

		if (!new GoogleAuthenticator().authorize(bytesToString(secretBytes), code))
		{
			throw new ServerSideException("Invalid Authenticator code");
		}

		return null;
	}

	@Override
	public Void authenticate2FA(final User user, final byte[] code)
	{
		return authenticate2FA(decrypt(user.getTwoFactorAuthSecret()).getBytes(), code);
	}

	@Override
	public Void enable2FA(final User user, final byte[] code)
	{
		authenticate2FA(user, code);
		updateUser(user, newUser -> newUser.setTwoFAPasswordResetEnabled(true));

		return null;
	}

	private static Map<String, String> queryStringMap(final String uris) throws URISyntaxException {
		// Parse Query Sting and create a map of key value
		Map<String, String> myMap = new HashMap<String, String>();
		@SuppressWarnings("deprecation")
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(uris), "UTF-8");
		for (NameValuePair param : params)
		{
			myMap.put(param.getName().toLowerCase(), param.getValue());
		}
		return myMap;
	}

	private static TwoFactorDTO validateTOTPUri(final String otpAuthUri) {
		TwoFactorDTO dto = new TwoFactorDTO();

		String scheme;
		String type;
		String secret;
		String uriStr = otpAuthUri.replaceAll(" ", "%20").trim();

		URI uri;
		try
		{
			uri = new URI(uriStr);
		} catch (URISyntaxException e)
		{
			throw new ObidosException("Invalid URI: " + e.getMessage());
		}

		Map<String, String> paramsMap;
		try
		{
			paramsMap = queryStringMap(uriStr);
		} catch (URISyntaxException e)
		{
			throw new ObidosException("Could not parse parameters. Invalid URI Syntax: " + e.getMessage());
		}

		scheme = uri.getScheme();
		if (scheme == null)
		{
			throw new ObidosException("No scheme found in URI, expected otpauth");
		}
		type = uri.getAuthority();
		if (type == null)
		{
			throw new ObidosException("No type found in URI, expected totp");
		}

		if (!scheme.equalsIgnoreCase("otpauth"))
		{
			throw new ObidosException("Invalid Scheme " + scheme + ". Expected otpauth");
		}

		if (!type.equalsIgnoreCase("totp"))
		{
			throw new ObidosException("Invalid Type " + type + ". Expected totp");
		}

		secret = paramsMap.get("secret");
		if (secret == null)
		{
			throw new ObidosException("Could not find secret in URI parameters");
		}
		dto.setSecret(secret);

		String issuer = paramsMap.get("issuer");
		if (issuer == null)
		{
			throw new ObidosException("Could not find issuer in URI parameters");
		}
		dto.setIssuser(issuer);

		// now calculate 2FA Code
		try
		{
			String code = TimeBasedOneTimePasswordUtil.generateCurrentNumberString(secret);
			code = code.replaceAll("...", "$0 ");
			dto.setTwoFACode(code);
		} catch (Exception e)
		{
			throw new ObidosException("Invalid TOTP OTP Auth URI: " + e.getMessage());
		}
		// Get email/account path path
		// path = /Acme%20 Co:jdoe@example.com
		String path = uri.getPath();
		if (path != null)
		{
			String[] sa = path.split(":");
			if (sa.length == 2)
			{
				dto.setUserEmail(sa[1]);
			}
		}
		dto.setOtpUri(otpAuthUri);

		return dto;
	}

	// Bug# 875
	// Apparently github create PURE type QR Code image. Now we'll try several
	// types one by one
	private static String getTOTPUri(final String dataUri) throws ServerSideException
	{
		final String b64Data           = dataUri.substring("data:image/png;base64,".length());
		final ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(b64Data));
		BufferedImage bufferedImage;
		try
		{
			bufferedImage = ImageIO.read(bis);
			// Bug# 875
			return ServerUtils.decodeQRCodeImage(bufferedImage);
		} catch (IOException e)
		{
			throw new ServerSideException("Could not decode QR Code image: " + e.getMessage());
		}
//		final HybridBinarizer hb       = new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(bis)));
//		return new QRCodeReader().decode(new BinaryBitmap(hb)).getText();
	}

	// Return 2FA OTPAUTH URI on success, throw exception on failure
	@Override
	public TwoFactorDTO decodeQRCodeImageDataUri(User user, String dataUri) {
		String totp_uri = "";
		totp_uri = getTOTPUri(dataUri); // ObidosException is not thrown from here, so message will be set if we receive one
		return validateTOTPUri(totp_uri);
		/*
		try {
			totp_uri = getTOTPUri(dataUri); // ObidosException is not thrown from here, so message will be set if we receive one
			// validate the totp uri, the image could be a QRCode image but
			// we only care if it is a TOTP URI
			return validateTOTPUri(totp_uri);
		} catch (NotFoundException | ChecksumException | FormatException e) {
			throw new ServerSideException("No valid TOTP OtpAuth URI found in the image");
		} catch (final IOException e) {
			throw new ServerSideException("Could not decode QR Code image: " + e.getMessage());
		} catch (final ObidosException e) {
			throw new ServerSideException("Invalid TOTP URI '" + totp_uri + "' in QR Code Image: " + e.getMessage());
		}
		*/
	}

	@Override
	public TwoFactorDTO generate2FACode(final User user, final String otpAuthUri)
	{
		final TwoFactorDTO dto = validateTOTPUri(otpAuthUri);
		// create a base64 image data uri from othAuthUri
		try {
			dto.setBase64QrImage(encodeToBase64(createQRCodeImageBytes(otpAuthUri)));
		} catch (ServerSideException e) { // ignore
 		} catch (WriterException e) { // ignore
		}
		return dto;
	}
}
