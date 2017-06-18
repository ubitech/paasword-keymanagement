package eu.paasword.keymanagement.test;

import eu.paasword.keymanagement.util.security.PaaSwordSecurityKey;
import eu.paasword.keymanagement.util.security.SecurityUtil;
import eu.paasword.keymanagement.util.transfer.AppRegistration;
import eu.paasword.keymanagement.util.transfer.ProxyRegistration;
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
    
    
    private static String proxyid = "1234567";
    private static String proxypubkey="wqzDrQAFc3IAFGphdmEuc2VjdXJpdHkuS2V5UmVwwr3DuU/Cs8KIwprCpUMCAARMAAlhbGdvcml0aG10ABJMamF2YS9sYW5nL1N0cmluZztbAAdlbmNvZGVkdAACW0JMAAZmb3JtYXRxAH4AAUwABHR5cGV0ABtMamF2YS9zZWN1cml0eS9LZXlSZXAkVHlwZTt4cHQAA1JTQXVyAAJbQsKsw7MXw7gGCFTDoAIAAHhwAAABJjDCggEiMA0GCSrChkjChsO3DQEBAQUAA8KCAQ8AMMKCAQoCwoIBAQDCmjM/wozDlgtdwopcw48RED3CmsKnwojDkcKXw6LDj8KdwqV2bVU8wp3ClEFewrI1w4jCoFELw7k+wpV9wpPDnTnDjcKSwrNiwpA3wqdlw74hwqtPJlnDjMOwKFFjSkbDoyRYw4fCt0svVMKsw47ClsK2wr7Do0bCr0MJHVnCuXXCi8KyNDHCvcKCTzrCqcOiwoXCqMK5dsOUwrIEO0UBwps6Rn7CusKmwpPCtzJ9wowsax0rwppgVXDDg1DCgWbDtDwEH8OXYFoPbG7CpcKMwo/DuGDDnMKpDSgpwrVtXgzClMO6PMKiITIrwp9IRhZvasODw7bCuxLDmsKGbmcTQTI2LW/Du2jDvsKIwo/DnsKkw4oJwopSEFo7DQYtwqzCicOYw53DvcKcw7NRS8OuPcK0eBxIwoFJeypSGcKeJsKvw70OwqVDwpFDw5XCr8OSVsOJw7Yyw69Xw5QYNFTDm8KTHsO5dcKkNsOWB0rCux3DisOjwqVLAgMBAAF0AAVYLjUwOX5yABlqYXZhLnNlY3VyaXR5LktleVJlcCRUeXBlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAGUFVCTElD";
    private static String apppubkey="wqzDrQAFc3IAFGphdmEuc2VjdXJpdHkuS2V5UmVwwr3DuU/Cs8KIwprCpUMCAARMAAlhbGdvcml0aG10ABJMamF2YS9sYW5nL1N0cmluZztbAAdlbmNvZGVkdAACW0JMAAZmb3JtYXRxAH4AAUwABHR5cGV0ABtMamF2YS9zZWN1cml0eS9LZXlSZXAkVHlwZTt4cHQAA1JTQXVyAAJbQsKsw7MXw7gGCFTDoAIAAHhwAAABJjDCggEiMA0GCSrChkjChsO3DQEBAQUAA8KCAQ8AMMKCAQoCwoIBAQDCt8ODYMODw6tQw4gEw7PDmhdwwp/CkcOADE84ew02DMOLRFJGXyTCvGYewrMfc8OHW8KMHcOLwpnDnWQHw5I3Y8KGI1N5w5tWwqfDjcOsw5gjcMOBw7zCr8O7e2A7SMOCw4Ezw5IILcOpw63CucOAwpDCkGg+Mk7CiMKPWjXCj8KlGT4gRzbCk8OLwoHDgsKJbcOhwohww7IxZkbDhsK7w54IwrfCv8O9w4Z5ERjDosO9w7PDnxpgHcOGIsOpwpHCpBfCvSw7woBoW8KQNWACwot+w6Y6wqHDicOTw67CtsOtw7DDnMKHQXbDs8KiV8O5AR7CoMOWw7VYwp0AwpnCqhHCvsOjZC7DqToaXjpUw5LDp3rDkxM5QwQfZsKCPD0mwobCgTvCrcKrMsKdwpnCuHfDvsO4BVDDtGHDvMKiw4Vxw5YAwr1Mw5PCgMOMfBFEw7hQwpJcZTLCm17ChD3DiMKjwoDCiMOAbn1rTcKKw6vCthEfLsKXwpdHwo5uw4cnAgMBAAF0AAVYLjUwOX5yABlqYXZhLnNlY3VyaXR5LktleVJlcCRUeXBlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAGUFVCTElD";
    
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        RestResponse result = null;
        String invocationurl;
        try {
            //register a new proxy
            //This is not needed. Instead boot sequentially 
            //1-Keytenantadmin
            //2-dbproxy
            //3-paaswordapp
            
//            invocationurl = tenantadminurl + "/api/keytenantadmin/registerproxy";
//            ProxyRegistration proxyregistration = new ProxyRegistration(proxyid, proxypubkey, proxyurl);
//            result = restTemplate.postForObject(invocationurl, proxyregistration, RestResponse.class);
//            logger.info("Proxy registered: "+result);
//            
//            //register a new app
//            invocationurl = tenantadminurl + "/api/keytenantadmin/registerapp";
//            AppRegistration appregistration = new AppRegistration(proxyid, apppubkey, appurl);
//            result = restTemplate.postForObject(invocationurl, appregistration, RestResponse.class);
//            logger.info("App registered: "+result);            
            
            
            String userid = "12";
            //Step 1 Register User            
            invocationurl = tenantadminurl + "/api/keytenantadmin/registeruser/" + proxyid + "/" + userid;
            result = restTemplate.getForObject(invocationurl, RestResponse.class);
            logger.info("Result of register user: " + result.getReturnobject().toString());

            //Step 2 - fetch user key & test casting
            invocationurl = tenantadminurl + "/api/keytenantadmin/getuserkey/" + proxyid + "/" + userid;
            result = restTemplate.getForObject(invocationurl, RestResponse.class);
            logger.info("User Key: " + result.getReturnobject());
            String userkeyasstring = (String) result.getReturnobject();
            byte[] base64decodedBytes = Base64.getDecoder().decode(userkeyasstring);
            PaaSwordSecurityKey aeskey = SecurityUtil.deSerializeObject(new String(base64decodedBytes, "utf-8"), PaaSwordSecurityKey.class);
            //is it a valid aes key?
            String unencrypted = "test";
            String symencrypted = SecurityUtil.encryptSymmetrically(aeskey, unencrypted);
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
