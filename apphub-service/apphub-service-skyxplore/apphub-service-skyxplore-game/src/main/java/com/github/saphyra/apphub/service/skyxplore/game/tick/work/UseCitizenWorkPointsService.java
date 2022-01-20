package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
//TODO unit test
class UseCitizenWorkPointsService {
    private final int experiencePerLevel;
    private final TickCache tickCache;
    private final CitizenToResponseConverter citizenToResponseConverter;
    private final WsMessageSender messageSender;
    private final CitizenToModelConverter citizenToModelConverter;

    public UseCitizenWorkPointsService(
        @Value("${game.citizen.skill.experiencePerLevel}") int experiencePerLevel,
        TickCache tickCache,
        CitizenToResponseConverter citizenToResponseConverter,
        WsMessageSender messageSender,
        CitizenToModelConverter citizenToModelConverter
    ) {
        this.experiencePerLevel = experiencePerLevel;
        this.tickCache = tickCache;
        this.citizenToResponseConverter = citizenToResponseConverter;
        this.messageSender = messageSender;
        this.citizenToModelConverter = citizenToModelConverter;
    }

    /*
    Making citizens tired, and more experienced
     */
    void useWorkPoints(UUID gameId, UUID userId, UUID planetId, Citizen citizen, int workPointsUsed, SkillType skillType) {
        citizen.setMorale(citizen.getMorale() - workPointsUsed);

        Skill skill = citizen.getSkills()
            .get(skillType);

        skill.setExperience(skill.getExperience() + workPointsUsed);
        if (skill.getExperience() >= skill.getNextLevel()) {
            skill.setLevel(skill.getLevel() + 1);
            skill.setExperience(skill.getExperience() - skill.getNextLevel());
            skill.setNextLevel(skill.getLevel() * experiencePerLevel);
        }

        GameItemCache gameItemCache = tickCache.get(gameId)
            .getGameItemCache();
        gameItemCache.saveAll(citizenToModelConverter.convertDeep(citizen, gameId));

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
