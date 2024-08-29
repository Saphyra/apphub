package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class ToolType {
    private final UUID toolTypeId;
    private final UUID userId;
    private String name;
}
