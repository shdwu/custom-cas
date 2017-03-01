package org.apereo.cas.ext.login.handle;

import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.ext.login.entity.User;
import org.apereo.cas.ext.login.repository.UserRepository;
import org.apereo.cas.ext.login.util.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.apereo.cas.web.support.WebUtils;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wudongshen on 2017/2/16.
 */
public class QueryUserAuthenticationHandle extends AbstractUsernamePasswordAuthenticationHandler {

    Logger logger = LoggerFactory.getLogger(QueryUserAuthenticationHandle.class);

    private UserRepository userRepository;

    private RedisTemplate<String, Integer> redisTemplate;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setRedisTemplate(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential transformedCredential) throws GeneralSecurityException, PreventedException {

        final String username = transformedCredential.getUsername();
        final String password = transformedCredential.getPassword();

        // 判断用户是否被锁定
        final Integer maxRetryCount = 5;
        final String redisKey = username + "_loginError";
        HashOperations<String, String, Integer> ho = redisTemplate.opsForHash();
        int retryCount = 1;

        if (ho.hasKey(redisKey, QueryUserAuthenticationHandle.class.getName())){
            retryCount = ho.get(redisKey, QueryUserAuthenticationHandle.class.getName());
            if (retryCount >= maxRetryCount) {
                logger.error("账号：{} 登录验证超过5次，锁定30分钟", username);
                throw new FailedLoginException("账号已被锁定");
            }
        }

        Assert.notNull(userRepository, "数据库访问异常");

        List<User> users = userRepository.findByUsername(username);

        if ( users == null ) {
            logger.warn( username + "不存在" );
            throw new FailedLoginException("用户名密码错误");

        }
        if ( users.size() > 1) {
            logger.warn( username + "存在多条记录");
            throw new FailedLoginException("用户存在多条记录");
        }

        User user = users.get(0);

        if( user.getPassword().equals(
                Md5Utils.hash(user.getUsername() + password + user.getSalt()))
        ){
            if(ho.hasKey(redisKey, QueryUserAuthenticationHandle.class.getName())){
                redisTemplate.delete(redisKey);
            }
            WebUtils.getHttpServletRequest().getSession().setAttribute("user", user);
            return createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username), null);
        }else {
            if (ho.hasKey(redisKey, QueryUserAuthenticationHandle.class.getName())) {
                retryCount = ho.get(redisKey, QueryUserAuthenticationHandle.class.getName());
                retryCount++;
                ho.put(redisKey, QueryUserAuthenticationHandle.class.getName(), retryCount);
            } else {
                ho.put(redisKey, QueryUserAuthenticationHandle.class.getName(), retryCount);
                redisTemplate.expire(redisKey, 1800, TimeUnit.SECONDS);
            }

            throw new FailedLoginException("用户名密码错误");
        }

    }
}
