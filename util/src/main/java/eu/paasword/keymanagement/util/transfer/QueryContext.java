package eu.paasword.keymanagement.util.transfer;

import java.io.Serializable;

/**
 * Created by smantzouratos on 08/02/2017.
 */
public class QueryContext implements Serializable {

    private String userid;
    private String proxyid;
    private String query;
    private byte[] asymencrypteduserkey;
    private byte[] asymencryptedappkey;

    public QueryContext() {
    }

    public QueryContext(String userid, String proxyid, String query, byte[] asymencrypteduserkey) {
        this.userid = userid;
        this.proxyid = proxyid;
        this.query = query;
        this.asymencrypteduserkey = asymencrypteduserkey;
    }    
    
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProxyid() {
        return proxyid;
    }

    public void setProxyid(String proxyid) {
        this.proxyid = proxyid;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public byte[] getAsymencrypteduserkey() {
        return asymencrypteduserkey;
    }

    public void setAsymencrypteduserkey(byte[] asymencrypteduserkey) {
        this.asymencrypteduserkey = asymencrypteduserkey;
    }

    public byte[] getAsymencryptedappkey() {
        return asymencryptedappkey;
    }

    public void setAsymencryptedappkey(byte[] asymencryptedappkey) {
        this.asymencryptedappkey = asymencryptedappkey;
    }    
    
}
