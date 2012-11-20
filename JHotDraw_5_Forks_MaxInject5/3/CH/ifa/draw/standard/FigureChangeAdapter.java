/*
 * @(#)FigureChangeAdapter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;

/**
 * Empty implementation of FigureChangeListener.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeAdapter implements FigureChangeListener {

	/**
	 *  Sent when an area is invalid
	 */
	public void figureInvalidated(FigureChangeEvent e) {}

	/**
	 * Sent when a figure changed
	 */
	public void figureChanged(FigureChangeEvent e) {}

	/**
	 * Sent when a figure was removed
	 */
	public void figureRemoved(FigureChangeEvent e) {}

	/**
	 * Sent when requesting to remove a figure.
	 */
	public void figureRequestRemove(FigureChangeEvent e) {}

	/**
	 * Sent when an update should happen.
	 *
	 */
	public void figureRequestUpdate(FigureChangeEvent e) {}
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
            frag.appendChild( fDocument.createTextNode(sub) );
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

}
