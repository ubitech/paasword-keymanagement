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
package eu.paasword.keymanagement.keydbproxy.repository.service;

import eu.paasword.keymanagement.keydbproxy.repository.dao.UserentryRepository;
import eu.paasword.keymanagement.keydbproxy.repository.domain.Userentry;
import eu.paasword.keymanagement.util.transfer.AppQueryContext;
import eu.paasword.keymanagement.util.transfer.ProxyUserKey;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Service
public class DBProxyService {

    private static final Logger logger = Logger.getLogger(DBProxyService.class.getName());

    @Value("${tenantadmin.url}")
    private String tenantadminurl;

    @Value("${dbproxy.id}")
    private String dbproxyid;

    @Autowired
    UserentryRepository userentryRepository;


    public boolean registerUser(ProxyUserKey proxyUserKey) {
        logger.info("Register proxy key for the user");

        Userentry userentry = new Userentry();
        userentry.setProxyid(proxyUserKey.getProxyID());
        userentry.setUserid(proxyUserKey.getUserID());
        userentry.setProxykey(proxyUserKey.getProxyKey());
        //---
        userentryRepository.save(userentry);

        return true;
    }//EoM

    public String queryHandler(AppQueryContext appQueryContext) {
        logger.info("Querying handling..");

        // Step 1: Decrypt User Key

        // Step 2: Reconstruct tenant key based on user, app and proxy key

        // Step 2a: Check timestamp for replay attacks (white box encryption ???)

        // Step 3: Query DB and return results assymetricly encrypted

        return null;
    }//EoM

}//EoC
