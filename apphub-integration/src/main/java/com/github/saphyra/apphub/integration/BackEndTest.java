package com.github.saphyra.apphub.integration;

import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BackEndTest extends TestBase {
    private static final String LANGUAGE;

    private static final String BOTH = "both";

    static {
        LANGUAGE = System.getProperty("languages", BOTH);
    }

    @DataProvider(name = "languageDataProvider", parallel = true)
    public Object[] languageDataProvider() {
        if (LANGUAGE.equals(BOTH)) {
            return Language.values();
        }

        return new Object[]{Language.valueOf(LANGUAGE)};
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(Method method) {
        boolean wsConnectionsCleaned = ApphubWsClient.getClients().isEmpty();
        if (!wsConnectionsCleaned) {
            log.debug("WsConnections not cleaned for method {}", method.getName(), new RuntimeException());
            ApphubWsClient.cleanUpConnections();
        }
        assertThat(ApphubWsClient.getClients().isEmpty()).isTrue();
    }
}
