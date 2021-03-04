package com.example.demo.webconfig;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

public class HttpInputMessageWrapper implements HttpInputMessage {

    private final HttpInputMessage httpInputMessage;

    private final InputStream inputStream;

    public HttpInputMessageWrapper(HttpInputMessage httpInputMessage, InputStream inputStream) {
        this.httpInputMessage = httpInputMessage;
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getBody() {
        return inputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpInputMessage.getHeaders();
    }
}
