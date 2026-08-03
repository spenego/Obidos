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

package com.spenego.Obidos.client.application.widgets;

import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.base.HasPlaceholder;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.util.ClientUtils;

// adapted from: https://groups.google.com/forum/#!topic/google-web-toolkit/TAsGLJPn1UQ
// which is for GWT.  by Julio Feferman
// I am using gwtbootstrap3 and add to add initialize in constructor to be used in ui binder.
// Also use i18n strings.
// spgdev@spenego.com, May-25-20189
public class ObidosIntegerTextBox extends IntegerBox implements HasPlaceholder
{
    public ObidosIntegerTextBox()
    {
        super();
        initialize();
    }

    public void initialize()
    {
    	// remove thousand comma delimiter
    	// https://stackoverflow.com/questions/15978638/longbox-in-gwt-remove-thousand-delimiter-formatting
    	changeCachedDecimalFormat(NumberFormat.getFormat("######0"));
        addKeyDownHandler(new KeyDownHandler()
        {
            public void onKeyDown(KeyDownEvent event)
            {
                int keyCode = event.getNativeKeyCode();
                switch (keyCode)
                {
                    case KeyCodes.KEY_LEFT:
                    case KeyCodes.KEY_RIGHT:
                    case KeyCodes.KEY_DELETE:
                    case KeyCodes.KEY_BACKSPACE:
                    case KeyCodes.KEY_ENTER:
                    case KeyCodes.KEY_ESCAPE:
                    case KeyCodes.KEY_TAB:
                    return;
                }

                String parsedInput = parseNumberKey(keyCode);
                if (parsedInput.length() > 0)
                {
                    return;
                } else
                {
                    cancelKey();
                }

            }
        });
    }
    
    public Integer getValue()
    {
    	return ClientUtils.fromInteger(super.getValue());
    }

    private String parseNumberKey(int keyCode)
    {
        String result = new String();

        switch (keyCode)
        {
            case KeyCodes.KEY_ZERO:
            case KeyCodes.KEY_ONE:
            case KeyCodes.KEY_TWO:
            case KeyCodes.KEY_THREE:
            case KeyCodes.KEY_FOUR:
            case KeyCodes.KEY_FIVE:
            case KeyCodes.KEY_SIX:
            case KeyCodes.KEY_SEVEN:
            case KeyCodes.KEY_EIGHT:
            case KeyCodes.KEY_NINE:
                return result = String.valueOf((char) keyCode);
            case KeyCodes.KEY_NUM_ZERO:
                return result = ObidosMessages.LANG.zero();
            case KeyCodes.KEY_NUM_ONE:
                return result = ObidosMessages.LANG.one();
            case KeyCodes.KEY_NUM_TWO:
                return result = ObidosMessages.LANG.two();
            case KeyCodes.KEY_NUM_THREE:
                return result = ObidosMessages.LANG.three();
            case KeyCodes.KEY_NUM_FOUR:
                return result = ObidosMessages.LANG.four();
            case KeyCodes.KEY_NUM_FIVE:
                return result = ObidosMessages.LANG.five();
            case KeyCodes.KEY_NUM_SIX:
                return result =  ObidosMessages.LANG.six();
            case KeyCodes.KEY_NUM_SEVEN:
                return result = ObidosMessages.LANG.seven();
            case KeyCodes.KEY_NUM_EIGHT:
                return result = ObidosMessages.LANG.eight();
            case KeyCodes.KEY_NUM_NINE:
                return result = ObidosMessages.LANG.nine();
        }
        return result;
    }
    private static native void changeCachedDecimalFormat(NumberFormat f) /*-{
    	@com.google.gwt.i18n.client.NumberFormat::cachedDecimalFormat = f;
  	}-*/;
}
