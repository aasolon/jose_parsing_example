package com.example.demo.service;

import com.nimbusds.jose.util.Base64URL;

/**
 * Тестовый пример сервиса для работы с ЭЦП (может быть как локальным, так и удаленным JSON-RPC сервисом)
 */
public interface CryptoService {

    byte[] signData(byte[] signingInput);

    boolean checkSignatureDistributed(String keyID, byte[] signingInput, Base64URL signature);

    byte[] decrypt(byte[] decode);

    byte[] encrypt(byte[] clearText);

    String getTechnicalCertificateGuid();
}
