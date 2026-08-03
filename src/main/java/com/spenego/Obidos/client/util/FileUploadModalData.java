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
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.wisepersist.gwt.uploader.client.Uploader;

import com.google.gwt.user.client.Timer;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;

public class FileUploadModalData
{
	private Modal 				modal;
	private Uploader            wiseUploader;
	private PlaceManager        placeManager;
	private TextBox       		filenameTextBox;
	private ProgressBar         progressBar;
	private TextBox             averageSpeedTextBox;
	private TextBox             bytesUploadedTextBox;
	private TextBox             secsElapsedTextBox;
	private TextBox             secsRemainingTextBox;
	private TextBox             filesizeTextBox;
	
	private ObidosMessageRow    messageRow;
	private ObidosMessageRow    parentMessageRow; // main window
	private String              filename;
	private Span                fileChosenSpan;
	private Timer               sessionExtendTimer;
	private Button              cancelCloseButton;
	private boolean             uploadCancelled;
	private Button              uploadingSpinnerButton;
	private String              place;
	private ObidosTextBox       documentFilenameTextBox;

	private ObidosRowBottom2px  uploadRow;
	private ToggleSwitch        attachmentSwitch;
	private boolean             qrcodeImageDecoded;
	

	private ObidosMessages glang = ObidosMessages.LANG;

	public Modal getModal()
	{
		return modal;
	}
	public void setModal(Modal modal)
	{
		this.modal = modal;
	}

	public ProgressBar getProgressBar()
	{
		return progressBar;
	}
	public void setProgressBar(ProgressBar progressBar)
	{
		this.progressBar = progressBar;
	}
	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}
	public void setMessageRow(ObidosMessageRow messageRow)
	{
		this.messageRow = messageRow;
	}
	public TextBox getFilenameTextBox()
	{
		return filenameTextBox;
	}
	public void setFilenameTextBox(TextBox filenameTextBox)
	{
		this.filenameTextBox = filenameTextBox;
	}
	public String getFilename()
	{
		return filename;
	}
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	public TextBox getAverageSpeedTextBox()
	{
		return averageSpeedTextBox;
	}
	public void setAverageSpeedTextBox(TextBox averageSpeedTextBox)
	{
		this.averageSpeedTextBox = averageSpeedTextBox;
	}

	public TextBox getBytesUploadedTextBox()
	{
		return bytesUploadedTextBox;
	}
	public void setBytesUploadedTextBox(TextBox bytesUploadedTextBox)
	{
		this.bytesUploadedTextBox = bytesUploadedTextBox;
	}
	public TextBox getSecsElapsedTextBox()
	{
		return secsElapsedTextBox;
	}
	public void setSecsElapsedTextBox(TextBox secsElapsedTextBox)
	{
		this.secsElapsedTextBox = secsElapsedTextBox;
	}
	public TextBox getSecsRemainingTextBox()
	{
		return secsRemainingTextBox;
	}
	public void setSecsRemainingTextBox(TextBox secsRemainingTextBox)
	{
		this.secsRemainingTextBox = secsRemainingTextBox;
	}
	public TextBox getFilesizeTextBox()
	{
		return filesizeTextBox;
	}
	public void setFilesizeTextBox(TextBox filesizeTextBox)
	{
		this.filesizeTextBox = filesizeTextBox;
	}
	public Uploader getWiseUploader()
	{
		return wiseUploader;
	}
	public void setWiseUploader(Uploader wiseUploader)
	{
		this.wiseUploader = wiseUploader;
	}
	public PlaceManager getPlaceManager()
	{
		return placeManager;
	}
	public void setPlaceManager(PlaceManager placeManager)
	{
		this.placeManager = placeManager;
	}
	public ObidosMessageRow getParentMessageRow()
	{
		return parentMessageRow;
	}
	public void setParentMessageRow(ObidosMessageRow parentMessageRow)
	{
		this.parentMessageRow = parentMessageRow;
	}
	public Timer getSessionExtendTimer()
	{
		return sessionExtendTimer;
	}
	public void setSessionExtendTimer(Timer sessionExtendTimer)
	{
		this.sessionExtendTimer = sessionExtendTimer;
	}
	public void startSessionExtendTimer()
	{
		cancelSessionExtendTimer();
		// Extend session at every t minute
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
	
	public void showMessage(final String msg)
	{
		ObidosMessageRow messageRow = getMessageRow();
		if (messageRow != null)
		{
			messageRow.showMessage(msg);
		}
	}

	public void showErrorMessage(final String msg)
	{
		ObidosMessageRow messageRow = getMessageRow();
		if (messageRow != null)
		{
			messageRow.showErrorMessage(msg);
		}
	}

	public void showMessageToParnetWindow(final String msg)
	{
		ObidosMessageRow messageRow = getParentMessageRow();
		if (messageRow != null)
		{
			messageRow.showMessage(msg);
		}
	}

	public void showErrorMessageToParnetWindow(final String msg)
	{
		ObidosMessageRow messageRow = getParentMessageRow();
		if (messageRow != null)
		{
			messageRow.showErrorMessage(msg);
		}
	}
	
	public void clear()
	{
		cancelSessionExtendTimer();
		getCancelCloseButton().setType(ButtonType.DANGER);
		getCancelCloseButton().setText(glang.cancelUpload());
		getUploadingSpinnerButton().setVisible(true);
		setUploadCancelled(false);
	}

	public void clearMessage()
	{
		ObidosMessageRow messageRow = getMessageRow();
		if (messageRow != null)
		{
			messageRow.clear();
		}
	}
	public Button getCancelCloseButton()
	{
		return cancelCloseButton;
	}
	public void setCancelCloseButton(Button cancelCloseButton)
	{
		this.cancelCloseButton = cancelCloseButton;
	}
	public boolean isUploadCancelled()
	{
		return uploadCancelled;
	}
	public void setUploadCancelled(boolean uploadCancelled)
	{
		this.uploadCancelled = uploadCancelled;
	}
	public Span getFileChosenSpan()
	{
		return fileChosenSpan;
	}
	public void setFileChosenSpan(Span fileChosenSpan)
	{
		this.fileChosenSpan = fileChosenSpan;
	}
	public Button getUploadingSpinnerButton()
	{
		return uploadingSpinnerButton;
	}
	public void setUploadingSpinnerButton(Button uploadingSpinnerButton)
	{
		this.uploadingSpinnerButton = uploadingSpinnerButton;
	}
	public ObidosTextBox getDocumentFilenameTextBox()
	{
		return documentFilenameTextBox;
	}
	public void setDocumentFilenameTextBox(ObidosTextBox documentFilenameTextBox)
	{
		this.documentFilenameTextBox = documentFilenameTextBox;
	}
	public String getPlace()
	{
		return place;
	}
	public void setPlace(String place)
	{
		this.place = place;
	}
	public ObidosRowBottom2px getUploadRow()
	{
		return uploadRow;
	}
	public void setUploadRow(ObidosRowBottom2px uploadRow)
	{
		this.uploadRow = uploadRow;
	}
	public ToggleSwitch getAttachmentSwitch()
	{
		return attachmentSwitch;
	}
	public void setAttachmentSwitch(ToggleSwitch attachmentSwitch)
	{
		this.attachmentSwitch = attachmentSwitch;
	}
	public boolean isQrcodeImageDecoded()
	{
		return qrcodeImageDecoded;
	}
	public void setQrcodeImageDecoded(boolean qrcodeImageDecoded)
	{
		this.qrcodeImageDecoded = qrcodeImageDecoded;
	}
}
