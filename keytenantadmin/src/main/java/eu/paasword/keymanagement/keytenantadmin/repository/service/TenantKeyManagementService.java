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
import eu.paasword.keymanagement.keytenantadmin.repository.dao.TenantkeyRepository;
import eu.paasword.keymanagement.keytenantadmin.repository.dao.UserEntryRepository;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Tenantkey;
import eu.paasword.keymanagement.keytenantadmin.repository.domain.Userentry;
import eu.paasword.keymanagement.keytenantadmin.repository.service.exception.DBProxyNotAuthorizedException;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.security.SeparatedKeyContainer;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    TenantkeyRepository tenantrepo;
    @Autowired
    UserEntryRepository userentryrepo;
    
    public String generateSymmetricEnrcyptionKey(String dbproxyid) throws DBProxyNotAuthorizedException, UnsupportedEncodingException {
        SecretKey secretkey = null;
        String secretkeyasstring;
        if (authrepo.findByProxyid(dbproxyid) == null) {
            logger.info("Proxy not authorized");
            throw new DBProxyNotAuthorizedException("Proxy not Authorized");
        } else {  //proxy is authorized
            logger.info("AES key will be created for " + dbproxyid);
            //generate a key and the subkeys
            secretkey = SecurityUtil.generateAESKey();
            Tenantkey tenantkey = new Tenantkey();
            tenantkey.setProxyid(dbproxyid);
            secretkeyasstring = Base64.getEncoder().encodeToString(SecurityUtil.serializeObject(secretkey).getBytes("UTF-8"));
            tenantkey.setSecretkey(secretkeyasstring);
            tenantrepo.save(tenantkey);
        }//else
        return secretkeyasstring;
    }//EoM

    public String createKeysForUser(String dbproxyid, String userid) throws UnsupportedEncodingException, Exception {
        Tenantkey tenantkey = tenantrepo.findByProxyid(dbproxyid);
        byte[] base64decodedBytes = Base64.getDecoder().decode(tenantkey.getSecretkey());
        SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), SecretKey.class);
        //is it a valid aes key?
        String unencrypted = "test";
        byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
        String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);        
        if (unencrypted.equals(symdecrypted)) logger.info("It is a valid key!");
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
        
        return "ok";
    }//EoM

}//EoC
