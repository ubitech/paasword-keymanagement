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

import eu.paasword.keymanagement.keydbproxy.repository.domain.ProxyConfiguration;
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
import eu.paasword.keymanagement.util.transfer.EncryptedAndSignedSecretKey;
import eu.paasword.keymanagement.util.transfer.ProxyRegistration;
import java.util.List;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Configuration
public class Initializer {

    private static final Logger logger = Logger.getLogger(Initializer.class.getName());

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
    public ProxyConfiguration createRSAKeyPair() {
        logger.info("Checking RSA for " + dbproxyid);
        ProxyConfiguration proxyconfig = null;
        if (configrepo.findAll().isEmpty() || configrepo.findAll().get(0).getPubkey() == null) {
            try {
                logger.info("Generating key pair");
                KeyPair keypair = SecurityUtil.generateRSAKeyPair(2048);
                proxyconfig = new ProxyConfiguration();
                proxyconfig.setPubkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPublic()).getBytes("UTF-8")));
                proxyconfig.setPrivkey(Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(keypair.getPrivate()).getBytes("UTF-8")));
                logger.info("keypair.getPublic().getClass " + keypair.getPublic().getClass());
                logger.info("keypair.getPrivate().getClass " + keypair.getPrivate().getClass());
                proxyconfig.setProxyid(dbproxyid);
                proxyconfig.setAessynched(0);
                proxyconfig.setPubsynched(0);
                configrepo.save(proxyconfig);

            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return proxyconfig;
    }//EoM

    @Bean
    @Order(2)
    public String transmitPublicKey() {
        logger.info("Transmit public key for " + dbproxyid);
        
        if (configrepo.findAll().get(0) != null &&  configrepo.findAll().get(0).getPubsynched() != 1 ) {
            try {
                
                RestTemplate restTemplate = new RestTemplate();
                ProxyRegistration proxiregistration = new ProxyRegistration(dbproxyid, configrepo.findAll().get(0).getPubkey());
                String invocationurl = tenantadminurl + "/api/keytenantadmin/registerproxy";
                RestResponse result = restTemplate.postForObject(invocationurl, proxiregistration, RestResponse.class);
                
                //update database
                ProxyConfiguration proxyconfig = configrepo.findAll().get(0);
                proxyconfig.setPubsynched(1);
                configrepo.save(proxyconfig);
                
            } catch (Exception ex) {
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "ok";
    }//EoM    

    @Bean
    @Order(3)
    public String generateSymmetricEncryptionKeyAndEncryptDB() {
        ProxyConfiguration proxyconfig = configrepo.findByProxyid(dbproxyid).get(0);

        if (proxyconfig != null && proxyconfig.getSecretkey() != null) {
            logger.info("AES Exists and Database already configured");
        } else {
            logger.info("Generate Symmetric key and encrypt the database");
            try {
                //Generate
                SecretKey aeskey = SecurityUtil.generateAESKey();
                String secretkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(aeskey).getBytes("UTF-8"));
                //Store
                proxyconfig.setSecretkey(secretkeyasstring);
                //
                dbrepo.deleteAll();
                //Encrypt
                for (int i = 1; i < 10; i++) {
                    Dbentry dbentry = new Dbentry();
                    dbentry.setKey("key" + i);
                    dbentry.setValue(Base64.getEncoder().encodeToString(SecurityUtil.encryptSymmetrically(aeskey, "value" + i)));
                    dbrepo.save(dbentry);
                }//for
                
                //Self-check that encryption is working
                Dbentry entry = dbrepo.findByKey("key1").get(0);
                //decrypt-process of stored value
                String base64encrypted = entry.getValue();
                byte[] base64dencryptedBytes = Base64.getDecoder().decode(base64encrypted);
                String output = SecurityUtil.decryptSymmetrically(aeskey, base64dencryptedBytes);
                logger.info("expected: value1 found: " + output);

                //save the generated key
                configrepo.save(proxyconfig);

            } catch (Exception ex) {
                ex.printStackTrace();
                logger.severe("Exception during the invocation of initializeDatabase");
            }
        } // key does not exist
        return "ok";
    }//EoM  

    @Bean
    @Order(4)
    public String transmitSymmetricEncryptionKeyToTenant() {
        try {
            ProxyConfiguration proxyconfig = configrepo.findByProxyid(dbproxyid).get(0);
            if (proxyconfig.getAessynched() != 1) {
                logger.info("AES Key not transmited so i will transmit it");
                String secretkeyasstring = proxyconfig.getSecretkey();
                logger.info("SecretKeyAsString: '" + secretkeyasstring + "'");
                String privkeyasstring = proxyconfig.getPrivkey();
                //transfer the key to tenant - secretkeyasstring will be firt digitally signed and then encrypted with the PubKey of Tenant
                //1 Sign them with the private key
                byte[] base64decodedBytes = Base64.getDecoder().decode(privkeyasstring);
                PrivateKey privkeyofproxy = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPrivateCrtKeyImpl.class);
                byte[] signature = SecurityUtil.signContent(secretkeyasstring, privkeyofproxy);    //signed private key
                //2 Encrypt it with the PubKey
                //fetch PubKey of Tenant
                RestTemplate restTemplate = new RestTemplate();
                String invocationurl = tenantadminurl + "/api/keytenantadmin/getpubkey";
                RestResponse result = restTemplate.getForObject(invocationurl, RestResponse.class);
                logger.info("PubKey of Tenant Fetched: \n" + result.getReturnobject());
                byte[] base64decodedBytes2 = Base64.getDecoder().decode((String) result.getReturnobject());
                PublicKey pubkeyoftenant = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), RSAPublicKeyImpl.class);
                logger.info("Public Key of Tenant has been reconstructed");
                //encrypt electronically signed payload
                byte[] asymencryptedkey = SecurityUtil.encryptAssymetrically(pubkeyoftenant, secretkeyasstring);
                //create payload straucture
                EncryptedAndSignedSecretKey encryptedkeyandsignature = new EncryptedAndSignedSecretKey(dbproxyid, asymencryptedkey, signature);
                //transmit it
                invocationurl = tenantadminurl + "/api/keytenantadmin/registersek/" + dbproxyid;
                result = restTemplate.postForObject(invocationurl, encryptedkeyandsignature, RestResponse.class);

                logger.info("Send Key response:" + result.getMessage());
                proxyconfig.setAessynched(1);
                proxyconfig.setSecretkey(null);
                configrepo.save(proxyconfig);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ok";
    }//EoM

}//EoC
