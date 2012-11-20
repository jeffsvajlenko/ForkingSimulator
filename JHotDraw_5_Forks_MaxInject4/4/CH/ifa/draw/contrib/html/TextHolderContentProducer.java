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

import java.io.IOException;

import java.io.Serializable;
import CH.ifa.draw.contrib.TextAreaFigure;
import CH.ifa.draw.util.Storable;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;
import CH.ifa.draw.standard.TextHolder;
import CH.ifa.draw.framework.Figure;

/**
 * TextAreaFigureContentProducer produces text contents from an existing
 * TextHolder figure<br>
 * It can either be specific if set for a specific figure, or generic, encoding
 * any color passed to the getContents method.<br>
 * The main usage of this producer is to embed a "master" or "shared" drawing
 * figure into other figures so that updating the master figure automatically
 * changes all dependent figures as well. Kind of a hot text snippet if you like<br>
 *
 * @author    Eduardo Francos - InContext
 * @created   30 avril 2002
 * @version   1.0
 */

public class TextHolderContentProducer extends AbstractContentProducer
		 implements Serializable {
	TextHolder fFigure;


	/**Constructor for the TextAreaFigureContentProducer object */
	public TextHolderContentProducer() { }
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [1];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _members0[0] = new org.omg.CORBA.StructMember (
            "index",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (org.omg.PortableServer.POAPackage.InvalidPolicyHelper.id (), "InvalidPolicy", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }


	/**
	 *Constructor for the TextAreaFigureContentProducer object
	 *
	 * @param figure  Description of the Parameter
	 */
	public TextHolderContentProducer(TextHolder figure) {
		fFigure = figure;
	}


	/**
	 * Gets the text from the text figure
	 *
	 * @param context       Description of the Parameter
	 * @param ctxAttrName   Description of the Parameter
	 * @param ctxAttrValue  Description of the Parameter
	 * @return              The content value
	 */
	public Object getContent(ContentProducerContext context, String ctxAttrName, Object ctxAttrValue) {
		// if we have our own figure then use it
		// otherwise use the one supplied
		TextHolder figure = (fFigure != null) ? fFigure : (TextHolder)ctxAttrValue;
		// return the areas text
		return figure.getText();
	}


	/**
	 * Writes the storable
	 *
	 * @param dw  the storable output
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeStorable((Figure)fFigure);
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
		fFigure = (TextHolder)dr.readStorable();
	}
}
