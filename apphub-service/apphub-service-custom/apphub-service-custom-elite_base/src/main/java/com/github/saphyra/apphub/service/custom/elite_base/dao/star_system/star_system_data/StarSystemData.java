package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.SecurityLevel;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflict;
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
    private UUID controllingFactionId;
    private FactionStateEnum controllingFactionState;

    //Powerplay
    private Power controllingPower;
    private PowerplayState powerplayState;
    private Double powerplayStateControlProgress;
    private Double powerplayStateReinforcement;
    private Double powerplayStateUndermining;
    @Builder.Default
    private LazyLoadedField<List<Power>> powers = LazyLoadedField.loaded(List.of());
    @Builder.Default
    private LazyLoadedField<List<PowerplayConflict>> powerplayConflicts = LazyLoadedField.loaded(List.of());
    @Builder.Default
    private LazyLoadedField<List<UUID>> minorFactions = LazyLoadedField.loaded(List.of());
    @Builder.Default
    private LazyLoadedField<List<MinorFactionConflict>> conflicts = LazyLoadedField.loaded(List.of());

    public List<UUID> getMinorFactions() {
        return minorFactions.get();
    }

    public List<Power> getPowers() {
        return powers.get();
    }

    public List<PowerplayConflict> getPowerplayConflicts() {
        return powerplayConflicts.get();
    }

    public List<MinorFactionConflict> getConflicts() {
        return conflicts.get();
    }
}
