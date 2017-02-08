package eu.paasword.keymanagement.util.transfer;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by smantzouratos on 08/02/2017.
 */
public class ProxyUserKey implements Serializable {

    private String userID;
    private String proxyID;
    private String proxyKey;

    public ProxyUserKey(String userID, String proxyID, String proxyKey) {
        this.userID = userID;
        this.proxyID = proxyID;
        this.proxyKey = proxyKey;
    }

    public ProxyUserKey() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProxyID() {
        return proxyID;
    }

    public void setProxyID(String proxyID) {
        this.proxyID = proxyID;
    }

    public String getProxyKey() {
        return proxyKey;
    }

    public void setProxyKey(String proxyKey) {
        this.proxyKey = proxyKey;
    }
}
