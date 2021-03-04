package com.example.demo.jose;

import com.example.demo.service.CryptoService;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.util.Base64URL;

import java.util.Set;

public class CustomJweEncrypter implements JWEEncrypter {

    private final CryptoService cryptoService;

    public CustomJweEncrypter(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) {
        byte[] encryptedData = cryptoService.encrypt(clearText); // тут удаленный JSON-RPC вызов или локальный вызов сервиса
        return new JWECryptoParts(
                header,
                null,
                null,
                Base64URL.encode(encryptedData),
                null);
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
