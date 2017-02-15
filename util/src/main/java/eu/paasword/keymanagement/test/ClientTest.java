package eu.paasword.keymanagement.test;

import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.QueryContext;
import eu.paasword.keymanagement.util.transfer.RestResponse;
import java.security.PublicKey;
import java.util.Base64;
import org.springframework.web.client.RestTemplate;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import sun.security.rsa.RSAPublicKeyImpl;

/**
 * Created by smantzouratos on 09/02/2017.
 */
public class ClientTest {

    private static final Logger logger = Logger.getLogger(ClientTest.class.getName());

    private static String tenantadminurl = "http://127.0.0.1:8081";
    private static String proxyurl = "http://127.0.0.1:8080";
    private static String appurl = "http://127.0.0.1:8082";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        RestResponse result = null;
        String invocationurl;
        try {
            String proxyid = "1234567";
            String userid = "10";
            //Step 1 Register User            
//            invocationurl = tenantadminurl + "/api/keytenantadmin/registeruser/" + proxyid + "/" + userid;
//            result = restTemplate.getForObject(invocationurl, RestResponse.class);
//            logger.info("Result of register user: " + result.getReturnobject().toString());

            //Step 2 - fetch user key & test casting
            invocationurl = tenantadminurl + "/api/keytenantadmin/getuserkey/" + proxyid + "/" + userid;
            result = restTemplate.getForObject(invocationurl, RestResponse.class);
            logger.info("User Key: " + result.getReturnobject());
            String userkeyasstring = (String) result.getReturnobject();
            byte[] base64decodedBytes = Base64.getDecoder().decode(userkeyasstring);
            SecretKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), SecretKey.class);
            //is it a valid aes key?
            String unencrypted = "test";
            byte[] symencrypted = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
            String symdecrypted = SecurityUtil.decryptSymmetrically(aeskey, symencrypted);
            if (unencrypted.equals(symdecrypted)) {
                logger.info("It is a valid key!");
            }

            //step 3 - Fetch Pub Key of Proxy
            //fetch PubKey of Proxy
            invocationurl = proxyurl + "/api/keydbproxy/getpubkey/" + proxyid;
            result = restTemplate.getForObject(invocationurl, RestResponse.class);
            logger.info("PubKey of Proxy Fetched: \n" + result.getReturnobject());
            byte[] base64decodedBytes1 = Base64.getDecoder().decode((String) result.getReturnobject());
            PublicKey pubkeyofproxy = SecurityUtil.deSerializeObject(new String(base64decodedBytes1, "utf-8"), RSAPublicKeyImpl.class);
            logger.info("Public Key of Proxy has been reconstructed");

            //step 4 - Encrypt userkey with proxy pub-key
            logger.info("Encryption user key");
            byte[] asymencrypteduserkey = SecurityUtil.encryptAssymetrically(pubkeyofproxy, userkeyasstring);

            //step 5 - Create and send query
            QueryContext query = new QueryContext(userid, proxyid, "key1", asymencrypteduserkey);
            invocationurl = appurl + "/api/paaswordapp/query";
            result = restTemplate.postForObject(invocationurl, query, RestResponse.class);
            logger.info("result: " + result.getReturnobject());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Exception during the invocation of register key to proxy");
        }
    }//EoM

}//EoC
