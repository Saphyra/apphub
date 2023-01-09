package com.github.saphyra.apphub.service.platform.storage.event;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
class CleanupProperties {
    @Value("${storedFile.expirationSeconds}")
    private Integer expirationSeconds;
}
