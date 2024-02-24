package com.github.saphyra.apphub.service.notebook.dao.pin.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class PinGroup {
    private final UUID pinGroupId;
    private final UUID userId;
    private String pinGroupName;
}
