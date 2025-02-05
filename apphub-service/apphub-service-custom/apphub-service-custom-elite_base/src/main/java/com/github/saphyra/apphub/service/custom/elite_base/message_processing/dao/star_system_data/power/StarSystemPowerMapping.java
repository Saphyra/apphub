package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.Power;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class StarSystemPowerMapping {
    private final UUID starSystemId;
    private final Power power;
}
