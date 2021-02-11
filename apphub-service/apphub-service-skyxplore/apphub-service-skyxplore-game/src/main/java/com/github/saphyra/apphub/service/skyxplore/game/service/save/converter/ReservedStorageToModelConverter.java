package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ReservedStorageToModelConverter {
    public List<ReservedStorageModel> convert(List<ReservedStorage> reservedStorages, Game game) {
        return reservedStorages.stream()
            .map(reservedStorage -> convert(reservedStorage, game))
            .collect(Collectors.toList());
    }

    public ReservedStorageModel convert(ReservedStorage reservedStorage, Game game) {
        ReservedStorageModel model = new ReservedStorageModel();
        model.setId(reservedStorage.getReservedStorageId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.RESERVED_STORAGE);
        model.setLocation(reservedStorage.getLocation());
        model.setLocationType(reservedStorage.getLocationType().name());
        model.setExternalReference(reservedStorage.getExternalReference());
        model.setDataId(reservedStorage.getDataId());
        model.setAmount(reservedStorage.getAmount());
        return model;
    }
}
