package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceValidator implements DataValidator<Map<String, ResourceData>> {
    private final GameDataItemValidator gameDataItemValidator;

    @Override
    public void validate(Map<String, ResourceData> item) {
        item.forEach(this::validate);
    }

    private void validate(String key, ResourceData resource) {
        gameDataItemValidator.validate(resource);

        ValidationUtil.notNull(resource.getStorageType(), "storageType");
    }
}
