package org.apereo.cas.ext.login.webflow;

import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.DefaultWebflowConfigurer;
import org.springframework.webflow.engine.Flow;


import javax.security.auth.login.FailedLoginException;

/**
 * Created by wudongshen on 2017/3/7.
 */
public class ValidateCodeDefaultWebflowConfigurer extends DefaultWebflowConfigurer {

    @Override
    protected void createRememberMeAuthnWebflowConfig(final Flow flow)  {
        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, UsernamePasswordValidateCodeCredential.class);
    }

    @Override
    protected void createDefaultGlobalExceptionHandlers(Flow flow) {
        final CustomExceptionHandler h = new CustomExceptionHandler();
        h.add(FailedLoginException.class);
        flow.getExceptionHandlerSet().add(h);
    }
}
