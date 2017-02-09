package eu.paasword.keymanagement.test;

import eu.paasword.keymanagement.util.transfer.RestResponse;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

/**
 * Created by smantzouratos on 09/02/2017.
 */
public class ClientTest {

    private static final Logger logger = Logger.getLogger(ClientTest.class.getName());

    public static void main(String[] args) {

        String tenantAdmin = "http://127.0.0.1:8081";
        String proxyURL = "http://127.0.0.1:8080";
        String proxyID = "1234567";
        String userID = "2";

        // Step 1: Get Public Key of Proxy
        RestTemplate restTemplate = new RestTemplate();
        RestResponse result = null;
        String pubKeyProxy = null;
        String userKey = null;

        try {
            String invocationurl = proxyURL + "/api/keydbproxy/getpubkey/" + proxyID;

            result = restTemplate.getForObject(invocationurl, RestResponse.class);

            logger.info("PubKey of Proxy: " + result.getReturnobject().toString());

            pubKeyProxy = result.getReturnobject().toString();

            // Step 2: Get User Key from Tenant Admin

            invocationurl = tenantAdmin + "/api/keytenantadmin/getuserkey/" + userID;

            result = restTemplate.getForObject(invocationurl, RestResponse.class);

            logger.info("User key from Tenant Admin: " + result.getReturnobject().toString());

            userKey = result.getReturnobject().toString();

            // Step 3: Encrypt User Key with Public Key of the DB Proxy



            // Step 4: Invoke Rest PaaSwordApp.query


        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Exception during the invocation of register key to proxy");
        }


    }

}
