package com.github.saphyra.apphub.service.platform.encryption.shared_data.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "encryption", name = "shared_data")
class SharedDataEntity {
    @Id
    private String sharedDataId;
    private String externalId;
    private String dataType;
    private String sharedWith;
    private boolean publicData;
    private String accessMode;
}
