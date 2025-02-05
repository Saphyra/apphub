package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.SecurityLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "elite_base", name = "star_system_data")
class StarSystemDataEntity {
    @Id
    private String starSystemId;
    private String lastUpdate;
    private Long population;
    @Enumerated(EnumType.STRING)
    private Allegiance allegiance;
    @Enumerated(EnumType.STRING)
    private EconomyEnum economy;
    @Enumerated(EnumType.STRING)
    private EconomyEnum secondaryEconomy;
    @Enumerated(EnumType.STRING)
    private SecurityLevel securityLevel;
    @Enumerated(EnumType.STRING)
    private Power controllingPower;
    @Enumerated(EnumType.STRING)
    private PowerplayState powerplayState;
    private String controllingFactionId;
    @Enumerated(EnumType.STRING)
    private FactionStateEnum controllingFactionState;
}
