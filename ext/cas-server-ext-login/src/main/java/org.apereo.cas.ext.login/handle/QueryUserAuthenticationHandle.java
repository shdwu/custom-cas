package org.apereo.cas.ext.login.handle;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.ext.login.entity.User;
import org.apereo.cas.ext.login.webflow.UserLoginDecision;
import org.apereo.cas.ext.login.webflow.UsernamePasswordValidateCodeCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * Created by wudongshen on 2017/2/16.
 */
public class QueryUserAuthenticationHandle extends AbstractUsernamePasswordAuthenticationHandler {

    Logger logger = LoggerFactory.getLogger(QueryUserAuthenticationHandle.class);

    private UserLoginDecision userLoginDecision;

    private RedisTemplate<String, String> stringRedisTemplate;

    private RedisTemplate<String, Long> redisTemplate;

    public void setUserLoginDecision(UserLoginDecision userLoginDecision) {
        this.userLoginDecision = userLoginDecision;
    }

    public void setStringRedisTemplate(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential transformedCredential) throws GeneralSecurityException, PreventedException {

        UsernamePasswordValidateCodeCredential codeCredential =
                UsernamePasswordValidateCodeCredential.class.cast(transformedCredential);
        final String username = codeCredential.getUsername();
        final String validateCode = codeCredential.getVlidateCode();

        // 判断用户是否满足登录条件，并判断用户名密码
        // 这里再次判断是为了防止用户在发送校验码后又更改了用户名密码的输入框
        User user = userLoginDecision.doDecision(codeCredential);

        // 判断校验码
        ValueOperations<String, String> vo = stringRedisTemplate.opsForValue();
        String code = vo.get(username + "_validateCode");

        if( code == null || !code.equalsIgnoreCase(validateCode)){
            throw new FailedLoginException("验证码错误");
        } else {
            // 校验成功后删除验证码
            stringRedisTemplate.delete(username + "_validateCode");
            redisTemplate.delete(username + "_prevValidateCodeGenerateTime");
        }

        return createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username), null);
    }

    @Override
    protected HandlerResult doAuthentication(final Credential credential) throws GeneralSecurityException, PreventedException {

        final UsernamePasswordValidateCodeCredential originalUserPass = (UsernamePasswordValidateCodeCredential) credential;
        final UsernamePasswordValidateCodeCredential userPass = new UsernamePasswordValidateCodeCredential(originalUserPass.getUsername(), originalUserPass.getPassword());

        if (StringUtils.isBlank(userPass.getUsername())) {
            throw new AccountNotFoundException("Username is null.");
        }

        final String transformedUsername = this.principalNameTransformer.transform(userPass.getUsername());
        if (StringUtils.isBlank(transformedUsername)) {
            throw new AccountNotFoundException("Transformed username is null.");
        }

        if (StringUtils.isBlank(userPass.getPassword())) {
            throw new FailedLoginException("Password is null.");
        }

        final String transformedPsw = this.passwordEncoder.encode(userPass.getPassword());
        if (StringUtils.isBlank(transformedPsw)) {
            throw new AccountNotFoundException("Encoded password is null.");
        }

        final String validateCode = originalUserPass.getVlidateCode();
        if (StringUtils.isBlank(validateCode)) {
            throw new AccountNotFoundException("ValidateCode is null.");
        }

        userPass.setUsername(transformedUsername);
        userPass.setPassword(transformedPsw);
        userPass.setVlidateCode(validateCode);

        return authenticateUsernamePasswordInternal(userPass, originalUserPass.getPassword());
    }

    @Override
    public boolean supports(final Credential credential) {
        if (credential instanceof UsernamePasswordValidateCodeCredential) {
            return true;
        }
        return false;
    }

}
