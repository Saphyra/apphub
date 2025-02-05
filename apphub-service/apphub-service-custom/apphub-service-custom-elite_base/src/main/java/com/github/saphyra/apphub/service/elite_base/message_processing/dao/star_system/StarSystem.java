package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class StarSystem {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private Long starId;
    private String starName;
    private StarSystemPosition position;
    private StarType starType;
}
