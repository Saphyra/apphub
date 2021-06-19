package com.github.saphyra.apphub.service.skyxplore.facade.config;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "character-existence")
@Data
@Configuration
public class CharacterExistenceWhitelistConfiguration {
    private List<WhiteListedEndpoint> whiteListedEndpoints = new ArrayList<>();
}
