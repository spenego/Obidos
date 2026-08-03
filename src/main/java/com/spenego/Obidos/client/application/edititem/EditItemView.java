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

import javax.inject.Inject;

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
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.wisepersist.gwt.uploader.client.Uploader;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;

class EditItemView extends ViewWithUiHandlers<EditItemUiHandlers> implements EditItemPresenter.MyView
{
	interface Binder extends UiBinder<Widget, EditItemView>
	{
	}

	@UiField
	Row itemRow;

	@UiField
	BlockQuote helpBlockQuote;

	@UiField
	ObidosPanelHeader panelHeader;

	@UiField
	Button updateButton;

	@UiField
	Button resetButton;

	@UiField
	InlineRadio publicItemRadio;

	@UiField
	InlineRadio privateItemRadio;

	@UiField
	FormGroup formGroup;

	@UiField
	FormLabel sharedByNameLabel;

	@UiField
	DropDown ownerDropDown;

	@UiField
	FormLabel sharedOnLabel;

	@UiField
	InputGroup sharedOnInputGroup;

	@UiField
	TextBox sharedOnTextBox;

	@UiField
	Button ownerDropDownAnchor;

	@UiField
	Button grantPermissionsButton;

	@UiField
	Button showHideButton;

	@UiField
	Row sharedByRow;

	@UiField
	Row sharedOnRow;

	@UiField
	Row radioRow;

	@UiField
	Row hideItemCheckBoxRow;

	@UiField
	CheckBox hideItemCheckBox;

	@UiField
	ProgressBar progressbar;

	@UiField
	Row buttonsRow;

	@UiField
	ObidosButtonToolBar topToolBar;

	@UiField
	ObidosButtonToolBar bottomToolBar;

	@UiField
	ObidosMessageRow messageRow;

	@UiField
	Row expiresRow;

	@UiField
	DatePicker datePicker;
	
	@UiField
	Button sharedWithUsersButton;

	@UiField
	AnchorListItem ownerDetails;
	
	@UiField
	Anchor emailAnchor;
	
	@UiField
	Anchor phoneAnchor;

	@UiField
	ObidosTimeBox timeBox;
	
	@UiField
	InlineCheckBox clearExpirationCheckbox;
	
	@UiField
	ObidosRowBottom2px docFilenameRow;

	@UiField
	ObidosTextBox docFilenameTextBox;

	@UiField
	ObidosRowBottom2px uploadRow;

	@UiField
	FormLabel relpaceDocLabel;
	
	@UiField
	Uploader wiseUploader;

    @UiField
    Span fileChosenSpan;

	@UiField
	ObidosRowBottom2px languageRow;

	@UiField
	ListBox languageListBox;
	
	// Bug #75
	@UiField
	ObidosRowBottom2px containerNameRowNew;

	@UiField
	ObidosTextBox containerNameTextBoxNew;

	@UiField
	FormLabel containerShareableLabel;

	@UiField
	FormLabel itemNameLabelNew;

	@UiField
	ObidosTextBox itemNameTextBoxNew;

	@UiField
	FormLabel itemShareableLabel;
	// Bug #75

	@Inject
	EditItemView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		
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
				getUiHandlers().showListItemsPage();
			}
		});
	}

	@UiHandler("updateButton")
	void onClickUpdateButton(ClickEvent e)
	{
		boolean goback = false;
		getUiHandlers().updateItem(goback);
	}

	@UiHandler("resetButton")
	void onclickResetButton(ClickEvent e)
	{
		getUiHandlers().reset();
	}

	@UiHandler("grantPermissionsButton")
	void onclickGrantPermissinoButton(ClickEvent e)
	{
		getUiHandlers().showGrantPermissionsToUsersView();
	}

	@UiHandler("publicItemRadio")
	void onclickPublicRadio(ClickEvent e)
	{
		getUiHandlers().publicRadioCallback();
	}

	@UiHandler("privateItemRadio")
	void onclickPrivateRadio(ClickEvent e)
	{
		getUiHandlers().privateRadioCallback();
	}

	@UiHandler("showHideButton")
	void onclickShowHideButton(ClickEvent e)
	{
		getUiHandlers().showHideItem();
	}

	@UiHandler("hideItemCheckBox")
	void onclickHideItemCheckBox(ClickEvent e)
	{
		getUiHandlers().hideItemCheckBoxCallback();
	}
	
	@UiHandler("sharedWithUsersButton")
	void onclickSharedWithUsersButton (ClickEvent e)
	{
		getUiHandlers().listUsersItemIsSharedWithMe();
	}

	@UiHandler("ownerDetails")
	void onclick (ClickEvent e)
	{
        getUiHandlers().showOwnerDetailsPage();
	}
    
    @UiHandler("languageListBox")
    void onChablgeLanguageListBox(ChangeEvent e)
    {
    	getUiHandlers().languageListBoxCallback();
    }

	public Button getUpdateButton()
	{
		return updateButton;
	}

	public Button getResetButton()
	{
		return resetButton;
	}

	public Button getListButton()
	{
		return null;
	}

	public Button getHelpButton()
	{
		return null;
	}

	public InlineRadio getPublicItemRadio()
	{
		return publicItemRadio;
	}

	public InlineRadio getPrivateItemRadio()
	{
		return privateItemRadio;
	}

	public FormGroup getFormGroup()
	{
		return formGroup;
	}

	public Heading getPanelHeading()
	{
		return null;
	}

	public FormLabel getSharedByNameLabel()
	{
		return sharedByNameLabel;
	}

	public DropDown getOwnerDropDown()
	{
		return ownerDropDown;
	}

	public Button getOwnerDropDownAnchor()
	{
		return ownerDropDownAnchor;
	}

	public Button getGrantPermissionsButton()
	{
		return grantPermissionsButton;
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public FormLabel getSharedOnLabel()
	{
		return sharedOnLabel;
	}

	public InputGroup getSharedOnInputGroup()
	{
		return sharedOnInputGroup;
	}

	public TextBox getSharedOnTextBox()
	{
		return sharedOnTextBox;
	}

	public Row getItemRow()
	{
		return itemRow;
	}

	public Button getShowHideButton()
	{
		return showHideButton;
	}

	public Row getSharedByRow()
	{
		return sharedByRow;
	}

	public Row getSharedOnRow()
	{
		return sharedOnRow;
	}

	public Row getRadioRow()
	{
		return radioRow;
	}

	public Row getHideItemCheckBoxRow()
	{
		return hideItemCheckBoxRow;
	}

	public CheckBox getHideItemCheckBox()
	{
		return hideItemCheckBox;
	}

	public ProgressBar getProgressbar()
	{
		return progressbar;
	}

	public Row getButtonsRow()
	{
		return buttonsRow;
	}

	public ObidosButtonToolBar getBottomToolBar()
	{
		return bottomToolBar;
	}

	public ObidosButtonToolBar getTopToolBar()
	{
		return topToolBar;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public DatePicker getDatePicker()
	{
		return datePicker;
	}

	public Row getExpiresRow()
	{
		return expiresRow;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Button getSharedWithUsersButton()
	{
		return sharedWithUsersButton;
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

	public ObidosTimeBox getTimeBox()
	{
		return timeBox;
	}

	public InlineCheckBox getClearExpirationCheckbox()
	{
		return clearExpirationCheckbox;
	}

	public ObidosRowBottom2px getDocFilenameRow()
	{
		return docFilenameRow;
	}

	public ObidosTextBox getDocFilenameTextBox()
	{
		return docFilenameTextBox;
	}

	public ObidosRowBottom2px getUploadRow()
	{
		return uploadRow;
	}

	public FormLabel getRelpaceDocLabel()
	{
		return relpaceDocLabel;
	}

	public Uploader getWiseUploader()
	{
		return wiseUploader;
	}

	public Span getFileChosenSpan()
	{
		return fileChosenSpan;
	}

	public ListBox getLanguageListBox()
	{
		return languageListBox;
	}

	public ObidosRowBottom2px getLanguageRow()
	{
		return languageRow;
	}

	// #75
	public ObidosTextBox getContainerNameTextBoxNew()
	{
		return containerNameTextBoxNew;
	}

	public FormLabel getContainerShareableLabel()
	{
		return containerShareableLabel;
	}

	public ObidosRowBottom2px getContainerNameRowNew()
	{
		return containerNameRowNew;
	}

	public FormLabel getItemNameLabelNew()
	{
		return itemNameLabelNew;
	}

	public ObidosTextBox getItemNameTextBoxNew()
	{
		return itemNameTextBoxNew;
	}

	public FormLabel getItemShareableLabel()
	{
		return itemShareableLabel;
	}

	// #75
}
