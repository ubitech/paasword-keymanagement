package eu.paasword.keymanagement.util.transfer;

import java.io.Serializable;

/**
 * Created by smantzouratos on 08/02/2017.
 */
public class ClientQueryContext implements Serializable {

    private String userID;
    private String proxyID;
    private String query;
    private String pubKeyEncryptedUserKey;

    public ClientQueryContext() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getPubKeyEncryptedUserKey() {
        return pubKeyEncryptedUserKey;
    }

    public void setPubKeyEncryptedUserKey(String pubKeyEncryptedUserKey) {
        this.pubKeyEncryptedUserKey = pubKeyEncryptedUserKey;
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
}
