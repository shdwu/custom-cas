package org.apereo.cas.ext.login.webflow;

import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.ext.login.entity.User;
import org.apereo.cas.ext.login.util.ValidateCodeSmsUtil;
import org.apereo.cas.ext.login.util.ValidateCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.security.auth.login.FailedLoginException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by wudongshen on 2017/2/20.
 * 生成校验码Action
 */
public class GenerateValidateCodeAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateValidateCodeAction.class);

    final private int vlidateCodeExpire = 300;

    private RedisTemplate<String, Long> redisTemplate;

    private RedisTemplate<String, String> stringRedisTemplate;

    private UserLoginDecision userLoginDecision;

    private ValidateCodeSmsUtil validateCodeSmsUtil;

    public void setValidateCodeSmsUtil(ValidateCodeSmsUtil validateCodeSmsUtil) {
        this.validateCodeSmsUtil = validateCodeSmsUtil;
    }

    public void setUserLoginDecision(UserLoginDecision userLoginDecision) {
        this.userLoginDecision = userLoginDecision;
    }

    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setStringRedisTemplate(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        // 获取用户名密码
        UsernamePasswordCredential credential = UsernamePasswordCredential.class.cast(requestContext.getFlowScope().get("credential"));

        // 判断用户是否满足登录条件，并校验用户名密码
        User user = userLoginDecision.doDecision(credential);

        if(user == null ){
            LOGGER.error("用户为空");
            return new Event(this, "error");
        }

        // 获取上一次校验码的发送时间，进行间隔时间判断
        ValueOperations<String, Long> vo = redisTemplate.opsForValue();
        Long preTime = vo.get(user.getUsername() + "_prevValidateCodeGenerateTime");
        Long currentTime = new Date().getTime()/1000;
        if(preTime != null){
            if(currentTime - preTime < vlidateCodeExpire){
                throw new FailedLoginException("验证码有效期为5分钟");
            }
        }

        String code = ValidateCodeUtil.getShortMessageVlidateCode();

        LOGGER.info("发送验证码：手机号码："+user.getMobilePhoneNumber()+"验证码:"+code);

        int statusCode = validateCodeSmsUtil.sendValidateSmsMessage(user.getMobilePhoneNumber(), code);
        if(statusCode == 200){
            LOGGER.info("短信发送成功");
        }else {
            LOGGER.info("短信发送失败");
            throw new FailedLoginException("验证码发送失败");
        }

        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(user.getUsername() + "_validateCode", code, vlidateCodeExpire, TimeUnit.SECONDS);
        vo.set(user.getUsername() + "_prevValidateCodeGenerateTime", currentTime, vlidateCodeExpire, TimeUnit.SECONDS);

        return new Event(this, "success");
    }

}
