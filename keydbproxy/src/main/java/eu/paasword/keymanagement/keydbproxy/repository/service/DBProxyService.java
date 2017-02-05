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

import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.util.logging.Logger;
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

    //In this step the tenant-admin will be asked to create a new symmetric Key 
    //and will be fetched in order to be used only once
    public String initializeDatabase() {
        logger.info("Initialize the database");
        RestTemplate restTemplate = new RestTemplate();
        RestResponse result = null;
        try {
            String invocationurl = tenantadminurl + "/api/keytenantadmin/createsek/" + dbproxyid;
            logger.info("Invodation url: " + invocationurl);
            result = restTemplate.getForObject(invocationurl, RestResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Exception during the invocation of initializeDatabase");
        }
        return result.getMessage();
    }//EoM

}//EoC
