package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Convoy {
    private final UUID convoyId;
    private final UUID resourceDeliveryRequestId;
    private int capacity;
}
