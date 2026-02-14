package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class StarSystemDataLoader {
    private final StarSystemPowerMappingDao starSystemPowerMappingDao;
    private final StarSystemDataDao starSystemDataDao;

    Map<UUID, List<Power>> loadPowers(List<Offer> offers) {
        List<UUID> starSystemIds = getStarSystemIds(offers);

        return starSystemPowerMappingDao.getByStarSystemIds(starSystemIds)
            .stream()
            .collect(Collectors.groupingBy(StarSystemPowerMapping::getStarSystemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(StarSystemPowerMapping::getPower).toList()));
    }

    Map<UUID, StarSystemData> loadStarSystemData(List<Offer> offers) {
        List<UUID> starSystemIds = getStarSystemIds(offers);

        return starSystemDataDao.findAllById(starSystemIds)
            .stream()
            .collect(Collectors.toMap(StarSystemData::getStarSystemId, starSystemData -> starSystemData));
    }

    private static List<UUID> getStarSystemIds(List<Offer> offers) {
        return offers.stream()
            .map(Offer::getStarSystemId)
            .distinct()
            .toList();
    }
}
