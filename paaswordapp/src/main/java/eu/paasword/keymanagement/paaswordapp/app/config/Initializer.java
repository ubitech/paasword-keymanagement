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
package eu.paasword.keymanagement.paaswordapp.app.config;

import eu.paasword.keymanagement.paaswordapp.repository.dao.AppconfigRepository;
import eu.paasword.keymanagement.paaswordapp.repository.domain.AppConfiguration;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.AppRegistration;
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

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Configuration
public class Initializer {

    private static final Logger logger = Logger.getLogger(Initializer.class.getName());
    private static final int keysize = 4096;
    
    @Value("${dbproxy.id}")
    private String dbproxyid;

    @Value("${tenantadmin.url}")
    private String tenantadminurl;
    
    @Value("${paaswordapp.url}")
    private String paaswordappurl;
    
    @Value("${proxy.url}")
    private String proxyurl;

    @Autowired
    AppconfigRepository configrepo;

    @Bean
    @Order(1)
    public AppConfiguration createRSAKeyPair() {
        logger.info("Checking RSA for " + dbproxyid);
        AppConfiguration appconfig = null;
        if (configrepo.findAll().isEmpty() || configrepo.findAll().get(0).getPubkey() == null) {
            try {
                logger.info("Generating key pair");
                KeyPair keypair = SecurityUtil.generateRSAKeyPair(4096);
                appconfig = new AppConfiguration();
                appconfig.setPubkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPublic()).getBytes("UTF-8")));
                appconfig.setPrivkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPrivate()).getBytes("UTF-8")));
                logger.info("keypair.getPublic().getClass " + keypair.getPublic().getClass());
                logger.info("keypair.getPrivate().getClass " + keypair.getPrivate().getClass());
                appconfig.setProxyid(dbproxyid);
                appconfig.setPubsynched(0);
                configrepo.save(appconfig);

            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            logger.info("RSA key pair already exists i will check its validity");
            appconfig = configrepo.findAll().get(0);
            byte[] base64decodedBytes = Base64.getDecoder().decode(appconfig.getPubkey());
            byte[] base64decodedBytes2 = Base64.getDecoder().decode(appconfig.getPrivkey());
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
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return appconfig;
    }//EoM

    @Bean
    @Order(2)
    public String transmitPublicKey() {
        logger.info("Transmit public key for " + dbproxyid);
        
        if (configrepo.findAll().get(0) != null &&  configrepo.findAll().get(0).getPubsynched() != 1 ) {
            try {
                
                RestTemplate restTemplate = new RestTemplate();
                AppRegistration appregistration = new AppRegistration(dbproxyid, configrepo.findAll().get(0).getPubkey(), paaswordappurl);
                String invocationurl = tenantadminurl + "/api/keytenantadmin/registerapp";
                RestResponse result = restTemplate.postForObject(invocationurl, appregistration, RestResponse.class);
                
                //update database
                AppConfiguration proxyconfig = configrepo.findAll().get(0);
                proxyconfig.setPubsynched(1);
                configrepo.save(proxyconfig);
                
            } catch (Exception ex) {
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "ok";
    }//EoM

    
    
}//EoC
