package com.github.saphyra.apphub.service.platform.storage.service.store;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class StoreFileProperties {
    @Value("${storedFile.maxSize}")
    private Long maxFileSize;
}
