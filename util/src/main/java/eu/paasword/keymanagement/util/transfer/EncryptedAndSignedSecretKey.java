/*
 *  Copyright 2016-2017 PaaSword Framework, http://www.paasword.eu/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.paasword.keymanagement.util.transfer;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
public class EncryptedAndSignedSecretKey {

    private String proxyid;
    private byte[] asymencryptedkey;
    private byte[] signature;

    public EncryptedAndSignedSecretKey() {
    }    
    
    public EncryptedAndSignedSecretKey(String proxyid, byte[] asymencryptedkey, byte[] signature) {
        this.proxyid = proxyid;
        this.asymencryptedkey = asymencryptedkey;
        this.signature = signature;
    }    
    
    public String getProxyid() {
        return proxyid;
    }

    public void setProxyid(String proxyid) {
        this.proxyid = proxyid;
    }
        
    public byte[] getAsymencryptedkey() {
        return asymencryptedkey;
    }

    public void setAsymencryptedkey(byte[] asymencryptedkey) {
        this.asymencryptedkey = asymencryptedkey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }    
    
}
