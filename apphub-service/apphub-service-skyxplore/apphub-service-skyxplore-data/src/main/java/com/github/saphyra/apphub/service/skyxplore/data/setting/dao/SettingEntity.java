package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "skyxplore", name = "setting")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class SettingEntity {
    @Id
    private String settingId;
    private String gameId;
    private String userId;
    @Enumerated(EnumType.STRING)
    private SettingType type;
    private String location;
    private String data;
}
