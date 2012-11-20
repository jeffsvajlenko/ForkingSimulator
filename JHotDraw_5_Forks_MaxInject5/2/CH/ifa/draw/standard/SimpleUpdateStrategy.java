/*
 * @(#)SimpleUpdateStrategy.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import CH.ifa.draw.framework.*;

/**
 * The SimpleUpdateStrategy implements an update
 * strategy that directly redraws a DrawingView.
 *
 * @see DrawingView
 *
 * @version <$CURRENT_VERSION$>
 */
public  class SimpleUpdateStrategy implements Painter {

	/*
	 * Serialization support. In JavaDraw only the Drawing is serialized.
	 * However, for beans support SimpleUpdateStrategy supports
	 * serialization
	 */
	private static final long serialVersionUID = -7539925820692134566L;

	/**
	* Draws the view contents.
	*/
	public void draw(Graphics g, DrawingView view) {
		view.drawAll(g);
	}
    private byte[] encodeImpl( Any data, boolean sendTypeCode )
        throws InvalidTypeForEncoding
    {
        if( data == null )
            throw wrapper.nullParam() ;

        // _REVISIT_ Note that InvalidTypeForEncoding is never thrown in
        // the body of this method.  This is due to the fact that CDR*Stream
        // will never throw an exception if the encoding is invalid.  To
        // fix this, the CDROutputStream must know the version of GIOP it
        // is encoding for and it must check to ensure that, for example,
        // wstring cannot be encoded in GIOP 1.0.
        //
        // As part of the GIOP 1.2 work, the CDRInput and OutputStream will
        // be versioned.  This can be handled once this work is complete.

        // Create output stream with default endianness.
        EncapsOutputStream cdrOut = new EncapsOutputStream(
            (com.sun.corba.se.X1.orb.ORB)orb, giopVersion );

        // This is an encapsulation, so put out the endian:
        cdrOut.putEndian();

        // Sometimes encode type code:
        if( sendTypeCode ) {
            cdrOut.write_TypeCode( data.type() );
        }

        // Encode value and return.
        data.write_value( cdrOut );

        return cdrOut.toByteArray();
    }
}
