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

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroupAddon;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;

class ViewItemView extends ViewWithUiHandlers<ViewItemUiHandlers> implements ViewItemPresenter.MyView
{
	interface Binder extends UiBinder<Widget, ViewItemView>
	{
	}
	
	@UiField
	HTMLPanel htmlPanel;

	@UiField
	Row contentRow;
	
	@UiField
	FormGroup mainFormGroup;
	
	@UiField
	BlockQuote helpBlockQuote;
	
	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	Row sharedByRow;
	
	@UiField
	Row sharedOnRow;

	@UiField
	Button ownerDropDownAnchor;
	

	@UiField
	Row expiresRow;
	
	@UiField
	TextBox expiresTextBox;

	@UiField
	ObidosTextBox timeTextBox;
	
	@UiField
	ObidosMessageRow messageRow;


	@UiField
	FormGroup formGroup;
	
	@UiField
	TextBox sharedOnTextBox;
	
	@UiField
	Button showHideButton;

	@UiField
	Row hideItemCheckBoxRow;
	
	@UiField
	CheckBox hideItemCheckBox;
	
	@UiField
	Button takeOwnershipButton;
	
	@UiField
	ProgressBar progressbar;
	
	@UiField
    Button listUsers;
	
	@UiField
	Button editButton;
	
	@UiField
	AnchorListItem ownerDetails;
	
	@UiField
	Anchor emailAnchor;
	
	@UiField
	Anchor phoneAnchor;
	
	@UiField
	ObidosButtonToolBar buttonToolBar;
	
	@UiField
	ObidosRowBottom2px containerSharedWithMeNameRow;

	@UiField
	ObidosTextBox containerSharedWithMeNameTextBox;

	@UiField
	ObidosInputGroupAddon containerSharedWithMeTypeAddon;
	
	@UiField
	FormPanel formPanel;

	@UiField
	ObidosRowBottom2px fileDownloadRow;

	@UiField
	Button fileDowloadButton;

	@UiField
	ObidosRowBottom2px show2FACodeButtonRow;

	@UiField
	Button show2FACodeButton;

	@UiField
	Row fileUploadPendingRow;
	
	@UiField
	Label pendingTimerLabel;

	@UiField
	ObidosInputGroup pendinFileUploadInputGroup;

	@UiField
	ObidosTextBox createdAtTextBox;

	@UiField
	ObidosTextBox updatedAtTextBox;

	@UiField
	ObidosRowBottom2px updatedAtRow;

	// Bug #75 starts
	@UiField
	ObidosRowBottom2px containerNameRowNew;

	@UiField
	ObidosTextBox containerNameTextBoxNew;

	@UiField
	FormLabel containerShareableLabel;
	
	@UiField
	FormLabel itemNameLabelNew;

	@UiField
	FlowPanel itemFlowPanelNew;

	@UiField
	ObidosTextBox itemNameTextBoxNew;

	@UiField
	FormLabel itemShareableLabel;
	// Bug #75 ends

	@Inject
	ViewItemView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		
		pendinFileUploadInputGroup.setIcon(IconType.SPINNER);
		pendinFileUploadInputGroup.animateIcon(true);
		
		helpButton.addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				getUiHandlers().help();
				
			}
		});
		
		backButton.addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				getUiHandlers().back();
			}
		});
	}
	
	@UiHandler("showHideButton")
	void onclickShowHideButton (ClickEvent e)
	{
		getUiHandlers().showHideItem();
	}
	
	@UiHandler("hideItemCheckBox")
	void onclickHideItemCheckBox (ClickEvent e)
	{
		getUiHandlers().hideItemCheckBoxCallback();
	}
	
	@UiHandler("takeOwnershipButton")
	void onclickTakeOwnershipButton (ClickEvent e)
	{
		getUiHandlers().takeOwnership();
	}
	
    @UiHandler("listUsers")
    void onclickListUsers (ClickEvent e)
    {
        getUiHandlers().listUsersItemIsSharedWithMe();
    }

	@UiHandler("ownerDetails")
	void onclick (ClickEvent e)
	{
        getUiHandlers().showOwnerDetailsPage();
	}
	
    
    @UiHandler("editButton")
    void onclickEditITem (ClickEvent e)
	{
    	getUiHandlers().editItem();
	}
    
    @UiHandler("containerNameTextBoxNew")
    void onclickContainerNameTextBoxNew (ClickEvent e)
	{
    	getUiHandlers().showContainerPage();
	}
    
    // Test file download --
    @UiHandler("fileDowloadButton")
    void onclickFileDowloadButton (ClickEvent e)
	{
    	getUiHandlers().downloadFile();
	}

   	@UiHandler("show2FACodeButton")
   	void onClickShow2FACodeButton(ClickEvent e)
   	{
   		getUiHandlers().show2FAButtonCallback();
   	}
   	
   	@UiHandler("pendingTimerLabel")
   	void onClickpendingTimerLabel(ClickEvent e)
   	{
   		GWT.log("Label clicked...");
   	}
   	
	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}
	

	public Row getSharedByRow()
	{
		return sharedByRow;
	}


	public Row getSharedOnRow()
	{
		return sharedOnRow;
	}

	public FormGroup getFormGroup()
	{
		return formGroup;
	}

	public Button getOwnerDropDownAnchor()
	{
		return ownerDropDownAnchor;
	}


	public Row getExpiresRow()
	{
		return expiresRow;
	}

	public TextBox getSharedOnTextBox()
	{
		return sharedOnTextBox;
	}

	public FormGroup getMainFormGroup()
	{
		return mainFormGroup;
	}

	public Button getShowHideButton()
	{
		return showHideButton;
	}

	public Row getHideItemCheckBoxRow()
	{
		return hideItemCheckBoxRow;
	}

	public CheckBox getHideItemCheckBox()
	{
		return hideItemCheckBox;
	}

	public Button getTakeOwnershipButton()
	{
		return takeOwnershipButton;
	}
	public ProgressBar getProgressbar()
	{
		return progressbar;
	}

	public Button getListUsers()
	{
		return listUsers;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Row getContentRow()
	{
		return contentRow;
	}

	public TextBox getExpiresTextBox()
	{
		return expiresTextBox;
	}

	public Button getEditButton()
	{
		return editButton;
	}

	public AnchorListItem getOwnerDetails()
	{
		return ownerDetails;
	}

	public Anchor getEmailAnchor()
	{
		return emailAnchor;
	}

	public Anchor getPhoneAnchor()
	{
		return phoneAnchor;
	}

	public ObidosButtonToolBar getButtonToolBar()
	{
		return buttonToolBar;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosRowBottom2px getContainerSharedWithMeNameRow()
	{
		return containerSharedWithMeNameRow;
	}

	public ObidosTextBox getContainerSharedWithMeNameTextBox()
	{
		return containerSharedWithMeNameTextBox;
	}

	public ObidosInputGroupAddon getContainerSharedWithMeTypeAddon()
	{
		return containerSharedWithMeTypeAddon;
	}

	public HTMLPanel getHtmlPanel()
	{
		return htmlPanel;
	}

	public ObidosTextBox getTimeTextBox()
	{
		return timeTextBox;
	}

	public FormPanel getFormPanel()
	{
		return formPanel;
	}

	public ObidosRowBottom2px getFileDownloadRow()
	{
		return fileDownloadRow;
	}

	public Button getFileDowloadButton()
	{
		return fileDowloadButton;
	}

	public ObidosRowBottom2px getShow2FACodeButtonRow()
	{
		return show2FACodeButtonRow;
	}

	public Button getShow2FACodeButton()
	{
		return show2FACodeButton;
	}

	public Row getFileUploadPendingRow()
	{
		return fileUploadPendingRow;
	}

	public Label getPendingTimerLabel()
	{
		return pendingTimerLabel;
	}

	public ObidosInputGroup getPendinFileUploadInputGroup()
	{
		return pendinFileUploadInputGroup;
	}

	public ObidosTextBox getCreatedAtTextBox()
	{
		return createdAtTextBox;
	}

	public ObidosTextBox getUpdatedAtTextBox()
	{
		return updatedAtTextBox;
	}

	public ObidosRowBottom2px getUpdatedAtRow()
	{
		return updatedAtRow;
	}
	// Bug #75 starts
	public ObidosRowBottom2px getContainerNameRowNew()
	{
		return containerNameRowNew;
	}

	public ObidosTextBox getContainerNameTextBoxNew()
	{
		return containerNameTextBoxNew;
	}

	public FormLabel getContainerShareableLabel()
	{
		return containerShareableLabel;
	}

	public FormLabel getItemNameLabelNew()
	{
		return itemNameLabelNew;
	}

	public FlowPanel getItemFlowPanelNew()
	{
		return itemFlowPanelNew;
	}

	public ObidosTextBox getItemNameTextBoxNew()
	{
		return itemNameTextBoxNew;
	}

	public FormLabel getItemShareableLabel()
	{
		return itemShareableLabel;
	}
	
	// Bug #75 ends
}
