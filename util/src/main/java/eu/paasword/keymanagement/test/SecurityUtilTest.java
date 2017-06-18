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
package eu.paasword.keymanagement.test;

import eu.paasword.keymanagement.util.security.PaaSwordSecurityKey;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import static eu.paasword.keymanagement.util.security.SecurityUtil.AES_KEY_SIZE;
import eu.paasword.keymanagement.util.security.SeparatedKeyContainer;
import java.security.KeyPair;
import java.util.Random;
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
public class SecurityUtilTest {

    private static final Logger logger = Logger.getLogger(SecurityUtilTest.class.getName());

    public static void main(String[] args) throws Exception {
        String unencrypted = "123456789";

        //Symmetric Encryption & Decryption
        PaaSwordSecurityKey pkey = SecurityUtil.generateAESKey();

        logger.info("\nalgorithm: " + pkey.getKey().getAlgorithm() + " format: " + pkey.getKey().getFormat() + " " + pkey.getClass()); //javax.crypto.spec.SecretKeySpec        
        String symencrypted = SecurityUtil.encryptSymmetrically(pkey.getKey(), unencrypted);
        logger.info("\nencrypted-cipher:" + symencrypted);
        String symdecrypted = SecurityUtil.decryptSymmetrically(pkey.getKey(),symencrypted);
        //symdecrypted = SecurityUtil.decryptSymmetrically(pkey, symencrypted);
        //symdecrypted = SecurityUtil.decryptSymmetrically(pkey, symencrypted);

        logger.info("\nunencrypted: " + unencrypted + " encrypted: " + symencrypted + " decrypted: " + symdecrypted);

        //Signature Creation & Verification Circle
        KeyPair pair = SecurityUtil.generateRSAKeyPair(2048);
        String content = "this is a message to be signed";
        byte[] signature = SecurityUtil.signContent(content, pair.getPrivate());
        logger.info("signature: " + signature); //1o part tou QR
        boolean verify = SecurityUtil.verifySignature(pair.getPublic(), content, signature);
        logger.info("verify: " + verify);

        //Assymetric Encryption and Decryption
        byte[] asymencrypted = SecurityUtil.encryptAssymetrically(pair.getPublic(), unencrypted);
        String asymdecrypted = SecurityUtil.decryptAssymetrically(pair.getPrivate(), asymencrypted);
        logger.info(" unencrypted: " + unencrypted + " asymencrypted: " + asymencrypted + " asymdecrypted: " + asymdecrypted);

        logger.info("\n\n\npkey: "+pkey.getKey().toString());
        
        //splitting
        SeparatedKeyContainer separated = SecurityUtil.splitKeyInParts(pkey);
        logger.info("\nuserkey: "+separated.getUserkey().getKey().toString() + "\nproxykey: " + separated.getProxykey().getKey().toString() + "\nappkey: " + separated.getAppkey().getKey().toString() );

        PaaSwordSecurityKey merged = SecurityUtil.mergeKeysInParts(separated.getUserkey(), separated.getAppkey(), separated.getProxykey());
        logger.info("merged.getKey: "+merged.getKey().toString() +" spec: "+merged.getSpec()+" aad "+merged.getAad());
        

        String symencrypted2 = SecurityUtil.encryptSymmetrically(pkey.getKey(), unencrypted);
        logger.info("encrypted-cipher:"+symencrypted2);        
        String symdecryptedbytes = SecurityUtil.decryptSymmetrically(pkey.getKey(), symencrypted2);
        logger.info(" unencrypted: " + unencrypted + " encrypted: " + symencrypted2 + " decrypted: " + symdecryptedbytes);        
    }//EoM

//    public static void main(String[] args) throws Exception {
//        byte[] data  = null;
//        byte[] encrypted = null;
//        //final byte[] key = new byte[32];
//        final byte[] iv = new byte[12];
//        final Random random = new Random(1);
//        
//        
//        data = "hello".getBytes();
//        //random.nextBytes(key);
//        random.nextBytes(iv);
//
//        System.out.println("Benchmarking AES-256 GCM encryption for 10 seconds");
//        long javaEncryptInputBytes = 0;
//        long javaEncryptStartTime = System.currentTimeMillis();
//        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        byte[] tag = new byte[16];
//        long encryptInitTime = 0L;
//        long encryptUpdate1Time = 0L;
//        long encryptDoFinalTime = 0L;
//
//        KeyGenerator generator;
//        generator = KeyGenerator.getInstance("AES");    //TODO 
//        generator.init(AES_KEY_SIZE);
//        SecretKey skey = generator.generateKey();
//
////        while (System.currentTimeMillis() - javaEncryptStartTime < 10000) {
//        random.nextBytes(iv);
//        long n1 = System.nanoTime();
//
//        cipher.init(Cipher.ENCRYPT_MODE, skey, new GCMParameterSpec(16 * Byte.SIZE, iv));
//        long n2 = System.nanoTime();
//        cipher.update(data, 0, data.length, encrypted, 0);
//        long n3 = System.nanoTime();
//        cipher.doFinal(tag, 0);
//        long n4 = System.nanoTime();
//        javaEncryptInputBytes += data.length;
//
//        encryptInitTime = n2 - n1;
//        encryptUpdate1Time = n3 - n2;
//        encryptDoFinalTime = n4 - n3;
//        //       }
//        long javaEncryptEndTime = System.currentTimeMillis();
//        System.out.println("Time init (ns): " + encryptInitTime);
//        System.out.println("Time update (ns): " + encryptUpdate1Time);
//        System.out.println("Time do final (ns): " + encryptDoFinalTime);
//        System.out.println("Java calculated at " + (javaEncryptInputBytes / 1024 / 1024 / ((javaEncryptEndTime - javaEncryptStartTime) / 1000)) + " MB/s");
//
//        System.out.println("Benchmarking AES-256 GCM decryption for 10 seconds");
//        long javaDecryptInputBytes = 0;
//        long javaDecryptStartTime = System.currentTimeMillis();
//        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * Byte.SIZE, iv);
//        //final SecretKeySpec keySpec = new SecretKeySpec(skey, "AES");
//        long decryptInitTime = 0L;
//        long decryptUpdate1Time = 0L;
//        long decryptUpdate2Time = 0L;
//        long decryptDoFinalTime = 0L;
////        while (System.currentTimeMillis() - javaDecryptStartTime < 10000) {
////            long n1 = System.nanoTime();
//            cipher.init(Cipher.DECRYPT_MODE, skey, gcmParameterSpec);
////            long n2 = System.nanoTime();
//            int offset = cipher.update(encrypted, 0, encrypted.length, data, 0);
////            long n3 = System.nanoTime();
//            cipher.update(tag, 0, tag.length, data, offset);
////            long n4 = System.nanoTime();
//             cipher.doFinal(data, offset);
//             
//            
////            long n5 = System.nanoTime();
////            javaDecryptInputBytes += data.length;
//
////            decryptInitTime += n2 - n1;
////            decryptUpdate1Time += n3 - n2;
////            decryptUpdate2Time += n4 - n3;
////            decryptDoFinalTime += n5 - n4;
////        }
////        long javaDecryptEndTime = System.currentTimeMillis();
////        System.out.println("Time init (ns): " + decryptInitTime);
////        System.out.println("Time update 1 (ns): " + decryptUpdate1Time);
////        System.out.println("Time update 2 (ns): " + decryptUpdate2Time);
////        System.out.println("Time do final (ns): " + decryptDoFinalTime);
////        System.out.println("Total bytes processed: " + javaDecryptInputBytes);
////        System.out.println("Java calculated at " + (javaDecryptInputBytes / 1024 / 1024 / ((javaDecryptEndTime - javaDecryptStartTime) / 1000)) + " MB/s");
//    }
}//EoC
