package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SkillToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@Builder
class UseCitizenWorkPointsService {
    private final int experiencePerLevel;
    private final TickCache tickCache;
    private final CitizenToResponseConverter citizenToResponseConverter;
    private final WsMessageSender messageSender;
    private final CitizenToModelConverter citizenToModelConverter;
    private final SkillToModelConverter skillToModelConverter;

    public UseCitizenWorkPointsService(
        @Value("${game.citizen.skill.experiencePerLevel}") int experiencePerLevel,
        TickCache tickCache,
        CitizenToResponseConverter citizenToResponseConverter,
        WsMessageSender messageSender,
        CitizenToModelConverter citizenToModelConverter,
        SkillToModelConverter skillToModelConverter
    ) {
        this.experiencePerLevel = experiencePerLevel;
        this.tickCache = tickCache;
        this.citizenToResponseConverter = citizenToResponseConverter;
        this.messageSender = messageSender;
        this.citizenToModelConverter = citizenToModelConverter;
        this.skillToModelConverter = skillToModelConverter;
    }

    /*
    Making citizens tired, and more experienced
     */
    void useWorkPoints(UUID gameId, UUID userId, UUID planetId, Citizen citizen, int workPointsUsed, SkillType skillType) {
        log.debug("Citizen {} in game {} used {} workPoints of skill {}", citizen, gameId, workPointsUsed, skillType);
        citizen.reduceMorale(workPointsUsed);

        Skill skill = citizen.getSkills()
            .get(skillType);

        skill.increaseExperience(workPointsUsed);
        if (skill.getExperience() >= skill.getNextLevel()) {
            log.debug("Skill {} level earned for citizen {} in game {}", skillType, citizen.getCitizenId(), gameId);
            skill.increaseLevel(1);
            skill.setExperience(skill.getExperience() - skill.getNextLevel());
            skill.setNextLevel(skill.getLevel() * experiencePerLevel);
        }

        GameItemCache gameItemCache = tickCache.get(gameId)
            .getGameItemCache();
        gameItemCache.save(skillToModelConverter.convert(skill, gameId));
        gameItemCache.save(citizenToModelConverter.convert(citizen, gameId));

        MessageCache messageCache = tickCache.get(gameId)
            .getMessageCache();
        messageCache.add(
            userId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> messageSender.planetCitizenModified(userId, planetId, citizenToResponseConverter.convert(citizen))
        );
    }
}
