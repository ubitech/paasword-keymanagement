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
package eu.paasword.keymanagement.keydbproxy.rest;

import eu.paasword.keymanagement.keydbproxy.repository.dao.ConfigurationRepository;
import eu.paasword.keymanagement.keydbproxy.repository.service.DBProxyService;
import eu.paasword.keymanagement.util.transfer.*;

import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@RestController
@RequestMapping("/api/keydbproxy")
public class RestAPIController {

    private static final Logger logger = Logger.getLogger(RestAPIController.class.getName());
    
    @Autowired
    DBProxyService dbproxy;

    @Autowired
    ConfigurationRepository configurationRepository;
    
    @RequestMapping(value = "/encryptdb", method = RequestMethod.GET)
    public RestResponse encryptdb() {
        try {
            logger.info("Rest request to encrypt the database");
            return new RestResponse(ResponseCode.SUCCESS.name(), "Database was configured", dbproxy.initializeDatabase() );
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM

    @RequestMapping(value = "/getpubkey/{proxyid}", method = RequestMethod.GET)
    public RestResponse getPublicKey(@PathVariable("proxyid") String proxyid) {
        try {
            logger.info("Rest request to get public key");
            return new RestResponse(ResponseCode.SUCCESS.name(), "Database was configured", configurationRepository.findAll().get(0).getPubkey());
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM


    @RequestMapping(value = "/registeruser", method = RequestMethod.POST)
    public RestResponse registeruser(@RequestBody ProxyUserKey proxyUserKey) {
        try {
            logger.info("Rest register the proxy key for a user to the database");

            return new RestResponse(ResponseCode.SUCCESS.name(), "Key registered successfully", dbproxy.registerUser(proxyUserKey));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public RestResponse query(@RequestBody AppQueryContext appQueryContext) {
        try {
            logger.info("Querying DB Proxy for " + appQueryContext.getQuery());

            return new RestResponse(ResponseCode.SUCCESS.name(), "Key registered successfully", dbproxy.queryHandler(appQueryContext));
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
    
}
