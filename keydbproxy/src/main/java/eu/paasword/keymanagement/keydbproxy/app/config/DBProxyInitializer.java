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
package eu.paasword.keymanagement.keydbproxy.app.config;

import eu.paasword.keymanagement.keydbproxy.repository.domain.Proxyconfig;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.ResponseCode;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;
import eu.paasword.keymanagement.keydbproxy.repository.dao.ConfigurationRepository;
import eu.paasword.keymanagement.keydbproxy.repository.dao.DbentryRepository;
import eu.paasword.keymanagement.keydbproxy.repository.domain.Dbentry;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Configuration
public class DBProxyInitializer {
    
    private static final Logger logger = Logger.getLogger(DBProxyInitializer.class.getName());
    
    @Value("${dbproxy.id}")
    private String dbproxyid;
    
    @Value("${tenantadmin.url}")
    private String tenantadminurl;
    
    @Autowired
    ConfigurationRepository configrepo;
    
    @Autowired
    DbentryRepository dbrepo;
    
    @Bean
    @Order(1)
    public Proxyconfig chechRSAKeyPair() {
        logger.info("Checking RSA for " + dbproxyid);
        Proxyconfig proxyconfig = null;
        if (configrepo.findAll().isEmpty()) {
            try {
                logger.info("Generating key pair");
                KeyPair keypair = SecurityUtil.generateRSAKeyPair();
                proxyconfig = new Proxyconfig();
                proxyconfig.setPubkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPublic()).getBytes("UTF-8")));
                proxyconfig.setPrivkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPrivate()).getBytes("UTF-8")));
                logger.info("keypair.getPublic().getClass " + keypair.getPublic().getClass());
                logger.info("keypair.getPrivate().getClass " + keypair.getPrivate().getClass());
                proxyconfig.setAesconfigured(0);
                configrepo.save(proxyconfig);
                
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(DBProxyInitializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            logger.info("RSA key pair already exists i will check its validity");
            proxyconfig = configrepo.findAll().get(0);
            byte[] base64decodedBytes = Base64.getDecoder().decode(proxyconfig.getPubkey());
            byte[] base64decodedBytes2 = Base64.getDecoder().decode(proxyconfig.getPrivkey());
            PublicKey pubkey;
            PrivateKey privkey;
            try {
                pubkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPublicKeyImpl.class);
                privkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), RSAPrivateCrtKeyImpl.class);
                //PrivateKey privkey = new RSAPrivateKeyImpl()
                String unencrypted = "test";
                byte[] asymencrypted = SecurityUtil.encryptAssymetrically(pubkey, unencrypted);
                String asymdecrypted = SecurityUtil.decryptAssymetrically(privkey, asymencrypted);
                if (unencrypted.equals(asymdecrypted)) {
                    logger.info("RSA Key Existing and is valid");
                }
                logger.info(" unencrypted: " + unencrypted + " asymencrypted: " + asymencrypted + " asymdecrypted: " + asymdecrypted);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DBProxyInitializer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return proxyconfig;
    }//EoM

    @Bean
    @Order(2)
    public String fetechSymmetricEncryptionKeyAndEncryptDB() {
        Proxyconfig proxyconfig = configrepo.findAll().get(0);
        
        if (proxyconfig != null && proxyconfig.getAesconfigured() == 1) {
            logger.info("AES Exists and Database already configured");
        } else {
            logger.info("Fetch the Symmetric key and encrypt the database");
            RestTemplate restTemplate = new RestTemplate();
            RestResponse result = null;
            try {
                String invocationurl = tenantadminurl + "/api/keytenantadmin/createsek/" + dbproxyid;
                logger.info("Invodation url: " + invocationurl);
                result = restTemplate.getForObject(invocationurl, RestResponse.class);
                
                if (result.getCode().equalsIgnoreCase(ResponseCode.SUCCESS.name())) {
                    //reconstruction process of aes key
                    String base64encodedaeskey = (String) result.getReturnobject();
                    byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedaeskey);
                    SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), SecretKey.class);
                    
                    for (int i = 1; i < 10; i++) {
                        Dbentry dbentry = new Dbentry();
                        dbentry.setKey("key" + i);
                        dbentry.setValue(Base64.getEncoder().encodeToString(SecurityUtil.encryptSymmetrically(aeskey, "value" + i)));
                        dbrepo.save(dbentry);
                    }//for
                    
                    //Self-check that encryption is working
                    Dbentry entry = dbrepo.findByKey("key1").get(0);
                    //decrypt-process of stored value
                    String  base64encrypted = entry.getValue();
                    byte[] base64dencryptedBytes = Base64.getDecoder().decode(base64encrypted);
                    String output = SecurityUtil.decryptSymmetrically(aeskey, base64dencryptedBytes);
                    logger.info("expected: value1 found: "+output);
                    
                    //set true to configured
                    proxyconfig.setAesconfigured(1);
                    configrepo.save(proxyconfig);
                    
                }//if

            } catch (Exception ex) {
                ex.printStackTrace();
                logger.severe("Exception during the invocation of initializeDatabase");
            }
        } // key does not exist
        return "ok";
    }//EoM

}//EoC