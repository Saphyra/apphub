package com.github.saphyra.apphub.service.platform.storage.config;

import com.github.saphyra.apphub.service.platform.storage.dao.Storage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(value = "storage")
@Data
@Configuration
public class StorageProperties {
    private Storage type;
}
