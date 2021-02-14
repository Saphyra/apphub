package com.github.saphyra.apphub.service.skyxplore.data.save_game.storage_settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String locationType;
    private String dataId;
    private int targetAmount;
    private int priority;
    private int batchSize;
}
