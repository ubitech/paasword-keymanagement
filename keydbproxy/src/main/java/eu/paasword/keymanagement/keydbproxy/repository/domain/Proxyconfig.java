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
@Table(name = "proxyconfig", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class Proxyconfig implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10024)
    @Column(name = "pubkey")
    private String pubkey;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10024)
    @Column(name = "privkey")
    private String privkey;

    @Column(name = "aesconfigured", columnDefinition = "tinyint(1) default 0")
    private Integer aesconfigured;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getPrivkey() {
        return privkey;
    }

    public void setPrivkey(String privkey) {
        this.privkey = privkey;
    }

    public Integer getAesconfigured() {
        return aesconfigured;
    }

    public void setAesconfigured(Integer aesconfigured) {
        this.aesconfigured = aesconfigured;
    }    

}
