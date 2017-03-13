package org.apereo.cas.ext.login.webflow;

import org.apache.http.HttpResponse;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.execution.FlowExecutionException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wudongshen on 2017/3/8.
 */
public class CustomExceptionHandler implements FlowExecutionExceptionHandler {

    private List<Class<? extends Throwable>> exceptionTarget = new ArrayList();

    public CustomExceptionHandler add(Class<? extends Throwable> exceptionClass){
        exceptionTarget.add(exceptionClass);
        return this;
    }

    @Override
    public boolean canHandle(FlowExecutionException e) {
        if(e.getCause() != null){
            return exceptionTarget.contains(e.getCause().getClass());
        }
        return false;
    }

    @Override
    public void handle(FlowExecutionException e, RequestControlContext requestControlContext) {
        requestControlContext.getFlowScope().put("errorMessage", e.getCause().getMessage());
        HttpServletResponse response = (HttpServletResponse)requestControlContext.getExternalContext().getNativeResponse();
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("{errorMessage:'" + e.getCause().getMessage() + "'}");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
