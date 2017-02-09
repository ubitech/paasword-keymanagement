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
package eu.paasword.keymanagement.paaswordapp.repository.service;

import java.util.logging.Logger;

import eu.paasword.keymanagement.paaswordapp.repository.dao.UserentryRepository;
import eu.paasword.keymanagement.paaswordapp.repository.domain.Userentry;
import eu.paasword.keymanagement.util.transfer.ClientQueryContext;
import eu.paasword.keymanagement.util.transfer.AppUserKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class PaaSwordService {

    private static final Logger logger = Logger.getLogger(PaaSwordService.class.getName());

    @Autowired
    UserentryRepository userentryRepository;

    public boolean registerUser(AppUserKey appUserKey) {
        logger.info("Register app key for the user");

        Userentry userentry = new Userentry();
        userentry.setProxyid(appUserKey.getProxyID());
        userentry.setUserid(appUserKey.getUserID());
        userentry.setAppkey(appUserKey.getAppKey());
        //---
        userentryRepository.save(userentry);

        return true;
    }//EoM

    public String queryHandler(ClientQueryContext appUserKey) {
        logger.info("Querying handling..");

        // Step 1: Get App Key for userID

        // Step 2: Forward query to DBProxy.query

        return null;
    }//EoM

}//EoC
