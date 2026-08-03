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

package com.spenego.Obidos.client.application.widgets.bootbox;

import com.spenego.Obidos.client.application.widgets.bootbox.callback.ConfirmCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.PromptCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.AlertOptions;
import com.spenego.Obidos.client.application.widgets.bootbox.options.BootboxLocale;
import com.spenego.Obidos.client.application.widgets.bootbox.options.ConfirmOptions;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.application.widgets.bootbox.options.PromptOptions;

//
// I ripped it out of gwtbootstrap3-extras because the project is stale and I need to
// use the latest Bootbox and should be able to update the code if I want.
// spgdev@spenego.com, Oct-01-2017

/**
 * Bootbox.js is a small JavaScript library which allows you
 * to create programmatic dialog boxes using Bootstrap modals.
 *
 * @author Xiaodong Sun
 * @see http://bootboxjs.com/
 */
public class Bootbox {

    /**
     * Displays a message in a modal dialog box.
     *
     * @param msg the message to be displayed.
     */
    public static native void alert(String msg) /*-{
        $wnd.bootbox.alert(msg);
    }-*/;

    /**
     * Displays a message in a modal dialog box.
     * With callback handler.
     *
     * @param msg      the message to be displayed.
     * @param callback the callback handler.
     */
    public static native void alert(String msg, SimpleCallback callback) /*-{
        $wnd.bootbox.alert(msg, function () {
            callback.@com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback::callback()();
        });
    }-*/;

    /**
     * Displays a customized alert with the given {@link AlertOptions}.
     *
     * @param options
     */
    public static native void alert(AlertOptions options) /*-{
        $wnd.bootbox.alert(options);
    }-*/;

    /**
     * Displays a message in a modal dialog box, along with the standard 'OK' and
     * 'Cancel' buttons.
     *
     * @param msg      the message to be displayed.
     * @param callback the callback handler.
     */
    public static native void confirm(String msg, ConfirmCallback callback) /*-{
        $wnd.bootbox.confirm(msg, function (result) {
            callback.@com.spenego.Obidos.client.application.widgets.bootbox.callback.ConfirmCallback::callback(Z)(result);
        });
    }-*/;

    /**
     * Displays a customized confirm with the given {@link ConfirmOptions}.
     *
     * @param options
     */
    public static native void confirm(ConfirmOptions options) /*-{
        $wnd.bootbox.confirm(options);
    }-*/;

    /**
     * Displays a request for information in a modal dialog box, along with the
     * standard 'OK' and 'Cancel' buttons.
     *
     * @param msg      the message to be displayed.
     * @param callback the callback handler.
     */
    public static native void prompt(String msg, PromptCallback callback) /*-{
        $wnd.bootbox.prompt(msg, function (result) {
            callback.@com.spenego.Obidos.client.application.widgets.bootbox.callback.PromptCallback::callback(Ljava/lang/String;)(result);
        });
    }-*/;

    /**
     * Displays a customized prompt with the given {@link PromptOptions}.
     *
     * @param options
     */
    public static native void prompt(PromptOptions options) /*-{
        $wnd.bootbox.prompt(options);
    }-*/;

    /**
     * Displays a completely customizable dialog in a modal dialog box.
     *
     * @param options the dialog options.
     */
    public static native void dialog(final DialogOptions options) /*-{
        $wnd.bootbox.dialog(options);
    }-*/;

    /**
     * Sets a callback when dialog gets initialized.
     *
     * @param callback
     */
    public static native void init(SimpleCallback callback) /*-{
        $wnd.bootbox.init(function() {
            if (callback)
                callback.@com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback::callback()();
        });
    }-*/;

    /**
     * Set many of the default options shown in the dialog example.<br>
     * <br>
     * Many of these options are also applied to the basic wrapper methods
     * and can be overridden whenever the wrapper methods are invoked
     * with a single options argument.
     *
     * @param options
     */
    public static native void setDefaults(DialogOptions options) /*-{
        $wnd.bootbox.setDefaults(options);
    }-*/;

    /**
     * Sets a locale.
     *
     * @param locale if <code>null</code>, defaults to {@link BootboxLocale#EN}.
     */
    public static void setLocale(final BootboxLocale locale) {
        BootboxLocale l = (locale != null) ? locale : BootboxLocale.getDefault();
        setLocale(l.getLocale());
    }

    private static native void setLocale(String locale) /*-{
        $wnd.bootbox.setLocale(locale);
    }-*/;

    /**
     * Hide all currently active bootbox dialogs.
     * <p>Individual dialogs can be closed as per normal Bootstrap dialogs: dialog.modal('hide').
     */
    public static native void hideAll() /*-{
        $wnd.bootbox.hideAll();
    }-*/;
}
