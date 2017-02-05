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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
public class SecurityUtil {

    private static final Logger logger = Logger.getLogger(SecurityUtil.class.getName());

    public static SecretKey generateAESKey() {
        SecretKey key = null;
        //SecureRandom rand = new SecureRandom();
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");    //TODO 
//            generator.init(rand);
            generator.init(128);                            //TODO
            key = generator.generateKey();
            logger.info("Key created: " + key);
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return key;
    } //EoM

    /**
     * Encrypts plainText in AES using the secret key
     *
     * @param plainText
     * @param secKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptSymmetrically(SecretKey secKey, String plainText) throws Exception {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
        return byteCipherText;
    }//EoM

    /**
     * Decrypts encrypted byte array using the key used for encryption.
     *
     * @param byteCipherText
     * @param secKey
     * @return
     * @throws Exception
     */
    public static String decryptSymmetrically(SecretKey secKey, byte[] byteCipherText) throws Exception {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        return new String(bytePlainText);
    }//EoM

    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPair keypair = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);       //TODO alter hardcoded parameter
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
            Signature sig = Signature.getInstance("MD5WithRSA");
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
            Signature sig = Signature.getInstance("MD5WithRSA");
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

    public static SeparatedKeyContainer splitKeyInParts(SecretKey key) {
        SecretKey random1key = generateAESKey();
        SecretKey random2key = generateAESKey();
        byte[] encodedbytes = XORByteArrays(key.getEncoded(), random1key.getEncoded());
        encodedbytes = XORByteArrays(encodedbytes, random2key.getEncoded());
        SecretKey generatedkey = new SecretKeySpec(encodedbytes, key.getAlgorithm());

        SeparatedKeyContainer separated = new SeparatedKeyContainer(random1key, random2key, generatedkey);
        return separated;
    }//EoM

    public static SecretKey mergeKeysInParts(SecretKey userkey, SecretKey appkey, SecretKey proxykey) {
        byte[] encodedbytes = XORByteArrays(userkey.getEncoded(), appkey.getEncoded());
        encodedbytes = XORByteArrays(encodedbytes, proxykey.getEncoded());
        SecretKey generatedkey = new SecretKeySpec(encodedbytes, userkey.getAlgorithm());
        return generatedkey;
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
