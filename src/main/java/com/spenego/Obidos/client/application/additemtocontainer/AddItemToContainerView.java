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

package com.spenego.Obidos.client.application.additemtocontainer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.FileUploadExt;
import org.wisepersist.gwt.uploader.client.Uploader;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosItemRow;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;

class AddItemToContainerView extends ViewWithUiHandlers<AddItemToContainerUiHandlers>
        implements AddItemToContainerPresenter.MyView
{
    interface Binder extends UiBinder<Widget, AddItemToContainerView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph helpParagraph;

    @UiField
    TextBox templateNameTextBox;

    @UiField
    FormLabel itemNameLabel;

    @UiField
    ObidosTextBox itemNameTextBox;

    @UiField
    FormGroup formGroup;

    @UiField
    InlineRadio publicItemRadio;

    @UiField
    InlineRadio privateItemRadio;
    
    @UiField
	ObidosMessageRow messageRow;

    @UiField
    Button saveButton;

    @UiField
    Button listButton;

    @UiField
    ObidosButtonToolBar buttonToolBarBottom;

   	@UiField
	Row expiresRow;

	@UiField
	DatePicker datePicker;
	
	@UiField
	ObidosTimeBox timeBox;

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	ObidosRowBottom2px addAttachmentRow;

	@UiField
	ObidosRowBottom2px uploadRow;

	@UiField(provided=true)
    Canvas qrCodeCanvas = Canvas.createIfSupported();

	@UiField
	FileUploadExt qrCodefileUploadHTML5;

	@UiField
	Row qrCodeManualTextBoxesRow;
	
	@UiField
	TextBox accountTextBox;

	@UiField
	TextBox issuerTextBox;

	@UiField
	TextBox secretTextBox;

	@UiField
	Image qrCodeImage;
	
	@UiField
	Image qrCodeResizerImage;

	@UiField
	FormLabel checkMarkLabel;
	
	@UiField
	FormLabel crossLabel;

    @UiField
    Row qrCodeInfoRow;

    @UiField
    Button show2FACodeButton;

    @UiField
    Button genURIButton;

    @UiField
    ObidosTextBox totpUriTextBox;

	@UiField
	ObidosRowBottom2px qrCodeUploadRow;

	@UiField
	CheckBox qrCodeManualCheckBox;

    @UiField
    ToggleSwitch add2FASwitch;

    @UiField
    Button qrCodeResetButton;

    @UiField
    ToggleSwitch addAttachmentSwitch;

	@UiField
	ObidosRowBottom2px add2FASwitchRow;

	@UiField
	Row bottomUhrRow;

	@UiField
	Uploader wiseUploader;

    @UiField
    Span fileChosenSpan;
    
	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;
	
	// WiFi
	@UiField
	Row wifiQRCodeRow;

	@UiField
	ObidosTextBox wifiPasswordTextBox;

	@UiField
	Select wifiEncryptionSelect;

	@UiField
	Image wifiQrCodeImage;
	// WiFi
	
	// Bug #76
	@UiField
	ObidosTextBox containerNameTextBoxNew;

	@UiField
	FormLabel containerShareableLabel;
	// Bug #76
	
	@UiField
	InputGroupAddon wifiEntryptionInputGroupAddon;

	@UiField
	InputGroupAddon wifiHiddenNetworkInputGroupAddon;

	@UiField
	ObidosItemRow ssidItemRow;

	@UiField
	InlineRadio hiddenYesRadio;

	@UiField
	InlineRadio hiddenNoRadio;

	@UiField
	ObidosItemRow passwordItemRow;
	
	@UiField
	HTMLPanel workingPanel;

	@UiField
	FormLabel wifiQrcodeCheckMarkLabel;

	@UiField
	FormLabel wifiCrossLabel;
	
	@UiField
	ObidosRowBottom2px qrCodeThumbnailImageRow;
	
	@UiField
	ObidosRowBottom2px qrCodeImageRow;

    @Inject
    AddItemToContainerView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().showHideHelp();
				}
			});
		}

		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().back();
				}
			});
		}
		// set key up handler
		ssidItemRow.getTextBox().addKeyUpHandler(event -> {
			getUiHandlers().wifiPasswordKeyUpHandler();
		});

		passwordItemRow.getTextBox().addKeyUpHandler(event -> {
			getUiHandlers().wifiPasswordKeyUpHandler();
		});
		 
		
    }
    
    @UiHandler("listButton")
    void onCLickListButton(ClickEvent e)
    {
        getUiHandlers().listContainers();
    }

    @UiHandler("saveButton")
    void onClickSaveButton(ClickEvent e)
    {
        getUiHandlers().addItemToContainer();
    }

    @UiHandler("publicItemRadio")
    void onclickPublicRadio (ClickEvent e)
	{
    	getUiHandlers().publicRadioCallback();
	}

    @UiHandler("privateItemRadio")
    void onclickPrivateRadio (ClickEvent e)
	{
    	getUiHandlers().privateRadioCallback();
	}

    @UiHandler("listItemsButton")
    void onclickListItemsButton (ClickEvent e)
	{
    	getUiHandlers().listItems();
	}
    
    @UiHandler("itemNameTextBox")
    void onKeyUpItemNameTextBox (KeyUpEvent e)
	{
    	getUiHandlers().showNameChange(e);
	}

    @UiHandler("itemNameTextBox")
    void onPasteItemNameTextBox(ValueChangeEvent<String> text)
    {
    	getUiHandlers().namePasted();
    }

    @UiHandler("qrCodeManualCheckBox") 
    void onClickqrCodeManualCheckBox(ClickEvent e)
    {
    	getUiHandlers().showQrCodeTextBoxes();
    }
    
    @UiHandler("qrCodefileUploadHTML5")
    void onClickqrCodefileUploadHTML5(ChangeEvent e)
    {
    	getUiHandlers().processQRCodeImageFile();
    }
    
    @UiHandler("add2FASwitch")
    void onClickadd2FASwitch(ValueChangeEvent<Boolean> e)
    {
    	getUiHandlers().showQRCodeInfoWidgets();
    }
    
    @UiHandler("qrCodeResetButton")
    void onClickqrCodeResetButton(ClickEvent e)
    {
    	getUiHandlers().resetQRCodeInfo();
    }
    
    @UiHandler("show2FACodeButton")
    void onClickshow2FACodeButton(ClickEvent e)
    {
    	getUiHandlers().show2FACodeDialog();
    }
    
    @UiHandler("genURIButton")
    void onClickGenURIButton(ClickEvent e)
    {
    	getUiHandlers().generateTotpOtpAuthUri();
    }
    
    @UiHandler("addAttachmentSwitch")
    void onClickaddAttachmentSwitch(ValueChangeEvent<Boolean>e)
    {
    	getUiHandlers().showAttachmentFileUploadWidget();
    }

    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }

    // wifi qr code -- starts --
    @UiHandler("wifiPasswordTextBox")
    void onTypewifiPasswordTextBox(KeyUpEvent e)
    {
    	getUiHandlers().wifiPasswordKeyUpHandler();
    }
    
    @UiHandler("wifiHiddenToggleSwitch")
    void onSlidewifiHiddenToggleSwitch(ValueChangeEvent<Boolean> event)
    {
    	getUiHandlers().wifiHiddenToggleSwitchHandler();
    	
    }

    @UiHandler("wifiEncryptionSelect")
    void onSelectwifiEncryptionSelect(ValueChangeEvent<String> event)
    {
    	getUiHandlers().wifiEncryptionSelectHandler();
    }
    
    @UiHandler("hiddenYesRadio")
    void onclickhiddenYesRadio(ClickEvent e)
    {
		getUiHandlers().wifiPasswordKeyUpHandler();
    }

    @UiHandler("hiddenNoRadio")
    void onClickHiddenNoRadio(ClickEvent e)
    {
		getUiHandlers().wifiPasswordKeyUpHandler();
    }

    // wifi qr code -- ends --

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

    public FormGroup getFormGroup()
    {
        return formGroup;
    }

    public Button getSaveButton()
    {
        return saveButton;
    }

    public TextBox getTemplateNameTextBox()
    {
        return templateNameTextBox;
    }

    public Button getListButton()
    {
        return listButton;
    }

    public InlineRadio getPublicItemRadio()
    {
        return publicItemRadio;
    }

    public InlineRadio getPrivateItemRadio()
    {
        return privateItemRadio;
    }

    public ObidosTextBox getItemNameTextBox()
    {
        return itemNameTextBox;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    public FormLabel getItemNameLabel()
    {
        return itemNameLabel;
    }

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public Row getExpiresRow()
	{
		return expiresRow;
	}

	public DatePicker getDatePicker()
	{
		return datePicker;
	}

	public ObidosTimeBox getTimeBox()
	{
		return timeBox;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosRowBottom2px getUploadRow()
	{
		return uploadRow;
	}

	public Canvas getQrCodeCanvas()
	{
		return qrCodeCanvas;
	}

	public FileUploadExt getQrCodefileUploadHTML5()
	{
		return qrCodefileUploadHTML5;
	}

	public Row getQrCodeManualTextBoxesRow()
	{
		return qrCodeManualTextBoxesRow;
	}

	public TextBox getAccountTextBox()
	{
		return accountTextBox;
	}

	public TextBox getIssuerTextBox()
	{
		return issuerTextBox;
	}

	public TextBox getSecretTextBox()
	{
		return secretTextBox;
	}

	public Image getQrCodeImage()
	{
		return qrCodeImage;
	}

	public Image getQrCodeResizerImage()
	{
		return qrCodeResizerImage;
	}

	public FormLabel getCheckMarkLabel()
	{
		return checkMarkLabel;
	}

	public FormLabel getCrossLabel()
	{
		return crossLabel;
	}

	public Row getQrCodeInfoRow()
	{
		return qrCodeInfoRow;
	}

	public Button getShow2FACodeButton()
	{
		return show2FACodeButton;
	}

	public Button getGenURIButton()
	{
		return genURIButton;
	}

	public ObidosTextBox getTotpUriTextBox()
	{
		return totpUriTextBox;
	}

	public ObidosRowBottom2px getQrCodeUploadRow()
	{
		return qrCodeUploadRow;
	}

	public CheckBox getQrCodeManualCheckBox()
	{
		return qrCodeManualCheckBox;
	}

	public ToggleSwitch getAdd2FASwitch()
	{
		return add2FASwitch;
	}

	public Button getQrCodeResetButton()
	{
		return qrCodeResetButton;
	}

	public ObidosRowBottom2px getAddAttachmentRow()
	{
		return addAttachmentRow;
	}

	public ToggleSwitch getAddAttachmentSwitch()
	{
		return addAttachmentSwitch;
	}

	public ObidosRowBottom2px getAdd2FASwitchRow()
	{
		return add2FASwitchRow;
	}

	public Row getBottomUhrRow()
	{
		return bottomUhrRow;
	}

	public Uploader getWiseUploader()
	{
		return wiseUploader;
	}

	public Span getFileChosenSpan()
	{
		return fileChosenSpan;
	}

	public ObidosRowBottom2px getLanguageRow()
	{
		return languageRow;
	}

	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}

	public Row getWifiQRCodeRow()
	{
		return wifiQRCodeRow;
	}

	public ObidosTextBox getWifiPasswordTextBox()
	{
		return wifiPasswordTextBox;
	}

	public Select getWifiEncryptionSelect()
	{
		return wifiEncryptionSelect;
	}

	public Image getWifiQrCodeImage()
	{
		return wifiQrCodeImage;
	}
	// Bug #76
	public ObidosTextBox getContainerNameTextBoxNew()
	{
		return containerNameTextBoxNew;
	}

	public FormLabel getContainerShareableLabel()
	{
		return containerShareableLabel;
	}
	// Bug #76

	public InputGroupAddon getWifiEntryptionInputGroupAddon()
	{
		return wifiEntryptionInputGroupAddon;
	}

	public InputGroupAddon getWifiHiddenNetworkInputGroupAddon()
	{
		return wifiHiddenNetworkInputGroupAddon;
	}

	public ObidosItemRow getSsidItemRow()
	{
		return ssidItemRow;
	}

	public InlineRadio getHiddenYesRadio()
	{
		return hiddenYesRadio;
	}

	public InlineRadio getHiddenNoRadio()
	{
		return hiddenNoRadio;
	}

	public ObidosItemRow getPasswordItemRow()
	{
		return passwordItemRow;
	}

	public HTMLPanel getWorkingPanel()
	{
		return workingPanel;
	}

	public FormLabel getWifiQrcodeCheckMarkLabel()
	{
		return wifiQrcodeCheckMarkLabel;
	}

	public FormLabel getWifiCrossLabel()
	{
		return wifiCrossLabel;
	}

	public ObidosRowBottom2px getQrCodeThumbnailImageRow()
	{
		return qrCodeThumbnailImageRow;
	}

	public ObidosRowBottom2px getQrCodeImageRow()
	{
		return qrCodeImageRow;
	}
}