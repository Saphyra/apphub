package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Embeddable
class EncryptionKeyPk implements Serializable {
    private String externalId;
    private String dataType;
}
