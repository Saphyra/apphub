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
    private UUID userId;
    private String encryptionKey;
}
