package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class LabelEntity {
    private String labelId;
    private String label; //Encrypted
}
