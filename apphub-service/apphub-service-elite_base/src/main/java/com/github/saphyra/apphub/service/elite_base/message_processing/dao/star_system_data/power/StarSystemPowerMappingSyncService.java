package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.Power;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemPowerMappingSyncService {
    private final StarSystemPowerMappingDao starSystemPowerMappingDao;
    private final StarSystemPowerMappingFactory starSystemPowerMappingFactory;

    public void sync(UUID starSystemId, List<Power> powers) {
        if (isNull(powers)) {
            return;
        }

        List<StarSystemPowerMapping> newMappings = powers.stream()
            .map(minorFactionId -> starSystemPowerMappingFactory.create(starSystemId, minorFactionId))
            .toList();
        List<StarSystemPowerMapping> existingMappings = starSystemPowerMappingDao.getByStarSystemId(starSystemId);

        List<StarSystemPowerMapping> toDelete = existingMappings.stream()
            .filter(starSystemMinorFactionMapping -> !newMappings.contains(starSystemMinorFactionMapping))
            .toList();
        List<StarSystemPowerMapping> toSave = newMappings.stream()
            .filter(starSystemMinorFactionMapping -> !existingMappings.contains(starSystemMinorFactionMapping))
            .toList();

        starSystemPowerMappingDao.deleteAll(toDelete);
        starSystemPowerMappingDao.saveAll(toSave);
    }
}
