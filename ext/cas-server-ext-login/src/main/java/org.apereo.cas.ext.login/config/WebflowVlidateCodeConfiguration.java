package org.apereo.cas.ext.login.config;

import org.apereo.cas.authentication.RememberMeUsernamePasswordCredential;
import org.apereo.cas.ext.login.webflow.GenerateVlidateCodeAction;
import org.apereo.cas.ext.login.webflow.VlidateCode;
import org.apereo.cas.ext.login.webflow.VlidateCodeAuthenticationAction;
import org.apereo.cas.ext.login.webflow.VlidateCodeAuthenticationWebflowConfigurer;
import org.apereo.cas.web.flow.AbstractCasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.DefaultWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import javax.annotation.PostConstruct;

/**
 * Created by wudongshen on 2017/2/20.
 */
@Configuration
public class WebflowVlidateCodeConfiguration {

    @Autowired
    FlowDefinitionRegistry loginFlowRegistry;

    @Autowired
    FlowDefinitionRegistry logoutFlowRegistry;

    @Autowired
    FlowBuilderServices builder;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Bean
    public Action generateVlidateCodeAction(){
        final GenerateVlidateCodeAction action = new GenerateVlidateCodeAction();
        action.setRedisTemplate(redisTemplate);
        action.setStringRedisTemplate(stringRedisTemplate);
        return action;
    }

    @Bean
    public Action vlidateCodeAuthenticationAction(){
        final VlidateCodeAuthenticationAction action = new VlidateCodeAuthenticationAction();
        action.setStringRedisTemplate(stringRedisTemplate);
        return action;
    }


    @Bean
    public CasWebflowConfigurer defaultWebflowConfigurer() {
        final VlidateCodeAuthenticationWebflowConfigurer c = new VlidateCodeAuthenticationWebflowConfigurer();
        c.setLoginFlowDefinitionRegistry(loginFlowRegistry);
        c.setLogoutFlowDefinitionRegistry(logoutFlowRegistry);
        c.setFlowBuilderServices(builder);
        return c;
    }
}
