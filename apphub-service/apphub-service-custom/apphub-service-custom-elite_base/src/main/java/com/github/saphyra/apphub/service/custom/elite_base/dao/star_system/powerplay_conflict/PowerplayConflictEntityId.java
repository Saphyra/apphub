package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PowerplayConflictEntityId implements Serializable {
    private String starSystemId;
    @Enumerated(EnumType.STRING)
    private Power power;
}
