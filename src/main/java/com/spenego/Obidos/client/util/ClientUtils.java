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

import static com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO.PASSWORD_COMPLEXITY_REQUIREMENTS;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalShowEvent;
import org.gwtbootstrap3.client.shared.event.ModalShowHandler;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.InputGroupButton;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.ButtonDismiss;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ImageType;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ProgressBarType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Small;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.ColorType;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.vectomatic.file.FileUtils;
import org.wisepersist.gwt.uploader.client.Stats;
import org.wisepersist.gwt.uploader.client.Uploader;
import org.wisepersist.gwt.uploader.client.events.UploadErrorEvent;
import org.wisepersist.gwt.uploader.client.events.UploadErrorHandler;
import org.wisepersist.gwt.uploader.client.events.UploadProgressEvent;
import org.wisepersist.gwt.uploader.client.events.UploadProgressHandler;
import org.wisepersist.gwt.uploader.client.events.UploadSuccessEvent;
import org.wisepersist.gwt.uploader.client.events.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.Range;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.events.LogoutEvent;
import com.spenego.Obidos.client.application.events.SendMessageEvent;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosItemRow;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRowWithStyle;
import com.spenego.Obidos.client.application.widgets.ObidosNoteRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosPasswordBox;
import com.spenego.Obidos.client.application.widgets.ObidosReadonlyTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosRowTop2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosTimeBox;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.application.widgets.summernote.base.Toolbar;
import com.spenego.Obidos.client.application.widgets.summernote.base.ToolbarButton;
import com.spenego.Obidos.client.application.widgets.summernote.event.SummernoteKeyDownEvent;
import com.spenego.Obidos.client.application.widgets.summernote.event.SummernoteKeyDownHandler;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.client.rpc.DicewareService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.LoginService;
import com.spenego.Obidos.client.rpc.SessionEstablishService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.ObidosMap;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CapabilityDTO;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;
import com.spenego.Obidos.shared.dto.ComplexityRequirementsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.CountryCodeDTO;
import com.spenego.Obidos.shared.dto.DocumentDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.ItemExpiration;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedItemDTO;
import com.spenego.Obidos.shared.dto.LoginResult;
import com.spenego.Obidos.shared.dto.MessageDTO;
import com.spenego.Obidos.shared.dto.NotificationTemplateJSONDTO;
import com.spenego.Obidos.shared.dto.PassComplexityDTO;
import com.spenego.Obidos.shared.dto.PasswordAnalysisResults;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldDTO;
import com.spenego.Obidos.shared.dto.UserDefinedFieldValueDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;
import com.spenego.Obidos.shared.exceptions.ClientSideException;
import com.spenego.Obidos.shared.exceptions.ObidosException;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

/**
 * @author spgdev@spenego.com - Jan 1, 2017
 */
public class ClientUtils
{
    private static ObidosMessages glang = ObidosMessages.LANG;

    public static void clearCookies()
    {
        log(null, "Clearing cookies");
        Cookies.removeCookie(ObidosConstants.OBIDOS_LOGIN_COOKIE);
        Cookies.removeCookie(ObidosConstants.OBIDOS_SESSION);
        Cookies.removeCookie(ObidosConstants.OBIDOS_SESSION_COOKIE);
        Cookies.removeCookie(ObidosConstants.OBIDOS_USERNAME_COOKIE);
        Cookies.removeCookie(ObidosConstants.OBIDOS_USERID_COOKIE);
        Cookies.removeCookie(ObidosConstants.OBIDOS_USERTYPE_COOKIE);
        Cookies.removeCookie(ObidosConstants.XSRF_COOKIE_NAME);
        myLog("Done clearing cookies");
    }

    public static boolean isDevelopmentMode()
    {
        return !GWT.isProdMode() && GWT.isClient();
    }

    /**
     * Sets the debug level according to the URL parameter 'log_level'. Set by
     * appending: "?log_level=debug" to URL.
     */
    public static final void setLoggingLevel()
    {
    }

    /**
     * A client side lame method to check if the logged in user is admin or not
     * Note: There is no security, it can be easily bypassed but it stops some
     * annoyances in the UI, the real security is in place in server side.
     * 
     * @return true of false
     *         <p>
     * @author spgdev@spenego.com - Apr 1, 2017
     */
    /*
     * public static boolean isAdmin() { AuthCredsDTO userDTO =
     * ClientUtils.getAuthCreds(); if (userDTO != null &&
     * userDTO.getUserType().equals("admin")) { return true; } return false; }
     */

    public static boolean isAdmin(CurrentUser currentUser)
    {
        if (currentUser != null)
        {
            return fromBoolean(currentUser.getUserDTO().getAdministrator());
        }
        return false;
    }

    public static boolean loggedInCookieFound()
    {
        String cookie = Cookies.getCookie(ObidosConstants.OBIDOS_LOGIN_COOKIE);
        if (cookie == null)
            return false;
        if (cookie.equals("null"))
        {
            return false;
        }
        return true;
    }

    public static String getXSRFTokenFromCookie()
    {
        String cookie = Cookies.getCookie(ObidosConstants.OBIDOS_LOGIN_COOKIE);
        if ((cookie != null) && cookie.equals("null"))
            return null;
        return cookie;
    }

    //
    // Parse YAML string of license as key: value pair and give a score
    // If the score > 40, assume it's our license
    // This method can be called if a base64 license is pasted to give a simple
    // mechanism to identify it's possibly a Obidos license. Of course, license
    // will be validated properly in back end after Install button is pressed.
    // Bug # 867
    public static int licenseScore(final String yamlString)
    {
        int score = 0;
        if (yamlString == null || yamlString.length() == 0)
        {
            return 0;
        }
        String[] lines = yamlString.split("\n");
        for (String line : lines)
        {
            if (line.equals("---"))
            {
                score++; // a good start, possible YAML
                continue;
            }
            String[] parts = line.split(":");
            int len = parts.length;
            if (len == 2)
            {
                String key = parts[0].toLowerCase();
                if (key.equals("nonce")) // must be there
                {
                    score = score + 10; // give a good score
                }
                if (key.equals("signature")) // must be there
                {
                    score = score + 10; // give a good score
                }
                if (key.equals("signingepoch")) // must be there
                {
                    score = score + 10;
                }
                if (key.equals("customerid")) // must be there
                {
                    score = score + 10;
                }
                if (key.equals("productname"))
                {
                    score++;
                }
                if (key.equals("companayname"))
                {
                    score++;
                }
            }
        }

        return (score);
    }

    public static void setXSRFToken(String value)
    {
        Cookies.setCookie(ObidosConstants.OBIDOS_LOGIN_COOKIE, value);
    }

    public static AuthCredsDTO getAuthCreds()
    {
        return new AuthCredsDTO(getXSRFTokenFromCookie());
    }

    // it does not seem to be true modal
    // for example, when server is not available, new password can be typed and
    // each key stroke pop up the dialog
    public static void showDialog(String title, String message)
    {
        final Modal modal = new Modal();
        final ModalBody modalBody = new ModalBody();
        final ModalFooter modalFooter = new ModalFooter();

        modal.setTitle(title);
        modal.setClosable(true);
        modal.setRemoveOnHide(true);
        modal.setFade(true);
        modalBody.add(new Span(message));

        modalFooter.add(new Button(glang.closeC(), new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent arg0)
            {
                modal.hide();
            }

        }));

        modal.add(modalBody);
        modal.add(modalFooter);
        modal.show();
    }

    /**
     *
     * @param placeManager
     * @param action
     * @param nameToken
     *            - will be set from menu items, it must not be set from a page
     *            <p>
     * @author spgdev@spenego.com - Dec 2, 2018
     */
    public static void showRegisterPassphraseDialog(PlaceManager placeManager, String action, String nameToken)
    {
        // String messagex =
        // "Before you can " + "<b>" + action +"</b>," + " you must enter the
        // passphrase " +
        // "you generated the very first time you logged into the system. After
        // the passphrase is validated " +
        // "you will be redirected to this page to " + "<b>" + action + "</b>.";
        ObidosMessages lang = ObidosMessages.LANG;
        String message = lang.registerPassphraseDialogMessage(action);
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(lang.registerPassphraseDialogTitle());
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
                log("ClientUtils", " Escape caught");
            }
        });

        options.addButton(lang.registerPassphraseCancelButtonTitle(), ButtonType.DEFAULT.getCssName(),
                new SimpleCallback()
                {
                    @Override
                    public void callback()
                    {
                        log("ClientUtils", " Cancel");
                    }
                });

        options.addButton(lang.registerPassphraseGoToRegisterPageButtonTitle(), ButtonType.PRIMARY.getCssName(),
                new SimpleCallback()
                {

                    @Override
                    public void callback()
                    {
                        log("ClientUtils", " Go to Register PP page");
                        goToRegisterPassphrasePage(placeManager, nameToken);
                    }
                });
        Bootbox.dialog(options);
    }

    // sent to About page if Cancel is clicked
    public static void showRegisterPassphraseDialogWithCancelCallback(PlaceManager placeManager, String action,
            String nameToken)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String message = lang.registerPassphraseDialogMessage(action);
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(lang.registerPassphraseDialogTitle());
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
                log("ClientUtils", " Escape caught");
            }
        });

        options.addButton(lang.registerPassphraseCancelButtonTitle(), ButtonType.DEFAULT.getCssName(),
                new SimpleCallback()
                {
                    @Override
                    public void callback()
                    {
                        // ClientUtils.showPage(placeManager, NameTokens.ABOUT);
                        ClientUtils.goBack(placeManager);
                    }
                });

        options.addButton(lang.registerPassphraseGoToRegisterPageButtonTitle(), ButtonType.PRIMARY.getCssName(),
                new SimpleCallback()
                {

                    @Override
                    public void callback()
                    {
                        goToRegisterPassphrasePage(placeManager, nameToken);
                    }
                });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    // pass the method for cancel
    public static void showAlertDialogWithCallback(final String title, final String message, PromptForAction c)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);

        options.setOnEscape(new SimpleCallback()
        {
            @Override
            public void callback()
            {
                c.action();
            }

        });
        options.addButton(lang.cancel(), ButtonType.PRIMARY.getCssName(), new SimpleCallback()
        {

            @Override
            public void callback()
            {
                c.action();
            }
        });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    public static void showRegisterPassphraseDialog(final String actionText, PromptForAction c, PromptForAction s)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String message = lang.registerPassphraseDialogMessage(actionText);
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(lang.registerPassphraseDialogTitle());
        options.setOnEscape(new SimpleCallback()
        {

            // ESC pressed
            @Override
            public void callback()
            {
                myLog("Cancel: " + c);
                c.action();
            }
        });

        options.addButton(lang.registerPassphraseCancelButtonTitle(), ButtonType.DEFAULT.getCssName(),
                new SimpleCallback()
                {
                    @Override
                    public void callback()
                    {
                        myLog("Cancel: " + c);
                        c.action();
                    }
                });

        options.addButton(lang.registerPassphraseGoToRegisterPageButtonTitle(), ButtonType.PRIMARY.getCssName(),
                new SimpleCallback()
                {

                    @Override
                    public void callback()
                    {
                        s.action();
                    }
                });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    public static void hideBootBoxDialog()
    {
        Bootbox.hideAll();
    }

    public static void showRegisterPassphraseDialogWith(PlaceManager placeManager, String action,
            Map<String, String> with)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String message = lang.registerPassphraseDialogMessage(action);
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(lang.registerPassphraseDialogTitle());
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
                log("ClientUtils", " Escape caught");
            }
        });

        options.addButton(lang.registerPassphraseCancelButtonTitle(), ButtonType.DEFAULT.getCssName(),
                new SimpleCallback()
                {
                    @Override
                    public void callback()
                    {
                        log("ClientUtils", " Cancel");
                    }
                });

        options.addButton(lang.registerPassphraseGoToRegisterPageButtonTitle(), ButtonType.PRIMARY.getCssName(),
                new SimpleCallback()
                {

                    @Override
                    public void callback()
                    {
                        log("ClientUtils", " Go to Register PP page");
                        goToRegisterPassphrasePage(placeManager, with);
                    }
                });
        Bootbox.dialog(options);
    }

    public static void goToRegisterPassphrasePage(final PlaceManager placeManager, final String navigateToPageNameToken)
    {
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        String nameToken = placeRequest.getNameToken();
        myLog("NameToken: " + nameToken);
        Map<String, String> with = new HashMap<>();
        with.put("lastPlace", nameToken);
        Set<String> paramNames = placeRequest.getParameterNames();
        if (paramNames != null && paramNames.size() > 0)
        {
            myLog("Param size: " + paramNames.size());
            myLog("Parameter names found");
            for (String key : paramNames)
            {
                String value = placeRequest.getParameter(key, "");
                myLog("key: " + key + " value: " + value);
                with.put(key, value);
            }
        }
        if (navigateToPageNameToken != null)
        {
            // set from menu, after passphrase is registered it should
            // send the user to that page
            with.put(ObidosConstants.PLACE, navigateToPageNameToken);
        }
        nameToken = NameTokens.REGISTER_PASSPHRASE;
        ClientUtils.showPage(placeManager, nameToken, with);
    }

    public static void showUsersInGroupPage(final PlaceManager placeManager, Long groupId)
    {
        if (groupId == null)
        {
            return;
        }
        String nameToken = NameTokens.VIEW_USERS_IN_GROUP;
        Map<String, String> with = new HashMap<>();
        with.put(ObidosConstants.GROUP_ID, groupId.toString());
        ClientUtils.showPage(placeManager, nameToken, with);

    }

    public static void dumpWith(Map<String, String> with)
    {
        if (with != null)
        {
            myLog("-- dump with ---");
            for (Map.Entry<String, String> entry : with.entrySet())
            {
                myLog("key=" + entry.getKey());
                myLog("val=" + entry.getValue());
            }
            myLog("-- dump with ---");
        }
    }

    public static void goToRegisterPassphrasePage(PlaceManager placeManager, Map<String, String> with)
    {
        ClientUtils.showPage(placeManager, NameTokens.REGISTER_PASSPHRASE, with);
    }

    public static boolean isPassphraseRegistered(CurrentUser currentUser)
    {
        if (currentUser == null)
        {
            return false;
        }
        Boolean registered = currentUser.getPassphraseRegistered();
        if (registered == null)
        {
            return false;
        }
        return registered;
    }

    public static String getTypeFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.TYPE);
    }

    public static boolean editAdmin(final PlaceManager placeManager)
    {
        return ObidosConstants.LIST_ADMINS.equals(ClientUtils.getTypeFromUrl(placeManager));
    }

    public static String getStringParameterFromUrl(final PlaceManager placeManager, final String id)
    {
        try
        {
            return getParameterFromUrl(placeManager, id);
        } catch (ParamNotFoundException e)
        {
            return null;
        }
    }

    public static Long getIdParameterFromUrl(final PlaceManager placeManager, final String id)
    {
        try
        {
            return getIdFromUrl(placeManager, id);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
        }
        return null;
    }

    public static int stringToInt(final String str)
    {
        try
        {
            Integer i = Integer.parseInt(str);
            return i;
        } catch (NumberFormatException e)
        {
        }
        return 0;
    }

    public static String getItemTypeFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.ITEM_TYPE);
    }

    public static String getNameTokenFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.NAME_TOKEN);
    }

    public static String getMarkTypeFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.MARK_TYPE);
    }

    public static boolean markTypeLockedUsers(final PlaceManager placeManager) throws ObidosException
    {
        String markType = ClientUtils.getMarkTypeFromUrl(placeManager);
        if (ObidosConstants.LOCKED.equals(markType))
        {
            return true;
        } else if (ObidosConstants.DELETED.equals(markType))
        {
            return false;
        }
        throw new ObidosException(ObidosMessages.LANG.couldNotDetermineMarkedType());

    }

    public static String getLockedFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.LOCKED);
    }

    public static String getDeletedFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.DELETED);
    }

    public static String getShareTypeFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.SHARE_TYPE);
    }

    public static String getPlaceFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.PLACE);
    }

    public static String getFromFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.FROM);
    }

    public static String getShareFromUrl(final PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.SHARE);
    }

    public static Long getItemIdFromUrl(PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.ITEM_ID);
    }

    public static Long getIdFromUrl(final PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.ID);
    }

    public static Long getGroupIdFromUrl(PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.GROUP_ID);
    }

    public static boolean checkTwoFaRequiredInUrl(final PlaceManager placeManager)
    {
        String twoFACodeRequired = getStringParameterFromUrl(placeManager, ObidosConstants.TWO_FACTOR_CODE_REQUIRED);
        if (twoFACodeRequired == null)
        {
            return false;
        }
        if (twoFACodeRequired.equals("false"))
        {
            return false;
        } else if (twoFACodeRequired.equals("true"))
        {
            return true;
        }
        return false;
    }

    public static boolean getSharedWithOthersFromUrl(PlaceManager placeManager)
    {
        try
        {
            String yes = ClientUtils.getParameterFromUrl(placeManager, ObidosConstants.SHARED_WITH_OTHERS);
            if (ObidosMessages.LANG.yes().equals(yes))
            {
                return true;
            }
        } catch (ParamNotFoundException e)
        {
            return false;
        }
        return false;
    }

    public static String getPasswordIsResetFromUrl(PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.PASSWORD_IS_RESET);
    }

    public static Long getContainerIdFromUrl(PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.CONTAINER_ID);
    }

    public static Long getUserIdFromUrl(PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.USER_ID);
    }

    public static String getContainerTypeFromUrl(PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.CONTAINER_TYPE);
    }

    public static Long getTemplateIdFromUrl(PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.TEMPLATE_ID);
    }

    public static Long getOwnerIdFromUrl(PlaceManager placeManager)
    {
        return getIdParameterFromUrl(placeManager, ObidosConstants.OWNERID);
    }

    public static String getActionFromUrl(PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.ACTION);
    }

    public static String getTokenFromUrl(PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, ObidosConstants.RESET_TOKEN);
    }

    public static String getxEnableFromUrl(PlaceManager placeManager)
    {
        return getStringParameterFromUrl(placeManager, "xenable");
    }

    public static boolean getTwoFaRequiredFromUrl(PlaceManager placeManager)
    {
        String val = getStringParameterFromUrl(placeManager, ObidosConstants.TWO_FACTOR_CODE_REQUIRED);
        if (val != null && val.equals("true"))
        {
            return true;
        }
        return false;
    }

    public static boolean is2FAEnabeld(final CurrentUser currentUser)
    {
        if (currentUser != null && currentUser.getUserDTO() != null
                && currentUser.getUserDTO().getTwoFAPasswordResetEnabled())
        {
            return true;
        }
        return false;
    }

    public static String getResetTokenFromUrl(final PlaceManager placeManager)
    {
        return getTokenFromUrl(placeManager);
    }

    // there is also a race here but at least it will close the already
    // popped dialog
    // title is not used
    public static void showBootboxDialogSimple(String title, String message)
    {
        /*
         * AlertOptions options = AlertOptions.newOptions(message);
         * options.setTitle(title); options.setBackdrop(true);
         * options.setOnEscape(new SimpleCallback() {
         * 
         * @Override public void callback() { }
         * 
         * }); Bootbox.hideAll(); Bootbox.alert(options);
         */
        Bootbox.hideAll();
        Bootbox.alert(message);
    }

    public static void showBootboxDialog(String title, String message)
    {

        DialogOptions options = DialogOptions.newOptions(message);
        if (title != null)
        {
            options.setTitle(title);
        }
        options.addButton(ObidosMessages.LANG.okButtonTitle());
        Bootbox.dialog(options);
        // Document.get().getElementById("modal-body").getStyle().setProperty("maxHeight","100px");
        // Document.get().getElementById("modal-body").getStyle().setProperty("overflowY","auto");
    }

    public static void showErrorMessageInSpan(Span span, String errorMessage)
    {
        span.setHTML(ObidosConstants.ERROR_ICON + errorMessage);
    }

    public static void showMessageInSpan(Span span, String message)
    {
        span.setHTML(ObidosConstants.INFO_ICON + message);
    }

    private static void setColor(UIObject formLabel, String color)
    {
        formLabel.getElement().getStyle().setColor(color);
    }

    private static void showMessgaeReal(String message, FormLabel formLabel, String color)
    {
        formLabel.setVisible(true);
        setColor(formLabel, color);
        formLabel.getElement().getStyle().setFontSize(12.0, Unit.PX);
        if (message == null || message.length() == 0)
        {
            formLabel.setHTML("&nbsp;");
            return;
        }
        formLabel.setHTML(message);
    }

    public static void setEmptyLabelValue(Label label)
    {
        label.getElement().getStyle().setProperty("cursor", "default");
        label.getElement().getStyle().setProperty("backgroundColor", "transparent");
        label.setHTML("&nbsp;");
        label.setVisible(true);
    }

    // set bg color, ref:
    // https://blog.francoismaillet.com/ways-of-setting-css-property-to-element-in-gwt/
    // bootstrap colors:
    // https://stackoverflow.com/questions/47702879/bootstrap-4-hex-colors-for-rails-app
    private static void showMessageReal(String message, final Label label, String bgColor)
    {
        if (message == null || message.length() == 0)
        {
            setEmptyLabelValue(label);
            return;
        }

        label.getElement().getStyle().setProperty("backgroundColor", bgColor);
        label.getElement().getStyle().setProperty("cursor", "pointer");
        message = message + "&nbsp;&times;";
        label.setHTML(message);
    }

    // use Label to display message so that they can be closed
    public static void showMessage(String message, Label label)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                showMessageReal(message, label, "#28a745");
            }

        });
    }

    public static void showErrorMessage(final String errorMessage, final Label label)
    {

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                showMessageReal(errorMessage, label, "#dc3545");
            }
        });
    }

    public static void showMessage(String message, FormLabel formLabel)
    {
        showMessgaeReal(message, formLabel, ObidosConstants.GREEN_COLOR);
    }

    public static void showErrorMessage(String errorMessage, FormLabel formLabel)
    {
        showMessgaeReal(errorMessage, formLabel, ObidosConstants.RED_COLOR);
    }

    public static void showMessage(final ObidosMessageRow messageRow, final String message)
    {
        if (messageRow != null)
        {
            messageRow.showMessage(message);
        } else
        {
            myLog("WARNING: ObidosMessageRow is passwd as null");
        }

    }

    public static void showErrorMessage(final ObidosMessageRow messageRow, final String errorMessage)
    {
        if (messageRow != null)
        {
            messageRow.showErrorMessage(errorMessage);
        } else
        {
            myLog("WARNING: ObidosMessageRow is passwd as null");
        }
    }

    public static void showMessage(String message, Alert alert)
    {
        if (message == null || message.length() == 0)
        {
            alert.setVisible(false);
            return;
        }
        alert.setVisible(true);
        alert.setType(AlertType.SUCCESS);
        alert.setText(message);
    }

    public static void showErrorMessgae(String errorMessage, Alert alert)
    {
        if (errorMessage == null || errorMessage.length() == 0)
        {
            alert.setVisible(false);
            return;
        }
        alert.setVisible(true);
        alert.setType(AlertType.DANGER);
        alert.setText(errorMessage);
    }

    public static void showLoggedOutMessage(String message, FormLabel formLabel)
    {
        formLabel.setVisible(true);
        setColor(formLabel, "red");
        formLabel.getElement().getStyle().setFontSize(12.0, Unit.PX);
        formLabel.setText(message);
    }

    private static void sendMessageEvent(final HasHandlers source, final String message, final int messageType)
    {
        SendMessageEvent.fire(source, new MessageDTO(messageType, message));
    }

    /**
     *
     * A presenter can send a message easily which will show up below the nav
     * bar
     *
     * @param source
     * @param message
     *            <p>
     * @author spgdev@spenego.com - Jun 4, 2017
     */
    public static void showErrorMessageInMainView(HasHandlers source, String message)
    {
        sendMessageEvent(source, message, MessageDTO.ERROR);
    }

    public static void showInfoMessageInMainView(HasHandlers source, String message)
    {
        sendMessageEvent(source, message, MessageDTO.INFO);
    }

    public static void showWarningMessageInMainView(HasHandlers source, String message)
    {
        sendMessageEvent(source, message, MessageDTO.WARNING);
    }

    public static void setSearchboxPlaceHolder(HasHandlers source, String placeHolder)
    {
        sendMessageEvent(source, placeHolder, ObidosConstants.SEARCHBOX_PLACEHOLDER);
    }

    public static void setSearchboxText(HasHandlers source, String text)
    {
        sendMessageEvent(source, text, ObidosConstants.SEARCHBOX_TEXT);
    }

    public static void sendMessageToAppViewToRefreshPassphraseCachedIcon(HasHandlers source)
    {
        sendMessageEvent(source, "Refresh Passphrase cached icon", ObidosConstants.REFRESH_PASSPHRASE_CACHED_ICON);
    }

    /**
     * If timeout is 0, do not start the timer
     *
     * @param button
     * @param passwordBox
     * @param timeout
     *            <p>
     * @author spgdev@spenego.com - Jul 12, 2017
     */
    public static void toggleEyeIcon(Button button, Input passwordBox)
    {
        String type = passwordBox.getElement().getAttribute("type");
        if (type.equals("text"))
        {
            passwordBox.getElement().setAttribute("type", "password");
            button.setType(ButtonType.INFO);
            button.setIcon(IconType.EYE);
        } else
        {
            passwordBox.getElement().setAttribute("type", "text");
            button.setType(ButtonType.DANGER);
            button.setIcon(IconType.EYE_SLASH);
            hideTimer(button, passwordBox, ObidosConstants.HIDE_PASSWORD_BOX_SECONDS);
        }
    }

    public static void toggleEyeIcon(Button button, Input passwordBox, ButtonType buttonType)
    {
        String type = passwordBox.getElement().getAttribute("type");
        if (type.equals("text"))
        {
            passwordBox.getElement().setAttribute("type", "password");
            button.setType(buttonType);
            button.setIcon(IconType.EYE);
        } else
        {
            passwordBox.getElement().setAttribute("type", "text");
            button.setType(ButtonType.DANGER);
            button.setIcon(IconType.EYE_SLASH);
            hideTimer(button, passwordBox, ObidosConstants.HIDE_PASSWORD_BOX_SECONDS);
        }
    }

    // hide text after specified seconds
    private static void hideTimer(Button button, Input passwordBox, int afterSecs)
    {
        Timer hidePasswordBoxTimer = new Timer()
        {
            @Override
            public void run()
            {
                button.setType(ButtonType.INFO);
                button.setIcon(IconType.EYE);
                passwordBox.getElement().setAttribute("type", "password");
            }
        };
        if (hidePasswordBoxTimer != null)
        {
            hidePasswordBoxTimer.schedule(afterSecs * 1000);
        }
    }

    public static void setXsrfTokenToServiceEndPoints()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {
            @Override
            public void onFailure(Throwable e)
            {
                Window.alert("Could not get XSRF token: " + e);
            }

            @Override
            public void onSuccess(String xsrfToken)
            {
                if (xsrfToken == null)
                {
                    // No session established yet, clear cookies and establish
                    // session and
                    // get XSRF token and set it to service endpoints
                    ClientUtils.clearCookies();
                    initializeSessionForXSRF();
                } else
                {
                    // probably browser is refreshed, use our obtained token to
                    // set to the
                    // service end points. GWT's XSRF servlet fails to get token
                    // in this case,
                    // I do not know why. XSRF token is nothing but MD5 of the
                    // session id.
                    initializeXSRF(xsrfToken);
                }
            }
        };
        SessionEstablishService.Utility.getInstance().getXsrfToken(callback);
    }

    /**
     * Before GWT XSRF servlet can issue XSRF token, we have to
     *
     * <p>
     * 
     * @author spgdev@spenego.com - Dec 17, 2016
     */
    private static void initializeSessionForXSRF()
    {
        AsyncCallback<String> callback = new AsyncCallback<String>()
        {

            @Override
            public void onFailure(Throwable e)
            {
            }

            @Override
            public void onSuccess(String sessionId)
            {
                // Session must be established before asking for XSRF token
                //
                initializeXSRF(null);
            }
        };
        SessionEstablishService.Utility.getInstance().establishSession(callback);
    }

    private static void initializeXSRF(String token)
    {
        if (token != null)
        {
            XsrfToken xsrfToken = new XsrfToken(token);
            ((HasRpcToken) LoginService.Utility.getInstance()).setRpcToken(xsrfToken);

            ((HasRpcToken) AdminConfigService.Utility.getInstance()).setRpcToken(xsrfToken);

            ((HasRpcToken) UserService.Utility.getInstance()).setRpcToken(xsrfToken);
            ((HasRpcToken) DicewareService.Utility.getInstance()).setRpcToken(xsrfToken);

            return;
        }

        // Broken GWT's POS xsrf servlet
        XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT.create(XsrfTokenService.class);

        ((ServiceDefTarget) xsrf).setServiceEntryPoint(GWT.getModuleBaseURL() + "xsrf");
        xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>()
        {

            @Override
            public void onFailure(Throwable e)
            {
            }

            @Override
            public void onSuccess(XsrfToken token)
            {
                // perform HasRpcToken to all of our services
                ((HasRpcToken) LoginService.Utility.getInstance()).setRpcToken(token);

                ((HasRpcToken) AdminConfigService.Utility.getInstance()).setRpcToken(token);

                ((HasRpcToken) UserService.Utility.getInstance()).setRpcToken(token);

                ((HasRpcToken) DicewareService.Utility.getInstance()).setRpcToken(token);
            }

        });
    }

    public static void setFormLabelsColorOriginal(FormLabel label)
    {
        setColor(label, ObidosConstants.BLACK_COLOR);
    }

    public static void resetFormLabelColor(FormLabel label)
    {
        setFormLabelsColorOriginal(label);
    }

    public static void setFormLabelsColorChanged(FormLabel label)
    {
        setColor(label, ObidosConstants.PURPLE_COLOR);
    }

    public static Throwable unwrapUmbrellaException(Throwable e)
    {
        if (e instanceof UmbrellaException)
        {
            UmbrellaException ue = (UmbrellaException) e;
            if (ue.getCauses().size() == 1)
            {
                return unwrapUmbrellaException(ue.getCauses().iterator().next());
            }
        }
        return e;
    }

    public static void enableDisableButton(Button button, boolean what)
    {
        button.setEnabled(what);
    }

    // return null if key is not found
    public static String getParameterFromUrl(PlaceManager placeManager, String key) throws ParamNotFoundException
    {
        String defaultValue = "";
        String val = placeManager.getCurrentPlaceRequest().getParameter(key, defaultValue);
        if (defaultValue.equals(val))
        {
            throw new ParamNotFoundException("No " + key + " found in URL");
        }
        log(null, "CLientUtils.getParameterFromUrl.... returning for: " + key + "=" + val);
        return val;
    }

    public static Long getIdFromUrl(PlaceManager placeManager, String key)
            throws NumberFormatException, ParamNotFoundException
    {
        try
        {
            return Long.parseLong(ClientUtils.getParameterFromUrl(placeManager, key));
        } catch (ParamNotFoundException e)
        {
            throw new ParamNotFoundException("No " + key + " found in URL");
        }
    }
    /*
     * public static Long getLoggedInUserId() throws NumberFormatException,
     * SpenegoException { String userIdString = getLoggedInUseridString(); if
     * (userIdString == null) { throw new
     * SpenegoException("Could not obtain logged in user id"); } Long userId =
     * Long.parseLong(userIdString); return userId; }
     */

    public static Long getLoggedInUserId(CurrentUser currentUser) throws ObidosException
    {
        if (currentUser != null)
        {
            return currentUser.getUserDTO().getId();
        } else
        {
            throw new ObidosException("Could not obtain logged in user id");
        }
    }

    public static void showListContainersPage(PlaceManager placeManager)
    {
        Map<String, String> with = new HashMap<>();
        String nameToken = NameTokens.LIST_CONTAINERS_ADD_ITEM;
        String place = getPlaceFromUrl(placeManager);
        if (place != null)
        {
            nameToken = place;
        }
        Long containerId = getContainerIdFromUrl(placeManager);
        if (containerId != null)
        {
            with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
        }
        showPage(placeManager, nameToken, with);
    }

    public static void updateListButtonTitle(PlaceManager placeManager, Button lb)
    {
        String place = getPlaceFromUrl(placeManager);
        if (place != null)
        {
            if (place.equals(NameTokens.LIST_CONTAINERS) || place.equals(NameTokens.LIST_CONTAINERS_ADD_ITEM))
            {
                lb.setText(ObidosMessages.LANG.listContainers());
            } else if (place.equals(NameTokens.LIST_ITEMS))
            {
                lb.setText(ObidosMessages.LANG.listItems());
            }
        }

    }

    /**
     * Show the list of systems in a container "containerid" must exists in the
     * URL
     * 
     * @param placeManager
     *            <p>
     * @author spgdev@spenego.com - Jul 23, 2017
     * @throws ObidosException
     */
    public static String showListSystemsPage(PlaceManager placeManager) throws ObidosException
    {
        String containerIdString = "";
        try
        {
            containerIdString = getParameterFromUrl(placeManager, ObidosConstants.CONTAINER_ID);
        } catch (ParamNotFoundException e)
        {
        }
        String errorMessage = null;
        if (containerIdString.length() == 0)
        {
            errorMessage = "No " + ObidosConstants.CONTAINER_ID + " found in the URL";
            return errorMessage;
        }
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.LIST_SYSTEMS)
                .with(ObidosConstants.CONTAINER_ID, containerIdString).build();

        placeManager.revealPlace(placeRequest);

        return errorMessage;
    }

    /**
     * return false if the exception is not of the type SessionErrorException
     * otherwise logout and show the login page
     * 
     * @param e
     * @return false
     */
    public static boolean handleSessionErrorException(PlaceManager placeManger, Throwable e)
    {
        return false;
    }

    /**
     * ApplicationPresenter is listening for the LogutEvent. After receiving the
     * event, ApplicationPresenter will: - actually logout the user - clear
     * cookies - set currentUser singleton to loggoutout - navigate to
     * LoginPresenter - fire LogoutMessageEvent with the message so that
     * LoginPresenter can show the status message is any
     *
     * @param source
     * @param message
     *            <p>
     * @author spgdev@spenego.com - Dec 25, 2017
     */
    public static void logout(HasHandlers source, String message)
    {
        ObidosSessionTimer.cancelTimers();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(ObidosConstants.LOGOUT);
        if (message != null && message.length() > 0)
        {
            messageDTO.setMessage(message);
        }
        LogoutEvent.fire(source, messageDTO);
    }

    public static String getSessionTimedoutMessgage()
    {
        return "Session Timed out at: " + formattedDate((Date) null);
    }

    public static void showPage(PlaceManager placeManager, String nameToken)
    {
        myLog("Name token: " + nameToken);
        myLog("PlaceManager: " + placeManager);
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(nameToken).build());
    }

    public static void showPage(PlaceManager placeManager, String nameToken, Map<String, String> with)
    {
        PlaceRequest request = null;

        if (placeManager == null)
        {
            return;
        }

        if (with != null && with.size() > 0)
        {
            request = new PlaceRequest.Builder().nameToken(nameToken).with(with).build();
        } else
        {
            request = new PlaceRequest.Builder().nameToken(nameToken).build();
        }
        placeManager.revealPlace(request);
    }

    private static String getNameToken(TokenFormatter tokenFormatter)
    {
        String placeToken = History.getToken();
        if (placeToken == null || placeToken.length() == 0)
        {
            return null;
        }
        myLog(" PlaceToken: " + placeToken);
        PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

        String nameToken = placeRequest.getNameToken();
        return nameToken;
    }

    public static Map<String, String> makeMapFromUrl(final boolean addNameToken, TokenFormatter tokenFormatter)
    {
        String placeToken = History.getToken();
        if (placeToken == null || placeToken.length() == 0)
        {
            return null;
        }
        myLog(" PlaceToken: " + placeToken);
        PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

        String targetNameToken = placeRequest.getNameToken();
        Map<String, String> with = new HashMap<>();
        Set<String> paramNames = placeRequest.getParameterNames();
        if (paramNames != null && paramNames.size() > 0)
        {
            if (addNameToken)
            {
                with.put(ObidosConstants.NAME_TOKEN, targetNameToken);
            }
            myLog("Param size: " + paramNames.size());
            myLog("Parameter names found");
            for (String key : paramNames)
            {
                String value = placeRequest.getParameter(key, "");
                myLog("key: " + key + " value: " + value);
                with.put(key, value);
            }
        }
        return with;
    }

    // don't add place just parse and create map
    public static Map<String, String> makeMapFromUrlNoPlace(TokenFormatter tokenFormatter)
    {
        String placeToken = History.getToken();
        if (placeToken == null || placeToken.length() == 0)
        {
            return null;
        }
        myLog(" PlaceToken: " + placeToken);
        PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

        String nameToken = placeRequest.getNameToken();
        Map<String, String> with = new HashMap<>();
        myLog(" NameToken: " + nameToken);
        Set<String> paramNames = placeRequest.getParameterNames();
        if (paramNames != null && paramNames.size() > 0)
        {
            myLog("Param size: " + paramNames.size());
            myLog("Parameter names found");
            for (String key : paramNames)
            {
                String value = placeRequest.getParameter(key, "");
                myLog("key: " + key + " value: " + value);
                with.put(key, value);
            }
        }
        return with;
    }

    public static Map<String, String> makeMapFromUrl(TokenFormatter tokenFormatter)
    {
        if (tokenFormatter == null)
        {
            myLog("TokenFormatter is null");
            return null;
        }
        String placeToken = History.getToken();
        if (placeToken == null || placeToken.length() == 0)
        {
            return null;
        }
        myLog(" PlaceToken: " + placeToken);
        PlaceRequest placeRequest = tokenFormatter.toPlaceRequest(placeToken);

        String nameToken = placeRequest.getNameToken();
        Map<String, String> with = new HashMap<>();
        myLog(" NameToken: " + nameToken);
        Set<String> paramNames = placeRequest.getParameterNames();
        if (paramNames != null && paramNames.size() > 0)
        {
            myLog("Param size: " + paramNames.size());
            myLog("Parameter names found");
            for (String key : paramNames)
            {
                String value = placeRequest.getParameter(key, "");
                myLog("key: " + key + " value: " + value);
                with.put(key, value);
            }
        }
        // with.put(ObidosConstants.PLACE, nameToken);
        return with;
    }

    public static Summernote createSummernote(Integer noteHeight)
    {
        Summernote summernote = new Summernote();
        customizeSummernoteToolbar(summernote);
        if (noteHeight != null)
        {
            summernote.setDefaultHeight(noteHeight);
        }
        return summernote;
    }

    public static void customizeSummernoteToolbar(Summernote summernote)
    {
        /*
         * summernote.setFontNames( SummernoteFontName.ARIAL,
         * SummernoteFontName.VERDANA, SummernoteFontName.TIMES_NEW_ROMAN,
         * SummernoteFontName.HELVETICA_NEUE, SummernoteFontName.COURIER_NEW );
         */

        summernote.setDisableDragAndDrop(true);

        Toolbar tb = new Toolbar();
        tb.addGroup(ToolbarButton.STYLE);
        tb.addGroup(ToolbarButton.BOLD);
        // tb.addGroup(ToolbarButton.ITALIC);
        // tb.addGroup(ToolbarButton.CLEAR);
        // tb.addGroup(ToolbarButton.FONT_NAME, ToolbarButton.FONT_SIZE);
        // tb.addGroup(ToolbarButton.COLOR);
        tb.addGroup(ToolbarButton.UL);
        // tb.addGroup(ToolbarButton.OL);
        // tb.addGroup(ToolbarButton.PARAGRAPH);
        // tb.addGroup(ToolbarButton.TABLE);
        // tb.addGroup(ToolbarButton.LINK);
        // tb.addGroup(ToolbarButton.PICTURE);
        // tb.addGroup(ToolbarButton.VIDEO);
        tb.addGroup(ToolbarButton.FULL_SCREEN);
        // turn on code for testing
        tb.addGroup(ToolbarButton.CODE_VIEW);
        // tb.addGroup(ToolbarButton.HELP);
        summernote.setToolbar(tb);
    }

    private static final DateTimeFormat dtf = DateTimeFormat.getFormat("hh:mm:ss a, MMM-d-yyyy");

    public static String formattedDate(final Date date)
    {
        return dtf.format((date == null) ? new Date() : date);
    }

    // apparently best practice according to ChatGPT4o
    public static String simpleFormattedDate(final Date date)
    {
        // ugly there is a 'T' separates I don't like it
        // DateTimeFormat formatter =
        // DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static String formattedDate(Long epoch)
    {
        if (epoch == null)
        {
            epoch = new Date().getTime() / 1000;
        }
        return formattedDate(new Date(epoch * 1000));
    }

    /**
     * format can be mm/dd/yyyy yyyy-mm-d etc.
     * 
     * @param dateString
     * @param format
     * @return Date on success, null otherwise
     *         <p>
     * @author spgdev@spenego.com - Aug 10, 2019
     */
    public static Date getState(final String dateString, final String format)
    {
        try
        {
            DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(format);
            Date date = dateTimeFormat.parse(dateString);
            return date;
        } catch (Exception e)
        {
            return null;
        }
    }

    // replace mm with MM
    public static String getBootstrap2GwtDateFormat(final String format)
    {
        /*
         * mm/dd/yyyy dd/mm/yyyy yyyy/mm/dd
         */
        String gwtFormat = format.replace("mm", "MM");
        return gwtFormat;
    }

    public static void gwtLog(String className, String message)
    {
        Date date = null;
        String ds = formattedDate(date);
        String msg = ds + "(" + className + "):" + message;
        GWT.log(msg);
        // Log.info(()->msg);
    }

    public static void cancelTimers()
    {
        ObidosSessionTimer.cancelTimers();
    }

    private static void log(String callerClass, String message)
    {
        if (callerClass == null)
        {
            callerClass = "ClientUtils";
        }
        gwtLog(callerClass, message);
    }

    private static void myLog(String message)
    {
        log("ClientUtils", message);
    }

    public static void updateRPCTime(String callerClass)
    {
        Date date = new Date();
        long lastRpcCallEpoch = (date.getTime() / 1000);
        log(callerClass, "Session updating last RPC call epoch : " + lastRpcCallEpoch + "(" + date.toString() + ")");
        ObidosSessionTimer.setLastRpcCallEpoch(lastRpcCallEpoch);

    }

    // it's possible the value is null, I noticed it happens sometimes when
    // the comes back from back end
    public static long sessionTimeoutMillis(Integer sessionTimeoutSeconds)
    {
        if (sessionTimeoutSeconds == null || sessionTimeoutSeconds == 0)
        {
            return ObidosConstants.SESSION_TIMEOUT_MILLIS;
        }
        return sessionTimeoutSeconds * 1000L;
    }

    // from:
    // https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
    public static String getHexString(byte[] raw)
    {
        final String HEXES = "0123456789ABCDEF";
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw)
        {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * return hex string on success or the same string on failure
     *
     * @param str
     * @return
     *         <p>
     * @author spgdev@spenego.com - Jan 28, 2018
     */
    public static String getHexString(String str)
    {
        try
        {
            return ClientUtils.getHexString(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            return str;
        }
    }

    public static int getTemplateTypeFromUrl(final PlaceManager placeManager)
    {
        String templateType = null;
        String key = ObidosConstants.TEMPLATE_TYPE;
        try
        {
            try
            {
                templateType = ClientUtils.getParameterFromUrl(placeManager, key);
            } catch (ParamNotFoundException e)
            {
                return -1;
            }
        } catch (ObidosException e)
        {
            return ObidosConstants.TEMPLATE_TYPE_UNKNOWN;
        }

        if (templateType.equals(ObidosConstants.PERSONAL))
            return ObidosConstants.TEMPLATE_TYPE_PERSONAL;
        else if (templateType.equals(ObidosConstants.GLOBAL))
            return ObidosConstants.TEMPLATE_TYPE_GLOBAL;

        return ObidosConstants.TEMPLATE_TYPE_UNKNOWN;
    }

    public static void updateCurrentUser(CurrentUser currentUser, UserDTO userDTO)
    {
        currentUser.setLoggedIn(true);
        currentUser.setUserDTO(userDTO);
    }

    public static void showContainersPager(PlaceManager placeManager, Long userid)
    {
        String nameToken = NameTokens.LIST_CONTAINERS;
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(nameToken)
                .with(ObidosConstants.USER_ID, userid.toString()).build();

        placeManager.revealPlace(placeRequest);
    }

    public static boolean isNote(PlaceManager placeManager)
    {
        try
        {
            Long templateId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.TEMPLATE_ID);
            if (templateId == UserDefinedTypeDTO.NOTES_ID)
            {
                return true;
            }
        } catch (NumberFormatException | ParamNotFoundException e)
        {
        }
        return false;
    }

    public static boolean isGlobalTemplate(PlaceManager placeManager)
    {

        return getTemplateTypeFromUrl(placeManager) == ObidosConstants.TEMPLATE_TYPE_GLOBAL;
    }

    public static void cachePassphrase(String className, PlaceManager placeManager, CurrentUser currentUser,
            ObidosMessageRow messageRow, byte[] passphrase, String nameToken)
    {
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(placeManager)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                String msg = ObidosMessages.LANG.couldNoteCachePassphrase();
                ClientUtils.gwtLog(className, "ERROR: " + msg);
                String errorMessage = msg + ": " + caught.getMessage();
                messageRow.showErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(Void result)
            {
                ClientUtils.gwtLog(className, ObidosMessages.LANG.passphraseCached());
                if (currentUser != null)
                {
                    currentUser.setPassphraseRegistered(Boolean.TRUE);
                    ClientUtils.sendMessageToAppViewToRefreshPassphraseCachedIcon(placeManager);
                    if (nameToken != null)
                    {
                        ClientUtils.showPage(placeManager, nameToken);
                    }
                }
            }
        };
        AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().cachePassphrase(authCredsDTO, passphrase, null, callback);
    }

    // show strength of a password/passphrase
    public static void showPassStrength(final String pass, final PlaceManager placeManager,
            final CurrentUser currentUser, final ProgressBar pbar, final ObidosReadonlyTextBox entropyTextBox,
            final ObidosMessageRow messageRow)
    {
        if (pass == null || pass.length() == 0)
        {
            return;
        }
        showMessage(messageRow, null);
        GwtAsyncWrapper<PasswordAnalysisResults> callback = new GwtAsyncWrapper<PasswordAnalysisResults>(placeManager)
        {

            @Override
            public void uponFailure(Throwable t)
            {
                showErrorMessage(messageRow, "Error calculating password strength: " + t.getMessage());
            }

            @Override
            public void uponSuccess(PasswordAnalysisResults result)
            {
                int score = result.getPasswordScore();
                showMessage(messageRow, null);

                String localRequirementsMessage = result.getLocalRequirementsNotMetMessage();
                if (localRequirementsMessage != null)
                {
                    showErrorMessage(messageRow, localRequirementsMessage);
                }

                List<String> suggestions = result.getSuggestions();
                if (suggestions != null && suggestions.size() > 0)
                {
                    myLog("Sugestion size: " + suggestions.size());
                }
                boolean enable = false;
                switch (score)
                {
                case PasswordAnalysisResults.PASSWORD_WEAK:
                {
                    pbar.setPercent(20);
                    pbar.setText(glang.weak());
                    pbar.setType(ProgressBarType.DANGER);
                    enable = false;
                    break;
                }

                case PasswordAnalysisResults.PASSWORD_SO_SO:
                {
                    pbar.setPercent(20);
                    pbar.setText(glang.soso());
                    pbar.setType(ProgressBarType.DANGER);
                    enable = false;
                    break;
                }

                case PasswordAnalysisResults.PASSWORD_GOOD:
                {
                    pbar.setPercent(60);
                    pbar.setText(glang.good());
                    pbar.setType(ProgressBarType.INFO);
                    enable = false;
                    break;

                }
                case PasswordAnalysisResults.PASSWORD_STRONG:
                {
                    pbar.setPercent(80);
                    pbar.setText(glang.strong());
                    pbar.setType(ProgressBarType.INFO);
                    enable = true;
                    break;
                }

                case PasswordAnalysisResults.PASSWORD_VERY_STRONG:
                {
                    pbar.setPercent(100);
                    pbar.setText(glang.veryStrong());
                    pbar.setType(ProgressBarType.SUCCESS);
                    enable = true;
                    break;
                }
                }
                myLog("Entropy: " + result.getEntropy());
                entropyTextBox.setValue(result.getEntropy() + "");
                if (enable)
                {
                    // enableChangePasswordButton();
                } else
                {
                    // disableChangePasswordButton();
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        LoginService.Utility.getInstance().checkPassStrength(authCreds, pass, PASSWORD_COMPLEXITY_REQUIREMENTS,
                callback);
    }

    public static String bytesToString(byte[] bytes)
    {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String jsonStringVal(JSONObject jsonObject, String key)
    {
        return jsonObject.get(key).isString().stringValue();
    }

    public static ItemExpiration getExpiration(final Integer expirationDays, Integer expirationHours,
            Integer expirationMinutes)
    {
        if (expirationDays == null)
        {
            return null;
        }
        if (expirationHours == null)
        {
            expirationHours = 0;
        }
        if (expirationMinutes == null)
        {
            expirationMinutes = 0;
        }
        return new ItemExpiration(expirationDays, expirationHours, expirationMinutes);
    }

    public static String getShareMsg(LimitedItemDTO dto)
    {
        String msg = dto.isShareExpired() ? ObidosMessages.LANG.sharedItemHasExpired()
                : ObidosMessages.LANG.sharedItemHasExpiration();
        return msg + " on " + dto.getItemExpiration().getExpiresAt();
    }

    // return the HTML span for a shared item with tooltip, icon etc.
    // it is used for custom name cell of an item in the list of items screen
    public static String getSharedItemSpan(LimitedItemDTO dto, String value)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span style='cursor:pointer'");
        sb.append(" ");
        sb.append("title=");
        sb.append("'");
        sb.append(dto.isExpirationSet() ? getShareMsg(dto) : ObidosMessages.LANG.itemsSharedWithSomeone());
        sb.append("'");
        sb.append("</span>");
        sb.append("<div>");
        sb.append("	<span>");
        sb.append("<i class='fa ");
        if (dto.isExpirationSet())
        {
            sb.append("fa-clock-o");
        } else if (dto.getShared())
        {
            sb.append("fa-share-square");
        }
        // sb.append(dto.isExpirationSet() ? "fa-clock-o" : "fa-share-square");
        sb.append("' style='color:");
        sb.append(dto.isShareExpired() ? "red" : "green");
        sb.append("'></i>");
        sb.append("&nbsp;");
        sb.append(value);
        sb.append("	</span>");
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * 
     * @param title
     *            Title of the button
     * @param iconClass
     *            Icon class, can be null
     * @param iconColor
     *            Icon color, can be null
     * @param toolTip
     *            can be null
     * @return bootstrap3 button HTML string
     *         <p>
     * @author spgdev@spenego.com - Dec 14, 2018
     */
    public static String makeLinkButton(String title, String iconClass, String iconColor, String toolTip)
    {
        StringBuilder sb = new StringBuilder(256);
        if (toolTip != null)
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link' title=");
            sb.append("'");
            sb.append(toolTip);
            sb.append("'");
            sb.append(">");
        } else
        {
            sb.append("<button style=\"padding:0px\" id='add' type='button' class='btn btn-link'>");
        }
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(title);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeLinkButton2(String title, String iconClass, String iconClass2, String iconColor,
            String iconColor2, String toolTip)
    {
        StringBuilder sb = new StringBuilder(256);
        if (toolTip != null)
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link' title=");
            sb.append("'");
            sb.append(toolTip);
            sb.append("'");
            sb.append(">");
        } else
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link'>");
        }
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        // 2nd icon
        if (iconClass2 != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass2);
            sb.append("'");
            sb.append(" ");
            if (iconColor2 != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor2);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }

        sb.append(title);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeLinkButton3(String title, String iconClass, String iconClass2, String iconColor,
            String iconColor2, String iconClass3, String iconColor3, String toolTip)
    {
        StringBuilder sb = new StringBuilder(256);
        if (toolTip != null)
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link' title=");
            sb.append("'");
            sb.append(toolTip);
            sb.append("'");
            sb.append(">");
        } else
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link'>");
        }

        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        // 2nd icon
        if (iconClass2 != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass2);
            sb.append("'");
            sb.append(" ");
            if (iconColor2 != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor2);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        // 3rd icon
        if (iconClass3 != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass3);
            sb.append("'");
            sb.append(" ");
            if (iconColor3 != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor3);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }

        sb.append(title);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeMailtoButton(String title, String iconClass, String iconColor, String toolTip)
    {
        StringBuilder sb = new StringBuilder(256);
        if (toolTip != null)
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link' title=");
            sb.append("'");
            sb.append(toolTip);
            sb.append("'");
            sb.append(">");
        } else
        {
            sb.append("<button style=\"padding:0px\" type='button' class='btn btn-link'>");
        }
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        // sb.append("<a href=mailto:" + title + " target=\"_blank\">" + title +
        // "</a>");
        // sb.append(title);
        // <a href="mailto:someone@yoursite.com" target="_blank" rel="noopener
        // noreferrer">Email Us</a>
        String mailto = "<a href=mailto:" + title + " target=\"blank\"" + " rel=\"noopener noreferrer\">" + title
                + "</a>";
        sb.append(mailto);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeRegularText(String title, String iconClass, String iconColor, String toolTip)
    {
        StringBuilder sb = new StringBuilder(256);
        if (toolTip != null)
        {
            sb.append("<span title=");
            sb.append("'");
            sb.append(toolTip);
            sb.append("'");
            sb.append(">");
        } else
        {
            sb.append("<button type='button' class='btn btn-link'>");
        }
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(title);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeDisabledText(String text, String tooltip)
    {
        String color = ObidosConstants.GRAY_COLOR;
        StringBuilder sb = new StringBuilder(128);
        sb.append("<span style=");
        sb.append("'");
        sb.append("color:");
        sb.append(color);
        sb.append(";");
        sb.append("font-size:small");
        sb.append("'");
        // sb.append(" ");
        // sb.append("data-placement='top' data-toggle='tooltip'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append(">");
        sb.append(text);
        sb.append("</span>");
        return sb.toString();
    }

    public static String makeRegularText(String text, String tooltip)
    {
        // String color = ObidosConstants.GRAY_COLOR;
        StringBuilder sb = new StringBuilder(128);
        sb.append("<span ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append(">");
        sb.append(text);
        sb.append("</span>");
        String s = sb.toString();
        myLog(s);
        return s;
    }

    public static String makeTextButtonWithTooltip(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append("<span style=");
        sb.append("'");
        sb.append("font-size:small");
        sb.append("'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append(">");
        sb.append(text);
        sb.append("</span>");
        return sb.toString();
    }

    /*
     * public static String makeTextButtonWithTooltip(String text, String
     * tooltip,String spanGlypicon) { // spanGlipicon can be something like: //
     * <small><span class="glyphicon glyphicon-envelope"></small> StringBuilder
     * sb = new StringBuilder(128); if (!text.equals(ObidosMessages.LANG.na()))
     * { if (spanGlypicon != null) { sb.append(spanGlypicon); }
     * sb.append("<span style="); sb.append("'"); sb.append("font-size:small");
     * sb.append("'"); sb.append(" "); sb.append("title="); sb.append("\"");
     * sb.append(tooltip); sb.append("\""); sb.append(">"); sb.append(text);
     * sb.append("</span>"); return sb.toString(); } else { return
     * makeTextButtonWithTooltip(text, tooltip); } }
     */

    public static String makeDisabledTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type=\"button\" class=\"btn disabled\" style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");

        String iconClass = "fa fa-ban";
        String iconColor = "#000";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeDisabledTextButton(String text, String tooltip, String iconClass, String iconColor)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type=\"button\" class=\"btn disabled\" style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");

        if (iconClass == null)
        {
            iconClass = "fa fa-ban";
        }
        if (iconColor == null)
        {
            iconColor = "#000";
        }
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeNotSharedDisabledTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type=\"button\" class=\"btn disabled\" style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");

        String iconClass = "fa fa-ban";
        String iconColor = "#000";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeAddItemDisabledTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type=\"button\" class=\"btn disabled\" style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");

        String iconClass = "fa fa-ban";
        String iconColor = "#000";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeNotSharedDisabledTextButtonSAFE(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<button type='button' class='btn disabled'");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");

        String iconClass = "fa fa-ban";
        String iconColor = "#000";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();
    }

    public static String makeCanNotEditSharedItemTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<button type='button' class='btn disabled'");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");

        String iconClass = "fa fa-ban";
        String iconColor = "#000";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();

    }

    public static String makePrivateDisabledTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type='button' class='btn disabled' style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");
        String iconClass = "fa fa-lock";
        String iconColor = "#000";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();

    }

    public static String makeShareableDisabledTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type='button' class='btn disabled' style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");
        String iconClass = "fa fa-share-alt-square";
        String iconColor = "green";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();

    }

    public static String makeSharedDisabledTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "<button type='button' class='btn disabled' style=\"padding:0px;cursor:not-allowed;background:none;border:none;outline:none\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=\"" + tooltip + "\"");
        }
        sb.append(">");
        String iconClass = "fa fa-check";
        String iconColor = "green";
        if (iconClass != null)
        {
            sb.append("<i class=");
            sb.append("'");
            sb.append(iconClass);
            sb.append("'");
            sb.append(" ");
            if (iconColor != null)
            {
                sb.append("area-hidden='true' style='color:");
                sb.append(iconColor);
                sb.append("'");
            }
            sb.append("></i>");
            sb.append(" ");
        }
        sb.append(text);
        sb.append("</button>");

        return sb.toString();

    }

    public static String makeSharedWithOthersTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span style='cursor:pointer' data-placement='right' data-toggle='tooltip'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append("</span>");

        sb.append("<div>");
        sb.append("	<span>");
        sb.append("		​<i class='fa fa-share-square' style='color:green'></i>");
        sb.append("&nbsp;");
        sb.append(text);
        sb.append("	</span>");
        sb.append("</div>");
        return sb.toString();
    }

    public static String makeViewTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span style='cursor:pointer;font-size:small' data-placement='right' data-toggle='tooltip'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append("</span>");

        sb.append("<div>");
        sb.append("	<span>");
        sb.append("		​<i class='fa fa-eye' style='color:blue'></i>");
        sb.append("&nbsp;");
        sb.append(text);
        sb.append("	</span>");
        sb.append("</div>");
        return sb.toString();
    }

    public static String makeViewTextButton(String text, String tooltip, String iconClass, String iconColor)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span style='cursor:pointer;font-size:small' data-placement='right' data-toggle='tooltip'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append("</span>");

        sb.append("<div>");
        sb.append("	<span>");
        // sb.append(" ​<i class='fa fa-eye' style='color:blue'></i>");
        sb.append("<i class='");
        sb.append(iconClass);
        sb.append("'");
        sb.append(" style='color:");
        sb.append(iconColor);
        sb.append("'></i>");
        sb.append("&nbsp;");
        sb.append(text);
        sb.append("	</span>");
        sb.append("</div>");
        return sb.toString();
    }

    public static String makeRelinquishedTextButton(String text, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span style='cursor:not-allowed' data-placement='right' data-toggle='tooltip'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append("</span>");

        sb.append("<div>");
        sb.append("	<span>");
        sb.append("		​<i class='fa fa-arrow-circle-o-right' style='color:black'></i>");
        sb.append("&nbsp;");
        sb.append(text);
        sb.append("	</span>");
        sb.append("</div>");
        return sb.toString();
    }

    public static String makeNonclickableTextButton(String text, String tooltip, String textColor, String iconClass,
            String iconColor)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span style='cursor:not-allowed;");
        sb.append("color:");
        sb.append(textColor);
        sb.append(";");
        sb.append("font-size:normal");
        sb.append("'");
        sb.append(" ");
        sb.append("data-placement='right' data-toggle='tooltip'");
        sb.append(" ");
        sb.append("title=");
        sb.append("\"");
        sb.append(tooltip);
        sb.append("\"");
        sb.append("</span>");
        sb.append("<div>");
        sb.append("	<span>");
        sb.append("<i class=\"");
        sb.append(iconClass);
        sb.append(" ");
        sb.append("area-hidden='true' style='color:");
        sb.append(iconColor);
        sb.append("\"></i>");
        sb.append("&nbsp;");
        sb.append(text);
        sb.append("	</span>");
        sb.append("</div>");
        return sb.toString();
    }

    public static String makeNonclickableTextButtonX(String text, String tooltip, String textColor, String iconClass,
            String iconColor)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span>");
        sb.append("<div style=\"text-align:left;\">");
        sb.append(
                "<input type=\"submit\" class=\"SubmitButtonClass\" style=\"cursor:not-allowed;border:none;background:none;padding:6px 12px\">");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=");
            sb.append("\"");
            sb.append(tooltip);
            sb.append("\"");
        }
        sb.append("<i class=\"fa fa-facebook-square fa-large\" aria-hidden=\"true\"></i>");
        /*
         * if (iconClass != null) { sb.append(" "); sb.append("<i class=\"");
         * sb.append(iconClass); sb.append(" ");
         * sb.append("area-hidden='true' style='color:"); sb.append(iconColor);
         * sb.append("\"></i>"); }
         */
        sb.append(" />");
        sb.append(text);
        sb.append("</div>");
        sb.append("</span>");

        /*
         * sb.append("<span style='cursor:not-allowed;text-align:left;");
         * sb.append("color:"); sb.append(textColor); sb.append(";");
         * sb.append("font-size:normal"); sb.append("'"); sb.append(" ");
         * sb.append("data-placement='right' data-toggle='tooltip'");
         * sb.append(" "); sb.append("title="); sb.append("\"");
         * sb.append(tooltip); sb.append("\""); sb.append("</span>");
         * 
         * sb.append("<div>"); sb.append("	<span>"); sb.append("<i class=\"");
         * sb.append(iconClass); sb.append(" ");
         * sb.append("area-hidden='true' style='color:"); sb.append(iconColor);
         * sb.append("\"></i>"); sb.append("&nbsp;"); // sb.append(text);
         * 
         * sb.
         * append("<input type=\"submit\" class=\"SubmitButtonClass\" style=\"cursor:not-allowed;border:none;background:none;padding:6px 12px\" value=\""
         * ); sb.append(text); sb.append("\""); sb.append(" />");
         * sb.append("	</span>"); sb.append("</div>");
         */
        return sb.toString();
    }

    public static String makeNonClickableButton(String title, String tooltip)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<span>");
        sb.append("<div style=\"text-align:left;\">");
        // sb.append("<input type=\"submit\" class=\"SubmitButtonClass\"
        // style=\"padding:0px;cursor:not-allowed;border:none;background:none;\"
        // value=\"");
        sb.append(
                "<input type=\"submit\" class=\"SubmitButtonClass\" style=\"padding:0px;cursor:default;border:none;background:none;\" value=\"");
        sb.append(title);
        sb.append("\"");
        if (tooltip != null)
        {
            sb.append(" ");
            sb.append("title=");
            sb.append("\"");
            sb.append(tooltip);
            sb.append("\"");
        }

        sb.append(" />");
        sb.append("</div>");
        sb.append("</span>");
        return sb.toString();
    }

    // not disabled
    public static void makeNonClickableSharedItemNoLongerAvaiable(String text)
    {
        // ClientUtils.makeNonclickableTextButton(text, String tooltip, String
        // iconClass)
    }

    public static String makeUnknownTextButton()
    {
        String iconClass = "fa fa-question";
        String title = ObidosMessages.LANG.unknown();
        String textColor = ObidosConstants.BLACK_COLOR;
        return ClientUtils.makeNonclickableTextButton(title, title, textColor, iconClass, textColor);
    }

    public static String makeNotificationButton(String title, String link)
    {
        // String iconClass = "fa fa-ban";
        // return link != null ? ClientUtils.makeLinkButton(title, null, null,
        // title) : ClientUtils.makeNonclickableTextButton(" " + title, title,
        // ObidosConstants.BLACK_COLOR, null, ObidosConstants.BLACK_COLOR);
        // return link != null ? ClientUtils.makeLinkButton(title, null, null,
        // title) : ClientUtils.makeNonclickableTextButton(title, title,
        // ObidosConstants.BLACK_COLOR, iconClass, ObidosConstants.BLACK_COLOR);
        return link != null ? ClientUtils.makeLinkButton(title, null, null, title)
                : ClientUtils.makeNonClickableButton(title, title);
    }

    public static String makeShareButton()
    {
        String title = " " + ObidosMessages.LANG.shareButtonTitle();
        String iconClass = "fa fa-share-alt-square";
        String toolTip = null;
        String iconColor = null;
        return ClientUtils.makeLinkButton(title, iconClass, iconColor, toolTip);
    }

    // if license does not allow to share
    public static String makeDisabledShareButton(String tooltip)
    {
        String title = " " + ObidosMessages.LANG.shareButtonTitle();
        String iconClass = "fa fa-share-alt-square";
        String iconColor = ObidosConstants.REVOKE_ICON_COLOR;
        return ClientUtils.makeDisabledTextButton(title, tooltip, iconClass, iconColor);
    }

    public static String makeRevokeButton()
    {
        String iconClass = "fa fa-ban";
        String title = ObidosMessages.LANG.revoke();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.REVOKE_ICON_COLOR, null);
    }

    public static String makeRevokeButton(String title, String tooltip)
    {
        String iconClass = "fa fa-minus-circle";
        return ClientUtils.makeDisabledTextButton(title, tooltip, iconClass, ObidosConstants.REVOKE_ICON_COLOR);
    }

    // an item/container is shared but now revoked
    public static String makeRevokedButton(String title, String tooltip)
    {
        String iconClass = "fa fa-ban";
        String iconColor = ObidosConstants.REVOKE_ICON_COLOR;
        String textColor = ObidosConstants.BLACK_COLOR;
        return ClientUtils.makeNonclickableTextButton(title, tooltip, textColor, iconClass, iconColor);
    }

    public static String makeOwnedButton(String title, String tooltip)
    {
        String iconClass = "fa fa-angle-left";
        String iconColor = ObidosConstants.REVOKE_ICON_COLOR;
        String textColor = ObidosConstants.BLACK_COLOR;
        return ClientUtils.makeNonclickableTextButton(title, tooltip, textColor, iconClass, iconColor);
    }

    public static String makeLockedUserButton(String title, String tooltip)
    {
        String iconClass = "fa fa-lock";
        String iconColor = ObidosConstants.BOOTSTRAP3_PRAMRY_BGCOLOR;
        // return makeDisabledTextButton(title, tooltip, iconClass, iconColor);
        // return ClientUtils.makeViewTextButton(title, tooltip, iconClass,
        // iconColor);
        return ClientUtils.makeRegularText(title, iconClass, iconColor, "");
    }

    public static String makeDeletedUserButton(String title, String tooltip)
    {
        String iconClass = "fa fa-trash";
        String iconColor = ObidosConstants.RED_COLOR;
        return makeDisabledTextButton(title, tooltip, iconClass, iconColor);
    }

    public static String makeRenamedButton(String title, String tooltip)
    {
        String iconClass = "fa fa-pencil-square-o";
        String iconColor = "blue";
        return ClientUtils.makeViewTextButton(title, tooltip, iconClass, iconColor);
    }

    // has been relinquished
    public static String makeRelinquishedButton(String title, String tooltip)
    {
        String iconClass = "fa fa-arrow-circle-o-right";
        String iconColor = ObidosConstants.REVOKE_ICON_COLOR;
        String textColor = ObidosConstants.BLACK_COLOR;
        return ClientUtils.makeNonclickableTextButton(title, tooltip, textColor, iconClass, iconColor);
    }

    public static String makeDeletedButton(String title, String tooltip)
    {
        String iconClass = "fa fa-trash";
        String iconColor = ObidosConstants.REVOKE_ICON_COLOR;
        String textColor = ObidosConstants.BLACK_COLOR;
        return ClientUtils.makeNonclickableTextButton(title, tooltip, textColor, iconClass, iconColor);
    }

    public static String makeNonClickableRootAdminTextButton(String title, String tooltip)
    {
        String iconClass = "fa fa-circle";
        String iconColor = ObidosConstants.REVOKE_ICON_COLOR;
        String textColor = ObidosConstants.BLACK_COLOR;
        return ClientUtils.makeNonclickableTextButton(title, tooltip, textColor, iconClass, iconColor);
    }

    public static String makeListItemsInContainerButton(final String title, final String tooltip)
    {
        // String iconClass = "fa fa-eye";
        // String iconClass = "fa fa-list-alt";
        // return ClientUtils.makeLinkButton(title, iconClass,
        // ObidosConstants.VIEW_ICON_COLOR, tooltip);
        String iconClass = null;
        return ClientUtils.makeLinkButton(title, iconClass, null, tooltip);
    }

    public static String makeViewItemButton(final String title, final boolean shared, final boolean hasExpiration,
            final boolean hasExpired, final String tooltip)
    {

        String iconClass = null;
        String iconColor = null;
        if (shared)
        {
            iconClass = "fa fa-share-square";
            iconColor = null;

        }
        if (hasExpiration)
        {
            iconClass = "fa fa-clock-o";
            iconColor = "green";
            if (hasExpired)
            {
                iconColor = "red";
            }
            if (shared)
            {
                String iconClass2 = "fa fa-share-square";
                String iconColor2 = null;
                return makeLinkButton2(title, iconClass, iconClass2, iconColor, iconColor2, tooltip);
            }
            return makeLinkButton(title, iconClass, iconColor, tooltip);
        }
        return ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);
        // TODO show a document icon if the item has cone
        // return ClientUtils.makeLinkButton3(title, null, null, null, null, "fa
        // fa-file", null, tooltip);
    }

    // relinquish (verb)
    public static String makeRelinquishButton()
    {
        String iconClass = "fa fa-minus-circle";
        String title = ObidosMessages.LANG.relinquishLabel();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.RELINQUISH_ICON_COLOR, null);
    }

    public static String makeColoredTextButton(String title, String tooltip, String fgColor, String bgColor)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append("<div style=\"background-color:" + bgColor + ";color:" + fgColor);
        sb.append(" ");
        sb.append("title=\"");
        sb.append(title);
        sb.append("\"");
        sb.append(">");
        sb.append(title);
        sb.append("</div>");
        return sb.toString();
    }

    public static String makeAddButton()
    {
        String iconClass = "fa fa-plus-circle";
        String title = ObidosMessages.LANG.addButtonTitle();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.ADD_ICON_COLOR, null);
    }

    public static String makeEditButton()
    {
        String iconClass = "fa fa-edit";
        String title = ObidosMessages.LANG.editButtonTitle();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.EDIT_ICON_COLOR, null);
    }

    public static String makeTestButton()
    {
        // String iconClass = "fa fa-connectdevelop";
        String iconClass = "fa fa-arrows-h";
        String title = ObidosMessages.LANG.testButtonTitle();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.EDIT_ICON_COLOR, null);
    }

    public static String makeDeleteButton()
    {
        String iconClass = "fa fa-times-circle";
        String title = ObidosMessages.LANG.deleteButtonTitle();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.DELETE_ICON_COLOR, null);
    }

    public static String makeRemoveBtton()
    {
        String iconClass = "fa fa-times-circle";
        String title = ObidosMessages.LANG.removeButtonTitle();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.DELETE_ICON_COLOR, null);
    }

    public static String makeViewButton()
    {
        String iconClass = "fa fa-eye";
        String title = ObidosMessages.LANG.viewButtonTitle();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.VIEW_ICON_COLOR, null);
    }

    public static String makeCopyButton()
    {
        String iconClass = "fa fa-copy";
        String title = ObidosMessages.LANG.copy();
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.EDIT_ICON_COLOR, null);
    }

    public static String makeVeiwButton(String title, String tooltip)
    {
        String iconClass = "fa fa-eye";
        if (title == null)
        {
            title = ObidosMessages.LANG.viewButtonTitle();
        }
        return ClientUtils.makeLinkButton(title, iconClass, ObidosConstants.VIEW_ICON_COLOR, tooltip);
    }

    public static String makeListButton()
    {
        String iconClass = "fa fa-list-alt";
        String title = ObidosMessages.LANG.list();
        return ClientUtils.makeLinkButton(title, iconClass, null, null);
    }

    public static String makeSharedWithUsersTextbutton(final String title, final String tooltip)
    {
        String iconClass = "fa fa-users";
        return ClientUtils.makeLinkButton(title, iconClass, null, tooltip);
    }

    public static String makeUsersTextbutton(final String title, final String tooltip)
    {
        String iconClass = "fa fa-users";
        return ClientUtils.makeLinkButton(title, iconClass, null, tooltip);
    }

    public static String makeUserTextButton()
    {
        String iconClass = "fa fa-user";
        String iconColor = "#337AB7";
        String title = ObidosMessages.LANG.user();
        String tooltip = ObidosMessages.LANG.sharedWithUser();
        return makeRegularText(title, iconClass, iconColor, tooltip);
    }

    public static String makeGroupTextButton()
    {
        String iconClass = "fa fa-users";
        String iconColor = "#337AB7";
        String title = ObidosMessages.LANG.group();
        String tooltip = ObidosMessages.LANG.sharedWithGroup();
        return makeRegularText(title, iconClass, iconColor, tooltip);
    }

    public static void addItemOnResetCallback(CheckBox checkBox, ObidosIntegerTextBox textBox, FlowPanel flowPanel,
            InlineRadio publicRadio, InlineRadio privateRadio, boolean publicContainer, boolean publicItem)
    {
        checkBox.setVisible(true);
        checkBox.setValue(false);
        flowPanel.setVisible(true);
        textBox.setEnabled(false);
        textBox.setValue(null);
        if (!publicContainer)
        {
            publicRadio.setEnabled(false);
            privateRadio.setEnabled(false);
            privateRadio.setValue(true);

            checkBox.setVisible(false);
            flowPanel.setVisible(false);
        } else
        {
            // If Container is Public, make Item Sharable by default as per
            // Mike's suggestion
            // May-29-2018
            publicRadio.setEnabled(true);
            publicRadio.setValue(true);
            privateRadio.setEnabled(true);

        }
    }

    public static void addItemOnResetCallback(Row expiresRow, InlineRadio publicRadio, InlineRadio privateRadio,
            boolean publicContainer, boolean publicItem)
    {
        if (!publicContainer)
        {
            publicRadio.setEnabled(false);
            privateRadio.setEnabled(false);
            privateRadio.setValue(true);
            expiresRow.setVisible(false);
        } else
        {
            // If Container is Public, make Item Sharable by default as per
            // Mike's suggestion
            // May-29-2018
            publicRadio.setEnabled(true);
            publicRadio.setValue(true);
            privateRadio.setEnabled(true);
            expiresRow.setVisible(true);

        }
    }

    public static void addItemCheckBoxCallback(CheckBox checkBox, ObidosIntegerTextBox textBox, FlowPanel flowPanel)
    {
        flowPanel.setVisible(true);
        if (checkBox.getValue() == true)
        {
            textBox.setEnabled(true);
        } else
        {
            textBox.setEnabled(false);
        }

    }

    public static void editItemCheckBoxCallback(CheckBox checkBox, ObidosIntegerTextBox textBox, FlowPanel flowPanel,
            Integer savedExpireDays)
    {
        flowPanel.setVisible(true);
        textBox.setValue(null);
        if (checkBox.getValue() == true)
        {
            textBox.setEnabled(true);
            if (savedExpireDays != null)
            {
                textBox.setValue(savedExpireDays);
            }
        } else
        {
            textBox.setEnabled(false);
        }

    }

    // get date format for DatePicker widget
    public static String getDateFormat(final CurrentUser currentUser)
    {
        String format = ObidosConstants.DEFAULT_DATEPICKER_DATE_FORMAT;
        if (currentUser == null)
        {
            return format;
        }
        LoginResult ls = currentUser.getLoginResult();
        if (ls == null)
        {
            return format;
        }
        String f = ls.getDateFormat();
        if (f == null || f.length() == 0)
        {
            return format;
        }
        return f;
    }

    // write error message and return null on error
    public static Date getDateFromDatePickerAndTimeBox(final CurrentUser currentUser, final DatePicker datePicker,
            final ObidosTimeBox timeBox, final ObidosMessageRow messageRow)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        Date date = datePicker.getValue();
        String dateString = datePicker.getTextBox().getValue();
        // validate first
        if (!dateString.isEmpty())
        {
            if (date == null)
            {
                messageRow.showErrorMessage(lang.invalidDate());
                return null;
            }
        }
        if (date == null)
        {
            return null;
        }
        Long timeVal = timeBox.getValue();
        if (timeVal == null)
        {
            messageRow.showErrorMessage(lang.invalidTimeValue() + ": " + timeBox.getText());
            return null;
        }
        String bootstrapDateFormat = getDateFormat(currentUser);
        // bootstrap and gwt date formats are different.
        // bootstrap uses mm for day for month, gwt uses MM. We use GWT's
        // DateTimeFormat, so we have to convert.
        String gwtDateFormat = getBootstrap2GwtDateFormat(bootstrapDateFormat);

        String timeFormat = timeBox.getTimeFormat();
        String format = gwtDateFormat + " " + timeFormat;
        String dateTimeString = dateString + " " + timeBox.getText();
        return DateTimeFormat.getFormat(format).parse(dateTimeString);
    }

    // editItemUpdateForm() sets the selection for Shareable & Private radio
    // buttons
    // and the Shareable & Private icons next to name of Container & Item

    public static void editItemUpdateForm(final Row expiresRow, final DatePicker datePicker, InlineRadio publicRadio,
            InlineRadio privateRadio, Button permissionButton, boolean isEditing, TextBox containerNameTextBox,
            FormLabel containerShareableLabel, FormLabel itemShareableLabel, ItemDTO dto)
    {

        // +++++++++++ First set the radio buttons for Shareable and Public
        // ++++++++++++++++
        // +++++++++++ This is to switch between Shareable and Public item type
        // ++++++++++++++++

        // if Item is Private, disable radio buttons and lock private radio
        myLog("CCCC1 " + dto.getShareable());
        boolean x = ClientUtils.fromBoolean(dto.getShareable());
        boolean itemIsPrivate = !x;
        myLog("CCCC2 " + itemIsPrivate);
        boolean containerIsPrivate = ClientUtils.fromBoolean(dto.getIsContainerPrivate());
        
        myLog("CCCC3 " + containerIsPrivate);
        containerNameTextBox.setValue(dto.getContainerName());

        // Container is private, Item type cannot be changed from Private
        if (containerIsPrivate)
        {
            publicRadio.setEnabled(false);
            publicRadio.setValue(false);
            privateRadio.setEnabled(false);
            privateRadio.setValue(true);

            // Permission button is for setting permissions for shared users
            permissionButton.setVisible(false);
            expiresRow.setVisible(false);
        } else // Container is Shareable
        {
            if (itemIsPrivate)
            {
                publicRadio.setEnabled(true);
                publicRadio.setValue(false);
                privateRadio.setEnabled(true);
                privateRadio.setValue(true);

                permissionButton.setVisible(false);
                expiresRow.setVisible(false);
            } else // Shareable Item
            {
                publicRadio.setEnabled(true);
                publicRadio.setValue(true);
                privateRadio.setEnabled(true);
                privateRadio.setValue(false);

                permissionButton.setVisible(true);
                expiresRow.setVisible(true);

                if (dto.isExpirationSet())
                {
                    Integer expireDays = dto.getItemExpiration().getDaysUntilExpire();
                    if (expireDays != null)
                    {
                        // don't bother to calculate date, just the one returned
                        Date expirationDate = dto.getItemExpiration().getExpiresAt();
                        datePicker.setTitle(expirationDate.toString());
                        datePicker.setValue(expirationDate); // short formatted
                                                             // date
                    } else
                    {

                    }
                } else
                {
                }

                if (!isEditing)
                {
                }
            }
        }

        // +++++++++++ End of setting radio buttons for Shareable and Public
        // Items ++++++++++++++

        // +++++++++++ Now set the Shareable and Private icons on right of
        // Container & Item names displayed in UI ++++++++++++++

        boolean shareableItem = ClientUtils.fromBoolean(dto.getShareable());
        if (!shareableItem)
        {
            itemShareableLabel.setHTML(glang.privateX());
        } else
        {
            itemShareableLabel.setHTML(glang.shareable());
        }

        // ++++ End of setting icon for Item

        // ++++ Start of setting icon for Container

        boolean priv = ClientUtils.fromBoolean(dto.getIsContainerPrivate());
        if (priv)
        {

            containerShareableLabel.setHTML(glang.privateX());
        } else
        {
            containerShareableLabel.setHTML(glang.shareable());
            // Shareable Item in Shareable Container. Shareable Item can be
            // changed to Private
            if (!itemIsPrivate)
            {
                if (isEditing)
                {
                    publicRadio.setEnabled(true);
                    privateRadio.setEnabled(true);
                    publicRadio.setValue(true);
                    privateRadio.setValue(false);

                    permissionButton.setVisible(true);
                } else
                {
                    publicRadio.setEnabled(false);
                    privateRadio.setEnabled(false);
                    publicRadio.setValue(true);
                    privateRadio.setValue(false);

                    permissionButton.setVisible(false);
                }
            }
            // Private Item in Shareable Container. Private Item can be changed
            // to Shareable
            else
            {
                if (isEditing)
                {
                    publicRadio.setEnabled(true);
                    privateRadio.setEnabled(true);
                    publicRadio.setValue(false);
                    privateRadio.setValue(true);

                    permissionButton.setVisible(false);
                } else
                {
                    publicRadio.setEnabled(false);
                    privateRadio.setEnabled(false);
                    publicRadio.setValue(false);
                    privateRadio.setValue(true);

                    permissionButton.setVisible(false);
                }
            }
        }

    }

    public static void itemPublicRadioCallback(CheckBox checkBox, ObidosIntegerTextBox textBox, FlowPanel flowPanel)
    {
        checkBox.setVisible(true);
        // checkBox.setValue(false);
        flowPanel.setVisible(true);
        textBox.setValue(null);

    }

    public static void itemPrivateRadioCallback(CheckBox checkBox, ObidosIntegerTextBox textBox, FlowPanel flowPanel)
    {
        checkBox.setVisible(false);
        checkBox.setValue(false);
        flowPanel.setVisible(false);
        textBox.setValue(null);
    }

    public static void enableRadios(InlineRadio publicRadio, InlineRadio privateRadio, boolean enable)
    {
        publicRadio.setEnabled(enable);
        privateRadio.setEnabled(enable);
        publicRadio.setValue(false);
        privateRadio.setValue(true);
    }

    public static Throwable getExceptionToDisplay(Throwable throwable)
    {
        Throwable result = throwable;
        if (throwable instanceof UmbrellaException && ((UmbrellaException) throwable).getCauses().size() >= 1)
        {
            result = ((UmbrellaException) throwable).getCauses().iterator().next();
        }
        return result;
    }

    public static Throwable unwrap(Throwable e)
    {
        if (e instanceof UmbrellaException)
        {
            UmbrellaException ue = (UmbrellaException) e;
            if (ue.getCauses().size() == 1)
            {
                return unwrap(ue.getCauses().iterator().next());
            }
        }
        return e;
    }

    public static void setupGWTUmbrellaExceptionHandler()
    {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
        {
            public void onUncaughtException(Throwable e)
            {
                if (e.getMessage().contains("IndexSizeError"))
                {
                    // summernote Bangla input throws that exception at times
                    GWT.log("MMM Ara Summernote exception caught and handled: " + e.getMessage());
                } else
                {
                    String msg = ObidosMessages.LANG.crashed();
                    StackTraceElement[] stackTraceElements = e.getStackTrace();
                    StringBuilder trace = new StringBuilder(256);
                    trace.append(e).append('\n');
                    for (StackTraceElement element : stackTraceElements)
                    {
                        trace.append(element.toString()).append('\n');
                    }
                    Window.alert(msg + trace.toString());
                }
            }
        });
    }

    // https://stackoverflow.com/questions/5693082/gwt-browser-window-resized-handler
    public static <T> void addWindowResizeHandler(final DataGrid<T> grid)
    {
        Window.addResizeHandler(new ResizeHandler()
        {
            @Override
            public void onResize(ResizeEvent event)
            {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                {
                    public void execute()
                    {
                        /*
                         ** The following activates bootstrap's tooltip but I
                         * personally like the HTML ones
                         */
                        /*
                         * if (ClientUtils.isjQueryInjected()) {
                         * ClientUtils.initjQueryTooltips(); }
                         */
                        adjustDataGridHeight(grid);
                    }
                });
            }

        });
    }

    public static <T> void refreshDataGrid(final DataGrid<T> grid)
    {
        Range range = new Range(0, ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range, true);
    }

    public static int getClientHeight()
    {
        return Window.getClientHeight();
    }

    public static int getClientWidth()
    {
        return Window.getClientWidth();
    }

    public static <T> void adjustDataGridHeight(final DataGrid<T> grid)
    {
        int h = Window.getClientHeight();
        // myLog(">>>>>>>>>>>>>>>>>>>>>>>>> Window height: " + h);
        int absoluteTop = grid.getAbsoluteTop();
        // myLog(">>>>>>>>>>>>>>>>>>>>>>>> ABS Top: " + absoluteTop);
        // double hh = h - 300 ;
        // the following seems to position the Pager almost perfectly at the
        // bottom of the page
        double hh = h - absoluteTop - 67;
        h = (int) hh;
        // myLog(">>>>>>>>>>>>>>>>>>>>>>>> grid h: " + h);
        // // height can not be negative
        if (h > 0)
        {
            String hs = Integer.toString(h) + "px";
            // myLog(">>>>>>>>>>>>>>>>>>>>>>>> Setting grid height to: " + hs);
            grid.setHeight(hs);
            // refreshDataGrid(grid);
        } else
        {
            h = Window.getClientHeight();
            hh = h - h * .80;
            h = (int) hh;
            String hs = Integer.toString(h) + "px";
            myLog("--------- WARNING: negative grid height, setting hs: " + hs);
            grid.setHeight(hs);
            myLog("--- grid offset height; " + grid.getOffsetHeight());
            // hs = Integer.toString(300) + "px";
            // myLog("--------- WARNING: setting hs: " + hs);
            // grid.setHeight(hs);
        }

    }

    public static int getScreenHeight(int percent)
    {
        int h = Window.getClientHeight();
        if (percent > 100)
        {
            percent = 100;
        }
        percent = percent / 100;
        return h * percent;
    }

    public static boolean isContainerPrivate(ContainerDTO dto)
    {
        Boolean isPrivate = dto.getIsPrivate();
        // I noticed this thing can be null
        if (isPrivate == null)
        {
            isPrivate = true;
        }
        return isPrivate;
    }

    public static boolean isContainerSharedWithSomeone(ContainerDTO dto)
    {
        Boolean shared = dto.getShared();
        if (shared == null)
        {
            shared = false;
        }
        return shared;
    }

    public static boolean isItemPublic(LimitedItemDTO dto)
    {
        Boolean shareable = dto.getShareable();
        if (shareable == null)
        {
            return false;
        }
        return shareable;
    }

    public static boolean isItemPrivate(LimitedItemDTO dto)
    {
        if (isItemPublic(dto))
        {
            return false;
        }
        return true;
    }

    /**
     * populate form in view item, edit item page
     * 
     * @param dto
     *            ItemDTO
     * @param formGroup
     *            FromGroup Client must create and pass it
     * @param textboxList
     *            populate with textBox if not null (returns)
     * @param textBoxFieldValueDTOMap
     *            populate with textBox and UserDefinedFieldValuteDTO if not
     *            null (returns)
     * @param summernoteFieldValueDTOMap
     *            populate if not null (returns)
     * @return true if the item is note, false otherwise
     *         <p>
     * @author spgdev@spenego.com - Jan 2, 2019
     */
    public static ViewItemFormData populateFormWithItem(boolean readOnly, boolean isSecure, ItemDTO dto,
            FormGroup formGroup, ArrayList<TextBox> textboxList,
            Map<TextBox, UserDefinedFieldValueDTO> textBoxFieldValueDTOMap,
            Map<Summernote, UserDefinedFieldValueDTO> summernoteFieldValueDTOMap)
    {
        ViewItemFormData itemFormData = new ViewItemFormData();
        itemFormData.setNote(false);

        formGroup.clear();
        List<UserDefinedTypeValueDTO> typeValues = dto.getValues();
        myLog("TypeValue size: " + typeValues.size());
        myLog("MMM created at: " + dto.getCreatedAt());
        myLog("MMM updated at: " + dto.getUpdatedAt());

        for (UserDefinedTypeValueDTO typeValue : typeValues)
        {
            List<UserDefinedFieldValueDTO> fieldValues = typeValue.getFieldValues();
            if (!typeValue.isNote())// not note
            {
                for (UserDefinedFieldValueDTO fieldValueDTO : fieldValues)
                {
                    String labelText = fieldValueDTO.getName();
                    String fieldText = "";
                    byte[] fieldBytes = fieldValueDTO.getBlobValue();
                    myLog(">>>>>>>>>>>>>>>>>>>>>>. Field Bytes: " + fieldBytes);
                    if (fieldBytes == null)
                    {
                        myLog(">>>>>>>>>>>>>>>>>>>>>>. Field Bytes is null");
                        continue;
                    }
                    try
                    {
                        fieldText = new String(fieldBytes, "UTF-8");
                    } catch (UnsupportedEncodingException e)
                    {
                        fieldText = "UTF-8 decode Error";
                    }
                    // we don't display 2fa othAuthUri
                    if (fieldValueDTO.getType() == UserDefinedFieldDTO.TYPE_QRCODE)
                    {
                        itemFormData.setOthAuthUri(fieldText);
                        continue;
                    }

                    ObidosItemRow itemRow = new ObidosItemRow(readOnly, labelText, fieldText, isSecure);
                    formGroup.add(itemRow);
                    TextBox textBox = itemRow.getTextBox();
                    if (textboxList != null)
                    {
                        textboxList.add(textBox);
                    }
                    if (textBoxFieldValueDTOMap != null)
                    {
                        textBoxFieldValueDTOMap.put(textBox, fieldValueDTO);
                    }
                }
            } else // Note
            {
                UserDefinedFieldValueDTO fvDTO = fieldValues.get(0);
                if (fvDTO != null)
                {
                    DocumentDTO docDTO = fvDTO.getDocument();
                    if (docDTO != null)
                    {
                        byte[] filenameBytes = docDTO.getFilename();
                        if (filenameBytes != null)
                        {
                            String filename = new String(docDTO.getFilename());
                            myLog("Filename: " + filename);
                        }
                    } else
                    {
                        myLog("Document DTO is null");
                    }
                }
                // why are we here twice sometimes???
                myLog("It's a note");
                itemFormData.setNote(true);
                String noteText = "Could  not decode UTF-8 note";
                // we have only one Note
                byte[] noteBytes = fieldValues.get(0).getBlobValue();
                try
                {
                    noteText = new String(noteBytes, "UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                }

                ObidosNoteRow noteRow = new ObidosNoteRow(readOnly, noteText, ObidosConstants.NOTE_HEIGHT);
                formGroup.add(noteRow);
                if (summernoteFieldValueDTOMap != null)
                {
                    summernoteFieldValueDTOMap.put(noteRow.getSummernote(), fieldValues.get(0));
                }
            }
        }
        return (itemFormData);
    }

    public static void setCopytoClipboardButtonHander(final Button copyButton, final TextBox textBox)
    {
        copyButton.setTitle(glang.copy());
        copyButton.setDataLoadingText("✓");
        copyButton.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                int t = Integer.parseInt(ObidosMessages.LANG.copyTooltipTimerSchedule());
                String text = textBox.getText();
                if (text != null && text.length() > 0)
                {
                    ClientUtils.copyTextToClipboard(text);
                    copyButton.state().loading();
                    new Timer()
                    {
                        @Override
                        public void run()
                        {
                            copyButton.state().reset();
                        }
                    }.schedule(t);
                }
            }
        });
    }

    public static void showHelp(BlockQuote helpBlockQuote)
    {
        helpBlockQuote.setVisible(!helpBlockQuote.isVisible());
    }

    public static void validateDisplayOrderValues(ArrayList<ObidosIntegerTextBox> orderTextBoxes)
            throws ClientSideException
    {
        if (orderTextBoxes == null)
        {
            return;
        }
        Integer[] orderArray = new Integer[orderTextBoxes.size()];
        for (int i = 0; i < orderTextBoxes.size(); i++)
        {
            ObidosIntegerTextBox integerTextBox = orderTextBoxes.get(i);
            int intValue = integerTextBox.getValue();
            // int row = i + 2;

            orderArray[i] = intValue;

            /*
             * if (intValue <= 1) { showErrorMessage("Invalid number '" +
             * valueString + "' at row: " + row); return false; }
             * 
             * if (intValue >= (nBoxes + 2)) {
             * showErrorMessage("Invalid Display Oder Value: " + intValue +
             * " at row: " + row); return false; }
             */
        }
        List<Integer> list = Arrays.asList(orderArray);
        List<Integer> dps = list.stream().distinct().filter(entry -> Collections.frequency(list, entry) > 1)
                .collect(Collectors.toList());
        if (dps.size() > 0)
        {
            throw new ClientSideException("Duplicate Display Order number " + dps.get(0));
        }

    }

    public static void setNoteSpanHtml(Span span, String label)
    {
        String tt = ObidosMessages.LANG.encrypted();
        String html = "<i class='fa fa-lock' title='" + tt + "'" + " style='color: green;'></i> "
                + "<span style='font-weight:bold;'>" + label + "</span>";
        // right align the span
        // Ref:
        // https://stackoverflow.com/questions/5067279/how-to-align-this-span-to-the-right-of-the-div
        span.getElement().getStyle().setProperty("float", "right");
        span.setHTML(html);
    }

    public static void navigateToPlace(PlaceManager placeManager, String nameToken)
    {
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        Long containerId = ClientUtils.getContainerIdFromUrl(placeManager);
        String containerType = ClientUtils.getContainerTypeFromUrl(placeManager);
        String type = ClientUtils.getTypeFromUrl(placeManager);

        Map<String, String> with = new HashMap<>();
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
        if (place != null)
        {
            nameToken = place;
            if (place.equals(NameTokens.LIST_ITEMS) || place.equals(NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER))
            {
                if (containerId != null)
                {
                    with.put(ObidosConstants.CONTAINER_ID, containerId.toString());
                    if (containerType != null && containerType.equals(ObidosConstants.SHARED))
                    {
                        with.put(ObidosConstants.CONTAINER_TYPE, ObidosConstants.SHARED);
                        nameToken = NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER;
                    }
                }
            } else if (place.equals(NameTokens.LIST_ALL_MY_ITEMS))
            {
                if (type != null && type.equals(ObidosConstants.SHARED_WITH_OTHERS))
                {
                    with.put(ObidosConstants.TYPE, type);
                }
            }
            String action = ClientUtils.getActionFromUrl(placeManager);
            if (action != null)
            {
                with.put(ObidosConstants.ACTION, action);
            }
        } else
        {
            if (nameToken != null)
            {
                ClientUtils.showPage(placeManager, nameToken);
            }
        }
        if (type != null)
        {
            with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);
    }

    // must have place in URL
    public static void navigateToPlace(PlaceManager placeManager)
    {
        ClientUtils.navigateToPlace(placeManager, null);

    }

    public static void updatePanelHeader(final PlaceManager placeManager, ObidosPanelHeader panelHeading, String place)
    {
        ObidosMessages lang = ObidosMessages.LANG;

        String share = ClientUtils.getShareFromUrl(placeManager);
        String shareType = ClientUtils.getShareTypeFromUrl(placeManager);
        if (share != null && shareType != null)
        {
            if (ObidosConstants.NOTE.equals(share))
            {
                if (ObidosConstants.SHARE_WITH_USERS.equals(shareType))
                {
                    panelHeading.setHeadingText(lang.shareNoteWithUsers());
                }
                if (ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
                {
                    panelHeading.setHeadingText(lang.shareNoteWithGroups());
                }
            }
            if (ObidosConstants.ITEM.equals(share))
            {
                if (ObidosConstants.SHARE_WITH_USERS.equals(shareType))
                {
                    panelHeading.setHeadingText(lang.shareItemWithUsers());
                }
                if (ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
                {
                    panelHeading.setHeadingText(lang.shareItemWithGroups());
                }
            }
            if (ObidosConstants.CONTAINER.equals(share))
            {
                if (ObidosConstants.SHARE_WITH_USERS.equals(shareType))
                {
                    panelHeading.setHeadingText(lang.shareContainerWithUsers());
                }
                if (ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
                {
                    panelHeading.setHeadingText(lang.shareContainerWithGroups());
                }
            }
        }
        String action = ClientUtils.getActionFromUrl(placeManager);
        if (ObidosConstants.EDIT.equals(action))
        {
            if (NameTokens.LIST_NOTES.equals(place))
            {
                panelHeading.setHeadingText(lang.editNote());
            } else
            {
                panelHeading.setHeadingText(lang.editItem());
            }
        }
    }

    public static void changeItemPageTitles(PlaceManager placeManager, Button button, ObidosPanelHeader panelHeading)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        String action = ClientUtils.getActionFromUrl(placeManager);
        String type = ClientUtils.getTypeFromUrl(placeManager);
        String itemType = ClientUtils.getItemTypeFromUrl(placeManager);

        if (place != null)
        {
            // button.setText(lang.listNotes());
            if (place.equals(NameTokens.LIST_CONTAINERS))
            {
                // button.setText(lang.listContainers());
            } else if (place.equals(NameTokens.LIST_ALL_MY_ITEMS))
            {
                // button.setText(lang.listItems());
            } else if (place.equals(NameTokens.LIST_ITEMS))
            {
                // button.setText(lang.listItems());
            } else if (place.equals(NameTokens.LIST_NOTES))
            {
                // button.setText(lang.listNotes());
                if (type != null && type.equals(ObidosConstants.SHARED_WITH_OTHERS))
                {
                    // button.setText(lang.listNotes());
                }
                if (action != null && action.equals(ObidosConstants.SHARED_WITH_OTHERS))
                {
                    // button.setText(lang.listNotes());
                }
                if (action != null && action.equals(ObidosConstants.EDIT))
                {
                    panelHeading.setHeadingText(lang.editNote());
                }
            } else if (place.equals(NameTokens.LIST_ITEMS_SHARED_WITH_ME))
            {
                // button.setText(lang.listItems());
            } else if (place.contentEquals(NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER))
            {
                // button.setText(lang.listItems());
            } else if (place.equals(NameTokens.NOTIFICATION_MESSAGE))
            {
                // button.setText(lang.notifications());
            } else if (place.equals(NameTokens.LIST_NOTES_SHARED_WITH_ME))
            {
                // button.setText(lang.listNotes());
            }
        }
        Long ownerId = ClientUtils.getOwnerIdFromUrl(placeManager);
        if (ownerId != null)
        {
            if (place != null && place.equals(NameTokens.LIST_NOTES_SHARED_WITH_ME))
            {
                panelHeading.setHeadingText(lang.viewNoteSharedWithMe());
            } else
            {
                panelHeading.setHeadingText(lang.viewItemSharedWithMe());
            }
        }
        ClientUtils.updatePanelHeader(placeManager, panelHeading, place);

        if (ObidosConstants.NOTEBOOK.equals(itemType))
        {
            panelHeading.setHeadingText(lang.editNote());
        } else
        {
            panelHeading.setHeadingText(lang.editItem());
        }
    }

    public static void updatePanelHeading(final PlaceManager placeManager, Heading panelHeading, String place)
    {
        ObidosMessages lang = ObidosMessages.LANG;

        String share = ClientUtils.getShareFromUrl(placeManager);
        String shareType = ClientUtils.getShareTypeFromUrl(placeManager);
        if (share != null && shareType != null)
        {
            if (ObidosConstants.NOTE.equals(share))
            {
                if (ObidosConstants.SHARE_WITH_USERS.equals(shareType))
                {
                    panelHeading.setText(lang.shareNoteWithUsers());
                }
                if (ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
                {
                    panelHeading.setText(lang.shareNoteWithGroups());
                }
            }
            if (ObidosConstants.ITEM.equals(share))
            {
                if (ObidosConstants.SHARE_WITH_USERS.equals(shareType))
                {
                    panelHeading.setText(lang.shareItemWithUsers());
                }
                if (ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
                {
                    panelHeading.setText(lang.shareItemWithGroups());
                }
            }
            if (ObidosConstants.CONTAINER.equals(share))
            {
                if (ObidosConstants.SHARE_WITH_USERS.equals(shareType))
                {
                    panelHeading.setText(lang.shareContainerWithUsers());
                }
                if (ObidosConstants.SHARE_WITH_GROUPS.equals(shareType))
                {
                    panelHeading.setText(lang.shareContainerWithGroups());
                }
            }
        }
        String action = ClientUtils.getActionFromUrl(placeManager);
        if (ObidosConstants.EDIT.equals(action))
        {
            if (NameTokens.LIST_NOTES.equals(place))
            {
                panelHeading.setText(lang.editNote());
            } else
            {
                panelHeading.setText(lang.editItem());
            }
        }
    }

    public static void changeItemPageTitles(PlaceManager placeManager, Button button, Heading panelHeading)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        String action = ClientUtils.getActionFromUrl(placeManager);
        String type = ClientUtils.getTypeFromUrl(placeManager);
        String itemType = ClientUtils.getItemTypeFromUrl(placeManager);

        if (place != null)
        {
            // button.setText(lang.listNotes());
            if (place.equals(NameTokens.LIST_CONTAINERS))
            {
                // button.setText(lang.listContainers());
            } else if (place.equals(NameTokens.LIST_ALL_MY_ITEMS))
            {
                // button.setText(lang.listItems());
            } else if (place.equals(NameTokens.LIST_ITEMS))
            {
                // button.setText(lang.listItems());
            } else if (place.equals(NameTokens.LIST_NOTES))
            {
                // button.setText(lang.listNotes());
                if (type != null && type.equals(ObidosConstants.SHARED_WITH_OTHERS))
                {
                    // button.setText(lang.listNotes());
                }
                if (action != null && action.equals(ObidosConstants.SHARED_WITH_OTHERS))
                {
                    // button.setText(lang.listNotes());
                }
                if (action != null && action.equals(ObidosConstants.EDIT))
                {
                    panelHeading.setText(lang.editNote());
                }
            } else if (place.equals(NameTokens.LIST_ITEMS_SHARED_WITH_ME))
            {
                // button.setText(lang.listItems());
            } else if (place.contentEquals(NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER))
            {
                // button.setText(lang.listItems());
            } else if (place.equals(NameTokens.NOTIFICATION_MESSAGE))
            {
                // button.setText(lang.notifications());
            } else if (place.equals(NameTokens.LIST_NOTES_SHARED_WITH_ME))
            {
                // button.setText(lang.listNotes());
            }
        }
        Long ownerId = ClientUtils.getOwnerIdFromUrl(placeManager);
        if (ownerId != null)
        {
            if (place != null && place.equals(NameTokens.LIST_NOTES_SHARED_WITH_ME))
            {
                panelHeading.setText(lang.viewNoteSharedWithMe());
            } else
            {
                panelHeading.setText(lang.viewItemSharedWithMe());
            }
        }
        ClientUtils.updatePanelHeading(placeManager, panelHeading, place);

        if (ObidosConstants.NOTEBOOK.equals(itemType))
        {
            panelHeading.setText(lang.editNote());
        } else
        {
            panelHeading.setText(lang.editItem());
        }
    }

    public static void addPlace(PlaceManager placeManager, Map<String, String> with)
    {
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (place != null)
        {
            with.put(ObidosConstants.PLACE, place);
        }
        if (type != null)
        {
            with.put(ObidosConstants.TYPE, type);
        }
    }

    public static void addPlace(PlaceManager placeManager, Map<String, String> with, String nameToken)
    {
        with.put(ObidosConstants.PLACE, nameToken);
    }

    public static void addParamToMap(PlaceManager placeManager, Map<String, String> with, String paramKey)
    {
        try
        {
            String paramValue = ClientUtils.getParameterFromUrl(placeManager, paramKey);
            with.put(paramKey, paramValue);
        } catch (ParamNotFoundException e)
        {
        }
    }

    public static void addType(PlaceManager placeManager, Map<String, String> with)
    {
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.TYPE);
    }

    public static void addShare(PlaceManager placeManager, Map<String, String> with)
    {
        ClientUtils.addParamToMap(placeManager, with, ObidosConstants.SHARE);
    }

    // It is critical that keys are same as defined in the UserSettingsView.xml
    // file
    // of the application will crash
    public static ObidosMap<String, Integer> defaultPageAfterLoginMap()
    {
        ObidosMessages lang = ObidosMessages.LANG;
        ObidosMap<String, Integer> map = new ObidosMap<>();
        map.put(lang.listMyContainers(), UserDTO.DEFAULT_PAGE_LIST_CONTAINERS);
        map.put(lang.listContainersSharedWithMe(), UserDTO.DEFAULT_PAGE_LIST_CONTAINERS_SHARED_WITH_ME);
        map.put(lang.listMyItems(), UserDTO.DEFAULT_PAGE_LIST_MY_ITEMS);
        map.put(lang.listItemsSharedWithMe(), UserDTO.DEFAULT_PAGE_LIST_ITEMS_SHARED_WITH_ME);
        map.put(lang.listMyNotes(), UserDTO.DEFAULT_PAGE_LIST_MY_NOTES);
        map.put(lang.listNotesSharedWithMe(), UserDTO.DEFAULT_PAGE_LIST_NOTES_SHARED_WITH_ME);

        return map;
    }

    public static String nameTokenForDefaultPageAfterLogin(Integer pageNumber)
    {
        if (pageNumber == null)
        {
            pageNumber = UserDTO.DEFAULT_PAGE_LIST_CONTAINERS;
        }
        switch (pageNumber)
        {
        case UserDTO.DEFAULT_PAGE_LIST_CONTAINERS:
        {
            return NameTokens.LIST_CONTAINERS;
        }

        case UserDTO.DEFAULT_PAGE_LIST_CONTAINERS_SHARED_WITH_ME:
        {
            return NameTokens.LIST_CONTAINERS_SHARED_WITH_ME;
        }

        case UserDTO.DEFAULT_PAGE_LIST_ITEMS_SHARED_WITH_ME:
        {
            return NameTokens.LIST_ITEMS_SHARED_WITH_ME;
        }

        case UserDTO.DEFAULT_PAGE_LIST_MY_ITEMS:
        {
            return NameTokens.LIST_ALL_MY_ITEMS;
        }

        case UserDTO.DEFAULT_PAGE_LIST_MY_NOTES:
        {
            return NameTokens.LIST_NOTES;
        }

        case UserDTO.DEFAULT_PAGE_LIST_NOTES_SHARED_WITH_ME:
        {
            return NameTokens.LIST_NOTES_SHARED_WITH_ME;
        }

        default:
        {
            return NameTokens.LIST_CONTAINERS;
        }
        }
    }

    public static void showDefaultPage(final UserDTO dto, final PlaceManager placeManager)
    {
        if (dto != null & dto.getDefaultPage() != null)
        {
            myLog("Show default page");
            String nameToken = ClientUtils.nameTokenForDefaultPageAfterLogin(dto.getDefaultPage());
            Map<String, String> with = new HashMap<>();
            with.put(ObidosConstants.USER_ID, dto.getId().toString());
            ClientUtils.showPage(placeManager, nameToken);
        } else
        {
            myLog("Showing login page");
            ClientUtils.showPage(placeManager, NameTokens.LOGIN);
        }
    }

    public static boolean passphraseResetRequest(TokenFormatter tokenFormatter, PlaceManager placeManager)
    {
        if (placeManager == null)
        {
            return false;
        }

        String nameToken = ClientUtils.getNameToken(tokenFormatter);
        myLog("name token: " + nameToken);
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        myLog("Place: " + place);
        String token = ClientUtils.getTokenFromUrl(placeManager);
        myLog("Token: " + token);
        if ((place != null && place.equals(NameTokens.RESET_PASSPHRASE))
                || (nameToken != null && nameToken.equals(NameTokens.RESET_PASSPHRASE)))
        {
            return true;
        }
        if ((place != null && place.equals(NameTokens.FORGOT_PASSPHRASE))
                || (nameToken != null && nameToken.equals(NameTokens.FORGOT_PASSPHRASE)))
        {
            return true;
        }

        /*
         * if ((place != null && place.equals(NameTokens.RESET_PASSPHRASE)) ||
         * (nameToken != null && nameToken.equals(NameTokens.RESET_PASSPHRASE)))
         * { myLog("CHeck if token is in the URL"); if
         * (ClientUtils.getTokenFromUrl(placeManager) != null) { return true; }
         * }
         */
        return false;
    }

    // https://stackoverflow.com/questions/24950435/utility-method-to-convert-boolean-into-boolean-and-handle-null-in-java
    public static boolean fromBoolean(Boolean b)
    {
        boolean x = Boolean.TRUE.equals(b);
        return x;
    }

    public static Integer fromInteger(Integer i)
    {
        if (i == null)
        {
            return 0;
        }
        return i;
    }

    public static Long fromLong(Long i)
    {
        if (i == null)
        {
            return 0L;
        }
        return i;
    }

    public static boolean isItemSharedWithOthers(ItemDTO dto)
    {
        if (dto == null)
        {
            return false;
        }
        if (ClientUtils.fromBoolean(dto.getShared()))
        {
            return true;
        }
        return false;
    }

    // Bug #822
    public static void showLicenseEnforcementDialog(final ToggleSwitch addAttachmetnSwitch,
            final ObidosRowBottom2px uploadLoRow)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String title = lang.notSupportedByLicense();
        String message = lang.notSupportedByLicenseMessage();
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);

        // no need to do anything about Escape, it's not on by default

        // close the form
        options.addButton(lang.okButtonTitle(), ButtonType.PRIMARY.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
                boolean f = false;
                addAttachmetnSwitch.setValue(f);
                uploadLoRow.setVisible(f);
                Bootbox.hideAll();
            }
        });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    // Bug #822
    public static void showLicenseEnforcementDialogQRCode(final ToggleSwitch add2FASwitch, final Row qrCodeInfoRow)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String title = lang.notSupportedByLicense();
        String message = lang.notSupportedByLicenseMessage();
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);

        // no need to do anything about Escape, it's not on by default

        // close the form
        options.addButton(lang.okButtonTitle(), ButtonType.PRIMARY.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
                boolean f = false;
                add2FASwitch.setValue(f);
                qrCodeInfoRow.setVisible(f);
                Bootbox.hideAll();
            }
        });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    /**
     * One prompt method for everything. If different object is passed, it must
     * be type casted
     * 
     * @param t
     * @param title
     *            Title of the dialog
     * @param message
     *            Message
     * @param dtoObject
     *            Object to preform action on
     *            <p>
     * @author spgdev@spenego.com - Mar 23, 2019
     */
    public static void promptForAction(PromptForAction t, String title, String message)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        options.setOnEscape(new SimpleCallback()
        {
            @Override
            public void callback()
            {
                options.setOnEscape(new SimpleCallback()
                {
                    @Override
                    public void callback()
                    {
                    }
                });

            }
        });

        // No
        options.addButton(lang.no(), ButtonType.DEFAULT.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
            }
        });

        // Yes
        options.addButton(lang.yes(), ButtonType.DANGER.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
                t.action();
            }
        });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    public static void promptToSaveChange(PromptForAction t, final String title, final String message,
            final PlaceManager placeManager)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        options.setOnEscape(new SimpleCallback()
        {
            @Override
            public void callback()
            {
            }
        });

        String label = "&nbsp;&nbsp;&nbsp;&nbsp;" + lang.yes() + "&nbsp;&nbsp;&nbsp;&nbsp;";

        // Save and leave
        options.addButton(label, ButtonType.SUCCESS.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
                t.action();
            }
        });

        label = "&nbsp;&nbsp;&nbsp;&nbsp;" + lang.no() + "&nbsp;&nbsp;&nbsp;&nbsp;";
        // leave without saving
        options.addButton(label, ButtonType.DANGER.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
                ClientUtils.back(placeManager);
            }
        });

        // close the form
        options.addButton(lang.cancel(), ButtonType.PRIMARY.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
                Bootbox.hideAll();
            }
        });

        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    public static int getHideViewItemSeconds(CurrentUser currentUser)
    {
        if (currentUser == null)
        {
            return 0;
        }
        Integer delay = currentUser.getUserDTO().getHideItemDelay();
        int seconds = delay;
        if (delay == null)
        {
            seconds = 0;
        }
        return seconds;
    }

    public static String getSelectedTypeString(ArrayList<Long> ids, int idType)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (ids == null)
        {
            return lang.na();
        }
        int sz = ids.size();

        switch (idType)
        {
        case ObidosConstants.NOTE_ID_N:
        {
            if (sz == 1)
            {
                return lang.note();
            }
            return lang.notes();
        }

        case ObidosConstants.ITEM_ID_N:
        {
            if (sz == 1)
            {
                return lang.item();
            }

            return lang.items();
        }

        case ObidosConstants.CONTAINER_ID_N:
        {
            if (sz == 1)
            {
                return lang.container();
            }

            return lang.containers();
        }

        case ObidosConstants.GROUP_ID_N:
        {
            if (sz == 1)
            {
                return lang.group();
            }

            return lang.groups();
        }

        case ObidosConstants.TEMPLATE_ID_N:
        {
            if (sz == 1)
            {
                return lang.template();
            }

            return lang.templates();
        }
        case ObidosConstants.USER_ID_N:
        {
            if (sz == 1)
            {
                return lang.user();
            }

            return lang.users();
        }

        case ObidosConstants.ADMIN_ID_N:
        {
            if (sz == 1)
            {
                return lang.admin();
            }

            return lang.admins();
        }

        case ObidosConstants.NOTIFICATION_MESSAGE_ID_N:
        {
            if (sz == 1)
            {
                return lang.notificationMessage();
            }
            return lang.notificationMessages();
        }
        }
        return lang.na();
    }

    public static boolean loggedInUserIsTheOwner(CurrentUser currentUser, SharedItemDTO dto)
    {
        if (currentUser == null)
        {
            return false;
        }
        if (dto == null)
        {
            return true;
        }
        return dto.ownerId(currentUser.getUserDTO().getId());
    }

    // can be shared item or shared note
    public static boolean itIsASharedItem(final PlaceManager placeManager)
    {
        String place = ClientUtils.getPlaceFromUrl(placeManager);
        if (NameTokens.LIST_ITEMS_SHARED_WITH_ME.equals(place)
                || NameTokens.LIST_ITEMS_IN_SHARED_CONTAINER.equals(place)
                || NameTokens.NOTIFICATION_MESSAGE.equals(place))
        {
            return true;
        }
        return false;
    }

    public static void showListOfUsersContainerIsSharedWith(final PlaceManager placeManager, final ContainerDTO dto)
    {
        String nameToken = NameTokens.LIST_USERS_CONTAINER_IS_SHARED_WITH;
        Map<String, String> with = new HashMap<>();
        with.put(ObidosConstants.CONTAINER_ID, dto.getId().toString());
        ClientUtils.addType(placeManager, with);
        ClientUtils.addPlace(placeManager, with, NameTokens.LIST_CONTAINERS);
        ClientUtils.showPage(placeManager, nameToken, with);

    }

    public static void enableToggleSwitch(ToggleSwitch ts, boolean enable)
    {
        if (enable)
        {
            // must enable first
            ts.setReadOnly(false);
            ts.setValue(true);
        } else
        {
            // must enable to set the value
            ts.setReadOnly(false);
            ts.setValue(false);
            // turn disable it
            ts.setReadOnly(true);
        }
    }

    public static void setToggleSwitchValue(ToggleSwitch ts, Boolean value)
    {
        boolean b = fromBoolean(value);

        boolean readOnly = ts.isReadOnly();
        // enable in order to set value
        if (readOnly)
        {
            ts.setReadOnly(false);
        }
        ts.setValue(b);
        ts.setReadOnly(readOnly);
    }

    public static void setToggleSwitchValue(ToggleSwitch ts, boolean value, boolean enabled)
    {
        ts.setReadOnly(false); // must enable to set value
        ts.setValue(value);

        if (enabled)
        {
            ts.setReadOnly(false);
        } else
        {
            ts.setReadOnly(true);
        }
    }

    /**
     * method is used by NewAdminPresenter and EditAdminPresenter
     * 
     * @param placeManager
     * @param currentUser
     * @param tsMap
     *            - returns
     * @param cdtoMap
     *            - returns
     * @param errorFormLabel
     * @param getCreateUserSwitch
     * @param getCreateAdminSwitch
     * @param getDeleteUserSwitch
     * @param getDeleteAdminSwitch
     * @param getLockUserSwitch
     * @param getLockAdminSwitch
     * @param getChangeUserCredentialsSwitch
     * @param getChangeAdminCredentialsSwitch
     * @param getModifyEmailTemplatesSwitch
     *            <p>
     * @author spgdev@spenego.com - Apr 28, 2019
     */
    public static void updateAdminCapabilitesToggleSwitches(PlaceManager placeManager, CurrentUser currentUser,
            Map<String, ToggleSwitch> tsMap, Map<String, Boolean> cdtoMap, FormLabel errorFormLabel,
            ToggleSwitch getCreateUserSwitch, ToggleSwitch getCreateAdminSwitch, ToggleSwitch getDeleteUserSwitch,
            ToggleSwitch getDeleteAdminSwitch, ToggleSwitch getLockUserSwitch, ToggleSwitch getLockAdminSwitch,
            ToggleSwitch getChangeUserCredentialsSwitch, ToggleSwitch getChangeAdminCredentialsSwitch,
            ToggleSwitch getModifyEmailTemplatesSwitch)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (currentUser == null) // all bets are off
        {
            ClientUtils.showErrorMessage(lang.couldNotDetermineAdminsCapabilites(), errorFormLabel);
            return;
        }
        CapabilityDTO cdto = currentUser.getUserDTO().getCapabilities();
        ToggleSwitch s = getCreateUserSwitch;
        // user
        String key = lang.canCreateUser();
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getCreateUser());

        key = lang.canDeleteUser();
        s = getDeleteUserSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getDeleteUser());

        key = lang.canLockUser();
        s = getLockUserSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getLockUser());

        key = lang.canChangeUserCredentials();
        s = getChangeUserCredentialsSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getChangeUserCredentials());

        // admin
        key = lang.canCreateAdmin();
        s = getCreateAdminSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getCreateAdmin());

        key = lang.canDeleteAdmin();
        s = getDeleteAdminSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getDeleteAdmin());

        key = lang.canLockAdmin();
        s = getLockAdminSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getLockAdmin());

        key = lang.canChangeAdminCredentials();
        s = getChangeAdminCredentialsSwitch;
        tsMap.put(key, s);
        cdtoMap.put(key, cdto.getChangeAdminCredentials());

        key = lang.canModifyEmailTemplates();
        s = getModifyEmailTemplatesSwitch;
        tsMap.put(lang.canModifyEmailTemplates(), s);
        cdtoMap.put(key, cdto.getModifyEmailTemplates());

        // enable/disable switches
        for (Map.Entry<String, ToggleSwitch> entry : tsMap.entrySet())
        {
            // enable all first, otherwise we might have some stale ones from
            // last visit
            ClientUtils.enableToggleSwitch(entry.getValue(), true);
            // now enable or disable based on as capabilities set
            ClientUtils.enableToggleSwitch(entry.getValue(), cdtoMap.get(entry.getKey()));
        }
    }

    public static void updateAdminCapabilitesToggleSwitchesNew(PlaceManager placeManager, CurrentUser currentUser,
            ObidosMessageRow messageRow, ToggleSwitch getCreateUserSwitch, ToggleSwitch getCreateAdminSwitch,
            ToggleSwitch getDeleteUserSwitch, ToggleSwitch getDeleteAdminSwitch, ToggleSwitch getLockUserSwitch,
            ToggleSwitch getLockAdminSwitch, ToggleSwitch getChangeUserCredentialsSwitch,
            ToggleSwitch getChangeAdminCredentialsSwitch, ToggleSwitch getModifyEmailTemplatesSwitch,
            ToggleSwitch getChangeSystemSettingSwitch)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (currentUser == null) // all bets are off
        {
            messageRow.showErrorMessage(lang.couldNotDetermineAdminsCapabilites());
            return;
        }

        CapabilityDTO cdto = currentUser.getUserDTO().getCapabilities();
        myLog("MMM modify switch in client utils:" + cdto.getModifySettings());

        // Create Users
        ClientUtils.setToggleSwitchValue(getCreateUserSwitch, cdto.getCreateUser(), cdto.getCreateUser());

        // Create Admins
        ClientUtils.setToggleSwitchValue(getCreateAdminSwitch, cdto.getCreateAdmin(), cdto.getCreateAdmin());
        myLog("Create admin: " + cdto.getCreateAdmin());

        // Delete Users
        ClientUtils.setToggleSwitchValue(getDeleteUserSwitch, cdto.getDeleteUser(), cdto.getDeleteUser());

        // Delete Admins
        ClientUtils.setToggleSwitchValue(getDeleteAdminSwitch, cdto.getDeleteAdmin(), cdto.getDeleteAdmin());

        // Lock Users
        ClientUtils.setToggleSwitchValue(getLockUserSwitch, cdto.getLockUser(), cdto.getLockUser());

        // Lock Admins
        ClientUtils.setToggleSwitchValue(getLockAdminSwitch, cdto.getLockAdmin(), cdto.getLockAdmin());

        // Change User Credentials
        ClientUtils.setToggleSwitchValue(getChangeUserCredentialsSwitch, cdto.getChangeUserCredentials(),
                cdto.getChangeUserCredentials());

        // Change Admin Credentials
        ClientUtils.setToggleSwitchValue(getChangeAdminCredentialsSwitch, cdto.getChangeAdminCredentials(),
                cdto.getChangeAdminCredentials());

        // Modify Email Templates
        ClientUtils.setToggleSwitchValue(getModifyEmailTemplatesSwitch, cdto.getModifyEmailTemplates(),
                cdto.getModifyEmailTemplates());

        // Modify system setting
        ClientUtils.setToggleSwitchValue(getChangeSystemSettingSwitch, cdto.getModifySettings(),
                cdto.getModifySettings());
    }

    public static void updateAdminCapabilitesToggleSwitchesEdit(PlaceManager placeManager, CurrentUser currentUser,
            FormLabel errorFormLabel, ToggleSwitch getCreateUserSwitch, ToggleSwitch getCreateAdminSwitch,
            ToggleSwitch getDeleteUserSwitch, ToggleSwitch getDeleteAdminSwitch, ToggleSwitch getLockUserSwitch,
            ToggleSwitch getLockAdminSwitch, ToggleSwitch getChangeUserCredentialsSwitch,
            ToggleSwitch getChangeAdminCredentialsSwitch, ToggleSwitch getModifyEmailTemplatesSwitch,
            ToggleSwitch getChangeSystemSettingSwitch)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (currentUser == null) // all bets are off
        {
            ClientUtils.showErrorMessage(lang.couldNotDetermineAdminsCapabilites(), errorFormLabel);
            return;
        }

        CapabilityDTO cdto = currentUser.getUserDTO().getCapabilities();

        // Create Users
        ClientUtils.setToggleSwitchValue(getCreateUserSwitch, cdto.getCreateUser(), cdto.getCreateUser());

        // Create Admins
        ClientUtils.setToggleSwitchValue(getCreateAdminSwitch, cdto.getCreateAdmin(), cdto.getCreateAdmin());

        // Delete Users
        ClientUtils.setToggleSwitchValue(getDeleteUserSwitch, cdto.getDeleteUser(), cdto.getDeleteUser());

        // Delete Admins
        ClientUtils.setToggleSwitchValue(getDeleteAdminSwitch, cdto.getDeleteAdmin(), cdto.getDeleteAdmin());

        // Lock Users
        ClientUtils.setToggleSwitchValue(getLockUserSwitch, cdto.getLockUser(), cdto.getLockUser());

        // Lock Admins
        ClientUtils.setToggleSwitchValue(getLockAdminSwitch, cdto.getLockAdmin(), cdto.getLockAdmin());

        // Change User Credentials
        ClientUtils.setToggleSwitchValue(getChangeUserCredentialsSwitch, cdto.getChangeUserCredentials(),
                cdto.getChangeUserCredentials());

        // Change Admin Credentials
        ClientUtils.setToggleSwitchValue(getChangeAdminCredentialsSwitch, cdto.getChangeAdminCredentials(),
                cdto.getChangeAdminCredentials());

        // Modify Email Templates
        ClientUtils.setToggleSwitchValue(getModifyEmailTemplatesSwitch, cdto.getModifyEmailTemplates(),
                cdto.getModifyEmailTemplates());

        // Change system setting
        ClientUtils.setToggleSwitchValue(getChangeSystemSettingSwitch, cdto.getModifySettings(),
                cdto.getModifySettings());
    }

    // Use bootstrap table
    // adapted from: https://codepen.io/SitePoint/pen/raXdwZ
    // I am not using responsive part of it as my table is placed in a
    // bootbox dialog
    /**
     * 
     * 
     * @param title
     * @param dto
     *            <p>
     * @author spgdev@spenego.com - May 1, 2019
     */
    public static void showCapabilities(String title, UserDTO userDTO)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        CapabilityDTO cdto = userDTO.getCapabilities();
        if (cdto.getRootAdmin())
        {
            // HACK: set system settings to true
            // cdto.setModifySettings(true);
        }

        StringBuilder sb = new StringBuilder(256);

        /*
         * sb.append("<div class=\"container\">");
         * sb.append("  <div class=\"row\">");
         * sb.append("    <div class=\"col-xs-12\">");
         * sb.append("      <div class=\"table-responsive\">");
         */

        String red = "<span style=\"color:red;font-weight:bold\">";
        String green = "<span style=\"color:green;font-weight:bold\">";
        String enabled = "&nbsp;<b>Enabled: " + green + "✓</span>";
        String disabled = "<b>Disabled: " + red + "✗</span>";
        sb.append("        <table class=\"table table-bordered table-hover\">");
        String msg = enabled + "&nbsp;&nbsp;&nbsp;" + disabled;
        sb.append("			 <caption class=\"text-center\">" + msg + "</caption>");
        sb.append("          <thead>");
        sb.append("            <tr>");
        sb.append("              <th>" + lang.capability() + "</th>");
        sb.append("              <th>" + lang.status() + "</th>");
        sb.append("            </tr>");
        sb.append("          </thead>");
        sb.append("          <tbody>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canCreateUser() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getCreateUser()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canCreateAdmin() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getCreateAdmin()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canDeleteUser() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getDeleteUser()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canDeleteAdmin() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getDeleteAdmin()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canLockUser() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getLockUser()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canLockAdmin() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getLockAdmin()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canChangeUserCredentials() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getChangeUserCredentials()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canChangeAdminCredentials() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getChangeAdminCredentials()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canModifyEmailTemplates() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getModifyEmailTemplates()) + "</td>");
        sb.append("            </tr>");

        sb.append("            <tr>");
        sb.append("              <td>" + lang.canChangeSystemSettings() + "</td>");
        sb.append("              <td>" + getCdtoValue(cdto.getModifySettings()) + "</td>");
        sb.append("            </tr>");

        sb.append("          </tbody>");
        String footerMsg = "";
        if (cdto.getRootAdmin())
        {
            footerMsg = lang.youAreARootAdmin();
        } else
        {
            footerMsg = lang.youAreNotARootAdmin();
        }
        sb.append("          <tfoot>");
        sb.append("            <tr>");
        sb.append("              <td colspan=\"2\" class=\"text-center\">" + footerMsg + "</td>");
        sb.append("            </tr>");
        sb.append("          </tfoot>");

        sb.append("        </table>");
        /*
         * sb.append("      </div><!--end of .table-responsive-->");
         * sb.append("    </div>"); sb.append("  </div>"); sb.append("</div>");
         */

        ClientUtils.showBootboxDialog(title, sb.toString());
    }

    public static int round(float n)
    {
        return (int) Math.round(n);
    }

    public static void showPasswordAnalysisResult(final String pass, final PasswordAnalysisResults r)
    {
        String title = "Password Strength Analysis Result";
        StringBuilder sb = new StringBuilder(256);
        sb.append(
                "Entropy in password security measures the unpredictability and strength of a password. There are two main approaches to calculating password entropy:\n"
                        + "<ol>"
                        + "<li><b>RawEntropy:</b> This method calculates entropy based solely on the password’s length and character set. It often results in higher entropy values, which some password strength tools use.\n"
                        + "<li><b>Adjusted Entropy:</b> Our tool uses more nuanced approach. We start with the raw entropy, then apply various password cracking reduction techniques including <a href=\"https://pages.nist.gov/800-63-3/sp800-63b.html\" target=\"_blank\" \">NIST publication 800-63B</a> to account for real-world password vulnerabilities.\n"
                        + "</ol>" + "<b>Entropies of the generated password:</b> <span style=\"color:#ccc\">" + pass
                        + "</span>" + "<pre>" + "              Raw Entropy: " + round(r.getRawEntropy()) + "\n"
                        + "After repetition Adjusted: " + round(r.getEntropyAfterRepeatsWeakened()) + "\n"
                        + "        After lower cased: " + round(r.getEntropyAfterLowerCased()) + "\n"
                        + "    After Qwerty Adjusted: " + round(r.getEntropyAfterQwertyAdjusted()) + "\n"
                        + "After Dictionary Adjusted: " + round(r.getEntropyAfterDictionaryAdjusted()) + "\n"
                        + "         Adjusted Entropy: " + round(r.getEntropy()) + "\n" + "</pre>"
                        + "Please look at <b>Password Strength</b> section in Obidos User Guide for details on mathematics behind entropy calculation. We also use <a href=\"https://en.wikipedia.org/wiki/Argon2\" target=\"_blank\">Argon2id</a> hashing algorithm which is resistant against large scale GPU based cracking attemtps e.g. requires more RAM, requires more computation time to slow down cracking attempts, does not give a large advantage to parallel cracking attempts and reistant against timing attacks and other side channel vulnerabilities. We consider adjusted entropy:</br/><b>0-19 is Weak</b>, <b>20-24 is Moderate</b>, <b>25-29 Strong</b> and <b>30+ is Very Strong</b>");
        showBootboxDialog(title, sb.toString());
    }

    public static void showFileUploadInProgressDialog()
    {
        ObidosMessages lang = ObidosMessages.LANG;
        DialogOptions options = DialogOptions.newOptions(lang.uploadProgressMessage());
        options.setTitle(lang.uploadProgressTitle());
        options.setOnEscape(new SimpleCallback()
        {
            @Override
            public void callback()
            {
                myLog("ESCAPE........");
                return;
            }
        });
        String t = "<i class=\"fa fa-spinner fa-spin\"></i> Uploading ...";
        options.addButton(t, ButtonType.SUCCESS.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
            }
        });

        options.addButton(glang.cancelUpload(), ButtonType.DANGER.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
            }
        });
        Bootbox.hideAll();
        Bootbox.dialog(options);
    }

    public static CapabilityDTO getCapabilitesDTO(final CurrentUser currentUser)
    {
        if (currentUser == null)
        {
            return null;
        }
        UserDTO udto = currentUser.getUserDTO();
        if (udto == null)
        {
            return null;
        }
        return udto.getCapabilities();
    }

    public static boolean hasDeleteCapability(final PlaceManager placeManager, final CurrentUser currentUser)
    {
        CapabilityDTO cdto = getCapabilitesDTO(currentUser);
        if (cdto == null)
        {
            return false;
        }
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (ObidosConstants.LIST_ADMINS.equals(type))
        {
            return cdto.getDeleteAdmin();
        }
        return cdto.getDeleteUser();
    }

    public static boolean hasLockCapability(final PlaceManager placeManager, final CurrentUser currentUser)
    {
        CapabilityDTO cdto = getCapabilitesDTO(currentUser);
        if (cdto == null)
        {
            return false;
        }
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (ObidosConstants.LIST_ADMINS.equals(type))
        {
            return cdto.getLockAdmin();
        }
        return cdto.getLockUser();
    }

    public static boolean listingAdmins(final PlaceManager placeManager)
    {
        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type == null)
        {
            return false;
        }
        if (ObidosConstants.TYPE_ADMINS.equals(type))
        {
            return true;
        }
        return false;
    }

    /*
     * public static boolean canEditAdmin(final CurrentUser currentUser) { if
     * (currentUser == null) { return false; } CapabilityDTO cdto =
     * currentUser.getUserDTO().getCapabilities(); }
     */

    private static String getCdtoValue(Boolean b)
    {
        // ObidosMessages lang = ObidosMessages.LANG;
        String red = "<span style=\"color:red;font-weight:bold\">";
        String green = "<span style=\"color:green;font-weight:bold\">";
        if (b == null)
        {
            // return red + lang.no() + "</span>";
            return red + "✗" + "</span>";
        }
        if (b)
        {
            // return green + lang.yes() + "</span>";
            return green + "✓ " + "</span>";
        }
        // return red + lang.no() + "</span>";
        return red + "✗" + "</span>";
    }

    public static int getButtonWidth(final Button b)
    {
        if (b == null)
        {
            return 0;
        }
        return b.getOffsetWidth();
    }

    public static int getMaxButtonWidth(Button... buttons)
    {
        if (buttons == null)
        {
            return 0;
        }
        int maxWidth = 0;
        for (Button b : buttons)
        {
            int w = ClientUtils.getButtonWidth(b);
            if (w > maxWidth)
            {
                maxWidth = w;
            }

        }
        return maxWidth;
    }

    public static void adjustButtonWidth(Button button, String width)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                if (width != null)
                {
                    button.setWidth(width);
                }
            }
        });
    }

    public static void increaseDivHeight(final Div w, int pixel)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                int height = w.getOffsetHeight();
                int clientHeight = Window.getClientHeight();
                int divHeight = height + pixel;
                if (divHeight > clientHeight)
                {
                    divHeight = divHeight - 100;
                }
                myLog("Height: " + height);
                String heightPx = Integer.toString(divHeight) + "px";
                w.setHeight(heightPx);
            }
        });

    }

    public static void focusToListBox(ListBox lbox)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                lbox.setFocus(true);
            }
        });
    }

    public static void focusToButton(Button button)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                button.setFocus(true);
            }
        });
    }

    public static void focusToWidegt(FocusWidget w)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                w.setFocus(true);
            }
        });
    }

    public static void adjustButtonsWidth(ObidosButtonToolBar toolbar)
    {
        toolbar.adjustButtonsWidth();
    }

    // first adjust the buttons in the toolbar
    // and then adjust the passed buttons' width to the max with of the buttons
    // in the toolbar
    public static void adjustButtonsWidth(ObidosButtonToolBar toolbar, Button... buttons)
    {
        toolbar.adjustButtonsWidth();
        String maxWidth = toolbar.getMaxWidth();
        for (Button b : buttons)
        {
            adjustButtonWidth(b, maxWidth);
        }
    }

    public static void adjustButtonWidth(String width, Button... buttons)
    {
        for (Button b : buttons)
        {
            b.setWidth(width);
        }
    }

    public static void adjustButtonWidth(int width, Button... buttons)
    {
        if (width <= 0)
        {
            return;
        }
        String widthPx = String.valueOf(width + "px");
        adjustButtonWidth(widthPx, buttons);
    }

    public static void adjustButtonsWidth(Button... buttons)
    {

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                int maxWidth = ClientUtils.getMaxButtonWidth(buttons);
                if (maxWidth <= 0)
                {
                    return;
                }
                String width = String.valueOf(maxWidth + "px");
                for (Button b : buttons)
                {
                    b.setWidth(width);
                }
            }
        });
    }

    public static void setTextToInputGroup(ObidosInputGroup ig, String value)
    {
        ig.setText(value);
    }

    public static String getTextFromInputGroup(ObidosInputGroup ig)
    {
        return ig.getText();
    }

    public static void showEditNoteOrItemPage(final String itemId, final String ownerId, String place,
            final PlaceManager placeManager)
    {
        String itemType = ClientUtils.getItemTypeFromUrl(placeManager);

        String nameToken = NameTokens.EDIT_ITEM;
        Map<String, String> with = new HashMap<>();

        String key = ObidosConstants.ACTION;
        with.put(key, ObidosConstants.EDIT);

        key = ObidosConstants.ITEM_ID;
        with.put(key, itemId);
        key = ObidosConstants.OWNERID;
        with.put(key, ownerId);
        if (itemType == null)
        {
            with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.ITEM);
        } else
        {
            with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.NOTEBOOK);
        }
        if (place == null)
        {
            place = NameTokens.LIST_NOTES_SHARED_WITH_ME;
        }
        // Issue #700
        if (place.equals(NameTokens.LIST_NOTES_SHARED_WITH_ME))
        {
            with.put(ObidosConstants.ITEM_TYPE, ObidosConstants.NOTEBOOK);
        }

        // with.put(ObidosConstants.PLACE,
        // NameTokens.LIST_NOTES_SHARED_WITH_ME);
        with.put(ObidosConstants.PLACE, place);

        String type = ClientUtils.getTypeFromUrl(placeManager);
        if (type != null)
        {
            with.put(ObidosConstants.TYPE, type);
        }

        ClientUtils.showPage(placeManager, nameToken, with);

    }

    public static long getUnixEpochSecond()
    {
        Date date = new Date();
        long epoch = (long) (date.getTime() * .001);
        return epoch;
    }

    public static String calculateTime(long seconds)
    {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;
        String msg = days + "D " + hours + "H:" + minutes + "M:" + sec + "S";

        if (seconds > 86400)
        {
            return msg;
        } else
        {
            msg = hours + "H:" + minutes + "M:" + sec + "S";
        }
        return msg;
    }

    public static void setStartDateFromToday(final DatePicker datePicker, final int days)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                datePicker.setTitle(null);
                Date startDate = new Date();
                CalendarUtil.addDaysToDate(startDate, days);
                // as our format is yyyy-mm-dd, start date must be in the
                // same format. Note GWT uses MM for month not mm which is for
                // minute
                String dateString = DateTimeFormat.getFormat("yyyy-MM-dd").format(startDate);
                datePicker.setStartDate(dateString);
            }
        });
    }

    public static void setStartDateToTomorrow(final DatePicker datePicker)
    {
        setStartDateFromToday(datePicker, 1);
    }

    public static void setStartDateToToday(final DatePicker datePicker)
    {
        setStartDateFromToday(datePicker, 0);
    }

    public static int getShareExpirationDays(final DatePicker datePicker, final ObidosMessageRow messageRow,
            final PlaceManager placeManager)
    {
        String action = ClientUtils.getActionFromUrl(placeManager);
        Date expireDate = datePicker.getValue();
        myLog("Expiration date: " + expireDate);
        ObidosMessages lang = ObidosMessages.LANG;
        if (expireDate == null)
        {
            return 0;
        }
        Date today = new Date(); // now
        int days = CalendarUtil.getDaysBetween(today, expireDate);
        myLog("Days between today and exp date: " + days);
        if (days < 0)
        {
            messageRow.showErrorMessage(lang.expirationDateinPast());
            return -1;
        } else if (days == 0)
        {
            if (!ObidosConstants.EDIT.equals(action))
            {
                messageRow.showErrorMessage(lang.expirationDateMustBeTomorrow());
                return -1;
            }
        }
        return days;
    }

    public static int getShareExpirationDays(final DatePicker datePicker, final ObidosMessageRowWithStyle messageRow,
            final PlaceManager placeManager)
    {
        String action = ClientUtils.getActionFromUrl(placeManager);
        Date expireDate = datePicker.getValue();
        ObidosMessages lang = ObidosMessages.LANG;
        if (expireDate == null)
        {
            return 0;
        }
        Date today = new Date(); // now
        int days = CalendarUtil.getDaysBetween(today, expireDate);
        if (days < 0)
        {
            messageRow.showErrorMessage(lang.expirationDateinPast());
            return -1;
        } else if (days == 0)
        {
            if (!ObidosConstants.EDIT.equals(action))
            {
                messageRow.showErrorMessage(lang.expirationDateMustBeTomorrow());
                return -1;
            }
        }
        return days;
    }

    public static void setTypeAddon(final InputGroupAddon inputGroupAddon, final boolean shareable)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (shareable)
        {
            inputGroupAddon.setText(lang.shareable());
            inputGroupAddon.setIcon(IconType.SHARE_ALT);
        } else
        {
            inputGroupAddon.setText(lang.privateXWithInvisibleSapces());
            inputGroupAddon.setIcon(IconType.USER_SECRET);
        }
    }

    public static boolean isAddonShareable(final InputGroupAddon addon)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        String text = addon.getText();
        if (lang.shareable().equals(text))
        {
            return true;
        }
        return false;
    }

    public static String makeHTMLLink(final String proto, final String hrefVal, final String iconClass)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<i class=\"");
        sb.append(iconClass);
        sb.append("\">");
        sb.append("</i>");
        sb.append("&nbsp;");
        sb.append("<a href=\"");
        sb.append(proto);
        sb.append(":");
        sb.append(hrefVal);
        sb.append("\"");
        sb.append(" ");
        sb.append("target=\"_blank\"");
        sb.append(">");
        sb.append(hrefVal);
        sb.append("</a>");
        return sb.toString();
    }

    public static String makeEmailLink(final String email)
    {
        return ClientUtils.makeHTMLLink("mailto", email, "fa fa-envelope");
        /*
         * StringBuilder sb = new StringBuilder(256);
         * sb.append("<i class=\"fa fa-envelope\"></i>"); sb.append("&nbsp;");
         * sb.append("<a href=\"mailto:"); sb.append(email); sb.append("\"");
         * sb.append(" "); sb.append("target=\"_blank\""); sb.append(">");
         * sb.append(email); sb.append("</a>"); return sb.toString();
         */
    }

    public static String makePhoneLink(final String phone)
    {

        StringBuilder sb = new StringBuilder(256);
        sb.append("<i class=\"fa fa-phone\"></i>");
        sb.append("&nbsp;");
        sb.append("<a href=\"tel:");
        sb.append(phone);
        sb.append("\"");
        sb.append(" ");
        sb.append("target=\"_blank\"");
        sb.append(">");
        sb.append(phone);
        sb.append("</a>");
        return sb.toString();
    }

    private static FormLabel createFormLabel(final String html)
    {
        FormLabel label = new FormLabel();
        label.addStyleName("col-sm-3");
        label.setHTML(html);
        return label;
    }

    private static FlowPanel createFlowPanel()
    {
        FlowPanel fp = new FlowPanel();
        fp.addStyleName("col-sm-7");
        return fp;
    }

    // create readonly textbox
    private static TextBox createReadOnlyTextBox()
    {
        TextBox tbox = new TextBox();
        tbox.setReadOnly(true);
        tbox.getElement().getStyle().setProperty("color", "#000");
        tbox.getElement().getStyle().setProperty("fontWeight", "bold");
        return tbox;
    }

    private static TextBox createTextBox()
    {
        TextBox tbox = new TextBox();
        tbox.setReadOnly(false);
        tbox.getElement().getStyle().setProperty("color", "#000");
        tbox.getElement().getStyle().setProperty("fontWeight", "bold");
        return tbox;
    }

    public static SendNotificationTestEmailModalData createNotificationTestEmailModal()
    {
        ObidosMessages lang = ObidosMessages.LANG;
        /*
         * <Modal> <ModalBody> <Form> <Row> <HTMLPanel/> </Row> <Row>
         * <FormLabel/> <FlowPanel> <TextBox/> </FlowPanel> </Row> .... </Form>
         * 
         * </ModalBody> </Modal>
         * 
         */
        SendNotificationTestEmailModalData md = new SendNotificationTestEmailModalData();

        /* Modal */
        Modal modal = new Modal();
        /* ModalBody */
        ModalBody modalBody = new ModalBody();
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
        modal.setTitle(lang.sendTestEmailWithTemplate());
        modal.setClosable(true);
        modal.setFade(true);
        modal.setDataKeyboard(true);

        // HTMLPanelrow ============================
        ObidosRowBottom2px row = new ObidosRowBottom2px();

        BlockQuote bq = new BlockQuote();
        form.add(bq);
        Paragraph p = new Paragraph();
        p.getElement().getStyle().setProperty("fontSize", "13px");
        bq.add(p);
        String html = lang.notificationTestMailHelp();
        p.setHTML(html);

        // Subject row ============================
        row = new ObidosRowBottom2px();
        FormLabel label = createFormLabel(lang.subject());
        FlowPanel fp = createFlowPanel();
        TextBox tbox = createReadOnlyTextBox();
        tbox.setReadOnly(false);
        md.setSubjectTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // To row ============================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.to());
        fp = createFlowPanel();
        tbox = new TextBox();
        md.setToTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // From row ============================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.from());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();
        tbox.setReadOnly(false);
        md.setFromTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // URL row ============================
        row = new ObidosRowBottom2px();
        row.setVisible(false); // don't bother to show, we'll generate it
        label = createFormLabel(lang.testUrl());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();
        tbox.setReadOnly(false);
        md.setUrlTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // modal footer ------------------
        ModalFooter footer = new ModalFooter();

        // save button, we have to add and remove click handler by the
        // caller
        Button button = new Button(lang.sendEmail());
        button.setType(ButtonType.SUCCESS);
        footer.add(button);
        md.setSendMailButton(button);

        button = new Button(lang.close());
        button.setType(ButtonType.DANGER);
        button.setDataDismiss(ButtonDismiss.MODAL);
        footer.add(button);

        // add to modal body
        modal.add(modalBody);
        modal.add(footer);
        modalBody.add(form);

        // update fields
        md.setModal(modal);

        return md;
    }

    /*
     * private static void cancel2FARefreshTimer(TwoFAModalData md) { Timer
     * timer = md.getRefreshTimer(); if (timer != null) { timer.cancel(); } }
     * 
     * private static void start2FARefreshTimer(TwoFAModalData md) {
     * cancel2FARefreshTimer(md); int delay = 30; Timer timer = new Timer() {
     * int count = delay;
     * 
     * @Override public void run() { count--; if (count < 0) { count = 0; } } };
     * timer.scheduleRepeating(1000);
     * 
     * }
     */

    public static TwoFAModalData createTwoFAModal()
    {
        ObidosMessages lang = ObidosMessages.LANG;
        TwoFAModalData md = new TwoFAModalData();
        /*
         * <Modal> <ModayBody> <Form> <Row> <FormLabel/> <FlowPanel> <TextBox/>
         * </FlowPanel> </Row> .... </form> </ModalBody> </Moda>
         */
        Modal modal = new Modal();
        ModalBody modalBody = new ModalBody();
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
        modal.setTitle(glang.show2FACode());
        modal.setClosable(true);
        modal.setFade(true);
        modal.setDataKeyboard(true);

        ObidosRowBottom2px row = new ObidosRowBottom2px();
        FlowPanel fp = new FlowPanel();

        // Help =======================================
        BlockQuote bq = new BlockQuote();
        bq.setVisible(false);
        bq.getElement().getStyle().setProperty("backgroundColor", "#eee");
        bq.getElement().getStyle().setProperty("borderColor", "#2f79b9");
        Heading heading = new Heading(HeadingSize.H4);
        Paragraph para = new Paragraph();
        para.getElement().getStyle().setProperty("fontSize", "13px");
        para.setHTML(lang.twoFactorModalHelp());
        row.add(bq);
        bq.add(heading);
        heading.add(para);
        form.add(row);

        // QR Code Image ==============================
        row = new ObidosRowBottom2px();
        row.setVisible(false);

        md.setQrCodeImageRow(row);

        fp = new FlowPanel();
        fp.addStyleName("col-sm-offset-3 col-sm-7");
        Image img = new Image();

        md.setQrCodeImage(img);

        img.setType(ImageType.THUMBNAIL);
        img.setUrl("128x128.png");
        fp.add(img);
        FormLabel fl = new FormLabel();
        fl.setText("✓");
        fl.setVisible(false);
        fl.getElement().getStyle().setProperty("color", "#58b957");
        fl.getElement().getStyle().setProperty("fontWeight", "bold");
        fl.getElement().getStyle().setProperty("marginLeft", "5px");
        fp.add(fl);

        md.setCheckMarkLabel(fl);

        fl = new FormLabel();
        fl.setText("✗");
        fl.setVisible(true);
        fl.getElement().getStyle().setProperty("color", "#ff0000");
        fl.getElement().getStyle().setProperty("fontWeight", "bold");
        fl.getElement().getStyle().setProperty("marginLeft", "5px");
        fp.add(fl);

        md.setCrossLabel(fl);

        // add
        row.add(fp);
        form.add(row);

        // Issuer =====================================
        row = new ObidosRowBottom2px();
        FormLabel label = createFormLabel("Issuer");
        String tHtml = "<b>Issuer <font color=\"#ff0000\">*</font></b>";
        label.setHTML(tHtml);
        fp = createFlowPanel();
        TextBox tbox = createTextBox();

        md.setIssuerTextBox(tbox);
        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // Account =====================================
        row = new ObidosRowBottom2px();
        label = createFormLabel("Account");
        tHtml = "<b>Account <font color=\"#ff0000\">*</font></b>";
        label.setHTML(tHtml);
        fp = createFlowPanel();
        tbox = createTextBox();

        md.setAccountTextBox(tbox);
        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // Secret =====================================
        row = new ObidosRowBottom2px();
        label = createFormLabel("Secret Key");
        tHtml = "<b>Secret Key <font color=\"#ff0000\">*</font></b>";
        label.setHTML(tHtml);
        fp = createFlowPanel();
        InputGroup ig = new InputGroup();
        ObidosPasswordBox secretTextBox = new ObidosPasswordBox();
        secretTextBox.getElement().getStyle().setProperty("fontFamily", "monospace");
        secretTextBox.getElement().getStyle().setProperty("fontSize", "small");
        ig.add(secretTextBox);
        InputGroupButton igb = new InputGroupButton();
        ig.add(igb);
        Button showHideButton = new Button();
        showHideButton.setType(ButtonType.INFO);
        showHideButton.setIcon(IconType.EYE);
        igb.add(showHideButton);

        md.setShowHideButton(showHideButton);
        md.setSecretTextBox(secretTextBox);

        row.add(label);
        row.add(fp);
        fp.add(ig);
        form.add(row);

        // set the show hide handler for the text box
        showHideButton.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                ClientUtils.toggleEyeIcon(showHideButton, secretTextBox);

            }
        });

        // refresh count-down timer label
        Row labelRow = new Row();
        FlowPanel refreshFlowPanel = new FlowPanel();
        refreshFlowPanel.addStyleName("col-sm-offset-3 col-sm-9");
        refreshFlowPanel.getElement().getStyle().setProperty("marginBottom", "2px");
        Label refreshLabel = new Label();
        refreshLabel.setText("30");
        refreshLabel.setType(LabelType.INFO);

        // Last updated html =======
        Small lastUpdateHtml = new Small();

        md.setLastUpdatedHtml(lastUpdateHtml);

        lastUpdateHtml.setText("");
        lastUpdateHtml.getElement().getStyle().setProperty("marginLeft", "10px");

        // add
        labelRow.add(refreshFlowPanel);
        refreshFlowPanel.add(refreshLabel);

        refreshFlowPanel.add(lastUpdateHtml); // XXX

        form.add(labelRow);

        md.setCountDownLabel(refreshLabel);

        // 2FA Code
        row = new ObidosRowBottom2px();
        label = createFormLabel("2FA Code");
        fp = createFlowPanel();
        // add copy to clibpboard button
        // Issue #784
        InputGroup inputGroup = new InputGroup();
        tbox = createReadOnlyTextBox();
        tbox.getElement().getStyle().setProperty("fontWeight", "bold");
        tbox.getElement().getStyle().setProperty("textAlign", "center");
        tbox.setText("??? ???");
        inputGroup.add(tbox);
        Button copyButton = new Button();
        InputGroupButton inputGroupButton = new InputGroupButton();
        copyButton.setType(ButtonType.DEFAULT);
        copyButton.setIcon(IconType.COPY);
        copyButton.setTitle(lang.copy());
        copyButton.setDataLoadingText(glang.copied());
        inputGroupButton.add(copyButton);
        inputGroup.add(inputGroupButton);

        md.setTwoFACodeTextBox(tbox);
        // add
        row.add(label);
        row.add(inputGroup);
        row.add(fp);
        fp.add(inputGroup);
        form.add(row);

        copyButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                String txt = md.getTwoFACodeTextBox().getValue();
                if (txt != null && txt.length() > 0)
                {
                    txt = txt.replaceAll("\\s+", "");
                    ClientUtils.copyTextToClipboard(txt);
                    copyButton.state().loading();
                    new Timer()
                    {
                        @Override
                        public void run()
                        {
                            copyButton.state().reset();
                        }
                    }.schedule(Integer.parseInt(glang.copyTooltipTimerSchedule()));
                }
            }
        });

        // Show QR Code Image Toggle Switch =================================
        row = new ObidosRowBottom2px();
        label = createFormLabel(glang.showQRCodeImage());
        fp = createFlowPanel();
        ToggleSwitch ts = new ToggleSwitch();

        md.setShowQrCodeImageSwitch(ts);

        ts.setSize(SizeType.SMALL);
        ts.setOnText("✓");
        ts.setOffText("✗");
        ts.setOnColor(ColorType.SUCCESS);
        ts.setOffColor(ColorType.DANGER);
        ts.setValue(false);

        // add
        row.add(label);
        row.add(fp);
        fp.add(ts);
        form.add(row);

        // handler for toggle switch
        ts.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                Row row = md.getQrCodeImageRow();
                row.setVisible(!row.isVisible());
            }
        });

        // Message Row ============================================
        ObidosMessageRow xrow = new ObidosMessageRow();
        form.add(xrow);

        md.setMessageRow(xrow);

        // modal footer with buttons
        ModalFooter footer = new ModalFooter();
        // Re-Generate
        Button button = new Button(glang.refresh());
        button.setType(ButtonType.SUCCESS);
        footer.add(button);

        button.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                md.generate2FACode();
            }
        });

        // help button
        button = new Button(lang.help());
        button.setType(ButtonType.PRIMARY);
        footer.add(button);
        button.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                bq.setVisible(!bq.isVisible());
            }
        });

        // Close
        button = new Button(lang.close());
        button.setType(ButtonType.DANGER);
        button.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                modal.hide();
            }
        });

        footer.add(button);

        // add to modal body
        modal.add(modalBody);
        modal.add(footer);
        modalBody.add(form);

        // update fields
        md.setModal(modal);

        // setup hide handler, cleanup things here
        modal.addHiddenHandler(new ModalHiddenHandler()
        {

            @Override
            public void onHidden(ModalHiddenEvent evt)
            {
                myLog("Modal hide handler fired.");
                md.clear();
            }
        });

        modal.addShowHandler(new ModalShowHandler()
        {

            @Override
            public void onShow(ModalShowEvent evt)
            {
                md.clearMessage();
                md.showQrCodeWidgets(false);
                md.generate2FACode();
            }
        });

        return md;
    }

    public static void showTwoFACode(final PlaceManager placeManager, final TwoFAModalData modalData)
    {
        // make RPC call, populate modal on Success
        populateTwoFAModalAndShow(modalData);
    }

    public static void setupWiseUploadHandlers(final FileUploadModalData md)
    {
        Uploader u = md.getWiseUploader();
        ProgressBar pb = md.getProgressBar();
        ObidosMessageRow mr = md.getMessageRow();
        NumberFormat decimalFormat = NumberFormat.getFormat(".##");
        u.setUploadSuccessHandler(new UploadSuccessHandler()
        {
            @Override
            public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent)
            {
                String filename = md.getFilename();
                myLog("upload success: file: " + filename);
                String msg = "File uploaded successfully!";
                if (filename != null)
                {
                    filename = ClientUtils.shortenFilename(filename, ObidosConstants.SHORTEN_STRING_AFTER_LENGTH);
                    msg = "File " + filename + " uploaded successfully!";
                }
                md.showMessage(msg);
                md.getParentMessageRow().showMessage(msg);
                md.getCancelCloseButton().setType(ButtonType.DEFAULT);
                md.getCancelCloseButton().setText(glang.closeC());
                md.getUploadingSpinnerButton().setVisible(false);
                return true;
            }
        });

        u.setUploadProgressHandler(new UploadProgressHandler()
        {

            @Override
            public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent)
            {
                double percent = (double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal();
                percent = Math.round(percent * 100);
                String text = Double.toString(percent) + "%";
                pb.setPercent(percent);
                pb.setText(text);

                long fileSize = uploadProgressEvent.getFile().getSize();
                String fsFormatted = NumberFormat.getFormat("#,###").format(fileSize);
                String fs = fsFormatted + " bytes";
                md.getFilesizeTextBox().setValue(fs);

                // speed is in bits/sec
                double averageSpeed = uploadProgressEvent.getFile().getAverageSpeed();
                // convert to Mbbs
                double mbps = 0.0;
                if (averageSpeed > 0.0)
                {
                    mbps = averageSpeed / 1048576.0;
                }
                String as = decimalFormat.format(mbps) + " Mbps";
                md.getAverageSpeedTextBox().setValue(as);

                // it seems to give wrong number
                // long bytesTotal = uploadProgressEvent.getBytesTotal();
                long bytesTotal = fileSize;
                long bytesComplete = uploadProgressEvent.getBytesComplete();
                String bytesCompletedFormatted = NumberFormat.getFormat("#,###").format(bytesComplete);
                // String bu = bytesComplete + "/" + bytesTotal;
                String bu = bytesCompletedFormatted + "";
                md.getBytesUploadedTextBox().setValue(bu);

                double secsElapsed = uploadProgressEvent.getFile().getTimeElapsed();
                secsElapsed = Math.round(secsElapsed);
                String se = secsElapsed + "";
                long duration = (long) secsElapsed;
                int hours = (int) duration / 3600;
                int remainder = (int) duration - hours * 3600;
                int mins = remainder / 60;
                remainder = remainder - mins * 60;
                int secs = remainder;

                String hrs = NumberFormat.getFormat("00").format(hours);
                String minss = NumberFormat.getFormat("00").format(mins);
                String secss = NumberFormat.getFormat("00").format(secs);

                String timeString = hrs + ":" + minss + ":" + secss;
                md.getSecsElapsedTextBox().setValue(timeString);

                double secsRemaining = uploadProgressEvent.getFile().getTimeRemaining();
                secsRemaining = Math.round(secsRemaining);
                duration = (long) secsRemaining;
                hours = (int) duration / 3600;
                remainder = (int) duration - hours * 3600;
                mins = remainder / 60;
                remainder = remainder - mins * 60;
                secs = remainder;

                hrs = NumberFormat.getFormat("00").format(hours);
                minss = NumberFormat.getFormat("00").format(mins);
                secss = NumberFormat.getFormat("00").format(secs);
                timeString = hrs + ":" + minss + ":" + secss;
                md.getSecsRemainingTextBox().setValue(timeString);
                return true;
            }
        });

        u.setUploadErrorHandler(new UploadErrorHandler()
        {

            @Override
            public boolean onUploadError(UploadErrorEvent uploadErrorEvent)
            {
                String emsg = "Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to ["
                        + uploadErrorEvent.getErrorCode().toString() + "]: " + uploadErrorEvent.getMessage();
                md.getMessageRow().showErrorMessage(emsg);
                return false;
            }
        });
    }

    private static void cancelFileUpload(final FileUploadModalData md)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        Uploader u = md.getWiseUploader();
        Stats stats = md.getWiseUploader().getStats();
        String filename = md.getFilenameTextBox().getValue();
        String with2FAInfo = "";
        myLog("XXX qrcode image decoded: " + md.isQrcodeImageDecoded());
        if (md.isQrcodeImageDecoded())
        {
            with2FAInfo = " with 2FA Info ";
        }
        myLog("XXX with2FAInfo: " + with2FAInfo);

        if (filename == null)
        {
            filename = "N/A";
        }
        int maxLen = ObidosConstants.SHORTEN_STRING_AFTER_LENGTH;
        String shortened = shortenFilename(filename, maxLen);
        if (stats.getUploadsInProgress() == 1)
        {
            myLog("File upload in progress...");
            u.cancelUpload();
            md.showErrorMessage(glang.fileUploadCancelled());
            md.getCancelCloseButton().setType(ButtonType.DEFAULT);
            md.getCancelCloseButton().setText(glang.close());
            md.getUploadingSpinnerButton().setVisible(false);
            if (md.getPlace().equals(NameTokens.EDIT_ITEM))
            {
                md.showMessageToParnetWindow(
                        "Item updated " + with2FAInfo + "." + " But file " + shortened + " upload is cancelled!");
            } else
            {
                md.showMessageToParnetWindow(
                        "Item created " + with2FAInfo + "." + " But file " + shortened + " upload is cancelled!");
            }
            md.setUploadCancelled(true);
        } else
        {
            if (!md.isUploadCancelled())
            {
                if (md.getPlace().equals(NameTokens.EDIT_ITEM))
                {
                    md.showMessageToParnetWindow("Item updated " + with2FAInfo + " and file " + shortened
                            + " is also uploaded successfully");
                    ObidosTextBox tb = md.getDocumentFilenameTextBox();
                    if (tb != null)
                    {
                        // file uploaded from edit item page
                        if (filename != null)
                        {
                            tb.setValue(shortened);
                        }
                    }
                } else
                {
                    md.showMessageToParnetWindow("Item created " + with2FAInfo + " and file " + shortened
                            + " is also uploaded successfully");
                }
            } else
            {
                if (md.getPlace().equals(NameTokens.EDIT_ITEM))
                {
                    md.showMessageToParnetWindow(
                            "Item Updated " + with2FAInfo + "." + " But File " + shortened + " upload is cancelled!");
                } else
                {
                    md.showMessageToParnetWindow(
                            "Item created " + with2FAInfo + "." + " But File " + shortened + " upload is cancelled!");
                }
            }
            md.getFileChosenSpan().setText(lang.noFileChosen());
            md.setFilename(null);

            // Bug #875
            if (md.getUploadRow() != null)
            {
                // Bug #63
                md.getUploadRow().setVisible(false);
            }
            if (md.getAttachmentSwitch() != null)
            {
                // Bug #63
                md.getAttachmentSwitch().setValue(false);
            }

            md.getModal().hide();
        }
    }

    private static void confirmCancelUpload(final FileUploadModalData md)
    {
        String message = "File Upload is in proress!<br/>Are you sure you can to cancel?";
        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle("Cacel File Upload");
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
                Uploader u = md.getWiseUploader();
                Stats stats = md.getWiseUploader().getStats();
                if (stats.getUploadsInProgress() == 1)
                {
                    myLog("File upload in progress...");
                    u.cancelUpload();
                    md.showMessage("Upload Cancelled!");
                    md.getCancelCloseButton().setType(ButtonType.DEFAULT);
                    md.getCancelCloseButton().setText(glang.close());
                }
            }
        });

        // Yes
        options.addButton(ObidosMessages.LANG.yes(), ButtonType.DANGER.getCssName(), new SimpleCallback()
        {
            @Override
            public void callback()
            {
            }
        });
        Bootbox.dialog(options);
    }

    public static FileUploadModalData createWiseFileUploadProgressModal(final PlaceManager placeManager,
            final Uploader wiseUploader, final String filenameToUpload, final ObidosTextBox documentFilenameTextBox, // Edit
                                                                                                                     // item
                                                                                                                     // with
                                                                                                                     // doc
            final String place, final ObidosMessageRow parentMessageRow, final Span fileChosenSpan)
    {
        /*
         * <Modal> <ModayBody> <Form> <Row> <FormLabel/> <FlowPanel>
         * <ObidosTextBox/> </FlowPanel> </Row> .... </form> </ModalBody>
         * </Moda>
         */

        FileUploadModalData md = new FileUploadModalData();

        md.setPlaceManager(placeManager);
        md.setWiseUploader(wiseUploader);
        md.setFilename(filenameToUpload);
        md.setDocumentFilenameTextBox(documentFilenameTextBox);
        md.setPlace(place);
        md.setParentMessageRow(parentMessageRow);
        md.setFileChosenSpan(fileChosenSpan);

        Modal modal = new Modal();

        md.setModal(modal);

        ModalBody modalBody = new ModalBody();
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
        modal.setTitle(glang.uploadingFile());
        modal.setClosable(true);
        modal.setFade(true);
        modal.setDataKeyboard(true);

        ObidosRowBottom2px row = new ObidosRowBottom2px();
        FlowPanel fp = new FlowPanel();

        // progress bar
        row = new ObidosRowBottom2px();
        fp = new FlowPanel();
        fp.addStyleName("col-sm-offset-3 col-sm-7");
        Progress progress = new Progress();
        ProgressBar progressBar = new ProgressBar();

        md.setProgressBar(progressBar);

        row.add(fp);
        fp.add(progress);
        progress.add(progressBar);
        form.add(row);

        // filename
        row = new ObidosRowBottom2px();
        FormLabel flabel = createFormLabel(glang.filename());
        fp = createFlowPanel();
        TextBox filenameTextBox = createReadOnlyTextBox();

        md.setFilenameTextBox(filenameTextBox);

        row.add(flabel);
        row.add(fp);
        fp.add(filenameTextBox);
        form.add(row);

        // filesize
        row = new ObidosRowBottom2px();
        flabel = createFormLabel(glang.filesize());
        fp = createFlowPanel();
        TextBox filesizeTextBox = createReadOnlyTextBox();

        md.setFilesizeTextBox(filesizeTextBox);

        row.add(flabel);
        row.add(fp);
        fp.add(filesizeTextBox);
        form.add(row);

        // Bytes uploaded
        row = new ObidosRowBottom2px();
        flabel = createFormLabel(glang.bytesUploaded());
        fp = createFlowPanel();
        TextBox bytesUploadedTextBox = createReadOnlyTextBox();

        md.setBytesUploadedTextBox(bytesUploadedTextBox);

        row.add(flabel);
        row.add(fp);
        fp.add(bytesUploadedTextBox);
        form.add(row);

        // Average speed
        row = new ObidosRowBottom2px();
        flabel = createFormLabel(glang.speed());
        fp = createFlowPanel();
        TextBox averageSpeedTextBox = createReadOnlyTextBox();

        md.setAverageSpeedTextBox(averageSpeedTextBox);

        row.add(flabel);
        row.add(fp);
        fp.add(averageSpeedTextBox);
        form.add(row);

        // Secs elapsed
        row = new ObidosRowBottom2px();
        flabel = createFormLabel(glang.timeElapsed());
        fp = createFlowPanel();
        TextBox secsElapsedTextBox = createReadOnlyTextBox();

        md.setSecsElapsedTextBox(secsElapsedTextBox);

        row.add(flabel);
        row.add(fp);
        fp.add(secsElapsedTextBox);
        form.add(row);

        // Secs remaining
        row = new ObidosRowBottom2px();
        flabel = createFormLabel(glang.timeRemaining());
        fp = createFlowPanel();
        TextBox secsRemainingTextBox = createReadOnlyTextBox();

        md.setSecsRemainingTextBox(secsRemainingTextBox);

        row.add(flabel);
        row.add(fp);
        fp.add(secsRemainingTextBox);
        form.add(row);

        // message
        ObidosMessageRow messageRow = new ObidosMessageRow();
        md.setMessageRow(messageRow);
        form.add(messageRow);

        // footer
        ModalFooter footer = new ModalFooter();
        Button uploadingButton = new Button();
        uploadingButton.setType(ButtonType.LINK);
        uploadingButton.setIcon(IconType.SPINNER);
        uploadingButton.setIconSpin(true);
        uploadingButton.getElement().getStyle().setCursor(Cursor.DEFAULT);
        footer.add(uploadingButton);

        md.setUploadingSpinnerButton(uploadingButton);

        // CLOSE/CANCEL UPLOAD
        Button cancelButton = new Button();
        cancelButton.setText(glang.cancelUpload());
        cancelButton.setType(ButtonType.DANGER);
        footer.add(cancelButton);

        md.setCancelCloseButton(cancelButton);

        cancelButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                cancelFileUpload(md);
                return;
            }
        });

        modal.add(modalBody);
        modal.add(footer);
        modalBody.add(form);

        // setup hide handler, cleanup things here
        modal.addHiddenHandler(new ModalHiddenHandler()
        {

            @Override
            public void onHidden(ModalHiddenEvent evt)
            {
                myLog("Modal hide handler fired.");
                md.clear();
            }
        });

        modal.addShowHandler(new ModalShowHandler()
        {

            @Override
            public void onShow(ModalShowEvent evt)
            {
                myLog("Modal show handler fired");
                md.clearMessage();
                md.startSessionExtendTimer();
            }
        });

        // setup the upload handlers
        myLog("filename: " + md.getFilename());
        setupWiseUploadHandlers(md);

        return md;
    }

    public static void showWiseUploadModal(FileUploadModalData md)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                String filename = md.getFilename();
                if (filename != null)
                {
                    String shortened = shortenFilename(filename, ObidosConstants.SHORTEN_STRING_AFTER_LENGTH);
                    md.setFilename(filename);
                    md.getFilenameTextBox().setValue(shortened);
                }
                md.getModal().show();
            }
        });
    }

    public static String makeOtpAuthUri(String issuer, String account, final String secret)
    {
        if (issuer == null)
        {
            issuer = "UNKNOWN";
        }
        if (account == null)
        {
            account = "UNKNOWN";
        }
        if (secret == null)
        {
            return null;
        }
        return "otpauth://totp/" + issuer + ":" + account + "?secret=" + secret + "&issuer=" + issuer
                + "&algorithm=SHA1&digits=6&period=30";
    }

    public static void populateTwoFAModalAndShow(TwoFAModalData md)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                String str = md.getIssuer();
                if (str != null)
                {
                    md.getIssuerTextBox().setValue(str);
                }
                str = md.getAccount();
                if (str != null)
                {
                    md.getAccountTextBox().setValue(str);
                }
                str = md.getBase32Secret();
                if (str != null)
                {
                    md.getSecretTextBox().setValue(str);
                }
                md.getModal().show();
            }
        });
    }

    public static UserInfoModalData createUserInfoModal()
    {
        ObidosMessages lang = ObidosMessages.LANG;
        UserInfoModalData md = new UserInfoModalData();

        /*
         * <Modal> <ModayBody> <Form> <Row> <FormLabel/> <FlowPanel> <TextBox/>
         * </FlowPanel> </Row> .... </form> </ModalBody> </Moda>
         */

        Modal modal = new Modal();
        ModalBody modalBody = new ModalBody();
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
        modal.setTitle(lang.userInfo());
        modal.setClosable(true);
        modal.setFade(true);
        modal.setDataKeyboard(true);

        // profile pic
        ObidosRowBottom2px row = new ObidosRowBottom2px();
        FlowPanel fp = new FlowPanel();
        fp.addStyleName("col-sm-offset-4 col-sm-8");
        row.add(fp);
        Image profileImage = new Image();
        profileImage.setType(ImageType.THUMBNAIL);
        fp.add(profileImage);
        profileImage.setUrl("128x128.png");
        form.add(row);

        md.setProfileImage(profileImage);

        // need an invisible image for determining profile pic's size
        row = new ObidosRowBottom2px();
        row.setVisible(false);
        Image invisibleImage = new Image();
        invisibleImage.setVisible(false);
        row.add(invisibleImage);
        form.add(row);

        md.setInvisibleImage(invisibleImage);

        // fullname row ============================
        row = new ObidosRowBottom2px();
        FormLabel label = createFormLabel(lang.fullname());
        fp = createFlowPanel();
        TextBox tbox = createReadOnlyTextBox();

        md.setFullnameTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // email row ==========================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.primaryEmail());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setEmailTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // accept email ======================
        row = new ObidosRowBottom2px();
        label = createFormLabel("Accept email");
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setAcceptEmailNotificationTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // phone row ==========================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.primaryPhone());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setPhoneTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // mobile phone row ==================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.mobilePhoneSimple());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setMobilePhoneTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // accept SMS notification ================
        row = new ObidosRowBottom2px();
        label = createFormLabel("Accept SMS");
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setAcceptSMSNotificationTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // md.setMobilePhoneTextBox(tbox);

        // office row =========================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.office());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setOfficeTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // fb row =========================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.facebook());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setFacebookTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // twitter row =========================
        row = new ObidosRowBottom2px();
        label = createFormLabel(lang.twitter());
        fp = createFlowPanel();
        tbox = createReadOnlyTextBox();

        md.setTwitterTextBox(tbox);

        // add
        row.add(label);
        row.add(fp);
        fp.add(tbox);
        form.add(row);

        // modal footer ------------------
        ModalFooter footer = new ModalFooter();
        Button button = new Button(lang.close());
        button.setType(ButtonType.DANGER);
        button.setDataDismiss(ButtonDismiss.MODAL);
        footer.add(button);

        // add to modal body
        modal.add(modalBody);
        modal.add(footer);
        modalBody.add(form);

        // update fields
        md.setModal(modal);

        return md;
    }

    public static void setSummernoteWidth(final FlowPanel fp)
    {
        fp.addStyleName("col-sm-12");
        fp.getElement().getStyle().setProperty("maxWidth", "95%");
        fp.getElement().getStyle().setProperty("marginLeft", "30px");
    }

    public static void clearSummernote(final Summernote summernote)
    {
        summernote.setCode("");
    }

    public static void summernoteCopyToClipboard(final Summernote summernote)
    {
        String htmlString = summernote.getCode();
        if (htmlString != null && htmlString.length() > 0)
        {
            HTML html = new HTML(SafeHtmlUtils.fromTrustedString(htmlString));
            String txt = html.getText();
            ClientUtils.copyTextToClipboard(txt);
        }
    }

    // If CTRL+L is pressed toggle between Bangla and English
    public static void setSummernoteLanguageInputToggle(final Summernote summernote, final ListBox lb,
            final ObidosRowBottom2px row)
    {
        myLog("MMM in set summernote input toggle");
        if (summernote == null)
        {
            return;
        }
        if (!isBanglaEditingEnabled())
        {
            return;
        }
        summernote.addSummernoteKeyDownHandler(new SummernoteKeyDownHandler()
        {
            @Override
            public void onSummnernoteKeyDown(SummernoteKeyDownEvent event)
            {
                NativeEvent nativeEvent = event.getNativeEvent();
                myLog("MMM key code: " + nativeEvent.getKeyCode());
                // if (nativeEvent.getAltKey() && nativeEvent.getKeyCode() ==
                // 'L') // L = 76
                // In Windows CTRL is a browser shortcut

                // Escape key code is 27
                // Toggle between Bangla and English when ESC is pressed
                if (nativeEvent.getKeyCode() == 27)
                {
                    int idx = lb.getSelectedIndex();
                    // Toggle index
                    idx = (idx == 0) ? 1 : 0;
                    lb.setSelectedIndex(idx);
                    // Callback will correctly set the input language based on
                    // the index of the selection box
                    languageListBoxCallback(lb, row);
                } else if (nativeEvent.getCtrlKey() && nativeEvent.getKeyCode() == 'C')
                {
                    summernoteCopyToClipboard(summernote);
                    clearSummernote(summernote);
                }
            }
        });
    }

    public static void languageListBoxCallback(final ListBox lb, final ObidosRowBottom2px row)
    {
        String lang = lb.getSelectedValue();
        int idx = lb.getSelectedIndex();
        // only support English and Bangla for editing at this time
        switch (idx)
        {
        case 0: // English
        {
            ClientUtils.enableBanglaEditing(false, row);
            break;
        }
        case 1: // Bangla
        {
            ClientUtils.enableBanglaEditing(true, row);
            break;
        }
        }
    }

    private static void setTextBoxValue(final TextBox textBox, String val)
    {
        if (val == null || val.length() == 0)
        {
            val = glang.na();
        }
        textBox.setValue(val);
    }

    private static void populateUserInfoModal(final UserDTO dto, final UserInfoModalData md)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                byte[] picBytes = dto.getProfilePic();
                if (picBytes != null)
                {
                    String dataURL = new String(picBytes);
                    ClientUtils.displayProfilePicture(dataURL, md.getProfileImage(), md.getInvisibleImage());
                } else
                {
                    ClientUtils.displayProfilePlaceHolderImage(md.getProfileImage());
                }
                setTextBoxValue(md.getFullnameTextBox(), dto.getFullname());
                setTextBoxValue(md.getEmailTextBox(), dto.getEmail1());
                String txt = glang.na();
                boolean acceptEmailNotification = fromBoolean(dto.getAcceptEmailNotification());
                if (acceptEmailNotification)
                {
                    txt = glang.yes();
                } else
                {
                    txt = glang.no();
                    if (dto.getEmail1() == null || dto.getEmail1().length() == 0)
                    {
                        txt = glang.na();
                    }
                }
                setTextBoxValue(md.getAcceptEmailNotificationTextBox(), txt);

                txt = glang.na();
                setTextBoxValue(md.getPhoneTextBox(), dto.getPhone());
                setTextBoxValue(md.getMobilePhoneTextBox(), dto.getMobile1());
                boolean acceptSmsNotification = fromBoolean(dto.getAcceptSMSNotification());
                if (acceptSmsNotification)
                {
                    txt = glang.yes();
                } else
                {
                    txt = glang.no();
                    if (dto.getMobile1() == null)
                    {
                        txt = glang.na();
                    }
                }
                setTextBoxValue(md.getAcceptSMSNotificationTextBox(), txt);
                setTextBoxValue(md.getOfficeTextBox(), dto.getOffice());
                setTextBoxValue(md.getFacebookTextBox(), dto.getFacebook());
                setTextBoxValue(md.getTwitterTextBox(), dto.getTwitter());
                md.getModal().show();
            }
        });
    }

    /**
     * pop dialog to send test template mail and populate fields
     * 
     * @param placeManager
     * @param modalData
     * @param envelope
     * @param jsonDTO
     * @param messageRow
     * @param authCreds
     *            <p>
     * @author spgdev@spenego.com - Sep 25, 2019
     */
    public static void showSendTestNotificatinEmailDialog(final SendNotificationTestEmailModalData modalData,
            final ObidosMessageRow messageRow)
    {
        Modal modal = modalData.getModal();
        modal.show();
        /*
         * GwtAsyncWrapper<Void> callback = new
         * GwtAsyncWrapper<Void>(placeManager) {
         * 
         * @Override public void uponSuccess(Void result) { String d = new
         * Date().toString();
         * messageRow.showMessage("Test email sent successfully on: " + d); }
         * 
         * @Override public void uponFailure(Throwable caught) {
         * messageRow.showErrorMessage("Could not send test email: "+
         * caught.getMessage()); } }; AdminConfigService.Utility.getInstance().
         * sendNotificationTemplateTestEmail(authCreds, envelope, jsonDTO,
         * callback);
         */
    }

    public static void showUserInfo(final PlaceManager placeManager, final UserInfoModalData modalData,
            final Long userId)
    {
        if (userId == null)
        {
            return;
        }
        // the following RPC is not designed for this
        GwtAsyncWrapper<UserDTO> callback = new GwtAsyncWrapper<UserDTO>(placeManager)
        {

            @Override
            public void uponSuccess(UserDTO dto)
            {
                populateUserInfoModal(dto, modalData);
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                ObidosMessageRow row = modalData.getMessageRow();
                if (row != null)
                {
                    modalData.getMessageRow().showErrorMessage("ERROR: " + caught.getMessage());
                } else
                {
                    ClientUtils.showBootboxDialog("ERROR", caught.getMessage());
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getUser(authCreds, userId, callback);
    }

    public static void showUserInfo(final PlaceManager placeManager, final Long userId,
            final ObidosMessageRow messageRow)
    {
        UserInfoModalData modalData = ApplicationPresenter.getUserInfoModalData();
        modalData.setMessageRow(messageRow);
        ClientUtils.showUserInfo(placeManager, modalData, userId);
    }

    public static void goBack(final PlaceManager placeManager)
    {
        placeManager.navigateBack();
    }

    public static void back(final PlaceManager placeManager)
    {
        placeManager.navigateBack();
    }

    public static void displayProfilePicture(final String dataURL, final Image profilePicImageWidget,
            final Image invisibleImageWidget)
    {
        invisibleImageWidget.addLoadHandler(new LoadHandler()
        {
            @Override
            public void onLoad(LoadEvent event)
            {
                setURLToInvisibleImage(profilePicImageWidget, invisibleImageWidget, dataURL);
            }
        });
        invisibleImageWidget.setUrl(dataURL);
    }

    public static void displayProfilePlaceHolderImage(final Image profilePicImageWidget)
    {
        Image image = profilePicImageWidget;
        image.setUrl(ObidosConstants.PROFILE_PLACEHOLDER_IMAGE);
        image.getElement().getStyle().setWidth(ObidosConstants.PROFILE_PIC_MAX_WIDTH, Unit.PX);
        image.getElement().getStyle().setHeight(ObidosConstants.PROFILE_PIC_MAX_HEIGHT, Unit.PX);
    }

    public static void setURLToInvisibleImage(final Image profileImage, final Image invisibleImage,
            final String dataURL)
    {
        int width = invisibleImage.getWidth();
        if (width == 0)
        {
            width = ieWidth(invisibleImage.getElement());
        }
        int height = invisibleImage.getHeight();
        if (height == 0)
        {
            height = ieHeight(invisibleImage.getElement());
        }
        int newWidth = ObidosConstants.PROFILE_PIC_MAX_WIDTH;
        int newHeight = ObidosConstants.PROFILE_PIC_MAX_HEIGHT;
        if (width < newWidth)
        {
            newWidth = width;
        }
        if (height < newHeight)
        {
            newHeight = height;
        }

        // maintain aspect ratio
        float aspectRatio = (float) ((width * 1.0) / (height * 1.0));
        newHeight = Math.round((newWidth / aspectRatio));
        newWidth = Math.round((newHeight * aspectRatio));

        Image image = profileImage;
        image.setUrl(dataURL);
        image.getElement().getStyle().setWidth(newWidth, Unit.PX);
        image.getElement().getStyle().setHeight(newHeight, Unit.PX);
    }

    public static boolean isHTML5FileApiSupported()
    {
        return FileUtils.supportsFileAPI();
    }

    public static void setPanelHeaderColor(final ObidosPanelHeader panelHeader, final CurrentUser currentUser)
    {
        if (currentUser == null)
        {
            return;
        }
        if (ClientUtils.isAdmin(currentUser))
        {
            panelHeader.getElement().getStyle().setProperty("backgroundColor", "#222");

        } else
        {
            panelHeader.getElement().getStyle().setProperty("backgroundColor", "#337AB7");
        }
    }

    public static boolean checkIfpasswordHasExpired(final CurrentUser currentUser)
    {
        boolean b = ClientUtils.getPasswordChangeRequired(currentUser);
        myLog("password change required: " + b);
        if (b)
        {
            ClientUtils.showBootboxDialog(ObidosMessages.LANG.changePassword(),
                    ObidosMessages.LANG.passwordChangeRequired());
            return true;
        }

        return false;
    }

    public static boolean getPasswordChangeRequired(final CurrentUser currentUser)
    {
        if (currentUser == null)
        {
            return true;
        }
        UserDTO userDTO = currentUser.getUserDTO();
        if (userDTO == null)
        {
            return true;
        }
        return userDTO.authSourceIsLocal() && userDTO.getPasswordChangeRequired();
    }

    public static boolean checkIfKeypairIsCreated(final CurrentUser currentUser, final PlaceManager placeManager)
    {
        if (currentUser == null)
        {
            return true;
        }
        // admins don't have keypair
        if (isAdmin(currentUser))
        {
            return true;
        }
        if (!currentUser.getUserDTO().keyPairExists())
        {
            ClientUtils.showBootboxDialog(ObidosMessages.LANG.keyPairNotCreated(),
                    ObidosMessages.LANG.createKeypairFirst());
            ClientUtils.showPage(placeManager, NameTokens.KEY_PAIR);
            return false;
        }
        return true;
    }

    public static List<Option> getAuditLogSearchSelectItems(final CurrentUser currentUser)
    {
        ObidosMessages glang = ObidosMessages.LANG;
        List<Option> list = new ArrayList<>(32);

        list.add(addToOption(glang.auditLogLogin()));

        if (ClientUtils.isAdmin(currentUser))
        {
            list.add(addToOption(glang.auditLogSystem()));
            list.add(addToOption(glang.auditLogLockUser()));
            list.add(addToOption(glang.auditLogUnLockUser()));
            list.add(addToOption(glang.auditLogTombstoneUser()));
            list.add(addToOption(glang.auditLogRestoreUser()));
            list.add(addToOption(glang.auditLogUser()));
        }

        list.add(addToOption(glang.auditLogItem()));
        list.add(addToOption(glang.auditLogContainer()));
        list.add(addToOption(glang.auditLogGroup()));
        list.add(addToOption(glang.auditLogCreate()));
        list.add(addToOption(glang.auditLogCreateItem()));
        list.add(addToOption(glang.auditLogCreateContainer()));
        list.add(addToOption(glang.auditLogUpdate()));
        list.add(addToOption(glang.auditLogUpdateItem()));
        list.add(addToOption(glang.auditLogUpdateContainer()));
        list.add(addToOption(glang.auditLogDelete()));
        list.add(addToOption(glang.auditLogDeleteContainer()));
        list.add(addToOption(glang.auditLogDeleteGroup()));
        list.add(addToOption(glang.auditLogRevoke()));
        list.add(addToOption(glang.auditLogRevokeItem()));
        list.add(addToOption(glang.auditLogRevokeContainer()));
        list.add(addToOption(glang.auditLogRelinquish()));
        list.add(addToOption(glang.auditLogRelinquishItem()));
        list.add(addToOption(glang.auditLogRelinquishContainer()));
        list.add(addToOption(glang.auditLogShare()));
        list.add(addToOption(glang.auditLogShareItem()));
        list.add(addToOption(glang.auditLogShareContainer()));
        list.add(addToOption(glang.auditLogPasswordManagement()));

        return list;
    }

    private static Option addToOption(final String text)
    {
        Option option = new Option();
        option.setText(text);
        return option;
    }

    private static void addPassItem(final ListGroup lg, String html)
    {
        ListGroupItem item = new ListGroupItem();
        item.setHTML(html);
        lg.add(item);
    }

    public static String passwordStrengthFromEntropy(final int entropy)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (entropy < ObidosConstants.PASSWORD_ENTROPY_VERY_STRONG)
        {
            return lang.weak();
        } else if (entropy == ObidosConstants.PASSWORD_ENTROPY_STRONG)
        {
            return lang.strong();
        }
        return lang.veryStrong();
    }

    public static String passphraseStrengthFromEntropy(final int entropy)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        if (entropy < ObidosConstants.PASSPHRASE_ENTROPY_STRONG)
        {
            return lang.weak();
        } else if (entropy == ObidosConstants.PASSPHRASE_ENTROPY_STRONG)
        {
            return lang.strong();
        }
        return lang.veryStrong();
    }

    public static void displayPasswordPolicy(final ListGroup lg, final PassComplexityDTO dto)
    {
        ObidosMessages glang = ObidosMessages.LANG;
        lg.clear();

        final ComplexityRequirementsDTO complexity = dto.getPasswordComplexityRequirements();
        int minEntropy = complexity.getMinimumEntropy();
        String strength = passwordStrengthFromEntropy(minEntropy);

        String row = glang.passwordRowDouble(glang.passwordStrength(), strength, glang.minimumPasswordLength(),
                String.valueOf(complexity.getMinimumLength()));

        addPassItem(lg, row);

        addPassItem(lg,
                glang.passwordRowDouble(glang.minimumUppercaseCharacters(),
                        String.valueOf(complexity.getMinimumUppercase()), glang.minimumLowercaseCharacters(),
                        String.valueOf(complexity.getMinimumLowercase())));
        addPassItem(lg,
                glang.passwordRowDouble(glang.minimumSpecialCharacters(),
                        String.valueOf(complexity.getMinimumSpecial()), glang.minimumNumericCharacters(),
                        String.valueOf(complexity.getMinimumNumbers())));
    }

    public static void displayPassphrasePolicy(final ListGroup lg, final PassComplexityDTO dto)
    {
        ObidosMessages glang = ObidosMessages.LANG;
        lg.clear();
        final ComplexityRequirementsDTO complexity = dto.getPassphraseComplexityRequirements();
        int minEntropy = complexity.getMinimumEntropy();
        String strength = passphraseStrengthFromEntropy(minEntropy);

        addPassItem(lg, glang.passwordRowDouble(glang.passphraseStrength(), strength, glang.minimumPassphraseLength(),
                String.valueOf(complexity.getMinimumLength())));
        addPassItem(lg,
                glang.passwordRowDouble(glang.minimumUppercaseCharacters(),
                        String.valueOf(complexity.getMinimumUppercase()), glang.minimumLowercaseCharacters(),
                        String.valueOf(complexity.getMinimumLowercase())));
        addPassItem(lg,
                glang.passwordRowDouble(glang.minimumSpecialCharacters(),
                        String.valueOf(complexity.getMinimumSpecial()), glang.minimumNumericCharacters(),
                        String.valueOf(complexity.getMinimumNumbers())));
    }

    public static void changePasswordExpirationLabelColor(final FormLabel label, final int days)
    {
        if (days <= 7)
        {
            label.setColor(ObidosConstants.RED_COLOR);
        } else
        {
            label.setColor(ObidosConstants.BOOTSTRAP3_BLACK_BGCOLOR);
        }
    }

    public static void changePasswordExpirationTexts(final FormLabel label, final TextBox textBox, final int days)
    {
        ObidosMessages glang = ObidosMessages.LANG;

        ClientUtils.changePasswordExpirationLabelColor(label, days);
        if (days == ObidosConstants.PASSWORD_NEVER_EXPIRES)
        {
            label.setText(glang.passwordExpires2());
            textBox.setValue(glang.never());
        } else if (days > 0)
        {
            label.setText(glang.passwordWillExpireIn());
            String message = days + "";
            textBox.setValue(message);
        } else if (days == 0)
        {
            label.setText(glang.passwordHasExpired());
            textBox.setText(glang.today());
        } else
        {
            label.setText(glang.passwordHasExpired());
            String msg = days + " ago";
            textBox.setText(msg);
        }
    }

    public static String getLicenseExpirationDate(Long expiredEpoch)
    {
        Date date = new Date(expiredEpoch * 1000L);
        return ClientUtils.formattedDate(date);
    }

    public static boolean isLicenseOk(final LicenseStats license)
    {
        // ObidosMessages glang = ObidosMessages.LANG;
        int licenseState = license.getLicenseExpirationState();
        switch (licenseState)
        {
        case LicenseStats.LICENSE_STATE_NOMINAL:
        case LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH:
        case LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH:
        case LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY:
        {
            return true;
        }
        }
        return false;
    }

    public static boolean isLicenseOkForAdmin(final LicenseStats license)
    {
        // Bug #868
        // only NOMINAL STATE was valid for admin
        if (license.getLicenseExpirationState() == LicenseStats.LICENSE_STATE_NOMINAL
                || license.getLicenseExpirationState() == LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY)
        {
            return true;
        }
        return false;

    }

    /**
     * Pop a dialog telling why the operation can not be done and return false
     * Otherwise stay quiet and return true
     * 
     * @param currentUser
     * @return true if license allows destructive operation
     *         <p>
     * @author spgdev@spenego.com - Mar 14, 2020
     */
    public static boolean doesLicenseAllowToCreateModify(final CurrentUser currentUser, final boolean admin)
    {
        if (currentUser == null)
        {
            ClientUtils.showDialog("ERROR", "Could not check license. Could not determine logged in user");
            return false;
        }
        LicenseStats lstat = currentUser.getLoginResult().getLicenseStats();
        if (lstat == null)
        {
            myLog("License stat is null in LoginResult");
            ClientUtils.showDialog("ERROR", "Could not find license info in login result");
            return false;
        }
        int licenseState = lstat.getLicenseExpirationState();

        myLog("License state: " + licenseState);
        myLog("License state admin?: " + admin);

        if (!admin)
        {
            if (licenseState == LicenseStats.LICENSE_STATE_NOMINAL
                    || licenseState == LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY
                    || licenseState == LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH)
            {
                return true;
            }
        } else
        {
            myLog("License state admin: " + licenseState);
            if (isLicenseOkForAdmin(lstat))
            {
                return true; // Bug #868
            }
        }
        // the following method pops a dialog pops a message dialog if the
        // license is not OK
        ClientUtils.popLicenseWarningDialog(lstat);
        return false;
    }

    public static boolean doesLicenseSupportEmailNotifiation(final CurrentUser currentUser)
    {
        if (currentUser == null)
        {
            ClientUtils.showDialog("ERROR", "Could not check license. Could not determine logged in user");
            return false;
        }
        LicenseStats license = currentUser.getLoginResult().getLicenseStats();
        return doesLicenseSupportEmailNotifiation(license);
    }

    public static boolean doesLicenseSupportEmailNotifiation(final LicenseStats license)
    {
        if (license != null)
        {
            return ClientUtils.fromBoolean(license.getSupportsNotificationEmails());
        }
        return false;
    }

    public static boolean doesLicenseSupportSMSNotification(final LicenseStats license)
    {
        if (license != null)
        {
            return ClientUtils.fromBoolean(license.getSupportsSMS());
        }
        return false;
    }

    public static boolean doesLicenseSupportSMSSettings(final CurrentUser currentUser, final boolean admin)
    {
        if (currentUser == null)
        {
            ClientUtils.showDialog("ERROR", "Could not check license. Could not determine logged in user");
            return false;
        }
        LicenseStats license = currentUser.getLoginResult().getLicenseStats();
        if (license == null)
        {
            ClientUtils.showDialog("ERROR", "Could not check license. Could not determine logged in user");
            return false;
        }
        myLog("MMM MMM sms setting: " + license.getSupportsSMS());
        return ClientUtils.fromBoolean(license.getSupportsSMS());

    }

    public static boolean doesLicenseSupportAuditReport(final CurrentUser currentUser, final boolean admin)
    {
        if (currentUser == null)
        {
            ClientUtils.showDialog("ERROR", "Could not check license. Could not determine logged in user");
            return false;
        }
        LicenseStats license = currentUser.getLoginResult().getLicenseStats();
        return doesLicenseSupportAuditReport(license);
    }

    public static boolean doesLicenseSupportAuditReport(final LicenseStats license)
    {
        if (license != null)
        {
            return ClientUtils.fromBoolean(license.getSupportsAudit());
        }
        return false;
    }

    public static String getLicenseStateMessage(final int state)
    {
        ObidosMessages glang = ObidosMessages.LANG;
        switch (state)
        {
        case LicenseStats.LICENSE_STATE_NOMINAL: // quiet
        {
            return "license state is normal";
        }
        case LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY:
        {
            myLog("LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY");
            return glang.licenseWillExpireInOneMonth();
        }

        case LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH:
        {
            return glang.licenseGracePeriodFirstMonthAfterExpiry();
        }

        case LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH:
        {
            return glang.licenseGracePeriodSecondMonthAfterExpiry();
        }

        case LicenseStats.LICENSE_STATE_BEYOND_GRACE_PERIOD:
        {
            return glang.licenseBeyondGracePeriodsAfterExpiry();
        }

        default:
        {
            return "Unknown license state: " + state;
        }
        }
    }

    // pop dialog and return license state
    public static int popLicenseWarningDialog(final LicenseStats license)
    {
        ObidosMessages glang = ObidosMessages.LANG;
        int state = license.getLicenseExpirationState();
        /*
         * return delta < -SIXTY_DAYS ?
         * LicenseStats.LICENSE_STATE_BEYOND_GRACE_PERIOD : delta <
         * -THIRTY_DAYS? LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH :
         * delta < 0 ? LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH :
         * delta < THIRTY_DAYS ?
         * LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY :
         * LicenseStats.LICENSE_STATE_NOMINAL;
         */
        String message = getLicenseStateMessage(state);
        switch (state)
        {
        case LicenseStats.LICENSE_STATE_NOMINAL: // quiet
        {
            myLog("LICENSE_STATE_NOMINAL");
            break;
        }
        case LicenseStats.LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY:
        {
            myLog("LICENSE_STATE_ONE_MONTH_UNTIL_EXPIREY");
            ClientUtils.showBootboxDialog(glang.warning(), message);
            break;
        }

        case LicenseStats.LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH:
        {
            myLog("LICENSE_STATE_GRACE_PERIOD_FIRST_MONTH");
            ClientUtils.showBootboxDialog(glang.warning(), message);
            break;
        }

        case LicenseStats.LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH:
        {
            myLog("LICENSE_STATE_GRACE_PERIOD_SECOND_MONTH");
            ClientUtils.showBootboxDialog(glang.warning(), message);
            break;
        }

        case LicenseStats.LICENSE_STATE_BEYOND_GRACE_PERIOD:
        {
            myLog("LICENSE_STATE_BEYOND_GRACE_PERIOD");
            ClientUtils.showBootboxDialog(glang.error(), message);
            break;
        }

        default:
        {
            myLog("Unknown license state: " + state);
            break;
        }
        }

        return state;
    }

    public static void configureDateFormat(final DatePicker datePicker, final CurrentUser currentUser)
    {
        datePicker.setReadOnly(false);
        LoginResult loginResult = currentUser.getLoginResult();
        if (loginResult != null)
        {
            String dateFormat = loginResult.getDateFormat();
            if (dateFormat != null)
            {
                datePicker.setPlaceholder(dateFormat);
                datePicker.setFormat(dateFormat);
                datePicker.reload();
            }
        }
    }

    public static void addTextBoxKeyDownHandler(final TextBox textBox, ObidosMessageRow messageRow)
    {
        textBox.addKeyDownHandler(new KeyDownHandler()
        {

            @Override
            public void onKeyDown(KeyDownEvent event)
            {
                messageRow.showMessage(null);
            }
        });
    }

    public static void addPasswordBoxKeyDownHandler(final ObidosPasswordBox passwordBox, ObidosMessageRow messageRow)
    {
        passwordBox.addKeyDownHandler(new KeyDownHandler()
        {

            @Override
            public void onKeyDown(KeyDownEvent event)
            {
                messageRow.showMessage(null);
            }
        });
    }

    private static String makeToplevelHeading(final String heading)
    {
        StringBuilder sb = new StringBuilder(160);
        sb.append("<tr>");
        sb.append("<th style=\"color:#F45E43\"colspan=\"2\">");
        sb.append(heading);
        sb.append("</th>");
        sb.append("</tr>");
        return sb.toString();
    }

    private static String makeCertHeading(final String heading)
    {
        StringBuilder sb = new StringBuilder(160);
        sb.append("<tr>");
        sb.append("<th colspan=\"2\">");
        sb.append("&nbsp;&nbsp;&nbsp;" + heading);
        sb.append("</th>");
        sb.append("</tr>");
        return sb.toString();
    }

    private static String makeCertLabel(final String label)
    {
        StringBuilder sb = new StringBuilder(32);
        sb.append("<tr>");
        sb.append("<td align=\"right\" style=\"width:15%;font-weight:bold\">");
        sb.append(label);
        sb.append("</td>");
        return sb.toString();
    }

    private static String makeCertValue(final String value)
    {
        StringBuilder sb = new StringBuilder(32);
        sb.append("<td>");
        sb.append(value);
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    /**
     * Create table content without
     * <table>
     * and
     * </table>
     * . The caller must add
     * <table>
     * at the top and
     * </table>
     * at the end.
     * 
     * @param dto
     * @return content of table
     *         <p>
     * @author spgdev@spenego.com - Jul 3, 2020
     */
    public static String getCertificateInfo(final CertificateInfoDTO dto, final boolean decode)
    {
        StringBuilder sb = new StringBuilder(512);

        sb.append(makeToplevelHeading(dto.getSubjectCommonName()));
        sb.append(makeCertLabel("Common Name:"));
        sb.append(makeCertValue(dto.getSubjectCommonName()));

        // Issuer
        sb.append(makeCertHeading(dto.getIssuerNameHeading()));

        if (dto.getIssuerCountry() == null)
        {
            sb.append(makeCertLabel("Country:"));
            sb.append(makeCertValue("N/A"));
        } else
        {
            sb.append(makeCertLabel("Country:"));
            sb.append(makeCertValue(dto.getIssuerCountry()));
        }
        if (dto.getIssuerOrganization() == null)
        {
            sb.append(makeCertLabel("Organization:"));
            sb.append(makeCertValue("N/A"));
        } else
        {
            sb.append(makeCertLabel("Organization:"));
            sb.append(makeCertValue(dto.getIssuerOrganization()));
        }

        sb.append(makeCertLabel("Common Name:"));
        sb.append(makeCertValue(dto.getIssuerCommonName()));

        // Validity
        sb.append(makeCertHeading(dto.getValidityHeading()));
        sb.append(makeCertLabel("Not Before:"));
        sb.append(makeCertValue(dto.getNotBeforeDate().toString()));

        sb.append(makeCertLabel("Not After:"));
        sb.append(makeCertValue(dto.getNotAfterDate().toString()));

        // Subject Alt Names
        List<String> ipaAltNames = dto.getIpaAltNames();
        List<String> dnsAltNames = dto.getDnsAltNames();
        if (!ipaAltNames.isEmpty() || !dnsAltNames.isEmpty())
        {
            sb.append(makeCertHeading(dto.getSubjectAltNamesHeading()));
            for (String aname : dnsAltNames)
            {
                sb.append(makeCertLabel("DNS Name:"));
                sb.append(makeCertValue(aname));
            }
            for (String aname : ipaAltNames)
            {
                sb.append(makeCertLabel("IP Name:"));
                sb.append(makeCertValue(aname));
            }
        }

        // Public key info
        sb.append(makeCertHeading(dto.getPublicKeyInfoHeading()));
        sb.append(makeCertLabel("Algorithm:"));
        sb.append(makeCertValue(dto.getPublicKeyAlgorithm()));

        // Misc
        sb.append(makeCertHeading(dto.getMiscHeading()));

        sb.append(makeCertLabel("CA Certificate:"));
        if (dto.getIsCA() != null)
        {
            sb.append(makeCertValue("TRUE"));
        } else
        {
            sb.append(makeCertValue("FALSE"));
        }
        sb.append(makeCertLabel("Serial Number:"));
        sb.append(makeCertValue(dto.getSerialNumber()));
        sb.append(makeCertLabel("Signature Algorithm:"));
        sb.append(makeCertValue(dto.getSignatureAlgorithmString()));
        sb.append(makeCertLabel("Version:"));
        sb.append(makeCertValue(String.valueOf(dto.getVersion())));

        // don't show install date if decoding
        if (!decode)
        {
            if (dto.getInstallDate() != null)
            {
                sb.append(makeCertLabel("Installed on:"));
                sb.append(makeCertValue(dto.getInstallDate().toString()));
            }
        }

        return sb.toString();
    }

    public static boolean pemCertificateMarkersFound(final String pemChain)
    {
        if (pemChain == null || pemChain.length() == 0)
        {
            return false;
        }
        myLog(pemChain);

        myLog("B: " + pemChain.contains("-----BEGIN CERTIFICATE-----"));
        myLog("E: " + pemChain.contains("-----END CERTIFICATE-----"));

        // just a silly check for client side
        if (pemChain.contains("-----BEGIN CERTIFICATE-----") && pemChain.contains("-----END CERTIFICATE-----"))
        {
            return true;
        }
        return false;
    }

    public static boolean pemPrivateKeyMarkers(final String pemChain)
    {
        if (pemChain == null || pemChain.length() == 0)
        {
            return false;
        }
        // just a silly check for client side
        if (pemChain.contains("-----BEGIN PRIVATE KEY-----") && pemChain.contains("-----END PRIVATE KEY-----"))
        {
            return true;
        }
        return false;
    }

    private static UserDefinedTypeValueDTO getTypeValueDTO(final ItemDTO itemDTO)
    {
        List<UserDefinedTypeValueDTO> values = itemDTO.getValues();
        if (values == null)
        {
            return null;
        }
        return values.get(0);
    }

    // only one document at this time
    public static Long getDocumentId(final ItemDTO itemDTO)
    {
        UserDefinedTypeValueDTO typeValueDTO = getTypeValueDTO(itemDTO);
        if (typeValueDTO == null)
        {
            return null;
        }
        for (UserDefinedFieldValueDTO dto : typeValueDTO.getFieldValues())
        {
            if (dto.getDocument() != null)
            {
                return dto.getDocument().getId();
            }
        }
        return null;
    }

    public static boolean isFileUploadPending(final ItemDTO itemDTO)
    {
        UserDefinedTypeValueDTO typeValueDTO = getTypeValueDTO(itemDTO);
        if (typeValueDTO == null)
        {
            return false;
        }
        for (UserDefinedFieldValueDTO dto : typeValueDTO.getFieldValues())
        {
            if (dto.getDocument() != null)
            {
                return dto.getDocument().isUploadPending();
            }
        }
        return false;
    }

    public static boolean itemHasDocument(final ItemDTO itemDTO)
    {
        UserDefinedTypeValueDTO typeValueDTO = getTypeValueDTO(itemDTO);
        if (typeValueDTO == null)
        {
            return false;
        }
        for (UserDefinedFieldValueDTO dto : typeValueDTO.getFieldValues())
        {
            if (dto.getDocument() != null)
            {
                return true;
            }
        }
        return false;

    }

    public static boolean hasDocumentFieldId(final ItemDTO itemDTO)
    {
        UserDefinedTypeValueDTO typeValueDTO = getTypeValueDTO(itemDTO);
        if (typeValueDTO == null)
        {
            return false;
        }

        for (UserDefinedFieldValueDTO dto : typeValueDTO.getFieldValues())
        {
            if (dto.getType() == UserDefinedFieldDTO.TYPE_DOCUMENT)
            {
                return true;
            }
        }
        return false;
    }

    public static String getDocumentFilename(final ItemDTO itemDTO)
    {
        UserDefinedTypeValueDTO typeValueDTO = getTypeValueDTO(itemDTO);
        if (typeValueDTO == null)
        {
            return null;
        }

        for (UserDefinedFieldValueDTO dto : typeValueDTO.getFieldValues())
        {
            DocumentDTO docDTO = dto.getDocument();
            if (docDTO != null)
            {
                return docDTO.getFilenameString();
            }
        }
        return null;

    }

    public static String trimString(final String str, final int len)
    {
        if (str == null)
        {
            return null;
        }
        return str.substring(0, Math.min(str.length(), len));
    }

    public static String trimStringEllipsis(final String str, final int len)
    {
        if (str == null)
        {
            return null;
        }
        int slen = str.length();
        String ts = trimString(str, len);
        if (len < slen)
        {
            ts = ts + " ...";
            return ts;
        }
        return ts;
    }

    // if filename is longer than 25, add ... after 12 characters
    // and last 12 characters after the ellipsis
    public static String shortenFilenameOld(final String filename)
    {
        if (filename == null)
        {
            return filename;
        }
        final int maxLen = 25;
        final int len = filename.length();
        if (len <= maxLen)
        {
            return filename;
        }
        String firstPart = filename.substring(0, 12);
        String lastPart = filename.substring((len - 12), len);
        return firstPart + "..." + lastPart;
    }

    // Bug #74
    // This method is written by Claude AI 3.5 Sonnet (New) Nov-24 6:30PM
    // There is a class ClientUtilsTest.java for testing
    public static String shortenFilename(final String filename, final int maxLength)
    {
        if (filename == null || filename.isEmpty())
        {
            return filename;
        }

        // Default max length if not specified
        final int targetLength = maxLength <= 0 ? 40 : maxLength;

        // If filename is already shorter than max length, return as is
        if (filename.length() <= targetLength)
        {
            return filename;
        }

        // Handle file extension
        String name = filename;
        String extension = "";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0)
        {
            name = filename.substring(0, lastDotIndex);
            extension = filename.substring(lastDotIndex);
        }

        // If extension alone is too long, truncate the extension
        if (extension.length() > targetLength / 3)
        {
            extension = extension.substring(0, Math.max(4, targetLength / 4));
        }

        // Calculate remaining space for the name
        int remainingLength = targetLength - extension.length() - 3; // 3 for
                                                                     // "..."

        // Find word boundaries for more natural truncation
        int firstPartEnd = findWordBoundary(name, remainingLength / 2, true);
        int lastPartStart = findWordBoundary(name, name.length() - (remainingLength / 2), false);

        String firstPart = name.substring(0, firstPartEnd);
        String lastPart = name.substring(lastPartStart);

        return firstPart + "..." + lastPart + extension;
    }

    private static int findWordBoundary(String text, int targetIndex, boolean forward)
    {
        // Common file name separators
        char[] separators =
        { ' ', '_', '-', '/' };

        if (forward)
        {
            // Search forward for a word boundary
            for (int i = targetIndex; i > 0; i--)
            {
                char c = text.charAt(i);
                for (char separator : separators)
                {
                    if (c == separator)
                    {
                        return i + 1;
                    }
                }
            }
            return Math.min(targetIndex, text.length());
        } else
        {
            // Search backward for a word boundary
            for (int i = targetIndex; i < text.length(); i++)
            {
                char c = text.charAt(i);
                for (char separator : separators)
                {
                    if (c == separator)
                    {
                        return i + 1;
                    }
                }
            }
            return Math.max(targetIndex, 0);
        }
    }

    public static String shortenString(final String str)
    {
        return shortenFilename(str, ObidosConstants.SHORTEN_STRING_AFTER_LENGTH);
    }

    // what is Upload or Download - needs to print error message
    public static void addFormPaneUploadDownloadHandlers(final FormPanel formPanel, final ObidosMessageRow messageRow,
            final String what)
    {
        formPanel.addSubmitHandler(new SubmitHandler()
        {

            @Override
            public void onSubmit(SubmitEvent event)
            {
            }
        });

        formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler()
        {

            @Override
            public void onSubmitComplete(SubmitCompleteEvent event)
            {
                String res = event.getResults();

                // FF returns <pre></pre> if there is no error
                if (res != null && res.length() > 0 && !"<pre></pre>".equals(res))
                {
                    myLog("XXX error uploading file" + res + " res.length: " + res.length());
                    showErrorMessage(messageRow, "Could not " + what + " file: " + res);
                } else
                {
                    myLog("XXX File uploaded successfully");
                    showMessage(messageRow, "File " + what + " succeded");
                }
            }
        });
    }

    public static void extendSession(final PlaceManager placeManager)
    {
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(placeManager)
        {

            @Override
            public void uponSuccess(Void result)
            {
                myLog("XXX Session refreshed successfully");
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                myLog("XXX Could not refresh session: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        LoginService.Utility.getInstance().refreshSession(authCreds, callback);
    }

    // just create the Modal
    // it is caller's responsibility to cache the modal, show or hide it
    public static Modal createFileUploadModal(final PlaceManager placeManager)
    {
        ObidosMessages lang = ObidosMessages.LANG;
        Modal modal = new Modal();
        modal.setTitle(lang.uploadProgressTitle());
        modal.setClosable(false);
        modal.setRemoveOnHide(true);
        modal.setFade(true);
        modal.setDataBackdrop(ModalBackdrop.STATIC);
        modal.setDataKeyboard(true);

        String html = lang.uploadProgressMessage();
        Span span = new Span(html);
        ModalBody body = new ModalBody();
        body.add(span);

        ModalFooter footer = new ModalFooter();
        Button uploadingButton = new Button();
        uploadingButton.setType(ButtonType.LINK);
        uploadingButton.setIcon(IconType.SPINNER);
        uploadingButton.setIconSpin(true);
        // set cursor to default arrow cursor
        uploadingButton.getElement().getStyle().setCursor(Cursor.DEFAULT);
        footer.add(uploadingButton);

        Button cancelButton = new Button();
        cancelButton.setText(glang.cancelUpload());
        cancelButton.setType(ButtonType.DANGER);
        footer.add(cancelButton);

        cancelButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                modal.hide();
                ClientUtils.showPage(placeManager, NameTokens.LIST_ALL_MY_ITEMS);
            }
        });

        modal.add(body);
        modal.add(footer);
        return modal;
    }

    public static boolean isBanglaEditingEnabled()
    {
        String cookie = Cookies.getCookie(ObidosConstants.OBIDOS_EBE_COOKIE);
        myLog("BEC: " + cookie);
        if (cookie == null || "undefined".equals(cookie))
        {
            myLog("BEC: cookie is undefined, return false");
            return false;
        }
        if (cookie != null && "yes".equals(cookie))
        {
            return true;
        }
        return false;
    }

    public static boolean isLanguageChangeEasterEggEnabled()
    {
        String cookie = Cookies.getCookie(ObidosConstants.OBIDOS_LANGUAGE_COOKIE);
        if (cookie != null && "yes".equals(cookie))
        {
            return true;
        }
        return false;

    }

    public static native void nativeDisableBanglaEditing() /*-{
		$wnd.$('.note-editable').bangla('off');
		$wnd.$('input[type="text"]').bangla('off');
	}-*/;

    public static native void nativeEnableBanglaEditing() /*-{
		$wnd.$('input[type="text"]').bangla('on');
		$wnd.$('.note-editable').bangla('on');
	}-*/;

    // callback for checkbox
    public static void toggleBanglaEditingCheckBox(final CheckBox cbox)
    {
        cbox.setValue(true);
        if (cbox.getText().equals("বাংলা"))
        {
            cbox.setText("English");
            myLog("XX disable Bangla");
            nativeDisableBanglaEditing();
        } else if (cbox.getText().equals("English"))
        {
            myLog("XX enable Bangla");
            cbox.setText("বাংলা");
            nativeEnableBanglaEditing();
        }
    }

    // UI calls this
    public static void enableBanglaEditing(final boolean enable, final Row banglaRow)
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                if (ClientUtils.isBanglaEditingEnabled())
                {
                    myLog("XX enable bangla");
                    banglaRow.setVisible(true);
                    if (enable)
                    {
                        nativeEnableBanglaEditing();
                    } else
                    {
                        nativeDisableBanglaEditing();
                    }
                }
            }
        });
    }

    // called from onHide()
    public static void resetLanguage(final ObidosRowBottom2px languageRow)
    {
        boolean e = isBanglaEditingEnabled();
        languageRow.setVisible(e);
        if (e)
        {
            nativeDisableBanglaEditing();
        }
    }

    // Remove margin-bottom from summernote, I need to put a tiny button
    // below the summernote, but the panel had 20px margin and the button
    // looks out of place. I need to change the bottom margin to 2px

    // This solution is given by ChatGPT-4o
    // How in the world it came up with this solution at 2nd try and it
    // actually works?? wow!!
    // I was certain it will not be able to give the solution as the technology
    // is so old.
    // I only gave it the following div and it figured out the rest.
    // Jun-6-2024
    public static void setSummernoteBottomMargin(String npx)
    {
        NodeList<Element> elements = RootPanel.get().getElement().getElementsByTagName("div");
        for (int i = 0; i < elements.getLength(); i++)
        {
            Element div = elements.getItem(i);
            if (div.hasClassName("note-editor") && div.hasClassName("note-frame") && div.hasClassName("panel")
                    && div.hasClassName("panel-default"))
            {
                div.getStyle().setProperty("marginBottom", npx); // Set the
                                                                 // margin-bottom
                                                                 // to 2px
            }
        }
    }

    // called from onReset()
    // show language input option if Bangla is enabled
    public static void showLanguageListBox(final ObidosRowBottom2px languageRow, final ListBox languageListBox)
    {
        boolean e = isBanglaEditingEnabled();
        languageRow.setVisible(e);
        if (e)
        {
            languageListBox.setSelectedIndex(1); // select বাংলা 
        }
    }

    public static void showLanguageSwitchSelect(final ObidosRowTop2px changeLanguageRow,
            final Select languageChangeSelect, final PanelBody panelBody)
    {
        boolean e = isLanguageChangeEasterEggEnabled();
        changeLanguageRow.setVisible(e);
        int selectHeight = languageChangeSelect.getOffsetHeight();
        int estimatedDropdownHeight = selectHeight * 6; // Adjust this
                                                        // multiplier as needed
        int buffer = 20;
        panelBody.setMarginBottom(estimatedDropdownHeight + buffer);
    }

    // If the content is not enough in the panel, the select widget gets cut off
    // at the
    // bottom of the panel body. Programatically adjust the length. This kind of
    // issues
    // can be seen in console presenter, password generation presenter diceware
    // number of
    // dice roll selection etc.
    public static void adjustSelectWidgetLength(final PanelBody panelBody, final Select select)
    {
        int selectHeight = select.getOffsetHeight();
        if (selectHeight == 0)
        {
            selectHeight = 40;
        }
        myLog("MMM select heigth: " + selectHeight);
        int estimatedDropdownHeight = selectHeight * 6; // Adjust this
                                                        // multiplier as needed
        int buffer = 20;
        panelBody.setMarginBottom(estimatedDropdownHeight + buffer);

    }

    // called from onReset()
    // show copy button if Bangla is enabled
    public static void showCopyToClipbaordButtom(final Button copyToClipboardButton)
    {
        copyToClipboardButton.setVisible(isBanglaEditingEnabled());
    }

    public static void showClearButton(final Button clearButton)
    {
        clearButton.setVisible(isBanglaEditingEnabled());
    }

    // ara
    public static boolean isValidPort(int port)
    {
        return port > 0 && port <= 65535;
    }

    // Method to extract dial code from a string
    // Cannot use in client side
    /*
     * public static String extractDialCode(String input) { // Regular
     * expression to match the dial code within parentheses String regex =
     * "\\(\\+([0-9]+)\\)"; Pattern pattern = Pattern.compile(regex); Matcher
     * matcher = pattern.matcher(input);
     * 
     * // If the pattern matches, return the dial code if (matcher.find()) {
     * return matcher.group(1); }
     * 
     * // Return null if no dial code is found return null; }
     */
    // Method to extract dial code from a string
    // By ChatGPT 4o, Jun-30-2024
    public static String extractDialCode(String input)
    {
        // Regular expression to match the dial code within parentheses
        String regex = "\\(\\+([0-9]+)\\)";
        RegExp regExp = RegExp.compile(regex);
        MatchResult matcher = regExp.exec(input);

        // If the pattern matches, return the dial code
        if (matcher != null)
        {
            return "+" + matcher.getGroup(1);
        }

        // Return null if no dial code is found
        return null;
    }

    /**
     * Removes the country code from a phone number.
     * 
     * @param phoneNumber
     *            The full phone number including country code. The country code
     *            is at the beginning of the phoneNumber without +
     * @param countryCode
     *            The country code to remove. e.g +1, +405 etc
     * @return The phone number without the country code
     */
    public static String removeCountryCode(String phoneNumber, String countryCode)
    {
        // First, remove any non-digit characters
        String cleanNumber = phoneNumber.replaceAll("\\D", "");
        String cleanCountryCode = countryCode.replaceAll("\\D", "");

        // Check if the number starts with the country code
        if (cleanNumber.startsWith(cleanCountryCode))
        {
            return cleanNumber.substring(cleanCountryCode.length());
        } else
        {
            // If the number doesn't start with the country code, return the
            // original number
            return cleanNumber;
        }
    }

    public static void updateCountryCodeSelectList(final Select countryCodeSelect, List<String> countryCodes)
    {
        countryCodeSelect.clear();
        if (countryCodes == null)
        {
            return;
        }
        for (String countryCode : countryCodes)
        {
            Option option = new Option();
            option.setText(countryCode);
            // gwtLog("MMM: " + countryCode);
            countryCodeSelect.add(option);
        }
        countryCodeSelect.refresh();
    }

    public static void updateCountyCodeSelectListWithMap(final Select countryCodeSelect,
            final Map<String, CountryCodeDTO> countryCodesMap)
    {
        countryCodeSelect.clear();
        if (countryCodesMap == null)
        {
            return;
        }

        for (Map.Entry<String, CountryCodeDTO> entry : countryCodesMap.entrySet())
        {
            String countryCodeLine = entry.getKey();
            Option option = new Option();
            option.setText(countryCodeLine);
            countryCodeSelect.add(option);
        }
        countryCodeSelect.refresh();
    }

    public static void updateCountryCodeSelectList(final UserDTO dto, final TextBox mobileTextBox,
            final Select countryCodeSelect)
    {
        Map<String, CountryCodeDTO> countryCodesMap = dto.getCountryCodesMap();
        ClientUtils.updateCountyCodeSelectListWithMap(countryCodeSelect, countryCodesMap);
        String mobilePhone = dto.getMobile1();
        mobileTextBox.clear();
        if (mobilePhone != null)
        {
            mobileTextBox.setValue(mobilePhone);
        } else
        {
            countryCodeSelect.setValue(null);
            mobileTextBox.setPlaceholder(glang.mobilePhoneWithoutCountryCode());
        }

        String countryNameAndCode = dto.getCountryNameAndCode();
        if (countryNameAndCode != null)
        {
            // select the item
            countryCodeSelect.setValue(countryNameAndCode);
        }
    }

    // set the natational formatted example phonenumber as placeholder
    // for the text box
    public static void countryCodeSelectCallback(final TextBox mobilePhoneTextBox, final Select select,
            Map<String, CountryCodeDTO> countryCodeMap)
    {
        String line = select.getSelectedItem().getText();
        if (countryCodeMap != null)
        {
            CountryCodeDTO ccDTO = countryCodeMap.get(line);
            if (ccDTO != null)
            {
                String exampleNumber = ccDTO.getExampleNumber();
                if (exampleNumber != null && exampleNumber.length() > 0)
                {
                    mobilePhoneTextBox.setPlaceholder(exampleNumber);
                } else
                {
                    mobilePhoneTextBox.setPlaceholder(glang.mobilePhoneWithoutCountryCode());
                }
            } else
            {
                mobilePhoneTextBox.setPlaceholder(glang.mobilePhoneWithoutCountryCode());
            }
        }
    }

    public static void setPasswordStrengthProgressBar(final int adjustedEntropy, final ProgressBar bar)
    {
        if (adjustedEntropy <= 19) // Weak
        {
            bar.setPercent(20);
            bar.setText(glang.strengthWeak());
            bar.setType(ProgressBarType.DANGER);

        } else if (adjustedEntropy > 19 && adjustedEntropy <= 24)
        {
            bar.setPercent(20);
            bar.setText(glang.strengthModerate());
            bar.setType(ProgressBarType.DANGER);
        } else if (adjustedEntropy > 24 && adjustedEntropy <= 29)
        {
            bar.setPercent(80);
            bar.setText(glang.strengthStrong());
            bar.setType(ProgressBarType.INFO);
        } else if (adjustedEntropy >= 30)
        {
            bar.setPercent(100);
            bar.setText(glang.strengthVeryStrong());
            bar.setType(ProgressBarType.SUCCESS);
        }
    }

    public static void updateURLAndRefresh(String locale)
    {
        String currentURL = Window.Location.getHref();
        String newURL;

        if (currentURL.contains("?"))
        {
            // URL already has parameters
            if (currentURL.contains("locale="))
            {
                // Replace existing locale parameter
                newURL = currentURL.replaceFirst("locale=[^&]*", "locale=" + locale);
            } else
            {
                // Add locale parameter
                newURL = currentURL + "&locale=" + locale;
            }
        } else
        {
            // Add locale as the first parameter
            String hash = Window.Location.getHash();
            newURL = currentURL.replace(hash, "") + "?locale=" + locale + hash;
        }

        // Reload the page with the new URL
        Window.Location.assign(newURL);
    }

    public static void setDefaultThumbnailImage(final Image image)
    {
        image.setUrl("128x128.png");
    }

    public static boolean isLocaleArabicOld()
    {
        String browserLanguage = getBrowserLanguage();
        myLog("MMM browser language: " + browserLanguage);
        String currentURL = Window.Location.getHref();
        if (currentURL.contains("locale=ar"))
        {
            return true;
        }
        return false;
    }

    public static boolean isLocaleArabic()
    {
        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        // use startWith because there are several arabic locales e.g.
        /*
         * ar (Standard Arabic) ar-EG (Egyptian Arabic) ar-SA (Saudi Arabian
         * Arabic) ar-AE (UAE) ar-DZ (Algerian Arabic)
         */
        return currentLocale.startsWith("ar");
    }

    public static String getBrowserLanguage()
    {
        return getBrowserLanguageNative();
    }

    private static native String getBrowserLanguageNative() /*-{
	    return navigator.language || navigator.userLanguage;
	}-*/;

    // Issue #257
    public static native void activatejQueryTooltips() /*-{
		$wnd.jQuery("[data-toggle='tooltip']").tooltip();
	}-*/;

    public static native boolean isjQueryInjected() /*-{
		return !(typeof $wnd.jQuery === "undefined") && !(null === $wnd.jQuery);
	}-*/;

    public native void focusNext(NativeEvent event)/*-{
		var inputs = $wnd.$(':input:visible');
		inputs.eq(inputs.index(event.target) + 1).focus();
	}-*/;

    public static native void enableBangla() /*-{
    	$wnd.$('input[type="text"]').bangla('on');
		$wnd.$(".note-editable").bangla('on');
	}-*/;

    public static native void disableBangla() /*-{
    	$wnd.$('input[type="text"]').bangla('on');
		$wnd.$(".note-editable").bangla('on');
	}-*/;

    public static native void toggleBangla() /*-{
    	$wnd.$('input[type="text"').bangla('toggle');
    	$wnd.$('.note-editable').bangla('toggle');
    }-*/;

    // ref: https://codepen.io/damianocel/pen/Yxxzdj
    // https://stackoverflow.com/questions/10338704/javascript-to-detect-if-user-changes-tab
    public static native boolean windowHasFocus() /*-{
		if ($doc.hasFocus() ) {
			return true;
		}
		return false;
	}-*/;

    // if window/tab is not visible, return false, true otherwise
    public static native boolean windowisVisible() /*-{
		if (document.visibilityState == "visible") {
			return true;
		}
		return false;
	}-*/;

    // For that piece of crap called IE
    public static native int ieWidth(Element elt) /*-{
		return elt.naturalWidth;
	}-*/;

    public static native int ieHeight(Element elt) /*-{
		return elt.naturalHeight;
	}-*/;

    public native void saveBase64AsFile(String base64, String fileName) /*-{

		var link = document.createElement("a");

		link.setAttribute("href", base64);
		link.download
		link.setAttribute("download", fileName);
		link.click();
	}-*/;

    // https://code.tutsplus.com/tutorials/creating-an-image-editor-using-camanjs-applying-basic-filters--cms-30251
    public native void download(String dataUrl, String fileName) /*-{
		console.log("hi");

		var e;
		var lnk = document.createElement("a");

		var fileExtension = fileName.slice(-4);
		console.log('file extension:' + fileExtension);

		if (fileExtension == ".jpg" || fileExtension == ".png"
				|| fileExtension == "jpeg") {
			var actualName = fileName.substring(0, fileName.length - 4);
			console.log("actual name: " + actualName);
		}
		console.log("actual name: " + actualName);
		actualName = actualName + "-edited.jpg";

		lnk.href = dataUrl;
		lnk.download = actualName;
		//		  lnk.click();

		if (document.createEvent) {
			e = document.createEvent("MouseEvents");
			e.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false,
					false, false, false, 0, null);
			lnk.dispatchEvent(e);
		} else if (lnk.fireEvent) {
			lnk.fireEvent("onclick");
		}
	}-*/;

    // works on Chrome, FF and Safari
    public static native void copyTextToClipboard(String text) /*-{
		$wnd.navigator.clipboard.writeText(text);
	}-*/;

    public static native void clearClipboard() /*-{
		$wnd.navigator.clipboard.writeText("");
	}-*/;

    public static native boolean isJQueryLoaded() /*-{
    	return (typeof $wnd['jQuery'] !== 'undefined');
	}-*/;

}
