package com.imooc.controller.interceptor;

import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MiniInteceptor implements HandlerInterceptor {


    public static final String USER_REDIS_SESSION = "user-redis-session";

    @Autowired
    public RedisOperator redis;

    /**
     * 预处理   调用前
     * @param request
     * @param response
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redis.get(USER_REDIS_SESSION.concat(":").concat(userId));
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请登录..."));
                return false;
            }
            if (!uniqueToken.equals(userToken)){
                returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("账号被挤出..."));
                return false;
            }
        }else {
            returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请登录..."));
            return false;
        }


        /**
         * 返回false :请求被拦截，返回
         *         true: 请求ok，可以执行
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

    /**
     *  请求Controller之后，渲染视图之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求完成后     视图渲染之后
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
