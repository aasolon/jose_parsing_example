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
                JweRequired jweRequiredAnnotation = handlerMethod.getMethod().getAnnotation(JweRequired.class);
                if (needTechEncryption && jweRequiredAnnotation != null) {
                    if (!isJoseContentTypeHeader(request, jweRequiredAnnotation) || !isJoseAcceptHeader(request, jweRequiredAnnotation)) {
                        throw new BadRequestException("Заголовок Content-Type и/или заголовок Accept должен быть равен application/jose");
                    }
                    callContext.setJweRequired(true);
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

    private boolean isJoseContentTypeHeader(HttpServletRequest request, JweRequired jweRequiredAnnotation) {
        return !jweRequiredAnnotation.jweConsumes() || request.getContentType() != null && request.getContentType().startsWith("application/jose");
    }

    private boolean isJoseAcceptHeader(HttpServletRequest request, JweRequired jweRequiredAnnotation) {
        return !jweRequiredAnnotation.jweProduces() || request.getHeader("Accept") != null && request.getHeader("Accept").startsWith("application/jose");
    }
}
