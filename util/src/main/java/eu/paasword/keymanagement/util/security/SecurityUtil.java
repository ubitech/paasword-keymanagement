/*
 *  Copyright 2016-2017 PaaSword Framework, http://www.paasword.eu/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.paasword.keymanagement.util.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
public class SecurityUtil {

    private static final Logger logger = Logger.getLogger(SecurityUtil.class.getName());
    // AES-GCM parameters
    public static final int AES_KEY_SIZE = 128; // in bits
    public static final int GCM_NONCE_LENGTH = 12; // in bytes


    public static PaaSwordSecurityKey generateAESKey() {
        SecretKey key = null;
        PaaSwordSecurityKey pkey = null;
        Random random = null;
        KeyGenerator generator;
        try {
            //key
            generator = KeyGenerator.getInstance("AES");    //TODO 
            generator.init(AES_KEY_SIZE);
            key = generator.generateKey();
            logger.info("Key created: " + key);
            random = new Random();               //SecureRandom.getInstanceStrong(); add to /dev/./random            
            //nonce
            byte[] iv = new byte[GCM_NONCE_LENGTH];
            random.nextBytes(iv);
//            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            pkey = new PaaSwordSecurityKey(key, iv);
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return pkey;
    } //EoM    
    
    public static String encryptSymmetrically(PaaSwordSecurityKey pkey,String unencryptedstring ) throws Exception {
        SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
        // Generate 128 bit IV for Encryption

//        SecretKeySpec eks = new SecretKeySpec(key, "AES");
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");

        // Generated Authentication Tag should be 128 bits
        c.init(Cipher.ENCRYPT_MODE, pkey.getKey(), new GCMParameterSpec(128, pkey.getIv()) );
        byte[] es = c.doFinal(unencryptedstring.getBytes(StandardCharsets.UTF_8));

        // Construct Output as "IV + CIPHERTEXT"
        byte[] os = new byte[GCM_NONCE_LENGTH + es.length];
        System.arraycopy(pkey.getIv(), 0, os, 0, GCM_NONCE_LENGTH);
        System.arraycopy(es, 0, os, GCM_NONCE_LENGTH, es.length);

        // Return a Base64 Encoded String
        return Base64.getEncoder().encodeToString(os);

    }//EoM

    public static String decryptSymmetrically(PaaSwordSecurityKey pkey,String encryptedstring) throws Exception {
        // Recover our Byte Array by Base64 Decoding
        byte[] os = Base64.getDecoder().decode(encryptedstring);

        // Check Minimum Length (IV (12) + TAG (16))
        if (os.length > 28) {
            byte[] iv = Arrays.copyOfRange(os, 0, GCM_NONCE_LENGTH);
            byte[] es = Arrays.copyOfRange(os, GCM_NONCE_LENGTH, os.length);

            // Perform Decryption
//            SecretKeySpec dks = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, pkey.getKey(), new GCMParameterSpec(128, pkey.getIv()));

            // Return our Decrypted String
            return new String(c.doFinal(es), StandardCharsets.UTF_8);
        }
        throw new Exception();
    }//EoM



//    /**
//     * Encrypts plainText in AES using the secret key
//     *
//     * @param plainText
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static byte[] encryptSymmetrically(PaaSwordSecurityKey pkey, String plainText) throws Exception {
//        // AES defaults to AES/ECB/PKCS5Padding in Java 7
//        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        cipher.init(Cipher.ENCRYPT_MODE, pkey.getKey(), pkey.getSpec());
//        cipher.updateAAD(pkey.getAad().getBytes());
//        byte[] byteCipherText = cipher.doFinal();
//        return byteCipherText;
//    }//EoM

//    /**
//     * Decrypts encrypted byte array using the key used for encryption.
//     *
//     * @param pkey
//     * @param byteCipherText
//     * @return
//     * @throws Exception
//     */
//    public static String decryptSymmetrically(PaaSwordSecurityKey pkey, byte[] byteCipherText) {
//        byte[] bytePlainText = null;
//        try {
//            // AES defaults to AES/ECB/PKCS5Padding in Java 7
//            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//            cipher.init(Cipher.DECRYPT_MODE, pkey.getKey(), pkey.getSpec());
//
////            cipher.updateAAD(pkey.getAad().getBytes());
//            bytePlainText = cipher.doFinal(byteCipherText);
//            logger.info("Decrypted: " + new String(bytePlainText));
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return new String(bytePlainText);
//    }//EoM

    public static KeyPair generateRSAKeyPair(int bitsize) throws NoSuchAlgorithmException {   //1024 2048
        KeyPair keypair = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(bitsize);       //TODO alter hardcoded parameter
            keypair = kpg.genKeyPair();
            //logger.info("public: " + keypair.getPublic());
            //logger.info("private: " + keypair.getPrivate());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return keypair;
    }//EoM

    /*
     * Returns a Base64 encoded String that represents a signed entity
     */
    public static byte[] signContent(String content, PrivateKey priv) {
        byte[] signatureBytes = null;
        try {
            byte[] data = content.getBytes("UTF8");
            Signature sig = Signature.getInstance("SHA1withRSA");   //MD5WithRSA    RSASSA-PSS
            sig.initSign(priv);
            sig.update(data);
            signatureBytes = sig.sign();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return signatureBytes;
    }//EoM

    public static boolean verifySignature(PublicKey pub, String claimeddata, byte[] signatureBytes) {
        boolean ret = false;
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(pub);
            sig.update(claimeddata.getBytes());
            ret = sig.verify(signatureBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }//EoM

    /**
     * Encrypt the plain text using public key.
     *
     * @param text : original plain text
     * @param key :The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public static byte[] encryptAssymetrically(PublicKey key, String text) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }//EoM

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @param key :The private key
     * @return plain text
     * @throws java.lang.Exception
     */
    public static String decryptAssymetrically(PrivateKey key, byte[] text) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(dectyptedText);
    }//EoM

    public static SeparatedKeyContainer splitKeyInParts(PaaSwordSecurityKey pkey) {
        SecretKey random1key = generateAESKey().getKey();
        SecretKey random2key = generateAESKey().getKey();
        byte[] encodedbytes = XORByteArrays(pkey.getKey().getEncoded(), random1key.getEncoded());
        encodedbytes = XORByteArrays(encodedbytes, random2key.getEncoded());
        SecretKey generatedkey = new SecretKeySpec(encodedbytes, pkey.getKey().getAlgorithm());

        SeparatedKeyContainer separated = new SeparatedKeyContainer(
                new PaaSwordSecurityKey(random1key, pkey.getIv()),
                new PaaSwordSecurityKey(random2key, pkey.getIv()),
                new PaaSwordSecurityKey(generatedkey, pkey.getIv())
        );
        return separated;
    }//EoM

    public static PaaSwordSecurityKey mergeKeysInParts(PaaSwordSecurityKey userkey, PaaSwordSecurityKey appkey, PaaSwordSecurityKey proxykey) {
        byte[] encodedbytes = XORByteArrays(userkey.getKey().getEncoded(), appkey.getKey().getEncoded());
        encodedbytes = XORByteArrays(encodedbytes, proxykey.getKey().getEncoded());
        SecretKey generatedkey = new SecretKeySpec(encodedbytes, userkey.getKey().getAlgorithm());
        PaaSwordSecurityKey ret = new PaaSwordSecurityKey(generatedkey, userkey.getIv());
        return ret;
    }//EoM    

    public static byte[] XORByteArrays(byte[] array_1, byte[] array_2) {
        byte[] array_3 = new byte[array_1.length];
        int i = 0;
        for (byte b : array_1) {
            array_3[i] = (byte) (b ^ array_2[i++]);
        }
        return array_3;
    }//EoM

    /**
     * Serialize any object
     *
     * @param obj
     * @return
     */
    public static String serializeObject(Object obj) {
        String ret = "";
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(obj);
            so.flush();
            // This encoding induces a bijection between byte[] and String (unlike UTF-8)
            ret = bo.toString("ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Deserialize any object
     *
     * @param str
     * @param cls
     * @return
     */
    public static <T> T deSerializeObject(String str, Class<T> cls) {
        T obj = null;
        // deserialize the object
        try {
            // This encoding induces a bijection between byte[] and String (unlike UTF-8)
            byte b[] = str.getBytes("ISO-8859-1");
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            obj = cls.cast(si.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}//EoC
