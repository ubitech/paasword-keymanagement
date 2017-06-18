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
package eu.paasword.keymanagement.keytenantadmin.repository.service;

import eu.paasword.keymanagement.keytenantadmin.repository.dao.AuthorizedProxyRepository;
import eu.paasword.keymanagement.keytenantadmin.repository.dao.TenantconfigRepository;
import eu.paasword.keymanagement.keytenantadmin.repository.dao.UserEntryRepository;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Authorizedproxy;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Userentry;
import eu.paasword.keymanagement.model.AppKey;
import eu.paasword.keymanagement.model.ProxyKey;
import eu.paasword.keymanagement.util.security.PaaSwordSecurityKey;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.AppRegistration;
import eu.paasword.keymanagement.util.transfer.EncryptedAndSignedSecretKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import eu.paasword.keymanagement.util.transfer.ProxyRegistration;
import eu.paasword.keymanagement.util.transfer.ResponseCode;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class TenantKeyManagementService {

    private static final Logger logger = Logger.getLogger(TenantKeyManagementService.class.getName());

    @Autowired
    AuthorizedProxyRepository authrepo;
    @Autowired
    UserEntryRepository userentryrepo;
    @Autowired
    TenantconfigRepository configrepo;

    @Value("${proxy.url}")
    String proxyURL;

    @Value("${paaswordapp.url}")
    String paaswordAppURL;

    public String getUserKey(String dbproxyid, String userid) {
        return userentryrepo.findByProxyidAndUserid(dbproxyid,userid).get(0).getUserkey();
    }//EoM

//    public String createKeysForUser(String dbproxyid, String userid) throws UnsupportedEncodingException, Exception {
//        String tenantkey = authrepo.findByProxyid(dbproxyid).get(0).getSecretkey(); //by default only one is accepted
//        byte[] base64decodedBytes = Base64.getDecoder().decode(tenantkey);
//        SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), SecretKey.class);
//        //is it a valid aes key?
//        String unencrypted = "test";
//        byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
//        String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);
//        if (unencrypted.equals(symdecrypted)) {
//            logger.info("It is a valid key!");
//        }
//        //Split the keys
//        SeparatedKeyContainer separated = SecurityUtil.splitKeyInParts(aeskey);
//        //Create DAO entity
//        Userentry userentry = new Userentry();
//        userentry.setUserid(userid);
//        userentry.setProxyid(dbproxyid);
//        String userkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(separated.getUserkey()).getBytes("UTF-8"));
//        String appkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(separated.getAppkey()).getBytes("UTF-8"));
//        String proxykeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(separated.getProxykey()).getBytes("UTF-8"));
//        userentry.setUserkey(userkeyasstring);
//        userentry.setAppkey(appkeyasstring);
//        userentry.setProxykey(proxykeyasstring);
//        //---
//        userentryrepo.save(userentry);
//
//        try {
//            Authorizedproxy authproxy = authrepo.findByProxyid(dbproxyid).get(0);
//            String proxyurl = authproxy.getProxyurl();
//            String appurl = authproxy.getAppurl();
//
//            //fetch PubKey of Proxy
//            RestTemplate restTemplate = new RestTemplate();
//            String invocationurl = proxyurl + "/api/keydbproxy/getpubkey/" + dbproxyid;
//            RestResponse result = restTemplate.getForObject(invocationurl, RestResponse.class);
//            logger.info("PubKey of Proxy Fetched: \n" + result.getReturnobject());
//            byte[] base64decodedBytes1 = Base64.getDecoder().decode((String) result.getReturnobject());
//            PublicKey pubkeyofproxy = SecurityUtil.deSerializeObject(new String(base64decodedBytes1, "utf-8"), RSAPublicKeyImpl.class);
//            logger.info("Public Key of Proxy has been reconstructed");
//
//            invocationurl = appurl + "/api/paaswordapp/getpubkey/" + dbproxyid;
//            result = restTemplate.getForObject(invocationurl, RestResponse.class);
//            logger.info("PubKey of App Fetched: \n" + result.getReturnobject());
//            byte[] base64decodedBytes2 = Base64.getDecoder().decode((String) result.getReturnobject());
//            PublicKey pubkeyofapp = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), RSAPublicKeyImpl.class);
//            logger.info("Public Key of App has been reconstructed");
//
//            //Encrypt assymetrically appkey & proxy
//            byte[] asymencryptedappkey = SecurityUtil.encryptAssymetrically(pubkeyofapp, appkeyasstring);
//            byte[] asymencryptedproxykey = SecurityUtil.encryptAssymetrically(pubkeyofproxy, proxykeyasstring);
//
//            EncryptedAndSignedUserKeys encryptedAndSignedUserKeys = new EncryptedAndSignedUserKeys(dbproxyid, userid, asymencryptedproxykey, asymencryptedappkey, null, null);
//            invocationurl = appurl + "/api/paaswordapp/registeruser";
//            result = restTemplate.postForObject(invocationurl, encryptedAndSignedUserKeys, RestResponse.class);
//            logger.info("Registeruser result: \n" + result.getReturnobject());
//
//        } catch (Exception ex) {
//            logger.severe("Exception during the transmition of the keys");
//        }
//
//        return "ok";
//    }//EoM

    public List<AppKey> getappkeys(String dbproxyid) {
        List<AppKey> appkeys = new ArrayList<>();
        List<Userentry> userentries = userentryrepo.findAll();
        for (Userentry userentry : userentries) {
            appkeys.add(new AppKey(dbproxyid, userentry.getAppkey(), userentry.getUserid()));
        }//for
        return appkeys;
    }//EoM

    public List<ProxyKey> getproxykeys(String dbproxyid) {
        List<ProxyKey> proxykeys = new ArrayList<>();
        List<Userentry> userentries = userentryrepo.findAll();
        for (Userentry userentry : userentries) {
            proxykeys.add(new ProxyKey(dbproxyid, userentry.getProxykey(), userentry.getUserid()));
        }//for
        return proxykeys;
    }//EoM

    public String registerProxy(ProxyRegistration proxyregistration) {
        if (authrepo.findByProxyid(proxyregistration.getProxyid()).isEmpty()) {
            logger.info("Not existing proxy! I will register for the first time");
            Authorizedproxy authentry = new Authorizedproxy();
            authentry.setProxyid(proxyregistration.getProxyid());
            authentry.setPubkeyofproxy(proxyregistration.getPublickey());
            authentry.setProxyurl(proxyregistration.getProxyurl());
            authrepo.save(authentry);
        } else {
            logger.info("Proxy already registered. I will replace it");
            Authorizedproxy authentry = authrepo.findByProxyid(proxyregistration.getProxyid()).get(0);
            authentry.setProxyid(proxyregistration.getProxyid());
            authentry.setPubkeyofproxy(proxyregistration.getPublickey());
            authentry.setProxyurl(proxyregistration.getProxyurl());
            authrepo.save(authentry);
        }
        return "ok";
    }//EoM

    public String registerApplication(AppRegistration appregistration) {
        if (authrepo.findByProxyid(appregistration.getProxyid()).isEmpty()) {
            logger.info("Not existing proxy! I will register for the first time");
            Authorizedproxy authentry = new Authorizedproxy();
            authentry.setProxyid(appregistration.getProxyid());
            authentry.setPubkeyofapp(appregistration.getPublickey());
            authentry.setAppurl(appregistration.getAppurl());
            authrepo.save(authentry);
        } else {
            logger.info("Proxy already registered. I will replace it");
            Authorizedproxy authentry = authrepo.findByProxyid(appregistration.getProxyid()).get(0);
            authentry.setProxyid(appregistration.getProxyid());
            authentry.setPubkeyofapp(appregistration.getPublickey());
            authentry.setAppurl(appregistration.getAppurl());
            authrepo.save(authentry);
        }
        return "ok";
    }//EoM    

    public String registerSymmetricEnrcyptionKey(EncryptedAndSignedSecretKey encryptedkeyandsignature) {
        try {
            String proxyid = encryptedkeyandsignature.getProxyid();
            byte[] asymencryptedkey = encryptedkeyandsignature.getAsymencryptedkey();
            byte[] signature = encryptedkeyandsignature.getSignature();
            //Step 1 - Decrypt the private key
            //fetch my Private Key
            String privkeyasstring = configrepo.findAll().get(0).getPrivkey();
            byte[] base64decodedBytes = Base64.getDecoder().decode(privkeyasstring);
            PrivateKey privkey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), RSAPrivateCrtKeyImpl.class);
            //decrypt the aes key
            String asymdecryptedkeyasstring = SecurityUtil.decryptAssymetrically(privkey, asymencryptedkey);
            //cast it to verify that it is a valid key
            byte[] base64decodedBytes2 = Base64.getDecoder().decode(asymdecryptedkeyasstring);
            PaaSwordSecurityKey paeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes2, "utf-8"), PaaSwordSecurityKey.class);
            String testmsg = "test input";
            String symencrypted = SecurityUtil.encryptSymmetrically(paeskey, testmsg);
            String symdecrypted = SecurityUtil.decryptSymmetrically(paeskey, symencrypted);
            if (symdecrypted.equalsIgnoreCase(testmsg)) {
                logger.info("Secret key was decrypted and casted");
            }
            //Step 2 - Verify the Secret Key Signature  by consulting the trust list
            String pubkeyofproxyasstring = authrepo.findByProxyid(proxyid).get(0).getPubkeyofproxy();
            byte[] base64decodedBytes3 = Base64.getDecoder().decode(pubkeyofproxyasstring);
            PublicKey pubkeyofproxy = SecurityUtil.deSerializeObject(new String(base64decodedBytes3, "utf-8"), RSAPublicKeyImpl.class);
            //create claim data
            String secretkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(paeskey).getBytes("UTF-8"));
            boolean verify = SecurityUtil.verifySignature(pubkeyofproxy, secretkeyasstring, signature);
            logger.info("Signature has been verified: " + verify);
            //save key
            Authorizedproxy authorizedproxy = authrepo.findByProxyid(proxyid).get(0);
            authorizedproxy.setSecretkey(secretkeyasstring);
            authrepo.save(authorizedproxy);
        } //EoM
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TenantKeyManagementService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TenantKeyManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ResponseCode.SUCCESS.toString();
    }//EoM

    public String getPubKey() {
        String pubkey = configrepo.findAll().get(0).getPubkey();   //configrepo.findAll().isEmpty()?:"":  TODO
        return pubkey;
    }

}//EoC
