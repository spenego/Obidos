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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.FileUtils;
import org.vectomatic.file.events.ErrorEvent;
import org.vectomatic.file.events.ErrorHandler;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import org.wisepersist.gwt.uploader.client.Uploader;
import org.wisepersist.gwt.uploader.client.Uploader.ButtonAction;
import org.wisepersist.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.wisepersist.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.wisepersist.gwt.uploader.client.events.FileDialogStartEvent;
import org.wisepersist.gwt.uploader.client.events.FileDialogStartHandler;
import org.wisepersist.gwt.uploader.client.events.FileQueueErrorEvent;
import org.wisepersist.gwt.uploader.client.events.FileQueueErrorHandler;
import org.wisepersist.gwt.uploader.client.events.FileQueuedEvent;
import org.wisepersist.gwt.uploader.client.events.FileQueuedHandler;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosAdhocItemRow;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.TwoFactorService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.FileUploadModalData;
import com.spenego.Obidos.client.util.TwoFAModalData;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

/*
The way 2FA QR Code image is converted to othauth TOTP URI:
 - vectomatic FileUploadExt HTML5 based library is used to upload image
 https://www.vectomatic.org/
 A test application is at: https://github.com/laaglu/lib-gwt-file-test
 
 In UI XML file we created:
  - Image to display QR Code
  - Image to resize loaded Image (hidden)
  - Canvas to cut down pixels (hidden)
 
 - in onReset(), call setupQRCodeImageUploadHandler(), we want to setup handlers only once.
   - When an QR code image upload is ended: 
     - Create base64 based image data URL. The image can be used, so we will resize
       it to 128x128 on client side, because we do not want to send a huge image to
       server side. We draw the image to an invisible Image widget. Note this does not
       decrease the pixel size of the image, only the image is displayed as 128x128.
       We need to reduce the pixels as well. To do that we'll use HTML5 Canvas.

     - When the image display is done, get the resized image and draw it on canvas
       as ImageElement. Then get the image data url from canvas as image/png. This URL
       actually represent a 200x200 Image. We will send that data URL to back-end to
       verify if it is actually a QR Code image. If it is, otpauth TOTP URL will be 
       returned and we display the QR Code image in the thumbnail image and print
       the TOTP URI at the text field.
       
       Note: 200x200 QRCode image seems to be ok to decode the largest densely
       packed QR code image.
 */

public class AddFreeFormatItemToContainerPresenter
        extends Presenter<AddFreeFormatItemToContainerPresenter.MyView, AddFreeFormatItemToContainerPresenter.MyProxy>
        implements AddFreeFormatItemToContainerUiHandlers
{
	private ObidosMessages glang 								= ObidosMessages.LANG;
	private List<ObidosAdhocItemRow> itemRows 					= new ArrayList<>();
    private List<ObidosTextBox> fieldNameTextBoxes  			= new ArrayList<>();
    private List<ObidosTextBox> valueTextBoxes 	    			= new ArrayList<>();
    private List<ObidosIntegerTextBox> displayOrderTextBoxes 	= new ArrayList<>();
    private boolean sItemCreated 			  					= false;
    private boolean sFileUploaded 								= false;
    private String sItemCreatedMessage        					= null;
    private Date sUploadStart									= null;
    
    private FileReader sQrCodeImageUploadReader = new FileReader();
    private File sQrCodeFile = null;
    private String sQrCodeImageUrl = null;
	private HandlerRegistration sQrCodeResizeImageHandler = null;
	private HandlerRegistration sQrCodeImageHandler = null;
	private String sImageStyle = "1.5px solid #ddd";
	private String sOkImageStyle = "1.5px solid #58B957";
	private String sInvalidImageStype = "1.5px solid #ff0000";
	private TwoFactorDTO sTwoFactorDTO = null;
	private TwoFAModalData sTwoFAModalData = ClientUtils.createTwoFAModal();
	
	private boolean isContainerPrivate = false;

	private boolean sFileUploadInProgress = false;
	private boolean sSupportedByLicense = true;
	
	private boolean wiseFileUploadHandlersSet = false;
	private FileUploadModalData sWiseUploadModalData = null;
	private String sFilenameToUpload = null;

    interface MyView extends View, HasUiHandlers<AddFreeFormatItemToContainerUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
    	public Heading getHeading();
        public Paragraph getHelpParagraph();
        public TextBox getContainerNameTextBox();
        public TextBox getItemNameTextBox();

        public FormGroup getFormGroup();
        public InlineRadio getPublicItemRadio();
        public InlineRadio getPrivateItemRadio();

        public Button getSaveButton();
        public Button getListButton();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public ObidosMessageRow getMessageRow();
		public Row getExpiresRow();
		public DatePicker getDatePicker();
		public ObidosTimeBox getTimeBox();
		public ObidosAdhocItemRow getFirstRow();
		public ObidosRowBottom2px getUploadRow();

		public ObidosRowBottom2px getQrCodeUploadRow();
		public FileUploadExt getQrCodefileUploadHTML5();
		public CheckBox getQrCodeManualCheckBox();
		public Row getQrCodeManualTextBoxesRow();
		public TextBox getAccountTextBox();
		public TextBox getIssuerTextBox();
		public TextBox getSecretTextBox();
		public Image getQrCodeImage();
		public Image getQrCodeResizerImage();
		public Canvas getQrCodeCanvas();
		public ObidosTextBox getTotpUriTextBox();
		public FormLabel getCheckMarkLabel();
		public FormLabel getCrossLabel();
		public ToggleSwitch getAddAttachmentSwitch();
		public ToggleSwitch getAdd2FASwitch();
		public Row getQrCodeInfoRow();
		public Button getQrCodeResetButton();
		public Button getShow2FACodeButton();
		public Button getGenURIButton();

		public Uploader getWiseUploader();
		public Span getFileChosenSpan();

       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
       	public ObidosRowBottom2px getQrCodeThumbnailImageRow();
       	public ObidosRowBottom2px getTotpUriRow();
       	public ObidosRowBottom2px getQrCodeImageRow();
       	public Row getQrCodeManualEntryRow();
       	public FormLabel getContainerShareableLabel();
    }

    @NameToken(NameTokens.ADD_FREE_FORMAT_ITEM_TO_CONTAINER)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<AddFreeFormatItemToContainerPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    AddFreeFormatItemToContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

        this.placeManager = placeManager;
        this.currentUser = currentUser;

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        setupFirstAdhocItemRowHanders();
        setupWisePersistFileUploader();
    }

    protected void onReveal()
    {
        super.onReveal();
        setDefaultPlaceHolder();
    }

    protected void onHide()
    {
        super.onHide();
        clearFormGroup();
        clearGlobals();
        hideAttachmentFileUploadWidet();
        clearAllQrCodeStuff();
        resetFileChosen();
        ClientUtils.resetLanguage(getView().getLanguageRow());
    }
    

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
		resetExpiration();
    	getView().getButtonToolBarBottom().adjustButtonsWidth();
    	makeFirstRowIconVisible();
		ClientUtils.configureDateFormat(getView().getDatePicker(), currentUser);
		resetRadioButtons();
        updateContainerNameInForm();
		setupQRCodeImageUploadHandler();
        showMessage(null);
        enforceLicenseRestrictions();
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }

    private void resetFileChosen()
    {
    	getView().getFileChosenSpan().setText(glang.noFileChosen());
        sFilenameToUpload = null;
    }
    
    private void enableWiseUploadButton(final boolean enable)
    {
    	Uploader wiseUploader = getView().getWiseUploader();
		String style = ObidosConstants.enableUploadButonText;
    	if (!enable)
    	{
    		gwtLog("Disbale wiseupload button");
			style = ObidosConstants.disableUploadButonText;
    		wiseUploader.setButtonText(style);
    	}
    	else
    	{
			wiseUploader.setButtonDisabled(! enable);
			getView().getFileChosenSpan().setText(glang.noFileChosen());
    	}
    }

    private void enforceLicenseRestrictions()
    {
    	FileUploadExt fileUploadExt = getView().getQrCodefileUploadHTML5();
    	Button genUriButton = getView().getGenURIButton();
    	Button clearButton = getView().getQrCodeResetButton();

    	enableWiseUploadButton(true);
		
		fileUploadExt.setEnabled(true);
		fileUploadExt.setTitle("");

		genUriButton.setEnabled(true);
		genUriButton.setTitle("");
		
		clearButton.setEnabled(true);
		clearButton.setTitle("");
		
    	if (currentUser != null)
    	{
    		UserDTO userDTO = currentUser.getUserDTO();
    		if (userDTO != null)
    		{
    			LicenseStats license = userDTO.getLicense();
    			if (license != null)
    			{
    				String t = glang.notSupportedByLicense();
    				boolean supported = ClientUtils.fromBoolean(license.getSupportsDocumentUpload());
    				sSupportedByLicense = supported;
    				enableWiseUploadButton(supported);
   					
   					getView().getAccountTextBox().setEnabled(supported);
   					getView().getIssuerTextBox().setEnabled(supported);
   					getView().getSecretTextBox().setEnabled(supported);
   					if (! supported)
   					{
   						getView().getFileChosenSpan().setText(t);
   					}
    				
    				// API does not have QRCode support variable TODO
   					supported = ClientUtils.fromBoolean(license.getSupportQRCodeUpload());

   					fileUploadExt.setEnabled(supported);
   					if (! supported)
   					{
   						fileUploadExt.setTitle(t); // tooltip
   					}

   					clearButton.setEnabled(supported);
   					if (! supported)
   					{
   						clearButton.setTitle(t);
   					}

   					genUriButton.setEnabled(supported);
   					if (! supported)
   					{
   						genUriButton.setTitle(t);
   					}
    			}
    		}
    		
    	}
    	
    }
    
    private void clearMessage()
    {
    	showMessage("");
    }
    
    private void makeFirstRowIconVisible()
    {
    	int sz = itemRows.size();
    	gwtLog("Size of template rows: " + sz);
    	if (sz == 0)
    	{
			getView().getFirstRow().getIconButton().setVisible(true);
			getView().getFirstRow().getIconButton().setIcon(IconType.PLUS);
    	}
    }

    private void resetExpiration()
	{
		getView().getDatePicker().setValue(null);
		getView().getTimeBox().setText("12:00 AM");
	}

    private void resetRadioButtons()
    {
    	getView().getPublicItemRadio().setValue(false);
    	getView().getPrivateItemRadio().setValue(false);
    	getView().getExpiresRow().setVisible(false);
    }

    private void clearGlobals()
    {
        sFileUploaded = false;
        sItemCreated = false;
		sFileUploadInProgress = false;
		sFilenameToUpload = null;
    }
    
    private void clearQR()
    {
    	sTwoFactorDTO = null;
    	sTwoFAModalData = null;
    	getView().getTotpUriTextBox().clear();
    }
    
    private void clearFormCollections()
    {
    	gwtLog("Clear form");
        getView().getItemNameTextBox().setFocus(true);
        ObidosAdhocItemRow firstRow = getView().getFirstRow();
        firstRow.getFieldNameTextBox().clear();
        firstRow.getValueTextBox().clear();
        getView().getPrivateItemRadio().setValue(false);
        getView().getPublicItemRadio().setValue(false);
        fieldNameTextBoxes.clear();
        valueTextBoxes.clear();
        displayOrderTextBoxes.clear();
        itemRows.clear();
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    @Override
    public void navigateToListContainers()
    {
    	ClientUtils.showListContainersPage(placeManager);
    }

    @Override
    public void resetForm()
    {
    }

    private boolean checkFieldValue(String value,String fieldType, int row)
    {
    	if (value == null || value.length() == 0)
    	{
    		showErrorMessage("Please specify " + fieldType + " at row " + row);
    		return false;
    	}
    	return true;
    }
    
    // if found that means user has loaded a valid 2FA QR Code or entered the
    // 2FA info correctly
    private String getTotpOthAuthUri()
    {
    	return null;
    	
    }
    
    private String getWith2FAMessage()
    {
    	String with = "";
    	String totpUri = getView().getTotpUriTextBox().getValue();
    	if (totpUri != null && totpUri.length() > 0)
    	{
    		with = " with 2FA Info ";
    	}
    	return with;
    }
    
    @Override
    public void addItemToContainer()
    {
    	showErrorMessage("");
        String containerName = getView().getContainerNameTextBox().getValue();

    	gwtLog("Add Item to Container..");
    	Long containerId = getContainerIdFromUrl();
    	if (containerId == null)
    	{
    		return;
    	}
        gwtLog("Container ID: " + containerId);

    	String itemName = getView().getItemNameTextBox().getValue();
    	if (itemName == null || itemName.length() == 0)
    	{
    		showErrorMessage("Please specify the Item Name");
    		return;
    	}
    	Boolean sharable = getView().getPublicItemRadio().getValue();
    	Boolean notSharale = getView().getPrivateItemRadio().getValue();
    	// solarqube
    	if (sharable != null && sharable.equals(notSharale))
    	{
    		showErrorMessage(glang.itemTypeWarning());
    		return;
    	}
    	
    	ObidosAdhocItemRow firstRow = getView().getFirstRow();

    	String firstRowFieldName = firstRow.getFieldNameTextBox().getValue();
    	String firstRowValue = firstRow.getValueTextBox().getValue();
    	Integer firstRowDisplayOrder = firstRow.getDisplayOrderTextBox().getValue();
    	if (!checkFieldValue(firstRowFieldName, "Field Name", 1))
    	{
    		return;
    	}
    	if (!checkFieldValue(firstRowValue, "Field Value", 1))
    	{
    		return;
    	}

   		gwtLog(" Field Name:" + firstRowFieldName);
   		gwtLog("Field Value:" + firstRowValue);
   		gwtLog("Field Position: " + firstRowDisplayOrder);
    	ArrayList<UserDefinedTypeValueDTO> list = new ArrayList<UserDefinedTypeValueDTO>();
        UserDefinedTypeValueDTO userDefinedTypeValueDTO = new UserDefinedTypeValueDTO();
        list.add(userDefinedTypeValueDTO);

    	UserDefinedTypeDTO template = new UserDefinedTypeDTO();
		template.setPersonal(false); // ??
		template.setName(null); // ??

    	// Add first row - it is statically created in ui.xml
		// Field Name (label)
    	Long fieldId = null;
		UserDefinedFieldDTO fieldDTO = new UserDefinedFieldDTO(fieldId, UserDefinedFieldDTO.TYPE_ENCRYPTED,firstRowFieldName,firstRowDisplayOrder);
		template.addField(fieldDTO);

		UserDefinedFieldValueDTO userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
		userDefinedFieldValueDTO.setBlobValue(firstRowValue.getBytes());
		userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
		userDefinedFieldValueDTO.setPosition(firstRowDisplayOrder);

		// now handle all the added rows
    	int nBoxes = fieldNameTextBoxes.size();
    	gwtLog(">>>>>>>>>> Number of rows: " + nBoxes);
    	Integer position = 1;
    	for (int i = 0; i < nBoxes; i++)
		{
    		String fieldName = fieldNameTextBoxes.get(i).getValue();
    		String fieldValue = valueTextBoxes.get(i).getValue();
    		Integer displayOrder = displayOrderTextBoxes.get(i).getValue();
    		position = displayOrder  + 1;

    		int row = i + 2;
    		// Issue #12
    		// if no field value specified, ignore the row
    		if (fieldName == null || fieldName.length() == 0)
    		{
    			gwtLog("Ignore row: " + row);
    			position = position - 1;
    			continue;
    		}
    		// if no value specified, use a space
    		if (fieldValue == null || fieldValue.length() == 0)
    		{
    			fieldValue = "";
    		}
    		gwtLog(" Field Name:" + fieldName);
    		gwtLog("Field Value:" + fieldValue);
    		gwtLog("Field Position: " + displayOrder);

    		// Field Name (label)
    		fieldDTO = new UserDefinedFieldDTO(fieldId, UserDefinedFieldDTO.TYPE_ENCRYPTED,fieldName,displayOrder);
    		template.addField(fieldDTO);

    		// Field values
            userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
            userDefinedFieldValueDTO.setBlobValue(fieldValue.getBytes());
            userDefinedFieldValueDTO.setPosition(displayOrder);
            userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
		}
    	
    	if (position == 1)
    	{
    		position = position + 1;
    	}
    	gwtLog("Display order: " + position);
    	// document
    	boolean fileSelected = fileSelectedForUpload();
    	if (fileSelected)
    	{
    		gwtLog("++++ Add type document");
			fieldDTO = new UserDefinedFieldDTO(fieldId, UserDefinedFieldDTO.TYPE_DOCUMENT,"Attachment",position);
			template.addField(fieldDTO);
			Long documentFieldId = null;
			fieldDTO.setId(documentFieldId);
			userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
            userDefinedFieldValueDTO.setPosition(position);
			userDefinedFieldValueDTO.setUserDefinedFieldId(documentFieldId);
			userDefinedFieldValueDTO.setBlobValue(sFilenameToUpload.getBytes());
			userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
    	}

     	if (position == 1)
    	{
    		position = position + 1;
    	}
    	gwtLog("XX Display order: " + position);
   	
    	if (shouldAddQRCode())
    	{
    		gwtLog("XXX add QR code uri to item");
    		// Bug# 875 do not increment position unless an attachment was uploaded
    		if (fileSelected)
    		{
    			position = position + 1;
    		}
    		gwtLog("QRCode field position: " + position);
    		fieldDTO = new UserDefinedFieldDTO(fieldId, UserDefinedFieldDTO.TYPE_QRCODE,"2FA otpAuth URI",position);
			template.addField(fieldDTO);
			Long twoFAFieldId = null;
			fieldDTO.setId(twoFAFieldId);
			userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
            userDefinedFieldValueDTO.setPosition(position);
			userDefinedFieldValueDTO.setUserDefinedFieldId(twoFAFieldId);
			userDefinedFieldValueDTO.setBlobValue(getView().getTotpUriTextBox().getValue().getBytes());
			userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
    	}
    	else
    	{
    		gwtLog("XXX QR code not set");
    	}


        boolean rc = validateDisplayOrderValues();
        gwtLog("Validate display order: " + rc);
        if (rc == false)
        {
            return;
        }


        gwtLog("Item name: " + itemName);
        gwtLog("Container id: " + containerId);
    	GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
		{


			@Override
			public void uponSuccess(ItemDTO itemDTO)
			{
				String with = getWith2FAMessage();

				Long documentId = ClientUtils.getDocumentId(itemDTO);
				gwtLog("Document ID: " + documentId);
				sItemCreatedMessage = ObidosMessages.LANG.item() + " " 
					+ "<span style=\"color: purple;\">" + "'" +itemName + "'" + "</span>"  + with
					+ " " + ObidosMessages.LANG.createdAndAdded() + " " 
					+ "<span style=\"color: purple;\">" + "'" + containerName + "'" + "</span>";


				boolean qrCodeImageDecoded = false;
				String totpUri = getView().getTotpUriTextBox().getValue();
				if (totpUri != null && totpUri.length() > 0)
				{
					qrCodeImageDecoded = true;
				}
				
				gwtLog("Item added to Container successfully");
				clearFormGroup();
				clearAllQrCodeStuff();
				updateRadioButtons();
				
				// upload document
				gwtLog("MMMM MMMM document id: " + documentId);
				gwtLog("MMMM MMMM fileSelectedForUplaod: " + fileSelectedForUpload());
				if (fileSelectedForUpload() && documentId != null)
				{
					gwtLog("Upload document");
					gwtLog("TOTP uri: " + getView().getTotpUriTextBox().getValue());
					uploadFile(documentId, qrCodeImageDecoded);
				}
				else
				{
					showMessage(sItemCreatedMessage);
				}

			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not add Item to Container: " + caught.getMessage());
			}
		};

    	ItemExpiration itemExpiration = null;
    	Date expireDate = ClientUtils.getDateFromDatePickerAndTimeBox(currentUser,
    			getView().getDatePicker(), getView().getTimeBox(), getView().getMessageRow());
    	if (expireDate != null)
    	{
    		itemExpiration = new ItemExpiration(expireDate);
    	}
   		gwtLog("Expire date: " + expireDate);
    	AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
    	ItemService.Utility.getInstance().create(authCreds, itemName, itemExpiration, containerId, null, sharable, template, list, callback);
    }

    private void clearFormGroup()
    {
    	getView().getItemNameTextBox().clear();
        FormGroup formGroup = getView().getFormGroup();
        formGroup.clear();
        resetExpiration();
        clearFormCollections();
        resetRadioButtons();
        sSupportedByLicense = true;
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    private Long getContainerIdFromUrl()
    {
        Long containerId = null;

        try
        {
            containerId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.CONTAINER_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            gwtLog("Could not get " + ObidosConstants.CONTAINER_ID + " in URL");
            return null;
        }
        return containerId;
    }
    
    private void updateRadioButtons()
    {
    	if (isContainerPrivate == false)
    	{
			getView().getPrivateItemRadio().setEnabled(true);
			getView().getPublicItemRadio().setEnabled(true);
			getView().getPublicItemRadio().setValue(false);
			getView().getPrivateItemRadio().setValue(false);
    	}
    	else
    	{
            getView().getPrivateItemRadio().setEnabled(false);
            getView().getPublicItemRadio().setEnabled(false);
            getView().getPrivateItemRadio().setValue(true);
    	}
    }

    private void updateContainerNameInForm()
    {
        Long containerId = getContainerIdFromUrl();
        if (containerId == null)
        {
            return;
        }

        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {
            @Override
            public void uponSuccess(ContainerDTO result)
            {
                boolean privateContainer = ClientUtils.fromBoolean(result.getIsPrivate());
                boolean sharedContainer = ClientUtils.fromBoolean(result.getShared());
                getView().getContainerNameTextBox().setValue(result.getName());
                FormLabel fl = getView().getContainerShareableLabel();
                if (result.getIsPrivate())
                {
                	isContainerPrivate = true;
                	fl.setHTML(glang.privateX());
                	getView().getPrivateItemRadio().setValue(true);
                	getView().getPrivateItemRadio().setEnabled(false);
                	getView().getPublicItemRadio().setEnabled(false);
                }
                else
                {
                	isContainerPrivate = true;
                	fl.setHTML(glang.shareable());
                	getView().getPrivateItemRadio().setEnabled(true);
                	getView().getPublicItemRadio().setEnabled(true);
                	getView().getPrivateItemRadio().setValue(false);
                	getView().getPublicItemRadio().setValue(false);

                }
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not fetch Container");
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
    }

    private void changePreviousRowClickHandlerToMinus()
    {
    	int sz = itemRows.size();
    	for (int i = 0; i < sz; i++)
    	{
    		ObidosAdhocItemRow irow = itemRows.get(i);
    		Button iconButton = irow.getIconButton();
    		if (iconButton.getIcon() == IconType.PLUS)
    		{
    			iconButton.setIcon(IconType.MINUS);
    			addMinusButtonHandler(irow);
    		}
    	}
    }
    
    private void addMinusButtonHandler(final ObidosAdhocItemRow irow)
    {
    	HandlerRegistration clickHandler = irow.getClickHandler();
    	if (clickHandler != null)
    	{
    		clickHandler.removeHandler();
    	}
    	
    	FormGroup formGroup = getView().getFormGroup();

    	ObidosTextBox fieldNameTextBox = irow.getFieldNameTextBox();
    	ObidosTextBox valueTextBox = irow.getValueTextBox();
    	ObidosIntegerTextBox doTextBox = irow.getDisplayOrderTextBox();
    	Button iconButton = irow.getIconButton();
    	
    	iconButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				formGroup.remove(irow);
				fieldNameTextBoxes.remove(fieldNameTextBox);
				valueTextBoxes.remove(valueTextBox);
				displayOrderTextBoxes.remove(doTextBox);
				normalizeDisplayOrderTextBoxes();
				itemRows.remove(irow);
			}
		});
    }
    
    private void addAdocItemRow(final boolean firstRow)
    {
    	gwtLog("addAdhocItemRow......");
    	if (firstRow)
    	{
    		// don't remove click handler but change the icon to MINUS and hide
    		ObidosAdhocItemRow frow = getView().getFirstRow();
    		Button plusButton = frow.getIconButton();
     		IconType iconType = plusButton.getIcon();
    		if (iconType == IconType.PLUS )
    		{
    			gwtLog("Hide Plus button from first row");
    			plusButton.setIcon(IconType.MINUS);
    			plusButton.setVisible(false);
    		}
    	}

    	FormGroup formGroup = getView().getFormGroup();
    	ObidosAdhocItemRow irow = new ObidosAdhocItemRow(IconType.PLUS);
    	formGroup.add(irow);

    	itemRows.add(irow);                       // save
    	
    	ObidosTextBox fieldnameTextBox = irow.getFieldNameTextBox();
    	ObidosTextBox valueTextBox = irow.getValueTextBox();
    	ObidosIntegerTextBox doTextBox = irow.getDisplayOrderTextBox();

    	fieldNameTextBoxes.add(fieldnameTextBox); // save
    	valueTextBoxes.add(valueTextBox);         // save
    	displayOrderTextBoxes.add(doTextBox);     // save
    	
    	int sz = fieldNameTextBoxes.size();
    	doTextBox.setValue(sz + 1);
    	
    	Button iconButton = irow.getIconButton();
    	HandlerRegistration clickHandler = iconButton.addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				changePreviousRowClickHandlerToMinus();
				addAdocItemRow(false); // recursive
			}
		});
    	irow.setClickHandler(clickHandler);       // save

        fieldnameTextBox.setFocus(true);

        fieldnameTextBox.addKeyDownHandler((KeyDownEvent e) ->
        {
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
                showMessage(null);
                valueTextBox.setFocus(true);
            }
        });

        valueTextBox.addKeyDownHandler((KeyDownEvent e) ->
        {
            showMessage(null);
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
				changePreviousRowClickHandlerToMinus();
				addAdocItemRow(false); // recursive
            }
        });

    	
    }

    /*
    @Override
    public void addFields()
    {
        FormGroup formGroup = getView().getFormGroup();

        // Label
        Row row = new Row();
			FlowPanel fp1 = new FlowPanel();
			fp1.addStyleName("col-sm-offset-2 col-sm-3");
			ObidosTextBox labelTextBox = new ObidosTextBox();
			labelTextBox.setPlaceholder("Field Name");
			labelTextBox.setMaxLength(ObidosConstants.FIELD_NAME_LENGTH);
			fp1.add(labelTextBox);
			row.add(fp1);

        labelTextBoxes.add(labelTextBox);

        // Value
			FlowPanel fp2 = new FlowPanel();
			fp2.addStyleName("col-sm-3");
			ObidosTextBox valueTextBox = new ObidosTextBox();
			valueTextBox.setPlaceholder("Field Value");
			fp2.add(valueTextBox);
			row.add(fp2);

        valueTextBoxes.add(valueTextBox);

        // Display order
			FlowPanel fp3 = new FlowPanel();
			fp3.addStyleName("col-sm-1");
			ObidosIntegerTextBox doTextBox = new ObidosIntegerTextBox();
			doTextBox.setPlaceholder("Display Order");
			fp3.add(doTextBox);
			row.add(fp3);
//        formGroup.add(row);

        displayOrderTextBoxes.add(doTextBox);
        int doVal = displayOrderTextBoxes.size() + 1;
        doTextBox.setValue(doVal);

        // Minus button
			FlowPanel fp4 = new FlowPanel();
			fp4.addStyleName("col-sm-1");
			Button b = new Button();
			b.setType(ButtonType.INFO);
			b.setIcon(IconType.MINUS);
			fp4.add(b);
			row.add(fp4);
        formGroup.add(row);

        row.getElement().getStyle().setMarginBottom(4, Unit.PX);
//        fp1.getElement().getStyle().setMarginBottom(10, Unit.PX);
//        fp2.getElement().getStyle().setMarginBottom(10, Unit.PX);
//        fp3.getElement().getStyle().setMarginBottom(10, Unit.PX);
//        fp4.getElement().getStyle().setMarginBottom(10, Unit.PX);


        b.addClickHandler(e ->
        {
            showMessage("");
            formGroup.remove(row);
//            formGroup.remove(fp2);
//            formGroup.remove(fp3);
//            formGroup.remove(fp4);

            labelTextBoxes.remove(labelTextBox);
            valueTextBoxes.remove(valueTextBox);
            displayOrderTextBoxes.remove(doTextBox);

            normalizeDisplayOrderTextBoxes();

        });

        labelTextBox.setFocus(true);

        labelTextBox.addKeyDownHandler((KeyDownEvent e) ->
        {
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
                showMessage("");
                valueTextBox.setFocus(true);
            }
        });

        valueTextBox.addKeyDownHandler((KeyDownEvent e) ->
        {
            showMessage("");
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
                addFields();
            }
        });
    }
    */

    private void normalizeDisplayOrderTextBoxes()
    {
        int sz = displayOrderTextBoxes.size();
        for (int i = 2; i <= sz + 1; i++)
        {
            int idx = i - 2;
            gwtLog("Get value at index: " + idx);
            ObidosIntegerTextBox otbox = displayOrderTextBoxes.get(idx);
            otbox.setValue(i);
        }

    }

    private boolean validateDisplayOrderValues()
    {
        int nBoxes = displayOrderTextBoxes.size();
        Integer[] orderArray = new Integer[nBoxes+1];
        orderArray[0] = getView().getFirstRow().getDisplayOrderTextBox().getValue();

        for (int i = 0; i < nBoxes; i++)
        {
            ObidosIntegerTextBox textBox = displayOrderTextBoxes.get(i);
            int intValue = textBox.getValue();
            //int row = i + 2;

            orderArray[i+1] = intValue;
        }
        List<Integer> list = Arrays.asList(orderArray);
        List<Integer> dps = list.stream().distinct().filter(entry -> Collections.frequency(list, entry) > 1).collect(Collectors.toList());
        if (dps.size() > 0)
        {
            showErrorMessage("Duplicate Display Order number " + dps.get(0));
            return false;
        }
        gwtLog("dps size: " + dps.size());

        return true;
    }

	@Override
	public void publicRadioCallback()
	{
		getView().getExpiresRow().setVisible(true);
		showMessage(null);
	}

	@Override
	public void privateRadioCallback()
	{
		getView().getExpiresRow().setVisible(false);
		showMessage(null);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void listItems()
	{
		ClientUtils.showPage(placeManager, NameTokens.LIST_ALL_MY_ITEMS);
	}
	
	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

	private void setupFirstAdhocItemRowHanders()
	{
		gwtLog("Setup first row click handler..");
		
		ObidosAdhocItemRow firstRow = getView().getFirstRow();
		ObidosIntegerTextBox doTextBox = firstRow.getDisplayOrderTextBox();
		doTextBox.setValue(1);
		doTextBox.setReadOnly(true);
		
		Button iconButton = firstRow.getIconButton();
		HandlerRegistration clickHandler = iconButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				addAdocItemRow(true);
			}
		});
		firstRow.setClickHandler(clickHandler);
		
		ObidosTextBox valueTextBox = firstRow.getValueTextBox();
		valueTextBox.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent e)
			{
				if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					addAdocItemRow(true);
				}
			}
		});

		
		
	}
	private boolean fileSelectedForUpload()
	{
		Uploader wiseUploader = getView().getWiseUploader();
		int n = wiseUploader.getStats().getFilesQueued();
		gwtLog(">> File queued: " + n);
		if (n > 0 && sFilenameToUpload != null)
		{
			gwtLog(">> filename to uplaod: " + sFilenameToUpload);
			return true;
		}
		return false;
	}
	
	private boolean qrCodeImageSelectedForUpload()
	{
		FileUploadExt fu = getView().getQrCodefileUploadHTML5();
		if (fu.getName().length() == 0)
		{
			return false;
		}
		return true;
	}
	
	private boolean shouldAddQRCode()
	{
		String otpAuthUri = getView().getTotpUriTextBox().getValue();
		if (otpAuthUri != null && otpAuthUri.length() > 0)
		{
			return true;
		}
		return false;
	}
	
	// If a valid QR Code is uploaded
	// of if all the fields are filled up
	private boolean qrCodeInputComplete()
	{
		return false;
	}

	

	// https://stackoverflow.com/questions/16883231/check-file-size-before-upload-using-gwt
	private native int getFileSize(final Element data) /*-{
    	return data.files[0].size;
	}-*/;
	
	private void clearUpload()
	{
		sItemCreatedMessage = null;
	}

	private void uploadFile(final long documentId, final boolean qrCodeImageDecoded)
	{
		GwtAsyncWrapper<LimitedFernetDTO> callback = new GwtAsyncWrapper<LimitedFernetDTO>(this)
		{

			@Override
			public void uponSuccess(LimitedFernetDTO dto)
			{
				StringBuilder sb = new StringBuilder(256);
				sb.append("token=")
				  .append(dto.getToken());
				String parameters = sb.toString();
				String url = GWT.getHostPageBaseURL() + "upload?" + parameters;
				gwtLog("XX upload url: " + url);
				Uploader u = getView().getWiseUploader();
				u.setUploadURL(url);

				if (sFilenameToUpload == null)
				{
					showErrorMessage("No file chosen for upload!");
					return;
				}
				gwtLog("XXX URL: " + url);
				showMessage("Item created. Uploading file, please wait........");
				sUploadStart = new Date();
				if (sWiseUploadModalData != null)
				{
					gwtLog("Swow wise dialog");
					// HTML5 upload
					sWiseUploadModalData.setFilename(sFilenameToUpload);
					sWiseUploadModalData.clear();
					
					// clear them when dialog is popped down
					// Bug #875
					sWiseUploadModalData.setUploadRow(getView().getUploadRow());
					sWiseUploadModalData.setAttachmentSwitch(getView().getAddAttachmentSwitch());
					sWiseUploadModalData.setQrcodeImageDecoded(false);
					String totpUri = getView().getTotpUriTextBox().getValue();
					gwtLog("XXX qr code image decoded: " + qrCodeImageDecoded);
					sWiseUploadModalData.setQrcodeImageDecoded(qrCodeImageDecoded);

					getView().getWiseUploader().startUpload();
					ClientUtils.showWiseUploadModal(sWiseUploadModalData);
					gwtLog("XXXXXXX");
				}
				else
				{
					gwtLog("Wise dialog is null");
				}
				// Old GWT upload
				// formPanel.submit(); 
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get security token to upload file: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		FernetService.Utility.getInstance().getFileUploadFernetToken(authCreds, documentId, ObidosConstants.ACTION_TYPE_NA, callback);
	}

	// callback/handler for CheckBox
	@Override
	public void showQrCodeTextBoxes()
	{
		Boolean v = getView().getQrCodeManualCheckBox().getValue();
		if (v && ! sSupportedByLicense)
		{
//			ClientUtils.showNotSupportedByLicenseDialog();
		}

		clearQR();
		showQRCodeButtons(false);
		showCrossIcon();
		setDefaultPlaceHolder();
		getView().getMessageRow().showMessage("");
		if (v == Boolean.TRUE)
		{
			getView().getQrCodeUploadRow().setVisible(false);
			getView().getQrCodeManualTextBoxesRow().setVisible(true);
		}
		else
		{
			getView().getQrCodeUploadRow().setVisible(true);
			getView().getQrCodeManualTextBoxesRow().setVisible(false);
		}
	}

	private void setupQRCodeImageUploadHandler()
	{
		FileUploadExt fileupload = getView().getQrCodefileUploadHTML5();
	    fileupload.getElement().removeAttribute("multiple");
	    fileupload.getElement().setAttribute("accept", "image/*");
	    
	    sQrCodeImageUploadReader.addLoadEndHandler(new LoadEndHandler()
		{
			
			@Override
			public void onLoadEnd(LoadEndEvent event)
			{
				if (sQrCodeImageUploadReader.getError() == null)
				{
					sQrCodeImageUrl = null;
					setupQRCodeImageLoadHandler();
					setupResizeImageLoadHandler();
					String imageDataUrl = createQRCodeImageUrl(sQrCodeFile);
					if (imageDataUrl == null)
					{
						return;
					}
					sQrCodeImageUrl = null;
					gwtLog("XX before size url len: " + imageDataUrl.length());
					resizeQRCodeImage(imageDataUrl);
				}
			}
		});
	    
	    sQrCodeImageUploadReader.addErrorHandler(new ErrorHandler()
		{
			
			@Override
			public void onError(ErrorEvent event)
			{
				handleQRCodeUploadError(sQrCodeFile);
			}
		});
	}
	private void handleQRCodeUploadError(File file)
	{
		FileError error = sQrCodeImageUploadReader.getError();
		String errorDesc = "";
		if (error != null)
		{
			ErrorCode errorCode = error.getCode();
			if (errorCode != null)
			{
				errorDesc = ": " + errorCode.name();
			}
		}
		Window.alert("File loading error for file: " + file.getName() + "\n" + errorDesc);

	}

	private void setupResizeImageLoadHandler()
	{
		if (sQrCodeResizeImageHandler != null)
		{
			gwtLog("XXX QRCode Resize image handler is already set");
			return;
		}
		Image image = getView().getQrCodeResizerImage();
		sQrCodeResizeImageHandler = image.addLoadHandler(new LoadHandler()
		{
			
			@Override
			public void onLoad(LoadEvent event)
			{
				if (sQrCodeImageHandler == null)
				{
					gwtLog("QR Code Image handler is not set yet");
					return;
				}

				// show qr code image
				showQrCodeInfoRow(true);

				gwtLog("At Resize Image onLoad handler");
				showMessage("QR Code Image represents valid 2FA TOTP OTPAUTH URI");
				int w = image.getWidth();
				int h = image.getHeight();
				gwtLog(">>>>>>>>>>>> wxh=" + w + "x" + h);

				Canvas canvas = getView().getQrCodeCanvas();
				canvas.setWidth(200 + "px");
				canvas.setHeight(200 + "px");
				canvas.setCoordinateSpaceWidth(w);
				canvas.setCoordinateSpaceHeight(h);

				Context2d context = canvas.getContext2d();
				ImageElement resizedImage = ImageElement.as(image.getElement());
				//canvas.setVisible(true);
				context.drawImage(resizedImage, 0, 0, w, h);
				//context.drawImage(resizedImage, 0, 0, 128, 128);
				gwtLog("draw on canvas...");
				String url = canvas.toDataUrl("image/png");
				gwtLog("URL size after resize: " + url.length() + " bytes");
				getView().getQrCodeImage().setUrl(url);
				sQrCodeImageUrl = url;
			}
		});
		
		image.addErrorHandler(new com.google.gwt.event.dom.client.ErrorHandler()
		{
			
			@Override
			public void onError(com.google.gwt.event.dom.client.ErrorEvent event)
			{
				gwtLog("Could not load image on resize image error handler");
				showErrorMessage("Could not load image");
				clearQR();
				sQrCodeImageUrl = null;
			}
		});
	}

	private void setupQRCodeImageLoadHandler()
	{
		if (sQrCodeImageHandler != null)
		{
			gwtLog("XXX QR Code image handler is already set");
			return;
		}
		Image image = getView().getQrCodeImage();
		sQrCodeImageHandler = image.addLoadHandler(new LoadHandler()
		{
			
			@Override
			public void onLoad(LoadEvent event)
			{
//				gwtLog("At QR Code Image onLoad handler: " + imageUrl);
				gwtLog("At QR Code Image onLoad handler");
				if (sQrCodeImageUrl != null)
				{
					gwtLog(">> It's an image, Try to Decode QR Code");
					decodeQRCodeImage();
				}
				else
				{
					gwtLog(">>>>>>>>>>>>>>>>>>>>>image url is null, will not try to decode qr code");
				}
			}
		});
		image.addErrorHandler(new com.google.gwt.event.dom.client.ErrorHandler()
		{
			
			@Override
			public void onError(com.google.gwt.event.dom.client.ErrorEvent event)
			{
				sQrCodeImageUrl = null;
				gwtLog("on qr code image error handler");
				showErrorMessage("Could not load image");
			}
		});

	}


	private String  createQRCodeImageUrl(final File file)
	{
		gwtLog(">>>>>>>>>>>>>>>> in createIamgeUrl");
		String type = file.getType();
		int length = type.length();
		gwtLog("Type length: " + length);
		gwtLog("Type: '" + type + "'");
		String result = sQrCodeImageUploadReader.getStringResult();
		String url = FileUtils.createDataUrl(file.getType(), result);
		gwtLog("returning url");
		return url;
	}

	private void resizeQRCodeImage(String dataUrl)
	{
		Image resizeImage = getView().getQrCodeResizerImage();
		gwtLog("Reisize image w: " + resizeImage.getWidth());
		gwtLog("Reisize image h: " + resizeImage.getWidth());
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

		int newWidth = 128;
		int newHeight = 128;
		if (width < 128)
		{
			newWidth = width;
		}
		if (height < 128)
		{
			newHeight = height;
		}
		float aspectRatio =  (float) ((width * 1.0) / (height * 1.0));
		newHeight = Math.round((newWidth / aspectRatio));
		newWidth = Math.round(( newHeight * aspectRatio ));

		int w = newWidth;
		int h = newHeight;
		gwtLog("size=" + w + "x" + h);
		
    	gwtLog("URL before resize: " + dataUrl.length() + " bytes");
		resizeImage.setPixelSize(w, h);
		resizeImage.getElement().getStyle().setWidth(w, Unit.PX);
		resizeImage.getElement().getStyle().setHeight(h, Unit.PX);
		resizeImage.setUrl(dataUrl);
	}
	// For that piece of crap called IE
	private static native int ieWidth(Element elt) /*-{
		return elt.naturalWidth;
	}-*/;

	private static native int ieHeight(Element elt) /*-{
		return elt.naturalHeight;
	}-*/;
	
	private void resetQRCodeImageHandlers() 
	{
		sQrCodeImageUrl = null;
		HandlerRegistration h = sQrCodeResizeImageHandler;
		if (h != null)
		{
			h.removeHandler();
			h = null;
		}
		h  = sQrCodeImageHandler;
		if (h != null)
		{
			h.removeHandler();
			h = null;
		}
		// TODO some other stuff
	}
	
	private void resetQRCodeTextBoxes()
	{
		getView().getQrCodeManualCheckBox().setValue(false);
		getView().getTotpUriTextBox().clear();
		getView().getIssuerTextBox().clear();
		getView().getAccountTextBox().clear();
		getView().getSecretTextBox().clear();
	}

	@Override
	public void resetQRCodeInfo()
	{
		showQRCodeButtons(false);
		resetQRCodeTextBoxes();
		showCrossIcon();
		getView().getQrCodeUploadRow().setVisible(true);
		getView().getQrCodeManualTextBoxesRow().setVisible(false);
		getView().getMessageRow().showMessage("");
		setDefaultPlaceHolder();
		// the QR code image row to make it look like default view
		// APr-03-2025
		getView().getQrCodeImageRow().setVisible(false);
	}
	
	private void clearAllQrCodeStuff()
    {
		clearQR();
		// DO NOT remove QR code image handlers
		resetQRCodeInfo();
		resetQRCodeTextBoxes();
		getView().getAdd2FASwitch().setValue(false);
		getView().getQrCodeInfoRow().setVisible(false);
		setDefaultPlaceHolder();
    }

	
	private void showQRCodeButtons(boolean visible)
	{
		show2FACodeButton(visible);
	}
	private void showResetQRcodeButton(boolean visible)
	{
		getView().getQrCodeResetButton().setVisible(visible);
	}
	
	private void showGenerateURIButton(boolean visible)
	{
		getView().getGenURIButton().setVisible(visible);
	}
	
	private void show2FACodeButton(boolean visible)
	{
		getView().getShow2FACodeButton().setVisible(visible);
	}

	@Override
	public void processQRCodeImageFile()
	{
		sQrCodeImageUrl = null;
		
		gwtLog("in processFile");
		setDefaultPlaceHolder();
		Image ri = getView().getQrCodeImage();
		ri.setUrl("128x128.png");
		ri.setPixelSize(128, 128);
		ri.getElement().getStyle().setWidth(128, Unit.PX);
		ri.getElement().getStyle().setHeight(128, Unit.PX);

		FileList files = getView().getQrCodefileUploadHTML5().getFiles();
		if (files.getLength() == 0)
		{
			gwtLog("No files selected");
			Window.alert("No image file selected.....");
			return;
		}
		sQrCodeFile = files.getItem(0);
		dumpFileInfo(sQrCodeFile);
		String type = sQrCodeFile.getType();
		gwtLog("Type: " + type);
		int length = type.length();
		gwtLog("type len: " + length);
		if (type == null || type.length() == 0)
		{
			Window.alert("Could not determine image type");
			setDefaultImagBorderStyle();
			return;
		}

		try
		{
			if (type.startsWith("image/"))
			{
				gwtLog("read as binary string...");
				sQrCodeImageUrl = null;
				sQrCodeImageUploadReader.readAsBinaryString(sQrCodeFile);
				gwtLog("back");
			}
			else
			{
				sQrCodeImageUrl = null;
				gwtLog(">>>>>>>>>> Setting default image");
				setDefaultPlaceHolder();
			}
			
		}
		catch (Throwable t)
		{
			gwtLog("Error: " + t.getMessage());
			Window.alert("ERRROR: " + t.getMessage());
		}
	}

	private void setDefaultPlaceHolder()
	{
		gwtLog("++++ setting default 128x128 image");
		sQrCodeImageUrl = null;
		Image ri = getView().getQrCodeImage();
		ri.setTitle("QR Code image placeholder");
		ri.setUrl("128x128.png");
		ri.setPixelSize(128, 128);
		ri.getElement().getStyle().setWidth(128, Unit.PX);
		ri.getElement().getStyle().setHeight(128, Unit.PX);
		ri.setTitle("");

		setDefaultImagBorderStyle();
	}
	private void setDefaultImagBorderStyle() 
	{
		getView().getQrCodeImage().getElement().getStyle().setProperty("border", sImageStyle);
	}
	
	private void setValidQRCodeImageBorderStyle()
	{
		getView().getQrCodeImage().getElement().getStyle().setProperty("border", sOkImageStyle);
	}

	private void setinValidQRCodeImageBorderStyle()
	{
		getView().getQrCodeImage().getElement().getStyle().setProperty("border", sInvalidImageStype);
	}

	private void dumpFileInfo(File file)
	{
		gwtLog("File name: " + file.getName());
		gwtLog("File type: " + file.getType());
		gwtLog("File size: " + file.getSize());
	}
	private void updateImageTooltip(String tooltip)
	{
		Image ri = getView().getQrCodeImage();
		ri.setTitle(tooltip);
	}

	private void updateImageTooltip(TwoFactorDTO dto)
	{
		String title = "";
		String issuer = dto.getIssuser();
		String email = dto.getUserEmail();
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
		Image ri = getView().getQrCodeImage();
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
	private void decodeQRCodeImage()
	{
		gwtLog("in decode QRCodeImage");
		if (sQrCodeImageUrl == null)
		{
			showErrorMessage("No QRCode image data URL found");
			return;
		}
		gwtLog("XX After rsize lenght: " + sQrCodeImageUrl.length());
		//GWT.log("XX: " + sQrCodeImageUrl);

    	GwtAsyncWrapper<TwoFactorDTO> callback = new GwtAsyncWrapper<TwoFactorDTO>(this)
    	{

			@Override
			public void uponSuccess(TwoFactorDTO dto)
			{
				gwtLog("OK");
				sTwoFactorDTO = dto;
				showCheckMarkIcon();
				setValidQRCodeImageBorderStyle();
				getView().getShow2FACodeButton().setVisible(true);
				getView().getTotpUriTextBox().setValue(dto.getOtpUri());
				updateImageTooltip(dto);
				gwtLog("Code: " + dto.getTwoFACode());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("Failed to decode qr code image: " + caught.getMessage());
				clearQR();
				showCrossIcon();
				getView().getShow2FACodeButton().setVisible(false);
				updateImageTooltip("Image has no valid 2FA TOTP OTPAUTH URI");
				String emsg = caught.getMessage();
				gwtLog("XXX Emsg len: " + emsg.length());
				if (emsg != null && emsg.length() > 80)
				{
					emsg = ClientUtils.trimStringEllipsis(emsg, 80);
				}
				showErrorMessage(emsg);
			}
    		
    	};
    	AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
    	TwoFactorService.Utility.getInstance().decodeQRCodeImageDataUri(authCreds, sQrCodeImageUrl, callback);
	}
	
	private void showCheckMarkIcon()
	{
		getView().getCheckMarkLabel().setVisible(true);
		getView().getCrossLabel().setVisible(false);
	}
	
	private void showCrossIcon()
	{
		getView().getCheckMarkLabel().setVisible(false);
		getView().getCrossLabel().setVisible(true);
		setinValidQRCodeImageBorderStyle();
	}
	
	private void showQrCodeInfoRow(boolean show)
	{
		getView().getQrCodeImageRow().setVisible(show);
	}

	// callback for the switch
	@Override
	public void showQRCodeInfoWidgets()
	{
		Boolean v = getView().getAdd2FASwitch().getValue();
		getView().getQrCodeInfoRow().setVisible(v);
		if (v && ! sSupportedByLicense)
		{
			// Bug #822
			ClientUtils.showLicenseEnforcementDialogQRCode(
					getView().getAdd2FASwitch(),
					getView().getQrCodeInfoRow());
		}

		getView().getQrCodeInfoRow().setVisible(v);
		// hide qr code image row, only show if necessary
		showQrCodeInfoRow(false);
		// show many entry checkbox
		getView().getQrCodeManualCheckBox().setVisible(true);
	}
	
	@Override
	public void showAttachmentFileUploadWidget()
	{
		Boolean v = getView().getAddAttachmentSwitch().getValue();
		getView().getUploadRow().setVisible(v);
		if (v && ! sSupportedByLicense)
		{
			// Bug #822
			ClientUtils.showLicenseEnforcementDialog(
					getView().getAddAttachmentSwitch(),
					getView().getUploadRow());
		}
	}
	
	private void hideAttachmentFileUploadWidet()
	{
		getView().getUploadRow().setVisible(false);
		getView().getAddAttachmentSwitch().setValue(false);
		
	}
	
	private TwoFAModalData getTwoFAModalData()
	{
		if (sTwoFAModalData == null)
		{
			sTwoFAModalData = ClientUtils.createTwoFAModal();
		}
		return sTwoFAModalData;
	}
	

	@Override
	public void show2FACodeDialog()
	{
		TwoFAModalData md = getTwoFAModalData();
		TwoFactorDTO dto = sTwoFactorDTO;
		if (dto != null && getView().getShow2FACodeButton().isVisible())
		{
			md.setIssuer(dto.getIssuser());
			md.setAccount(dto.getUserEmail());
			md.setBase32Secret(dto.getSecret());
			md.setQrCodeInfoRow(getView().getQrCodeInfoRow());
			clearMessage();
			ClientUtils.showTwoFACode(placeManager, md);
		}
	}

	@Override
	public void generateTotpOtpAuthUri()
	{
		// construct URI
		String issuer = getView().getIssuerTextBox().getValue();
		if (issuer == null || issuer.length() == 0)
		{
			showErrorMessage("Please specify Issuer");
			return;
		}

		String account = getView().getAccountTextBox().getValue();
		if (account == null || account.length() == 0)
		{
			showErrorMessage("Please specify Account");
			return;
		}

		String secret = getView().getSecretTextBox().getValue();
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

    	GwtAsyncWrapper<TwoFactorDTO> callback = new GwtAsyncWrapper<TwoFactorDTO>(this)
    	{

			@Override
			public void uponSuccess(TwoFactorDTO dto)
			{
				gwtLog("OK");

				// show the 2fa qrcode image row
				getView().getQrCodeImageRow().setVisible(true);

				sTwoFactorDTO = dto;
				showCheckMarkIcon();
				// set qr code image
				String imageDataUrl = dto.getBase64QrImage();
				gwtLog(imageDataUrl);
				if (imageDataUrl != null);
				{
					Image image = getView().getQrCodeImage();
					image.setUrl("data:image/png;base64," + imageDataUrl);
				}
				setValidQRCodeImageBorderStyle();
				getView().getShow2FACodeButton().setVisible(true);
				getView().getTotpUriTextBox().setValue(dto.getOtpUri());
				updateImageTooltip(dto);
				gwtLog("Code: " + dto.getTwoFACode());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				clearQR();
				showCrossIcon();
				getView().getShow2FACodeButton().setVisible(false);
				updateImageTooltip("Invalid TOTP othAuth URI");
				showErrorMessage(caught.getMessage());
			}
    		
    	};
    	AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
    	TwoFactorService.Utility.getInstance().generate2FACode(authCreds, otpAuthUri, callback);
	}
	
	private void setupWisePersistFileUploader()
	{
		gwtLog(">>>>>>>>>>>>>>>>>> in setupWisePersistFileUploader");
		Date d = new Date();
		gwtLog("Date: " + d);
		// move the following to view
		// set our upload servlet URL
		Uploader u = getView().getWiseUploader();

		// button style
//		u.setButtonText("<button type=\"button\" class=\"btn btn-primary\"><i class=\"fa fa-upload\"></i> Choose File</button>");
		u.setButtonText(ObidosConstants.enableUploadButonText);
		u.setButtonCursor(Uploader.Cursor.HAND).setButtonAction(Uploader.ButtonAction.SELECT_FILE);
		u.setButtonAction(ButtonAction.SELECT_FILE); // select single file
		// TODO set filesize etc

		u.setFileQueuedHandler(new FileQueuedHandler()
		{
			@Override
			public boolean onFileQueued(FileQueuedEvent fileQueuedEvent)
			{
				String filename = fileQueuedEvent.getFile().getName();
				gwtLog("in setFileQueuedHandler File: " + filename + " queued");
				sFilenameToUpload = filename;
				getView().getFileChosenSpan().setText(sFilenameToUpload);
				return true;
			}
		});

		u.setFileQueueErrorHandler(new FileQueueErrorHandler()
		{
			@Override
			public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent)
			{
				String name = fileQueueErrorEvent.getFile().getName();
				gwtLog("ERROR queuing file: " + name);
				sFilenameToUpload = null;
				return false;
			}
		});
	
		u.setFileDialogStartHandler(new FileDialogStartHandler()
		{
			
			@Override
			public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent)
			{
				gwtLog("File dialog started");
				return true;
			}
		});

		u.setFileDialogCompleteHandler(new FileDialogCompleteHandler()
		{
			
			@Override
			public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent)
			{
				int n = fileDialogCompleteEvent.getNumberOfFilesQueued();
				gwtLog(n + " in setFIleDialogComplete Handler Files queued");
				return true;
			}
		});

		
		if (sWiseUploadModalData == null)
		{
			gwtLog("Create the wise upload modal");
			ObidosTextBox otb = null;
			sWiseUploadModalData = ClientUtils.createWiseFileUploadProgressModal(
					placeManager,
					u,
					sFilenameToUpload,
					otb, // in edit item view
					NameTokens.ADD_FREE_FORMAT_ITEM_TO_CONTAINER,
					getView().getMessageRow(),
					getView().getFileChosenSpan());

			// Bug #875
			sWiseUploadModalData.setUploadRow(getView().getUploadRow());
			sWiseUploadModalData.setAttachmentSwitch(getView().getAddAttachmentSwitch());
		}

	}

	@Override
	public void languageListBoxCallback()
	{
		ListBox lb = getView().getLanguageListBox();
		String lang = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		gwtLog("index: "+ idx);
		gwtLog("Lang: " + lang);
		// only support English and Bangla for editing at this time
		switch(idx)
		{
			case 0: // English
			{
				ClientUtils.enableBanglaEditing(false, getView().getLanguageRow());
				break;
			}
			case 1: // Bangla
			{
				ClientUtils.enableBanglaEditing(true, getView().getLanguageRow());
				break;
			}
		}
	}

}
