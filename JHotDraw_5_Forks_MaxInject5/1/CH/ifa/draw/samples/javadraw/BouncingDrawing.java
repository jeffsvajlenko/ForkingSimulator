/*
 * @(#)BouncingDrawing.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Animatable;

/**
 * @version <$CURRENT_VERSION$>
 */
public class BouncingDrawing extends StandardDrawing implements Animatable {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -8566272817418441758L;
	private int bouncingDrawingSerializedDataVersion = 1;

	public synchronized Figure add(Figure figure) {
		if (!(figure instanceof AnimationDecorator) &&
			!(figure instanceof ConnectionFigure)) {
			figure = new AnimationDecorator(figure);
		}
		return super.add(figure);
	}

	public synchronized Figure remove(Figure figure) {
		Figure f = super.remove(figure);
		if (f instanceof AnimationDecorator) {
			return ((AnimationDecorator) f).peelDecoration();
		}
		return f;
	}

	/**
	 * @param figure figure to be replaced
	 * @param replacement figure that should replace the specified figure
	 * @return the figure that has been inserted (might be different from the figure specified)
	 */
	public synchronized Figure replace(Figure figure, Figure replacement) {
		if (!(replacement instanceof AnimationDecorator) &&
			!(replacement instanceof ConnectionFigure)) {
			replacement = new AnimationDecorator(replacement);
		}
		return super.replace(figure, replacement);
	}

	public void animationStep() {
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();

			if(!(f instanceof ConnectionFigure)) {
				((AnimationDecorator) f).animationStep();
			}
		}
	}
    protected static NamingContext resolveFirstAsContext(NamingContextDataStore impl,
                                                         NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound {
        org.omg.CORBA.Object topRef = null;
        BindingTypeHolder bth = new BindingTypeHolder();
        NamingContext context = null;

        synchronized (impl) {
            // Resolve first  - must be resolveable
            topRef = impl.Resolve(n[0],bth);
            if (topRef == null) {
                // It was not bound
                throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.missing_node,n);
            }
        }

        // Was it bound as a context?
        if (bth.value != BindingType.ncontext) {
            // It was not a context
            throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.not_context,n);
        }

        // Narrow to a naming context
        try {
            context = NamingContextHelper.narrow(topRef);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            // It was not a context
            throw new org.omg.CosNaming.NamingContextPackage.NotFound(NotFoundReason.not_context,n);
        }

        // Hmm. must be ok
        return context;
    }
}
