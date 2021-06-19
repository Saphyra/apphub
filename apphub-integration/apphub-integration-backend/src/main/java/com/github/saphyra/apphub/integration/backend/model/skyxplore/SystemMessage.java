package com.github.saphyra.apphub.integration.backend.model.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemMessage {
    private String room;
    private String characterName;
    private UUID userId;
}
