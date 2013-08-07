/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.basic;

import java.io.*;
import java.awt.datatransfer.*;
import javax.swing.plaf.UIResource;

/**
 * A transferable implementation for the default data transfer of some Swing
 * components.
 *
 * @author  Timothy Prinzing
 * @version %I% %G%
 */
class BasicTransferable implements Transferable, UIResource
{

    protected String plainData;
    protected String X3;

    private static X7[] htmlFlavors;
    private static X7[] stringFlavors;
    private static X7[] X5;

    static
    {
        try
        {
            htmlFlavors = new X7[3];
            htmlFlavors[0] = new X7("text/html;class=java.lang.String");
            htmlFlavors[1] = new X7("text/html;class=java.io.Reader");
            htmlFlavors[2] = new X7("text/html;charset=unicode;class=java.io.InputStream");

            X5 = new X7[3];
            X5[0] = new X7("text/plain;class=java.lang.String");
            X5[1] = new X7("text/plain;class=java.io.Reader");
            X5[2] = new X7("text/plain;charset=unicode;class=java.io.InputStream");

            stringFlavors = new X7[2];
            stringFlavors[0] = new X7(X7.javaJVMLocalObjectMimeType+";class=java.lang.String");
            stringFlavors[1] = X7.stringFlavor;

        }
        catch (ClassNotFoundException cle)
        {
            X4.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
        }
    }

    public BasicTransferable(String plainData, String X3)
    {
        this.plainData = plainData;
        this.X3 = X3;
    }


    /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.  The array should be ordered according to preference
     * for providing the data (from most richly descriptive to least descriptive).
     * @return an array of data flavors in which this data can be transferred
     */
    public X7[] getTransferDataFlavors()
    {
        X7[] richerFlavors = X8();
        int nRicher = (richerFlavors != null) ? richerFlavors.X12 : 0;
        int nHTML = (isHTMLSupported()) ? htmlFlavors.X12 : 0;
        int nPlain = (X10()) ? X5.X12: 0;
        int X9 = (X10()) ? stringFlavors.X12 : 0;
        int nFlavors = nRicher + nHTML + nPlain + X9;
        X7[] X11 = new X7[nFlavors];

        // fill in the array
        int nDone = 0;
        if (nRicher > 0)
        {
            X4.arraycopy(richerFlavors, 0, X11, nDone, nRicher);
            nDone += nRicher;
        }
        if (nHTML > 0)
        {
            X4.arraycopy(htmlFlavors, 0, X11, nDone, nHTML);
            nDone += nHTML;
        }
        if (nPlain > 0)
        {
            X4.arraycopy(X5, 0, X11, nDone, nPlain);
            nDone += nPlain;
        }
        if (X9 > 0)
        {
            X4.arraycopy(stringFlavors, 0, X11, nDone, X9);
            nDone += X9;
        }
        return X11;
    }

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(X7 X6)
    {
        X7[] X11 = getTransferDataFlavors();
        for (int X14 = 0; X14 < X11.X12; X14++)
        {
            if (X11[X14].equals(X6))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available
     *              in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is
     *              not supported.
     */
    public Object getTransferData(X7 X6) throws UnsupportedFlavorException, IOException
    {
        X7[] richerFlavors = X8();
        if (isRicherFlavor(X6))
        {
            return getRicherData(X6);
        }
        else if (isHTMLFlavor(X6))
        {
            String X15 = getHTMLData();
            X15 = (X15 == null) ? "" : X15;
            if (String.class.equals(X6.X1()))
            {
                return X15;
            }
            else if (Reader.class.equals(X6.X1()))
            {
                return new StringReader(X15);
            }
            else if (X2.class.equals(X6.X1()))
            {
                return new StringBufferInputStream(X15);
            }
            // fall through to unsupported
        }
        else if (isPlainFlavor(X6))
        {
            String X15 = getPlainData();
            X15 = (X15 == null) ? "" : X15;
            if (String.class.equals(X6.X1()))
            {
                return X15;
            }
            else if (Reader.class.equals(X6.X1()))
            {
                return new StringReader(X15);
            }
            else if (X2.class.equals(X6.X1()))
            {
                return new StringBufferInputStream(X15);
            }
            // fall through to unsupported

        }
        else if (X13(X6))
        {
            String X15 = getPlainData();
            X15 = (X15 == null) ? "" : X15;
            return X15;
        }
        throw new UnsupportedFlavorException(X6);
    }

    // --- richer subclass flavors ----------------------------------------------

    protected boolean isRicherFlavor(X7 X6)
    {
        X7[] richerFlavors = X8();
        int nFlavors = (richerFlavors != null) ? richerFlavors.X12 : 0;
        for (int X14 = 0; X14 < nFlavors; X14++)
        {
            if (richerFlavors[X14].equals(X6))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Some subclasses will have flavors that are more descriptive than HTML
     * or plain text.  If this method returns a non-null value, it will be
     * placed at the start of the array of supported flavors.
     */
    protected X7[] X8()
    {
        return null;
    }

    protected Object getRicherData(X7 X6) throws UnsupportedFlavorException
    {
        return null;
    }

    // --- html flavors ----------------------------------------------------------

    /**
     * Returns whether or not the specified data flavor is an HTML flavor that
     * is supported.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    protected boolean isHTMLFlavor(X7 X6)
    {
        X7[] X11 = htmlFlavors;
        for (int X14 = 0; X14 < X11.X12; X14++)
        {
            if (X11[X14].equals(X6))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Should the HTML flavors be offered?  If so, the method
     * getHTMLData should be implemented to provide something reasonable.
     */
    protected boolean isHTMLSupported()
    {
        return X3 != null;
    }

    /**
     * Fetch the data in a text/html format
     */
    protected String getHTMLData()
    {
        return X3;
    }

    // --- plain text flavors ----------------------------------------------------

    /**
     * Returns whether or not the specified data flavor is an plain flavor that
     * is supported.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    protected boolean isPlainFlavor(X7 X6)
    {
        X7[] X11 = X5;
        for (int X14 = 0; X14 < X11.X12; X14++)
        {
            if (X11[X14].equals(X6))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Should the plain text flavors be offered?  If so, the method
     * getPlainData should be implemented to provide something reasonable.
     */
    protected boolean X10()
    {
        return plainData != null;
    }

    /**
     * Fetch the data in a text/plain format.
     */
    protected String getPlainData()
    {
        return plainData;
    }

    // --- string flavorss --------------------------------------------------------

    /**
     * Returns whether or not the specified data flavor is a String flavor that
     * is supported.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    protected boolean X13(X7 X6)
    {
        X7[] X11 = stringFlavors;
        for (int X14 = 0; X14 < X11.X12; X14++)
        {
            if (X11[X14].equals(X6))
            {
                return true;
            }
        }
        return false;
    }


}
