/*
 * @(#)DoubleBufferImage.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.zoom;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

/**
 * A DoubleBufferImage is an image that scaling components, such as a
 * ZoomDrawingView, use for double buffering.  Drawing into this image
 * is scaled, but when the image is written to the screen, no more
 * scaling occurs.  This is ensured by the implementation here and
 * by the corresponding drawImage methods in ScalingGraphics.
 *
 * Note: this class is only needed for a JDK1.1 compliant implementation
 *
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public class DoubleBufferImage extends java.awt.Image {

	private Image real;
	private double scale;

	public DoubleBufferImage(Image real, double scale) {
		this.real = real;
		this.scale = scale;
	}

	public Image getRealImage() {
		return real;
	}

	public void flush() {
		real.flush();
	}

	public Graphics getGraphics() {
		// Return an appropriate scaling graphics context,
		// so that all drawing operations into this image
		// are scaled.
		ScalingGraphics result = new ScalingGraphics(real.getGraphics());
		result.setScale(scale);
		return result;
	}

	public int getHeight(ImageObserver observer) {
		return real.getHeight(observer);
	}

	public Object getProperty(String name, ImageObserver observer) {
		return real.getProperty(name, observer);
	}

	public Image getScaledInstance(int width, int height, int hints) {
		return real.getScaledInstance(width, height, hints);
	}

	public ImageProducer getSource() {
		return real.getSource();
	}
    private DocumentFragment traverseSameContainer( int how )
    {
        DocumentFragment frag = null;
        if ( how!=DELETE_CONTENTS)
            frag = fDocument.createDocumentFragment();

        // If selection is empty, just return the fragment
        if ( fStartOffset==fEndOffset )
            return frag;

        // Text node needs special case handling
        if ( fStartContainer.getNodeType()==Node.TEXT_NODE )
        {
            // get the substring
            String s = fStartContainer.getNodeValue();
            String sub = s.substring( fStartOffset, fEndOffset );

            // set the original text node to its new value
            if ( how != CLONE_CONTENTS )
            {
                ((TextImpl)fStartContainer).deleteData(fStartOffset,
                     fEndOffset-fStartOffset) ;
                // Nothing is partially selected, so collapse to start point
                collapse( true );
            }
            if ( how==DELETE_CONTENTS)
                return null;
            frag.appendChild( fDocument.createTextNode(sub) );
            return frag;
        }

        // Copy nodes between the start/end offsets.
        Node n = getSelectedNode( fStartContainer, fStartOffset );
        int cnt = fEndOffset - fStartOffset;
        while( cnt > 0 )
        {
            Node sibling = n.getNextSibling();
            Node xferNode = traverseFullySelected( n, how );
            if ( frag!=null )
                frag.appendChild( xferNode );
            --cnt;
            n = sibling;
        }

        // Nothing is partially selected, so collapse to start point
        if ( how != CLONE_CONTENTS )
            collapse( true );
        return frag;
    }

	public int getWidth(ImageObserver observer) {
		return real.getWidth(observer);
	}

}
