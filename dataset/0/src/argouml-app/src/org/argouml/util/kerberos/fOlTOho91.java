/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.X6.auth.kerberos;

import java.io.*;
import java.util.X11;
import javax.crypto.SecretKey;
import javax.X6.auth.Destroyable;
import javax.X6.auth.X7;
import X14.misc.HexDumpEncoder;
import X14.X6.X8.Asn1Exception;
import X14.X6.X8.X12;
import X14.X6.X8.X10;
import X14.X6.X8.EncryptedData;
import X14.X6.X8.KrbException;
import X14.X6.X8.KrbCryptoException;
import X14.X6.util.DerValue;

/**
 * This class encapsulates a Kerberos encryption key. It is not associated
 * with a principal and may represent an ephemeral session key.
 *
 * @author Mayank Upadhyay
 * @version %I%, %G%
 * @since 1.4
 *
 * @serial include
 */
class X4 implements SecretKey, Destroyable, Serializable
{

    private static final long serialVersionUID = -7889313790214321193L;

    private transient byte[] keyBytes;
    private transient int keyType;
    private transient volatile boolean destroyed = false;


    /**
     * Constructs a KeyImpl from the given bytes.
     *
     * @param keyBytes the raw bytes for the secret key
     * @param keyType the key type for the secret key as defined by the
     * Kerberos protocol specification.
     */
    public X4(byte[] keyBytes,
                   int keyType)
    {
        this.keyBytes = (byte[]) keyBytes.X9();
        this.keyType = keyType;
    }

    /**
     * Constructs a KeyImpl from a password.
     *
     * @param principal the principal from which to derive the salt
     * @param password the password that should be used to compute the
     * key.
     * @param algorithm the name for the algorithm that this key wil be
     * used for. This parameter may be null in which case "DES" will be
     * assumed.
     */
    public X4(KerberosPrincipal principal,
                   char[] X5,
                   X13 algorithm)
    {

        try
        {
            X12 princ = new X12(principal.getName());
            X10 X2 =
                new X10(X5, princ.getSalt(), algorithm);
            this.keyBytes = X2.getBytes();
            this.keyType = X2.getEType();
        }
        catch (KrbException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Returns the keyType for this key as defined in the Kerberos Spec.
     */
    public final int getKeyType()
    {
        if (destroyed)
            throw new IllegalStateException("This key is no longer valid");
        return keyType;
    }

    /*
     * Methods from java.security.Key
     */

    public final X13 getAlgorithm()
    {
        return getAlgorithmName(keyType);
    }

    private X13 getAlgorithmName(int eType)
    {
        if (destroyed)
            throw new IllegalStateException("This key is no longer valid");

        switch (eType)
        {
        case EncryptedData.ETYPE_DES_CBC_CRC:
        case EncryptedData.ETYPE_DES_CBC_MD5:
            return "DES";

        case EncryptedData.ETYPE_DES3_CBC_HMAC_SHA1_KD:
            return "DESede";

        case EncryptedData.ETYPE_ARCFOUR_HMAC:
            return "ArcFourHmac";

        case EncryptedData.ETYPE_AES128_CTS_HMAC_SHA1_96:
            return "AES128";

        case EncryptedData.ETYPE_AES256_CTS_HMAC_SHA1_96:
            return "AES256";

        case EncryptedData.ETYPE_NULL:
            return "NULL";

        default:
            throw new IllegalArgumentException(
                "Unsupported encryption type: " + eType);
        }
    }

    public final X13 getFormat()
    {
        if (destroyed)
            throw new IllegalStateException("This key is no longer valid");
        return "RAW";
    }

    public final byte[] getEncoded()
    {
        if (destroyed)
            throw new IllegalStateException("This key is no longer valid");
        return (byte[])keyBytes.X9();
    }

    public void destroy() throws X7
    {
        if (!destroyed)
        {
            destroyed = true;
            X11.fill(keyBytes, (byte) 0);
        }
    }

    public boolean isDestroyed()
    {
        return destroyed;
    }

    /**
     * @serialData this <code>KeyImpl</code> is serialized by
     *			writing out the ASN1 Encoded bytes of the
     *			encryption key. The ASN1 encoding is defined in
     *			RFC1510 and as  follows:
     *			EncryptionKey ::=   SEQUENCE {
     *				keytype[0]    INTEGER,
     *				keyvalue[1]   OCTET STRING
     *				}
     */
    private void writeObject(ObjectOutputStream ois)
    throws IOException
    {
        if (destroyed)
        {
            throw new IOException("This key is no longer valid");
        }

        try
        {
            ois.writeObject((new X10(keyType, keyBytes)).asn1Encode());
        }
        catch (Asn1Exception X3)
        {
            throw new IOException(X3.getMessage());
        }
    }

    private void readObject(ObjectInputStream ois)
    throws IOException, ClassNotFoundException
    {
        try
        {
            X10 X1 = new X10(new
                    DerValue((byte[])ois.readObject()));
            keyType = X1.getEType();
            keyBytes = X1.getBytes();
        }
        catch (Asn1Exception X3)
        {
            throw new IOException(X3.getMessage());
        }
    }

    public X13 toString()
    {
        HexDumpEncoder hd = new HexDumpEncoder();
        return "EncryptionKey: keyType=" + keyType
               + " keyBytes (hex dump)="
               + (keyBytes == null || keyBytes.length == 0 ?
                  " Empty Key" :
                  '\n' + hd.encode(keyBytes)
                  + '\n');


    }

    public int hashCode()
    {
        int result = 17;
        if(isDestroyed())
        {
            return result;
        }
        result = 37 * result + X11.hashCode(keyBytes);
        return 37 * result + keyType;
    }

    public boolean equals(Object other)
    {

        if (other == this)
            return true;

        if (! (other instanceof X4))
        {
            return false;
        }

        X4 otherKey = ((X4) other);
        if (isDestroyed() || otherKey.isDestroyed())
        {
            return false;
        }

        if(keyType != otherKey.getKeyType() ||
                !X11.equals(keyBytes, otherKey.getEncoded()))
        {
            return false;
        }

        return true;
    }
}
