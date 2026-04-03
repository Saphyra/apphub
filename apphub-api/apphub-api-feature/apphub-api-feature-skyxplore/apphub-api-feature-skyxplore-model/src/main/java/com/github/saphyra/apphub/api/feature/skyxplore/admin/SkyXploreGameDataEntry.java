package com.github.saphyra.apphub.api.feature.skyxplore.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SkyXploreGameDataEntry {
    private UUID id;
    private Object data;
}
