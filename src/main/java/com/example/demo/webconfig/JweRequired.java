package com.example.demo.webconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотаиця для метода контроллера, указывающая на обязательность использования JWE, если включен флаг шифрования
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JweRequired {

    /**
     * Требуется проверка заголовка Content-Type = application/jose
     */
    boolean jweConsumes() default false;

    /**
     * Требуется проверка заголовка Accept = application/jose
     */
    boolean jweProduces() default false;
}
