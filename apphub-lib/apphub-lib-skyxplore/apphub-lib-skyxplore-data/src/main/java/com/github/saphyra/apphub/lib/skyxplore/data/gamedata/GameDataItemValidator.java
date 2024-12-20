package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.github.saphyra.apphub.lib.data.DataValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
public class GameDataItemValidator implements DataValidator<GameDataItem> {
    @Override
    public void validate(GameDataItem item) {
        log.info("Validating {}", item.getId());
        requireNonNull(item.getId(), "Id must not be null.");
    }
}
