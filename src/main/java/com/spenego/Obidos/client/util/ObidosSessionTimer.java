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

import java.util.Date;

import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.events.SendSessionExtendMessageEvent;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.MessageDTO;

/**
 * @author spgdev@spenego.com - Nov 4, 2017
 */
public class ObidosSessionTimer
{
    private DialogOptions confirmDialogOptions;
    private static Timer timeoutTimer;
    private static Timer graceTimer;

    private static long sessionTimeoutMillis;
    private static long sessionCheckMillis;
    private static long sessionGraceMillis;

    private HandlerRegistration closeHandler;
    private static PlaceManager placeManager;

    // set by RPC wrapper when a successful RPC call is made
    private static long lastRpcCallEpochSeconds = new Date().getTime() / 1000;

    public static void start(PlaceManager placeManager,
    		CurrentUser currentUser, // not used at this time
            long sessionTimeoutMillis,
            long sessionCheckMillis,
            long sessionGraceMillis)
    {
        gwtLog("+++++++++++++++ Start new Session Timer.................");
        stop();
        ObidosSessionTimer.placeManager = placeManager;
        ObidosSessionTimer.sessionTimeoutMillis = sessionTimeoutMillis;
        ObidosSessionTimer.sessionCheckMillis = sessionCheckMillis;
        ObidosSessionTimer.sessionGraceMillis = sessionGraceMillis;


        new ObidosSessionTimer();
    }

    private static void cancelTimer(Timer timer)
    {
        if (timer != null)
        {
            gwtLog("in cancelTimer... ");
            timer.cancel();
            timer = null;
        }
    }

    public static void cancelTimers()
    {
        gwtLog("Cancelling timers...");
        cancelSessionCheckTimer();
        cancelGraceTimer();
    }

    private static void cancelSessionCheckTimer()
    {
        gwtLog("Cancel session check timer...");
        cancelTimer(timeoutTimer);
    }

    private static void cancelGraceTimer()
    {
        gwtLog("Cancel grace timer...");
        cancelTimer(graceTimer);
    }
    public static void stop()
    {
        cancelTimers();
    }
    public ObidosSessionTimer()
    {
        gwtLog("Creating dialog options..");
        String title = ObidosMessages.LANG.idleLogoutWarningTitle();
        String message = ObidosMessages.LANG.idleLogoutWarningMessage();

        confirmDialogOptions = DialogOptions.newOptions(message);
        confirmDialogOptions.setTitle(title);
        confirmDialogOptions.setCloseButton(false);
        confirmDialogOptions.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
                gwtLog("Escape caught..");
            }

        });


        confirmDialogOptions.addButton(ObidosMessages.LANG.keepMeLoggedInButtonTitle(), ButtonType.SUCCESS.getCssName(),new SimpleCallback()
        {
            @Override
            public void callback()
            {
                gwtLog("Session Cancel clicked... extend session.");
                cancelGraceTimer();
                rescheduleCheckTimer();
                sendRefreshMessageToSessionInfoPresenter();
            }
        });

        // logout if user did not interact with grace popup dialog
        graceTimer = new Timer()
        {
            @Override
            public void run()
            {
                Date date = null;
                gwtLog("Session in graceTimer run(); logging out..." + ClientUtils.formattedDate(date));
                logout();
            }
        };
        gwtLog("Session Grace Timer..." + graceTimer);

        timeoutTimer = new Timer()
        {
            @Override
            public void run()
            {
                checkLastAccess();
            }
        };

        gwtLog("Session timeout timer: " + timeoutTimer);

        closeHandler = Window.addWindowClosingHandler(new Window.ClosingHandler()
        {
            @Override
            public void onWindowClosing(ClosingEvent event)
            {
                gwtLog("Session XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX in close handler....");
                cancelTimers();
            }
        });

        gwtLog("Session schedule check timer..");
        rescheduleCheckTimer();

    }

    private void sendMessageToSessionInfoPresenter(final String message)
    {
        if (ObidosSessionTimer.placeManager != null)
        {
            gwtLog("Fire '" + message + "' message to session info presenter");
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setMessageType(MessageDTO.ERROR);
            messageDTO.setMessage(message);
            SendSessionExtendMessageEvent.fire(ObidosSessionTimer.placeManager, messageDTO);
        }
    }

    private void sendRefreshMessageToSessionInfoPresenter()
    {
        sendMessageToSessionInfoPresenter(ObidosConstants.REFRESH_SESSION);
    }

    private void rescheduleCheckTimer()
    {
        Date expire =  new Date(new Date().getTime() + sessionCheckMillis);
        gwtLog("in resetTimeout.. timer will expire in ..." + toMinutes(sessionCheckMillis) + " minutes at " + expire);
        // no need to cancel timer, it will be cancelled
        timeoutTimer.schedule((int)sessionCheckMillis);
        gwtLog("Send get session message to SessionInfoPresenter..");
        sendMessageToSessionInfoPresenter(ObidosConstants.GET_SESSION);
        
        /*
         * send message to application presenter to refresh passphrase
         * cached icon
         */
        MessageDTO dto = new MessageDTO();
        dto.setMessageType(ObidosConstants.PASSPHRASE_CACHED);
        dto.setMessage("Refresh passphrase cached icon");
        SendMessageEvent.fire(ObidosSessionTimer.placeManager, dto);
    }


    private void logout()
    {
        if (closeHandler != null)
        {
            closeHandler.removeHandler();
        }
        if (graceTimer != null)
        {
            graceTimer.cancel();
            graceTimer = null;
        }
        if (timeoutTimer != null)
        {
            timeoutTimer.cancel();
            timeoutTimer = null;
        }
        Bootbox.hideAll();
        if (placeManager != null)
        {
            gwtLog("Session ObidosSessionTimer ... logging out user...");
            Bootbox.hideAll();
            Date date = new Date();
            String msg = ObidosMessages.LANG.idleLogoutMessage() + " " + ClientUtils.formattedDate(date);
            ClientUtils.logout(placeManager,msg);
        }
    }
/*
    private String getTimeoutMessage()
    {
        Date date = new Date();
        Date expireDate = new Date(date.getTime() + sessionGraceMillis);
        String message = "Your session is about to expire!\n" +
        "If you do not respond, you will be logged out in 1 minute at " + expireDate.toString();
        return message;
    }
    */
    // we just look at the static with the RPC wrapper set
    private void checkLastAccess()
    {
    			
        long lastAccessEpoch = getLastRpcCallEpoch();
        long nowEpoch = getNowEpoch();
        long sessionExpiresEpoch = lastAccessEpoch + (sessionTimeoutMillis/1000);
        long  nextCheckEpoch = nowEpoch + (sessionCheckMillis/1000);

        cancelGraceTimer();

        gwtLog("                 Now: " + epochToDate(nowEpoch));
        gwtLog("  Session Expires at: " + epochToDate(sessionExpiresEpoch));
        gwtLog("Session Next access check at: " + epochToDate(nextCheckEpoch));
        long secsLeft = sessionExpiresEpoch - nextCheckEpoch;
        gwtLog("Session Secs left: " + secsLeft);
        gwtLog("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (secsLeft <= 120)
        {
        	// if window is not visible, logout
        	if (!ClientUtils.windowisVisible())
        	{
        		gwtLog("Session Seconds left: " + secsLeft + " But window is not visible, loging out..");
        		logout();
        		return;
        	}
        		
        			
            Date date = new Date(new Date().getTime() + sessionGraceMillis);
            gwtLog("Session Schedule the check timer for 60 seconds at..." + date.toString());
            rescheduleGraceTimer();
            gwtLog("Session   popping dialog...");
            // we need send message so that form will be updated. otherwise it will not 
            // show if grace timer is running
            sendRefreshMessageToSessionInfoPresenter();
            Bootbox.hideAll();
            Bootbox.dialog(confirmDialogOptions);
            gwtLog("Session  after popping dialog");
            return;
        }
        gwtLog("  Session -- rechedule check timer ....");
        rescheduleCheckTimer();
    }

    private Date epochToDate(long epoch)
    {
        return new Date((epoch * 1000));

    }

    private Long getNowEpoch()
    {
        return new Date().getTime()/1000;
    }

    private void rescheduleGraceTimer()
    {
        gwtLog("Starting grace timer..it will expire in " + toMinutes(sessionGraceMillis) + " minutes");
        graceTimer.schedule((int)sessionGraceMillis);
    }


    private long toMinutes(long sessionCheckMillis2)
    {
        return  ((sessionCheckMillis2 / 1000)/60);
    }

    public static void setLastRpcCallEpoch(long lastRpcCallEpoch)
    {
        ObidosSessionTimer.lastRpcCallEpochSeconds = lastRpcCallEpoch;
    }

    private static void gwtLog(String message)
    {
        ClientUtils.gwtLog("ObidosSessionTimer", message);
    }

    public static boolean isSessionCheckTimerRunning()
    {
        if (timeoutTimer != null && timeoutTimer.isRunning())
        {
            return true;
        }
        return false;
    }

    public static boolean isGraceTimerRunning()
    {
        if (graceTimer != null && graceTimer.isRunning())
        {
            return true;
        }
        return false;
    }

    /**
     * @return epoch when last RPC call was made
     * <p>
     * @author spgdev@spenego.com - Mar 30, 2019
     */
    public static long getLastRpcCallEpoch()
    {
        return lastRpcCallEpochSeconds;
    }
   
    /**
     * @return the session timeout interval in seconds. This is configured in 
     * the servlet.
     * <p>
     * @author spgdev@spenego.com - Mar 30, 2019
     */
    public static int getSessionTimeoutIntervalSeconds()
    {
    	return (int) (sessionTimeoutMillis / 1000);
    }
    
    /**
     * @return epoch when the session will timeout
     * <p>
     * @author spgdev@spenego.com - Mar 30, 2019
     */
    public static long getSessionTimeoutEpoch()
    {
    	return getLastRpcCallEpoch() + getSessionTimeoutIntervalSeconds();
    }
    
    /**
     * @return seconds after the session will timeout
     * <p>
     * @author spgdev@spenego.com - Mar 30, 2019
     */
    public static int getSessionWillTimeoutInSeconds()
    {
    	long timeoutEpoch = getSessionTimeoutEpoch();
    	long now = new Date().getTime() / 1000;
    	return (int) (timeoutEpoch - now);
    }
    
    /**
     * @return date when session will timeout
     * <p>
     * @author spgdev@spenego.com - Mar 30, 2019
     */
    public static Date getSessionWillTimeoutAt()
    {
    	long timeoutEpoch = getSessionTimeoutEpoch();
    	return new Date(timeoutEpoch * 1000);
    }

}
