/*
 * @(#)SplitPaneDrawApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.application.*;

/**
 * A specialised DrawApplication, which offers basic support for a simple
 * splitted pane content.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public  class SplitPaneDrawApplication extends DrawApplication {

	/**
	 * Constructs a drawing window with a default title.
	 */
	public SplitPaneDrawApplication() {
		this("JHotDraw");
	}

	/**
	 * Constructs a drawing window with the given title.
	 */
	public SplitPaneDrawApplication(String title) {
		super(title);
	}

	protected Desktop createDesktop() {
		return new SplitPaneDesktop();
	}
    public SerialJavaObject(Object obj) throws SerialException {

        // if any static fields are found, an exception
        // should be thrown


        // get Class. Object instance should always be available
        Class<?> c = obj.getClass();

        // determine if object implements Serializable i/f
        if (!(obj instanceof java.io.Serializable)) {
            setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable"));
        }

        // can only determine public fields (obviously). If
        // any of these are static, this should invalidate
        // the action of attempting to persist these fields
        // in a serialized form

        boolean anyStaticFields = false;
        fields = c.getFields();

        for (int i = 0; i < fields.length; i++ ) {
            if ( fields[i].getModifiers() == Modifier.STATIC ) {
                anyStaticFields = true;
            }
        }


        if (anyStaticFields) {
            throw new SerialException("Located static fields in " +
                "object instance. Cannot serialize");
        }

        this.obj = obj;
    }
}
