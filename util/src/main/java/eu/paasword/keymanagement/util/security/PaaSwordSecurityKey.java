package eu.paasword.keymanagement.util.security;

import java.io.Serializable;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class PaaSwordSecurityKey implements Serializable{

    private SecretKey key;
    private byte[] iv;      //nonce xor

        
    public PaaSwordSecurityKey(SecretKey key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }    
    
    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }    
    
}//EoC
