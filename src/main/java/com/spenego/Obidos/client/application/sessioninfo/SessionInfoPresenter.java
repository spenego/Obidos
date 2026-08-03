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

package com.spenego.Obidos.client.application.sessioninfo;

import java.util.Date;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.events.SendSessionExtendMessageEvent;
import com.spenego.Obidos.client.application.events.SendSessionExtendMessageEvent.SendSessionExtendMessageEventHandler;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosSessionTimer;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.SessionInfoDTO;

public class SessionInfoPresenter extends Presenter<SessionInfoPresenter.MyView, SessionInfoPresenter.MyProxy>
        implements SessionInfoUiHandlers,
        SendSessionExtendMessageEventHandler
{
//    private final String[] months = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsNarrow();

    interface MyView extends View, HasUiHandlers<SessionInfoUiHandlers>
    {
        public TextBox getSessionIdTextBox();
        public TextBox getLastAccessTextBox();
        public TextBox getExpiresTextBox();
        public TextBox getCreatedTextBox();
        public TextBox getMaxInactiveTextBox();
        public TextBox getNowTextBox();
        public TextBox getDialogPopsTextBox();
        public TextBox getSessionCheckTimerTextBox();
        public TextBox getGraceTimerTextBox();
        public BlockQuote getHelpBlockQuote();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
    }

    @NameToken(NameTokens.SESSION_INFO)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class)
    interface MyProxy extends ProxyPlace<SessionInfoPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    SessionInfoPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage("");
        getSessionInfo();
        updatePanelHeaderColor();

    }

    private void updatePanelHeaderColor()
    {
    	ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
    }

    private String formattedDate(Date date)
    {
        return ClientUtils.formattedDate(date);
    }

    private void gwtLog(String message)
    {
    	ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    private void logout(final String message)
    {
    	if (message != null)
    	{
    		ClientUtils.logout(this, message);
    	}
    	else
    	{
    		ClientUtils.logout(this, ObidosMessages.LANG.loggedOutMsg());
    	}
    }

    public void getSessionInfo()
    {
        // don't call the async wrapper because it will update the session access timer
        // we don't want to do that here
        AsyncCallback<SessionInfoDTO> callback = new AsyncCallback<SessionInfoDTO>()
        {

            @Override
            public void onFailure(Throwable caught)
            {
				if (caught instanceof StatusCodeException)
				{
					ObidosMessages lang = ObidosMessages.LANG;
					StatusCodeException e = (StatusCodeException) caught;
					String errorMessage = null;
					String date = new Date().toString();
					switch (e.getStatusCode())
					{
						case 500:
						{
							// Internal error, server does not know specifically what the hell happened
							errorMessage = lang.loggedOutAt(date) + ": " + lang.serverErrorCode500();
							break;
						}

						case 0:
						{
							errorMessage = lang.loggedOutAt(date) + ": " + lang.serverIsTemporarilyUnavailable();
							// do not logout however. Q: why?
							break;
						}
						default:
						{
							errorMessage = lang.loggedOutAt(date) + ": " + lang.serverErrorCode(e.getStatusCode());
						}
					}
					logout(errorMessage);
				}
            }

            @Override
            public void onSuccess(SessionInfoDTO dto)
            {
                if (dto.getExpireEpoch() == null)
                {
//                    showErrorMessage("Sorry, your session has expired due to inactivity...");
                    // no point to hang around
                    logout(null);
                    return;
                }
                // Note java Date uses milli seconds for most stuff
                gwtLog("Updating Session information..........................");
                gwtLog("++ Window has focus..." + ClientUtils.windowHasFocus());
                gwtLog("++ Windows is visibe: " + ClientUtils.windowisVisible());

                // session id
                getView().getSessionIdTextBox().setValue(dto.getSessionId());

                // session creation time
                Date creationTime = new Date(dto.getCreationTimeEpoch() * 1000);
                getView().getCreatedTextBox().setValue(formattedDate(creationTime));

                // last session access time
                long lastAccessEpochSeconds = ObidosSessionTimer.getLastRpcCallEpoch();
                gwtLog("Last RPC made epoch seconds: " + lastAccessEpochSeconds + " " + ClientUtils.formattedDate(lastAccessEpochSeconds));
                Date date = new Date(lastAccessEpochSeconds * 1000);
                getView().getLastAccessTextBox().setValue(formattedDate(date));

                // session expires at
                Long expireEpoch = dto.getExpireEpoch();
                gwtLog("Expire epoch: " + expireEpoch + " " + ClientUtils.formattedDate(expireEpoch));
                
                gwtLog("Servlet max inactive Interval: " + dto.getMaxInactiveInterval() + " Seconds");
                // is it possible that sometimes we can get it as null??
                if (dto.getMaxInactiveInterval() == null)
                {
                	gwtLog("Setting inactive interval to 1800 seconds");
                	dto.setMaxInactiveInterval(1800);
                }
                if (dto.getMaxInactiveInterval() <= 0)
                {
                	gwtLog("Setting inactive interval to 1800 seconds");
                	dto.setMaxInactiveInterval(1800);
                }
                // hack for testing....
                // also change in LoginPresenter.java around line# 215
                //	dto.setMaxInactiveInterval(300);
                // hack for testing....

                date = new Date((lastAccessEpochSeconds + dto.getMaxInactiveInterval()) * 1000);
                gwtLog("Session timeout at: " + date);
                long timeoutEpochSeconds = date.getTime() / 1000;
                currentUser.setSessionTimeoutAt(date);

                long nowSeconds = new Date().getTime()/1000;
                int sessionTimeoutInSeconds = (int) (timeoutEpochSeconds - nowSeconds);
                gwtLog(">>>>>>>>>>>>>>>>>>>>>>> timeout in: " + sessionTimeoutInSeconds + " seconds");
                currentUser.setSessionWillTimeoutInSeconds(sessionTimeoutInSeconds);

                gwtLog("Expire date: " + date);
                String formatted = ClientUtils.formattedDate(date);
                gwtLog("Formatted: " + formatted);
                getView().getExpiresTextBox().setValue(formatted);

                // dialog pops about 2 minutes before session expiration
                Date popDate = new Date(date.getTime() - 120 * 1000);
                getView().getDialogPopsTextBox().setValue(formattedDate(popDate));

                // Timeout
                int timeoutMins = dto.getMaxInactiveInterval() / 60;
                getView().getMaxInactiveTextBox().setValue(Integer.toString(timeoutMins) + " Minutes");

                // Session Timer running or not
                boolean running = ObidosSessionTimer.isSessionCheckTimerRunning();
                getView().getSessionCheckTimerTextBox().setValue(running ? "Yes" : "No");

                // Grace Timer running or not
                running = ObidosSessionTimer.isGraceTimerRunning();
                getView().getGraceTimerTextBox().setValue(running ? "Yes" : "No");

                // Updated
                getView().getNowTextBox().setValue(formattedDate(new Date()));

            }
        };
        LoginService.Utility.getInstance().sessionInfo(callback);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    public void extendSession()
    {
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                getSessionInfo();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not update session: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        LoginService.Utility.getInstance().refreshSession(authCreds, callback);
    }

    private String className()
    {
        return this.getClass().getSimpleName();
    }

    // Note to myself again: without that Fuckin @ProxyEvent the fired message will
    // never reach here

    @ProxyEvent
    @Override
    public void onSendSessionExtendMessageEvent(SendSessionExtendMessageEvent event)
    {
        // ObidosSessionTimer send these messages
		MessageDTO messageDTO = event.getMessageDTO();
        gwtLog("IN onSendSessionExtendMessageEvent.... message DTO: " + messageDTO);
		if (messageDTO == null)
		{
		    gwtLog("IN onSendSessionExtendMessageEvent.... message DTO is null");
		    return;
		}
		String message = messageDTO.getMessage();
		gwtLog(">>>  " + className() + " SessMessage '" + message + "' received");
		if (message.equals(ObidosConstants.REFRESH_SESSION))
		{
		    gwtLog(">>> Extend session..");
		    extendSession();
		}
		else if (messageDTO.getMessage().equals(ObidosConstants.GET_SESSION))
		{
		    gwtLog(">>> get session..");
		    getSessionInfo();
		}
    }

	@Override
	public void help()
	{
		getView().getHelpBlockQuote().setVisible(!getView().getHelpBlockQuote().isVisible());
		
	}
}
