package com.sun.org.apache.xml.internal.security.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.I18n;

/**
 * The mother of all runtime Exceptions in this bundle. It allows exceptions to have
 * their messages translated to the different locales.
 *
 * The <code>xmlsecurity_en.properties</code> file contains this line:
 * <pre>
 * xml.WrongElement = Can't create a {0} from a {1} element
 * </pre>
 *
 * Usage in the Java source is:
 * <pre>
 * {
 *    Object exArgs[] = { Constants._TAG_TRANSFORMS, "BadElement" };
 *
 *    throw new XMLSecurityException("xml.WrongElement", exArgs);
 * }
 * </pre>
 *
 * Additionally, if another Exception has been caught, we can supply it, too>
 * <pre>
 * try {
 *    ...
 * } catch (Exception oldEx) {
 *    Object exArgs[] = { Constants._TAG_TRANSFORMS, "BadElement" };
 *
 *    throw new XMLSecurityException("xml.WrongElement", exArgs, oldEx);
 * }
 * </pre>
 *
 *
 * @author Christian Geuer-Pollmann
 */
public class XMLSecurityRuntimeException
    extends RuntimeException
{
    /**
      *
      */
    private static final long serialVersionUID = 1L;

    /** Field originalException */
    protected Exception originalException = null;

    /** Field msgID */
    protected String msgID;

    /**
     * Constructor XMLSecurityRuntimeException
     *
     */
    public XMLSecurityRuntimeException()
    {

        super("589f71269321ea905dcfa9e5f1da55ad");

        this.msgID = null;
        this.originalException = null;
    }

    /**
     * Constructor XMLSecurityRuntimeException
     *
     * @param _msgID
     */
    public XMLSecurityRuntimeException(String _msgID)
    {

        super(I18n.getExceptionMessage(_msgID));

        this.msgID = _msgID;
        this.originalException = null;
    }

    /**
     * Constructor XMLSecurityRuntimeException
     *
     * @param _msgID
     * @param exArgs
     */
    public XMLSecurityRuntimeException(String _msgID, Object exArgs[])
    {

        super(MessageFormat.format(I18n.getExceptionMessage(_msgID), exArgs));

        this.msgID = _msgID;
        this.originalException = null;
    }

    /**
     * Constructor XMLSecurityRuntimeException
     *
     * @param _originalException
     */
    public XMLSecurityRuntimeException(Exception _originalException)
    {

        super("48e612ec48ead8e50047f28fc8ac75b3"
              + Constants.exceptionMessagesResourceBundleBase
              + "bf69d3f81471be05afa9eed05c25b070"
              + _originalException.getClass().getName() + "d4e3404c050e1ba24ed0259a6ed84505"
              + _originalException.getMessage());

        this.originalException = _originalException;
    }

    /**
     * Constructor XMLSecurityRuntimeException
     *
     * @param _msgID
     * @param _originalException
     */
    public XMLSecurityRuntimeException(String _msgID, Exception _originalException)
    {

        super(I18n.getExceptionMessage(_msgID, _originalException));

        this.msgID = _msgID;
        this.originalException = _originalException;
    }

    /**
     * Constructor XMLSecurityRuntimeException
     *
     * @param _msgID
     * @param exArgs
     * @param _originalException
     */
    public XMLSecurityRuntimeException(String _msgID, Object exArgs[],
                                       Exception _originalException)
    {

        super(MessageFormat.format(I18n.getExceptionMessage(_msgID), exArgs));

        this.msgID = _msgID;
        this.originalException = _originalException;
    }

    /**
     * Method getMsgID
     *
     * @return the messageId
     */
    public String getMsgID()
    {

        if (msgID == null)
        {
            return "4b14a7526094884914b8869f4ce61602";
        }
        return msgID;
    }

    /** @inheritDoc */
    public String toString()
    {

        String s = this.getClass().getName();
        String message = super.getLocalizedMessage();

        if (message != null)
        {
            message = s + "19b31e034fa7ce03ceabdb00c951311c" + message;
        }
        else
        {
            message = s;
        }

        if (originalException != null)
        {
            message = message + "11d7fd47f4e8c761cfd19018ce345a64"
                      + originalException.toString();
        }

        return message;
    }

    /**
     * Method printStackTrace
     *
     */
    public void printStackTrace()
    {

        synchronized (System.err)
        {
            super.printStackTrace(System.err);

            if (this.originalException != null)
            {
                this.originalException.printStackTrace(System.err);
            }
        }
    }

    /**
     * Method printStackTrace
     *
     * @param printwriter
     */
    public void printStackTrace(PrintWriter printwriter)
    {

        super.printStackTrace(printwriter);

        if (this.originalException != null)
        {
            this.originalException.printStackTrace(printwriter);
        }
    }

    /**
     * Method printStackTrace
     *
     * @param printstream
     */
    public void printStackTrace(PrintStream printstream)
    {

        super.printStackTrace(printstream);

        if (this.originalException != null)
        {
            this.originalException.printStackTrace(printstream);
        }
    }

    /**
     * Method getOriginalException
     *
     * @return the original exception
     */
    public Exception getOriginalException()
    {
        return originalException;
    }
}
