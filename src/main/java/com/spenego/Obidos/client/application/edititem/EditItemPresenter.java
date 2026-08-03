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

package com.spenego.Obidos.client.application.edititem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
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
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosItemRow;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.application.widgets.summernote.event.SummernoteKeyUpEvent;
import com.spenego.Obidos.client.application.widgets.summernote.event.SummernoteKeyUpHandler;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.FileUploadModalData;
import com.spenego.Obidos.client.util.UserInfoModalData;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;

/**
 * Class to sHoldEditItem form values, so that we will update only the changes fields.
 * @author spgdev@spenego.com - Mar 26, 2019
 */
class HoldEditItem
{
	private Summernote summernote                                             = ClientUtils.createSummernote(ObidosConstants.NOTE_HEIGHT);
	private SharedItemDTO itemDTO                                             = null;
	private Long userDefinedTypeValueDTOId                                    = null;
	private List<TextBox> textBoxList                                         = new ArrayList<TextBox>();
	private Map<TextBox, UserDefinedFieldValueDTO> textBoxFieldValueDTO       = new HashMap<>();
	private Map<Summernote, UserDefinedFieldValueDTO> summernoteFieldValueDTO = new HashMap<>();
	private Boolean itemIsNote                                                = null;
	private Integer shareExpiresInDays                                        = null;
	private String hideTime                                                   = null;
	private boolean shareableItem                                             = false;
	private boolean isNote                                                    = false;

	
	public HoldEditItem()
	{
	}
	public Summernote getSummernote()
	{
		return summernote;
	}
	public void setSummernote(Summernote summernote)
	{
		this.summernote = summernote;
	}

	public SharedItemDTO getItemDTO()
	{
		return itemDTO;
	}
	public void setItemDTO(SharedItemDTO itemDTO)
	{
		this.itemDTO = itemDTO;
	}
	public Long getUserDefinedTypeValueDTOId()
	{
		return userDefinedTypeValueDTOId;
	}
	public void setUserDefinedTypeValueDTOId(Long userDefinedTypeValueDTOId)
	{
		this.userDefinedTypeValueDTOId = userDefinedTypeValueDTOId;
	}
	public List<TextBox> getTextBoxList()
	{
		return textBoxList;
	}
	public void setTextBoxList(List<TextBox> textBoxList)
	{
		this.textBoxList = textBoxList;
	}
	public Map<TextBox, UserDefinedFieldValueDTO> getTextBoxFieldValueDTO()
	{
		return textBoxFieldValueDTO;
	}
	public void setTextBoxFieldValueDTO(Map<TextBox, UserDefinedFieldValueDTO> textBoxFieldValueDTO)
	{
		this.textBoxFieldValueDTO = textBoxFieldValueDTO;
	}
	public Map<Summernote, UserDefinedFieldValueDTO> getSummernoteFieldValueDTO()
	{
		return summernoteFieldValueDTO;
	}
	public void setSummernoteFieldValueDTO(Map<Summernote, UserDefinedFieldValueDTO> summernoteFieldValueDTO)
	{
		this.summernoteFieldValueDTO = summernoteFieldValueDTO;
	}
	public Boolean getItemIsNote()
	{
		return itemIsNote;
	}
	public void setItemIsNote(Boolean itemIsNote)
	{
		this.itemIsNote = itemIsNote;
	}

	public boolean isNote()
	{
		return isNote;
	}
	public void setNote(boolean isNote)
	{
		this.isNote = isNote;
	}
	public Integer getShareExpiresInDays()
	{
		return shareExpiresInDays;
	}
	public void setShareExpiresInDays(Integer shareExpiresInDays)
	{
		this.shareExpiresInDays = shareExpiresInDays;
	}
	public boolean isShareableItem()
	{
		return shareableItem;
	}
	public void setShareableItem(boolean shareableItem)
	{
		this.shareableItem = shareableItem;
	}
	public String getHideTime()
	{
		return hideTime;
	}
	public void setHideTime(String hideTime)
	{
		this.hideTime = hideTime;
	}
}

public class EditItemPresenter extends Presenter<EditItemPresenter.MyView, EditItemPresenter.MyProxy>
		implements EditItemUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private static UserInfoModalData sModalData = null;
	
	// initialized and gets created at onReset()
	// reset() method also re-creates it
	private HoldEditItem sHoldEditItem 	= null;
	private Timer countDownTimer 		= null;
	private Timer hideItemTimer  		= null;
    private String sItemCreatedMessage 	= null;
	private Long sDocumentId            = null;
	
	private FileUploadModalData sWiseUploadModalData = null;
	private String sFilenameToUpload = null;


	interface MyView extends View, HasUiHandlers<EditItemUiHandlers>
	{
		public BlockQuote getHelpBlockQuote();
  		public Heading getPanelHeading();
		public ObidosMessageRow getMessageRow();
		public Button getUpdateButton();
		public Button getResetButton();
		public Button getListButton();
		public Button getHelpButton();
//		public TextBox getItemNameTextBox();
//		public FormLabel getItemNameLabel();
//		public InputGroupAddon getItemTypeAddon();
		public InlineRadio getPublicItemRadio();
		public InlineRadio getPrivateItemRadio();
		public FormGroup getFormGroup();
		public FormLabel getSharedByNameLabel();
		public Button getOwnerDropDownAnchor();
		public DropDown getOwnerDropDown();
		public Button getGrantPermissionsButton();
		public FormLabel getSharedOnLabel();
		public InputGroup getSharedOnInputGroup();
		public TextBox getSharedOnTextBox();
		public Row getItemRow();
		public Button getShowHideButton();
		public Row getSharedByRow();
		public Row getSharedOnRow();
		public Row getRadioRow();
		public Row getHideItemCheckBoxRow();
		public CheckBox getHideItemCheckBox();
		public ProgressBar getProgressbar();
		public Row getButtonsRow();
		public ObidosButtonToolBar getTopToolBar();
		public ObidosButtonToolBar getBottomToolBar();
		public ObidosPanelHeader getPanelHeader();
		public DatePicker getDatePicker();
		public Row getExpiresRow();
		public Button getSharedWithUsersButton();
		public AnchorListItem getOwnerDetails();
		public Anchor getEmailAnchor();
		public Anchor getPhoneAnchor();
		public ObidosTimeBox getTimeBox();
		public InlineCheckBox getClearExpirationCheckbox();
		public ObidosRowBottom2px getDocFilenameRow();
		public ObidosTextBox getDocFilenameTextBox();
		public ObidosRowBottom2px getUploadRow();
		public FormLabel getRelpaceDocLabel();

		public Uploader getWiseUploader();
		public Span getFileChosenSpan();

       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
       	
       	// Bug #75
       	public ObidosRowBottom2px getContainerNameRowNew();
       	public ObidosTextBox getContainerNameTextBoxNew();
       	public FormLabel getContainerShareableLabel();

		public FormLabel getItemNameLabelNew();
		public ObidosTextBox getItemNameTextBoxNew();
		public FormLabel getItemShareableLabel();

       	// Bug #75
	}


	@NameToken(NameTokens.EDIT_ITEM)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<EditItemPresenter>
	{
	}

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

	@Inject
	EditItemPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
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
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		cancelTimers();
		clearForms();
		clearGlobals();
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
		allocateHoldEditItem();
		initializeExpiration();
		clearForms();
		showMessage("");
		adjustButtonsWidth();
		Button button = getView().getShowHideButton();
		button.setType(ButtonType.DANGER);
		button.setIcon(IconType.EYE_SLASH);
		button.setText(ObidosMessages.LANG.hide());
		showItemHtmlPanel();
		changeTitles();
		resetProgressBar();
		hideContainerNameForNote();
		fetchAndPopulateForm();
		setTextBoxKeyUpHandler(getView().getItemNameTextBoxNew());
		setSummernoteKeyUpHandler();
		startTimers();
		setExpirationStartDay();
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
	}
	
	private void resetFileChosen()
    {
    	getView().getFileChosenSpan().setText(glang.noFileChosen());
        sFilenameToUpload = null;
    }

	private void initializeExpiration()
	{
		getView().getDatePicker().setValue(null);
		getView().getTimeBox().setText("12:00 AM");
		getView().getClearExpirationCheckbox().setVisible(false);
		getView().getClearExpirationCheckbox().setValue(false);
	}
	
	private void setExpirationStartDay()
	{
		String action = ClientUtils.getActionFromUrl(placeManager);
		DatePicker dp = getView().getDatePicker();
		if (ObidosConstants.EDIT.equals(action))
		{
			ClientUtils.setStartDateToToday(dp);
			
		}
		else
		{
			ClientUtils.setStartDateToTomorrow(dp);
		}
	}

	private void adjustButtonsWidth()
	{
		Button b1 = getView().getShowHideButton();
		Button b2 = getView().getUpdateButton();
		Button b3 = getView().getResetButton();
		Button b4 = getView().getGrantPermissionsButton();
		ClientUtils.adjustButtonsWidth(b1, b2, b3, b4);
//		getView().getBottomToolBar().adjustButtonsWidth();
	}

	private void resetProgressBar()
	{
		ProgressBar pb = getView().getProgressbar();
		pb.setPercent(100);
		pb.setType(ProgressBarType.SUCCESS);
	}
	
	private void restartTimers()
	{
		cancelTimers();
		startTimers();
	}
	
	private void setSummernoteKeyUpHandler()
	{
		Summernote summernote = sHoldEditItem.getSummernote();
		summernote.addSummernoteKeyUpHandler(new SummernoteKeyUpHandler()
		{
			
			@Override
			public void onSummernoteKeyUp(SummernoteKeyUpEvent event)
			{
				// If privacy checkbox is Off, do not restart the timer
				// spgdev@spenego.com - Jun 3, 2023
				if (getView().getHideItemCheckBox().getValue())
				{
					restartTimers();
				}
			}
		});
	}
	
	private void setHideButtonToolTip(Integer seconds)
	{
		Button button = getView().getShowHideButton();
		String tooltip = null;
		if (seconds != null)
		{
			// TODO: use timeout from UserDTO
			// Issue #343
			Date date = new Date();
			date.setTime(date.getTime() + seconds * 1000);
			tooltip = "Will hide at " + date.toString();
			
		}
		button.setTitle(tooltip);
	}

	private void startTimers()
	{
		if (ClientUtils.getHideViewItemSeconds(currentUser) <= 0)
		{
			return;
		}

		cancelTimers();
		
      	getView().getHideItemCheckBox().setValue(true);

      	startItemHideTimer();
      	startCountDownTimer();
	}
	private void cancelTimers()
	{
		cancelItemHideTimer();
		cancelCountdownTimer();
	}
	
	private void clearCountdownTimer()
	{
		cancelCountdownTimer();
	}

	private void startItemHideTimer()
	{
		int seconds = ClientUtils.getHideViewItemSeconds(currentUser);
		if (seconds <= 0)
		{
			return;
		}
      	int delayMillis = seconds * 1000;
      	gwtLog("hideItemTimer: " + hideItemTimer);
      	
      	if (hideItemTimer  != null)
      	{
      		gwtLog("Timer is already created...");
      		hideItemTimer.cancel();
      		hideItemTimer.schedule(delayMillis);
      	}
      	else
      	{
			  hideItemTimer = new Timer() {

				@Override
				public void run()
				{
					gwtLog("+++++++RUN++++++++ HIDE ITEM ++++++++++++++++++");
					this.cancel();
					timerCallback();
				}
			  };
      	}
      	hideItemTimer.schedule(seconds * 1000);
	
	}
	private void startCountDownTimer()
	{
		int delay = ClientUtils.getHideViewItemSeconds(currentUser);
		gwtLog("Delay: " + delay);
		ProgressBar pb = getView().getProgressbar();
		resetProgressBar();
		if (delay <= 0)
		{
			return;
		}
		CheckBox cb = getView().getHideItemCheckBox();

		clearCountdownTimer();
		
      	countDownTimer = new Timer() {
      		int count = delay;
			@Override
			public void run()
			{
				count--;
				if (count < 0)
				{
					count = 0;
				}
				double percent = 0.0;
				if (count > 0)
				{
					percent = 100 * (count * 1.0 / delay);
					if (percent <= 50)
					{
						pb.setType(ProgressBarType.DANGER);
					}
					pb.setPercent(percent);
				}
				cb.setText(Integer.toString(count));
				if (count == 0)
				{
					count = delay;
					this.cancel();
				}
			}
      	};
      	countDownTimer.scheduleRepeating(1000);
	}
	
	private void timerCallback()
	{
		gwtLog("in timer calback");
		Row itemRow = getView().getItemRow();
		boolean visible = itemRow.isVisible();
		Button button = getView().getShowHideButton();
		button.setFocus(false);
		
		// if visible, hide
		if (visible)
		{
			// hide the item
			itemRow.setVisible(false);
			button.setType(ButtonType.SUCCESS);
			button.setIcon(IconType.EYE);
			button.setText(ObidosMessages.LANG.show());
			setHideButtonToolTip(null);
			// timer is not running
		}
	}
	
	private void cancelItemHideTimer()
	{
		gwtLog("hide item timer: " + hideItemTimer);
		if (hideItemTimer != null)
		{
			gwtLog("Cancel Hide Timer>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			gwtLog(" Timer running?: " + hideItemTimer.isRunning());
			hideItemTimer.cancel();
			hideItemTimer = null;
		}
	}
	private void cancelCountdownTimer()
	{
		gwtLog("Cancel countDownTimer...");
		if (countDownTimer != null)
		{
			gwtLog("Cancel Hide Timer>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			gwtLog(" Timer running?: " + countDownTimer.isRunning());
			countDownTimer.cancel();
		}
	}


	private void clearForms()
	{
		gwtLog("Clearing form elements");
		getView().getDocFilenameRow().setVisible(false);
		getView().getUploadRow().setVisible(false);
		getView().getFormGroup().clear();
		getView().getDatePicker().setValue(null);
		getView().getDatePicker().clearStartDate();
		getView().getDatePicker().setValue(null);
	}

	private void clearGlobals()
    {
		sHoldEditItem = null;
		sDocumentId = null;
		sFilenameToUpload = null;
    }

	private boolean formChanged()
	{
		if (sHoldEditItem == null)
		{
			return false;
		}
		String nameOld = sHoldEditItem.getItemDTO().getName();
		String nameNew = getView().getItemNameTextBoxNew().getValue();
		if (nameOld != null)
		{
			if (!nameOld.equals(nameNew))
			{
				gwtLog("name has changed...");
				return true;
			}
		}
		
		boolean itemIsNote = ClientUtils.fromBoolean(sHoldEditItem.getItemIsNote());
		if (itemIsNote)
		{
			Summernote summernote = sHoldEditItem.getSummernote();
			Map<Summernote, UserDefinedFieldValueDTO> summernoteFieldValueDTO = sHoldEditItem.getSummernoteFieldValueDTO();
			if (!Arrays.equals(summernote.getCode().getBytes(),summernoteFieldValueDTO.get(summernote).getBlobValue()))
			{
				return true;
			}
			return false;
		}

		List<TextBox> textBoxList = sHoldEditItem.getTextBoxList();
		Map<TextBox, UserDefinedFieldValueDTO> textBoxFieldValueDTO = sHoldEditItem.getTextBoxFieldValueDTO();
		for (TextBox textBox : textBoxList)
		{ 
			if (!Arrays.equals(textBox.getValue().getBytes(), textBoxFieldValueDTO.get(textBox).getBlobValue()))
			{
				return true;
			}
		}

		return false;
	}

	private void addModifiedFieldValue(String value, UserDefinedFieldValueDTO userDefinedFieldValueDTO, UserDefinedTypeValueDTO userDefinedTypeValueDTO) {
		if (!Arrays.equals(value.getBytes(), userDefinedFieldValueDTO.getBlobValue())) {
			userDefinedFieldValueDTO.setBlobValue(value.getBytes());
			userDefinedTypeValueDTO.addFieldValue(userDefinedFieldValueDTO);
		}
	}
	
	/**
	 * @return days if a date in future is set
	 *         0 if no date is set 
	 *         -1 if date is set in past
	 * <p>
	 * @author spgdev@spenego.com - Aug 12, 2019
	 */
	private int getShareExpirationDays()
	{
		DatePicker dp = getView().getDatePicker();
		Date expireDate = dp.getValue();
		if (expireDate == null)
		{
			return 0;
		}
		Date today = new Date(); // now
		gwtLog("Expire date: " + expireDate.toString());
		int days = CalendarUtil.getDaysBetween(today, expireDate);
		if (days < 0)
		{
			showErrorMessage(glang.expirationDateinPast());
			return -1;
		}
		else if (days == 0)
		{
			showErrorMessage(glang.expirationDateMustBeTomorrow());
			return -1;
		}
		return days;
	}

	@Override
	public void updateItem(boolean goback)
	{
		showMessage(null);

		SharedItemDTO itemDTO = sHoldEditItem.getItemDTO();
		String itemName = getView().getItemNameTextBoxNew().getValue();
		itemDTO.setName(itemName);

		UserDefinedTypeValueDTO userDefinedTypeValueDTO = new UserDefinedTypeValueDTO();

		final ArrayList<UserDefinedTypeValueDTO> list = new ArrayList<UserDefinedTypeValueDTO>(1);
		list.add(userDefinedTypeValueDTO);
		itemDTO.clearValues();

		// get the id saved when the form is populated

		// Note: it must have to set set before hand during form population
		Long dtoId = sHoldEditItem.getUserDefinedTypeValueDTOId();
		userDefinedTypeValueDTO.setId(dtoId);

		boolean itemIsNote = ClientUtils.fromBoolean(sHoldEditItem.getItemIsNote());
		Summernote summernote = sHoldEditItem.getSummernote();
		Map<Summernote, UserDefinedFieldValueDTO> summernoteFieldValueDTO = sHoldEditItem.getSummernoteFieldValueDTO();

		if (itemIsNote)
		{
			// we saved userDefinedTypeValueDTOId when we fetched the item
			// assume Note for now
			addModifiedFieldValue(summernote.getCode(), summernoteFieldValueDTO.get(summernote), userDefinedTypeValueDTO);
		}
		else
		{
			List<TextBox> textBoxList = sHoldEditItem.getTextBoxList();
			Map<TextBox, UserDefinedFieldValueDTO> textBoxFieldValueDTO = sHoldEditItem.getTextBoxFieldValueDTO();
			for (TextBox textBox : textBoxList) { // get the DTO we saved when populated the form
				addModifiedFieldValue(textBox.getValue(), textBoxFieldValueDTO.get(textBox), userDefinedTypeValueDTO);
			}
		}
		
		if (userDefinedTypeValueDTO.getFieldValues() != null && !userDefinedTypeValueDTO.getFieldValues().isEmpty())
		{
			itemDTO.setValues(list);
		}

		Date expirationDate = ClientUtils.getDateFromDatePickerAndTimeBox(currentUser,
				getView().getDatePicker(),
				getView().getTimeBox(),
				getView().getMessageRow());
		if (expirationDate != null)
		{
			ItemExpiration itemExpiration = new ItemExpiration(expirationDate);
			itemDTO.setItemExpiration(itemExpiration);
		}

		InlineCheckBox cb = getView().getClearExpirationCheckbox();
		if (cb.isVisible() && cb.getValue())
		{
			gwtLog("Clear Expiration...");
			itemDTO.setClearExpireTime(true);
		}

		// if private radio is selected, remove expiration
		gwtLog("itemdto shareable: " + itemDTO.getShareable());
		gwtLog("Share count: " + itemDTO.getShareCount());
		
		InlineRadio privateRadio = getView().getPrivateItemRadio();
		if (privateRadio.isVisible() && privateRadio.getValue() == true)
		{
			itemDTO.setShareable(Boolean.FALSE);
			gwtLog("Private radio is selected remove expiration");
			itemDTO.clearExpireTime();
			boolean shareableItem = sHoldEditItem.isShareableItem();
			if (shareableItem)
			{
				String type = glang.item();
				if (sHoldEditItem.isNote())
				{
					type = glang.note();
				}
				String message = glang.publicToPrivateEditNotSharedWarning(type, type, itemDTO.getName());
				if (itemDTO.getShared())
				{
					message = glang.publicToPrivateEditAlreadySharedWarning(type, itemDTO.getName());
					
				}
				String title = glang.warning();
				ClientUtils.promptForAction(() -> updateItemReal(itemDTO, goback), title, message);
			}
			else
			{
				updateItemReal(itemDTO, goback);
			}
		}
		else
		{
			itemDTO.setShareable(Boolean.TRUE);
			updateItemReal(itemDTO, goback);
		}
	}
	
	private void updateItemReal(SharedItemDTO dto, boolean goback)
	{
    	if (dto == null)
    	{
    		return;
    	}
    	ObidosMessages lang = ObidosMessages.LANG;
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				String what = lang.item();
				if (isNote())
				{
					what = lang.note();
				}
				if (goback)
				{
					ClientUtils.goBack(placeManager);
				}
				else
				{
					if (getView().getClearExpirationCheckbox().getValue())
					{
						initializeExpiration();
					}
					sItemCreatedMessage = lang.xUpdatedSuccessfully(what, getView().getItemNameTextBoxNew().getValue());
					showMessage(sItemCreatedMessage);
					Long documentId = getDocumentId();
					if (fileSelectedForUpload() && documentId != null)
					{
						gwtLog("Upload new file");
						uploadFile(documentId);
					}
					else
					{
						fetchAndPopulateForm();
					}
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(lang.couldNotUpdateItem(caught.getMessage()));
			}
		};
		gwtLog("Share expired? " + dto.isShareExpired());
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ItemService.Utility.getInstance().update(authCreds, dto, callback);
	}
	
	private boolean isNote()
	{
		String type = ClientUtils.getItemTypeFromUrl(placeManager);
		if (ObidosConstants.NOTEBOOK.equals(type))
		{
			return true;
		}
		return false;
	}
	
	private void allocateHoldEditItem()
	{
		sHoldEditItem = null;
		sHoldEditItem = new HoldEditItem();
	}

	@Override
	public void reset()
	{
		showMessage(null);
		allocateHoldEditItem();
		fetchAndPopulateForm();
		initializeExpiration();
	}

	@Override
	public void showListItemsPage()
	{
		boolean changed = formChanged();
		gwtLog("Form chagned: " + changed);
		if (changed)
		{
			promptToSaveChange();
			return;
		}
		ClientUtils.goBack(placeManager);
	}

	private Integer getStoredDaysUntilExpire()
	{
		return sHoldEditItem.getShareExpiresInDays();
	}
	private void storeDaysUntilExpire(SharedItemDTO dto)
	{
		if (dto.isExpirationSet())
		{
			Integer expireDays = dto.getItemExpiration().getDaysUntilExpire();
			if (expireDays == null)
			{
				expireDays = 0;
			}
			sHoldEditItem.setShareExpiresInDays(expireDays);
		}
		else
		{
			sHoldEditItem.setShareExpiresInDays(null);
		}
	}
	
	private void setTextBoxKeyUpHandler(final TextBox textBox)
	{
		textBox.addKeyUpHandler(new KeyUpHandler()
		{
			
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				gwtLog("Reset hide timer...");
				restartTimers();
			}
		});
	}
	
	private void handleSharedBySomeone(SharedItemDTO dto)
	{
		Row sharedByRow = getView().getSharedByRow();
		Row sharedOnRow = getView().getSharedOnRow();
		if (currentUser == null)
		{
			return;
		}

		if (ClientUtils.loggedInUserIsTheOwner(currentUser, dto))
		{
			gwtLog("Owner .. don't show shared by");
			sharedByRow.setVisible(false);
			sharedOnRow.setVisible(false);
			getView().getRadioRow().setVisible(true);
		}
		else
		{
			sharedByRow.setVisible(true);
			sharedOnRow.setVisible(true);
			getView().getRadioRow().setVisible(false);

			if (dto.getOwnerFullname() != null)
			{
				getView().getOwnerDropDownAnchor().setText(dto.getOwnerFullname());
				boolean hasProfilePic = ClientUtils.fromBoolean(dto.getProfilePictureEnabled());
				if (hasProfilePic)
				{
					getView().getOwnerDetails().setIconColor(ObidosConstants.PURPLE_COLOR);
				}
				else
				{
					getView().getOwnerDetails().setIconColor("#000");
				}

				Anchor emailAnchor = getView().getEmailAnchor();
				String mailHref = ClientUtils.makeEmailLink(dto.getEmail());
				emailAnchor.setHTML(mailHref);
				
				// primary phone
				Anchor phoneAnchor = getView().getPhoneAnchor();
				String phone = dto.getPhone();
				if (phone != null && phone.length() > 0)
				{
					String phoneHref = ClientUtils.makePhoneLink(phone);
					phoneAnchor.setHTML(phoneHref);
					phoneAnchor.setVisible(true);
				}
				else
				{
					phoneAnchor.setVisible(false);
				}
				Date d = dto.getSharedAt();
				if (d != null)
				{
					gwtLog("Shared at: " + d);
					getView().getSharedOnTextBox().setValue(d.toString());
				}
				else
				{
					gwtLog("Shared at date is null");
					getView().getSharedOnTextBox().setValue(ObidosMessages.LANG.unknown());
				}
			}
		}
	}

	private void fetchAndPopulateForm()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			return;
		}

		GwtAsyncWrapper<SharedItemDTO> callback = new GwtAsyncWrapper<SharedItemDTO>(this)
		{
			@Override
			public void uponSuccess(SharedItemDTO sidto)
			{
				gwtLog("EditItemPresenter: shared item name = " + sidto.getName());
				if (sidto.getIsContainerPrivate() == null)
				{
				    String msg = "ERROR: SharedItemDTO->getIsContainerPrivate() returning null";
				    Window.alert(msg);
				    showErrorMessage(msg);
				}
				Boolean isContainerPrivate = sidto.getIsContainerPrivate();
				gwtLog("YYY is container private?" + isContainerPrivate);

				if (sidto.isExpirationSet())
				{
					Date date = sidto.getItemExpiration().getExpiresAt();

					gwtLog("Expiration date: "+ date);
					
			        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(getView().getTimeBox().getTimeFormat());
			        String timeStr = dateTimeFormat.format(date);
			        gwtLog("Time: " + timeStr);
					getView().getDatePicker().setValue(date);
					getView().getTimeBox().setText(timeStr);
					getView().getClearExpirationCheckbox().setVisible(true);
				}

				// save
				boolean shareableItem =  ClientUtils.fromBoolean(sidto.getShareable());
				
				// save
				sHoldEditItem.setShareableItem(shareableItem);
				
				// save the SharedItemDTO for update
				sHoldEditItem.setItemDTO(sidto);

				// show owner info if logged in user is not the owner
				handleSharedBySomeone(sidto);
				// show hide certain elements in form 
				updateForm(sidto);

				// If the item is shared by someone, shaerable property must be
				// set to false
				// Issue #412
				// also hide permission button. Issue #443
				getView().getGrantPermissionsButton().setVisible(true);

				if (!ClientUtils.loggedInUserIsTheOwner(currentUser, sidto) )
				{
					sidto.setShareable(Boolean.FALSE);
					getView().getGrantPermissionsButton().setVisible(false);
				}
				else
				{
			        if (!ClientUtils.fromBoolean(sidto.getShared()))
					{
						getView().getGrantPermissionsButton().setVisible(false);
					}
				}
				
				// Issue #442
				// Update on Oct-28-2025
				// If Item is not shared, do not show the Grant button
				// just because the item is shareable, it does not give the option
				// to show the grant permission button
				//if (!ClientUtils.fromBoolean(sidto.getShareable()))
				boolean isShareable = ClientUtils.fromBoolean(sidto.getShareable());
				boolean isShared = ClientUtils.fromBoolean(sidto.getShared());
				boolean isSharedEplicitly = ClientUtils.fromBoolean(sidto.getSharedExplicitly());
				gwtLog("XXX YYY: isShareable: " + isShareable + " is Shared: " + isShared + " isSharedExplicitly " + isSharedEplicitly);
				if (isShareable && isShared && isSharedEplicitly)
				{
					getView().getGrantPermissionsButton().setVisible(true);
				}
				else
				{
					getView().getGrantPermissionsButton().setVisible(false);
				}
				
				// Show only if the item is already shared with someone
				getView().getSharedWithUsersButton().setVisible(false);
				if (ClientUtils.fromBoolean(sidto.getShared()))
				{
					getView().getSharedWithUsersButton().setVisible(true);
					// Issue# 700. show only if the user is owner
					if (ClientUtils.loggedInUserIsTheOwner(currentUser, sidto) )
					{
					    getView().getSharedWithUsersButton().setVisible(false);
					}
				}
				
				// hide expire row if the user is not the owner #700
				getView().getContainerNameRowNew().setVisible(false); // #75
				getView().getExpiresRow().setVisible(false);
				if (ClientUtils.loggedInUserIsTheOwner(currentUser, sidto) )
				{
					getView().getContainerNameRowNew().setVisible(true);
					if (sidto.getShareable())
					{
						getView().getExpiresRow().setVisible(true);
					}
					if (ClientUtils.fromBoolean(sidto.getShared()))
					{
						getView().getSharedWithUsersButton().setVisible(true);
					}
				}
				
				// Also Issue #700
				hideContainerNameForNote();
				

				// create form dynamically
				FormGroup formGroup = getView().getFormGroup();
				formGroup.clear();

				List<UserDefinedTypeValueDTO> typeValues = sidto.getValues();
				

				gwtLog("  TypeValues Size: " + typeValues.size());
				gwtLog("        Item name: " + sidto.getName());
				gwtLog("        >>> SharedItemDTO id: " + sidto.getId());
				gwtLog(" Shareable? " + sidto.getShareable());
				gwtLog(" Container is private? " + sidto.getIsContainerPrivate());

				Long id = sidto.getValues().get(0).getId();
				// must save it for update
				sHoldEditItem.setUserDefinedTypeValueDTOId(id);
				
				getView().getItemNameTextBoxNew().setValue(sidto.getName());

				for (UserDefinedTypeValueDTO typeValue : typeValues)
				{
					List<UserDefinedFieldValueDTO> fieldValues = typeValue.getFieldValues();
					gwtLog(">> Field Values size: " + fieldValues.size());
					gwtLog(">> is it a note?: " + typeValue.isNote());
					gwtLog(">> name: " + typeValue.getName());
					// ID for UserDefinedTypeValue
					gwtLog(">>>>> +++ ++ Field value id: " + typeValue.getId());

					// must save it for update
					// userDefinedTypeValueDTOId = typeValue.getId();

					boolean isNote = typeValue.isNote();
					sHoldEditItem.setItemIsNote(isNote);

					if (!isNote)
					{
						setPublicRadioText(ObidosMessages.LANG.shareableItem());
						setPrivateRadioText(ObidosMessages.LANG.privateItem());
						// create label and text boxes
						
						for (UserDefinedFieldValueDTO fieldValueDTO : fieldValues)
						{
							if (fieldValueDTO.getDocument() != null)
							{
								continue;
							}

							Long fieldId = fieldValueDTO.getId();
							
							// use same widget as view screen
							// Bug #76
							// Nov-25 2024
							ObidosItemRow itemRow = new ObidosItemRow(false, "", "", true);

							String labelText = fieldValueDTO.getName();
							itemRow.setLabelText(labelText);

							TextBox textBox = itemRow.getTextBox();
							textBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);

							// Save TextBox ++++++++++++++++++++++++++++++
							sHoldEditItem.getTextBoxList().add(textBox);
							// Save DTO ++++++++++++++++++++++++++++++++++
							sHoldEditItem.getTextBoxFieldValueDTO().put(textBox, fieldValueDTO);
							textBox.setEnabled(true);
							formGroup.add(itemRow);

							byte[] fieldBytes = fieldValueDTO.getBlobValue();
							try
							{
								if (fieldBytes != null)
								{
									String fieldText = new String(fieldBytes, "UTF-8");
									gwtLog("Field id: " + fieldId + " Field value: " + fieldText + " Position: " + fieldValueDTO.getPosition());
									textBox.setValue(fieldText);
								}
							} catch (UnsupportedEncodingException e)
							{
								showErrorMessage("Could not covnert field bytes to UTF-8");
							}
						}
					}
					else /* note */
					{
						setItemNameLabel(ObidosMessages.LANG.noteName());
						setPublicRadioText(ObidosMessages.LANG.shareableNote());
						setPrivateRadioText(ObidosMessages.LANG.privateNote());
						
						// Row starts--
					    Row row = new Row();
					    FlowPanel fp = new FlowPanel();
					    fp.addStyleName("col-sm-offset-3 col-sm-1");
					    FormLabel fl = new FormLabel();
					    Span span = new Span();
					    ClientUtils.setNoteSpanHtml(span, ObidosMessages.LANG.note());
					    fp.add(span);
					    fp.add(fl);
					    row.add(fp);

						fp  = new FlowPanel();
						fp.addStyleName("col-sm-7");
						// add Summernote to FlowPanel
						fp.add(sHoldEditItem.getSummernote());
						row.add(fp);
						formGroup.add(row);
						for (UserDefinedFieldValueDTO fieldValue : fieldValues)
						{
							gwtLog(">> Field value ID:   " + fieldValue.getId());
							// store field value dto keyed with summernote
							sHoldEditItem.getSummernoteFieldValueDTO().put(sHoldEditItem.getSummernote(), fieldValue);
							byte[] noteBytes = fieldValue.getBlobValue();
							try
							{
								String note = new String(noteBytes, "UTF-8");
								sHoldEditItem.getSummernote().setCode(note);
							} catch (UnsupportedEncodingException e)
							{
								gwtLog("Error converted note bytes: " + e);
								showErrorMessage("Could not convert notes to UTF-8");
							}
						}
					}
				}
				
				
				Long documentId = ClientUtils.getDocumentId(sidto);
				getView().getDocFilenameRow().setVisible(false);
				getView().getUploadRow().setVisible(false);
				if (documentId != null)
				{
					// save it so that it can be used if user uploads a new file.
					saveDocumentId(documentId);

					String filename = ClientUtils.getDocumentFilename(sidto);
					gwtLog("Documnet id: " + documentId);
					if (filename != null)
					{
						updateFileUplaodLabel(glang.replaceDocument());
						getView().getDocFilenameRow().setVisible(true);
						getView().getDocFilenameTextBox().setValue(filename);
						getView().getUploadRow().setVisible(true);
						gwtLog("Document file name: " + filename);
					}
					else
					{
						updateFileUplaodLabel(glang.selectFile());
						// show the file upload widget if there is a document field
						// but user did not upload any file before
						showUploadWidget(ClientUtils.hasDocumentFieldId(sidto));
					}
				}
				restartTimers();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get Item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
//		ItemService.Utility.getInstance().get(authCreds, itemId, OrderBy.UDF_POSITION_ASC, callback);
		ItemService.Utility.getInstance().getSharedItem(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
	}

	private <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
	}
	
	private void saveDocumentId(Long documentId)
	{
		sDocumentId = documentId;
	}
	
	// saved during form population
	private Long getDocumentId()
	{
		return sDocumentId;
	}

	@Override
	public void help()
	{
		restartTimers();
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}

	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}

	private void showErrorMessage(String errorMessage)
	{
//		ClientUtils.showBootboxDialog(glang.error(), errorMessage);
		
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	// no need to fetch it anymore as itemDTO contains everything we need
	private void updateForm(SharedItemDTO itemDTO)
	{

		storeDaysUntilExpire(itemDTO);
		
		DatePicker datePicker = getView().getDatePicker();
		Row expiresRow = getView().getExpiresRow();
		InlineRadio publicRadio = getView().getPublicItemRadio();
		InlineRadio privateRadio = getView().getPrivateItemRadio();
		Button permissionButton = getView().getGrantPermissionsButton();
		TextBox containerNameTextBox = getView().getContainerNameTextBoxNew();
		
		boolean editing = true;
		ClientUtils.editItemUpdateForm(expiresRow, datePicker,
				publicRadio, privateRadio,
				permissionButton,
				editing,
				containerNameTextBox,
				getView().getContainerShareableLabel(),
				getView().getItemShareableLabel(),
				itemDTO);
		
		boolean p = ClientUtils.fromBoolean(itemDTO.getIsContainerPrivate());
		gwtLog("YYY is container private: " + p);
		ClientUtils.configureDateFormat(getView().getDatePicker(), currentUser);
	}


	private void setItemNameLabel(String text)
	{
//		getView().getItemNameLabel().setText(text);
	}

	private void setPublicRadioText(String text)
	{
		getView().getPublicItemRadio().setText(text);
	}

	private void setPrivateRadioText(String text)
	{
		getView().getPrivateItemRadio().setText(text);
	}

	private void changeTitles()
	{
		ClientUtils.changeItemPageTitles(placeManager, getView().getListButton(), getView().getPanelHeader());
	}

	@Override
	public void showGrantPermissionsToUsersView()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage("No item id found in URL");
			return;
		}
		Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
				
		String action = ClientUtils.getActionFromUrl(placeManager);
        Map<String,String> with = new HashMap<>();
        String nameToken = NameTokens.GRANT_PERMISSIONS_TO_USERS_FOR_ITEMS;
        with.put(ObidosConstants.ITEM_ID, itemId.toString());
        if (containerId != null)
        {
        	with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        }
        if (action != null)
        {
        	with.put(ObidosConstants.ACTION , action);
        }
        ClientUtils.addPlace(placeManager, with, NameTokens.EDIT_ITEM);
        ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void publicRadioCallback()
	{
		showMessage("");
		restartTimers();
		getView().getExpiresRow().setVisible(true);

		Integer expireDays = getStoredDaysUntilExpire();
		if (expireDays != null)
		{
		}

	}

	@Override
	public void privateRadioCallback()
	{
		restartTimers();
		showMessage("");
		getView().getExpiresRow().setVisible(false);
	}

  	private void setHideButtonToolTip()
	{
  		ObidosMessages lang = ObidosMessages.LANG;
  		
		int seconds = ClientUtils.getHideViewItemSeconds(currentUser);
		Button button = getView().getShowHideButton();
		String tooltip = null;
		CheckBox cb = getView().getHideItemCheckBox();
		// save
		if (sHoldEditItem != null)
		{
			sHoldEditItem.setHideTime(lang.off());
		}
		if (seconds > 0)
		{
			Date date = new Date();
			date.setTime(date.getTime() + seconds * 1000);
			// save
			if (sHoldEditItem != null)
			{
				sHoldEditItem.setHideTime(Integer.toString(seconds));
			}
			tooltip = ObidosMessages.LANG.willHideAt(date.toString());
			cb.setValue(true);
		}
		cb.setValue(false);
		if (sHoldEditItem != null)
		{
			getView().getHideItemCheckBox().setText(sHoldEditItem.getHideTime());
		}
		button.setTitle(tooltip);
	}
  	
  	private void showItemHtmlPanel()
  	{
		Row itemRow = getView().getItemRow();
		Button button = getView().getShowHideButton();
		button.setType(ButtonType.DANGER);
		button.setIcon(IconType.EYE_SLASH);
		button.setText(ObidosMessages.LANG.hide());
		setHideButtonToolTip();
		itemRow.setVisible(true);
  	}
	
	@Override
	public void showHideItem()
	{
		int delay = ClientUtils.getHideViewItemSeconds(currentUser);
		if (delay < 0)
		{
			delay = 0;
		}
		Row itemRow = getView().getItemRow();
		boolean visible = itemRow.isVisible();
		itemRow.setVisible(!visible);
		visible = itemRow.isVisible();

		Button button = getView().getShowHideButton();
		button.setFocus(false);
		if (visible)
		{
			button.setType(ButtonType.DANGER);
			button.setIcon(IconType.EYE_SLASH);
			button.setText(ObidosMessages.LANG.hide());
			setHideButtonToolTip();
			restartTimers();
		}
		else
		{
			cancelTimers();
			button.setType(ButtonType.SUCCESS);
			button.setIcon(IconType.EYE);
			button.setText(ObidosMessages.LANG.show());
			button.setTitle(null);
		}
	}

	@Override
	public void hideItemCheckBoxCallback()
	{
		CheckBox checkBox = getView().getHideItemCheckBox();
		Button button = getView().getShowHideButton();
		boolean on = checkBox.getValue();
		if (!on)
		{
			gwtLog("Cancel timers..");
			cancelTimers();
			checkBox.setText(ObidosMessages.LANG.off());
			resetProgressBar();
			button.setTitle(null);
			return;
		}
		else
		{
			setHideButtonToolTip();
			checkBox.setText(sHoldEditItem.getHideTime());
			startTimers();
		}
	}

	private boolean itemIsShared()
	{
		String type = ClientUtils.getTypeFromUrl(placeManager);
		return ObidosConstants.SHARED_WITH_ME.equals(type) || ObidosConstants.SHARED_WITH_OTHERS.equals(type);
	}

	// note has a special builtin container, we want to hide them
	// Issue #395
	private void hideContainerNameForNote()
	{
		String itemType = ClientUtils.getItemTypeFromUrl(placeManager);
		gwtLog("Item type: " + itemType);
		
		boolean typeIsItem = !ObidosConstants.NOTEBOOK.equals(ClientUtils.getItemTypeFromUrl(placeManager));

		getView().getContainerNameRowNew().setVisible(typeIsItem);
		getView().getItemNameLabelNew().setText(typeIsItem ? glang.itemNameLabel() : glang.noteNameLabel());
	}

	private void promptToSaveChange()
	{
		String title = glang.formChagned();
		String message = glang.confirmSaveForm();

		ClientUtils.promptToSaveChange(() -> updateItem(true), title, message, placeManager);
	}

	@Override
	public void listUsersItemIsSharedWithMe()
	{
	    gwtLog("List Users");
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage("Could not get Item ID from URL");
			return;
		}
		String itemType = ClientUtils.getItemTypeFromUrl(placeManager);
		Map<String,String> with = new HashMap<>();
		with.put(ObidosConstants.ITEM_ID, itemId.toString());
		if (itemType != null)
		{
			with.put(ObidosConstants.ITEM_TYPE, itemType);
		}
		String nameToken = NameTokens.LIST_USERS_ITEM_IS_SHARED_WITH_ME;
		ClientUtils.addParamToMap(placeManager, with, ObidosConstants.OWNERID);
		ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
		ClientUtils.addPlace(placeManager, with);
		ClientUtils.showPage(placeManager, nameToken, with);
	}

	@Override
	public void showOwnerDetailsPage()
	{
		Long ownerId = ClientUtils.getOwnerIdFromUrl(placeManager);
		if (ownerId == null)
		{
			return;
		}
		/*
		ClientUtils.showUserInfoPage(placeManager, ownerId);
		*/
		if (sModalData == null)
		{
			sModalData = ClientUtils.createUserInfoModal();
		}
		ClientUtils.showUserInfo(placeManager, sModalData, ownerId);
	}

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

	private void uploadFile(final long documentId)
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
					showErrorMessage("No file specified to upload!");
					return;
				}
				showMessage("Uploading, please wait........");
				if (sWiseUploadModalData != null)
				{
					gwtLog("Swow wise dialog");
					// HTML5 upload
					sWiseUploadModalData.setFilename(sFilenameToUpload);
					sWiseUploadModalData.clear();
					getView().getWiseUploader().startUpload();
					ClientUtils.showWiseUploadModal(sWiseUploadModalData);
					gwtLog("XXXXXXX");
				}
				else
				{
					gwtLog("Wise dialog is null");
				}
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
    private void showUploadWidget(boolean visible)
    {
    	gwtLog("Show uplaod widget: " + visible);
    	getView().getUploadRow().setVisible(visible);
    }
    
    private void updateFileUplaodLabel(String text)
    {
    	getView().getRelpaceDocLabel().setText(text);
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
		u.setButtonText("<button type=\"button\" class=\"btn btn-primary\"><i class=\"fa fa-upload\"></i> Choose File</button>");
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
			sWiseUploadModalData = ClientUtils.createWiseFileUploadProgressModal(
					placeManager,
					u,
					sFilenameToUpload,
					getView().getDocFilenameTextBox(),
					NameTokens.EDIT_ITEM,
					getView().getMessageRow(),
					getView().getFileChosenSpan());
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

