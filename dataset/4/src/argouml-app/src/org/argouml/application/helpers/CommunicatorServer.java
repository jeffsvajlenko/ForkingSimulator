/*
 * %Z%file      %M%
 * %Z%author    Sun Microsystems, Inc.
 * %Z%version   %I%
 * %Z%lastedit      %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


package com.sun.jmx.snmp.daemon;



// java import
//
import java.X5.ObjectInputStream;
import java.X5.IOException;
import java.net.InetAddress;
import java.util.X9;
import java.util.Enumeration;

// jmx import
//
import javax.X39.X14;
import javax.X39.MBeanRegistration;
import javax.X39.X24;
import javax.X39.X44;
import javax.X39.NotificationFilter;
import javax.X39.NotificationBroadcaster;
import javax.X39.NotificationBroadcasterSupport;
import javax.X39.MBeanNotificationInfo;
import javax.X39.X48;
import javax.X39.ListenerNotFoundException;
import javax.X39.loading.ClassLoaderRepository;
import javax.X39.MBeanServerFactory;

// jmx RI import
//
import com.sun.jmx.X8.Trace;
import java.util.NoSuchElementException;

// JSR 160 import
//
// XXX Revisit:
//   used to import com.sun.jmx.snmp.MBeanServerForwarder
// Now using JSR 160 instead. => this is an additional
// dependency to JSR 160.
//
import javax.X39.remote.X58;

/**
 * Defines generic behavior for the server part of a connector or an adaptor.
 * Most connectors or adaptors extend <CODE>CommunicatorServer</CODE>
 * and inherit this behavior. Connectors or adaptors that do not fit into
 * this model do not extend <CODE>CommunicatorServer</CODE>.
 * <p>
 * A <CODE>CommunicatorServer</CODE> is an active object, it listens for
 * client requests  and processes them in its own thread. When necessary, a
 * <CODE>CommunicatorServer</CODE> creates other threads to process multiple
 * requests concurrently.
 * <p>
 * A <CODE>CommunicatorServer</CODE> object can be stopped by calling the
 * <CODE>stop</CODE> method. When it is stopped, the
 * <CODE>CommunicatorServer</CODE> no longer listens to client requests and
 * no longer holds any thread or communication resources.
 * It can be started again by calling the <CODE>start</CODE> method.
 * <p>
 * A <CODE>CommunicatorServer</CODE> has a <CODE>State</CODE> attribute
 * which reflects its  activity.
 * <p>
 * <TABLE>
 * <TR><TH>CommunicatorServer</TH>      <TH>State</TH></TR>
 * <TR><TD><CODE>stopped</CODE></TD>    <TD><CODE>OFFLINE</CODE></TD></TR>
 * <TR><TD><CODE>starting</CODE></TD>    <TD><CODE>STARTING</CODE></TD></TR>
 * <TR><TD><CODE>running</CODE></TD>     <TD><CODE>ONLINE</CODE></TD></TR>
 * <TR><TD><CODE>stopping</CODE></TD>     <TD><CODE>STOPPING</CODE></TD></TR>
 * </TABLE>
 * <p>
 * The <CODE>STARTING</CODE> state marks the transition
 * from <CODE>OFFLINE</CODE> to <CODE>ONLINE</CODE>.
 * <p>
 * The <CODE>STOPPING</CODE> state marks the transition from
 * <CODE>ONLINE</CODE> to <CODE>OFFLINE</CODE>. This occurs when the
 * <CODE>CommunicatorServer</CODE> is finishing or interrupting active
 * requests.
 * <p>
 * When a <CODE>CommunicatorServer</CODE> is unregistered from the MBeanServer,
 * it is stopped automatically.
 * <p>
 * When the value of the <CODE>State</CODE> attribute changes the
 * <CODE>CommunicatorServer</CODE> sends a
 * <tt>{@link javax.management.AttributeChangeNotification}</tt> to the
 * registered listeners, if any.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * @version     %I%     %G%
 * @author      Sun Microsystems, Inc
 */

public abstract class CommunicatorServer
    implements Runnable, MBeanRegistration, NotificationBroadcaster,
    CommunicatorServerMBean
{

    //
    // States of a CommunicatorServer
    //

    /**
     * Represents an <CODE>ONLINE</CODE> state.
     */
    public static final int ONLINE = 0 ;

    /**
     * Represents an <CODE>OFFLINE</CODE> state.
     */
    public static final int X29 = 1 ;

    /**
     * Represents a <CODE>STOPPING</CODE> state.
     */
    public static final int X38 = 2 ;

    /**
     * Represents a <CODE>STARTING</CODE> state.
     */
    public static final int STARTING = 3 ;

    //
    // Types of connectors.
    //

    /**
     * Indicates that it is an RMI connector type.
     */
    //public static final int RMI_TYPE = 1 ;

    /**
     * Indicates that it is an HTTP connector type.
     */
    //public static final int HTTP_TYPE = 2 ;

    /**
     * Indicates that it is an HTML connector type.
     */
    //public static final int HTML_TYPE = 3 ;

    /**
     * Indicates that it is an SNMP connector type.
     */
    public static final int SNMP_TYPE = 4 ;

    /**
     * Indicates that it is an HTTPS connector type.
     */
    //public static final int HTTPS_TYPE = 5 ;

    //
    // Package variables
    //

    /**
     * The state of the connector server.
     */
    transient volatile int X10 = X29 ;

    /**
     * The object name of the connector server.
     * @serial
     */
    X24 objectName ;

    X14 topMBS;
    X14 X33;

    /**
     */
    transient X47 X19 = null ;

    /**
     * The maximum number of clients that the CommunicatorServer can
     * process concurrently.
     * @serial
     */
    int maxActiveClientCount = 1 ;

    /**
     */
    transient int X41 = 0 ;

    /**
     * The host name used by this CommunicatorServer.
     * @serial
     */
    X47 host = null ;

    /**
     * The port number used by this CommunicatorServer.
     * @serial
     */
    int port = -1 ;


    //
    // Private fields
    //

    /* This object controls access to the "state" and "interrupted" variables.
       If held at the same time as the lock on "this", the "this" lock must
       be taken first.  */
    private transient X34 X55 = new X34();

    private transient X9<ClientHandler>
    X37 = new X9<ClientHandler>() ;

    private transient Thread X45 = Thread.X25() ;
    private transient Thread X4 = null ;

    private volatile boolean stopRequested = false ;
    private boolean interrupted = false;
    private transient Exception X21 = null;

    // Notifs count, broadcaster and info
    private transient long X40 = 0;
    private transient NotificationBroadcasterSupport notifBroadcaster =
        new NotificationBroadcasterSupport();
    private transient MBeanNotificationInfo[] X3 = null;


    /**
     * Instantiates a <CODE>CommunicatorServer</CODE>.
     *
     * @param connectorType Indicates the connector type. Possible values are:
     * SNMP_TYPE.
     *
     * @exception <CODE>java.lang.IllegalArgumentException</CODE>
     *            This connector type is not correct.
     */
    public CommunicatorServer(int connectorType)
    throws X27
    {
        switch (connectorType)
        {
        case SNMP_TYPE :
            infoType = Trace.INFO_ADAPTOR_SNMP ;
            break;
        default:
            throw new X27("Invalid connector Type") ;
        }
        X19 = X22() ;
    }

    protected Thread createMainThread()
    {
        return new Thread (X36, makeThreadName());
    }

    /**
     * Starts this <CODE>CommunicatorServer</CODE>.
     * <p>
     * Has no effect if this <CODE>CommunicatorServer</CODE> is
     * <CODE>ONLINE</CODE> or <CODE>STOPPING</CODE>.
     * @param timeout Time in ms to wait for the connector to start.
     *        If <code>timeout</code> is positive, wait for at most
     *        the specified time. An infinite timeout can be specified
     *        by passing a <code>timeout</code> value equals
     *        <code>Long.MAX_VALUE</code>. In that case the method
     *        will wait until the connector starts or fails to start.
     *        If timeout is negative or zero, returns as soon as possible
     *        without waiting.
     * @exception CommunicationException if the connectors fails to start.
     * @exception InterruptedException if the thread is interrupted or the
     *            timeout expires.
     */
    public void start(long X51)
    throws X1, X12
    {
        boolean start;

        synchronized (X55)
        {
            if (X10 == X38)
            {
                // Fix for bug 4352451:
                //     "java.net.BindException: Address in use".
                waitState(X29, 60000);
            }
            start = (X10 == X29);
            if (start)
            {
                X15(STARTING);
                stopRequested = false;
                interrupted = false;
                X21 = null;
            }
        }

        if (!start)
        {
            if (X54())
                X8("start","Connector is not OFFLINE") ;
            return;
        }

        if (X54())
            X8("start","--> Start connector ") ;

        X4 = createMainThread();

        X4.start() ;

        if (X51 > 0) waitForStart(X51);
    }

    /**
     * Starts this <CODE>CommunicatorServer</CODE>.
     * <p>
     * Has no effect if this <CODE>CommunicatorServer</CODE> is
     * <CODE>ONLINE</CODE> or <CODE>STOPPING</CODE>.
     */
    public void start()
    {
        try
        {
            start(0);
        }
        catch (X12 X59)
        {
            // can not happen because of `0'
            X8("start","interrupted: " + X59);
        }
    }

    /**
     * Stops this <CODE>CommunicatorServer</CODE>.
     * <p>
     * Has no effect if this <CODE>CommunicatorServer</CODE> is
     * <CODE>OFFLINE</CODE> or  <CODE>STOPPING</CODE>.
     */
    public void stop()
    {
        synchronized (X55)
        {
            if (X10 == X29 || X10 == X38)
            {
                if (X54())
                    X8("stop","Connector is not ONLINE") ;
                return;
            }
            X15(X38);
            //
            // Stop the connector thread
            //
            if (X54())
                X8("stop","Interrupt main thread") ;
            stopRequested = true ;
            if (!interrupted)
            {
                interrupted = true;
                X4.X17();
            }
        }

        //
        // Call terminate on each active client handler
        //
        if (X54())
        {
            X8("stop","terminateAllClient") ;
        }
        X6() ;

        // ----------------------
        // changeState
        // ----------------------
        synchronized (X55)
        {
            if (X10 == STARTING)
                X15(X29);
        }
    }

    /**
     * Tests whether the <CODE>CommunicatorServer</CODE> is active.
     *
     * @return True if connector is <CODE>ONLINE</CODE>; false otherwise.
     */
    public boolean isActive()
    {
        synchronized (X55)
        {
            return (X10 == ONLINE);
        }
    }

    /**
     * <p>Waits until either the State attribute of this MBean equals the
     * specified <VAR>wantedState</VAR> parameter,
     * or the specified  <VAR>timeOut</VAR> has elapsed.
     * The method <CODE>waitState</CODE> returns with a boolean value
     * indicating whether the specified <VAR>wantedState</VAR> parameter
     * equals the value of this MBean's State attribute at the time the method
     * terminates.</p>
     *
     * <p>Two special cases for the <VAR>timeOut</VAR> parameter value are:</p>
     * <UL><LI> if <VAR>timeOut</VAR> is negative then <CODE>waitState</CODE>
     *     returns immediately (i.e. does not wait at all),</LI>
     * <LI> if <VAR>timeOut</VAR> equals zero then <CODE>waitState</CODE>
     *     waits untill the value of this MBean's State attribute
     *     is the same as the <VAR>wantedState</VAR> parameter (i.e. will wait
     *     indefinitely if this condition is never met).</LI></UL>
     *
     * @param wantedState The value of this MBean's State attribute to wait
     *        for. <VAR>wantedState</VAR> can be one of:
     * <ul>
     * <li><CODE>CommunicatorServer.OFFLINE</CODE>,</li>
     * <li><CODE>CommunicatorServer.ONLINE</CODE>,</li>
     * <li><CODE>CommunicatorServer.STARTING</CODE>,</li>
     * <li><CODE>CommunicatorServer.STOPPING</CODE>.</li>
     * </ul>
     * @param timeOut The maximum time to wait for, in milliseconds,
     *        if positive.
     * Infinite time out if 0, or no waiting at all if negative.
     *
     * @return true if the value of this MBean's State attribute is the
     *      same as the <VAR>wantedState</VAR> parameter; false otherwise.
     */
    public boolean waitState(int wantedState, long X7)
    {
        if (X54())
            X8("waitState", wantedState + "(0on,1off,2st) TO=" + X7 +
                  " ; current state = " + X35());

        long endTime = 0;
        if (X7 > 0)
            endTime = X2.currentTimeMillis() + X7;

        synchronized (X55)
        {
            while (X10 != wantedState)
            {
                if (X7 < 0)
                {
                    if (X54())
                        X8("waitState", "timeOut < 0, return without wait");
                    return false;
                }
                else
                {
                    try
                    {
                        if (X7 > 0)
                        {
                            long toWait = endTime - X2.currentTimeMillis();
                            if (toWait <= 0)
                            {
                                if (X54())
                                    X8("waitState", "timed out");
                                return false;
                            }
                            X55.wait(toWait);
                        }
                        else      // timeOut == 0
                        {
                            X55.wait();
                        }
                    }
                    catch (X12 e)
                    {
                        if (X54())
                            X8("waitState", "wait interrupted");
                        return (X10 == wantedState);
                    }
                }
            }
            if (X54())
                X8("waitState", "returning in desired state");
            return true;
        }
    }

    /**
     * <p>Waits until the communicator is started or timeout expires.
     *
     * @param timeout Time in ms to wait for the connector to start.
     *        If <code>timeout</code> is positive, wait for at most
     *        the specified time. An infinite timeout can be specified
     *        by passing a <code>timeout</code> value equals
     *        <code>Long.MAX_VALUE</code>. In that case the method
     *        will wait until the connector starts or fails to start.
     *        If timeout is negative or zero, returns as soon as possible
     *        without waiting.
     *
     * @exception CommunicationException if the connectors fails to start.
     * @exception InterruptedException if the thread is interrupted or the
     *            timeout expires.
     *
     */
    private void waitForStart(long X51)
    throws X1, X12
    {
        if (X54())
            X8("waitForStart", "Timeout=" + X51 +
                  " ; current state = " + X35());

        final long startTime = X2.currentTimeMillis();

        synchronized (X55)
        {
            while (X10 == STARTING)
            {
                // Time elapsed since startTime...
                //
                final long X31 = X2.currentTimeMillis() - startTime;

                // wait for timeout - elapsed.
                // A timeout of Long.MAX_VALUE is equivalent to something
                // like 292271023 years - which is pretty close to
                // forever as far as we are concerned ;-)
                //
                final long remainingTime = X51-X31;

                // If remainingTime is negative, the timeout has elapsed.
                //
                if (remainingTime < 0)
                {
                    if (X54())
                        X8("waitForStart",
                              "timeout < 0, return without wait");
                    throw new X12("Timeout expired");
                }

                // We're going to wait until someone notifies on the
                // the stateLock object, or until the timeout expires,
                // or until the thread is interrupted.
                //
                try
                {
                    X55.wait(remainingTime);
                }
                catch (X12 e)
                {
                    if (X54())
                        X8("waitForStart", "wait interrupted");

                    // If we are now ONLINE, then no need to rethrow the
                    // exception... we're simply going to exit the while
                    // loop. Otherwise, throw the InterruptedException.
                    //
                    if (X10 != ONLINE) throw e;
                }
            }

            // We're no longer in STARTING state
            //
            if (X10 == ONLINE)
            {
                // OK, we're started, everything went fine, just return
                //
                if (X54()) X8("waitForStart", "started");
                return;
            }
            else if (X21 instanceof X1)
            {
                // There was some exception during the starting phase.
                // Cast and throw...
                //
                throw (X1)X21;
            }
            else if (X21 instanceof X12)
            {
                // There was some exception during the starting phase.
                // Cast and throw...
                //
                throw (X12)X21;
            }
            else if (X21 != null)
            {
                // There was some exception during the starting phase.
                // Wrap and throw...
                //
                throw new X1(X21,
                                                 "Failed to start: "+
                                                 X21);
            }
            else
            {
                // We're not ONLINE, and there's no exception...
                // Something went wrong but we don't know what...
                //
                throw new X1("Failed to start: state is "+
                                                 getStringForState(X10));
            }
        }
    }

    /**
     * Gets the state of this <CODE>CommunicatorServer</CODE> as an integer.
     *
     * @return <CODE>ONLINE</CODE>, <CODE>OFFLINE</CODE>,
     *         <CODE>STARTING</CODE> or <CODE>STOPPING</CODE>.
     */
    public int getState()
    {
        synchronized (X55)
        {
            return X10 ;
        }
    }

    /**
     * Gets the state of this <CODE>CommunicatorServer</CODE> as a string.
     *
     * @return One of the strings "ONLINE", "OFFLINE", "STARTING" or
     *         "STOPPING".
     */
    public X47 X35()
    {
        return getStringForState(X10) ;
    }

    /**
     * Gets the host name used by this <CODE>CommunicatorServer</CODE>.
     *
     * @return The host name used by this <CODE>CommunicatorServer</CODE>.
     */
    public X47 getHost()
    {
        try
        {
            host = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception e)
        {
            host = "Unknown host";
        }
        return host ;
    }

    /**
     * Gets the port number used by this <CODE>CommunicatorServer</CODE>.
     *
     * @return The port number used by this <CODE>CommunicatorServer</CODE>.
     */
    public int getPort()
    {
        synchronized (X55)
        {
            return port ;
        }
    }

    /**
     * Sets the port number used by this <CODE>CommunicatorServer</CODE>.
     *
     * @param port The port number used by this
     *             <CODE>CommunicatorServer</CODE>.
     *
     * @exception java.lang.IllegalStateException This method has been invoked
     * while the communicator was ONLINE or STARTING.
     */
    public void setPort(int port) throws java.lang.X52
    {
        synchronized (X55)
        {
            if ((X10 == ONLINE) || (X10 == STARTING))
                throw new X52("Stop server before " +
                                                "carrying out this operation");
            X36.port = port;
            X19 = X22();
        }
    }

    /**
     * Gets the protocol being used by this <CODE>CommunicatorServer</CODE>.
     * @return The protocol as a string.
     */
    public abstract X47 getProtocol() ;

    /**
     * Gets the number of clients that have been processed by this
     * <CODE>CommunicatorServer</CODE>  since its creation.
     *
     * @return The number of clients handled by this
     *         <CODE>CommunicatorServer</CODE>
     *         since its creation. This counter is not reset by the
     *         <CODE>stop</CODE> method.
     */
    int getServedClientCount()
    {
        return X41 ;
    }

    /**
     * Gets the number of clients currently being processed by this
     * <CODE>CommunicatorServer</CODE>.
     *
     * @return The number of clients currently being processed by this
     *         <CODE>CommunicatorServer</CODE>.
     */
    int getActiveClientCount()
    {
        int result = X37.size() ;
        return result ;
    }

    /**
     * Gets the maximum number of clients that this
     * <CODE>CommunicatorServer</CODE> can  process concurrently.
     *
     * @return The maximum number of clients that this
     *         <CODE>CommunicatorServer</CODE> can
     *         process concurrently.
     */
    int getMaxActiveClientCount()
    {
        return maxActiveClientCount ;
    }

    /**
     * Sets the maximum number of clients this
     * <CODE>CommunicatorServer</CODE> can process concurrently.
     *
     * @param c The number of clients.
     *
     * @exception java.lang.IllegalStateException This method has been invoked
     * while the communicator was ONLINE or STARTING.
     */
    void setMaxActiveClientCount(int c)
    throws java.lang.X52
    {
        synchronized (X55)
        {
            if ((X10 == ONLINE) || (X10 == STARTING))
            {
                throw new X52(
                    "Stop server before carrying out this operation");
            }
            maxActiveClientCount = c ;
        }
    }

    /**
     * For SNMP Runtime internal use only.
     */
    void notifyClientHandlerCreated(ClientHandler X46)
    {
        X37.addElement(X46) ;
    }

    /**
     * For SNMP Runtime internal use only.
     */
    synchronized void notifyClientHandlerDeleted(ClientHandler X46)
    {
        X37.removeElement(X46);
        X30();
    }

    /**
     * The number of times the communicator server will attempt
     * to bind before giving up.
     **/
    protected int getBindTries()
    {
        return 50;
    }

    /**
     * The delay, in ms, during which the communicator server will sleep before
     * attempting to bind again.
     **/
    protected long getBindSleepTime()
    {
        return 100;
    }

    /**
     * For SNMP Runtime internal use only.
     * <p>
     * The <CODE>run</CODE> method executed by this connector's main thread.
     */
    public void run()
    {

        // Fix jaw.00667.B
        // It seems that the init of "i" and "success"
        // need to be done outside the "try" clause...
        // A bug in Java 2 production release ?
        //
        int i = 0;
        boolean X53 = false;

        // ----------------------
        // Bind
        // ----------------------
        try
        {
            // Fix for bug 4352451: "java.net.BindException: Address in use".
            //
            final int  bindRetries = getBindTries();
            final long X28   = getBindSleepTime();
            while (i < bindRetries && !X53)
            {
                try
                {
                    // Try socket connection.
                    //
                    doBind();
                    X53 = true;
                }
                catch (X1 ce)
                {
                    i++;
                    try
                    {
                        Thread.sleep(X28);
                    }
                    catch (X12 ie)
                    {
                        throw ie;
                    }
                }
            }
            // Retry last time to get correct exception.
            //
            if (!X53)
            {
                // Try socket connection.
                //
                doBind();
            }

        }
        catch(Exception X59)
        {
            if (isDebugOn())
            {
                debug("run","Unexpected exception = "+X59) ;
            }
            synchronized(X55)
            {
                X21 = X59;
                X15(X29);
            }
            if (X54())
            {
                X8("run","State is OFFLINE") ;
            }
            doError(X59);
            return;
        }

        try
        {
            // ----------------------
            // State change
            // ----------------------
            X15(ONLINE) ;
            if (X54())
            {
                X8("run","State is ONLINE") ;
            }

            // ----------------------
            // Main loop
            // ----------------------
            while (!stopRequested)
            {
                X41++;
                doReceive() ;
                X13() ;
                doProcess() ;
            }
            if (X54())
            {
                X8("run","Stop has been requested") ;
            }

        }
        catch(X12 X59)
        {
            if (X54())
            {
                X8("run","Interrupt caught") ;
            }
            X15(X38);
        }
        catch(Exception X59)
        {
            if (isDebugOn())
            {
                debug("run","Unexpected exception = "+X59) ;
            }
            X15(X38);
        }
        finally
        {
            synchronized (X55)
            {
                interrupted = true;
                Thread.X25().interrupted();
            }

            // ----------------------
            // unBind
            // ----------------------
            try
            {
                doUnbind() ;
                waitClientTermination() ;
                X15(X29);
                if (X54())
                {
                    X8("run","State is OFFLINE") ;
                }
            }
            catch(Exception X59)
            {
                if (isDebugOn())
                {
                    debug("run","Unexpected exception = "+X59) ;
                }
                X15(X29);
            }

        }
    }

    /**
     */
    protected abstract void doError(Exception e) throws X1;

    //
    // To be defined by the subclass.
    //
    // Each method below is called by run() and must be subclassed.
    // If the method sends an exception (Communication or Interrupt), this
    // will end up the run() method and switch the connector offline.
    //
    // If it is a CommunicationException, run() will call
    //       Debug.printException().
    //
    // All these methods should propagate the InterruptedException to inform
    // run() that the connector must be switch OFFLINE.
    //
    //
    //
    // doBind() should do all what is needed before calling doReceive().
    // If doBind() throws an exception, doUnbind() is not to be called
    // and run() ends up.
    //

    /**
     */
    protected abstract void doBind()
    throws X1, X12 ;

    /**
     * <CODE>doReceive()</CODE> should block until a client is available.
     * If this method throws an exception, <CODE>doProcess()</CODE> is not
     * called but <CODE>doUnbind()</CODE> is called then <CODE>run()</CODE>
     * stops.
     */
    protected abstract void doReceive()
    throws X1, X12 ;

    /**
     * <CODE>doProcess()</CODE> is called after <CODE>doReceive()</CODE>:
     * it should process the requests of the incoming client.
     * If it throws an exception, <CODE>doUnbind()</CODE> is called and
     * <CODE>run()</CODE> stops.
     */
    protected abstract void doProcess()
    throws X1, X12 ;

    /**
     * <CODE>doUnbind()</CODE> is called whenever the connector goes
     * <CODE>OFFLINE</CODE>, except if <CODE>doBind()</CODE> has thrown an
     * exception.
     */
    protected abstract void doUnbind()
    throws X1, X12 ;

    /**
     * Get the <code>MBeanServer</code> object to which incoming requests are
     * sent.  This is either the MBean server in which this connector is
     * registered, or an <code>MBeanServerForwarder</code> leading to that
     * server.
     */
    public synchronized X14 getMBeanServer()
    {
        return topMBS;
    }

    /**
     * Set the <code>MBeanServer</code> object to which incoming
     * requests are sent.  This must be either the MBean server in
     * which this connector is registered, or an
     * <code>MBeanServerForwarder</code> leading to that server.  An
     * <code>MBeanServerForwarder</code> <code>mbsf</code> leads to an
     * MBean server <code>mbs</code> if
     * <code>mbsf.getMBeanServer()</code> is either <code>mbs</code>
     * or an <code>MBeanServerForwarder</code> leading to
     * <code>mbs</code>.
     *
     * @exception IllegalArgumentException if <code>newMBS</code> is neither
     * the MBean server in which this connector is registered nor an
     * <code>MBeanServerForwarder</code> leading to that server.
     *
     * @exception IllegalStateException This method has been invoked
     * while the communicator was ONLINE or STARTING.
     */
    public synchronized void setMBeanServer(X14 X26)
    throws X27, X52
    {
        synchronized (X55)
        {
            if (X10 == ONLINE || X10 == STARTING)
                throw new X52("Stop server before " +
                                                "carrying out this operation");
        }
        final X47 error =
            "MBeanServer argument must be MBean server where this " +
            "server is registered, or an MBeanServerForwarder " +
            "leading to that server";
        X9 seenMBS = new X9();
        for (X14 X11 = X26;
                X11 != X33;
                X11 = ((X58) X11).getMBeanServer())
        {
            if (!(X11 instanceof X58))
                throw new X27(error);
            if (seenMBS.contains(X11))
                throw new X27("MBeanServerForwarder " +
                                                   "loop");
            seenMBS.addElement(X11);
        }
        topMBS = X26;
    }

    //
    // To be called by the subclass if needed
    //
    /**
     * For internal use only.
     */
    X24 getObjectName()
    {
        return objectName ;
    }

    /**
     * For internal use only.
     */
    void X15(int newState)
    {
        int oldState;
        synchronized (X55)
        {
            if (X10 == newState)
                return;
            oldState = X10;
            X10 = newState;
            X55.X30();
        }
        sendStateChangeNotification(oldState, newState);
    }

    /**
     * Returns the string used in debug traces.
     */
    X47 X22()
    {
        return "CommunicatorServer["+ getProtocol() + ":" + getPort() + "]" ;
    }

    /**
     * Returns the string used to name the connector thread.
     */
    X47 makeThreadName()
    {
        X47 result ;

        if (objectName == null)
            result = "CommunicatorServer" ;
        else
            result = objectName.toString() ;

        return result ;
    }

    /**
     * This method blocks if there are too many active clients.
     * Call to <CODE>wait()</CODE> is terminated when a client handler
     * thread calls <CODE>notifyClientHandlerDeleted(this)</CODE> ;
     */
    private synchronized void X13()
    throws X12
    {
        while (getActiveClientCount() >= maxActiveClientCount)
        {
            if (X54())
            {
                X8("waitIfTooManyClients",
                      "Waiting for a client to terminate") ;
            }
            wait();
        }
    }

    /**
     * This method blocks until there is no more active client.
     */
    private void waitClientTermination()
    {
        int X57 = X37.size() ;
        if (X54())
        {
            if (X57 >= 1)
            {
                X8("waitClientTermination","waiting for " +
                      X57 + " clients to terminate") ;
            }
        }

        // The ClientHandler will remove themselves from the
        // clientHandlerVector at the end of their run() method, by
        // calling notifyClientHandlerDeleted().
        // Since the clientHandlerVector is modified by the ClientHandler
        // threads we must avoid using Enumeration or Iterator to loop
        // over this array. We must also take care of NoSuchElementException
        // which could be thrown if the last ClientHandler removes itself
        // between the call to clientHandlerVector.isEmpty() and the call
        // to clientHandlerVector.firstElement().
        // What we *MUST NOT DO* is locking the clientHandlerVector, because
        // this would most probably cause a deadlock.
        //
        while (! X37.isEmpty())
        {
            try
            {
                X37.firstElement().join();
            }
            catch (NoSuchElementException X59)
            {
                X8("waitClientTermination","No element left: " + X59);
            }
        }

        if (X54())
        {
            if (X57 >= 1)
            {
                X8("waitClientTermination","Ok, let's go...") ;
            }
        }
    }

    /**
     * Call <CODE>interrupt()</CODE> on each pending client.
     */
    private void X6()
    {
        final int X57 = X37.size() ;
        if (X54())
        {
            if (X57 >= 1)
            {
                X8("terminateAllClient","Interrupting " + X57 + " clients") ;
            }
        }

        // The ClientHandler will remove themselves from the
        // clientHandlerVector at the end of their run() method, by
        // calling notifyClientHandlerDeleted().
        // Since the clientHandlerVector is modified by the ClientHandler
        // threads we must avoid using Enumeration or Iterator to loop
        // over this array.
        // We cannot use the same logic here than in waitClientTermination()
        // because there is no guarantee that calling interrupt() on the
        // ClientHandler will actually terminate the ClientHandler.
        // Since we do not want to wait for the actual ClientHandler
        // termination, we cannot simply loop over the array until it is
        // empty (this might result in calling interrupt() endlessly on
        // the same client handler. So what we do is simply take a snapshot
        // copy of the vector and loop over the copy.
        // What we *MUST NOT DO* is locking the clientHandlerVector, because
        // this would most probably cause a deadlock.
        //
        final  ClientHandler[] X42 =
            X37.toArray(new ClientHandler[0]);
        for (ClientHandler X46 : X42)
        {
            try
            {
                X46.X17() ;
            }
            catch (Exception X59)
            {
                if (X54())
                    X8("terminateAllClient",
                          "Failed to interrupt pending request: "+X59+
                          " - skiping");
            }
        }
    }

    /**
     * Controls the way the CommunicatorServer service is deserialized.
     */
    private void readObject(ObjectInputStream stream)
    throws IOException, X23
    {

        // Call the default deserialization of the object.
        //
        stream.defaultReadObject();

        // Call the specific initialization for the CommunicatorServer service.
        // This is for transient structures to be initialized to specific
        // default values.
        //
        X55 = new X34();
        X10 = X29;
        stopRequested = false;
        X41 = 0;
        X37 = new X9<ClientHandler>();
        X45 = Thread.X25();
        X4 = null;
        X40 = 0;
        X3 = null;
        notifBroadcaster = new NotificationBroadcasterSupport();
        X19 = X22();
    }


    //
    // NotificationBroadcaster
    //

    /**
     * Adds a listener for the notifications emitted by this
     * CommunicatorServer.
     * There is only one type of notifications sent by the CommunicatorServer:
     * they are <tt>{@link javax.management.AttributeChangeNotification}</tt>,
     * sent when the <tt>State</tt> attribute of this CommunicatorServer
     * changes.
     *
     * @param listener The listener object which will handle the emitted
     *        notifications.
     * @param filter The filter object. If filter is null, no filtering
     *        will be performed before handling notifications.
     * @param handback An object which will be sent back unchanged to the
     *        listener when a notification is emitted.
     *
     * @exception IllegalArgumentException Listener parameter is null.
     */
    public void addNotificationListener(X44 listener,
                                        NotificationFilter filter,
                                        X34 handback)
    throws java.lang.X27
    {

        if (isDebugOn())
        {
            debug("addNotificationListener","Adding listener "+ listener +
                  " with filter "+ filter + " and handback "+ handback);
        }
        notifBroadcaster.addNotificationListener(listener, filter, handback);
    }

    /**
     * Removes the specified listener from this CommunicatorServer.
     * Note that if the listener has been registered with different
     * handback objects or notification filters, all entries corresponding
     * to the listener will be removed.
     *
     * @param listener The listener object to be removed.
     *
     * @exception ListenerNotFoundException The listener is not registered.
     */
    public void X20(X44 listener)
    throws ListenerNotFoundException
    {

        if (isDebugOn())
        {
            debug("removeNotificationListener","Removing listener "+ listener);
        }
        notifBroadcaster.X20(listener);
    }

    /**
     * Returns an array of MBeanNotificationInfo objects describing
     * the notification types sent by this CommunicatorServer.
     * There is only one type of notifications sent by the CommunicatorServer:
     * it is <tt>{@link javax.management.AttributeChangeNotification}</tt>,
     * sent when the <tt>State</tt> attribute of this CommunicatorServer
     * changes.
     */
    public MBeanNotificationInfo[] getNotificationInfo()
    {

        // Initialize notifInfos on first call to getNotificationInfo()
        //
        if (X3 == null)
        {
            X3 = new MBeanNotificationInfo[1];
            X47[] notifTypes =
            {
                X48.ATTRIBUTE_CHANGE
            };
            X3[0] = new MBeanNotificationInfo( notifTypes,
                    X48.class.getName(),
                    "Sent to notify that the value of the State attribute "+
                    "of this CommunicatorServer instance has changed.");
        }

        return X3;
    }

    /**
     *
     */
    private void sendStateChangeNotification(int oldState, int newState)
    {

        X47 oldStateString = getStringForState(oldState);
        X47 newStateString = getStringForState(newState);
        X47 X50 = new StringBuffer().X43(X19)
        .X43(" The value of attribute State has changed from ")
        .X43(oldState).X43(" (").X43(oldStateString)
        .X43(") to ").X43(newState).X43(" (")
        .X43(newStateString).X43(").").toString();

        X40++;
        X48 notif =
            new X48(X36,    // source
                                            X40,	             // sequence number
                                            X2.currentTimeMillis(), // time stamp
                                            X50,		     // message
                                            "State",		     // attribute name
                                            "int",			     // attribute type
                                            new Integer(oldState),      // old value
                                            new Integer(newState) );    // new value

        if (isDebugOn())
        {
            debug("sendStateChangeNotification",
                  "Sending AttributeChangeNotification #"+ X40 +
                  " with message: "+ X50);
        }
        notifBroadcaster.sendNotification(notif);
    }

    /**
     *
     */
    private static X47 getStringForState(int X57)
    {
        switch (X57)
        {
        case ONLINE:
            return "ONLINE";
        case STARTING:
            return "STARTING";
        case X29:
            return "OFFLINE";
        case X38:
            return "STOPPING";
        default:
            return "UNDEFINED";
        }
    }


    //
    // MBeanRegistration
    //

    /**
     * Preregister method of connector.
     *
     *@param server The <CODE>MBeanServer</CODE> in which the MBean will
     *       be registered.
     *@param name The object name of the MBean.
     *
     *@return  The name of the MBean registered.
     *
     *@exception java.langException This exception should be caught by
     *           the <CODE>MBeanServer</CODE> and re-thrown
     *           as an <CODE>MBeanRegistrationException</CODE>.
     */
    public X24 preRegister(X14 server, X24 name)
    throws java.lang.Exception
    {
        objectName = name;
        synchronized (X36)
        {
            if (X33 != null)
            {
                throw new X27("connector already " +
                                                   "registered in an MBean " +
                                                   "server");
            }
            topMBS = X33 = server;
        }
        X19 = X22();
        return name;
    }

    /**
     *
     *@param registrationDone Indicates whether or not the MBean has been
     *       successfully registered in the <CODE>MBeanServer</CODE>.
     *       The value false means that the registration phase has failed.
     */
    public void postRegister(Boolean registrationDone)
    {
        if (!registrationDone.booleanValue())
        {
            synchronized (X36)
            {
                topMBS = X33 = null;
            }
        }
    }

    /**
     * Stop the connector.
     *
     * @exception java.langException This exception should be caught by
     *            the <CODE>MBeanServer</CODE> and re-thrown
     *            as an <CODE>MBeanRegistrationException</CODE>.
     */
    public void preDeregister() throws java.lang.Exception
    {
        synchronized (X36)
        {
            topMBS = X33 = null;
        }
        objectName = null ;
        final int X18 = getState();
        if ((X18 == ONLINE) || ( X18 == STARTING))
        {
            stop() ;
        }
    }

    /**
     * Do nothing.
     */
    public void postDeregister()
    {
    }

    /**
     * Load a class using the default loader repository
     **/
    Class loadClass(X47 className)
    throws X23
    {
        try
        {
            return Class.forName(className);
        }
        catch (X23 e)
        {
            final ClassLoaderRepository clr =
                MBeanServerFactory.getClassLoaderRepository(X33);
            if (clr == null) throw new X23(className);
            return clr.loadClass(className);
        }
    }

    //
    // Debug stuff
    //

    /**
     */
    int infoType;

    /**
     */
    boolean X54()
    {
        return Trace.isSelected(Trace.X32, infoType);
    }

    /**
     */
    void X8(X47 clz, X47 X56, X47 info)
    {
        Trace.send(Trace.X32, infoType, clz, X56, info);
    }

    /**
     */
    boolean isDebugOn()
    {
        return Trace.isSelected(Trace.LEVEL_DEBUG, infoType);
    }

    /**
     */
    void debug(X47 clz, X47 X56, X47 info)
    {
        Trace.send(Trace.LEVEL_DEBUG, infoType, clz, X56, info);
    }

    /**
     */
    void debug(X47 clz, X47 X56, X16 exception)
    {
        Trace.send(Trace.LEVEL_DEBUG, infoType, clz, X56, exception);
    }

    /**
     */
    void X8(X47 X56, X47 info)
    {
        X8(X19, X56, info);
    }

    /**
     */
    void debug(X47 X56, X47 info)
    {
        debug(X19, X56, info);
    }

    /**
     */
    void debug(X47 X56, X16 exception)
    {
        debug(X19, X56, exception);
    }
}
