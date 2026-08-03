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

package com.spenego.Obidos.client.util;

import java.util.Date;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Small;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.rpc.TwoFactorService;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;

public class TwoFAModalData
{
	private Modal 				modal;
	private TextBox 			issuerTextBox;
	private TextBox 			accountTextBox;
	private ObidosPasswordBox 	secretTextBox;
    private ObidosMessageRow    messageRow;
    private Label 				countDownLabel;
    private Small               lastUpdatedHtml;
    private TextBox             twoFACodeTextBox;
    private TwoFactorDTO		twoFADTO;
    private Timer               refreshTimer;
    private Row                 qrCodeInfoRow; // row in adhoc template, not in Modal
    private Button				showHideButton;
    
    private Row                 qrCodeImageRow;
    private Image               qrCodeImage;
    private FormLabel           checkMarkLabel;
    private FormLabel           crossLabel;
    private ToggleSwitch		showQrCodeImageSwitch;

    private String issuer;
    private String account;
    private String base32Secret;
    
    private String otpAuthUri;
    
    private Timer twoFACodeRefreshTImer;

	private String sImageStyle = "1.5px solid #ddd";
	private String sOkImageStyle = "1.5px solid #58B957";
	private String sInvalidImageStype = "1.5px solid #ff0000";

	private final int COUNT_DOWN_START_SEC= 30;
	private final String DEFAULT_2FA_CODE = "??? ???";
	
	public Modal getModal()
	{
		return modal;
	}
	public void setModal(Modal modal)
	{
		this.modal = modal;
	}
	public TextBox getIssuerTextBox()
	{
		return issuerTextBox;
	}
	public void setIssuerTextBox(TextBox issuerTextBox)
	{
		this.issuerTextBox = issuerTextBox;
	}
	public TextBox getAccountTextBox()
	{
		return accountTextBox;
	}
	public void setAccountTextBox(TextBox accountTextBox)
	{
		this.accountTextBox = accountTextBox;
	}
	
	public ObidosPasswordBox getSecretTextBox()
	{
		return secretTextBox;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}
	public void setMessageRow(ObidosMessageRow messageRow)
	{
		this.messageRow = messageRow;
	}

	public Label getCountDownLabel()
	{
		return countDownLabel;
	}
	public void setCountDownLabel(Label countDownLabel)
	{
		this.countDownLabel = countDownLabel;
	}
	public TextBox getTwoFACodeTextBox()
	{
		return twoFACodeTextBox;
	}
	public void setTwoFACodeTextBox(TextBox twoFACodeTextBox)
	{
		this.twoFACodeTextBox = twoFACodeTextBox;
	}
	public String getIssuer()
	{
		return issuer;
	}
	public void setIssuer(String issuer)
	{
		this.issuer = issuer;
	}
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
	public String getBase32Secret()
	{
		return base32Secret;
	}
	public void setBase32Secret(String base32Secret)
	{
		this.base32Secret = base32Secret;
	}
	public TwoFactorDTO getTwoFADTO()
	{
		return twoFADTO;
	}
	public void setTwoFADTO(TwoFactorDTO twoFADTO)
	{
		this.twoFADTO = twoFADTO;
	}
	public void setSecretTextBox(ObidosPasswordBox secretTextBox)
	{
		this.secretTextBox = secretTextBox;
	}
	public Timer getRefreshTimer()
	{
		return refreshTimer;
	}
	public void setRefreshTimer(Timer refreshTimer)
	{
		this.refreshTimer = refreshTimer;
	}
	
	public void start2FARefreshTimer()
	{
		cancel2FARefreshTimer();
		twoFACodeRefreshTImer = new Timer() {
			int count = 31;
			@Override
			public void run()
			{
				count--;
				if (count < 0)
				{
					count = 0;
				}
				countDownLabel.setText(Integer.toString(count));
				if (count == 0)
				{
					count = COUNT_DOWN_START_SEC;
//					this.cancel();
					generate2FACode();
				}
			}
			
		};
		twoFACodeRefreshTImer.scheduleRepeating(1000);
		
	}
	
	
	private void setDefaultImgae()
	{
		Image img = getQrCodeImage();
		img.setUrl("128x128.png");
		img.setPixelSize(128, 128);
		img.getElement().getStyle().setWidth(128, Unit.PX);
		img.getElement().getStyle().setHeight(128, Unit.PX);
		img.setTitle("");
		img.getElement().getStyle().setProperty("border", sImageStyle);
		getCrossLabel().setVisible(true);
		getCheckMarkLabel().setVisible(false);
	}
	
	// make RPC call to get 2FA code
	public void generate2FACode()
	{
		String otpAuthUri = getOtpAuthUri();
		clearMessage();
		
		if (otpAuthUri == null) // construct
		{
			String issuer = getIssuerTextBox().getValue();
			String msg = "";
			if (issuer == null || issuer.length() == 0)
			{
				msg = "No issuer specified. ";
			}
			String account = getAccountTextBox().getValue();
			if (account == null || account.length() == 0)
			{
				msg = msg + "No Acccount specified. ";
			}

			String secret = getSecretTextBox().getValue();
			if (secret == null || secret.length() == 0)
			{
				msg = msg + "No Secret specified.";
			}
			String codeStr = getTwoFACodeTextBox().getValue();
			if (msg.length() > 0)
			{
				showErrorMessage(msg);
				return;
			}
			// remove spaces if any
			secret = secret.replaceAll("\\s+", "");
			otpAuthUri = ClientUtils.makeOtpAuthUri(issuer, account, secret);
			if (otpAuthUri == null)
			{
				showErrorMessage("Could not create OtpAuth URI");
				setDefaultImgae();
				return;
			}
		}

		AsyncCallback<TwoFactorDTO> callback = new AsyncCallback<TwoFactorDTO>()
    	{

			@Override
			public void onSuccess(TwoFactorDTO dto)
			{
				String code = dto.getTwoFACode();
				Image img = getQrCodeImage();

				// show code
				getTwoFACodeTextBox().setValue(code);
			    DateTimeFormat format = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
			    String lastUpdated = "Last update : " + format.format(new Date());
				getLastUpdatedHtml().setHTML(lastUpdated);

				if (dto.getIssuser() != null)
				{
					getIssuerTextBox().setValue(dto.getIssuser());
				}
				
				if (dto.getUserEmail() != null)
				{
					getAccountTextBox().setValue(dto.getUserEmail());
				}
				
				if (dto.getSecret() != null)
				{
					getSecretTextBox().setValue(dto.getSecret());
				}
				String tooltip = getIssuerTextBox().getValue() + ":" + getAccountTextBox().getValue();

				img.setTitle(tooltip);
				img.setUrl("data:image/png;base64," + dto.getBase64QrImage());
				img.setSize("200px", "200px");
				img.getElement().getStyle().setProperty("border", sOkImageStyle);
				getCrossLabel().setVisible(false);
				getCheckMarkLabel().setVisible(true);
				start2FARefreshTimer();
			}

			@Override
			public void onFailure(Throwable caught)
			{
				showErrorMessage(caught.getMessage());
			}
    		
    	};
    	AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
    	TwoFactorService.Utility.getInstance().generate2FACode(authCreds, otpAuthUri, callback);
	}
	
	public void cancel2FARefreshTimer()
	{
		if (twoFACodeRefreshTImer != null)
		{
			twoFACodeRefreshTImer.cancel();
			twoFACodeRefreshTImer = null;
		}
		
	}
	
	public void showErrorMessage(String message)
	{
		ObidosMessageRow row = getMessageRow();
		if (row != null)
		{
			row.showErrorMessage(message);
		}
		else
		{
		}
	}
	
	public void showMessage(String message)
	{
		ObidosMessageRow row = getMessageRow();
		if (row != null)
		{
			row.showMessage(message);
		}
		else
		{
		}
	}
	
	public void clearMessage()
	{
		getMessageRow().clear();
	}
	
	public void clear()
	{
		clearMessage();
		getSecretTextBox().getElement().setAttribute("type", "password");
		Button b = getShowHideButton();
		if (b != null)
		{
			b.setType(ButtonType.INFO);
			b.setIcon(IconType.EYE);
		}
		setOtpAuthUri(null); // must do that
		getIssuerTextBox().clear();
		getAccountTextBox().clear();
		getSecretTextBox().clear();
		showQrCodeWidgets(true);
		setDefaultImgae();
		getQrCodeImageRow().setVisible(false);
		getShowQrCodeImageSwitch().setValue(false);
		getCountDownLabel().setText(Integer.toString(COUNT_DOWN_START_SEC));
		cancel2FARefreshTimer();
		getLastUpdatedHtml().setHTML(null);
		getTwoFACodeTextBox().setValue(DEFAULT_2FA_CODE);
	}
	
	public Row getQrCodeInfoRow()
	{
		return qrCodeInfoRow;
	}
	public Timer getTwoFACodeRefreshTImer()
	{
		return twoFACodeRefreshTImer;
	}

	public void setQrCodeInfoRow(Row qrCodeInfoRow)
	{
		this.qrCodeInfoRow = qrCodeInfoRow;
	}
	public void showQrCodeWidgets(boolean visible)
	{
		Row row = getQrCodeInfoRow();
		if (row != null)
		{
			row.setVisible(visible);
		}
	}
	public Button getShowHideButton()
	{
		return showHideButton;
	}
	public void setShowHideButton(Button showHideButton)
	{
		this.showHideButton = showHideButton;
	}
	public void setTwoFACodeRefreshTImer(Timer twoFACodeRefreshTImer)
	{
		this.twoFACodeRefreshTImer = twoFACodeRefreshTImer;
	}
	public Image getQrCodeImage()
	{
		return qrCodeImage;
	}
	public void setQrCodeImage(Image qrCodeImage)
	{
		this.qrCodeImage = qrCodeImage;
	}
	public FormLabel getCheckMarkLabel()
	{
		return checkMarkLabel;
	}
	public void setCheckMarkLabel(FormLabel checkMarkLabel)
	{
		this.checkMarkLabel = checkMarkLabel;
	}
	public FormLabel getCrossLabel()
	{
		return crossLabel;
	}
	public void setCrossLabel(FormLabel crossLabel)
	{
		this.crossLabel = crossLabel;
	}

	public Row getQrCodeImageRow()
	{
		return qrCodeImageRow;
	}
	public void setQrCodeImageRow(Row qrCodeImageRow)
	{
		this.qrCodeImageRow = qrCodeImageRow;
	}
	public ToggleSwitch getShowQrCodeImageSwitch()
	{
		return showQrCodeImageSwitch;
	}
	public void setShowQrCodeImageSwitch(ToggleSwitch showQrCodeImageSwitch)
	{
		this.showQrCodeImageSwitch = showQrCodeImageSwitch;
	}
	public String getsImageStyle()
	{
		return sImageStyle;
	}
	public void setsImageStyle(String sImageStyle)
	{
		this.sImageStyle = sImageStyle;
	}
	public String getsOkImageStyle()
	{
		return sOkImageStyle;
	}
	public void setsOkImageStyle(String sOkImageStyle)
	{
		this.sOkImageStyle = sOkImageStyle;
	}
	public String getsInvalidImageStype()
	{
		return sInvalidImageStype;
	}
	public void setsInvalidImageStype(String sInvalidImageStype)
	{
		this.sInvalidImageStype = sInvalidImageStype;
	}
	public String getOtpAuthUri()
	{
		return otpAuthUri;
	}
	public void setOtpAuthUri(String otpAuthUri)
	{
		this.otpAuthUri = otpAuthUri;
	}
	public Small getLastUpdatedHtml()
	{
		return lastUpdatedHtml;
	}
	public void setLastUpdatedHtml(Small lastUpdatedHtml)
	{
		this.lastUpdatedHtml = lastUpdatedHtml;
	}
}
