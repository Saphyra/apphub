package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStat;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.StatResponse;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class StatConverter {
    private static final Map<CitizenStat, Function<Citizen, Integer>> CITIZEN_PROPERTY_EXTRACTORS = new HashMap<>() {{
        put(CitizenStat.MORALE, Citizen::getMorale);
        put(CitizenStat.SATIETY, Citizen::getSatiety);
    }};

    private final GameProperties gameProperties;

    Map<CitizenStat, StatResponse> convert(Citizen citizen) {
        return Arrays.stream(CitizenStat.values())
            .collect(Collectors.toMap(Function.identity(), stat -> convert(stat, citizen)));
    }

    private StatResponse convert(CitizenStat stat, Citizen citizen) {
        return StatResponse.builder()
            .value(CITIZEN_PROPERTY_EXTRACTORS.get(stat).apply(citizen))
            .maxValue(gameProperties.getCitizen().getMaxStatValues().get(stat))
            .build();
    }
}
