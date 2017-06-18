package eu.paasword.keymanagement.util.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class PaaSwordSecurityKey {

    private SecretKey key;
    private GCMParameterSpec spec;      //nonce xor
    private String aad;                 // getBytes()-->xor 

    public PaaSwordSecurityKey(SecretKey key, GCMParameterSpec spec, String aad) {
        this.key = key;
        this.spec = spec;
        this.aad = aad;
    }       
    
    public PaaSwordSecurityKey(SecretKey key, GCMParameterSpec spec) {
        this.key = key;
        this.spec = spec;
    }    
    
    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public GCMParameterSpec getSpec() {
        return spec;
    }

    public void setSpec(GCMParameterSpec spec) {
        this.spec = spec;
    }    

    public String getAad() {
        return aad;
    }

    public void setAad(String aad) {
        this.aad = aad;
    }        
    
}//EoC
