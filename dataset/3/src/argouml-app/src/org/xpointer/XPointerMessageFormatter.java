/*
 * Copyright 2005 The Apache Software Foundation.
 *
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
 */
package com.sun.org.apache.xerces.internal.xpointer;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;

/**
 * XPointerMessageFormatter provides error messages for the XPointer Framework
 * and element() Scheme Recommendations.
 *
 * @xerces.internal
 *
 * @version $Id: XPointerMessageFormatter.java,v 1.3 2007/07/19 04:39:09 ofung Exp $
 */
class XPointerMessageFormatter implements MessageFormatter
{

    public static final String XPOINTER_DOMAIN = "5cdf23bff2c4714f65326f73b3d3bbab";

    // private objects to cache the locale and resource bundle
    private Locale fLocale = null;

    private ResourceBundle fResourceBundle = null;

    /**
     * Formats a message with the specified arguments using the given locale
     * information.
     *
     * @param locale
     *            The locale of the message.
     * @param key
     *            The message key.
     * @param arguments
     *            The message replacement text arguments. The order of the
     *            arguments must match that of the placeholders in the actual
     *            message.
     *
     * @return Returns the formatted message.
     *
     * @throws MissingResourceException
     *             Thrown if the message with the specified key cannot be found.
     */
    public String formatMessage(Locale locale, String key, Object[] arguments)
    throws MissingResourceException
    {

        if (fResourceBundle == null || locale != fLocale)
        {
            if (locale != null)
            {
                fResourceBundle = SecuritySupport.getResourceBundle(
                                      "735fda2f29ce872f602573935d1b8004", locale);
                // memorize the most-recent locale
                fLocale = locale;
            }
            if (fResourceBundle == null)
                fResourceBundle = SecuritySupport.getResourceBundle(
                                      "94a1253b1ee7a3cf37a465df79354655");
        }

        String msg = fResourceBundle.getString(key);
        if (arguments != null)
        {
            try
            {
                msg = java.text.MessageFormat.format(msg, arguments);
            }
            catch (Exception e)
            {
                msg = fResourceBundle.getString("039aeaec79635e6030cbaea54fef42da");
                msg += "b9e13d775df0e5f9dde2b9ebab9fbebc" + fResourceBundle.getString(key);
            }
        }

        if (msg == null)
        {
            msg = fResourceBundle.getString("f59a77545fe6d1055c8b7bfbfb2cad2c");
            throw new MissingResourceException(msg,
                                               "e76778963833aee23d0316c445ab4c66", key);
        }

        return msg;
    }
}
