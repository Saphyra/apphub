package com.github.saphyra.apphub.ci.service.integration_server.cleanup;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CleanupProperties {
    private int expirationDays = 90;
}
