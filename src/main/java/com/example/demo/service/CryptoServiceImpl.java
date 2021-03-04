package com.example.demo.service;

import com.nimbusds.jose.util.Base64URL;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Тестовый пример сервиса для работы с ЭЦП (может быть как локальным, так и удаленным JSON-RPC сервисом)
 */
@Service
public class CryptoServiceImpl implements CryptoService {

    @Override
    public byte[] signData(byte[] signingInput) {
        return new byte[]{0,1,2}; // подписали (нет)
    }

    @Override
    public boolean checkSignatureDistributed(String keyID, byte[] signingInput, Base64URL signature) {
        return true; // проверили подпись (нет)
    }

    @Override
    public byte[] decrypt(byte[] decode) {
        String result = "ewogICJ0eXAiOiAiSk9TRSIsCiAgImFsZyI6ICJnb3N0MzQuMTAtMjAxMiIsCiAgImtpZCI6ICIxM2JjMDk3YS1hMjBiLTRlNjItYWJhMy1mZTczMjUzZjE3ZTgiCn0.\n" +
                "ewogICJleHRlcm5hbElkIjogImI5ZWUzYjQyLThhY2QtNGU4OC1iZjQ4LThiMjNiMDE3ZjNlZSIsCiAgImRvY051bWJlciI6ICJTQkItOTg3IiwKICAiYW1vdW50IjogOTEzLAogICJ1bmtub3duUHJvcGVydHkiOiAiaXRfY2hlY2tzX2lmX29iamVjdF9tYXBwZXJfZmFpbF9vbl9kZXNlcmlhbGl6ZSIKfQ.\n" +
                "YXNk";
        return result.getBytes(StandardCharsets.UTF_8); // расшифровали (нет)
    }

    @Override
    public byte[] encrypt(byte[] clearText) {
        return new byte[]{0,1,2}; // зашифровали (нет)
    }

    @Override
    public String getTechnicalCertificateGuid() {
        return "13bc097a-a20b-4e62-aba3-fe73253f17e8";
    }
}
