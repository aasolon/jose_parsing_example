package com.example.demo.service;

import com.example.demo.rest.model.Document;

/**
 * Тестовый пример сервиса для работы документом (может быть как локальным, так и удаленным JSON-RPC сервисом)
 */
public interface DocumentService {

    Document create(Document document);
}
