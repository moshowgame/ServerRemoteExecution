package com.softdev.system.config;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 白名单路径直接放行
        String uri = request.getRequestURI();

        if(uri.startsWith("/login") || uri.startsWith("/entitlement")){
            return true;
        }

        // 检查Session中的登录状态
        HttpSession session = request.getSession(false); // 不创建新session
        if(session != null && session.getAttribute("entitledUser") != null){
            return true;
        }

//        // 检查Cookie中的userName
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                if("userName".equals(cookie.getName()) &&
//                   !StringUtils.isEmpty(cookie.getValue())){
//                    return true; // 已登录
//                }
//            }
//        }

        // 未登录跳转到登录页
        response.sendRedirect(request.getContextPath()+"/login");
        return false;
//        return true;
    }
}
