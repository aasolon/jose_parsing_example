package com.example.demo.rest;

import com.example.demo.rest.model.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Контроллер для документа, по которому разрешены запросы со стандартным списком разрешенных форматов, т.е.
 * может принимать либо обычные JSON запросы, либо запросы с транспортной подписью JWS (а например JWE запрещен),
 * и возвращает всегда только JSON
 */
@RestController
@RequestMapping(value = "/simple-document")
public class SimpleDocumentController {

    @PostMapping(value = "/create", consumes = "application/json", produces = "!application/jose")
    public Document createDocumentFromJsonAndReturnJson(@Valid @RequestBody Document document) {
        return document;
    }

    @PostMapping(value = "/create", consumes = "application/jose", produces = "!application/jose")
    public Document createDocumentFromJoseAndReturnJson(@Valid @RequestBody Document document) {
        return document;
    }
}
