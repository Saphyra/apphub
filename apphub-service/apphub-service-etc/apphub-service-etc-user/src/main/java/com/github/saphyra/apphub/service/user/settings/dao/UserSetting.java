package com.github.saphyra.apphub.service.user.settings.dao;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserSetting {
    private final UUID userId;
    private final String category;
    private final String key;
    private String value;
}
