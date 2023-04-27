package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "storage_setting")
class StorageSettingEntity {
    @Id
    private String storageSettingId;
    private String gameId;
    private String location;
    private String dataId;
    private int targetAmount;
    private int priority;
    private int batchSize;
}
