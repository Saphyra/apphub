package com.github.saphyra.integration.server.cleanup;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CleanupProperties {
    @Value("${cleanup.expirationDays}")
    private Integer expirationDays;
}
