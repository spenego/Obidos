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

package com.spenego.Obidos.client.application.widgets.bootbox.options;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2016 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.core.client.JavaScriptObject;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.PromptCallback;

/**
 * Prompt options.
 *
 * @author Xiaodong Sun
 */
public class PromptOptions extends DialogOptions {

    /**
     *
     */
    protected PromptOptions() {}

    /**
     * Creates a new {@link PromptOptions}.
     *
     * @param message
     * @return
     */
    public static final PromptOptions newOptions(final String message) {
        PromptOptions options = JavaScriptObject.createObject().cast();
        options.setMessage(message);
        options.setCallback(PromptCallback.DEFAULT_PROMPT_CALLBACK);
        return options;
    }

    public final native void setCallback(PromptCallback callback) /*-{
        this.callback = function(result) {
            callback.@com.spenego.Obidos.client.application.widgets.bootbox.callback.PromptCallback::callback(Ljava/lang/String;)(result);
        };
    }-*/;
}
