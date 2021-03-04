package com.example.demo.jose;

import com.example.demo.service.CryptoService;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.util.Base64URL;

import java.util.Set;

public class CustomJwsSigner implements JWSSigner {

    private final CryptoService cryptoService;

    public CustomJwsSigner(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) {
        byte[] signedData = cryptoService.signData(signingInput); // тут удаленный JSON-RPC вызов или локальный вызов сервиса
        return Base64URL.encode(signedData);
    }

    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {
        return ImmutableSet.of(new JWSAlgorithm("gost34.10-2012"));
    }

    @Override
    public JCAContext getJCAContext() {
        return null;
    }
}
