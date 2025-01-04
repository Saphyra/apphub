package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.minor_faction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StarSystemMinorFactionMappingSyncService {
    private final StarSystemMinorFactionMappingFactory starSystemMinorFactionMappingFactory;
    private final StarSystemMinorFactionMappingDao starSystemMinorFactionMappingDao;

    public void sync(UUID starSystemId, List<UUID> minorFactionIds) {
        List<StarSystemMinorFactionMapping> newMappings = minorFactionIds.stream()
            .map(minorFactionId -> starSystemMinorFactionMappingFactory.create(starSystemId, minorFactionId))
            .toList();
        List<StarSystemMinorFactionMapping> existingMappings = starSystemMinorFactionMappingDao.getByStarSystemId(starSystemId);

        List<StarSystemMinorFactionMapping> toDelete = existingMappings.stream()
            .filter(starSystemMinorFactionMapping -> !newMappings.contains(starSystemMinorFactionMapping))
            .toList();
        List<StarSystemMinorFactionMapping> toSave = newMappings.stream()
            .filter(starSystemMinorFactionMapping -> !existingMappings.contains(starSystemMinorFactionMapping))
            .toList();

        starSystemMinorFactionMappingDao.deleteAll(toDelete);
        starSystemMinorFactionMappingDao.saveAll(toSave);
    }
}
