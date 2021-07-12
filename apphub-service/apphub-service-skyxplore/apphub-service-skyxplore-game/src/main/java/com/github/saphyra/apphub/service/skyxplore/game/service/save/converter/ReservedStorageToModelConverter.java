package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageToModelConverter {
    public List<ReservedStorageModel> convert(List<ReservedStorage> reservedStorages, Game game, UUID location, LocationType locationType) {
        return reservedStorages.stream()
            .map(reservedStorage -> convert(reservedStorage, game, location, locationType))
            .collect(Collectors.toList());
    }

    private ReservedStorageModel convert(ReservedStorage reservedStorage, Game game, UUID location, LocationType locationType) {
        ReservedStorageModel model = new ReservedStorageModel();
        model.setId(reservedStorage.getReservedStorageId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.RESERVED_STORAGE);
        model.setExternalReference(reservedStorage.getExternalReference());
        model.setLocation(location);
        model.setLocationType(locationType.name());
        model.setDataId(reservedStorage.getDataId());
        model.setAmount(reservedStorage.getAmount());
        return model;
    }
}
