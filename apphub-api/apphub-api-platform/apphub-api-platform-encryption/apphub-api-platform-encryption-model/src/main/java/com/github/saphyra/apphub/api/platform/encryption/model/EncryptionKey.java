package com.github.saphyra.apphub.api.platform.encryption.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString(exclude = "encryptionKey")
public class EncryptionKey {
    private UUID externalId;
    private DataType dataType;
    private UUID userId; //Owner of the record. Always the one who created the originally shared data, regardless the depth of the hierarchy.
    private String encryptionKey;
}
