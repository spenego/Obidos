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

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @since  Obidos1.0
 * @author spgdev@spenego.com - Dec 30, 2017
 * - Make it AutoCloseable, better for security-sensitive data
 * - remove finalize, it is useless as it is deprecated in jdk 9
 * We're using jdk 11
 * spgdev - Dec-27-2025
 */
public final class TwoFactorDTO implements Clearable, Serializable, AutoCloseable {
	private static final long serialVersionUID = 1L;

	private String issuser;
	private String userEmail;
	private String secret;
	private String otpUri;
	private String base64QrImage;
	private String twoFACode;
	private byte[] secretBytes;
	private byte[] base64QrImageBytes;
	private ArrayList<Integer> scratchCodes;

	public TwoFactorDTO() {}

	public TwoFactorDTO(final String issuer, final String userEmail) {
		this.issuser = issuer;
		this.userEmail = userEmail;
	}

	public TwoFactorDTO(final String issuer, final String userEmail, final String secret, final String otpUri, final String base64QrImage, final Collection<Integer> scratchCodes) {
		this.issuser = issuer;
		this.userEmail = userEmail;
		this.secret = secret;
		this.otpUri = otpUri;
		this.base64QrImage = base64QrImage;
		this.scratchCodes = new ArrayList<>(scratchCodes);
	}

	@Override
	public void clear() {
		if (scratchCodes != null) {
			scratchCodes.clear();
			scratchCodes = null;
		}
		if (base64QrImageBytes != null) {
			Arrays.fill(base64QrImageBytes, (byte) 0);
			base64QrImageBytes = null;
		}
		if (secretBytes != null) {
			Arrays.fill(secretBytes, (byte) 0);
			secretBytes = null;
		}

		issuser = userEmail = secret = otpUri = base64QrImage = null;
	}

	// finalize is useless, it is deprecated in jdk 9, 
	// added AutoCloseable, which is better
	/*
	@Override
	protected void finalize() { // NOSONAR -- we know that this method may not be called timely
		clear();
	}
	*/

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getOtpUri() {
		return otpUri;
	}

	public void setOtpUri(final String otpUri) {
		this.otpUri = otpUri;
	}

	public String getBase64QrImage() {
		return base64QrImage;
	}

	public void setBase64QrImage(final String base64QrImage) {
		this.base64QrImage = base64QrImage;
	}

	public List<Integer> getScratchCodes() {
		return scratchCodes;
	}

	public void setScratchCodes(final Collection<Integer> scratchCodes) {
		this.scratchCodes = new ArrayList<>(scratchCodes);
	}

	public byte[] getSecretBytes() {
		return secretBytes;
	}

	public void setSecretBytes(byte[] secretBytes) {
		this.secretBytes = secretBytes;
	}

	public byte[] getBase64QrImageBytes() {
		return base64QrImageBytes;
	}

	public void setBase64QrImageBytes(byte[] base64QrImageBytes) {
		this.base64QrImageBytes = base64QrImageBytes;
	}

	public String getIssuser() {
		return issuser;
	}

	public void setIssuser(String issuser) {
		this.issuser = issuser;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getTwoFACode()
	{
		return twoFACode;
	}

	public void setTwoFACode(String twoFACode)
	{
		this.twoFACode = twoFACode;
	}

	// spgdev, Dec-27-2025
    @Override
    public void close() throws Exception
    {
        clear();
    }
}
