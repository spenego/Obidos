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

package com.spenego.Obidos.client.application.viewitem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroupAddon;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.DocumentService;
import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ClientUtilsTest;
import com.spenego.Obidos.client.util.TwoFAModalData;
import com.spenego.Obidos.client.util.UserInfoModalData;
import com.spenego.Obidos.client.util.ViewItemFormData;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.DocumentDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.exceptions.NoSuchRecordException;

public class ViewItemPresenter extends Presenter<ViewItemPresenter.MyView, ViewItemPresenter.MyProxy>
		implements ViewItemUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
			
	private Timer hideItemTimer = null;
	private Timer countDownTimer = null;
	private Timer fileUploadPendingTimer = null;
	private Timer sessionExtendTimer = null;

	private String hideTime = null;
	private Long sContainerId = null;
	private Long sDocumentId = null;
	private static UserInfoModalData sModalData = null;
	private ViewItemFormData sItemFormData = null;
	
	interface MyView extends View, HasUiHandlers<ViewItemUiHandlers>
	{
		public HTMLPanel getHtmlPanel();
		public BlockQuote getHelpBlockQuote();
		public Row getSharedByRow();
		public Row getSharedOnRow();
		public FormGroup getFormGroup();
		public Button getOwnerDropDownAnchor();
		public TextBox getSharedOnTextBox();
		public Row getExpiresRow();
		public TextBox getExpiresTextBox();
		public Row getContentRow();
		public Button getShowHideButton();
        public Row getHideItemCheckBoxRow();
        public CheckBox getHideItemCheckBox();
        public ProgressBar getProgressbar();
        public Button getTakeOwnershipButton();
        public Button getListUsers();
        public ObidosMessageRow getMessageRow();
//        public InputGroupAddon getItemTypeAddon();
        public AnchorListItem getOwnerDetails();
        public Anchor getEmailAnchor();
        public Anchor getPhoneAnchor();
        public ObidosButtonToolBar getButtonToolBar();
        public ObidosPanelHeader getPanelHeader();
        public ObidosRowBottom2px getContainerSharedWithMeNameRow();
        public ObidosTextBox getContainerSharedWithMeNameTextBox();
        public ObidosInputGroupAddon getContainerSharedWithMeTypeAddon();
//        public FlowPanel getItemFlowPanel();
        public ObidosTextBox getTimeTextBox();
        public FormPanel getFormPanel();
        public ObidosRowBottom2px getFileDownloadRow();
        public Button getFileDowloadButton();
        public ObidosRowBottom2px getShow2FACodeButtonRow();
        public Button getShow2FACodeButton();
		public Row getFileUploadPendingRow();
		public Label getPendingTimerLabel();
		public ObidosInputGroup getPendinFileUploadInputGroup();
		public ObidosTextBox getCreatedAtTextBox();
		public ObidosTextBox getUpdatedAtTextBox();
		public ObidosRowBottom2px getUpdatedAtRow();

		// Bug #75 starts
		public ObidosRowBottom2px getContainerNameRowNew();
		public ObidosTextBox getContainerNameTextBoxNew();
		public FormLabel getContainerShareableLabel();

		public FormLabel getItemNameLabelNew();
		public FlowPanel getItemFlowPanelNew();
		public ObidosTextBox getItemNameTextBoxNew();
		public FormLabel getItemShareableLabel();

		// Bug #75 starts
	}

	@NameToken(NameTokens.VIEW_ITEM)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<ViewItemPresenter>
	{
	}

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;
	@Inject
	ViewItemPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		clearTimers();
		cancelFileUploadPendingTimer();
		clearForm();
		resetForm();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMainHemlPanel(true);
		resetForm();
		adjustButtonWidth();
		resetProgressBar();
		clearForm();
		hideContainerNameForNote(); // Issue #395
		fetchAndPopulateForm();
		getView().getContentRow().setVisible(true);
		getView().getFileDownloadRow().setVisible(false);
        Button button = getView().getShowHideButton();
        button.setType(ButtonType.DANGER);
        button.setIcon(IconType.EYE_SLASH);
        button.setText(ObidosMessages.LANG.hide());
        setHideButtonToolTip();
		setHideButtonToolTip();
		startTimers();
		addFileDownloadHandlers();
	}
	
	// test long filenames
	private void testLongFileNamesShortening()
	{
		ClientUtilsTest.runAllTests();
	}
	
	private void showMainHemlPanel(boolean visible)
	{
		getView().getHtmlPanel().setVisible(visible);
	}
	private void resetForm()
	{
		sContainerId = null;
	}
	
	private Long getContainerId()
	{
		return sContainerId;
	}
	private void setContainerId(Long containerId)
	{
		sContainerId = containerId;
	}
	
	private void adjustButtonWidth()
	{
        getView().getButtonToolBar().adjustButtonsWidth();
	}

	private void resetProgressBar()
	{
		ProgressBar pb = getView().getProgressbar();
		pb.setPercent(100);
		pb.setType(ProgressBarType.SUCCESS);
	}

	private void setHideButtonToolTip()
	{
		int seconds = getHideViewItemSeconds();
		Button button = getView().getShowHideButton();
		String tooltip = null;
		CheckBox cb = getView().getHideItemCheckBox();
		hideTime = ObidosMessages.LANG.off();
		if (seconds > 0)
		{
			Date date = new Date();
			date.setTime(date.getTime() + seconds * 1000);
			hideTime = Integer.toString(seconds);
			tooltip = ObidosMessages.LANG.willHideAt(date.toString());
			cb.setValue(true);
		}
		cb.setValue(false);
		getView().getHideItemCheckBox().setText(hideTime);
		button.setTitle(tooltip);
	}
	
	private int getHideViewItemSeconds()
	{
		if (currentUser == null)
		{
			return 0;
		}
		Integer delay = currentUser.getUserDTO().getHideItemDelay();
		return ClientUtils.fromInteger(delay);
	}
	
	
	private void startTimers()
	{
		if (getHideViewItemSeconds() <= 0)
		{
			return;
		}

		cancelTimers();
		
      	getView().getHideItemCheckBox().setValue(true);

      	startItemHideTimer();
      	startCountDownTimer();
	}
	
	private void showItemButton(boolean visible)
	{
		Button button = getView().getShowHideButton();
		if (visible)
		{
			button.setType(ButtonType.SUCCESS);
			button.setIcon(IconType.EYE);
			button.setText(ObidosMessages.LANG.show());
			setHideButtonToolTip();
		}
	}
	
	private void timerCallback()
	{
		gwtLog("in timer calback");
		Row row = getView().getContentRow();
		boolean visible = row.isVisible();
		// if visible, hide
		if (visible)
		{
			// hide the item
			row.setVisible(false);
			showItemButton(visible);
			// timer is not running
		}
	}
	
	private void startItemHideTimer()
	{
		int seconds = getHideViewItemSeconds();
		if (seconds <= 0)
		{
			return;
		}
      	int delayMillis = seconds * 1000;
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
	private void cancelTimers()
	{
		cancelItemHideTimer();
		cancelCountdownTimer();
	}
	
	private void cancelCountdownTimer()
	{
		gwtLog("Cancel countDownTimer...");
		cancelTimer(countDownTimer);
	}
	private void cancelFileUploadPendingTimer()
	{
		cancelTimer(fileUploadPendingTimer);
	}


	private void cancelItemHideTimer()
	{
		gwtLog("Cancel hide item timer..");
		cancelTimer(hideItemTimer);
	}
	
	private void cancelTimer(Timer timer)
	{
		if (timer != null)
		{
			gwtLog("	Cancel timer...........");
			timer.cancel();
		}
	}
	
	private void clearTimer(Timer timer)
	{
		cancelTimer(timer);
		timer = null;
	}
	
	private void clearIdleTimer()
	{
		cancelItemHideTimer();
		hideItemTimer = null;
	}
	private void clearCountdownTimer()
	{
		cancelCountdownTimer();
		countDownTimer = null;
	}

	private void clearFileUploadPendingTimer()
	{
		cancelFileUploadPendingTimer();
		fileUploadPendingTimer = null;
	}
	
	private void clearTimers()
	{
		clearIdleTimer();
		clearCountdownTimer();
		cancelSessionExtendTimer();
	}
	
	private void startCountDownTimer()
	{
		int delay = getHideViewItemSeconds();
		if (delay <= 0)
		{
			return;
		}

		ProgressBar pb = getView().getProgressbar();
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
	
	private void updateFileUploadPendingMessage()
	{
	    DateTimeFormat fmt = DateTimeFormat.getFormat("hh:mm:ss a");
	    String msg = "In Progress.. last checked at: " + fmt.format(new Date());
	    getView().getPendinFileUploadInputGroup().setText(msg);
	}

	// fetch the item so that we can get the uploaded filename
	private void fileUploadCompleted()
	{
		cancelFileUploadPendingTimer();
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage("Could not find Item ID in URL");
			return;
		}
		GwtAsyncWrapper<SharedItemDTO> callback = new GwtAsyncWrapper<SharedItemDTO>(this)
		{

			@Override
			public void uponSuccess(SharedItemDTO sharedItemDTO)
			{
				getView().getFileUploadPendingRow().setVisible(false);

				Long documentId = ClientUtils.getDocumentId(sharedItemDTO);
				sDocumentId = documentId;
				String filename = ClientUtils.getDocumentFilename(sharedItemDTO);
				gwtLog(">>>>> Document id: " + documentId);
				gwtLog(">>>>>> Document filename: " + filename);
				getView().getFileDownloadRow().setVisible(false);
				Button downloadButton = getView().getFileDowloadButton();
				downloadButton.setTitle("");
				downloadButton.setText("");
				if (filename != null)
				{
					getView().getFileDownloadRow().setVisible(true);
					// Shorten filename 
					// #74 
					String shortFilename = ClientUtils.shortenFilename(filename, ObidosConstants.SHORTEN_STRING_AFTER_LENGTH);
					downloadButton.setText(shortFilename);
					downloadButton.setTitle(glang.clickToDownload());
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not fetch item to refresh form: " + caught.getMessage());
			}
		
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		gwtLog("MMM caling getSharedItem from fileUploadCompleted");
		ItemService.Utility.getInstance().getSharedItem(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
	}

	private void showFileUploadedRow(final SharedItemDTO sharedItemDTO)
	{
		cancelFileUploadPendingTimer();
		getView().getFileUploadPendingRow().setVisible(false);

		Long documentId = ClientUtils.getDocumentId(sharedItemDTO);
		sDocumentId = documentId;
		String filename = ClientUtils.getDocumentFilename(sharedItemDTO);
		gwtLog(">>>>> Document id: " + documentId);
		gwtLog(">>>>>> Document filename: " + filename);
		getView().getFileDownloadRow().setVisible(false);
		Button downloadButton = getView().getFileDowloadButton();
		downloadButton.setTitle("");
		downloadButton.setText("");
		if (filename != null)
		{
			getView().getFileDownloadRow().setVisible(true);
			// Shorten filename 
			// #74 
			String shortFilename = ClientUtils.shortenFilename(filename, ObidosConstants.SHORTEN_STRING_AFTER_LENGTH);
			downloadButton.setText(shortFilename);
			downloadButton.setTitle(glang.clickToDownload());
		}

		
	}

	private void checkFileUploadPendingStatus(final SharedItemDTO sharedItemDTO, final Long documentId)
	{
		GwtAsyncWrapper<Integer> callback = new GwtAsyncWrapper<Integer>(this)
		{

			@Override
			public void uponSuccess(Integer state)
			{
				gwtLog("XX sate: " + state);
				if (state == DocumentDTO.STATE_PENDING)
				{
					gwtLog("XX Refresh pending status......");
					updateFileUploadPendingMessage();
				}
				else
				{
					gwtLog("XX file upload complete...");
					fileUploadCompleted();
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("XXX Exception: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		DocumentService.Utility.getInstance().getUploadState(authCreds, documentId, callback);
	}	

	private void startFileUploadPendingTimer(SharedItemDTO dto)
	{
		clearFileUploadPendingTimer();
		Long documentId = ClientUtils.getDocumentId(dto);
      	fileUploadPendingTimer = new Timer() {
      		long seconds = 0L;
			int duration = 0;
			@Override
			public void run()
			{
				duration++;
				if (seconds == 0)
				{
					updateFileUploadPendingMessage();
				}
				seconds = seconds + 1;
			    int hours = (int) seconds / 3600;
			    int remainder = (int) seconds - hours * 3600;
			    int mins = remainder / 60;
			    remainder = remainder - mins * 60;
			    int secs = remainder;

			    String hrs = NumberFormat.getFormat("00").format(hours);
			    String minss = NumberFormat.getFormat("00").format(mins);
			    String secss = NumberFormat.getFormat("00").format(secs);
			    
			    String timeString = hrs + ":" + minss + ":" + secss;
			    getView().getPendingTimerLabel().setText(timeString);
			    if (duration >= 10)
			    {
			    	duration = 0;
			    	if (documentId != null)
			    	{
			    		gwtLog("XXX refresh item with doc id: "+ documentId);
			    		checkFileUploadPendingStatus(dto, documentId);
			    	}
			    }
			}
      	};
      	fileUploadPendingTimer.scheduleRepeating(1000);

	}
	// note has a special builtin container, we want to hide them
	// Issue #395
	private void hideContainerNameForNote()
	{
		getView().getContainerNameRowNew().setVisible(true); // #75
		getView().getItemNameLabelNew().setText(glang.itemNameLabel()); // #75
		String itemType = ClientUtils.getItemTypeFromUrl(placeManager);
		String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.NOTEBOOK.equals(itemType))
		{
			getView().getContainerNameRowNew().setVisible(false); // #75
			getView().getContainerSharedWithMeNameRow().setVisible(false);
			getView().getItemNameLabelNew().setText(glang.noteNameLabel()); // #75
		}
		if (ObidosConstants.SHARED_WITH_ME.equals(type) ||
			ObidosConstants.SHARED_WITH_OTHERS.equals(type))
		{
		}
	}

	private void clearForm()
	{
		sItemFormData = null;
		showMessage("");
		getView().getContainerNameTextBoxNew().setValue(null); // #75

		getView().getItemNameTextBoxNew().setValue(null); // #75

		getView().getCreatedAtTextBox().clear();
		getView().getUpdatedAtTextBox().clear();

		getView().getExpiresRow().setVisible(false);

		getView().getExpiresTextBox().setValue(null);
		getView().getExpiresTextBox().setReadOnly(true);
		getView().getFormGroup().clear();
	}

	
	private void visibleSharedByRows(boolean visible)
	{
		getView().getSharedByRow().setVisible(visible);
		getView().getSharedOnRow().setVisible(visible);
		/*
		if (visible)
		{
			getView().getContainerNameRow().setVisible(false);
			getView().getContainerSharedWithMeNameRow().setVisible(visible);
		}
		else
		{
			getView().getContainerNameRow().setVisible(true);
			getView().getContainerSharedWithMeNameRow().setVisible(false);
		}
		*/
	}
	
	private boolean isContainerPrivate(ItemDTO dto)
	{
    	Boolean priv = dto.getIsContainerPrivate();
    	if (priv == null)
    	{
    		priv = true;
    	}
    	return priv;
	}
	
	private void changeTitles()
	{
		String itemType = ClientUtils.getItemTypeFromUrl(placeManager);
		String type = ClientUtils.getTypeFromUrl(placeManager);
		String place = ClientUtils.getPlaceFromUrl(placeManager);

		Heading heading = getView().getPanelHeader().getHeading();
			
		if (ObidosConstants.NOTEBOOK.equals(itemType))
		{
			heading.setText(glang.viewNote());
			if (ObidosConstants.SHARED_WITH_OTHERS.equals(type))
			{
				heading.setText(glang.viewNoteSharedWithOthers());
			}
			else if (ObidosConstants.SHARED_WITH_ME.equals(type))
			{
				heading.setText(glang.viewNoteSharedWithMe());
			}
		}
		else if (ObidosConstants.ITEM.equals(itemType))
		{
			heading.setText(glang.viewItem());
			if (ObidosConstants.SHARED_WITH_OTHERS.equals(type))
			{
				heading.setText(glang.viewItemSharedWithOthers());
			}
			else if (ObidosConstants.SHARED_WITH_ME.equals(type))
			{
				heading.setText(glang.viewItemSharedWithMe());
			}
		}
		else
		{
			heading.setText(glang.viewItem());
		}

		if (NameTokens.NOTIFICATION_MESSAGE.equals(place))
		{
		}
		
		// Issue #494
		if (NameTokens.LIST_ITEMS.equals(place))
		{
		}
		
		if (NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER.equals(place))
		{
		}
	}
	
	private boolean sharedItemIsNote()
	{
		String type = ClientUtils.getItemTypeFromUrl(placeManager);
		if (ObidosConstants.NOTEBOOK.equals(type))
		{
			return true;
		}
		return false;
	}
	
	
	private void updateContainerNameTextBox(final String containerName)
	{
		// Just hide the Container name if the item is shared by someone.
		// Real fix TODO: after #626 is completed
		if (ClientUtils.getOwnerIdFromUrl(placeManager) != null)
		{
			gwtLog("Shared.........................");
			getView().getContainerNameRowNew().setVisible(false);
			getView().getItemShareableLabel().setVisible(false); // #75
		}
		else
		{
			gwtLog("Not shared................");
			getView().getContainerNameRowNew().setVisible(true);
			getView().getItemShareableLabel().setVisible(true); // #75
		}
		if (sharedItemIsNote())
		{
			getView().getContainerNameRowNew().setVisible(false);
		}
		getView().getContainerNameTextBoxNew().setValue(containerName); //#75
	}
	
	private boolean uploadPossiblyFailed(SharedItemDTO dto)
	{
		cancelFileUploadPendingTimer();

		Date sharedOn = dto.getSharedAt();
		Date now = new Date();
		long diffInMillies = Math.abs(now.getTime() - sharedOn.getTime());
		gwtLog("XX Diff: " + diffInMillies);
		String owner = dto.getOwnerFullname();
		long twoHoursMillies = 7200000L;
		
		if (diffInMillies >= twoHoursMillies)
		{
			long secs = diffInMillies / 1000L;
			gwtLog("XX secs: " + secs);

			long hours = secs / 3600L;
			gwtLog("XX hours: " + hours);
			
			String msg = "A file upload as attempted about " + hours + " hours ago and is still in in-progress state. It is possible that the upload has failed.";
			showErrorMessage(msg);
			return true;
		}
		return false;
	}

	
	private void fetchAndPopulateForm()
	{
		if (ClientUtils.getOwnerIdFromUrl(placeManager) == null)
		{
			visibleSharedByRows(false);
		}
		else
		{
			visibleSharedByRows(true);
		}

		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage("Could not find Item ID in URL");
			return;
		}
		
		changeTitles();
		
		GwtAsyncWrapper<SharedItemDTO> callback = new GwtAsyncWrapper<SharedItemDTO>(this)
		{

			@Override
			public void uponSuccess(SharedItemDTO dto)
			{
				gwtLog("File attached? " + dto.containsDocument());
				gwtLog("MMMM owner id: " + dto.getOwnerId());
				Long loggedInUserId = currentUser.getUserDTO().getId();
				Long realOwnerId = dto.getOwnerId();
				gwtLog("MMM logged in id: " + loggedInUserId);

				if (loggedInUserId != null && realOwnerId != null)
				{
					if (realOwnerId.equals(loggedInUserId))
					{
						visibleSharedByRows(false);
					}
				}
				else
				{
					gwtLog("MMM what??");
				}
			
				getView().getTakeOwnershipButton().setVisible(false); // Testing new layout
				getView().getListUsers().setVisible(false);
				
				
				// if logged in user is not the owner and if the owner of the
				// item has granted to take ownership show take ownership control
				// button
				PermissionDTO pdto = dto.getPermissions();
				if (pdto != null)
				{
					gwtLog("Given ownership control? " + pdto.getHasOwnershipControl());
					if (!ClientUtils.loggedInUserIsTheOwner(currentUser, dto))
					{
						if (pdto.getHasOwnershipControl())
						{
							getView().getTakeOwnershipButton().setVisible(true); // Testing new layout with less space between buttons
						}
					}
					if (!ClientUtils.loggedInUserIsTheOwner(currentUser, dto) && ClientUtils.fromBoolean(pdto.getMayUpdate()))
					{
						getView().getListUsers().setVisible(true);
					}
						
				}
				if (dto.getOwnerFullname() != null)
				{
					getView().getOwnerDropDownAnchor().setText(dto.getOwnerFullname());
					gwtLog("Owner id: " + dto.getOwnerId());
					gwtLog("Profile pic? " + dto.getProfilePictureEnabled());
					boolean hasProfilePic = ClientUtils.fromBoolean(dto.getProfilePictureEnabled());
					if (hasProfilePic)
					{
						getView().getOwnerDetails().setIconColor(ObidosConstants.PURPLE_COLOR);
					}
					else
					{
						getView().getOwnerDetails().setIconColor("#000");
					}

					// Email
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
					getView().getContainerNameRowNew().setVisible(false);
				}
				else
				{
					getView().getContainerNameRowNew().setVisible(true);
				}

				gwtLog(">>>> Container name: " + dto.getContainerName());
				gwtLog("Oner id: " + dto.getOwnerId());
				gwtLog("Container is private: " + dto.getIsContainerPrivate());
				gwtLog("Shared count: " + dto.getShareCount());
				gwtLog("Shaerd: " + dto.getShared());
				
				
				Date createdAtDate = dto.getCreatedAt();
				Date updatedAtDate = dto.getUpdatedAt();
				
				
				gwtLog("Container id: " + dto.getcontainerAssignmentId());
				setContainerId(dto.getcontainerAssignmentId());

				// damn it, I like date.toString() very clear
				getView().getItemNameTextBoxNew().setValue(dto.getName()); // #75
				getView().getCreatedAtTextBox().setText(createdAtDate.toString());
				getView().getUpdatedAtTextBox().setText(updatedAtDate.toString());

				// #75 starts 
				FormLabel sl = getView().getContainerShareableLabel();
				if (isContainerPrivate(dto))
				{
					sl.setHTML(glang.privateX());
				}
				else
				{
					sl.setHTML(glang.shareable());
				}
				FormLabel ifl = getView().getItemShareableLabel();
				if (ClientUtils.isItemPrivate(dto))
				{
					ifl.setHTML(glang.privateX());
				}
				else
				{
					ifl.setHTML(glang.shareable());
				}
				// #75 ends
				
				boolean readOnly = true;
				boolean isSecure = true;
				show2FAButtonRow(false);
				sItemFormData = ClientUtils.populateFormWithItem(readOnly, isSecure, dto, getView().getFormGroup(), null, null, null);
				if (sItemFormData.getOthAuthUri() != null)
				{
					show2FAButtonRow(true);
				}
				
				if (dto.isExpirationSet())
				{
					Integer expireDays = dto.getItemExpiration().getDaysUntilExpire();
	    			if (expireDays == null)
	    			{
	    				expireDays = 0;
	    			}
	    			getView().getExpiresRow().setVisible(true);

	    			Date expireDate = dto.getItemExpiration().getExpiresAt();
	    			String bootstrapDateFormat = ClientUtils.getDateFormat(currentUser);
	    			String gwtDateFormat =  ClientUtils.getBootstrap2GwtDateFormat(bootstrapDateFormat);
	    			DateTimeFormat dateFormat = DateTimeFormat.getFormat(gwtDateFormat);
	    			String dateStr = dateFormat.format(expireDate);
	    			gwtLog("Expire date: " + dateStr);
	    			
	    			DateTimeFormat timeFormat = DateTimeFormat.getFormat(ObidosConstants.TIME_FORMAT);
	    			String timeStr = timeFormat.format(expireDate);
	    			gwtLog("Expire time: " + timeStr);

	    			getView().getExpiresTextBox().setValue(dateStr);
	    			getView().getTimeTextBox().setValue(timeStr);
				}
				else
				{
					getView().getExpiresRow().setVisible(false);
				}
				
				// Issue #811
				boolean pending = ClientUtils.isFileUploadPending(dto);
				if (pending)
				{
					// if item was shared 2 hours ago but upload is still pending,
					// assume upload has failed
					if (uploadPossiblyFailed(dto))
					{
						return;
					}
					
					getView().getFileDownloadRow().setVisible(false);
					// start the timer, refresh item at 10 secs
					startFileUploadPendingTimer(dto);
					getView().getFileUploadPendingRow().setVisible(true);
				}
				else
				{
					// Bug #100, I'm not sure why the following call was made
					// Jan-26-2025

					if (ClientUtils.itemHasDocument(dto))
					{
						fileUploadCompleted();
					}
				}
				updateContainerNameTextBox(dto.getContainerName());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				try
				{
					throw caught;
				}
				catch(NoSuchRecordException e)
				{
					showMainHemlPanel(false);
					ClientUtils.showAlertDialogWithCallback(glang.error(), e.getMessage(),
							()->ClientUtils.goBack(placeManager));
					
					return;
				}
				catch (Throwable e)
				{
					gwtLog("EEE: " + e.getMessage());
				}
				gwtLog("Exception: " + caught.getMessage());
				showErrorMessage("ERROR: " + caught.getMessage());
			}
			
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		gwtLog("MMM caling getSharedItem from fetchAndPopulateForm");
		ItemService.Utility.getInstance().getSharedItem(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
	}
	protected <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
	}


	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}

	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}



	@Override
	public void showHideItem()
	{
		int delay = getHideViewItemSeconds();
		if (delay < 0)
		{
			delay = 0;
		}

		getView().getProgressbar().setPercent(100);
		getView().getProgressbar().setType(ProgressBarType.SUCCESS);
		Row row = getView().getContentRow();
		boolean visible = row.isVisible();
		row.setVisible(!visible);
		visible = row.isVisible();

		Button button = getView().getShowHideButton();
		button.setFocus(false);
		if (visible)
		{
			button.setType(ButtonType.DANGER);
			button.setIcon(IconType.EYE_SLASH);
			button.setText(ObidosMessages.LANG.hide());
			setHideButtonToolTip();
			cancelTimers();
			startTimers();
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
			cancelTimers();
			checkBox.setText(ObidosMessages.LANG.off());
			resetProgressBar();
			button.setTitle(null);
			return;
		}
		else
		{
			setHideButtonToolTip();
			checkBox.setText(hideTime);
			startTimers();
		}
	}
	private void showSelectContainerPage()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage(ObidosMessages.LANG.couldNotGetItemId());
			return;
		}
		Long ownerId = ClientUtils.getOwnerIdFromUrl(placeManager);
		
		String nameToken = NameTokens.SELECT_CONTAINER;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.ITEM_ID, itemId.toString());
        if (ownerId != null)
        {
        	with.put(ObidosConstants.OWNERID, ownerId.toString());
        }
        String place = NameTokens.VIEW_ITEM;
        ClientUtils.addPlace(placeManager, with, place);
        ClientUtils.showPage(placeManager, nameToken, with);

	}
	@Override
	public void takeOwnership()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
		if (ClientUtils.itIsASharedItem(placeManager))
		{
			showSelectContainerPage();
			return;
		}
    	ObidosMessages lang = ObidosMessages.LANG;
		String title = lang.takeOwnership();
		String message = lang.takeOwnershipDialogMessage();
		ClientUtils.promptForAction(() -> takeOwnershipReal(), title, message);
	}

	public void takeOwnershipReal()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			showErrorMessage("Could not find " + ObidosConstants.ITEM_ID + " from URL");
			return;
		}
		
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				Date d = new Date();
				showMessage(ObidosMessages.LANG.ownershipOfItemAquired() + " on " + d.toString());
//				getView().getTakeOwnershipButton().setVisible(false);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(ObidosMessages.LANG.couldNotTakeOwnershipOfItem() + ":" + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		Long containerId = null; // TODO Prompt for the container
		ItemService.Utility.getInstance().takeOwnership(authCreds, itemId, containerId, null, callback);
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
	public void editItem()
	{
		Long itemId = ClientUtils.getItemIdFromUrl(placeManager);
		if (itemId == null)
		{
			return;
		}
		String nameToken = NameTokens.EDIT_ITEM;
        Map<String,String> with = new HashMap<>();
//		http://127.0.0.1:8888/index.html#EDIT_ITEM;action=edit;itemId=2052344232949316097;place=LIST_ALL_MY_ITEMS
        with.put(ObidosConstants.ACTION, ObidosConstants.EDIT);
        with.put(ObidosConstants.ITEM_ID, itemId.toString());
        ClientUtils.showPage(placeManager, nameToken, with);
        
	}

	@Override
	public void emailOwner()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callOwner()
	{
		// TODO Auto-generated method stub
		
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

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

	@Override
	public void showContainerPage()
	{
		Long containerId = getContainerId();
		if (containerId == null)
		{
			gwtLog("Container id  is null");
			return;
		}
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        ClientUtils.showPage(placeManager, NameTokens.LIST_ITEMS, with);
	}
	
	// Ref: https://stackoverflow.com/questions/7563791/is-it-possible-to-download-a-file-with-http-post?lq=1
	private native void downloadWithPostJSNI(String url) /*-{
		var mapForm = document.createElement("form");
		mapForm.target ="_self"||"_blank";
		mapForm.id="stmtForm";
		mapForm.method = "POST";
		mapForm.action = url;

		var mapInput = document.createElement("input");
		mapInput.type = "hidden";
		mapInput.name = "Data";
		mapForm.appendChild(mapInput);
		document.body.appendChild(mapForm);

		mapForm.submit()
	}-*/;

	private void downloadWithPOSTGWT(String url)
	{
		startSessionExtendTimer();
		FormPanel form = getView().getFormPanel();
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(url);
		form.submit();
	}

	@Override
	public void downloadFile()
	{
//		String url = GWT.getHostPageBaseURL() + "download?username=" + username + "&passphrase=" + passphrase + "&file=" + dto.getFileName();
//		gwtLog("URL: " + url);
//		Window.open(url, "_blank", "");
		Boolean isNote = false;
		String itemType = ClientUtils.getItemTypeFromUrl(placeManager);
		if (ObidosConstants.NOTEBOOK.equals(itemType))
		{
			isNote = true;
		}
		
		// saved during form population
		Long documentId = sDocumentId;
		if (documentId == null)
		{
			gwtLog("No document id in the item... will not try to download..");
			return;
		}
		gwtLog("Download document; " + documentId);
		
		GwtAsyncWrapper<LimitedFernetDTO> callback = new GwtAsyncWrapper<LimitedFernetDTO>(this)
		{

			@Override
			public void uponSuccess(LimitedFernetDTO dto)
			{
				StringBuilder sb = new StringBuilder(256);
				sb.append("token=")
				  .append(dto.getToken());
				String parameters = sb.toString();
				gwtLog("Parameters: " + parameters);

				gwtLog("Key: " + dto.getToken());
				String url = GWT.getHostPageBaseURL() + "download?" + parameters;
//				Window.open(url, "_blank", "");
				// Use POST method as the Fernet token can have large payload
				// if needed. 
				//downloadWithPostJSNI(url);
				gwtLog("XXX URL: " + url);
				downloadWithPOSTGWT(url);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get security token to download file: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		FernetService.Utility.getInstance().getFileDownloadFernetToken(authCreds, documentId, ObidosConstants.ACTION_TYPE_NA, callback);
	}

	@Override
	public void gwtFileDownloadCompleteCallback(SubmitCompleteEvent event)
	{
		String res = event.getResults();
		if (res != null)
		{
			res = res.trim();
		}
		gwtLog("gwtFileDownloadCompleteCallback: File download completed: results: " + event.getResults());

		// FF returns <pre></pre> if there is no error
		if (res != null && res.length() > 0 && ! "<pre></pre>".equals(res))
		{
			if (res.startsWith("<"))
			{
				int start = res.indexOf(">") + 1;
				int end = res.indexOf("<", start);
				res = res.substring(start, end);
			}
			showErrorMessage(res);
		}
	}
	
	private void addFileDownloadHandlers()
	{
		FormPanel formPanel = getView().getFormPanel();
		formPanel.addSubmitHandler(new SubmitHandler()
		{
			
			@Override
			public void onSubmit(SubmitEvent event)
			{
				gwtLog(">>>>>>>>>>>>>> XX on Submit");
			}
		});

        formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event)
			{
				gwtLog("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				gwtLog("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				gwtLog("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				gwtLog("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				//int fileSize = getFileSize(getView().getFileUpload().getElement());
				gwtLog("Event: " + event);
				gwtLog("Event results: " + event.getResults());
				String results = event.getResults();
				// FF returns <pre></pre> if there is no error
				if (results != null && results.length() > 0 && ! "<pre></pre>".equals(results))
				{
					showErrorMessage("Could not Download file: '" + event.getResults() + "'");
				}
				else
				{
					showMessage("File downloaded...");

				}
			}
		});
	}
	
	private void show2FAButtonRow(boolean visible)
	{
		getView().getShow2FACodeButtonRow().setVisible(visible);
	}

	@Override
	public void show2FAButtonCallback()
	{
		String otpAuthUri = sItemFormData.getOthAuthUri();
		if (otpAuthUri == null)
		{
			showErrorMessage("Cannot show 2FA Code: Could  not find othAuthUri");
			return;
		}
		TwoFAModalData md = ApplicationPresenter.getTwoFAModalData();
		md.clear();
		md.setOtpAuthUri(otpAuthUri);
		ClientUtils.showTwoFACode(placeManager, md);
	}
	
	private void startSessionExtendTimer()
	{
		cancelSessionExtendTimer();
		// Extend session at every 5 minutes
		int delayMillies = 5 * 60 * 1000;
		Timer t = sessionExtendTimer;
		if (t != null)
		{
			t.cancel();
			t.schedule(delayMillies);
		}
		else
		{
			sessionExtendTimer = new Timer() {

				@Override
				public void run()
				{
					ClientUtils.extendSession(placeManager);
//					Date d = new Date();
//					showMessage("Session extended on: " + d);
//					showMessage("Session extended on: " + d);
				}
			};
		}
		sessionExtendTimer.scheduleRepeating(delayMillies);
	}

	public void cancelSessionExtendTimer()
	{
		if (sessionExtendTimer != null)
		{
			sessionExtendTimer.cancel();
			sessionExtendTimer = null;
		}
	}
}
