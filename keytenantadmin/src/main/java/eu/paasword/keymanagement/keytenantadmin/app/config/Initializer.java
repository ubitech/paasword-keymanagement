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
package eu.paasword.keymanagement.keytenantadmin.app.config;


import eu.paasword.keymanagement.keytenantadmin.repository.dao.TenantconfigRepository;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Tenantconfig;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Configuration
public class Initializer {
    
    private static final Logger logger = Logger.getLogger(Initializer.class.getName());
        
    @Autowired
    TenantconfigRepository configrepo;    
    
    @Bean
    @Order(1)
    public Tenantconfig chechRSAKeyPair() {
        logger.info("Checking RSA for Tenant Admin");
        Tenantconfig config = null;
        if (configrepo.findAll().isEmpty()) {
            try {
                logger.info("Generating key pair");
                KeyPair keypair = SecurityUtil.generateRSAKeyPair();
                config = new Tenantconfig();
                config.setPubkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPublic()).getBytes("UTF-8")));
                config.setPrivkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPrivate()).getBytes("UTF-8")));
                logger.info("keypair.getPublic().getClass " + keypair.getPublic().getClass());
                logger.info("keypair.getPrivate().getClass " + keypair.getPrivate().getClass());
                configrepo.save(config);                
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            logger.info("RSA key pair already exists i will check its validity");
            config = configrepo.findAll().get(0);
            byte[] base64decodedBytes = Base64.getDecoder().decode(config.getPubkey());
            byte[] base64decodedBytes2 = Base64.getDecoder().decode(config.getPrivkey());
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
        return config;
    }//EoM



}//EoC
