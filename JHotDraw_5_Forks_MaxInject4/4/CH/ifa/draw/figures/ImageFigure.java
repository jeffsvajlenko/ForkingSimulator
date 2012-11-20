/*
 * @(#)ImageFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.awt.image.ImageObserver;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.*;

/**
 * A Figure that shows an Image.
 * Images shown by an image figure are shared by using the Iconkit.
 *
 * @see Iconkit
 *
 * @version <$CURRENT_VERSION$>
 */
public  class ImageFigure
		extends AttributeFigure implements ImageObserver {

	private String   fFileName;
	private transient Image fImage;
	private Rectangle fDisplayBox;
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 148012030121282439L;
	private int imageFigureSerializedDataVersion = 1;

	public ImageFigure() {
		fFileName = null;
		fImage = null;
		fDisplayBox = null;
	}

	public ImageFigure(Image image, String fileName, Point origin) {
		fFileName = fileName;
		fImage = image;
		// fix for bug-id: 593080 (ImageFigure calculates the image rectangle wrongly)
		basicDisplayBox(origin, new Point(origin.x + fImage.getWidth(this), origin.y + fImage.getHeight(this)));
	}

	public void basicDisplayBox(Point origin, Point corner) {
		fDisplayBox = new Rectangle(origin);
		fDisplayBox.add(corner);
	}

	public HandleEnumeration handles() {
		List handles = CollectionsFactory.current().createList();
		BoxHandleKit.addHandles(this, handles);
		return new HandleEnumerator(handles);
	}
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
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.corba.se.spi.activation.ServerIdHelper.id (), "ServerId", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "serverId",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (com.sun.corba.se.spi.activation.ServerAlreadyUninstalledHelper.id (), "ServerAlreadyUninstalled", _members0);

          __active = false;
        }
      }
    }
    return __typeCode;
  }

	public Rectangle displayBox() {
		return new Rectangle(
			fDisplayBox.x,
			fDisplayBox.y,
			fDisplayBox.width,
			fDisplayBox.height);
	}

	protected void basicMoveBy(int x, int y) {
		fDisplayBox.translate(x,y);
	}

	public void draw(Graphics g) {
		if (fImage == null) {
			fImage = Iconkit.instance().getImage(fFileName);
		}
		if (fImage != null) {
			g.drawImage(fImage, fDisplayBox.x, fDisplayBox.y, fDisplayBox.width, fDisplayBox.height, this);
		}
		else {
			drawGhost(g);
		}
	}

	private void drawGhost(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(fDisplayBox.x, fDisplayBox.y, fDisplayBox.width, fDisplayBox.height);
	}

   /**
	* Handles asynchroneous image updates.
	*/
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		if ((flags & (FRAMEBITS|ALLBITS)) != 0) {
			invalidate();
			if (listener() != null) {
				listener().figureRequestUpdate(new FigureChangeEvent(this));
			}
		}
		return (flags & (ALLBITS|ABORT)) == 0;
	}

	/**
	 * Releases a figure's resources. Release is called when
	 * a figure is removed from a drawing. Informs the listeners that
	 * the figure is removed by calling figureRemoved.
	 */
	public void release() {
		fImage.flush();
	}

   /**
	* Writes the ImageFigure to a StorableOutput. Only a reference to the
	* image, that is its pathname is saved.
	*/
	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fDisplayBox.x);
		dw.writeInt(fDisplayBox.y);
		dw.writeInt(fDisplayBox.width);
		dw.writeInt(fDisplayBox.height);
		dw.writeString(fFileName);
	}

   /**
	* Reads the ImageFigure from a StorableInput. It registers the
	* referenced figure to be loaded from the Iconkit.
	* @see Iconkit#registerImage
	*/
	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fDisplayBox = new Rectangle(
			dr.readInt(),
			dr.readInt(),
			dr.readInt(),
			dr.readInt());
		fFileName = dr.readString();
		Iconkit.instance().registerImage(fFileName);
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		s.defaultReadObject();
		Iconkit.instance().registerImage(fFileName);
		fImage = null;
	}
}
