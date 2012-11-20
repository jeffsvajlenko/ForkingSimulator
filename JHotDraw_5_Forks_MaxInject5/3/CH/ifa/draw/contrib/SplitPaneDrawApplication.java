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
}
