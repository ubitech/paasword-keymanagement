package eu.paasword.keymanagement.util.transfer;

import java.io.Serializable;

/**
 * Created by smantzouratos on 08/02/2017.
 */
public class AppUserKey implements Serializable {

    private String userID;
    private String proxyID;
    private String appKey;

    public AppUserKey(String userID, String proxyID, String appKey) {
        this.userID = userID;
        this.proxyID = proxyID;
        this.appKey = appKey;
    }

    public AppUserKey() {
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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
