package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Organization {
    private final UUID id;
    private String name;
    private String description;
}
