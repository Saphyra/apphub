package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageToModelConverter {
    public List<ReservedStorageModel> convert(UUID gameId, Collection<ReservedStorage> reservedStorages) {
        return reservedStorages.stream()
            .map(reservedStorage -> convert(gameId, reservedStorage))
            .collect(Collectors.toList());
    }

    public ReservedStorageModel convert(UUID gameId, ReservedStorage reservedStorage) {
        ReservedStorageModel model = new ReservedStorageModel();
        model.setId(reservedStorage.getReservedStorageId());
        model.setGameId(gameId);
        model.setType(GameItemType.RESERVED_STORAGE);
        model.setExternalReference(reservedStorage.getExternalReference());
        model.setLocation(reservedStorage.getLocation());
        model.setDataId(reservedStorage.getDataId());
        model.setAmount(reservedStorage.getAmount());
        return model;
    }
}
