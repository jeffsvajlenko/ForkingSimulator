/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio;


import java.io.IOException;


/**
 * A character buffer.
 *
 * <p> This class defines four categories of operations upon
 * character buffers:
 *
 * <ul>
 *
 *   <li><p> Absolute and relative {@link #get() </code><i>get</i><code>} and
 *   {@link #put(char) </code><i>put</i><code>} methods that read and write
 *   single characters; </p></li>
 *
 *   <li><p> Relative {@link #get(char[]) </code><i>bulk get</i><code>}
 *   methods that transfer contiguous sequences of characters from this buffer
 *   into an array; and</p></li>
 *
 *   <li><p> Relative {@link #put(char[]) </code><i>bulk put</i><code>}
 *   methods that transfer contiguous sequences of characters from a
 *   character array,&#32;a&#32;string, or some other character
 *   buffer into this buffer;&#32;and </p></li>
 *












 *
 *   <li><p> Methods for {@link #compact </code>compacting<code>}, {@link
 *   #duplicate </code>duplicating<code>}, and {@link #slice
 *   </code>slicing<code>} a character buffer.  </p></li>
 *
 * </ul>
 *
 * <p> Character buffers can be created either by {@link #allocate
 * </code><i>allocation</i><code>}, which allocates space for the buffer's
 *






 *
 * content, by {@link #wrap(char[]) </code><i>wrapping</i><code>} an existing
 * character array or&#32;string into a buffer, or by creating a
 * <a href="ByteBuffer.html#views"><i>view</i></a> of an existing byte buffer.
 *

 *



































































































*

 *
 * <p> Like a byte buffer, a character buffer is either <a
 * href="ByteBuffer.html#direct"><i>direct</i> or <i>non-direct</i></a>.  A
 * character buffer created via the <tt>wrap</tt> methods of this class will
 * be non-direct.  A character buffer created as a view of a byte buffer will
 * be direct if, and only if, the byte buffer itself is direct.  Whether or not
 * a character buffer is direct may be determined by invoking the {@link
 * #isDirect isDirect} method.  </p>
 *

*

 *
 * <p> This class implements the {@link CharSequence} interface so that
 * character buffers may be used wherever character sequences are accepted, for
 * example in the regular-expression package <tt>{@link java.util.regex}</tt>.
 * </p>
 *

 *



 *
 * <p> Methods in this class that do not otherwise have a value to return are
 * specified to return the buffer upon which they are invoked.  This allows
 * method invocations to be chained.
 *
















 *
 * The sequence of statements
 *
 * <blockquote><pre>
 * cb.put("text/");
 * cb.put(subtype);
 * cb.put("; charset=");
 * cb.put(enc);</pre></blockquote>
 *
 * can, for example, be replaced by the single statement
 *
 * <blockquote><pre>
 * cb.put("text/").put(subtype).put("; charset=").put(enc);</pre></blockquote>
 *

 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @version %I%, %E%
 * @since 1.4
 */

public abstract class CharBuffer
    extends Buffer
    implements Comparable<CharBuffer>, Appendable, CharSequence, Readable
{

    // These fields are declared here rather than in Heap-X-Buffer in order to
    // reduce the number of virtual method invocations needed to access these
    // values, which is especially costly when coding small buffers.
    //
    final char[] hb;			// Non-null only for heap buffers
    final int offset;
    boolean isReadOnly;			// Valid only for heap buffers

    // Creates a new buffer with the given mark, position, limit, capacity,
    // backing array, and array offset
    //
    CharBuffer(int mark, int pos, int lim, int cap,	// package-private
               char[] hb, int offset)
    {
        super(mark, pos, lim, cap);
        this.hb = hb;
        this.offset = offset;
    }

    // Creates a new buffer with the given mark, position, limit, and capacity
    //
    CharBuffer(int mark, int pos, int lim, int cap)  	// package-private
    {
        this(mark, pos, lim, cap, null, 0);
    }
























    /**
     * Allocates a new character buffer.
     *
     * <p> The new buffer's position will be zero, its limit will be its
     * capacity, and its mark will be undefined.  It will have a {@link #array
     * </code>backing array<code>}, and its {@link #arrayOffset </code>array
     * offset<code>} will be zero.
     *
     * @param  capacity
     *         The new buffer's capacity, in characters
     *
     * @return  The new character buffer
     *
     * @throws  IllegalArgumentException
     *          If the <tt>capacity</tt> is a negative integer
     */
    public static CharBuffer allocate(int capacity)
    {
        if (capacity < 0)
            throw new IllegalArgumentException();
        return new HeapCharBuffer(capacity, capacity);
    }

    /**
     * Wraps a character array into a buffer.
     *
     * <p> The new buffer will be backed by the given character array;
     * that is, modifications to the buffer will cause the array to be modified
     * and vice versa.  The new buffer's capacity will be
     * <tt>array.length</tt>, its position will be <tt>offset</tt>, its limit
     * will be <tt>offset + length</tt>, and its mark will be undefined.  Its
     * {@link #array </code>backing array<code>} will be the given array, and
     * its {@link #arrayOffset </code>array offset<code>} will be zero.  </p>
     *
     * @param  array
     *         The array that will back the new buffer
     *
     * @param  offset
     *         The offset of the subarray to be used; must be non-negative and
     *         no larger than <tt>array.length</tt>.  The new buffer's position
     *         will be set to this value.
     *
     * @param  length
     *         The length of the subarray to be used;
     *         must be non-negative and no larger than
     *         <tt>array.length - offset</tt>.
     *         The new buffer's limit will be set to <tt>offset + length</tt>.
     *
     * @return  The new character buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>offset</tt> and <tt>length</tt>
     *          parameters do not hold
     */
    public static CharBuffer wrap(char[] array,
                                  int offset, int length)
    {
        try
        {
            return new HeapCharBuffer(array, offset, length);
        }
        catch (IllegalArgumentException x)
        {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Wraps a character array into a buffer.
     *
     * <p> The new buffer will be backed by the given character array;
     * that is, modifications to the buffer will cause the array to be modified
     * and vice versa.  The new buffer's capacity and limit will be
     * <tt>array.length</tt>, its position will be zero, and its mark will be
     * undefined.  Its {@link #array </code>backing array<code>} will be the
     * given array, and its {@link #arrayOffset </code>array offset<code>} will
     * be zero.  </p>
     *
     * @param  array
     *         The array that will back this buffer
     *
     * @return  The new character buffer
     */
    public static CharBuffer wrap(char[] array)
    {
        return wrap(array, 0, array.length);
    }



    /**
     * Attempts to read characters into the specified character buffer.
     * The buffer is used as a repository of characters as-is: the only
     * changes made are the results of a put operation. No flipping or
     * rewinding of the buffer is performed.
     *
     * @param target the buffer to read characters into
     * @return The number of characters added to the buffer, or
     *         -1 if this source of characters is at its end
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if target is null
     * @throws ReadOnlyBufferException if target is a read only buffer
     * @since 1.5
     */
    public int read(CharBuffer target) throws IOException
    {
        // Determine the number of bytes n that can be transferred
        int targetRemaining = target.remaining();
        int remaining = remaining();
        if (remaining == 0)
            return -1;
        int n = Math.min(remaining, targetRemaining);
        int limit = limit();
        // Set source limit to prevent target overflow
        if (targetRemaining < remaining)
            limit(position() + n);
        try
        {
            if (n > 0)
                target.put(this);
        }
        finally
        {
            limit(limit); // restore real limit
        }
        return n;
    }

    /**
     * Wraps a character sequence into a buffer.
     *
     * <p> The content of the new, read-only buffer will be the content of the
     * given character sequence.  The buffer's capacity will be
     * <tt>csq.length()</tt>, its position will be <tt>start</tt>, its limit
     * will be <tt>end</tt>, and its mark will be undefined.  </p>
     *
     * @param  csq
     *         The character sequence from which the new character buffer is to
     *         be created
     *
     * @param  start
     *         The index of the first character to be used;
     *         must be non-negative and no larger than <tt>csq.length()</tt>.
     *         The new buffer's position will be set to this value.
     *
     * @param  end
     *         The index of the character following the last character to be
     *         used; must be no smaller than <tt>start</tt> and no larger
     *         than <tt>csq.length()</tt>.
     *         The new buffer's limit will be set to this value.
     *
     * @return  The new character buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>start</tt> and <tt>end</tt>
     *          parameters do not hold
     */
    public static CharBuffer wrap(CharSequence csq, int start, int end)
    {
        try
        {
            return new StringCharBuffer(csq, start, end);
        }
        catch (IllegalArgumentException x)
        {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Wraps a character sequence into a buffer.
     *
     * <p> The content of the new, read-only buffer will be the content of the
     * given character sequence.  The new buffer's capacity and limit will be
     * <tt>csq.length()</tt>, its position will be zero, and its mark will be
     * undefined.  </p>
     *
     * @param  csq
     *         The character sequence from which the new character buffer is to
     *         be created
     *
     * @return  The new character buffer
     */
    public static CharBuffer wrap(CharSequence csq)
    {
        return wrap(csq, 0, csq.length());
    }



    /**
     * Creates a new character buffer whose content is a shared subsequence of
     * this buffer's content.
     *
     * <p> The content of the new buffer will start at this buffer's current
     * position.  Changes to this buffer's content will be visible in the new
     * buffer, and vice versa; the two buffers' position, limit, and mark
     * values will be independent.
     *
     * <p> The new buffer's position will be zero, its capacity and its limit
     * will be the number of characters remaining in this buffer, and its mark
     * will be undefined.  The new buffer will be direct if, and only if, this
     * buffer is direct, and it will be read-only if, and only if, this buffer
     * is read-only.  </p>
     *
     * @return  The new character buffer
     */
    public abstract CharBuffer slice();

    /**
     * Creates a new character buffer that shares this buffer's content.
     *
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer, and vice
     * versa; the two buffers' position, limit, and mark values will be
     * independent.
     *
     * <p> The new buffer's capacity, limit, position, and mark values will be
     * identical to those of this buffer.  The new buffer will be direct if,
     * and only if, this buffer is direct, and it will be read-only if, and
     * only if, this buffer is read-only.  </p>
     *
     * @return  The new character buffer
     */
    public abstract CharBuffer duplicate();

    /**
     * Creates a new, read-only character buffer that shares this buffer's
     * content.
     *
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer; the new
     * buffer itself, however, will be read-only and will not allow the shared
     * content to be modified.  The two buffers' position, limit, and mark
     * values will be independent.
     *
     * <p> The new buffer's capacity, limit, position, and mark values will be
     * identical to those of this buffer.
     *
     * <p> If this buffer is itself read-only then this method behaves in
     * exactly the same way as the {@link #duplicate duplicate} method.  </p>
     *
     * @return  The new, read-only character buffer
     */
    public abstract CharBuffer asReadOnlyBuffer();

    
    // -- Singleton get/put methods --

    /**
     * Relative <i>get</i> method.  Reads the character at this buffer's
     * current position, and then increments the position. </p>
     *
     * @return  The character at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If the buffer's current position is not smaller than its limit
     */
    public abstract char get();

    /**
     * Relative <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given character into this buffer at the current
     * position, and then increments the position. </p>
     *
     * @param  c
     *         The character to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If this buffer's current position is not smaller than its limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract CharBuffer put(char c);

    /**
     * Absolute <i>get</i> method.  Reads the character at the given
     * index. </p>
     *
     * @param  index
     *         The index from which the character will be read
     *
     * @return  The character at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit
     */
    public abstract char get(int index);

    /**
     * Absolute <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given character into this buffer at the given
     * index. </p>
     *
     * @param  index
     *         The index at which the character will be written
     *
     * @param  c
     *         The character value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract CharBuffer put(int index, char c);

    
    // -- Bulk get operations --

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p> This method transfers characters from this buffer into the given
     * destination array.  If there are fewer characters remaining in the
     * buffer than are required to satisfy the request, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>, then no
     * characters are transferred and a {@link BufferUnderflowException} is
     * thrown.
     *
     * <p> Otherwise, this method copies <tt>length</tt> characters from this
     * buffer into the given array, starting at the current position of this
     * buffer and at the given offset in the array.  The position of this
     * buffer is then incremented by <tt>length</tt>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>src.get(dst,&nbsp;off,&nbsp;len)</tt> has exactly the same effect as
     * the loop
     *
     * <pre>
     *     for (int i = off; i < off + len; i++)
     *         dst[i] = src.get(); </pre>
     *
     * except that it first checks that there are sufficient characters in
     * this buffer and it is potentially much more efficient. </p>
     *
     * @param  dst
     *         The array into which characters are to be written
     *
     * @param  offset
     *         The offset within the array of the first character to be
     *         written; must be non-negative and no larger than
     *         <tt>dst.length</tt>
     *
     * @param  length
     *         The maximum number of characters to be written to the given
     *         array; must be non-negative and no larger than
     *         <tt>dst.length - offset</tt>
     *
     * @return  This buffer
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than <tt>length</tt> characters
     *          remaining in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>offset</tt> and <tt>length</tt>
     *          parameters do not hold
     */
    public CharBuffer get(char[] dst, int offset, int length)
    {
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            dst[i] = get();
        return this;
    }

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p> This method transfers characters from this buffer into the given
     * destination array.  An invocation of this method of the form
     * <tt>src.get(a)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     src.get(a, 0, a.length) </pre>
     *
     * @return  This buffer
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than <tt>length</tt> characters
     *          remaining in this buffer
     */
    public CharBuffer get(char[] dst)
    {
        return get(dst, 0, dst.length);
    }

    
    // -- Bulk put operations --

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the characters remaining in the given source
     * buffer into this buffer.  If there are more characters remaining in the
     * source buffer than in this buffer, that is, if
     * <tt>src.remaining()</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>,
     * then no characters are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <i>n</i>&nbsp;=&nbsp;<tt>src.remaining()</tt> characters from the given
     * buffer into this buffer, starting at each buffer's current position.
     * The positions of both buffers are then incremented by <i>n</i>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>dst.put(src)</tt> has exactly the same effect as the loop
     *
     * <pre>
     *     while (src.hasRemaining())
     *         dst.put(src.get()); </pre>
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient. </p>
     *
     * @param  src
     *         The source buffer from which characters are to be read;
     *         must not be this buffer
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the remaining characters in the source buffer
     *
     * @throws  IllegalArgumentException
     *          If the source buffer is this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public CharBuffer put(CharBuffer src)
    {
        if (src == this)
            throw new IllegalArgumentException();
        int n = src.remaining();
        if (n > remaining())
            throw new BufferOverflowException();
        for (int i = 0; i < n; i++)
            put(src.get());
        return this;
    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers characters into this buffer from the given
     * source array.  If there are more characters to be copied from the array
     * than remain in this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>, then no
     * characters are transferred and a {@link BufferOverflowException} is
     * thrown.
     *
     * <p> Otherwise, this method copies <tt>length</tt> characters from the
     * given array into this buffer, starting at the given offset in the array
     * and at the current position of this buffer.  The position of this buffer
     * is then incremented by <tt>length</tt>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>dst.put(src,&nbsp;off,&nbsp;len)</tt> has exactly the same effect as
     * the loop
     *
     * <pre>
     *     for (int i = off; i < off + len; i++)
     *         dst.put(a[i]); </pre>
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient. </p>
     *
     * @param  src
     *         The array from which characters are to be read
     *
     * @param  offset
     *         The offset within the array of the first character to be read;
     *         must be non-negative and no larger than <tt>array.length</tt>
     *
     * @param  length
     *         The number of characters to be read from the given array;
     *         must be non-negative and no larger than
     *         <tt>array.length - offset</tt>
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>offset</tt> and <tt>length</tt>
     *          parameters do not hold
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public CharBuffer put(char[] src, int offset, int length)
    {
        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            this.put(src[i]);
        return this;
    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the entire content of the given source
     * character array into this buffer.  An invocation of this method of the
     * form <tt>dst.put(a)</tt> behaves in exactly the same way as the
     * invocation
     *
     * <pre>
     *     dst.put(a, 0, a.length) </pre>
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public final CharBuffer put(char[] src)
    {
        return put(src, 0, src.length);
    }



    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers characters from the given string into this
     * buffer.  If there are more characters to be copied from the string than
     * remain in this buffer, that is, if
     * <tt>end&nbsp;-&nbsp;start</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>,
     * then no characters are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <i>n</i>&nbsp;=&nbsp;<tt>end</tt>&nbsp;-&nbsp;<tt>start</tt> characters
     * from the given string into this buffer, starting at the given
     * <tt>start</tt> index and at the current position of this buffer.  The
     * position of this buffer is then incremented by <i>n</i>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>dst.put(src,&nbsp;start,&nbsp;end)</tt> has exactly the same effect
     * as the loop
     *
     * <pre>
     *     for (int i = start; i < end; i++)
     *         dst.put(src.charAt(i)); </pre>
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient. </p>
     *
     * @param  src
     *         The string from which characters are to be read
     *
     * @param  start
     *         The offset within the string of the first character to be read;
     *         must be non-negative and no larger than
     *         <tt>string.length()</tt>
     *
     * @param  end
     *         The offset within the string of the last character to be read,
     *         plus one; must be non-negative and no larger than
     *         <tt>string.length()</tt>
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>start</tt> and <tt>end</tt>
     *          parameters do not hold
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public CharBuffer put(String src, int start, int end)
    {
        checkBounds(start, end - start, src.length());
        for (int i = start; i < end; i++)
            this.put(src.charAt(i));
        return this;
    }

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the entire content of the given source string
     * into this buffer.  An invocation of this method of the form
     * <tt>dst.put(s)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     dst.put(s, 0, s.length()) </pre>
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public final CharBuffer put(String src)
    {
        return put(src, 0, src.length());
    }



    
    // -- Other stuff --

    /**
     * Tells whether or not this buffer is backed by an accessible character
     * array.
     *
     * <p> If this method returns <tt>true</tt> then the {@link #array() array}
     * and {@link #arrayOffset() arrayOffset} methods may safely be invoked.
     * </p>
     *
     * @return  <tt>true</tt> if, and only if, this buffer
     *          is backed by an array and is not read-only
     */
    public final boolean hasArray()
    {
        return (hb != null) && !isReadOnly;
    }

    /**
     * Returns the character array that backs this
     * buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Modifications to this buffer's content will cause the returned
     * array's content to be modified, and vice versa.
     *
     * <p> Invoke the {@link #hasArray hasArray} method before invoking this
     * method in order to ensure that this buffer has an accessible backing
     * array.  </p>
     *
     * @return  The array that backs this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is backed by an array but is read-only
     *
     * @throws  UnsupportedOperationException
     *          If this buffer is not backed by an accessible array
     */
    public final char[] array()
    {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return hb;
    }

    /**
     * Returns the offset within this buffer's backing array of the first
     * element of the buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> If this buffer is backed by an array then buffer position <i>p</i>
     * corresponds to array index <i>p</i>&nbsp;+&nbsp;<tt>arrayOffset()</tt>.
     *
     * <p> Invoke the {@link #hasArray hasArray} method before invoking this
     * method in order to ensure that this buffer has an accessible backing
     * array.  </p>
     *
     * @return  The offset within this buffer's array
     *          of the first element of the buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is backed by an array but is read-only
     *
     * @throws  UnsupportedOperationException
     *          If this buffer is not backed by an accessible array
     */
    public final int arrayOffset()
    {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return offset;
    }

    /**
     * Compacts this buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> The characters between the buffer's current position and its limit,
     * if any, are copied to the beginning of the buffer.  That is, the
     * character at index <i>p</i>&nbsp;=&nbsp;<tt>position()</tt> is copied
     * to index zero, the character at index <i>p</i>&nbsp;+&nbsp;1 is copied
     * to index one, and so forth until the character at index
     * <tt>limit()</tt>&nbsp;-&nbsp;1 is copied to index
     * <i>n</i>&nbsp;=&nbsp;<tt>limit()</tt>&nbsp;-&nbsp;<tt>1</tt>&nbsp;-&nbsp;<i>p</i>.
     * The buffer's position is then set to <i>n+1</i> and its limit is set to
     * its capacity.  The mark, if defined, is discarded.
     *
     * <p> The buffer's position is set to the number of characters copied,
     * rather than to zero, so that an invocation of this method can be
     * followed immediately by an invocation of another relative <i>put</i>
     * method. </p>
     *















     *
     * @return  This buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract CharBuffer compact();

    /**
     * Tells whether or not this character buffer is direct. </p>
     *
     * @return  <tt>true</tt> if, and only if, this buffer is direct
     */
    public abstract boolean isDirect();


























    /**
     * Returns the current hash code of this buffer.
     *
     * <p> The hash code of a char buffer depends only upon its remaining
     * elements; that is, upon the elements from <tt>position()</tt> up to, and
     * including, the element at <tt>limit()</tt>&nbsp;-&nbsp;<tt>1</tt>.
     *
     * <p> Because buffer hash codes are content-dependent, it is inadvisable
     * to use buffers as keys in hash maps or similar data structures unless it
     * is known that their contents will not change.  </p>
     *
     * @return  The current hash code of this buffer
     */
    public int hashCode()
    {
        int h = 1;
        int p = position();
        for (int i = limit() - 1; i >= p; i--)
            h = 31 * h + (int)get(i);
        return h;
    }

    /**
     * Tells whether or not this buffer is equal to another object.
     *
     * <p> Two char buffers are equal if, and only if,
     *
     * <p><ol>
     *
     *   <li><p> They have the same element type,  </p></li>
     *
     *   <li><p> They have the same number of remaining elements, and
     *   </p></li>
     *
     *   <li><p> The two sequences of remaining elements, considered
     *   independently of their starting positions, are pointwise equal.
     *   </p></li>
     *
     * </ol>
     *
     * <p> A char buffer is not equal to any other type of object.  </p>
     *
     * @param  ob  The object to which this buffer is to be compared
     *
     * @return  <tt>true</tt> if, and only if, this buffer is equal to the
     *           given object
     */
    public boolean equals(Object ob)
    {
        if (!(ob instanceof CharBuffer))
            return false;
        CharBuffer that = (CharBuffer)ob;
        if (this.remaining() != that.remaining())
            return false;
        int p = this.position();
        for (int i = this.limit() - 1, j = that.limit() - 1; i >= p; i--, j--)
        {
            char v1 = this.get(i);
            char v2 = that.get(j);
            if (v1 != v2)
            {
                if ((v1 != v1) && (v2 != v2))	// For float and double
                    continue;
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this buffer to another.
     *
     * <p> Two char buffers are compared by comparing their sequences of
     * remaining elements lexicographically, without regard to the starting
     * position of each sequence within its corresponding buffer.
     *
     * <p> A char buffer is not comparable to any other type of object.
     *
     * @return  A negative integer, zero, or a positive integer as this buffer
     *		is less than, equal to, or greater than the given buffer
     */
    public int compareTo(CharBuffer that)
    {
        int n = this.position() + Math.min(this.remaining(), that.remaining());
        for (int i = this.position(), j = that.position(); i < n; i++, j++)
        {
            char v1 = this.get(i);
            char v2 = that.get(j);
            if (v1 == v2)
                continue;
            if ((v1 != v1) && (v2 != v2)) 	// For float and double
                continue;
            if (v1 < v2)
                return -1;
            return +1;
        }
        return this.remaining() - that.remaining();
    }


    
    // -- Other char stuff --



    /**
     * Returns a string containing the characters in this buffer.
     *
     * <p> The first character of the resulting string will be the character at
     * this buffer's position, while the last character will be the character
     * at index <tt>limit()</tt>&nbsp;-&nbsp;1.  Invoking this method does not
     * change the buffer's position. </p>
     *
     * @return  The specified string
     */
    public String toString()
    {
        return toString(position(), limit());
    }

    abstract String toString(int start, int end);	// package-private


    // --- Methods to support CharSequence ---

    /**
     * Returns the length of this character buffer.
     *
     * <p> When viewed as a character sequence, the length of a character
     * buffer is simply the number of characters between the position
     * (inclusive) and the limit (exclusive); that is, it is equivalent to
     * <tt>remaining()</tt>. </p>
     *
     * @return  The length of this character buffer
     */
    public final int length()
    {
        return remaining();
    }

    /**
     * Reads the character at the given index relative to the current
     * position. </p>
     *
     * @param  index
     *         The index of the character to be read, relative to the position;
     *         must be non-negative and smaller than <tt>remaining()</tt>
     *
     * @return  The character at index
     *          <tt>position()&nbsp;+&nbsp;index</tt>
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on <tt>index</tt> do not hold
     */
    public final char charAt(int index)
    {
        return get(position() + checkIndex(index, 1));
    }

    /**
     * Creates a new character buffer that represents the specified subsequence
     * of this buffer, relative to the current position.
     *
     * <p> The new buffer will share this buffer's content; that is, if the
     * content of this buffer is mutable then modifications to one buffer will
     * cause the other to be modified.  The new buffer's capacity will be that
     * of this buffer, its position will be
     * <tt>position()</tt>&nbsp;+&nbsp;<tt>start</tt>, and its limit will be
     * <tt>position()</tt>&nbsp;+&nbsp;<tt>end</tt>.  The new buffer will be
     * direct if, and only if, this buffer is direct, and it will be read-only
     * if, and only if, this buffer is read-only.  </p>
     *
     * @param  start
     *         The index, relative to the current position, of the first
     *         character in the subsequence; must be non-negative and no larger
     *         than <tt>remaining()</tt>
     *
     * @param  end
     *         The index, relative to the current position, of the character
     *         following the last character in the subsequence; must be no
     *         smaller than <tt>start</tt> and no larger than
     *         <tt>remaining()</tt>
     *
     * @return  The new character sequence
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on <tt>start</tt> and <tt>end</tt>
     *          do not hold
     */
    public abstract CharSequence subSequence(int start, int end);


    // --- Methods to support Appendable ---

    /**
     * Appends the specified character sequence  to this
     * buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> An invocation of this method of the form <tt>dst.append(csq)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     dst.put(csq.toString()) </pre>
     *
     * <p> Depending on the specification of <tt>toString</tt> for the
     * character sequence <tt>csq</tt>, the entire sequence may not be
     * appended.  For instance, invoking the {@link CharBuffer#toString()
     * toString} method of a character buffer will return a subsequence whose
     * content depends upon the buffer's position and limit.
     *
     * @param  csq
     *         The character sequence to append.  If <tt>csq</tt> is
     *         <tt>null</tt>, then the four characters <tt>"null"</tt> are
     *         appended to this character buffer.
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     *
     * @since  1.5
     */
    public CharBuffer append(CharSequence csq)
    {
        if (csq == null)
            return put("null");
        else
            return put(csq.toString());
    }

    /**
     * Appends a subsequence of the  specified character sequence  to this
     * buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> An invocation of this method of the form <tt>dst.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in exactly the
     * same way as the invocation
     *
     * <pre>
     *     dst.put(csq.subSequence(start, end).toString()) </pre>
     *
     * @param  csq
     *         The character sequence from which a subsequence will be
     *         appended.  If <tt>csq</tt> is <tt>null</tt>, then characters
     *         will be appended as if <tt>csq</tt> contained the four
     *         characters <tt>"null"</tt>.
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *          is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *          <tt>csq.length()</tt>
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     *
     * @since  1.5
     */
    public CharBuffer append(CharSequence csq, int start, int end)
    {
        CharSequence cs = (csq == null ? "null" : csq);
        return put(cs.subSequence(start, end).toString());
    }

    /**
     * Appends the specified character  to this
     * buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> An invocation of this method of the form <tt>dst.append(c)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     dst.put(c) </pre>
     *
     * @param  c
     *         The 16-bit character to append
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     *
     * @since  1.5
     */
    public CharBuffer append(char c)
    {
        return put(c);
    }



    
    // -- Other byte stuff: Access to binary data --



    /**
     * Retrieves this buffer's byte order.
     *
     * <p> The byte order of a character buffer created by allocation or by
     * wrapping an existing <tt>char</tt> array is the {@link
     * ByteOrder#nativeOrder </code>native order<code>} of the underlying
     * hardware.  The byte order of a character buffer created as a <a
     * href="ByteBuffer.html#views">view</a> of a byte buffer is that of the
     * byte buffer at the moment that the view is created.  </p>
     *
     * @return  This buffer's byte order
     */
    public abstract ByteOrder order();





















































}
