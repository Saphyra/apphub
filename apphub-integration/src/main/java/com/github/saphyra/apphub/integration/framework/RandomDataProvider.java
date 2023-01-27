package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.TestBase;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class RandomDataProvider {
    public final IdGenerator ID_GENERATOR = new IdGenerator();

    public String generateEmail() {
        return "email-" + ID_GENERATOR.generateRandomId() + "@" + TestBase.getEmailDomain() + ".com";
    }

    public String generateUsername() {
        String[] userNameCharacters = ("user-" + ID_GENERATOR.generateRandomId()).split("");
        return Arrays.stream(userNameCharacters)
            .limit(30)
            .collect(Collectors.joining());
    }
}
