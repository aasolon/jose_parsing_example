package com.example.demo.webconfig;

import com.example.demo.exception.BadRequestException;
import com.example.demo.service.ConfigurationService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomHandlerInterceptor implements HandlerInterceptor {

    private final ConfigurationService configurationService;

    public CustomHandlerInterceptor(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                CallContext callContext = new CallContext();
                boolean needTechEncryption = configurationService.isNeedTechEncryption(); // тут удаленный JSON-RPC вызов или локальный вызов сервиса
                if (needTechEncryption && handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(JweAllowed.class)) {
                    if (request.getContentType() == null || !request.getContentType().startsWith("application/jose") ||
                            request.getHeader("Accept") == null || !request.getHeader("Accept").startsWith("application/jose")) {
                        throw new BadRequestException("В соответствии с текущими настройками необходимо отправлять запрос и запрашивать ответ только в формате JWE");
                    }
                    callContext.setOnlyJweAllowed(true);
                }
                CallContextHolder.setCallContext(callContext);
            }
            return true;
        } catch (Exception e) {
            CallContextHolder.resetCallContext();
            throw e;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CallContextHolder.resetCallContext();
    }
}
