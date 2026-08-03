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

package com.spenego.Obidos.client.application.ldapconfig;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.ldapconfig.LDAPConfigPresenter.FieldNumber;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.util.ClientUtils;

/**
 * @author spgdev@spenego.com
 * Aug 28, 2017 9:25:59 AM - first cut
 */
/**
 * @author spgdev@spenego.com
 * Aug 28, 2017 9:26:06 AM - first cut
 */
class LDAPConfigView extends ViewWithUiHandlers<LDAPConfigUiHandlers> implements LDAPConfigPresenter.MyView
{
    interface Binder extends UiBinder<Widget, LDAPConfigView>
    {
    }

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph helpParagraph;

    @UiField
    FormLabel configNameLabel;

    @UiField
    TextBox configNameTextBox;

    @UiField
    FormLabel ldapUriLabel;

    @UiField
    TextBox uriTextBox;

    @UiField
    FormLabel baseDnLabel;

    @UiField
    TextBox basednTextBox;

    @UiField
    FormLabel bindDnLabel;

    @UiField
    TextBox binddnTextBox;

    @UiField
    FormLabel bindPasswordLabel;

    @UiField
    ObidosPasswordBox bindpassTextBox;

    @UiField
    FormLabel authAttrLabel;

    @UiField
    TextBox authAttrTextBox;

    @UiField
    Button testConnectionButton;

    @UiField
    Button saveButton;

    @UiField
    Button resetButton;

    @UiField
    Button showHideBindPassButton;

	@UiField
	ObidosMessageRow messageRow;

	@UiField
	ObidosMessageRow testMessageRow;
	
	@UiField
	ObidosButtonToolBar buttonToolBarBottom;

	@UiField
	ObidosButtonToolBar buttonToolBarTest;

	@UiField
	ObidosTextBox testUsernameTextBox;
	
	@UiField
	ObidosPasswordBox testPasswordBox;
	
	@UiField
	Button showHideTestPassButton;

	@UiField
	CheckBox startTLSCheckBox;

	@UiField
	HTMLPanel processingPanel;

	@UiField
	Button testAuthenticationButton;

    @Inject
    LDAPConfigView(Binder uiBinder)
    {
        initWidget(uiBinder.createAndBindUi(this));
        Button helpButton = panelHeader.getHelpButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().showHideHelp();
				}
			});
		}
		Button backButton = panelHeader.getBackButton();
		backButton.addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				getUiHandlers().goBack();
			}
		});
		ClientUtils.addTextBoxKeyDownHandler(configNameTextBox, messageRow);
		ClientUtils.addTextBoxKeyDownHandler(uriTextBox, messageRow);
		ClientUtils.addTextBoxKeyDownHandler(basednTextBox, messageRow);
		ClientUtils.addTextBoxKeyDownHandler(binddnTextBox, messageRow);
		ClientUtils.addPasswordBoxKeyDownHandler(bindpassTextBox, messageRow);

		ClientUtils.addTextBoxKeyDownHandler(testUsernameTextBox, testMessageRow);
//		ClientUtils.addPasswordBoxKeyDownHandler(testPasswordBox, testMessageRow);
    }

    @UiHandler("saveButton")
    void onClickADLDAPSettings(ClickEvent e)
    {
  		getUiHandlers().saveLDAPSettings();
    }

    @UiHandler("resetButton")
    void onClickResetButton(ClickEvent e)
    {
        getUiHandlers().fetchLdapSettings();
    }

    @UiHandler("showHideBindPassButton")
    void onClickshowHideBindPassButton(ClickEvent e)
    {
        ClientUtils.toggleEyeIcon(showHideBindPassButton,bindpassTextBox);
    }
    
    @UiHandler("showHideTestPassButton")
    void onclickShowHideTestPassButton (ClickEvent e)
	{
        ClientUtils.toggleEyeIcon(showHideTestPassButton, testPasswordBox);
	}

    @UiHandler("testConnectionButton")
    void onClickTestConnectionButton(ClickEvent e)
    {
    	messageRow.showMessage("");
    	getUiHandlers().testLDAPConnection();
    }
    
    @UiHandler("testAuthenticationButton")
    void onclickTestAuthenticationButton (ClickEvent e)
	{
    	messageRow.showMessage("");
    	getUiHandlers().testLDAPAuthentication();
	}

    @UiHandler("configNameTextBox")
    void onKeyUpconfigNameTextBox(KeyUpEvent e)
    {
        getUiHandlers().showFormFieldChange(FieldNumber.configName);
    }

    @UiHandler("uriTextBox")
    void onKeyUpuriTextBox(KeyUpEvent e)
    {
        getUiHandlers().showFormFieldChange(FieldNumber.ldapURI);
    }

    @UiHandler("basednTextBox")
    void onKeyUpbasednTextBox(KeyUpEvent e)
    {
        getUiHandlers().showFormFieldChange(FieldNumber.baseDN);
    }

    @UiHandler("binddnTextBox")
    void onKeyUpbinddnTextBox(KeyUpEvent e)
    {
        getUiHandlers().showFormFieldChange(FieldNumber.bindDN);
    }

    @UiHandler("bindpassTextBox")
    void onKeyUpbindpassTextBox(KeyUpEvent e)
    {
        getUiHandlers().showFormFieldChange(FieldNumber.bindPassword);
    }
    
    @UiHandler("authAttrTextBox")
    void onKeyUpAuthAttrTextBox (KeyUpEvent e)
	{
        getUiHandlers().showFormFieldChange(FieldNumber.authAttr);
	}
    
    @UiHandler("startTLSCheckBox")
    void onClickstartTLSCheckBox(ClickEvent e)
    {
    	getUiHandlers().startTLSCheckBoxClickHandler();
    }
   
    @UiHandler("testPasswordBox")
    void onEntertestPasswordBox(KeyDownEvent e)
    {
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    		messageRow.clear();
    		getUiHandlers().testLDAPAuthentication();
    	}
    }
    

	public TextBox getConfigNameTextBox()
    {
        return configNameTextBox;
    }

    public TextBox getUriTextBox()
	{
		return uriTextBox;
	}

	public TextBox getBasednTextBox()
	{
		return basednTextBox;
	}

	public TextBox getBinddnTextBox()
	{
		return binddnTextBox;
	}

	public ObidosPasswordBox getBindpassTextBox()
	{
		return bindpassTextBox;
	}

	public TextBox getAuthAttrTextBox()
	{
		return authAttrTextBox;
	}
	

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public Button getTestConnectionButton()
	{
		return testConnectionButton;
	}

	public Button getSaveButton()
	{
		return saveButton;
	}

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    public Button getShowHideBindPassButton()
    {
        return showHideBindPassButton;
    }

    public FormLabel getConfigNameLabel()
    {
        return configNameLabel;
    }

    public FormLabel getLdapUriLabel()
    {
        return ldapUriLabel;
    }

    public FormLabel getBaseDnLabel()
    {
        return baseDnLabel;
    }

    public FormLabel getBindDnLabel()
    {
        return bindDnLabel;
    }

    public FormLabel getBindPasswordLabel()
    {
        return bindPasswordLabel;
    }

    public FormLabel getAuthAttrLabel()
    {
        return authAttrLabel;
    }


    public Button getResetButton()
    {
        return resetButton;
    }

	public ObidosButtonToolBar getButtonToolBarBottom()
	{
		return buttonToolBarBottom;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public ObidosMessageRow getTestMessageRow()
	{
		return testMessageRow;
	}

	public ObidosButtonToolBar getButtonToolBarTest()
	{
		return buttonToolBarTest;
	}

	public ObidosTextBox getTestUsernameTextBox()
	{
		return testUsernameTextBox;
	}

	public ObidosPasswordBox getTestPasswordBox()
	{
		return testPasswordBox;
	}

	public Button getShowHideTestPassButton()
	{
		return showHideTestPassButton;
	}

	public CheckBox getStartTLSCheckBox()
	{
		return startTLSCheckBox;
	}

	public HTMLPanel getProcessingPanel()
	{
		return processingPanel;
	}

	public Button getTestAuthenticationButton()
	{
		return testAuthenticationButton;
	}
}
