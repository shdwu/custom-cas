package org.apereo.cas.ext.login.webflow;

import org.apereo.cas.web.flow.DefaultWebflowConfigurer;
import org.springframework.webflow.engine.Flow;

/**
 * Created by wudongshen on 2017/2/21.
 */
public class VlidateCodeAuthenticationWebflowConfigurer extends DefaultWebflowConfigurer {


    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        final Flow flow = getLoginFlow();
        createFlowVariable(flow, "vlidateCode", VlidateCode.class);
    }


}
