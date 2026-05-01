package com.github.saphrya.apphub.service.platform.authorization.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
@Getter
//TODO unit test
public class KeyService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public KeyService(
        @Value("${authorization.privateKey}") String privateKey,
        @Value("${authorization.publicKey}") String publicKey
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        this.privateKey = KeyFactory.getInstance("RSA")
            .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        this.publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }
}
