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

import eu.paasword.keymanagement.paaswordapp.repository.dao.RsakeypairRepository;
import eu.paasword.keymanagement.paaswordapp.repository.domain.Rsakeypair;
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
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Configuration
public class KeyPairInitializer {

    private static final Logger logger = Logger.getLogger(KeyPairInitializer.class.getName());

    @Value("${dbproxy.id}")
    private String dbproxyid;

    @Autowired
    RsakeypairRepository rsarepo;

    @Bean
    public Rsakeypair chechRSAKeyPair() {
        logger.info("Checking RSA for " + dbproxyid);
        Rsakeypair rsakeypair = null;
        if (rsarepo.findAll().isEmpty()) {
            try {
                logger.info("Generating key pair");
                KeyPair keypair = SecurityUtil.generateRSAKeyPair();
                rsakeypair = new Rsakeypair();
                rsakeypair.setPubkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPublic()).getBytes("UTF-8")));
                rsakeypair.setPrivkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPrivate()).getBytes("UTF-8")));
                logger.info("keypair.getPublic().getClass " + keypair.getPublic().getClass());
                logger.info("keypair.getPrivate().getClass " + keypair.getPrivate().getClass());

                rsarepo.save(rsakeypair);

            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(KeyPairInitializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            rsakeypair = rsarepo.findAll().get(0);
            byte[] base64decodedBytes = Base64.getDecoder().decode(rsakeypair.getPubkey());
            byte[] base64decodedBytes2 = Base64.getDecoder().decode(rsakeypair.getPrivkey());
            PublicKey pubkey;
            PrivateKey privkey;
            try {
                pubkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPublicKeyImpl.class);
                privkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), RSAPrivateCrtKeyImpl.class);
                //PrivateKey privkey = new RSAPrivateKeyImpl()
                String unencrypted = "test";
                byte[] asymencrypted = SecurityUtil.encryptAssymetrically(pubkey, unencrypted);
                String asymdecrypted = SecurityUtil.decryptAssymetrically(privkey, asymencrypted);
                if (unencrypted.equals(asymdecrypted)) logger.info("RSA Key Existing and is valid");
                logger.info(" unencrypted: " + unencrypted + " asymencrypted: " + asymencrypted + " asymdecrypted: " + asymdecrypted);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(KeyPairInitializer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return rsakeypair;
    }//EoM

}//EoC
