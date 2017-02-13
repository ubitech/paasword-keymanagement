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
import eu.paasword.keymanagement.keytenantadmin.repository.dao.UserEntryRepository;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Authorizedproxy;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Userentry;
import eu.paasword.keymanagement.keytenantadmin.repository.service.exception.DBProxyNotAuthorizedException;
import eu.paasword.keymanagement.model.AppKey;
import eu.paasword.keymanagement.model.ProxyKey;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.security.SeparatedKeyContainer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

import eu.paasword.keymanagement.util.transfer.AppUserKey;
import eu.paasword.keymanagement.util.transfer.ProxyRegistration;
import eu.paasword.keymanagement.util.transfer.ProxyUserKey;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

    @Value("${proxy.url}")
    String proxyURL;

    @Value("${paaswordapp.url}")
    String paaswordAppURL;

    public String getUserKey(String userid) {

        return userentryrepo.findByUserid(userid).getUserkey();

    }//EoM


    public String createKeysForUser(String dbproxyid, String userid) throws UnsupportedEncodingException, Exception {
        String tenantkey = authrepo.findByProxyid(dbproxyid).get(0).getSecretkey(); //by default only one is accepted
        byte[] base64decodedBytes = Base64.getDecoder().decode(tenantkey);
        SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), SecretKey.class);
        //is it a valid aes key?
        String unencrypted = "test";
        byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
        String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);
        if (unencrypted.equals(symdecrypted)) {
            logger.info("It is a valid key!");
        }
        //Split the keys
        SeparatedKeyContainer separated = SecurityUtil.splitKeyInParts(aeskey);
        //Create DAO entity
        Userentry userentry = new Userentry();
        userentry.setUserid(userid);
        userentry.setProxyid(dbproxyid);
        String userkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(separated.getUserkey()).getBytes("UTF-8"));
        String appkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(separated.getAppkey()).getBytes("UTF-8"));
        String proxykeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(separated.getProxykey()).getBytes("UTF-8"));
        userentry.setUserkey(userkeyasstring);
        userentry.setAppkey(appkeyasstring);
        userentry.setProxykey(proxykeyasstring);
        //---
        userentryrepo.save(userentry);

        // Sending keys to proxy and app
        RestTemplate restTemplate = new RestTemplate();
        RestResponse result = null;
        try {
            String invocationurl = proxyURL + "/api/keydbproxy/registeruser";
//            logger.info("Invodation url: " + invocationurl);

            ProxyUserKey proxyUserKey = new ProxyUserKey(userid, dbproxyid, proxykeyasstring);

            result = restTemplate.postForObject(invocationurl, proxyUserKey, RestResponse.class);

            invocationurl = paaswordAppURL + "/api/paaswordapp/registeruser";
//            logger.info("Invodation url: " + invocationurl);

            AppUserKey appUserKey = new AppUserKey(userid, dbproxyid, appkeyasstring);

            result = restTemplate.postForObject(invocationurl, appUserKey, RestResponse.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Exception during the invocation of register key to proxy");
        }

        return "ok";
    }//EoM

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

    public String registerProxy(ProxyRegistration proxiregistration) {
        if ( authrepo.findByProxyid(proxiregistration.getProxyid()).isEmpty() ){
            Authorizedproxy authentry = new Authorizedproxy();
            authentry.setProxyid(proxiregistration.getProxyid());
            authentry.setPubkeyofproxy(proxiregistration.getPublickey());
            authrepo.save(authentry);            
        } else {
            
        }

        return "ok";
    }//EoM
    
}//EoC
