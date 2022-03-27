package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItemToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction.BuildingConstructionToQueueItemConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AssignCitizenService;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AvailableCitizenProvider;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.MakeCitizenWorkService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProceedWithConstructionService {
    private final AvailableCitizenProvider availableCitizenProvider;
    private final TickCache tickCache;
    private final AssignCitizenService assignCitizenService;
    private final MakeCitizenWorkService makeCitizenWorkService;
    private final SurfaceToResponseConverter surfaceToResponseConverter;
    private final BuildingConstructionToQueueItemConverter buildingConstructionToQueueItemConverter;
    private final QueueItemToResponseConverter queueItemToResponseConverter;
    private final WsMessageSender messageSender;
    private final ConstructionToModelConverter constructionToModelConverter;

    /*
    Assigning citizens to work on the construction
     */
    void proceedWithConstruction(UUID gameId, Planet planet, Surface surface, Construction construction) {
        log.debug("Proceeding with construction {} in game {}", construction, gameId);

        TickCacheItem tickCacheItem = tickCache.get(gameId);
        Map<UUID, Assignment> citizenAssignments = tickCacheItem.getCitizenAssignments();

        for (int i = 0; i < construction.getParallelWorkers() && construction.getCurrentWorkPoints() < construction.getRequiredWorkPoints(); i++) {
            log.debug("Employing Citizen #{} out of {}", i + 1, construction.getParallelWorkers());
            Optional<Citizen> maybeUnemployedCitizen = availableCitizenProvider.findMostCapableUnemployedCitizen(
                citizenAssignments,
                planet.getPopulation().values(),
                construction.getConstructionId(),
                SkillType.BUILDING
            );
            log.debug("Unemployed citizen: {} in game {}", maybeUnemployedCitizen, gameId);
            if (maybeUnemployedCitizen.isPresent()) {
                Citizen citizen = maybeUnemployedCitizen.get();
                Assignment assignment = assignCitizenService.assignCitizen(gameId, citizen, construction.getConstructionId());

                int requestedWorkPoints = construction.getRequiredWorkPoints() - construction.getCurrentWorkPoints();
                int completedWork = makeCitizenWorkService.requestWork(gameId, planet.getOwner(), planet.getPlanetId(), assignment, requestedWorkPoints, SkillType.BUILDING);
                log.debug("{} work completed out of requested {} in game {}", completedWork, requestedWorkPoints, gameId);
                construction.setCurrentWorkPoints(construction.getCurrentWorkPoints() + completedWork);
            } else {
                log.debug("No unemployed citizen found to proceed with {} in game {}", construction, gameId);
                break;
            }
        }

        log.debug("Work completed on {} in game {}", construction, gameId);

        tickCacheItem.getGameItemCache()
            .save(constructionToModelConverter.convert(construction, gameId));

        MessageCache messageCache = tickCacheItem
            .getMessageCache();
        messageCache.add(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED,
            surface.getSurfaceId(),
            () -> messageSender.planetSurfaceModified(planet.getOwner(), planet.getPlanetId(), surfaceToResponseConverter.convert(surface))
        );
        messageCache.add(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED,
            construction.getConstructionId(),
            () -> messageSender.planetQueueItemModified(
                planet.getOwner(),
                planet.getPlanetId(),
                queueItemToResponseConverter.convert(buildingConstructionToQueueItemConverter.convert(surface.getBuilding()), planet)
            )
        );
    }
}
