package com.github.saphyra.apphub.lib.common_util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.util.Arrays;

public class Base64Encoder {
    public String encode(String in) {
        return Arrays.toString(Base64.encodeBase64(in.getBytes()));
    }

    public String decode(String in) {
        return Arrays.toString(Base64.decodeBase64(in.getBytes()));
    }
}
