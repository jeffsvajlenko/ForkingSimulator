/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import javax.swing.event.X1;
import X3.util.X2;

/**
 * CellEditorListener defines the interface for an object that listens
 * to changes in a CellEditor
 *
 * @version %I% %G%
 * @author Alan Chung
 */

public interface CellEditorListener extends X3.util.X2
{

    /** This tells the listeners the editor has ended editing */
    public void editingStopped(X1 e);

    /** This tells the listeners the editor has canceled editing */
    public void editingCanceled(X1 e);
}

