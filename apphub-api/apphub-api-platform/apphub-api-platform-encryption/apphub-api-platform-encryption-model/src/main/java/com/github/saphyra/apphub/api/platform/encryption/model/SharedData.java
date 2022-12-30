package com.github.saphyra.apphub.api.platform.encryption.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SharedData {
    private UUID sharedDataId;
    private UUID externalId;
    private DataType dataType;
    private UUID sharedWith;
    @Builder.Default
    private Boolean publicData = false;
    private AccessMode accessMode;
}
