package com.github.saphyra.apphub.api.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SetUserSettingsRequest {
    private String category;
    private String key;
    private String value;
}
