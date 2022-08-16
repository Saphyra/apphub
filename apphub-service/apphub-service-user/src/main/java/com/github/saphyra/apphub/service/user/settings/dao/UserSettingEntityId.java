package com.github.saphyra.apphub.service.user.settings.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
class UserSettingEntityId implements Serializable {
    private String userId;
    private String category;
    private String key;
}
