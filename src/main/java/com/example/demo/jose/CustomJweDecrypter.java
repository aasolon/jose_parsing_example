package com.example.demo.jose;

import com.example.demo.service.CryptoService;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.util.Base64URL;

import java.util.Set;

public class CustomJweDecrypter implements JWEDecrypter {

    private final CryptoService cryptoService;

    public CustomJweDecrypter(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) {
        return cryptoService.decrypt(cipherText.decode()); // тут удаленный JSON-RPC вызов или локальный вызов сервиса
    }

    @Override
    public Set<JWEAlgorithm> supportedJWEAlgorithms() {
        return ImmutableSet.of(JWEAlgorithm.DIR);
    }

    @Override
    public Set<EncryptionMethod> supportedEncryptionMethods() {
        return ImmutableSet.of(new EncryptionMethod("gost28147-89"));
    }

    @Override
    public JWEJCAContext getJCAContext() {
        return null;
    }
}
