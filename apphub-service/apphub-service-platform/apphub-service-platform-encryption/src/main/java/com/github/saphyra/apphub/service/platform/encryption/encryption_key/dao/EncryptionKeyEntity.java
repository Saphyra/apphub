package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "encryption", name = "encryption_key")
class EncryptionKeyEntity {
    @EmbeddedId
    private EncryptionKeyPk pk;
    private String userId;
    private String encryptionKey;
}
