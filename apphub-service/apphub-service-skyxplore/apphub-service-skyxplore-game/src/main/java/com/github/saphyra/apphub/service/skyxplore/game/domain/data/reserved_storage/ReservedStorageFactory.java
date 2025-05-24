package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageFactory {
    private final IdGenerator idGenerator;
    private final ReservedStorageConverter reservedStorageConverter;

    public ReservedStorage save(GameProgressDiff progressDiff, GameData gameData, UUID containerId, ContainerType containerType, UUID externalReference, String dataId, Integer amount) {
        ReservedStorage reservedStorage = create(containerId, containerType, externalReference, dataId, amount);

        gameData.getReservedStorages()
            .add(reservedStorage);
        progressDiff.save(reservedStorageConverter.toModel(gameData.getGameId(), reservedStorage));

        return reservedStorage;
    }

    public ReservedStorage create(UUID containerId, ContainerType containerType, UUID externalReference, String dataId, int amount) {
        ReservedStorage result = ReservedStorage.builder()
            .reservedStorageId(idGenerator.randomUuid())
            .containerId(containerId)
            .containerType(containerType)
            .externalReference(externalReference)
            .dataId(dataId)
            .amount(amount)
            .build();
        log.debug("{} is created.", result);
        return result;
    }
}
