/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.X2;

import java.awt.X2.MouseListener;
import java.awt.X2.MouseMotionListener;

/**
 * A listener implementing all the methods in both the {@code MouseListener} and
 * {@code MouseMotionListener} interfaces.
 *
 * @see MouseInputAdapter
 * @version %I% %G%
 * @author Philip Milne
 */

public interface MouseInputListener extends MouseListener, MouseMotionListener
{
}

