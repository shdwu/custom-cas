package org.apereo.cas.ext.login.webflow;

import org.apereo.cas.ext.login.entity.User;
import org.apereo.cas.ext.login.util.ValidateCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by wudongshen on 2017/2/20.
 * 生成校验码Action
 */
public class GenerateVlidateCodeAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateVlidateCodeAction.class);

    final private int vlidateCodeExpire = 300;


    private RedisTemplate<String, Long> redisTemplate;

    private RedisTemplate<String, String> stringRedisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setStringRedisTemplate(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        // 获取session中的用户
        User user = (User)WebUtils.getSessionAttribute((HttpServletRequest)requestContext.getExternalContext().getNativeRequest(), "user");
        if(user == null ){
            LOGGER.error("用户为空");
            return new Event(this, "error");
        }

        // 获取上一次校验码的发送时间，进行间隔时间判断
        ValueOperations<String, Long> vo = redisTemplate.opsForValue();
        Long preTime = vo.get(user.getUsername() + "_prevVlidateCodeGenerateTime");
        Long currentTime = new Date().getTime()/1000;
        if(preTime != null){
            if(currentTime - preTime < vlidateCodeExpire){
                new Event(this, "error");
            }
        }

        String code = ValidateCodeUtil.getShortMessageVlidateCode();

        LOGGER.info("发送验证码：手机号码："+user.getMobilePhoneNumber()+"验证码:"+code);

        // TODO 发送验证码
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(user.getUsername() + "_vlidateCode", code, vlidateCodeExpire, TimeUnit.SECONDS);
        vo.set(user.getUsername() + "_prevVlidateCodeGenerateTime", currentTime, vlidateCodeExpire, TimeUnit.SECONDS);

        return new Event(this, "success");
    }
}
