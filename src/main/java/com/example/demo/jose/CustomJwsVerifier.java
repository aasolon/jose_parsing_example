package com.example.demo.jose;

import com.example.demo.service.CryptoService;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.util.Base64URL;

import java.util.Set;

public class CustomJwsVerifier implements JWSVerifier {

    private final CryptoService cryptoService;

    public CustomJwsVerifier(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public boolean verify(JWSHeader header, byte[] signingInput, Base64URL signature) {
        return cryptoService.checkSignatureDistributed(header.getKeyID(), signingInput, signature); // тут удаленный JSON-RPC вызов или локальный вызов сервиса
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
