package com.github.saphyra.apphub.service.user.settings.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
class UserSettingEntityId implements Serializable {
    private String userId;
    private String category;
    @Column(name = "setting_key")
    private String key;
}
