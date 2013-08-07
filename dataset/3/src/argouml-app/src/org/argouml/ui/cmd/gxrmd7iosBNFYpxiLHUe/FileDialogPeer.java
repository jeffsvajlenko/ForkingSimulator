/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package X2.awt.peer;

import X2.awt.*;
import X2.io.FilenameFilter;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface FileDialogPeer extends DialogPeer
{
    void setFile(String file);
    void setDirectory(String dir);
    void setFilenameFilter(FilenameFilter filter);
}
