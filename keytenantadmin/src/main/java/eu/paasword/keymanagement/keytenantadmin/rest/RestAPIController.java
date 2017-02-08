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
import eu.paasword.keymanagement.util.transfer.ResponseCode;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    /*
    *  This is the entry point of the Key Management Process since a proxy will 
    *  ask the tenant to create a new key. The proxyid should be pre-authorized
    */
    @RequestMapping(value = "/createsek/{dbproxyid}", method = RequestMethod.GET)
    public RestResponse createsymmetricencryptionkey(@PathVariable("dbproxyid") String dbproxyid) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "Key created", tkm.generateSymmetricEnrcyptionKey(dbproxyid));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM       

    /*
    *  This method
    */
    @RequestMapping(value = "/registeruser/{dbproxyid}/{userid}", method = RequestMethod.GET)
    public RestResponse registeruser( @PathVariable("dbproxyid") String dbproxyid , @PathVariable("userid") String userid ) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "User was successfully added", tkm.createKeysForUser(dbproxyid, userid) );
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM

    /*
    *  This method
    */
    @RequestMapping(value = "/getuserkey/{userid}", method = RequestMethod.GET)
    public RestResponse getuserkey(@PathVariable("userid") String userid) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "User key successfully fetched", tkm.getUserKey(userid));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM
    
    /*
    *  This method
    */
    @RequestMapping(value = "/getappkeys/{dbproxyid}", method = RequestMethod.GET)
    public RestResponse getappkeys( @PathVariable("dbproxyid") String dbproxyid ) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "List successfully added", tkm.getappkeys(dbproxyid) );
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM      
    
    /*
    *  This method
    */
    @RequestMapping(value = "/getproxykeys/{dbproxyid}", method = RequestMethod.GET)
    public RestResponse getproxykeys( @PathVariable("dbproxyid") String dbproxyid ) {
        try {
            return new RestResponse(ResponseCode.SUCCESS.name(), "List successfully added", tkm.getproxykeys(dbproxyid) );
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
