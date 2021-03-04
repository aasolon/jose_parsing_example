package com.example.demo.webconfig;

import com.example.demo.jose.CustomJweDecrypter;
import com.example.demo.jose.CustomJweEncrypter;
import com.example.demo.jose.CustomJwsSigner;
import com.example.demo.jose.CustomJwsVerifier;
import com.example.demo.service.ConfigurationService;
import com.example.demo.service.CryptoService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        JoseHttpMessageConverter joseHttpMessageConverter = new JoseHttpMessageConverter(
                new CustomJwsVerifier(cryptoService),
                new CustomJwsSigner(cryptoService),
                new CustomJweDecrypter(cryptoService),
                new CustomJweEncrypter(cryptoService),
                cryptoService);
        joseHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(new MediaType("application", "jose")));
        joseHttpMessageConverter.setObjectMapper(customObjectMapper());
        converters.add(joseHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomHandlerInterceptor(configurationService));
    }

    @Bean
    @Primary
    public ObjectMapper customObjectMapper() {
        ObjectMapper customObjectMapper = new ObjectMapper();

        // тут конфигурируем objectMapper для DTO-моделек как нам нужно

        customObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return customObjectMapper;
    }
}
