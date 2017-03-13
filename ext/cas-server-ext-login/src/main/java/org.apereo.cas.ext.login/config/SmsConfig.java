package org.apereo.cas.ext.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by wudongshen on 2017/3/10.
 */
@ConfigurationProperties("sms")
public class SmsConfig {

    private String url;

    private String templateId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
