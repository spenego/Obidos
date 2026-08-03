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

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.FileUtils;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.rpc.TwoFactorService;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;

// Common code to upload/decode 2FA QR Code image
// it is used from item creating with templates
// spgdev@spenego.com Jul-30-2022
public class TwoFAQRCodeUtils
{
	private TwoFAQRCodeModel twoFAQRCodeModel;
	private final String DEFAULT_IMAGE 		= "128x128.png";
	private final String IMAGE_WIDTH  		= "200px";
	private final String IMAGE_HEIGHT 		= "200px";
	private final int DEFAULT_IMAGE_WIDTH  	= 128;
	private final int DEFAULT_IMAGE_HEIGHT 	= 128;
	
	public TwoFAQRCodeUtils(TwoFAQRCodeModel model)
	{
		this.twoFAQRCodeModel = model;
	}
	public TwoFAQRCodeUtils(PlaceManager placemanager, FileUploadExt fileUploadHtml5, FileReader fileReader,
			File qrCodeImageFile, ObidosMessageRow messageRow, CheckBox manualCheckBox, TextBox totpUriTextBox,
			TextBox issuerTextBox, TextBox accountTextBox, TextBox secretTextBox, Button show2faCodeButton,
			Button qrCodeResetButton, Button generateURIButton, FormLabel checkMarkLabel, FormLabel crossLabel,
			Image qrCodeImage, Image qrCodeResizerImage, Canvas canvas, 
			ObidosRowBottom2px uploadRow,
			Row manualTextBoxesRow,
			Row qrCodeInfoRow,
			ToggleSwitch add2FASwitch,
			ObidosRowBottom2px qrCodeImageRow,
			TwoFAModalData modalData)
	{
		super();
		GWT.log("XX in constructor");
		this.twoFAQRCodeModel = new TwoFAQRCodeModel(placemanager,
				fileUploadHtml5, 
				fileReader,
				qrCodeImageFile,
				messageRow,
				manualCheckBox,
				totpUriTextBox,
				issuerTextBox,
				accountTextBox,
				secretTextBox,
				show2faCodeButton,
				qrCodeResetButton,
				generateURIButton,
				checkMarkLabel,crossLabel,
				qrCodeImage,
				qrCodeResizerImage,
				canvas,
				uploadRow,
				manualTextBoxesRow,
				qrCodeInfoRow,
				add2FASwitch,
				qrCodeImageRow,
				modalData);
	}

	public TwoFAQRCodeModel getTwoFAQRCodeModel()
	{
		return twoFAQRCodeModel;
	}

	public void setTwoFAQRCodeModel(TwoFAQRCodeModel twoFAQRCodeModel)
	{
		this.twoFAQRCodeModel = twoFAQRCodeModel;
	}

	
	public void setupQRCodeImageUploadHandler()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		FileReader reader = qm.getFileReader();
		if (reader == null)
		{
			showErrorMessage("FileReader is null");
			return;
		}
		HandlerRegistration readerLoadHandler = qm.getReaderLoadHandler();
		if (readerLoadHandler != null)
		{
			gwtLog("Reader Load Handler is already set...");
		}

		readerLoadHandler = reader.addLoadEndHandler(new LoadEndHandler()
		{
			
			@Override
			public void onLoadEnd(LoadEndEvent event)
			{
				if (reader.getError() != null)
				{
					showErrorMessage("Filereader rrorr");
					return;
				}
				qm.setQrCodeImageDataUrl(null);
				setupQRCodeImageLoadHandler();
				setupResizeImageLoadHandler();
				String imageDataUrl = createQRCodeImageUrl();
				if (imageDataUrl == null)
				{
					qm.setQrCodeImageDataUrl(null);
					return;
				}
				resizeQRCodeImage();
			}
		});
	}

	public void processQRCodeImageFile()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		setDefaultImagePlaceHolder();
		Image ri = qm.getQrCodeImage();
		ri.setUrl(DEFAULT_IMAGE);
		ri.setPixelSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
		ri.getElement().getStyle().setWidth(DEFAULT_IMAGE_WIDTH, Unit.PX);
		ri.getElement().getStyle().setHeight(DEFAULT_IMAGE_HEIGHT, Unit.PX);
		
		FileList files = qm.getFileUploadHtml5().getFiles();
		if (files.getLength() == 0)
		{
			showErrorMessage("No files selected");
			return;
		}
		File file = files.getItem(0);

		qm.setQrCodeImageFile(file); // must set for image load end handlers

		String type = file.getType();
		if (type == null || type.length() == 0)
		{
			setDefaultImagBorderStyle();
			return;
		}
				try
		{
			if (type.startsWith("image/"))
			{
				gwtLog("read as binary string...");
				qm.setQrCodeImageDataUrl(null);
				qm.getFileReader().readAsBinaryString(file);
				gwtLog("back");
			}
			else
			{
				qm.setQrCodeImageDataUrl(null);
				gwtLog(">>>>>>>>>> Setting default image");
				setDefaultImagePlaceHolder();
			}
			
		}
		catch (Throwable t)
		{
			gwtLog("Error: " + t.getMessage());
			showErrorMessage(t.getMessage());
		}
	}
	public void clearAllQRCodeStuff()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		clearQR();
		resetQRCodeInfo();
		resetQRCodeTextBoxes();
	}
	
	public void show2FACodeDialog()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		TwoFAModalData  md = qm.getModalData();
		if (md == null)
		{
			md = ClientUtils.createTwoFAModal();
			qm.setModalData(md);
		}
		TwoFactorDTO dto = qm.getTwoFactorDto();
		if (dto != null && qm.getShow2FACodeButton().isVisible())
		{
			md.setIssuer(dto.getIssuser());
			md.setAccount(dto.getUserEmail());
			md.setBase32Secret(dto.getSecret());
			md.setQrCodeInfoRow(qm.getQrCodeInfoRow());
			showMessage("");
			ClientUtils.showTwoFACode(qm.getPlacemanager(), md);
		}
	}
	
	public void generateTotpOtpAuthUri()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		// construct URI
		String issuer = qm.getIssuerTextBox().getValue();
		if (issuer == null || issuer.length() == 0)
		{
			showErrorMessage("Please specify Issuer");
			return;
		}

		String account = qm.getAccountTextBox().getValue();
		if (account == null || account.length() == 0)
		{
			showErrorMessage("Please specify Account");
			return;
		}

		String secret = qm.getSecretTextBox().getValue();
		if (secret == null || secret.length() == 0)
		{
			showErrorMessage("Please specify Base32 encoded secret");
			return;
		}
		// sample uri
		// Ref: https://github.com/google/google-authenticator/wiki/Key-Uri-Format
		// otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30
		String otpAuthUri = "otpauth://totp/" + issuer + ":" + account + "?secret=" + secret + "&issuer=" + issuer + "&algorithm=SHA1&digits=6&period=30";
		gwtLog("URI: " + otpAuthUri);

    	AsyncCallback<TwoFactorDTO> callback = new AsyncCallback<TwoFactorDTO>()
    	{

			@Override
			public void onSuccess(TwoFactorDTO dto)
			{
				gwtLog("OK");
				qm.setTwoFactorDto(dto);
				showCheckMarkIcon();
				// set qr code image
				String imageDataUrl = dto.getBase64QrImage();
				gwtLog(imageDataUrl);
				if (imageDataUrl != null);
				{
					Image image = qm.getQrCodeImage();
					image.setUrl("data:image/png;base64," + imageDataUrl);
				}
				setValidQRCodeImageBorderStyle();
				qm.getShow2FACodeButton().setVisible(true);
				gwtLog("URI in DTO: " + dto.getOtpUri());
				qm.getTotpUriTextBox().setValue(dto.getOtpUri());
				updateImageTooltip(dto);
				gwtLog("Code: " + dto.getTwoFACode());
			}

			@Override
			public void onFailure(Throwable caught)
			{
				clearQR();
				showCrossIcon();
				qm.getShow2FACodeButton().setVisible(false);
				updateImageTooltip("Invalid TOTP othAuth URI");
				showErrorMessage(caught.getMessage());
			}
    		
    	};
    	AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
    	TwoFactorService.Utility.getInstance().generate2FACode(authCreds, otpAuthUri, callback);
	}
	public void resetQRCodeInfo()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		showQRCodeButton(false);
		resetQRCodeTextBoxes();
		showCrossIcon();
		qm.getUploadRow().setVisible(true);
		qm.getManualTextBoxesRow().setVisible(false);
		qm.getMessageRow().showMessage("");
		qm.getAdd2FASwitch().setValue(false);
		qm.getQrCodeInfoRow().setVisible(false);
		setDefaultImagePlaceHolder();
	}

	public void showQrCodeTextBoxes()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		clearQR();
		showQRCodeButton(false);
		showCrossIcon();
		setDefaultImagePlaceHolder();
		showMessage("");
		Boolean v = qm.getManualCheckBox().getValue();
		qm.getUploadRow().setVisible(v);
		qm.getManualTextBoxesRow().setVisible(v);
		qm.getTotpUriTextBox().clear();
		
	}

	////////////////////////////////////////////////////////////////////////////
	
	// all the methods below are private --------
	
	private String createQRCodeImageUrl()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		File file = qm.getQrCodeImageFile();
		FileReader reader = qm.getFileReader();
		String result = reader.getStringResult();
		String url = FileUtils.createDataUrl(file.getType(), result);
		gwtLog("returning url");
		qm.setQrCodeImageDataUrl(url);
		return url;
	}
	
	private void setupQRCodeImageLoadHandler()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		HandlerRegistration imageHandler = qm.getQrCodeImageHandler();
		if (imageHandler != null)
		{
			gwtLog("XX imagHandler is already set");
			return;
		}
		Image image = qm.getQrCodeImage();
		imageHandler = image.addLoadHandler(new LoadHandler()
		{
			
			@Override
			public void onLoad(LoadEvent event)
			{
				String imageDataUrl = qm.getQrCodeImageDataUrl();
				if (imageDataUrl != null)
				{
					decodeQrCodeImage();
				}
				else
				{
					gwtLog("XX image is null, will not try to decode qr code");
				}
				
			}
		});
		if (imageHandler != null)
		{
			gwtLog("Setting image load hnalder");
			qm.setQrCodeImageHandler(imageHandler);
		}
		image.addErrorHandler(new ErrorHandler()
		{
			
			@Override
			public void onError(ErrorEvent event)
			{
				showErrorMessage("Could not load image");
			}
		});
	}
	
	private void setupResizeImageLoadHandler()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		HandlerRegistration imageHandler = qm.getQrCodeResizeImageHandler();
		if (imageHandler != null)
		{
			gwtLog("XX Resize imagHandler is already set");
			return;
		}
		Image image = qm.getQrCodeResizerImage();
		imageHandler = image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				if (qm.getQrCodeImageHandler() == null)
				{
					gwtLog("QR Code image handler is not set yet");
					return;
				}
				showMessage("OK: The QR Code image represents a valid 2FA TOTP OTPAUTH URI");
				// Bug# 119
				// QR Code is valid, show the image row with qr code bitmap in 
				// the canvas
				qm.getQrCodeImageRow().setVisible(true);

				int w = image.getWidth();
				int h = image.getHeight();
				Canvas canvas = qm.getCanvas();
				canvas.setWidth(IMAGE_WIDTH);
				canvas.setHeight(IMAGE_HEIGHT);
				canvas.setCoordinateSpaceWidth(w);
				canvas.setCoordinateSpaceHeight(h);
				
				Context2d context = canvas.getContext2d();
				ImageElement resizedImage = ImageElement.as(image.getElement());
				context.drawImage(resizedImage, 0, 0, w, h);
				String url = canvas.toDataUrl("image/png");
				qm.getQrCodeImage().setUrl(url);
				qm.setQrCodeImageDataUrl(url);
			}
		});
		image.addErrorHandler(new ErrorHandler()
		{
			@Override
			public void onError(ErrorEvent event)
			{
				showErrorMessage("Could not load image");
				//TODO Clear
				qm.setQrCodeImageDataUrl(null);
				handleQRCodeUploadError();
			}
		});

	}
	private void handleQRCodeUploadError()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		File file = qm.getQrCodeImageFile();
		FileError error = qm.getFileReader().getError();
		String errorDesc = "";
		if (error != null)
		{
			ErrorCode errorCode = error.getCode();
			if (errorCode != null)
			{
				errorDesc = ": " + errorCode.name();
			}
		}
		showErrorMessage("File loading error for file: " + file.getName() + "\n" + errorDesc);

	}

	private void resizeQRCodeImage()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		Image resizeImage = qm.getQrCodeResizerImage();
		String imageDataUrl = qm.getQrCodeImageDataUrl();
		int width = resizeImage.getWidth();
		if (width == 0)
		{
			width = ieWidth(resizeImage.getElement());
		}
		int height = resizeImage.getHeight();
		if (height == 0)
		{
			height = ieHeight(resizeImage.getElement());
		}
		gwtLog("size=" + width + "x" + height);

		int newWidth = DEFAULT_IMAGE_WIDTH;
		int newHeight = DEFAULT_IMAGE_HEIGHT;
		if (width < DEFAULT_IMAGE_WIDTH)
		{
			newWidth = width;
		}
		if (height < DEFAULT_IMAGE_HEIGHT)
		{
			newHeight = height;
		}
		float aspectRatio =  (float) ((width * 1.0) / (height * 1.0));
		newHeight = Math.round((newWidth / aspectRatio));
		newWidth = Math.round(( newHeight * aspectRatio ));

		int w = newWidth;
		int h = newHeight;
		gwtLog("size=" + w + "x" + h);
		
    	gwtLog("URL before resize: " + imageDataUrl.length() + " bytes");
		resizeImage.setPixelSize(w, h);
		resizeImage.getElement().getStyle().setWidth(w, Unit.PX);
		resizeImage.getElement().getStyle().setHeight(h, Unit.PX);
		resizeImage.setUrl(imageDataUrl);
	}
	
	
	private void showErrorMessage(String emsg)
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		ClientUtils.showErrorMessage(qm.getMessageRow(), emsg);
	}
	private void showMessage(String msg)
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		ClientUtils.showMessage(qm.getMessageRow(), msg);
	}
	
	void clear()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
	}
	
	private void setDefaultImagePlaceHolder()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		Image image = qm.getQrCodeImage();
		image.setTitle("QR Code image placeholder");
		image.setUrl("128x128.png");
		image.setPixelSize(128, 128);
		image.getElement().getStyle().setWidth(DEFAULT_IMAGE_WIDTH, Unit.PX);
		image.getElement().getStyle().setHeight(DEFAULT_IMAGE_HEIGHT, Unit.PX);
		image.setTitle("");

		setDefaultImagBorderStyle();

	}
	void setDefaultImagBorderStyle()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		Image image = qm.getQrCodeImage();
		image.getElement().getStyle().setProperty("border", qm.getDEFAULT_BORDER_STYLE());
	}

	void setValidQRCodeImageBorderStyle()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		Image image = qm.getQrCodeImage();
		image.getElement().getStyle().setProperty("border", qm.getOK_IMAGE_BORDER_STYLE());
	}

	void setinValidQRCodeImageBorderStyle()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		Image image = qm.getQrCodeImage();
		image.getElement().getStyle().setProperty("border", qm.getINVALID_IMAGE_BORDER_STYLE());
	}

	
	private void gwtLog(String message)
	{
        ClientUtils.gwtLog(this.getClass().getSimpleName(), "XXX " + message);
	}

	private void showCrossIcon()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();

		qm.getCheckMarkLabel().setVisible(false);
		gwtLog("XX in showCrossIcon");
		qm.getCrossLabel().setVisible(true);
		setinValidQRCodeImageBorderStyle();
	}
	private void showCheckMarkIcon()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		qm.getCheckMarkLabel().setVisible(true);
		qm.getCrossLabel().setVisible(false);
	}

	private void updateImageTooltip(String tooltip)
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		Image ri = qm.getQrCodeImage();
		ri.setTitle(tooltip);
	}
	private void updateImageTooltip(TwoFactorDTO dto)
	{
		String title = "";
		String issuer = dto.getIssuser();
		String email = dto.getUserEmail();
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		if (issuer != null)
		{
			title = issuer;
		}
		if (email != null)
		{
			if (title != null)
			{
				title = title + ":" + email; 
			}
		}
		Image ri = qm.getQrCodeImage();
		ri.setTitle(title);
	}

	/**
	 * The method sends the Image data URI to back-end to decode. If decoded
	 * successfully, 2FA OTPAUTH URI will be returned and a text field will 
	 * be populated. If text field is populated, then when adding the item to 
	 * Container, QRCode type will be added with the OTPAUTH URI.
	 * <p>
	 * @author spgdev@spenego.com - Jul 10, 2022
	 */
	private void decodeQrCodeImage()
	{
		gwtLog("in Decode Qr Code Image");
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		String imageDataUrl = qm.getQrCodeImageDataUrl();
		if (imageDataUrl == null)
		{
			showErrorMessage("No QR Code Image data URL found");
			return;
		}
    	AsyncCallback<TwoFactorDTO> callback = new AsyncCallback<TwoFactorDTO>()
    	{

			@Override
			public void onSuccess(TwoFactorDTO dto)
			{
				gwtLog("OK");
				qm.setTwoFactorDto(dto);
				showCheckMarkIcon();
				setValidQRCodeImageBorderStyle();
				qm.getShow2FACodeButton().setVisible(true);
				qm.getTotpUriTextBox().setValue(dto.getOtpUri());
				updateImageTooltip(dto);
			}

			@Override
			public void onFailure(Throwable caught)
			{
				gwtLog("Failed to decode qr code image: " + caught.getMessage());
				clear();
				showCrossIcon();
				qm.getShow2FACodeButton().setVisible(false);
				updateImageTooltip("Image has no valid 2FA TOTP OTPAUTH URI");
				String emsg = caught.getMessage();
				gwtLog("XXX Emsg len: " + emsg.length());
				qm.getTotpUriTextBox().clear();
				if (emsg != null && emsg.length() > 80)
				{
					emsg = ClientUtils.trimStringEllipsis(emsg, 80);
				}
				showErrorMessage(emsg);
			}
    		
    	};
    	AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
    	TwoFactorService.Utility.getInstance().decodeQRCodeImageDataUri(authCreds, imageDataUrl, callback);
	}
	// For that piece of crap called IE
	private static native int ieWidth(Element elt) /*-{
		return elt.naturalWidth;
	}-*/;

	private static native int ieHeight(Element elt) /*-{
		return elt.naturalHeight;
	}-*/;

	// method to load QR Code image when user selects a QR Code image file
	// it uses HTML5 upload API

	
	void clearQR()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		qm.setQrCodeImageDataUrl(null);
		qm.setTwoFactorDto(null);
		qm.setModalData(null);
		qm.getTotpUriTextBox().clear();
	}
	
	void show2FACodeButton(boolean visible)
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		qm.getShow2FACodeButton().setVisible(visible);
	}
	void showQRCodeButton(boolean visible)
	{
		show2FACodeButton(visible);
	}
	
	void resetQRCodeTextBoxes()
	{
		TwoFAQRCodeModel qm = getTwoFAQRCodeModel();
		qm.getManualCheckBox().setValue(false);
		qm.getTotpUriTextBox().clear();
		qm.getIssuerTextBox().clear();
		qm.getAccountTextBox().clear();
		qm.getSecretTextBox().clear();
	}

	

}
