package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.Power;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class PowerplayConflict {
    private UUID starSystemId;
    private Power power;
    private Double conflictProgress;
}
