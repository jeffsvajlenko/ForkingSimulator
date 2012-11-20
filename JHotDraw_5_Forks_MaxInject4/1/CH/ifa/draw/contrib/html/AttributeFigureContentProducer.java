/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.io.Serializable;
import CH.ifa.draw.util.Storable;
import CH.ifa.draw.util.StorableOutput;
import CH.ifa.draw.util.StorableInput;
import java.io.IOException;

/**
 * AttributeFigureContentProducer provides content for AttributeFigures.<br>
 * It gives priority to base class supplied values, and if none, then it
 * gets the value from the supplied AttributeContentProducerContext.
 *
 * @author    Eduardo Francos - InContext
 * @created   30 avril 2002
 * @version   1.0
 */

public class AttributeFigureContentProducer extends FigureDataContentProducer
		 implements Serializable {

	/**Constructor for the AttributeFigureContentProducer object */
	public AttributeFigureContentProducer() { }


	/**
	 * Produces the contents for the attribute
	 *
	 * @param context       the calling client context
	 * @param ctxAttrName   the attribute name
	 * @param ctxAttrValue  the attribute value that led to the call to this
	 * @return              The content value
	 */
	public Object getContent(ContentProducerContext context, String ctxAttrName, Object ctxAttrValue) {
		// first chance to basic values
		Object attrValue = super.getContent(context, ctxAttrName, ctxAttrValue);
		if (attrValue != null) {
			return attrValue;
		}

		// no, return value from attributes
		return ((AttributeContentProducerContext)context).getAttribute(ctxAttrName);
	}


	/**
	 * Writes the storable
	 *
	 * @param dw  the storable output
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
	}
    private void run(String[] args)
    {
        try {
            redirectIOStreams() ;

            String serverClassName = System.getProperty(
                ORBConstants.SERVER_NAME_PROPERTY ) ;

            // determine the class loader to be used for loading the class
            // since ServerMain is going to be in JDK and we need to have this
            // class to load application classes, this is required here.
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (cl == null)
                cl = ClassLoader.getSystemClassLoader();

            // determine the main class
            Class serverClass = null;

            try {
                // determine the main class, try loading with current class loader
                serverClass = Class.forName( serverClassName ) ;
            } catch (ClassNotFoundException ex) {
                // eat the exception and try to load using SystemClassLoader
                serverClass = Class.forName( serverClassName, true, cl);
            }

            if (debug)
                System.out.println("class " + serverClassName + " found");

            // get the main method
            Method mainMethod = getMainMethod( serverClass ) ;

            // This piece of code is required, to verify the server definition
            // without launching it.

            // verify the server

            boolean serverVerifyFlag = Boolean.getBoolean(
                ORBConstants.SERVER_DEF_VERIFY_PROPERTY) ;
            if (serverVerifyFlag) {
                if (mainMethod == null)
                    logTerminal("", NO_MAIN_METHOD);
                else {
                    if (debug)
                        System.out.println("Valid Server");
                    logTerminal("", OK);
                }
            }


            registerCallback( serverClass ) ;

            // build args to the main and call it
            Object params [] = new Object [1];
            params[0] = args;
            mainMethod.invoke(null, params);

        } catch (ClassNotFoundException e) {
            logTerminal("ClassNotFound exception: " + e.getMessage(),
                MAIN_CLASS_NOT_FOUND);
        } catch (Exception e) {
            logTerminal("Exception: " + e.getMessage(),
                APPLICATION_ERROR);
        }
    }


	/**
	 * Writes the storable
	 *
	 * @param dr               the storable input
	 * @exception IOException  thrown by called methods
	 */
	public void read(StorableInput dr)
		throws IOException {
		super.read(dr);
	}
}
