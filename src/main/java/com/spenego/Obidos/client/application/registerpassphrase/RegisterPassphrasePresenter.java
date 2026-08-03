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

package com.spenego.Obidos.client.application.registerpassphrase;


import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
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
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class RegisterPassphrasePresenter
        extends Presenter<RegisterPassphrasePresenter.MyView, RegisterPassphrasePresenter.MyProxy>
        implements RegisterPassphraseUiHandlers
{
    interface MyView extends View, HasUiHandlers<RegisterPassphraseUiHandlers>
    {
        public Paragraph getPassphraseParagraph();
        public ObidosPasswordBox getPassphraseBox();
        public BlockQuote getHelpBlockQuote();
        public Button getLaterButton();
        public ObidosMessageRow getMessageRow();
        public ObidosButtonToolBar getButtonToolBarBottom();
        public ObidosPanelHeader getPanelHeader();
        public Button getShowHidePassphrasedButton();
    }

    @NameToken(NameTokens.REGISTER_PASSPHRASE)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<RegisterPassphrasePresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;
    private final TokenFormatter tokenFormatter;

    @Inject
    RegisterPassphrasePresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser,
            TokenFormatter tokenFormatter)
    {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

        this.placeManager = placeManager;
        this.currentUser = currentUser;
        this.tokenFormatter = tokenFormatter;

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        showMessage("");
    }

    protected void onReveal()
    {
        super.onReveal();
        resetForm();
    }

    protected void onHide()
    {
        super.onHide();
        resetEyeIcon();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        resetEyeIcon();
        if (ClientUtils.isAdmin(currentUser))
        {
        	return;
        }
        showMessage("");
        adjustButtonsWidth();
        showLaterButton(false);
        updateInfoParagraph();
        resetForm();
        checkLoginStage();
        setFocusOnTextField();
    }
    
   private void resetEyeIcon()
   {
	   getView().getPassphraseBox().getElement().setAttribute("type","password");
	   getView().getShowHidePassphrasedButton().setType(ButtonType.PRIMARY);
	   getView().getShowHidePassphrasedButton().setIcon(IconType.EYE);

   }

    private void adjustButtonsWidth()
    {
    	getView().getButtonToolBarBottom().adjustButtonsWidth();
    }

    private void setFocusOnTextField()
    {
    	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
		{

			@Override
			public void execute()
			{
				getView().getPassphraseBox().setFocus(true);
			}
	
		});
    }

    private void checkLoginStage()
    {
        String loginStage = null;
        String key = ObidosConstants.LOGIN_STAGE;
        try
        {
            loginStage = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            return;
        }
        if (loginStage.equals(ObidosConstants.LOGIN))
        {
            showLaterButton(true);
        }
    }

    private void updateInfoParagraph()
    {
        Paragraph p = getView().getPassphraseParagraph();
        String html = ObidosMessages.LANG.registerPassphraseHelpHtml();
        p.setHTML(html);
    }

    private void resetForm()
    {
    	showMessage("");
        getView().getPassphraseBox().setValue("");
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    
    /**
     * Registering passphrase is needed before:
     * - Displaying a Note
     * - Add Users to a Group
     */

    @Override
    public void registerPassphrase()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        String passphrase = getView().getPassphraseBox().getValue();
        if (passphrase == null || passphrase.length() == 0)
        {
        	showErrorMessage(lang.specifyPassphrase());
        	return;
        }

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                gwtLog("ERROR: Could not cache passphrase");
                showErrorMessage(ObidosMessages.LANG.invalidPassphrase());
                getView().getPassphraseBox().setFocus(true);
            }

            @Override
            public void uponSuccess(Void result)
            {
                if (currentUser != null)
                {
                    currentUser.setPassphraseRegistered(Boolean.TRUE);
					// Nag if 2FA is not enabled
					UserDTO dto = currentUser.getUserDTO();
					boolean twoFactorRequired = ClientUtils.fromBoolean(dto.getTwoFARequired());
					boolean twoFactorEnabled = ClientUtils.fromBoolean(dto.getTwoFAPasswordResetEnabled());
					String message = ObidosMessages.LANG.twoFactorNotEnabledWarning();

					// required but not enabled. User will not be able to reset
					// password or passphrase
					gwtLog("2FA required: " + twoFactorRequired);
					gwtLog("2FA enabled: " + twoFactorEnabled);
					if (twoFactorRequired && !twoFactorEnabled)
					{
						// required but not enabled
						message = ObidosMessages.LANG.twoFactorRequiredButNotEnabledWarning();
						gwtLog(message);
					}
					
					if (!twoFactorRequired && !twoFactorEnabled)
					{
						// not required and not enabled, this is deadly!
						// black hat can reset password or passphrase  if the
						// user's email account is compromised.
						message = ObidosMessages.LANG.twoFactorNotEnabledWarning();
						gwtLog(message);
					}

					if (twoFactorRequired && !twoFactorEnabled)
					{
						promptToEnable2FA(message);
						return;
					}
					else
					{
						navigateToDestinationPage();
					}

                }
            }
        };
        AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().cachePassphrase(authCredsDTO, passphrase.getBytes(), null, callback);
    }
    
    private String getLoginStateFromUrl()
    {
		try
		{
            String key = ObidosConstants.LOGIN_STAGE;
			return ClientUtils.getParameterFromUrl(placeManager, key);
		} catch (ParamNotFoundException e)
		{
		}
		return null;

    }

    private String getPlaceFromUrl()
    {
		try
		{
			return ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.PLACE);
		} catch (ParamNotFoundException e1)
		{
		}
		return null;
    }
    
    private void promptToEnable2FA(String message)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
    	String title = lang.warning();

        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
            	// catch Esc or click on x
            	promptToEnable2FA(message);
            	return;
            }
        });

        // No
       options.addButton(lang.enable2FALater(), ButtonType.DANGER.getCssName(), new SimpleCallback()
       {
            @Override
            public void callback()
            {
            	navigateToDestinationPage();
            }
       });

        // Yes
        options.addButton(lang.enable2FANow(), ButtonType.SUCCESS.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
            	String nameToken = NameTokens.TWO_FACTOR;
            	String place = ClientUtils.getPlaceFromUrl(placeManager);
            	if (place != null)
            	{
            		Map<String,String> with = new HashMap<>();
            		with.put(ObidosConstants.PLACE, place);
            		ClientUtils.showPage(placeManager, nameToken, with);
            	}
            	else
            	{
            		ClientUtils.showPage(placeManager, nameToken);
            	}
            	/*
            	Map<String,String> with = ClientUtils.makeMapFromUrl(tokenFormatter);
            	if (with != null)
            	{
            		ClientUtils.showPage(placeManager, nameToken, with);
            	}
            	else
            	{
            		ClientUtils.showPage(placeManager, nameToken);
            	}
            	*/

            }
        });
        options.setAnimate(true);
        Bootbox.dialog(options);
    }

    private void navigateToDestinationPageX()
    {
    	String nameToken = ClientUtils.getNameTokenFromUrl(placeManager);
    	gwtLog("Name Token: " + nameToken);
    	String place = ClientUtils.getPlaceFromUrl(placeManager);
    	gwtLog("Place: " + place);
    	Map<String,String> with = ClientUtils.makeMapFromUrlNoPlace(tokenFormatter);
    }
    
    // currentUser can not be null!
    private void navigateToDestinationPage()
    {
    	gwtLog("Navigate............");
   		Map<String, String> with = ClientUtils.makeMapFromUrl(tokenFormatter);
    	
    	String nameToken = ClientUtils.getNameTokenFromUrl(placeManager);
    	if (nameToken != null)
    	{
    		// user pasted or clicked on a link before login and login page
    		// added the name token
    		if (with != null && with.size() > 0)
    		{
    			gwtLog("Show nameToken: " + nameToken);
    			ClientUtils.showPage(placeManager, nameToken, with);
    			return;
    		}
    	}
    	gwtLog("nameToken is null...........");
    	ClientUtils.dumpWith(with);

    	
    	String navigateToPlace = getPlaceFromUrl();
    	String stage = getLoginStateFromUrl();
    	if (navigateToPlace != null)
    	{
    		if (with != null)
    		{
    			ClientUtils.showPage(placeManager, navigateToPlace, with);
    		}
    		else
    		{
    			ClientUtils.showPage(placeManager, navigateToPlace);
    		}
    		return;
    	}
    	else if (stage != null)
    	{
    		nameToken = ClientUtils.nameTokenForDefaultPageAfterLogin(currentUser.getUserDTO().getDefaultPage());
    		gwtLog(">>>>>>>>>>>>> nameToken: " + nameToken);
    		ClientUtils.showPage(placeManager, nameToken);
    		return;
    	}
    	else 
    	{
    		goBack();
    		return;
    	}
    }



    private void goBack()
    {
        gwtLog("Going back..");
        currentUser.setPassphraseRegistered(true);
        placeManager.navigateBack();
    }


    @Override
    public void showHideHelp()
    {
    	getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
    }

    private void gwtLog(String message)
    {
        String className = this.getClass().getSimpleName();
        ClientUtils.gwtLog(className, message);
    }

    private void showLaterButton(boolean visible)
    {
        getView().getLaterButton().setVisible(visible);

    }

    @Override
    public void registerPassphraseLater()
    {
        if (currentUser != null)
        {
            Long userid = currentUser.getUserDTO().getId();
            gwtLog("Logged in user id: " + userid);
            ClientUtils.showContainersPager(placeManager, userid);
        }
        else
        {
            showErrorMessage("Could not determine logged on user id");
        }
    }

	@Override
	public void keyDownCallback(KeyDownEvent e)
	{
		showMessage("");
    	if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
    	{
    	    registerPassphrase();
    	}
	}
}
