package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenUpdateService {
    private final GameProperties gameProperties;
    private final WsMessageSender messageSender;
    private final SkillConverter skillConverter;
    private final CitizenConverter citizenConverter;

    void updateCitizen(SyncCache syncCache, GameData gameData, UUID location, UUID citizenId, int workPoints, SkillType skillType) {
        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);
        log.info("Citizen {} in game {} used {} workPoints of skill {}", citizen, gameData.getGameId(), workPoints, skillType);

        citizen.reduceMorale(workPoints);

        Skill skill = gameData.getSkills()
            .findByCitizenIdAndSkillType(citizenId, skillType);

        skill.increaseExperience(workPoints);
        if (skill.getExperience() >= skill.getNextLevel()) {
            log.info("Skill {} level earned for citizen {} in game {}", skillType, citizen.getCitizenId(), gameData.getGameId());
            skill.increaseLevel();
            skill.setExperience(skill.getExperience() - skill.getNextLevel());
            int experiencePerLevel = gameProperties.getCitizen()
                .getSkill()
                .getExperiencePerLevel();
            skill.setNextLevel(skill.getLevel() * experiencePerLevel);
        }

        syncCache.saveGameItem(skillConverter.toModel(gameData.getGameId(), skill));
        syncCache.saveGameItem(citizenConverter.toModel(gameData.getGameId(), citizen));

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> messageSender.planetCitizenModified(
                ownerId,
                location,
                citizenConverter.toResponse(gameData, citizen)
            )
        );
    }
}
