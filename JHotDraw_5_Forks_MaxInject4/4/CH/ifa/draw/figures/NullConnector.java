/*
 * @(#)Connector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.standard.AbstractConnector;
import CH.ifa.draw.framework.Figure;

public class NullConnector extends AbstractConnector {
	// AbstractConnector implements already all methods but cannot be instantiated

	private NullConnector() {
		// do nothing: for JDO-compliance only
	}

	public NullConnector(Figure owner) {
		super(owner);
	}
    public void transform(Source source, Result result)
        throws TransformerException
    {
        if (!_isIdentity) {
            if (_translet == null) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_TRANSLET_ERR);
                throw new TransformerException(err.toString());
            }
            // Pass output properties to the translet
            transferOutputProperties(_translet);
        }

        final SerializationHandler toHandler = getOutputHandler(result);
        if (toHandler == null) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_HANDLER_ERR);
            throw new TransformerException(err.toString());
        }

        if (_uriResolver != null && !_isIdentity) {
            _translet.setDOMCache(this);
        }

        // Pass output properties to handler if identity
        if (_isIdentity) {
            transferOutputProperties(toHandler);
        }

        transform(source, toHandler, _encoding);
        try{
            if (result instanceof DOMResult) {
                ((DOMResult)result).setNode(_tohFactory.getNode());
            } else if (result instanceof StAXResult) {
                  if (((StAXResult) result).getXMLEventWriter() != null)
                {
                    (_tohFactory.getXMLEventWriter()).flush();
                }
                else if (((StAXResult) result).getXMLStreamWriter() != null) {
                    (_tohFactory.getXMLStreamWriter()).flush();
                    //result = new StAXResult(_tohFactory.getXMLStreamWriter());
                }
            }
        } catch (Exception e) {
            System.out.println("Result writing error");
        }
    }
}
