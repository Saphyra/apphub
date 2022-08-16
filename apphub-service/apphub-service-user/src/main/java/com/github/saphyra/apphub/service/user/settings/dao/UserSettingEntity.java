package com.github.saphyra.apphub.service.user.settings.dao;

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
@Table(schema = "apphub_user", name = "settings")
class UserSettingEntity {
    @EmbeddedId
    private UserSettingEntityId id;
    private String value;
}
