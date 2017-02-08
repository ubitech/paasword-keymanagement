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
package eu.paasword.keymanagement.paaswordapp.rest;

import java.util.Optional;
import java.util.logging.Logger;

import eu.paasword.keymanagement.paaswordapp.repository.dao.UserentryRepository;
import eu.paasword.keymanagement.paaswordapp.repository.service.PaaSwordService;
import eu.paasword.keymanagement.util.transfer.AppUserKey;
import eu.paasword.keymanagement.util.transfer.ProxyUserKey;
import eu.paasword.keymanagement.util.transfer.ResponseCode;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@RestController
@RequestMapping("/api/paaswordapp")
public class RestAPIController {

    private static final Logger logger = Logger.getLogger(RestAPIController.class.getName());

    @Autowired
    PaaSwordService paaSwordService;

    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        logger.info("Rest Request");
        return "echo";
    }

    @RequestMapping(value = "/registeruser", method = RequestMethod.POST)
    public RestResponse registeruser(@RequestBody AppUserKey appUserKey) {
        try {
            logger.info("Rest register the app key for a user to the database");

            return new RestResponse(ResponseCode.SUCCESS.name(), "Key registered successfully", paaSwordService.registerUser(appUserKey));
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return new RestResponse(ResponseCode.EXCEPTION.name(), ex.getMessage(), Optional.empty());
        }
    }//EoM
    
}
