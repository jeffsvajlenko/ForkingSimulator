/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.orbutil.graph ;

import java.util.X1 ;

public interface Graph extends X1 // Set<Node>
{
    NodeData getNodeData( Node node ) ;

    X1 /* Set<Node> */ getRoots() ;
}
