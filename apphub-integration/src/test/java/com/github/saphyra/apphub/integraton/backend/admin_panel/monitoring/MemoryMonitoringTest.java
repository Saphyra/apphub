package com.github.saphyra.apphub.integraton.backend.admin_panel.monitoring;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.admin_panel.MemoryStatusModel;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryMonitoringTest extends BackEndTest {
    @Test
    public void memoryMonitoring() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        ApphubWsClient wsClient = ApphubWsClient.createAdminPanelMonitoring(language, accessTokenId);

        wsClient.awaitForEvent(WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS);

        SleepUtil.sleep(5000);

        Set<String> services = wsClient.getMessages()
            .stream()
            .filter(webSocketEvent -> webSocketEvent.getEventName().equals(WebSocketEventName.ADMIN_PANEL_MONITORING_MEMORY_STATUS))
            .map(webSocketEvent -> webSocketEvent.getPayloadAs(MemoryStatusModel.class).getService())
            .collect(Collectors.toSet());

        assertThat(services).containsExactlyInAnyOrder(
            "event-gateway",
            "admin-panel",
            "localization",
            "main-gateway",
            "message-sender",
            "modules",
            "notebook",
            "scheduler",
            "skyxplore-data",
            "skyxplore-game",
            "skyxplore-lobby",
            "training",
            "user",
            "utils",
            "web-content",
            "community",
            "diary",
            "encryption"
        );
    }
}
