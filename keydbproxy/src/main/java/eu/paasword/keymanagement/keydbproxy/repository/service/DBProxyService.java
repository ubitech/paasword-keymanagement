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
package eu.paasword.keymanagement.keydbproxy.repository.service;

import eu.paasword.keymanagement.keydbproxy.repository.dao.ConfigurationRepository;
import eu.paasword.keymanagement.keydbproxy.repository.dao.DbentryRepository;
import eu.paasword.keymanagement.keydbproxy.repository.dao.UserentryRepository;
import eu.paasword.keymanagement.keydbproxy.repository.domain.Dbentry;
import eu.paasword.keymanagement.keydbproxy.repository.domain.Userentry;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.EncryptedAndSignedUserKeys;
import eu.paasword.keymanagement.util.transfer.QueryContext;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class DBProxyService {

    private static final Logger logger = Logger.getLogger(DBProxyService.class.getName());

    @Value("${tenantadmin.url}")
    private String tenantadminurl;

    @Value("${dbproxy.id}")
    private String dbproxyid;

    @Autowired
    UserentryRepository userrepo;
    @Autowired
    ConfigurationRepository configrepo;
    @Autowired
    DbentryRepository dbrepo;

//    public boolean registerUser(EncryptedAndSignedUserKeys encryptedandsigneduserkeys) {
//        logger.info("Register proxy key for the user");
//
//        Userentry userentry = new Userentry();
//        userentry.setProxyid(encryptedandsigneduserkeys.getProxyid());
//        userentry.setUserid(encryptedandsigneduserkeys.getUserid());
//        //Decrypt App part of the key
//        try {
//            String privkeyasstring = configrepo.findAll().get(0).getPrivkey();
//            byte[] base64decodedBytes = Base64.getDecoder().decode(privkeyasstring);
//            PrivateKey privkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPrivateCrtKeyImpl.class);
//            //decrypt the aes key
//            String asymdecryptedkeyasstring = SecurityUtil.decryptAssymetrically(privkey, encryptedandsigneduserkeys.getAsymencryptedproxykey());
//            //cast it to verify that it is a valid key
//            byte[] base64decodedBytes2 = Base64.getDecoder().decode(asymdecryptedkeyasstring);
//            SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), SecretKey.class);
//            String testmsg = "test input";
//            byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, testmsg);
//            String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);
//            if (symdecrypted.equalsIgnoreCase(testmsg)) {
//                logger.info("Secret key was decrypted and casted");
//            }
//            //save assymetrically encrypted key
//            userentry.setProxykey(asymdecryptedkeyasstring);
//            userrepo.save(userentry);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return true;
//    }//EoM

//    public String queryHandler(QueryContext query) {
//        logger.info("Querying handling..");
//        String ret = "";
//        try {
//            //Step 0 - extract Private Key
//            String privkeyasstring = configrepo.findAll().get(0).getPrivkey();
//            byte[] base64decodedBytes = Base64.getDecoder().decode(privkeyasstring);
//            PrivateKey privkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPrivateCrtKeyImpl.class);
//
//            //Step 1 - Decrypt the User aes key
//            String asymdecryptedUserkeyasstring = SecurityUtil.decryptAssymetrically(privkey, query.getAsymencrypteduserkey());
//            //cast it to verify that it is a valid key
//            byte[] base64decodedBytes1 = Base64.getDecoder().decode(asymdecryptedUserkeyasstring);
//            SecretKey UserAESkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes1, "utf-8"), SecretKey.class);
//            String testmsg = "test input";
//            byte[] symencrypted = SecurityUtil.encryptSymmetrically(UserAESkey, testmsg);
//            String symdecrypted = SecurityUtil.decryptSymmetrically(UserAESkey, symencrypted);
//            if (symdecrypted.equalsIgnoreCase(testmsg)) {
//                logger.info("User key was decrypted and casted");
//            }
//
//            //Step 2 - Decrypt the App aes key
//            String asymdecryptedAppkeyasstring = SecurityUtil.decryptAssymetrically(privkey, query.getAsymencryptedappkey());
//            //cast it to verify that it is a valid key
//            byte[] base64decodedBytes2 = Base64.getDecoder().decode(asymdecryptedAppkeyasstring);
//            SecretKey AppAESkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), SecretKey.class);
//            String testmsg2 = "test input2";
//            byte[] symencrypted2 = SecurityUtil.encryptSymmetrically(AppAESkey, testmsg2);
//            String symdecrypted2 = SecurityUtil.decryptSymmetrically(AppAESkey, symencrypted2);
//            if (symdecrypted2.equalsIgnoreCase(testmsg2)) {
//                logger.info("App key was decrypted and casted");
//            }
//
//            //Step 3 - Get 
//            Userentry userentry = userrepo.findByProxyidAndUserid(query.getProxyid(), query.getUserid()).get(0);
//            String proxykeyasstring = userentry.getProxykey();
//            byte[] base64decodedBytes3 = Base64.getDecoder().decode(proxykeyasstring);
//            SecretKey ProxyAESkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes3, "utf-8"), SecretKey.class);
//
//            String testmsg3 = "test input3";
//            byte[] symencrypted3 = SecurityUtil.encryptSymmetrically(AppAESkey, testmsg3);
//            String symdecrypted3 = SecurityUtil.decryptSymmetrically(AppAESkey, symencrypted3);
//            if (symdecrypted3.equalsIgnoreCase(testmsg3)) {
//                logger.info("Proxy key was decrypted and casted");
//            }
//
//            logger.info("Merge User App and Proxy keys");
//            SecretKey mergedkey = SecurityUtil.mergeKeysInParts(UserAESkey, AppAESkey, ProxyAESkey);
//
//            //check that key is correct
//            String unencrypted4 = "test";
//            byte[] symencrypted4 = SecurityUtil.encryptSymmetrically(mergedkey, unencrypted4);
//            String symdecrypted4 = SecurityUtil.decryptSymmetrically(mergedkey, symencrypted4);
//            if (unencrypted4.equals(symdecrypted4)) {
//                logger.info("It is a valid Merged key!");
//            }
//
//            //Step 4 - Query
//            logger.info("Key merged - i will query");
//            Dbentry entry = dbrepo.findByKey("key1").get(0);
//
//            //decrypt-process of stored value
//            String base64encrypted = entry.getValue();
//            logger.info("value: " + base64encrypted);
//            byte[] base64dencryptedBytes = Base64.getDecoder().decode(base64encrypted);
//            ret = SecurityUtil.decryptSymmetrically(mergedkey, base64dencryptedBytes);
//            logger.info("Query output: " + ret);
//        } catch (Exception ex) {
//            logger.severe("Exception " + ex.getMessage());
//            ex.printStackTrace();
//        }
//
//        return ret;
//    }//EoM

}//EoC
