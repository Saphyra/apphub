package com.github.saphyra.apphub.service.platform.storage.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class StoredFileProperties {
    @Value("${storedFile.expirationSeconds}")
    private Integer expirationSeconds;
}
