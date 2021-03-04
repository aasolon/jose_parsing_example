package com.example.demo.service;

import com.example.demo.rest.model.Document;
import org.springframework.stereotype.Service;

/**
 * Тестовый пример сервиса для работы документом (может быть как локальным, так и удаленным JSON-RPC сервисом)
 */
@Service
public class DocumentServiceImpl implements DocumentService {
    @Override
    public Document create(Document document) {

        // тут какая-нибудь обработка документа

        return document;
    }
}
