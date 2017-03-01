package org.apereo.cas.ext.login.config;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.ext.login.handle.QueryUserAuthenticationHandle;
import org.apereo.cas.ext.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by wudongshen on 2017/2/16.
 */
@Configuration
public class LoginConfiguration {

    @Autowired
    @Qualifier("authenticationHandlersResolvers")
    private Map authenticationHandlersResolvers;

    @Autowired
    @Qualifier("personDirectoryPrincipalResolver")
    private PrincipalResolver personDirectoryPrincipalResolver;

    @Autowired
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;


    @PostConstruct
    public void initializeJdbcAuthenticationHandlers() {
        authenticationHandlersResolvers.put(
                queryUserAuthenticationHandle(),
                personDirectoryPrincipalResolver);
    }

    private AuthenticationHandler queryUserAuthenticationHandle(){
        final QueryUserAuthenticationHandle quah = new QueryUserAuthenticationHandle();
        quah.setUserRepository(jpaRepositoryFactory.getRepository(UserRepository.class));
        quah.setRedisTemplate(redisTemplate);
        return quah;
    }

}
