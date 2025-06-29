package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
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
public class MinorFaction {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private String factionName;
    private FactionStateEnum state;
    private Double influence;
    private Allegiance allegiance;
    @Builder.Default
    private LazyLoadedField<List<MinorFactionState>> activeStates = LazyLoadedField.loaded(List.of());
    @Builder.Default
    private LazyLoadedField<List<MinorFactionState>> pendingStates = LazyLoadedField.loaded(List.of());
    @Builder.Default
    private LazyLoadedField<List<MinorFactionState>> recoveringStates = LazyLoadedField.loaded(List.of());

    public List<MinorFactionState> getActiveStates() {
        return activeStates.get();
    }

    public List<MinorFactionState> getPendingStates() {
        return pendingStates.get();
    }

    public List<MinorFactionState> getRecoveringStates() {
        return recoveringStates.get();
    }
}
