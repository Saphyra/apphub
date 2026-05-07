package com.github.saphyra.apphub.ci.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    @SneakyThrows
    public Map<String, String> generateKeyPair() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair keyPair = keyGen.generateKeyPair();

        return Map.of(
            "PUBLIC_KEY", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),
            "PRIVATE_KEY", Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())
        );
    }
}
