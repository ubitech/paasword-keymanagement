package eu.paasword.keymanagement.test;

import eu.paasword.keymanagement.util.transfer.ProxyRegistration;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

/**
 * Created by smantzouratos on 09/02/2017.
 */
public class ClientTest {

    private static final Logger logger = Logger.getLogger(ClientTest.class.getName());

    private static String tenantadminurl = "http://127.0.0.1:8081";
    private static String proxyurl = "http://127.0.0.1:8080";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        RestResponse result = null;
        try {
            String proxyid = "1234567";
            String userid = "5";
            //Step 1 Get Public Key of Proxy            
            String invocationurl = tenantadminurl + "/api/keytenantadmin/registeruser/" + proxyid + "/" + userid;
            result = restTemplate.getForObject(invocationurl, RestResponse.class);
            logger.info("PubKey of Proxy: " + result.getReturnobject().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Exception during the invocation of register key to proxy");
        }
    }//EoM

}//EoC
