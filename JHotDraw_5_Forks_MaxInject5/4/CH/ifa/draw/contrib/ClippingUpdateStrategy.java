package CH.ifa.draw.contrib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.FigureEnumerator;

/**
 * The ClippingUpdateStrategy will only draw those Figures in the DrawingView
 * which intersect the Graphic's clipping rectangle. 
 * 
 * @author Aviv Hurvitz
 */
public class ClippingUpdateStrategy implements Painter {

	public ClippingUpdateStrategy() {
		super();
	}
  public void list (int how_many, org.omg.CosNaming.BindingListHolder bl, org.omg.CosNaming.BindingIteratorHolder bi)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("list", true);
                $out.write_ulong (how_many);
                $in = _invoke ($out);
                bl.value = org.omg.CosNaming.BindingListHelper.read ($in);
                bi.value = org.omg.CosNaming.BindingIteratorHelper.read ($in);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                list (how_many, bl, bi        );
            } finally {
                _releaseReply ($in);
            }
  } // list

	/**
	 * @see CH.ifa.draw.framework.Painter#draw(Graphics, DrawingView)
	 */
	public void draw(Graphics g, DrawingView view) {
		Rectangle viewClipRectangle = g.getClipBounds();

		if (viewClipRectangle == null) {
			// it seems clip is always set, but nevertheless handle it
			view.drawAll(g);
			return;
		}

		FigureEnumeration fe = view.drawing().figures();
		
		// it's better to start big than to do Log(nFigures) reallocations 
		Vector v = new Vector(1000);

		// create a List of the figures within the clip rectangle
		while (fe.hasNextFigure()) {
			Figure fig = fe.nextFigure();
			Rectangle r = fig.displayBox();

			// grow Rectangles that have 0 width or height, since 
			// Rectangle.intersects() returns false on them. 
			// These Rectangles are common, as they are the typical bounding 
			// boxes of horizontal and vertical lines.
			// see my (regression) bug report on Sun's site:
			//   http://developer.java.sun.com/developer/bugParade/bugs/4643428.html
			if (r.width <= 0) {
				r.width = 1;
			}
			if (r.height <= 0) {
				r.height = 1;
			}

			if (r.intersects(viewClipRectangle)) {
				v.add(fig);
			}
		}

		// draw the figures in the clip rectangle
		FigureEnumeration clippedFE = new FigureEnumerator(v);
		view.draw(g, clippedFE);
	}
}
