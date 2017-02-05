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
package eu.paasword.keymanagement.util.security;

import javax.crypto.SecretKey;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
public class SeparatedKeyContainer {
    SecretKey userkey;
    SecretKey appkey;
    SecretKey proxykey;

    public SeparatedKeyContainer(SecretKey userkey, SecretKey appkey, SecretKey proxykey) {
        this.userkey = userkey;
        this.appkey = appkey;
        this.proxykey = proxykey;
    }    
    
    public SecretKey getUserkey() {
        return userkey;
    }

    public void setUserkey(SecretKey userkey) {
        this.userkey = userkey;
    }

    public SecretKey getAppkey() {
        return appkey;
    }

    public void setAppkey(SecretKey appkey) {
        this.appkey = appkey;
    }

    public SecretKey getProxykey() {
        return proxykey;
    }

    public void setProxykey(SecretKey proxykey) {
        this.proxykey = proxykey;
    }       
    
}//EoM
