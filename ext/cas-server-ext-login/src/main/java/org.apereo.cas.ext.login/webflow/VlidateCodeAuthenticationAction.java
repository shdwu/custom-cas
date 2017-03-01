package org.apereo.cas.ext.login.webflow;

import org.apereo.cas.ext.login.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wudongshen on 2017/2/21.
 */
public class VlidateCodeAuthenticationAction extends AbstractAction {

    private final static String PARAMETER_VLIDATECODE = "vlidateCode";

    private RedisTemplate<String, String> stringRedisTemplate;

    public void setStringRedisTemplate(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        User user = (User) WebUtils.getSessionAttribute((HttpServletRequest)requestContext.getExternalContext().getNativeRequest(), "user");
//        final String codeFromFlow = (String) requestContext.getRequestScope().get(PARAMETER_VLIDATECODE);
        final VlidateCode vlidateCode = (VlidateCode) requestContext.getFlowScope().get(PARAMETER_VLIDATECODE);
        final String vlidateCodeString = vlidateCode.getVlidateCode();

        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String code = stringValueOperations.get(user.getUsername() + "_vlidateCode");
        if( code == null) {
            return new Event(this, "error");
        }

        if( code.equalsIgnoreCase(vlidateCodeString)){
            return new Event(this, "success");
        }
        return new Event(this, "error");
    }
}
