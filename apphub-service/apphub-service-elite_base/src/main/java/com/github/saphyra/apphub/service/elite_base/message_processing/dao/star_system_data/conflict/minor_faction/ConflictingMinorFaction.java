package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@EqualsAndHashCode(exclude = "conflictId")
public class ConflictingMinorFaction {
    private final UUID conflictId;
    private final UUID factionId;
    private Integer wonDays;
    private String stake;
}
