package com.github.saphyra.apphub.lib.encryption.base;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class DefaultEncryptor {
    private static final int SIZE = 16;
    private static final String ALGORITHM = "AES";

    private final Key key;
    private final Cipher cipher;
    private final Base64Encoder encoder;

    public DefaultEncryptor(Base64Encoder encoder, String password) {
        byte[] key = createKey(password);
        this.key = new SecretKeySpec(key, ALGORITHM);
        this.encoder = encoder;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("Error creating encryptor.", e);
            throw new RuntimeException(e);
        }
    }

    private byte[] createKey(String password) {
        if (password.length() < SIZE) {
            int missingLength = SIZE - password.length();
            StringBuilder passwordBuilder = new StringBuilder(password);
            for (int i = 0; i < missingLength; i++) {
                passwordBuilder.append(" ");
            }
            password = passwordBuilder.toString();
        }
        return password.substring(0, SIZE).getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String text) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return encoder.encode(encrypted);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("Error encryping value.", e);
            throw new RuntimeException(e);
        }

    }

    public String decrypt(String text) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] base64 = encoder.decodeBytes(text);
            byte[] decrypted = cipher.doFinal(base64);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("Error decrypting value.", e);
            throw new RuntimeException(e);
        }
    }
}
