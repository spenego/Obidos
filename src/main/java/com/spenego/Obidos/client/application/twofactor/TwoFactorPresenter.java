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
import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TwoFactorService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.TwoFactorDTO;
import com.spenego.Obidos.shared.dto.UserDTO;

public class TwoFactorPresenter extends Presenter<TwoFactorPresenter.MyView,TwoFactorPresenter.MyProxy>
        implements TwoFactorUiHandlers
{
    interface MyView extends View,HasUiHandlers<TwoFactorUiHandlers>
    {
       // Automatically generated from TwoFactorView.ui.xml by mk_uifield.rb
       // spgdev@spenego.com 2018-06-24 09:24:50 +0200, Copenhagen, Denmark
       public HTMLPanel getHtmlPanel();
       public BlockQuote getHelpBlockQuote();
       public Paragraph getHelpParagraph();
       public Image getQrImage();
       public Button getConfigureButton();
       public FlowPanel getUseGoogleAuthenticatorFlowPanel();
       public HTMLPanel getUseGoogleAuthHtmlPanel();
       public FlowPanel getEnableFlowPanel();
       public FormLabel getCodeLabel();
       public FlowPanel getCodeFlowPanel();
       public TextBox getCodeTextBox();
       public Button getEnableButton();
       public ToggleSwitch getRequiresTwoFAPasswordResetSwitch();
       public ToggleSwitch getIsTwoFAEnabledSwitch();
       public HTML getGenSecretHTMLBody();
       public ObidosMessageRow getConfigure2FAMessageRow();
       public ObidosMessageRow getMessageRow();
		public Row getSecretRow();
		public TextBox getIssuerTextBox();
		public ObidosPasswordBox getSecretBox();
		public Button getShowHideSecretButton();

    }

    @NameToken(NameTokens.TWO_FACTOR)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<TwoFactorPresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;

    @Inject
    TwoFactorPresenter(EventBus eventBus,MyView view,MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus,view,proxy,ApplicationPresenter.SLOT_MAIN);

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
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        populateForm();
        showMessage("");
        show2FAConfigureMessage(null);
        resetTextBoxes();
        showSecretWidgets(false);
        showEnable2FAWidgets(false);
        showConfigureButton(true);
        showMessage(null);
        show2FAConfigureMessage(null);
    }

    private void showConfigureButton(boolean visible)
    {
    	getView().getConfigureButton().setVisible(visible);
    }

    private void resetTextBoxes()
    {
        getView().getSecretBox().setValue("");
        getView().getCodeTextBox().setValue("");
    }
    
    void enableButtons(boolean enable)
    {
    	getView().getConfigureButton().setEnabled(enable);
    	getView().getEnableButton().setEnabled(enable);
    }

    private void populateForm()
    {
    	enableButtons(true);
    	// switches must be enabled in order to set value
    	ToggleSwitch r = getView().getRequiresTwoFAPasswordResetSwitch();
    	ToggleSwitch a = getView().getIsTwoFAEnabledSwitch();
    	r.setEnabled(true);
    	a.setEnabled(true);
    	
    	if (currentUser != null)
    	{
    		UserDTO dto = currentUser.getUserDTO();
    		gwtLog("2FA required? " + dto.getTwoFARequired());
    		gwtLog("2FA enabeld? " + dto.getTwoFAPasswordResetEnabled());
    		r.setValue(dto.getTwoFARequired());
    		a.setValue(dto.getTwoFAPasswordResetEnabled());

    	}
    	r.setEnabled(false);
    	a.setEnabled(false);
    }
    
    private void create2FASecretReal()
    {
        GwtAsyncWrapper<TwoFactorDTO> callback = new GwtAsyncWrapper<TwoFactorDTO>(this)
        {

            @Override
            public void uponSuccess(TwoFactorDTO dto)
            {
//                showMessage(ObidosMessages.LANG.useGoogleAuthenticator());

                showSecretWidgets(true);
                showEnable2FAWidgets(true);

                gwtLog("Success");
                Image image = getView().getQrImage();
                String base64Image = dto.getBase64QrImage();
                image.setUrl("data:image/png;base64," + base64Image);
                
                getView().getIssuerTextBox().setValue(dto.getIssuser());
                getView().getSecretBox().setValue(dto.getSecret());
                
                getView().getConfigureButton().setEnabled(false);
                // 2FA is disabled now
                setToggleSwitch(getView().getIsTwoFAEnabledSwitch(), false);
                fire2FADisabledEvent();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                gwtLog("Failed to configure 2FA: " + caught.getMessage());
                showErrorMessage("Failed to create 2FA Secret: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TwoFactorService.Utility.getInstance().create2FASecret(authCreds, callback);
    }

    @Override
    public void create2FASecret()
    {
    	if (currentUser != null && currentUser.getUserDTO().getTwoFAPasswordResetEnabled())
    	{
    		promptReCreate2FASecret();
    	}
    	else
    	{
    		create2FASecretReal();
    	}
    }
    
    private void promptReCreate2FASecret()
    {
        String message = ObidosMessages.LANG.twoFactorReConfigureHelp();
        String title = ObidosMessages.LANG.reConfigure2FA();
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
            	create2FASecretReal();
            }
        });
        Bootbox.dialog(options);
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(),message);
    }

    @Override
    public boolean is2FAConfigured()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reConfigure2FA()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset2FA()
    {
        // TODO Auto-generated method stub

    }
    
    private void setToggleSwitch(ToggleSwitch s,boolean value)
    {
    	s.setEnabled(true);
    	s.setValue(value);
    	s.setEnabled(false);
    }
     private void fire2FADisabledEvent()
    {
    	gwtLog("Firing 2FA disabled event..");
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessageType(ObidosConstants.TWO_FACTOR_DISABLED);
		messageDTO.setMessage("2FA Disabled");
		SendMessageEvent.fire(this, messageDTO);
    }
   
    private void fire2FAEnabledEvent()
    {
    	gwtLog("Firing 2FA enabled event..");
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessageType(ObidosConstants.TWO_FACTOR_ENABLED);
		messageDTO.setMessage("2FA Enabled");
		SendMessageEvent.fire(this, messageDTO);
    }

    @Override
    public void enable2FA()
    {
        String code = getView().getCodeTextBox().getValue();
        if (code == null || code.length() == 0)
        {
            showErrorMessage(ObidosMessages.LANG.specifyCodeError());
            return;
        }
        // remove spaces
        code = code.replaceAll("\\s+", "");
        
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                getView().getEnableButton().setEnabled(false);
            	showMessage(ObidosMessages.LANG.twoFactorAutenticationIsEnabled());
            	setToggleSwitch(getView().getIsTwoFAEnabledSwitch(), true);
            	if (currentUser != null)
            	{
            		UserDTO dto = currentUser.getUserDTO();
            		dto.setTwoFAPasswordResetEnabled(true);
            	}
           		fire2FAEnabledEvent();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
            	gwtLog("ERROR: " + caught.getMessage());
            	showErrorMessage(ObidosMessages.LANG.couldNotEnabletwoFactorAutentication());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TwoFactorService.Utility.getInstance().enable2FA(authCreds, code.getBytes(), callback);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void showSecretWidgets(boolean visible)
    {
    	getView().getGenSecretHTMLBody().setVisible(visible);
    	getView().getQrImage().setVisible(visible);
    	getView().getSecretRow().setVisible(visible);
    	/*
        getView().getSecretLabel().setVisible(visible);
        getView().getSecretFlowPanel().setVisible(visible);
        */
    }

    private void showEnable2FAWidgets(boolean visible)
    {
        gwtLog("2FA widgets: " + visible);
        getView().getEnableFlowPanel().setVisible(visible);
        getView().getCodeLabel().setVisible(visible);
        getView().getCodeTextBox().setVisible(visible);
        getView().getEnableButton().setVisible(visible);
        getView().getEnableButton().setEnabled(visible);
    }

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
	}
	
	private void show2FAConfigureMessage(String message)
	{
		getView().getConfigure2FAMessageRow().showMessage(message);
	}
	private void show2FAConfigureErrorMessage(String errorMessage)
	{
		getView().getConfigure2FAMessageRow().showErrorMessage(errorMessage);
	}
}
