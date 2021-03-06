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
package eu.paasword.keymanagement.keytenantadmin.repository.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Entity
@Table(name = "authorizedproxy", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class Authorizedproxy implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "proxyid")
    private String proxyid;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10024)
    @Column(name = "pubkeyofproxy")
    private String pubkeyofproxy;

    @Basic(optional = true)
    @Size(min = 1, max = 10024)    
    @Column(name = "secretkey")
    private String secretkey;
    
    @Basic(optional = false)
    @Size(min = 1, max = 250)
    @Column(name = "proxyurl", nullable = true)
    private String proxyurl;    
    
    @Basic(optional = true)
    @Size(min = 1, max = 10024)
    @Column(name = "pubkeyofapp")
    private String pubkeyofapp;    
    
    @Basic(optional = true)
    @Size(min = 1, max = 250)
    @Column(name = "appurl", nullable = true)
    private String appurl;     
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProxyid() {
        return proxyid;
    }

    public void setProxyid(String proxyid) {
        this.proxyid = proxyid;
    }

    public String getPubkeyofproxy() {
        return pubkeyofproxy;
    }

    public void setPubkeyofproxy(String pubkeyofproxy) {
        this.pubkeyofproxy = pubkeyofproxy;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }    

    public String getProxyurl() {
        return proxyurl;
    }

    public void setProxyurl(String proxyurl) {
        this.proxyurl = proxyurl;
    }    

    public String getPubkeyofapp() {
        return pubkeyofapp;
    }

    public void setPubkeyofapp(String pubkeyofapp) {
        this.pubkeyofapp = pubkeyofapp;
    }

    public String getAppurl() {
        return appurl;
    }

    public void setAppurl(String appurl) {
        this.appurl = appurl;
    }    
    
}
