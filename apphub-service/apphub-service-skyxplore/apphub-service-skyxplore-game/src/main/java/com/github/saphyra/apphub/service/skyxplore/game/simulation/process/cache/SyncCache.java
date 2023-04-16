package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction.ConstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction.DeconstructionProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation.TerraformationProcess;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
//TODO unit test
public class SyncCache {
    private static final List<OpenedPageType> PLANET_PAGE_TYPE_GROUP = ImmutableList.of(OpenedPageType.PLANET, OpenedPageType.PLANET_POPULATION_OVERVIEW);

    @NonNull
    private final MessageCache messageCache;

    @NonNull
    private final GameItemCache gameItemCache;

    @NonNull
    private final SyncCacheContext context;

    @NonNull
    private final Game game;

    public void saveGameItem(GameItem gameItem) {
        gameItemCache.save(gameItem);
    }

    public void deleteGameItem(UUID id, GameItemType type) {
        gameItemCache.delete(id, type);
    }

    public void process() {
        messageCache.process(game);
        gameItemCache.process();
    }

    //Citizen
    public void citizenModified(UUID recipient, Citizen citizen, Skill skill) {
        saveGameItem(context.getSkillConverter().toModel(game.getGameId(), skill));

        citizenModified(recipient, citizen);
    }

    public void citizenModified(Citizen citizen) {
        UUID recipient = game.getData()
            .getPlanets()
            .get(citizen.getLocation())
            .getOwner();

        citizenModified(recipient, citizen);
    }

    private void citizenModified(UUID recipient, Citizen citizen) {
        saveGameItem(context.getCitizenConverter().toModel(game.getGameId(), citizen));

        WsMessageKey key = context.getMessageKeyFactory()
            .create(
                recipient,
                WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
                citizen.getCitizenId(),
                OpenedPageType.PLANET_POPULATION_OVERVIEW,
                citizen.getLocation()
            );
        messageCache.put(key, () -> WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED, recipient, context.getCitizenConverter().toResponse(game.getData(), citizen)));
    }

    //Terraformation
    public void terraformationFinished(UUID recipient, UUID location, Construction terraformation, Surface surface) {
        deleteGameItem(terraformation.getConstructionId(), GameItemType.CONSTRUCTION);

        planetQueueItemDeleted(recipient, location, terraformation.getConstructionId());
        surfaceModified(recipient, location, surface);
        buildingDetailsModified(recipient, location);
    }

    public void terraformationUpdated(UUID recipient, UUID location, Construction terraformation, Surface surface) {
        saveGameItem(context.getConstructionConverter().toModel(game.getGameId(), terraformation));

        surfaceModified(recipient, location, surface);
        queueItemModified(recipient, location, context.getTerraformationToQueueItemConverter().convert(terraformation, surface));
    }

    public void terraformationCancelled(UUID recipient, UUID location, UUID constructionId, Surface surface) {
        planetQueueItemDeleted(recipient, location, constructionId);
        buildingDetailsModified(recipient, location);
        surfaceModified(recipient, location, surface);
    }

    public void terraformationCreated(UUID recipient, UUID location, Construction terraformation, Surface surface, TerraformationProcess process) {
        saveGameItem(context.getConstructionConverter().toModel(game.getGameId(), terraformation));
        saveGameItem(process.toModel());

        surfaceModified(recipient, location, surface);
        queueItemModified(recipient, location, context.getTerraformationToQueueItemConverter().convert(terraformation, surface));
        buildingDetailsModified(recipient, location);
    }

    public void terraformationModified(UUID recipient, UUID location, Construction terraformation, Surface surface) {
        saveGameItem(context.getConstructionConverter().toModel(game.getGameId(), terraformation));

        queueItemModified(recipient, location, context.getTerraformationToQueueItemConverter().convert(terraformation, surface));
    }

    //Planet overview
    private void buildingDetailsModified(UUID recipient, UUID location) {
        WsMessageKey key = context.getMessageKeyFactory()
            .create(recipient, WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED, location, PLANET_PAGE_TYPE_GROUP, location);

        messageCache.put(
            key,
            () -> WebSocketMessage.forEventAndRecipients(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED,
                recipient,
                context.getPlanetBuildingOverviewQueryService().getBuildingOverview(game.getData(), location)
            )
        );
    }

    private void surfaceModified(UUID recipient, UUID location, Surface surface) {
        SurfaceConverter surfaceConverter = context.getSurfaceConverter();

        saveGameItem(surfaceConverter.toModel(game.getGameId(), surface));

        WsMessageKey key = context.getMessageKeyFactory()
            .create(recipient, WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, surface.getSurfaceId(), PLANET_PAGE_TYPE_GROUP, location);

        messageCache.put(key, () -> WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, recipient, surfaceConverter.toResponse(game.getData(), surface)));
    }

    //Queue item
    private void planetQueueItemDeleted(UUID recipient, UUID location, UUID queueItemId) {
        WsMessageKey key = context.getMessageKeyFactory()
            .create(recipient, WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED, queueItemId, PLANET_PAGE_TYPE_GROUP, location);

        messageCache.put(key, () -> WebSocketMessage.forEventAndRecipients(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED, recipient, queueItemId));

        WsMessageKey modifiedKey = context.getMessageKeyFactory()
            .create(recipient, WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED, queueItemId, PLANET_PAGE_TYPE_GROUP, location);
        messageCache.remove(modifiedKey);
    }

    private void queueItemModified(UUID recipient, UUID location, QueueItem queueItem) {
        WsMessageKey key = context.getMessageKeyFactory()
            .create(recipient, WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED, queueItem.getItemId(), PLANET_PAGE_TYPE_GROUP, location);

        messageCache.put(
            key,
            () -> WebSocketMessage.forEventAndRecipients(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED,
                recipient,
                context.getQueueItemToResponseConverter().convert(queueItem, game.getData(), location)
            )
        );
    }

    //Construction
    public void constructionFinished(UUID recipient, UUID location, Construction construction, Building building, Surface surface) {
        deleteGameItem(construction.getConstructionId(), GameItemType.CONSTRUCTION);
        saveGameItem(context.getBuildingConverter().toModel(game.getGameId(), building));

        planetQueueItemDeleted(recipient, location, construction.getConstructionId());
        surfaceModified(recipient, location, surface);
        buildingDetailsModified(recipient, location);
    }

    public void constructionUpdated(UUID recipient, UUID location, Construction construction, Surface surface) {
        saveGameItem(context.getConstructionConverter().toModel(game.getGameId(), construction));
        surfaceModified(recipient, location, surface);
        queueItemModified(recipient, location, context.getBuildingConstructionToQueueItemConverter().convert(game.getData(), construction));
    }

    public void constructionModified(UUID recipient, UUID location, Construction construction) {
        saveGameItem(context.getConstructionConverter().toModel(game.getGameId(), construction));

        queueItemModified(recipient, location, context.getBuildingConstructionToQueueItemConverter().convert(game.getData(), construction));
    }

    public void constructionCancelled(UUID recipient, UUID location, UUID constructionId, Surface surface) {
        deleteGameItem(constructionId, GameItemType.CONSTRUCTION);

        planetQueueItemDeleted(recipient, location, constructionId);
        buildingDetailsModified(recipient, location);
        surfaceModified(recipient, location, surface);
    }

    public void constructionCreated(UUID recipient, UUID location, Construction construction, Surface surface, ConstructionProcess process) {
        saveGameItem(context.getConstructionConverter().toModel(game.getGameId(), construction));
        saveGameItem(process.toModel());

        queueItemModified(recipient, location, context.getBuildingConstructionToQueueItemConverter().convert(game.getData(), construction));
        buildingDetailsModified(recipient, location);
        surfaceModified(recipient, location, surface);
    }

    //Deconstruction
    public void deconstructionFinished(UUID recipient, UUID location, Deconstruction deconstruction, Building building, Surface surface) {
        gameItemCache.delete(deconstruction.getDeconstructionId(), GameItemType.DECONSTRUCTION);
        gameItemCache.delete(building.getBuildingId(), GameItemType.BUILDING);

        planetQueueItemDeleted(recipient, location, deconstruction.getDeconstructionId());
        surfaceModified(recipient, location, surface);
        buildingDetailsModified(recipient, location);
    }

    public void deconstructionCancelled(UUID recipient, UUID location, UUID deconstructionId, Surface surface) {
        deleteGameItem(deconstructionId, GameItemType.DECONSTRUCTION);

        planetQueueItemDeleted(recipient, location, deconstructionId);
        buildingDetailsModified(recipient, location);
        surfaceModified(recipient, location, surface);
    }

    public void deconstructionUpdated(UUID recipient, UUID location, Deconstruction deconstruction, Surface surface) {
        saveGameItem(context.getDeconstructionConverter().toModel(game.getGameId(), deconstruction));

        surfaceModified(recipient, location, surface);
        buildingDetailsModified(recipient, location);
    }

    public void deconstructionModified(UUID recipient, UUID location, Deconstruction deconstruction) {
        saveGameItem(context.getDeconstructionConverter().toModel(game.getGameId(), deconstruction));

        queueItemModified(recipient, location, context.getBuildingDeconstructionToQueueItemConverter().convert(game.getData(), deconstruction));
    }

    public void deconstructionCreated(UUID recipient, UUID location, Deconstruction deconstruction, Surface surface, DeconstructionProcess process) {
        saveGameItem(context.getDeconstructionConverter().toModel(game.getGameId(), deconstruction));
        saveGameItem(process.toModel());

        queueItemModified(recipient, location, context.getBuildingDeconstructionToQueueItemConverter().convert(game.getData(), deconstruction));
        buildingDetailsModified(recipient, location);
        surfaceModified(recipient, location, surface);
    }

    //Storage
    public void allocatedResourceResolved(UUID recipient, UUID location, AllocatedResource allocatedResource, StoredResource storedResource) {
        saveGameItem(context.getAllocatedResourceConverter().toModel(game.getGameId(), allocatedResource));
        saveGameItem(context.getStoredResourceConverter().toModel(game.getGameId(), storedResource));

        storageModified(recipient, location);
    }

    public void storageModified(UUID recipient, UUID location) {
        WsMessageKey key = context.getMessageKeyFactory()
            .create(
                recipient,
                WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
                location,
                PLANET_PAGE_TYPE_GROUP,
                location
            );

        messageCache.put(
            key,
            () -> WebSocketMessage.forEventAndRecipients(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED,
                recipient,
                context.getPlanetStorageOverviewQueryService().getStorage(recipient, location)
            )
        );
    }

    public void resourceStored(UUID recipient, UUID location, StoredResource storedResource, ReservedStorage reservedStorage) {
        saveGameItem(context.getStoredResourceConverter().toModel(game.getGameId(), storedResource));
        saveGameItem(context.getReservedStorageConverter().toModel(game.getGameId(), reservedStorage));

        storageModified(recipient, location);
    }

    public void resourceAllocated(UUID recipient, UUID location, AllocatedResource allocatedResource, ReservedStorage reservedStorage) {
        saveGameItem(context.getAllocatedResourceConverter().toModel(game.getGameId(), allocatedResource));
        saveGameItem(context.getReservedStorageConverter().toModel(game.getGameId(), reservedStorage));

        storageModified(recipient, location);
    }
}
