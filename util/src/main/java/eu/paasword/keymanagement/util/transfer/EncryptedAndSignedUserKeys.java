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
public class EncryptedAndSignedUserKeys {

    private String proxyid;
    private String userid;
    private byte[] asymencryptedproxykey;
    private byte[] asymencryptedappkey;
    private byte[] proxykeysignature;
    private byte[] appkeysignature;

    public EncryptedAndSignedUserKeys() {
    }    

    public EncryptedAndSignedUserKeys(String proxyid, String userid,byte[] asymencryptedproxykey, byte[] asymencryptedappkey, byte[] proxykeysignature, byte[] appkeysignature) {
        this.proxyid = proxyid;
        this.userid = userid;
        this.asymencryptedproxykey = asymencryptedproxykey;
        this.asymencryptedappkey = asymencryptedappkey;
        this.proxykeysignature = proxykeysignature;
        this.appkeysignature = appkeysignature;
    }    
    
    public String getProxyid() {
        return proxyid;
    }

    public void setProxyid(String proxyid) {
        this.proxyid = proxyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }    
    
    public byte[] getAsymencryptedproxykey() {
        return asymencryptedproxykey;
    }

    public void setAsymencryptedproxykey(byte[] asymencryptedproxykey) {
        this.asymencryptedproxykey = asymencryptedproxykey;
    }

    public byte[] getAsymencryptedappkey() {
        return asymencryptedappkey;
    }

    public void setAsymencryptedappkey(byte[] asymencryptedappkey) {
        this.asymencryptedappkey = asymencryptedappkey;
    }

    public byte[] getProxykeysignature() {
        return proxykeysignature;
    }

    public void setProxykeysignature(byte[] proxykeysignature) {
        this.proxykeysignature = proxykeysignature;
    }

    public byte[] getAppkeysignature() {
        return appkeysignature;
    }

    public void setAppkeysignature(byte[] appkeysignature) {
        this.appkeysignature = appkeysignature;
    }

    
    
}//EoM
