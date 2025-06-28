package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.ConvoyFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy.ConvoyProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy.ConvoyProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceDeliveryProcessHelper {
    private final ConvoyFactory convoyFactory;
    private final CitizenAllocationFactory citizenAllocationFactory;
    private final ConvoyProcessFactory convoyProcessFactory;

    int calculateToDeliver(GameData gameData, UUID resourceDeliveryRequestId) {
        int deliveryNeeded = calculateDeliveryNeeded(gameData, resourceDeliveryRequestId);
        int deliveryInProgress = calculateDeliveryInProgress(gameData, resourceDeliveryRequestId);

        return deliveryNeeded - deliveryInProgress;
    }

    private int calculateDeliveryInProgress(GameData gameData, UUID resourceDeliveryRequestId) {
        return gameData.getConvoys()
            .getByResourceDeliveryRequestId(resourceDeliveryRequestId)
            .stream()
            .mapToInt(Convoy::getCapacity)
            .sum();
    }

    private int calculateDeliveryNeeded(GameData gameData, UUID resourceDeliveryRequestId) {
        UUID reservedStorageId = gameData.getResourceDeliveryRequests()
            .findByIdValidated(resourceDeliveryRequestId)
            .getReservedStorageId();
        return gameData.getReservedStorages()
            .findByIdValidated(reservedStorageId)
            .getAmount();
    }

    Optional<Integer> assembleConvoy(Game game, UUID location, UUID processId, UUID resourceDeliveryRequestId, int toDeliver) {
        return getAvailableCitizen(game.getData(), location)
            .map(citizen -> assembleConvoy(game, location, processId, citizen, resourceDeliveryRequestId, toDeliver));
    }

    private Integer assembleConvoy(Game game, UUID location, UUID processId, Citizen citizen, UUID resourceDeliveryRequestId, int toDeliver) {
        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        Convoy convoy = convoyFactory.save(progressDiff, gameData, location, resourceDeliveryRequestId, toDeliver);

        ConvoyProcess process = convoyProcessFactory.save(game, location, processId, convoy.getConvoyId());

        citizenAllocationFactory.save(progressDiff, gameData, citizen.getCitizenId(), process.getProcessId());

        return convoy.getCapacity();
    }

    private Optional<Citizen> getAvailableCitizen(GameData gameData, UUID location) {
        List<Citizen> citizens = gameData.getCitizens()
            .getByLocation(location);

        List<UUID> allocatedCitizens = gameData.getCitizenAllocations()
            .stream()
            .map(CitizenAllocation::getCitizenId)
            .toList();

        return citizens.stream()
            .filter(citizen -> !allocatedCitizens.contains(citizen.getCitizenId()))
            .findAny();
    }
}
