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

import java.lang.ref.WeakReference;
import java.util.Iterator;

import java.util.WeakHashMap;

/**
 * StandardDisposableResourceManager implements disposable resource management
 * using a client supplied strategy.<br>
 *
 * @author    Eduardo Francos - InContext
 * @created   2 mai 2002
 * @version   1.0
 */

public class StandardDisposableResourceManager implements DisposableResourceManager {
	/** The registered resources */
	protected WeakHashMap resources;

	/** The disposing strategy */
	protected ResourceDisposabilityStrategy strategy;


	/**
	 *Constructor for the StandardDisposableResourceManager object
	 *
	 * @param strategy  Description of the Parameter
	 */
	public StandardDisposableResourceManager(ResourceDisposabilityStrategy strategy) {
		resources = new WeakHashMap();
		this.strategy = strategy;
		strategy.setManager(this);
	}
    protected void intersectRanges(Token token) {
        RangeToken tok = (RangeToken)token;
        if (tok.ranges == null || this.ranges == null)
            return;
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        tok.sortRanges();
        tok.compactRanges();

        int[] result = new int[this.ranges.length+tok.ranges.length];
        int wp = 0, src1 = 0, src2 = 0;
        while (src1 < this.ranges.length && src2 < tok.ranges.length) {
            int src1begin = this.ranges[src1];
            int src1end = this.ranges[src1+1];
            int src2begin = tok.ranges[src2];
            int src2end = tok.ranges[src2+1];
            if (src1end < src2begin) {          // Not overlapped
                                                // src1: o-----o
                                                // src2:         o-----o
                                                // res:  empty
                                                // Reuse src2
                src1 += 2;
            } else if (src1end >= src2begin
                       && src1begin <= src2end) { // Overlapped
                                                // src1:    o--------o
                                                // src2:  o----o
                                                // src2:      o----o
                                                // src2:          o----o
                                                // src2:  o------------o
                if (src2begin <= src2begin && src1end <= src2end) {
                                                // src1:    o--------o
                                                // src2:  o------------o
                                                // res:     o--------o
                                                // Reuse src2
                    result[wp++] = src1begin;
                    result[wp++] = src1end;
                    src1 += 2;
                } else if (src2begin <= src1begin) {
                                                // src1:    o--------o
                                                // src2:  o----o
                                                // res:     o--o
                                                // Reuse the rest of src1
                    result[wp++] = src1begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end+1;
                    src2 += 2;
                } else if (src1end <= src2end) {
                                                // src1:    o--------o
                                                // src2:          o----o
                                                // res:           o--o
                                                // Reuse src2
                    result[wp++] = src2begin;
                    result[wp++] = src1end;
                    src1 += 2;
                } else {
                                                // src1:    o--------o
                                                // src2:      o----o
                                                // res:       o----o
                                                // Reuse the rest of src1
                    result[wp++] = src2begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end+1;
                }
            } else if (src2end < src1begin) {
                                                // Not overlapped
                                                // src1:          o-----o
                                                // src2: o----o
                src2 += 2;
            } else {
                throw new RuntimeException("Token#intersectRanges(): Internal Error: ["
                                           +this.ranges[src1]
                                           +","+this.ranges[src1+1]
                                           +"] & ["+tok.ranges[src2]
                                           +","+tok.ranges[src2+1]
                                           +"]");
            }
        }
        while (src1 < this.ranges.length) {
            result[wp++] = this.ranges[src1++];
            result[wp++] = this.ranges[src1++];
        }
        this.ranges = new int[wp];
        System.arraycopy(result, 0, this.ranges, 0, wp);
                                                // this.ranges is sorted and compacted.
    }


	/**
	 * Registers a resource to be automatically disposed of
	 *
	 * @param resource  the resource
	 */
	public synchronized void registerResource(DisposableResourceHolder resource) {
		resources.put(resource, resource);
	}


	/**
	 * Unregisters a resource so it is not automatically GCed.<br>
	 * If does nothing if the resource was not registered with this manager
	 *
	 * @param resource  the resource
	 */
	public synchronized void unregisterResource(DisposableResourceHolder resource) {
		resources.remove(resource);
	}


	/**
	 * Gets an iterator on the managed resources
	 *
	 * @return   The iterator
	 */
	public Iterator getResources() {
		return resources.values().iterator();
	}


	/**
	 * Description of the Method
	 *
	 * @param resource  the resource
	 * @return          True if the resource is registered with this manager
	 */
	public synchronized boolean managesResource(DisposableResourceHolder resource) {
		return resources.containsValue(resource);
	}


	/**
	 * Gets the strategy attribute of the StandardDisposableResourceManager object
	 *
	 * @return   The strategy value
	 */
	public ResourceDisposabilityStrategy getStrategy() {
		return strategy;
	}


	/**
	 * Sets the strategy attribute of the StandardDisposableResourceManager object
	 *
	 * @param newStrategy  The new strategy value
	 */
	public void setStrategy(ResourceDisposabilityStrategy newStrategy) {
		strategy = newStrategy;
	}


	/**
	 * Activates the strategy which starts disposing of resources as fitted
	 *
	 * @exception ResourceManagerNotSetException  Description of the Exception
	 */
	public void startDisposing()
		throws ResourceManagerNotSetException {
		strategy.startDisposing();
	}


	/**
	 * Deactivates the strategy that stops automatic disposal of resource.<br>
	 * The millis parameters specifies in milliseconds the time to wait for
	 * the disposal to stop. After this time the method returns, but the
	 * deactivation request remain active.
	 *
	 * @param millis  time to wait for disposal to stop
	 */
	public void stopDisposing(long millis) {
		strategy.stopDisposing(millis);
	}
}
