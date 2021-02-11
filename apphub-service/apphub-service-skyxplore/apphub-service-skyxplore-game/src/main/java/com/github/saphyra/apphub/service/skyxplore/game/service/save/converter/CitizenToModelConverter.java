package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CitizenToModelConverter {
    private final SkillToModelConverter skillConverter;

    public List<GameItem> convertDeep(Collection<Citizen> citizens, Game game) {
        return citizens.stream()
            .map(citizen -> convertDeep(citizen, game))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<GameItem> convertDeep(Citizen citizen, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(citizen, game));
        result.addAll(skillConverter.convert(citizen.getSkills().values(), game));
        return result;
    }

    private CitizenModel convert(Citizen citizen, Game game) {
        CitizenModel model = new CitizenModel();
        model.setId(citizen.getCitizenId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.CITIZEN);
        model.setLocation(citizen.getLocation());
        model.setLocationType(citizen.getLocationType().name());
        model.setName(citizen.getName());
        model.setMorale(citizen.getMorale());
        model.setSatiety(citizen.getSatiety());
        return model;
    }
}
