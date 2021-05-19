package com.github.saphyra.apphub.lib.common_util;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encoder {
    public String encode(byte[] in) {
        return Base64.getEncoder().encodeToString(in);
    }

    public String encode(String in) {
        return encode(in.getBytes());
    }

    public String decode(String in) {
        byte[] bytes = decodeBytes(in);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte[] decodeBytes(String in) {
        return Base64.getDecoder().decode(in);
    }
}
