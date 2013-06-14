/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

/**
 * Represents a class or interface program element.  Provides access
 * to information about the type and its members.  Note that an enum
 * type is a kind of class and an annotation type is a kind of
 * interface.
 *
 * <p> <a name="ELEM_VS_TYPE"></a>
 * While a {@code TypeElement} represents a class or interface
 * <i>element</i>, a {@link DeclaredType} represents a class
 * or interface <i>type</i>, the latter being a use
 * (or <i>invocation</i>) of the former.
 * The distinction is most apparent with generic types,
 * for which a single element can define a whole
 * family of types.  For example, the element
 * {@code java.util.Set} corresponds to the parameterized types
 * {@code java.util.Set<String>} and {@code java.util.Set<Number>}
 * (and many others), and to the raw type {@code java.util.Set}.
 *
 * <p> Each method of this interface that returns a list of elements
 * will return them in the order that is natural for the underlying
 * source of program information.  For example, if the underlying
 * source of information is Java source code, then the elements will be
 * returned in source code order.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @version %I% %E%
 * @see DeclaredType
 * @since 1.6
 */
public interface TypeElement extends Element
{

    /**
     * Returns the <i>nesting kind</i> of this type element.
     *
     * @return the nesting kind of this type element
     */
    NestingKind getNestingKind();

    /**
     * Returns the fully qualified name of this type element.
     * More precisely, it returns the <i>canonical</i> name.
     * For local and anonymous classes, which do not have canonical names,
     * an empty name is returned.
     *
     * <p>The name of a generic type does not include any reference
     * to its formal type parameters.
     * For example, the fully qualified name of the interface
     * {@code java.util.Set<E>} is "{@code java.util.Set}".
     * Nested types use "{@code .}" as a separator, as in
     * "{@code java.util.Map.Entry}".
     *
     * @return the fully qualified name of this class or interface, or
     * an empty name if none
     *
     * @see Elements#getBinaryName
     * @jls3 6.7 Fully Qualified Names and Canonical Names
     */
    Name getQualifiedName();

    /**
     * Returns the direct superclass of this type element.
     * If this type element represents an interface or the class
     * {@code java.lang.Object}, then a {@link NoType}
     * with kind {@link TypeKind#NONE NONE} is returned.
     *
     * @return the direct superclass, or a {@code NoType} if there is none
     */
    TypeMirror getSuperclass();

    /**
     * Returns the interface types directly implemented by this class
     * or extended by this interface.
     *
     * @return the interface types directly implemented by this class
     * or extended by this interface, or an empty list if there are none
     */
    List<? extends TypeMirror> getInterfaces();

    /**
     * Returns the formal type parameters of this type element
     * in declaration order.
     *
     * @return the formal type parameters, or an empty list
     * if there are none
     */
    List<? extends TypeParameterElement> getTypeParameters();
}
