package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SkillToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Data
@Builder
@Slf4j
@ToString(exclude = {"game", "planet", "applicationContextProxy"})
public class Work implements Callable<Work> {
    private final int workPoints;
    private final Game game;
    private final Planet planet;
    private final UUID citizenId;
    private final SkillType skillType;
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Work call() {
        long processTime = calculateSleepTime();

        applicationContextProxy.getBean(GameSleepService.class)
            .sleep(game, processTime);

        SyncCache syncCache = new SyncCache();

        Future<?> citizenUpdateProcess = game.getEventLoop()
            .process(() -> updateCitizen(syncCache), syncCache);

        while (!citizenUpdateProcess.isDone()) {
            applicationContextProxy.getBean(SleepService.class)
                .sleep(100);
        }
        return this;
    }

    //TODO extract
    private void updateCitizen(SyncCache syncCache) {
        Citizen citizen = planet.getPopulation()
            .get(citizenId);
        log.info("Citizen {} in game {} used {} workPoints of skill {}", citizen, game.getGameId(), workPoints, skillType);

        citizen.reduceMorale(workPoints);

        Skill skill = citizen.getSkills()
            .get(skillType);

        skill.increaseExperience(workPoints);
        if (skill.getExperience() >= skill.getNextLevel()) {
            log.info("Skill {} level earned for citizen {} in game {}", skillType, citizen.getCitizenId(), game.getGameId());
            skill.increaseLevel(1);
            skill.setExperience(skill.getExperience() - skill.getNextLevel());
            skill.setNextLevel(skill.getLevel() * applicationContextProxy.getBean(GameProperties.class).getCitizen().getSkill().getExperiencePerLevel());
        }

        syncCache.saveGameItem(applicationContextProxy.getBean(SkillToModelConverter.class).convert(skill, game.getGameId()));
        syncCache.saveGameItem(applicationContextProxy.getBean(CitizenToModelConverter.class).convert(citizen, game.getGameId()));
        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> applicationContextProxy.getBean(WsMessageSender.class).planetCitizenModified(
                planet.getOwner(),
                planet.getPlanetId(),
                applicationContextProxy.getBean(CitizenToResponseConverter.class).convert(citizen)
            )
        );
    }

    private long calculateSleepTime() {
        double workPointsPerSecond = applicationContextProxy.getBean(CitizenEfficiencyCalculator.class)
            .calculateEfficiency(planet.getPopulation().get(citizenId), skillType)
            *
            applicationContextProxy.getBean(GameProperties.class)
                .getCitizen()
                .getWorkPointsPerSeconds();

        long result = Math.round(workPoints / workPointsPerSecond * 1000);
        log.info("Citizen {} will work {} milliseconds to achieve {} workPoints.", citizenId, result, workPoints);
        return result;
    }
}
