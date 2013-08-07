/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package X2.X1.peer;

import X2.X1.Menu;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface MenuBarPeer extends MenuComponentPeer
{
    void addMenu(Menu m);
    void delMenu(int index);
    void addHelpMenu(Menu m);
}
