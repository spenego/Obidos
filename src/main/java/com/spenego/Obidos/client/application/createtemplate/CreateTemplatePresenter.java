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

package com.spenego.Obidos.client.application.createtemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTemplateRow;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseCanCreateShareGateKeepr;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class CreateTemplatePresenter
        extends Presenter<CreateTemplatePresenter.MyView, CreateTemplatePresenter.MyProxy>
        implements CreateTemplateUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private List<ObidosTemplateRow> templateRows = new ArrayList<ObidosTemplateRow>();
    private List<TextBox> nameTextBoxes = new ArrayList<TextBox>();
    private List<ObidosIntegerTextBox> orderTextBoxes = new ArrayList<ObidosIntegerTextBox>();

    interface MyView extends View, HasUiHandlers<CreateTemplateUiHandlers>
    {
        public Paragraph getParagraph();
        public FormGroup getAddFormGroup();
        public HTMLPanel getHtmlPanel();
        public BlockQuote getHelpBlockQuote();
        public TextBox getTempalteNameTextBox();
        public Button getSaveTemplateButton();
        public ObidosButtonToolBar getBottomToolBar();
        public ObidosTemplateRow getFirstRow();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
        public ToggleSwitch getAttachDocumentToggleSwitch();
        public ToggleSwitch getAdd2FAInfoToggleSwitch();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
       	public Label getLicenseLabelDoc();
       	public Label getLicenseLabel2FA();
    }

    @NameToken(NameTokens.TYPE_TEMPLATE)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseCanCreateShareGateKeepr.class)
    interface MyProxy extends ProxyPlace<CreateTemplatePresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    CreateTemplatePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        // can not set handlers in onReset() as they will accumulate
        setupFirstTemplateRowHanders();
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

    protected void onReset()
    {
        super.onReset();

        if (ClientUtils.isAdmin(currentUser)) return;

        gwtLog("MMM onReset...");
        if (!ClientUtils.isPassphraseRegistered(currentUser))
        {
        	Map<String, String> with = new HashMap<>();
        	with.put(ObidosConstants.TEMPLATE_TYPE, ObidosConstants.PERSONAL);
        	with.put(ObidosConstants.PLACE, NameTokens.TYPE_TEMPLATE);

        	ClientUtils.showRegisterPassphraseDialog(glang.createPersonalTemplate(),
        			()->ClientUtils.goBack(placeManager), 
        			()->ClientUtils.goToRegisterPassphrasePage(placeManager, with));
        	return;
        }
        updateBackButtonLabel();
        getView().getBottomToolBar().adjustButtonsWidth();
        makeFirstRowIconVisible();
        clearMessage();
        clearFields();
        updateHelpMessage();
        showHelpArea(false);
        updateHeadings();
        gwtLog("MMM 5");
        enforceLicenseRestrictions();
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
		gwtLog("MMM 6");
    }

    private void makeFirstRowIconVisible()
    {
    	int sz = templateRows.size();
    	gwtLog("Size of template rows: " + sz);
    	if (sz == 0)
    	{
			getView().getFirstRow().getIconButton().setVisible(true);
			getView().getFirstRow().getIconButton().setIcon(IconType.PLUS);
    	}
    }
    
    private void updateBackButtonLabel()
    {
    	Button backButton = getView().getPanelHeader().getBackButton();
   		backButton.setVisible(true);
    	int type = ClientUtils.getTemplateTypeFromUrl(placeManager);
    	if (type == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
    	{
    		backButton.setText(glang.listPersonalTemplates());
    	}
    	else if (type == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
    	{
    		backButton.setText(glang.listGlobalTemplates());
    	}
    	else
    	{
    		backButton.setVisible(false);
    	}
    	
    }

    private void setupFirstTemplateRowHanders()
    {
    	gwtLog("Add first row handler");
    	
    	ObidosTemplateRow row = getView().getFirstRow();
    	// save the row
//    	templateRows.add(row);

    	ObidosIntegerTextBox doTextBox = row.getDisplayOrderTextBox();
		doTextBox.setValue(1);
		doTextBox.setReadOnly(true);

    	Button iconButton = row.getIconButton();
		HandlerRegistration handler = iconButton.addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				gwtLog("+ Clicked");
				addTemplateRow(true);
			}
		});
		// save the handler so that we can remove and add a new one
		row.saveClickHandler(handler);
		
		TextBox textBox = row.getFieldNameTextBox();
		textBox.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent e)
			{
				if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					addTemplateRow(true);
				}
			}
		});
    }

    private void clearMessage()
    {
    	getView().getMessageRow().showMessage(null);
    }

    private void clearFields()
    {
        getView().getTempalteNameTextBox().clear();

    	ObidosTemplateRow trow = getView().getFirstRow();
    	trow.getFieldNameTextBox().clear();
    	trow.getDisplayOrderTextBox().setValue(1);

    }

    private void setSaveButtonTitle(String title)
    {
        getView().getSaveTemplateButton().setText(title);
    }

    @Override
    public void listMyPersonalTemplates()
    {
         gwtLog("Show List Personal Templates View");
        String nameToken = NameTokens.LIST_TEMPLATES;
        Map<String,String> with = new HashMap<>();
        String key = ObidosConstants.TEMPLATE_TYPE;
        String value = ObidosConstants.PERSONAL;
        with.put(key, value);
        ClientUtils.showPage(placeManager, nameToken, with);

    }

    @Override
    public void listGlobalTemplates()
    {
        String nameToken = NameTokens.LIST_GLOBAL_TEMPLATES;
        ClientUtils.showPage(placeManager, nameToken);
    }

    private void showHelpArea(boolean visible)
    {
        getView().getHelpBlockQuote().setVisible(visible);
    }

    private void setKeyUpEventHandler(TextBox textBox)
    {
        /*
         * textBox.addKeyDownHandler(new KeyDownHandler() {
         *
         * @Override public void onKeyDown(KeyDownEvent e) { showMessage("");
         *
         * if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
         * gwtLog("Add new Row..."); addFields(); } } });
         */

        textBox.setFocus(true);
        textBox.addKeyDownHandler((KeyDownEvent e) ->
        {
        	clearMessage();
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
                gwtLog("Add new Row...");
                addFields();
            }
        });
    }

    private boolean validateDisplayOrderValues()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        int nBoxes = orderTextBoxes.size();
        Integer[] orderArray = new Integer[nBoxes];

        gwtLog("nBoxes: " + nBoxes);
        for (int i = 0; i < nBoxes; i++)
        {
            ObidosIntegerTextBox textBox = orderTextBoxes.get(i);
            Integer displayOrder = textBox.getValue();
            if (displayOrder == null)
            {
            	int rowNum = i + 2;
            	showErrorMessage(lang.specifyDisplayOrderAtRow(rowNum));
            	return false;
            }
            int intValue = textBox.getValue();
            gwtLog("i: " + i + " value: "+ intValue);
            orderArray[i] = intValue;
        }
        List<Integer> list = Arrays.asList(orderArray);
        List<Integer> dps = list.stream().distinct().filter(entry -> Collections.frequency(list, entry) > 1).collect(Collectors.toList());
        if (dps.size() > 0)
        {
            showErrorMessage(lang.duplicateDisplayOrder(dps.get(0)));
            return false;
        }
        gwtLog("dps size: " + dps.size());

        return true;
    }
    
    private void storeNameTextBox(TextBox textBox)
    {
    	nameTextBoxes.add(textBox);
    }
    
    private void removeNameTextBox(TextBox textBox)
    {
    	getNameTextBoxes().remove(textBox);
    }
    
    private  List<TextBox>  getNameTextBoxes()
    {
    	return nameTextBoxes;
    }
    
    
    private List<ObidosIntegerTextBox>  getOrderTextBoxes()
    {
    	return orderTextBoxes;
    }

    private void storeOrderTextBox(ObidosIntegerTextBox textBox)
    {
    	getOrderTextBoxes().add(textBox);
    }

    private void removeOrderTextBox(ObidosIntegerTextBox textBox)
    {
    	getOrderTextBoxes().remove(textBox);
    }

    private int numberOfOrderTextBoxes()
    {
    	return getOrderTextBoxes().size();
    }
    
    private void clearOrderTexBoxes()
    {
    	getOrderTextBoxes().clear();
    }
    
    private void clearNameTextBoxes()
    {
    	getNameTextBoxes().clear();
    }
    
    private ObidosIntegerTextBox getOrderTextBox(int idx)
    {
    	return orderTextBoxes.get(idx);
    }
    private void adjustOrderTextBoxes()
    {
    	int sz = numberOfOrderTextBoxes();
    	for (int i = 2; i <= sz + 1; i++)
    	{
    		int idx = i - 2;
    		ObidosIntegerTextBox textBox = getOrderTextBox(idx);
    		textBox.setValue(i);
    	}
    }
    
    
    // change + to minus
    private void changePreviousRowClickHandlerToMinus()
    {
    	// fix first row
    	int sz = templateRows.size();
    	gwtLog("Number of rows: "+ sz);
    	for (int i = 0; i < sz; i ++)
    	{
    		ObidosTemplateRow row = templateRows.get(i);
    		gwtLog("Row: " + i + " Icon type: " + row.getIconButton().getIcon());
    		IconType iconType = row.getIconButton().getIcon();
    		if (iconType == IconType.PLUS)
    		{
    			Button iconButton = row.getIconButton();
    			iconButton.setIcon(IconType.MINUS);
    			addMinusButtonHandler(row);
    		}
    	}
    }
    
    private void addMinusButtonHandler(ObidosTemplateRow templateRow)
    {
    	gwtLog("----------------");
		HandlerRegistration handler = templateRow.getClickHandler();
		if (handler != null)
		{
			gwtLog(">>>>>>>>>>> Remove click handler");
			handler.removeHandler();
		}

    	FormGroup formGroup = getView().getAddFormGroup();
    	Button iconButton = templateRow.getIconButton();
    	TextBox fieldNameTextBox = templateRow.getFieldNameTextBox();
    	ObidosIntegerTextBox doTextBox = templateRow.getDisplayOrderTextBox();


    	iconButton.addClickHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				formGroup.remove(templateRow);
				removeNameTextBox(fieldNameTextBox);
				removeOrderTextBox(doTextBox);
				adjustOrderTextBoxes();
				// remove it from our row holder
				templateRows.remove(templateRow);
			}
		});
    }
    
    private void addTemplateRow(boolean firstRow)
    {
    	if (firstRow)
    	{
    		gwtLog("Fix First Row");
    		ObidosTemplateRow frow = getView().getFirstRow();
    		Button plusButton = frow.getIconButton();
    		IconType iconType = plusButton.getIcon();
    		if (iconType == IconType.PLUS )
    		{
    			gwtLog("Hide Plus button from first row");
    			plusButton.setIcon(IconType.MINUS);
    			plusButton.setVisible(false);
    		}
    	}
    	FormGroup formGroup = getView().getAddFormGroup();
    	ObidosTemplateRow templateRow = new ObidosTemplateRow(IconType.PLUS);

    	// save. we don't add first row in that map
    	templateRows.add(templateRow);
    	formGroup.add(templateRow);
    	
    	// store
    	TextBox fieldNameTextBox = templateRow.getFieldNameTextBox();
    	storeNameTextBox(fieldNameTextBox);

    	// store
    	ObidosIntegerTextBox doTextBox = templateRow.getDisplayOrderTextBox();
    	storeOrderTextBox(doTextBox);
    	int sz = numberOfOrderTextBoxes();
    	doTextBox.setValue(sz + 1);
    	
    	// add handler for minus button
    	Button b = templateRow.getIconButton();
    	HandlerRegistration handler;
    	handler = b.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				changePreviousRowClickHandlerToMinus();
				addTemplateRow(false); // recursive!
				/*
				formGroup.remove(templateRow);
				removeNameTextBox(fieldNameTextBox);
				removeOrderTextBox(doTextBox);
				adjustOrderTextBoxes();
				*/
			}
		});
    	// sve the click handler so that we can remove and change it 
    	templateRow.saveClickHandler(handler);
    	
    	fieldNameTextBox.setFocus(true);
    	
    	// add handler for Enter in field name textbox
    	fieldNameTextBox.addKeyDownHandler(new KeyDownHandler()
		{
			
			@Override
			public void onKeyDown(KeyDownEvent e)
			{
				if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					changePreviousRowClickHandlerToMinus();
					addTemplateRow(false); // recursive!
				}
			}
		});
    	
    }

    // keep appending fields to the form an save to nameTextBoxes and orderTextBoxes
    @Override
    public void addFields()
    {
        gwtLog("++++++++++++++++++++++++++++++++= addFields ++++++++++++++++++++++++++++++++++");
        ObidosMessages lang = ObidosMessages.LANG;
//        getView().getName1FlowPanel().getElement().getStyle().setMarginBottom(10, Unit.PX);

        FormGroup formGroup = getView().getAddFormGroup();

        // row starts --
        // field name
        Row row1 = new Row();
        FlowPanel fp1 = new FlowPanel();
        fp1.addStyleName("col-sm-offset-2 col-sm-4");
        // field name
        TextBox textBox = new TextBox();
        textBox.setPlaceholder(lang.fieldName());
        fp1.add(textBox);
        row1.add(fp1);

        nameTextBoxes.add(textBox);

        // display order
        FlowPanel fp2 = new FlowPanel();
        fp2.addStyleName("col-sm-3");
        ObidosIntegerTextBox doTextBox = new ObidosIntegerTextBox();
        doTextBox.setPlaceholder(lang.displayOrder());
        fp2.add(doTextBox);
        row1.add(fp2);

        orderTextBoxes.add(doTextBox);
        int doVal = orderTextBoxes.size() + 1;
        gwtLog("order text boxes size: " + doVal);
        doTextBox.setValue(doVal);

        // minus button
        FlowPanel fp3 = new FlowPanel();
        fp3.addStyleName("col-sm-1");
        Button b = new Button();
        b.setType(ButtonType.INFO);
        b.setIcon(IconType.MINUS);
        fp3.add(b);
        row1.add(fp3);
        formGroup.add(row1);
        // row ends ---

         row1.getElement().getStyle().setMarginBottom(10, Unit.PX);

        b.addClickHandler(e ->
        {
            formGroup.remove(row1);

            clearMessage();
            nameTextBoxes.remove(textBox);
            orderTextBoxes.remove(doTextBox);
            int sz = orderTextBoxes.size();
            gwtLog("obx size: " + sz);
            for (int i = 2; i <= sz + 1; i++)
            {
                int idx = i - 2;
                gwtLog("Getn value at aindex: " + idx);
                ObidosIntegerTextBox otbox = orderTextBoxes.get(idx);
                otbox.setValue(i);
            }

        });

        setKeyUpEventHandler(textBox);
    }

    @Override
    public void saveTemplate()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        clearMessage();

        int templateType = getTemplateTypeFromUrl();
        final String templateTypeStr;

        if (templateType == ObidosConstants.TEMPLATE_TYPE_UNKNOWN)
        {
            return;
        }
        if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
        {
            templateTypeStr = lang.global();
        }
        else if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
        {
            templateTypeStr = lang.personal();
        }
        else
        {
            templateTypeStr = lang.freeFormat();
        }

        // verify template name has value
        String templateName = getView().getTempalteNameTextBox().getValue();
        if (templateName == null || templateName.length() == 0)
        {
            showErrorMessage(lang.noTemplateNameSpecified());
            return;
        }
        gwtLog("Template Name: " + templateName);

        // verify first pre built name row has value
        // order field is always 1
        ObidosTemplateRow otrow = getView().getFirstRow();
        String name1 = otrow.getFieldNameTextBox().getValue();
        if (name1 == null || name1.length() == 0)
        {
            showErrorMessage("Please fill up all the Field Names");
            return;
        }
        gwtLog("Name1: " + name1);

        // make sure all dynamic fields are filled
        for (TextBox textBox : nameTextBoxes)
        {
            String name = textBox.getValue();
            if (name == null || name.length() == 0)
            {
                showErrorMessage("Please fill up all the Field Names");
                return;
            }
            gwtLog(" >>> Field: " + name);
        }

        // value sure values in display order are sane
        boolean rc = validateDisplayOrderValues();
        if (rc == false)
        {
            return;
        }


        // dynamic fields
        UserDefinedFieldDTO field = null;

        int numberOfTextBoxes = nameTextBoxes.size();
        int numberOfOrderTextBoxes = orderTextBoxes.size();

        if (numberOfTextBoxes != numberOfOrderTextBoxes)
        {
            showErrorMessage("Number of fields and  display order text fields do not match");
            return;
        }
        gwtLog("Number of fields: " + numberOfTextBoxes);

        UserDefinedTypeDTO typeDTO = new UserDefinedTypeDTO();
        typeDTO.setName(templateName);

        // add the pre-built fields at first row, display order is always 1
        gwtLog("Add pre-built fields..");
        field = new UserDefinedFieldDTO();
        field.setName(name1);
        field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED); // always encrypted by the server anyway
        field.setPosition(1);
        typeDTO.addField(field);

         gwtLog("    Field1 Name: " + name1);

         // add dynamic fields
         Integer position = 1;
        for (int i = 0; i < numberOfTextBoxes; i++)
        {
            TextBox nameTextBox = nameTextBoxes.get(i);
            ObidosIntegerTextBox orderTextBox = orderTextBoxes.get(i);
            String name = nameTextBox.getValue();
            position = orderTextBox.getValue();

            gwtLog("    Field Name: " + name);
            gwtLog("Field Position: " + position);

            field = new UserDefinedFieldDTO();
            field.setName(name);
            field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED); // always encrypted by the server anyway
            field.setPosition(position);
            typeDTO.addField(field);
        }
        
        // add document type 
        if (getView().getAttachDocumentToggleSwitch().getValue())
        {
        	position = position + 1;
            field = new UserDefinedFieldDTO();
            field.setName(glang.canAttachDocument());
            field.setType(UserDefinedFieldDTO.TYPE_DOCUMENT);
            field.setPosition(position);
            typeDTO.addField(field);
        }
        
        // add 2FA info
        if (getView().getAdd2FAInfoToggleSwitch().getValue())
        {
        	position = position + 1;
            field = new UserDefinedFieldDTO();
            field.setName(glang.canAttachDocument());
            field.setType(UserDefinedFieldDTO.TYPE_QRCODE);
            field.setPosition(position);
            typeDTO.addField(field);
        }

        GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
        {

            @Override
            public void uponSuccess(Long result)
            {
                clearFormGroups();

                String templateName = getView().getTempalteNameTextBox().getValue();
                getView().getTempalteNameTextBox().clear();
                otrow.getFieldNameTextBox().clear();
                int templateType = getTemplateTypeFromUrl();
                if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
                {
                        showMessage(ObidosMessages.LANG.globalTemplateCreatedSuccessFully(templateName));
                }
                else if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
                {
                        showMessage(ObidosMessages.LANG.personalTemplateCreatedSuccessFully(templateName));
                }
                // make Plus button visible again
                makeFirstRowIconVisible();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
            	if (caught != null)
            	{
            		showErrorMessage("Could not save Template: " + templateTypeStr + ":" + caught.getMessage());
            	}
            	else
            	{
            		showErrorMessage("Could not save Template " + templateTypeStr);
            	}
            }
        };
        typeDTO.setPersonal(true);
        if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
        {
            gwtLog("Saving Personal Template");
            typeDTO.setPersonal(true);
        }
        else if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
        {
            gwtLog("+++++++++++++++++++++++++++++++++++++++++++ Saving Global Template");
            typeDTO.setPersonal(false);
        }
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().create(authCreds, typeDTO,  callback);
    }

    public void saveTemplateOld()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        clearMessage();

        int templateType = getTemplateTypeFromUrl();
        final String templateTypeStr;

        if (templateType == ObidosConstants.TEMPLATE_TYPE_UNKNOWN)
        {
            return;
        }
        if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
        {
            templateTypeStr = lang.global();
        }
        else if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
        {
            templateTypeStr = lang.personal();
        }
        else
        {
            templateTypeStr = lang.freeFormat();
        }

        // verify template name has value
        String templateName = getView().getTempalteNameTextBox().getValue();
        if (templateName == null || templateName.length() == 0)
        {
            showErrorMessage(lang.noTemplateNameSpecified());
            return;
        }
        gwtLog("Template Name: " + templateName);

        // verify first pre built name row has value
        // order field is always 1
        // make sure all dynamic fields are filled
        for (TextBox textBox : nameTextBoxes)
        {
            String name = textBox.getValue();
            if (name == null || name.length() == 0)
            {
                showErrorMessage("Please fill up all the Field Names");
                return;
            }
            gwtLog(" >>> Field: " + name);
        }

        // value sure values in display order are sane
        boolean rc = validateDisplayOrderValues();
        if (rc == false)
        {
            return;
        }


        // dynamic fields
        UserDefinedFieldDTO field = null;

        int numberOfTextBoxes = nameTextBoxes.size();
        int numberOfOrderTextBoxes = orderTextBoxes.size();

        if (numberOfTextBoxes != numberOfOrderTextBoxes)
        {
            showErrorMessage("Number of fields and  display order text fields do not match");
            return;
        }
        gwtLog("Number of fields: " + numberOfTextBoxes);

        UserDefinedTypeDTO typeDTO = new UserDefinedTypeDTO();
        typeDTO.setName(templateName);

        // add the pre-built fields at first row, display order is always 1
        gwtLog("Add pre-build fields..");
        field = new UserDefinedFieldDTO();
        field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED); // always encrypted by the server anyway
        field.setPosition(1);
        typeDTO.addField(field);

         // add dynamic fields
        for (int i = 0; i < numberOfTextBoxes; i++)
        {
            TextBox nameTextBox = nameTextBoxes.get(i);
            ObidosIntegerTextBox orderTextBox = orderTextBoxes.get(i);
            String name = nameTextBox.getValue();
            Integer position = orderTextBox.getValue();

            gwtLog("    Field Name: " + name);
            gwtLog("Field Position: " + position);

            field = new UserDefinedFieldDTO();
            field.setName(name);
            field.setType(UserDefinedFieldDTO.TYPE_ENCRYPTED); // always encrypted by the server anyway
            field.setPosition(position);
            typeDTO.addField(field);
        }

        GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
        {

            @Override
            public void uponSuccess(Long result)
            {
            	Date d = new Date();
            	String ds = ClientUtils.formattedDate(d);
                clearFormGroups();
                String message = lang.templateSavedSuccessfully(templateTypeStr, getView().getTempalteNameTextBox().getValue(), ds);
                showMessage(message);
            }

            @Override
            public void uponFailure(Throwable caught)
            {
            	if (caught != null)
            	{
            		showErrorMessage("Could not save Template: " + templateTypeStr + ":" + caught.getMessage());
            	}
            	else
            	{
            		showErrorMessage("Could not save Template " + templateTypeStr);
            	}
            }
        };
        typeDTO.setPersonal(true);
        if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
        {
            gwtLog("Saving Personal Template");
            typeDTO.setPersonal(true);
        }
        else if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
        {
            gwtLog("+++++++++++++++++++++++++++++++++++++++++++ Saving Global Template");
            typeDTO.setPersonal(false);
        }
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().create(authCreds, typeDTO,  callback);
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
    public void textBoxKeyUpHandler()
    {
        showMessage("");
    }

    @Override
    public void showHideHelp()
    {
    	getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
    }

    void clearFormGroups()
    {
        gwtLog("Clearing formgroups...");

        nameTextBoxes.clear();
        orderTextBoxes.clear();
        templateRows.clear();

        // ad the first pre-built fields

        FormGroup fg = getView().getAddFormGroup();
        int widgetCount = fg.getWidgetCount();
        gwtLog("Widget count: " + widgetCount);
        fg.clear();
    }

    private int getTemplateTypeFromUrl()
    {
        String key = ObidosConstants.TEMPLATE_TYPE;
        String templateType = null;
        try
        {
            templateType = ClientUtils.getParameterFromUrl(placeManager, key);
            if (templateType.equals(ObidosConstants.PERSONAL))
            {
                return ObidosConstants.TEMPLATE_TYPE_PERSONAL;
            }
            if (templateType.equals(ObidosConstants.GLOBAL))
            {
                return ObidosConstants.TEMPLATE_TYPE_GLOBAL;
            }

        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " from URL");
            return ObidosConstants.TEMPLATE_TYPE_UNKNOWN;
        }
        return ObidosConstants.TEMPLATE_TYPE_UNKNOWN;
    }

    private void updateHeadings()
    {
        int templateType = getTemplateTypeFromUrl();
        if (templateType == ObidosConstants.TEMPLATE_TYPE_UNKNOWN)
        {
            return;
        }

        if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
        {
            String title = ObidosMessages.LANG.createPersonalTemplate();
            getView().getPanelHeader().setHeadingText(title);
            setSaveButtonTitle(ObidosMessages.LANG.create());
            getView().getTempalteNameTextBox().setPlaceholder(ObidosMessages.LANG.nameOfPersonalTemplate());
        }
        else if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
        {
            String title = ObidosMessages.LANG.createGlobalTemplate();
            getView().getPanelHeader().setHeadingText(title);
            setSaveButtonTitle(ObidosMessages.LANG.create());
            getView().getTempalteNameTextBox().setPlaceholder(ObidosMessages.LANG.nameOfGlobalemplate());
        }
    }

    private void updateHelpMessage()
    {
    	int templateType = ClientUtils.getTemplateTypeFromUrl(placeManager);
    	if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
    	{
    		getView().getParagraph().setHTML(ObidosMessages.LANG.createPersonalTemplateHelp());
    		
    	}
    	else if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
    	{
    		getView().getParagraph().setHTML(ObidosMessages.LANG.createGlobalTemplateHelp());
    	}
    	else
    	{
    		
    	}
    }

    private void listTemplates(int templateType)
    {

        switch(templateType)
        {
            case ObidosConstants.TEMPLATE_TYPE_PERSONAL:
            {
                gwtLog("Show personal template list");
                String nameToken = NameTokens.LIST_TEMPLATES;
                Map<String,String> with = new HashMap<>();
                String key = ObidosConstants.TEMPLATE_TYPE;
                String value = ObidosConstants.PERSONAL;
                with.put(key, value);
                ClientUtils.showPage(placeManager, nameToken, with);
                break;
            }

            case ObidosConstants.TEMPLATE_TYPE_GLOBAL:
            {
                gwtLog("Show global template list");
                String nameToken = NameTokens.LIST_TEMPLATES;
                Map<String,String> with = new HashMap<>();
                String key = ObidosConstants.TEMPLATE_TYPE;
                String value = ObidosConstants.GLOBAL;
                with.put(key, value);
                ClientUtils.showPage(placeManager, nameToken, with);
            }
            default:
            {
                break;
            }
        }
    }

	@Override
	public void back()
	{
       	Map<String, String> with = new HashMap<>();
       	int templateType = ClientUtils.getTemplateTypeFromUrl(placeManager);
       	if (templateType == ObidosConstants.TEMPLATE_TYPE_PERSONAL)
       	{
       		String key = ObidosConstants.TEMPLATE_TYPE;
       		String value = ObidosConstants.PERSONAL;
       		with.put(key, value);
       		ClientUtils.showPage(placeManager, NameTokens.LIST_TEMPLATES, with);
       	}
       	else if (templateType == ObidosConstants.TEMPLATE_TYPE_GLOBAL)
       	{
       		String key = ObidosConstants.TEMPLATE_TYPE;
       		String value = ObidosConstants.GLOBAL;
       		with.put(key, value);
       		ClientUtils.showPage(placeManager, NameTokens.LIST_TEMPLATES, with);
       	}
       	else
       	{
       		
       	}
		
	}

    private void enforceLicenseRestrictions()
    {
        ToggleSwitch tsD = getView().getAttachDocumentToggleSwitch();
        ToggleSwitch tsQ = getView().getAdd2FAInfoToggleSwitch();
		tsD.setValue(true);
        tsD.setEnabled(true);
		tsD.setLabelText("");

		tsQ.setValue(true);
        tsQ.setEnabled(true);
		tsQ.setLabelText("");
        if (currentUser == null) // all bets are off
        {
        	return;
        }
   		UserDTO userDTO = currentUser.getUserDTO();
   		if (userDTO == null) // all bets are off
   		{
   			return;
   		}
		LicenseStats license = userDTO.getLicense();
		if (license == null) // all bets are off
		{
			return;
		}
		String t = glang.notSupportedByLicense();
		boolean supported = ClientUtils.fromBoolean(license.getSupportsDocumentUpload());
		gwtLog("MMM 7");
		// Title does not work as tooltip for switches, probably because they're
		// JavaScript objects and not GWT widgets.
		Label label = getView().getLicenseLabelDoc();
		label.setVisible(false);
		if (! supported)
		{
			tsD.setValue(false);
			tsD.setEnabled(false);
			label.setVisible(true);
			label.setText(t);
//			tsD.setLabelText(t);
		}
		supported = ClientUtils.fromBoolean(license.getSupportQRCodeUpload());
		label = getView().getLicenseLabel2FA();
		label.setVisible(false);
		if (! supported)
		{
			tsQ.setValue(false);
			tsQ.setEnabled(false);
			label.setVisible(true);
			label.setText(t);
//			tsQ.setLabelText(t);
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
