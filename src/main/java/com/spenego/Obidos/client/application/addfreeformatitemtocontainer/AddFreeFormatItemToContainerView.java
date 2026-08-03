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

package com.spenego.Obidos.client.application.addfreeformatitemtocontainer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.FileUploadExt;
import org.wisepersist.gwt.uploader.client.Uploader;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosAdhocItemRow;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;

class AddFreeFormatItemToContainerView extends ViewWithUiHandlers<AddFreeFormatItemToContainerUiHandlers>
        implements AddFreeFormatItemToContainerPresenter.MyView
{
    interface Binder extends UiBinder<Widget, AddFreeFormatItemToContainerView>
    {
    }

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Heading heading;

    @UiField
    Paragraph helpParagraph;

    @UiField
    TextBox containerNameTextBox;

    @UiField
    TextBox itemNameTextBox;

    @UiField
    FormGroup formGroup;

    @UiField
    InlineRadio publicItemRadio;

    @UiField
    InlineRadio privateItemRadio;


    @UiField
    Button saveButton;

    @UiField
    ObidosPanelHeader panelHeader;

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
	ObidosAdhocItemRow firstRow;

	@UiField
	ObidosRowBottom2px uploadRow;

	
	@UiField
	ObidosRowBottom2px qrCodeUploadRow;

	@UiField
	ObidosRowBottom2px qrCodeThumbnailImageRow;

	@UiField
	ObidosRowBottom2px qrCodeImageRow;

	@UiField
	ObidosRowBottom2px totpUriRow;

	@UiField
	CheckBox qrCodeManualCheckBox;
	

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

	@UiField(provided=true)
    Canvas qrCodeCanvas = Canvas.createIfSupported();

    @UiField
	ObidosMessageRow messageRow;
    
    @UiField
    ObidosTextBox totpUriTextBox;

    @UiField
    Button qrCodeResetButton;

    @UiField
    ToggleSwitch add2FASwitch;
    
    @UiField
    Row qrCodeInfoRow;

    @UiField
    Button show2FACodeButton;

    @UiField
    Button genURIButton;
    
    @UiField
    ToggleSwitch addAttachmentSwitch;

	@UiField
	Uploader wiseUploader;

    @UiField
    Span fileChosenSpan;

	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;

	@UiField
	Row qrCodeManualEntryRow;

	@UiField
	FormLabel containerShareableLabel;
    
    @Inject
    AddFreeFormatItemToContainerView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        
        /*
	    fileUpload.getElement().removeAttribute("multiple");
	    fileUpload.getElement().setAttribute("accept", "image/*");
	    */
	    qrCodefileUploadHTML5.getElement().removeAttribute("multiple");
	    qrCodefileUploadHTML5.getElement().setAttribute("accept", "image/*");

        Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().help();
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
    }

    @UiHandler("listButton")
    void onCLickListButton(ClickEvent e)
    {
        getUiHandlers().navigateToListContainers();
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

    public FormGroup getFormGroup()
    {
        return formGroup;
    }
    
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

    public Button getSaveButton()
    {
        return saveButton;
    }

    public TextBox getContainerNameTextBox()
    {
        return containerNameTextBox;
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

    public TextBox getItemNameTextBox()
    {
        return itemNameTextBox;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

	public Heading getHeading()
	{
		return heading;
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

	public ObidosAdhocItemRow getFirstRow()
	{
		return firstRow;
	}


	public ObidosRowBottom2px getUploadRow()
	{
		return uploadRow;
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

	public CheckBox getQrCodeManualCheckBox()
	{
		return qrCodeManualCheckBox;
	}

	public ObidosRowBottom2px getQrCodeUploadRow()
	{
		return qrCodeUploadRow;
	}

	public Image getQrCodeImage()
	{
		return qrCodeImage;
	}

	public Image getQrCodeResizerImage()
	{
		return qrCodeResizerImage;
	}

	public Canvas getQrCodeCanvas()
	{
		return qrCodeCanvas;
	}

	public ObidosTextBox getTotpUriTextBox()
	{
		return totpUriTextBox;
	}

	public FormLabel getCheckMarkLabel()
	{
		return checkMarkLabel;
	}

	public FormLabel getCrossLabel()
	{
		return crossLabel;
	}

	public Button getQrCodeResetButton()
	{
		return qrCodeResetButton;
	}

	public ToggleSwitch getAdd2FASwitch()
	{
		return add2FASwitch;
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

	public ToggleSwitch getAddAttachmentSwitch()
	{
		return addAttachmentSwitch;
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

	public ObidosRowBottom2px getQrCodeThumbnailImageRow()
	{
		return qrCodeThumbnailImageRow;
	}

	public ObidosRowBottom2px getTotpUriRow()
	{
		return totpUriRow;
	}

	public ObidosRowBottom2px getQrCodeImageRow()
	{
		return qrCodeImageRow;
	}

	public Row getQrCodeManualEntryRow()
	{
		return qrCodeManualEntryRow;
	}

	public FormLabel getContainerShareableLabel()
	{
		return containerShareableLabel;
	}
}