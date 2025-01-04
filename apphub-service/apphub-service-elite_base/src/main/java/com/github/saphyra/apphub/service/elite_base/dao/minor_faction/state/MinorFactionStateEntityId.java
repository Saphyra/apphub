package com.github.saphyra.apphub.service.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.service.elite_base.dao.FactionStateEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MinorFactionStateEntityId implements Serializable {
    private String minorFactionId;
    @Enumerated(EnumType.STRING)
    private StateStatus status;
    @Enumerated(EnumType.STRING)
    private FactionStateEnum state;
}
