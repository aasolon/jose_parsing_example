package com.example.demo.webconfig;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.OutputStream;

public class HttpOutputMessageWrapper implements HttpOutputMessage {

    private final HttpOutputMessage httpOutputMessage;

    private final OutputStream outputStream;

    public HttpOutputMessageWrapper(HttpOutputMessage httpOutputMessage, OutputStream outputStream) {
        this.httpOutputMessage = httpOutputMessage;
        this.outputStream = outputStream;
    }

    @Override
    public OutputStream getBody() {
        return outputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpOutputMessage.getHeaders();
    }
}
