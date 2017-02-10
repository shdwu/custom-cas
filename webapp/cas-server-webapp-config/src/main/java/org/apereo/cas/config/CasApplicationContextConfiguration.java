package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wudongshen on 2017/2/10.
 */
@Configuration("casApplicationContextConfiguration")
public class CasApplicationContextConfiguration {

    // TODO 感觉没有什么用
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

    // TODO 感觉没有什么用
    @Bean
    protected UrlFilenameViewController passThroughController(){
        return new UrlFilenameViewController();
    }

    @Bean
    protected Controller rootController(){
        return new ParameterizableViewController(){
            @Override
            protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
                final String queryString = request.getQueryString();
                final String url = request.getContextPath() + "/login" + (queryString != null ? '?' + queryString : "");
                return new ModelAndView(new RedirectView(response.encodeURL(url)));
            }
        };
    }
}
