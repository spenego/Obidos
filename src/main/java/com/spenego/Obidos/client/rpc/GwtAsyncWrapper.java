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

package com.spenego.Obidos.client.rpc;

import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.MessageDTO;

/**
 * An abstract wrapper around GWT's AsyncCallback so that common session
 * timeout code can be implemented on onFailure(). In future some other kind
 * of errors can also be handled.
 * @author spgdev@spenego.com
 * Sep 6, 2017 12:08:49 PM - first cut
 */
public abstract class GwtAsyncWrapper<T> implements AsyncCallback<T>
{
    private HasHandlers source;

    public GwtAsyncWrapper(HasHandlers source) {
        this.source = source;
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

    public final void onFailure(Throwable caught)
    {
        String message = caught.getMessage();
        gwtLog("in GwtAsyncWrapper..exception: " + message);
        gwtLog("this.source: " + this.source);
        if (ObidosConstants.SESSION_ERROR.equals(message))
        {
            gwtLog("in GwtAsyncWrapper..SESSION_ERROR caught");
            if (this.source != null)
            {
                logout();
            }
            return;
        }

        if (ObidosConstants.ADMIN_DENIED.equals(message))
        {
            // ignore silently
            gwtLog("Ignoring silently.." + message);
            return;
        }

        if (caught instanceof StatusCodeException)
        {
        	ObidosMessages lang = ObidosMessages.LANG;
        	String error = lang.error();
            StatusCodeException e = (StatusCodeException) caught;
            switch (e.getStatusCode())
            {
                case 500:
                {
                	// Internal error, server does not know specifically what the hell happened
                	Bootbox.hideAll();
                	ClientUtils.showBootboxDialog(error, lang.serverErrorCode500());
                    break;
                }

                case 0:
                {
                	Bootbox.hideAll();
                    ClientUtils.showBootboxDialog(error,lang.serverIsTemporarilyUnavailable());
                    // do not logout however
                    break;
                }
                default:
                {
                	Bootbox.hideAll();
                    ClientUtils.showBootboxDialog(error, lang.serverErrorCode(e.getStatusCode()));
                }
            }
            return;
        }

        uponFailure(caught);
    }

    /**
     *  special logout, we do not call the one in ClientUtils
     * because that one makes a RPC call to logout and will create an infinite loop
     * session in server side is gone already, so we can't do anything there
     * <p>
     * @author spgdev@spenego.com - Dec 26, 2017
     */
    private void logout()
    {
        // send a message to ApplicationPresenter to clear client side and send us to the Login View
        MessageDTO dto = new MessageDTO();
        dto.setMessageType(ObidosConstants.SESSION_TIMED_OUT);
        String message = ClientUtils.getSessionTimedoutMessgage();
        dto.setMessage(message);
        if (this.source != null)
        {
            gwtLog("Sending session timed out message to ApplicationPresenter");
            SendMessageEvent.fire(this.source, dto);
        }
        else
        {
            gwtLog("Sorry source is null, could not send timed out message to ApplicationPresenter");
        }
    }

    public final void onSuccess(T result)
    {
        ClientUtils.updateRPCTime("GwtAsyncWrapper");
        uponSuccess(result);
    }

    public abstract void uponSuccess(T result);
    public abstract void uponFailure(Throwable caught);
}
