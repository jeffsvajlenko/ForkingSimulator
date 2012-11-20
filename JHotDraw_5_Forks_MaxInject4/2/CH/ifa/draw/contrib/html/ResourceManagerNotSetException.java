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

/**
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class ResourceManagerNotSetException extends Exception {

	/**Constructor for the ResourceManagerNotSetException object */
	public ResourceManagerNotSetException() { }
  public void insert_wchar (char value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_wchar", _opsClass);
      DynSequenceOperations  $self = (DynSequenceOperations) $so.servant;

      try {
         $self.insert_wchar (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } // insert_wchar
}
