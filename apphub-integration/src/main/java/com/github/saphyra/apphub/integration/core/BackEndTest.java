package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BackEndTest extends TestBase {
    @AfterMethod(alwaysRun = true)
    public void cleanup(Method method) {
        boolean wsConnectionsCleaned = ApphubWsClient.getClients().isEmpty();
        if (!wsConnectionsCleaned) {
            log.debug("WsConnections not cleaned for method {}", method.getName(), new RuntimeException());
            ApphubWsClient.cleanUpConnections();
        }
        assertThat(ApphubWsClient.getClients().isEmpty()).isTrue();
    }

    @Override
    protected String getTestType() {
        return "be";
    }
}
