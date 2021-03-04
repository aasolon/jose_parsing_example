package com.example.demo.webconfig;

public class CallContextHolder {

    private static final ThreadLocal<CallContext> callContext = new ThreadLocal<>();

    public static CallContext getCallContext() {
        return callContext.get();
    }

    public static void setCallContext(CallContext context) {
        if (callContext.get() != null) {
            throw new IllegalStateException("CallContext уже установлен для текущего потока");
        }
        callContext.set(context);
    }

    public static void resetCallContext() {
        callContext.remove();
    }
}
