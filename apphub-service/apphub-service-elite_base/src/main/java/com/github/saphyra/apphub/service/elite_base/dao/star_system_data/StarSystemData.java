package com.github.saphyra.apphub.service.elite_base.dao.star_system_data;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.dao.SecurityLevel;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.MinorFactionConflict;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class StarSystemData {
    private final UUID starSystemId;
    private LocalDateTime lastUpdate;
    private Long population;
    private Allegiance allegiance;
    private EconomyEnum economy;
    private EconomyEnum secondaryEconomy;
    private SecurityLevel securityLevel;

    //Populated
    private Power controllingPower;
    private PowerplayState powerplayState;
    private UUID controllingFactionId;
    private FactionStateEnum controllingFactionState;

    private LazyLoadedField<List<UUID>> minorFactions;
    private LazyLoadedField<List<Power>> powers;
    private LazyLoadedField<List<MinorFactionConflict>> conflicts;

    public List<UUID> getMinorFactions() {
        return minorFactions.get();
    }

    public List<Power> getPowers() {
        return powers.get();
    }

    public List<MinorFactionConflict> getConflicts() {
        return conflicts.get();
    }
}
