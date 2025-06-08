package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message.PowerplayConflictProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PowerplayConflictFactory {
    public List<PowerplayConflict> create(UUID starSystemId, List<PowerplayConflictProgress> powerplayConflictProgresses) {
        return powerplayConflictProgresses.stream()
            .map(powerplayConflictProgress -> PowerplayConflict.builder()
                .starSystemId(starSystemId)
                .power(Power.parse(powerplayConflictProgress.getPower()))
                .conflictProgress(powerplayConflictProgress.getConflictProgress())
                .build())
            .toList();
    }
}
