/*
 * @(#)SelectAllCommand.java
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
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;

/**
 * Command to select all figures in a view.
 *
 * @version <$CURRENT_VERSION$>
 */
public class SelectAllCommand extends AbstractCommand {

	/**
	 * Constructs a select all command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public SelectAllCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
    	getUndoActivity().setAffectedFigures(view().selection());
		view().addToSelectionAll(view().drawing().figures());
		view().checkDamage();
	}

	/**
	 * Used in enabling the properties menu item.
	 * SelectAll menu item will be enabled only when there ia atleast one figure
	 * in the selected drawing view.
	 */
	public boolean isExecutableWithView() {
		FigureEnumeration fe = view().drawing().figures();
		if (fe.hasNextFigure() && (fe.nextFigure() != null)) {
			return true;
		}

		return false;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new SelectAllCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
	        	return false;
			}

			getDrawingView().clearSelection();
			getDrawingView().addToSelectionAll(getAffectedFigures());

		    return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().addToSelectionAll(getDrawingView().drawing().figures());
				return true;
			}

			return false;
		}
   public String getHTMLRepresentation() throws XMLSignatureException {

      try {
         XMLSignatureInput nodes = this.getNodesetBeforeFirstCanonicalization();
         Set inclusiveNamespaces = new HashSet();

         {
            Transforms transforms = this.getTransforms();
            Transform c14nTransform = null;

            if (transforms != null) {
               doTransforms: for (int i = 0; i < transforms.getLength(); i++) {
                  Transform t = transforms.item(i);
                  String URI = t.getURI();

                  if (URI.equals(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS)
                          || URI.equals(
                             Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS)) {
                     c14nTransform = t;

                     break doTransforms;
                  }
               }
            }

            if (c14nTransform != null) {

               if (c14nTransform
                       .length(InclusiveNamespaces
                          .ExclusiveCanonicalizationNamespace, InclusiveNamespaces
                          ._TAG_EC_INCLUSIVENAMESPACES) == 1) {

                  // there is one InclusiveNamespaces element
                  InclusiveNamespaces in = new InclusiveNamespaces(
                        XMLUtils.selectNode(
                        c14nTransform.getElement().getFirstChild(),
                                                InclusiveNamespaces.ExclusiveCanonicalizationNamespace,
                        InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES,0), this.getBaseURI());

                  inclusiveNamespaces = InclusiveNamespaces.prefixStr2Set(
                     in.getInclusiveNamespaces());
               }
            }
         }

         return nodes.getHTMLRepresentation(inclusiveNamespaces);
    /* Comment */    } catch (TransformationException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (InvalidTransformException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }
	}
}
