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
package eu.paasword.keymanagement.keydbproxy.repository.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Entity
@Table(name = "userentry", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class Userentry {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "userid")
    private String userid;

    @Basic(optional = false)
    @NotNull
    @Column(name = "proxyid")
    private String proxyid;

    @Basic(optional = false)
    @NotNull
    @Column(name = "proxykey")
    private String proxykey;      

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getProxykey() {
        return proxykey;
    }

    public void setProxykey(String proxykey) {
        this.proxykey = proxykey;
    }    
    
} //EoC
