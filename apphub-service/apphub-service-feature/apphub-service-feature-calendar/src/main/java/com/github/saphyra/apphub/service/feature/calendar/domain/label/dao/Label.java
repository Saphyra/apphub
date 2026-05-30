package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Label {
    @NonNull
    private final UUID labelId;
    @NonNull
    private String label;
}
