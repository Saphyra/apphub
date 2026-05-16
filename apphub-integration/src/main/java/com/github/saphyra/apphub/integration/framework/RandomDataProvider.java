package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.TestBase;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.Constants.CREDENTIAL_PREFIX;

@UtilityClass
public class RandomDataProvider {
    public final IdGenerator ID_GENERATOR = new IdGenerator();

    public String generateEmail() {
        return CREDENTIAL_PREFIX + "mail-" + ID_GENERATOR.generateRandomId() + "@" + TestBase.getTestMethodName() + ".com";
    }

    public String generateUsername() {
        String[] userNameCharacters = (CREDENTIAL_PREFIX + "user-" + ID_GENERATOR.generateRandomId()).split("");
        return Arrays.stream(userNameCharacters)
            .limit(30)
            .collect(Collectors.joining());
    }
}
