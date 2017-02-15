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
package eu.paasword.keymanagement.paaswordapp.repository.service;

import eu.paasword.keymanagement.paaswordapp.repository.dao.AppconfigRepository;
import java.util.logging.Logger;

import eu.paasword.keymanagement.paaswordapp.repository.dao.UserentryRepository;
import eu.paasword.keymanagement.paaswordapp.repository.domain.Userentry;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.ClientQueryContext;
import eu.paasword.keymanagement.util.transfer.AppUserKey;
import eu.paasword.keymanagement.util.transfer.EncryptedAndSignedUserKeys;
import java.security.PrivateKey;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class PaaSwordService {

    private static final Logger logger = Logger.getLogger(PaaSwordService.class.getName());

    @Autowired
    UserentryRepository userentryRepository;
    @Autowired
    AppconfigRepository configrepo;

    public boolean registerUser(EncryptedAndSignedUserKeys encryptedandsigneduserkeys) {
        logger.info("Register app key for the user");

        Userentry userentry = new Userentry();
        userentry.setProxyid(encryptedandsigneduserkeys.getProxyid());
        userentry.setUserid(encryptedandsigneduserkeys.getUserid());
        //Decrypt user part of the key
        try {
            String privkeyasstring = configrepo.findAll().get(0).getPrivkey();
            byte[] base64decodedBytes = Base64.getDecoder().decode(privkeyasstring);
            PrivateKey privkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPrivateCrtKeyImpl.class);
            //decrypt the aes key
            String asymdecryptedkeyasstring = SecurityUtil.decryptAssymetrically(privkey, encryptedandsigneduserkeys.getAsymencryptedappkey());
            //cast it to verify that it is a valid key
            byte[] base64decodedBytes2 = Base64.getDecoder().decode(asymdecryptedkeyasstring);
            SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), SecretKey.class);
            String testmsg = "test input";
            byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, testmsg);
            String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);
            if (symdecrypted.equalsIgnoreCase(testmsg)) {
                logger.info("Secret key was decrypted and casted");
            }
            //save assymetrically encrypted key
            userentry.setAppkey(asymdecryptedkeyasstring);
            userentryRepository.save(userentry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Forward the proxy part to the proxy
        
        
        
        return true;
    }//EoM

    public String queryHandler(ClientQueryContext appUserKey) {
        logger.info("Querying handling..");

        // Step 1: Get App Key for userID
        // Step 2: Forward query to DBProxy.query
        return null;
    }//EoM

}//EoC
