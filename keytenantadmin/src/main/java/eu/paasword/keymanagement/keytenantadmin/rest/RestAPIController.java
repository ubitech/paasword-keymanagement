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
package eu.paasword.keymanagement.keytenantadmin.rest;

import eu.paasword.keymanagement.keytenantadmin.repository.service.TenantKeyManagementService;
import eu.paasword.keymanagement.util.transfer.AppRegistration;
import eu.paasword.keymanagement.util.transfer.EncryptedAndSignedSecretKey;
import eu.paasword.keymanagement.util.transfer.ProxyRegistration;
import eu.paasword.keymanagement.util.transfer.ResponseCode;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@RestController
@RequestMapping("/api/keytenantadmin")
public class RestAPIController {

    private static final Logger logger = Logger.getLogger(RestAPIController.class.getName());

    @Autowired
    TenantKeyManagementService tkm;

    @RequestMapping(value = "/getpubkey", method = RequestMethod.GET)
    public RestResponse getpubkey() {
        try {
            logger.info("Rest getpubkey");

            return new RestResponse(ResponseCode.SUCCESS.name(), "Proxy registered successfully", tkm.getPubKey());
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM       

    @RequestMapping(value = "/registerproxy", method = RequestMethod.POST)
    public RestResponse registerproxy(@RequestBody ProxyRegistration proxyregistration) {
        try {
            logger.info("Rest registerproxy");
            return new RestResponse(ResponseCode.SUCCESS.name(), "Proxy registered successfully", tkm.registerProxy(proxyregistration));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM    
    
    @RequestMapping(value = "/registerapp", method = RequestMethod.POST)
    public RestResponse registerapp(@RequestBody AppRegistration appregistration) {
        try {
            logger.info("Rest registerproxy");
            return new RestResponse(ResponseCode.SUCCESS.name(), "App registered successfully", tkm.registerApplication(appregistration));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM    

    /*
    *  This is the entry point of the Key Management Process since a proxy will 
    *  ask the tenant to create a new key. The proxyid should be pre-authorized
     */
    @RequestMapping(value = "/registersek/{dbproxyid}", method = RequestMethod.POST)
    public RestResponse registersek(@PathVariable("dbproxyid") String dbproxyid, @RequestBody EncryptedAndSignedSecretKey encryptedkeyandsignature) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "Key created", tkm.registerSymmetricEnrcyptionKey(encryptedkeyandsignature));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM       

    /*
    *  This method
     */
    @RequestMapping(value = "/registeruser/{dbproxyid}/{userid}", method = RequestMethod.GET)
    public RestResponse registeruser(@PathVariable("dbproxyid") String dbproxyid, @PathVariable("userid") String userid) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "User was successfully added", tkm.createKeysForUser(dbproxyid, userid));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM

    /*
    *  This method
     */
    @RequestMapping(value = "/getuserkey/{dbproxyid}/{userid}", method = RequestMethod.GET)
    public RestResponse getuserkey(@PathVariable("dbproxyid") String dbproxyid,@PathVariable("userid") String userid) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "User key successfully fetched", tkm.getUserKey(dbproxyid,userid));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM

    /*
    *  This method
     */
    @RequestMapping(value = "/getappkeys/{dbproxyid}", method = RequestMethod.GET)
    public RestResponse getappkeys(@PathVariable("dbproxyid") String dbproxyid) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "List successfully added", tkm.getappkeys(dbproxyid));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM      

    /*
    *  This method
     */
    @RequestMapping(value = "/getproxykeys/{dbproxyid}", method = RequestMethod.GET)
    public RestResponse getproxykeys(@PathVariable("dbproxyid") String dbproxyid) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "List successfully added", tkm.getproxykeys(dbproxyid));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM      

    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        logger.info("Rest Request");
        return "echo";
    }

}//EoC
