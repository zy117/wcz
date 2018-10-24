package com.myway.spider.mvc;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.myway.khs.base.api.account.bean.vo.AccountVO;
import com.myway.usf.common.context.ActionContext;
import com.myway.usf.common.context.ContextInfo;
import com.myway.usf.mvc.interceptor.AbstractContextInfoHandler;
import com.myway.util.common.JSONUtil;

/**
 * @author shiningvon
 * @date 2017/12/18
 */
public class AccountInfoContextHandler extends AbstractContextInfoHandler {

    @Override
    protected boolean process(ActionContext context, HttpServletRequest request,
            HttpServletResponse response, Object handler) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return super.process(context, request, response, handler);
    }

    @Override
    protected ContextInfo prepareContextInfo(ActionContext context, HttpServletRequest request,
            HttpServletResponse response, Object handler) {
        return null;
    }

    @Override
    protected ContextInfo parseContextInfo(String contextContent, Map<String, String> params) {
        return JSONUtil.toObject(contextContent, AccountVO.class);
    }

    @Override
    protected boolean validateContextInfo(ActionContext context, boolean validationResult,
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!validationResult) {
            response.setHeader("sessionstatus", "invalid");

            // AJAX请求标识
            String requestType = request.getHeader("X-Requested-With");

            // 非AJAX请求
            if (null == requestType) {
                sendError(response, 911, "session invalid,not an ajax request.");
            }

            // AJAX请求
            else if ("XMLHttpRequest".equalsIgnoreCase(requestType)) {
                sendError(response, 518, "session invalid,an ajax request.");
            }

            return false;
        } else if (null == context.getInfo()) {
            response.setHeader("sessionstatus", "timeout");

            // AJAX请求标识
            String requestType = request.getHeader("X-Requested-With");

            // 非AJAX请求
            if (null == requestType) {
                sendError(response, 911, "session timeout,not an ajax request.");
            }

            // AJAX请求
            else if ("XMLHttpRequest".equalsIgnoreCase(requestType)) {
                sendError(response, 518, "session timeout,an ajax request.");
            }

            return false;
        }

        return true;
    }

    private void sendError(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.print(msg);
        outputStream.flush();
        outputStream.close();
    }

}
