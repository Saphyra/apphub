package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.data.DataValidator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
//TODO unit test
public class GameDataItemValidator implements DataValidator<GameDataItem> {
    @Override
    public void validate(GameDataItem item) {
        requireNonNull(item.getId(), "Id must not be null.");
    }
}
