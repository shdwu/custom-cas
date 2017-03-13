package org.apereo.cas.ext.login.webflow;

import org.apereo.cas.authentication.UsernamePasswordCredential;

/**
 * Created by wudongshen on 2017/3/7.
 */
public class UsernamePasswordValidateCodeCredential extends UsernamePasswordCredential {

    public UsernamePasswordValidateCodeCredential(){}

    public UsernamePasswordValidateCodeCredential(final String userName, final String password){
        super(userName, password);
    }

    private String vlidateCode;

    public String getVlidateCode() {
        return vlidateCode;
    }

    public void setVlidateCode(String vlidateCode) {
        this.vlidateCode = vlidateCode;
    }
}
