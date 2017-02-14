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

import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.security.SeparatedKeyContainer;
import java.security.KeyPair;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
public class SecurityUtilTest {

    private static final Logger logger = Logger.getLogger(SecurityUtilTest.class.getName());

    public static void main(String[] args) throws Exception {
        String unencrypted = "This is a test";
//        String unencrypted = "wqzDrQAFc3IAH2phdmF4LmNyeXB0by5zcGVjLlNlY3JldEtleVNwZWNbRwtmw6IwYU0CAAJMAAlhbGdvcml0aG10ABJMamF2YS9sYW5nL1N0cmluZztbAANrZXl0AAJbQnhwdAADQUVTdXIAAltCwqzDsxfDuAYIVMOgAgAAeHAAAAAQVhhOcGrChcKBb8KBPsKvEsK3bMOxbQ==";

        //Symmetric Encryption & Decryption
        SecretKey aeskey = SecurityUtil.generateAESKey();

        logger.info("algorithm: " + aeskey.getAlgorithm() + " format: " + aeskey.getFormat() + " " + aeskey.getClass()); //javax.crypto.spec.SecretKeySpec        

        byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
        String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);
        logger.info(" unencrypted: " + unencrypted + " encrypted: " + symencrypted + " decrypted: " + symdecrypted);

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

        SeparatedKeyContainer separated = SecurityUtil.splitKeyInParts(aeskey);
        logger.info(separated.getUserkey() + " " + separated.getProxykey() + " " + separated.getAppkey());

        SecretKey merged = SecurityUtil.mergeKeysInParts(separated.getUserkey(), separated.getAppkey(), separated.getProxykey());

        byte[] symencrypted2 = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
        String symdecrypted2 = SecurityUtil.decryptSymmetrically(merged, symencrypted2);
        logger.info(" unencrypted: " + unencrypted + " encrypted: " + symencrypted2 + " decrypted: " + symdecrypted2);

        
        
        
        
        
    }//EoM

}//EoC
