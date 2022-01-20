package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor;

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
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
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
//TODO unit test
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
        Map<UUID, Assignment> citizenAssignments = tickCache.get(gameId).getCitizenAssignments();

        for (int i = 0; i < construction.getParallelWorkers() && construction.getCurrentWorkPoints() < construction.getRequiredWorkPoints(); i++) {
            Optional<Citizen> maybeUnemployedCitizen = availableCitizenProvider.findMostCapableUnemployedCitizen(citizenAssignments.keySet(), planet.getPopulation().values(), SkillType.BUILDING);
            if (maybeUnemployedCitizen.isPresent()) {
                Citizen citizen = maybeUnemployedCitizen.get();
                Assignment assignment = assignCitizenService.assignCitizen(gameId, citizen, construction.getConstructionId());

                int completedWork = makeCitizenWorkService.requestWork(gameId, planet.getOwner(), planet.getPlanetId(), assignment, construction.getRequiredWorkPoints() - construction.getCurrentWorkPoints(), SkillType.BUILDING);
                construction.setCurrentWorkPoints(construction.getCurrentWorkPoints() + completedWork);
            } else {
                break;
            }
        }

        tickCache.get(gameId)
            .getGameItemCache()
            .save(constructionToModelConverter.convert(construction, gameId));

        MessageCache messageCache = tickCache.get(gameId)
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
            () -> messageSender.planetQueueItemModified(planet.getOwner(), planet.getPlanetId(), queueItemToResponseConverter.convert(
                buildingConstructionToQueueItemConverter.convert(surface.getBuilding()),
                planet
            ))
        );
    }
}
