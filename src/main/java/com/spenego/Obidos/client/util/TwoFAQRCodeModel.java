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
import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;

// Common data for Presenters, so that QR QR Code image upload
// so that ui can be used from any presenter.
// This class is used at client side.
// spgdev@spenego.com - Aug 21, 2022
public class TwoFAQRCodeModel
{
	// caller must set the following - starts -
	private PlaceManager 			placemanager;

	private FileUploadExt 			fileUploadHtml5;
	private FileReader 				fileReader;
	private File 					qrCodeImageFile;

	private ObidosMessageRow 		messageRow;
	private CheckBox                manualCheckBox;
	private TextBox                 totpUriTextBox;
	private TextBox                 issuerTextBox;
	private TextBox                 accountTextBox;
	private TextBox                 secretTextBox;
	private Button                  show2FACodeButton;
	private Button                  qrCodeResetButton;
	private Button                  generateURIButton;
	private FormLabel               checkMarkLabel;
	private FormLabel               crossLabel;

	private Image                   qrCodeImage;
	private Image                   qrCodeResizerImage;
	private Canvas                  canvas;
	private ObidosRowBottom2px 		uploadRow;
	private Row 					manualTextBoxesRow;
	private Row 					qrCodeInfoRow;
	private ToggleSwitch 			add2FASwitch;
	private ObidosRowBottom2px 		qrCodeImageRow; // Bug #119
	private TwoFAModalData 			modalData;
	// - ends -

	private String 					qrCodeImageDataUrl; 
	private HandlerRegistration 	readerLoadHandler;
	private HandlerRegistration 	qrCodeResizeImageHandler; 
	private HandlerRegistration 	qrCodeImageHandler; 
	private TwoFactorDTO 			twoFactorDto; 		
	

	private final String DEFAULT_BORDER_STYLE 		 = "1.5px solid #ddd";
	private final String OK_IMAGE_BORDER_STYLE 	 	 = "1.5px solid #58B957";
	private final String INVALID_IMAGE_BORDER_STYLE  = "1.5px solid #ff0000";
	


	public TwoFAQRCodeModel(PlaceManager placemanager, FileUploadExt fileUploadHtml5, FileReader fileReader,
			File qrCodeImageFile, ObidosMessageRow messageRow, CheckBox manualCheckBox, TextBox totpUriTextBox,
			TextBox issuerTextBox, TextBox accountTextBox, TextBox secretTextBox, Button show2faCodeButton,
			Button qrCodeResetButton, Button generateURIButton, FormLabel checkMarkLabel, FormLabel crossLabel,
			Image qrCodeImage, Image qrCodeResizerImage, Canvas canvas,
			ObidosRowBottom2px uploadRow,
			Row manualTextBoxesRow,
			Row	qrCodeInfoRow,
			ToggleSwitch add2FASwitch,
			ObidosRowBottom2px qrCodeImageRow,
			TwoFAModalData modalData
			)
	{
		super();
		this.placemanager = placemanager;
		this.fileUploadHtml5 = fileUploadHtml5;
		this.fileReader = fileReader;
		this.qrCodeImageFile = qrCodeImageFile;
		this.messageRow = messageRow;
		this.manualCheckBox = manualCheckBox;
		this.totpUriTextBox = totpUriTextBox;
		this.issuerTextBox = issuerTextBox;
		this.accountTextBox = accountTextBox;
		this.secretTextBox = secretTextBox;
		this.show2FACodeButton = show2faCodeButton;
		this.qrCodeResetButton = qrCodeResetButton;
		this.generateURIButton = generateURIButton;
		this.checkMarkLabel = checkMarkLabel;
		this.crossLabel = crossLabel;
		this.qrCodeImage = qrCodeImage;
		this.qrCodeResizerImage = qrCodeResizerImage;
		this.canvas = canvas;
		this.uploadRow = uploadRow;
		this.manualTextBoxesRow = manualTextBoxesRow;
		this.qrCodeInfoRow = qrCodeInfoRow;
		this.add2FASwitch = add2FASwitch;		
		this.qrCodeImageRow = qrCodeImageRow;
		this.modalData = modalData;
	}

	public PlaceManager getPlacemanager()
	{
		return placemanager;
	}
	public void setPlacemanager(PlaceManager placemanager)
	{
		this.placemanager = placemanager;
	}
	public FileUploadExt getFileUploadHtml5()
	{
		return fileUploadHtml5;
	}
	public void setFileUploadHtml5(FileUploadExt fileUploadHtml5)
	{
		this.fileUploadHtml5 = fileUploadHtml5;
	}
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}
	public void setMessageRow(ObidosMessageRow messageRow)
	{
		this.messageRow = messageRow;
	}
	public FileReader getFileReader()
	{
		return fileReader;
	}
	public void setFileReader(FileReader fileReader)
	{
		this.fileReader = fileReader;
	}
	public File getQrCodeImageFile()
	{
		return qrCodeImageFile;
	}
	public void setQrCodeImageFile(File qrCodeImageFile)
	{
		this.qrCodeImageFile = qrCodeImageFile;
	}
	public String getQrCodeImageDataUrl()
	{
		return qrCodeImageDataUrl;
	}
	public void setQrCodeImageDataUrl(String qrCodeImageDataUrl)
	{
		this.qrCodeImageDataUrl = qrCodeImageDataUrl;
	}
	public HandlerRegistration getQrCodeResizeImageHandler()
	{
		return qrCodeResizeImageHandler;
	}
	public void setQrCodeResizeImageHandler(HandlerRegistration qrCodeResizeImageHandler)
	{
		this.qrCodeResizeImageHandler = qrCodeResizeImageHandler;
	}
	public HandlerRegistration getQrCodeImageHandler()
	{
		return qrCodeImageHandler;
	}
	public void setQrCodeImageHandler(HandlerRegistration qrCodeImageHandler)
	{
		this.qrCodeImageHandler = qrCodeImageHandler;
	}
	public TwoFactorDTO getTwoFactorDto()
	{
		return twoFactorDto;
	}
	public void setTwoFactorDto(TwoFactorDTO twoFactorDto)
	{
		this.twoFactorDto = twoFactorDto;
	}
	public TwoFAModalData getModalData()
	{
		return modalData;
	}
	public void setModalData(TwoFAModalData modalData)
	{
		this.modalData = modalData;
	}

	public String getDEFAULT_BORDER_STYLE()
	{
		return DEFAULT_BORDER_STYLE;
	}
	public String getOK_IMAGE_BORDER_STYLE()
	{
		return OK_IMAGE_BORDER_STYLE;
	}
	public String getINVALID_IMAGE_BORDER_STYLE()
	{
		return INVALID_IMAGE_BORDER_STYLE;
	}
	public Image getQrCodeImage()
	{
		return qrCodeImage;
	}
	public void setQrCodeImage(Image qrCodeImage)
	{
		this.qrCodeImage = qrCodeImage;
	}
	public Image getQrCodeResizerImage()
	{
		return qrCodeResizerImage;
	}
	public void setQrCodeResizerImage(Image qrCodeResizerImage)
	{
		this.qrCodeResizerImage = qrCodeResizerImage;
	}
	public Canvas getCanvas()
	{
		return canvas;
	}
	public void setCanvas(Canvas canvas)
	{
		this.canvas = canvas;
	}
	public CheckBox getManualCheckBox()
	{
		return manualCheckBox;
	}
	public void setManualCheckBox(CheckBox manualCheckBox)
	{
		this.manualCheckBox = manualCheckBox;
	}
	public TextBox getTotpUriTextBox()
	{
		return totpUriTextBox;
	}
	public void setTotpUriTextBox(TextBox totoUriTextBox)
	{
		this.totpUriTextBox = totoUriTextBox;
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
	public TextBox getSecretTextBox()
	{
		return secretTextBox;
	}
	public void setSecretTextBox(TextBox secretTextBox)
	{
		this.secretTextBox = secretTextBox;
	}
	public Button getShow2FACodeButton()
	{
		return show2FACodeButton;
	}
	public void setShow2FACodeButton(Button show2faCodeButton)
	{
		show2FACodeButton = show2faCodeButton;
	}
	public Button getQrCodeResetButton()
	{
		return qrCodeResetButton;
	}
	public void setQrCodeResetButton(Button qrCodeResetButton)
	{
		this.qrCodeResetButton = qrCodeResetButton;
	}
	public Button getGenerateURIButton()
	{
		return generateURIButton;
	}
	public void setGenerateURIButton(Button generateURIButton)
	{
		this.generateURIButton = generateURIButton;
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
	public HandlerRegistration getReaderLoadHandler()
	{
		return readerLoadHandler;
	}
	public void setReaderLoadHandler(HandlerRegistration readerLoadHandler)
	{
		this.readerLoadHandler = readerLoadHandler;
	}

	public ObidosRowBottom2px getUploadRow()
	{
		return uploadRow;
	}

	public Row getManualTextBoxesRow()
	{
		return manualTextBoxesRow;
	}

	public void setManualTextBoxesRow(Row manualTextBoxesRow)
	{
		this.manualTextBoxesRow = manualTextBoxesRow;
	}

	public void setUploadRow(ObidosRowBottom2px uploadRow)
	{
		this.uploadRow = uploadRow;
	}

	public Row getQrCodeInfoRow()
	{
		return qrCodeInfoRow;
	}

	public void setQrCodeInfoRow(Row qrCodeInfoRow)
	{
		this.qrCodeInfoRow = qrCodeInfoRow;
	}

	public ToggleSwitch getAdd2FASwitch()
	{
		return add2FASwitch;
	}

	public void setAdd2FASwitch(ToggleSwitch add2faSwitch)
	{
		add2FASwitch = add2faSwitch;
	}

	public ObidosRowBottom2px getQrCodeImageRow()
	{
		return qrCodeImageRow;
	}

	public void setQrCodeImageRow(ObidosRowBottom2px qrCodeImageRow)
	{
		this.qrCodeImageRow = qrCodeImageRow;
	}
}
