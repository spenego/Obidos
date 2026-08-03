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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosItemRow;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.QRCodeService;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.FileUploadModalData;
import com.spenego.Obidos.client.util.TwoFAQRCodeUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.dto.QRCodeDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class AddItemToContainerPresenter
        extends Presenter<AddItemToContainerPresenter.MyView, AddItemToContainerPresenter.MyProxy>
        implements AddItemToContainerUiHandlers
{
	private ObidosMessages glang 								= ObidosMessages.LANG;
    private List<TextBox> fieldTextBoxes = new ArrayList<TextBox>();
    // we only have one Note at this time
    private Summernote summernote 			  = ClientUtils.createSummernote(ObidosConstants.NOTE_HEIGHT);
    private Map<TextBox,Long> fieldIds 		  = new HashMap<>();
    private Map<Summernote,Long> noteFieldIds = new HashMap<>();
    private boolean sItemCreated 			  = false;
    private String sItemName 				  = null;
    private Long sDocumentFieldId             = null;
    private Long sQRCodeFieldId               = null;
    private Date sUploadStart                 = null;
    private String sItemCreatedMessage        = null;
    private boolean sFileUploaded             = false;

    private Long fieldTypeNote = UserDefinedFieldDTO.TYPE_NOTES_FIELD_ID;
    
	private boolean sFileUploadInProgress = false;

    private TwoFAQRCodeUtils sQRCodeUtils = null;
	private boolean sSupportedByLicense = true;
	
	private boolean wiseFileUploadHandlersSet = false;
	private FileUploadModalData sWiseUploadModalData = null;
	private String sFilenameToUpload = null;

	private boolean isContainerPrivate = false;
	
	// WiFi QRCode - starts --
	private boolean wifiQrCode     = false;
	private String  wifiSsidName   = null;
	private String  wifiPassword   = null;
	private boolean wifiHiddenSsid = false;
	private String  wifiEncryption = null;
	
	private Timer wifiSsidTypingTimer = null;
	private Timer wifiPasswordTypingTimer = null;
	// WiFi QRCode - starts --
	private String sImageStyle = "1.5px solid #ddd";
	private String sOkImageStyle = "1.5px solid #58B957";
	private String sInvalidImageStype = "1.5px solid #ff0000";


    interface MyView extends View, HasUiHandlers<AddItemToContainerUiHandlers>
    {
        public Paragraph getHelpParagraph();
        public FormLabel getItemNameLabel();
        public TextBox getTemplateNameTextBox();
        public ObidosTextBox getItemNameTextBox();

        public FormGroup getFormGroup();

        // Radio boxes
        public InlineRadio getPublicItemRadio();
        public InlineRadio getPrivateItemRadio();

        public Button getSaveButton();
        public Button getListButton();
		public BlockQuote getHelpBlockQuote();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public ObidosMessageRow getMessageRow();
		public Row getExpiresRow();
		public DatePicker getDatePicker();
		public ObidosTimeBox getTimeBox();
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
		public ToggleSwitch getAdd2FASwitch();
		public Row getQrCodeInfoRow();
		public Button getQrCodeResetButton();
		public Button getShow2FACodeButton();
		public Button getGenURIButton();
		public ObidosRowBottom2px getAddAttachmentRow();
		public ToggleSwitch getAddAttachmentSwitch();
		public ObidosRowBottom2px getAdd2FASwitchRow();
		public Row getBottomUhrRow();
		public Uploader getWiseUploader();
		public Span getFileChosenSpan();

       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();

       	public Row getWifiQRCodeRow();
       	public ObidosTextBox getWifiPasswordTextBox();
       	public Select getWifiEncryptionSelect();
       	public Image getWifiQrCodeImage();
       	
       	// Bug #76
       	public ObidosTextBox getContainerNameTextBoxNew();
       	public FormLabel getContainerShareableLabel();
       	// Bug #76
       	public ObidosItemRow getSsidItemRow();
       	public InputGroupAddon getWifiEntryptionInputGroupAddon();
       	public InputGroupAddon getWifiHiddenNetworkInputGroupAddon();
       	public InlineRadio getHiddenYesRadio();
       	public InlineRadio getHiddenNoRadio();
       	public ObidosItemRow getPasswordItemRow();
       	public HTMLPanel getWorkingPanel();
       	public FormLabel getWifiQrcodeCheckMarkLabel();
       	public FormLabel getWifiCrossLabel();
       	public ObidosRowBottom2px getQrCodeImageRow();
    }

    @NameToken(NameTokens.ADD_ITEM_TO_CONTAINER)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<AddItemToContainerPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    AddItemToContainerPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        setupWisePersistFileUploader();
        getView().getHiddenNoRadio().setValue(true);
    }

    protected void onReveal()
    {
        super.onReveal();
        getView().getHiddenNoRadio().setValue(true);
    }

    protected void onHide()
    {
        super.onHide();
        clearTimers();
        clearGlobals();
        clearAllQRCodeStuff();
        resetFileChosen();
        resetWiFiQRCodeForm();
        ClientUtils.resetLanguage(getView().getLanguageRow());
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        getView().getHiddenNoRadio().setValue(true);
        resetWiFiQRCodeForm();
       	getView().getUploadRow().setVisible(false);
		resetExpiration();
        enableCreateButton(false);
        updateDateFormat();
        ClientUtils.updateListButtonTitle(placeManager, getView().getListButton());
        setItemNameLabel(ObidosMessages.LANG.itemName());
        setPublicRadioText(ObidosMessages.LANG.shareableItem());
        setPrivateRadioText(ObidosMessages.LANG.privateItem());

        getView().getButtonToolBarBottom().adjustButtonsWidth();
        
        ClientUtils.focusToWidegt(getView().getItemNameTextBox());
        
        
        showUploadWidget(false);
        show2FAWidget(false);

        resetExpireCheckBox();
        setHelpHtml();
        clearFormCollections();
        clearFormGroup();
        clearSummerNote();
        resetFields();
        enableRadios(true);
        updateContainerNameInForm();
        fetchAndPopulateForm();
        
        setupQRCodeImgaeHandlers();
        enforceLicenseRestrictions();

		ClientUtils.setStartDateToTomorrow(getView().getDatePicker());
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
		RootPanel.get().addDomHandler(new ContextMenuHandler() {

		    @Override
		    public void onContextMenu(ContextMenuEvent event) {
		        event.preventDefault();
		        event.stopPropagation();
		    }
		}, ContextMenuEvent.getType());

		ClientUtils.setSummernoteBottomMargin("2px");
		
		// toggle between Bangla and English if CTRL+L is pressed
        if (ClientUtils.isBanglaEditingEnabled())
        {
        	ClientUtils.setSummernoteLanguageInputToggle(summernote,
				getView().getLanguageListBox(),
				getView().getLanguageRow());
        }
        getView().getWifiEntryptionInputGroupAddon().setWidth(ObidosConstants.INPUT_GROUP_ADDON_WIDTH);
        getView().getWifiHiddenNetworkInputGroupAddon().setWidth(ObidosConstants.INPUT_GROUP_ADDON_WIDTH);

        getView().getHiddenNoRadio().setValue(true);
        getView().getHiddenNoRadio().setFormValue("No");
        // Bug #119 Jul-6-2025 MMM MMM 
        getView().getQrCodeImageRow().setVisible(false);

        Scheduler.get().scheduleDeferred(() -> {
            new Timer() {
                @Override
                public void run() {
                    Element radioElement = getView().getHiddenNoRadio().getElement();
                    gwtLog("MMM Step 1 - Initial state:");
                    gwtLog("MMM Widget value: " + getView().getHiddenNoRadio().getValue());
                    gwtLog("MMM DOM checked: " + radioElement.getPropertyBoolean("checked"));
                    
                    // First step - set DOM property
                    radioElement.setPropertyBoolean("checked", true);
                    
                    new Timer() {
                        @Override
                        public void run() {
                            gwtLog("MMM Step 2 - After DOM property:");
                            gwtLog("MMM Widget value: " + getView().getHiddenNoRadio().getValue());
                            gwtLog("MMM DOM checked: " + radioElement.getPropertyBoolean("checked"));
                            
                            // Second step - set widget value
                            getView().getHiddenNoRadio().setValue(true, true);
                        }
                    }.schedule(100);
                }
            }.schedule(100);
        });
        getView().getPublicItemRadio().setValue(false);
        getView().getPrivateItemRadio().setValue(false);
    }
    
    private void clearQRCodeImage()
    {
    	Image wifiQRCodeImage = getView().getWifiQrCodeImage();
    	ClientUtils.setDefaultThumbnailImage(wifiQRCodeImage);
		setDefaultImagBorderStyle();
    }
    
    private void clearTimers()
    {
    	showWorkingCircle(false);
    	if (wifiSsidTypingTimer != null)
    	{
    		wifiSsidTypingTimer.cancel();
    		wifiSsidTypingTimer = null;
    	}
    	if (wifiPasswordTypingTimer != null)
    	{
    		wifiPasswordTypingTimer.cancel();
    		wifiPasswordTypingTimer = null;
    	}
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
    	Uploader wiseUploader = getView().getWiseUploader();
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
    				boolean supported = license.getSupportsDocumentUpload();
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
   					supported = license.getSupportQRCodeUpload();

   					fileUploadExt.setEnabled(supported);
   					if (! supported)
   					{
   						fileUploadExt.setTitle(t);
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
    private Long getDocumentFieldId()
    {
    	return sDocumentFieldId;
    }
    
    private Long getQRCodeFieldId()
    {
    	return sQRCodeFieldId;
    }
    
    private void saveDocumentFieldId(Long documentId)
    {
    	sDocumentFieldId = documentId;
    }
    private void saveQRCodeFieldId(Long qrCodeFieldId)
    {
    	sQRCodeFieldId = qrCodeFieldId;
    }
    
    private void clearGlobals()
    {
        fieldTextBoxes.clear();
        sItemCreated = false;
        sItemName = null;
        sDocumentFieldId = null;
        sQRCodeFieldId = null;
        sFileUploaded = false;
        sSupportedByLicense = true;
		sFileUploadInProgress = false;
    }
    

    private void resetExpiration()
	{
		getView().getDatePicker().setValue(null);
		getView().getTimeBox().setText("12:00 AM");
	}

    private void updateDateFormat()
    {
		ClientUtils.configureDateFormat(getView().getDatePicker(), currentUser);
    }

    private void resetExpireCheckBox()
    {
    	getView().getDatePicker().setValue(null);
    }

    private void enableRadios(boolean enable)
    {
    	InlineRadio publicRadio = getView().getPublicItemRadio();
    	InlineRadio privateRadio = getView().getPrivateItemRadio();
    	ClientUtils.enableRadios(publicRadio, privateRadio, enable);
    	getView().getExpiresRow().setVisible(false);
    }

    private void clearSummerNote()
    {
        summernote.setCode("");
    }

    private void resetFields()
    {
        getView().getItemNameTextBox().setValue("");
        enableRadios(false);
        showMessage("");
    }

    private void clearFormCollections()
    {
        fieldTextBoxes.clear();
        fieldIds.clear();
        noteFieldIds.clear();
    }

    private void setItemNameLabel(String text)
    {
        getView().getItemNameLabel().setText(text);
    }

    private void setPublicRadioText(String text)
    {
        getView().getPublicItemRadio().setText(text);
    }
    private void setPrivateRadioText(String text)
    {
        getView().getPrivateItemRadio().setText(text);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private Long getContainerIdFromUrl()
    {
        Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
        if (containerId == null)
        {
            gwtLog("Could not get " + ObidosConstants.CONTAINER_ID + " in URL");
            return null;
        }
        return containerId;
    }
    private Long getContainerOwnerIdFromUrl()
    {
        Long containerOwnerId = ClientUtils.getOwnerIdFromUrl(placeManager);
        return containerOwnerId;
    }

    private void updateRadioButtonsNew(final boolean x)
    {
    	if (x)
    	{
			getView().getPrivateItemRadio().setEnabled(true);
			getView().getPublicItemRadio().setEnabled(true);
			getView().getPrivateItemRadio().setValue(false);
    	}
    	else
    	{
            getView().getPrivateItemRadio().setEnabled(false);
//            getView().getPublicItemRadio().setEnabled(false);
            getView().getPrivateItemRadio().setValue(true);
    	}
    	
    }
    
    private void updateRadioButtons()
    {
    	// #75 we don't use InputGrupAddon at the right side
    	// spgdev@spenego.com - Nov 27, 2024
    	updateRadioButtonsNew(isContainerPrivate);
    	/*
    	if (ClientUtils.isAddonShareable(getView().getTypeAddon()))
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
    	*/
    }

    private void updateContainerNameInForm()
    {
        Long containerId = getContainerIdFromUrl();
        gwtLog("XXXX Container Id: " + containerId);
        if (containerId == null)
        {
            showErrorMessage("Could not get container Id");
            return;
        }

        Long containerOwnerId = getContainerOwnerIdFromUrl();
        /*
        if (containerOwnerId == null)
        {
            return;
        }
        */
        Long userId = currentUser.getUserDTO().getId();
        
        gwtLog("MMMM MMMM MMMM user Id: " + userId +  " container owner: " + containerOwnerId);
        final boolean isOwner;
        gwtLog("MMM C: " + containerOwnerId);
        gwtLog("MMM U: " + userId);
        if (userId.equals(containerOwnerId))
        {
            isOwner = true;
        }
        
        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponSuccess(ContainerDTO result)
            {
                getView().getContainerNameTextBoxNew().setValue(result.getName());
                getView().getContainerNameTextBoxNew().setValue(result.getName());
                boolean privateContainer = ClientUtils.fromBoolean(result.getIsPrivate());
                boolean sharedContainer = ClientUtils.fromBoolean(result.getShared());

                Long userId = currentUser.getUserDTO().getId();
                Long containerOwnerId = getContainerOwnerIdFromUrl();

                if (sharedContainer)
                {
                	getView().getPrivateItemRadio().setVisible(false);
                	getView().getPublicItemRadio().setValue(true);
                	getView().getExpiresRow().setVisible(true);
                }
                // The above logic is not correct, owner of the container should be 
                // able to create private item
                // Nov-03-2015
                if (userId.equals(containerOwnerId))
                {
                	getView().getPrivateItemRadio().setVisible(true);
                	getView().getPublicItemRadio().setValue(true);
                }


                updateRadioButtonsNew(privateContainer);

                FormLabel fl = getView().getContainerShareableLabel();
                if (privateContainer)
                {
                	isContainerPrivate = true;
                	// private
                	fl.setHTML(glang.privateX());
                    showContainerHelp(ObidosMessages.LANG.addItemToPrivateContainerHelp());
                    // disable both
                    getView().getPrivateItemRadio().setEnabled(false);
                    getView().getPublicItemRadio().setEnabled(false);
                    // only enable private one
                    getView().getPrivateItemRadio().setValue(true);
                }
                else
                {
                	isContainerPrivate = false;
                	// shareable
                	fl.setHTML(glang.shareable());
                    showContainerHelp(ObidosMessages.LANG.addItemToPublicContainerHelp());
                    // enable both radio button
                    getView().getPrivateItemRadio().setEnabled(true);
                    getView().getPublicItemRadio().setEnabled(true);
                    // set value to false for both, user can select any
                    getView().getPublicItemRadio().setValue(false);
                    getView().getPrivateItemRadio().setValue(false);
                }
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not fetch Container");
            }
        };
        
        ContainerService.Utility.getInstance().get(ClientUtils.getAuthCreds(), containerId, callback);
    }

    private void fetchAndPopulateForm()
    {
        getView().getHiddenNoRadio().setValue(true);
    	// Bug# 34 support.spenego.com 
    	// The visibility was set when going though the items. Make them
    	// invisible first and then enable if dto has an id
    	// May-09-2024
	    showUploadWidget(false);
	    show2FAWidget(false);
        Long templateId = ClientUtils.getTemplateIdFromUrl(placeManager);
        if (templateId == null)
		{
			return;
		}

        GwtAsyncWrapper<UserDefinedTypeDTO> callback = new GwtAsyncWrapper<UserDefinedTypeDTO>(this)
        {

            @Override
            public void uponSuccess(UserDefinedTypeDTO dto)
            {
                gwtLog("Template Name: " + dto.getName());
                populateForm(dto);
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not get Template + " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().getUserDefinedType(authCreds, templateId, callback);
    }
    
    private void resetWiFiQRCodeForm()
    {
       	getView().getWifiQRCodeRow().setVisible(false);
       	getView().getSsidItemRow().getTextBox().clear();
       	getView().getPasswordItemRow().getTextBox().clear();
       	getView().getWifiEncryptionSelect().setValue(glang.wpa());
       	getView().getHiddenNoRadio().setValue(false);
        clearQRCodeImage();
		getView().getWifiQrcodeCheckMarkLabel().setVisible(false);
		getView().getWifiCrossLabel().setVisible(true);
		setDefaultImagBorderStyle();
    }

    private void populateForm(UserDefinedTypeDTO dto)
    {
    	sDocumentFieldId = null;
    	sQRCodeFieldId = null;

        getView().getTemplateNameTextBox().setValue(dto.getName());
        FormGroup formGroup = getView().getFormGroup();
        List<UserDefinedFieldDTO> fields = dto.getFields();

        // sort by display order
        Collections.sort(fields, (f1,f2) -> f1.getPosition().compareTo(f2.getPosition()));

        fieldTextBoxes.clear();
        // XXXX
        boolean showWifiQRCodeForm = false;
        resetWiFiQRCodeForm();
        for (UserDefinedFieldDTO fieldDTO:fields)
        {
            String fieldName = fieldDTO.getName();
            Integer position = fieldDTO.getPosition();
            gwtLog("MMM WIFI QR Field: " + fieldName);
            gwtLog("MMM WIFI QR posit: " + position);
            if (ObidosConstants.WIFI_QRCODE.equals(fieldName))
            {
            	gwtLog("MMM WIFI SHOW WIFI QRCode form <<<<<<<<<<<<<>>>>>>>>>>>>>>");
            	showWifiQRCodeForm = true;
            	break;
            }
        }
        /* TODO HIDE FOR NOW */
        if (showWifiQRCodeForm)
        {
        	getView().getWifiQRCodeRow().setVisible(true);
        	return;
        }
        // XXXX

        for (UserDefinedFieldDTO fieldDTO:fields)
        {
            String fieldName = fieldDTO.getName();
            Integer position = fieldDTO.getPosition();

            gwtLog("Field name: " + fieldName + " Position: " + position);

            Row row = new Row();

           	if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_ENCRYPTED)
           	{
                // #76 starts --
           		/*
                ObidosItemRow itemRow = new ObidosItemRow(showWifiQRCodeForm, "", "", true);
                itemRow.setLabelText(fieldName);
                formGroup.add(itemRow);
                */
                // #76 ends --

                /*
				FormLabel label = new FormLabel();
				label.setText(fieldName);
				label.addStyleName("col-sm-4");
				row.add(label);
				formGroup.add(row);
				*/
           	}

            // it will work for only one Note at this time
            if (fieldDTO.getId() == UserDefinedFieldDTO.TYPE_NOTES_FIELD_ID)
            {

            	// Bug #53
           		showUploadWidget(false);
           		show2FAWidget(false);

            	// Hide the Notes label, it looks awkward
            	row.setVisible(false);

                setItemNameLabel(ObidosMessages.LANG.noteName());
                getView().getItemNameTextBox().setPlaceholder(ObidosMessages.LANG.noteName());
                setPublicRadioText(ObidosMessages.LANG.shareableNote());
                setPrivateRadioText(ObidosMessages.LANG.privateNote());
                

                // for Lock icon and Note Label
                row = new Row();
                FlowPanel fp = new FlowPanel();
                fp.addStyleName("col-sm-4");
                
				// create a span for Lock icon
				Span span = new Span();
				ClientUtils.setNoteSpanHtml(span, ObidosMessages.LANG.note());
				fp.add(span);
				row.add(fp);
				formGroup.add(row);
                
				// Note itself
				row = new Row();
				summernote.clear();
                // set default height of Summernote
                fp = new FlowPanel();
				fp.addStyleName("col-sm-offset-4 col-sm-7");
                fp.add(summernote);
                row.add(fp);
                formGroup.add(row);

                noteFieldIds.put(summernote, fieldDTO.getId());
                if (ClientUtils.isBanglaEditingEnabled())
                {
                	languageListBoxCallback();
                }
            }
            else
            {
            	if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_ENCRYPTED)
            	{
					// #76 starts --
					ObidosItemRow itemRow = new ObidosItemRow(showWifiQRCodeForm, "", "", true);
					TextBox textBox = itemRow.getTextBox();
					fieldTextBoxes.add(textBox);
					fieldIds.put(textBox, fieldDTO.getId());
					itemRow.setLabelText(fieldName);
					formGroup.add(itemRow);
					// #76 ends --

					/*
					FlowPanel fp = new FlowPanel();
					// set style of FlowPanel to 9 columns
					fp.addStyleName("col-sm-4");

					InputGroup ig = new InputGroup();
					TextBox textBox = new TextBox();
					textBox.setPlaceholder("Add Value");
					textBox.setEnabled(true);
					InputGroupAddon iga = new InputGroupAddon();
					iga.setIcon(IconType.LOCK);
					iga.setTitle(ObidosMessages.LANG.encrypted());
					iga.setIconColor("green");
					ig.add(iga);
					ig.add(textBox);
					fp.add(ig);
					row.add(fp);
					formGroup.add(row);
					fieldTextBoxes.add(textBox);
					gwtLog("+ Add field id " + fieldDTO.getId() + " for field: " + fieldDTO.getName());
					fieldIds.put(textBox, fieldDTO.getId());
					*/
            	}
            	// some crazy bug, need to look at it more
            	// May-01-2024
            	// Apr-16, Bug #123, the bug #34 re-appeared why? The next two
            	// lines were uncommented.
           		//showUploadWidget(false);
           		//show2FAWidget(false);
                
                if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_DOCUMENT)
                {
                	gwtLog("MMM type document");
                	gwtLog("MMM field dto id: " + fieldDTO.getId());
                	if (fieldDTO.getId() != null)
                	{
                		gwtLog("MMM show upload widget");
                		showUploadWidget(true);
                		saveDocumentFieldId(fieldDTO.getId());
                	}
                }
                if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_QRCODE)
                {
                	if (fieldDTO.getId() != null)
                	{
                		gwtLog("MMM field dto id: " + fieldDTO.getId());
                		gwtLog("MMM Setting 2FA widget show to true");
                		show2FAWidget(true);
                		saveQRCodeFieldId(fieldDTO.getId());
                	}
                }
            }
        }
    }
    
    private void showUploadWidget(boolean visible)
    {
    	getView().getAddAttachmentRow().setVisible(visible);
    	showAttachmentFileUploadWidget();
    }
    
    private void show2FAWidget(boolean visible)
    {
    	gwtLog("MMM show 2FA widget: " + visible);
    	getView().getAdd2FASwitchRow().setVisible(visible);
    	getView().getBottomUhrRow().setVisible(visible);
    	
    }

    private void clearFormGroup()
    {
    	getView().getItemNameTextBox().clear();
        FormGroup formGroup = getView().getFormGroup();
        formGroup.clear();
        resetExpiration();
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    @Override
    public void listContainers()
    {
    	/*
    	String place = ClientUtils.getPlaceFromUrl(placeManager);
        String nameToken = NameTokens.LIST_CONTAINERS_ADD_ITEM;
        if (place != null)
        {
        	nameToken = place;
        }
        ClientUtils.showPage(placeManager, nameToken);
        */
    	ClientUtils.showListContainersPage(placeManager);
    }

    @Override
    public void resetForm()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void showHideHelp()
    {
    	getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
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
        showMessage("");
        String itemName = getView().getItemNameTextBox().getValue();
        String containerNameNew = getView().getContainerNameTextBoxNew().getValue();
        if (itemName == null || itemName.length() == 0)
        {
            if (isNote())
            {
                showErrorMessage(ObidosMessages.LANG.specifyNoteName());
            }
            else
            {
                showErrorMessage(ObidosMessages.LANG.specifyItemName());
            }
            return;
        }

        Long containerId = getContainerIdFromUrl();
        if (containerId == null)
        {
            return;
        }

        Long templateId = null;
        try
        {
            templateId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.TEMPLATE_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + ObidosConstants.TEMPLATE_ID + " from URL");
            return;
        }

        Boolean isPublicItem = getView().getPublicItemRadio().getValue();
        Boolean isPrivateItem = getView().getPrivateItemRadio().getValue();

        // fields can be empty
        for (TextBox textBox:fieldTextBoxes)
        {
            String value = textBox.getValue();
            if (value == null || value.length() == 0)
            {
//                showErrorMessage("Please fill up all the fields");
//                return;
            }
        }
        if (isPublicItem == false && isPrivateItem == false)
        {
            if (isNote())
            {
                showErrorMessage(ObidosMessages.LANG.noteTypeWarning());
            }
            else
            {
                showErrorMessage(ObidosMessages.LANG.itemTypeWarning());
            }
            return;
        }
        UserDefinedTypeValueDTO userDefinedTypeValueDTO = new UserDefinedTypeValueDTO();
        userDefinedTypeValueDTO.setUserDefinedTypeId(templateId);

        final ArrayList<UserDefinedTypeValueDTO> list = new ArrayList<UserDefinedTypeValueDTO>();
        list.add(userDefinedTypeValueDTO);

        gwtLog("Number of field TextBoxes: " + fieldTextBoxes.size());
        gwtLog("Number of field hash: " + fieldIds.size());

        if (isNote())
        {
            String value = summernote.getCode();
            gwtLog("Note Value: " + value);
            UserDefinedFieldValueDTO userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
            userDefinedFieldValueDTO.setBlobValue(value.getBytes());
            Long userDefinedField = fieldTypeNote;
            userDefinedFieldValueDTO.setUserDefinedFieldId(userDefinedField);
            userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
        }
        else
        {
            for (TextBox textBox:fieldTextBoxes)
            {
                String value = textBox.getValue();

                UserDefinedFieldValueDTO userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
                userDefinedFieldValueDTO.setBlobValue(value.getBytes());
                Long userDefinedField = fieldIds.get(textBox);
                gwtLog(">>>>> Field id: " + userDefinedField);
                gwtLog(">>>> Value: " + value);
                userDefinedFieldValueDTO.setUserDefinedFieldId(userDefinedField);

                userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
            }
            Long documentFieldId = getDocumentFieldId(); // saved during form population
            gwtLog(">>> Issue #735 document field id: " + documentFieldId);
			UserDefinedFieldValueDTO userDefinedFieldValueDTO = null;

            // user selected a file to upload
			// Issue #817
            if (getView().getUploadRow().isVisible() && fileSelectedForUpload() && documentFieldId != null)
            {
				// add a document type if user has selected a file to upload
				userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
				userDefinedFieldValueDTO.setUserDefinedFieldId(documentFieldId);
                userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
                if (sFilenameToUpload != null)
                {
                	userDefinedFieldValueDTO.setBlobValue(sFilenameToUpload.getBytes());
                }
            }

            Long qrCodeFieldId = getQRCodeFieldId();
            if (shouldAddQRCode() && qrCodeFieldId != null)
            {
            	gwtLog("XX Add QR Code totp url field id: " + qrCodeFieldId);
            	userDefinedFieldValueDTO = new UserDefinedFieldValueDTO();
            	userDefinedFieldValueDTO.setUserDefinedFieldId(qrCodeFieldId);
            	userDefinedFieldValueDTO.setBlobValue(getView().getTotpUriTextBox().getValue().getBytes());
                userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
            }
            
        }

        GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
        {

            @Override
            public void uponSuccess(ItemDTO itemDTO)
            {
				String with = getWith2FAMessage();
            	gwtLog("itemDTO getvalues: " + itemDTO.getValues());
            	
            	Long documentId = ClientUtils.getDocumentId(itemDTO);
            	gwtLog("Document ID: " + documentId);

            	
            	Date date = new Date();
                sItemCreatedMessage = ObidosMessages.LANG.item() + " " 
                		+ "<span style=\"color: purple;\">" + "'" +itemName + "'"+ "</span>" + with 
                		+ " " + ObidosMessages.LANG.createdAndAdded() + " " 
                		+ "<span style=\"color: purple;\">" + "'" + containerNameNew + "'" + "</span>";

				boolean qrCodeImageDecoded = false;
				String totpUri = getView().getTotpUriTextBox().getValue();
				if (totpUri != null && totpUri.length() > 0)
				{
					qrCodeImageDecoded = true;
				}

                
                sItemName = getView().getItemNameTextBox().getValue();
                sItemCreated = true;
                enableCreateButton(true);
                clearFormGroup();
                fetchAndPopulateForm();
                updateRadioButtons(); // XXX
                clearAllQRCodeStuff();
                getView().getExpiresRow().setVisible(false);
                // Bug #120 Apr-06-2025
                enableRadios(true);
                // Bug #12 reopened. If the container is private
                // then make sure the they radio buttons stayed grayed out
//                getView().getPublicItemRadio().setValue(false);
                getView().getPrivateItemRadio().setValue(false);
                
                // check if attachment row is visible
                ObidosRowBottom2px arow = getView().getAddAttachmentRow();
                boolean arowVisible = arow.isVisible();
                gwtLog("NNN attachment row visible? " + arow.isVisible());
                // hide file upload row if attachment row is not visible
                gwtLog("NNNN show upload row?" + arowVisible);
                getView().getUploadRow().setVisible(arowVisible);

                
                if (isContainerPrivate)
                {
//                	getView().getPublicItemRadio().setValue(false);
                	getView().getPrivateItemRadio().setValue(true);
                	enableRadios(false);
                }
                
//                if (getView().getUploadRow().isVisible() && fileSelectedForUpload())
                if (fileSelectedForUpload() && documentId != null)
                {
					gwtLog("Upload file...");
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
            	showErrorMessage(ObidosMessages.LANG.couldNotAddItem() + ". " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        Boolean shareable = getView().getPublicItemRadio().getValue();
        gwtLog("Item is shareable: " + shareable);
        ItemExpiration itemExpiration = null;
        Date expireDate = ClientUtils.getDateFromDatePickerAndTimeBox(
        		currentUser,
        		getView().getDatePicker(), 
        		getView().getTimeBox(),
        		getView().getMessageRow());
        if (expireDate != null)
        {
        	itemExpiration = new ItemExpiration(expireDate);
        }
        gwtLog("Expire date: " + expireDate);
        ItemService.Utility.getInstance().create(authCreds, itemName, itemExpiration, containerId, null, shareable, list, callback);
    }

    private void setHelpHtml()
    {
    	String html = "";
    	getView().getHelpParagraph().setHTML(html);
    }

    private void showContainerHelp(String html)
    {
    	getView().getHelpParagraph().setHTML(html);
    }

    private Long getTemplateIdFromUrl()
    {
    	Long templateId = null;
    	String key = ObidosConstants.TEMPLATE_ID;
    	try
    	{
    		templateId = ClientUtils.getIdFromUrl(placeManager, key);
    	} catch (NumberFormatException | ParamNotFoundException e)
    	{
    		showErrorMessage("Could not get " + key + " from URL");
    		return null;
    	}
    	return templateId;
    }

    private boolean isNote()
    {
    	Long templateId = getTemplateIdFromUrl();
    	if (templateId == null)
    	{
    		return false;
    	}
    	if (templateId == UserDefinedTypeDTO.NOTES_ID)
    	{
    		return true;
    	}
    	return false;

    }

    @Override
    public void publicRadioCallback()
    {
    	showMessage("");
    	getView().getExpiresRow().setVisible(true);
    }

    @Override
    public void privateRadioCallback()
    {
    	showMessage("");
    	getView().getExpiresRow().setVisible(false);
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

    @Override
    public void showNameChange(KeyUpEvent e)
    {
    	showMessage(null);
    	String itemName = getView().getItemNameTextBox().getValue();
    	if (itemName.length() > 0)
    	{
    		if (sItemName != null) // saved already
    		{
    			if (sItemName.equals(itemName))
    			{
    				gwtLog("Item name didn't change");
    				enableCreateButton(false);
    				return;
    			}
    		}
    		enableCreateButton(true);
    	}
    	else
    	{
    		enableCreateButton(false);
    	}
    }

    @Override
    public void namePasted()
    {
    	showMessage(null);
    	String itemName = getView().getItemNameTextBox().getValue();
    	enableCreateButton(false);
    	if (itemName != null && itemName.length() > 0)
    	{
    		enableCreateButton(true);
    	}
    }

    private void enableCreateButton(boolean enabled)
    {
    	getView().getSaveButton().setEnabled(enabled);
    }

    private boolean formChanged()
    {
    	String itemName = getView().getItemNameTextBox().getValue();
    	if (itemName != null && itemName.length() > 0)
    	{
    		return false;
    	}
    	if (isNote())
    	{
    		if (summernote.getCode().length() > 0)
    		{
    			return false;
    		}
    	}
    	// sonarqube
    	return true;
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
				
				Uploader uploader = getView().getWiseUploader();
				uploader.setUploadURL(url);

				if (sFilenameToUpload == null)
				{
					showErrorMessage("No file chosen for upload");
					return;
				}

				showMessage("Uploading, please wait........");
				gwtLog("XX start extend session timer..");
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

				//formPanel.submit();
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


	// https://stackoverflow.com/questions/16883231/check-file-size-before-upload-using-gwt
	// this stupid thing does not work, don't use it
	private native int getFileSize(final Element data) /*-{
    	return data.files[0].size;
	}-*/;
	
	private void clearUpload()
	{
		sItemCreatedMessage = null;
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
	
	// callback for the switch
	@Override
	public void showQRCodeInfoWidgets()
	{
		Boolean v = getView().getAdd2FASwitch().getValue();
		getView().getQrCodeInfoRow().setVisible(v);
		if (v && ! sSupportedByLicense)
		{
			ClientUtils.showLicenseEnforcementDialogQRCode(
					getView().getAdd2FASwitch(),
					getView().getQrCodeInfoRow());
		}

	}

	// callback for checkbox
	@Override
	public void showQrCodeTextBoxes()
	{
		boolean v = getView().getQrCodeManualCheckBox().getValue();
		sQRCodeUtils.showQrCodeTextBoxes();
		if (v && ! sSupportedByLicense)
		{
//			ClientUtils.showNotSupportedByLicenseDialog();
		}

	}


	@Override
	public void resetQRCodeInfo()
	{
		sQRCodeUtils.resetQRCodeInfo();
	}

	@Override
	public void showAttachmentFileUploadWidget()
	{
    	boolean vv = getView().getAddAttachmentRow().isVisible();
    	
		Boolean v = getView().getAddAttachmentSwitch().getValue();
		getView().getUploadRow().setVisible(v);
		if (v)
		{
			if (! sSupportedByLicense)
			{
				ClientUtils.showLicenseEnforcementDialog(
					getView().getAddAttachmentSwitch(),
					getView().getUploadRow());
			}
		}
		
	}

	@Override
	public void show2FACodeDialog()
	{
		sQRCodeUtils.show2FACodeDialog();
	}

	@Override
	public void generateTotpOtpAuthUri()
	{
		sQRCodeUtils.generateTotpOtpAuthUri();
	}
	
	// called from onReset to initialize everything
	public void setupQRCodeImgaeHandlers()
	{
		if (sQRCodeUtils != null)
		{
			return;
		}
		sQRCodeUtils = new TwoFAQRCodeUtils(placeManager, 
        getView().getQrCodefileUploadHTML5(),
        new FileReader(),
        null, // File object - must be filled up from upload process handler
        getView().getMessageRow(),
        getView().getQrCodeManualCheckBox(),
        getView().getTotpUriTextBox(),
        getView().getIssuerTextBox(), 
        getView().getAccountTextBox(),
        getView().getSecretTextBox(),
        getView().getShow2FACodeButton(),
        getView().getQrCodeResetButton(),
        getView().getGenURIButton(),
        getView().getCheckMarkLabel(),
        getView().getCrossLabel(),
        getView().getQrCodeImage(),
        getView().getQrCodeResizerImage(),
        getView().getQrCodeCanvas(),
        getView().getUploadRow(),
        getView().getQrCodeManualTextBoxesRow(),
        getView().getQrCodeInfoRow(),
        getView().getAdd2FASwitch(),
        getView().getQrCodeImageRow(),
        ClientUtils.createTwoFAModal());
		sQRCodeUtils.setupQRCodeImageUploadHandler();
	}
	
	
	// method to load QR Code image when user selects a QR Code image file
	// it uses HTML5 upload API
	@Override
	public void processQRCodeImageFile()
	{
		sQRCodeUtils.processQRCodeImageFile();
	}
	
    void clearAllQRCodeStuff()
    {
    	sQRCodeUtils.clearAllQRCodeStuff();
    	
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


	private void setupWisePersistFileUploader()
	{
		gwtLog(">>>>>>>>>>>>>>>>>> in setupWisePersistFileUploader");
		Date d = new Date();
		gwtLog("Date: " + d);
		// move the following to view
		// set our upload servlet URL
		String url = GWT.getHostPageBaseURL() + glang.fileUploadServlet();
		Uploader u = getView().getWiseUploader();

		// button style
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
					otb,
					NameTokens.ADD_ITEM_TO_CONTAINER,
					getView().getMessageRow(),
					getView().getFileChosenSpan());
		}

	}
	
	private void generateQRCodeImage()
	{
		String ssid = getView().getSsidItemRow().getTextBox().getValue();
		String password = getView().getPasswordItemRow().getTextBox().getValue();
		if (ssid.isEmpty() || password.isEmpty())
		{
			return;
		}
		GwtAsyncWrapper<QRCodeDTO> callback = new GwtAsyncWrapper<QRCodeDTO>(this)
		{

			@Override
			public void uponSuccess(QRCodeDTO dto)
			{
				gwtLog("MMMM qr code image: " + dto.getBase64QRCodeImage());
				Image image = getView().getWifiQrCodeImage();
				image.setUrl("data:image/png;base64," + dto.getBase64QRCodeImage());
				image.setTitle(dto.getTitle());
				getView().getWifiQrcodeCheckMarkLabel().setVisible(true);
				getView().getWifiCrossLabel().setVisible(false);
				setValidQRCodeImageBorderStyle();
				// Bug #84 Jul-6-2025
				getView().getQrCodeImageRow().setVisible(true);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not generate QR Code image" + caught.getMessage());
				getView().getWifiQrcodeCheckMarkLabel().setVisible(false);
				getView().getWifiCrossLabel().setVisible(true);
				setDefaultImagBorderStyle();
			}

		};
		/*
		WIFI:S:NETWORK-NAME;T:WEP;P:NETWORK-PASSWORD;H:false;;
		and i use the python tool `qr` to generate one: https://pypi.org/project/qrcode/
		The key/value pairs after the "WIFI:" prefix are as follows:

		  S - the SSID
		  P - the password
		  T - the encryption type (WEP, WPA, or blank)
		  H - whether or not it's a hidden network
		The only required pair is "S"; all other pairs are optional and may be omitted if desired.
		*/
		String eType = getView().getWifiEncryptionSelect().getValue();
		if (glang.wpa().equals(eType))
		{
			eType = "WPA";
		}
		else if (glang.wep().equals(eType))
		{
			eType = "WEP";
		}
		else
		{
			eType = "nopass";
		}
				
		boolean hidden = getView().getHiddenYesRadio().getValue();
		String hiddenStr = hidden ? "true" : "false";
		String url = "WIFI:S:" + ssid +";T:" + eType + ";P:" + password + ";H:" + hiddenStr + ";;";
		gwtLog("MMM URL: " + url);
		
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		QRCodeService.Utility.getInstance().createQRCodeImage(authCreds, url, callback);
	}
	
	private void showWorkingCircle(boolean show)
	{
		getView().getWorkingPanel().setVisible(show);
	}

	@Override
	public void wifiSsidKeyUpHandler()
	{
		showWorkingCircle(true);
		if (wifiSsidTypingTimer != null)
		{
			wifiSsidTypingTimer.cancel();
		}
		wifiSsidTypingTimer = new Timer() {

			@Override
			public void run()
			{
				gwtLog("MMM in SSID key up handler, update qrcode");
				generateQRCodeImage();
				showWorkingCircle(false);
			}
		};
		wifiSsidTypingTimer.schedule(1000); // 1 sec delay
	}

	/**
	 * delay a second between key strokes 
	 */
	@Override
	public void wifiPasswordKeyUpHandler()
	{
		showWorkingCircle(true);
		gwtLog("MMM MMM MMM MMM");
		if (wifiPasswordTypingTimer != null)
		{
			wifiPasswordTypingTimer.cancel();
		}
		wifiPasswordTypingTimer = new Timer() {

			@Override
			public void run()
			{
				gwtLog("MMM in wifi password key up handler, update qrcode");
				generateQRCodeImage();
				showWorkingCircle(false);
			}
			
		};
		wifiPasswordTypingTimer.schedule(1000);
	}

	@Override
	public void wifiHiddenToggleSwitchHandler()
	{
		gwtLog("MMM wifi hidden ts");
		generateQRCodeImage();
	}

	@Override
	public void wifiEncryptionSelectHandler()
	{
		gwtLog("MMM wifi encryption handler");
		showWorkingCircle(true);
		generateQRCodeImage();
		showWorkingCircle(false);
	}

	private void setDefaultImagBorderStyle() 
	{
		getView().getWifiQrCodeImage().getElement().getStyle().setProperty("border", sImageStyle);
	}
	
	private void setValidQRCodeImageBorderStyle()
	{
		getView().getWifiQrCodeImage().getElement().getStyle().setProperty("border", sOkImageStyle);
	}

	private void setinValidQRCodeImageBorderStyle()
	{
		getView().getWifiQrCodeImage().getElement().getStyle().setProperty("border", sInvalidImageStype);
	}

}

