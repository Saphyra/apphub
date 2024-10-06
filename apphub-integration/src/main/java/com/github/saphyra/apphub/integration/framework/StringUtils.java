package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.util.Random;
import lombok.NonNull;

public class StringUtils {
    public static String randomCase(@NonNull String in) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            String character = ((Character) in.charAt(i)).toString();

            if (Random.RANDOM.randBoolean()) {
                result.append(character.toLowerCase());
            } else {
                result.append(character.toUpperCase());
            }
        }

        return result.toString();
    }
}
