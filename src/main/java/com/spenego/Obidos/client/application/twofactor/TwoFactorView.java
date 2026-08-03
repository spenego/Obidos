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

package com.spenego.Obidos.client.application.twofactor;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.util.ClientUtils;

class TwoFactorView extends ViewWithUiHandlers<TwoFactorUiHandlers>
        implements TwoFactorPresenter.MyView
{
    interface Binder extends UiBinder<Widget,TwoFactorView>
    {
    }

    @Inject
    TwoFactorView(Binder uiBinder)
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
					getUiHandlers().help();
				}
			});
		}
    }


   // Automatically generated from TwoFactorView.ui.xml by mk_uifield.rb
   // spgdev@spenego.com 2018-06-24 09:24:50 +0200, Copenhagen, Denmark
    @UiField
    HTMLPanel htmlPanel;

    @UiField
    BlockQuote helpBlockQuote;

    @UiField
    Paragraph helpParagraph;

    @UiField
    ObidosPanelHeader panelHeader;

    @UiField
    Image qrImage;

    @UiField
    Button configureButton;

    @UiField
    FlowPanel useGoogleAuthenticatorFlowPanel;

    @UiField
    HTMLPanel useGoogleAuthHtmlPanel;

    @UiField
    FlowPanel enableFlowPanel;

    @UiField
    FormLabel codeLabel;

    @UiField
    FlowPanel codeFlowPanel;

    @UiField
    TextBox codeTextBox;

    @UiField
    Button enableButton;

    @UiField
	ObidosMessageRow configure2FAMessageRow;

    @UiField
	ObidosMessageRow messageRow;


    @UiField
    HTML genSecretHTMLBody;
    
    @UiField
    ToggleSwitch requiresTwoFAPasswordResetSwitch;
    
    @UiField
    ToggleSwitch isTwoFAEnabledSwitch;
    
    @UiField
    Row secretRow;
    
    @UiField
    TextBox issuerTextBox;
    
    @UiField
    ObidosPasswordBox secretBox;
    
    @UiField
    Button showHideSecretButton;
    

    @UiHandler("configureButton")
    void onClickCconfigureButton(ClickEvent e)

    {
        getUiHandlers().create2FASecret();
    }

    @UiHandler("enableButton")
    void onClickEnableButton(ClickEvent e)
    {
        getUiHandlers().enable2FA();
    }
    
    @UiHandler("showHideSecretButton")
    void onclickShowHideSecretButton (ClickEvent e)
	{
    	ClientUtils.toggleEyeIcon(showHideSecretButton, secretBox);
	}

    public HTMLPanel getHtmlPanel()
    {
        return htmlPanel;
    }

    public BlockQuote getHelpBlockQuote()
    {
        return helpBlockQuote;
    }

    public Paragraph getHelpParagraph()
    {
        return helpParagraph;
    }

    public ObidosPanelHeader getPanelHeader()
    {
        return panelHeader;
    }

    public Image getQrImage()
    {
        return qrImage;
    }

    public Button getConfigureButton()
    {
        return configureButton;
    }

    public FlowPanel getUseGoogleAuthenticatorFlowPanel()
    {
        return useGoogleAuthenticatorFlowPanel;
    }

    public HTMLPanel getUseGoogleAuthHtmlPanel()
    {
        return useGoogleAuthHtmlPanel;
    }

    public FlowPanel getEnableFlowPanel()
    {
        return enableFlowPanel;
    }

    public FormLabel getCodeLabel()
    {
        return codeLabel;
    }

    public FlowPanel getCodeFlowPanel()
    {
        return codeFlowPanel;
    }

    public TextBox getCodeTextBox()
    {
        return codeTextBox;
    }

    public Button getEnableButton()
    {
        return enableButton;
    }

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public HTML getGenSecretHTMLBody()
	{
		return genSecretHTMLBody;
	}

	public ToggleSwitch getRequiresTwoFAPasswordResetSwitch()
	{
		return requiresTwoFAPasswordResetSwitch;
	}

	public ToggleSwitch getIsTwoFAEnabledSwitch()
	{
		return isTwoFAEnabledSwitch;
	}

	public ObidosMessageRow getConfigure2FAMessageRow()
	{
		return configure2FAMessageRow;
	}

	public Row getSecretRow()
	{
		return secretRow;
	}

	public TextBox getIssuerTextBox()
	{
		return issuerTextBox;
	}

	public ObidosPasswordBox getSecretBox()
	{
		return secretBox;
	}

	public Button getShowHideSecretButton()
	{
		return showHideSecretButton;
	}

}
