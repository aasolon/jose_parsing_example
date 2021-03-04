package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Тестовый пример сервиса для получения каких-то настроек системы (может быть как локальным, так и удаленным JSON-RPC сервисом)
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Value("${needTechEncryption:false}")
    private boolean needTechEncryption;

    @Override
    public boolean isNeedTechEncryption() {
        return needTechEncryption;
    }
}
