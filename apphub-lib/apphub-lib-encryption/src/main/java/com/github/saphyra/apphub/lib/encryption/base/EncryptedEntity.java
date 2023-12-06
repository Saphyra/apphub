package com.github.saphyra.apphub.lib.encryption.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EncryptedEntity {
    private String entity;
    private String entityId;
    private String column;
}
