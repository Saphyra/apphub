package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class ResourceValidator implements DataValidator<Map<String, ResourceData>> {
    private final GameDataItemValidator gameDataItemValidator;

    @Override
    public void validate(Map<String, ResourceData> item) {
        item.forEach(this::validate);
    }

    private void validate(String key, ResourceData resource) {
        try {
            log.debug("Validating Resource with key {}", key);
            gameDataItemValidator.validate(resource);

            requireNonNull(resource.getStorageType(), "StorageType must not be null.");
        } catch (Exception e) {
            throw new IllegalStateException("Invalid data with key " + key, e);
        }
    }
}
