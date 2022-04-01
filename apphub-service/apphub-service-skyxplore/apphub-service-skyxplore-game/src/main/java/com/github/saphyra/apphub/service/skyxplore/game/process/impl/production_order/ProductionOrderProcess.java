package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO unit test
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class ProductionOrderProcess implements Process {
    @Getter
    private final UUID processId;

    @Getter
    private volatile ProcessStatus status = ProcessStatus.CREATED;

    private volatile String producerBuildingDataId;

    @Getter
    private final UUID externalReference;
    private final Game game;
    private final Planet planet;
    private final AllocatedResource allocatedResource;
    private final ReservedStorage reservedStorage;
    private final int amount;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public int getPriority() {
        return game.getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    @Override
    public ProcessType getType() {
        return ProcessType.PRODUCTION_ORDER;
    }

    @Override
    public void work(SyncCache syncCache) {
        if (status == ProcessStatus.CREATED) {
            Optional<String> maybeProductionBuilding = applicationContextProxy.getBean(ProducerBuildingFinderService.class)
                .findProducerBuildingDataId(planet, reservedStorage.getDataId());

            if (maybeProductionBuilding.isPresent()) {
                producerBuildingDataId = maybeProductionBuilding.get();

                createResourceRequirementProcesses(syncCache);

                status = ProcessStatus.IN_PROGRESS;
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.PRODUCTION_ORDER).stream().anyMatch(process -> process.getStatus() != ProcessStatus.DONE)) {
                return;
            }

            List<Process> requestWorkProcesses = game.getProcesses().getByExternalReferenceAndType(processId, ProcessType.REQUEST_WORK);
            if (requestWorkProcesses.isEmpty()) {
                requestWorkProcesses = createWorkPointProcesses(syncCache);
            }

            if (requestWorkProcesses.stream().allMatch(process -> process.getStatus() == ProcessStatus.DONE)) {
                storeResource();
                status = ProcessStatus.DONE;
            }
        }
    }

    //TODO extract
    private List<Process> createWorkPointProcesses(SyncCache syncCache) {
        ProductionData productionData = applicationContextProxy.getBean(ProductionBuildingService.class)
            .get(producerBuildingDataId)
            .getGives()
            .get(reservedStorage.getDataId());
        ConstructionRequirements constructionRequirements = productionData.getConstructionRequirements();
        int requiredWorkPoints = reservedStorage.getAmount() * constructionRequirements.getRequiredWorkPoints();

        int maxWorkPointsBatch = applicationContextProxy.getBean(GameProperties.class)
            .getCitizen()
            .getMaxWorkPointsBatch();

        List<Process> result = new ArrayList<>();

        for (int workPointsLeft = requiredWorkPoints; workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            Process requestWorkProcess = new RequestWorkProcess(
                applicationContextProxy,
                processId,
                game,
                planet,
                producerBuildingDataId,
                productionData.getRequiredSkill(),
                Math.min(workPointsLeft, maxWorkPointsBatch)
            );

            game.getProcesses()
                .add(requestWorkProcess);
            syncCache.saveGameItem(requestWorkProcess.toModel());
            result.add(requestWorkProcess);
        }

        return result;
    }

    //TODO extract
    private void createResourceRequirementProcesses(SyncCache syncCache) {
        ProductionRequirementsAllocationService productionRequirementsAllocationService = applicationContextProxy.getBean(ProductionRequirementsAllocationService.class);

        applicationContextProxy.getBean(ProductionBuildingService.class)
            .get(producerBuildingDataId)
            .getGives()
            .get(reservedStorage.getDataId())
            .getConstructionRequirements()
            .getRequiredResources()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() * amount))
            .entrySet()
            .stream()
            .map(entry -> productionRequirementsAllocationService.allocate(syncCache, game.getGameId(), planet, processId, entry.getKey(), entry.getValue()))
            .flatMap(reservedStorageId -> applicationContextProxy.getBean(ProductionOrderProcessFactory.class).create(applicationContextProxy, processId, game, planet, reservedStorageId).stream())
            .forEach(productionOrderProcess -> {
                game.getProcesses().add(productionOrderProcess);
                syncCache.saveGameItem(productionOrderProcess.toModel());
            });
    }

    //TODO extract
    private void storeResource() {
        //TODO
    }

    @Override
    public void cancel(SyncCache syncCache) {
        //TODO
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        //TODO delete reservedStorages and allocatedResources

        game.getProcesses()
            .getByExternalReference(processId)
            .forEach(process -> process.cleanup(syncCache));

        status = ProcessStatus.READY_TO_DELETE;

        syncCache.saveGameItem(toModel());
    }

    @Override
    public ProcessModel toModel() {
        ProcessModel model = new ProcessModel();
        ProcessModel result = new ProcessModel();
        result.setId(processId);
        result.setGameId(game.getGameId());
        result.setType(GameItemType.PROCESS);
        result.setProcessType(getType());
        result.setLocation(planet.getPlanetId());
        result.setLocationType(LocationType.PLANET.name());
        result.setExternalReference(getExternalReference());
        result.setData(CollectionUtils.singleValueMap(GameConstants.PRODUCTION_BUILDING_DATA_ID, producerBuildingDataId));
        return model;
    }
}
