package com.github.saphyra.apphub.integration.structure.api.villany_atesz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ToolTypeModel {
    private UUID toolTypeId;
    private String name;
}
