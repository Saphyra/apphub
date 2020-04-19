package com.github.saphyra.apphub.lib.common_util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Encoder {
    public String encode(String in) {
        byte[] bytes = Base64.encodeBase64(in.getBytes());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String decode(String in) {
        byte[] bytes = Base64.decodeBase64(in.getBytes());
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
