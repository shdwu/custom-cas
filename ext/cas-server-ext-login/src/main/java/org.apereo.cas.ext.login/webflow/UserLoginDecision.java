package org.apereo.cas.ext.login.webflow;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.ext.login.entity.User;
import org.apereo.cas.ext.login.handle.QueryUserAuthenticationHandle;
import org.apereo.cas.ext.login.repository.UserRepository;
import org.apereo.cas.ext.login.util.Md5Utils;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.security.auth.login.FailedLoginException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wudongshen on 2017/3/7.
 */
public class UserLoginDecision {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginDecision.class);

    private static final Integer MAX_RETRY_COUNT = 5;

    private UserRepository userRepository;

    private RedisTemplate<String, Integer> redisTemplate;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setRedisTemplate(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public User doDecision(UsernamePasswordCredential credential) throws FailedLoginException {
        String username = credential.getUsername();
        String password = credential.getPassword();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            LOGGER.error("用户名或密码不能为空");
            throw new FailedLoginException("用户名或密码不能为空");
        }

        final String redisKey = username + "_loginError";
        ValueOperations<String, Integer> vo = redisTemplate.opsForValue();
        Integer retryCount = 1;

        // 判断用户是否超出最大重试限制
        if (vo.get(redisKey) != null) {
            retryCount = vo.get(redisKey);
            if (retryCount >= MAX_RETRY_COUNT) {
                LOGGER.error("用户{}已超过最大重试限制,账号被暂时锁定", username);
                throw new FailedLoginException("账号已被锁定");
            }
        }


        List<User> users = userRepository.findByUsername(username);

        if (users == null || users.size() == 0) {
            LOGGER.warn(username + "不存在");
            throw new FailedLoginException("用户名密码错误,错误5次后将锁定30分钟");

        }
        if (users.size() > 1) {
            LOGGER.warn(username + "存在多条记录");
            throw new FailedLoginException("用户存在多条记录");
        }

        User user = users.get(0);

        if (user.getPassword().equals(Md5Utils.hash(user.getUsername() + password + user.getSalt()))) {
            if (vo.get(redisKey) != null) {
                redisTemplate.delete(redisKey);
            }
        } else {
            if (vo.get(redisKey) != null) {
                retryCount = vo.get(redisKey);
                retryCount++;
                vo.set(redisKey, retryCount);
            } else {
                vo.set(redisKey, retryCount);

            }
            redisTemplate.expire(redisKey, 1800, TimeUnit.SECONDS);
            throw new FailedLoginException("用户名密码错误,错误5次后将锁定30分钟");
        }

        return user;

    }
}
