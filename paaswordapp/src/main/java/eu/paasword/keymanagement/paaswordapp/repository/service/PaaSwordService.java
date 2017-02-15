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
import eu.paasword.keymanagement.util.transfer.QueryContext;
import eu.paasword.keymanagement.util.transfer.EncryptedAndSignedUserKeys;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class PaaSwordService {

    private static final Logger logger = Logger.getLogger(PaaSwordService.class.getName());

    @Autowired
    UserentryRepository userrepo;
    @Autowired
    AppconfigRepository configrepo;

    @Value("${proxy.url}")
    private String proxyurl;

    public boolean registerUser(EncryptedAndSignedUserKeys encryptedandsigneduserkeys) {
        logger.info("Register app key for the user");

        Userentry userentry = new Userentry();
        userentry.setProxyid(encryptedandsigneduserkeys.getProxyid());
        userentry.setUserid(encryptedandsigneduserkeys.getUserid());
        //Decrypt App part of the key
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
            userrepo.save(userentry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Forward the proxy part to the proxy
        logger.info("Forwarding key to proxy");
        RestTemplate restTemplate = new RestTemplate();
        String invocationurl = proxyurl + "/api/keydbproxy/registeruser";
        RestResponse result = restTemplate.postForObject(invocationurl, encryptedandsigneduserkeys, RestResponse.class);
        logger.info("Registeruser result: \n" + result.getReturnobject());

        return true;
    }//EoM

    public String queryHandler(QueryContext query) {
        String ret = "";
        logger.info("Querying handling..");
        RestTemplate restTemplate = new RestTemplate();
        // Step 1: Get App Key for userID
        Userentry userentry = userrepo.findByProxyidAndUserid(query.getProxyid(), query.getUserid()).get(0);
        String appkeyasstring = userentry.getAppkey();
        try {
            // Step 2 - fetch pubkey of proxy
            String invocationurl = proxyurl + "/api/keydbproxy/getpubkey/" + query.getProxyid();
            RestResponse result = restTemplate.getForObject(invocationurl, RestResponse.class);
            logger.info("PubKey of Proxy Fetched: \n" + result.getReturnobject());
            byte[] base64decodedBytes1 = Base64.getDecoder().decode((String) result.getReturnobject());
            PublicKey pubkeyofproxy = SecurityUtil.deSerializeObject(new String(base64decodedBytes1, "utf-8"), RSAPublicKeyImpl.class);
            logger.info("Public Key of Proxy has been reconstructed");

            //step 3 - Encrypt appkey with proxy pub-key
            logger.info("Encryption user key");
            byte[] asymencryptedappkey = SecurityUtil.encryptAssymetrically(pubkeyofproxy, appkeyasstring);

            // Step 4 - Forward query to DBProxy.query
            query.setAsymencryptedappkey(asymencryptedappkey);
            invocationurl = proxyurl + "/api/keydbproxy/query";
            result = restTemplate.postForObject(invocationurl, query, RestResponse.class);
            logger.info("result: " + result.getReturnobject());
            ret = (String) result.getReturnobject();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Exception during the invocation of register key to proxy");
        }

        return ret;
    }//EoM

}//EoC
