package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
