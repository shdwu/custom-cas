package org.apereo.cas.ext.login.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_user")
@Data
public class User{

    @Id
    private Long id;

    private String username;

    private String password;

    private String salt;

    @Column(name = "mobile_phone_number")
    private String mobilePhoneNumber;

    private Integer deleted;
}
