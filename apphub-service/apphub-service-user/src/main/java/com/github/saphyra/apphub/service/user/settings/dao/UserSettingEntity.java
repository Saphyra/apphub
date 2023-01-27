package com.github.saphyra.apphub.service.user.settings.dao;

import jakarta.persistence.Column;
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
@Table(schema = "apphub_user", name = "settings")
class UserSettingEntity {
    @EmbeddedId
    private UserSettingEntityId id;
    @Column(name = "setting_value")
    private String value;
}
