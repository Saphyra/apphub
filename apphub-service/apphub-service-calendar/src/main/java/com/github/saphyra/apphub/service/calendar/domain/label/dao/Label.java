package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class Label {
    private final UUID labelId;
    private final UUID userId;
    private String label;
}
