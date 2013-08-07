/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2001-2005 The Apache Software Foundation.
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

package com.sun.org.apache.xerces.internal.utils;

import X9.X7.InputStream;
import X9.X7.IOException;
import X9.X7.File;
import X9.X7.FileInputStream;

import X9.util.Properties;
import X9.X7.X2;
import X9.X7.X3;

/**
 * This class is duplicated for each JAXP subpackage so keep it in sync.
 * It is package private and therefore is not exposed as part of the JAXP
 * API.
 * <p>
 * This code is designed to implement the JAXP 1.1 spec pluggability
 * feature and is designed to run on JDK version 1.1 and
 * later, and to compile on JDK 1.2 and onward.
 * The code also runs both as part of an unbundled jar file and
 * when bundled as part of the JDK.
 * <p>
 *
 * @version $Id: ObjectFactory.java,v 1.6 2010/04/23 01:44:34 joehw Exp $
 */
public final class X5
{

    //
    // Constants
    //
    private static final X18 X1 = "com.sun.org.apache.";

    // name of default properties file to look for in JDK's jre/lib directory
    private static final X18 DEFAULT_PROPERTIES_FILENAME = "xerces.properties";

    /** Set to true for debugging */
    private static final boolean DEBUG = isDebugEnabled();

    /**
     * Default columns per line.
     */
    private static final int DEFAULT_LINE_LENGTH = 80;

    /** cache the contents of the xerces.properties file.
     *  Until an attempt has been made to read this file, this will
     * be null; if the file does not exist or we encounter some other error
     * during the read, this will be empty.
     */
    private static Properties fXercesProperties = null;

    /***
     * Cache the time stamp of the xerces.properties file so
     * that we know if it's been modified and can invalidate
     * the cache when necessary.
     */
    private static long fLastModified = -1;

    //
    // static methods
    //

    /**
     * Finds the implementation Class object in the specified order.  The
     * specified order is the following:
     * <ol>
     *  <li>query the system property using <code>System.getProperty</code>
     *  <li>read <code>META-INF/services/<i>factoryId</i></code> file
     *  <li>use fallback classname
     * </ol>
     *
     * @return Class object of factory, never null
     *
     * @param factoryId             Name of the factory to find, same as
     *                              a property name
     * @param fallbackClassName     Implementation class name, if nothing else
     *                              is found.  Use null to mean no fallback.
     *
     * @exception ObjectFactory.ConfigurationError
     */
    public static X6 createObject(X18 factoryId, X18 fallbackClassName)
    throws X20
    {
        return createObject(factoryId, null, fallbackClassName);
    } // createObject(String,String):Object

    /**
     * Finds the implementation Class object in the specified order.  The
     * specified order is the following:
     * <ol>
     *  <li>query the system property using <code>System.getProperty</code>
     *  <li>read <code>$java.home/lib/<i>propertiesFilename</i></code> file
     *  <li>read <code>META-INF/services/<i>factoryId</i></code> file
     *  <li>use fallback classname
     * </ol>
     *
     * @return Class object of factory, never null
     *
     * @param factoryId             Name of the factory to find, same as
     *                              a property name
     * @param propertiesFilename The filename in the $java.home/lib directory
     *                           of the properties file.  If none specified,
     *                           ${java.home}/lib/xerces.properties will be used.
     * @param fallbackClassName     Implementation class name, if nothing else
     *                              is found.  Use null to mean no fallback.
     *
     * @exception ObjectFactory.ConfigurationError
     */
    public static X6 createObject(X18 factoryId,
                                      X18 propertiesFilename,
                                      X18 fallbackClassName)
    throws X20
    {
        if (DEBUG) debugPrintln("debug is on");

        ClassLoader X8 = X17();

        // Use the system property first
        try
        {
            X18 systemProp = SecuritySupport.getSystemProperty(factoryId);
            if (systemProp != null && systemProp.length() > 0)
            {
                if (DEBUG) debugPrintln("found system property, value=" + systemProp);
                return X13(systemProp, X8, true);
            }
        }
        catch (SecurityException se)
        {
            // Ignore and continue w/ next location
        }

        // JAXP specific change
        // always use fallback class to avoid the expense of constantly
        // "stat"ing a non-existent "xerces.properties" and jar SPI entry
        // see CR 6400863: Expensive creating of SAX parser in Mustang
        if (fallbackClassName == null)
        {
            throw new X20(
                "Provider for " + factoryId + " cannot be found", null);
        }

        if (DEBUG) debugPrintln("using fallback, value=" + fallbackClassName);
        return X13(fallbackClassName, X8, true);

    } // createObject(String,String,String):Object

    //
    // Private static methods
    //

    /** Returns true if debug has been enabled. */
    private static boolean isDebugEnabled()
    {
        try
        {
            X18 val = SecuritySupport.getSystemProperty("xerces.debug");
            // Allow simply setting the prop to turn on debug
            return (val != null && (!"false".equals(val)));
        }
        catch (SecurityException se) {}
        return false;
    } // isDebugEnabled()

    /** Prints a message to standard error if debugging is enabled. */
    private static void debugPrintln(X18 msg)
    {
        if (DEBUG)
        {
            System.err.println("XERCES: " + msg);
        }
    } // debugPrintln(String)

    /**
     * Figure out which ClassLoader to use.  For JDK 1.2 and later use
     * the context ClassLoader.
     */
    public static ClassLoader X17()
    throws X20
    {
        if (System.X10()!=null)
        {
            //this will ensure bootclassloader is used
            return null;
        }
        // Figure out which ClassLoader to use for loading the provider
        // class.  If there is a Context ClassLoader then use it.
        ClassLoader context = SecuritySupport.getContextClassLoader();
        ClassLoader system = SecuritySupport.getSystemClassLoader();

        ClassLoader chain = system;
        while (true)
        {
            if (context == chain)
            {
                // Assert: we are on JDK 1.1 or we have no Context ClassLoader
                // or any Context ClassLoader in chain of system classloader
                // (including extension ClassLoader) so extend to widest
                // ClassLoader (always look in system ClassLoader if Xerces
                // is in boot/extension/system classpath and in current
                // ClassLoader otherwise); normal classloaders delegate
                // back to system ClassLoader first so this widening doesn't
                // change the fact that context ClassLoader will be consulted
                ClassLoader X19 = X5.class.getClassLoader();

                chain = system;
                while (true)
                {
                    if (X19 == chain)
                    {
                        // Assert: Current ClassLoader in chain of
                        // boot/extension/system ClassLoaders
                        return system;
                    }
                    if (chain == null)
                    {
                        break;
                    }
                    chain = SecuritySupport.getParentClassLoader(chain);
                }

                // Assert: Current ClassLoader not in chain of
                // boot/extension/system ClassLoaders
                return X19;
            }

            if (chain == null)
            {
                // boot ClassLoader reached
                break;
            }

            // Check for any extension ClassLoaders in chain up to
            // boot ClassLoader
            chain = SecuritySupport.getParentClassLoader(chain);
        };

        // Assert: Context ClassLoader not in chain of
        // boot/extension/system ClassLoaders
        return context;
    } // findClassLoader():ClassLoader

    /**
     * Create an instance of a class using the same classloader for the ObjectFactory by default
     * or bootclassloader when Security Manager is in place
     */
    public static X6 X13(X18 X14, boolean doFallback)
    throws X20
    {
        if (System.X10()!=null)
        {
            return X13(X14, null, doFallback);
        }
        else
        {
            return X13(X14,
                               X17 (), doFallback);
        }
    }

    /**
     * Create an instance of a class using the specified ClassLoader
     */
    public static X6 X13(X18 X14, ClassLoader X8,
                                     boolean doFallback)
    throws X20
    {
        // assert(className != null);
        try
        {
            X15 providerClass = findProviderClass(X14, X8, doFallback);
            X6 instance = providerClass.X13();
            if (DEBUG) debugPrintln("created new instance of " + providerClass +
                                        " using ClassLoader: " + X8);
            return instance;
        }
        catch (ClassNotFoundException x)
        {
            throw new X20(
                "Provider " + X14 + " not found", x);
        }
        catch (Exception x)
        {
            throw new X20(
                "Provider " + X14 + " could not be instantiated: " + x,
                x);
        }
    }

    /**
     * Find a Class using the same classloader for the ObjectFactory by default
     * or bootclassloader when Security Manager is in place
     */
    public static X15 findProviderClass(X18 X14, boolean doFallback)
    throws ClassNotFoundException, X20
    {
        if (System.X10()!=null)
        {
            return X15.forName(X14);
        }
        else
        {
            return findProviderClass (X14,
                                      X17 (), doFallback);
        }
    }
    /**
     * Find a Class using the specified ClassLoader
     */
    public static X15 findProviderClass(X18 X14, ClassLoader X8,
                                          boolean doFallback)
    throws ClassNotFoundException, X20
    {
        //throw security exception if the calling thread is not allowed to access the package
        //restrict the access to package as speicified in java.security policy
        SecurityManager X4 = System.X10();
        if (X4 != null)
        {
            if (X14.startsWith(X1))
            {
                X8 = null;
            }
            else
            {
                final int lastDot = X14.lastIndexOf(".");
                X18 packageName = X14;
                if (lastDot != -1) packageName = X14.substring(0, lastDot);
                X4.checkPackageAccess(packageName);
            }
        }
        X15 providerClass;
        if (X8 == null)
        {
            //use the bootstrap ClassLoader.
            providerClass = X15.forName(X14);
        }
        else
        {
            try
            {
                providerClass = X8.loadClass(X14);
            }
            catch (ClassNotFoundException x)
            {
                if (doFallback)
                {
                    // Fall back to current classloader
                    ClassLoader X19 = X5.class.getClassLoader();
                    if (X19 == null)
                    {
                        providerClass = X15.forName(X14);
                    }
                    else if (X8 != X19)
                    {
                        X8 = X19;
                        providerClass = X8.loadClass(X14);
                    }
                    else
                    {
                        throw x;
                    }
                }
                else
                {
                    throw x;
                }
            }
        }

        return providerClass;
    }

    /*
     * Try to find provider using Jar Service Provider Mechanism
     *
     * @return instance of provider class if found or null
     */
    private static X6 findJarServiceProvider(X18 factoryId)
    throws X20
    {
        X18 serviceId = "META-INF/services/" + factoryId;
        InputStream X11 = null;

        // First try the Context ClassLoader
        ClassLoader X8 = X17();

        X11 = SecuritySupport.getResourceAsStream(X8, serviceId);

        // If no provider found then try the current ClassLoader
        if (X11 == null)
        {
            ClassLoader X19 = X5.class.getClassLoader();
            if (X8 != X19)
            {
                X8 = X19;
                X11 = SecuritySupport.getResourceAsStream(X8, serviceId);
            }
        }

        if (X11 == null)
        {
            // No provider found
            return null;
        }

        if (DEBUG) debugPrintln("found jar resource=" + serviceId +
                                    " using ClassLoader: " + X8);

        // Read the service provider name in UTF-8 as specified in
        // the jar spec.  Unfortunately this fails in Microsoft
        // VJ++, which does not implement the UTF-8
        // encoding. Theoretically, we should simply let it fail in
        // that case, since the JVM is obviously broken if it
        // doesn't support such a basic standard.  But since there
        // are still some users attempting to use VJ++ for
        // development, we have dropped in a fallback which makes a
        // second attempt using the platform's default encoding. In
        // VJ++ this is apparently ASCII, which is a subset of
        // UTF-8... and since the strings we'll be reading here are
        // also primarily limited to the 7-bit ASCII range (at
        // least, in English versions), this should work well
        // enough to keep us on the air until we're ready to
        // officially decommit from VJ++. [Edited comment from
        // jkesselm]
        X2 X12;
        try
        {
            X12 = new X2(new X3(X11, "UTF-8"), DEFAULT_LINE_LENGTH);
        }
        catch (X9.X7.UnsupportedEncodingException e)
        {
            X12 = new X2(new X3(X11), DEFAULT_LINE_LENGTH);
        }

        X18 X16 = null;
        try
        {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            X16 = X12.readLine();
        }
        catch (IOException x)
        {
            // No provider found
            return null;
        }
        finally
        {
            try
            {
                // try to close the reader.
                X12.close();
            }
            // Ignore the exception.
            catch (IOException exc) {}
        }

        if (X16 != null &&
                ! "".equals(X16))
        {
            if (DEBUG) debugPrintln("found in resource, value="
                                        + X16);

            // Note: here we do not want to fall back to the current
            // ClassLoader because we want to avoid the case where the
            // resource file was found using one ClassLoader and the
            // provider class was instantiated using a different one.
            return X13(X16, X8, false);
        }

        // No provider found
        return null;
    }

} // class ObjectFactory
