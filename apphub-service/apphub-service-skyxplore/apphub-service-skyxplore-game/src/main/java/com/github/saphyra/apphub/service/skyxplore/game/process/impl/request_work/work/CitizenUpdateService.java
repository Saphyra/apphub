package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SkillToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenUpdateService {
    private final SkillToModelConverter skillToModelConverter;
    private final GameProperties gameProperties;
    private final CitizenToModelConverter citizenToModelConverter;
    private final WsMessageSender messageSender;
    private final CitizenToResponseConverter citizenToResponseConverter;

    void updateCitizen(SyncCache syncCache, UUID gameId, Planet planet, UUID citizenId, int workPoints, SkillType skillType) {
        Citizen citizen = planet.getPopulation()
            .get(citizenId);
        log.info("Citizen {} in game {} used {} workPoints of skill {}", citizen, gameId, workPoints, skillType);

        citizen.reduceMorale(workPoints);

        Skill skill = citizen.getSkills()
            .get(skillType);

        skill.increaseExperience(workPoints);
        if (skill.getExperience() >= skill.getNextLevel()) {
            log.info("Skill {} level earned for citizen {} in game {}", skillType, citizen.getCitizenId(), gameId);
            skill.increaseLevel();
            skill.setExperience(skill.getExperience() - skill.getNextLevel());
            int experiencePerLevel = gameProperties.getCitizen()
                .getSkill()
                .getExperiencePerLevel();
            skill.setNextLevel(skill.getLevel() * experiencePerLevel);
        }

        syncCache.saveGameItem(skillToModelConverter.convert(skill, gameId));
        syncCache.saveGameItem(citizenToModelConverter.convert(citizen, gameId));

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> messageSender.planetCitizenModified(
                planet.getOwner(),
                planet.getPlanetId(),
                citizenToResponseConverter.convert(citizen)
            )
        );
    }
}
