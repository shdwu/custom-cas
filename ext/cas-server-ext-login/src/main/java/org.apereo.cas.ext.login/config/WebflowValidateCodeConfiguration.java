package org.apereo.cas.ext.login.config;

import org.apereo.cas.ext.login.repository.UserRepository;
import org.apereo.cas.ext.login.util.ValidateCodeSmsUtil;
import org.apereo.cas.ext.login.util.ValidateCodeUtil;
import org.apereo.cas.ext.login.webflow.GenerateValidateCodeAction;
import org.apereo.cas.ext.login.webflow.UserLoginDecision;
import org.apereo.cas.ext.login.webflow.ValidateCodeDefaultWebflowConfigurer;
import org.apereo.cas.web.flow.AuthenticationExceptionHandler;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.DefaultWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;


/**
 * Created by wudongshen on 2017/2/20.
 */
@Configuration
@EnableConfigurationProperties(SmsConfig.class)
public class WebflowValidateCodeConfiguration {

    @Autowired
    FlowDefinitionRegistry loginFlowRegistry;

    @Autowired
    FlowDefinitionRegistry logoutFlowRegistry;

    @Autowired
    FlowBuilderServices builder;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private RedisTemplate<String, Long> longRedisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Autowired
    private SmsConfig smsConfig;

    @Bean
    public UserLoginDecision userLoginDecision(){
        UserLoginDecision userLoginDecision = new UserLoginDecision();
        userLoginDecision.setUserRepository(jpaRepositoryFactory.getRepository(UserRepository.class));
        userLoginDecision.setRedisTemplate(redisTemplate);
        return userLoginDecision;
    }

    @Bean
    public Action generateValidateCodeAction(){
        final GenerateValidateCodeAction action = new GenerateValidateCodeAction();
        action.setUserLoginDecision(userLoginDecision());
        action.setRedisTemplate(longRedisTemplate);
        action.setStringRedisTemplate(stringRedisTemplate);
        action.setValidateCodeSmsUtil(validateCodeSmsUtil());
        return action;
    }

    @Bean
    public CasWebflowConfigurer defaultWebflowConfigurer() {
        final DefaultWebflowConfigurer c = new ValidateCodeDefaultWebflowConfigurer();
        c.setLoginFlowDefinitionRegistry(loginFlowRegistry);
        c.setLogoutFlowDefinitionRegistry(logoutFlowRegistry);
        c.setFlowBuilderServices(builder);
        return c;
    }

    @Bean
    public ValidateCodeSmsUtil validateCodeSmsUtil(){
        ValidateCodeSmsUtil validateCodeSmsUtil = new ValidateCodeSmsUtil();
        validateCodeSmsUtil.setSmsUrl(smsConfig.getUrl());
        validateCodeSmsUtil.setTemplateId(smsConfig.getTemplateId());
        return validateCodeSmsUtil;
    }

}
