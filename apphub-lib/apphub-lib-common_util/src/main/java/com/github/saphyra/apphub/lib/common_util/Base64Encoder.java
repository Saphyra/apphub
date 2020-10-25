package com.github.saphyra.apphub.lib.common_util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Encoder {
    public String encode(byte[] in) {
        byte[] bytes = Base64.encodeBase64(in);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String encode(String in) {
        byte[] bytes = Base64.encodeBase64(in.getBytes());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String decode(String in) {
        byte[] bytes = decodeBytes(in);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte[] decodeBytes(String in) {
        return Base64.decodeBase64(in.getBytes());
    }
}
