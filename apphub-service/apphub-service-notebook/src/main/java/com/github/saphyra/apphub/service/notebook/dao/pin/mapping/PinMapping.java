package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class PinMapping {
    private final UUID pinMappingId;
    private final UUID userId;
    private final UUID pinGroupId;
    private final UUID listItemId;
}
