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

package com.spenego.Obidos.client.application.edittemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

/**
 * A class to keep hold of template fields in the current session in the UI. It
 * gets cleared if the user leaves the page
 * 
 * @author spgdev@spenego.com - May 26, 2018
 */
class TemplateForm
{
	private List<TextBox> nameTextBoxes;
	private ArrayList<ObidosIntegerTextBox> positionTextBoxes;
	private Map<TextBox, UserDefinedFieldDTO> fieldNameDTOMap;
	private UserDefinedTypeDTO userDefinedTypeDTO;

	private ArrayList<TextBox> newNameTextBoxes;
	private ArrayList<ObidosIntegerTextBox> newPositionTextBoxes;

	TemplateForm()
	{
		nameTextBoxes = new ArrayList<>();
		positionTextBoxes = new ArrayList<>();
		fieldNameDTOMap = new HashMap<>();
		userDefinedTypeDTO = null;
		newNameTextBoxes = new ArrayList<>();
		newPositionTextBoxes = new ArrayList<>();
	}

	public List<TextBox> getNameTextBoxes()
	{
		return nameTextBoxes;
	}

	public void setNameTextBoxes(ArrayList<TextBox> nameTextBoxes)
	{
		this.nameTextBoxes = nameTextBoxes;
	}

	public void addNameTextBox(final TextBox textBox)
	{
		if (nameTextBoxes == null)
		{
			nameTextBoxes = new ArrayList<TextBox>();
		}
		nameTextBoxes.add(textBox);
	}

	public TextBox getNameTextBox(final int index)
	{
		return nameTextBoxes.get(index);
	}

	public ArrayList<ObidosIntegerTextBox> getPositionTextBoxes()
	{
		return positionTextBoxes;
	}

	public void setPositionTextBoxes(ArrayList<ObidosIntegerTextBox> positionTextBoxes)
	{
		this.positionTextBoxes = positionTextBoxes;
	}

	public void addPositionIntegerTextBox(ObidosIntegerTextBox box)
	{
		if (positionTextBoxes == null)
		{
			positionTextBoxes = new ArrayList<ObidosIntegerTextBox>();
		}
		positionTextBoxes.add(box);
	}

	public ObidosIntegerTextBox getPositionIntegerTextBox(final int index)
	{
		return positionTextBoxes.get(index);
	}

	public Map<TextBox, UserDefinedFieldDTO> getFieldNameDTOMap()
	{
		return fieldNameDTOMap;
	}

	public void setFieldNameDTOMap(Map<TextBox, UserDefinedFieldDTO> fieldNameDTOMap)
	{
		this.fieldNameDTOMap = fieldNameDTOMap;
	}

	public void putNameTextBoxToMap(final TextBox box, final UserDefinedFieldDTO fdto)
	{
		if (fieldNameDTOMap == null)
		{
			fieldNameDTOMap = new HashMap<>();
		}
		fieldNameDTOMap.put(box, fdto);
	}

	public UserDefinedFieldDTO getUserDefinedFieldDTO(final TextBox box)
	{
		return fieldNameDTOMap.get(box);
	}

	public UserDefinedTypeDTO getUserDefinedTypeDTO()
	{
		return userDefinedTypeDTO;
	}

	public void setUserDefinedTypeDTO(UserDefinedTypeDTO userDefinedTypeDTO)
	{
		this.userDefinedTypeDTO = userDefinedTypeDTO;
	}

	public List<TextBox> getNewNameTextBoxes()
	{
		return newNameTextBoxes;
	}

	public void setNewNameTextBoxes(ArrayList<TextBox> newNameTextBoxes)
	{
		this.newNameTextBoxes = newNameTextBoxes;
	}

	public void addNewNameTextBox(final TextBox textBox)
	{
		if (newNameTextBoxes == null)
		{
			newNameTextBoxes = new ArrayList<TextBox>();
		}
		newNameTextBoxes.add(textBox);
	}

	public List<ObidosIntegerTextBox> getNewPositionTextBoxes()
	{
		return newPositionTextBoxes;
	}

	public void setNewPositionTextBoxes(ArrayList<ObidosIntegerTextBox> newPositionTextBoxes)
	{
		this.newPositionTextBoxes = newPositionTextBoxes;
	}

	public void addNewPositionTextBox(final ObidosIntegerTextBox box)
	{
		if (newPositionTextBoxes == null)
		{
			newPositionTextBoxes = new ArrayList<ObidosIntegerTextBox>();
		}
		newPositionTextBoxes.add(box);
	}
}

public class EditTemplatePresenter extends Presenter<EditTemplatePresenter.MyView, EditTemplatePresenter.MyProxy>
		implements EditTemplateUiHandlers
{
	private TemplateForm templateForm = new TemplateForm();
	private ObidosMessages glang = ObidosMessages.LANG;

	interface MyView extends View, HasUiHandlers<EditTemplateUiHandlers>
	{
		public FormGroup getFormGroup();
		public HTMLPanel getHtmlPanel();
		public BlockQuote getHelpBlockQuote();
		public TextBox getTempalteNameTextBox();
		public Button getSubmitButton();
		public Button getHelpButton();
		public Button getResetButton();
		public FormGroup getAddFieldFormGroup();
		public Row getAddFieldGroupRow();
		public TextBox getAddFieldTextBox();
		public ObidosIntegerTextBox getAddFieldDisplayOrderTextBox();
		public FlowPanel getAddFieldFp();
		public FlowPanel getAddDisplayOrderFp();
		public FlowPanel getAddButtonFp();
		public ObidosPanelHeader getPanelHeader();
		public ObidosButtonToolBar getBottomToolBar();
		public ObidosMessageRow getMessageRow();
		public ToggleSwitch getAttachDocumentToggleSwitch();
		public ObidosRowBottom2px getUploadRow();
		public ObidosRowBottom2px getTwoFARow();
		public ToggleSwitch getAdd2FAToggleSwitch();
		public ListBox getLanguageListBox();
		public ObidosRowBottom2px getLanguageRow();
	}

	@NameToken(NameTokens.EDIT_TEMPLATE)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<EditTemplatePresenter>
	{
	}

	private final PlaceManager placeManager;

	@Inject
	EditTemplatePresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
			CurrentUser currentUser)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		this.placeManager = placeManager;
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
		clearFormGroups();
		ClientUtils.resetLanguage(getView().getLanguageRow());
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	void clearFormGroups()
	{
		gwtLog("Clearing formgroups...");

		this.templateForm = null;
		getView().getFormGroup().clear();
		FormGroup fg = getView().getAddFieldFormGroup();
		fg.clear();
	}

	protected void onReset()
	{
		super.onReset();
		showUploadWidget(false);
		show2FAWidget(false);
		redrawForm();
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
		showUpdateMessage();
	}

	void redrawForm()
	{
		showMessage(null);
		getView().getBottomToolBar().adjustButtonsWidth();
		resetView();
		getView().getAddFieldFormGroup().add(getView().getAddFieldGroupRow());
		saveFirstAddRow();
	}

	private void saveFirstAddRow()
	{
		allocateTemplateForm();
		templateForm.addNewNameTextBox(getView().getAddFieldTextBox());
		templateForm.addNewPositionTextBox(getView().getAddFieldDisplayOrderTextBox());
	}

	private void allocateTemplateForm()
	{
		if (templateForm == null)
		{
			gwtLog(">> Allocate TemplateForm <<");
			templateForm = new TemplateForm();
		}
	}

	private void resetView()
	{
		showMessage("");
		clearFormGroups();
		Long templateId = null;
		fetchAndPopulateForm(templateId);
		getView().getAddFieldTextBox().setValue("");
	}

	private void fetchAndPopulateForm(Long tid)
	{
		allocateTemplateForm();

		Long templateId = null;

		if (tid == null)
		{
			templateId = ClientUtils.getTemplateIdFromUrl(placeManager);
		} else
		{
			templateId = tid;
		}
		if (templateId == null)
		{
			String errorMessage = "Could not get template id from URL";
			showErrorMessage(errorMessage);
			return;
		}

		gwtLog("Template id: " + templateId);

		GwtAsyncWrapper<UserDefinedTypeDTO> callback = new GwtAsyncWrapper<UserDefinedTypeDTO>(this)
		{
			@Override
			public void uponSuccess(UserDefinedTypeDTO dto)
			{
				gwtLog("Update form fields...");
				gwtLog(" Personal Template? " + dto.getPersonal());
				Long id = dto.getFields().get(0).getId();
				gwtLog(" Id: " + id);
				gwtLog("TemplateField class: " + templateForm);
				templateForm.setUserDefinedTypeDTO(dto);

				populateForm(dto);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get Personal Template: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		TemplateService.Utility.getInstance().getUserDefinedType(authCreds, templateId, callback);
	}

	class PositionComp implements Comparator<UserDefinedFieldDTO>
	{
		@Override
		public int compare(UserDefinedFieldDTO o1, UserDefinedFieldDTO o2)
		{
			return o1.getPosition().compareTo(o2.getPosition());
		}

	}

	private UserDefinedFieldDTO getMaxDisplayOrder(List<UserDefinedFieldDTO> fields)
	{
		return Collections.max(fields, new PositionComp());
	}

	private void populateForm(UserDefinedTypeDTO dto)
	{
		boolean editing = editingTemplate();
		final String COL_SM_1 = "col-sm-1";

		if (Boolean.TRUE.equals(dto.getPersonal()))
		{
			if (editing)
			{
				getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.editPersonalTemplate());
			} else
			{
				getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.viewPersonalTemplate());
			}
		} else
		{
			if (editing)
			{
				getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.editGlobalTemplate());
			} else
			{
				getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.viewGlobalTemplate());
			}
		}

		Long type = dto.getFields().get(0).getType();
		Long typeId = dto.getFields().get(0).getTypeId();
		gwtLog(" Type: " + type);
		gwtLog(" Type Id: " + typeId);

		if (ClientUtils.isNote(placeManager))
		{
			gwtLog("Item is a Note. Hide add group");
			getView().getAddFieldFormGroup().setVisible(false);
		} else
		{
			getView().getAddFieldFormGroup().setVisible(true);
		}

		// update template name text box
		getView().getTempalteNameTextBox().setValue(dto.getName());

		FormGroup formGroup = getView().getFormGroup();
		// clear everything attached, so we can re-use this method
		formGroup.clear();

		List<UserDefinedFieldDTO> fields = dto.getFields();
		// sort by display order
		Collections.sort(fields, (f1, f2) -> f1.getPosition().compareTo(f2.getPosition()));

		showUploadWidget(false);
		show2FAWidget(false);
		int nrows = 0;
		boolean wifiQRCodeTemplate = false;
		for (UserDefinedFieldDTO fieldDTO : fields)
		{
			if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_ENCRYPTED)
			{
				if (ObidosConstants.WIFI_QRCODE == fieldDTO.getName())
				{
					// do not display special field 
					wifiQRCodeTemplate = true;
					continue;
				}
				nrows++;
				// field name TextBox
				Row row = new Row();
				FlowPanel flowPanel = new FlowPanel();
				flowPanel.addStyleName("col-sm-offset-4 col-sm-3");
				TextBox nameTextBox = new TextBox();
				nameTextBox.setPlaceholder(glang.nameOfField());
				nameTextBox.setValue(fieldDTO.getName());
				flowPanel.add(nameTextBox);
				flowPanel.getElement().getStyle().setMarginBottom(10, Unit.PX);
				row.add(flowPanel);
				formGroup.add(row);

				templateForm.addNameTextBox(nameTextBox);
				templateForm.putNameTextBoxToMap(nameTextBox, fieldDTO);

				// display order IntegerTextBox
				FlowPanel doFlowPanel = new FlowPanel();
				doFlowPanel = new FlowPanel();
				doFlowPanel.addStyleName(COL_SM_1);
				ObidosIntegerTextBox positionTextBox = new ObidosIntegerTextBox();
				positionTextBox.setPlaceholder(glang.displayOrderPlaceHolder());
				Integer position = fieldDTO.getPosition();

				if (wifiQRCodeTemplate)
				{
					// do not display special field , so decrease the position
					position = position - 1;
				}
				positionTextBox.setValue(position);
				doFlowPanel.add(positionTextBox);
				doFlowPanel.getElement().getStyle().setMarginBottom(10, Unit.PX);
				row.add(doFlowPanel);
				formGroup.add(row);

				templateForm.addPositionIntegerTextBox(positionTextBox);

				gwtLog("EF Field: " + fieldDTO.getId() + " " + fieldDTO.getName() + " Position: "
						+ fieldDTO.getPosition());
			}
			if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_DOCUMENT)
			{
				gwtLog("EF Doc Field: " + fieldDTO.getId() + " " + fieldDTO.getName() + " Position: "
						+ fieldDTO.getPosition());
				showUploadWidget(true);
			}
			if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_QRCODE)
			{
				gwtLog("EF QRC Field: " + fieldDTO.getId() + " " + fieldDTO.getName() + " Position: "
						+ fieldDTO.getPosition());
				show2FAWidget(true);
			}
		}
		gwtLog("EF: nrows: " + nrows);
		nrows++;
		getView().getAddFieldDisplayOrderTextBox().setValue(nrows);

		gwtLog("              >>> addFields, texboxes: " + templateForm.getNameTextBoxes().size());
		gwtLog("       >>>> addFields, position boxes: " + templateForm.getPositionTextBoxes().size());

		// Now enable/disable fields
		enableOrDisableFields(); // editing or viewing
	}

	private void showUploadWidget(boolean visible)
	{
		getView().getUploadRow().setVisible(visible);
		getView().getAttachDocumentToggleSwitch().setValue(true);
		getView().getAttachDocumentToggleSwitch().setEnabled(false);
	}

	private void show2FAWidget(boolean visible)
	{
		getView().getTwoFARow().setVisible(visible);
		getView().getAdd2FAToggleSwitch().setValue(true);
		getView().getAdd2FAToggleSwitch().setEnabled(false);
	}

	private String getActionFromUrl()
	{
		String key = ObidosConstants.ACTION;
		try
		{
			return ClientUtils.getParameterFromUrl(placeManager, key);
		} catch (ParamNotFoundException e)
		{
		}
		return null;
	}

	private boolean editingTemplate()
	{
		String action = getActionFromUrl();
		if (action == null)
		{
			return false;
		}
		if (action.equals(ObidosConstants.EDIT))
		{
			return true;
		}
		return false;
	}

	// disable fields if we are viewing the template
	// enable fields if we are editing the template
	private void enableOrDisableFields()
	{
		boolean enabled = editingTemplate();
		boolean visible = enabled;

		gwtLog("              >>> addFields, texboxes: " + templateForm.getNameTextBoxes().size());
		gwtLog("       >>>> addFields, position boxes: " + templateForm.getPositionTextBoxes().size());

		if (enabled)
		{
			getView().getTempalteNameTextBox().setReadOnly(false);
			getView().getSubmitButton().setVisible(true);
			getView().getResetButton().setVisible(true);
		} else
		{
			getView().getTempalteNameTextBox().setReadOnly(true);
			getView().getSubmitButton().setVisible(false);
			getView().getResetButton().setVisible(false);

		}

		// show/hide the + button row
		getView().getAddFieldFp().setVisible(visible);
		getView().getAddDisplayOrderFp().setVisible(visible);
		getView().getAddButtonFp().setVisible(visible);

		// enable disable the fields
		getView().getSubmitButton().setEnabled(enabled);
		getView().getResetButton().setEnabled(enabled);

		List<TextBox> nameBoxes = templateForm.getNameTextBoxes();
		List<ObidosIntegerTextBox> posBoxes = templateForm.getPositionTextBoxes();
		for (TextBox textBox : nameBoxes)
		{
			textBox.getElement().getStyle().setProperty("color", "#000000");
			textBox.getElement().getStyle().setProperty("fontWeight", "bold");
			textBox.setReadOnly(!enabled);
		}

		for (ObidosIntegerTextBox textBox : posBoxes)
		{
			textBox.getElement().getStyle().setProperty("color", "#000000");
			textBox.getElement().getStyle().setProperty("fontWeight", "bold");
			textBox.setReadOnly(!enabled);
		}

	}

	void enableDiasableSumbitButton(boolean enabled)
	{
		getView().getSubmitButton().setEnabled(enabled);
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

	// return number of rows on success -1 on failure
	private int validateFieldPositions()
	{
		showMessage("");
		gwtLog(">> start validateFieldPositions");
		int nPositionBoxes = templateForm.getPositionTextBoxes().size();
		int nNameBoxes = templateForm.getNameTextBoxes().size();
		int newPosiotnBoxes = templateForm.getNewNameTextBoxes().size();

		gwtLog("Number of field name boxes: " + nNameBoxes);
		gwtLog("Number of position boxes: " + nPositionBoxes);
		gwtLog("Number of new positon boxes: " + newPosiotnBoxes);
		gwtLog(">> end validateFieldPositions");

		if (nNameBoxes != nPositionBoxes)
		{
			showErrorMessage("Number fields and position boxes do not match");
			return -1;
		}

		List<TextBox> textBoxes = templateForm.getNameTextBoxes();
		ArrayList<Integer> positionArrayList = new ArrayList<Integer>();
		int row_num = 0;
		// check if any field name is empty
		int nTextBoxes = textBoxes.size();
		for (int i = 0; i < nTextBoxes; i++)
		{
			row_num++;
			String fieldName = textBoxes.get(i).getValue();
			if (fieldName == null || fieldName.isEmpty())
			{
				continue;
			}
			ObidosIntegerTextBox integerTextBox = templateForm.getPositionIntegerTextBox(i);
			int displayOrder = integerTextBox.getValue();
			gwtLog("Display order: " + displayOrder);
			positionArrayList.add(displayOrder);
		}

		// new rows
		List<TextBox> newTextBoxes = templateForm.getNewNameTextBoxes();
		int nNewTextBoxes = newTextBoxes.size();
		gwtLog("New text boxes: " + nNewTextBoxes);
		for (int i = 0; i < nNewTextBoxes; i++)
		{
			TextBox textBox = newTextBoxes.get(i);

			String fieldName = textBox.getValue();
			if (fieldName == null || fieldName.isEmpty())
			{
				// ignore the row if field name does not have any name
				continue;
			}
			row_num++;
			ObidosIntegerTextBox integerTextBox = templateForm.getNewPositionTextBoxes().get(i);
			int displayOrder = integerTextBox.getValue();
			gwtLog("Display order of New: " + displayOrder);
			positionArrayList.add(displayOrder);
		}
		gwtLog("Total number of rows: " + row_num);
		// see if there is any duplicate display order
		// do the old fashioned way because it is so hard to find out what the
		// hell is going on in Java 8 stream way.
		gwtLog("DO Size: " + positionArrayList.size());

		for (int i = 0; i < positionArrayList.size(); i++)
		{
			for (int j = i + 1; j < positionArrayList.size(); j++)
			{
				Integer n1 = positionArrayList.get(i);
				Integer n2 = positionArrayList.get(j);
				gwtLog("n1: " + n1 + " n2: " + n2);
				if (n1.equals(n2))
				{
					showErrorMessage("Duplicate Dispaly Order number " + n1);
					return -1;
				}
			}
		}

		// make sure any display order is not bigger than total number of size
		for (int i = 0; i < positionArrayList.size(); i++)
		{
			Integer displayOrderNumber = positionArrayList.get(i);
			if (displayOrderNumber > row_num)
			{
				int r = i + 1;
				showErrorMessage("Invalid Display Order " + displayOrderNumber + " at row: " + r
						+ ", cannot be larger than total number of rows: " + row_num);
				return -1;
			}
		}
		gwtLog("Valid validation OK");

		return row_num;
	}

	// if some fields are deleted normalize the display orders in
	// ascending chronological order
	// return true if normalized false otherwise
	private boolean normalizeDisplayOrders(final UserDefinedTypeDTO typeDTO)
	{
		int displayFieldsCount = 0; // To keep track of number of text fields in
									// the Template
		int index = 0;
		boolean normalized = true;
		boolean documentFieldExists = false;
		boolean qrCodeFieldExists = false;

		List<UserDefinedFieldDTO> fields = typeDTO.getFields();

		List<Integer> displayOrders = new ArrayList<>();

		gwtLog(">>>>> inside normalizeDisplayOders ..........S");
		for (UserDefinedFieldDTO fdto : fields)
		{
			gwtLog("***> " + fdto.getName() + ":" + fdto.getPosition());
			if (fdto.getType() == UserDefinedFieldDTO.TYPE_DOCUMENT)
			{
				documentFieldExists = true;
			} else if (fdto.getType() == UserDefinedFieldDTO.TYPE_QRCODE)
			{
				qrCodeFieldExists = true;
			} else
			{
				Integer position = fdto.getPosition();
				displayOrders.add(position);
			}

		}
		displayFieldsCount = displayOrders.size();

		Collections.sort(displayOrders);

		gwtLog(">>>>> in normalizeDisplayOders ..........E  " + displayFieldsCount);

		index = 0;

		int position; // To keep track of the display order that came from the
						// UI.

		for (UserDefinedFieldDTO fdto : fields)
		{
			if (fdto.getType() == UserDefinedFieldDTO.TYPE_DOCUMENT)
			{
				// document upload comes right after text fields.

				fdto.setPosition(displayFieldsCount + 1);
				gwtLog("Name: " + fdto.getName());
				gwtLog("Doc type qr type do ignore");
			} else if (fdto.getType() == UserDefinedFieldDTO.TYPE_QRCODE)
			{
				gwtLog("Name: " + fdto.getName());
				gwtLog("Doc type qr type do ignore");

				// QR Code field comes after document upload field (if it
				// exists).

				if (documentFieldExists == true && qrCodeFieldExists == true)
				{
					fdto.setPosition(displayFieldsCount + 2);
				}
				if (documentFieldExists == false && qrCodeFieldExists == true)
				{
					fdto.setPosition(displayFieldsCount + 1);
				}
			} else
			{
				position = fdto.getPosition();
				gwtLog(fdto.getName() + " POSITION: " + position);

				index = displayOrders.indexOf(position);

				fdto.setPosition(index + 1);
				displayOrders.set(index, -1); // Clear the position that we just
												// found. Not really needed, but
												// this will
				// accommodate duplicates.
			}
		}

		gwtLog(">>>>>>>>>> Final display orders ... starts");
		for (UserDefinedFieldDTO fdto : fields)
		{
			gwtLog("Final >>>>" + fdto.getName() + ":" + fdto.getPosition());
		}
		gwtLog(">>>>>>>>>>> Final display orders ... ends");
		return normalized;
	}

	@Override
	public void updateTemplate()
	{
		showMessage("");
		Long id = null;
		String key = ObidosConstants.TEMPLATE_ID;
		int nfieldsDeleted = 0;

		try
		{
			id = ClientUtils.getIdFromUrl(placeManager, key);
		} catch (NumberFormatException | ParamNotFoundException e)
		{
			showErrorMessage("Could not find " + key + " in URL");
			return;
		}

		if (templateForm.getNameTextBoxes().size() <= 0)
		{
			gwtLog("No Fields found ...");
			return;
		}

		gwtLog("              +++ addFields, texboxes: " + templateForm.getNameTextBoxes().size());
		gwtLog("       +++ addFields, position boxes: " + templateForm.getPositionTextBoxes().size());
		int sz = templateForm.getPositionTextBoxes().size();
		for (int i = 0; i < sz; i++)
		{
			gwtLog("  Position: " + i + "=" + templateForm.getPositionIntegerTextBox(i).getValue());
		}

		int nrows = validateFieldPositions();
		if (nrows == -1)
		{
			gwtLog(">>> Field Position Validation Falied");
			return;
		}
		gwtLog("NUmber of rows: " + nrows);

		String templateName = getView().getTempalteNameTextBox().getValue();

		// use everything from saved DTO, just update the values
		UserDefinedTypeDTO typeDTO = templateForm.getUserDefinedTypeDTO();
		typeDTO.setName(templateName);

		gwtLog("XXX Update Template name: " + templateName);
		gwtLog("XXX Template id: " + id);
		gwtLog("XXX Template type personal: " + typeDTO.getPersonal());
		gwtLog("XXX Template type glbal: " + typeDTO.getGlobal());
		Integer position = null;

		int nBoxes = templateForm.getNameTextBoxes().size();
		int mapSize = templateForm.getFieldNameDTOMap().size();

		gwtLog(" --- number of boxes: " + nBoxes);
		gwtLog(" --- map size: " + mapSize);

		boolean fieldDeleted = false;
		for (int i = 0; i < nBoxes; i++)
		{
			TextBox fieldNameTextBox = templateForm.getNameTextBox(i);
			ObidosIntegerTextBox positionTextBox = templateForm.getPositionIntegerTextBox(i);

			UserDefinedFieldDTO fdto = templateForm.getUserDefinedFieldDTO(fieldNameTextBox);
			Long type = fdto.getType();
			gwtLog(" ++ Type: " + type);

			// if no value specified in an existing field, it means that the
			// user wants to delete that field
			String name = fieldNameTextBox.getValue();
			if (name == null || name.length() == 0)
			{
				nfieldsDeleted++;
				gwtLog("Delete field: " + name);
				fieldDeleted = true;
				// It seems the following call is not used in back end anymore
				// But do not remove this call, it is used to delete fields in
				// front end
				fdto.setDelete(Boolean.TRUE);
				// remove the field
				typeDTO.removeField(fdto);
			}
			position = positionTextBox.getValue();
			gwtLog("  Field id: " + fdto.getId() + " Name: " + name + " Position: " + position);

			fdto.setName(name);
			fdto.setPosition(position);
		}

		// Now check if user added any new fields
		int newBoxes = templateForm.getNewNameTextBoxes().size();
		int newPosBoxes = templateForm.getNewPositionTextBoxes().size();
		gwtLog(" new boxes: " + newBoxes + " new Pos boxes: " + newPosBoxes);
		for (int i = 0; i < templateForm.getNewNameTextBoxes().size(); i++)
		{
			String fieldName = templateForm.getNewNameTextBoxes().get(i).getValue();
			Integer pos = templateForm.getNewPositionTextBoxes().get(i).getValue();
			gwtLog("Field: " + fieldName + " Pos: " + position);
			Long typeId = null;
			Long type = UserDefinedFieldDTO.TYPE_ENCRYPTED;
			if (fieldName != null && fieldName.length() > 0 && pos != null)
			{
				gwtLog("   Add field: " + fieldName + " Pos: " + pos);
				UserDefinedFieldDTO fdto = new UserDefinedFieldDTO(typeId, type, fieldName, pos);
				typeDTO.addField(fdto);
			}
		}

		if (fieldDeleted)
		{
			normalizeDisplayOrders(typeDTO);
			promptDeleteWarning(typeDTO, nrows, nfieldsDeleted);
		} else
		{
			// update the display order of doc upload qr code upload fields
			List<UserDefinedFieldDTO> fields = typeDTO.getFields();
			int displayOrder = nrows;
			;
			for (UserDefinedFieldDTO fieldDTO : fields)
			{
				if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_DOCUMENT)
				{
					gwtLog("EFR Field Doc: " + fieldDTO.getId() + " " + fieldDTO.getName() + " Position: "
							+ fieldDTO.getPosition());
					displayOrder++;
					gwtLog(">>>>>>>>>>>>>> Set doc display order to: " + displayOrder);
					fieldDTO.setPosition(displayOrder);
				} else if (fieldDTO.getType() == UserDefinedFieldDTO.TYPE_QRCODE)
				{
					gwtLog("EFR Field QR: " + fieldDTO.getId() + " " + fieldDTO.getName() + " Position: "
							+ fieldDTO.getPosition());
					displayOrder++;
					gwtLog(">>>>>>>>>>>>>> Set QR code display order to: " + displayOrder);
					fieldDTO.setPosition(displayOrder);
				} else
				{
					gwtLog("EFR Field: " + fieldDTO.getId() + " " + fieldDTO.getName() + " Position: "
							+ fieldDTO.getPosition());
				}
			}

			updateTemplateReal(typeDTO);
		}
	}

	private void promptDeleteWarning(UserDefinedTypeDTO typeDTO, final int numberOfRows, final int nFieldsDeleted)
	{
		String message = ObidosMessages.LANG.personalTemplateFieldRemovedWarning(nFieldsDeleted);
		String title = ObidosMessages.LANG.templateFieldDeleted();
		DialogOptions options = DialogOptions.newOptions(message);
		options.setTitle(title);
		options.setOnEscape(new SimpleCallback()
		{

			@Override
			public void callback()
			{
			}
		});

		// No
		options.addButton(ObidosMessages.LANG.no(), ButtonType.DEFAULT.getCssName(), new SimpleCallback()
		{
			@Override
			public void callback()
			{
			}
		});

		// Yes
		options.addButton(ObidosMessages.LANG.yes(), ButtonType.DANGER.getCssName(), new SimpleCallback()
		{

			@Override
			public void callback()
			{
				updateTemplateReal(typeDTO);
			}
		});
		Bootbox.dialog(options);
	}

	private void updateTemplateReal(final UserDefinedTypeDTO typeDTO)
	{
		gwtLog("In updateTemplateReal...");
		GwtAsyncWrapper<UserDefinedTypeDTO> callback = new GwtAsyncWrapper<UserDefinedTypeDTO>(this)
		{

			@Override
			public void uponSuccess(UserDefinedTypeDTO typeDTO)
			{
				String templateName = getView().getTempalteNameTextBox().getValue();
				showMessage(ObidosMessages.LANG.templateUpdatedSucessFully(templateName));

				// We're here that means a template is updated and the id has changed.
				// Short circuit the refresh, come back to the same update page
				// and the existing code will refresh the form.
				// Sep-08-2023
				showEditTemplatePage(typeDTO);

				getView().getSubmitButton().setEnabled(true);
				getView().getResetButton().setEnabled(false);

			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(ObidosMessages.LANG.couldnotUpdateTemplate() + ": " + caught.getMessage());
			}
		};

		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		Boolean modifyGlobalTemplateInstances = Boolean.TRUE;
		TemplateService.Utility.getInstance().update(authCreds, typeDTO, modifyGlobalTemplateInstances, callback);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	private void setKeyUpEventHandler(TextBox textBox)
	{
		textBox.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent e)
			{
				showMessage("");

				if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					gwtLog("Add new Row...");
					addFields();
				}
			}
		});
	}

	private Integer findLargestPositionNumber()
	{
		int nBoxes = templateForm.getPositionTextBoxes().size();
		int max = 0;

		for (int i = 0; i < nBoxes; i++)
		{
			Integer n = templateForm.getPositionTextBoxes().get(i).getValue();
			if (n == null)
			{
				int row = i + 1;
				showErrorMessage(ObidosMessages.LANG.noDisplayOrderSpecified() + ": " + row);
				return null;
			}
			if (n > max)
			{
				max = n;
			}
		}
		gwtLog("nBoxes: " + nBoxes);
		int newNBoxes = templateForm.getNewPositionTextBoxes().size();
		for (int i = 0; i < newNBoxes; i++)
		{
			Integer n = templateForm.getNewPositionTextBoxes().get(i).getValue();
			if (n == null)
			{
				int row = nBoxes + i + 1;
				showErrorMessage(ObidosMessages.LANG.noDisplayOrderSpecified() + ": " + row);
				return null;
			}
			if (n > max)
			{
				max = n;
			}
		}

		return max;
	}

	@Override
	public void addFields()
	{
		final String COL_SM_1 = "col-sm-1";
		showMessage("");
		int nrows = validateFieldPositions();
		if (nrows == -1)
		{
			return;
		}

		gwtLog(" number of rows: " + nrows);
		gwtLog(" addFields, texboxes: " + templateForm.getNameTextBoxes().size());
		gwtLog(" addFields, position boxes: " + templateForm.getPositionTextBoxes().size());
		gwtLog("Finding largest position...");
		Integer doVal = findLargestPositionNumber();
		if (doVal == null)
		{
			return;
		}
		gwtLog("largest position: " + doVal);
		doVal++;

		FormGroup formGroup = getView().getAddFieldFormGroup();
		Row row = new Row();

		FlowPanel fp1 = new FlowPanel();
		fp1.addStyleName("col-sm-offset-4 col-sm-3");
		TextBox tbox = new TextBox();
		tbox.setPlaceholder(glang.templateFieldNames());
		fp1.add(tbox);

		// save the new TextBox as long as the user is in the view
		templateForm.addNewNameTextBox(tbox);

		FlowPanel fp2 = new FlowPanel();
		fp2.addStyleName(COL_SM_1);
		ObidosIntegerTextBox doTextBox = new ObidosIntegerTextBox();
		doTextBox.setPlaceholder(ObidosMessages.LANG.displayOrder());
		fp2.add(doTextBox);

		// save the new Position box as long as the user is in the view
		templateForm.addNewPositionTextBox(doTextBox);
		doTextBox.setValue(doVal);

		FlowPanel fp3 = new FlowPanel();
		fp3.addStyleName(COL_SM_1);
		Button b = new Button();
		b.setType(ButtonType.INFO);
		b.setIcon(IconType.MINUS);
		fp3.add(b);

		row.add(fp1);
		row.add(fp2);
		row.add(fp3);
		row.getElement().getStyle().setMarginBottom(10, Unit.PX);
		formGroup.add(row);

		// handler for Minus button
		b.addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				formGroup.remove(row);

				showMessage("");

				templateForm.getNewNameTextBoxes().remove(tbox);
				templateForm.getNewPositionTextBoxes().remove(doTextBox);
				int sz = templateForm.getNewPositionTextBoxes().size();

				gwtLog(" after addFields, texboxes: " + templateForm.getNewNameTextBoxes().size());
				gwtLog(" after addFields, position boxes: " + templateForm.getNewNameTextBoxes().size());
				gwtLog("Number of position text boxes: " + sz);
				for (int i = 0; i < sz; i++)
				{
					ObidosIntegerTextBox ptbox = templateForm.getNewPositionTextBoxes().get(i);
					gwtLog(" position: " + ptbox.getValue());
				}
				for (int i = 0; i < sz; i++)
				{
					int idx = i;
					gwtLog("Get value at index: " + idx);
					ObidosIntegerTextBox otbox = templateForm.getNewPositionTextBoxes().get(idx);
					otbox.setValue(otbox.getValue());
				}

			}
		});

		tbox.setFocus(true);
		setKeyUpEventHandler(tbox);
	}

	@Override
	public void resetForm()
	{
		redrawForm();
	}

	@Override
	public void back()
	{
		ClientUtils.goBack(placeManager);
	}

	@Override
	public void languageListBoxCallback()
	{
		ListBox lb = getView().getLanguageListBox();
		String lang = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		gwtLog("index: " + idx);
		gwtLog("Lang: " + lang);
		// only support English and Bangla for editing at this time
		switch (idx)
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
	
	// Bug #873
	// User has updated the template successfully  and a new UserDefinedTypeDTO
	// is returned. Update URL bar with info to simulate Edit click, so that
	// page can be refreshed as usual as URL bar will have the new template Id.
    private void showEditTemplatePage(UserDefinedTypeDTO dto)
    {
        gwtLog("Show edit template page, template id: " + dto.getId());
        String templateType = ObidosConstants.PERSONAL;
        String nameToken = NameTokens.EDIT_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.ACTION, ObidosConstants.EDIT);
        with.put(ObidosConstants.TEMPLATE_TYPE, templateType);
        // add this extra param, so that we can print a message that the template
        // is updated successfully!
        with.put(ObidosConstants.SHOW_TEMPLATE_UPDATE_MESSAGE,glang.yes());
        ClientUtils.showPage(placeManager, nameToken, with);
    }
    
    // Bug #873
    private void showUpdateMessage()
    {
		String showUpdateMessage = ClientUtils.getStringParameterFromUrl(placeManager, ObidosConstants.SHOW_TEMPLATE_UPDATE_MESSAGE);
		if (glang.yes().equals(showUpdateMessage))
		{
			String templateName = getView().getTempalteNameTextBox().getValue();
			showMessage(ObidosMessages.LANG.templateUpdatedSucessFully(templateName));
		}
    }



}
